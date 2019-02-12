package model.metadatos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.db.ConsultaBD;

public class EstadoProyecto implements Cargable, Loadable {
	
	public static final int FINAL = 1;

	public int id =0;
	public String descripcion = "";
	public int valor = 0;
	public int estFinal = 0;	
	
	public ArrayList<EstadoProyecto> siguientes = null;
	boolean inicial = false;
	
	public static HashMap<Integer, EstadoProyecto> listado = null;
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		this.id = (Integer) salida.get("estId");
		this.descripcion = (String) salida.get("estDesc");
		this.valor = (Integer) salida.get("estCod");
		this.estFinal = (Integer) salida.get("estFinal");
		
		this.siguientes = new ArrayList<EstadoProyecto>();
		inicial = false;
		
		return this;
	}

	@Override
	public void load() {
		listado = new HashMap<Integer, EstadoProyecto>();	
			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> estados = consulta.ejecutaSQL("cConsultaEstados", null, this);
		
		Iterator<Cargable> it = estados.iterator();
		while (it.hasNext()){
			EstadoProyecto est = (EstadoProyecto) it.next();
			listado.put(est.valor, est);
		}
	}
	
	@Override
	public String toString() {
		return descripcion;
	}

	@Override
	public HashMap<?, ?> getListado() {
		return EstadoProyecto.listado;
	}

	@Override
	public int getId() {
		return this.id;
	}

}
