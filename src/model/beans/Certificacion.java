package model.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.metadatos.Sistema;
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
	

	
}
