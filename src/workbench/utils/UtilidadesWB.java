package workbench.utils;

import java.util.Iterator;

import com.dlsc.workbenchfx.model.WorkbenchModule;

import application.Main;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import workbench.modulos.Modulo;

public class UtilidadesWB {
	
	public static void insertaNodosMenu(Modulo wbm) {
		Image image = new Image(wbm.getIconoModulo(), 32, 32, true, true);
    	ImageView iv = new ImageView(image);
    	Menu menuItem = new Menu(wbm.getNombreModulo(),iv);
    	Main.customWorkbench.getNavigationDrawerItems().add(menuItem);
    	
    	for (int i=0;i<wbm.getSubMenu().length;i++) {
    		image = new Image(wbm.getIconosSubMenu()[i], 32, 32, true, true);
        	iv = new ImageView(image);
        	
    		MenuItem item = new MenuItem(wbm.getSubMenu()[i],iv);
    		
    		try {	    		
	    		int opcion = i;
	    		
	    		item.setOnAction( event -> {
	        		Iterator<WorkbenchModule> itwbm =  Main.customWorkbench.getModules().iterator();
	        		WorkbenchModule wbmAux = null;
	        		while (itwbm.hasNext()) {
	        			wbmAux = itwbm.next();
	        			if (wbmAux.getClass().getSimpleName().equals(wbm.getClass().getSimpleName()))
	        				break;
	        		}
	        		
	        		if (Main.customWorkbench.getOpenModules().contains(wbmAux)) {
	        			Main.customWorkbench.closeModule(wbmAux);
	        		}
	        		
	        		((Modulo) wbmAux).setOpcion(opcion);
	        		Main.customWorkbench.openModule(wbmAux);
	        	});
	    		
	    		menuItem.getItems().add(item); 
	    		
    		} catch (Exception ex) {
    			ex.printStackTrace();
    		}
    	}	
    }
}
