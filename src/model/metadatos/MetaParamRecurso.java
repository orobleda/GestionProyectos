package model.metadatos;

import java.util.HashMap;

import model.interfaces.Cargable;

public class MetaParamRecurso implements Cargable {
	
	public int id =0;
	public String descripcion = "";
	public String tipo = "";
	public int tipoDato = 0;
	public String valor = "";
	
	public static final int IDRecurso = 3;
	public static final int INTERNO = 4;
	public static final int COD_PARM_GESTOR = 1;
	
	public static HashMap<Integer, MetaParamRecurso> listado = null;
	
	public MetaParamRecurso() {
	}
	
	@Override
	public Object clone() {
		MetaParamRecurso mpp = new MetaParamRecurso();
		
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
		
		this.id = (Integer) salida.get("mpRecId");
		this.descripcion = (String) salida.get("mpRecDesc");
		this.tipo = (String) salida.get("mpRecTipo");
		this.tipoDato = (Integer) salida.get("mpRecTipoDato");
	
		return this;
	}

	@Override
	public String toString() {
		return descripcion;
	}
	
}
