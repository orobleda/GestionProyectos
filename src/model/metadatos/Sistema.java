package model.metadatos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.beans.Concepto;
import model.beans.Estimacion;
import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.db.ConsultaBD;

public class Sistema implements Cargable, Loadable {

	public int id =0;
	public String descripcion = "";
	public String codigo = "";
	public int responsable = 0;
	
	public static String TOTAL = "TOTAL";
	public static String TODOS = "TODOS";
	public static int ID_TODOS = 4001;
	
	public static HashMap<Integer, Sistema> listado = null;
		
	public HashMap<String, Concepto> listaConceptos = null;
	public HashMap<Integer, Estimacion> listaEstimaciones = null;
	
	public Sistema clone() {
		Sistema retorno = new Sistema();
		retorno.id = this.id;
		retorno.descripcion = this.descripcion;
		retorno.codigo = this.codigo;
		retorno.responsable = this.responsable;
		
		if (listaConceptos!=null) {
			retorno.listaConceptos = new HashMap<String, Concepto>();
			
			for (int i=0;i<this.listaConceptos.keySet().size();i++) {
				retorno.listaConceptos.put((String) this.listaConceptos.keySet().toArray()[i], this.listaConceptos.get(this.listaConceptos.keySet().toArray()[i]).clone());
			}
		}
		
		return retorno;
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		this.id = (Integer) salida.get("areaId");
		this.descripcion = (String) salida.get("areaDesc");
		this.codigo = (String) salida.get("areaCod");
		if (salida.get("areaResp") !=null)
			this.responsable = (Integer) salida.get("areaResp");
		
		return this;
	}

	@Override
	public void load() {
		listado = new HashMap<Integer, Sistema>();	
			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> estados = consulta.ejecutaSQL("cConsultaAreas", null, this);
		
		Iterator<Cargable> it = estados.iterator();
		while (it.hasNext()){
			Sistema est = (Sistema) it.next();
			listado.put(est.id, est);
		}
	}
	
	public static Sistema getInstanceTotal() {
		Sistema t = new Sistema();
		t.id = 4000;
		t.codigo = Sistema.TOTAL;
		t.descripcion = Sistema.TOTAL;
		t.responsable = 0;
		return t;
	}
	
	public static Sistema getInstanceTodos() {
		Sistema t = new Sistema();
		t.id = Sistema.ID_TODOS;
		t.codigo = Sistema.TODOS;
		t.descripcion = Sistema.TODOS;
		t.responsable = 0;
		return t;
	}
	
	public static Sistema get(String codigo) {
		Iterator<Sistema> itSistema = Sistema.listado.values().iterator();
		
		while (itSistema.hasNext()) {
			Sistema s = itSistema.next();
			if (codigo.equals(s.codigo)) return s;
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return descripcion;
	}
	
	@Override
	public HashMap<?, ?> getListado() {
		return Sistema.listado;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

}
