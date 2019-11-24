package ui.workbech.modulos.PantallaBlanca;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import ui.Administracion.Parametricas.GestionParametros;
import ui.interfaces.ControladorPantalla;

public class PantallaBlanca implements ControladorPantalla {

	public static final String fxml = "file:src/ui/workbech/modulos/PantallaBlanca/PantallaBlanca.fxml"; 
	
	GestionParametros gestPar = null;
	
	private AnchorPane anchor;
	
	
	
	public PantallaBlanca(){
	}
	
	@Override
	public void resize(Scene escena) {
	}
	
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


