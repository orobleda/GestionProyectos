package ui.Economico.ControlPresupuestario.Tables;

import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.beans.Concepto;
import model.constantes.FormateadorDatos;
import model.metadatos.Sistema;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;

public class LineaCosteDesglosadoSumario extends ParamTable implements Tableable  {
	
	public HashMap<String,Concepto> listaConceptos = null;
	
	public LineaCosteDesglosadoSumario(HashMap<String,Concepto> lista) {
		listaConceptos = lista;
    	setConfig();
    }
    
    public LineaCosteDesglosadoSumario() {
    	listaConceptos = new HashMap<String,Concepto>();
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	configuracionTabla.put("%", new ConfigTabla("%", "%", false,0, false));
    	configuracionTabla.put("Diferencia", new ConfigTabla("Diferencia", "Diferencia", false,1, false));
    	configuracionTabla.put("SISTEMA", new ConfigTabla("SISTEMA", "SISTEMA", false,1, false));
    	
    	anchoColumnas = new HashMap<String, Integer>();
    	anchoColumnas.put("%", new Integer(60));
    	anchoColumnas.put("Diferencia", new Integer(85));
    	anchoColumnas.put("SISTEMA", new Integer(80));
    }
    
   	public void set(String campo, String valor){
   		try {
   			Concepto c = listaConceptos.get(campo);
   			if ("SISTEMA".equals(campo)) {
   				c.coste.sistema = Sistema.listado.get(valor);
   			} else{
   				c.valorEstimado = (Float) FormateadorDatos.parseaDato(valor, FormateadorDatos.FORMATO_MONEDA);
   	   			listaConceptos.put(campo, c);
   			}			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			Concepto c = listaConceptos.get(campo);
   			if ("SISTEMA".equals(campo)) {
   				return c.coste.sistema.codigo;
   			} else
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
    		return new LineaCosteDesglosadoSumario(listaConceptos);
    	} catch (Exception e){
    		try {
    			LineaCosteDesglosadoSumario f = (LineaCosteDesglosadoSumario) o;
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
