package model.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import model.metadatos.Festivo;
import model.metadatos.MetaJornada;

public class JornadasMes {

	public static final int JORNADAS = 1;
	public static final int VACACIONES = 2;
	public static final int AUSENCIAS = 4;
	public static final int TOTAL = 4;
	
	public ArrayList<Float> jornadas = null;
	public int configTipo = 0;
	public int mes = 0;
	public int anio = 0;
	public Recurso recurso = null;
	public VacacionesAusencias vacasAusencias = null;
	public float horasAcumuladas = 0;
	
	
	public JornadasMes(int tipo, Recurso recurso, int mes, int anio, VacacionesAusencias vacasAusencias){
		jornadas = new ArrayList<Float>();
		this.mes = mes;
		this.anio = anio;
		this.vacasAusencias = vacasAusencias;
		this.recurso = recurso;
		
		if (tipo == JornadasMes.JORNADAS) {
			cargaJornadas(tipo, recurso, mes, anio);
		}
		
		if (tipo == JornadasMes.VACACIONES) {
			cargaVacaciones(tipo, recurso, mes, anio);
		}
		
		if (tipo == JornadasMes.AUSENCIAS) {
			cargaAusencias(tipo, recurso, mes, anio);
		}
		
		if (tipo == JornadasMes.TOTAL) {
			cargaAusencias(tipo, recurso, mes, anio);
		}
	}

	public JornadasMes(JornadasMes jornada, JornadasMes vacaciones, JornadasMes ausencias, int tipo, Recurso recurso, int mes, int anio){
		jornadas = new ArrayList<Float>();
		this.mes = mes;
		this.anio = anio;
		this.recurso = recurso;
		
		cargaTotal(jornada, vacaciones, ausencias);
	}
	
	public JornadasMes(Recurso recurso, int anio){
		jornadas = new ArrayList<Float>();
		this.mes = -1;
		this.anio = anio;
		this.recurso = recurso;
	}
	
	public JornadasMes(int tipo, Recurso r, int mes, int anio){
		jornadas = new ArrayList<Float>();
		this.mes = mes;
		this.anio = anio;
		this.recurso = r;
		this.configTipo = tipo;
		this.horasAcumuladas = 0;
	}
	
	public HashMap<Integer,HashMap<Integer, JornadasMes>> getListadoAnual () {
		HashMap<Integer,HashMap<Integer, JornadasMes>> salida = new HashMap<Integer,HashMap<Integer, JornadasMes>>();
		
		VacacionesAusencias vaAu = new VacacionesAusencias();
		ArrayList<VacacionesAusencias> listadoVacasAusencias = vaAu.listado(this.recurso, anio);
		
		for (int i=0;i<12;i++) {
			HashMap<Integer, JornadasMes> mesIterado = new HashMap<Integer, JornadasMes>();
			salida.put(i+1, mesIterado);
			
			JornadasMes jd = new JornadasMes(JornadasMes.JORNADAS, this.recurso, i+1, this.anio);
			jd.cargaJornadas(JornadasMes.JORNADAS, recurso, i+1, this.anio);
			
			mesIterado.put(JornadasMes.JORNADAS, jd);
			mesIterado.put(JornadasMes.AUSENCIAS, new JornadasMes(JornadasMes.AUSENCIAS, this.recurso, i+1, this.anio));
			mesIterado.put(JornadasMes.VACACIONES, new JornadasMes(JornadasMes.VACACIONES, this.recurso, i+1, this.anio));
			mesIterado.put(JornadasMes.TOTAL, jd);
		}
		
		Iterator<VacacionesAusencias> itVa = listadoVacasAusencias.iterator();
		
		while (itVa.hasNext()) {
			VacacionesAusencias va = itVa.next();
			HashMap<Integer, JornadasMes> mesIterado = salida.get(va.mes);			
			
			if (va.ausencia == VacacionesAusencias.AUSENCIAS) {
				JornadasMes j = mesIterado.get(JornadasMes.AUSENCIAS);
				j.horasAcumuladas += va.horas;
				JornadasMes jd = mesIterado.get(JornadasMes.TOTAL);
				jd.horasAcumuladas -= va.horas;
			} else {
				JornadasMes jdTotal = mesIterado.get(JornadasMes.JORNADAS);
				float horasJornada = jdTotal.jornadas.get(va.dia);
				JornadasMes j = mesIterado.get(JornadasMes.VACACIONES);
				j.horasAcumuladas += horasJornada;				
				JornadasMes jd = mesIterado.get(JornadasMes.TOTAL);
				jd.horasAcumuladas -= horasJornada;
			} 
		}
		
		return salida;
	}
	
