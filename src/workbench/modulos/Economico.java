package workbench.modulos;

import com.dlsc.workbenchfx.model.WorkbenchModule;

import application.Main;
import javafx.scene.Node;
import javafx.scene.image.Image;
import ui.Economico.CargaImputaciones.CargaImputaciones;
import ui.Economico.ControlPresupuestario.ControlPresupuestario;
import ui.Economico.EstimacionesInternas.EstimacionesInternas;
import ui.Economico.EstimacionesValoraciones.EstimacionesValoraciones;
import ui.Economico.GestionPresupuestos.GestionPresupuestos;
import ui.interfaces.ControladorPantalla;

public class Economico extends WorkbenchModule implements Modulo  {
	public static final String NOMBRE_MODULO = "Económico";
	public static final String ICONO_MODULO = "Economico_PAS.png";
	
	public static final Class[] manejadores = {
			EstimacionesValoraciones.class,
			GestionPresupuestos.class,
			ControlPresupuestario.class,
			EstimacionesInternas.class,
			ui.Economico.Tarifas.GestionTarifas.class,
			CargaImputaciones.class
	};
	
	public static final String [] submenu = {
			"Gestión Estimaciones",
			"Gestión Presupuestos",
			"Planificación Económica",
			"Estimaciones Por Horas",
			"Gestión Tarifas",
			"Alta de Imputaciones"
	};
	

	public static final String [] iconosSubmenu = {
			"PequeGestionEstimacion.png",
			"PequeGestionPresupuestos.png",
			"PequePlanificacionEconomica.png",
			"PequeEstimacionesHoras.png",
			"PequeGestionTarifas.png",
			"PequeCargaImputaciones.png"
	};
	
	public int opcion = 0;
	public String nombreModulo = "";
	
	static WorkbenchModule instancia = null;
	
	public Economico() {
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

