package model.metadatos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.db.ConsultaBD;

public class TransicionEstados implements Cargable, Loadable {

	public int id =0;
	public int estadoInicial = 0;
	public int estadoFinal = 0;
	public int tipoProy = 0;
	
	public static HashMap<Integer, TransicionEstados> listado = null;
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		this.id = (Integer) salida.get("transId");
		this.estadoInicial = (Integer) salida.get("transEstIni");
		this.estadoFinal = (Integer) salida.get("transEstFin");
		this.tipoProy = (Integer) salida.get("transTipProy");
		
		return this;
	}

	@Override
	public void load() {
		if (EstadoProyecto.listado==null || EstadoProyecto.listado.size()==0)
			new EstadoProyecto().load();
		if (TipoProyecto.listado==null || TipoProyecto.listado.size()==0)
			new TipoProyecto().load();
		
		listado = new HashMap<Integer, TransicionEstados>();	
		
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> estados = consulta.ejecutaSQL("cConsultaTransEstados", null, this);
		
		Iterator<Cargable> it = estados.iterator();
		while (it.hasNext()){
			TransicionEstados est = (TransicionEstados) it.next();
			
			if (est.estadoInicial==0) {
				EstadoProyecto estPrFin = EstadoProyecto.listado.get(est.estadoFinal);
				estPrFin.inicial = true;
			} else {
				EstadoProyecto estPrIni = EstadoProyecto.listado.get(est.estadoInicial);
				TipoProyecto tpP = TipoProyecto.listado.get(est.tipoProy);
				tpP.estados.add(estPrIni);
			}
			
			listado.put(est.id, est);
		}
	}
	
	@Override
	public HashMap<?, ?> getListado() {
		return TransicionEstados.listado;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

}
