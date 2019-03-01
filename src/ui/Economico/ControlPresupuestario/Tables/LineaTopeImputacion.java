package ui.Economico.ControlPresupuestario.Tables;

import java.util.ArrayList;
import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.beans.TopeImputacion;
import model.constantes.FormateadorDatos;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Economico.ControlPresupuestario.ModificaTope;
import ui.interfaces.Tableable;

public class LineaTopeImputacion extends ParamTable implements Tableable  {
	
	public static final String ANIO = "Año";
	public static final String RESTO = "A Resto";
	public static final String CANTIDAD = "Cantidad";	
	public static final String PORCENTAJE = "Porcentaje";
	
	public ArrayList<Float> resumen = null;
	
	public TopeImputacion ti = null;
	
	public LineaTopeImputacion(TopeImputacion tp) {
		this.ti = tp;
    	setConfig();
    }
    
    public LineaTopeImputacion() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
		configuracionTabla.put(LineaTopeImputacion.ANIO, new ConfigTabla(LineaTopeImputacion.ANIO, LineaTopeImputacion.ANIO, true,1, false));
		configuracionTabla.put(LineaTopeImputacion.RESTO, new ConfigTabla(LineaTopeImputacion.RESTO, LineaTopeImputacion.RESTO, true,2, false));
		configuracionTabla.put(LineaTopeImputacion.CANTIDAD, new ConfigTabla(LineaTopeImputacion.CANTIDAD, LineaTopeImputacion.CANTIDAD, true,3, false));
		configuracionTabla.put(LineaTopeImputacion.PORCENTAJE, new ConfigTabla(LineaTopeImputacion.PORCENTAJE, LineaTopeImputacion.PORCENTAJE, true,4, false));
		this.controlPantalla =  new ModificaTope(new ParamTable(), "vueltaPopUp");
    }
    
   	public void set(String campo, String valor){
   		try {
   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			if (LineaTopeImputacion.ANIO.equals(campo)) {
   				if (ti==null) return "";
   				return ""+ ti.anio;
   			}
   			if (LineaTopeImputacion.RESTO.equals(campo)) {
   				if (ti==null) return "";
   				if (ti.resto) return "SI";
   				else return "NO";
   			}
   			if (LineaTopeImputacion.CANTIDAD.equals(campo)) {
   				if (ti==null) return "";
   				return FormateadorDatos.formateaDato(ti.cantidad,FormateadorDatos.FORMATO_MONEDA);
   			}
   			if (LineaTopeImputacion.PORCENTAJE.equals(campo)) {
   				if (ti==null) return "";
   				return FormateadorDatos.formateaDato(ti.porcentaje,FormateadorDatos.FORMATO_PORC);
   			} 
   			   			
   			return "";
   		} catch ( Exception e) {
   			return "";
   		}
   	}
   	
	@Override
	public Object muestraSelector() {
		return this;
	}
    
    @Override
	public Tableable toTableable(Object o) {
    	try {
    		TopeImputacion ti = (TopeImputacion) o;
    		return new LineaTopeImputacion(ti);
    	} catch (Exception e){
    		try {
    			LineaTopeImputacion f = (LineaTopeImputacion) o;
    			return f;
    		} catch (Exception ex) {
    			return null;
    		}
    	}
	}   
	
	@Override
	public AnchorPane getFilaEmbebida(TableRowDataFeatures<Tableable> expander) {
		return null;
	}
	  	
}
