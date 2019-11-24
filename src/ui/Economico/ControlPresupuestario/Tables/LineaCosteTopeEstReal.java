package ui.Economico.ControlPresupuestario.Tables;

import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.constantes.FormateadorDatos;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;

public class LineaCosteTopeEstReal extends ParamTable implements Tableable  {
	
	HashMap<String,Float> conceptos = null;
	
	public LineaCosteTopeEstReal(HashMap<String,Float> conceptos) {
		this.conceptos = conceptos;
		setConfig();
    }
    
    public LineaCosteTopeEstReal() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	configuracionTabla.put("Tope Estimado", new ConfigTabla("Tope Estimado", "Tope Estimado", false,0, false));
    	configuracionTabla.put("Estimado", new ConfigTabla("Estimado", "Estimado", false,0, false));
    	configuracionTabla.put("Real", new ConfigTabla("Real", "Real", false,0, false));
    	configuracionTabla.put("Resto", new ConfigTabla("Resto", "Resto", false,0, false));
    	
    	anchoColumnas = new HashMap<String, Integer>();
    	anchoColumnas.put("Tope Estimado", new Integer(100));
    	anchoColumnas.put("Estimado", new Integer(100));
    	anchoColumnas.put("Real", new Integer(100));
    	anchoColumnas.put("Resto", new Integer(100));
    }
    
   	public void set(String campo, String valor){
   		try {
   			conceptos.put(campo,(Float) FormateadorDatos.parseaDato(valor,FormateadorDatos.FORMATO_MONEDA));  			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			if ("Resto".equals(campo)) {
   				float valor0 = (Float) conceptos.get("Tope Estimado");
   				if (valor0==-9999) valor0 = (Float) conceptos.get("Estimado");
   				float valor1 = (Float) conceptos.get("Estimado");
   				return FormateadorDatos.formateaDato(valor0-valor1,FormateadorDatos.FORMATO_MONEDA);
   			}
   			if ("Tope Estimado".equals(campo)) {
   				float valor0 = (Float) conceptos.get("Tope Estimado");
   				float valor1 = (Float) conceptos.get("Estimado");
   				if (valor0==-9999)
   					return FormateadorDatos.formateaDato(valor1,FormateadorDatos.FORMATO_MONEDA);
   				else 
   					return FormateadorDatos.formateaDato(valor0,FormateadorDatos.FORMATO_MONEDA);
   			}
   			return FormateadorDatos.formateaDato(conceptos.get(campo),FormateadorDatos.FORMATO_MONEDA);   			
   		} catch ( Exception e) {
   			return "";
   		}
   	}
   	
	@Override
	public Object muestraSelector() {
		return null;
	}
    
    @SuppressWarnings("unchecked")
	@Override
	public Tableable toTableable(Object o) {
    	try {
    		return new LineaCosteTopeEstReal((HashMap<String,Float>)o);
    	} catch (Exception e){
    		try {
    			LineaCosteTopeEstReal f = (LineaCosteTopeEstReal) o;
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
