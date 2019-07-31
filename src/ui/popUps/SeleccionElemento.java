package ui.popUps;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.interfaces.Loadable;
import ui.interfaces.ControladorPantalla;

public class SeleccionElemento implements ControladorPantalla, PopUp {
	

	public static final String fxml = "file:src/ui/popUps/SeleccionElemento.fxml";
	
	public static Object claseRetorno = null;
	public static Loadable claseContenida = null;
	public static String metodoRetorno = null;
	public static HashMap<String, Object> variablesPaso = null;
	public static ArrayList<Object> filtro = null;
	
	@FXML
	private ComboBox<Object> cbSistema = null;
	@FXML
	private ImageView imElegirSistema = null;
	
	@FXML
	private AnchorPane anchor;
	
	@Override
	public void resize(Scene escena) {
		
	}
	
	public void initialize(){
		cbSistema.getItems().addAll(claseContenida.getListado().values());
		
		if (SeleccionElemento.filtro!=null) {
			cbSistema.getItems().removeAll(filtro);
		}
		
		imElegirSistema.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 seleccionado(); }	});
	}
	
	public void seleccionado(){
		try {
			Method metodo = claseRetorno.getClass().getDeclaredMethod(metodoRetorno, SeleccionElemento.variablesPaso.getClass() );
			
			SeleccionElemento.variablesPaso.put("seleccionado", cbSistema.getValue());
	
			metodo.invoke(claseRetorno, variablesPaso );
			
			SeleccionElemento.claseContenida = null;
	     	SeleccionElemento.variablesPaso = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public SeleccionElemento (Object claseRetorno, String metodoRetorno, Loadable claseContenida, HashMap<String, Object> variablesPaso){
		SeleccionElemento.claseRetorno = claseRetorno;
		SeleccionElemento.metodoRetorno = metodoRetorno;
		SeleccionElemento.claseContenida = claseContenida;
     	SeleccionElemento.variablesPaso = variablesPaso;
     	SeleccionElemento.filtro = null;
	}
	
	public SeleccionElemento (Object claseRetorno, String metodoRetorno, Loadable claseContenida, HashMap<String, Object> variablesPaso, ArrayList<Object> filtro){
		SeleccionElemento.claseRetorno = claseRetorno;
		SeleccionElemento.metodoRetorno = metodoRetorno;
		SeleccionElemento.claseContenida = claseContenida;
     	SeleccionElemento.variablesPaso = variablesPaso;
     	SeleccionElemento.filtro = filtro;
	}	
	
	public SeleccionElemento (Object claseRetorno, String metodoRetorno, Loadable claseContenida){
		SeleccionElemento.claseRetorno = claseRetorno;
		SeleccionElemento.metodoRetorno = metodoRetorno;
		SeleccionElemento.claseContenida = claseContenida;
	}
	
	public SeleccionElemento (){
	}	
	
	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		return fxml;
	}

	@Override
	public String getControlFXML() {
		return this.getFXML();
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		SeleccionElemento.variablesPaso = variablesPaso;		
	}

	@Override
	public void setClaseContenida(Object claseContenida) {
		SeleccionElemento.claseContenida = (Loadable) claseContenida;		
	}

	@Override
	public boolean noEsPopUp() {
		return false;
	}

	@Override
	public String getMetodoRetorno() {
		return "";
	}

}
