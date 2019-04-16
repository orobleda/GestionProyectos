package ui.Economico.ControlPresupuestario;

import java.util.ArrayList;
import java.util.Iterator;

import controller.AnalizadorPresupuesto;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.Certificacion;
import model.beans.CertificacionFase;
import model.beans.CertificacionFaseParcial;
import model.beans.Concepto;
import model.beans.Coste;
import model.metadatos.Sistema;
import model.utils.db.ConsultaBD;
import ui.Dialogo;
import ui.GestionBotones;
import ui.Tabla;
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
	private AnchorPane anchor;
	
   @Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		
		
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
	            	Dialogo.alert("Certificación añadida", "Certificación añadida", "Certificación añadida correctamente");
	            	ControlPresupuestario.cargaPosicionActual();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
            } }, "Añadir Certificacion Adicional");
		gbNuevaCertAdicional.activarBoton();
		
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
		
		tablaCertificaciones.pintaTabla(cfpList);
		tablaResumenFases.pintaTabla(cfList);
		tablaResumenSistemas.pintaTabla(cList);
	}
	
}
