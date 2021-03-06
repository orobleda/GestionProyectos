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
import model.metadatos.TipoEnumerado;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;

public class LineaDetalleImputacion extends ParamTable implements Tableable  {
	
	public static final String USUARIO = "Usuario";
	public static final String NOMBRE = "Nombre ENAGAS";
	public static final String NOMBRE_REAL = "Nombre";
	public static final String TARIFA = "Tarifa";
	public static final String SISTEMA = "Sistema";
	public static final String ESTIMADO = "Estimado";
	public static final String IMPUTADO = "Imputado";
	public static final String FRACCIONADO = "Fraccionado";
	public static final String A_CARGAR = "A cargar";
	public static final String PERIODO = "Periodo";
	public static final String ESTADO = "Estado";
	
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
		configuracionTabla.put(LineaDetalleImputacion.SISTEMA, new ConfigTabla(LineaDetalleImputacion.SISTEMA, LineaDetalleImputacion.SISTEMA, true,4, false));
		configuracionTabla.put(LineaDetalleImputacion.ESTIMADO, new ConfigTabla(LineaDetalleImputacion.ESTIMADO, LineaDetalleImputacion.ESTIMADO, true,4, false));
		configuracionTabla.put(LineaDetalleImputacion.IMPUTADO, new ConfigTabla(LineaDetalleImputacion.IMPUTADO, LineaDetalleImputacion.IMPUTADO, true,5, false));
		configuracionTabla.put(LineaDetalleImputacion.A_CARGAR, new ConfigTabla(LineaDetalleImputacion.A_CARGAR, LineaDetalleImputacion.A_CARGAR, true,6, false));
		configuracionTabla.put(LineaDetalleImputacion.FRACCIONADO, new ConfigTabla(LineaDetalleImputacion.FRACCIONADO, LineaDetalleImputacion.FRACCIONADO, true,7, false));
		configuracionTabla.put(LineaDetalleImputacion.PERIODO, new ConfigTabla(LineaDetalleImputacion.PERIODO, LineaDetalleImputacion.PERIODO, true,8, false));
		configuracionTabla.put(LineaDetalleImputacion.ESTADO, new ConfigTabla(LineaDetalleImputacion.ESTADO, LineaDetalleImputacion.ESTADO, true,9, false));
		
    	anchoColumnas = new HashMap<String, Integer>();
    	anchoColumnas.put(LineaDetalleImputacion.USUARIO, new Integer(100));
    	anchoColumnas.put(LineaDetalleImputacion.NOMBRE, new Integer(190));
    	anchoColumnas.put(LineaDetalleImputacion.NOMBRE_REAL, new Integer(190));
    	anchoColumnas.put(LineaDetalleImputacion.TARIFA, new Integer(100));
    	anchoColumnas.put(LineaDetalleImputacion.SISTEMA, new Integer(100));
    	anchoColumnas.put(LineaDetalleImputacion.ESTIMADO, new Integer(100));
    	anchoColumnas.put(LineaDetalleImputacion.IMPUTADO, new Integer(100));
    	anchoColumnas.put(LineaDetalleImputacion.A_CARGAR, new Integer(100));
    	anchoColumnas.put(LineaDetalleImputacion.FRACCIONADO, new Integer(100));
    	anchoColumnas.put(LineaDetalleImputacion.PERIODO, new Integer(100));
    	anchoColumnas.put(LineaDetalleImputacion.ESTADO, new Integer(80));
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
   			if (LineaDetalleImputacion.A_CARGAR.equals(campo)) return FormateadorDatos.formateaDato(this.imp.importe,TipoDato.FORMATO_MONEDA);
   			if (LineaDetalleImputacion.SISTEMA.equals(campo)) 
   				if (this.est !=null)
   					return est.sistema.codigo;
   			if (LineaDetalleImputacion.IMPUTADO.equals(campo)) 
   				if (this.imp!=null && this.imp.imputacionPrevia!=null) {
   					if (this.imp.imputacionPrevia.tipoImputacion == Imputacion.IMPUTACION_NO_FRACCIONADA) 
   						return FormateadorDatos.formateaDato(this.imp.imputacionPrevia.importe,TipoDato.FORMATO_MONEDA);
   					else 
   						if (this.imp.imputacionPrevia.imputacionFraccion!=null) {
   							return FormateadorDatos.formateaDato(this.imp.imputacionPrevia.imputacionFraccion.getImporte(),TipoDato.FORMATO_MONEDA);
   						}
   				}
   			if (LineaDetalleImputacion.FRACCIONADO.equals(campo)) 
   				if (this.imp!=null && this.imp.imputacionPrevia!=null) {
   					if (this.imp.imputacionPrevia.tipoImputacion == Imputacion.IMPUTACION_NO_FRACCIONADA) 
   						return "NO";
   					else 
   						return "SI";
   				}
   			if (LineaDetalleImputacion.PERIODO.equals(campo)) return FormateadorDatos.formateaDato(this.imp.fxInicio,TipoDato.FORMATO_FECHA);
   			if (LineaDetalleImputacion.ESTADO.equals(campo)) {
   				TipoEnumerado tp = TipoEnumerado.listadoIds.get(this.imp.estado);
   				if (tp!=null)
   					return tp.valor;
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
