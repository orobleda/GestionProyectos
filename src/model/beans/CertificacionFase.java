package model.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.metadatos.MetaParametro;
import model.metadatos.TipoCobroVCT;
import model.metadatos.TipoEnumerado;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class CertificacionFase implements Cargable{
	
	public static final int CERTIFICACION_BASE = 0;
	public static final int CERTIFICACION_ADICIONAL = 1;
	
	public Concepto concAdicional = null;
	
	public int id = 0;
	public FaseProyecto fase;
	public int idFase;
	public Certificacion certificacion;
	public int idCertificacion;
	public boolean adicional;
	public Concepto concepto = null;
	public float porcentaje = 0;
	
	public HashMap<String,? extends Parametro> parametrosCertificacionFase = null;
	public ArrayList<CertificacionFaseParcial> certificacionesParciales = null;
	
	
	public CertificacionFase clone() {
		CertificacionFase fp = new CertificacionFase();
		fp.id = this.id;
		fp.idFase = this.idFase;
		fp.fase = this.fase;
		fp.certificacion = this.certificacion;
		fp.adicional = this.adicional;
		fp.porcentaje = this.porcentaje;
		
		if (this.certificacionesParciales!=null ) {
			fp.certificacionesParciales = new ArrayList<CertificacionFaseParcial>();
			Iterator<CertificacionFaseParcial> itFps = this.certificacionesParciales.iterator();
			while (itFps.hasNext()) {
				CertificacionFaseParcial fps = itFps.next();
				fp.certificacionesParciales.add(fps.clone());
				
			}
		}
		
		if (parametrosCertificacionFase!=null) {
			HashMap<String,Parametro> mapAux = new HashMap<String,Parametro>();
			Iterator<? extends Parametro> itParFas = this.parametrosCertificacionFase.values().iterator();
			while (itParFas.hasNext()) {
				ParametroCertificacion parCert = (ParametroCertificacion) itParFas.next();
				mapAux.put(parCert.codParametro, parCert.clone());
			}
			
			fp.parametrosCertificacionFase = mapAux;
		}
		
		return fp;
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		certificacionesParciales = new ArrayList<CertificacionFaseParcial>();
		
		try {
		 	if (salida.get("cerFase")==null)  { 
		 		this.idFase = 0;
			} else {
		 		this.idFase = (Integer) salida.get("cerFase");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("cerCertificacion")==null)  { 
		 		this.idCertificacion = 0;
			} else {
		 		this.idCertificacion = (Integer) salida.get("cerCertificacion");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("cerId")==null)  { 
		 		this.id = 0;
			} else {
		 		this.id = (Integer) salida.get("cerId");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("cerAdicional")==null)  { 
		 		this.adicional = false;
			} else {
		 		this.adicional = (Integer) salida.get("cerAdicional") == CertificacionFase.CERTIFICACION_ADICIONAL?Constantes.TRUE:Constantes.FALSE;
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("cerPorc")==null)  { 
		 		this.porcentaje = 0;
			} else {
		 		this.porcentaje = ((Double) salida.get("cerPorc")).floatValue();
			}
		} catch (Exception ex) {}
		
		return this;
	}
	
	
	public ArrayList<CertificacionFase> listado () {		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.certificacion.id));
				
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> fases = consulta.ejecutaSQL("cConsultaCertificacion_fase", listaParms, this);
		
		Iterator<Cargable> itFase = fases.iterator();
		ArrayList<CertificacionFase> salida = new ArrayList<CertificacionFase>();
		
		while (itFase.hasNext()) {
			CertificacionFase p = (CertificacionFase) itFase.next();
			
			ParametroCertificacion pf = new ParametroCertificacion();
			p.parametrosCertificacionFase = pf.dameParametros(this.getClass().getSimpleName(), p.id);
			
			CertificacionFaseParcial cf = new CertificacionFaseParcial();
			cf.certificacion_fase = p.id;
			p.certificacionesParciales = cf.listado();
			
			Iterator<CertificacionFaseParcial> itCFP = p.certificacionesParciales.iterator();
			while (itCFP.hasNext()) {
				CertificacionFaseParcial cfp = itCFP.next();
				cfp.certificacionFase = p;
			}
			
			salida.add(p);
		}
				
		return salida;
	}
	
	public void borraCertificacionFase(String idTransaccion)  throws Exception{
		
		Iterator<CertificacionFaseParcial> itFps = this.certificacionesParciales.iterator();
		while (itFps.hasNext()) {
			CertificacionFaseParcial fps = itFps.next();
			fps.borraCertificacionFaseParcial(idTransaccion);
		}
		
		if (this.parametrosCertificacionFase!=null) {
			Iterator<? extends Parametro> itpf = this.parametrosCertificacionFase.values().iterator();
			while (itpf.hasNext()) {
				Parametro par = itpf.next();
				par.bajaParametro(idTransaccion);
			}
		}
				
		ConsultaBD consulta = new ConsultaBD();
	
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.id));
	
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("dBorraCertificacion_fase", listaParms, this, idTransaccion);
	}
	
	public void insertCertificacionFase(String idTransaccion)  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_ID, this.id));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.certificacion.id));
		listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_INT, this.adicional == Constantes.TRUE? CertificacionFase.CERTIFICACION_ADICIONAL: CertificacionFase.CERTIFICACION_BASE));
		listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_REAL, this.porcentaje));
		if (this.fase!=null)
			listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.fase.id));
		
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("iInsertaCertificacion_fase", listaParms, this, idTransaccion);
		
		this.id = ParametroBD.ultimoId;
		
		if (this.parametrosCertificacionFase!=null) {
			Iterator<? extends Parametro> itpf = this.parametrosCertificacionFase.values().iterator();
			while (itpf.hasNext()) {
				Parametro par = itpf.next();
				par.idEntidadAsociada = this.id;
				par.actualizaParametro(idTransaccion, false);
			}
		}
		
		Iterator<CertificacionFaseParcial> itFps = this.certificacionesParciales.iterator();
		while (itFps.hasNext()) {
			CertificacionFaseParcial fps = itFps.next();
			fps.certificacion_fase = this.id;
			fps.insertCertificacionFaseParcial(idTransaccion);

		}
	}
		
	public void updateCertificacionFase(String idTransaccion)  throws Exception{

		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));
		if (this.fase!=null)
			listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.fase.id));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, this.certificacion.id));
		listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_INT, this.adicional == Constantes.TRUE? CertificacionFase.CERTIFICACION_ADICIONAL: CertificacionFase.CERTIFICACION_BASE));
		listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_REAL, this.porcentaje));
		
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("uActualizaCertificacion_fase", listaParms, this, idTransaccion);
		
			
		if (this.parametrosCertificacionFase!=null) {
			Iterator<? extends Parametro> itpf = this.parametrosCertificacionFase.values().iterator();
			while (itpf.hasNext()) {
				Parametro par = itpf.next();
				par.idEntidadAsociada = this.id;
				par.actualizaParametro(idTransaccion, false);
			}
		}
		
		Iterator<CertificacionFaseParcial> itFps = this.certificacionesParciales.iterator();
		while (itFps.hasNext()) {
			CertificacionFaseParcial fps = itFps.next();
			fps.certificacion_fase = this.id;
			if (fps.id == -1)
				fps.insertCertificacionFaseParcial(idTransaccion);
			else
				fps.updateCertificacionFaseParcial(idTransaccion);

		}		
	}
	
	public void reparteCoste(float coste, Tarifa t) {
		Iterator<CertificacionFaseParcial> itcfp = this.certificacionesParciales.iterator();
		while (itcfp.hasNext()) {
			CertificacionFaseParcial cfp = itcfp.next();
			cfp.valEstimado = coste*cfp.porcentaje/100;
			if (t.costeHora!=0)
				cfp.horEstimadas = cfp.valEstimado/t.costeHora;
			else 
				cfp.horEstimadas = 0;
			cfp.tarifaEstimada = t.idTarifa;
		}
	}
	
	public Float calculaCoste() {
		float acumulado = 0;
		
		Iterator<CertificacionFaseParcial> itCfp = this.certificacionesParciales.iterator();
		while (itCfp.hasNext()) {
			CertificacionFaseParcial cfp = itCfp.next();
			
			acumulado += cfp.calculaCoste();
		}
		
		return acumulado;
	}
	
	public void cambiaTipoVCT(TipoCobroVCT tpcv, String idTransaccion) throws Exception{
		Iterator<CertificacionFaseParcial> itTpcv = this.certificacionesParciales.iterator();
		
		boolean igual = true;
		float cantidadRepartir = 0;
		
		while (itTpcv.hasNext()) {
			CertificacionFaseParcial cfp = itTpcv.next();
			cantidadRepartir += cfp.valEstimado;
			
			if (cfp.tarifaReal!=-1) {
				return;
			}
			
			Iterator<Double> itPorc = tpcv.porcentajes.iterator();
			boolean encontrado = false;
			while (itPorc.hasNext()) {
				Double porc = itPorc.next();
				if (cfp.porcentaje - porc == 0) {
					encontrado = true;
					break;
				}
			}
			
			if (!encontrado) {
				igual = false;
			}
		}
		
		if (igual) {
			 return;
		}
		
		ArrayList<CertificacionFaseParcial> aCFP = new ArrayList<CertificacionFaseParcial>();
		
		Iterator<Double> itPorc = tpcv.porcentajes.iterator();
		Date fechaAnterior = null;
		
		if (this.fase!=null)
			fechaAnterior =  this.fase.getFechaImplantacion();
		else {
			fechaAnterior = (Date) (this.certificacion.p.getValorParametro(MetaParametro.PROYECTO_FX_FIN)).getValor();				
		}
		
		int contador = 0;
		
		while (itPorc.hasNext()) {
			Double porc = itPorc.next();
			
			double cantidadFase = porc * cantidadRepartir/100;
			
			CertificacionFaseParcial cfp = new CertificacionFaseParcial();
			cfp.certificacion_fase = this.id;
			cfp.certificacionFase = this;
			cfp.fxCertificacion = fechaAnterior;
			
			Tarifa tAux = this.certificacion.s.getTarifa(); 
			if (tAux!=null) {
				cfp.horEstimadas = new Double(cantidadFase/tAux.costeHora).floatValue();
				cfp.tarifaEstimada = tAux.idTarifa;
			}
					
			cfp.id = -1;
			cfp.nombre = tpcv.nombres.get(contador++);
			cfp.porcentaje = new Double(porc).floatValue();
			cfp.tipoEstimacion = TipoEnumerado.TIPO_PRESUPUESTO_EST;
			cfp.valEstimado = new Double(cantidadFase).floatValue();
			
			aCFP.add(cfp);
			
			fechaAnterior = this.certificacion.calcularFechaPrevia(fechaAnterior);
		}
		
		Iterator<CertificacionFaseParcial> itFps = this.certificacionesParciales.iterator();
		while (itFps.hasNext()) {
			CertificacionFaseParcial fps = itFps.next();
			fps.borraCertificacionFaseParcial(idTransaccion);
		}
		
		this.certificacionesParciales = aCFP;		
		
	}
	
}
