package ui.Economico.CargaImputaciones.Tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.beans.Concepto;
import model.beans.Coste;
import model.constantes.FormateadorDatos;
import model.metadatos.Sistema;
import model.metadatos.TipoDato;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;

public class LineaResumenEconomico extends ParamTable implements Tableable  {
	
	public static final String SISTEMA = "Sistema";
	public static final String PRESUPUESTADO = "Presupuestado";
	public static final String ESTIMADO = "Estimado";
	public static final String IMPUTADO = "Imputado";
	public static final String DESVIO = "Cobertura";
	
	public HashMap<String, Object> mapaValores;
	
	public LineaResumenEconomico(HashMap<String, Object> mapa) {
		this.mapaValores = mapa;
		setConfig();
    }
    
    public LineaResumenEconomico() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
		configuracionTabla.put(LineaResumenEconomico.SISTEMA, new ConfigTabla(LineaResumenEconomico.SISTEMA, LineaResumenEconomico.SISTEMA, false,0, false));
		configuracionTabla.put(LineaResumenEconomico.PRESUPUESTADO, new ConfigTabla(LineaResumenEconomico.PRESUPUESTADO, LineaResumenEconomico.PRESUPUESTADO, false,1, false));
		configuracionTabla.put(LineaResumenEconomico.ESTIMADO, new ConfigTabla(LineaResumenEconomico.ESTIMADO, LineaResumenEconomico.ESTIMADO, false,2, false));
		configuracionTabla.put(LineaResumenEconomico.IMPUTADO, new ConfigTabla(LineaResumenEconomico.IMPUTADO, LineaResumenEconomico.IMPUTADO, false,3, false));
		configuracionTabla.put(LineaResumenEconomico.DESVIO, new ConfigTabla(LineaResumenEconomico.DESVIO, LineaResumenEconomico.DESVIO, false,4, false));
		
    	anchoColumnas = new HashMap<String, Integer>();
    	anchoColumnas.put(LineaResumenEconomico.SISTEMA, new Integer(120));
    	anchoColumnas.put(LineaResumenEconomico.PRESUPUESTADO, new Integer(70));
    	anchoColumnas.put(LineaResumenEconomico.ESTIMADO, new Integer(70));
    	anchoColumnas.put(LineaResumenEconomico.IMPUTADO, new Integer(70));
    	anchoColumnas.put(LineaResumenEconomico.DESVIO, new Integer(70));
	}
    
   	public void set(String campo, String valor){
   		try {
   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			if (LineaResumenEconomico.SISTEMA.equals(campo))
   					return ((Sistema) this.mapaValores.get(LineaResumenEconomico.SISTEMA)).toString();
   			if (LineaResumenEconomico.PRESUPUESTADO.equals(campo)) {
   				return FormateadorDatos.formateaDato((Float) this.mapaValores.get(LineaResumenEconomico.PRESUPUESTADO), TipoDato.FORMATO_MONEDA);
   			}	
   			if (LineaResumenEconomico.ESTIMADO.equals(campo)) {
   				return FormateadorDatos.formateaDato((Float) this.mapaValores.get(LineaResumenEconomico.ESTIMADO), TipoDato.FORMATO_MONEDA);
   			}
   			if (LineaResumenEconomico.IMPUTADO.equals(campo)){
   				return FormateadorDatos.formateaDato(Math.abs((Float) this.mapaValores.get(LineaResumenEconomico.IMPUTADO)), TipoDato.FORMATO_MONEDA);
   			}
   			if (LineaResumenEconomico.DESVIO.equals(campo)){
   				return FormateadorDatos.formateaDato(Math.abs((Float) this.mapaValores.get(LineaResumenEconomico.IMPUTADO)/(Float) this.mapaValores.get(LineaResumenEconomico.PRESUPUESTADO)*100), TipoDato.FORMATO_PORC);
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
    		@SuppressWarnings("unchecked")
			HashMap<String, Object> valores = (HashMap<String, Object>) o;
    		return new LineaResumenEconomico(valores);
    	} catch (Exception e){
    		try {
    			LineaResumenEconomico f = (LineaResumenEconomico) o;
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
	
	public static ArrayList<Object> totaliza(HashMap<String, Coste> estimado, HashMap<String, Coste> imputado, HashMap<Integer, Coste> presupuestado) {
		float sumaEstimado = 0;
		float sumaImputado = 0;
		float sumaPresupuestado = 0;
		
		ArrayList<Object> salida = new ArrayList<Object>();
		
		Iterator<Coste> itCostes = estimado.values().iterator();
		while (itCostes.hasNext()) {
			Coste c = itCostes.next();
			
			HashMap<String, Object> datosSistema = new HashMap<String, Object>();
			datosSistema.put(LineaResumenEconomico.SISTEMA,c.sistema);
			
			float festimado = LineaResumenEconomico.sumado(c);
			datosSistema.put(LineaResumenEconomico.ESTIMADO,festimado);
			
			Coste cAux = imputado.get(c.sistema.codigo);
			float fimputado = LineaResumenEconomico.sumado(cAux);
			datosSistema.put(LineaResumenEconomico.IMPUTADO,fimputado);
			
			Coste cAux2 = presupuestado.get(c.sistema.id);
			float fpresupuestado = LineaResumenEconomico.sumado(cAux2);
			datosSistema.put(LineaResumenEconomico.PRESUPUESTADO,fpresupuestado);
			
			sumaEstimado += festimado;
			sumaImputado += fimputado;
			sumaPresupuestado += fpresupuestado;
			
			salida.add(datosSistema);
		}
		
		HashMap<String, Object> datosSistema = new HashMap<String, Object>();
		datosSistema.put(LineaResumenEconomico.SISTEMA,Sistema.getInstanceTotal());
		datosSistema.put(LineaResumenEconomico.ESTIMADO,sumaEstimado);
		datosSistema.put(LineaResumenEconomico.IMPUTADO,sumaImputado);
		datosSistema.put(LineaResumenEconomico.PRESUPUESTADO,sumaPresupuestado);
		
		salida.add(datosSistema);
		
		return salida;
	}
	
	private static Float sumado(Coste c) {
		float suma = 0;
		
		Iterator<Concepto> iConceptos = c.conceptosCoste.values().iterator();
		while (iConceptos.hasNext()) {
			Concepto conc = iConceptos.next();
			suma += conc.valor; 
		}
		
		return new Float(suma);
	}
	  	
	  	
}
