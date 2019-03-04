package ui.interfaces;

import java.util.ArrayList;

import model.beans.Parametro;
import model.beans.ParametroProyecto;
import model.beans.Proyecto;
import ui.Propiedad;

public interface Propiediable {
	public static final String[] SUBCLASES = {Parametro.class.getSimpleName()};
	
	public Propiedad toPropiedad();

	public void setValor(Object o);
	
	public static ArrayList<Object> listaSubValores(String entidad){
		if (Parametro.class.getSimpleName().equals(entidad))
			return null;
		return null;
	}
	
	public static Parametro beanControlador(String entidad){
		if (Parametro.class.getSimpleName().equals(entidad))
			return new Parametro();
		if (Proyecto.class.getSimpleName().equals(entidad))
			return new ParametroProyecto();
		return null;
	}
	
	public static int getIdEntidad(String entidad, Object elemento){
		if (Parametro.class.getSimpleName().equals(entidad))
			return -1;
		if (Proyecto.class.getSimpleName().equals(entidad)) {
			Proyecto p = (Proyecto) elemento;
			return p.id;
		}	
		
		return -1;
	}
}
