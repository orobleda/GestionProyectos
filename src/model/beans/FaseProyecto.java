package model.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class FaseProyecto implements Cargable, Comparable<FaseProyecto>{
	public int id = 0;
	public String nombre = "";
	public int idProyecto = 0;
	public Proyecto p;
	
	public HashMap<String,? extends Parametro> parametrosFase = null;
	public HashMap<String, FaseProyectoSistema> fasesProyecto = null;
	
	
	public FaseProyecto clone() {
		FaseProyecto fp = new FaseProyecto();
		fp.id = this.id;
		fp.idProyecto = this.idProyecto;
		fp.nombre = this.nombre;
		fp.p = this.p;
		
		if (this.fasesProyecto!=null ) {
			fp.fasesProyecto = new HashMap<String,FaseProyectoSistema>();
			Iterator<FaseProyectoSistema> itFps = this.fasesProyecto.values().iterator();
			while (itFps.hasNext()) {
				FaseProyectoSistema fps = itFps.next();
				fp.fasesProyecto.put(fps.s.codigo, fps.clone());
				
			}
		}
		
		if (parametrosFase!=null) {
			HashMap<String,Parametro> mapAux = new HashMap<String,Parametro>();
			Iterator<? extends Parametro> itParFas = this.parametrosFase.values().iterator();
			while (itParFas.hasNext()) {
				ParametroFases parFase = (ParametroFases) itParFas.next();
				mapAux.put(parFase.codParametro, parFase.clone());
			}
			
			fp.parametrosFase = mapAux;
		}
		
		return fp;
	}
	
	public int compareTo(FaseProyecto o) {
		ParametroFases pf = new ParametroFases();
		if (this.parametrosFase==null || this.parametrosFase.size()==0) {
			this.parametrosFase = pf.dameParametros(this.getClass().getSimpleName(), this.id);
		}
		if (o.parametrosFase==null || o.parametrosFase.size()==0) {
			o.parametrosFase = pf.dameParametros(this.getClass().getSimpleName(), o.id);
		}
		
		Date fxImpThis = this.getFechaImplantacion();
		Date fxImpO = o.getFechaImplantacion();

		return fxImpThis.compareTo(fxImpO);
	}
	
	public Date getFechaImplantacion () {
		Parametro par = this.parametrosFase.get(MetaParametro.FASE_PROYECTO_FX_IMPLANTACION);
		Date fxImpThis = null;
		
		if (par.getValor()==null) 
			if (p==null || p.getValorParametro(MetaParametro.PROYECTO_FX_FIN)==null) fxImpThis = Constantes.fechaActual();
			else 														             fxImpThis = (Date) (p.getValorParametro(MetaParametro.PROYECTO_FX_FIN)).getValor();		
		else 
			fxImpThis = (Date) par.getValor();
		
		return fxImpThis;
	}
	
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
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.id));
	
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("dBorraFasesProyecto", listaParms, this, idTransaccion);
	}
	
	public void insertFaseProyecto(String idTransaccion)  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		consulta = new ConsultaBD();
		
		if (this.id == -1) {
			listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_ID, this.id));
			listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_STR, this.nombre));
			listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, this.idProyecto));
			
			consulta.ejecutaSQL("iInsertaFasesProyecto", listaParms, this, idTransaccion);
			
			this.id = ParametroBD.ultimoId;
		} else {
			listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));
			listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_STR, this.nombre));
			listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, this.idProyecto));
			listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_INT, this.id));
			
			consulta.ejecutaSQL("iUpdateFasesProyecto", listaParms, this, idTransaccion);
						
			Iterator<FaseProyectoSistema> itFps = this.fasesProyecto.values().iterator();
			while (itFps.hasNext()) {
				FaseProyectoSistema fps = itFps.next();
				fps.borraFasesProyectoSistema(idTransaccion);
			}
		}
		
		if (this.parametrosFase!=null) {
			Iterator<? extends Parametro> itpf = this.parametrosFase.values().iterator();
			while (itpf.hasNext()) {
				Parametro par = itpf.next();
				par.idEntidadAsociada = this.id;
				par.actualizaParametro(idTransaccion, false);
			}
		}
		
		Iterator<FaseProyectoSistema> itFps = this.fasesProyecto.values().iterator();
		while (itFps.hasNext()) {
			FaseProyectoSistema fps = itFps.next();
			fps.idFase = this.id;
			fps.insertFaseProyectoSistema(idTransaccion);
		}
	}
		
	public void updateFasesProyecto(ArrayList<FaseProyecto> listadoFases, String idTransaccion)  throws Exception{
		Proyecto p = new Proyecto();
		p.id = this.p.id;
		p.cargaFasesProyecto();
		
		Iterator<FaseProyecto> iFp = p.fasesProyecto.iterator();
		while (iFp.hasNext()) {
			FaseProyecto fp = iFp.next();
			
			boolean encontrado = false;
			Iterator<FaseProyecto> itFProyecto = listadoFases.iterator();
			while (itFProyecto.hasNext()) {
				FaseProyecto fpAux = itFProyecto.next();
				if (fp.id == fpAux.id) {
					encontrado = true;
					break;
				}
			}
			
			if (!encontrado)
				fp.borraFasesProyecto(idTransaccion);
		}
		
		Iterator<FaseProyecto> itFProyecto = listadoFases.iterator();
		while (itFProyecto.hasNext()) {
			FaseProyecto fp = itFProyecto.next();
			fp.insertFaseProyecto(idTransaccion);
		}		
	}
	
	public float coberturaDemandaFases(Proyecto pDemanda, boolean apunteContable, Sistema s) {
		if (this.fasesProyecto==null) return 0;
		
		FaseProyectoSistema fp = this.fasesProyecto.get(s.codigo);
		if (fp!=null)
			return fp.coberturaDemandaFases(pDemanda, apunteContable);
		else
			return 0;
	} 
	
	public String toString() {
		return "Fase: " + this.nombre;
	}
	
	public ArrayList<FaseProyecto> purgarFases(ArrayList<FaseProyecto> listadoFases) {
		ArrayList<FaseProyecto> salida = new ArrayList<FaseProyecto>();
		
		Iterator<FaseProyecto> itFases = listadoFases.iterator();
		while (itFases.hasNext()) {
			FaseProyecto fp = itFases.next();
			
			if (fp.fasesProyecto!=null && fp.fasesProyecto.size()!=0) {
				Iterator<FaseProyectoSistema> itSistemas = fp.fasesProyecto.values().iterator();
				
				fp.fasesProyecto = new HashMap<String,FaseProyectoSistema>();
				
				while (itSistemas.hasNext()) {
					FaseProyectoSistema fps = itSistemas.next();
					
					if (fps.demandasSistema!=null && fps.demandasSistema.size()!=0) {
						Iterator<FaseProyectoSistemaDemanda> itDemandas = fps.demandasSistema.iterator();
						fps.demandasSistema = new ArrayList<FaseProyectoSistemaDemanda>();
						while (itDemandas.hasNext()) {
							FaseProyectoSistemaDemanda fpsd = itDemandas.next();
							Parametro paramCobertura = fpsd.parametrosFaseSistemaDemanda.get(MetaParametro.FASES_COBERTURA_DEMANDA);
							if ( (Float) paramCobertura.getValor() !=  0) fps.demandasSistema.add(fpsd);
						}
					}
					
					if (fps.demandasSistema.size()>0) {
						fp.fasesProyecto.put(fps.s.codigo,fps);
					}
				}				
			} 
		}
		
		
		salida = new ArrayList<FaseProyecto>();
		
		itFases = listadoFases.iterator();
		while (itFases.hasNext()) {
			FaseProyecto fp = itFases.next();
			
			boolean insertar = false;
			
			if (fp.fasesProyecto!=null && fp.fasesProyecto.size()!=0) {
				Iterator<FaseProyectoSistema> itSistemas = fp.fasesProyecto.values().iterator();
				while (itSistemas.hasNext()) {
					FaseProyectoSistema fps = itSistemas.next();
					
					if (fps.demandasSistema!=null && fps.demandasSistema.size()!=0) {
						insertar=true;
					}					
				}				
				if (insertar) {
					salida.add(fp);
				}
			} 
		}
		
		return salida;
	}
	
	public void insertaFaseSistema(Proyecto p, Sistema s) throws Exception  {
		if (p.fasesProyecto==null) {
			p.fasesProyecto = new ArrayList<FaseProyecto>();
		}
		
		Iterator<FaseProyecto> itFases = p.fasesProyecto.iterator();
		while (itFases.hasNext()) {
			FaseProyecto fp = itFases.next();
			
			if (fp.fasesProyecto.containsKey(s.codigo)) return;
		}
		
		FaseProyecto fp = null;
		ParametroFases pf = new ParametroFases();
		
		if (p.fasesProyecto.size()==0) {
			fp = new FaseProyecto();
			fp.id = -1;
			fp.p = p;
			fp.idProyecto = p.id;
			fp.fasesProyecto = new HashMap<String, FaseProyectoSistema>();
			fp.parametrosFase = pf.dameParametros(fp.getClass().getSimpleName(), Parametro.SOLO_METAPARAMETROS);
			pf = (ParametroFases) fp.parametrosFase.get(MetaParametro.FASE_PROYECTO_FX_IMPLANTACION);
			ParametroProyecto pp = p.getValorParametro(MetaParametro.PROYECTO_FX_FIN);
			
			pf.valorfecha = (Date) pp.getValor();
		} else {
			fp = p.fasesProyecto.get(p.fasesProyecto.size()-1);
		}		
		
		FaseProyectoSistema fps = new FaseProyectoSistema();
		fp.fasesProyecto.put(s.codigo, fps);
		fps.demandasSistema = new ArrayList<FaseProyectoSistemaDemanda>();
		fps.id = -1;
		fps.idFase = fp.id;
		fps.idSistema = s.id;
		fps.parametrosFaseSistema = pf.dameParametros(fps.getClass().getSimpleName(), Parametro.SOLO_METAPARAMETROS);
		fps.s = s;		
		
		ArrayList<Proyecto> listaProy = p.getDemandasAsociadas();
		Iterator<Proyecto> itDems = listaProy.iterator();
		while (itDems.hasNext()) {
			Proyecto demanda = itDems.next();
			Presupuesto pres = new Presupuesto();
			demanda.presupuestoActual = pres.dameUltimaVersionPresupuesto(demanda);
			if (demanda.presupuestoActual.costes.containsKey(s.id)) {
				FaseProyectoSistemaDemanda fpsd = new FaseProyectoSistemaDemanda();
				fpsd.id = -1;
				fpsd.apunteContable = demanda.apunteContable;
				fpsd.idDemanda = demanda.id;
				fpsd.idSistema = s.id;
				fpsd.p = demanda;
				fpsd.parametrosFaseSistemaDemanda = pf.dameParametros(fpsd.getClass().getSimpleName(), Parametro.SOLO_METAPARAMETROS);
				pf = (ParametroFases) fpsd.parametrosFaseSistemaDemanda.get(MetaParametro.FASES_COBERTURA_DEMANDA);
				pf.valorReal = 100;
				fps.demandasSistema.add(fpsd);
			}
		}
	}
	
}
