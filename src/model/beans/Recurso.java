package model.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaParametro;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class Recurso implements Cargable{
	public int id = 0;
	public String nombre = "";
	
	public static HashMap<Integer, Recurso> listaRecursos = null;
	
	public HashMap<String,? extends Parametro> listadoParametros = null;

	@Override
	public boolean equals(Object o) {
		try {
			return this.id == ((Recurso) o).id;
		} catch (Exception e) {
			return false;
		}
	}
	
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
	
	public static HashMap<Integer, Recurso> listadoRecursosEstatico(boolean recargar) {
		
		Recurso.listaRecursos = new HashMap<Integer, Recurso>();
		
			ConsultaBD consulta = new ConsultaBD();
			ArrayList<Cargable> proyectos = consulta.ejecutaSQL("cListaRecursos", null, new Recurso());
			
			Iterator<Cargable> itProyecto = proyectos.iterator();
			
			while (itProyecto.hasNext()) {
				Recurso p = (Recurso) itProyecto.next();
				Recurso.listaRecursos.put(new Integer(p.id),p);
			}
		
        return Recurso.listaRecursos;
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
				
		this.listadoParametros = pp.dameParametros(this.getClass().getSimpleName(), this.id);
	}
	
	public int maxIdRecurso() {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> proyectos = consulta.ejecutaSQL("cMaxIdRecurso", null, this);
		
		Recurso p = (Recurso) proyectos.get(0);
        return p.id+1;
	}
	
	public Recurso getRecursoPorCodigo(String codUser) throws Exception{
		HashMap<Integer, Recurso> mRecursos = Recurso.listadoRecursosEstatico();
		Iterator<Recurso> itRec = mRecursos.values().iterator();
		while (itRec.hasNext()) {
			Recurso rec = itRec.next();
			ParametroRecurso pr = (ParametroRecurso) rec.getValorParametro(MetaParametro.RECURSO_COD_USUARIO);
			
			if (pr!=null) {
				if (codUser.equals((String) pr.getValor())) {
					return rec;
				}
			}
		}
		
		return null;
	}
	
	public Object getValorParametro(String codParm) throws Exception{
		if (this.listadoParametros==null) {
			this.cargaRecurso();
		}
		
		return this.listadoParametros.get(codParm);
	}
	
	public boolean guardaRecurso(String idTransaccion) {
		boolean modificacion = true;
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		String cadConexion = "";
		
		if (this.id == -1) { // Recurso Nuevo
			ParametroBD pBD = new ParametroBD(1,ConstantesBD.PARAMBD_ID,this.id);
			listaParms.add(pBD);
			cadConexion = "iAltaRec";
			modificacion = false;
		} else {
			ParametroBD pBD = new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.id);
			listaParms.add(pBD);
			cadConexion = "iUpdateRec";
		}
		
		ParametroBD pBD = new ParametroBD(2,ConstantesBD.PARAMBD_STR,this.nombre);
		listaParms.add(pBD);
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL(cadConexion, listaParms, this, idTransaccion);
		
		if (!modificacion) {
			this.id = ParametroBD.ultimoId;
		}
			
		return modificacion;
	}
	
	public void bajaRecurso(String idTransaccion) throws Exception{		
		cargaRecurso(); 
		
		Iterator<? extends Parametro> itParm = this.listadoParametros.values().iterator();
		while (itParm.hasNext()) {
			ParametroRecurso pr = (ParametroRecurso) itParm.next();
			pr.bajaParametro(idTransaccion);
		}
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("dDelRecurso", listaParms, this, idTransaccion);
	}
	
	@Override
	public String toString() {
		return this.id + " - " + this.nombre;
	}

}
