package model.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class FaseProyectoSistemaDemanda implements Cargable{
	public int id = 0;
	public int idSistema = 0;
	public int idDemanda = 0;
	public boolean apunteContable = false;
	public Proyecto p;
	
	public HashMap<String,? extends Parametro> parametrosFaseSistemaDemanda = null;
	
	public FaseProyectoSistemaDemanda clone() {
		FaseProyectoSistemaDemanda fpsd = new FaseProyectoSistemaDemanda();
		fpsd.apunteContable = this.apunteContable;
		fpsd.id = this.id;
		fpsd.idDemanda = this.id;
		fpsd.idSistema = this.idSistema;
		fpsd.p = this.p;
		
		if (parametrosFaseSistemaDemanda!=null) {
			HashMap<String,Parametro> mapAux = new HashMap<String,Parametro>();
			Iterator<? extends Parametro> itParFas = this.parametrosFaseSistemaDemanda.values().iterator();
			while (itParFas.hasNext()) {
				ParametroFases parFase = (ParametroFases) itParFas.next();
				mapAux.put(parFase.codParametro, parFase.clone());
			}
			
			fpsd.parametrosFaseSistemaDemanda = mapAux;
		}
		
		return fpsd;
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
				
		try { if (salida.get("fpsdId")==null)     { this.id = 0;            } else this.id = (Integer) salida.get("fpsdId");                           } catch (Exception e){System.out.println();}
		try { if (salida.get("fpsdFaseSistema")==null)   { this.idSistema = 0;    } else {
			this.idSistema = (Integer) salida.get("fpsdFaseSistema");     
			} 
		} catch (Exception e){System.out.println();}
		try { if (salida.get("fpsdApContable")==null)  { this.idDemanda = 0; } else {
			this.apunteContable = Constantes.toNumBoolean((Integer) salida.get("fpsdApContable")); 
						
		} 
		} catch (Exception e){System.out.println();}
		try { if (salida.get("fpsdDemanda")==null)  { this.idDemanda = 0; } else {
			this.idDemanda = (Integer) salida.get("fpsdDemanda"); 
			if (!this.apunteContable)
				this.p = Proyecto.getProyectoEstatico(this.idDemanda);
			else {
				ApunteContable ap = new ApunteContable();
				ap.id = this.idDemanda;
				this.p = ap.buscaApunteContable();
			}  
		} 
		} catch (Exception e){System.out.println();}
		
		return this;
	}
	
	public ParametroFases getParametro(String codParametro) {
		if (this.parametrosFaseSistemaDemanda == null) {
			ParametroFases pf = new ParametroFases();
			
			int id = Parametro.SOLO_METAPARAMETROS;
			
			if (this.id>0) {
				id = this.id;
			}
			
			this.parametrosFaseSistemaDemanda = pf.dameParametros(FaseProyectoSistemaDemanda.class.getSimpleName(), id);
		}
		
		return (ParametroFases) this.parametrosFaseSistemaDemanda.get(codParametro);
	}
	
	
	public ArrayList<FaseProyectoSistemaDemanda> listado () {		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.idSistema));
				
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> fases = consulta.ejecutaSQL("cConsultaDemandaSistemasFasesProyecto", listaParms, this);
		
		Iterator<Cargable> itFase = fases.iterator();
		ArrayList<FaseProyectoSistemaDemanda> salida = new ArrayList<FaseProyectoSistemaDemanda>();
		
		while (itFase.hasNext()) {
			FaseProyectoSistemaDemanda p = (FaseProyectoSistemaDemanda) itFase.next();
			
			ParametroFases pf = new ParametroFases();
			p.parametrosFaseSistemaDemanda = pf.dameParametros(this.getClass().getSimpleName(), p.id);
			
			salida.add(p);
		}
				
		return salida;
	}
	
	public void borraFasesProyectoSistemaDemanda(String idTransaccion)  throws Exception{
		
		if (this.parametrosFaseSistemaDemanda!=null) {
			Iterator<? extends Parametro> itpf = this.parametrosFaseSistemaDemanda.values().iterator();
			while (itpf.hasNext()) {
				Parametro par = itpf.next();
				par.bajaParametro(idTransaccion);
			}
		}
				
		ConsultaBD consulta = new ConsultaBD();
	
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.idSistema));
	
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("dBorraDemandaSistemasFasesProyecto", listaParms, this, idTransaccion);
	}
	
	public void insertFaseProyectoSistemaDemanda(String idTransaccion)  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_ID, this.id));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.idSistema));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, this.p.id));
		listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_INT, Constantes.toNumBoolean(this.p.apunteContable)));
		
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("iInsertaDemandaSistemasFasesProyecto", listaParms, this, idTransaccion);
		
		this.id = ParametroBD.ultimoId;
		
		if (this.parametrosFaseSistemaDemanda!=null) {
			Iterator<? extends Parametro> itpf = this.parametrosFaseSistemaDemanda.values().iterator();
			while (itpf.hasNext()) {
				Parametro par = itpf.next();
				par.idEntidadAsociada = this.id;
				par.actualizaParametro(idTransaccion,false);
			}
		}
		
	}
		
	public void updateFasesProyecto(ArrayList<FaseProyectoSistemaDemanda> listadoFases, String idTransaccion)  throws Exception{

		if (listadoFases!=null && listadoFases.size()>0) {
			FaseProyectoSistemaDemanda fp = listadoFases.get(0);
			fp.borraFasesProyectoSistemaDemanda(idTransaccion);
		}
		
		Iterator<FaseProyectoSistemaDemanda> itFProyecto = listadoFases.iterator();
		while (itFProyecto.hasNext()) {
			FaseProyectoSistemaDemanda fp = itFProyecto.next();
			fp.insertFaseProyectoSistemaDemanda(idTransaccion);
		}		
	}
	
	public String toString() {
		return this.p.toString();
	}
	
	
}
