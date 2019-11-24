package ui.PanelPrincipal;

import java.net.URL;

import org.controlsfx.control.PopOver;

import com.dlsc.workbenchfx.model.WorkbenchModule;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import ui.PanelPrincipal.Visores.VisorPlanificacion;
import ui.interfaces.ControladorPantalla;

public class PanelPrincipal  implements ControladorPantalla  {
	public static final String fxml = "file:src/ui/PanelPrincipal/PanelPrincipal.fxml";
	
	PopOver popUp = null;
	
	@FXML
	private AnchorPane anchor;	

    @FXML
    private Pane pnVisor;
	
	@Override
	public void resize(Scene escena) {
		if (escena!=null && pnVisor!=null) {
			pnVisor.setMaxHeight(escena.getHeight());
			pnVisor.setMaxWidth(escena.getWidth());
		}
	}
	
	public void initialize(){
		ControladorPantalla visorPlanificacion = new VisorPlanificacion();
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(new URL(visorPlanificacion.getFXML()));
			pnVisor.getChildren().add(loader.load());
			//visorPlanificacion = (AniadeDemanda) loader.getController();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		// TODO Auto-generated method stub
		return fxml;
	}

		
}
