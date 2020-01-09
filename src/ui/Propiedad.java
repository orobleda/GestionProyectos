package ui;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import org.controlsfx.control.PropertySheet;

import javafx.beans.value.ObservableValue;
import model.constantes.Constantes;
import model.metadatos.TipoDato;
import ui.interfaces.Propiediable;

public class Propiedad implements PropertySheet.Item {
	
	public String categoria;
	public String nombre;
	public Object valor;
	public int tipo;
	public String descripcion;
	public Propiediable oOrigenDato;
	public boolean editable = true;
	
	
	public Propiedad(String categoria, String descripcion, String nombre, Object valor, int tipo, Propiediable oOrigenDato) {
		this.categoria = categoria;
		this.nombre = nombre;
		this.valor = valor;
		this.tipo = tipo;
		this.descripcion = descripcion;
		this.oOrigenDato = oOrigenDato;
	}
	
	@Override
	public boolean isEditable() {
		return editable;
	}
	
	@Override
	public String getCategory() {
		return this.categoria;
	}

	@Override
	public String getDescription() {
		return this.descripcion;
	}

	@Override
	public String getName() {
		return this.nombre;
	}

	@Override
	public Optional<ObservableValue<? extends Object>> getObservableValue() {
		return Optional.empty();
	}

	@Override
	public Class<?> getType() {
		return TipoDato.getClassTipo(this.tipo);
	}

	@Override
	public Object getValue() {
		if (this.tipo == TipoDato.FORMATO_FECHA) {
			if (this.valor == null) return  null;//Instant.ofEpochMilli((Constantes.fechaActual()).getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			return Instant.ofEpochMilli(((Date)this.valor).getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		}
		return this.valor;
	}

	@Override
	public void setValue(Object arg0) {
		this.oOrigenDato.setValor(arg0);		
	}

}
