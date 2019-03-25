package ui.Economico.ControlPresupuestario.Tables;

import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.beans.CertificacionFaseParcial;
import model.beans.Concepto;
import model.constantes.FormateadorDatos;
import model.metadatos.TipoDato;
import model.metadatos.TipoPresupuesto;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;

public class LineaCosteCertificacion extends ParamTable implements Tableable  {
	
	public static final String SISTEMA = "Sistema";
	public static final String FASE = "Fase";
	public static final String FECHA = "Fecha";
	public static final String TOTAL = "Presupuestado";
	public static final String TIPO_ESTIMACION = "Tipo Est.";
	public static final String VALOR_ESTIMADO = "Estimado";
	public static final String PORCENTAJE_ESTIMADO = "% Estimado";
	public static final String VALOR_REAL = "Imputado";
	
	public CertificacionFaseParcial cfp = null;
	
	public LineaCosteCertificacion( CertificacionFaseParcial cfp) {
		this.cfp = cfp;
    	setConfig();
    }
    
    public LineaCosteCertificacion() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
		configuracionTabla.put(LineaCosteCertificacion.SISTEMA, new ConfigTabla(LineaCosteCertificacion.SISTEMA, LineaCosteCertificacion.SISTEMA, false,0, false));
		configuracionTabla.put(LineaCosteCertificacion.FASE, new ConfigTabla(LineaCosteCertificacion.FASE, LineaCosteCertificacion.FASE, false,1, false));
		configuracionTabla.put(LineaCosteCertificacion.FECHA, new ConfigTabla(LineaCosteCertificacion.FECHA, LineaCosteCertificacion.FECHA, false,2, false));
		configuracionTabla.put(LineaCosteCertificacion.PORCENTAJE_ESTIMADO, new ConfigTabla(LineaCosteCertificacion.PORCENTAJE_ESTIMADO, LineaCosteCertificacion.PORCENTAJE_ESTIMADO, false,3, false));
		configuracionTabla.put(LineaCosteCertificacion.TOTAL, new ConfigTabla(LineaCosteCertificacion.TOTAL, LineaCosteCertificacion.TOTAL, false,4, false));
		configuracionTabla.put(LineaCosteCertificacion.TIPO_ESTIMACION, new ConfigTabla(LineaCosteCertificacion.TIPO_ESTIMACION, LineaCosteCertificacion.TIPO_ESTIMACION, false,5, false));
		configuracionTabla.put(LineaCosteCertificacion.VALOR_ESTIMADO, new ConfigTabla(LineaCosteCertificacion.VALOR_ESTIMADO, LineaCosteCertificacion.VALOR_ESTIMADO, false,6, false));
		configuracionTabla.put(LineaCosteCertificacion.VALOR_REAL, new ConfigTabla(LineaCosteCertificacion.VALOR_REAL, LineaCosteCertificacion.VALOR_REAL, false,7, false));

    	anchoColumnas = new HashMap<String, Integer>();
    	anchoColumnas.put(LineaCosteCertificacion.SISTEMA, new Integer(60));
    	anchoColumnas.put(LineaCosteCertificacion.TOTAL, new Integer(100));
    	anchoColumnas.put(LineaCosteCertificacion.PORCENTAJE_ESTIMADO, new Integer(80));
    	anchoColumnas.put(LineaCosteCertificacion.TIPO_ESTIMACION, new Integer(80));
    }
    
   	public void set(String campo, String valor){
   		try {
   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			if (LineaCosteCertificacion.SISTEMA.equals(campo))  return this.cfp.certificacionFase.certificacion.s.codigo;
   			if (LineaCosteCertificacion.FASE.equals(campo))  
   				if (this.cfp.certificacionFase.fase!=null) 
   					return this.cfp.certificacionFase.fase.nombre;
   				else
   					return "";
   			if (LineaCosteCertificacion.FECHA.equals(campo))  return FormateadorDatos.formateaDato(this.cfp.fxCertificacion,TipoDato.FORMATO_FECHA);
   			if (LineaCosteCertificacion.TOTAL.equals(campo)) {
   				if (this.cfp.certificacionFase.certificacion.concepto!=null) {
   					Concepto conc = this.cfp.certificacionFase.concepto;
   					return FormateadorDatos.formateaDato(conc.valor*this.cfp.porcentaje/100,TipoDato.FORMATO_MONEDA);
   				}
   				return FormateadorDatos.formateaDato(0,TipoDato.FORMATO_MONEDA);
   			} 
   			if (LineaCosteCertificacion.TIPO_ESTIMACION.equals(campo))  return TipoPresupuesto.listado.get(this.cfp.tipoEstimacion).toString();
   			if (LineaCosteCertificacion.PORCENTAJE_ESTIMADO.equals(campo))  return FormateadorDatos.formateaDato(this.cfp.porcentaje,TipoDato.FORMATO_PORC);
   			if (LineaCosteCertificacion.VALOR_ESTIMADO.equals(campo))  return FormateadorDatos.formateaDato(this.cfp.valEstimado,TipoDato.FORMATO_MONEDA);
   			if (LineaCosteCertificacion.VALOR_REAL.equals(campo))  return FormateadorDatos.formateaDato(this.cfp.valReal,TipoDato.FORMATO_MONEDA);
   				   			
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
    		CertificacionFaseParcial valores = (CertificacionFaseParcial) o;
    		return new LineaCosteCertificacion(valores);
    	} catch (Exception e){
    		try {
    			LineaCosteCertificacion f = (LineaCosteCertificacion) o;
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
