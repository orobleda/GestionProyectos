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
import model.metadatos.Sistema;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Economico.ControlPresupuestario.ControlPresupuestario;
import ui.interfaces.Tableable;

public class LineaCosteDesglosado extends ParamTable implements Tableable, Comparable<LineaCosteDesglosado>  {
	
	public HashMap<String,Concepto> listaConceptos = null;
	
	public LineaCosteDesglosado(HashMap<String,Concepto> lista) {
		listaConceptos = lista;
    	setConfig();
    }
    
    public LineaCosteDesglosado() {
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
    	
    	configuracionTabla.put("TOTAL", new ConfigTabla("TOTAL", "TOTAL", false,contador++, false));
    	configuracionTabla.put("SISTEMA", new ConfigTabla("SISTEMA", "SISTEMA", false,contador, false));
    	
    	anchoColumnas = new HashMap<String, Integer>();
    	anchoColumnas.put("TREI", ControlPresupuestario.anchoResumen);
    	anchoColumnas.put("CC", ControlPresupuestario.anchoResumen);
    	anchoColumnas.put("SATAD",ControlPresupuestario.anchoResumen);
    	anchoColumnas.put("Desarrollo",ControlPresupuestario.anchoResumen);
    	anchoColumnas.put("GOSC", ControlPresupuestario.anchoResumen);
    	anchoColumnas.put("TOTAL", ControlPresupuestario.anchoResumen);
    	anchoColumnas.put("SISTEMA", ControlPresupuestario.anchoResumen);
    	
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
    		return new LineaCosteDesglosado(listaConceptos);
    	} catch (Exception e){
    		try {
    			LineaCosteDesglosado f = (LineaCosteDesglosado) o;
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

	@Override
	public int compareTo(LineaCosteDesglosado arg0) {
		try {
			return this.listaConceptos.get("SISTEMA").coste.sistema.compareTo(arg0.listaConceptos.get("SISTEMA").coste.sistema);
		} catch (Exception e) {
			
		}
		return 0;
	}
	  	
}
