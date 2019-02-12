package ui.Economico.ControlPresupuestario.Tables;

import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.beans.Concepto;
import model.constantes.FormateadorDatos;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Tableable;

public class LineaCosteSumario extends ParamTable implements Tableable  {
	
	public HashMap<String,Concepto> listaConceptos = null;
	
	public LineaCosteSumario(HashMap<String,Concepto> lista) {
		listaConceptos = lista;
    	setConfig();
    }
    
    public LineaCosteSumario() {
    	listaConceptos = new HashMap<String,Concepto>();
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	configuracionTabla.put("%", new ConfigTabla("%", "%", false,0, false));
    	configuracionTabla.put("Diferencia", new ConfigTabla("Diferencia", "Diferencia", false,1, false));    	
    }
    
   	public void set(String campo, String valor){
   		try {
   			Concepto c = listaConceptos.get(campo);
   			c.valorEstimado = (Float) FormateadorDatos.parseaDato(valor, FormateadorDatos.FORMATO_MONEDA);
   			listaConceptos.put(campo, c);   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			Concepto c = listaConceptos.get(campo);
   			if ("%".equals(campo)){
   				return FormateadorDatos.formateaDato(c.valorEstimado, FormateadorDatos.FORMATO_REAL);
   			} else   			
   				return FormateadorDatos.formateaDato(c.valorEstimado, FormateadorDatos.FORMATO_MONEDA);   			
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
    		@SuppressWarnings("unchecked")
			HashMap<String,Concepto> listaConceptos = (HashMap<String,Concepto>) o;
    		return new LineaCosteSumario(listaConceptos);
    	} catch (Exception e){
    		try {
    			LineaCosteSumario f = (LineaCosteSumario) o;
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
