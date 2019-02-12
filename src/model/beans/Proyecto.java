package model.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.metadatos.EstadoProyecto;
import model.metadatos.MetaParamProyecto;
import model.metadatos.TipoProyecto;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class Proyecto implements Cargable{
	public int id = 0;
	public String nombre = "";
	
	public ArrayList<MetaParamProyecto> listadoParametros = null;
	public static HashMap<Integer, Proyecto> listaProyecto = null;
	public ArrayList<EstadoProyecto> estadosProyecto = null; 

	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
			if (salida.get("proyId")==null) {
				this.id = 0;
			} else this.id = (Integer) salida.get("proyId");
			this.nombre = (String) salida.get("proyNom");
		} catch (Exception e) {
			
		}
		
		return this;
	}
	
	public static Proyecto getProyectoEstatico(int idProyecto) {
		if (listaProyecto==null) {
			listaProyecto = new HashMap<Integer, Proyecto>();
			
			Iterator<Proyecto> itProy = new Proyecto().listadoProyectos().iterator();
			while (itProy.hasNext()) {
				Proyecto p = itProy.next();
				listaProyecto.put(p.id, p);
			}
			
			return listaProyecto.get(idProyecto);
		} else {
			return listaProyecto.get(idProyecto);
		}
	}
	
	public ArrayList<Proyecto> listadoProyectos() {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> proyectos = consulta.ejecutaSQL("cListaProyectos", null, this);
		
		Iterator<Cargable> itProyecto = proyectos.iterator();
		ArrayList<Proyecto> salida = new ArrayList<Proyecto>();
		
		while (itProyecto.hasNext()) {
			Proyecto p = (Proyecto) itProyecto.next();
			salida.add(p);
		}
		
        return salida;
	}
	
	
	public ArrayList<Proyecto> listadoProyectosGGP() {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> proyectos = consulta.ejecutaSQL("cListaProyectos", null, this);
		
		Iterator<Cargable> itProyecto = proyectos.iterator();
		ArrayList<Proyecto> salida = new ArrayList<Proyecto>();
		
		while (itProyecto.hasNext()) {
			Proyecto p = (Proyecto) itProyecto.next();
			try {
				p.cargaProyecto();
				int tpProyecto = new Integer((String) p.getValorParametro(MetaParamProyecto.TIPO_PROYECTO));
				if (tpProyecto == TipoProyecto.ID_EVOLUTIVO || tpProyecto == TipoProyecto.ID_PROYECTO)
					salida.add(p);
				
			} catch (Exception  ex) {
				ex.printStackTrace();
			}
			
		}
		
        return salida;
	}
	
	public ArrayList<Proyecto> listadoDemandas() {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> proyectos = consulta.ejecutaSQL("cListaProyectos", null, this);
		
		Iterator<Cargable> itProyecto = proyectos.iterator();
		ArrayList<Proyecto> salida = new ArrayList<Proyecto>();
		
		while (itProyecto.hasNext()) {
			Proyecto p = (Proyecto) itProyecto.next();
			try {
				p.cargaProyecto();
				int tpProyecto = new Integer((String) p.getValorParametro(MetaParamProyecto.TIPO_PROYECTO));
				if (tpProyecto == TipoProyecto.ID_DEMANDA)
					salida.add(p);
				
			} catch (Exception  ex) {
				ex.printStackTrace();
			}
			
		}
		
        return salida;
	}
	
	public void cargaProyecto() throws Exception{			
		ParametroProyecto pp = new ParametroProyecto();
		pp.idProyecto = this.id;
		
		ArrayList<ParametroProyecto> listado = pp.listadoParamProy();
		Iterator<ParametroProyecto> itParam = listado.iterator();
		
		this.listadoParametros = new ArrayList<MetaParamProyecto> ();
		
		while (itParam.hasNext()) {
			ParametroProyecto ppaux = (ParametroProyecto) itParam.next();
			MetaParamProyecto mppaux = (MetaParamProyecto) ppaux.mpProy.clone();
			Object valor = ppaux.getValor();
			if (valor!=null)
				if ("Date".equals(valor.getClass().getSimpleName())){
					mppaux.valorDate = (Date) valor;
					mppaux.valor =  valor.toString();
				} else 
					mppaux.valor = valor.toString();
			else
				mppaux.valor = "";
			this.listadoParametros.add(mppaux);
		}
		
		if (this.listadoParametros.size()!=MetaParamProyecto.listado.size()){
			Iterator<MetaParamProyecto> itmtp = MetaParamProyecto.listado.values().iterator();
			while (itmtp.hasNext()){
				MetaParamProyecto mtp =  itmtp.next();
				
				boolean encontrado = false;
				for (int i=0; i<this.listadoParametros.size();i++){
					if (mtp.id == this.listadoParametros.get(i).id) {
						encontrado = true;
						break;
					}
				}
				
				if (!encontrado) {
					mtp = (MetaParamProyecto) mtp.clone();
					mtp.valor = "";
					this.listadoParametros.add(mtp);	
				}
				
			}
		}
		
		this.estadosProyecto = EstadoAsignadoProyecto.dameEstados(this); 		
	}
	
	public boolean isCerrado() {
		if (this.estadosProyecto==null) {
			this.estadosProyecto = EstadoAsignadoProyecto.dameEstados(this); 
		}
		
		Iterator<EstadoProyecto> itEstado = this.estadosProyecto.iterator();
		
		while (itEstado.hasNext()) {
			EstadoProyecto estP = itEstado.next();
			
			if (estP.estFinal == EstadoProyecto.FINAL ) {
				return true;
			}
		}
		
		return false;
	}
	
	public int maxIdProyecto() {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> proyectos = consulta.ejecutaSQL("cMaxIdProyecto", null, this);
		
		Proyecto p = (Proyecto) proyectos.get(0);
        return p.id+1;
	}
	
	public Object getValorParametro(int idParm) {
		if (this.listadoParametros!=null) {
			Iterator<MetaParamProyecto> itParm = this.listadoParametros.iterator();
			
			while (itParm.hasNext()) {
				MetaParamProyecto pp = itParm.next();
				if (pp.id==idParm) {
					if (pp.valorDate!=null) return pp.valorDate;
					else return pp.valor;
				}
			}
		}
		return null;
	}
	
	public boolean altaProyecto(Proyecto p) {
		boolean modificacion = true;
		int maxId = this.maxIdProyecto();
		
		if (maxId == p.id) {
			ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
			ParametroBD pBD = new ParametroBD();
			pBD.id = 1;
			pBD.tipo = ConstantesBD.PARAMBD_INT;
			pBD.valorInt = p.id;
			listaParms.add(pBD);
			pBD = new ParametroBD();
			pBD.id = 2;
			pBD.tipo = ConstantesBD.PARAMBD_STR;
			pBD.valorStr = p.nombre;
			listaParms.add(pBD);
			
			ConsultaBD consulta = new ConsultaBD();
			consulta.ejecutaSQL("iAltaProy", listaParms, this);
			
			modificacion = false;
		}
		
		return modificacion;
	}
	
	public void bajaProyecto() {			
		ParametroProyecto pp = new ParametroProyecto();
		pp.bajaProyecto(this);
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("dDelProyecto", listaParms, this);
	}
	
	
	
	@Override
	public String toString() {
		return this.id + " - " + this.nombre;
	}

}
