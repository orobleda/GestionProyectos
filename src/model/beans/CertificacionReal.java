package model.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class CertificacionReal implements Cargable{
	
	public static final int CONSULTA_ID = 1;
	public static final int CONSULTA_NUMSOLI = 2;
	public static final int CONSULTA_TODO = 0;
	public static final int CONSULTA_CERTIF = 3;
	
	public static final int ACCION_CREAR = 0;
	public static final int ACCION_ELIMINAR = 1;
	public static final int ACCION_SUBSTITUIR = 2;
	
	public static ArrayList<CertificacionReal> copiadas = null;
	public CertificacionReal versionPrevia = null;

	public CertificacionFaseParcial certiAsignada = null;
	
	public int modo = -1;
	
	public String descripcion;
	public String descripcionHito;
	public String estado;
	public String tipo;
	public String nSolicitud;
	public String ruta;
	public Double horasHito;
	public Double importeHito;
	public Date fxCertificacion;
	public String pedido;
	public int proveedor;
	public Proveedor prov;
	public int usuario;
	public Recurso recurso;
	public int id;
	public Date fxConsulta;	
	public int idCertiAsignada;	
	public File ficheroTratado;
	
	public CertificacionReal clone() {
		CertificacionReal cr = new CertificacionReal();
		cr.descripcion = this.descripcion;
		cr.descripcionHito = this.descripcionHito;
		cr.estado = this.estado;
		cr.tipo = this.tipo;
		cr.nSolicitud = this.nSolicitud;
		cr.ruta = this.ruta;
		cr.horasHito = this.horasHito;
		cr.importeHito = this.importeHito;
		cr.fxCertificacion = (Date) this.fxCertificacion.clone();
		cr.pedido = this.pedido;
		cr.proveedor = this.proveedor;
		cr.prov = this.prov;
		cr.usuario = this.usuario;
		cr.recurso = this.recurso;
		cr.id = this.id;
		cr.fxConsulta = (Date) this.fxConsulta.clone();
		cr.versionPrevia = null;
		cr.certiAsignada = this.certiAsignada;
		cr.ficheroTratado = this.ficheroTratado;
		
		return cr;
	}
		
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
		 	if (salida.get("creDescripcion")==null)  { 
		 		this.descripcion = null;
			} else {
		 		this.descripcion = (String) salida.get("creDescripcion");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("creDescripcionhito")==null)  { 
		 		this.descripcionHito = null;
			} else {
		 		this.descripcionHito = (String) salida.get("creDescripcionhito");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("creEstado")==null)  { 
		 		this.estado = null;
			} else {
		 		this.estado = (String) salida.get("creEstado");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("creTipo")==null)  { 
		 		this.tipo = null;
			} else {
		 		this.tipo = (String) salida.get("creTipo");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("creNsolicitud")==null)  { 
		 		this.nSolicitud = null;
			} else {
		 		this.nSolicitud = (String) salida.get("creNsolicitud");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("creRuta")==null)  { 
		 		this.ruta = null;
			} else {
		 		this.ruta = (String) salida.get("creRuta");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("creHorashito")==null)  { 
		 		this.horasHito = null;
			} else {
		 		this.horasHito = (Double) salida.get("creHorashito");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("creImportehito")==null)  { 
		 		this.importeHito = null;
			} else {
		 		this.importeHito = (Double) salida.get("creImportehito");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("creFxcertificacion")==null)  { 
		 		this.fxCertificacion = null;
			} else {
		 		this.fxCertificacion = (Date) FormateadorDatos.parseaDato(salida.get("creFxcertificacion").toString(),FormateadorDatos.FORMATO_FECHA);
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("crePedido")==null)  { 
		 		this.pedido = null;
			} else {
		 		this.pedido = (String) salida.get("crePedido");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("creProveedor")==null)  { 
		 		this.proveedor = 0;
			} else {
		 		this.proveedor = (Integer) salida.get("creProveedor");
		 		this.prov = Proveedor.listadoId.get(this.proveedor);
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("creUsuario")==null)  { 
		 		this.usuario = 0;
			} else {
		 		this.usuario = (Integer) salida.get("creUsuario");
		 		this.recurso = Recurso.listadoRecursosEstatico().get(this.usuario);
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("creId")==null)  { 
		 		this.id = 0;
			} else {
		 		this.id = (int) salida.get("creId");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("creFxconsulta")==null)  { 
		 		this.fxConsulta = null;
			} else {
				this.fxConsulta = (Date) FormateadorDatos.parseaDato(salida.get("creFxconsulta").toString(),FormateadorDatos.FORMATO_FECHA);
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("creIdCert")==null)  { 
		 		this.idCertiAsignada = 0;
			} else {
				this.idCertiAsignada = (int) salida.get("creIdCert");
			}
		} catch (Exception ex) {}
		
		return this;
	}
	
	public void AniadirACopiadas() {
		if (CertificacionReal.copiadas== null)
			CertificacionReal.copiadas = new ArrayList<CertificacionReal>();
		
		CertificacionReal.copiadas.add(this);
	}
	
	public ArrayList<CertificacionReal> listado (int flagConsulta, int id, String numSoli) {		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		
		if (CertificacionReal.CONSULTA_ID == flagConsulta)
			listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));
		if (CertificacionReal.CONSULTA_NUMSOLI  == flagConsulta)
			listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_STR, this.nSolicitud));
		if (CertificacionReal.CONSULTA_CERTIF  == flagConsulta)
			listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, this.certiAsignada.id));
				
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> certificaciones = consulta.ejecutaSQL("cConsultaCertificacionReal", listaParms, this);
		
		Iterator<Cargable> itCert = certificaciones.iterator();
		ArrayList<CertificacionReal> salida = new ArrayList<CertificacionReal>();
		
		while (itCert.hasNext()) {
			CertificacionReal p = (CertificacionReal) itCert.next();
			
			
			salida.add(p);
		}
				
		return salida;
	}
	
	public void borraCertificacionReal(String idTransaccion, int flagConsulta)  throws Exception{
		ConsultaBD consulta = new ConsultaBD();
	
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		
		if (CertificacionReal.CONSULTA_ID == flagConsulta)
			listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));
		if (CertificacionReal.CONSULTA_NUMSOLI  == flagConsulta)
			listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_STR, this.nSolicitud));
		if (CertificacionReal.CONSULTA_CERTIF  == flagConsulta)
			listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_STR, this.certiAsignada.id));
		
		
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("dBorraCertificacionReal", listaParms, this, idTransaccion);
		
        if (copiadas==null)
        	copiadas = new ArrayList<CertificacionReal>();
        copiadas.add(this);
	}
	
	public void insertCertificacionReal(String idTransaccion)  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_STR, this.descripcion));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_STR, this.descripcionHito));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_STR, this.estado));
		listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_STR, this.tipo));
		listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_STR, this.nSolicitud));
		listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_STR, this.ruta));
		listaParms.add(new ParametroBD(7, ConstantesBD.PARAMBD_REAL, this.horasHito));
		listaParms.add(new ParametroBD(8, ConstantesBD.PARAMBD_REAL, this.importeHito));
		listaParms.add(new ParametroBD(9, ConstantesBD.PARAMBD_FECHA, this.fxCertificacion));
		listaParms.add(new ParametroBD(10, ConstantesBD.PARAMBD_STR, this.pedido));
		listaParms.add(new ParametroBD(11, ConstantesBD.PARAMBD_INT, this.proveedor));
		listaParms.add(new ParametroBD(12, ConstantesBD.PARAMBD_INT, this.usuario));
		listaParms.add(new ParametroBD(13, ConstantesBD.PARAMBD_ID, this.id ));
		listaParms.add(new ParametroBD(14, ConstantesBD.PARAMBD_FECHA, this.fxConsulta ));
		listaParms.add(new ParametroBD(15, ConstantesBD.PARAMBD_INT, this.certiAsignada.id ));
		
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("iInsertaCertificacionReal", listaParms, this, idTransaccion);
		
		this.id = ParametroBD.ultimoId;
		
        if (copiadas==null)
        	copiadas = new ArrayList<CertificacionReal>();
        copiadas.add(this);
		
	}
	
	public void substituirCertificacion(Proyecto p, CertificacionReal certiOriginal) throws Exception{
		this.copiarCertificacion(p);
		certiOriginal.borrarCertificacion(p);
	}
	
	public String nombreDocumento() throws Exception{
		return this.nSolicitud+" - " + this.descripcion+".pdf";
	}
	
	public boolean copiarCertificacion(Proyecto p) throws Exception{
		File origin = this.ficheroTratado;
        File destination = new File(p.rutaCertificacion()+"\\"+nombreDocumento());
        if (origin.exists() && !destination.exists()) {
                InputStream in = new FileInputStream(origin);
                OutputStream out = new FileOutputStream(destination);
                // We use a buffer for the copy (Usamos un buffer para la copia).
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();                
                this.ruta = destination.getAbsolutePath();
                
                return true;
        } else {
            return false;
        }
	}
	
	public boolean borrarCertificacion(Proyecto p) throws Exception{
        File destination = new File(p.rutaCertificacion()+"\\"+nombreDocumento());
        if (destination.exists()) {
                destination.delete();
                return true;
        } else {
            return false;
        }
	}
		
}
