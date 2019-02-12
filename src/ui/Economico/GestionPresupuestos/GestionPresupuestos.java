package ui.Economico.GestionPresupuestos;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import ui.ControladorPantalla;

public class GestionPresupuestos implements ControladorPantalla {
	

	public static final String fxml = "file:src/ui/Economico/EstimacionesValoraciones/GestionPresupuestos.fxml";
	
	@FXML
    private TextField tNomProyecto;

    @FXML
    private TableView<?> tCoste;

    @FXML
    private TableView<?> tDemandas;

    @FXML
    private ComboBox<?> cbProyecto;

    @FXML
    private ImageView imGuardarNuevaVersion;

    @FXML
    private TextField tVsProyecto;

    @FXML
    private ImageView imNuevoProyecto;

    @FXML
    private ImageView imAniadirDemanda;

    @FXML
    private ImageView imBuscarVersion;

    @FXML
    private ImageView imGuardar;

    @FXML
    private ComboBox<?> cbVersion;
	
	@FXML
	private AnchorPane anchor;
	
	
	public void initialize(){
		
		
	}
	
	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		return fxml;
	}

}
