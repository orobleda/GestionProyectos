package ui.Recursos.GestionVacaciones;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.constantes.Constantes;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
import ui.interfaces.ControladorPantalla;
import ui.popUps.PopUp;

public class FiltrosConsultaGV implements ControladorPantalla, PopUp {
									   
	public static final String fxml = "file:src/ui/Recursos/GestionVacaciones/FiltrosConsultaGV.fxml"; 
	
	public HashMap<String, Object> variablesPaso = null;
	public ControladorPantalla ventanaPadre = null;
	
	@FXML
	private AnchorPane anchor;
    
	@FXML
    private ComboBox<String> cbMeses;

    @FXML
    private ComboBox<Integer> cbAnios;
    @FXML
    private ImageView imBuscarAn;
    private GestionBotones gbBuscarAn;
		
	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public FiltrosConsultaGV(){
	}
	
	@Override
	public void resize(Scene escena) {	
	}
	
	public void initialize(){ 
		Date d = Constantes.fechaActual();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		cbAnios.getItems().add(c.get(Calendar.YEAR)-1);
		for (int i=c.get(Calendar.YEAR);i<c.get(Calendar.YEAR)+3;i++){
			cbAnios.getItems().add(i);	
		}
		
		new Constantes();
		
		for (int i=0;i<Constantes.meses.size();i++){
			cbMeses.getItems().add(Constantes.meses.get(i));	
		}
		
		cbAnios.setValue(c.get(Calendar.YEAR));
		cbMeses.setValue(Constantes.meses.get(c.get(Calendar.MONTH)));
		
		int mes = Constantes.numMes(cbMeses.getValue());
		int anio = cbAnios.getValue();
		
		HBox contenedorRecurso = null;
		
		try {			
			gbBuscarAn = new GestionBotones(imBuscarAn, "Buscar3", false, new EventHandler<MouseEvent>() {        
				@Override
	            public void handle(MouseEvent t)
	            {   
					try {
						consulta();
					} catch (Exception e) {
						e.printStackTrace();
					}
	            } }, "Buscar Mes", this);	
			gbBuscarAn.activarBoton();
			
			resize(null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	public void consulta() {
		if (cbMeses.getValue()==null || cbAnios.getValue()==null) {
			Dialogo.alert("Campos sin informar", "Campos sin informar", "Faltan campos obligatorios sin informar");
		} else {
			int mes = Constantes.numMes(cbMeses.getValue());
			int anio = cbAnios.getValue();
			
			try {
				((GestionVacaciones) ventanaPadre).consulta(mes,anio);
			} catch (Exception e) {
				
			}
		} 	
		
		ParamTable.po.hide();
    }

	@Override
	public String getControlFXML() {
		return FiltrosConsultaGV.fxml;
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		this.ventanaPadre = (ControladorPantalla) variablesPaso.get("ventanaPadre");
		this.variablesPaso = variablesPaso;
	}

	@Override
	public void setClaseContenida(Object claseContenida) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean noEsPopUp() {
		return false;
	}

	@Override
	public String getMetodoRetorno() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
