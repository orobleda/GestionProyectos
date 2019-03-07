package ui.interfaces;

import java.util.ArrayList;

import model.beans.FaseProyecto;
import model.beans.FaseProyectoSistema;
import model.beans.FaseProyectoSistemaDemanda;
import model.beans.Parametro;
import model.beans.ParametroFases;
import model.beans.ParametroProyecto;
import model.beans.Proyecto;
import ui.Propiedad;

public interface Propiediable {
	public static final String[] SUBCLASES = {Parametro.class.getSimpleName()};
	
	public Propiedad toPropiedad();

	public void setValor(Object o);
	
	public String getCodigo();
	
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
		if (FaseProyectoSistemaDemanda.class.getSimpleName().equals(entidad))
			return new ParametroFases();
		if (FaseProyectoSistema.class.getSimpleName().equals(entidad))
			return new ParametroFases();
		if (FaseProyecto.class.getSimpleName().equals(entidad))
			return new ParametroFases();
		return null;
	}
	
	public static int getIdEntidad(String entidad, Object elemento){
		if (Parametro.class.getSimpleName().equals(entidad))
			return -1;
		if (Proyecto.class.getSimpleName().equals(entidad)) {
			Proyecto p = (Proyecto) elemento;
			return p.id;
		}
		if (FaseProyectoSistemaDemanda.class.getSimpleName().equals(entidad)) {
			FaseProyectoSistemaDemanda p = (FaseProyectoSistemaDemanda) elemento;
			return p.id;
		}
		if (FaseProyectoSistema.class.getSimpleName().equals(entidad)) {
			FaseProyectoSistema p = (FaseProyectoSistema) elemento;
			return p.id;
		}
		if (FaseProyecto.class.getSimpleName().equals(entidad)) {
			FaseProyecto p = (FaseProyecto) elemento;
			return p.id;
		}		
		return -1;
	}
}
