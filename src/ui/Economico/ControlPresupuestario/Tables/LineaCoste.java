package ui.Economico.ControlPresupuestario.Tables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.beans.Concepto;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaConcepto;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;

public class LineaCoste extends ParamTable implements Tableable  {
	
	public HashMap<String,Concepto> listaConceptos = null;
	
	public LineaCoste(HashMap<String,Concepto> lista) {
		listaConceptos = lista;
    	setConfig();
    }
    
    public LineaCoste() {
    	listaConceptos = new HashMap<String,Concepto>();
    	setConfig();
    }
    
	public void setConfig() {
    	ArrayList<MetaConcepto> mcpL = new ArrayList<MetaConcepto>();
    	mcpL.addAll(MetaConcepto.listado.values());
    	Collections.sort(mcpL);
    	
    	Iterator<MetaConcepto> itMCPL = mcpL.iterator();
    	
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	int contador = 0;
    	
    	while (itMCPL.hasNext()) {
    		MetaConcepto mc = itMCPL.next();
    		configuracionTabla.put(""+mc.codigo, new ConfigTabla(mc.codigo, ""+mc.codigo, false,contador, false));
    		contador++;
    	}
    	
    	configuracionTabla.put("TOTAL", new ConfigTabla("TOTAL", "TOTAL", false,contador, false));
    	
    	anchoColumnas = new HashMap<String, Integer>();
    	anchoColumnas.put("TREI", new Integer(65));
    	anchoColumnas.put("CC", new Integer(65));
    	anchoColumnas.put("SATAD", new Integer(65));
    	anchoColumnas.put("Desarrollo", new Integer(70));
    	anchoColumnas.put("GOSC", new Integer(65));
    	anchoColumnas.put("TOTAL", new Integer(70));
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
    		return new LineaCoste(listaConceptos);
    	} catch (Exception e){
    		try {
    			LineaCoste f = (LineaCoste) o;
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
