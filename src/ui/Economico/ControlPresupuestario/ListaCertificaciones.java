package ui.Economico.ControlPresupuestario;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFileChooser;

import org.controlsfx.control.PopOver;

import controller.AnalizadorPresupuesto;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import model.beans.Certificacion;
import model.beans.CertificacionFase;
import model.beans.CertificacionFaseParcial;
import model.beans.Concepto;
import model.beans.Coste;
import model.metadatos.Sistema;
import model.utils.db.ConsultaBD;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Tabla;
import ui.Economico.ControlPresupuestario.EdicionCert.ImportaCertificacion;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteCertResumenFase;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteCertResumenSistema;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteCertificacion;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class ListaCertificaciones implements ControladorPantalla {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/ListaCertificaciones.fxml";
	
	public AnalizadorPresupuesto ap = null;
	
	@FXML
    private TableView<Tableable> tCertificaciones;
	public Tabla tablaCertificaciones;
	
    @FXML
    private TableView<Tableable> tResumenSistemas;
	public Tabla tablaResumenSistemas;

    @FXML
    private TableView<Tableable> tResumenFases;
	public Tabla tablaResumenFases;
	
    @FXML
    private ComboBox<Sistema> cbSistema;

    @FXML
    private ImageView imNuevaCertAdicional;
    private GestionBotones gbNuevaCertAdicional;

    @FXML
    private ImageView imCargaCertificacion;
    private GestionBotones gbCargaCertificacion;
    
	@FXML
	private AnchorPane anchor;
	
    @FXML
    private Label lCertificaciones;
	
   @Override
	public AnchorPane getAnchor() {
		return anchor;
	}
   
   @Override
	public void resize(Scene escena) {
		
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void procesaArchivo() throws Exception{
			if (this.cbSistema.getValue()==null) {
				Dialogo.alert("Sistema no disponible", "Falta el sistema", "Es necesario seleccionar un sistema");
				return;
			}
		
			JFileChooser selectorArchivos = new JFileChooser();
			selectorArchivos.setFileSelectionMode(JFileChooser.FILES_ONLY);

			int resultado = selectorArchivos.showOpenDialog(null);
			
			if (resultado==0) {
				File archivo = selectorArchivos.getSelectedFile();
				
				HashMap<String,Object> variablesPaso = new HashMap<String,Object>();
				variablesPaso.put(ImportaCertificacion.FICHERO,archivo);
				variablesPaso.put(ImportaCertificacion.SISTEMA,cbSistema.getValue());
				variablesPaso.put(ImportaCertificacion.PROYECTO,this.ap.proyecto);
				variablesPaso.put(ImportaCertificacion.CERTIFICACIONES,getCertificaciones());
				
				ImportaCertificacion importarCertificacion = new ImportaCertificacion();
		        FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(importarCertificacion.getFXML()));
		        Pane pane = loader.load();
		        importarCertificacion = loader.getController();
		        importarCertificacion.setParametrosPaso(variablesPaso);	
		        
		        ParamTable.po = new PopOver(pane);
		        ParamTable.po.setTitle("");
		        ParamTable.po.show(this.cbSistema);
		        ParamTable.po.setAnimated(true);
		        ParamTable.po.setAutoHide(true);
			} 
	}
	
	public void initialize(){

		gbCargaCertificacion = new GestionBotones(imCargaCertificacion, "BuscaFichero3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            { 
				try {
					String idTransaccion = ConsultaBD.getTicket();
					procesaArchivo();
										/*
					ConsultaBD.ejecutaTicket(idTransaccion);
					ControlPresupuestario.salvaPosicionActual();
	            	Dialogo.alert("Certificaci�n a�adida", "Certificaci�n a�adida", "Certificaci�n a�adida correctamente");
	            	ControlPresupuestario.cargaPosicionActual();*/
				} catch (Exception ex) {
					ex.printStackTrace();
				}
            } }, "A�adir Certificacion Adicional");
		gbCargaCertificacion.activarBoton();
		
		gbNuevaCertAdicional = new GestionBotones(imNuevaCertAdicional, "NuevaFila3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            { 
				try {
					Certificacion cert = new Certificacion();
					cert = cert.generaCertificacionVacia(cbSistema.getValue(), ap.proyecto );
					String idTransaccion = ConsultaBD.getTicket();
					cert.guardarCertificacion(idTransaccion);
					ap.certificaciones.add(cert);
					ConsultaBD.ejecutaTicket(idTransaccion);
					ControlPresupuestario.salvaPosicionActual();
	            	Dialogo.alert("Certificaci�n a�adida", "Certificaci�n a�adida", "Certificaci�n a�adida correctamente");
	            	ControlPresupuestario.cargaPosicionActual();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
            } }, "A�adir Certificacion Adicional");
		gbNuevaCertAdicional.activarBoton();
		
		lCertificaciones.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t)
            {
            	try {
	            	File file = new File ("C:\\Users\\Oscar\\OneDrive - Enag�s, S.A\\Repositorio\\Evolutivo\\2.5PROTEMP\\Certificaciones\\");
	            	Desktop desktop = Desktop.getDesktop();
	            	desktop.open(file);
            	} catch (Exception e) {
            		Dialogo.error("Error al abrir", "Error al abrir", "Error al abrir la carpeta de certificaciones");
            		e.printStackTrace();
            	}
            }
        });
		
	}
	
	public ArrayList<CertificacionFaseParcial> getCertificaciones() {
		ArrayList<CertificacionFaseParcial> salida = new ArrayList<CertificacionFaseParcial>();
		
		if (ap.certificaciones!=null) {
			Iterator<Certificacion> itCert = ap.certificaciones.iterator();
			while (itCert.hasNext()) {
				Certificacion cert = itCert.next();				
				Iterator<CertificacionFase> itCFase = cert.certificacionesFases.iterator();
				while (itCFase.hasNext()) {
					CertificacionFase certF = itCFase.next();					
					Iterator<CertificacionFaseParcial> itCfp = certF.certificacionesParciales.iterator();
					while (itCfp.hasNext()) {
						CertificacionFaseParcial cfp = itCfp.next();
						salida.add(cfp);
					}
				}
			}
			/*
			itCert = ap.certificaciones.iterator();
			while (itCert.hasNext()) {
				Certificacion cert = itCert.next();
				
				if (cert.isAdicional()) {
					Iterator<Object> itCAux = cList.iterator();
					while (itCAux.hasNext()) {
						Certificacion cAux = (Certificacion) itCAux.next();
						
						if (cAux.s.codigo.equals(cert.s.codigo)) {
							Iterator<CertificacionFase> itCFase = cert.certificacionesFases.iterator();
							while (itCFase.hasNext()) {
								CertificacionFase certF = itCFase.next();
								
								Iterator<CertificacionFase> itCFaseAux = cAux.certificacionesFases.iterator();
								while (itCFaseAux.hasNext()) {
									CertificacionFase certFAux = itCFaseAux.next();
									
									if (certFAux.fase == certF.fase) {
										Iterator<CertificacionFaseParcial> itCfp = certF.certificacionesParciales.iterator();
										while (itCfp.hasNext()) {
											CertificacionFaseParcial cfp = itCfp.next();
											certFAux.concAdicional.valorEstimado += cfp.valEstimado;
											certFAux.concAdicional.valor += cfp.valReal;
											cAux.conceptoAdicional.valorEstimado += cfp.valEstimado;
											cAux.conceptoAdicional.valor += cfp.valReal;
										}
									}
								}
							}
						}
					}
				}
			}*/
		}
		
		return salida;
	}
	
	public void pintaCertificaciones(AnalizadorPresupuesto ap) {
		tablaCertificaciones = new Tabla(tCertificaciones,new LineaCosteCertificacion());
		tablaResumenFases = new Tabla(tResumenFases,new LineaCosteCertResumenFase());
		tablaResumenSistemas = new Tabla(tResumenSistemas,new LineaCosteCertResumenSistema());
		
		this.ap = ap;
		
		Iterator<Coste> itCoste = this.ap.presupuesto.costes.values().iterator();
		while (itCoste.hasNext()) {
			Coste c = itCoste.next();
			this.cbSistema.getItems().add(c.sistema);
		}
		
		ArrayList<Object> cfpList = new ArrayList<Object>();
		ArrayList<Object> cfList = new ArrayList<Object>();
		ArrayList<Object> cList = new ArrayList<Object>();
		
		if (ap.certificaciones!=null) {
			Iterator<Certificacion> itCert = ap.certificaciones.iterator();
			while (itCert.hasNext()) {
				Certificacion cert = itCert.next();
				if (!cert.isAdicional()) {
					cList.add(cert);
					cert.conceptoAdicional = new Concepto();
				}
				
				Iterator<CertificacionFase> itCFase = cert.certificacionesFases.iterator();
				while (itCFase.hasNext()) {
					CertificacionFase certF = itCFase.next();
					if (!certF.adicional) {
						cfList.add(certF);
						certF.concAdicional = new Concepto();
					}
					
					Iterator<CertificacionFaseParcial> itCfp = certF.certificacionesParciales.iterator();
					while (itCfp.hasNext()) {
						CertificacionFaseParcial cfp = itCfp.next();
						cfpList.add(cfp);
					}
				}
			}
			
			itCert = ap.certificaciones.iterator();
			while (itCert.hasNext()) {
				Certificacion cert = itCert.next();
				
				if (cert.isAdicional()) {
					Iterator<Object> itCAux = cList.iterator();
					while (itCAux.hasNext()) {
						Certificacion cAux = (Certificacion) itCAux.next();
						
						if (cAux.s.codigo.equals(cert.s.codigo)) {
							Iterator<CertificacionFase> itCFase = cert.certificacionesFases.iterator();
							while (itCFase.hasNext()) {
								CertificacionFase certF = itCFase.next();
								
								Iterator<CertificacionFase> itCFaseAux = cAux.certificacionesFases.iterator();
								while (itCFaseAux.hasNext()) {
									CertificacionFase certFAux = itCFaseAux.next();
									
									if (certFAux.fase == certF.fase) {
										Iterator<CertificacionFaseParcial> itCfp = certF.certificacionesParciales.iterator();
										while (itCfp.hasNext()) {
											CertificacionFaseParcial cfp = itCfp.next();
											certFAux.concAdicional.valorEstimado += cfp.valEstimado;
											certFAux.concAdicional.valor += cfp.valReal;
											cAux.conceptoAdicional.valorEstimado += cfp.valEstimado;
											cAux.conceptoAdicional.valor += cfp.valReal;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		tablaCertificaciones.pintaTabla(cfpList);
		tablaResumenFases.pintaTabla(cfList);
		tablaResumenSistemas.pintaTabla(cList);
	}
	
}
