package workbench.utils;

import com.dlsc.workbenchfx.view.controls.NavigationDrawer;

import javafx.scene.control.Skin;

public class MenuNavegacion extends NavigationDrawer  {
	
	public MenuNavegacion() {
	    super();
	  }

	  @Override
	  protected Skin<?> createDefaultSkin() {
	    return new MenuNavegacionSkin(this);
	  }

}
