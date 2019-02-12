package ui.Economico.Tarifas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.ToggleSwitch;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.Proveedor;
import model.beans.Tarifa;
import ui.ControladorPantalla;
import ui.Dialogo;
import ui.Tableable;
import ui.Economico.Tarifas.Tables.TarifaTabla;

public class GestionTarifas  implements ControladorPantalla {
	public static final String fxml = "file:src/ui/Economico/Tarifas/GestionTarifas.fxml";
	
	public static ArrayList<Tableable> listaBorrados = null;
	public static ImageView botonGuardar = null;;
	
	PopOver popUp = null;
	
	@FXML
	private AnchorPane anchor;
	@FXML // fx:id="tpFiltros"
    private TitledPane tpFiltros; 
    @FXML // fx:id="cbProveedores"
    private ComboBox<Proveedor> cbProveedores; 
    @FXML // fx:id="tsVigentes"
    private ToggleSwitch tsVigentes; 
    @FXML // fx:id="tsDesarrollo"
    private ToggleSwitch tsDesarrollo; 
    @FXML // fx:id="tsMantenimiento"
    private ToggleSwitch tsMantenimiento; 
    @FXML // fx:id="imBuscar"
    private ImageView imBuscar; 
    @FXML // fx:id="tpResultados"
    private TitledPane tpResultados; 
    @FXML // fx:id="tTarifas"
    private TableView<Tableable> tTarifas; 
    @FXML // fx:id="imGuardar"
    private ImageView imGuardar; 
    @FXML // fx:id="imGuardar"
    private ImageView imAniadirTarifa; 

	
	public void initialize(){
		botonGuardar = imGuardar;
		
		cbProveedores.getItems().addAll(Proveedor.listado.values());
		cbProveedores.getItems().add(new Proveedor());
		
		imAniadirTarifa.setVisible(false);
		
		imBuscar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 buscaTarifas(); }	});
		imGuardar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 procesaTarifas(); }	});
		imAniadirTarifa.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 aniadeTarifa(); }	});
		
		listaBorrados = new ArrayList<Tableable> ();
	}
	
	public void aniadeTarifa() {
		Tarifa t  = new Tarifa();
		t.idTarifa = -1;
		TarifaTabla tt = new TarifaTabla();
		tt.t = t;
		
		tTarifas.getItems().add(tt);
		
		tTarifas.refresh();
	}
	
	public void buscaTarifas() {
		TarifaTabla tarTab = new TarifaTabla();
		
		HashMap<String, Object> filtros = new HashMap<String, Object>();
		if (cbProveedores.getValue()!=null && cbProveedores.getValue().id!=0) {
			filtros.put(Tarifa.filtro_PROVEEDOR, cbProveedores.getValue());
		}
		if (tsVigentes.isSelected()) filtros.put(Tarifa.filtro_VIGENTES, new Boolean(true));
		if (tsDesarrollo.isSelected()) filtros.put(Tarifa.filtro_DESARROLLO, new Boolean(true));
		if (tsMantenimiento.isSelected()) filtros.put(Tarifa.filtro_MANTENIMIENTO, new Boolean(true));
		
		ArrayList<Object> lista = new ArrayList<Object>();
		lista.addAll(new Tarifa().listado(filtros));
		ObservableList<Tableable> dataTable = tTarifas.getItems();
		dataTable = tarTab.toListTableable(lista);
		tTarifas.setItems(dataTable);
		tTarifas.getProperties().put("GestionTarifas", this);
		
		try {
			tarTab = new TarifaTabla();
			tarTab.limpiarColumnas(tTarifas);			
			tarTab.fijaColumnas(tTarifas);	
			
			tTarifas.refresh();
		} catch (Exception e) {}
		
		tpFiltros.setExpanded(false);
		tpResultados.setExpanded(true);
		imAniadirTarifa.setVisible(true);
	}
	
	public void procesaTarifas() {
		
		try {
			Iterator<Tableable> it = tTarifas.getItems().iterator();
			
			while (it.hasNext()) {
				TarifaTabla tt = (TarifaTabla) it.next();
				if (tt.modificado) {
					Tarifa t = tt.t;
					if (t.proveedor==null || t.fInicioVig==null  || t.fFinVig==null   || t.costeHora==0)
						Dialogo.error("Gestión de Tarifas", "Error al guardar", "Faltan campos por informar en la tarifa");
					else {
						if (t.idTarifa==-1) {
							t.insertTarifa();
						} else {
							t.updateTarifa();
						}
						tt.modificado = false;
					}					
				}
			}
			
			it = GestionTarifas.listaBorrados.iterator();
			
			while (it.hasNext()) {
				TarifaTabla tt = (TarifaTabla) it.next();
				Tarifa t = tt.t;
				t.borraTarifa();
			}
			
			listaBorrados = new ArrayList<Tableable> ();

			GestionTarifas.botonGuardar.getStyleClass().remove("iconoEnabled");
			GestionTarifas.botonGuardar.getStyleClass().add("iconoDisabled");
			
			Dialogo.alert("Gestión de Tarifas", "Éxito al guardar", "Los cambios se han guardado correctamente");
			
			buscaTarifas() ;
			
		} catch (Exception e) {
			Dialogo.error("Gestión de Tarifas", "Error al guardar", "Se ha producido un fallo al guardar los datos.");
		}
	}

	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		// TODO Auto-generated method stub
		return fxml;
	}
}
