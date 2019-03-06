package model.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.metadatos.EstadoProyecto;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import model.metadatos.TipoProyecto;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class Proyecto implements Cargable{
	public int id = 0;
	public String nombre = "";
	
	public Presupuesto presupuestoActual = null;
	public Presupuesto presupuestoMaxVersion = null;
	
	public HashMap<String, ? extends Parametro> listadoParametros = null;
	public static HashMap<Integer, Proyecto> listaProyecto = null;
	public ArrayList<EstadoProyecto> estadosProyecto = null;

	public ArrayList<FaseProyecto> fasesProyecto = null;
	
	public static final int NEUTRO = 0;
	public static final int ANIADIR = 1;
	public static final int MODIFICAR = 2;
	public static final int ELIMINAR = 3;
	
	public int modo = 0;
	
	public boolean apunteContable = false;
	
	public Proyecto clone() {
		Proyecto p = new Proyecto();
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
			if (salida.get("proyId")==null) {
				this.id = 0;
			} else this.id = (Integer) salida.get("proyId");
			this.nombre = (String) salida.get("proyNom");
		} catch (Exception e) {
			
		}
		
		return this;
	}
	
	public static Proyecto getProyectoEstaticoCargaForzada(int idProyecto) {
			listaProyecto = new HashMap<Integer, Proyecto>();
			
			Iterator<Proyecto> itProy = new Proyecto().listadoProyectos().iterator();
			while (itProy.hasNext()) {
				Proyecto p = itProy.next();
				listaProyecto.put(p.id, p);
			}
			
			return listaProyecto.get(idProyecto);
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
				ParametroProyecto pp = p.getValorParametro(MetaParametro.PROYECTO_TIPO_PROYECTO);
				TipoProyecto tp = (TipoProyecto) pp.getValor();
				if (tp.codigo == TipoProyecto.ID_EVOLUTIVO || tp.codigo == TipoProyecto.ID_PROYECTO)
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
				ParametroProyecto pp = p.getValorParametro(MetaParametro.PROYECTO_TIPO_PROYECTO);
				TipoProyecto tp = (TipoProyecto) pp.getValor();
				if (tp.codigo == TipoProyecto.ID_DEMANDA)
					salida.add(p);
				
			} catch (Exception  ex) {
				ex.printStackTrace();
			}
			
		}
		
        return salida;
	}
	
	public ArrayList<Proyecto> getDemandasAsociadas() throws Exception{
		if (this.listadoParametros!=null || this.listadoParametros.size()>0)
			this.cargaProyecto();
		
		ParametroProyecto pp = this.getValorParametro(MetaParametro.PROYECTO_TIPO_PROYECTO);
		TipoProyecto tp = (TipoProyecto) pp.getValor();
		
		if (tp.codigo == TipoProyecto.ID_EVOLUTIVO || tp.codigo == TipoProyecto.ID_PROYECTO) {
			RelProyectoDemanda rpd = new RelProyectoDemanda();
			rpd.proyecto = this;
			rpd.pres = this.presupuestoActual;
			
			ArrayList<RelProyectoDemanda> listado =  rpd.buscaRelacion();
			
			if (listado!=null && listado.size()>0) {
				return listado.get(0).listaDemandas;
			}
		}
		
		return new ArrayList<Proyecto>();
	}
	
	public void cargaProyecto() throws Exception{			
		this.listadoParametros = new HashMap<String, ParametroProyecto> ();
		
		ParametroProyecto pp = new ParametroProyecto();
		this.listadoParametros = pp.dameParametros(this.getClass().getSimpleName(), this.id);
		
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
	
	public ParametroProyecto getValorParametro(String keyParam) {
		if (this.listadoParametros==null) return null;
		else return (ParametroProyecto) this.listadoParametros.get(keyParam);
	}
	
	public void cargaFasesProyecto() {
		FaseProyecto fp = new FaseProyecto();
		this.fasesProyecto = fp.listado();
	}
	
	public float coberturaDemandaFases(Proyecto pDemanda, boolean apunteContable, Sistema s) {
		float porcAcumulado = 0;
		
		Iterator<FaseProyecto> itFP = this.fasesProyecto.iterator();
		while (itFP.hasNext()) {
			FaseProyecto fp = itFP.next();
			porcAcumulado = fp.coberturaDemandaFases(pDemanda, apunteContable, s);
		}
		
		return porcAcumulado;
	} 
	
	public void nuevoProyecto(String idTransaccion) {
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		ParametroBD pBD = new ParametroBD();
		pBD.id = 1;
		pBD.tipo = ConstantesBD.PARAMBD_ID;
		pBD.valorInt = 1;
		listaParms.add(pBD);
		pBD = new ParametroBD();
		pBD.id = 2;
		pBD.tipo = ConstantesBD.PARAMBD_STR;
		pBD.valorStr = this.nombre;
		listaParms.add(pBD);
		
		ConsultaBD consulta = new ConsultaBD();
		
		if (idTransaccion == null)
			consulta.ejecutaSQL("iAltaProy", listaParms, this);
		else 
			consulta.ejecutaSQL("iAltaProy", listaParms, this,idTransaccion);
		
		this.id = ParametroBD.ultimoId;
			
	}
	
	public void actualizaProyecto(String idTransaccion) {
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		ParametroBD pBD = new ParametroBD();
		pBD.id = 1;
		pBD.tipo = ConstantesBD.PARAMBD_INT;
		pBD.valorInt = this.id;
		listaParms.add(pBD);
		pBD = new ParametroBD();
		pBD.id = 2;
		pBD.tipo = ConstantesBD.PARAMBD_STR;
		pBD.valorStr = this.nombre;
		listaParms.add(pBD);
		
		ConsultaBD consulta = new ConsultaBD();		
		consulta.ejecutaSQL("uActualizaProyecto", listaParms, this,idTransaccion);
	}
	
	
	public boolean altaProyecto(Proyecto p, String idTransaccion) {

		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		ParametroBD pBD = new ParametroBD();
		pBD.id = 1;
		pBD.tipo = ConstantesBD.PARAMBD_ID;
		pBD.valorInt = p.id;
		listaParms.add(pBD);
		pBD = new ParametroBD();
		pBD.id = 2;
		pBD.tipo = ConstantesBD.PARAMBD_STR;
		pBD.valorStr = p.nombre;
		listaParms.add(pBD);
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("iAltaProy", listaParms, this, idTransaccion);
		
		p.id = ParametroBD.ultimoId;
		
		return true;
	}
	
	public void bajaProyecto(String idTransaccion) throws Exception {
		if (this.listadoParametros==null) {
			this.cargaProyecto();
		}
		
		ParametroProyecto pp = null;
		
		Iterator<? extends Parametro> itParametros = this.listadoParametros.values().iterator();
		
		while (itParametros.hasNext()) {
			pp = (ParametroProyecto) itParametros.next();
			pp.bajaParametro(idTransaccion);
		}
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));
		ConsultaBD consulta = new ConsultaBD();
		
		if (idTransaccion==null)
			consulta.ejecutaSQL("dDelProyecto", listaParms, this);
		else
			consulta.ejecutaSQL("dDelProyecto", listaParms, this,idTransaccion);
	}
	
	
	
	@Override
	public String toString() {
		return this.id + " - " + this.nombre;
	}

}
