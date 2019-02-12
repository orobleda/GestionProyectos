package model.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import controller.AnalizadorPresupuesto;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import ui.Economico.ControlPresupuestario.ControlPresupuestario;

public class EstimacionMes {
	public HashMap<String, Sistema> estimacionesPorSistemas = null;
	public int mes = -1;
	
	public EstimacionMes clone() {
		EstimacionMes em = new EstimacionMes();
		em.mes = this.mes;
		
		for (int i=0;i<this.estimacionesPorSistemas.keySet().size();i++) {
			em.estimacionesPorSistemas.put((String) this.estimacionesPorSistemas.keySet().toArray()[i], this.estimacionesPorSistemas.get(this.estimacionesPorSistemas.keySet().toArray()[i]).clone());
		}
		
		return em;
	}
 	
	public void repartirResto(Sistema s, MetaConcepto c, float reparto) {
		Sistema sBuscado = estimacionesPorSistemas.get(s.codigo);
		Concepto cAux = sBuscado.listaConceptos.get(c.codigo);
		
		TopeImputacion tp = new TopeImputacion();
		tp.cantidad = reparto;
		
		cAux.topeImputacion = tp;
	}
	
	public float calcularPresupuesto(MetaConcepto c, Sistema s, int tipoPres) {
		Iterator<Sistema> itSistema = this.estimacionesPorSistemas.values().iterator();
		
		float acumulado = 0;
		
		while (itSistema.hasNext()) {
			Sistema sAux = itSistema.next();
			
			if ((s!=null && sAux.codigo.equals(s.codigo)) || (s==null)) {
				Concepto cAux = sAux.listaConceptos.get(c.codigo);
				
				if (cAux!=null) {
					HashMap<Integer,Integer> yaImputado = new HashMap<Integer, Integer>();
					
					if (tipoPres!=ControlPresupuestario.VISTA_PRES_REAL) {
						if (cAux.topeImputacion!=null)
							acumulado += cAux.topeImputacion.cantidad;
					}
					
					if (tipoPres!=ControlPresupuestario.VISTA_PRES_ESTIMADO) {
						Iterator<Imputacion> itImp = cAux.listaImputaciones.iterator();
						while (itImp.hasNext()) {
							Imputacion iAux = itImp.next();
							acumulado += iAux.getImporte();
							yaImputado.put(iAux.recurso.id, iAux.recurso.id);
						}
					}
					
					if (tipoPres!=ControlPresupuestario.VISTA_PRES_REAL) {
						Iterator<Estimacion> itEst = cAux.listaEstimaciones.iterator();
						while (itEst.hasNext()) {
							Estimacion eAux = itEst.next();
							if (!yaImputado.containsKey(eAux.recurso.id))
								acumulado += eAux.importe;
						}
					}
				}
			}
		}
		
		return acumulado;
	}
	
	public HashMap<String, Concepto> totalPorConcepto(Sistema s) {
		HashMap<String, Concepto> salida = new HashMap<String, Concepto>();
		
		Iterator<MetaConcepto> itMC = MetaConcepto.listado.values().iterator();
		
		while (itMC.hasNext()) {
			MetaConcepto mc = itMC.next();
			
			Concepto c = new Concepto();
			c.tipoConcepto = mc;	
			salida.put(mc.codigo, c);
			
			Concepto acum = this.calcularPresupuestoDesglosado(mc,s);
			c.valor += acum.valor;
			c.valorEstimado += acum.valorEstimado;
		}
		
		return salida;
		
	}
	
	public Concepto calcularPresupuestoDesglosado(MetaConcepto c, Sistema s) {
		Iterator<Sistema> itSistema = this.estimacionesPorSistemas.values().iterator();
		
		Concepto cSalida = new Concepto();
		cSalida.tipoConcepto = c;
		
		while (itSistema.hasNext()) {
			Sistema sAux = itSistema.next();
			
			if ((s!=null && sAux.codigo.equals(s.codigo)) || (s==null)) {
				Concepto cAux = sAux.listaConceptos.get(c.codigo);
				
				if (cAux!=null) {
					if (cAux.topeImputacion!=null)
						cSalida.valorEstimado += cAux.topeImputacion.cantidad;
					
					Iterator<Imputacion> itImp = cAux.listaImputaciones.iterator();
					while (itImp.hasNext()) {
						Imputacion iAux = itImp.next();
						cSalida.valor += iAux.getImporte();
					}
					
					Iterator<Estimacion> itEst = cAux.listaEstimaciones.iterator();
					while (itEst.hasNext()) {
						Estimacion eAux = itEst.next();
						cSalida.valorEstimado += eAux.importe;
					}
				}
			}
		}
		
		return cSalida;
	}
	
