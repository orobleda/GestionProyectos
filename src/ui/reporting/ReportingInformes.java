package ui.reporting;

import java.net.URL;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ui.GestionBotones;
import ui.interfaces.ControladorPantalla;

public class ReportingInformes implements ControladorPantalla {

	public static final String fxml = "file:src/ui/reporting/ReportingInformes.fxml"; 
	
	public static final String AREA_ECONOMICO = "Económico";
	
	public static final String INFORME_ECONOMICO_REPORTE_PROYECTOS = "Reporte Proyectos";
	
	@FXML
	private AnchorPane anchor;
	
    @FXML
    private ComboBox<String> cbArea;

    @FXML
    private ComboBox<String> cbInforme;

    @FXML
    private ImageView imBuscar;
    private GestionBotones gbBuscar;

    @FXML
    private VBox vbInforme;
	
        
    @Override
	public void resize(Scene escena) {
		
	}
    
	public ReportingInformes(){
	}
	
	public void initialize(){
		gbBuscar = new GestionBotones(imBuscar, "Buscar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				try {
					cargarInforme();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Cargar Informe", this);	
		gbBuscar.desActivarBoton();
		
		cbArea.getItems().add(ReportingInformes.AREA_ECONOMICO);
		
		cbArea.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
				if (newValue.equals(ReportingInformes.AREA_ECONOMICO)){
					cbInforme.getItems().removeAll(cbInforme.getItems());
					cbInforme.getItems().add(ReportingInformes.INFORME_ECONOMICO_REPORTE_PROYECTOS);
				}
	    	}
	    );
		
		cbInforme.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			try {
				gbBuscar.activarBoton();
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    );
    }
	
	public void cargarInforme() throws Exception{
		if (cbInforme.getValue().equals(ReportingInformes.INFORME_ECONOMICO_REPORTE_PROYECTOS)){
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(new URL((new ReportingInfEcoReportProy()).getFXML()));
	        vbInforme.getChildren().add((loader.load()));
		}
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


