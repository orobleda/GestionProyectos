package ui.interfaces;

import java.util.ArrayList;

import model.beans.Certificacion;
import model.beans.CertificacionFase;
import model.beans.CertificacionFaseParcial;
import model.beans.FaseProyecto;
import model.beans.FaseProyectoSistema;
import model.beans.FaseProyectoSistemaDemanda;
import model.beans.Parametro;
import model.beans.ParametroCertificacion;
import model.beans.ParametroFases;
import model.beans.ParametroProyecto;
import model.beans.ParametroRecurso;
import model.beans.Proveedor;
import model.beans.Proyecto;
import model.beans.Recurso;
import model.metadatos.Sistema;
import model.metadatos.TipoDato;
import ui.Propiedad;

public interface Propiediable {
	public static final String[] SUBCLASES = {Parametro.class.getSimpleName(),Proveedor.class.getSimpleName(),Sistema.class.getSimpleName()};
	
	public Propiedad toPropiedad();

	public void setValor(Object o);
	
	public String getCodigo();
	
	public static ArrayList<Object> listaSubValores(String entidad){
		if (Parametro.class.getSimpleName().equals(entidad))
			return null;
		if (Recurso.class.getSimpleName().equals(entidad))
				return TipoDato.toListaObjetos(Recurso.listaRecursos.values());
		if (Proveedor.class.getSimpleName().equals(entidad))
			return TipoDato.toListaObjetos(Proveedor.listado.values());
		if (Sistema.class.getSimpleName().equals(entidad))
			return TipoDato.toListaObjetos(Sistema.listado.values());
		return null;
	}
	
	public static Parametro beanControlador(String entidad){
		if (Parametro.class.getSimpleName().equals(entidad))
			return new Parametro();
		if (Proveedor.class.getSimpleName().equals(entidad))
			return new Parametro();
		if (Sistema.class.getSimpleName().equals(entidad))
			return new Parametro();
		if (Proyecto.class.getSimpleName().equals(entidad))
			return new ParametroProyecto();
		if (FaseProyectoSistemaDemanda.class.getSimpleName().equals(entidad))
			return new ParametroFases();
		if (FaseProyectoSistema.class.getSimpleName().equals(entidad))
			return new ParametroFases();
		if (FaseProyecto.class.getSimpleName().equals(entidad))
			return new ParametroFases();
		if (Recurso.class.getSimpleName().equals(entidad))
			return new ParametroRecurso();
		if (Certificacion.class.getSimpleName().equals(entidad))
			return new ParametroCertificacion();
		if (CertificacionFase.class.getSimpleName().equals(entidad))
			return new ParametroCertificacion();	
		if (CertificacionFaseParcial.class.getSimpleName().equals(entidad))
			return new ParametroCertificacion();		
		return null;
	}
	
	public static int getIdEntidad(String entidad, Object elemento){
		if (Parametro.class.getSimpleName().equals(entidad))
			return -1;
		if (Sistema.class.getSimpleName().equals(entidad)) {
			Sistema s = (Sistema) elemento;
			return s.id;
		}
		if (Proveedor.class.getSimpleName().equals(entidad)) {
			Proveedor s = (Proveedor) elemento;
			return s.id;
		}		
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
		if (Recurso.class.getSimpleName().equals(entidad)) {
			Recurso r = (Recurso) elemento;
			return r.id;
		}
		return -1;
	}
}
