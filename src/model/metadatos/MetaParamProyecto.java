package model.metadatos;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.db.ConsultaBD;
import ui.GestionProyectos.Tables.ParamProyecto;

public class MetaParamProyecto implements Cargable, Loadable {
	
	public static final int PERFIL_ECONOMICO = 5;
	public static final int TIPO_PROYECTO = 2;
	public static final int FX_INICIO = 6;
	public static final int FX_FIN = 1;

	public int id =0;
	public String descripcion = "";
	public String tipo = "";
	public int tipoDato = 0;
	public String valor = "";
	public Date valorDate = null;
	
	public static HashMap<Integer, MetaParamProyecto> listado = null;
	
	public MetaParamProyecto() {
	}
	
	public MetaParamProyecto(ParamProyecto pproyecto) {
		try {
			this.id = new Integer(pproyecto.get(ParamProyecto.ID));
			this.descripcion = pproyecto.get(ParamProyecto.DESCRIPCION);
			this.tipo = pproyecto.get(ParamProyecto.TIPO);
			this.tipoDato = pproyecto.tipoDato;
		} catch (Exception e){}
	}
	
	@Override
	public Object clone() {
		MetaParamProyecto mpp = new MetaParamProyecto();
		
		mpp.id = this.id;
		mpp.descripcion = this.descripcion;
		mpp.tipo = this.tipo;
		mpp.tipoDato = this.tipoDato;
		mpp.valor = this.valor;
		
		return mpp;
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		this.id = (Integer) salida.get("mpProyId");
		this.descripcion = (String) salida.get("mpDesc");
		this.tipo = (String) salida.get("mpTipo");
		this.tipoDato = (Integer) salida.get("mpTipoDato");
	
		return this;
	}

	@Override
	public void load() {
		listado = new HashMap<Integer, MetaParamProyecto>();	
			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> estados = consulta.ejecutaSQL("cMetaParmProy", null, this);
		
		Iterator<Cargable> it = estados.iterator();
		while (it.hasNext()){
			MetaParamProyecto est = (MetaParamProyecto) it.next();
			listado.put(est.id, est);
		}
	}
	
	@Override
	public String toString() {
		return descripcion;
	}
	
	@Override
	public HashMap<?, ?> getListado() {
		return MetaParamProyecto.listado;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

}
