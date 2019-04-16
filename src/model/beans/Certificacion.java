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
import model.metadatos.TipoEnumerado;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class Certificacion implements Cargable{
	public int id = 0;
	public Proyecto p;
	public Sistema s;
	public Concepto concepto;
	public Concepto conceptoAdicional;
	
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
	
	public boolean isAdicional() {
		Iterator<CertificacionFase> itCf = this.certificacionesFases.iterator();
		while(itCf.hasNext()) {
			CertificacionFase cf = itCf.next();
			if (cf.adicional) return true;
		}
		
		return false;
	}
	
	public ArrayList<Certificacion> listado () {		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.p.id));
				
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> fases = consulta.ejecutaSQL("cConsultaCertificacion", listaParms, this);
		
		Iterator<Cargable> itFase = fases.iterator();
		ArrayList<Certificacion> salida = new ArrayList<Certificacion>();
		
		while (itFase.hasNext()) {
			Certificacion p = (Certificacion) itFase.next();
			
			ParametroCertificacion pf = new ParametroCertificacion();
			p.parametrosCertificacion = pf.dameParametros(this.getClass().getSimpleName(), p.id);
			
			CertificacionFase cf = new CertificacionFase();
			cf.idCertificacion = p.id;
			cf.certificacion = p;
			p.certificacionesFases = cf.listado();
			
			Iterator<CertificacionFase> itCert = p.certificacionesFases.iterator();
			while (itCert.hasNext()) {
				CertificacionFase cfAux = itCert.next();
				cfAux.certificacion = p;
			}
			
			salida.add(p);
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
		
	public void updateCertificacion( String idTransaccion)  throws Exception{

		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, this.p.id));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.s.id));
		
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("uActualizaCertificacion", listaParms, this, idTransaccion);
		
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
			if (fps.id == -1)
				fps.insertCertificacionFase(idTransaccion);
			else
				fps.updateCertificacionFase(idTransaccion);

		}		
	}
	
	public Certificacion generaCertificacion(Sistema s, Proyecto p, ArrayList<Certificacion> listaCertificaciones ) throws Exception {
		Certificacion cert = new Certificacion();
		
		Iterator<Certificacion> itC = listaCertificaciones.iterator();
		while (itC.hasNext()) {
			Certificacion cAux = itC.next();
			
			if (cAux.s.codigo.equals(s.codigo) && !cAux.isAdicional()) {
				return cAux;
			}
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
				float valorTotal = 0;
				
				Iterator<FaseProyectoSistemaDemanda> itFpsd = fps.demandasSistema.iterator();
				Concepto cSistema = new Concepto();
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
					valorTotal += c.valorEstimado;
				}
				
				cSistema.valor = valorFase; 
				
				if (valorFase!=0) {
					CertificacionFase cf = new CertificacionFase();
					cf.concepto = cSistema;
					cf.adicional = false;
					cf.certificacion = cert;
					cf.fase = fp;
					cf.certificacionesParciales = new ArrayList<CertificacionFaseParcial>();
					cf.id = -1;
					cf.parametrosCertificacionFase = par.dameParametros(cf.getClass().getSimpleName(), Parametro.SOLO_METAPARAMETROS);
					cf.porcentaje = valorFase/valorTotal*100;
					cert.certificacionesFases.add(cf);
					
					Parametro parGen = (new Parametro()).getParametro(MetaParametro.PARAMETRO_ECONOMICO_TIPOCOBROESTANDARVCT);
					TipoCobroVCT tpVCT = (TipoCobroVCT) parGen.getValor();
					parGen = cf.parametrosCertificacionFase.get(MetaParametro.CERTIFICACION_FASE_TIPOVCT);
					parGen.valorObjeto = tpVCT;
					
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
						cfp.tipoEstimacion = TipoEnumerado.TIPO_PRESUPUESTO_EST;
						cfp.tarifaEstimada = -1;
						cfp.tarifaReal = -1;
						
						cf.certificacionesParciales.add(cfp);
					}
				}
			}
		}
		
		return cert;
	}
	
	public Certificacion generaCertificacionVacia(Sistema s, Proyecto p ) throws Exception {
		Certificacion cert = new Certificacion();
		
		if (p.fasesProyecto==null)
			p.cargaFasesProyecto();
		
		cert.id = -1;
		cert.p = p;
		cert.s = s;
		
		ParametroCertificacion par = new ParametroCertificacion();
		cert.parametrosCertificacion = par.dameParametros(Certificacion.class.getSimpleName(), Parametro.SOLO_METAPARAMETROS);
		cert.certificacionesFases = new ArrayList<CertificacionFase>();
		
		FaseProyecto fp = p.fasesProyecto.get(p.fasesProyecto.size()-1);
			
		Concepto cSistema = new Concepto();
		cSistema.valor = 0; 
				
		CertificacionFase cf = new CertificacionFase();
		cf.concepto = cSistema;
		cf.adicional = true;
		cf.certificacion = cert;
		cf.fase = fp;
		cf.certificacionesParciales = new ArrayList<CertificacionFaseParcial>();
		cf.id = -1;
		cf.parametrosCertificacionFase = par.dameParametros(cf.getClass().getSimpleName(), Parametro.SOLO_METAPARAMETROS);
		cf.porcentaje = 100;
		cert.certificacionesFases.add(cf);
		
		TipoCobroVCT tpVCT = TipoCobroVCT.listadoIds.get(TipoCobroVCT.PAGO_UNICO);
		Parametro parGen = cf.parametrosCertificacionFase.get(MetaParametro.CERTIFICACION_FASE_TIPOVCT);
		parGen.valorObjeto = tpVCT;
					
		Date fechaAnterior =  cf.fase.getFechaImplantacion();
							
		for (int i=(tpVCT.porcentajes.size()-1);i>=0;i--) {
			double porcentaje = tpVCT.porcentajes.get(i);
			double valorParcial = 0*porcentaje;
			
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
			cfp.tipoEstimacion = TipoEnumerado.TIPO_PRESUPUESTO_EST;
			cfp.tarifaEstimada = -1;
			cfp.tarifaReal = -1;
			
			cf.certificacionesParciales.add(cfp);
		}
		
		return cert;
	}
	
	public CertificacionFase generaCertificacionFaseVacia(Sistema s, Proyecto p, Certificacion cert, FaseProyecto f) throws Exception {
		if (p.fasesProyecto==null)
			p.cargaFasesProyecto();
		
		FaseProyecto fp = f;
		
		CertificacionFase cf = new CertificacionFase();
		cert.certificacionesFases.add(cf);
		Concepto cSistema = new Concepto();
		
		Iterator<FaseProyectoSistemaDemanda> itFases = f.fasesProyecto.get(s.codigo).demandasSistema.iterator();
		float valorEstimado = 0;
		while (itFases.hasNext()) {
			FaseProyectoSistemaDemanda fpsd = itFases.next();
			Presupuesto presAux = new Presupuesto();
			presAux.p = fpsd.p;
			presAux = presAux.dameUltimaVersionPresupuesto(presAux.p);
			presAux.cargaCostes();
			fpsd.p.presupuestoActual = presAux;
			Concepto c = fpsd.p.presupuestoActual.getCosteConcepto(s, MetaConcepto.porId(MetaConcepto.DESARROLLO));
			
			if (c!=null) { 
					
				ParametroFases parFas = fpsd.getParametro(MetaParametro.FASES_COBERTURA_DEMANDA);
				float porc = (Float) parFas.getValor();
				
				valorEstimado += c.valorEstimado*porc/100;
			}
		}
		
		
		cSistema.valor = valorEstimado;
		cf.concepto = cSistema;
		cf.adicional = false;
		cf.certificacion = cert;
		cf.fase = fp;
		cf.certificacionesParciales = new ArrayList<CertificacionFaseParcial>();
		cf.id = -1;
		ParametroFases par = new ParametroFases();
		cf.parametrosCertificacionFase = par.dameParametros(cf.getClass().getSimpleName(), Parametro.SOLO_METAPARAMETROS);
		cf.porcentaje = 0;
		cert.certificacionesFases.add(cf);
		
		Parametro parGen = (new Parametro()).getParametro(MetaParametro.PARAMETRO_ECONOMICO_TIPOCOBROESTANDARVCT);
		TipoCobroVCT tpVCT = (TipoCobroVCT) parGen.getValor();
		parGen = cf.parametrosCertificacionFase.get(MetaParametro.CERTIFICACION_FASE_TIPOVCT);
		parGen.valorObjeto = tpVCT;
		
		Date fechaAnterior =  cf.fase.getFechaImplantacion();
				
		for (int i=(tpVCT.porcentajes.size()-1);i>=0;i--) {
			double porcentaje = tpVCT.porcentajes.get(i);
			double valorParcial = porcentaje*0/100;
			
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
			cfp.tipoEstimacion = TipoEnumerado.TIPO_PRESUPUESTO_EST;
			cfp.tarifaEstimada = -1;
			cfp.tarifaReal = -1;
			
			cf.certificacionesParciales.add(cfp);
		}
		
		return cf;
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
	
	public void guardarCertificacion(String idTransaccion) throws Exception{
		if (this.id==-1)
			insertCertificacion(idTransaccion);
		else 
			updateCertificacion(idTransaccion);
	}
	
	public Float calculaCoste() {
		float acumulado = 0;
		
		Iterator<CertificacionFase> itCf = this.certificacionesFases.iterator();
		while (itCf.hasNext()) {
			CertificacionFase cf = itCf.next();			
			acumulado += cf.calculaCoste();
		}
		
		return acumulado;
	}
	
	public Tarifa getTarifa() {
		int idTarifa = -1;
		
		Iterator<CertificacionFase> itCf = this.certificacionesFases.iterator();
		while (itCf.hasNext()) {
			CertificacionFase cf = itCf.next();
			
			Iterator<CertificacionFaseParcial> itCfp = cf.certificacionesParciales.iterator();
			while (itCfp.hasNext()) {
				CertificacionFaseParcial cfp = itCfp.next();
				
				if (cfp.tarifaReal != -1) {
					Tarifa t = new Tarifa();
					t = t.tarifaPorCoste(true, cfp.tarifaReal);
					return t;
				} else 
					if (cfp.tarifaEstimada!=idTarifa) {
						Tarifa t = new Tarifa();
						t = Tarifa.porId(cfp.tarifaEstimada);
						if (t!=null) return t;
					}
			}
		}
		
		try {
			Parametro par = new Parametro();
			par = par.dameParametros(this.s.getClass().getSimpleName(), this.s.id).get(MetaParametro.PARAMETRO_SISTEMA_PROVEEDOR);
					
			Proveedor prov = (Proveedor) par.getValor();
			
			ArrayList<Tarifa> lTarifas = prov.listaTarifas();
			Iterator<Tarifa> itTarifa = lTarifas.iterator();
			
			while (itTarifa.hasNext()) {
				return itTarifa.next(); 
			}
			
		} catch (Exception e) {
			
		}
		
		return null;
	}
	

	
}
