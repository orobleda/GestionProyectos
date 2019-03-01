package ui.Economico.EstimacionesInternas.tables;

import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import model.constantes.FormateadorDatos;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;

public class LineaDetalleUsuario extends ParamTable implements Tableable  {
	
	public static final String COD_USUARIO = "Código U.";
	public static final String USUARIO = "Usuario";
	public static final String CONCEPTO = "Concepto";
	public static final String SISTEMA = "Sistema";
	
	public static final int VISTA_COLAPSADA = 0;
	public static final int VISTA_EXPANDIDA = 1;
	
	public static final String CONCEPTO_HORAS_BASE = "Horas Base";
	public static final String CONCEPTO_HORAS_AUSENCIAS = "Ausencias";
	public static final String CONCEPTO_HORAS_TOTAL = "Horas Total";
	public static final String CONCEPTO_HORAS_ASIGNADAS = "Horas Asignadas";
	public static final String CONCEPTO_HORAS_PENDIENTES = "Horas Pendientes";
	
	public String codUsuario;
	public String nomUsuario;
	public String concepto;
	public String sistema;
	
	public LineaDetalleUsuario(LineaDetalleUsuario lcpe) {
		this.codUsuario = lcpe.codUsuario;
		this.nomUsuario = lcpe.nomUsuario;
		this.concepto = lcpe.concepto;
		
    	setConfig();
    }
	
	public LineaDetalleUsuario clone() {
		LineaDetalleUsuario lcpe = new LineaDetalleUsuario();
		lcpe.codUsuario = this.codUsuario;
		lcpe.nomUsuario = this.nomUsuario;
		lcpe.concepto = this.concepto;
				
		return lcpe;
	}
    
    public LineaDetalleUsuario() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
		configuracionTabla.put(LineaDetalleUsuario.COD_USUARIO, new ConfigTabla(LineaDetalleUsuario.COD_USUARIO, LineaDetalleUsuario.COD_USUARIO, false,0, false));
		configuracionTabla.put(LineaDetalleUsuario.USUARIO, new ConfigTabla(LineaDetalleUsuario.USUARIO, LineaDetalleUsuario.USUARIO, false,1, false));
		configuracionTabla.put(LineaDetalleUsuario.CONCEPTO, new ConfigTabla(LineaDetalleUsuario.CONCEPTO, LineaDetalleUsuario.CONCEPTO, false,2, false));
		configuracionTabla.put(LineaDetalleUsuario.SISTEMA, new ConfigTabla(LineaDetalleUsuario.SISTEMA, LineaDetalleUsuario.SISTEMA, false,3, false));
		
		anchoColumnas = new HashMap<String, Integer>();
		anchoColumnas.put(LineaDetalleUsuario.USUARIO, new Integer(130));
		anchoColumnas.put(LineaDetalleUsuario.CONCEPTO, new Integer(130));
		anchoColumnas.put(LineaDetalleUsuario.SISTEMA, new Integer(75));
		
    }
    
   	public void set(String campo, String valor){
   		try {
   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			if (LineaDetalleUsuario.COD_USUARIO.equals(campo)) {
   				return this.codUsuario;
   			}
   			if (LineaDetalleUsuario.USUARIO.equals(campo)) {
   				return this.nomUsuario;
   			}
   			if (LineaDetalleUsuario.CONCEPTO.equals(campo)) {
   				return this.concepto;
   			}
   			if (LineaDetalleUsuario.SISTEMA.equals(campo)) {
   				return this.sistema;
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
    			LineaDetalleUsuario f = (LineaDetalleUsuario) o;
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
				LineaDetalleUsuario f = (LineaDetalleUsuario) itLista.next();
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
	  	
}
