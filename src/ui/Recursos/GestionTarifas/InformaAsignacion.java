package ui.Recursos.GestionTarifas;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.RelRecursoTarifa;
import model.beans.Tarifa;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import ui.Recursos.GestionTarifas.Tables.AsignacionRecursoTarifa;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class InformaAsignacion implements ControladorPantalla {

	public static final String fxml = "file:src/ui/Recursos/GestionTarifas/InformaAsignacion.fxml"; 
	
	public static TableRowDataFeatures<Tableable> expander = null;
	
	@FXML
	private AnchorPane anchor;	

    @FXML
    private ComboBox<Tarifa> cbTarifa;

    @FXML
    private TextField tFechaInicio;

    @FXML
    private TextField tFechaFin;

    @FXML
    private ImageView imGuardar;
	
    	
	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public InformaAsignacion(){
	}
	
	public InformaAsignacion (TableRowDataFeatures<Tableable> expander){
		InformaAsignacion.expander = expander;
	}	
	
	public void initialize(){		
		Tarifa t = new Tarifa();
		
		HashMap<String, Object> filtros = new HashMap<String, Object> ();
		
		if (AsignacionTarifas.proveedores)
			filtros.put(Tarifa.filtro_DESARROLLO, new Boolean(true));
		else
			filtros.put(Tarifa.filtro_DESARROLLO, new Boolean(false));
		
		ArrayList<Tarifa> tarifas = t.listado(filtros);
		
		cbTarifa.getItems().addAll(tarifas);
		
		tFechaInicio.focusedProperty().addListener((ov, oldV, newV) -> {    if (!newV) { validaFecha(tFechaInicio); validaCampos();  }  });
		tFechaFin.focusedProperty().addListener((ov, oldV, newV) -> {    if (!newV) { validaFecha(tFechaFin); validaCampos();  }  });
		cbTarifa.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { validaCampos();   	});
		imGuardar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 guardaElemento(); }	});
		
		validaCampos();
		
		try {
			AsignacionRecursoTarifa art = (AsignacionRecursoTarifa) expander.getValue();
			RelRecursoTarifa lcp = art.relRecursoTarifa;
			
			if (lcp.tarifa!=null) cbTarifa.setValue(lcp.tarifa);
			if (lcp.fechaInicio!=null) tFechaInicio.setText(FormateadorDatos.formateaDato(lcp.fechaInicio, FormateadorDatos.FORMATO_FECHA));
			if (lcp.fechaFin!=null) 
				if (Constantes.fechaFinal.compareTo(lcp.fechaFin)<=0)
					tFechaFin.setText("");
				else
					tFechaFin.setText(FormateadorDatos.formateaDato(lcp.fechaFin, FormateadorDatos.FORMATO_FECHA));
			
		} catch (Exception e){
			
		}
    }
	
	public void validaFecha(TextField campo) {
		try{
			FormateadorDatos.formateaDato(campo.getText(), FormateadorDatos.FORMATO_FECHA);
		} catch (Exception e) {
			campo.setText("");
		}
	}
	
	public void validaCampos() {
		boolean validacion = true;
		
		if ("".equals(tFechaInicio.getText())){
			validacion = false;
		}
		
		if (cbTarifa.getValue() == null){
			validacion = false;
		}
		
		if (validacion) {
			imGuardar.setMouseTransparent(false);
		    imGuardar.getStyleClass().remove("iconoDisabled");
		    imGuardar.getStyleClass().add("iconoEnabled");
		} else {
			imGuardar.setMouseTransparent(true);
		    imGuardar.getStyleClass().remove("iconoEnabled");
		    imGuardar.getStyleClass().add("iconoDisabled");
		}
	}
	
	public void guardaElemento() {
		expander.toggleExpanded();
		
		try {
			AsignacionRecursoTarifa art = (AsignacionRecursoTarifa) expander.getValue();
			
			RelRecursoTarifa lcp = art.relRecursoTarifa;
			lcp.modificado = true;
			lcp.fechaInicio = (Date) FormateadorDatos.parseaDato(tFechaInicio.getText(), FormateadorDatos.FORMATO_FECHA);
			
			Calendar c;
			
			if ("".equals(tFechaFin.getText())) {
				c = Calendar.getInstance();
				c.setTime(Constantes.fechaFinal);
				c.add(Calendar.DAY_OF_MONTH, 1);
				lcp.fechaFin = c.getTime();
			} else {
				Date d = (Date) FormateadorDatos.parseaDato(tFechaFin.getText(), FormateadorDatos.FORMATO_FECHA);
				c = Calendar.getInstance();
				c.setTime(d);
				lcp.fechaFin = c.getTime();
			}	
			
			lcp.tarifa = cbTarifa.getValue();
			
			art.relRecursoTarifa = lcp;
			art.modificado = true;

			expander.getTableRow().getTableView().refresh();
		} catch (Exception e){
			
		}
		
	}
	
}
