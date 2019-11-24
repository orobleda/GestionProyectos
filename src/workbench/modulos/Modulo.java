package workbench.modulos;

import ui.interfaces.ControladorPantalla;

public interface Modulo  {
	public Class<ControladorPantalla>[] getManejadores();
	public String [] getSubMenu();
	public String [] getIconosSubMenu();
	public String getNombreModulo();
	public String getIconoModulo();
	public void setOpcion(int opcion);
}
