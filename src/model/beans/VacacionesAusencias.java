package model.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class VacacionesAusencias  implements Cargable{

	public static final int AUSENCIAS = 1;
	public static final int VACACIONES = 2;
	
	public ArrayList<VacacionesAusencias> listadoVacacionesAusencias = null;
	
	public int id = 0;
	public int ausencia = 0;
	public float horas = 0;
	public Date fxDia = null;
	public long tsDia = 0;
	public Recurso recurso = null;
	
	public int mes = 0;
	public int anio = 0;
	public int dia = 0;
	
	
	public VacacionesAusencias(){
		listadoVacacionesAusencias = new ArrayList<VacacionesAusencias>();
	}
	
	public HashMap<Long,VacacionesAusencias > get(int flag) {
		Iterator<VacacionesAusencias> it  = listadoVacacionesAusencias.iterator();
		
		HashMap<Long,VacacionesAusencias > salida = new HashMap<Long,VacacionesAusencias >();
		
		while (it.hasNext()) {
			VacacionesAusencias vaAux = it.next();
			if (vaAux.ausencia == flag) {
				salida.put(vaAux.fxDia.getTime(), vaAux);
			}			
		}
		
		return salida;
	}
	
	public ArrayList<VacacionesAusencias> listado (Recurso recurso, int mes, int anio) {
		Calendar cInicio = Calendar.getInstance();
		cInicio.set(anio, mes-1, 1,0,0,0);
		
		Calendar cFin = Calendar.getInstance();
		cFin.set(anio, mes-1, cInicio.getActualMaximum(Calendar.DAY_OF_MONTH),23,59,59);
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_LONG, cInicio.getTime().getTime()));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_LONG, cFin.getTime().getTime()));
		
		if (recurso!=null) {
			listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, recurso.id));
		}
		
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> vacaciAusencias = consulta.ejecutaSQL("cConsultaVacasAusencias", listaParms, this);
		
		listadoVacacionesAusencias = new ArrayList<VacacionesAusencias>();
		
		Iterator<Cargable> itVA = vacaciAusencias.iterator();
		
		while (itVA.hasNext()) {
			VacacionesAusencias va = (VacacionesAusencias) itVA.next();
			listadoVacacionesAusencias.add(va);			
		}
				
		return listadoVacacionesAusencias;
	}
	
	public ArrayList<VacacionesAusencias> listado (Recurso recurso, int anio) {
		Calendar cInicio = Calendar.getInstance();
		cInicio.set(anio, 0, 1,0,0,0);
		
		Calendar cFin = Calendar.getInstance();
		cFin.set(anio, 11, cInicio.getActualMaximum(Calendar.DAY_OF_MONTH),23,59,59);
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_LONG, cInicio.getTime().getTime()));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_LONG, cFin.getTime().getTime()));
		
		if (recurso!=null) {
			listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, recurso.id));
		}
		
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> vacaciAusencias = consulta.ejecutaSQL("cConsultaVacasAusencias", listaParms, this);
		
		listadoVacacionesAusencias = new ArrayList<VacacionesAusencias>();
		
		Iterator<Cargable> itVA = vacaciAusencias.iterator();
		
		while (itVA.hasNext()) {
			VacacionesAusencias va = (VacacionesAusencias) itVA.next();
			listadoVacacionesAusencias.add(va);			
		}
				
		return listadoVacacionesAusencias;
	}
	
	public void updateLista (ArrayList<VacacionesAusencias> listado, int mes, int anio, Recurso r, String idTransaccion) throws Exception{
		
		
		Calendar cInicio = Calendar.getInstance();
		cInicio.set(anio, mes-1, 1,0,0,0);
		
		Calendar cFin = Calendar.getInstance();
		cFin.set(anio, mes-1, cInicio.getActualMaximum(Calendar.DAY_OF_MONTH),23,59,59);
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_LONG, cInicio.getTime().getTime()));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_LONG, cFin.getTime().getTime()));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, r.id));
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("cLimpiaVacasAusencias", listaParms, this, idTransaccion);
		
		Iterator<VacacionesAusencias> itLista = listado.iterator();
		
		while (itLista.hasNext()) {
			VacacionesAusencias va = itLista.next();
			
			listaParms = new ArrayList<ParametroBD>();
			listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_ID, va.id));
			listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, va.ausencia));
			listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_REAL, va.horas));
			listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_FECHA, va.fxDia));
			listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_LONG, va.tsDia));
			listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_INT, va.recurso.id));
			
			consulta = new ConsultaBD();
			consulta.ejecutaSQL("cInsertaVacasAusencias", listaParms, this, idTransaccion);
		}				
	}
	
	public String toString() {
		try {
			return ""+this.ausencia + " " + this.recurso.nombre + " " + this.horas + " " + this.fxDia.toString();
		} catch (Exception e) {
			return "";
		}
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try { if (salida.get("vaId")==null)     { this.id = 0;            } else this.id = (Integer) salida.get("vaId");  } catch (Exception e){System.out.println();}
		try { if (salida.get("vaAusencia")==null)   { this.ausencia = -1;    } else this.ausencia = (Integer) salida.get("vaAusencia");     } catch (Exception e){System.out.println();}
		try { if (salida.get("vaHoras")==null)  { this.horas = -1; } else this.horas =  new Float((Double) salida.get("vaHoras"));   } catch (Exception e){System.out.println();}
		try { if (salida.get("vaTsDia")==null) { this.tsDia = 0;       } else this.tsDia = ((Double) salida.get("vaTsDia")).longValue();                       } catch (Exception e){System.out.println();}
		try { 
			if (salida.get("vaFxDia")==null)  { 
				this.fxDia = null;           
			} else {
				this.fxDia = (Date) FormateadorDatos.parseaDato((String) salida.get("vaFxDia"),FormateadorDatos.FORMATO_FECHA);
				Calendar c = Calendar.getInstance();
				c.setTime(this.fxDia);
				this.mes = c.get(Calendar.MONTH)+1;
				this.anio = c.get(Calendar.YEAR);
				this.dia = c.get(Calendar.DAY_OF_MONTH);
			}
		} catch (Exception e){e.printStackTrace();}
		
		
		try { 
			  if (salida.get("vaRecurso")==null){ 
				  	this.recurso = null;          
			  } 
			  else {
				  this.recurso = Recurso.listadoRecursosEstatico().get((Integer) salida.get("vaRecurso"));
			  }     
		} catch (Exception e){
			e.printStackTrace();
		}
		
					
		return this;
	}
}
