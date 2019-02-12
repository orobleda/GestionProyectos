package model.metadatos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.beans.Coste;
import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.db.ConsultaBD;

public class MetaConcepto implements Cargable, Loadable, Comparable<MetaConcepto> {
	
	public static int ID_TOTAL = -1;
	public static int ID_NINGUNO = -2;
	public static int ID_PORC = -3;
	public static int ID_DIFF = -4;
	
	public static String COD_TOTAL = "TOTAL";
	
	public static int TREI = 1;
	public static int SATAD = 2;
	public static int CC = 3;
	
	public static int GESTION_HORAS = 1;
	public static int GESTION_CERTIFICACION = 2;

	public int id =0;
	public String descripcion = "";
	public String codigo = "";
	public Coste costeAuxiliar = null;
	public int tipoGestionEconomica = 0;
		
	public static HashMap<Integer, MetaConcepto> listado = null;
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		this.id = (Integer) salida.get("metcId");
		this.descripcion = (String) salida.get("metcDesc");
		this.codigo = (String) salida.get("metcCod");
		this.tipoGestionEconomica = (Integer) salida.get("metGesEco");
		
		return this;
	}
	
	public static MetaConcepto porId(int id) {
		Iterator<MetaConcepto> itMC = listado.values().iterator();
		
		while (itMC.hasNext()) {
			MetaConcepto mc = itMC.next();
			if (mc.id == id) {
				return mc;
			}
		}
				
		return null;
	}

	@Override
	public void load() {
		listado = new HashMap<Integer, MetaConcepto>();	
			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> estados = consulta.ejecutaSQL("cConsultaMetaConceptos", null, this);
		
		Iterator<Cargable> it = estados.iterator();
		while (it.hasNext()){
			MetaConcepto est = (MetaConcepto) it.next();
			listado.put(est.id, est);
		}
	}
	
	public static MetaConcepto getTotal() {
		MetaConcepto mc = new MetaConcepto();
		mc.codigo = COD_TOTAL;
		mc.descripcion = COD_TOTAL;
		mc.id = MetaConcepto.ID_TOTAL;
		return mc;
	} 
	
	public ArrayList<MetaConcepto> aPorcentaje() {
		ArrayList<MetaConcepto> listadoSalida = new ArrayList<MetaConcepto>();
		
		listadoSalida.addAll(MetaConcepto.listado.values());
		
		MetaConcepto mc = new MetaConcepto();
		mc.codigo = COD_TOTAL;
		mc.descripcion = COD_TOTAL;
		mc.id = MetaConcepto.ID_TOTAL;
				
		listadoSalida.add(mc);
		
		mc = new MetaConcepto();
		mc.codigo = "";
		mc.descripcion = "";
		mc.id = MetaConcepto.ID_NINGUNO;
		
		listadoSalida.add(0, mc);
		
		return listadoSalida;
	}
	
	@Override
	public String toString() {
		return descripcion;
	}
	
	@Override
	public HashMap<?, ?> getListado() {
		return MetaConcepto.listado;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public int compareTo(MetaConcepto o) {
		return new Integer(this.id).compareTo((o).id);
	}

}
