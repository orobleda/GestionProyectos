package model.metadatos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.db.ConsultaBD;

public class MetaGerencia implements Cargable, Loadable, Comparable<MetaGerencia> {
	
	public static int GGP = 1;
	public static int GDT = 2;
	
	public int id =0;
	public String grupo ="";
	public String descripcion = "";
	public String codigo = "";
		
	public static HashMap<Integer, MetaGerencia> listado = null;
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		this.id = (Integer) salida.get("metbId");
		this.grupo = (String) salida.get("metbGrupo");
		this.descripcion = (String) salida.get("metbDesc");
		this.codigo = (String) salida.get("metbCod");
		
		return this;
	}
	
	public static MetaGerencia porId(int id) {
		return listado.get(id);
	}

	@Override
	public void load() {
		listado = new HashMap<Integer, MetaGerencia>();	
			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> estados = consulta.ejecutaSQL("cConsultaMetaBloque", null, this);
		
		Iterator<Cargable> it = estados.iterator();
		while (it.hasNext()){
			MetaGerencia est = (MetaGerencia) it.next();
			listado.put(est.id, est);
		}
	}
	
	@Override
	public String toString() {
		return codigo;
	}
	
	@Override
	public HashMap<?, ?> getListado() {
		return MetaGerencia.listado;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public int compareTo(MetaGerencia o) {
		return new Integer(this.id).compareTo((o).id);
	}

}
