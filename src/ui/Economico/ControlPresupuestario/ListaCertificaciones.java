package ui.Economico.ControlPresupuestario;

import java.util.ArrayList;
import java.util.Iterator;

import controller.AnalizadorPresupuesto;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import model.beans.Certificacion;
import model.beans.CertificacionFase;
import model.beans.CertificacionFaseParcial;
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
		
		
	}
	
	public void pintaCertificaciones(AnalizadorPresupuesto ap) {
		tablaCertificaciones = new Tabla(tCertificaciones,new LineaCosteCertificacion());
		tablaResumenFases = new Tabla(tResumenFases,new LineaCosteCertResumenFase());
		tablaResumenSistemas = new Tabla(tResumenSistemas,new LineaCosteCertResumenSistema());
		
		this.ap = ap;
		
		ArrayList<Object> cfpList = new ArrayList<Object>();
		ArrayList<Object> cfList = new ArrayList<Object>();
		ArrayList<Object> cList = new ArrayList<Object>();
		
		Iterator<Certificacion> itCert = ap.certificaciones.values().iterator();
		while (itCert.hasNext()) {
			Certificacion cert = itCert.next();
			cList.add(cert);
			
			Iterator<CertificacionFase> itCFase = cert.certificacionesFases.iterator();
			while (itCFase.hasNext()) {
				CertificacionFase certF = itCFase.next();
				cfList.add(certF);
				
				Iterator<CertificacionFaseParcial> itCfp = certF.certificacionesParciales.iterator();
				while (itCfp.hasNext()) {
					CertificacionFaseParcial cfp = itCfp.next();
					cfpList.add(cfp);
				}
			}
		}
		
		tablaCertificaciones.pintaTabla(cfpList);
		tablaResumenFases.pintaTabla(cfList);
		tablaResumenSistemas.pintaTabla(cList);
	}
	
}
