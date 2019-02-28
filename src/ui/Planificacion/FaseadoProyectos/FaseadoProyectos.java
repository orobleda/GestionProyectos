package ui.Planificacion.FaseadoProyectos;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import ui.ControladorPantalla;

public class FaseadoProyectos implements ControladorPantalla {
	

	public static final String fxml = "file:src/ui/Planificacion/FaseadoProyectos/FaseadoProyectos.fxml";
	
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
