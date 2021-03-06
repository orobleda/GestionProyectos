package model.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import controller.AnalizadorPresupuesto;
import controller.Log;
import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.metadatos.EstadoProyecto;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import model.metadatos.TipoProyecto;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class Proyecto implements Cargable, Comparable<Proyecto>{
	public int id = 0;
	public String nombre = "";
	
	public Presupuesto presupuestoActual = null;
	public Presupuesto presupuestoMaxVersion = null;
	
	public AnalizadorPresupuesto ap = null;
	
	public HashMap<String, ? extends Parametro> listadoParametros = null;
	public static HashMap<Integer, Proyecto> listaProyecto = null;
	public ArrayList<EstadoProyecto> estadosProyecto = null;

	public ArrayList<FaseProyecto> fasesProyecto = null;
	
	public static final int NEUTRO = 0;
	public static final int ANIADIR = 1;
	public static final int MODIFICAR = 2;
	public static final int ELIMINAR = 3;
	
	public int modoFiltro = 0;
	
	public int modo = 0;
	
	public boolean apunteContable = false;
	
	public Proyecto clone() {
		Proyecto p = new Proyecto();
		p.id = this.id;
		p.modo = this.modo;
		p.nombre = this.nombre;
		if (this.presupuestoActual!=null)
			p.presupuestoActual = this.presupuestoActual.clone();;
		p.apunteContable = this.apunteContable;
		
		if (this.fasesProyecto!=null) {
			p.fasesProyecto = new ArrayList<FaseProyecto>();
			Iterator<FaseProyecto> itFp = this.fasesProyecto.iterator();
			while (itFp.hasNext()) {
				FaseProyecto fp = itFp.next();
				p.fasesProyecto.add(fp.clone());
			}
		}
		
		return p;
	}

	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
			if (salida.get("proyId")==null) {
				this.id = 0;
			} else this.id = (Integer) salida.get("proyId");
			this.nombre = (String) salida.get("proyNom");
		} catch (Exception e) {
			
		}
		
		return this;
	}
	
	public static Proyecto getProyectoEstaticoCargaForzada(int idProyecto) {
			listaProyecto = new HashMap<Integer, Proyecto>();
			
			Iterator<Proyecto> itProy = new Proyecto().listadoProyectos().iterator();
			while (itProy.hasNext()) {
				Proyecto p = itProy.next();
				listaProyecto.put(p.id, p);
			}
			
			return listaProyecto.get(idProyecto);
	}
	
	public static ArrayList<Proyecto> getProyectosEstaticoTipo(int tipoProyecto) {
		ArrayList<Proyecto> salida = new ArrayList<Proyecto>();
		
		listaProyecto = Proyecto.getProyectosEstatico();
		ParametroProyecto pp = null;
		TipoProyecto tp = null;
		
		Iterator<Proyecto> itProy = listaProyecto.values().iterator();
		
		while (itProy.hasNext()) {
			Proyecto p = (Proyecto) itProy.next();
			try {
				try {
					pp = p.getValorParametro(MetaParametro.PROYECTO_TIPO_PROYECTO);
					tp = (TipoProyecto) pp.getValor();
				} catch (Exception e) {				
					p.cargaProyecto();
					pp = p.getValorParametro(MetaParametro.PROYECTO_TIPO_PROYECTO);
					tp = (TipoProyecto) pp.getValor();
				}
				
				if (tp!=null) {
					if (tipoProyecto == TipoProyecto.ID_TODO)
						salida.add(p);
					else
						if (tipoProyecto == TipoProyecto.ID_PROYEVOLS) {
							if (tp.codigo == TipoProyecto.ID_EVOLUTIVO || tp.codigo == TipoProyecto.ID_PROYECTO){
								salida.add(p);					
							}
						} else {
							if (tipoProyecto == tp.codigo) salida.add(p);
						}
				}
			} catch (Exception e) {
				Log.e(e);
			}
		}
		
		return salida;
	}
	
	public static HashMap<Integer,Proyecto> getProyectosEstatico() {
		if (listaProyecto==null) {
			listaProyecto = new HashMap<Integer, Proyecto>();
			
			Iterator<Proyecto> itProy = new Proyecto().listadoProyectos().iterator();
			while (itProy.hasNext()) {
				Proyecto p = itProy.next();
				listaProyecto.put(p.id, p);
			}
			
			return listaProyecto;
		} else {
			return listaProyecto;
		}
	}
	
	public static Proyecto getProyectoEstatico(int idProyecto) {
		if (listaProyecto==null) {
			listaProyecto = new HashMap<Integer, Proyecto>();
			
			Iterator<Proyecto> itProy = new Proyecto().listadoProyectos().iterator();
			while (itProy.hasNext()) {
				Proyecto p = itProy.next();
				listaProyecto.put(p.id, p);
			}
			
			return listaProyecto.get(idProyecto);
		} else {
			return listaProyecto.get(idProyecto);
		}
	}
	
	public ArrayList<Proyecto> listadoProyectos() {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> proyectos = consulta.ejecutaSQL("cListaProyectos", null, this);
		
		Iterator<Cargable> itProyecto = proyectos.iterator();
		ArrayList<Proyecto> salida = new ArrayList<Proyecto>();
		
		while (itProyecto.hasNext()) {
			Proyecto p = (Proyecto) itProyecto.next();
			salida.add(p);
		}
		
        return salida;
	}
	
	public Proyecto getProyectoPPM(String descPPM, boolean soloAbiertos) {
		HashMap<Integer,Proyecto> proyectos =Proyecto.getProyectosEstatico();
		
		Iterator<Proyecto> itProyecto = proyectos.values().iterator();
		while (itProyecto.hasNext()) {
			Proyecto p = itProyecto.next();
			ParametroProyecto pp = p.getValorParametro(MetaParametro.PROYECTO_NOMPPM);
			
			if (pp!=null) {
				
				if (descPPM.trim().equals(pp.getValor())) {
					boolean insertar = true;
					if (soloAbiertos) {
						pp = p.getValorParametro(MetaParametro.PROYECTO_CERRADO);
						insertar = ! (Boolean) pp.getValor();
					}
					
					if (insertar) return p;
				}
			}
		}
		
		return null;
	}
	
	public String rutaCertificacion() throws Exception{
		Parametro p = new Parametro();
		String ruta = p.getParametroRuta(MetaParametro.PARAMETRO_RUTA_REPOSITORIO);
				
		ParametroProyecto pp = this.getValorParametro(MetaParametro.PROYECTO_TIPO_PROYECTO);
		TipoProyecto tp = (TipoProyecto) pp.getValor();
		ruta += "\\"+tp.descripcion;
		
		pp = this.getValorParametro(MetaParametro.PROYECTO_ACRONPROY);
		ruta+= "\\"+pp.getValor()+"\\Certificaciones\\";
		
		
		return ruta;
	}
	
	public ArrayList<Proyecto> listadoProyectosGGP(boolean cerrado) {
		ArrayList<Proyecto> proyectos = listadoProyectosGGP();
		ArrayList<Proyecto> salida = new ArrayList<Proyecto>();
		
		Iterator<Proyecto> itProyecto = proyectos.iterator();
		while (itProyecto.hasNext()) {
			Proyecto p = itProyecto.next();
			ParametroProyecto pp = (ParametroProyecto) ParametroProyecto.getParametro(p.getClass().getSimpleName(),p.id,MetaParametro.PROYECTO_CERRADO);
			
			if ( (Boolean) pp.getValor()) {
				salida.add(p);
			}
		}
		
		return salida;
	}
	
	public ArrayList<Proyecto> listadoProyectosGGP() {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> proyectos = consulta.ejecutaSQL("cListaProyectos", null, this);
		
		Iterator<Cargable> itProyecto = proyectos.iterator();
		ArrayList<Proyecto> salida = new ArrayList<Proyecto>();
		
		while (itProyecto.hasNext()) {
			Proyecto p = (Proyecto) itProyecto.next();
			try {
				p.cargaProyecto();
				ParametroProyecto pp = p.getValorParametro(MetaParametro.PROYECTO_TIPO_PROYECTO);
				TipoProyecto tp = (TipoProyecto) pp.getValor();
				if (tp.codigo == TipoProyecto.ID_EVOLUTIVO || tp.codigo == TipoProyecto.ID_PROYECTO)
					salida.add(p);
				
			} catch (Exception  ex) {
				Log.e(ex);
			}
			
		}
		
        return salida;
	}
	
	public ArrayList<Proyecto> listadoDemandas() {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> proyectos = consulta.ejecutaSQL("cListaProyectos", null, this);
		
		Iterator<Cargable> itProyecto = proyectos.iterator();
		ArrayList<Proyecto> salida = new ArrayList<Proyecto>();
		
		while (itProyecto.hasNext()) {
			Proyecto p = (Proyecto) itProyecto.next();
			try {
				p.cargaProyecto();
				ParametroProyecto pp = p.getValorParametro(MetaParametro.PROYECTO_TIPO_PROYECTO);
				TipoProyecto tp = (TipoProyecto) pp.getValor();
				if (tp.codigo == TipoProyecto.ID_DEMANDA)
					salida.add(p);
				
			} catch (Exception  ex) {
				Log.e(ex);
			}
			
		}
		
        return salida;
	}
	
	public ArrayList<Proyecto> getDemandasAsociadas() throws Exception{
		if (this.listadoParametros!=null || this.listadoParametros.size()>0)
			this.cargaProyecto();
		if (this.presupuestoActual==null) {
			Presupuesto pres = new Presupuesto();
			this.presupuestoActual = pres.dameUltimaVersionPresupuesto(this);
		}
		
		ParametroProyecto pp = this.getValorParametro(MetaParametro.PROYECTO_TIPO_PROYECTO);
		TipoProyecto tp = (TipoProyecto) pp.getValor();
		
		if (tp.codigo == TipoProyecto.ID_EVOLUTIVO || tp.codigo == TipoProyecto.ID_PROYECTO) {
			RelProyectoDemanda rpd = new RelProyectoDemanda();
			rpd.proyecto = this;
			rpd.pres = this.presupuestoActual;
			
			ArrayList<RelProyectoDemanda> listado =  rpd.buscaRelacion();
			
			if (listado!=null && listado.size()>0) {
				return listado.get(0).listaDemandas;
			}
		}
		
		return new ArrayList<Proyecto>();
	}
	
	public void cargaProyecto() throws Exception{			
		this.listadoParametros = new HashMap<String, ParametroProyecto> ();
		
		ParametroProyecto pp = new ParametroProyecto();
		this.listadoParametros = pp.dameParametros(this.getClass().getSimpleName(), this.id);
		
		this.estadosProyecto = EstadoAsignadoProyecto.dameEstados(this); 		
	}
	
	public boolean isCerrado() {
		if (this.estadosProyecto==null) {
			this.estadosProyecto = EstadoAsignadoProyecto.dameEstados(this); 
		}
		
		Iterator<EstadoProyecto> itEstado = this.estadosProyecto.iterator();
		
		while (itEstado.hasNext()) {
			EstadoProyecto estP = itEstado.next();
			
			if (estP.estFinal == EstadoProyecto.FINAL ) {
				return true;
			}
		}
		
		return false;
	}
	
	public int maxIdProyecto() {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> proyectos = consulta.ejecutaSQL("cMaxIdProyecto", null, this);
		
		Proyecto p = (Proyecto) proyectos.get(0);
        return p.id+1;
	}
	
	public ParametroProyecto getValorParametro(String keyParam) {
		if (this.listadoParametros==null) {
			ParametroProyecto pp = new ParametroProyecto();
			this.listadoParametros = pp.dameParametros(this.getClass().getSimpleName(), this.id);
		} 
		
		return (ParametroProyecto) this.listadoParametros.get(keyParam);
	}
	
	public void cargaFasesProyecto() {
		FaseProyecto fp = new FaseProyecto();
		fp.idProyecto = this.id;
		this.fasesProyecto = fp.listado();
	}
	
	public float coberturaDemandaFases(Proyecto pDemanda, boolean apunteContable, Sistema s) {
		float porcAcumulado = 0;
		
		if (this.fasesProyecto==null) return 0;
		
		Iterator<FaseProyecto> itFP = this.fasesProyecto.iterator();
		while (itFP.hasNext()) {
			FaseProyecto fp = itFP.next();
			porcAcumulado += fp.coberturaDemandaFases(pDemanda, apunteContable, s);
		}
		
		return porcAcumulado;
	} 
	
	public void nuevoProyecto(String idTransaccion) {
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		ParametroBD pBD = new ParametroBD();
		pBD.id = 1;
		pBD.tipo = ConstantesBD.PARAMBD_ID;
		pBD.valorInt = 1;
		listaParms.add(pBD);
		pBD = new ParametroBD();
		pBD.id = 2;
		pBD.tipo = ConstantesBD.PARAMBD_STR;
		pBD.valorStr = this.nombre;
		listaParms.add(pBD);
		
		ConsultaBD consulta = new ConsultaBD();
		
		if (idTransaccion == null)
			consulta.ejecutaSQL("iAltaProy", listaParms, this);
		else 
			consulta.ejecutaSQL("iAltaProy", listaParms, this,idTransaccion);
		
		this.id = ParametroBD.ultimoId;
			
	}
	
	public void actualizaProyecto(String idTransaccion) {
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		ParametroBD pBD = new ParametroBD();
		pBD.id = 1;
		pBD.tipo = ConstantesBD.PARAMBD_INT;
		pBD.valorInt = this.id;
		listaParms.add(pBD);
		pBD = new ParametroBD();
		pBD.id = 2;
		pBD.tipo = ConstantesBD.PARAMBD_STR;
		pBD.valorStr = this.nombre;
		listaParms.add(pBD);
		
		ConsultaBD consulta = new ConsultaBD();		
		consulta.ejecutaSQL("uActualizaProyecto", listaParms, this,idTransaccion);
	}
	
	
	public boolean altaProyecto(Proyecto p, String idTransaccion) {

		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		ParametroBD pBD = new ParametroBD();
		pBD.id = 1;
		pBD.tipo = ConstantesBD.PARAMBD_ID;
		pBD.valorInt = p.id;
		listaParms.add(pBD);
		pBD = new ParametroBD();
		pBD.id = 2;
		pBD.tipo = ConstantesBD.PARAMBD_STR;
		pBD.valorStr = p.nombre;
		listaParms.add(pBD);
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("iAltaProy", listaParms, this, idTransaccion);
		
		p.id = ParametroBD.ultimoId;
		
		return true;
	}
	
	public void bajaProyecto(String idTransaccion) throws Exception {
		if (this.listadoParametros==null) {
			this.cargaProyecto();
		}
		
		ParametroProyecto pp = null;
		
		Iterator<? extends Parametro> itParametros = this.listadoParametros.values().iterator();
		
		while (itParametros.hasNext()) {
			pp = (ParametroProyecto) itParametros.next();
			pp.bajaParametro(idTransaccion);
		}
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));
		ConsultaBD consulta = new ConsultaBD();
		
		if (idTransaccion==null)
			consulta.ejecutaSQL("dDelProyecto", listaParms, this);
		else
			consulta.ejecutaSQL("dDelProyecto", listaParms, this,idTransaccion);
	}
	
	
	
	@Override
	public String toString() {
		return this.id + " - " + this.nombre;
	}

	@Override
	public int compareTo(Proyecto arg0) {
		try {
			Date fFin = (Date) this.getValorParametro(MetaParametro.PROYECTO_FX_FIN).getValor();;
			Date fFin2 = (Date) arg0.getValorParametro(MetaParametro.PROYECTO_FX_FIN).getValor();;
			return fFin.compareTo(fFin2);
		} catch (Exception e) {
			return 0;
		}
	}

}
