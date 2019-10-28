package ui.Economico.ControlPresupuestario.EdicionCert;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import model.beans.CertificacionFaseParcial;
import model.constantes.FormateadorDatos;
import model.metadatos.TipoDato;
import model.utils.pdf.ProcesaCertificacion;
import ui.Tabla;
import ui.Economico.ControlPresupuestario.EdicionCert.Tables.LineaCosteCertificacionReal;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class AsignacionCertificacion implements ControladorPantalla {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/EdicionCert/AsignacionCertificacion.fxml";
	
    @FXML
    private TableView<Tableable> tCertificaciones;
    public Tabla tablaCertificaciones;

    @FXML
    public ComboBox<CertificacionFaseParcial> cbCertificaciones;
	   
	@FXML
	private AnchorPane anchor;	
	
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
	
	public void initialize(){
		tablaCertificaciones = new Tabla(tCertificaciones,new LineaCosteCertificacionReal(),this);
	}

	public String getControlFXML() {
		return AsignacionCertificacion.fxml;
	}


	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		try {
			CertificacionFaseParcial lcfr = new CertificacionFaseParcial();
			try {
				lcfr.horReal = (Float) FormateadorDatos.parseaDato((String) variablesPaso.get(ProcesaCertificacion.CERTIFICACION_HORAS),TipoDato.FORMATO_REAL);
			} catch (Exception e) {
				lcfr.horReal = ((Double) variablesPaso.get(ProcesaCertificacion.CERTIFICACION_HORAS)).floatValue();
			}
			try {
				lcfr.valReal = (Float) FormateadorDatos.parseaDato((String) variablesPaso.get(ProcesaCertificacion.CERTIFICACION_IMPORTE),TipoDato.FORMATO_REAL);
			} catch (Exception e) {
				lcfr.valReal = ((Double) variablesPaso.get(ProcesaCertificacion.CERTIFICACION_IMPORTE)).floatValue();
			}
			lcfr.nombre = (String) variablesPaso.get(ProcesaCertificacion.CERTIFICACION_DESCRIPCION);
			
			ArrayList<Object> listado = new ArrayList<Object>();
			listado.add(lcfr);
			
			tablaCertificaciones.pintaTabla(listado);
			
			if (variablesPaso.get(ImportaCertificacion.CERTIFICACIONES)!=null)
				this.cbCertificaciones.getItems().addAll((ArrayList<CertificacionFaseParcial>) variablesPaso.get(ImportaCertificacion.CERTIFICACIONES));
			else {
				this.cbCertificaciones.setVisible(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
