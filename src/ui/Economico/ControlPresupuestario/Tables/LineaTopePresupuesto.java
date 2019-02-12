package ui.Economico.ControlPresupuestario.Tables;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import model.beans.Concepto;
import model.beans.EstimacionAnio;
import model.constantes.FormateadorDatos;
import model.metadatos.TipoParamProyecto;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Tableable;
import ui.Economico.ControlPresupuestario.ModificaTope;

public class LineaTopePresupuesto extends ParamTable implements Tableable  {
	 
    public Concepto concepto = null;
    public static Concepto conceptoGuarda = null;
    
    public static final String CONCEPTO = "Concepto";
    public static final String TOTALE = "Total Estimado";
    public static final String TOTALI = "Total Imputado";
    
    public LineaTopePresupuesto(Concepto concepto) {
		this.concepto = concepto;
		LineaTopePresupuesto.conceptoGuarda = concepto;
		
		if (configuracionTabla==null || configuracionTabla.size()==0) {
    		setConfig();
    	}
    }
    
    public LineaTopePresupuesto() {
    	if (configuracionTabla==null || configuracionTabla.size()==0) {
    		setConfig();
    	}
    }
    
    public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	configuracionTabla.put(LineaTopePresupuesto.CONCEPTO, new ConfigTabla(LineaTopePresupuesto.CONCEPTO, LineaTopePresupuesto.CONCEPTO, false,0, false));
    	
    	Iterator<EstimacionAnio> itEstimacion = LineaTopePresupuesto.conceptoGuarda.topeEstimacion.iterator();
    	ArrayList<Integer> listaAnios = new ArrayList<Integer>();
    	
    	while (itEstimacion.hasNext()) {
    		EstimacionAnio oEst = itEstimacion.next();
    		listaAnios.add(oEst.anio);
    	}   	
    	
    	Collections.sort(listaAnios);
    	int contador = 2;
    	
    	Iterator<Integer> itAnios = listaAnios.iterator();
    	while (itAnios.hasNext()) {
    		Integer anio = itAnios.next();
    		configuracionTabla.put("Est. "+anio, new ConfigTabla("Est. "+anio, "Est. "+anio, false,contador++));
    	}
    	
    	configuracionTabla.put(LineaTopePresupuesto.TOTALE, new ConfigTabla(LineaTopePresupuesto.TOTALE, LineaTopePresupuesto.TOTALE, false,contador++));
    	
    	itAnios = listaAnios.iterator();
    	while (itAnios.hasNext()) {
    		Integer anio = itAnios.next();
    		configuracionTabla.put("Imp. "+anio, new ConfigTabla("Imp. "+anio, "Imp. "+anio, false,contador++));
    	}
    	
    	configuracionTabla.put(LineaTopePresupuesto.TOTALI, new ConfigTabla(LineaTopePresupuesto.TOTALI, LineaTopePresupuesto.TOTALI, false,contador++));
    }
    
	@Override
	public Object muestraSelector() {
		TipoParamProyecto tpP = TipoParamProyecto.listado.get(this.tipoDato);
				
		return tpP.dameSelector();
	}
    
    @Override
	public Tableable toTableable(Object o) {
    	try {
			Concepto concepto = (Concepto) o;
			return new LineaTopePresupuesto(concepto);
    	} catch (Exception e){
    		e.printStackTrace();
    		return null;
    	}
	}
    
	public void set(String campo, String valor){
			
	}
    
	public String get(String campo) {
		try {
			if (!LineaTopePresupuesto.CONCEPTO.equals(campo)) {
				if (LineaTopePresupuesto.TOTALE.equals(campo)) {
					try {
						Iterator<EstimacionAnio> it = this.concepto.topeEstimacion.iterator();
						float suma = 0;
						
						while (it.hasNext()) {
							EstimacionAnio eA = it.next();
							suma += eA.cantidad;
						}
						
						return FormateadorDatos.formateaDato(suma, FormateadorDatos.FORMATO_MONEDA);	
					} catch (Exception e) {}
				} 
				if (LineaTopePresupuesto.TOTALI.equals(campo)) {
					try {
						Iterator<EstimacionAnio> it = this.concepto.topeEstimacion.iterator();
						float suma = 0;
						
						while (it.hasNext()) {
							EstimacionAnio eA = it.next();
							suma += eA.cantidadImputada;
						}
						
						return FormateadorDatos.formateaDato(suma, FormateadorDatos.FORMATO_MONEDA);	
					} catch (Exception e) {}
				} 
				else {
					Iterator<EstimacionAnio> it = this.concepto.topeEstimacion.iterator();
					
					String cortada = campo.replaceAll("Imp. ", "");
					cortada = cortada.replaceAll("Est. ", "");
					
					while (it.hasNext()) {
						EstimacionAnio eA = it.next();
						if (eA.anio == new Integer(cortada).intValue()){
							if (campo.contains("Imp. ")) 
								return FormateadorDatos.formateaDato(eA.cantidadImputada, FormateadorDatos.FORMATO_MONEDA);
							else 
								return FormateadorDatos.formateaDato(eA.cantidad, FormateadorDatos.FORMATO_MONEDA);
						}
					}
				}
			}	else {
					return this.concepto.tipoConcepto.codigo;
			}
			return "";
		} catch ( Exception e) {return "";}
	}
	
	@Override
	public AnchorPane getFilaEmbebida(TableRowDataFeatures<Tableable> expander) {
		try {
			FXMLLoader loader = new FXMLLoader();
			ModificaTope infConcepto = new ModificaTope(expander);
	        loader.setLocation(new URL(infConcepto.getFXML()));
	        
	        return (AnchorPane) loader.load();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
       
    	
}
