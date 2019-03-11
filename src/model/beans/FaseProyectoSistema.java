package model.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class FaseProyectoSistema implements Cargable{
	public int id = 0;
	public int idFase = 0;
	public int idSistema = 0;
	public Sistema s;
	
	public HashMap<String,? extends Parametro> parametrosFaseSistema = new HashMap<String,Parametro> ();
	public ArrayList<FaseProyectoSistemaDemanda> demandasSistema = null;
	
	
	public FaseProyectoSistema clone() {
		FaseProyectoSistema fps = new FaseProyectoSistema();
		fps.id = this.id;
		fps.idFase = this.idFase;
		fps.idSistema = this.idSistema;
		fps.s = this.s;
		
		if (this.demandasSistema!=null) {
			fps.demandasSistema = new ArrayList<FaseProyectoSistemaDemanda>();
			
			Iterator<FaseProyectoSistemaDemanda> itDemandas = this.demandasSistema.iterator();
			while (itDemandas.hasNext()) {
				FaseProyectoSistemaDemanda fpsd = itDemandas.next();
				fps.demandasSistema.add(fpsd.clone());
			}
		}
		
		if (parametrosFaseSistema!=null) {
			HashMap<String,Parametro> mapAux = new HashMap<String,Parametro>();
			Iterator<? extends Parametro> itParFas = this.parametrosFaseSistema.values().iterator();
			while (itParFas.hasNext()) {
				ParametroFases parFase = (ParametroFases) itParFas.next();
				mapAux.put(parFase.codParametro, parFase.clone());
			}
			
			fps.parametrosFaseSistema = mapAux;
		} else {
			Parametro p = new Parametro();
			parametrosFaseSistema = p.dameParametros(this.getClass().getSimpleName(), Parametro.SOLO_METAPARAMETROS);	
		}
		
		return fps;
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		demandasSistema = new ArrayList<FaseProyectoSistemaDemanda>();
		
		try { if (salida.get("fpsId")==null)     { this.id = 0;            } else this.id = (Integer) salida.get("fpsId");                           } catch (Exception e){System.out.println();}
		try { if (salida.get("fpsFase")==null)   { this.idFase = 0;    } else {
			this.idFase = (Integer) salida.get("fpsFase");     
			} 
		} catch (Exception e){System.out.println();}
		try { if (salida.get("fpsSistema")==null)  { this.idSistema = 0; } else {
			this.idSistema = (Integer) salida.get("fpsSistema"); 
			this.s = Sistema.listado.get(this.idSistema);			
		} 
		} catch (Exception e){System.out.println();}
		
		return this;
	}
	
	
	public HashMap<String,FaseProyectoSistema> listado () {		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.idFase));
				
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> fases = consulta.ejecutaSQL("cConsultaSistemasFasesProyecto", listaParms, this);
		
		Iterator<Cargable> itFase = fases.iterator();
		HashMap<String,FaseProyectoSistema> salida = new HashMap<String,FaseProyectoSistema>();
		
		while (itFase.hasNext()) {
			FaseProyectoSistema p = (FaseProyectoSistema) itFase.next();
			
			ParametroFases pf = new ParametroFases();
			p.parametrosFaseSistema = pf.dameParametros(this.getClass().getSimpleName(), p.id);
			
			FaseProyectoSistemaDemanda fpsd = new FaseProyectoSistemaDemanda();
			fpsd.idSistema = p.id;
			p.demandasSistema = fpsd.listado();
			
			salida.put(p.s.codigo,p);
		}
				
		return salida;
	}
	
	public void borraFasesProyectoSistema(String idTransaccion)  throws Exception{
		
		Iterator<FaseProyectoSistemaDemanda> itFps = this.demandasSistema.iterator();
		while (itFps.hasNext()) {
			FaseProyectoSistemaDemanda fps = itFps.next();
			fps.borraFasesProyectoSistemaDemanda(idTransaccion);
			break;
		}
		
		if (this.parametrosFaseSistema!=null) {
			Iterator<? extends Parametro> itpf = this.parametrosFaseSistema.values().iterator();
			while (itpf.hasNext()) {
				Parametro par = itpf.next();
				par.bajaParametro(idTransaccion);
			}
		}
				
		ConsultaBD consulta = new ConsultaBD();
	
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.idFase));
	
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("dBorraSistemasFasesProyecto", listaParms, this, idTransaccion);
	}
	
	public void insertFaseProyectoSistema(String idTransaccion)  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_ID, this.id));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.idFase));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, this.idSistema));
		
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("iInsertaSistemasFasesProyecto", listaParms, this, idTransaccion);
		
		this.id = ParametroBD.ultimoId;
		
		if (this.parametrosFaseSistema!=null) {
			Iterator<? extends Parametro> itpf = this.parametrosFaseSistema.values().iterator();
			while (itpf.hasNext()) {
				Parametro par = itpf.next();
				par.idEntidadAsociada = this.id;
				par.actualizaParametro(idTransaccion);
			}
		}
		
		Iterator<FaseProyectoSistemaDemanda> itFps = this.demandasSistema.iterator();
		while (itFps.hasNext()) {
			FaseProyectoSistemaDemanda fps = itFps.next();
			fps.idSistema = this.id;
			fps.insertFaseProyectoSistemaDemanda(idTransaccion);
			break;
		}
	}
		
	public void updateFasesProyecto(ArrayList<FaseProyectoSistema> listadoFases, String idTransaccion)  throws Exception{

		if (listadoFases!=null && listadoFases.size()>0) {
			FaseProyectoSistema fp = listadoFases.get(0);
			fp.borraFasesProyectoSistema(idTransaccion);
		}
		
		Iterator<FaseProyectoSistema> itFProyecto = listadoFases.iterator();
		while (itFProyecto.hasNext()) {
			FaseProyectoSistema fp = itFProyecto.next();
			fp.insertFaseProyectoSistema(idTransaccion);
		}		
	}
	
	public FaseProyectoSistemaDemanda getDemanda(int idDemanda, boolean apunteContable) {
		Iterator<FaseProyectoSistemaDemanda> itDemandas = this.demandasSistema.iterator();
		while (itDemandas.hasNext()) {
			FaseProyectoSistemaDemanda demanda = itDemandas.next();
			if (demanda.apunteContable == apunteContable && demanda.p.id == idDemanda) 
				return demanda;
		}
		return null;
	}
	
	public float coberturaDemandaFases(Proyecto pDemanda, boolean apunteContable) {
		Iterator<FaseProyectoSistemaDemanda> itF = this.demandasSistema.iterator();
		while (itF.hasNext()) {
			FaseProyectoSistemaDemanda fpsd = itF.next();
			
			if (apunteContable == fpsd.apunteContable && pDemanda.id == fpsd.p.id) {
				ParametroFases pf = (ParametroFases) fpsd.parametrosFaseSistemaDemanda.get(MetaParametro.FASES_COBERTURA_DEMANDA);
				return (Float) pf.getValor();
			}
		}		
		return 0;
	} 
	
	public String toString() {
		return this.s.toString();
	}
	
	
}
