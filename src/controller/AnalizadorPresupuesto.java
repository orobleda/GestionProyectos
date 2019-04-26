package controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import model.beans.Certificacion;
import model.beans.CertificacionFase;
import model.beans.CertificacionFaseParcial;
import model.beans.Concepto;
import model.beans.Coste;
import model.beans.Estimacion;
import model.beans.EstimacionAnio;
import model.beans.EstimacionMes;
import model.beans.FaseProyecto;
import model.beans.FaseProyectoSistemaDemanda;
import model.beans.FraccionImputacion;
import model.beans.Imputacion;
import model.beans.ParametroFases;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.beans.TopeImputacion;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import ui.Economico.ControlPresupuestario.ControlPresupuestario;
import ui.Economico.ControlPresupuestario.EdicionEstImp.NuevaEstimacion;

public class AnalizadorPresupuesto {
	
	public static int MODO_RESTANTE = 0;
	public static int MODO_IMPUTADO = 1;
	public static int MODO_ESTIMADO = 2;
	
	public Proyecto proyecto = null;
	public ArrayList<EstimacionAnio> estimacionAnual = null;
	public Date fechaPivote = null;
	public Presupuesto presupuesto = null;
	public ArrayList<Certificacion> certificaciones = null;
	public ArrayList<Estimacion> estimaciones = null;
	
	public AnalizadorPresupuesto clone() {
		AnalizadorPresupuesto ap = new AnalizadorPresupuesto();
		
		ap.proyecto = proyecto;
		ap.presupuesto = presupuesto;
		ap.fechaPivote = fechaPivote;
		
		ap.estimacionAnual = new ArrayList<EstimacionAnio>();
		
		for (int i=0;i<this.estimacionAnual.size();i++) {
			ap.estimacionAnual.add(this.estimacionAnual.get(i).clone());
		}
		
		return ap;
	}

