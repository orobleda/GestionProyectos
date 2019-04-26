package ui.Economico.CargaImputaciones.Tables;

import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.beans.Proyecto;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Semaforo;
import ui.interfaces.Tableable;

public class LineaEstadoProyectos extends ParamTable implements Tableable  {
	
	public static final String PROYECTO = "Proyecto";
	public static final String ESTADO = "Estado";
	public static final String TOTAL_IMPUTACIONES = "Imputaciones";
	public static final String TOTAL_ANIADIR = "Añadidas";
	public static final String TOTAL_CAMBIAR = "Cambiar";
	public static final String TOTAL_ELIMINAR = "Eliminadas";
	public static final String TOTAL = "Total";
	
	public HashMap<String, Object> valores;
		
	public LineaEstadoProyectos(HashMap<String, Object> valores) {
		this.valores = valores;
		setConfig();
    }
    
    public LineaEstadoProyectos() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
		configuracionTabla.put(LineaEstadoProyectos.PROYECTO, new ConfigTabla(LineaEstadoProyectos.PROYECTO, LineaEstadoProyectos.PROYECTO, false,0, false));
		configuracionTabla.put(LineaEstadoProyectos.ESTADO, new ConfigTabla(LineaEstadoProyectos.ESTADO, LineaEstadoProyectos.ESTADO, false,1, false));
		configuracionTabla.put(LineaEstadoProyectos.TOTAL_IMPUTACIONES, new ConfigTabla(LineaEstadoProyectos.TOTAL_IMPUTACIONES, LineaEstadoProyectos.TOTAL_IMPUTACIONES, false,2, false));
		configuracionTabla.put(LineaEstadoProyectos.TOTAL_ANIADIR, new ConfigTabla(LineaEstadoProyectos.TOTAL_ANIADIR, LineaEstadoProyectos.TOTAL_ANIADIR, false,3, false));
		configuracionTabla.put(LineaEstadoProyectos.TOTAL_CAMBIAR, new ConfigTabla(LineaEstadoProyectos.TOTAL_CAMBIAR, LineaEstadoProyectos.TOTAL_CAMBIAR, false,4, false));
		configuracionTabla.put(LineaEstadoProyectos.TOTAL_ELIMINAR, new ConfigTabla(LineaEstadoProyectos.TOTAL_ELIMINAR, LineaEstadoProyectos.TOTAL_ELIMINAR, false,5, false));
		configuracionTabla.put(LineaEstadoProyectos.TOTAL, new ConfigTabla(LineaEstadoProyectos.TOTAL, LineaEstadoProyectos.TOTAL, false,6, false));
		
    	anchoColumnas = new HashMap<String, Integer>();
    	anchoColumnas.put(LineaEstadoProyectos.PROYECTO, new Integer(300));
    	anchoColumnas.put(LineaEstadoProyectos.ESTADO, new Integer(150));
    	anchoColumnas.put(LineaEstadoProyectos.TOTAL_IMPUTACIONES, new Integer(90));
    	anchoColumnas.put(LineaEstadoProyectos.TOTAL_ANIADIR, new Integer(70));
    	anchoColumnas.put(LineaEstadoProyectos.TOTAL_CAMBIAR, new Integer(70));
    	anchoColumnas.put(LineaEstadoProyectos.TOTAL_ELIMINAR, new Integer(70));
    	anchoColumnas.put(LineaEstadoProyectos.TOTAL, new Integer(70));
	}
    
   	public void set(String campo, String valor){
   		try {
   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			if (LineaEstadoProyectos.PROYECTO.equals(campo))
   					return ((Proyecto) this.valores.get(LineaEstadoProyectos.PROYECTO)).toString();
   			if (LineaEstadoProyectos.ESTADO.equals(campo)) {
   				Integer i = (Integer) this.valores.get(LineaEstadoProyectos.ESTADO);
   				if (i==Semaforo.VERDE) return "OK";
   				if (i==Semaforo.ROJO) return "KO";
   				if (i==Semaforo.AMBAR) return "Con Salvedades";
   			}	
   			if (LineaEstadoProyectos.TOTAL_IMPUTACIONES.equals(campo)) {
   				return ((Integer) this.valores.get(LineaEstadoProyectos.TOTAL_IMPUTACIONES)).toString();				
   			}
   			if (LineaEstadoProyectos.TOTAL_ANIADIR.equals(campo)){
   				return ((Integer) this.valores.get(LineaEstadoProyectos.TOTAL_ANIADIR)).toString();
   			}
   			if (LineaEstadoProyectos.TOTAL_CAMBIAR.equals(campo)){
   				return ((Integer) this.valores.get(LineaEstadoProyectos.TOTAL_CAMBIAR)).toString();
   			}
   			if (LineaEstadoProyectos.TOTAL_ELIMINAR.equals(campo)){
   				return ((Integer) this.valores.get(LineaEstadoProyectos.TOTAL_ELIMINAR)).toString();
   			}
   			if (LineaEstadoProyectos.TOTAL.equals(campo)){
   				return ((Integer) this.valores.get(LineaEstadoProyectos.TOTAL)).toString();
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
    		return new LineaEstadoProyectos(valores);
    	} catch (Exception e){
    		try {
    			LineaEstadoProyectos f = (LineaEstadoProyectos) o;
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
	
	/*
	@Override
	public String resaltar(int fila, String columna, Tabla tabla) {
		try {
			LineaEstadoProyectos lce = (LineaEstadoProyectos) tabla.listaDatosFiltrada.get(fila);
			
			Float cantidad = (Float) FormateadorDatos.parseaDato(lce.get(columna),TipoDato.FORMATO_MONEDA);
			
			if (cantidad<0)
			     return "-fx-background-color: " + Constantes.COLOR_AMARILLO;
			else return null;
		} catch (Exception e) {return null;}
	}
	*/
}
