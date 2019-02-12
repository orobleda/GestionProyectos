package model.metadatos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.db.ConsultaBD;

public class MetaFormatoProyecto implements Cargable, Loadable {

	public int id =0;
	public String descripcion = "";
	public String codigo = "";
	
	public static HashMap<Integer, MetaFormatoProyecto> listado = null;
	
	public HashMap<Integer, MetaConcepto> conceptos = null;
	
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		if (salida.get("forId")==null) this.id = 0; else this.id = (Integer) salida.get("forId");
		if (salida.get("forDesc")==null) this.descripcion = ""; else this.descripcion = (String) salida.get("forDesc");
		if (salida.get("forCod")==null) this.codigo = ""; else this.codigo = (String) salida.get("forCod");
		
		if (salida.get("relcfId")!=null){
			MetaFormatoProyecto mfp = MetaFormatoProyecto.listado.get((Integer) salida.get("relcfFormato"));
			if (mfp.conceptos ==null) mfp.conceptos = new HashMap<Integer, MetaConcepto>();
			MetaConcepto mc = MetaConcepto.listado.get((Integer) salida.get("relcfConcepto"));
			mfp.conceptos.put(mc.id, mc);
		}
		
		return this;
	}

	@Override
	public void load() {
		listado = new HashMap<Integer, MetaFormatoProyecto>();	
			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> estados = consulta.ejecutaSQL("cConsultaFormatoProyecto", null, this);
		
		Iterator<Cargable> it = estados.iterator();
		while (it.hasNext()){
			MetaFormatoProyecto est = (MetaFormatoProyecto) it.next();
			listado.put(est.id, est);
		}
		
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("cRelConceptoFormato", null, this);
	}
	
	@Override
	public String toString() {
		return descripcion;
	}
	
	@Override
	public HashMap<?, ?> getListado() {
		return MetaFormatoProyecto.listado;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

}
