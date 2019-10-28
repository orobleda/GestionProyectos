package ui.Economico.Tarifas;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.Imputacion;
import model.beans.Proveedor;
import model.beans.Tarifa;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.metadatos.TipoDato;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Economico.CargaImputaciones.DetalleImputacion;
import ui.Economico.CargaImputaciones.Tables.LineaDetalleImputacion;
import ui.Economico.Tarifas.Tables.TarifaTabla;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;
import ui.popUps.PopUp;

public class InformaTarifa implements ControladorPantalla, PopUp {
	
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
    private GestionBotones gbGuardar;
    @FXML
    private ImageView imEliminar;
    private GestionBotones gbEliminar;
	
	public static TarifaTabla t = null;
	public DetalleImputacion di = null;
	public GestionTarifas gt = null;
	public Imputacion imputacion = null;
	
	public boolean esPopUp = false;
	
	@Override
	public void resize(Scene escena) {
		
	}

	public void initialize(){
		try {
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
		
		} catch (Exception ex) {
			
		}
		
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				modificaTarifa();
            } }, "Guardar Tarifa", this);	
		gbGuardar.desActivarBoton();
		
		gbEliminar = new GestionBotones(imEliminar, "Eliminar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				try {
					borraTarifa(); 
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Eliminar Tarifa", this);	
		gbEliminar.desActivarBoton();
		
		tsDesarrollo.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 modificaEsDesarrollo(); }	});
		tTarifa.focusedProperty().addListener((ov, oldV, newV) -> { if (!newV) { try { tTarifa.setText(FormateadorDatos.formateaDato(tTarifa.getText(), FormateadorDatos.FORMATO_MONEDA)); } catch (Exception e) {tTarifa.setText("");}   }  });
		
		if (!esPopUp) {	
			gbGuardar.activarBoton();
			gbEliminar.activarBoton();
		}
	}
	
	public void modificaTarifa () {
		try {
			Tarifa tar = null;
			
			if (this.gt!=null) {	
				tar = t.t;
				t.modificado = true;
				tar.modificado = true;
			} else
				tar = new Tarifa();
			
			tar.proveedor = cbProveedor.getValue();
			tar.esDesarrollo = tsDesarrollo.isSelected();
			tar.esMantenimiento = tsMantenimiento.isSelected();
			tar.costeHora = (Float) FormateadorDatos.parseaDato(tTarifa.getText(), FormateadorDatos.FORMATO_MONEDA);
			
			Calendar c = Calendar.getInstance();
			c.set(dpIniVig.getValue().getYear(), dpIniVig.getValue().getMonthValue()-1, dpIniVig.getValue().getDayOfMonth());
			tar.fInicioVig = c.getTime();
			
			if (dpFinVig.getValue()==null) {
				c = Calendar.getInstance();
				c.setTime(Constantes.fechaFinal);
				c.add(Calendar.DAY_OF_MONTH, 1);
				tar.fFinVig = c.getTime();
			} else {
				c = Calendar.getInstance();
				c.set(dpFinVig.getValue().getYear(), dpFinVig.getValue().getMonthValue()-1, dpFinVig.getValue().getDayOfMonth());
				tar.fFinVig = c.getTime();
			}			
						
			if (this.gt!=null) {	
				ParamTable.po.hide();
				this.gt.valorModificado();
			} else {
				tar.insertTarifa();
				Tarifa.forzarRecargaTarifas();
				this.di.altaFinalizada();
				
				Dialogo.alert("Gestión de Tarifas", "Tarifa guardada", "Se guardó la tarifa correctamente");
			}
			
		} catch (Exception e) {
			Dialogo.error("Gestión de Tarifas", "Error al guardar", "Faltan campos por informar en la tarifa");
		}
	}
	
	public void borraTarifa() {
		try {
			GestionTarifas.listaBorrados.add(t);
			ParamTable.po.hide();
			((GestionTarifas) GestionTarifas.objetoThis).valorModificado();
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

	@Override
	public String getControlFXML() {
		return InformaTarifa.fxml;
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		esPopUp = true;
		if (variablesPaso.containsKey("filaDatos")) {
			if (variablesPaso.get("filaDatos").getClass().getSimpleName().equals(TarifaTabla.class.getSimpleName())){
				TarifaTabla tt = (TarifaTabla) variablesPaso.get("filaDatos");
				InformaTarifa.t = tt;
				
				if (variablesPaso.containsKey("controladorPantalla")) {
					if (variablesPaso.get("controladorPantalla").getClass().getSimpleName().equals(GestionTarifas.class.getSimpleName())) {
						this.gt = (GestionTarifas) variablesPaso.get("controladorPantalla");
					}
				}
			}
		} else {
			if (this.imputacion==null){
				this.imputacion = (Imputacion) variablesPaso.get(LineaDetalleImputacion.IMPUTACION);
				this.di = (DetalleImputacion)  variablesPaso.get("padre");
				
				this.tsDesarrollo.setDisable(true);
				this.tsMantenimiento.setDisable(true);
				this.gbGuardar.activarBoton();
				
				try {
					this.tTarifa.setText(FormateadorDatos.formateaDato(this.imputacion.fTarifa, TipoDato.FORMATO_MONEDA));
					
					if (this.imputacion.prov!=null) {
						this.cbProveedor.setValue(this.imputacion.prov);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			} 
		}
	}

	@Override
	public void setClaseContenida(Object claseContenida) {
	}

	@Override
	public boolean noEsPopUp() {
		return false;
	}

	@Override
	public String getMetodoRetorno() {
		return null;
	}
	
	

}
