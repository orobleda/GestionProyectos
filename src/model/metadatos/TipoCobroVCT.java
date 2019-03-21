package model.metadatos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.db.ConsultaBD;

public class TipoCobroVCT implements Cargable, Loadable {

	public int id =0;
	public double porcentaje = 0;
	public String codigo = "";
	public String nombre = "";
	
	public ArrayList<Double> porcentajes = null;
	public ArrayList<String> nombres = null;
		
	public static HashMap<String, TipoCobroVCT> listado = null;
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		porcentajes = new ArrayList<Double>();
		nombres = new ArrayList<String>();
		
		try {
		 	if (salida.get("creCodigo")==null)  { 
		 		this.codigo = null;
			} else {
		 		this.codigo = (String) salida.get("creCodigo");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("creNombre")==null)  { 
		 		this.nombre = null;
			} else {
		 		this.nombre = (String) salida.get("creNombre");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("creId")==null)  { 
		 		this.id = 0;
			} else {
		 		this.id = (Integer) salida.get("creId");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("crePorcentaje")==null)  { 
		 		this.porcentaje = 0;
			} else {
		 		this.porcentaje = (Double) salida.get("crePorcentaje");
			}
		} catch (Exception ex) {}
				
		return this;
	}

	@Override
	public void load() {
		listado = new HashMap<String, TipoCobroVCT>();	
		
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> estados = consulta.ejecutaSQL("cConsultatipo_cobro_vct", null, this);
		TipoCobroVCT est = null;
		
		Iterator<Cargable> it = estados.iterator();
		while (it.hasNext()){
			TipoCobroVCT estAux = (TipoCobroVCT) it.next();
			
			if (listado.containsKey(estAux.codigo)) {
				est = listado.get(estAux.codigo);
			} else {
				listado.put(estAux.codigo, estAux);
				est = estAux;
			}
			
			est.porcentajes.add(estAux.porcentaje);
			est.nombres.add(estAux.nombre);
		}
	}
	
	@Override
	public String toString() {
		String salida = "";
		
		Iterator<Double> itPorcs = this.porcentajes.iterator();
		while (itPorcs.hasNext()) {
			salida += itPorcs.next();
			
			if (itPorcs.hasNext()) {
				salida += "/";
			}
		}
		return salida;
	}
	
	@Override
	public HashMap<?, ?> getListado() {
		return TipoCobroVCT.listado;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

}
