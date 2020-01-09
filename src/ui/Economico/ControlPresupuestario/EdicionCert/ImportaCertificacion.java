package ui.Economico.ControlPresupuestario.EdicionCert;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import application.Main;
import controller.Log;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.beans.Certificacion;
import model.beans.CertificacionFase;
import model.beans.CertificacionFaseParcial;
import model.beans.CertificacionReal;
import model.beans.ParametroProyecto;
import model.beans.Proveedor;
import model.beans.Proyecto;
import model.beans.Recurso;
import model.beans.Tarifa;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import model.metadatos.TipoDato;
import model.utils.db.ConsultaBD;
import model.utils.pdf.ProcesaCertificacion;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Economico.ControlPresupuestario.ControlPresupuestario;
import ui.Economico.ControlPresupuestario.EdicionCert.Tables.LineaCosteCertificacion;
import ui.interfaces.ControladorPantalla;
import ui.popUps.PopUp;

public class ImportaCertificacion implements ControladorPantalla, PopUp {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/EdicionCert/ImportaCertificacion.fxml";
	
	public static final String FICHERO = "FICHERO";
	public static final String SISTEMA = "SISTEMA";
	public static final String PROYECTO = "PROYECTO";
	public static final String CERTIFICACIONES = "CERTIFICACIONES";
	
	public static final int MODO_DETALLE = 0;
	public static final int MODO_CARGA = 1;
	public static final String MODO = "MODO";
	
	public int modo = -1;
	
	public boolean esPopUp = false;
	public static HashMap<String, Object> variablesPaso = null;
	public static Object claseRetorno = null;
	public static String metodoRetorno = null;	
	
	public ArrayList<AsignacionCertificacion> listaHitos = new ArrayList<AsignacionCertificacion>();
	
    @FXML
    private TextField tProyecto;

    @FXML
    private TextField tTpCertificacion;

    @FXML
    private TextField tEstCertificacion;

    @FXML
    private TextField tPedido;

    @FXML
    private ComboBox<Proveedor> cbProveedor;

    @FXML
    private ComboBox<Recurso> cbRecurso;

    @FXML
    private TextField tFxCertificacion;

    @FXML
    private Label lNombreFichero;    

    @FXML
    private VBox vbLineas;

    @FXML
    private ImageView imCargaCertificacion;
    private GestionBotones gbCargaCertificacion;
	
    @FXML
    private TextField tfnombreCertif;
    
    @FXML
    private TextField tNumSoli;
    
    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;
    
    @FXML
    private ImageView imBorrar;
    private GestionBotones gbBorrar;
    
    @FXML
    private ComboBox<Tarifa> cbTarifa;

    @FXML
    private ImageView imAltaTarifa;
    private GestionBotones gbAltaTarifa;
    
    private File ficheroBuscado = null;
    private Sistema sist = null;
    private Proyecto p = null;
    private ArrayList<CertificacionFaseParcial> lcfp = null;
    CertificacionFaseParcial cfpAsignada = null;
    CertificacionReal crAsignada = null;
	   
	@FXML
	private AnchorPane anchor;	
	
   @Override
	public AnchorPane getAnchor() {
		return anchor;
	}
   
