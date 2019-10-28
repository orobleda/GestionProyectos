package ui.Economico.EstimacionesInternas.tables;

import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Tabla;
import ui.Economico.EstimacionesInternas.EditarEstimacion;
import ui.Economico.EstimacionesInternas.EstimacionesInternas;
import ui.interfaces.Tableable;

public class LineaDetalleMes extends ParamTable implements Tableable  {
	
	public static final String HORAS = "Horas";
	public static final String IMPORTE = "Importe";
		
	public float horas;
	public float importe;
	public String concepto;
	public String sistema;
	public int recurso;
	
	public EstimacionesInternas ei = null;
	
	public LineaDetalleMes(LineaDetalleMes lcpe) {
		this.horas = lcpe.horas;
		this.importe = lcpe.importe;
		this.concepto = lcpe.concepto;
		this.recurso = lcpe.recurso;
		
    	setConfig();
    }
	
	public LineaDetalleMes clone() {
		LineaDetalleMes lcpe = new LineaDetalleMes();
		this.horas = lcpe.horas;
		this.importe = lcpe.importe;
		this.recurso = lcpe.recurso;
		
		return lcpe;
	}
    
    public LineaDetalleMes() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
		configuracionTabla.put(LineaDetalleMes.HORAS, new ConfigTabla(LineaDetalleMes.HORAS, LineaDetalleMes.HORAS, true,0, false));
		configuracionTabla.put(LineaDetalleMes.IMPORTE, new ConfigTabla(LineaDetalleMes.IMPORTE, LineaDetalleMes.IMPORTE, false,1, false));
		
		this.controlPantalla =  new EditarEstimacion(new ParamTable(), "vueltaPopUp");
    }
    
   	public void set(String campo, String valor){
   		try {
   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			if (LineaDetalleMes.HORAS.equals(campo)) {
   				return FormateadorDatos.formateaDato(this.horas,FormateadorDatos.FORMATO_REAL);
   			}
   			if (LineaDetalleMes.IMPORTE.equals(campo)) {
   				return FormateadorDatos.formateaDato(this.importe,FormateadorDatos.FORMATO_MONEDA);
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
    			LineaDetalleMes f = (LineaDetalleMes) o;
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
			Integer filtro = new Integer((Boolean) valorFiltro ? LineaDetalleUsuario.VISTA_COLAPSADA :  LineaDetalleUsuario.VISTA_EXPANDIDA);
			
			Iterator<Tableable> itLista = listaOriginal.iterator();
			while (itLista.hasNext()) {
				LineaDetalleMes f = (LineaDetalleMes) itLista.next();
				if (filtro == LineaDetalleUsuario.VISTA_COLAPSADA) {
					if (LineaDetalleUsuario.CONCEPTO_HORAS_TOTAL.equals(f.concepto)) dataTable.add(f); else
					if (LineaDetalleUsuario.CONCEPTO_HORAS_ASIGNADAS.equals(f.concepto)) dataTable.add(f); else
					if (LineaDetalleUsuario.CONCEPTO_HORAS_PENDIENTES.equals(f.concepto)) dataTable.add(f);  					
				} else
					dataTable.add(f);					 					
			}
			
			return dataTable;
		} catch (Exception e) {
			return listaOriginal;
		}
	}
	
	@Override
	public String resaltar(int fila, String columna, Tabla tabla) {
		LineaDetalleMes ldm = (LineaDetalleMes) tabla.listaDatosFiltrada.get(fila);
		if (LineaDetalleUsuario.CONCEPTO_HORAS_PENDIENTES.equals(ldm.concepto))
			if (Math.abs(ldm.importe)>0.1) {
				return "-fx-background-color: " + Constantes.COLOR_ROJO;
			} else {
				return "-fx-background-color: " + Constantes.COLOR_VERDE;
			}
		else return null;
	}
	  	
}
