package model.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.metadatos.TipoDato;
import model.metadatos.TipoEnumerado;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class Evento implements Cargable{

	public String descripcion;
	public TipoEnumerado tipo;
	public float tsInicio;
	public Date fxInicio;
	public float tsLimite;
	public String nombre;
	public String codAdjunto;
	public Date fxFin;
	public boolean eventoDiario;
	public int criticidad;
	public Date fxLimite;
	public float tsFin;
	public int id;
	public int idProyEvento;
	public int urgencia;
	
	public HashMap<String,Parametro> listaParametros = null;
	public ArrayList<Proyecto> listaProyectos = null;
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
		 	if (salida.get("eveDescripcion")==null)  { 
		 		this.descripcion = null;
			} else {
		 		this.descripcion = (String) salida.get("eveDescripcion");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("eveTipo")==null)  { 
		 		this.tipo = null;
			} else {
		 		String codigo = (String) salida.get("eveTipo");
		 		this.tipo = TipoEnumerado.getPorCod(TipoDato.FORMATO_TIPO_EVENTO, codigo);
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("eveTsinicio")==null)  { 
		 		this.tsInicio = 0;
			} else {
		 		this.tsInicio = (Float) salida.get("eveTsinicio");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("eveFxinicio")==null)  { 
		 		this.fxInicio = null;
			} else {
				Double d = (Double) salida.get("eveTsinicio");
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(d.longValue());
				Date fecha = c.getTime();
		 		this.fxInicio = fecha;
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("eveTslimite")==null)  { 
		 		this.tsLimite = 0;
			} else {
		 		this.tsLimite = (Float) salida.get("eveTslimite");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("eveNombre")==null)  { 
		 		this.nombre = null;
			} else {
		 		this.nombre = (String) salida.get("eveNombre");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("eveCodadjunto")==null)  { 
		 		this.codAdjunto = null;
			} else {
		 		this.codAdjunto = (String) salida.get("eveCodadjunto");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("eveFxfin")==null)  { 
		 		this.fxFin = null;
			} else {
				Double d = (Double) salida.get("eveTsfin");
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(d.longValue());
				Date fecha = c.getTime();
		 		this.fxFin = fecha;
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("eveEventodiario")==null)  { 
		 		this.eventoDiario = false;
			} else {
		 		this.eventoDiario = Constantes.toNumBoolean((Integer) salida.get("eveEventodiario"));
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("eveCriticidad")==null)  { 
		 		this.criticidad = 0;
			} else {
		 		this.criticidad = (Integer) salida.get("eveCriticidad");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("eveFxlimite")==null)  { 
		 		this.fxLimite = null;
			} else {
				Double d = (Double) salida.get("eveTsLimite");
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(d.longValue());
				Date fecha = c.getTime();
		 		this.fxLimite = fecha;
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("eveTsfin")==null)  { 
		 		this.tsFin = 0;
			} else {
		 		this.tsFin = (Float) salida.get("eveTsfin");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("eveId")==null)  { 
		 		this.id = 0;
			} else {
		 		this.id = (Integer) salida.get("eveId");
		 		Parametro p = new Parametro();
		 		this.listaParametros = p.dameParametros(this.getClass().getSimpleName(), this.id);
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("eveIdproyevento")==null)  { 
		 		this.idProyEvento = 0;
			} else {
		 		this.idProyEvento = (Integer) salida.get("eveIdproyevento");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("eveUrgencia")==null)  { 
		 		this.urgencia = 0;
			} else {
		 		this.urgencia = (Integer) salida.get("eveUrgencia");
			}
		} catch (Exception ex) {}
				
		return this;
	}
	
	public ArrayList<Evento> listaEventos() {
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<Cargable> eventos = consulta.ejecutaSQL("cConsultaEvento", listaParms, this);
			
		Iterator<Cargable> itEvt = eventos.iterator();
		ArrayList<Evento> salida = new ArrayList<Evento>();
			
		while (itEvt.hasNext()) {
			Evento evt = (Evento) itEvt.next();
			
			RelProyectoEvento rpe = new RelProyectoEvento();
			rpe.idEvento = evt.id;
			rpe.evt = evt;
			evt.listaProyectos = rpe.listaProyectosAsociados();
			
			salida.add(evt);
		}
				
		return salida;
	}
	
	public void borrarEvento(String idTransaccion) throws Exception{			
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.id));
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("dBorraEvento", listaParms, this, idTransaccion);	
		
		Iterator<Parametro> itParam = this.listaParametros.values().iterator();
		while (itParam.hasNext()) {
			Parametro param = itParam.next();
			param.bajaParametro(idTransaccion);
		}
		
		RelProyectoEvento rpe = new RelProyectoEvento();
		rpe.idEvento = this.id;
		rpe.borrarProyectosAsociados(idTransaccion);
	}
	
	public void updateEvento(String idTransaccion) throws Exception{
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT,this.id));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_STR, this.descripcion));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_STR, this.tipo.codigo));
		listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_LONG, this.fxInicio.getTime()));
		listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_FECHA, this.fxInicio));
		if (this.fxLimite!=null){
			listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_LONG, this.fxLimite.getTime()));
			listaParms.add(new ParametroBD(12, ConstantesBD.PARAMBD_FECHA, this.fxLimite));
		} else {
			listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_LONG, this.fxFin.getTime()));
			listaParms.add(new ParametroBD(12, ConstantesBD.PARAMBD_FECHA, this.fxFin));
		}
		listaParms.add(new ParametroBD(7, ConstantesBD.PARAMBD_STR, this.nombre));
		listaParms.add(new ParametroBD(8, ConstantesBD.PARAMBD_STR, this.codAdjunto));
		listaParms.add(new ParametroBD(9, ConstantesBD.PARAMBD_FECHA, this.fxFin));
		listaParms.add(new ParametroBD(10, ConstantesBD.PARAMBD_INT, Constantes.toNumBoolean(this.eventoDiario)));
		listaParms.add(new ParametroBD(11, ConstantesBD.PARAMBD_INT, this.criticidad));
		listaParms.add(new ParametroBD(13, ConstantesBD.PARAMBD_LONG, this.fxFin.getTime()));
		listaParms.add(new ParametroBD(14, ConstantesBD.PARAMBD_ID, this.id));
		listaParms.add(new ParametroBD(15, ConstantesBD.PARAMBD_INT, this.idProyEvento));
		listaParms.add(new ParametroBD(16, ConstantesBD.PARAMBD_INT, this.urgencia));
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("uActualizaEvento", listaParms, this, idTransaccion);	
		
		Iterator<Parametro> itParam = this.listaParametros.values().iterator();
		while (itParam.hasNext()) {
			Parametro param = itParam.next();
			param.actualizaParametro(idTransaccion, false);
		}
		
		RelProyectoEvento rpe = new RelProyectoEvento();
		rpe.idEvento = this.id;
		rpe.updateEvento(idTransaccion, this.listaProyectos);
		
	}
	
	public void insertEvento(String idTransaccion)  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
				
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_STR, this.descripcion));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_STR, this.tipo.codigo));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_LONG, this.fxInicio.getTime()));
		listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_FECHA, this.fxInicio));
		if (this.fxLimite!=null){
			listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_LONG, this.fxLimite.getTime()));
			listaParms.add(new ParametroBD(11, ConstantesBD.PARAMBD_FECHA, this.fxLimite));
		} else {
			listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_LONG, this.fxFin.getTime()));
			listaParms.add(new ParametroBD(11, ConstantesBD.PARAMBD_FECHA, this.fxFin));
		}		
		listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_STR, this.nombre));
		listaParms.add(new ParametroBD(7, ConstantesBD.PARAMBD_STR, this.codAdjunto));
		listaParms.add(new ParametroBD(8, ConstantesBD.PARAMBD_FECHA, this.fxFin));
		listaParms.add(new ParametroBD(9, ConstantesBD.PARAMBD_INT, Constantes.toNumBoolean(this.eventoDiario)));
		listaParms.add(new ParametroBD(10, ConstantesBD.PARAMBD_INT, this.criticidad));
		listaParms.add(new ParametroBD(12, ConstantesBD.PARAMBD_LONG, this.fxFin.getTime()));
		listaParms.add(new ParametroBD(13, ConstantesBD.PARAMBD_ID, this.id));
		listaParms.add(new ParametroBD(14, ConstantesBD.PARAMBD_INT, this.idProyEvento));
		listaParms.add(new ParametroBD(15, ConstantesBD.PARAMBD_INT, this.urgencia));
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("iInsertaEvento", listaParms, this, idTransaccion);
		
		this.id = ParametroBD.ultimoId;
		
		Iterator<Parametro> itParam = this.listaParametros.values().iterator();
		while (itParam.hasNext()) {
			Parametro param = itParam.next();
			param.idEntidadAsociada = this.id;
			param.actualizaParametro(idTransaccion, false);
		}
		
		RelProyectoEvento rpe = new RelProyectoEvento();
		rpe.idEvento = this.id;
		rpe.insertEvento(idTransaccion, this.listaProyectos);
	}

}
