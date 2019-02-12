package ui.Economico.ControlPresupuestario.Tables;

import java.util.ArrayList;
import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.beans.Concepto;
import model.constantes.FormateadorDatos;
import model.metadatos.Sistema;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Tableable;

public class LineaCosteEvolucion extends ParamTable implements Tableable  {
	
	public static final String SISTEMA = "Sistema";
	public static final String AC = "Año/Concepto";
	public static final String AT = "Año/Total";	
	public static final String TC = "Total/Concepto";
	public static final String TT = "Total/Total";
	
	public ArrayList<Float> resumen = null;
	
	public Sistema sistema = null;
	
	public LineaCosteEvolucion(Sistema sistema) {
		this.sistema = sistema;
    	setConfig();
    }
    
    public LineaCosteEvolucion() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
		configuracionTabla.put(LineaCosteEvolucion.SISTEMA, new ConfigTabla(LineaCosteEvolucion.SISTEMA, LineaCosteEvolucion.SISTEMA, false,1, false));
		configuracionTabla.put(LineaCosteEvolucion.AC, new ConfigTabla(LineaCosteEvolucion.AC, LineaCosteEvolucion.AC, true,2, false));
		configuracionTabla.put(LineaCosteEvolucion.AT, new ConfigTabla(LineaCosteEvolucion.AT, LineaCosteEvolucion.AT, true,3, false));
		configuracionTabla.put(LineaCosteEvolucion.TC, new ConfigTabla(LineaCosteEvolucion.TC, LineaCosteEvolucion.TC, true,4, false));
		configuracionTabla.put(LineaCosteEvolucion.TT, new ConfigTabla(LineaCosteEvolucion.TT, LineaCosteEvolucion.TT, true,5, false));
    }
    
   	public void set(String campo, String valor){
   		try {
   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			if (LineaCosteEvolucion.SISTEMA.equals(campo)) {
   				if (sistema==null) return "";
   				return sistema.descripcion;
   			}
   			if (LineaCosteEvolucion.AC.equals(campo)) {
   				Concepto c = sistema.listaConceptos.get(AC);
   				if (c==null) return "";
   				return FormateadorDatos.formateaDato(c.valor,FormateadorDatos.FORMATO_MONEDA);
   			}
   			if (LineaCosteEvolucion.AT.equals(campo)) {
   				Concepto c = sistema.listaConceptos.get(AT);
   				if (c==null) return "";
   				return FormateadorDatos.formateaDato(c.valor,FormateadorDatos.FORMATO_MONEDA);
   			}
   			if (LineaCosteEvolucion.TC.equals(campo)) {
   				Concepto c = sistema.listaConceptos.get(TC);
   				if (c==null) return "";
   				return FormateadorDatos.formateaDato(c.valor,FormateadorDatos.FORMATO_MONEDA);
   			}
   			if (LineaCosteEvolucion.TT.equals(campo)) {
   				Concepto c = sistema.listaConceptos.get(TT);
   				if (c==null) return "";
   				return FormateadorDatos.formateaDato(c.valor,FormateadorDatos.FORMATO_MONEDA);
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
			Sistema sistema = (Sistema) o;
    		return new LineaCosteEvolucion(sistema);
    	} catch (Exception e){
    		try {
    			LineaCosteEvolucion f = (LineaCosteEvolucion) o;
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
