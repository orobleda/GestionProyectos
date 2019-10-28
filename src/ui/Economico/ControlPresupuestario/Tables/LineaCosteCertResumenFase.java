package ui.Economico.ControlPresupuestario.Tables;

import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.beans.CertificacionFase;
import model.beans.CertificacionFaseParcial;
import model.beans.Concepto;
import model.constantes.FormateadorDatos;
import model.metadatos.TipoDato;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;

public class LineaCosteCertResumenFase extends ParamTable implements Tableable  {
	
	public static final String SISTEMA = "Sistema";
	public static final String FASE = "Fase";
	public static final String PORC = "%";
	public static final String TOTAL = "Presupuestado";
	public static final String VALOR_ESTIMADO = "Estimado";
	public static final String VALOR_REAL = "Imputado";
	
	public CertificacionFase cf = null;
	
	public LineaCosteCertResumenFase( CertificacionFase cf) {
		this.cf = cf;
    	setConfig();
    }
    
    public LineaCosteCertResumenFase() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
		configuracionTabla.put(LineaCosteCertResumenFase.SISTEMA, new ConfigTabla(LineaCosteCertResumenFase.SISTEMA, LineaCosteCertResumenFase.SISTEMA, false,0, false));
		configuracionTabla.put(LineaCosteCertResumenFase.FASE, new ConfigTabla(LineaCosteCertResumenFase.FASE, LineaCosteCertResumenFase.FASE, false,1, false));
		configuracionTabla.put(LineaCosteCertResumenFase.PORC, new ConfigTabla(LineaCosteCertResumenFase.PORC, LineaCosteCertResumenFase.PORC, false,1, false));
		configuracionTabla.put(LineaCosteCertResumenFase.TOTAL, new ConfigTabla(LineaCosteCertResumenFase.TOTAL, LineaCosteCertResumenFase.TOTAL, false,4, false));
		configuracionTabla.put(LineaCosteCertResumenFase.VALOR_ESTIMADO, new ConfigTabla(LineaCosteCertResumenFase.VALOR_ESTIMADO, LineaCosteCertResumenFase.VALOR_ESTIMADO, false,6, false));
		configuracionTabla.put(LineaCosteCertResumenFase.VALOR_REAL, new ConfigTabla(LineaCosteCertResumenFase.VALOR_REAL, LineaCosteCertResumenFase.VALOR_REAL, false,7, false));
		
		anchoColumnas = new HashMap<String, Integer>();
    	anchoColumnas.put(LineaCosteCertResumenFase.PORC, new Integer(40));
    	anchoColumnas.put(LineaCosteCertResumenFase.FASE, new Integer(150));
    	anchoColumnas.put(LineaCosteCertResumenFase.SISTEMA, new Integer(60));
    	anchoColumnas.put(LineaCosteCertResumenFase.TOTAL, new Integer(100));
    }
    
   	public void set(String campo, String valor){
   		try {
   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			if (LineaCosteCertResumenFase.SISTEMA.equals(campo))  return this.cf.certificacion.s.codigo;
   			if (LineaCosteCertResumenFase.FASE.equals(campo))  
   				if (this.cf.fase!=null) 
   					return this.cf.fase.nombre;
   				else
   					return "";
   			if (LineaCosteCertResumenFase.PORC.equals(campo))  {
   				Concepto conc = this.cf.concepto;
   				Concepto concTotal = this.cf.certificacion.concepto;
   				return FormateadorDatos.formateaDato(conc.valor/concTotal.valor*100,TipoDato.FORMATO_PORC);
   			}
   				
   			if (LineaCosteCertResumenFase.TOTAL.equals(campo)) {
   				if (this.cf.concepto!=null) {
   					Concepto conc = this.cf.concepto;
   					return FormateadorDatos.formateaDato(conc.valor,TipoDato.FORMATO_MONEDA);
   				}
   				return FormateadorDatos.formateaDato(0,TipoDato.FORMATO_MONEDA);
   			} 
   			if (LineaCosteCertResumenFase.VALOR_ESTIMADO.equals(campo)) {
   				float acumulado = 0;
   			   Iterator<CertificacionFaseParcial> itCfp = this.cf.certificacionesParciales.iterator();
   			   while (itCfp.hasNext()) {
   				CertificacionFaseParcial cfp = itCfp.next();
   				acumulado += cfp.valEstimado;
   			   }
   			   acumulado += this.cf.concAdicional.valorEstimado;
   			  return FormateadorDatos.formateaDato(new Float(acumulado),TipoDato.FORMATO_MONEDA);
   			}
   			if (LineaCosteCertResumenFase.VALOR_REAL.equals(campo))  {
   				float acumulado = 0;
    			   Iterator<CertificacionFaseParcial> itCfp = this.cf.certificacionesParciales.iterator();
    			   while (itCfp.hasNext()) {
    				CertificacionFaseParcial cfp = itCfp.next();
    				acumulado += cfp.valReal;
    			   }
    			   acumulado += this.cf.concAdicional.valor;
    			  return FormateadorDatos.formateaDato(new Float(acumulado),TipoDato.FORMATO_MONEDA);
   			} 
   				   			
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
    		CertificacionFase valores = (CertificacionFase) o;
    		return new LineaCosteCertResumenFase(valores);
    	} catch (Exception e){
    		try {
    			LineaCosteCertResumenFase f = (LineaCosteCertResumenFase) o;
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
