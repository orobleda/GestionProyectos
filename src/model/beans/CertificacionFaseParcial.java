package model.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.metadatos.TipoDato;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class CertificacionFaseParcial implements Cargable{
	
	public static final int CERTIFICACION_BASE = 0;
	public static final int CERTIFICACION_ADICIONAL = 1;
	
	public float valReal;
	public int certificacion_fase;
	public CertificacionFase certificacionFase;
	public float valEstimado;
	public float tarifaReal;
	public String nombre;
	public int tipoEstimacion;
	public Date fxCertificacion;
	public long tsCertificacion;
	public float horReal;
	public int tarifaEstimada;
	public int id;
	public float horEstimadas;
	public float porcentaje;
	
	public HashMap<String,? extends Parametro> paramCertificacionFaseParcial = null;	
	
	public CertificacionFaseParcial clone() {
		CertificacionFaseParcial fp = new CertificacionFaseParcial();
		fp.valReal = valReal;
		fp.certificacion_fase = certificacion_fase;
		fp.valEstimado = valEstimado;
		fp.tarifaReal = tarifaReal;
		fp.nombre = nombre;
		fp.tipoEstimacion = tipoEstimacion;
		fp.fxCertificacion = fxCertificacion;
		fp.tsCertificacion = tsCertificacion;
		fp.horReal = horReal;
		fp.tarifaEstimada = tarifaEstimada;
		fp.id = id;
		fp.horEstimadas = horEstimadas;
		fp.porcentaje = porcentaje;
		
		if (paramCertificacionFaseParcial!=null) {
			HashMap<String,Parametro> mapAux = new HashMap<String,Parametro>();
			Iterator<? extends Parametro> itParFas = this.paramCertificacionFaseParcial.values().iterator();
			while (itParFas.hasNext()) {
				ParametroCertificacion parCert = (ParametroCertificacion) itParFas.next();
				mapAux.put(parCert.codParametro, parCert.clone());
			}
			
			fp.paramCertificacionFaseParcial = mapAux;
		}
		
		return fp;
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
		 	if (salida.get("cerValreal")==null)  { 
		 		this.valReal = 0;
			} else {
		 		this.valReal = ((Double) salida.get("cerValreal")).floatValue();
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("cerCertificacion_fase")==null)  { 
		 		this.certificacion_fase = 0;
			} else {
		 		this.certificacion_fase = (Integer) salida.get("cerCertificacion_fase");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("cerValestimado")==null)  { 
		 		this.valEstimado = 0;
			} else {
		 		this.valEstimado = ((Double) salida.get("cerValestimado")).floatValue();
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("cerTarifareal")==null)  { 
		 		this.tarifaReal = 0;
			} else {
		 		this.tarifaReal = ((Double) salida.get("cerTarifareal")).floatValue();
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("cerNombre")==null)  { 
		 		this.nombre = null;
			} else {
		 		this.nombre = (String) salida.get("cerNombre");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("cerTipoestimacion")==null)  { 
		 		this.tipoEstimacion = 0;
			} else {
		 		this.tipoEstimacion = (Integer) salida.get("cerTipoestimacion");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("cerFxcertificacion")==null)  { 
		 		this.fxCertificacion = null;
			} else {
		 		this.fxCertificacion = (Date) FormateadorDatos.parseaDato(salida.get("cerFxcertificacion").toString(),FormateadorDatos.FORMATO_FECHA);;
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("cerTscertificacion")==null)  { 
		 		this.tsCertificacion = 0;
			} else {
		 		this.tsCertificacion = ((Double) salida.get("cerTscertificacion")).longValue();
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("cerHorreal")==null)  { 
		 		this.horReal = 0;
			} else {
		 		this.horReal = ((Double) salida.get("cerHorreal")).floatValue();
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("cerTarifaestimada")==null)  { 
		 		this.tarifaEstimada = 0;
			} else {
		 		this.tarifaEstimada = (Integer) salida.get("cerTarifaestimada");
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
		 	if (salida.get("cerHorestimadas")==null)  { 
		 		this.horEstimadas = 0;
			} else {
		 		this.horEstimadas = ((Double) salida.get("cerHorestimadas")).floatValue();
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("cerPorcentaje")==null)  { 
		 		this.porcentaje = 0;
			} else {
		 		this.porcentaje = ((Double) salida.get("cerPorcentaje")).floatValue();
			}
		} catch (Exception ex) {}
		
		return this;
	}
	
	
	public ArrayList<CertificacionFaseParcial> listado () {		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.certificacion_fase));
				
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> fases = consulta.ejecutaSQL("cConsultaCertificacion_fase_parcial", listaParms, this);
		
		Iterator<Cargable> itFase = fases.iterator();
		ArrayList<CertificacionFaseParcial> salida = new ArrayList<CertificacionFaseParcial>();
		
		while (itFase.hasNext()) {
			CertificacionFaseParcial p = (CertificacionFaseParcial) itFase.next();
			
			ParametroCertificacion pf = new ParametroCertificacion();
			p.paramCertificacionFaseParcial = pf.dameParametros(this.getClass().getSimpleName(), p.id);
			
			salida.add(p);
		}
				
		return salida;
	}
	
	public void borraCertificacionFaseParcial(String idTransaccion)  throws Exception{
		
		if (this.paramCertificacionFaseParcial!=null) {
			Iterator<? extends Parametro> itpf = this.paramCertificacionFaseParcial.values().iterator();
			while (itpf.hasNext()) {
				Parametro par = itpf.next();
				par.bajaParametro(idTransaccion);
			}
		}
				
		ConsultaBD consulta = new ConsultaBD();
	
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));
	
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("dBorraCertificacion_fase_parcial", listaParms, this, idTransaccion);
	}
	
	public void insertCertificacionFaseParcial(String idTransaccion)  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_REAL, this.valReal));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.certificacion_fase));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_REAL, this.valEstimado));
		listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_REAL, this.tarifaReal));
		listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_STR, this.nombre));
		listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_INT, this.tipoEstimacion));
		listaParms.add(new ParametroBD(7, ConstantesBD.PARAMBD_FECHA, this.fxCertificacion));
		listaParms.add(new ParametroBD(8, ConstantesBD.PARAMBD_LONG, this.fxCertificacion.getTime()));
		listaParms.add(new ParametroBD(9, ConstantesBD.PARAMBD_REAL, this.horReal));
		listaParms.add(new ParametroBD(10, ConstantesBD.PARAMBD_INT, this.tarifaEstimada));
		listaParms.add(new ParametroBD(11, ConstantesBD.PARAMBD_ID, this.id));
		listaParms.add(new ParametroBD(12, ConstantesBD.PARAMBD_REAL, this.horEstimadas));
		listaParms.add(new ParametroBD(13, ConstantesBD.PARAMBD_REAL, this.porcentaje ));
		
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("iInsertaCertificacion_fase_parcial", listaParms, this, idTransaccion);
		
		this.id = ParametroBD.ultimoId;
		
		if (this.paramCertificacionFaseParcial!=null) {
			Iterator<? extends Parametro> itpf = this.paramCertificacionFaseParcial.values().iterator();
			while (itpf.hasNext()) {
				Parametro par = itpf.next();
				par.idEntidadAsociada = this.id;
				par.actualizaParametro(idTransaccion, false);
			}
		}
	}
		
	public void updateCertificacionFaseParcial(String idTransaccion)  throws Exception{

		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_REAL, this.valReal));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, this.certificacion_fase));
		listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_REAL, this.valEstimado));
		listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_REAL, this.tarifaReal));
		listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_STR, this.nombre));
		listaParms.add(new ParametroBD(7, ConstantesBD.PARAMBD_INT, this.tipoEstimacion));
		listaParms.add(new ParametroBD(8, ConstantesBD.PARAMBD_FECHA, this.fxCertificacion));
		listaParms.add(new ParametroBD(9, ConstantesBD.PARAMBD_LONG, this.fxCertificacion.getTime()));
		listaParms.add(new ParametroBD(10, ConstantesBD.PARAMBD_REAL, this.horReal));
		listaParms.add(new ParametroBD(11, ConstantesBD.PARAMBD_INT, this.tarifaEstimada));
		listaParms.add(new ParametroBD(12, ConstantesBD.PARAMBD_INT, this.id));
		listaParms.add(new ParametroBD(13, ConstantesBD.PARAMBD_REAL, this.horEstimadas));
		listaParms.add(new ParametroBD(14, ConstantesBD.PARAMBD_REAL, this.porcentaje ));
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));

		consulta = new ConsultaBD();
		consulta.ejecutaSQL("uActualizaCertificacion_fase_parcial", listaParms, this, idTransaccion);
		
		
		if (this.paramCertificacionFaseParcial!=null) {
			Iterator<? extends Parametro> itpf = this.paramCertificacionFaseParcial.values().iterator();
			while (itpf.hasNext()) {
				Parametro par = itpf.next();
				par.idEntidadAsociada = this.id;
				par.actualizaParametro(idTransaccion, false);
			}
		}
				
	}
	
	public Float calculaCoste() {
		return this.valEstimado;
	}
	
}
