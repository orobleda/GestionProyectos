package ui;

import application.Main;
import controller.Log;
import javafx.scene.control.ButtonType;

public class Dialogo {

	@FunctionalInterface
	public interface Manejador<ButtonType> {
	   public void maneja(ButtonType buttonType);
	}
	
	public static void confirm(String titulo, String contenido, Manejador<ButtonType> m){
		Main.customWorkbench.showConfirmationDialog(titulo, contenido, buttonType -> {
			m.maneja(buttonType);
		});		
		/*
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(titulo);
		alert.setHeaderText(cabecera);
		alert.setContentText(contenido);

		Optional<ButtonType> result = alert.showAndWait();
		
		return result.get();*/
	}
	
	public static void alert(String titulo, String cabecera, String contenido){
		Main.customWorkbench.showInformationDialog(
				  titulo,
				  contenido,
		    buttonType -> { // Proceed and validate the result 
		    	}
		    );
	}
	
    public static void error(String titulo, String cabecera, String contenido){
		Main.customWorkbench.showErrorDialog(
				  titulo,
				  contenido,
		    buttonType -> { // Proceed and validate the result 
		    	}
		    );
	}
    
    public static void error(String descripcion, Exception e){
		if (null == descripcion) {
			descripcion = "Algo fue mal al procesar la petición";
		}    	
    	
    	Main.customWorkbench.showErrorDialog(
    		    "Se produjo un error",
    		    descripcion,
    		    e,
    		    buttonType -> { // Proceed and validate the result 
    		    	}
    		  );
    	
    	Log.e(e);
	}
}
