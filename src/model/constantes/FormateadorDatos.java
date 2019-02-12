package model.constantes;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;

import model.metadatos.TipoProyecto;

public class FormateadorDatos {
	
	public static final int FORMATO_TXT = 1;
	public static final int FORMATO_INT = 2;
	public static final int FORMATO_REAL = 3;
	public static final int FORMATO_MONEDA = 4;
	public static final int FORMATO_URL = 5;
	public static final int FORMATO_FECHA = 6;
	public static final int FORMATO_FORMATO_PROYECTO = 7;
	public static final int FORMATO_PORC = 9;
	public static final int FORMATO_TIPO_PROYECTO = 8;
	
	public static String formateaDato(String Dato, int formato) throws Exception {
		if ("".equals(Dato)) return Dato;
		
		if (FormateadorDatos.FORMATO_TXT == formato){
			return Dato;
		}
		if (FormateadorDatos.FORMATO_INT == formato){
			DecimalFormat formatea = new DecimalFormat("###,###");

			String salida = formatea.format(formatea.parse(Dato));
			
			return salida;
		}
		if (FormateadorDatos.FORMATO_REAL == formato){
			Dato = Dato.replace(".", "");
			
			DecimalFormat formatea = new DecimalFormat("###,###.##");

			String salida = formatea.format(formatea.parse(Dato));
			
			return salida;
		}
		if (FormateadorDatos.FORMATO_MONEDA == formato){
			Dato = Dato.replace(".", "");
			Dato = Dato.replace("€", "");
			DecimalFormat formatea = new DecimalFormat("###,###.##");

			String salida = formatea.format(formatea.parse(Dato));
			
			return salida + " €";
		}
		if (FormateadorDatos.FORMATO_URL == formato){
			return Dato;
		}
		if (FormateadorDatos.FORMATO_FECHA == formato){
			String salida = "";
			
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				salida = sdf.format(sdf.parse(Dato));
			} catch (Exception e) {
				SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
				SimpleDateFormat salidadf = new SimpleDateFormat("dd/MM/yyyy");
				salida = salidadf.format(sdf.parse(Dato));	
			}
			
			return salida;
		}
		if (FormateadorDatos.FORMATO_PORC == formato){
			Dato = Dato.replace(".", "");
			Dato = Dato.replace("%", "");
			DecimalFormat formatea = new DecimalFormat("###,###.##");

			String salida = formatea.format(formatea.parse(Dato));
			
			return salida + " %";
		}
		
		if (FormateadorDatos.FORMATO_TIPO_PROYECTO == formato){
			try {
				return TipoProyecto.listado.get(new Integer(Dato)).toString();
			} catch (Exception e) {
				return Dato.toString();
			}
			
		}
				
		return Dato;
	}
	
	public static String formateaDato(Object Dato, int formato) throws Exception {
		if ("".equals(Dato)) return Dato.toString();
		
		if (FormateadorDatos.FORMATO_TXT == formato){
			return Dato.toString();
		}
		if (FormateadorDatos.FORMATO_INT == formato){
			DecimalFormat formatea = new DecimalFormat("###,###");

			String salida = formatea.format(formatea.parse(Dato.toString()));
			
			return salida;
		}
		if (FormateadorDatos.FORMATO_REAL == formato){
			
			DecimalFormat formatea = new DecimalFormat("###,###.##");

			String salida = formatea.format(Dato);
			
			return salida;
		}
		if (FormateadorDatos.FORMATO_MONEDA == formato){
			DecimalFormat formatea = new DecimalFormat("###,###.##");
			
			try {
				String salida = formatea.format((Float) Dato);
				return salida + " €";
			} catch (Exception e) {
				String salida = formatea.format(formatea.parse((String) Dato));
				return salida + " €";
			}
		
			
			
			
		}
		if (FormateadorDatos.FORMATO_PORC == formato){
			DecimalFormat formatea = new DecimalFormat("###,###.##");
			
			try {
				String salida = formatea.format((Float) Dato);
				return salida + " %";
			} catch (Exception e) {
				String salida = formatea.format(formatea.parse((String) Dato));
				return salida + " %";
			}
		
			
			
			
		}
		if (FormateadorDatos.FORMATO_URL == formato){
			return Dato.toString();
		}
		if (FormateadorDatos.FORMATO_FECHA == formato){
			String salida = "";
			
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				salida = sdf.format(sdf.parse(Dato.toString()));
			} catch (Exception e) {
				SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
				SimpleDateFormat salidadf = new SimpleDateFormat("dd/MM/yyyy");
				salida = salidadf.format(sdf.parse(Dato.toString()));	
			}
			
			return salida;
		}
		if (FormateadorDatos.FORMATO_FORMATO_PROYECTO == formato){
			return Dato.toString();
		}
		if (FormateadorDatos.FORMATO_TIPO_PROYECTO == formato){
			try {
				return TipoProyecto.listado.get((Integer)Dato).toString();
			} catch (Exception e) {
				return Dato.toString();
			}
			
		}
		
		return null;
	}
	
	public static Object parseaDato(String Dato, int formato) throws Exception{
		if ("".equals(Dato)) return Dato;
		
		if (FormateadorDatos.FORMATO_TXT == formato){
			return Dato;
		}
		if (FormateadorDatos.FORMATO_INT == formato){
			DecimalFormat formatea = new DecimalFormat("###,###");
			Integer salida = formatea.parse(Dato).intValue();
			return salida;
		}
		if (FormateadorDatos.FORMATO_REAL == formato){
			DecimalFormat formatea = new DecimalFormat("###,###.##");
			Float salida = formatea.parse(Dato).floatValue();
			return salida;
		}
		if (FormateadorDatos.FORMATO_MONEDA == formato){
			Dato.replace("€", "");			
			DecimalFormat formatea = new DecimalFormat("###,###.##");
			Float salida = formatea.parse(Dato).floatValue();
			
			return salida;
		}
		if (FormateadorDatos.FORMATO_PORC == formato){
			Dato.replace("%", "");			
			DecimalFormat formatea = new DecimalFormat("###,###.##");
			Float salida = formatea.parse(Dato).floatValue();
			
			return salida;
		}
		if (FormateadorDatos.FORMATO_URL == formato){
			return Dato;
		}
		
		if (FormateadorDatos.FORMATO_FECHA == formato){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			return sdf.parse(Dato);
		}
		
		if (FormateadorDatos.FORMATO_TIPO_PROYECTO == formato){
			try {
				Iterator<TipoProyecto> tpProyecto =  TipoProyecto.listado.values().iterator();
				while (tpProyecto.hasNext()) 
				{
					TipoProyecto tp = tpProyecto.next();
					if (tp.descripcion.equals(Dato)) return tp;
					
				}
				return null;
			} catch (Exception e) {
				return Dato.toString();
			}
			
		}
		
		return Dato;
	}
}
