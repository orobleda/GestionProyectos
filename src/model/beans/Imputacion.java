package model.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import controller.Log;
import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaGerencia;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import model.metadatos.TipoDato;
import model.metadatos.TipoEnumerado;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class Imputacion implements Cargable, Comparable<Imputacion> {

	public static int ESTADO_CONGELADO = 3;
	public static int ESTADO_NO_CREADO = 4;
	public static int ESTADO_SIN_ENVIAR = 5;
	public static int ESTADO_RECHAZADO = 6;
	public static int ESTADO_CANCELADO = 7;
	public static int ESTADO_ENVIADO = 8;
	public static int ESTADO_APROBADO = 9;
	public static int ESTADO_CERRADO = 10;

	public static int IMPUTACION_NO_FRACCIONADA = 0;
	public static int IMPUTACION_FRACCIONADA = 1;
	public static int FRACCION_IMPUTACION = 2;

	public ArrayList<FraccionImputacion> listaFracciones = null;
	public Imputacion imputacionPadre = null;
	public Imputacion imputacionPrevia = null; //para la carga masiva de imputaciones
	public FraccionImputacion imputacionFraccion = null;

	public int tipoImputacion = 0;

	public int id = 0;
	public String codRecurso = "";
	public String nomRecurso = "";
	public Recurso recurso = null;
	public Proyecto proyecto = null;
	public String nomProyecto = "";
	public Sistema sistema = null;
	public MetaGerencia gerencia = null;
	public String nomGerencia = null;
	public MetaConcepto natCoste = null;
	public Date fxInicio = null;
	public Date fxFin = null;
	public float horas = 0;
	public float importe = 0;
	public Tarifa tarifa = null;
	public float fTarifa = 0;
	public String pedido = null;
	public String OT = null;
	public int estado = 0;
	public Date fxEnvioSAP = null;
	public boolean validado = false;
	public int modo = -1;
	public int modo_fichero = -2;
	public Sistema sistemaPrevio = null;

	public Proveedor prov = null;
	public String sProveedor = null;
	public Estimacion estimacionAsociada = null;

	public ArrayList<Imputacion> crearFracciones(ArrayList<FraccionImputacion> listadoFracciones) {
		ArrayList<Imputacion> listaSalida = new ArrayList<Imputacion>();

		this.tipoImputacion = Imputacion.IMPUTACION_FRACCIONADA;

		this.listaFracciones = new ArrayList<FraccionImputacion>();

		Iterator<FraccionImputacion> itFrIm = listadoFracciones.iterator();
		while (itFrIm.hasNext()) {
			FraccionImputacion frImpu = itFrIm.next();
			if (frImpu.idPadre == this.id) {
				this.listaFracciones.add(frImpu);
				frImpu.imputacionPadre = this;
			}
		}

		itFrIm = this.listaFracciones.iterator();

		while (itFrIm.hasNext()) {
			FraccionImputacion frImpu = itFrIm.next();

			if (!frImpu.sistema.codigo.equals(this.sistema.codigo)) {
				Imputacion nueva = this.clone();
				nueva.tipoImputacion = Imputacion.FRACCION_IMPUTACION;
				nueva.imputacionPadre = this;
				nueva.sistema = frImpu.sistema;
				nueva.imputacionFraccion = frImpu;

				listaSalida.add(nueva);
			} else {
				this.imputacionFraccion = frImpu;
			}
		}

		return listaSalida;
	}

	public ArrayList<Imputacion> generaImputacionesDesdeFichero(ArrayList<HashMap<String, Object>> listado,
			Date fInicio, Date fFin) throws Exception {
		ArrayList<Imputacion> salida = new ArrayList<Imputacion>();

		Iterator<HashMap<String, Object>> itListaImputs = listado.iterator();
		while (itListaImputs.hasNext()) {
			HashMap<String, Object> imputs = itListaImputs.next();
			Imputacion i = this.crearDesdeFichero(imputs);
			if (i != null) {
				boolean insertar = true;

				Calendar cPerImpIni = Calendar.getInstance();
				cPerImpIni.setTime(Constantes.inicioMes(i.fxInicio));

				Calendar cPerImpFin = Calendar.getInstance();
				cPerImpFin.setTime(Constantes.finMes(i.fxFin));

				if (fInicio != null) {
					Calendar cInicio = Calendar.getInstance();
					cInicio.setTime(Constantes.finMes(fInicio));
					if (cInicio.after(cPerImpIni))
						insertar = false;
				}

				if (fFin != null) {
					Calendar cFin = Calendar.getInstance();
					cFin.setTime(Constantes.finMes(fFin));
					if (cFin.before(cPerImpFin))
						insertar = false;
				}

				if (i.estado == Imputacion.ESTADO_CANCELADO || i.estado == Imputacion.ESTADO_NO_CREADO
						|| i.estado == Imputacion.ESTADO_RECHAZADO || i.estado == Imputacion.ESTADO_SIN_ENVIAR) {
					insertar = false;
				}
				
				
				if (insertar){
					
					salida.add(i);
				}
			}

		}

		return salida;
	}

	public Imputacion crearDesdeFichero(HashMap<String, Object> imput) throws Exception {
		String proyecto = (String) imput.get("Proyecto");
		if (proyecto == null || "".equals(proyecto))
			return null;
		
		Proyecto p = new Proyecto();
		p = p.getProyectoPPM(proyecto, true);

		Imputacion i = new Imputacion();

		if (p == null)
			return null;
		else
			i.proyecto = p;

		i.id = -5;
		i.codRecurso = (String) imput.get("Usuario");
		i.nomRecurso = (String) imput.get("NombreUsuario");

		Recurso r = new Recurso();
		i.recurso = r.getRecursoPorCodigo(i.codRecurso);

		i.nomProyecto = (String) imput.get("Proyecto");
		i.sistema = null;
		i.nomGerencia = (String) imput.get("Gerencia");

		i.gerencia = MetaGerencia.getPorNombre(i.nomGerencia);

		i.natCoste = null;

		String periodo = (String) imput.get("Periodo");
		String[] periodoCortado = periodo.split(" - ");
		String[] fechaCortada = periodoCortado[0].split("/");

		i.fxInicio = Constantes.inicioMes(new Integer(fechaCortada[1]), new Integer("20" + fechaCortada[2]));

		fechaCortada = periodoCortado[1].split("/");

		i.fxFin = Constantes.finMes(new Integer(fechaCortada[1]), new Integer("20" + fechaCortada[2]));

		i.horas = ((Double) imput.get("Horas")).floatValue();
		i.importe = ((Double) imput.get("Importe")).floatValue();
		i.fTarifa = ((Double) imput.get("Tarifa")).floatValue();

		ArrayList<Tarifa> lTarifas = Tarifa.getTarifas();
		Iterator<Tarifa> itTar = lTarifas.iterator();
		while (itTar.hasNext()) {
			Tarifa t = itTar.next();
			if (Math.abs(t.costeHora - i.fTarifa) <= 0.01) {
				i.tarifa = t;
				break;
			}
		}

		i.sProveedor = (String) imput.get("Proveedor");
		Proveedor prov = new Proveedor();
		if (!"".equals(i.sProveedor))
			i.prov = prov.getProveedorPPM(i.sProveedor);

		i.pedido = "";
		i.OT = "";
		String estado = (String) imput.get("Estado");
		TipoEnumerado tp = TipoEnumerado.getPorDesc(TipoDato.FORMATO_ESTADO_IMPUTACION, estado);

		if (tp != null)
			i.estado = tp.id;
		i.fxEnvioSAP = null;
		i.validado = true;
		i.modo = Imputacion.IMPUTACION_NO_FRACCIONADA;

		return i;
	}

	public Imputacion clone() {
		Imputacion i = new Imputacion();
		i.id = this.id;
		i.recurso = this.recurso;
		i.proyecto = this.proyecto;
		i.sistema = this.sistema;
		i.gerencia = this.gerencia;
		i.natCoste = this.natCoste;
		i.fxInicio = this.fxInicio;
		i.fxFin = this.fxFin;
		i.horas = this.horas;
		i.importe = this.importe;
		i.tarifa = this.tarifa;
		i.pedido = this.pedido;
		i.OT = this.OT;
		i.estado = this.estado;
		i.fxEnvioSAP = this.fxEnvioSAP;
		i.validado = this.validado;
		i.modo = this.modo;

		return i;
	}

	public void setHoras(float horas) {
		this.horas = horas;
	}

	public void setImporte(float importe) {
		this.importe = importe;
	}

	public float getHoras() {
		if (this.tipoImputacion == FRACCION_IMPUTACION || this.tipoImputacion == IMPUTACION_FRACCIONADA )
			return this.imputacionFraccion.getHoras();
			
		return horas;
	}

	public float getImporte() {
		if (this.tipoImputacion == FRACCION_IMPUTACION || this.tipoImputacion == IMPUTACION_FRACCIONADA)
			return this.imputacionFraccion.getImporte();
			
		return importe;
	}

	public ArrayList<Imputacion> listado(Proyecto p, ArrayList<FraccionImputacion> listaFracciones) {

		ArrayList<FraccionImputacion> listadoFracciones = null;

		if (listaFracciones == null)
			listadoFracciones = (new FraccionImputacion()).listado(p);
		else
			listadoFracciones = mergeaListaFracciones(p, listaFracciones);

		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, p.id));

		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> tarifas = consulta.ejecutaSQL("cConsultaImputacionesProyecto", listaParms, this);

		Iterator<Cargable> itProyecto = tarifas.iterator();
		ArrayList<Imputacion> salida = new ArrayList<Imputacion>();

		while (itProyecto.hasNext()) {
			Imputacion est = (Imputacion) itProyecto.next();

			if (est.tipoImputacion == Imputacion.IMPUTACION_FRACCIONADA) {
				ArrayList<Imputacion> listaImputFraccionadas = est.crearFracciones(listadoFracciones);
				salida.addAll(listaImputFraccionadas);
			}

			salida.add(est);
		}

		return salida;
	}

	public ArrayList<FraccionImputacion> mergeaListaFracciones(Proyecto p,
			ArrayList<FraccionImputacion> listaFracciones) {
		ArrayList<FraccionImputacion> listadoFraccionesOriginal = (new FraccionImputacion()).listado(p);
		ArrayList<FraccionImputacion> listadoSalida = new ArrayList<FraccionImputacion>();

		int idPadre = 0;

		if (listaFracciones != null) {
			idPadre = listaFracciones.get(0).idPadre;
		}

		Iterator<FraccionImputacion> itFraccion = listadoFraccionesOriginal.iterator();
		while (itFraccion.hasNext()) {
			FraccionImputacion fI = itFraccion.next();

			if (fI.idPadre != idPadre) {
				listadoSalida.add(fI);
			}
		}

		listadoSalida.addAll(listaFracciones);

		return listadoSalida;
	}

	public boolean existeImputacion(Imputacion i) {
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, i.proyecto.id));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, i.recurso.id));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, i.sistema.id));
		listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_INT, i.natCoste.id));
		listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_LONG, i.fxInicio.getTime()));
		listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_LONG, i.fxFin.getTime()));

		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> tarifas = consulta.ejecutaSQL("cConsultaImputacionesProyecto", listaParms, this);

		if (tarifas.size() > 0)
			return true;
		else
			return false;
	}

	public String toString() {
		return recurso.nombre + " " + proyecto.nombre + " " + importe;
	}

	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;

		try {
			if (salida.get("impId") == null) {
				this.id = 0;
			} else
				this.id = (Integer) salida.get("impId");
		} catch (Exception e) {
			System.out.println();
		}
		try {
			if (salida.get("impRecurso") == null) {
				this.recurso = null;
			} else {
				int idRec = ((Integer) salida.get("impRecurso"));
				this.recurso = Recurso.listadoRecursosEstatico().get(idRec);
			}
		} catch (Exception e) {
			this.recurso = null;
		}
		try {
			if (salida.get("impProyecto") == null) {
				this.proyecto = null;
			} else {
				int idProy = ((Integer) salida.get("impProyecto"));
				this.proyecto = Proyecto.getProyectoEstatico(idProy);
			}
		} catch (Exception e) {
			this.proyecto = null;
		}
		try {
			if (salida.get("impSistema") == null) {
				this.sistema = null;
			} else {
				int idSistema = ((Integer) salida.get("impSistema"));
				this.sistema = Sistema.listado.get(idSistema);
			}
		} catch (Exception e) {
			this.sistema = null;
		}
		try {
			if (salida.get("impGerencia") == null) {
				this.sistema = null;
			} else {
				int idGerencia = ((Integer) salida.get("impGerencia"));
				this.gerencia = MetaGerencia.porId(idGerencia);
			}
		} catch (Exception e) {
			this.sistema = null;
		}
		try {
			if (salida.get("impNatCoste") == null) {
				this.natCoste = null;
			} else {
				int idnatcoste = ((Integer) salida.get("impNatCoste"));
				this.natCoste = MetaConcepto.porId(idnatcoste);
			}
		} catch (Exception e) {
			this.sistema = null;
		}
		try {
			if (salida.get("impFxInicio") == null) {
				this.fxInicio = null;
			} else
				this.fxInicio = (Date) FormateadorDatos.parseaDato((String) salida.get("impFxInicio"),
						FormateadorDatos.FORMATO_FECHA);
		} catch (Exception e) {
			System.out.println();
		}
		try {
			if (salida.get("impFxFin") == null) {
				this.fxFin = null;
			} else
				this.fxFin = (Date) FormateadorDatos.parseaDato((String) salida.get("impFxFin"),
						FormateadorDatos.FORMATO_FECHA);
		} catch (Exception e) {
			System.out.println();
		}
		try {
			if (salida.get("impFxEnvioSAP") == null) {
				this.fxEnvioSAP = null;
			} else
				this.fxEnvioSAP = (Date) FormateadorDatos.parseaDato((String) salida.get("impFxEnvioSAP"),
						FormateadorDatos.FORMATO_FECHA);
		} catch (Exception e) {
			System.out.println();
		}
		try {
			if (salida.get("impHoras") == null) {
				this.horas = 0;
			} else
				this.horas = ((Double) salida.get("impHoras")).floatValue();
		} catch (Exception e) {
			System.out.println();
		}
		try {
			if (salida.get("impImporte") == null) {
				this.importe = 0;
			} else
				this.importe = ((Double) salida.get("impImporte")).floatValue();
		} catch (Exception e) {
			System.out.println();
		}
		try {
			if (salida.get("impTarifa") == null) {
				this.tarifa = null;
			} else {
				double idTarifa = ((Double) salida.get("impTarifa"));
				this.tarifa = Tarifa.porId(new Double(idTarifa).intValue());
			}
		} catch (Exception e) {
			this.tarifa = null;
		}
		try {
			if (salida.get("impOT") == null) {
				this.OT = "";
			} else
				this.OT = (String) salida.get("impOT");
		} catch (Exception e) {
			System.out.println();
		}
		try {
			if (salida.get("impPedido") == null) {
				this.pedido = "";
			} else
				this.pedido = (String) salida.get("impPedido");
		} catch (Exception e) {
			System.out.println();
		}
		try {
			if (salida.get("impEstado") == null) {
				this.estado = 0;
			} else
				this.estado = (Integer) salida.get("impEstado");
		} catch (Exception e) {
			System.out.println();
		}
		try {
			if (salida.get("impTipo") == null) {
				this.tipoImputacion = Imputacion.IMPUTACION_NO_FRACCIONADA;
			} else {
				this.tipoImputacion = ((Integer) salida.get("impTipo"));
			}
		} catch (Exception e) {
			this.tipoImputacion = Imputacion.IMPUTACION_NO_FRACCIONADA;
		}

		return this;
	}

	public void insertImputacion(String idTransaccion) throws Exception {

		ConsultaBD consulta = new ConsultaBD();

		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_ID, 0));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.recurso.id));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, this.proyecto.id));
		listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_INT, this.gerencia.id));
		listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_INT, this.sistema.id));
		listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_INT, this.natCoste.id));
		listaParms.add(new ParametroBD(7, ConstantesBD.PARAMBD_STR, this.pedido == null ? "" : this.pedido));
		listaParms.add(new ParametroBD(8, ConstantesBD.PARAMBD_FECHA, this.fxInicio));
		listaParms.add(new ParametroBD(9, ConstantesBD.PARAMBD_LONG, new Long(this.fxInicio.getTime())));
		listaParms.add(new ParametroBD(10, ConstantesBD.PARAMBD_FECHA, this.fxFin));
		listaParms.add(new ParametroBD(11, ConstantesBD.PARAMBD_LONG, new Long(this.fxFin.getTime())));
		listaParms.add(new ParametroBD(12, ConstantesBD.PARAMBD_REAL, this.horas));
		listaParms.add(new ParametroBD(13, ConstantesBD.PARAMBD_REAL, this.importe));
		if (this.tarifa != null)
			listaParms.add(new ParametroBD(14, ConstantesBD.PARAMBD_INT, this.tarifa.idTarifa));
		listaParms.add(new ParametroBD(15, ConstantesBD.PARAMBD_STR, this.OT == null ? "" : this.OT));
		listaParms.add(new ParametroBD(16, ConstantesBD.PARAMBD_INT, this.estado));
		if (this.fxEnvioSAP != null) {
			listaParms.add(new ParametroBD(17, ConstantesBD.PARAMBD_FECHA, this.fxEnvioSAP));
			listaParms.add(new ParametroBD(18, ConstantesBD.PARAMBD_LONG, new Long(this.fxEnvioSAP.getTime())));
		}
		listaParms.add(new ParametroBD(19, ConstantesBD.PARAMBD_INT, this.tipoImputacion));

		consulta = new ConsultaBD();

		consulta.ejecutaSQL("iAltaimputacion", listaParms, this, idTransaccion);

		RelRecursoSistema rrs = new RelRecursoSistema();
		rrs.recurso = this.recurso;
		rrs.sistema = this.sistema;
		rrs.insertaRelacion(idTransaccion, null);
		
		try {
			Calendar calCertif = Calendar.getInstance();
			calCertif.setTime(this.fxFin);
			
			ParametroProyecto pp = this.proyecto.getValorParametro(MetaParametro.PROYECTO_FX_FIN); 
			Date finProy = (Date) pp.getValor();
			Calendar calProy = Calendar.getInstance();
			calProy.setTime(finProy);
			
			if (calProy.before(calCertif)) {
				pp.valorfecha = this.fxFin;
				pp.actualizaParametro("", true);
			}
			
		} catch (Exception ex) {
			Log.e(ex);
		}
		
		//actualizaTarifa(idTransaccion);
	}

	public void modificaImputacion(String idTransaccion)  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.recurso.id));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, this.proyecto.id));
		listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_INT, this.gerencia.id));
		listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_INT, this.sistema.id));
		listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_INT, this.natCoste.id));
		listaParms.add(new ParametroBD(7, ConstantesBD.PARAMBD_FECHA, this.fxInicio));
		listaParms.add(new ParametroBD(8, ConstantesBD.PARAMBD_LONG, new Long(this.fxInicio.getTime())));
		listaParms.add(new ParametroBD(9, ConstantesBD.PARAMBD_FECHA, this.fxFin));
		listaParms.add(new ParametroBD(10, ConstantesBD.PARAMBD_LONG, new Long(this.fxFin.getTime())));
		listaParms.add(new ParametroBD(11, ConstantesBD.PARAMBD_REAL, this.horas));
		listaParms.add(new ParametroBD(12, ConstantesBD.PARAMBD_REAL, this.importe));
		if (this.tarifa!=null)
			listaParms.add(new ParametroBD(13, ConstantesBD.PARAMBD_INT, this.tarifa.idTarifa));
		
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("uActualizaImputacion", listaParms, this, idTransaccion);
		
		RelRecursoSistema rrs = new RelRecursoSistema();
		rrs.recurso = this.recurso;
		rrs.sistema = this.sistema;
		rrs.insertaRelacion(idTransaccion, this.sistemaPrevio);
		
		try {
			Calendar calCertif = Calendar.getInstance();
			calCertif.setTime(this.fxFin);
			
			ParametroProyecto pp = this.proyecto.getValorParametro(MetaParametro.PROYECTO_FX_FIN); 
			Date finProy = (Date) pp.getValor();
			Calendar calProy = Calendar.getInstance();
			calProy.setTime(finProy);
			
			if (calProy.before(calCertif)) {
				pp.valorfecha = this.fxFin;
				pp.actualizaParametro("", true);
			}
			
		} catch (Exception ex) {
			Log.e(ex);
		}

		
		//actualizaTarifa(idTransaccion);
	}
	/*
	private void actualizaTarifa(String idTransaccion) throws Exception{
		if (this.tarifa!=null) {
			Tarifa t = Tarifa.tarifaPorDefecto(this.recurso, null, false);
			
			boolean insertar = false;
			
			if (t==null){
				insertar=true;
				t = new Tarifa();
			}
			else 
				if (rrt.tarifa.costeHora!=this.tarifa.costeHora) insertar = true;
			
			if (rrt!=null && rrt.tarifa!=null && this.tarifa!=null) {
				Calendar cRRT = Calendar.getInstance();
				cRRT.setTime(rrt.tarifa.fFinVig);
				
				Calendar cThis = Calendar.getInstance();
				cThis.setTime(this.tarifa.fFinVig);
				
				if (cRRT.after(cThis)) insertar = false;
				else insertar = true;
			}
			
			if (insertar) {
				Calendar c = Calendar.getInstance();
				c.setTime(this.fxFin);
				
				rrt.tarifa = this.tarifa;
				rrt.mes = c.get(Calendar.MONTH)+1;
				rrt.anio = c.get(Calendar.YEAR);
				rrt.insertaRelacion(idTransaccion);
			}
		}
	}*/

	public void fraccionaImputacion(ArrayList<FraccionImputacion> listaImputaciones, Imputacion i) throws Exception {

		ConsultaBD consulta = new ConsultaBD();
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();

		String idTransaccion = "fraccionaImputacion" + Constantes.fechaActual().getTime();

		if (listaImputaciones.size() != 0) {
			listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, i.id));
			listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, Imputacion.IMPUTACION_FRACCIONADA));
		} else {
			listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, i.id));
			listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, Imputacion.IMPUTACION_NO_FRACCIONADA));
		}

		consulta = new ConsultaBD();
		consulta.ejecutaSQL("uActualizaTipoImputacion", listaParms, this, idTransaccion);

		FraccionImputacion fI = new FraccionImputacion();
		fI.insertaFraccionImputacion(listaImputaciones, i, idTransaccion);

		consulta.ejecutaTransaccion(idTransaccion);

	}

	public void borraImputacion(String idTransaccion) throws Exception {

		ConsultaBD consulta = new ConsultaBD();

		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));

		consulta = new ConsultaBD();

		if (idTransaccion == null)
			consulta.ejecutaSQL("dDelImputacion", listaParms, this);
		else
			consulta.ejecutaSQL("dDelImputacion", listaParms, this, idTransaccion);

		RelRecursoSistema rrs = new RelRecursoSistema();
		rrs.recurso = this.recurso;
		rrs.sistema = this.sistema;
		rrs.insertaRelacion(idTransaccion, this.sistemaPrevio);
	}

	@Override
	public int compareTo(Imputacion arg0) {

		if (arg0.fxFin.equals(this.fxFin)) {
			return arg0.codRecurso.compareTo(codRecurso);
		} else {
			return arg0.fxFin.compareTo(this.fxFin);
		}

	}

}