	public int salvaAusenciasVacaciones( JornadasMes vacaciones, JornadasMes ausencias, String idTransaccion) throws Exception{
		ArrayList<VacacionesAusencias> vaList = new ArrayList<VacacionesAusencias>();
		
		VacacionesAusencias va = null;
		
		Iterator<Float> itVacaciones = vacaciones.jornadas.iterator();
		int contDia = 1;
		
		while (itVacaciones.hasNext()){
			float horas = itVacaciones.next();
			
			if (horas==100) {
				Calendar c = Calendar.getInstance();
				c.set(this.anio, this.mes-1, contDia);
				
				va = new VacacionesAusencias();
				va.id = -1;
				va.ausencia = VacacionesAusencias.VACACIONES;
				va.fxDia = c.getTime();
				va.tsDia = c.getTime().getTime();
				va.mes = this.mes;
				va.anio = this.anio;
				va.recurso = this.recurso;
				va.horas = 0;
				
				vaList.add(va);
			}			
			contDia++;			
		}
		
		Iterator<Float> itAusencias = ausencias.jornadas.iterator();
		contDia = 1;
		
		while (itAusencias.hasNext()){
			float horas = itAusencias.next();
			
			if (horas>0) {
				Calendar c = Calendar.getInstance();
				c.set(this.anio, this.mes-1, contDia);
				
				va = new VacacionesAusencias();
				va.id = -1;
				va.ausencia = VacacionesAusencias.AUSENCIAS;
				va.fxDia = c.getTime();
				va.tsDia = c.getTime().getTime();
				va.mes = this.mes;
				va.anio = this.anio;
				va.recurso = this.recurso;
				va.horas = horas;
				
				vaList.add(va);
			}			
			contDia++;			
		}
		
		va = new VacacionesAusencias();
		va.updateLista(vaList, this.mes, this.anio, this.recurso,idTransaccion);
		
		return vaList.size();
	}
	
	private void cargaTotal(JornadasMes jornada, JornadasMes vacaciones, JornadasMes ausencias) {
		for (int i=0;i<jornada.jornadas.size();i++){
			if (jornada.jornadas.get(i)==-1) {
				this.jornadas.add(new Float(-1));
			} else {
				if (vacaciones.jornadas.get(i)!=100 && ausencias.jornadas.get(i)<0)  {
					float horas = jornada.jornadas.get(i);
					this.jornadas.add(horas);
					horasAcumuladas+= horas;
				} else {
					if (vacaciones.jornadas.get(i)==100) {
						this.jornadas.add(new Float(0));	
					} else {
						float horas = jornada.jornadas.get(i)-ausencias.jornadas.get(i);
						this.jornadas.add(horas);
						horasAcumuladas+= horas;
					}
				}
			}
		}
	}
	
	private void cargaVacaciones(int tipo, Recurso recurso, int mes, int anio) {
		HashMap<Long,VacacionesAusencias> listaVacaciones = this.vacasAusencias.get(VacacionesAusencias.VACACIONES);
		
		Calendar cal = Calendar.getInstance();
		cal.set(anio, mes-1, 1,0,0,0);
				
		while (mes == (cal.get(Calendar.MONTH)+1)) {
			if (Festivo.esFestivo(cal.getTime())) {
				jornadas.add(new Float(-1));
			} else {
				if (cal.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY && cal.get(Calendar.DAY_OF_WEEK)!=Calendar.SATURDAY  ) {
					if (listaVacaciones.containsKey((cal.getTime().getTime()-cal.get(Calendar.MILLISECOND)))){
						jornadas.add(new Float(100));
					} else {
						
						jornadas.add(new Float(-2));
					}					
				} else {
					jornadas.add(new Float(-1));
				}
			}
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
	}
	
	private void cargaAusencias(int tipo, Recurso recurso, int mes, int anio) {
		HashMap<Long,VacacionesAusencias> listaVacaciones = this.vacasAusencias.get(VacacionesAusencias.AUSENCIAS);
		
		Calendar cal = Calendar.getInstance();
		cal.set(anio, mes-1, 1,0,0,0);
				
		while (mes == (cal.get(Calendar.MONTH)+1)) {
			if (cal.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY && cal.get(Calendar.DAY_OF_WEEK)!=Calendar.SATURDAY  ) {
					if (listaVacaciones.containsKey((cal.getTime().getTime()-cal.get(Calendar.MILLISECOND)))){
						VacacionesAusencias va = listaVacaciones.get(cal.getTime().getTime()-cal.get(Calendar.MILLISECOND));
						jornadas.add(va.horas);
					} else {
						jornadas.add(new Float(-3));
					}					
			} else {
				jornadas.add(new Float(-1));
			}
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
	}
	
	private void cargaJornadas(int tipo, Recurso recurso, int mes, int anio) {
		int codJornada = 0;
		try {
			codJornada = new Integer((String) recurso.getValorParametro(ParametroRecurso.PARAM_JORNADA));
		} catch (Exception e) {
		}
		
		horasAcumuladas = 0;
		
		MetaJornada mj = new MetaJornada();
		mj.listado();
		
		mj = MetaJornada.listaMetaJornadas.get(codJornada);
		
		Calendar cal = Calendar.getInstance();
		cal.set(anio, mes-1, 1);
				
		while (mes == (cal.get(Calendar.MONTH)+1)) {
			if (Festivo.esFestivo(cal.getTime())) {
				jornadas.add(new Float(-1));
			} else {
				if (cal.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY && cal.get(Calendar.DAY_OF_WEEK)!=Calendar.SATURDAY  ) {
					jornadas.add(mj.horasDia(cal.getTime()));
					horasAcumuladas+=mj.horasDia(cal.getTime());
				} else {
					jornadas.add(new Float(-1));
				}
			}
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
	}
}
