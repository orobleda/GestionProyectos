package ui.Economico.ControlPresupuestario.EdicionCert;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.beans.Certificacion;
import model.beans.CertificacionFase;
import model.beans.CertificacionFaseParcial;
import model.beans.Concepto;
import model.beans.FaseProyecto;
import model.beans.Parametro;
import model.beans.ParametroFases;
import model.beans.Proveedor;
import model.beans.Tarifa;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaParametro;
import model.metadatos.TipoCobroVCT;
import model.metadatos.TipoDato;
import model.utils.db.ConsultaBD;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Administracion.Parametricas.GestionParametros;
import ui.Economico.ControlPresupuestario.ControlPresupuestario;
import ui.interfaces.ControladorPantalla;
import ui.popUps.PopUp;

public class EditCertificacionFaseParcial implements ControladorPantalla, PopUp {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/EdicionCert/EditCertificacionFaseParcial.fxml";
	
	public boolean esPopUp = false;
	public static HashMap<String, Object> variablesPaso = null;
	public static Object claseRetorno = null;
	public static String metodoRetorno = null;
	
	public CertificacionFase certificacion = null;
	
	public HashMap<String,Parametro> listaParametros = null;
	
    @FXML
    private TextField tImporte;

    @FXML
    private TextField tHoras;

    @FXML
    private ComboBox<?> cbTarifas;

    @FXML
    private ImageView imGuardar;

    @FXML
    private TextField tFxCertificacion;
	
	@FXML
	private AnchorPane anchor;
	    	
	public EditCertificacionFaseParcial (){
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
	
	public void pintaValores(CertificacionFaseParcial cert) throws Exception{
		
	}
	
	
	@Override
	public String getControlFXML() {
		return EditCertificacionFaseParcial.fxml;
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
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
