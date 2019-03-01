package ui.Economico.EstimacionesInternas.tables;

import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import model.beans.Proyecto;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.metadatos.Sistema;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Tabla;
import ui.interfaces.Tableable;

public class LineaCosteProyectoEstimacion extends ParamTable implements Tableable  {
	
	public static final String PROYECTO = "Proyecto";
	public static final String SISTEMA = "Sistema";
	public static final String PRESUPUESTADO = "Presupuestado";
	public static final String ESTIMADOCURSO = "Estimado Año";
	public static final String ESTIMADORESTO = "Estimado Resto";
	public static final String PROVISIONADOCURSO = "Provisionado Año";
	public static final String PROVISIONADORESTO = "Provisionado Resto";
	public static final String TOTAL = "Total";
	public static final String RESTANTE = "Restante";
	
	public Proyecto proyecto;
	public Sistema sistema;
	public float presupuestado;
	public float estimadoAnio;
	public float estimadoResto;
	public float provisionadoAnio;
	public float provisionadoResto;
	public float total;
	public float restante;	
	public boolean provisionadoAnioModificado = false;
	public boolean provisionadoRestoModificado = false;
	
	public LineaCosteProyectoEstimacion(LineaCosteProyectoEstimacion lcpe) {
		this.proyecto = lcpe.proyecto;
		this.sistema = lcpe.sistema;
		this.presupuestado = lcpe.presupuestado;
		this.estimadoAnio = lcpe.estimadoAnio;
		this.estimadoResto = lcpe.estimadoResto;
		this.provisionadoAnio = lcpe.provisionadoAnio;
		this.provisionadoResto = lcpe.provisionadoResto;
		this.total = lcpe.total;
		this.restante = lcpe.restante;
		
    	setConfig();
    }
    
    public LineaCosteProyectoEstimacion() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
		configuracionTabla.put(LineaCosteProyectoEstimacion.PROYECTO, new ConfigTabla(LineaCosteProyectoEstimacion.PROYECTO, LineaCosteProyectoEstimacion.PROYECTO, false,0, false));
		configuracionTabla.put(LineaCosteProyectoEstimacion.SISTEMA, new ConfigTabla(LineaCosteProyectoEstimacion.SISTEMA, LineaCosteProyectoEstimacion.SISTEMA, false,1, false));
		configuracionTabla.put(LineaCosteProyectoEstimacion.PRESUPUESTADO, new ConfigTabla(LineaCosteProyectoEstimacion.PRESUPUESTADO, LineaCosteProyectoEstimacion.PRESUPUESTADO, false,3, false));
		configuracionTabla.put(LineaCosteProyectoEstimacion.ESTIMADOCURSO, new ConfigTabla(LineaCosteProyectoEstimacion.ESTIMADOCURSO, LineaCosteProyectoEstimacion.ESTIMADOCURSO, false,4, false));
		configuracionTabla.put(LineaCosteProyectoEstimacion.ESTIMADORESTO, new ConfigTabla(LineaCosteProyectoEstimacion.ESTIMADORESTO, LineaCosteProyectoEstimacion.ESTIMADORESTO, false,5, false));
		configuracionTabla.put(LineaCosteProyectoEstimacion.PROVISIONADOCURSO, new ConfigTabla(LineaCosteProyectoEstimacion.PROVISIONADOCURSO, LineaCosteProyectoEstimacion.PROVISIONADOCURSO, false,6, false));
		configuracionTabla.put(LineaCosteProyectoEstimacion.PROVISIONADORESTO, new ConfigTabla(LineaCosteProyectoEstimacion.PROVISIONADORESTO, LineaCosteProyectoEstimacion.PROVISIONADORESTO, false,7, false));
		configuracionTabla.put(LineaCosteProyectoEstimacion.TOTAL, new ConfigTabla(LineaCosteProyectoEstimacion.TOTAL, LineaCosteProyectoEstimacion.TOTAL, true,8, false));
		configuracionTabla.put(LineaCosteProyectoEstimacion.RESTANTE, new ConfigTabla(LineaCosteProyectoEstimacion.RESTANTE, LineaCosteProyectoEstimacion.RESTANTE, true,9, false));
    }
    
   	public void set(String campo, String valor){
   		try {
   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			if (LineaCosteProyectoEstimacion.PROYECTO.equals(campo)) {
   				if (this.proyecto==null) return "";
   				return this.proyecto.nombre;
   			}
   			if (LineaCosteProyectoEstimacion.SISTEMA.equals(campo)) {
   				if (this.sistema==null) return "";
   				return this.sistema.codigo;
   			}
   			if (LineaCosteProyectoEstimacion.TOTAL.equals(campo)) {
   				return FormateadorDatos.formateaDato(this.total, FormateadorDatos.FORMATO_MONEDA);
   			}
   			if (LineaCosteProyectoEstimacion.PRESUPUESTADO.equals(campo)) {
   				return FormateadorDatos.formateaDato(this.presupuestado, FormateadorDatos.FORMATO_MONEDA);
   			}
   			if (LineaCosteProyectoEstimacion.RESTANTE.equals(campo)) {
   				return FormateadorDatos.formateaDato(this.restante, FormateadorDatos.FORMATO_MONEDA);
   			}
   			if (LineaCosteProyectoEstimacion.ESTIMADOCURSO.equals(campo)) {
   				return FormateadorDatos.formateaDato(this.estimadoAnio, FormateadorDatos.FORMATO_MONEDA);
   			}
   			if (LineaCosteProyectoEstimacion.ESTIMADORESTO.equals(campo)) {
   				return FormateadorDatos.formateaDato(this.estimadoResto, FormateadorDatos.FORMATO_MONEDA);
   			}
   			if (LineaCosteProyectoEstimacion.PROVISIONADOCURSO.equals(campo)) {
   				return FormateadorDatos.formateaDato(this.provisionadoAnio, FormateadorDatos.FORMATO_MONEDA);
   			}
   			if (LineaCosteProyectoEstimacion.PROVISIONADORESTO.equals(campo)) {
   				return FormateadorDatos.formateaDato(this.provisionadoResto, FormateadorDatos.FORMATO_MONEDA);
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
    			LineaCosteProyectoEstimacion f = (LineaCosteProyectoEstimacion) o;
    			return f;
    		} catch (Exception ex) {
    			return null;
    		}
	}   
	
	@Override
	public AnchorPane getFilaEmbebida(TableRowDataFeatures<Tableable> expander) {
		return null;
	}
	
	@Override
	public ObservableList<Tableable> filtrar(Object valorFiltro, ObservableList<Tableable> listaOriginal) {
		try {
			ObservableList<Tableable> dataTable = FXCollections.observableArrayList();
			Integer filtro = (Integer) FormateadorDatos.parseaDato((String) valorFiltro,FormateadorDatos.FORMATO_INT);
			
			Iterator<Tableable> itLista = listaOriginal.iterator();
			while (itLista.hasNext()) {
				LineaCosteProyectoEstimacion f = (LineaCosteProyectoEstimacion) itLista.next();
				if (Math.abs(f.restante)>=filtro) {
					dataTable.add(f);
				}
			}
			
			return dataTable;
		} catch (Exception e) {
			return listaOriginal;
		}
	}
	
	@Override
	public String resaltar(int fila, String columna, Tabla tabla) {
		LineaCosteProyectoEstimacion ldm = (LineaCosteProyectoEstimacion) tabla.listaDatosFiltrada.get(fila);
		if (ldm.restante<0)
				return "-fx-background-color: " + Constantes.COLOR_AMARILLO;
		else return null;
	}
	  	
}
