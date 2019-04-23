package ui.Economico.CargaImputaciones.Tables;

import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.beans.Concepto;
import model.beans.Coste;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaConcepto;
import model.metadatos.TipoDato;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;

public class LineaCosteEconomico extends ParamTable implements Tableable  {
	
	public static final String SISTEMA = "Sistema";
	public static final String TREI = "TREI";
	public static final String SATAD = "SATAD";
	public static final String CC = "CC";
	
	public Coste c;
		
	public LineaCosteEconomico(Coste c) {
		this.c = c;
		setConfig();
    }
    
    public LineaCosteEconomico() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
		configuracionTabla.put(LineaCosteEconomico.SISTEMA, new ConfigTabla(LineaCosteEconomico.SISTEMA, LineaCosteEconomico.SISTEMA, true,0, false));
		configuracionTabla.put(LineaCosteEconomico.TREI, new ConfigTabla(LineaCosteEconomico.TREI, LineaCosteEconomico.TREI, true,1, false));
		configuracionTabla.put(LineaCosteEconomico.SATAD, new ConfigTabla(LineaCosteEconomico.SATAD, LineaCosteEconomico.SATAD, true,2, false));
		configuracionTabla.put(LineaCosteEconomico.CC, new ConfigTabla(LineaCosteEconomico.CC, LineaCosteEconomico.CC, true,3, false));
		
    	anchoColumnas = new HashMap<String, Integer>();
    	anchoColumnas.put(LineaCosteEconomico.SISTEMA, new Integer(100));
    	anchoColumnas.put(LineaCosteEconomico.TREI, new Integer(100));
    	anchoColumnas.put(LineaCosteEconomico.SATAD, new Integer(100));
    	anchoColumnas.put(LineaCosteEconomico.CC, new Integer(100));
	}
    
   	public void set(String campo, String valor){
   		try {
   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			if (LineaCosteEconomico.SISTEMA.equals(campo))
   					return this.c.sistema.toString();
   			if (LineaCosteEconomico.TREI.equals(campo)) {
   				Concepto c = this.c.conceptosCoste.get(MetaConcepto.listado.get(MetaConcepto.TREI).codigo);
   				return FormateadorDatos.formateaDato(c.valor,TipoDato.FORMATO_MONEDA);
   			}	
   			if (LineaCosteEconomico.SATAD.equals(campo)) {
   				Concepto c = this.c.conceptosCoste.get(MetaConcepto.listado.get(MetaConcepto.SATAD).codigo);
				return FormateadorDatos.formateaDato(c.valor,TipoDato.FORMATO_MONEDA);
   			}
   			if (LineaCosteEconomico.CC.equals(campo)){
   				Concepto c = this.c.conceptosCoste.get(MetaConcepto.listado.get(MetaConcepto.CC).codigo);
				return FormateadorDatos.formateaDato(c.valor,TipoDato.FORMATO_MONEDA);
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
			Coste valores = (Coste) o;
    		return new LineaCosteEconomico(valores);
    	} catch (Exception e){
    		try {
    			LineaCosteEconomico f = (LineaCosteEconomico) o;
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
