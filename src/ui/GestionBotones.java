package ui;

import java.util.HashMap;

import com.dlsc.workbenchfx.view.controls.ToolbarItem;

import application.Main;
import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.constantes.Constantes;

public class GestionBotones {
	public static int MODO_SWITCH = 1;
	
	public static int IZQ = 0;
	public static int DER = 1;
	public static int PANT = 2;
	
	ImageView boton = null;
	public Object objetoPadre = null;
	
	public HashMap<Boolean, String> switchBotones;
	boolean valor = false; 
	int modoFuncionamiento = 0;
	
	public boolean liberado = false;
	
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
	
	public GestionBotones(ImageView boton, String nombreBoton, boolean dejarPresionado,EventHandler<MouseEvent> manejador, String textoContextual, int modo) {
		this.modoFuncionamiento = modo;
		switchBotones = new HashMap<Boolean, String>();
		getInstancia(boton, nombreBoton, dejarPresionado,manejador, textoContextual);		
	}
	
	public GestionBotones(int lado, ImageView boton, String nombreBoton, boolean dejarPresionado,EventHandler<MouseEvent> manejador, String nombre) {
		getInstancia(boton, nombreBoton, dejarPresionado,manejador, nombre);
		if (lado != GestionBotones.PANT) {
			ToolbarItem tbi = new ToolbarItem(nombre,this.boton, manejador);
			if (lado == GestionBotones.IZQ) {
				Main.customWorkbench.getToolbarControlsLeft().add(tbi);
			}  else {
				Main.customWorkbench.getToolbarControlsRight().add(tbi);
			}
		}
		
	}
	
	public void getInstancia(ImageView boton, String nombreBoton, boolean dejarPresionado,EventHandler<MouseEvent> manejador, String textoContextual) {
		
		this.boton = boton;
		this.nombreBoton = nombreBoton;
		this.manejador = manejador;	

		boton.setImage(new Image(nombreBoton + "_PAS.png"));
		
		Tooltip t = new Tooltip(textoContextual);
        Tooltip.install(this.boton, t);
		
		boton.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t)
            {
            	if (modoFuncionamiento == GestionBotones.MODO_SWITCH) {
        			valor = !valor;
        		}
            	boton.setImage(new Image(nomBoton() + "_PRES.png"));
            	presionado = !presionado;
            	manejador.handle(t);
            }
        });
		
		boton.setOnMouseEntered(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t)
            {
            	try {
            		if (!presionado) 
            			boton.setImage(new Image(nomBoton() + "_ON.png"));
            	} catch (Exception ex) {
            		ex.printStackTrace();
            		System.out.println(nomBoton() + "_ON.png");
            	}
            }
        });
		
		boton.setOnMouseExited(new EventHandler<MouseEvent>()
	        {
	            @Override
	            public void handle(MouseEvent t)
	            {	
	            	if (!dejarPresionado) {
	            		boton.setImage(new Image(nomBoton() + "_PAS.png"));
	            	} else {
	            		if (!presionado) boton.setImage(new Image(nomBoton() + "_PAS.png"));
	            	}
	            }
	     });
	}
	
	public String nomBoton() {
		if (modoFuncionamiento == GestionBotones.MODO_SWITCH) {
			return switchBotones.get(valor);
		} else return nombreBoton;
	}
	
	public void configuraSwitch(String imagenFalso, String imagenVerdadero) {
		this.switchBotones.put(Constantes.TRUE, imagenVerdadero);
		this.switchBotones.put(Constantes.FALSE, imagenFalso);
	}
	
	public void liberarBoton() {
		try {
			boton.setImage(new Image(nomBoton() + "_PAS.png"));
		} catch (Exception e) {
			System.out.println("No se encuentra el recurso " + nombreBoton + "_PAS.png" + "\n\r" + e.getMessage());
		}
		presionado = false;
		liberado = true;
	}
	
	public void desActivarBoton() {
		try {
			boton.setImage(new Image(nomBoton() + "_DES.png"));
			
		} catch (Exception e) {
			System.out.println("No se encuentra el recurso " + nombreBoton + "_DES.png" + "\n\r" + e.getMessage());
		}
		boton.setMouseTransparent(true);
		presionado = false;
	}
	
	public void activarBoton() {
		try {
			boton.setImage(new Image(nomBoton() + "_PAS.png"));
		} catch (Exception e) {
			System.out.println("No se encuentra el recurso " +nombreBoton + "_PAS.png" + "\n\r" + e.getMessage());
		}		
		boton.setMouseTransparent(false);
		presionado = false;
	}
	
	public void pulsarBoton() {
		try {
			boton.setImage(new Image(nomBoton() + "_PRES.png"));
		} catch (Exception e) {
			System.out.println("No se encuentra el recurso " +nombreBoton + "_PRES.png" + "\n\r" + e.getMessage());
		}
		presionado = true;
		liberado = false;
	}
	
	public void visibleBoton(boolean valor) {
		boton.setVisible(valor);		
	}
}
