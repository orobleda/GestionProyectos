package model.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class ApunteContable extends Proyecto {
	
	public ApunteContable() {
		this.apunteContable = true;
	}
	
	public ApunteContable clone() {
		ApunteContable p = new ApunteContable();
		p.id = this.id;
		p.modo = this.modo;
		p.nombre = this.nombre;
		if (this.presupuestoActual!=null)
			p.presupuestoActual = this.presupuestoActual.clone();;
		p.apunteContable = this.apunteContable;
		
		return p;
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
			if (salida.get("apConId")==null) {
				this.id = 0;
			} else this.id = (Integer) salida.get("apConId");
		} catch (Exception e) {
			
		}
		
		return this;
	}
	
	public ApunteContable buscaApunteContable() {			
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.id));
		
		ArrayList<Cargable> proyectos = consulta.ejecutaSQL("cListaApuntesContables", listaParms, this);
		
		Iterator<Cargable> itProyecto = proyectos.iterator();
		ArrayList<Proyecto> salida = new ArrayList<Proyecto>();
		
		while (itProyecto.hasNext()) {
			ApunteContable ap = (ApunteContable) itProyecto.next();
			return ap;
		}
		
        return null;
	}
	
	public ArrayList<Proyecto> listadoApunteContable() {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> proyectos = consulta.ejecutaSQL("cListaApuntesContables", null, this);
		
		Iterator<Cargable> itProyecto = proyectos.iterator();
		ArrayList<Proyecto> salida = new ArrayList<Proyecto>();
		
		while (itProyecto.hasNext()) {
			ApunteContable ap = (ApunteContable) itProyecto.next();
			salida.add(ap);
		}
		
        return salida;
	}
	
	public int altaApunteContable(Proyecto p, String idTransaccion) {
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_ID,this.id));
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("iInsertaApunteContable", listaParms, this, idTransaccion);
		
		return ParametroBD.ultimoId;	
	}
	
	public void bajaApunteContable(String idTransaccion) {			
		ParametroProyecto pp = new ParametroProyecto();
		pp.bajaProyecto(this);
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("dBorraApunteContable", listaParms, this,idTransaccion);
	}
	
	public String toString() {
		return "Apunte Contable (" + this.id + ")"; 
	}
}
