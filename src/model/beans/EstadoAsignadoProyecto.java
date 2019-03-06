package model.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.metadatos.EstadoProyecto;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class EstadoAsignadoProyecto implements Cargable {
	
	public static final int PERFIL_ECONOMICO = 5;
	public static final int FX_INICIO = 6;
	public static final int FX_FIN = 1;

	public int id =0;
	public EstadoProyecto estado = null;
	public Proyecto p = null;
	public int bloque = 0;
		
	public EstadoAsignadoProyecto() {
	}
	
	@Override
	public Object clone() {
		EstadoAsignadoProyecto mpp = new EstadoAsignadoProyecto();
		
		mpp.id = this.id;
		mpp.estado = this.estado;
		mpp.p = this.p;
		mpp.bloque = this.bloque;
		
		return mpp;
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		this.id = (Integer) salida.get("estPId");
		
		try {
			int idProy = (Integer) salida.get("estPProy");
			this.p = Proyecto.listaProyecto.get(idProy);
		} catch (Exception e) {
			
		}
		
		try {
			int idEst = (Integer) salida.get("estPEst");
			this.estado = EstadoProyecto.listado.get(idEst);
		} catch (Exception e) {
			
		}
		
		this.bloque = (Integer) salida.get("estPBloque");
	
		return this;
	}

	public static ArrayList<EstadoProyecto> dameEstados(Proyecto p) {
		ArrayList<EstadoProyecto> listado = new ArrayList<EstadoProyecto>();	
			
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, p.id));
		
		ArrayList<Cargable> estados = consulta.ejecutaSQL("cConsultaEstadoProyecto", null, new EstadoAsignadoProyecto());
		
		Iterator<Cargable> it = estados.iterator();
		while (it.hasNext()){
			EstadoAsignadoProyecto est = (EstadoAsignadoProyecto) it.next();
			listado.add(est.estado);
		}
		
		return listado;
	}
	
	@Override
	public String toString() {
		return this.p.nombre + " " + this.estado.descripcion;
	}

}
