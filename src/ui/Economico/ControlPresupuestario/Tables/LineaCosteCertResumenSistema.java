package ui.Economico.ControlPresupuestario.Tables;

import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.beans.Certificacion;
import model.beans.CertificacionFase;
import model.beans.CertificacionFaseParcial;
import model.beans.Concepto;
import model.constantes.FormateadorDatos;
import model.metadatos.TipoDato;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;

public class LineaCosteCertResumenSistema extends ParamTable implements Tableable  {
	
	public static final String SISTEMA = "Sistema";
	public static final String TOTAL = "Presupuestado";
	public static final String VALOR_ESTIMADO = "Estimado";
	public static final String VALOR_REAL = "Imputado";
	
	public Certificacion c = null;
	
	public LineaCosteCertResumenSistema( Certificacion c) {
		this.c = c;
    	setConfig();
    }
    
    public LineaCosteCertResumenSistema() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
		configuracionTabla.put(LineaCosteCertResumenSistema.SISTEMA, new ConfigTabla(LineaCosteCertResumenSistema.SISTEMA, LineaCosteCertResumenSistema.SISTEMA, false,0, false));
		configuracionTabla.put(LineaCosteCertResumenSistema.TOTAL, new ConfigTabla(LineaCosteCertResumenSistema.TOTAL, LineaCosteCertResumenSistema.TOTAL, false,4, false));
		configuracionTabla.put(LineaCosteCertResumenSistema.VALOR_ESTIMADO, new ConfigTabla(LineaCosteCertResumenSistema.VALOR_ESTIMADO, LineaCosteCertResumenSistema.VALOR_ESTIMADO, false,6, false));
		configuracionTabla.put(LineaCosteCertResumenSistema.VALOR_REAL, new ConfigTabla(LineaCosteCertResumenSistema.VALOR_REAL, LineaCosteCertResumenSistema.VALOR_REAL, false,7, false));
		
    }
    
   	public void set(String campo, String valor){
   		try {
   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			if (LineaCosteCertResumenSistema.SISTEMA.equals(campo))  return this.c.s.codigo;
   			
   			if (LineaCosteCertResumenSistema.TOTAL.equals(campo)) {
   				if (this.c.concepto!=null) {
   					Concepto conc = this.c.concepto;
   					return FormateadorDatos.formateaDato(conc.valor,TipoDato.FORMATO_MONEDA);
   				}
   				return FormateadorDatos.formateaDato(0,TipoDato.FORMATO_MONEDA);
   			} 
   			if (LineaCosteCertResumenSistema.VALOR_ESTIMADO.equals(campo)) {
   				float acumulado = 0;
   				Iterator<CertificacionFase> itCf = this.c.certificacionesFases.iterator();
   				while (itCf.hasNext()) {
   					CertificacionFase cf = itCf.next();
   					Iterator<CertificacionFaseParcial> itCfp = cf.certificacionesParciales.iterator();
   	   			   while (itCfp.hasNext()) {
   	   				CertificacionFaseParcial cfp = itCfp.next();
   	   				acumulado += cfp.valEstimado;
   	   			   }
   				}
   				acumulado += this.c.conceptoAdicional.valorEstimado;
   			   
   			  return FormateadorDatos.formateaDato(new Float(acumulado),TipoDato.FORMATO_MONEDA);
   			}
   			if (LineaCosteCertResumenSistema.VALOR_REAL.equals(campo))  {
   				float acumulado = 0;
   				Iterator<CertificacionFase> itCf = this.c.certificacionesFases.iterator();
   				while (itCf.hasNext()) {
   					CertificacionFase cf = itCf.next();
   					Iterator<CertificacionFaseParcial> itCfp = cf.certificacionesParciales.iterator();
   	   			   while (itCfp.hasNext()) {
   	   				CertificacionFaseParcial cfp = itCfp.next();
   	   				acumulado += cfp.valReal;
   	   			   }
   				}
   				acumulado += this.c.conceptoAdicional.valor;
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
    		Certificacion valores = (Certificacion) o;
    		return new LineaCosteCertResumenSistema(valores);
    	} catch (Exception e){
    		try {
    			LineaCosteCertResumenSistema f = (LineaCosteCertResumenSistema) o;
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
