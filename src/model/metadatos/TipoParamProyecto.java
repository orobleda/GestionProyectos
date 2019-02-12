package model.metadatos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.db.ConsultaBD;

public class TipoParamProyecto implements Cargable, Loadable {

	public int id =0;
	public String descripcion = "";
	public int selector = 0;
	
	private static final int SELECTOR_FORMATO_PROYECTO = 7;
	private static final int SELECTOR_TIPO_PROYECTO = 8;
	
	public static HashMap<Integer, TipoParamProyecto> listado = null;
	
	public TipoParamProyecto() {
	}
	
	public Object dameSelector (){
		if (this.id == SELECTOR_FORMATO_PROYECTO) {
			return new MetaFormatoProyecto();
		}
		
		if (this.id == SELECTOR_TIPO_PROYECTO) {
			return new TipoProyecto();
		}
		
		return null;
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		this.id = (Integer) salida.get("tipPPId");
		this.descripcion = (String) salida.get("tipPPDesc");
		if (salida.get("tipPPSelector")!=null)
			this.selector = (Integer) salida.get("tipPPSelector");
	
		return this;
	}

	@Override
	public void load() {
		listado = new HashMap<Integer, TipoParamProyecto>();	
			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> estados = consulta.ejecutaSQL("cConsultaTipoParmProy", null, this);
		
		Iterator<Cargable> it = estados.iterator();
		while (it.hasNext()){
			TipoParamProyecto est = (TipoParamProyecto) it.next();
			listado.put(est.id, est);
		}
	}
	
	@Override
	public String toString() {
		return descripcion;
	}
	
	@Override
	public HashMap<?, ?> getListado() {
		return TipoParamProyecto.listado;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

}
