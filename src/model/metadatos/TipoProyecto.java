package model.metadatos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.db.ConsultaBD;

public class TipoProyecto implements Cargable, Loadable {

	public int id =0;
	public String descripcion = "";
	public int codigo = 0;
	
	public static int ID_PROYEVOLS = -1;
	public static int ID_TODO = 0;
	public static int ID_DEMANDA = 1;
	public static int ID_PROYECTO = 2;
	public static int ID_EVOLUTIVO = 3;
	public static int ID_PRO_BONO = 4;
	
	public static HashMap<Integer, TipoProyecto> listado = null;
	
	public ArrayList<EstadoProyecto> estados = null;
	
	public static ArrayList<TipoProyecto> tiposCompleta() {
		ArrayList<TipoProyecto> salida = new ArrayList<TipoProyecto>();
		salida.addAll(TipoProyecto.listado.values());
		
		TipoProyecto tp = new TipoProyecto();
		tp.codigo = TipoProyecto.ID_TODO;
		tp.descripcion = "Todos los elementos";
		tp.id = tp.codigo;
		salida.add(tp);
		
		tp = new TipoProyecto();
		tp.codigo = TipoProyecto.ID_PROYEVOLS;
		tp.descripcion = "Proyectos y evolutivos";
		tp.id = tp.codigo;
		salida.add(tp);
		
		return salida;
	}

	public static ArrayList<TipoProyecto> tiposNoDemanda() {
		ArrayList<TipoProyecto> salida = new ArrayList<TipoProyecto>();
		Iterator<TipoProyecto> itTipoProyectos = listado.values().iterator();
		
		while (itTipoProyectos.hasNext()) {
			TipoProyecto tp = itTipoProyectos.next();
			if (tp.id == TipoProyecto.ID_EVOLUTIVO || tp.id == TipoProyecto.ID_PROYECTO)
				salida.add(tp);
		}
		
		return salida;
	}
	
	public static ArrayList<TipoProyecto> tiposDemanda() {
		ArrayList<TipoProyecto> salida = new ArrayList<TipoProyecto>();
		Iterator<TipoProyecto> itTipoProyectos = listado.values().iterator();
		
		while (itTipoProyectos.hasNext()) {
			TipoProyecto tp = itTipoProyectos.next();
			if (tp.id != TipoProyecto.ID_EVOLUTIVO && tp.id != TipoProyecto.ID_PROYECTO)
				salida.add(tp);
		}
		
		return salida;
	} 
	
	public static ArrayList<Object> objetosNoDemanda() {
		ArrayList<Object> salida = new ArrayList<Object>();
		Iterator<TipoProyecto> itTipoProyectos = listado.values().iterator();
		
		while (itTipoProyectos.hasNext()) {
			TipoProyecto tp = itTipoProyectos.next();
			if (tp.id == TipoProyecto.ID_EVOLUTIVO || tp.id == TipoProyecto.ID_PROYECTO)
				salida.add(tp);
		}
		
		return salida;
	} 
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		this.id = (Integer) salida.get("tipId");
		this.descripcion = (String) salida.get("tipDesc");
		this.codigo = (Integer) salida.get("tipCod");
		
		this.estados = new ArrayList<EstadoProyecto>();
		
		return this;
	}

	@Override
	public void load() {
		listado = new HashMap<Integer, TipoProyecto>();	
		
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> estados = consulta.ejecutaSQL("cConsultaTipos", null, this);
		
		Iterator<Cargable> it = estados.iterator();
		while (it.hasNext()){
			TipoProyecto est = (TipoProyecto) it.next();
			listado.put(est.codigo, est);
		}
	}
	
	@Override
	public String toString() {
		return descripcion;
	}
	
	@Override
	public HashMap<?, ?> getListado() {
		return TipoProyecto.listado;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

}
