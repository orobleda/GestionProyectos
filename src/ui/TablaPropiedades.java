package ui;

import java.util.ArrayList;
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

public class TablaPropiedades extends PropertySheet{
	
	public static ObservableList<PropertySheet.Item> toList(ArrayList<? extends Propiediable> listaElementos) {
		Iterator<? extends Propiediable> lista = listaElementos.iterator();
		ObservableList<PropertySheet.Item> list = FXCollections.observableArrayList();
		
		while (lista.hasNext()) {
			Propiediable elemento = lista.next();
			list.add(elemento.toPropiedad());
		}
		
		return list;
	}
	
	public TablaPropiedades(ObservableList<PropertySheet.Item> list) {	
		super(list);
		
		
		this.setPropertyEditorFactory(new Callback<PropertySheet.Item, PropertyEditor<?>>() {
		    @Override
		    public PropertyEditor<?> call(Item param) {
		    	Propiedad prop = (Propiedad) param;
		    	
		    	if (prop.tipo == TipoDato.FORMATO_INT) {
		    		return Editors.createNumericEditor(param);
		    	}
		    	
		    	if (prop.tipo == TipoDato.FORMATO_FORMATO_PROYECTO) {
		    		return Editors.createChoiceEditor(param, MetaFormatoProyecto.listado.values());
		    	}
		    	
		    	if (prop.tipo == TipoDato.FORMATO_BOOLEAN) {
		    		return Editors.createChoiceEditor(param, Constantes.opcionesYesNo());
		    	}
		    	
		    	return null;
		    	
		    	/*
		        if(param.getValue() instanceof String[]) {   
		            return Editors.createChoiceEditor(param, choices);
		         } else if (param.getValue() instanceof Boolean) {
		            return Editors.createCheckEditor(param);
		         } else if (param.getValue() instanceof Integer) {
		            return Editors.createNumericEditor(param);
		         } else {
		            return Editors.createTextEditor(param);
		         }*/
		     }
		});
	}
	
	public 	PropertySheet getTabla() {
		return (PropertySheet) this;
	}
}