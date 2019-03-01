package ui;

import model.beans.Parametro;

public interface Propiediable {
	public static final String[] SUBCLASES = {Parametro.class.getSimpleName()};
	
	public Propiedad toPropiedad();

	public void setValor(Object o);
}
