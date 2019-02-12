package model.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.metadatos.Sistema;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class RelRecursoSistema implements Cargable{
	
	public static ArrayList<RelRecursoSistema> listado = null; 
	 
	public int id = 0;
	
	public Recurso recurso = null;
	public Sistema sistema = null;

	public RelRecursoSistema() {
    }
	
	public ArrayList<Sistema> buscaRelaciones(Recurso r) {
		ArrayList<Sistema> listadoSalida = new ArrayList<Sistema>();
		ArrayList<RelRecursoSistema> listado = this.buscaRelaciones();
		
		Iterator<RelRecursoSistema> itRels = listado.iterator();
		while (itRels.hasNext()) {
			RelRecursoSistema rrsAux = itRels.next();
			if (rrsAux.recurso.id == r.id) {
				listadoSalida.add(rrsAux.sistema);
			}
		}
		
		return listadoSalida;
	}
	
	public ArrayList<RelRecursoSistema> buscaRelaciones() { 
		if (listado==null) {
			ConsultaBD consulta = new ConsultaBD();
			
			List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
			
			ArrayList<Cargable> relRecursoTarifas = consulta.ejecutaSQL("cRelRecSistema", listaParms, this);
			
			Iterator<Cargable> itCargable = relRecursoTarifas.iterator();
			ArrayList<RelRecursoSistema> salida = new ArrayList<RelRecursoSistema> ();
			
			while (itCargable.hasNext()) {
				RelRecursoSistema rec = (RelRecursoSistema) itCargable.next();
				salida.add(rec);
			}
			
			RelRecursoSistema.listado = salida;
			
			return salida;
		} else return listado;
    }
	
	public void insertaRelacion(String idTransaccion) throws Exception{
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_ID,this.id));
		listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,this.recurso.id));
		listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_INT,this.sistema.id));
				
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("iInsertaRelRecursoSistema", listaParms, this, idTransaccion);
	}
	
	public void deleteRelacion(int idElemento) throws Exception{
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,idElemento));
				
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("dRelRecursoSistema", listaParms, this);
	}

	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
			if (salida.get("relrsId")==null) this.id = 0; else this.id = (Integer) salida.get("relrsId");
			
			if (salida.get("relrsIdRec")==null) this.recurso = null; else try { 
					int id = (Integer) salida.get("relrsIdRec");
					this.recurso = Recurso.listadoRecursosEstatico().get(id);
			} catch (Exception e) {}
			
			if (salida.get("relrsIdSis")==null) this.sistema = null; else try { 
				int id = (Integer) salida.get("relrsIdSis");
				this.sistema = Sistema.listado.get(id);
			} catch (Exception e) {}
			
		} catch (Exception e) {
			
		} 
		
		return this;
	}
	
}
