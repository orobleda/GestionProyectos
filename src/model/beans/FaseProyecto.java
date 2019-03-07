package model.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.metadatos.Sistema;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class FaseProyecto implements Cargable{
	public int id = 0;
	public String nombre = "";
	public int idProyecto = 0;
	public Proyecto p;
	
	public HashMap<String,? extends Parametro> parametrosFase = null;
	public HashMap<String, FaseProyectoSistema> fasesProyecto = null;
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		fasesProyecto = new HashMap<String, FaseProyectoSistema>();
		
		try { if (salida.get("fpId")==null)     { this.id = 0;            } else this.id = (Integer) salida.get("fpId");                           } catch (Exception e){System.out.println();}
		try { if (salida.get("fpNombre")==null)   { this.nombre = "";    } else {
			this.nombre = (String) salida.get("fpNombre");     
			} 
		} catch (Exception e){System.out.println();}
		try { if (salida.get("fpProyecto")==null)  { this.idProyecto = 0; } else {
			this.idProyecto = (Integer) salida.get("fpProyecto"); 
			this.p = Proyecto.getProyectoEstatico(this.idProyecto);			
		} 
		} catch (Exception e){System.out.println();}
		
		return this;
	}
	
	
	public ArrayList<FaseProyecto> listado () {		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.idProyecto));
				
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> fases = consulta.ejecutaSQL("cConsultaFasesProyecto", listaParms, this);
		
		Iterator<Cargable> itFase = fases.iterator();
		ArrayList<FaseProyecto> salida = new ArrayList<FaseProyecto>();
		
		while (itFase.hasNext()) {
			FaseProyecto p = (FaseProyecto) itFase.next();
			
			ParametroFases pf = new ParametroFases();
			p.parametrosFase = pf.dameParametros(this.getClass().getSimpleName(), p.id);
			
			FaseProyectoSistema fps = new FaseProyectoSistema();
			fps.idFase = p.id;
			p.fasesProyecto = fps.listado();
			
			salida.add(p);
		}
				
		return salida;
	}
	
	public void borraFasesProyecto(String idTransaccion)  throws Exception{
		
		Iterator<FaseProyectoSistema> itFps = this.fasesProyecto.values().iterator();
		while (itFps.hasNext()) {
			FaseProyectoSistema fps = itFps.next();
			fps.borraFasesProyectoSistema(idTransaccion);
			break;
		}
		
		if (this.parametrosFase!=null) {
			Iterator<? extends Parametro> itpf = this.parametrosFase.values().iterator();
			while (itpf.hasNext()) {
				Parametro par = itpf.next();
				par.bajaParametro(idTransaccion);
			}
		}
				
		ConsultaBD consulta = new ConsultaBD();
	
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.idProyecto));
	
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("dBorraFasesProyecto", listaParms, this, idTransaccion);
	}
	
	public void insertFaseProyecto(String idTransaccion)  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_ID, this.id));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_STR, this.nombre));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, this.idProyecto));
		
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("iInsertaFasesProyecto", listaParms, this, idTransaccion);
		
		this.id = ParametroBD.ultimoId;
		
		if (this.parametrosFase!=null) {
			Iterator<? extends Parametro> itpf = this.parametrosFase.values().iterator();
			while (itpf.hasNext()) {
				Parametro par = itpf.next();
				par.idEntidadAsociada = this.id;
				par.actualizaParametro(idTransaccion);
			}
		}
		
		Iterator<FaseProyectoSistema> itFps = this.fasesProyecto.values().iterator();
		while (itFps.hasNext()) {
			FaseProyectoSistema fps = itFps.next();
			fps.idFase = this.id;
			fps.insertFaseProyectoSistema(idTransaccion);
			break;
		}
	}
		
	public void updateFasesProyecto(ArrayList<FaseProyecto> listadoFases, String idTransaccion)  throws Exception{

		if (listadoFases!=null && listadoFases.size()>0) {
			FaseProyecto fp = listadoFases.get(0);
			fp.borraFasesProyecto(idTransaccion);
		}
		
		Iterator<FaseProyecto> itFProyecto = listadoFases.iterator();
		while (itFProyecto.hasNext()) {
			FaseProyecto fp = itFProyecto.next();
			fp.insertFaseProyecto(idTransaccion);
		}		
	}
	
	public float coberturaDemandaFases(Proyecto pDemanda, boolean apunteContable, Sistema s) {
		FaseProyectoSistema fp = this.fasesProyecto.get(s.codigo);
		return fp.coberturaDemandaFases(pDemanda, apunteContable);		
	} 
	
	public String toString() {
		return "Fase: " + this.nombre;
	}
	
	
}
