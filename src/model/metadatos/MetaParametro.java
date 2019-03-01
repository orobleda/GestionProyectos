package model.metadatos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.Constantes;
import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.db.ConsultaBD;

public class MetaParametro implements Cargable, Loadable {

	public int id =0;
	public String entidad = "";
	public String codParametro = "";
	public String nomParametro = "";
	public String categoria = "";
	public boolean obligatorio = false;
	public int tipoDato = 0;
	
	public static HashMap<Integer, MetaParametro> listado = null;
	
	public MetaParametro() {
	}
	
	@Override
	public Object clone() {
		MetaParametro mp = new MetaParametro();
		
		mp.id = this.id;
		mp.entidad = this.entidad;
		mp.codParametro = this.codParametro;
		mp.categoria = this.categoria;
		mp.obligatorio = this.obligatorio;
		mp.tipoDato = this.tipoDato;
		mp.nomParametro = this.nomParametro;
		
		return mp;
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		this.id = (Integer) salida.get("tipPId");
		this.entidad = (String) salida.get("tipPEntidad");
		this.codParametro = (String) salida.get("tipPCodParm");
		this.categoria = (String) salida.get("tipPCategoria");
		this.obligatorio = (Constantes.NUM_TRUE == (Integer) salida.get("tipPObligatorio"))?true:false;
		this.tipoDato = (Integer) salida.get("tipPTipoDato");
		this.nomParametro = (String) salida.get("tipPNomParm");
	
		return this;
	}

	@Override
	public void load() {
		listado = new HashMap<Integer, MetaParametro>();	
			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> estados = consulta.ejecutaSQL("cConsultaTipoParametro", null, this);
		
		Iterator<Cargable> it = estados.iterator();
		while (it.hasNext()){
			MetaParametro est = (MetaParametro) it.next();
			listado.put(est.id, est);
		}
	}
	
	@Override
	public String toString() {
		return this.nomParametro;
	}
	
	@Override
	public HashMap<?, ?> getListado() {
		return MetaParametro.listado;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

}
