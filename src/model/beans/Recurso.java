package model.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.metadatos.MetaParamRecurso;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class Recurso implements Cargable{
	public int id = 0;
	public String nombre = "";
	
	public static HashMap<Integer, Recurso> listaRecursos = null;
	
	public ArrayList<MetaParamRecurso> listadoParametros = null;

	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
			if (salida.get("recId")==null) {
				this.id = 0;
			} else this.id = (Integer) salida.get("recId");
			this.nombre = (String) salida.get("recNom");
		} catch (Exception e) {
			
		}
		
		return this;
	}
	
	public ArrayList<Recurso> listadoRecursos() {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> proyectos = consulta.ejecutaSQL("cListaRecursos", null, this);
		
		Iterator<Cargable> itProyecto = proyectos.iterator();
		ArrayList<Recurso> salida = new ArrayList<Recurso>();
		
		while (itProyecto.hasNext()) {
			Recurso p = (Recurso) itProyecto.next();
			salida.add(p);
		}
		
        return salida;
	}
	
	public static HashMap<Integer, Recurso> listadoRecursosEstatico() {
		
		if (Recurso.listaRecursos == null){
			Recurso.listaRecursos = new HashMap<Integer, Recurso>();
		
			ConsultaBD consulta = new ConsultaBD();
			ArrayList<Cargable> proyectos = consulta.ejecutaSQL("cListaRecursos", null, new Recurso());
			
			Iterator<Cargable> itProyecto = proyectos.iterator();
			
			while (itProyecto.hasNext()) {
				Recurso p = (Recurso) itProyecto.next();
				Recurso.listaRecursos.put(new Integer(p.id),p);
			}
		}
		
        return Recurso.listaRecursos;
	}
	
	public void cargaRecurso() throws Exception{			
		ParametroRecurso pp = new ParametroRecurso();
		pp.idRecurso = this.id;
		
		ArrayList<ParametroRecurso> listado = pp.listadoParamRecurso();
		Iterator<ParametroRecurso> itParam = listado.iterator();
		
		this.listadoParametros = new ArrayList<MetaParamRecurso> ();
		
		while (itParam.hasNext()) {
			ParametroRecurso ppaux = (ParametroRecurso) itParam.next();
			MetaParamRecurso mppaux = (MetaParamRecurso) ppaux.mpRecurso.clone();
			Object valor = ppaux.getValor();
			if (valor!=null)
				mppaux.valor = valor.toString();
			else
				mppaux.valor = "";
			this.listadoParametros.add(mppaux);
		}
		
		if (this.listadoParametros.size()!=MetaParamRecurso.listado.size()){
			Iterator<MetaParamRecurso> itmtp = MetaParamRecurso.listado.values().iterator();
			while (itmtp.hasNext()){
				MetaParamRecurso mtp =  itmtp.next();
				
				boolean encontrado = false;
				for (int i=0; i<this.listadoParametros.size();i++){
					if (mtp.id == this.listadoParametros.get(i).id) {
						encontrado = true;
						break;
					}
				}
				
				if (!encontrado) {
					mtp = (MetaParamRecurso) mtp.clone();
					mtp.valor = "";
					this.listadoParametros.add(mtp);	
				}
				
			}
		}
		
		
	}
	
	public int maxIdRecurso() {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> proyectos = consulta.ejecutaSQL("cMaxIdRecurso", null, this);
		
		Recurso p = (Recurso) proyectos.get(0);
        return p.id+1;
	}
	
	public Object getValorParametro(int idParm) throws Exception{
		if (this.listadoParametros==null) {
			this.cargaRecurso();
		}
		Iterator<MetaParamRecurso> itParm = this.listadoParametros.iterator();
		
		while (itParm.hasNext()) {
			MetaParamRecurso pp = itParm.next();
			if (pp.id==idParm) {
				return pp.valor;
			}
		}
		
		return null;
	}
	
	public boolean altaRecurso(Recurso p) {
		boolean modificacion = true;
		int maxId = this.maxIdRecurso();
		
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
			consulta.ejecutaSQL("iAltaRec", listaParms, this);
			
			modificacion = false;
		}
		
		return modificacion;
	}
	
	public void bajaRecurso() {			
		ParametroRecurso pp = new ParametroRecurso();
		pp.bajaRecurso(this);
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("dDelRecurso", listaParms, this);
	}
	
	@Override
	public String toString() {
		return this.id + " - " + this.nombre;
	}

}
