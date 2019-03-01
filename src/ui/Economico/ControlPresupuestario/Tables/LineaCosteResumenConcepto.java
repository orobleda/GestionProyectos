package ui.Economico.ControlPresupuestario.Tables;

import java.util.ArrayList;
import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.constantes.FormateadorDatos;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;

public class LineaCosteResumenConcepto extends ParamTable implements Tableable  {
	
	public static final String EM = "Estimado Mes";
	public static final String IM = "Imputado Mes";
	public static final String EA = "Estimado Año en Curso";
	public static final String IA = "Imputado Año en Curso";
	public static final String ET = "Estimado Total";
	public static final String IT = "Imputado Total";
	
	public ArrayList<Float> valores = null;
	
	public LineaCosteResumenConcepto( ArrayList<Float> valores) {
		this.valores = valores;
    	setConfig();
    }
    
    public LineaCosteResumenConcepto() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
		configuracionTabla.put(LineaCosteResumenConcepto.EM, new ConfigTabla(LineaCosteResumenConcepto.EM, LineaCosteResumenConcepto.EM, false,0, false));
		configuracionTabla.put(LineaCosteResumenConcepto.IM, new ConfigTabla(LineaCosteResumenConcepto.IM, LineaCosteResumenConcepto.IM, false,1, false));
		configuracionTabla.put(LineaCosteResumenConcepto.EA, new ConfigTabla(LineaCosteResumenConcepto.EA, LineaCosteResumenConcepto.EA, false,2, false));
		configuracionTabla.put(LineaCosteResumenConcepto.IA, new ConfigTabla(LineaCosteResumenConcepto.IA, LineaCosteResumenConcepto.IA, false,3, false));
		configuracionTabla.put(LineaCosteResumenConcepto.ET, new ConfigTabla(LineaCosteResumenConcepto.ET, LineaCosteResumenConcepto.ET, false,4, false));
		configuracionTabla.put(LineaCosteResumenConcepto.IT, new ConfigTabla(LineaCosteResumenConcepto.IT, LineaCosteResumenConcepto.IT, false,5, false));
    }
    
   	public void set(String campo, String valor){
   		try {
   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			if (LineaCosteResumenConcepto.EM.equals(campo))  return FormateadorDatos.formateaDato(this.valores.get(0),FormateadorDatos.FORMATO_MONEDA);
   			if (LineaCosteResumenConcepto.IM.equals(campo))  return FormateadorDatos.formateaDato(this.valores.get(1),FormateadorDatos.FORMATO_MONEDA);
   			if (LineaCosteResumenConcepto.EA.equals(campo))  return FormateadorDatos.formateaDato(this.valores.get(2),FormateadorDatos.FORMATO_MONEDA);
   			if (LineaCosteResumenConcepto.IA.equals(campo))  return FormateadorDatos.formateaDato(this.valores.get(3),FormateadorDatos.FORMATO_MONEDA);
   			if (LineaCosteResumenConcepto.ET.equals(campo))  return FormateadorDatos.formateaDato(this.valores.get(4),FormateadorDatos.FORMATO_MONEDA);
   			if (LineaCosteResumenConcepto.IT.equals(campo))  return FormateadorDatos.formateaDato(this.valores.get(5),FormateadorDatos.FORMATO_MONEDA);
   				   			
   			return "";
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
			ArrayList<Float> valores = (ArrayList<Float>) o;
    		return new LineaCosteResumenConcepto(valores);
    	} catch (Exception e){
    		try {
    			LineaCosteResumenConcepto f = (LineaCosteResumenConcepto) o;
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
