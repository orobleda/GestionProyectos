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
import model.metadatos.TipoDato;
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
    private TextField tFecha;

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
		
		tFecha.focusedProperty().addListener((ov, oldV, newV) -> {    if (!newV) { validaFecha(tFecha); validaCampos();  }  });
		cbTarifa.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { validaCampos();   	});
		imGuardar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 guardaElemento(); }	});
		
		validaCampos();
		
		try {
			AsignacionRecursoTarifa art = (AsignacionRecursoTarifa) expander.getValue();
			RelRecursoTarifa lcp = art.relRecursoTarifa;
			
			if (lcp.tarifa!=null) cbTarifa.setValue(lcp.tarifa);
			
			Date fecha = Constantes.finMes(lcp.mes, lcp.anio);
			tFecha.setText(FormateadorDatos.formateaDato(fecha, TipoDato.FORMATO_FECHA));
			
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
		
		if ("".equals(tFecha.getText())){
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
			Date fecha = (Date) FormateadorDatos.parseaDato(tFecha.getText(), FormateadorDatos.FORMATO_FECHA);
			
			Calendar c = Calendar.getInstance();
			c.setTime(fecha);
			
			lcp.mes = c.get(Calendar.MONTH)+1;
			lcp.anio = c.get(Calendar.YEAR);
			
			lcp.tarifa = cbTarifa.getValue();
			
			art.relRecursoTarifa = lcp;
			art.modificado = true;

			expander.getTableRow().getTableView().refresh();
		} catch (Exception e){
			
		}
		
	}
	
}
