package ui.Economico.ControlPresupuestario.EdicionCert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import controller.AnalizadorPresupuesto;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import model.beans.Certificacion;
import model.beans.CertificacionFase;
import model.beans.CertificacionFaseParcial;
import ui.Tabla;
import ui.Economico.ControlPresupuestario.EdicionCert.Tables.LineaCosteCertificacion;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;
import ui.popUps.PopUp;

public class EdicionCertificacion implements ControladorPantalla, PopUp {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/EdicionCert/EdicionCertificacion.fxml";
	
	public AnalizadorPresupuesto ap = null;
	public boolean esPopUp = false;
	public static HashMap<String, Object> variablesPaso = null;

	public static String metodoRetorno = null;
	
	@FXML
    private TableView<Tableable> tCertificaciones;
	public Tabla tablaCertificaciones;
	
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
	
	public void pintaCertificaciones(Certificacion cert) {
		tablaCertificaciones = new Tabla(tCertificaciones,new LineaCosteCertificacion());
		
		ArrayList<Object> cfpList = new ArrayList<Object>();
		
		Iterator<CertificacionFase> itCFase = cert.certificacionesFases.iterator();
		while (itCFase.hasNext()) {
			CertificacionFase certF = itCFase.next();
			
			Iterator<CertificacionFaseParcial> itCfp = certF.certificacionesParciales.iterator();
			while (itCfp.hasNext()) {
				CertificacionFaseParcial cfp = itCfp.next();
				cfpList.add(cfp);
			}
		}
		
		tablaCertificaciones.pintaTabla(cfpList);
	}

	@Override
	public String getControlFXML() {
		return EdicionCertificacion.fxml;
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		/*ArrayList<Concepto> listaConceptos = new ArrayList<Concepto>();
		listaConceptos.add(((LineaCosteUsuario)variablesPaso.get("filaDatos")).concepto);
		ArrayList<Float> resumen = ((LineaCosteUsuario)variablesPaso.get("filaDatos")).resumen;*/
		EdicionCertificacion.variablesPaso = variablesPaso;		
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
