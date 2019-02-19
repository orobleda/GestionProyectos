package model.constantes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Constantes {
	public static final Date fechaFinal = init();
	
	public static final int USUARIORESPONSABLE = 3;
	
	public static final String COLOR_ROJO = "Red";
	public static final String COLOR_AMARILLO = "Yellow";
	public static final String COLOR_VERDE = "Green";
	public static final String COLOR_GRIS = "Silver";
	
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
		c.set(anio, 11,30,0,0,0);
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
	
	public Constantes() {
		cargaMeses();
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
