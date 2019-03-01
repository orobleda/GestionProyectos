package ui.interfaces;

import model.beans.Parametro;
import ui.Propiedad;

public interface Propiediable {
	public static final String[] SUBCLASES = {Parametro.class.getSimpleName()};
	
	public Propiedad toPropiedad();

	public void setValor(Object o);
}
