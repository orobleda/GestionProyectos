package model.metadatos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class TipoEnumerado implements Cargable, Loadable  {

	public String codigo;
	public int id_agrupacion;
	public String valor;
	public String nombre_agrupacion;
	public int id;
	public boolean persistir;
	
	public static int TIPO_PRESUPUESTO_EST = 1;
	public static int TIPO_PRESUPUESTO_VCT = 2;
	
	public static HashMap<Integer, HashMap<Integer,TipoEnumerado>> listado = null;
	public static  HashMap<Integer,TipoEnumerado> listadoIds = null;
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
		 	if (salida.get("metCodigo")==null)  { 
		 		this.codigo = null;
			} else {
		 		this.codigo = (String) salida.get("metCodigo");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("metPersistir")==null)  { 
		 		this.persistir = Constantes.toNumBoolean(0);;
			} else {
		 		this.persistir = Constantes.toNumBoolean((int) salida.get("metPersistir"));
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("metId_agrupacion")==null)  { 
		 		this.id_agrupacion = 0;
			} else {
		 		this.id_agrupacion = (Integer) salida.get("metId_agrupacion");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("metValor")==null)  { 
		 		this.valor = null;
			} else {
		 		this.valor = (String) salida.get("metValor");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("metNombre_agrupacion")==null)  { 
		 		this.nombre_agrupacion = null;
			} else {
		 		this.nombre_agrupacion = (String) salida.get("metNombre_agrupacion");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("metId")==null)  { 
		 		this.id = 0;
			} else {
		 		this.id = (Integer) salida.get("metId");
			}
		} catch (Exception ex) {}
		
		return this;
	}

	@Override
	public void load() {
		listado = new  HashMap<Integer, HashMap<Integer,TipoEnumerado>>();	
			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> estados = consulta.ejecutaSQL("cConsultaMeta_tipo_enumerado", null, this);
		HashMap<Integer,TipoEnumerado> listadoAux= null;
		listadoIds = new HashMap<Integer,TipoEnumerado> ();
		
		Iterator<Cargable> it = estados.iterator();
		while (it.hasNext()){
			TipoEnumerado est = (TipoEnumerado) it.next();
			
			if (listado.containsKey(est.id_agrupacion)) {
				listadoAux = listado.get(est.id_agrupacion);
			} else {
				listadoAux = new HashMap<Integer,TipoEnumerado>();
				listado.put(est.id_agrupacion, listadoAux);
			}
			
			listadoAux.put(est.id, est);
			listadoIds.put(est.id, est);
		}
	}
	
	public static HashMap<Integer,TipoEnumerado> getValores(int idAgrupacion) {
		return listado.get(idAgrupacion);
	}
	
	public void actualizaValores() {
		try {
			String idTransaccion = ConsultaBD.getTicket();
			
			ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
			listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id_agrupacion));
			ConsultaBD consulta = new ConsultaBD();
			consulta.ejecutaSQL("dBorraMeta_tipo_enumerado", listaParms, this,idTransaccion);
			
			Iterator<TipoEnumerado> itTips = this.listado.get(this.id_agrupacion).values().iterator();
			while (itTips.hasNext()) {
				listaParms = new ArrayList<ParametroBD>();
				listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_STR, this.codigo));
				listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id_agrupacion));
				listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, Constantes.toNumBoolean(this.persistir)));
				listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_STR, this.valor));
				listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_STR, this.nombre_agrupacion));
				listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_ID, this.id));
				
				consulta.ejecutaSQL("iInsertaMeta_tipo_enumerado", listaParms, this,idTransaccion);
			}
			
			ConsultaBD.ejecutaTicket(idTransaccion);
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public String toString() {
		return this.valor;
	}
	
	@Override
	public HashMap<?, ?> getListado() {
		return TipoEnumerado.listado;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

}
