package ui.Economico.ControlPresupuestario.Tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import model.beans.Foto;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import model.metadatos.TipoDato;
import model.metadatos.TipoEnumerado;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Economico.ControlPresupuestario.GestionesFoto;
import ui.interfaces.Tableable;

public class LineaFoto extends ParamTable implements Tableable  {
	
	public Foto f = null;
	
	public static final String ID = "Id";
	public static final String TIPO = "Tipo";
	public static final String FXCREACION = "Fecha Creación";
	public static final String NOMBRE = "Nombre";
	public static final String PERSISTIR = "Persistir";
	public static final String VALANIO = "Estimado Año";
	public static final String VALTOTAL = "Estimado Total";
	
	public LineaFoto(Foto f) {
		this.f = f;
    	setConfig();
    }
    
    public LineaFoto() {
    	f = new Foto();
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	configuracionTabla.put(LineaFoto.ID, new ConfigTabla(LineaFoto.ID, LineaFoto.ID, true,0, false));
    	configuracionTabla.put(LineaFoto.TIPO, new ConfigTabla(LineaFoto.TIPO, LineaFoto.TIPO, true,1, false));
    	configuracionTabla.put(LineaFoto.FXCREACION, new ConfigTabla(LineaFoto.FXCREACION, LineaFoto.FXCREACION, true,2, false));
    	configuracionTabla.put(LineaFoto.NOMBRE, new ConfigTabla(LineaFoto.NOMBRE, LineaFoto.NOMBRE, true,3, false));
    	configuracionTabla.put(LineaFoto.PERSISTIR, new ConfigTabla(LineaFoto.PERSISTIR, LineaFoto.PERSISTIR, true,4, false));
    	configuracionTabla.put(LineaFoto.VALANIO, new ConfigTabla(LineaFoto.VALANIO, LineaFoto.VALANIO, true,5, false));
    	configuracionTabla.put(LineaFoto.VALTOTAL, new ConfigTabla(LineaFoto.VALTOTAL, LineaFoto.VALTOTAL, true,6, false));
    	
    	this.controlPantalla =  new GestionesFoto(new ParamTable(), "vueltaPopUp");
    	
    	anchoColumnas = new HashMap<String, Integer>();
    	anchoColumnas.put(LineaFoto.NOMBRE, new Integer(250));
    	anchoColumnas.put(LineaFoto.TIPO, new Integer(150));
    }
    
   	public void set(String campo, String valor){
   		  			
   	}
       
   	public String get(String campo) {
   		try {
   			if (campo.equals(LineaFoto.ID)) return this.f.id+"";
   			if (campo.equals(LineaFoto.TIPO)) return this.f.tipo.valor;
   			if (campo.equals(LineaFoto.FXCREACION)) return FormateadorDatos.formateaDato(f.fxCreacion, FormateadorDatos.FORMATO_FECHA);;
   			if (campo.equals(LineaFoto.NOMBRE)) return this.f.nombreFoto;
   			if (campo.equals(LineaFoto.PERSISTIR)) return FormateadorDatos.formateaDato(this.f.persistir,TipoDato.FORMATO_BOOLEAN);
   			if (campo.equals(LineaFoto.VALANIO)) return FormateadorDatos.formateaDato(this.f.valorAnioCurso,TipoDato.FORMATO_MONEDA);
   			if (campo.equals(LineaFoto.VALTOTAL)) return FormateadorDatos.formateaDato(this.f.valorTotal,TipoDato.FORMATO_MONEDA);

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
			Foto f = (Foto) o;
    		return new LineaFoto(f);
    	} catch (Exception e){
    		try {
    			LineaFoto f = (LineaFoto) o;
    			return f;
    		} catch (Exception ex) {
    			return null;
    		}
    	}
	}
    
	@Override
	public ObservableList<Tableable> filtrar(Object valorFiltro, ObservableList<Tableable> listaOriginal){
		try {
			ObservableList<Tableable> dataTable = FXCollections.observableArrayList();
			ArrayList<Object> filtros  = ((ArrayList<Object>) valorFiltro);
			ComboBox<TipoEnumerado> cTipo = (ComboBox<TipoEnumerado>) filtros.get(0);
			
			Iterator<Tableable> itLista = listaOriginal.iterator();
			while (itLista.hasNext()) {
				boolean incluir = true;
				LineaFoto f = (LineaFoto) itLista.next();
				if (cTipo!=null && cTipo.getValue()!=null && cTipo.getValue().id != f.f.tipo.id) {
					incluir = false;
				}
				
				if (incluir)
					dataTable.add(f);
			}
			
			return dataTable;
		} catch (Exception e) {
			return listaOriginal;
		}
		
	} 
   
	
	@Override
	public AnchorPane getFilaEmbebida(TableRowDataFeatures<Tableable> expander) {
		return null;
	}
	  	
}
