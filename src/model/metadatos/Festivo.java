package model.metadatos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class Festivo implements Cargable, Loadable  {
	public int id=0;
	public Date dia = null;
	
	public static String LUNES = "L";
	public static String MARTES = "M";
	public static String MIERCOLES = "X";
	public static String JUEVES = "J";
	public static String VIERNES = "V";
	public static String SABADO = "S";
	public static String DOMINGO = "D";
	
	public boolean esFestivo = false;
	public boolean modificado = false;
	public static HashMap<String, Festivo> listado = new HashMap<String, Festivo>(); 
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try{
		
			this.id = (Integer) salida.get("festId");
			this.dia = (Date) FormateadorDatos.parseaDato((String) salida.get("festFx"),FormateadorDatos.FORMATO_FECHA);
			
		} catch (Exception e) {
			
		}
		
		return this;
	}

	@Override
	public void load() {
		listado = new HashMap<String, Festivo>();	
		
		try{
			ConsultaBD consulta = new ConsultaBD();
			ArrayList<Cargable> festivos = consulta.ejecutaSQL("cConsultaFestivos", null, this);
			
			Iterator<Cargable> it = festivos.iterator();
			while (it.hasNext()){
				Festivo fest = (Festivo) it.next();
				listado.put(FormateadorDatos.formateaDato(fest.dia,FormateadorDatos.FORMATO_FECHA), fest);
			}
		} catch (Exception e) {
			
		}
	}
	
	public static boolean esFestivo(Date fecha) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String cadFecha = sdf.format(fecha);
		
		if (listado.containsKey(cadFecha)) return true;
		else return false;
	}
	
	public void actualizaFestivo() {
		try {
			if (this.esFestivo) {
				ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
				listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_ID, this.id));
				listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_STR, FormateadorDatos.formateaDato(this.dia,FormateadorDatos.FORMATO_FECHA)));
				ConsultaBD consulta = new ConsultaBD();
				consulta.ejecutaSQL("iAltaFestivos", listaParms, this);
			} else {
				ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
				listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_STR, FormateadorDatos.formateaDato(this.dia,FormateadorDatos.FORMATO_FECHA)));
				ConsultaBD consulta = new ConsultaBD();
				consulta.ejecutaSQL("dBorraFestivo", listaParms, this);
			}
		} catch (Exception e) {
			
		}
	}


	@Override
	public HashMap<?, ?> getListado() {
		return EstadoProyecto.listado;
	}

	@Override
	public int getId() {
		return this.id;
	}
	
	public String diaMes(){
		Calendar c = Calendar.getInstance();
		c.setTime(dia);
		return c.get(GregorianCalendar.DAY_OF_MONTH) + "";
	}	
	
	public static int orden(String dia) {
		if (Festivo.LUNES.equals(dia)) return 0;
		if (Festivo.MARTES.equals(dia)) return 1;
		if (Festivo.MIERCOLES.equals(dia)) return 2;
		if (Festivo.JUEVES.equals(dia)) return 3;
		if (Festivo.VIERNES.equals(dia)) return 4;
		if (Festivo.SABADO.equals(dia)) return 5;
		if (Festivo.DOMINGO.equals(dia)) return 6;
		
		return -1;
	}
	
	public String toString(){
		try {
			return FormateadorDatos.formateaDato(dia, FormateadorDatos.FORMATO_FECHA);
		} catch (Exception e) {
			return "";
		}
	}
}
