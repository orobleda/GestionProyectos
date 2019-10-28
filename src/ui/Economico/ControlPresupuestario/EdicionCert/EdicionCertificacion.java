package ui.Economico.ControlPresupuestario.EdicionCert;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import controller.AnalizadorPresupuesto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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
	
	public CertificacionFaseParcial certificacionFaseParcial = null;
	
	public AnalizadorPresupuesto ap = null;
	public boolean esPopUp = false;
	public static HashMap<String, Object> variablesPaso = null;
	public static Object claseRetorno = null;
	public static String metodoRetorno = null;
	
	public static EdicionCertificacion objetoThis = null;
	
	@FXML
    private TableView<Tableable> tCertificaciones;
	public Tabla tablaCertificaciones;
	
    @FXML
    private HBox hbDetalle;
	
	@FXML
	private AnchorPane anchor;
	
	@Override
	public void resize(Scene escena) {
		
	}
	
	public EdicionCertificacion (Object claseRetorno, String metodoRetorno){
		EdicionCertificacion.objetoThis = this;
		EdicionCertificacion.claseRetorno = claseRetorno;
		EdicionCertificacion.metodoRetorno = metodoRetorno;
	}
	
	public EdicionCertificacion (){
		EdicionCertificacion.objetoThis = this;
	}
	
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
	
	public void elementoSeleccionado(HashMap<String, Object> parametrosPaso) {
		
		if (parametrosPaso.get("columna")== LineaCosteCertificacion.SISTEMA) {
			try {
				EditCertificacion editarCertificacion = new EditCertificacion();
		        FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(editarCertificacion.getFXML()));
		        hbDetalle.getChildren().removeAll(hbDetalle.getChildren());
		        hbDetalle.getChildren().add(loader.load());
		        editarCertificacion = loader.getController();
		        editarCertificacion.variablesPaso = this.variablesPaso;	
		        editarCertificacion.pintaValores(this.certificacionFaseParcial.certificacionFase.certificacion);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (parametrosPaso.get("columna")== LineaCosteCertificacion.FASE ||
				parametrosPaso.get("columna")== LineaCosteCertificacion.PORCENTAJE_ESTIMADO) {
			try {
				CertificacionFase cf = (CertificacionFase) (((LineaCosteCertificacion) parametrosPaso.get("filaDatos")).cfp).certificacionFase;
				
				EditCertificacionFase editarCertificacion = new EditCertificacionFase();
		        FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(editarCertificacion.getFXML()));
		        hbDetalle.getChildren().removeAll(hbDetalle.getChildren());
		        hbDetalle.getChildren().add(loader.load());
		        editarCertificacion = loader.getController();
		        editarCertificacion.variablesPaso = this.variablesPaso;	
		        editarCertificacion.pintaValores(cf);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (
				parametrosPaso.get("columna")== LineaCosteCertificacion.FECHA || 
				parametrosPaso.get("columna")== LineaCosteCertificacion.TIPO_ESTIMACION || 
				parametrosPaso.get("columna")== LineaCosteCertificacion.VALOR_ESTIMADO) {
			try {
				CertificacionFaseParcial cf = (CertificacionFaseParcial) (((LineaCosteCertificacion) parametrosPaso.get("filaDatos")).cfp);
				
				EditCertificacionFaseParcial editarCertificacion = new EditCertificacionFaseParcial();
		        FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(editarCertificacion.getFXML()));
		        hbDetalle.getChildren().removeAll(hbDetalle.getChildren());
		        hbDetalle.getChildren().add(loader.load());
		        editarCertificacion = loader.getController();
		        editarCertificacion.variablesPaso = this.variablesPaso;	
		        editarCertificacion.pintaValores(cf);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (parametrosPaso.get("columna")== LineaCosteCertificacion.VALOR_REAL) {
			try {
				CertificacionFaseParcial cf = (CertificacionFaseParcial) (((LineaCosteCertificacion) parametrosPaso.get("filaDatos")).cfp);
				
				ImportaCertificacion importCertificacion = new ImportaCertificacion();
		        FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(importCertificacion.getFXML()));
		        hbDetalle.getChildren().removeAll(hbDetalle.getChildren());
		        hbDetalle.getChildren().add(loader.load());
		        importCertificacion = loader.getController();
		        parametrosPaso.put(ImportaCertificacion.MODO, ImportaCertificacion.MODO_DETALLE);
		        importCertificacion.setParametrosPaso(parametrosPaso);	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
	}

	@Override
	public String getControlFXML() {
		return EdicionCertificacion.fxml;
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		EdicionCertificacion.variablesPaso = variablesPaso;	
		certificacionFaseParcial = ((ui.Economico.ControlPresupuestario.Tables.LineaCosteCertificacion)variablesPaso.get("filaDatos")).cfp;
		if (this.tCertificaciones!=null)
			pintaCertificaciones(certificacionFaseParcial.certificacionFase.certificacion);
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
