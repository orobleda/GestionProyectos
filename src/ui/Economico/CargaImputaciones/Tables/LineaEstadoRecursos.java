package ui.Economico.CargaImputaciones.Tables;

import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.beans.Imputacion;
import model.beans.ParametroRecurso;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaParametro;
import model.metadatos.TipoDato;
import model.metadatos.TipoEnumerado;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;

public class LineaEstadoRecursos extends ParamTable implements Tableable, Comparable<LineaEstadoRecursos>  {
	
	public static final String USUARIO = "Usuario";
	public static final String NOMBRE = "Nombre ENAGAS";
	public static final String NOMBRE_REAL = "Nombre";
	public static final String PERIODO = "Periodo";
	public static final String PROYECTO = "Proyecto";
	public static final String HORAS = "Horas";
	public static final String ESTADO = "Estado";
	
	public Imputacion i;
		
	public LineaEstadoRecursos(Imputacion i) {
		this.i = i;
		setConfig();
    }
    
    public LineaEstadoRecursos() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
		configuracionTabla.put(LineaEstadoRecursos.USUARIO, new ConfigTabla(LineaEstadoRecursos.USUARIO, LineaEstadoRecursos.USUARIO, false,0, false));
		configuracionTabla.put(LineaEstadoRecursos.NOMBRE, new ConfigTabla(LineaEstadoRecursos.NOMBRE, LineaEstadoRecursos.NOMBRE, false,1, false));
		configuracionTabla.put(LineaEstadoRecursos.NOMBRE_REAL, new ConfigTabla(LineaEstadoRecursos.NOMBRE_REAL, LineaEstadoRecursos.NOMBRE_REAL, false,2, false));
		configuracionTabla.put(LineaEstadoRecursos.PERIODO, new ConfigTabla(LineaEstadoRecursos.PERIODO, LineaEstadoRecursos.PERIODO, false,3, false));
		configuracionTabla.put(LineaEstadoRecursos.PROYECTO, new ConfigTabla(LineaEstadoRecursos.PROYECTO, LineaEstadoRecursos.PROYECTO, false,4, false));
		configuracionTabla.put(LineaEstadoRecursos.HORAS, new ConfigTabla(LineaEstadoRecursos.HORAS, LineaEstadoRecursos.HORAS, false,5, false));
		configuracionTabla.put(LineaEstadoRecursos.ESTADO, new ConfigTabla(LineaEstadoRecursos.ESTADO, LineaEstadoRecursos.ESTADO, false,6, false));
		
    	anchoColumnas = new HashMap<String, Integer>();
    	anchoColumnas.put(LineaEstadoRecursos.USUARIO, new Integer(80));
    	anchoColumnas.put(LineaEstadoRecursos.NOMBRE, new Integer(200));
    	anchoColumnas.put(LineaEstadoRecursos.NOMBRE_REAL, new Integer(200));
    	anchoColumnas.put(LineaEstadoRecursos.PERIODO, new Integer(200));
    	anchoColumnas.put(LineaEstadoRecursos.PROYECTO, new Integer(300));
    	anchoColumnas.put(LineaEstadoRecursos.HORAS, new Integer(70));
    	anchoColumnas.put(LineaEstadoRecursos.ESTADO, new Integer(70));
	}
    
   	public void set(String campo, String valor){
   		try {
   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			if (LineaEstadoRecursos.USUARIO.equals(campo)) {
   				ParametroRecurso pr = (ParametroRecurso) this.i.recurso.getValorParametro(MetaParametro.RECURSO_COD_USUARIO);
   				return pr.getValor().toString();
   			}
   			if (LineaEstadoRecursos.NOMBRE.equals(campo)) {
   				return this.i.recurso.nombre;
   			}
   			if (LineaEstadoRecursos.NOMBRE_REAL.equals(campo)) {
   				ParametroRecurso pr = (ParametroRecurso) this.i.recurso.getValorParametro(MetaParametro.RECURSO_NOMBRE_REAL);
   				return pr.getValor().toString();
   			}
   			if (LineaEstadoRecursos.PERIODO.equals(campo)) {
   				return FormateadorDatos.formateaDato(this.i.fxInicio,TipoDato.FORMATO_FECHA) + " - " + FormateadorDatos.formateaDato(this.i.fxFin,TipoDato.FORMATO_FECHA) ;
   			}
   			if (LineaEstadoRecursos.PROYECTO.equals(campo)) {
   				return this.i.proyecto.toString();
   			}
   			if (LineaEstadoRecursos.HORAS.equals(campo)) {
   				return FormateadorDatos.formateaDato(this.i.horas,TipoDato.FORMATO_INT);
   			}
   			if (LineaEstadoRecursos.ESTADO.equals(campo)) {
   				TipoEnumerado tp = TipoEnumerado.listadoIds.get(this.i.estado);
   				if (tp!=null) return tp.toString();
   				else return "";
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
			Imputacion valores = (Imputacion) o;
    		return new LineaEstadoRecursos(valores);
    	} catch (Exception e){
    		try {
    			LineaEstadoRecursos f = (LineaEstadoRecursos) o;
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

	@Override
	public int compareTo(LineaEstadoRecursos arg0) {
		Imputacion o = arg0.i;
		
		if (i.recurso.id == o.recurso.id) {
			if (i.fxInicio.equals(o.fxInicio))
				return i.proyecto.nombre.compareTo(o.proyecto.nombre);
			else
				return i.fxInicio.compareTo(o.fxInicio);
		} else {
			return i.recurso.nombre.compareTo(o.recurso.nombre);
		}
		
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
