package ui;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class Dialogo {
	
	public static ButtonType confirm(String titulo, String cabecera, String contenido){
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(titulo);
		alert.setHeaderText(cabecera);
		alert.setContentText(contenido);

		Optional<ButtonType> result = alert.showAndWait();
		
		return result.get();
	}
	
	public static void alert(String titulo, String cabecera, String contenido){
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(titulo);
		alert.setHeaderText(cabecera);
		alert.setContentText(contenido);

		alert.showAndWait();
	}
	
    public static void error(String titulo, String cabecera, String contenido){
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(titulo);
		alert.setHeaderText(cabecera);
		alert.setContentText(contenido);

		alert.showAndWait();
	}
}
