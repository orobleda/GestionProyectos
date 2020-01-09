package ui.Economico.ControlPresupuestario.EdicionCert.Tables;

import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.beans.CertificacionFaseParcial;
import model.beans.Concepto;
import model.constantes.FormateadorDatos;
import model.metadatos.TipoDato;
import model.metadatos.TipoEnumerado;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Economico.ControlPresupuestario.EdicionCert.EdicionCertificacion;
import ui.interfaces.Tableable;

public class LineaCosteCertificacionReal extends ParamTable implements Tableable  {
	
	public static final String DESCRIPCION = "Descripción";
	public static final String IMPORTE = "Importe";
	public static final String HORAS = "Horas";
	public static final String TARIFA = "Tarifa";
	
	public CertificacionFaseParcial cfp = null;
	
	public LineaCosteCertificacionReal( CertificacionFaseParcial cfp) {
		this.cfp = cfp;
    	setConfig();
    }
    
    public LineaCosteCertificacionReal() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
		configuracionTabla.put(LineaCosteCertificacionReal.DESCRIPCION, new ConfigTabla(LineaCosteCertificacionReal.DESCRIPCION, LineaCosteCertificacionReal.DESCRIPCION, true,0, false));
		configuracionTabla.put(LineaCosteCertificacionReal.IMPORTE, new ConfigTabla(LineaCosteCertificacionReal.IMPORTE, LineaCosteCertificacionReal.IMPORTE, true,1, false));
		configuracionTabla.put(LineaCosteCertificacionReal.HORAS, new ConfigTabla(LineaCosteCertificacionReal.HORAS, LineaCosteCertificacionReal.HORAS, true,2, false));
		configuracionTabla.put(LineaCosteCertificacionReal.TARIFA, new ConfigTabla(LineaCosteCertificacionReal.TARIFA, LineaCosteCertificacionReal.TARIFA, true,3, false));
		
    }
    
   	public void set(String campo, String valor){
   		try {
   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			if (LineaCosteCertificacionReal.DESCRIPCION.equals(campo))  return this.cfp.nombre;
   			if (LineaCosteCertificacionReal.IMPORTE.equals(campo))   return FormateadorDatos.formateaDato(this.cfp.valReal,TipoDato.FORMATO_MONEDA);;
   			if (LineaCosteCertificacionReal.HORAS.equals(campo))  return FormateadorDatos.formateaDato(this.cfp.horReal,TipoDato.FORMATO_REAL);
   			if (LineaCosteCertificacionReal.TARIFA.equals(campo))  return FormateadorDatos.formateaDato(this.cfp.valReal/this.cfp.horReal,TipoDato.FORMATO_REAL);
   				   			
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
    		CertificacionFaseParcial valores = (CertificacionFaseParcial) o;
    		return new LineaCosteCertificacionReal(valores);
    	} catch (Exception e){
    		try {
    			LineaCosteCertificacionReal f = (LineaCosteCertificacionReal) o;
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
