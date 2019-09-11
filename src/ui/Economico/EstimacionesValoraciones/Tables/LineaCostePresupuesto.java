package ui.Economico.EstimacionesValoraciones.Tables;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import model.beans.Concepto;
import model.beans.Coste;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import model.metadatos.TipoParamProyecto;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Tabla;
import ui.Economico.CargaImputaciones.Tables.LineaDetalleImputacion;
import ui.Economico.EstimacionesInternas.tables.LineaCosteProyectoEstimacion;
import ui.Economico.EstimacionesValoraciones.InformaConcepto;
import ui.interfaces.Tableable;

public class LineaCostePresupuesto extends ParamTable implements Tableable  {
	 
    public HashMap<String,Concepto> conceptos = null;
    
    public Sistema sistema;
    public float totalCoste = 0;
    public boolean modificado = false;
    public Coste coste = null;
    public boolean resumen = false;
    
    public static final String SISTEMA = "Sistema";
    public static final String TOTAL = "Total"; 
    
    public LineaCostePresupuesto(Coste coste) {

		this.sistema = coste.sistema;
    	
        this.conceptos = coste.conceptosCoste;
        setConfig(coste);
    }
    
    public LineaCostePresupuesto() {
    	setConfig();
    }
    
	public void fijaMetaDatos(HashMap<String,Object> variablesPaso){
		coste = (Coste) variablesPaso.get("COSTE");
		
		if (variablesPaso.get("RESUMEN")!=null) {
			resumen = (Boolean) variablesPaso.get("RESUMEN");
		}
	}
	
	public void setConfig() {
		setConfig(coste);
	}
    
    public void setConfig(Coste coste) {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	configuracionTabla.put(LineaCostePresupuesto.SISTEMA, new ConfigTabla(LineaCostePresupuesto.SISTEMA, LineaCostePresupuesto.SISTEMA, false,0, true));
    	    	
    	if (coste!=null && coste.conceptosCoste!=null){
    		Iterator<Concepto> itCon = coste.conceptosCoste.values().iterator();
    		anchoColumnas = new HashMap<String, Integer>();
    		
    		while(itCon.hasNext()) {
    			Concepto c = itCon.next();
    			
    			if (c.tipoConcepto.id==MetaConcepto.ID_TOTAL) {
    				configuracionTabla.put(c.tipoConcepto.codigo, new ConfigTabla(c.tipoConcepto.codigo, c.tipoConcepto.codigo, false,32000+1));
    			} else {
    				configuracionTabla.put(c.tipoConcepto.codigo, new ConfigTabla(c.tipoConcepto.codigo, c.tipoConcepto.codigo, false,c.tipoConcepto.id+1));
    			}
    			
    			anchoColumnas.put(c.tipoConcepto.codigo, new Integer(100));
    			
    		}
    	}
    	
    	if (resumen) {
			ConfigTabla cf = configuracionTabla.get(LineaCostePresupuesto.SISTEMA);
			cf.desplegable = false;
			cf.idColumna = "Resumen Coste";
		}
    }
    
	@Override
	public Object muestraSelector() {
		TipoParamProyecto tpP = TipoParamProyecto.listado.get(this.tipoDato);
				
		return tpP.dameSelector();
	}
    
    @Override
	public Tableable toTableable(Object o) {
    	try {
			Coste coste = (Coste) o;
			
			if (this.resumen) {
				LineaCostePresupuesto lcp = new LineaCostePresupuesto(coste);
				ConfigTabla cf = lcp.configuracionTabla.get(LineaCostePresupuesto.SISTEMA);
				cf.desplegable = false;
				cf.idColumna = "Resumen Coste";
				return lcp;
			}
			else 			
				return new LineaCostePresupuesto(coste);
    	} catch (Exception e){
    		return null;
    	}
	}
    
	public void set(String campo, String valor){
		try {
			if (LineaCostePresupuesto.SISTEMA.equals(campo)) {
				//this.sistema = Sistema.listado.get(new Integer(valor));
			} else 
				if (LineaCostePresupuesto.TOTAL.equals(campo)) {
					this.totalCoste = (float) FormateadorDatos.parseaDato(valor, FormateadorDatos.FORMATO_REAL);
				} else {
					Concepto c = this.conceptos.get(campo);
					c.valor = (float) FormateadorDatos.parseaDato(valor, FormateadorDatos.FORMATO_REAL);
				}
		} catch ( Exception e) {}
			
	}
    
	public String get(String campo) {
		try {
			if (LineaCostePresupuesto.SISTEMA.equals(campo)) {
				if (this.sistema==null) return "";
				return this.sistema.toString();
			} else 
				if (LineaCostePresupuesto.TOTAL.equals(campo)) {
					return FormateadorDatos.formateaDato(this.totalCoste, FormateadorDatos.FORMATO_MONEDA);
				} else {
					Concepto c = this.conceptos.get(campo);
					return FormateadorDatos.formateaDato(c.valorEstimado, FormateadorDatos.FORMATO_MONEDA);
				}
			
		} catch ( Exception e) {
			return "0";
		}
	}
	
	@Override
	public AnchorPane getFilaEmbebida(TableRowDataFeatures<Tableable> expander) {
		try {
			FXMLLoader loader = new FXMLLoader();
			InformaConcepto infConcepto = new InformaConcepto(expander);
	        loader.setLocation(new URL(infConcepto.getFXML()));
	        
	        return (AnchorPane) loader.load();
		} catch (Exception e) {
			return null;
		}
	}
	
	
       
    	
}
