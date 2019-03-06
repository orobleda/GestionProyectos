package ui.planificacion.Faseado;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import ui.interfaces.ControladorPantalla;

public class GestionFases implements ControladorPantalla {
	
	public static final String fxml = "file:src/ui/planificacion/Faseado/GestionFases.fxml";
	
    @FXML
    private HBox hbContenedorFases;

    @FXML
    private TableView<?> tDemandas;

    @FXML
    private ComboBox<?> cbProyecto;

    @FXML
    private TableView<?> tDetalleDemandas;

    @FXML
    private ImageView imGuardar;

    @FXML
    private ImageView imNuevaFase;


	/*
    private GestionBotones gbGuardar;*/

	@FXML
	private AnchorPane anchor;
	
	
	public void initialize(){/*
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					guardarCambios();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Guardar Cambios");
		this.gbGuardar.desActivarBoton();*/
		
        /*cbElemento.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {} );*/
		
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
