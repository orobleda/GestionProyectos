package model.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class RelProyectoDemanda implements Cargable{
	
	public int id = 0;
	public Proyecto proyecto = null;
	public Presupuesto pres = null;
	public Proyecto demandaLinea = null;
	public ArrayList<Proyecto> listaDemandas = null;
	public int idPresupuesto = 0;
	public int idVsPresupuesto = 0;
	public int idVsPresupuestoDem = 0;

	public RelProyectoDemanda() {
		listaDemandas = new ArrayList<Proyecto>();
	}

	public ArrayList<RelProyectoDemanda> buscaRelacion() { 	
		if (this.proyecto == null || this.pres==null) return null;
		
		HashMap<Integer,RelProyectoDemanda> listaProyectos = new HashMap<Integer,RelProyectoDemanda>();
		
		ConsultaBD consulta = new ConsultaBD();
		
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.proyecto.id));
		listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,this.pres.id));
		
		ArrayList<Cargable> relRecursoTarifas = consulta.ejecutaSQL("cRelProyDem", listaParms, this);
		
		Iterator<Cargable> itCargable = relRecursoTarifas.iterator();
		ArrayList<RelProyectoDemanda> salida = new ArrayList<RelProyectoDemanda> ();
		
		while (itCargable.hasNext()) {
			RelProyectoDemanda rec = (RelProyectoDemanda) itCargable.next();
			RelProyectoDemanda recAux = null;
			
			try {
				if (listaProyectos.containsKey(rec.proyecto.id)) {
					recAux = listaProyectos.get(rec.proyecto.id);
				} else {
					listaProyectos.put(new Integer(rec.proyecto.id), rec);
					recAux = rec;
				}
				
				recAux.listaDemandas.add(rec.demandaLinea);
				
			} catch (Exception e) {
				
			}
			
		}
		
		salida.addAll(listaProyectos.values());
		
		return salida;
    }
	
	public void deleteRelacion(String idTransaccion) throws Exception{
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.proyecto.id));
				
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("dRelRelProyectoDemanda", listaParms, this, idTransaccion);
	}
	
	private void insertaRelacion(String idTransaccion) throws Exception{
		List<ParametroBD> listaParms = null;
		
		Iterator<Proyecto> itDemandas = this.listaDemandas.iterator();
		
		ConsultaBD consulta = new ConsultaBD();
		
		while (itDemandas.hasNext()) {
			Proyecto p = (Proyecto) itDemandas.next();
			listaParms = new ArrayList<ParametroBD>();
			
			listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_ID,1));
			listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,this.proyecto.id));
			listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_INT,this.pres.id));
			listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_INT,p.id));
			listaParms.add(new ParametroBD(5,ConstantesBD.PARAMBD_INT,p.presupuestoActual.id));
			listaParms.add(new ParametroBD(6,ConstantesBD.PARAMBD_INT,p.presupuestoActual.version));
			
			consulta.ejecutaSQL("iInsertaRelProyectoDemanda", listaParms, this, idTransaccion);
		}
	}
	
	public void actualizaRelaciones (String idTransaccion) throws Exception{
		if (this.proyecto == null) return;
		
		deleteRelacion(idTransaccion);
		insertaRelacion(idTransaccion);
	}
	


	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
				if (salida.get("rpdId")==null) this.id = 0; else this.id = (Integer) salida.get("reltrId");
				
				if (salida.get("rpdIdProy")==null) this.proyecto = null; else { 
					int id = (Integer) salida.get("rpdIdProy");	
					this.proyecto = Proyecto.getProyectoEstatico(id);
				} 
				
				if (salida.get("rpdIdDem")==null) this.demandaLinea = null; else { 
					int id = (Integer) salida.get("rpdIdDem");	
					this.demandaLinea = Proyecto.getProyectoEstatico(id);
				}
				
				if (salida.get("rpdIdPres")==null) this.idPresupuesto = -1; else { 
					int id = (Integer) salida.get("rpdIdPres");	
					this.idPresupuesto = id;
				} 
				
				if (salida.get("rpdIdvsPres")==null) this.idVsPresupuesto = -1; else { 
					int id = (Integer) salida.get("rpdIdvsPres");	
					this.idVsPresupuesto = id;
				}
				
				if (salida.get("rpdIdvsPresDem")==null) this.idVsPresupuestoDem = -1; else { 
					int id = (Integer) salida.get("rpdIdvsPresDem");	
					this.idVsPresupuestoDem = id;
				}
		} catch (Exception e) {
			
		} 
		
		return this;
	}
	
	public String toString() {
		return this.proyecto.nombre.toString() + " " + this.listaDemandas;
	}

}
