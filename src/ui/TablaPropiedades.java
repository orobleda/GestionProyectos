package ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.Editors;
import org.controlsfx.property.editor.PropertyEditor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import model.constantes.Constantes;
import model.metadatos.MetaFormatoProyecto;
import model.metadatos.TipoDato;
import model.metadatos.TipoProyecto;
import ui.interfaces.Propiediable;

public class TablaPropiedades extends PropertySheet{
	
	HashMap<Integer,Object> filtro = null;
	double ancho = 0;
	double alto = 0;
	ObservableList<PropertySheet.Item> list = null;
	
	
	public static ObservableList<PropertySheet.Item> toList(ArrayList<? extends Propiediable> listaElementos, HashMap<String,Boolean> readOnlyProps) {
		Iterator<? extends Propiediable> lista = listaElementos.iterator();
		ObservableList<PropertySheet.Item> list = FXCollections.observableArrayList();
		
		while (lista.hasNext()) {
			Propiediable elemento = lista.next();
			Propiedad pro = elemento.toPropiedad();
			
			if (readOnlyProps!=null && readOnlyProps.containsKey(elemento.getCodigo())) {
				pro.editable = readOnlyProps.get(elemento.getCodigo());
			}
			
			list.add(pro);
		}
		
		return list;
	}
	
	public static ObservableList<PropertySheet.Item> toList(ArrayList<? extends Propiediable> listaElementos) {
		return TablaPropiedades.toList(listaElementos, null);
	}
	
	public TablaPropiedades(ObservableList<PropertySheet.Item> list, double ancho, double alto, HashMap<Integer,Object> filtro) {
		super(list);
		this.filtro = filtro;
		this.list = list;
		this.ancho = ancho;
		this.alto = alto;
		inicializa();
	}
	
	public TablaPropiedades(ObservableList<PropertySheet.Item> list, double ancho, double alto) {	
		super(list);
		this.list = list;
		this.ancho = ancho;
		this.alto = alto;
		inicializa();
	}
	
	public void inicializa() {
		this.setPrefHeight(alto);
		this.setPrefWidth(ancho);
		
		this.setPropertyEditorFactory(new Callback<PropertySheet.Item, PropertyEditor<?>>() {
		    @Override
		    public PropertyEditor<?> call(Item param) {
		    	Propiedad prop = (Propiedad) param;
		    	
		    	if (prop.tipo == TipoDato.FORMATO_INT) {
		    		return Editors.createNumericEditor(param);
		    	}
		    	
		    	if (prop.tipo == TipoDato.FORMATO_REAL) {
		    		return Editors.createNumericEditor(param);
		    	}
		    	
		    	if (prop.tipo == TipoDato.FORMATO_FECHA) {
		    		return Editors.createDateEditor(param);
		    	}
		    	
		    	if (prop.tipo == TipoDato.FORMATO_FORMATO_PROYECTO) {
		    		return Editors.createChoiceEditor(param, MetaFormatoProyecto.listado.values());
		    	}
		    	
		    	if (prop.tipo == TipoDato.FORMATO_TIPO_PROYECTO) {
		    		if (filtro!=null && filtro.containsKey(prop.tipo)) {
		    			ArrayList<TipoProyecto> elementosFiltrados =  (ArrayList<TipoProyecto>) filtro.get(prop.tipo);
		    			return Editors.createChoiceEditor(param, elementosFiltrados);
		    		} else return Editors.createChoiceEditor(param, TipoProyecto.listado.values());
		    	}
		    	
		    	if (prop.tipo == TipoDato.FORMATO_BOOLEAN) {
		    		return Editors.createChoiceEditor(param, Constantes.opcionesYesNo());
		    	}
		    	
		    	if (prop.tipo == TipoDato.FORMATO_TXT || prop.tipo == TipoDato.FORMATO_URL) {
		    		return Editors.createTextEditor(param);
		    	}
		    	
		    	return null;
		     }
		});
	}
	
	public 	PropertySheet getTabla() {
		return (PropertySheet) this;
	}
}
