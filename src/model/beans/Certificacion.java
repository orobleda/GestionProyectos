package model.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import model.metadatos.TipoCobroVCT;
import model.metadatos.TipoPresupuesto;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class Certificacion implements Cargable{
	public int id = 0;
	public Proyecto p;
	public Sistema s;
	
	public HashMap<String,? extends Parametro> parametrosCertificacion = null;
	public ArrayList<CertificacionFase> certificacionesFases = null;
	
	
	public Certificacion clone() {
		Certificacion fp = new Certificacion();
		fp.id = this.id;
		fp.s = this.s;
		fp.p = this.p;
		
		if (this.certificacionesFases!=null ) {
			fp.certificacionesFases = new ArrayList<CertificacionFase>();
			Iterator<CertificacionFase> itFps = this.certificacionesFases.iterator();
			while (itFps.hasNext()) {
				CertificacionFase fps = itFps.next();
				fp.certificacionesFases.add(fps.clone());
				
			}
		}
		
		if (parametrosCertificacion!=null) {
			HashMap<String,Parametro> mapAux = new HashMap<String,Parametro>();
			Iterator<? extends Parametro> itParFas = this.parametrosCertificacion.values().iterator();
			while (itParFas.hasNext()) {
				ParametroCertificacion parCert = (ParametroCertificacion) itParFas.next();
				mapAux.put(parCert.codParametro, parCert.clone());
			}
			
			fp.parametrosCertificacion = mapAux;
		}
		
		return fp;
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		certificacionesFases = new ArrayList<CertificacionFase>();
		
		try {
		 	if (salida.get("cerSistema")==null)  { 
		 		this.s = null;
			} else {
		 		this.s = Sistema.listado.get((Integer) salida.get("cerSistema"));
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("cerProyecto")==null)  { 
		 		this.p = null;
			} else {
		 		this.p = Proyecto.getProyectoEstatico((Integer) salida.get("cerProyecto"));
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("cerId")==null)  { 
		 		this.id = 0;
			} else {
		 		this.id = (Integer) salida.get("cerId");
			}
		} catch (Exception ex) {}
		
		return this;
	}
	
	
	public HashMap<String,Certificacion> listado () {		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.p.id));
				
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> fases = consulta.ejecutaSQL("cConsultaCertificacion", listaParms, this);
		
		Iterator<Cargable> itFase = fases.iterator();
		HashMap<String,Certificacion> salida = new HashMap<String,Certificacion>();
		
		while (itFase.hasNext()) {
			Certificacion p = (Certificacion) itFase.next();
			
			ParametroCertificacion pf = new ParametroCertificacion();
			p.parametrosCertificacion = pf.dameParametros(this.getClass().getSimpleName(), p.id);
			
			CertificacionFase cf = new CertificacionFase();
			cf.idCertificacion = p.id;
			p.certificacionesFases = cf.listado();
			
			Iterator<CertificacionFase> itCert = p.certificacionesFases.iterator();
			while (itCert.hasNext()) {
				CertificacionFase cfAux = itCert.next();
				cfAux.certificacion = p;
			}
			
			salida.put(p.s.codigo,p);
		}
				
		return salida;
	}
	
	public void borraCertificacion(String idTransaccion)  throws Exception{
		
		Iterator<CertificacionFase> itFps = this.certificacionesFases.iterator();
		while (itFps.hasNext()) {
			CertificacionFase fps = itFps.next();
			fps.borraCertificacionFase(idTransaccion);
		}
		
		if (this.parametrosCertificacion!=null) {
			Iterator<? extends Parametro> itpf = this.parametrosCertificacion.values().iterator();
			while (itpf.hasNext()) {
				Parametro par = itpf.next();
				par.bajaParametro(idTransaccion);
			}
		}
				
		ConsultaBD consulta = new ConsultaBD();
	
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));
	
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("dBorraCertificacion", listaParms, this, idTransaccion);
	}
	
	public void insertCertificacion(String idTransaccion)  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_ID, this.id));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.p.id));
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.s.id));
		
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("iInsertaCertificacion", listaParms, this, idTransaccion);
		
		this.id = ParametroBD.ultimoId;
		
		if (this.parametrosCertificacion!=null) {
			Iterator<? extends Parametro> itpf = this.parametrosCertificacion.values().iterator();
			while (itpf.hasNext()) {
				Parametro par = itpf.next();
				par.idEntidadAsociada = this.id;
				par.actualizaParametro(idTransaccion, false);
			}
		}
		
		Iterator<CertificacionFase> itFps = this.certificacionesFases.iterator();
		while (itFps.hasNext()) {
			CertificacionFase fps = itFps.next();
			fps.idCertificacion = this.id;
			fps.insertCertificacionFase(idTransaccion);

		}
	}
		
	public void updateCertificacion(ArrayList<Certificacion> listadoFases, String idTransaccion)  throws Exception{

		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, this.p.id));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.s.id));
		
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("uActualizaCertificacion", listaParms, this, idTransaccion);
		
		this.id = ParametroBD.ultimoId;
		
		if (this.parametrosCertificacion!=null) {
			Iterator<? extends Parametro> itpf = this.parametrosCertificacion.values().iterator();
			while (itpf.hasNext()) {
				Parametro par = itpf.next();
				par.idEntidadAsociada = this.id;
				par.actualizaParametro(idTransaccion, false);
			}
		}
		
		Iterator<CertificacionFase> itFps = this.certificacionesFases.iterator();
		while (itFps.hasNext()) {
			CertificacionFase fps = itFps.next();
			fps.idCertificacion = this.id;
			fps.updateCertificacionFase(idTransaccion);

		}		
	}
	
	public Certificacion generaCertificacion(Sistema s, Proyecto p, HashMap<String,Certificacion> listaCertificaciones ) throws Exception {
		Certificacion cert = new Certificacion();
		
		if (listaCertificaciones.containsKey(s.codigo)) {
			return listaCertificaciones.get(s.codigo);
		}
		
		if (p.fasesProyecto==null)
			p.cargaFasesProyecto();
		
		FaseProyecto fp = new FaseProyecto();
		fp.insertaFaseSistema(p,s);
		
		cert.id = -1;
		cert.p = p;
		cert.s = s;
		
		ParametroCertificacion par = new ParametroCertificacion();
		cert.parametrosCertificacion = par.dameParametros(Certificacion.class.getSimpleName(), Parametro.SOLO_METAPARAMETROS);
		cert.certificacionesFases = new ArrayList<CertificacionFase>();
		
		Iterator<FaseProyecto> itFases = p.fasesProyecto.iterator();
		
		while (itFases.hasNext()) {
			fp = itFases.next();
			FaseProyectoSistema fps = fp.fasesProyecto.get(s.codigo);
			
			if (fps!=null) {
				float valorFase = 0;
				
				Iterator<FaseProyectoSistemaDemanda> itFpsd = fps.demandasSistema.iterator();
				while (itFpsd.hasNext()) {
					FaseProyectoSistemaDemanda fpsd = itFpsd.next();
					
					Presupuesto presAux = new Presupuesto();
					presAux.p = fpsd.p;
					presAux = presAux.dameUltimaVersionPresupuesto(presAux.p);
					presAux.cargaCostes();
					fpsd.p.presupuestoActual = presAux;
					Concepto c = fpsd.p.presupuestoActual.getCosteConcepto(s, MetaConcepto.porId(MetaConcepto.DESARROLLO));
					
					if (c==null) 
						return null;
					ParametroFases parFas = fpsd.getParametro(MetaParametro.FASES_COBERTURA_DEMANDA);
					float porc = (Float) parFas.getValor();
					
					valorFase += c.valorEstimado*porc/100;
				}
				
				if (valorFase!=0) {
					CertificacionFase cf = new CertificacionFase();
					cf.adicional = false;
					cf.certificacion = cert;
					cf.fase = fp;
					cf.certificacionesParciales = new ArrayList<CertificacionFaseParcial>();
					cf.id = -1;
					cf.parametrosCertificacionFase = par.dameParametros(cf.getClass().getSimpleName(), Parametro.SOLO_METAPARAMETROS);
					cert.certificacionesFases.add(cf);
					
					Parametro parGen = (new Parametro()).getParametro(MetaParametro.PARAMETRO_ECONOMICO_TIPOCOBROESTANDARVCT);
					TipoCobroVCT tpVCT = (TipoCobroVCT) parGen.getValor();
					
					Date fechaAnterior =  cf.fase.getFechaImplantacion();
							
					for (int i=(tpVCT.porcentajes.size()-1);i>=0;i--) {
						double porcentaje = tpVCT.porcentajes.get(i);
						double valorParcial = porcentaje*valorFase/100;
						
						CertificacionFaseParcial cfp = new CertificacionFaseParcial();
						cfp.certificacionFase = cf;
						if (i==(tpVCT.porcentajes.size()-1)) {
							cfp.fxCertificacion = fechaAnterior;
						} else {
							cfp.fxCertificacion = calcularFechaPrevia(fechaAnterior);
						}
						fechaAnterior = cfp.fxCertificacion;
						
						cfp.id = -1;
						cfp.nombre = tpVCT.nombres.get(i);
						cfp.paramCertificacionFaseParcial = par.dameParametros(cfp.getClass().getSimpleName(), Parametro.SOLO_METAPARAMETROS);
						cfp.porcentaje = new Float(porcentaje);
						cfp.tsCertificacion = fechaAnterior.getTime();
						cfp.valEstimado = new Float(valorParcial);
						cfp.tipoEstimacion = TipoPresupuesto.ESTIMACION;
						
						cf.certificacionesParciales.add(cfp);
					}
				}
			}
		}
		
		return cert;
	}
	
	public Date calcularFechaPrevia(Date fechaFinal) {
		ParametroProyecto parPro = this.p.getValorParametro(MetaParametro.PROYECTO_FX_INICIO);
		ParametroProyecto parProFin = this.p.getValorParametro(MetaParametro.PROYECTO_FX_FIN);
		
		Parametro par = new Parametro();
		par = Parametro.listadoParametros.get(MetaParametro.PARAMETRO_ECONOMICO_PERINTRACERT);
		
		Calendar c = Calendar.getInstance();
		c.setTime(fechaFinal);
		c.add(Calendar.DAY_OF_MONTH, -1*(Integer) par.getValor());
		
		Calendar cActual = Calendar.getInstance();
		cActual.setTime(Constantes.fechaActual());
		
		Calendar cInicio = Calendar.getInstance();
		cInicio.setTime((Date) parPro.getValor() );
		
		if (c.after(cActual) && c.after(cInicio)) {
			return c.getTime();
		}
		
		if (c.before(cActual) && c.before(cInicio)) {
			if (cActual.before(cInicio)) {
				return cInicio.getTime();
			} else {
				return cActual.getTime();
			}
		} 
		
		if (c.before(cActual) && c.after(cInicio)) {
			return cActual.getTime();
		}
		
		if (c.after(cActual) && c.before(cInicio)) {
			return cInicio.getTime();
		}
		
		return (Date) parProFin.getValor();
	} 
	

	
}
