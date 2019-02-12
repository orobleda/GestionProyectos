package ui;

import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class GestionBotones {
	ImageView boton = null;
	public Object objetoPadre = null;
	
	String nombreBoton = null;
	
	public boolean presionado = false;
	EventHandler<MouseEvent> manejador = null;
	
	public GestionBotones(ImageView boton, String nombreBoton, boolean dejarPresionado,EventHandler<MouseEvent> manejador, String textoContextual, Object padre) {
		objetoPadre = padre;
		getInstancia(boton, nombreBoton, dejarPresionado,manejador, textoContextual);
	}
	
	public GestionBotones(ImageView boton, String nombreBoton, boolean dejarPresionado,EventHandler<MouseEvent> manejador, String textoContextual) {
		getInstancia(boton, nombreBoton, dejarPresionado,manejador, textoContextual);
	}
	
	public void getInstancia(ImageView boton, String nombreBoton, boolean dejarPresionado,EventHandler<MouseEvent> manejador, String textoContextual) {
		
		this.boton = boton;
		this.nombreBoton = nombreBoton;
		this.manejador = manejador;
		
		Tooltip t = new Tooltip(textoContextual);
        Tooltip.install(this.boton, t);
		
		boton.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t)
            {
            	boton.setImage(new Image(nombreBoton + "_PRES.png"));
            	presionado = !presionado;
            	manejador.handle(t);
            }
        });
		
		boton.setOnMouseEntered(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t)
            {
            	if (!presionado) boton.setImage(new Image(nombreBoton + "_ON.png"));
            }
        });
		
		boton.setOnMouseExited(new EventHandler<MouseEvent>()
	        {
	            @Override
	            public void handle(MouseEvent t)
	            {
	            	if (!dejarPresionado) {
	            		boton.setImage(new Image(nombreBoton + "_PAS.png"));
	            	} else {
	            		if (!presionado) boton.setImage(new Image(nombreBoton + "_PAS.png"));
	            	}
	            }
	     });
	}
	
	public void liberarBoton() {
		try {
			boton.setImage(new Image(nombreBoton + "_PAS.png"));
		} catch (Exception e) {
			System.out.println("No se encuentra el recurso " + nombreBoton + "_PAS.png" + "\n\r" + e.getMessage());
		}
		presionado = false;
	}
	
	public void desActivarBoton() {
		try {
			boton.setImage(new Image(nombreBoton + "_DES.png"));
			
		} catch (Exception e) {
			System.out.println("No se encuentra el recurso " + nombreBoton + "_DES.png" + "\n\r" + e.getMessage());
		}
		boton.setMouseTransparent(true);
		presionado = false;
	}
	
	public void activarBoton() {
		try {
			boton.setImage(new Image(nombreBoton + "_PAS.png"));
		} catch (Exception e) {
			System.out.println("No se encuentra el recurso " +nombreBoton + "_PAS.png" + "\n\r" + e.getMessage());
		}		
		boton.setMouseTransparent(false);
		presionado = false;
	}
	
	public void pulsarBoton() {
		try {
			boton.setImage(new Image(nombreBoton + "_PRES.png"));
		} catch (Exception e) {
			System.out.println("No se encuentra el recurso " +nombreBoton + "_PRES.png" + "\n\r" + e.getMessage());
		}
		presionado = true;
	}
	
	public void visibleBoton(boolean valor) {
		boton.setVisible(valor);		
	}
}
