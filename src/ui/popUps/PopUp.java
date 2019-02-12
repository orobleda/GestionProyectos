package ui.popUps;

import java.util.HashMap;

public interface PopUp {
	public String getControlFXML();
	public void setParametrosPaso(HashMap<String, Object> variablesPaso);
	public void setClaseContenida(Object claseContenida);
	public boolean noEsPopUp();
	public String getMetodoRetorno();
}