	public void construyePresupuestoMensualizado(Presupuesto pres, Date fechaPivote, ArrayList<Estimacion> lEstimacion, ArrayList<Imputacion> lImputacion, ArrayList<FraccionImputacion> lFraccionImputacion, ArrayList<TopeImputacion> lTopeImputacion) {
		try {
			if (lEstimacion==null) lEstimacion = new ArrayList<Estimacion>();
			if (lImputacion==null) lImputacion = new ArrayList<Imputacion>();
						
			this.fechaPivote = fechaPivote;
			this.presupuesto = pres;
			
			construyeEsqueleto(pres.idProyecto, pres);
			pueblaImputaciones(lImputacion,lFraccionImputacion);
			pueblaEstimaciones(lEstimacion);
			pueblaTopes(lTopeImputacion);
			pueblaCertificaciones(null);
			
			Iterator<Coste> itCostes = pres.costes.values().iterator();
			
			while (itCostes.hasNext()) {
				Coste c = itCostes.next();
				Iterator<MetaConcepto> itMConcepto = MetaConcepto.listado.values().iterator();
				
				while (itMConcepto.hasNext()) {
					MetaConcepto mc = itMConcepto.next();
					if (mc.tipoGestionEconomica== MetaConcepto.GESTION_HORAS) {
						analizaConceptoSistema(c.sistema, mc, pres);
					} 				
				}				
			}
			
			Iterator<Certificacion> itCert = this.certificaciones.iterator();
			while (itCert.hasNext()) {
				Certificacion cAux = itCert.next();
				
				Iterator<EstimacionAnio> itEa = this.estimacionAnual.iterator();
				while (itEa.hasNext()) {
					EstimacionAnio eA = itEa.next();
					eA.repartirCertificacion(cAux);
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void analizaConceptoSistema(Sistema s, MetaConcepto c, Presupuesto pres) {
		int mesesRepartir = 0;
		int mesesRepartirResto = 0;
				
		Iterator<EstimacionAnio> itEA = this.estimacionAnual.iterator();
		float contadorFijo = 0;
		
		while (itEA.hasNext()) {
			EstimacionAnio eA = itEA.next();  
			mesesRepartir += eA.mesesHabilesRepartirFijo(s, c, this.fechaPivote, this);
			mesesRepartirResto += eA.mesesHabilesRepartir(s, c, this.fechaPivote, this);
		}
		
		Concepto concPresupuestado = pres.getCosteConcepto(s, c);
		
		float aRepartir = 0;
		
		if (concPresupuestado!=null) {
			aRepartir = concPresupuestado.valorEstimado-contadorFijo;
			float asignado = 0;
			
			itEA = this.estimacionAnual.iterator();
						
			while (itEA.hasNext()) {
				EstimacionAnio eA = itEA.next();  
				
				TopeImputacion tp = this.getEstimacionAnioTope(eA.anio, s, c);
				
				if (!tp.resto) {
					float cantidadAnual = concPresupuestado.valorEstimado*tp.porcentaje/100;
					contadorFijo = eA.cantidadYaEstimada(s, c, fechaPivote, this, pres);
					
					if (cantidadAnual<=contadorFijo) {
						eA.cantidad = 0;
						
						asignado+= contadorFijo;
					} else {
						eA.cantidad = cantidadAnual - contadorFijo;
						asignado = eA.cantidad + contadorFijo;
					}
				} else {
					contadorFijo = eA.cantidadYaEstimada(s, c, fechaPivote, this, pres);
					asignado+= contadorFijo;
				}
			}
						
			if (concPresupuestado.valorEstimado-asignado>0) {
				float cantidadPorMes = (concPresupuestado.valorEstimado-asignado)/mesesRepartir;
				
				itEA = this.estimacionAnual.iterator();
				
				while (itEA.hasNext()) {
					EstimacionAnio eA = itEA.next();  
					
					TopeImputacion tp = this.getEstimacionAnioTope(eA.anio, s, c);
					
					if (tp.resto) {
						float mesesRepartirAux = eA.mesesHabilesRepartirFijo(s, c, this.fechaPivote, this);
						float cantidadAnual = cantidadPorMes * mesesRepartirAux;
						contadorFijo = eA.cantidadYaEstimada(s, c, fechaPivote, this, pres);
						
						if (cantidadAnual<=contadorFijo) {
							eA.cantidad = 0;
						} else {
							eA.cantidad = cantidadAnual - contadorFijo;
							asignado += cantidadAnual - contadorFijo;
						}
						
					}
				}
			}
			
			if (concPresupuestado.valorEstimado-asignado>0) {
				float cantidadPorMes = (concPresupuestado.valorEstimado-asignado)/mesesRepartirResto;
				
				itEA = this.estimacionAnual.iterator();
				
				while (itEA.hasNext()) {
					EstimacionAnio eA = itEA.next();  
					
					TopeImputacion tp = this.getEstimacionAnioTope(eA.anio, s, c);
					
					if (tp.resto) {
						float mesesRepartirAux = eA.mesesHabilesRepartirFijo(s, c, this.fechaPivote, this);
						float cantidadAnual = cantidadPorMes * mesesRepartirAux;
						
						eA.cantidad += cantidadAnual;
					} 
				}
			}	
				
			itEA = this.estimacionAnual.iterator();
			
			while (itEA.hasNext()) {
				EstimacionAnio eA = itEA.next();  
				
				TopeImputacion tp = this.getEstimacionAnioTope(eA.anio, s, c);
													
				float mesesRepartirAnio = eA.mesesHabilesRepartirFijo(s, c, this.fechaPivote, this);
				if (mesesRepartirAnio!=0) {
					eA.repartirFijo(s, c, fechaPivote, this, eA.cantidad/mesesRepartirAnio);
					tp.cantidadTrasRepartir = eA.cantidad;
				} else {
					eA.repartirFijo(s, c, fechaPivote, this, 0);
					tp.cantidadTrasRepartir = 0;
				}
				
				eA.cantidad = 0;				
			}			
		}		
	}
	
	public HashMap<String, Sistema> detalleEstimado (Concepto c) {
		HashMap<String, Sistema> salida = new HashMap<String, Sistema>();
		Sistema sAux2 = null;
		
		if (this.estimacionAnual!=null ) {
			Iterator<EstimacionAnio> itEa = this.estimacionAnual.iterator();
			
			while (itEa.hasNext()) {
				EstimacionAnio eA = itEa.next();
				
				Iterator<EstimacionMes> itEm = eA.estimacionesMensuales.values().iterator();
				while (itEm.hasNext()) {
					EstimacionMes em = itEm.next();
					
					Iterator<Sistema> itSistema = em.estimacionesPorSistemas.values().iterator();
					while (itSistema.hasNext()) {
						Sistema sAux = itSistema.next();
						
						if (salida.containsKey(sAux.codigo)) {
							sAux2 = salida.get(sAux.codigo);
						} else {
							salida.put(sAux.codigo, sAux);
							sAux2 = sAux;
							sAux.listaEstimaciones = new HashMap<Integer,Estimacion>();
						}
						
						Estimacion eAux = null;
						
						if (sAux2.listaEstimaciones.containsKey(eA.anio)) {
							eAux = sAux2.listaEstimaciones.get(eA.anio);
						} else {
							eAux = new Estimacion();
							sAux2.listaEstimaciones.put(eA.anio,eAux);
						}						 
						
						Concepto cAux = sAux.listaConceptos.get(c.tipoConcepto.codigo);
						
						if (cAux.listaEstimaciones!=null) {
							Iterator<Estimacion> itEst = cAux.listaEstimaciones.iterator();
							
							while (itEst.hasNext()) {
								Estimacion est = itEst.next();
								if (est.recurso == null) eAux.importeProvisionado += est.importe;
								else 					 eAux.importe += est.importe;
							}
						}
						
						if (cAux.topeImputacion!=null) {
							eAux.importeProvisionado += cAux.topeImputacion.cantidad;
						}
						
					}
				}
			}
			
		}
	
		return salida;
	}
	
	public TopeImputacion getEstimacionAnioTope (int anio, Sistema s, MetaConcepto c) {
		Iterator<EstimacionAnio> itAnios = estimacionAnual.iterator();
		while (itAnios.hasNext()) {
			EstimacionAnio ea = itAnios.next();
			
			if (ea.anio == anio) {
				Sistema sAux = ea.topesAnuales.get(s.codigo);
				Concepto sConcepto = sAux.listaConceptos.get(c.codigo);
				return sConcepto.topeImputacion;				
			}
		}
		
		return null;
	}
	
	private void pueblaCertificaciones(HashMap<String, Certificacion> listaCertificaciones) throws Exception{
		Certificacion cert = new Certificacion();
		cert.p = this.proyecto;
		certificaciones = cert.listado();
		
		if (certificaciones!=null) {
			Iterator<Certificacion> itCerts = certificaciones.iterator();
			while (itCerts.hasNext()) {
				Certificacion certAux = itCerts.next();
				
				Iterator<CertificacionFase> itCf = certAux.certificacionesFases.iterator();
				while (itCf.hasNext()) {
					CertificacionFase cf = itCf.next();
					
					if (this.proyecto.fasesProyecto==null)
						this.proyecto.cargaFasesProyecto();
					
					Iterator<FaseProyecto> itFs = this.proyecto.fasesProyecto.iterator();
					while (itFs.hasNext()) {
						FaseProyecto fp = itFs.next();
						if (fp.id == cf.idFase) {
							cf.fase = fp;
							break;
						}
					}
				}
			}
		}
		
		if (listaCertificaciones!=null) {
			Iterator<Certificacion> itCerts = listaCertificaciones.values().iterator();
			while (itCerts.hasNext()) {
				Certificacion certAux = itCerts.next();
				/*if ()*/
			}
		}
		
		Iterator<Coste> itCostes = this.presupuesto.costes.values().iterator();
		this.proyecto.presupuestoActual = this.presupuesto;
		
		while (itCostes.hasNext()) {
			Coste c = itCostes.next();
			Iterator<MetaConcepto> itMConcepto = MetaConcepto.listado.values().iterator();
			
			while (itMConcepto.hasNext()) {
				MetaConcepto mc = itMConcepto.next();
				if (mc.id == MetaConcepto.DESARROLLO) {

					boolean encontrado = false;
					
					Iterator<Certificacion> itCert = this.certificaciones.iterator();
					while (itCert.hasNext()) {
						cert = itCert.next();
						if (cert.s.codigo.equals(c.sistema.codigo)) {
							cert.concepto = c.conceptosCoste.get(MetaConcepto.listado.get(MetaConcepto.DESARROLLO).codigo);
							Iterator<CertificacionFase> itCf = cert.certificacionesFases.iterator();
							
							while (itCf.hasNext()) {
								CertificacionFase cf = itCf.next();
								Concepto cp = new Concepto();
								float valorEstimado = 0;
								
								if (!cf.adicional) {
									encontrado = true;
								
									cp.valorEstimado = cf.porcentaje * cert.concepto.valorEstimado/100;
									
									Iterator<FaseProyectoSistemaDemanda> itFases = cf.fase.fasesProyecto.get(c.sistema.codigo).demandasSistema.iterator();
									
									while (itFases.hasNext()) {
										FaseProyectoSistemaDemanda fpsd = itFases.next();
										Presupuesto presAux = new Presupuesto();
										presAux.p = fpsd.p;
										presAux = presAux.dameUltimaVersionPresupuesto(presAux.p);
										presAux.cargaCostes();
										fpsd.p.presupuestoActual = presAux;
										Concepto caux = fpsd.p.presupuestoActual.getCosteConcepto(c.sistema, MetaConcepto.porId(MetaConcepto.DESARROLLO));
										
										if (caux!=null) { 
											ParametroFases parFas = fpsd.getParametro(MetaParametro.FASES_COBERTURA_DEMANDA);
											float porc = (Float) parFas.getValor();
											valorEstimado += caux.valorEstimado*porc/100;
										}
									}	
								}
								
								cp.valor = valorEstimado;
								cf.concepto = cp;
								
							}
						}
					} 
					
					if (!encontrado) {
						cert = cert.generaCertificacion(c.sistema, this.proyecto, certificaciones);
						if (cert!=null) {
							cert.concepto = c.conceptosCoste.get(mc.codigo);
							certificaciones.add(cert);
						}
					}
					
				}					
			}				
		}
		
	}
	
	
	private void pueblaTopes(ArrayList<TopeImputacion> listaTopes) {
		TopeImputacion tpImp = new TopeImputacion();
		
		if (listaTopes!=null)
			tpImp.topes = listaTopes;
		else 		
			tpImp.listadoTopes(proyecto);
		
		Iterator<EstimacionAnio> itEstAnio = this.estimacionAnual.iterator();
		
		while (itEstAnio.hasNext()) {
			EstimacionAnio ea = itEstAnio.next();
			
			if (ea.estimacionesMensuales.size()!=0) {
				EstimacionMes em = (EstimacionMes) ea.estimacionesMensuales.values().toArray()[0];
				
				if (em.estimacionesPorSistemas.size()!=0) {
					Iterator<Sistema> itSistemasMes = em.estimacionesPorSistemas.values().iterator();
					
					while (itSistemasMes.hasNext()) {
						Sistema s = itSistemasMes.next();
						Sistema sTope = (Sistema) s.clone();
						ea.topesAnuales.put(s.codigo, sTope);
						sTope.listaConceptos = new HashMap<String, Concepto>();
						
						Iterator<MetaConcepto> itMConcepto = MetaConcepto.listado.values().iterator();
						
						while (itMConcepto.hasNext()) {
							MetaConcepto mc = itMConcepto.next();
							Concepto cAux = new Concepto();
							cAux.tipoConcepto = mc;
							sTope.listaConceptos.put(mc.codigo, cAux);
							cAux.topeImputacion = tpImp.dameTope(sTope, cAux, ea.anio);
						}
					}
				}
			}
			
		}
	}
	
	public ArrayList<Estimacion> trataAccionesE(ArrayList<Estimacion> listaBD, ArrayList<Estimacion> listaAcciones) {
		
		if (listaAcciones.size()==0) return listaBD;
		
		ArrayList<Estimacion> listaSalida =  new ArrayList<Estimacion>();
		
		Iterator<Estimacion> itEstimacion = listaBD.iterator();
		while (itEstimacion.hasNext()) {
			Estimacion eAux = itEstimacion.next();
			
			Iterator<Estimacion> itEstimacion2 = listaAcciones.iterator();
			Estimacion eAux2 = null;
			while (itEstimacion2.hasNext()) {
				eAux2 = itEstimacion2.next();
				if (eAux2.id == eAux.id) {
					break;
				} else {
					eAux2 = null;
				} 
			}
			
			if (eAux2 == null) listaSalida.add(eAux);
			else {
				if (eAux2.modo == NuevaEstimacion.MODO_ELIMINAR) {
					
				}
				if (eAux2.modo == NuevaEstimacion.MODO_MODIFICAR) {
					listaSalida.add(eAux2);
				}
			}			
		}
		
		itEstimacion = listaAcciones.iterator();
		while (itEstimacion.hasNext()) {
			Estimacion eAux = itEstimacion.next();

			if (eAux.modo == NuevaEstimacion.MODO_INSERTAR) {
				listaSalida.add(eAux);
			}
		}		
		
		return listaSalida;
	}
	
	private void pueblaEstimaciones(ArrayList<Estimacion> lEstimacion) throws Exception {
		Estimacion estimacion = new Estimacion();
		EstimacionAnio eA = null;
		Sistema s = null;
		Calendar c = null;
		ArrayList<Estimacion> listaEstimaciones = estimacion.listado(proyecto);
		estimaciones = listaEstimaciones;
		
		listaEstimaciones = trataAccionesE(listaEstimaciones, lEstimacion);
		
		Iterator<Estimacion> itEstimaciones = listaEstimaciones.iterator();
		while (itEstimaciones.hasNext()) {
			estimacion = itEstimaciones.next();
			c = Calendar.getInstance();
			c.setTime(estimacion.fxInicio);
			
			Iterator<EstimacionAnio> itEst = estimacionAnual.iterator();
			while (itEst.hasNext()) {
				eA = itEst.next();
				if (eA.anio == c.get(Calendar.YEAR)) {
					break;
				}
			}
			
			EstimacionMes eM = eA.estimacionesMensuales.get(c.get(Calendar.MONTH));
			s = eM.estimacionesPorSistemas.get(estimacion.sistema.codigo);
						
			Concepto conc = null;
			conc = s.listaConceptos.get(estimacion.natCoste.codigo);
			if (conc.listaEstimaciones==null)
				conc.listaEstimaciones = new ArrayList<Estimacion>();
			conc.listaEstimaciones.add(estimacion);
		}		
		
	}
	
	public ArrayList<Imputacion> trataAccionesI(ArrayList<Imputacion> listaBD, ArrayList<Imputacion> listaAcciones) {
		
		if (listaAcciones.size()==0) return listaBD;
		
		ArrayList<Imputacion> listaSalida =  new ArrayList<Imputacion>();
		
		Iterator<Imputacion> itImputacion = listaBD.iterator();
		while (itImputacion.hasNext()) {
			Imputacion eAux = itImputacion.next();
			
			Iterator<Imputacion> itImputacion2 = listaAcciones.iterator();
			Imputacion eAux2 = null;
			while (itImputacion2.hasNext()) {
				eAux2 = itImputacion2.next();
				if (eAux2.id == eAux.id) {
					break;
				}
				else eAux2 = null;
			}
			
			if (eAux2 == null) listaSalida.add(eAux);
			else {
				if (eAux2.modo == NuevaEstimacion.MODO_ELIMINAR) {
					
				}
				if (eAux2.modo == NuevaEstimacion.MODO_MODIFICAR) {
					listaSalida.add(eAux2);
				}
			}			
		}
		
		itImputacion = listaAcciones.iterator();
		while (itImputacion.hasNext()) {
			Imputacion eAux = itImputacion.next();

			if (eAux.modo == NuevaEstimacion.MODO_INSERTAR) {
				listaSalida.add(eAux);
			}
		}		
		
		return listaSalida;
	}
	
	public Imputacion getImputacion(int idImputacion) throws Exception {
		Imputacion imputacion = new Imputacion();

		ArrayList<Imputacion> listaImputaciones = imputacion.listado(proyecto, null);
				
		Iterator<Imputacion> itImputaciones = listaImputaciones.iterator();
		while (itImputaciones.hasNext()) {
			imputacion = itImputaciones.next();

			if (imputacion.id == idImputacion) {
				return imputacion;
			}
		}
		
		return null;
		
	}
	
	private void pueblaImputaciones(ArrayList<Imputacion> lImputacion, ArrayList<FraccionImputacion> listaFracciones) throws Exception {
		Imputacion imputacion = new Imputacion();
		EstimacionAnio eA = null;
		Sistema s = null;
		Calendar c = null;
		ArrayList<Imputacion> listaImputaciones = imputacion.listado(proyecto,listaFracciones);
		
		listaImputaciones = trataAccionesI(listaImputaciones, lImputacion);
		
		Iterator<Imputacion> itImputaciones = listaImputaciones.iterator();
		while (itImputaciones.hasNext()) {
			imputacion = itImputaciones.next();
			c = Calendar.getInstance();
			c.setTime(imputacion.fxInicio);
			
			Iterator<EstimacionAnio> itEst = estimacionAnual.iterator();
			while (itEst.hasNext()) {
				eA = itEst.next();
				if (eA.anio == c.get(Calendar.YEAR)) {
					break;
				}
			}
			
			EstimacionMes eM = eA.estimacionesMensuales.get(c.get(Calendar.MONTH));
			s = eM.estimacionesPorSistemas.get(imputacion.sistema.codigo);
			
			Concepto conc = null;
			conc = s.listaConceptos.get(imputacion.natCoste.codigo);
			if (conc.listaImputaciones==null) 
				conc.listaImputaciones = new ArrayList<Imputacion>();
			conc.listaImputaciones.add(imputacion);	
		}		
		
	}
	
	private void construyeEsqueleto(int idProyecto, Presupuesto pres) throws Exception {
		estimacionAnual = new ArrayList<EstimacionAnio>();
		
		proyecto = new Proyecto();
		proyecto.id = idProyecto;		
		proyecto.cargaProyecto();
		
		Date fInicio = (Date) proyecto.getValorParametro(MetaParametro.PROYECTO_FX_INICIO).getValor();
		Date fFin = (Date) proyecto.getValorParametro(MetaParametro.PROYECTO_FX_FIN).getValor();
		
		Calendar cInicio = Calendar.getInstance();
		cInicio.setTime(fInicio);
		
		Calendar cFin = Calendar.getInstance();
		cFin.setTime(fFin);
		
		EstimacionAnio eA = null;
		EstimacionMes eM = null;
		
		while (cInicio.compareTo(cFin)<=0) {
			if (eA==null || eA.anio!=cInicio.get(Calendar.YEAR)){
				eA = new EstimacionAnio();
				eA.estimacionesMensuales = new HashMap<Integer,EstimacionMes>();
				eA.anio = cInicio.get(Calendar.YEAR);
				estimacionAnual.add(eA);
			} 
			
			eM = new EstimacionMes();
			eM.mes = cInicio.get(Calendar.MONTH)+1;
			eM.estimacionesPorSistemas = new HashMap<String, Sistema>();
			eA.estimacionesMensuales.put(eM.mes-1, eM);
			
			Iterator<Coste> itCostes = pres.costes.values().iterator();
			
			while (itCostes.hasNext()) {
				Coste c = itCostes.next();
				Sistema sAux = (Sistema) c.sistema.clone();
				sAux.listaConceptos = new HashMap<String, Concepto>();
				
				Iterator<MetaConcepto> itMConcepto = MetaConcepto.listado.values().iterator();
				
				while (itMConcepto.hasNext()) {
					MetaConcepto mc = itMConcepto.next();
					Concepto cAux = new Concepto();
					cAux.listaEstimaciones = new ArrayList<Estimacion>();
					cAux.listaImputaciones = new ArrayList<Imputacion>();
					cAux.tipoConcepto = mc;
					sAux.listaConceptos.put(mc.codigo, cAux);
				}
				
				eM.estimacionesPorSistemas.put(sAux.codigo,sAux);
			}
			
			cInicio.add(Calendar.MONTH, 1);
		}
		
	}
	
	public HashMap<String, Concepto> acumuladoPorSistemaConcepto(Sistema s, int anio) {
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
				
		Iterator<EstimacionAnio> itEa = this.estimacionAnual.iterator();
		while (itEa.hasNext()) {
			EstimacionAnio eA = itEa.next();
			
			if (anio==-1 || anio == eA.anio) {
				Iterator<Concepto> itConcepto = eA.acumuladoPorSistemaConcepto(s).values().iterator();
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
						iAcum.setImporte(iAcum.getImporte() + i.getImporte());
						iAcum.setHoras( iAcum.getHoras() + i.getHoras());
					}
					if (c.topeImputacion!=null) {
						Estimacion eAcum = cAcum.listaEstimaciones.get(0);
						eAcum.importe += c.topeImputacion.cantidad;
					}
				}
			}
		}
		
		return salida;
		
	}
	
	public Presupuesto toPresupuesto(int tipoPres, int anio, boolean conTotales) {
		Presupuesto pres = this.presupuesto.clone();
		Iterator<Coste> itCostes = pres.costes.values().iterator();
		
		while (itCostes.hasNext()) {
			Coste c = itCostes.next();
					
			Iterator<MetaConcepto> itMConcepto = MetaConcepto.listado.values().iterator();
			Concepto cAcumuladoS = null;			
			
			if (conTotales) {
				cAcumuladoS = new Concepto();
				MetaConcepto mc = new MetaConcepto();
				mc.codigo = "TOTAL";
				mc.descripcion = "Total";
				mc.id = MetaConcepto.ID_TOTAL;
				cAcumuladoS.tipoConcepto = mc;
				cAcumuladoS.valorEstimado = 0;
				c.conceptosCoste.put(mc.codigo,cAcumuladoS);
			}
			
			while (itMConcepto.hasNext()) {
				MetaConcepto mc = itMConcepto.next();
				
				Concepto cNuevoPres = c.conceptosCoste.get(mc.codigo);
				
				float acumulado  = 0;
				
				Iterator<EstimacionAnio> itestAnio = this.estimacionAnual.iterator();
				
				while (itestAnio.hasNext()) {
					EstimacionAnio eAux = itestAnio.next();
					
					if (tipoPres != ControlPresupuestario.VISTA_PRES_ANIO || (tipoPres == ControlPresupuestario.VISTA_PRES_ANIO && eAux.anio==anio)) 
						acumulado+= eAux.calcularPresupuesto(mc, c.sistema, tipoPres);
				}
				
				if (cNuevoPres==null) {
					cNuevoPres = new Concepto();
					cNuevoPres.tipoConcepto = mc;
					cNuevoPres.idCoste = c.id;
					c.conceptosCoste.put(mc.codigo,cNuevoPres);
				}
				
				cNuevoPres.valor = acumulado;
				if (conTotales) {
					cAcumuladoS.valorEstimado += acumulado;
				}
				cNuevoPres.valorEstimado = acumulado;
			}			
		}
		
		return pres;
	}
	
	public HashMap<String, Coste> getRestante(boolean total, int anio, int modo, boolean incluirDes) {
		HashMap<String, Coste> salida = new HashMap<String, Coste>();
		
		Iterator<EstimacionAnio> itEa = this.estimacionAnual.iterator();
		
		while (itEa.hasNext()) {
			EstimacionAnio ea = itEa.next();
			
			if (total == true || anio == ea.anio) {
				HashMap<String, Coste> costeAnio = ea.getRestante(modo);
				
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
						cAux = c;
					}
					
					if (incluirDes) {
						Iterator<Certificacion> itCertificacion = this.certificaciones.iterator();
						while (itCertificacion.hasNext()) {
							Certificacion cert = itCertificacion.next();
							Concepto cDesarrollo = null;
							
							if (cAux.conceptosCoste.containsKey(MetaConcepto.listado.get(MetaConcepto.DESARROLLO).codigo)) {
								cDesarrollo = cAux.conceptosCoste.get(MetaConcepto.listado.get(MetaConcepto.DESARROLLO).codigo);
							} else {
								cDesarrollo = new Concepto();
								cAux.conceptosCoste.put(MetaConcepto.listado.get(MetaConcepto.DESARROLLO).codigo,cDesarrollo);
							}
							
							if (cert.s.id == cAux.sistema.id) {
								CertificacionFaseParcial cfp = cert.totaliza();
								if (modo == AnalizadorPresupuesto.MODO_ESTIMADO) {
									cDesarrollo.valor = cfp.valEstimado;									
								}
								if (modo == AnalizadorPresupuesto.MODO_IMPUTADO) {
									cDesarrollo.valor = cfp.valReal;
								}
								if (modo == AnalizadorPresupuesto.MODO_RESTANTE) {
									cDesarrollo.valor = cfp.valEstimado- cfp.valReal;
								}
							}
						}

					}
				}

			}
		}
		
		return salida;
	}
	
	
	
}
