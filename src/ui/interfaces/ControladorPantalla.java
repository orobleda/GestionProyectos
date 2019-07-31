package ui.interfaces;

import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;

public interface ControladorPantalla {
	public AnchorPane getAnchor();
	public String getFXML();
	public void resize(Scene escena);
}
