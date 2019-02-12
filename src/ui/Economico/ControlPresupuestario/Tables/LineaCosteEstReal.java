package ui.Economico.ControlPresupuestario.Tables;

import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.constantes.FormateadorDatos;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Tableable;

public class LineaCosteEstReal extends ParamTable implements Tableable  {
	
	HashMap<String,Float> conceptos = null;
	
	public LineaCosteEstReal(HashMap<String,Float> conceptos) {
		this.conceptos = conceptos;
		setConfig();
    }
    
    public LineaCosteEstReal() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	configuracionTabla.put("Estimado", new ConfigTabla("Estimado", "Estimado", false,0, false));
    	configuracionTabla.put("Real", new ConfigTabla("Real", "Real", false,0, false));
    }
    
   	public void set(String campo, String valor){
   		try {
   			conceptos.put(campo,(Float) FormateadorDatos.parseaDato(valor,FormateadorDatos.FORMATO_MONEDA));  			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
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
    		return new LineaCosteEstReal((HashMap<String,Float>)o);
    	} catch (Exception e){
    		try {
    			LineaCosteEstReal f = (LineaCosteEstReal) o;
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