   @Override
	public void resize(Scene escena) {
	   int res = Main.resolucion();
		
		if (res == Main.ALTA_RESOLUCION ) {
			lNombreFichero.setPrefWidth(Main.scene.getWidth()*0.5);
		}
		
		if (res== Main.BAJA_RESOLUCION) {
			lNombreFichero.setMinWidth(Main.scene.getWidth()*0.7);
		}
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		gbCargaCertificacion = new GestionBotones(imCargaCertificacion, "Buscar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            { 
				try {
					procesaArchivo();
				} catch (Exception ex) {
					Dialogo.error(null, ex);
				}
            } }, "Procesa Certificacion");
		gbCargaCertificacion.activarBoton();
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            { 
				try {					
					ControlPresupuestario.salvaPosicionActual();
					guardarCertificacion();
					ParamTable.po.hide();
	            	Dialogo.alert("Certificación añadida", "Certificación añadida", "Certificación añadida correctamente");
	            	ControlPresupuestario.cargaPosicionActual();
				} catch (Exception ex) {
					Dialogo.error(null, ex);
				}
            } }, "Guarda Certificacion");
		gbGuardar.desActivarBoton();
		gbBorrar = new GestionBotones(imBorrar, "Eliminar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            { 
				try {
					ControlPresupuestario.salvaPosicionActual();
					confirmaBorrarCertificacion();
	            	Dialogo.alert("Certificación borrada", "Certificación borrada", "Certificación borrada correctamente");
	            	ControlPresupuestario.cargaPosicionActual();
				} catch (Exception ex) {
					Dialogo.error(null, ex);
				}
            } }, "Elimina Certificacion");
		gbBorrar.desActivarBoton();
		gbAltaTarifa = new GestionBotones(imAltaTarifa, "AltaTarifa3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            { 
				try {
					altaTarifa();
				} catch (Exception ex) {
					Dialogo.error(null, ex);
				}
            } }, "Alta nueva Tarifa");
		gbAltaTarifa.desActivarBoton();
	}

	@Override
	public String getControlFXML() {
		return ImportaCertificacion.fxml;
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		ImportaCertificacion.variablesPaso = variablesPaso;
		
		this.modo = (Integer) variablesPaso.get(ImportaCertificacion.MODO);
		
		if (this.modo == ImportaCertificacion.MODO_CARGA)
			modoCarga(variablesPaso);
		
		if (this.modo == ImportaCertificacion.MODO_DETALLE)
			modoDetalle(variablesPaso);
		
		resize(null);
	}
	
	public void modoDetalle(HashMap<String, Object> variablesPaso) {
		gbBorrar.activarBoton();
		LineaCosteCertificacion lcf = (LineaCosteCertificacion) variablesPaso.get("filaDatos");
		cbProveedor.getItems().addAll(Proveedor.listado.values());
		cbRecurso.getItems().addAll(Recurso.listadoRecursosEstatico().values());
		
		CertificacionFaseParcial cfp = lcf.cfp;
		cfpAsignada = cfp;
		
		CertificacionReal cr = new CertificacionReal();
		cr.certiAsignada = cfp;
		ArrayList<CertificacionReal> lcr = cr.listado(CertificacionReal.CONSULTA_CERTIF, 0, null);
		this.gbCargaCertificacion.desActivarBoton();
		
		if (lcr.size()==0) {
			this.lNombreFichero.setText("No existe certificacion asociada");
			return;
		}
		
		cr = lcr.get(0);
		crAsignada = cr;
		
		this.tfnombreCertif.setText(cr.descripcion);
		this.tNumSoli.setText(cr.nSolicitud);
		this.tProyecto.setText(cfp.certificacionFase.certificacion.p.nombre);
		this.tTpCertificacion.setText(cr.tipo);
		this.tEstCertificacion.setText(cr.estado);
		this.tPedido.setText(cr.pedido);
		
		this.cbProveedor.setValue(cr.prov);
		this.cbRecurso.setValue(cr.recurso);
		
		try{
			this.lNombreFichero.setText(cfp.certificacionFase.certificacion.p.rutaCertificacion()+"\\"+cr.ruta);
			
			lNombreFichero.setOnMouseClicked(new EventHandler<MouseEvent>()
	        {
	            @Override
	            public void handle(MouseEvent t)
	            {
	            	try {
		            	File file = new File (lNombreFichero.getText());
		            	Desktop desktop = Desktop.getDesktop();
		            	desktop.open(file);
	            	} catch (Exception e) {
						Dialogo.error("Error al abrir la carpeta de certificaciones", e);
	            	}
	            }
	        });
			
			this.tFxCertificacion.setText(FormateadorDatos.formateaDato(cr.fxCertificacion, TipoDato.FORMATO_FECHA));
			
			Iterator<CertificacionReal> itO = lcr.iterator();
			listaHitos = new ArrayList<AsignacionCertificacion>();
			float horas = 0;
			float importe = 0;
				
			while (itO.hasNext()) {
				cr = itO.next();
				HashMap<String,Object> varPaso = new HashMap<String,Object>();
				varPaso.put(ImportaCertificacion.CERTIFICACIONES,null);
				varPaso.put(ProcesaCertificacion.CERTIFICACION_HORAS, cr.horasHito);
				varPaso.put(ProcesaCertificacion.CERTIFICACION_DESCRIPCION, cr.descripcionHito);
				varPaso.put(ProcesaCertificacion.CERTIFICACION_IMPORTE, cr.importeHito);
				
				horas = new Float(cr.horasHito);
				importe = new Float(cr.importeHito);
				
				AsignacionCertificacion aCertificacion = new AsignacionCertificacion();
		        FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(aCertificacion.getFXML()));
		        Pane pane = loader.load();
		        aCertificacion = loader.getController();
		        aCertificacion.setParametrosPaso(varPaso);
		        listaHitos.add(aCertificacion);
		        
		        this.vbLineas.getChildren().add(pane);
			}
			
			cargaTarifa(horas, importe, cr.prov);
		} catch (Exception ex) {
			Dialogo.error(null, ex);
		}
		
		this.gbCargaCertificacion.desActivarBoton();
		this.gbAltaTarifa.desActivarBoton();
	}
	
	public void cargaTarifa(double horas, double importe, Proveedor prov) {
		Tarifa t = new Tarifa();
		
		HashMap<String,Object> filtros = new HashMap<String,Object>();
		filtros.put(Tarifa.filtro_PROVEEDOR, prov);
		
		ArrayList<Tarifa> lrt = t.listado(filtros);
		
		this.cbTarifa.getItems().addAll(lrt);
		
		if (horas!=0 && lrt.size()>0) {
			double tarifa = importe/horas;
			
			Iterator<Tarifa> irrt = lrt.iterator();
			while (irrt.hasNext()) {
				Tarifa rrt = irrt.next();
				if (tarifa == rrt.costeHora){
					this.cbTarifa.setValue(rrt);
					break;
				}
			}
		}
		
		if (this.cbTarifa.getValue()==null) {
			this.gbAltaTarifa.activarBoton();
		} else {
			this.gbAltaTarifa.desActivarBoton();
		}		
	}
			
	public void modoCarga(HashMap<String, Object> variablesPaso) {	
		this.ficheroBuscado = (File) variablesPaso.get(ImportaCertificacion.FICHERO);
		this.sist = (Sistema) variablesPaso.get(ImportaCertificacion.SISTEMA);
		this.p =  (Proyecto) variablesPaso.get(ImportaCertificacion.PROYECTO);
		this.lcfp = (ArrayList<CertificacionFaseParcial>) variablesPaso.get(ImportaCertificacion.CERTIFICACIONES);
		
		this.lNombreFichero.setText(this.ficheroBuscado.getAbsolutePath());
		
		lNombreFichero.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t)
            {
            	try {
	            	File file = new File (ficheroBuscado.getAbsolutePath());
	            	Desktop desktop = Desktop.getDesktop();
	            	desktop.open(file);
            	} catch (Exception e) {
					Dialogo.error( "Error al abrir la carpeta de certificaciones", e);
            	}
            }
        });
		
		
		cbProveedor.getItems().addAll(Proveedor.listado.values());
		cbRecurso.getItems().addAll(Recurso.listadoRecursosEstatico().values());
	}
	
	public void altaTarifa() throws Exception{
		Tarifa t = new Tarifa();
		
		AsignacionCertificacion ac = listaHitos.get(0);
		float horas = ((CertificacionFaseParcial) ac.tablaCertificaciones.listaDatosEnBruto.get(0)).horReal;
		float importe = ((CertificacionFaseParcial) ac.tablaCertificaciones.listaDatosEnBruto.get(0)).valReal;
		float tarifa = 1;
		
		if (horas!=0) {
			tarifa = importe/horas;
		}
		
		Date fecha = (Date) FormateadorDatos.parseaDato(this.tFxCertificacion.getText(), TipoDato.FORMATO_FECHA);
		Calendar cFecha = Calendar.getInstance();
		cFecha.setTime(fecha);
		
		t = t.tarifaPorCoste(true, tarifa);
		t.proveedor = this.cbProveedor.getValue();
		t.nomTarifa = this.cbProveedor.getValue().nomCorto + " - " + tarifa;
		t.fInicioVig = Constantes.inicioAnio(cFecha.get(Calendar.YEAR));
		t.fFinVig = Constantes.finAnio(cFecha.get(Calendar.YEAR));
		
		if (t.idTarifa == -1) {
			t.insertTarifa();
		}
		
		Tarifa tar = new Tarifa();
		
		HashMap<String,Object> filtros = new HashMap<String,Object>();
		filtros.put(Tarifa.filtro_PROVEEDOR, this.cbProveedor.getValue());
		
		ArrayList<Tarifa> lrt = tar.listado(filtros);
		
		this.cbTarifa.getItems().removeAll(this.cbTarifa.getItems());
		this.cbTarifa.getItems().addAll(lrt);
	}
	
	public void procesaArchivo() {
		
		ProcesaCertificacion pc = new ProcesaCertificacion();
		HashMap<String,Object> procesada = pc.procesa(this.ficheroBuscado.getAbsolutePath());
		
		try {
			this.tfnombreCertif.setText((String) procesada.get(ProcesaCertificacion.DESCRIPCION));
			this.tNumSoli.setText((String) procesada.get(ProcesaCertificacion.SOLICITUD));
			this.tProyecto.setText((String) procesada.get(ProcesaCertificacion.PROYECTO));
			this.tTpCertificacion.setText((String) procesada.get(ProcesaCertificacion.TIPO));
			this.tEstCertificacion.setText((String) procesada.get(ProcesaCertificacion.ESTADO));
			this.tPedido.setText((String) procesada.get(ProcesaCertificacion.PEDIDO));
			
			Iterator<Proveedor> itProv = this.cbProveedor.getItems().iterator();
			Proveedor prov = null;
			while (itProv.hasNext()) {
				Proveedor prov2 = itProv.next();
				if (prov2.identificaPorPPM(((String) procesada.get(ProcesaCertificacion.PROVEEDOR)))){
					prov = prov2;
					break;
				}
			}
			if (prov!=null) this.cbProveedor.setValue(prov);
			
			Iterator<Recurso> itRec = this.cbRecurso.getItems().iterator();
			Recurso rec = null;
			while (itRec.hasNext()) {
				Recurso rec2 = itRec.next();
				if (rec2.identificaPorPPM(((String) procesada.get(ProcesaCertificacion.CREADOR)))){
					rec = rec2;
					break;
				}
			}
			if (rec!=null) this.cbRecurso.setValue(rec);
			
			this.tFxCertificacion.setText((String) procesada.get(ProcesaCertificacion.FECHA_CERTIFICACION));
			
			Proyecto p = new Proyecto();
			p = p.getProyectoPPM((String) procesada.get(ProcesaCertificacion.PROYECTO), false);
			float horas = 0;
			float importe = 0;
			
			if (p==null || this.p.id == p.id) {
				Iterator<HashMap<String,String>> itO = ((HashMap<String,HashMap<String,String>>) procesada.get(ProcesaCertificacion.HITOS)).values().iterator();
				listaHitos = new ArrayList<AsignacionCertificacion>();
				
				while (itO.hasNext()) {
					HashMap<String,String> el = itO.next();
					HashMap<String,Object> varPaso = new HashMap<String,Object>();
					varPaso.put(ImportaCertificacion.CERTIFICACIONES,this.lcfp);
					Iterator<String> elIt = el.keySet().iterator();
					while (elIt.hasNext()) {
						String key = elIt.next();
						varPaso.put(key, el.get(key));
					}
					
					horas = (Float) FormateadorDatos.parseaDato((String) varPaso.get(ProcesaCertificacion.CERTIFICACION_HORAS), TipoDato.FORMATO_REAL);
					importe = (Float) FormateadorDatos.parseaDato((String) varPaso.get(ProcesaCertificacion.CERTIFICACION_IMPORTE), TipoDato.FORMATO_REAL);
					
					AsignacionCertificacion aCertificacion = new AsignacionCertificacion();
			        FXMLLoader loader = new FXMLLoader();
			        loader.setLocation(new URL(aCertificacion.getFXML()));
			        Pane pane = loader.load();
			        aCertificacion = loader.getController();
			        aCertificacion.setParametrosPaso(varPaso);
			        listaHitos.add(aCertificacion);
			        
			        this.vbLineas.getChildren().add(pane);
				}
				
				this.gbCargaCertificacion.desActivarBoton();
				this.gbGuardar.activarBoton();
			} 
			
			cargaTarifa(horas,importe,prov); 
			
		} catch (Exception e) {
			Dialogo.error(null, e);
		}
		
	}
	
	public void confirmaBorrarCertificacion(){
		Dialogo.confirm("Borrado de certificación", "¿Desea actualizar la cantidad imputada?", 
				new Dialogo.Manejador<ButtonType>() {			
			@Override
			public void maneja(ButtonType buttonType) {
				borrarCertificacion(buttonType);				
			}
		});
	}
		
	public void borrarCertificacion(ButtonType buttonType){
		try {
			boolean eliminarCantidades = (buttonType.equals(ButtonType.YES));
			
			String idTransaccion = ConsultaBD.getTicket();
			crAsignada.borraCertificacionReal(idTransaccion, CertificacionReal.CONSULTA_NUMSOLI);
			CertificacionReal.copiadas = new ArrayList<CertificacionReal>();
			CertificacionReal.copiadas.add(crAsignada);
			crAsignada.modo = CertificacionReal.ACCION_ELIMINAR;
			
			if (eliminarCantidades) {
				ArrayList<CertificacionReal> lcr = crAsignada.listado(CertificacionReal.CONSULTA_NUMSOLI, 0, crAsignada.nSolicitud);
				
				ArrayList<Certificacion> lCert = new ArrayList<Certificacion>();
				
				Iterator<Certificacion> itCerts = ControlPresupuestario.ap.certificaciones.iterator();
				while (itCerts.hasNext()) {
					Certificacion cert = itCerts.next();
					Iterator<CertificacionFase> itCF = cert.certificacionesFases.iterator();
					while (itCF.hasNext()) {
						CertificacionFase cf = itCF.next();
						Iterator<CertificacionFaseParcial> itcfp = cf.certificacionesParciales.iterator();
						while (itcfp.hasNext()) {
							CertificacionFaseParcial cfp = itcfp.next();
							
							Iterator<CertificacionReal> iCr = lcr.iterator();
							while (iCr.hasNext()) {
								CertificacionReal cr = iCr.next();
								if (cr.idCertiAsignada == cfp.id) {
									cfp.horReal = 0;
									cfp.valReal = 0;
									if (!lCert.contains(cert)) {
										lCert.add(cert);
									}
								}
							}
						}
					}
				}
				
				itCerts = lCert.iterator();
				while (itCerts.hasNext()) {
					Certificacion cert = itCerts.next();
					cert.guardarCertificacion(null);
				}
			}
			
			ConsultaBD.ejecutaTicket(idTransaccion);
			
			Certificacion c = new Certificacion();
			c.gestionCertificacionesReales(this.cfpAsignada.certificacionFase.certificacion.p);
		} catch (Exception e) {
			Log.e(e);
		}
	}
	
	public void guardarCertificacion() throws Exception{
		ArrayList<Certificacion> certs = new ArrayList<Certificacion>();
		
		CertificacionReal cfp = new CertificacionReal();
		cfp.descripcion = this.tfnombreCertif.getText();
		cfp.estado = this.tEstCertificacion.getText();
		cfp.fxCertificacion = (Date) FormateadorDatos.parseaDato(this.tFxCertificacion.getText(), TipoDato.FORMATO_FECHA);
		cfp.fxConsulta = new Date();
		cfp.id = -1;
		cfp.nSolicitud = this.tNumSoli.getText();
		cfp.pedido = this.tPedido.getText();
		cfp.prov = this.cbProveedor.getValue();
		cfp.proveedor = cfp.prov.id;
		cfp.recurso = this.cbRecurso.getValue();
		cfp.ruta = cfp.nombreDocumento();
		cfp.tipo = this.tTpCertificacion.getText();
		cfp.usuario = cfp.recurso.id;
		cfp.ficheroTratado = ficheroBuscado;
		
		Iterator<AsignacionCertificacion> itAsign = this.listaHitos.iterator();
		while (itAsign.hasNext()) {
			AsignacionCertificacion ac = itAsign.next();
			
			if (this.cbTarifa.getValue()==null) {
				Dialogo.error("Tarifa pendiente", "Tarifa pendiente", "Es necesario validar la tarifa de la certificación");
				return;
			}
			
			if (ac.cbCertificaciones.getValue()==null) {
				Dialogo.error("Certificaciones pendientes", "Certificaciones pendientes", "Falta por asignar alguna línea de la certificación");
				return;
			}
			
			CertificacionFaseParcial cfparc = ac.cbCertificaciones.getValue();
			CertificacionReal cfpAux = cfp.clone();
			cfpAux.certiAsignada = cfparc;
			if (cfparc.certReal==null) {
				cfparc.certReal = new ArrayList<CertificacionReal>();
			}
			cfparc.certReal.add(cfpAux);
			
			CertificacionFaseParcial valoresHito = (CertificacionFaseParcial) ac.tablaCertificaciones.listaDatosEnBruto.get(0);
			
			cfpAux.importeHito = new Float(valoresHito.valReal).doubleValue();
			cfpAux.horasHito = new Float(valoresHito.horReal).doubleValue();
			cfpAux.descripcionHito = valoresHito.nombre;
			
			cfparc.horReal += cfpAux.horasHito;
			cfparc.valReal += cfpAux.importeHito;
			cfparc.fxCertificacion = cfpAux.fxCertificacion;
			
			try {
				Calendar calCertif = Calendar.getInstance();
				calCertif.setTime(cfpAux.fxCertificacion);
				
				ParametroProyecto pp = cfparc.certificacionFase.certificacion.p.getValorParametro(MetaParametro.PROYECTO_FX_FIN); 
				Date finProy = (Date) pp.getValor();
				Calendar calProy = Calendar.getInstance();
				calProy.setTime(finProy);
				
				if (calProy.before(calCertif)) {
					pp.valorfecha = cfpAux.fxCertificacion;
					pp.actualizaParametro("", true);
				}
				
			} catch (Exception ex) {
				Dialogo.error(null, ex);
			}
				
			if (!certs.contains(cfparc.certificacionFase.certificacion)) {
				certs.add(cfparc.certificacionFase.certificacion);
			}
		}
		
		Iterator<Certificacion> itCerts = certs.iterator();
		while (itCerts.hasNext()) {
			Certificacion c = itCerts.next();
			c.guardarCertificacion(null);
		}

	}

	@Override
	public void setClaseContenida(Object claseContenida) {
	}

	@Override
	public boolean noEsPopUp() {
		if (!esPopUp) return false;
		else return true;
	}

	@Override
	public String getMetodoRetorno() {
		return metodoRetorno;
	}
	

	
}
