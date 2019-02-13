package ui.Economico.GestionPresupuestos;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import ui.ControladorPantalla;

public class AniadeDemanda implements ControladorPantalla {
	

	public static final String fxml = "file:src/ui/Economico/GestionPresupuestos/AniadeDemanda.fxml";
	
	@FXML
	private AnchorPane anchor;
	
    @FXML
    private TextField tImporte;

    @FXML
    private TableView<?> tTablaConceptos;

    @FXML
    private ComboBox<?> cbTarifa;

    @FXML
    private ImageView imGuardarConcepto;

    @FXML
    private ComboBox<?> cbConcepto;

    @FXML
    private TextField tHoras;

    @FXML
    private ImageView imGuardar;

    @FXML
    private ComboBox<?> cbEstimacion;

    @FXML
    private ComboBox<?> cbVersionPres;
	
	
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