	public float cantidadYaEstimada(Sistema s, MetaConcepto c, Date fechaPivote, int anio) {
		float acumulado = 0;
		
		Sistema sAux = estimacionesPorSistemas.get(s.codigo);
		Concepto conc = sAux.listaConceptos.get(c.codigo);
		
		Calendar calThis = Calendar.getInstance();
		calThis.set(anio,mes,1);
		
		Calendar pivote = Calendar.getInstance();
		pivote.setTime(fechaPivote);
		
		Iterator<Estimacion> itEstimacion = conc.listaEstimaciones.iterator();
			while (itEstimacion.hasNext()) {
				Estimacion est = itEstimacion.next();
				acumulado += est.importe;
		}

		
		return acumulado;
	}
	
	public float cantidadFija(Sistema s, MetaConcepto c, Date fechaPivote, int anio) {
		float acumulado = 0;
		
		Sistema sAux = estimacionesPorSistemas.get(s.codigo);
		Concepto conc = sAux.listaConceptos.get(c.codigo);
		
		Calendar calThis = Calendar.getInstance();
		calThis.set(anio,mes,1);
		
		Calendar pivote = Calendar.getInstance();
		pivote.setTime(fechaPivote);
		
		if (pivote.after(calThis)) {
			Iterator<Imputacion> itImputacion = conc.listaImputaciones.iterator();
			while (itImputacion.hasNext()) {
				Imputacion imp = itImputacion.next();
				acumulado += imp.getImporte();
			}
		} else {
			Iterator<Estimacion> itEstimacion = conc.listaEstimaciones.iterator();
			while (itEstimacion.hasNext()) {
				Estimacion est = itEstimacion.next();
				acumulado += est.importe;
			}
		}
		
		return acumulado;
	}
	
	public boolean esHabilParaRepartirFijo(Sistema s, MetaConcepto c, Date fechaPivote, int anio, AnalizadorPresupuesto ap) {		
		Calendar cal = Calendar.getInstance();
		cal.setTime(fechaPivote);
		
		Calendar calMes = Calendar.getInstance();
		calMes.set(anio, mes-1, 1);
		
		if (calMes.before(cal)) return false;
				
		return true;
	}
	
	public boolean esHabilParaRepartir(Sistema s, MetaConcepto c, Date fechaPivote, int anio, AnalizadorPresupuesto ap) {		
		Calendar cal = Calendar.getInstance();
		cal.setTime(fechaPivote);
		
		Calendar calMes = Calendar.getInstance();
		calMes.set(anio, mes-1, 1);
		
		if (calMes.before(cal)) return false;
				
		TopeImputacion tp = ap.getEstimacionAnioTope(anio, s, c);

		if (tp!=null) {
				if (tp.resto) 
						return true;
				else 
					return false;				
		}
		
		return false;
	}
	
