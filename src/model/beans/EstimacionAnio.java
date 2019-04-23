package model.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import controller.AnalizadorPresupuesto;
import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class EstimacionAnio  implements Cargable {
	
	public int id = 0;
	public Date fechaInicio = null;
	public Date fechaFin = null;
	public Concepto concepto = null;
	public int idConcepto = 0;
	public float cantidad = 0;
	public float cantidadImputada = 0;
	public float porcentaje = 0;
	public int anio = 0;
	public boolean resto = false;
		
	public HashMap<Integer, EstimacionMes> estimacionesMensuales = null;
	public HashMap<String, Sistema> topesAnuales = null;
	
	public EstimacionAnio () {
		topesAnuales = new HashMap<String, Sistema>();
		
		for (int i=1;i<4;i++) {
			Concepto c = new Concepto();
			c.tipoConcepto = MetaConcepto.porId(i);
			c.valor = 0;
		}		
	}
	
	public EstimacionAnio clone() {
		EstimacionAnio ea = new EstimacionAnio();
		
		ea.id = this.id;
		ea.fechaInicio = (Date) this.fechaInicio.clone();
		ea.fechaFin = (Date) this.fechaFin.clone();
		ea.concepto = this.concepto.clone();
		ea.idConcepto = this.idConcepto;
		ea.cantidad = this.cantidad;
		ea.porcentaje = this.porcentaje;
		ea.anio = this.anio;
		ea.resto = this.resto;
		
		ea.estimacionesMensuales = new HashMap<Integer, EstimacionMes>();
		ea.topesAnuales = new HashMap<String, Sistema>();		
				
		for (int i=0;i<this.estimacionesMensuales.keySet().size();i++) {
			ea.estimacionesMensuales.put((Integer) this.estimacionesMensuales.keySet().toArray()[i], this.estimacionesMensuales.get(this.estimacionesMensuales.keySet().toArray()[i]).clone());
		}
		
		for (int i=0;i<this.topesAnuales.keySet().size();i++) {
			ea.topesAnuales.put((String) this.topesAnuales.keySet().toArray()[i], this.topesAnuales.get(this.topesAnuales.keySet().toArray()[i]).clone());
		}
		
		return ea;
	}
	
	public void repartirResto(Sistema s, MetaConcepto c, Date fechaPivote, AnalizadorPresupuesto ap, float repartoMensual) {
		
		Iterator<EstimacionMes> itEM = this.estimacionesMensuales.values().iterator();
		
		while (itEM.hasNext()) {
			EstimacionMes em = itEM.next();
			
			if (em.esHabilParaRepartir(s, c, fechaPivote, anio, ap)) {
				em.repartirResto(s, c, repartoMensual);
			}			
		}		
	}
	
	public void repartirCertificacion(Certificacion cert) {
		Iterator<EstimacionMes> itEm = this.estimacionesMensuales.values().iterator();
		
		while (itEm.hasNext()) {
			EstimacionMes em = itEm.next();
			
			em.repartirCertificacion(cert, this);
		}
	}
	
	public void repartirFijo(Sistema s, MetaConcepto c, Date fechaPivote, AnalizadorPresupuesto ap, float repartoMensual) {
		
		Iterator<EstimacionMes> itEM = this.estimacionesMensuales.values().iterator();
		
		while (itEM.hasNext()) {
			EstimacionMes em = itEM.next();
			
			if (em.esHabilParaRepartirFijo(s, c, fechaPivote, anio, ap)) {
				em.repartirResto(s, c, repartoMensual);
			}			
		}		
	}
	
	public float cantidadYaEstimada(Sistema s, MetaConcepto c, Date fechaPivote, AnalizadorPresupuesto ap, Presupuesto pres) {
		
		TopeImputacion tp = ap.getEstimacionAnioTope(this.anio, s, c);
		float contadorEstimado = 0;
		float fijo = 0;
		
		Iterator<EstimacionMes> itEM = this.estimacionesMensuales.values().iterator();
		
		while (itEM.hasNext()) {
			EstimacionMes em = itEM.next();
			contadorEstimado += em.cantidadYaEstimada(s, c, fechaPivote, this.anio);			
		}
		
		return contadorEstimado;			
	}
	
	public float cantidadFija(Sistema s, MetaConcepto c, Date fechaPivote, AnalizadorPresupuesto ap, Presupuesto pres) {
		
		TopeImputacion tp = ap.getEstimacionAnioTope(this.anio, s, c);
		float contadorEstimado = 0;
		float fijo = 0;
		
		Iterator<EstimacionMes> itEM = this.estimacionesMensuales.values().iterator();
		
		while (itEM.hasNext()) {
			EstimacionMes em = itEM.next();
			contadorEstimado += em.cantidadFija(s, c, fechaPivote, this.anio);			
		}
		
		if (tp.resto==false) {
			if (tp.porcentaje!=0) {
				Concepto concPresupuestado = pres.getCosteConcepto(s, c);
				fijo = concPresupuestado.valorEstimado * tp.porcentaje/100;
			} else {
				fijo = tp.cantidad;
			}
			
			float diferencia = fijo - contadorEstimado;
			
			if (diferencia<0) {
				return 0;
			} else {
				int mesesHabiles = this.mesesHabilesRepartir(s, c, fechaPivote, ap);
				if (mesesHabiles>0) {
					return diferencia;
				} else {
					return fijo;
				}
			}
		} else {
			return contadorEstimado;
		}		
	}
	
	public int mesesHabilesRepartirFijo(Sistema s, MetaConcepto c, Date fechaPivote, AnalizadorPresupuesto ap) {
		int contador = 0;
		
		Iterator<EstimacionMes> itEM = this.estimacionesMensuales.values().iterator();
		
		while (itEM.hasNext()) {
			EstimacionMes em = itEM.next();
			
			if (em.esHabilParaRepartirFijo(s, c, fechaPivote, this.anio, ap))
				contador ++;
		}
		
		return contador;
		
	}
	
	public int mesesHabilesRepartir(Sistema s, MetaConcepto c, Date fechaPivote, AnalizadorPresupuesto ap) {
		int contador = 0;
		
		Iterator<EstimacionMes> itEM = this.estimacionesMensuales.values().iterator();
		
		while (itEM.hasNext()) {
			EstimacionMes em = itEM.next();
			
			if (em.esHabilParaRepartir(s, c, fechaPivote, this.anio, ap))
				contador ++;
		}
		
		return contador;
		
	}

	public ArrayList<EstimacionAnio> listado (Concepto c) {		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, c.id));

		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> listaEstimacion = consulta.ejecutaSQL("cConsultaTopeImputaciones", listaParms, this);
		
		Iterator<Cargable> itEa = listaEstimacion.iterator();
		ArrayList<EstimacionAnio> salida = new ArrayList<EstimacionAnio>();
		
		while (itEa.hasNext()) {
			EstimacionAnio est = (EstimacionAnio) itEa.next();
			est.concepto = c;
			salida.add(est);
		}
				
		return salida;
	}
	
	public float calcularPresupuesto(MetaConcepto c, Sistema s, int tipoPres) {
		Iterator<EstimacionMes> iMes = this.estimacionesMensuales.values().iterator();
		
		float acumulado = 0;
		
		while (iMes.hasNext()) {
			EstimacionMes emAux = iMes.next();
			acumulado += emAux.calcularPresupuesto(c, s,tipoPres);
		}
		
		return acumulado;
	}
	
	public String toString() {
		return concepto.tipoConcepto.codigo + " " + anio + " " + cantidad;
	}
	
	public HashMap<String, Concepto> totalPorConcepto(Sistema s) {
		HashMap<String, Concepto> salida = new HashMap<String, Concepto>();
		
		Iterator<MetaConcepto> itMC = MetaConcepto.listado.values().iterator();
		
		while (itMC.hasNext()) {
			MetaConcepto mc = itMC.next();
			
			Concepto c = new Concepto();
			c.tipoConcepto = mc;	
			salida.put(mc.codigo, c);
		}
		
		Iterator<EstimacionMes> itestMes = this.estimacionesMensuales.values().iterator();
		
		while (itestMes.hasNext()) {
			EstimacionMes em = itestMes.next();
			
			Iterator<Concepto> itConcepto = salida.values().iterator();
			
			while (itConcepto.hasNext()) {
				Concepto c = itConcepto.next();
				Concepto cAux = em.calcularPresupuestoDesglosado(c.tipoConcepto, s);
				
				c.valor += cAux.valor;
				c.valorEstimado += cAux.valorEstimado;
			}
			
		}
		
		return salida;
		
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
				
		Iterator<EstimacionMes> itMes = this.estimacionesMensuales.values().iterator();
		while (itMes.hasNext()) {
			EstimacionMes eM = itMes.next();
			
			Iterator<Concepto> itConcepto = eM.acumuladoPorSistemaConcepto(s).values().iterator();
			while (itConcepto.hasNext()) {
				Concepto c = itConcepto.next();
				Concepto cAcum = salida.get(c.tipoConcepto.codigo);
				
				if (c.listaEstimaciones.size()>0){
					Estimacion e = c.listaEstimaciones.get(0);
					Estimacion eAcum = cAcum.listaEstimaciones.get(0);
					eAcum.importe += e.importe;
					eAcum.horas += e.horas;
				}
				if (c.listaImputaciones.size()>0){
					Imputacion i = c.listaImputaciones.get(0);
					Imputacion iAcum = cAcum.listaImputaciones.get(0);
					iAcum.setImporte(iAcum.getImporte()+i.getImporte());
					iAcum.setHoras(iAcum.getHoras() + i.getHoras());
				}
				if (c.topeImputacion!=null) {
					Estimacion eAcum = cAcum.listaEstimaciones.get(0);
					eAcum.importe += c.topeImputacion.cantidad;
				}
			}
		}
		
		return salida;
	}
	
	public HashMap<String, Coste> getRestante() {
		HashMap<String, Coste> salida = new HashMap<String, Coste>();
		
		Iterator<EstimacionMes> itEm = this.estimacionesMensuales.values().iterator();
		
		while (itEm.hasNext()) {
			EstimacionMes em = itEm.next();
			
			HashMap<String, Coste> costeAnio = em.getRestante();
				
			Iterator<Coste> itCostes = costeAnio.values().iterator();
			while (itCostes.hasNext()) {
				Coste c = itCostes.next();
				
				Coste cAux = null;
				
				if (salida.containsKey(c.sistema.codigo)) {
					cAux = salida.get(c.sistema.codigo);
					
					Iterator<Concepto> itConcepto = c.conceptosCoste.values().iterator();
					while (itConcepto.hasNext()) {
						Concepto conc = itConcepto.next();
						
						Concepto concAux = cAux.conceptosCoste.get(conc.tipoConcepto.codigo);
						concAux.valor += conc.valor;
					}						
				} else {
					salida.put(c.sistema.codigo,c);
				}
			}
			
		}
		
		return salida;
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try { if (salida.get("tImpId")==null)     { this.id = 0;            } else this.id = (Integer) salida.get("tImpId");                           } catch (Exception e){System.out.println();}
		try { if (salida.get("tImpAnio")==null)     { this.anio = 0;            } else this.anio = (Integer) salida.get("tImpAnio");                           } catch (Exception e){System.out.println();}
		try { if (salida.get("tImpCantidad")==null)     { this.cantidad = 0;            } else this.cantidad = ((Double) salida.get("tImpCantidad")).floatValue();    } catch (Exception e){System.out.println();}
		try { if (salida.get("tImpPorcentaje")==null)     { this.porcentaje = 0;            } else this.porcentaje = ((Double) salida.get("tImpPorcentaje")).floatValue();    } catch (Exception e){System.out.println();}
		//try { if (salida.get("tImpResto")==null)     { this.resto = false;            } else this.resto = (Integer) salida.get("tImpResto")==1?true:false;    } catch (Exception e){System.out.println();}
		try { if (salida.get("tImpFxInicioImputacion")==null) { this.fechaInicio = null;       } else this.fechaInicio = (Date) FormateadorDatos.parseaDato((String)salida.get("tImpFxInicioImputacion"),FormateadorDatos.FORMATO_FECHA); } catch (Exception e){System.out.println();}
		try { if (salida.get("tImpFxFinImputacion")==null) { this.fechaFin = null;       } else this.fechaFin = (Date) FormateadorDatos.parseaDato((String)salida.get("tImpFxFinImputacion"),FormateadorDatos.FORMATO_FECHA); } catch (Exception e){System.out.println();}
		try { if (salida.get("tImpIdCoste")==null)     { this.idConcepto = 0;            } else this.idConcepto = (Integer) salida.get("tImpIdCoste");                           } catch (Exception e){System.out.println();}
				
		return this;
	}
}
