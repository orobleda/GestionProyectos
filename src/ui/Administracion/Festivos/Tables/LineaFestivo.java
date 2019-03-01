package ui.Administracion.Festivos.Tables;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.constantes.FormateadorDatos;
import model.metadatos.Festivo;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;

public class LineaFestivo extends ParamTable implements Tableable  {
	 
    public ArrayList<Festivo> semanaFestivos= null;
    public boolean modificado = false;
    
    public LineaFestivo(Date diaInicial) {
    	semanaFestivos = new ArrayList<Festivo>();
    	
    	Calendar c = Calendar.getInstance();
		c.setTime((Date) diaInicial.clone());
		int comienzo = c.get(Calendar.DAY_OF_WEEK)-2;
		
		if (comienzo<0) comienzo=6;
		
		for (int i=0;i<comienzo;i++){
			semanaFestivos.add(new Festivo());
		}
		
		Festivo f = new Festivo();
		f.dia = diaInicial;
		try{
			if (Festivo.listado.containsKey(FormateadorDatos.formateaDato(f.dia,FormateadorDatos.FORMATO_FECHA))) {
				f.esFestivo = true;
			}		
    	} catch (Exception e) {}
    	semanaFestivos.add(f);
    	
    	for (int i=semanaFestivos.size();i<7;i++) {
    		c = Calendar.getInstance();
    		c.setTime((Date) f.dia.clone());
    		c.add(Calendar.DAY_OF_MONTH, 1);
    		
    		if (c.get(Calendar.DAY_OF_MONTH)==1){
    			semanaFestivos.add(new Festivo());
    		} else {
    			f = new Festivo();
    			f.dia = c.getTime();
    			try {
	    			if (Festivo.listado.containsKey(FormateadorDatos.formateaDato(f.dia,FormateadorDatos.FORMATO_FECHA))) {
	    				f.esFestivo = true;
	    			}
    			} catch (Exception e) {}
        		semanaFestivos.add(f);
    		}
    	}
    	
        setConfig();
    }
    
    public LineaFestivo() {
    	setConfig();
    }
    
    public Date diaFinal(){
    	if (semanaFestivos.size()<7) {
    		return null;
    	}
    	
    	Calendar c = Calendar.getInstance();
    	if (semanaFestivos.get(6).dia==null) return null;
		c.setTime((Date) semanaFestivos.get(6).dia.clone());
		c.add(Calendar.DAY_OF_MONTH, 1);
		
		if (c.get(Calendar.DAY_OF_MONTH)==1) return null;
		
		return c.getTime();
    }
    
    public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	configuracionTabla.put(Festivo.LUNES, new ConfigTabla(Festivo.LUNES, Festivo.LUNES, false,0, false));
    	configuracionTabla.put(Festivo.MARTES, new ConfigTabla(Festivo.MARTES, Festivo.MARTES, false,1, false));
    	configuracionTabla.put(Festivo.MIERCOLES, new ConfigTabla(Festivo.MIERCOLES, Festivo.MIERCOLES, false,2, false));
    	configuracionTabla.put(Festivo.JUEVES, new ConfigTabla(Festivo.JUEVES, Festivo.JUEVES, false,3, false));
    	configuracionTabla.put(Festivo.VIERNES, new ConfigTabla(Festivo.VIERNES, Festivo.VIERNES, false,4, false));
    	configuracionTabla.put(Festivo.SABADO, new ConfigTabla(Festivo.SABADO, Festivo.SABADO, false,5, false));
    	configuracionTabla.put(Festivo.DOMINGO, new ConfigTabla(Festivo.DOMINGO, Festivo.DOMINGO, false,6, false));
    }
    
	@Override
	public Object muestraSelector() {
		return null;
	}
    
    @Override
	public Tableable toTableable(Object o) {
    	try {
			Festivo f = (Festivo) o;
			return new LineaFestivo(f.dia);
    	} catch (Exception e){
    		try {
    			LineaFestivo f = (LineaFestivo) o;
    			return f;
    		} catch (Exception ex) {
    			return null;
    		}
    	}
	}
    
	public void set(String campo, String valor){
		try {
			int orden = Festivo.orden(campo);
			Festivo f = semanaFestivos.get(orden);
			if (f.dia!=null) {
				if (f.esFestivo) f.esFestivo = false;
				else f.esFestivo = true;
			}
			
		} catch ( Exception e) {}
			
	}
    
	public String get(String campo) {
		try {
			int orden = Festivo.orden(campo);
			Festivo f = semanaFestivos.get(orden);
			return f.diaMes();
			
		} catch ( Exception e) {
			return "";
		}
	}
	
	@Override
	public AnchorPane getFilaEmbebida(TableRowDataFeatures<Tableable> expander) {
		return null;
	}
	
	  	
}