	public ArrayList<Concepto> costesPorRecurso(MetaConcepto mc, Sistema s) {
		ArrayList<Concepto> listaSalida = new ArrayList<Concepto>();
		HashMap<Integer,Concepto> usuariosProcesados = new HashMap<Integer,Concepto>();
		Concepto sinUsuario = new Concepto();
		sinUsuario.tipoConcepto = mc;
		sinUsuario.listaEstimaciones = new ArrayList<Estimacion>();
		sinUsuario.listaImputaciones = new ArrayList<Imputacion>();
		Estimacion estAcum = new Estimacion();
		sinUsuario.listaEstimaciones.add(estAcum);
		sinUsuario.s = s;
		
		Iterator<Sistema> itSistemas = this.estimacionesPorSistemas.values().iterator();
		
		while (itSistemas.hasNext()) {
			Sistema sIte = itSistemas.next();
			
			if (s==null || (s!=null && s.codigo.equals(sIte.codigo))) {
				Iterator<Concepto> itConcepto = sIte.listaConceptos.values().iterator();
				
				while (itConcepto.hasNext()) {
					Concepto cC = itConcepto.next();
					
					if (mc==null || (mc!=null && mc.codigo.equals(cC.tipoConcepto.codigo))) {
						Iterator<Estimacion> itEstimacion = cC.listaEstimaciones.iterator();
						
						while (itEstimacion.hasNext()) {
							Estimacion est = itEstimacion.next();
							Concepto c = null;
							Estimacion est2 = null;
							
							if (usuariosProcesados.containsKey(est.recurso.id)) {
								c = usuariosProcesados.get(est.recurso.id);
								if (c.listaEstimaciones.size()>0) {
									est2 = c.listaEstimaciones.get(0);
								} else {
									est2 = new Estimacion();
									c.listaEstimaciones.add(est2);
								}
							} else {
								c = new Concepto();
								usuariosProcesados.put(est.recurso.id,c);
								c.r = est.recurso;
								c.tipoConcepto = mc;
								c.listaEstimaciones = new ArrayList<Estimacion>();
								c.listaImputaciones = new ArrayList<Imputacion>();
								est2 = new Estimacion();
								c.listaEstimaciones.add(est2);
								c.s = sIte;
							}
							est2.id = est.id;
							est2.horas += est.horas;
							est2.importe += est.importe;
							
							if (est.aprobacion == Estimacion.APROBADA)
								est2.aprobacion = est.aprobacion;
						}
						
						Iterator<Imputacion> itImputacion = cC.listaImputaciones.iterator();
						
						while (itImputacion.hasNext()) {
							Imputacion est = itImputacion.next();
							Concepto c = null;
							Imputacion imp2 = null;
							
							if (usuariosProcesados.containsKey(est.recurso.id)) {
								c = usuariosProcesados.get(est.recurso.id);
								if (c.listaImputaciones.size()>0) {
									imp2 = c.listaImputaciones.get(0);
								} else {
									imp2 = new Imputacion();
									c.listaImputaciones.add(imp2);
								}
							} else {
								c = new Concepto();
								usuariosProcesados.put(est.recurso.id,c);
								c.r = est.recurso;
								c.tipoConcepto = mc;
								c.listaImputaciones = new ArrayList<Imputacion>();
								c.listaEstimaciones = new ArrayList<Estimacion>();
								imp2 = new Imputacion();
								c.listaImputaciones.add(imp2);
								c.s = sIte;
							}
							imp2.id = est.id;
							imp2.tarifa = est.tarifa;
							imp2.setHoras(imp2.getHoras() + est.getHoras());
							imp2.setImporte(imp2.getImporte() + est.getImporte());							
						}
						
						if (cC.topeImputacion!=null)
							estAcum.importe += cC.topeImputacion.cantidad;
					}
				}
			}
		}
		
		listaSalida.addAll(usuariosProcesados.values());
		listaSalida.add(sinUsuario);
		
		return listaSalida;
	}
	
	public HashMap<String, Concepto> acumuladoPorSistemaConcepto(Sistema s) {
		HashMap<String, Concepto> salida = new HashMap<String, Concepto>();
		
		Iterator<MetaConcepto> itMConcepto = MetaConcepto.listado.values().iterator();
		
		while (itMConcepto.hasNext()) {
			MetaConcepto mc = itMConcepto.next();
			Concepto c = new Concepto();
			c.tipoConcepto = mc;
			c.listaEstimaciones = new ArrayList<Estimacion>();
			Estimacion est = new Estimacion();
			c.listaEstimaciones.add(est);
			c.listaImputaciones = new ArrayList<Imputacion>();
			Imputacion imp = new Imputacion();
			c.listaImputaciones.add(imp);
			salida.put(mc.codigo,c);
		}
				
		Iterator<Sistema> itSistema = this.estimacionesPorSistemas.values().iterator();
		while (itSistema.hasNext()) {
			Sistema sis = itSistema.next();
			
			if (s==null || (s!=null && sis.codigo.equals(s.codigo))) {
				Iterator<Concepto> itConcepto = sis.listaConceptos.values().iterator();
				
				while (itConcepto.hasNext()) {
					Concepto c = itConcepto.next();
					Concepto cAcum = salida.get(c.tipoConcepto.codigo);
					Estimacion eAcum = cAcum.listaEstimaciones.get(0);
					
					if (c.listaEstimaciones!=null){
						Iterator<Estimacion> itEst = c.listaEstimaciones.iterator();						
						
						while (itEst.hasNext()) {
							Estimacion e = itEst.next();							
							eAcum.importe += e.importe;
							eAcum.horas += e.horas;
						}
					}
					if (c.listaImputaciones!=null){
						Iterator<Imputacion> itImp = c.listaImputaciones.iterator();
						Imputacion iAcum = cAcum.listaImputaciones.get(0);
						
						while (itImp.hasNext()) {
							Imputacion i = itImp.next();						
							iAcum.setImporte(iAcum.getImporte() + i.getImporte());
							iAcum.setHoras(iAcum.getHoras() + i.getHoras());
						}
					}
					if (c.topeImputacion!=null) {
						eAcum.importe += c.topeImputacion.cantidad;
					}
				}
			}
		}
		
		return salida;
		
	}
}
