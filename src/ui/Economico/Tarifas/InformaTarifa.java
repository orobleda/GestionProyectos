package ui.Economico.Tarifas;

import java.time.LocalDate;
import java.util.Calendar;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.Proveedor;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import ui.Dialogo;
import ui.Economico.Tarifas.Tables.TarifaTabla;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class InformaTarifa  implements ControladorPantalla  {
	
public static final String fxml = "file:src/ui/Economico/Tarifas/InformaTarifa.fxml";
	
	PopOver popUp = null;
	static TableRowDataFeatures<Tableable> expander = null;
	
	@FXML
	private AnchorPane anchor;
	@FXML
	private ComboBox<Proveedor> cbProveedor;
	@FXML
	private ToggleSwitch tsDesarrollo;
	@FXML
	private DatePicker dpIniVig;
	@FXML
	private DatePicker dpFinVig;
	@FXML
	private ToggleSwitch tsMantenimiento;
	@FXML
	private TextField tTarifa;
	@FXML
    private ImageView imGuardar;
    @FXML
    private ImageView imEliminar;
	
	public static TarifaTabla t = null;

	public void initialize(){
		cbProveedor.getItems().addAll(Proveedor.listado.values());
		if (t.t.proveedor!=null)
			cbProveedor.setValue(t.t.proveedor);
		
		tsDesarrollo.setSelected(t.t.esDesarrollo);
		
		if (t.t.esDesarrollo) {
			tsMantenimiento.setSelected(t.t.esMantenimiento);
		} else {
			tsMantenimiento.setSelected(false);
			tsMantenimiento.setDisable(true);
		}
		
		if (t.t.fInicioVig!=null) {
			Calendar c = Calendar.getInstance();
			c.setTime(t.t.fInicioVig);
			LocalDate d =  LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));
			dpIniVig.setValue(d);
		}
		
		if (t.t.fFinVig!=null) {
			
			if (Constantes.fechaFinal.compareTo(t.t.fFinVig)==1) {
				Calendar c = Calendar.getInstance();
				c.setTime(t.t.fFinVig);
				LocalDate d2 =  LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));
				dpFinVig.setValue(d2);
			}
		}
		
		try{
			tTarifa.setText(FormateadorDatos.formateaDato(t.t.costeHora, FormateadorDatos.FORMATO_MONEDA));
		} catch (Exception e) {}
		
		tsDesarrollo.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 modificaEsDesarrollo(); }	});
		imGuardar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 modificaTarifa(); }	});
		imEliminar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 borraTarifa(); }	});
		tTarifa.focusedProperty().addListener((ov, oldV, newV) -> { if (!newV) { try { tTarifa.setText(FormateadorDatos.formateaDato(tTarifa.getText(), FormateadorDatos.FORMATO_MONEDA)); } catch (Exception e) {tTarifa.setText("");}   }  });
	}
	
	public void modificaTarifa () {
		
		try {
			expander.toggleExpanded();
			
			t.t.proveedor = cbProveedor.getValue();
			t.t.esDesarrollo = tsDesarrollo.isSelected();
			t.t.esMantenimiento = tsMantenimiento.isSelected();
			t.t.costeHora = (Float) FormateadorDatos.parseaDato(tTarifa.getText(), FormateadorDatos.FORMATO_MONEDA);
			
			Calendar c = Calendar.getInstance();
			c.set(dpIniVig.getValue().getYear(), dpIniVig.getValue().getMonthValue()-1, dpIniVig.getValue().getDayOfMonth());
			t.t.fInicioVig = c.getTime();
			
			if (dpFinVig.getValue()==null) {
				c = Calendar.getInstance();
				c.setTime(Constantes.fechaFinal);
				c.add(Calendar.DAY_OF_MONTH, 1);
				t.t.fFinVig = c.getTime();
			} else {
				c = Calendar.getInstance();
				c.set(dpFinVig.getValue().getYear(), dpFinVig.getValue().getMonthValue()-1, dpFinVig.getValue().getDayOfMonth());
				t.t.fFinVig = c.getTime();
			}			
			
			t.set(t.t);
			t.modificado = true;
			
			expander.getTableRow().getTableView().refresh();
			
			GestionTarifas.botonGuardar.getStyleClass().remove("iconoDisabled");
			GestionTarifas.botonGuardar.getStyleClass().add("iconoEnabled");
			
		} catch (Exception e) {
			Dialogo.error("Gestión de Tarifas", "Error al guardar", "Faltan campos por informar en la tarifa");
		}
	}
	
	public void borraTarifa() {
		try {
			expander.toggleExpanded();
			
			GestionTarifas.listaBorrados.add(t);
			expander.getTableRow().getTableView().getItems().remove(t);
			
			expander.getTableRow().getTableView().refresh();
			
			GestionTarifas.botonGuardar.getStyleClass().remove("iconoDisabled");
			GestionTarifas.botonGuardar.getStyleClass().add("iconoEnabled");
		} catch (Exception e) {
			
		}
	}
	
	
	public void modificaEsDesarrollo() {
		if (tsDesarrollo.isSelected()) {
			tsMantenimiento.setDisable(false);
		} else {
			tsMantenimiento.setSelected(false);
			tsMantenimiento.setDisable(true);
		}
	}
	
	public InformaTarifa() {
			
	}
	
	public InformaTarifa(TableRowDataFeatures<Tableable> expander) {
		t = (TarifaTabla) expander.getValue();
		InformaTarifa.expander = expander;
	}

	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	

}
