package model.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaGerencia;
import model.metadatos.Sistema;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class Estimacion implements Cargable{
	
	public static final int APROBADA = 1;
	public static final int DESAPROBADA = 0;
	
	public int id = 0;
	public Recurso recurso = null;
	public Proyecto proyecto = null;
	public Sistema sistema = null;
	public MetaGerencia gerencia = null;
	public MetaConcepto natCoste = null;
	public Date fxInicio = null;
	public Date fxFin = null;
	public float horas = 0;
	public float importe = 0;	
	public int aprobacion = 0;
	public int modo = -1;
	
	public int mes = 0;
	public int anio = 0;

	public float importeProvisionado = 0;
	
	public ArrayList<Estimacion> listado (Proyecto p) {		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, p.id));

		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> tarifas = consulta.ejecutaSQL("cConsultaEstimacionesProyecto", listaParms, this);
		
		Iterator<Cargable> itProyecto = tarifas.iterator();
		ArrayList<Estimacion> salida = new ArrayList<Estimacion>();
		
		while (itProyecto.hasNext()) {
			Estimacion est = (Estimacion) itProyecto.next();
			salida.add(est);
		}
				
		return salida;
	}
	
	public ArrayList<Estimacion> listado (Recurso r, int anio) {
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, r.id));
		listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_LONG, Constantes.inicioAnio(anio).getTime()));
		listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_LONG, Constantes.finAnio(anio).getTime()));

		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> tarifas = consulta.ejecutaSQL("cConsultaEstimacionesProyecto", listaParms, this);
		
		Iterator<Cargable> itProyecto = tarifas.iterator();
		ArrayList<Estimacion> salida = new ArrayList<Estimacion>();
		
		while (itProyecto.hasNext()) {
			Estimacion est = (Estimacion) itProyecto.next();
			salida.add(est);
		}
				
		return salida;
	}
	
	public boolean existeEstimacion (Estimacion e) {		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, e.proyecto.id));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, e.recurso.id));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, e.sistema.id));
		listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_INT, e.natCoste.id));
		listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_LONG, e.fxInicio.getTime()));
		listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_LONG, e.fxFin.getTime()));

		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> tarifas = consulta.ejecutaSQL("cConsultaEstimacionesProyecto", listaParms, this);
		
		if (tarifas.size()>0) return true;
		else return false;
	}
	
	public void cambiaEstadoEstimacion (int id, int estadoAprobacion) {		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, id));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, estadoAprobacion));

		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("uCambiaEstadoEstimacion", listaParms, this);				
	}
	
	public String toString() {
		return recurso.nombre + " " + proyecto.nombre + " " + importe;
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try { if (salida.get("estHId")==null)     { this.id = 0;            } else this.id = (Integer) salida.get("estHId");                           } catch (Exception e){System.out.println();}
		try { if (salida.get("estHRecurso")==null)   { this.recurso = null;    } else {
			int idRec = ((Integer) salida.get("estHRecurso"));
			this.recurso = Recurso.listadoRecursosEstatico().get(idRec);
		}} catch (Exception e){this.recurso = null;}
		try { if (salida.get("estHProyecto")==null)   { this.proyecto = null;    } else {
			int idProy = ((Integer) salida.get("estHProyecto"));
			this.proyecto = Proyecto.getProyectoEstatico(idProy);
		}} catch (Exception e){this.proyecto = null;}
		try { if (salida.get("estHSistema")==null)   { this.sistema = null;    } else {
			int idSistema = ((Integer) salida.get("estHSistema"));
			this.sistema = Sistema.listado.get(idSistema);
		}} catch (Exception e){this.sistema = null;}
		try { if (salida.get("estHGerencia")==null)   { this.sistema = null;    } else {
			int idGerencia = ((Integer) salida.get("estHGerencia"));
			this.gerencia = MetaGerencia.porId(idGerencia);
		}} catch (Exception e){this.sistema = null;}
		try { if (salida.get("estHNatCoste")==null)   { this.natCoste = null;    } else {
			int idnatcoste = ((Integer) salida.get("estHNatCoste"));
			this.natCoste = MetaConcepto.porId(idnatcoste);
		}} catch (Exception e){this.sistema = null;}
		try { if (salida.get("estHFxInicio")==null) { 
					this.fxInicio = null;       
				} else {
					this.fxInicio = (Date) FormateadorDatos.parseaDato((String)salida.get("estHFxInicio"),FormateadorDatos.FORMATO_FECHA);
					Calendar c = Calendar.getInstance();
					c.setTime(this.fxInicio);
					this.mes = c.get(Calendar.MONTH)+1;
					this.anio = c.get(Calendar.YEAR);
				}
		} catch (Exception e){System.out.println();}
		
		try { if (salida.get("estHFxFin")==null) { this.fxFin = null;       } else this.fxFin = (Date) FormateadorDatos.parseaDato((String)salida.get("estHFxFin"),FormateadorDatos.FORMATO_FECHA); } catch (Exception e){System.out.println();}
		try { if (salida.get("estHHoras")==null)     { this.horas = 0;            } else this.horas = ((Double) salida.get("estHHoras")).floatValue();    } catch (Exception e){System.out.println();}
		try { if (salida.get("estHImporte")==null)     { this.importe = 0;            } else this.importe = ((Double) salida.get("estHImporte")).floatValue();    } catch (Exception e){System.out.println();}
			try { 
				if (salida.get("estAprobacion")==null)   { 
					this.aprobacion = Estimacion.DESAPROBADA;    
				} 
				else {	
					this.aprobacion = ((Integer) salida.get("estAprobacion"));	
				}
			} catch (Exception e){
				this.aprobacion = Estimacion.DESAPROBADA;
			}		
		
		return this;
	}
	
	public void insertEstimacion(String idTransaccion)  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_ID, 0));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.recurso.id));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, this.proyecto.id));
		listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_INT, this.sistema.id));
		listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_INT, this.gerencia.id));
		listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_INT, this.natCoste.id));
		listaParms.add(new ParametroBD(7, ConstantesBD.PARAMBD_FECHA, this.fxInicio));
		listaParms.add(new ParametroBD(8, ConstantesBD.PARAMBD_LONG, new Long(Constantes.inicioMes(this.fxInicio).getTime())));
		listaParms.add(new ParametroBD(9, ConstantesBD.PARAMBD_FECHA, this.fxFin));
		listaParms.add(new ParametroBD(10, ConstantesBD.PARAMBD_LONG, new Long(Constantes.finMes(this.fxInicio).getTime())));
		listaParms.add(new ParametroBD(11, ConstantesBD.PARAMBD_REAL, this.horas));
		listaParms.add(new ParametroBD(12, ConstantesBD.PARAMBD_REAL, this.importe));
		listaParms.add(new ParametroBD(13, ConstantesBD.PARAMBD_INT, this.aprobacion));
		
		consulta = new ConsultaBD();
		
		if (idTransaccion!= null)	consulta.ejecutaSQL("iAltaEstimacion", listaParms, this, idTransaccion);
		else consulta.ejecutaSQL("iAltaEstimacion", listaParms, this);
	}
	
	public void modificaEstimacion(String idTransaccion)  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.recurso.id));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, this.proyecto.id));
		listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_INT, this.sistema.id));
		listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_INT, this.gerencia.id));
		listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_INT, this.natCoste.id));
		listaParms.add(new ParametroBD(7, ConstantesBD.PARAMBD_FECHA, this.fxInicio));
		listaParms.add(new ParametroBD(8, ConstantesBD.PARAMBD_LONG, new Long(Constantes.inicioMes(this.fxInicio).getTime())));
		listaParms.add(new ParametroBD(9, ConstantesBD.PARAMBD_FECHA, this.fxFin));
		listaParms.add(new ParametroBD(10, ConstantesBD.PARAMBD_LONG, new Long(Constantes.finMes(this.fxInicio).getTime())));
		listaParms.add(new ParametroBD(11, ConstantesBD.PARAMBD_REAL, this.horas));
		listaParms.add(new ParametroBD(12, ConstantesBD.PARAMBD_REAL, this.importe));
		
		consulta = new ConsultaBD();
		if (idTransaccion!= null)	consulta.ejecutaSQL("uActualizaEstimacion", listaParms, this, idTransaccion);
		else consulta.ejecutaSQL("uActualizaEstimacion", listaParms, this);
	}
	
	public void borraEstimacion(String idTransaccion)  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));
		
		consulta = new ConsultaBD();
		
		if (idTransaccion!= null)	consulta.ejecutaSQL("dDelEstimacion", listaParms, this, idTransaccion);
		else consulta.ejecutaSQL("dDelEstimacion", listaParms, this);
	}
	
	public boolean equals(Estimacion est) {
		try {
			if (this.mes == est.mes && this.anio == est.anio && this.proyecto.id == est.proyecto.id &&
				this.recurso.id == est.recurso.id && this.sistema.id == est.sistema.id)
				return true;
			else 
				return false;
		} catch (Exception e) {
			return false;
		}
	}
}
