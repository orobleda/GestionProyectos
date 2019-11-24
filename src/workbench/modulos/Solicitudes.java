package workbench.modulos;

import com.dlsc.workbenchfx.model.WorkbenchModule;

import application.Main;
import javafx.scene.Node;
import javafx.scene.image.Image;
import ui.GestionProyectos.AltaModProyecto;
import ui.interfaces.ControladorPantalla;

public class Solicitudes extends WorkbenchModule implements Modulo  {
	public static final String NOMBRE_MODULO = "Solicitudes";
	public static final String ICONO_MODULO = "Solicitudes_PAS.png";
	
	public static final Class[] manejadores = {
			AltaModProyecto.class
	};
	
	public static final String [] submenu = {
			"Gestión solicitudes"
	}; 
	
	public static final String [] iconosSubmenu = {
			"PequeGestionSolicitudes.png"
	};
	
	public int opcion = 0;
	public String nombreModulo = "";
	
	static WorkbenchModule instancia = null;
	
	public Solicitudes() {
		super(NOMBRE_MODULO, new Image(ICONO_MODULO)); 
		nombreModulo = NOMBRE_MODULO;
		instancia = this; 
	}
	
    @Override
    public Node activate() {
    	Main.customWorkbench.getToolbarControlsRight().removeAll(Main.customWorkbench.getToolbarControlsRight());
		Main.customWorkbench.getToolbarControlsLeft().removeAll(Main.customWorkbench.getToolbarControlsLeft());
		
    	ControladorPantalla controlPantalla = null;
    	
    	try {
    	   	controlPantalla = (ControladorPantalla) manejadores[opcion].newInstance();
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    		  	  
    	return Main.cargarPantalla(controlPantalla.getFXML());
    }

	@Override
	public Class<ControladorPantalla>[] getManejadores() {
		return manejadores;		
	}

	@Override
	public String [] getSubMenu() {
		return submenu;		
	}
	
	@Override
	public String [] getIconosSubMenu() {
		return iconosSubmenu;		
	}

	@Override
	public String getNombreModulo() {
		return NOMBRE_MODULO;
	}
	
	@Override
	public String getIconoModulo() {
		return ICONO_MODULO;
	}

	@Override
	public void setOpcion(int opcion) {
		this.opcion = opcion;
	}
	    
    
}
