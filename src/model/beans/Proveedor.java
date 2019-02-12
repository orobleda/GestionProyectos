package model.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.db.ConsultaBD;

public class Proveedor implements Cargable, Loadable {

	public int id =0;
	public String descripcion = "";
	public String nomCorto = "";
		
	public static HashMap<String, Proveedor> listado = null;
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		this.id = (Integer) salida.get("provId");
		this.descripcion = (String) salida.get("provDesc");
		this.nomCorto = (String) salida.get("provNomCorto");
		
		return this;
	}

	@Override
	public void load() {
		listado = new HashMap<String, Proveedor>();	
			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> proveedores = consulta.ejecutaSQL("cConsultaProveedores", null, this);
		
		Iterator<Cargable> it = proveedores.iterator();
		while (it.hasNext()){
			Proveedor est = (Proveedor) it.next();
			listado.put(est.nomCorto, est);
		}
	}
	
	public Proveedor getProveedor(int id) {
		
		Iterator<Proveedor> it = listado.values().iterator();
		while (it.hasNext()){
			Proveedor est = (Proveedor) it.next();
			if (est.id == id)
				return est;	
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return descripcion;
	}

	@Override
	public HashMap<?, ?> getListado() {
		return Proveedor.listado;
	}

	@Override
	public int getId() {
		return this.id;
	}

}
