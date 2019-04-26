package model.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.metadatos.Sistema;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class RelRecursoSistema implements Cargable{
	
	public static ArrayList<RelRecursoSistema> listado = null; 
	 
	public int id = 0;
	public int ocurrencias = 0;
	
	public Recurso recurso = null;
	public Sistema sistema = null;

	public RelRecursoSistema() {
    }
	
	public ArrayList<Sistema> buscaRelaciones(Recurso r) {
		ArrayList<Sistema> listadoSalida = new ArrayList<Sistema>();
		ArrayList<RelRecursoSistema> listado = this.buscaRelaciones();
		
		Iterator<RelRecursoSistema> itRels = listado.iterator();
		while (itRels.hasNext()) {
			RelRecursoSistema rrsAux = itRels.next();
			if (rrsAux.recurso.id == r.id) {
				listadoSalida.add(rrsAux.sistema);
			}
		}
		
		return listadoSalida;
	}
	
	public ArrayList<RelRecursoSistema> buscaRelaciones() { 
		if (listado==null) {
			ConsultaBD consulta = new ConsultaBD();
			
			List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
			
			ArrayList<Cargable> relRecursoTarifas = consulta.ejecutaSQL("cRelRecSistema", listaParms, this);
			
			Iterator<Cargable> itCargable = relRecursoTarifas.iterator();
			ArrayList<RelRecursoSistema> salida = new ArrayList<RelRecursoSistema> ();
			
			while (itCargable.hasNext()) {
				RelRecursoSistema rec = (RelRecursoSistema) itCargable.next();
				salida.add(rec);
			}
			
			RelRecursoSistema.listado = salida;
			
			return salida;
		} else return listado;
    }
	
	public Sistema getMejorSistema(ArrayList<Sistema> sistemasDisponibles, ArrayList<Estimacion> lEstimaciones, int mes, int anio) {
		Sistema sEncontrado = null;
		Sistema sAux = null;
		
		// 1. Primera Opción existe estimación para la misma fecha
		if (anio!=-1 && mes !=-1 && lEstimaciones!=null) {
			Iterator<Estimacion> itEst = lEstimaciones.iterator();
			while (itEst.hasNext()) {
				Estimacion est = itEst.next();
				if (est.sistema!=null) {
					if (est.anio == anio && est.mes == mes && est.recurso.id == this.recurso.id) {
						Iterator<Sistema> itSistema = sistemasDisponibles.iterator();
						while (itSistema.hasNext()) {
							Sistema sIte = itSistema.next();
							if (sIte.id == est.sistema.id) {
								sEncontrado = est.sistema;
								break;
							}
						}
					} else
						if (est.recurso.id == this.recurso.id) {
							Iterator<Sistema> itSistema = sistemasDisponibles.iterator();
							while (itSistema.hasNext()) {
								Sistema sIte = itSistema.next();
								if (sIte.id == est.sistema.id) {
									sAux = est.sistema;
									break;
								}
							}
						}
				}
			}
			
			if (sEncontrado==null) {
				sEncontrado=sAux;
			}
		}
		
		//Tercera opción, conteo de asignaciones a sistemas
		if (sEncontrado==null) {
			List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
			listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.recurso.id));
					
			ConsultaBD consulta = new ConsultaBD();
			ArrayList<Cargable> relRecursoTarifas = consulta.ejecutaSQL("cRelRecSistema", listaParms, this);
			
			Iterator<Cargable> itCargable = relRecursoTarifas.iterator();
			
			float contadorMax = 0;
			Sistema sistemaMax = null;
			
			while (itCargable.hasNext()) {
				RelRecursoSistema rec = (RelRecursoSistema) itCargable.next();
				
				Iterator<Sistema> itSistema = sistemasDisponibles.iterator();
				while (itSistema.hasNext()) {
					Sistema sIte = itSistema.next();
					if (sIte.id == rec.sistema.id) {
						
						if (sistemaMax==null) {
							sistemaMax = rec.sistema;
						}
						
						if (rec.ocurrencias>contadorMax) {
							contadorMax = rec.ocurrencias;
							sistemaMax = sIte;
						}
					}
				}
			}
			
			sEncontrado = sistemaMax;
		}
		
		return sEncontrado;
	}
	
	public void insertaRelacion(String idTransaccion) throws Exception{
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.recurso.id));
		listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,this.sistema.id));
				
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> relRecursoTarifas = consulta.ejecutaSQL("cRelRecSistema", listaParms, this);
		
		Iterator<Cargable> itCargable = relRecursoTarifas.iterator();
		
		float contador = 0;
		
		while (itCargable.hasNext()) {
			RelRecursoSistema rec = (RelRecursoSistema) itCargable.next();
			contador = rec.ocurrencias;
		}
		
		contador++;
		
		if (contador==1) {
			listaParms = new ArrayList<ParametroBD>();
			listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_ID,this.id));
			listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,this.recurso.id));
			listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_INT,this.sistema.id));
			listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_INT,this.ocurrencias));
					
			consulta.ejecutaSQL("iInsertaRelRecursoSistema", listaParms, this, idTransaccion);
		} else {
			listaParms = new ArrayList<ParametroBD>();
			listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,this.recurso.id));
			listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_INT,this.sistema.id));
			listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.ocurrencias));
					
			consulta.ejecutaSQL("uActualizaRelRecursoSistema", listaParms, this, idTransaccion);
		}
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
			if (salida.get("relrsId")==null) this.id = 0; else this.id = (Integer) salida.get("relrsId");
			
			if (salida.get("relrsIdRec")==null) this.recurso = null; else try { 
					int id = (Integer) salida.get("relrsIdRec");
					this.recurso = Recurso.listadoRecursosEstatico().get(id);
			} catch (Exception e) {}
			
			if (salida.get("relrsIdSis")==null) this.sistema = null; else try { 
				int id = (Integer) salida.get("relrsIdSis");
				this.sistema = Sistema.listado.get(id);
			} catch (Exception e) {}
			
			if (salida.get("relrsOcur")==null) this.ocurrencias = 0; else try { 
				this.ocurrencias = (Integer) salida.get("relrsOcur");
			} catch (Exception e) {}
			
		} catch (Exception e) {
			
		} 
		
		return this;
	}
	
}
