package model.constantes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import model.beans.Parametro;
import model.beans.Recurso;
import model.metadatos.MetaParametro;

public class Constantes {
	public static final Date fechaFinal = init();
	
	public static Boolean TRUE = new Boolean(true);
	public static Boolean FALSE = new Boolean(false);
	
	public static int NUM_TRUE = 1;
	public static int NUM_FALSE = 0;
	
	public static final int USUARIORESPONSABLE = 3;
	
	public static final String COLOR_ROJO = "Red";
	public static final String COLOR_AMARILLO = "Yellow";
	public static final String COLOR_VERDE = "Green";
	public static final String COLOR_GRIS = "Silver";
	
	public static Recurso getAdministradorSistema() {
		Parametro p = new Parametro();
		return (Recurso) p.getParametro(MetaParametro.PARAMETRO_ADMIN).getValor();
	}
	
	public static Date init() {
		Calendar c = Calendar.getInstance();
		c.set(2100, 11,30,0,0,0);
		return c.getTime();
	}
	
	public static Date inicioAnio(int anio) {
		Calendar c = Calendar.getInstance();
		c.set(anio,00,01,0,0,0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	public static Date finAnio(int anio) {
		Calendar c = Calendar.getInstance();
		c.set(anio, 11,31,0,0,0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	public static Date inicioMes(int mes, int anio) {
		Calendar c = Calendar.getInstance();
		c.set(anio,mes-1,01,0,0,0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	public static Date finMes(int mes, int anio) {
		Calendar c = Calendar.getInstance();
		c.set(anio, mes-1,1,0,0,0); 
		c.set(Calendar.DAY_OF_MONTH,c.getActualMaximum(Calendar.DAY_OF_MONTH));
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	public static Date inicioMes(Date fecha) {
		Calendar cAux = Calendar.getInstance();
		cAux.setTime(fecha);
		
		Calendar c = Calendar.getInstance();
		c.set(cAux.get(Calendar.YEAR), cAux.get(Calendar.MONTH),1,0,0,0); 
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	public static Date finMes(Date fecha) {
		Calendar cAux = Calendar.getInstance();
		cAux.setTime(fecha);
		
		Calendar c = Calendar.getInstance();
		c.set(cAux.get(Calendar.YEAR), cAux.get(Calendar.MONTH),1,0,0,0); 
		c.set(Calendar.DAY_OF_MONTH,c.getActualMaximum(Calendar.DAY_OF_MONTH));
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	public Constantes() {
		cargaMeses();
	}
	
	public static ArrayList<Boolean> opcionesYesNo() {
		ArrayList<Boolean> salida = new ArrayList<Boolean>();
		salida.add(Constantes.TRUE);
		salida.add(Constantes.FALSE);
		return salida;
	}
	
	public static int toNumBoolean(Boolean estado) {
		if (estado) return Constantes.NUM_TRUE;
		else return Constantes.NUM_FALSE;
	}
	
	public static Boolean toNumBoolean(int estado) {
		if (estado==Constantes.NUM_TRUE) return Constantes.TRUE;
		else return Constantes.FALSE;
	}

	public static Date fechaActual() {
		Parametro p = new Parametro();
		
		Boolean bol = (Boolean) p.getParametro(MetaParametro.PARAMETRO_FIJAR_FX_ACTUAL).getValor();
		
		if (bol) {
			Date fxActual = (Date) p.getParametro(MetaParametro.PARAMETRO_FX_ACTUAL_FIJADA).getValor();	
			return fxActual;
		} else {
			return new Date();
		}
		
	}
	
	public static int numMes(String valor) {
		cargaMeses();
		return meses.indexOf(valor)+1;
	}
	
	public static String nomMes(int valor) {
		cargaMeses();
		return meses.get(valor);
	}
	
	private static void cargaMeses() {
		if (meses.size()==0){
			meses.add("Enero");
			meses.add("Febrero");
			meses.add("Marzo");
			meses.add("Abril");
			meses.add("Mayo");
			meses.add("Junio");
			meses.add("Julio");
			meses.add("Agosto");
			meses.add("Septiembre");
			meses.add("Octubre");
			meses.add("Noviembre");
			meses.add("Diciembre");
		}
	}
	
	public static ArrayList<String> meses = new ArrayList<String>();
}
