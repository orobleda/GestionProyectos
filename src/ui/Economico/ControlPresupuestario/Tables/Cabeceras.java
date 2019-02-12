package ui.Economico.ControlPresupuestario.Tables;

import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Tableable;

public class Cabeceras extends ParamTable implements Tableable  {
	
	String texto = "";
	
	public Cabeceras(String texto) {
		this.texto = texto;
    	setConfig();
    }
    
    public Cabeceras() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	configuracionTabla.put("Conceptos", new ConfigTabla("Conceptos", "Conceptos", false,0, false));    	
    }
    
   	public void set(String campo, String valor){
   		try {
   			this.texto = valor;   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			return texto;   			
   		} catch ( Exception e) {
   			return "";
   		}
   	}
   	
	@Override
	public Object muestraSelector() {
		return null;
	}
    
    @Override
	public Tableable toTableable(Object o) {
    	try {
    		return new Cabeceras((String)o);
    	} catch (Exception e){
    		try {
    			Cabeceras f = (Cabeceras) o;
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
