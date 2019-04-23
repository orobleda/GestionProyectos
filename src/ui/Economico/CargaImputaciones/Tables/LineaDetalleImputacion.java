package ui.Economico.CargaImputaciones.Tables;

import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.beans.Estimacion;
import model.beans.Imputacion;
import model.beans.ParametroRecurso;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaParametro;
import model.metadatos.TipoDato;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;

public class LineaDetalleImputacion extends ParamTable implements Tableable  {
	
	public static final String USUARIO = "Usuario";
	public static final String NOMBRE = "Nombre ENAGAS";
	public static final String NOMBRE_REAL = "Nombre";
	public static final String TARIFA = "Tarifa";
	public static final String ESTIMADO = "Estimado";
	public static final String IMPUTADO = "Imputado";
	public static final String PERIODO = "Periodo";
	
	public Estimacion est = null;
	public Imputacion imp = null;
	
	public static String ESTIMACION = "Estimacion";
	public static String IMPUTACION = "Imputacion";
	
	public LineaDetalleImputacion(HashMap<String, Object> detalle) {
		this.est = (Estimacion) detalle.get(ESTIMACION);
		this.imp = (Imputacion) detalle.get(IMPUTACION);
    	setConfig();
    }
    
    public LineaDetalleImputacion() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
		configuracionTabla.put(LineaDetalleImputacion.USUARIO, new ConfigTabla(LineaDetalleImputacion.USUARIO, LineaDetalleImputacion.USUARIO, true,0, false));
		configuracionTabla.put(LineaDetalleImputacion.NOMBRE, new ConfigTabla(LineaDetalleImputacion.NOMBRE, LineaDetalleImputacion.NOMBRE, true,1, false));
		configuracionTabla.put(LineaDetalleImputacion.NOMBRE_REAL, new ConfigTabla(LineaDetalleImputacion.NOMBRE_REAL, LineaDetalleImputacion.NOMBRE_REAL, true,2, false));
		configuracionTabla.put(LineaDetalleImputacion.TARIFA, new ConfigTabla(LineaDetalleImputacion.TARIFA, LineaDetalleImputacion.TARIFA, true,3, false));
		configuracionTabla.put(LineaDetalleImputacion.ESTIMADO, new ConfigTabla(LineaDetalleImputacion.ESTIMADO, LineaDetalleImputacion.ESTIMADO, true,4, false));
		configuracionTabla.put(LineaDetalleImputacion.IMPUTADO, new ConfigTabla(LineaDetalleImputacion.IMPUTADO, LineaDetalleImputacion.IMPUTADO, true,5, false));
		configuracionTabla.put(LineaDetalleImputacion.PERIODO, new ConfigTabla(LineaDetalleImputacion.PERIODO, LineaDetalleImputacion.PERIODO, true,6, false));
		
    	anchoColumnas = new HashMap<String, Integer>();
    	anchoColumnas.put(LineaDetalleImputacion.USUARIO, new Integer(100));
    	anchoColumnas.put(LineaDetalleImputacion.NOMBRE, new Integer(250));
    	anchoColumnas.put(LineaDetalleImputacion.NOMBRE_REAL, new Integer(250));
    	anchoColumnas.put(LineaDetalleImputacion.TARIFA, new Integer(100));
    	anchoColumnas.put(LineaDetalleImputacion.ESTIMADO, new Integer(100));
    	anchoColumnas.put(LineaDetalleImputacion.IMPUTADO, new Integer(100));
    	anchoColumnas.put(LineaDetalleImputacion.PERIODO, new Integer(100));
    }
    
   	public void set(String campo, String valor){
   		try {
   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			if (LineaDetalleImputacion.USUARIO.equals(campo))
   					return this.imp.codRecurso;
   			if (LineaDetalleImputacion.NOMBRE.equals(campo))
   				if (this.imp.recurso==null)
   					return "";
   				else 
   					return this.imp.recurso.nombre;
   			if (LineaDetalleImputacion.NOMBRE_REAL.equals(campo))
   				if (this.imp.recurso==null)
   					return "";
   				else {
   					ParametroRecurso pr = (ParametroRecurso) this.imp.recurso.getValorParametro(MetaParametro.RECURSO_NOMBRE_REAL);
   					if (pr!=null)
   						return (String) pr.getValor();
   				}
   					   			
   			if (LineaDetalleImputacion.TARIFA.equals(campo))
   				if (this.imp.tarifa==null)
   					return FormateadorDatos.formateaDato(this.imp.fTarifa,TipoDato.FORMATO_MONEDA);
   				else 
   					return FormateadorDatos.formateaDato(this.imp.tarifa.costeHora,TipoDato.FORMATO_MONEDA);
   			if (LineaDetalleImputacion.ESTIMADO.equals(campo))  
   				if (this.est !=null)
   					return FormateadorDatos.formateaDato(this.est.importe,TipoDato.FORMATO_MONEDA);
   			if (LineaDetalleImputacion.IMPUTADO.equals(campo)) return FormateadorDatos.formateaDato(this.imp.importe,TipoDato.FORMATO_MONEDA);
   			if (LineaDetalleImputacion.PERIODO.equals(campo)) return FormateadorDatos.formateaDato(this.imp.fxInicio,TipoDato.FORMATO_FECHA);
   				   			
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
    		return new LineaDetalleImputacion(valores);
    	} catch (Exception e){
    		try {
    			LineaDetalleImputacion f = (LineaDetalleImputacion) o;
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
