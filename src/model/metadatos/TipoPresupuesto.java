package model.metadatos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.db.ConsultaBD;

public class TipoPresupuesto implements Cargable, Loadable  {

	public int id =0;
	public String descripcion = "";
	public String codigo = "";
	
	public static final int ESTIMACION = 1;
	public static final int VCT = 2;
	
	public static HashMap<Integer, TipoPresupuesto> listado = null;
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		this.id = (Integer) salida.get("tpPId");
		this.descripcion = (String) salida.get("tpPDesc");
		this.codigo = (String) salida.get("tpPCod");
		
		return this;
	}

	@Override
	public void load() {
		listado = new HashMap<Integer, TipoPresupuesto>();	
			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> estados = consulta.ejecutaSQL("cConsultaTipoPresupuesto", null, this);
		
		Iterator<Cargable> it = estados.iterator();
		while (it.hasNext()){
			TipoPresupuesto est = (TipoPresupuesto) it.next();
			listado.put(est.id, est);
		}
	}
	
	@Override
	public String toString() {
		return descripcion;
	}
	
	@Override
	public HashMap<?, ?> getListado() {
		return TipoPresupuesto.listado;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

}
