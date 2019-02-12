package ui.GestionProyectos;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import model.metadatos.EstadoProyecto;
import model.metadatos.TipoProyecto;
import ui.ControladorPantalla;

public class ConsultaProyectos implements ControladorPantalla {
	
	public static final String fxml = "file:src/ui/GestionProyectos/ConsultaProyectos.fxml"; 
	
	@FXML
	private AnchorPane anchor;
	@FXML
	private ComboBox<TipoProyecto> cbTipo;
	@FXML
	private ComboBox<EstadoProyecto> cbEstado;
	
	public ConsultaProyectos(){
	}
	
	public void initialize(){
		cbTipo.getItems().addAll(TipoProyecto.listado.values());
		
		cbTipo.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			informaComboEstado (newValue);
	    	}
	    ); 
		
    }
	
	public void informaComboEstado (TipoProyecto tProy) {
		cbEstado.getItems().clear();
		cbEstado.getItems().addAll(tProy.estados);
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
