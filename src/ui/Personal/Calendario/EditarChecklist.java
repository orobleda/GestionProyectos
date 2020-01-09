package ui.Personal.Calendario;

import java.util.ArrayList;
import java.util.HashMap;

import application.Main;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import model.beans.Proyecto;
import ui.VentanaContextual;
import ui.interfaces.ControladorPantalla;
import ui.popUps.PopUp;

public class EditarChecklist implements ControladorPantalla, PopUp {

	public static final String fxml = "file:src/ui/Personal/Calendario/DetalleEvento.fxml"; 
	
	public static final String PADRE = "PADRE"; 
	
	public VentanaContextual vc = null;
	public DetalleEvento padre = null;
		   
    @Override
	public void resize(Scene escena) {
    	//scrDetalles.setPrefHeight(Main.scene.getHeight()*0.9);
    	
		if (Main.resolucion() == Main.BAJA_RESOLUCION) {
			
		}
	}
    
	public EditarChecklist(){
	}
	
	public void initialize(){
		
    }
		
	private void consultaAvanzadaProyectos() throws Exception{		
		//ConsultaAvanzadaProyectos.getInstance(padre.vc,this, 1000, TipoProyecto.ID_TODO, this.lProyectos);
	}
	
	public void fijaProyecto(ArrayList<Proyecto> listaProyecto) {
		if (listaProyecto!=null) {
			//this.lProyectos.getItems().removeAll(this.lProyectos.getItems());
			//this.lProyectos.getItems().addAll(listaProyecto);
			//padre.vc.showAtras();
		}
	}
	
	@Override
	public AnchorPane getAnchor() {
		return null;
	}

	@Override
	public String getFXML() {
		return fxml;
	}

	@Override
	public String getControlFXML() {
		return fxml;
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		padre = (DetalleEvento) variablesPaso.get(EditarChecklist.PADRE);
		
		resize(null);
	}
	
	@Override
	public void setClaseContenida(Object claseContenida) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean noEsPopUp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMetodoRetorno() {
		// TODO Auto-generated method stub
		return null;
	}
	
}


