package ui.Economico.Tarifas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.ToggleSwitch;

import application.Main;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.Proveedor;
import model.beans.Tarifa;
import ui.Dialogo;
import ui.GestionBotones;
import ui.Tabla;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteCertificacion;
import ui.Economico.Tarifas.Tables.TarifaTabla;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class GestionTarifas  implements ControladorPantalla {
	public static final String fxml = "file:src/ui/Economico/Tarifas/GestionTarifas.fxml";
	
	public static ArrayList<Tableable> listaBorrados = null;
	public static GestionBotones botonGuardar = null;;
	
	PopOver popUp = null;
	
	@FXML
	private AnchorPane anchor;
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
    private GestionBotones gbBuscar;
    @FXML // fx:id="tTarifas"
    private TableView<Tableable> tTarifas; 
	public static Tabla tablaTarifas;
    @FXML // fx:id="imGuardar"
    private ImageView imGuardar; 
    private GestionBotones gbGuardar;
    @FXML // fx:id="imGuardar"
    private ImageView imAniadirTarifa; 
    private GestionBotones gbAniadirTarifa;
    
    public static ControladorPantalla objetoThis = null;

    @Override
	public void resize(Scene escena) {
		tablaTarifas.formateaTabla(new Double(escena.getWidth()*0.95).intValue(), new Double(escena.getHeight()*0.5).intValue());
		cbProveedores.setPrefWidth(escena.getWidth()*0.65);
	}
	
	public void initialize(){

		tablaTarifas = new Tabla(tTarifas,new TarifaTabla(),this);
		tablaTarifas.altoLibre = true;
		
		cbProveedores.getItems().addAll(Proveedor.listado.values());
		cbProveedores.getItems().add(new Proveedor());
				
		gbBuscar = new GestionBotones(imBuscar, "Buscar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					buscaTarifas(); 
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Buscar Tarifas");
		gbBuscar.activarBoton();
		
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					procesaTarifas();  
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Guardar Cambios");
		gbGuardar.desActivarBoton();
		
		botonGuardar = gbGuardar;
		
		gbAniadirTarifa = new GestionBotones(imAniadirTarifa, "NuevaFila3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					aniadeTarifa(); 
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Nueva Demanda");
		gbAniadirTarifa.desActivarBoton();
		
		objetoThis = this;
		
		listaBorrados = new ArrayList<Tableable> ();
		
		resize(Main.scene);
	}
	
	public void aniadeTarifa() {
		Tarifa t  = new Tarifa();
		t.idTarifa = -1;
		TarifaTabla tt = new TarifaTabla();
		tt.t = t;
		
		tablaTarifas.listaDatosEnBruto.add(t);
		tablaTarifas.refrescaTabla();
		
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
		
		ArrayList<Object> tarifasList = new ArrayList<Object>();
		tarifasList.addAll(new Tarifa().listado(filtros));
		
		tTarifas.getProperties().put("GestionTarifas", this);
		
		try {
			tablaTarifas.pintaTabla(tarifasList);		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		gbAniadirTarifa.activarBoton();
		
		resize(Main.scene);		
	}
	
	public void procesaTarifas() {
		
		try { 
			Iterator<Tableable> it = tTarifas.getItems().iterator();
			
			while (it.hasNext()) {
				TarifaTabla tt = (TarifaTabla) it.next();
				Tarifa t = tt.t;
				
				if (t.modificado) {
					if ( t.fInicioVig==null  || t.fFinVig==null   || t.costeHora==0)
						Dialogo.error("Gestión de Tarifas", "Error al guardar", "Faltan campos por informar en la tarifa");
					else {
						if (t.idTarifa==-1) {
							t.insertTarifa();
						} else {
							t.updateTarifa();
						}
						t.modificado = false;
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
			
			gbGuardar.desActivarBoton();
			
			Dialogo.alert("Gestión de Tarifas", "Éxito al guardar", "Los cambios se han guardado correctamente");
			
			buscaTarifas() ;
			
		} catch (Exception e) {
			Dialogo.error("Gestión de Tarifas", "Error al guardar", "Se ha producido un fallo al guardar los datos.");
		}
	}
	
	public void valorModificado() {
		tablaTarifas.refrescaTabla();
		gbGuardar.activarBoton();		
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
