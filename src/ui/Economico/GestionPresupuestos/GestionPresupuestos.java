package ui.Economico.GestionPresupuestos;

import java.net.URL;
import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import ui.ControladorPantalla;
import ui.GestionBotones;
import ui.Economico.ControlPresupuestario.EdicionEstImp.FraccionarImputacion;
import ui.Economico.ControlPresupuestario.EdicionEstImp.General;
import ui.Economico.EstimacionesValoraciones.EstimacionesValoraciones;

public class GestionPresupuestos implements ControladorPantalla {
	

	public static final String fxml = "file:src/ui/Economico/GestionPresupuestos/GestionPresupuestos.fxml";
	
	@FXML
    private TextField tNomProyecto;

    @FXML
    private TableView<?> tCoste;

    @FXML
    private TableView<?> tDemandas;

    @FXML
    private ComboBox<Proyecto> cbProyecto;

    @FXML
    private ImageView imGuardarNuevaVersion;

    @FXML
    private TextField tVsProyecto;

    @FXML
    private ImageView imNuevoProyecto;
    private GestionBotones gbNuevoProyecto;

    @FXML
    private ImageView imAniadirDemanda;

    @FXML
    private ImageView imBuscarVersion;
    private GestionBotones gbBuscarVersion;

    @FXML
    private ImageView imGuardar;

    @FXML
    private ComboBox<Presupuesto> cbVersion;
	
	@FXML
	private AnchorPane anchor;
	
	
	public void initialize(){
		Proyecto p = new Proyecto();
		ArrayList<Proyecto> listaProyectos = p.listadoProyectosGGP();
		
		cbProyecto.getItems().addAll(listaProyectos);
		cbProyecto.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { buscaPresupuestos (newValue);  	}   );
		
		cbVersion.getItems().removeAll(cbVersion.getItems());
		
		gbNuevoProyecto = new GestionBotones(imNuevoProyecto, "Nuevo3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {						        
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Nuevo Proyecto");
		gbNuevoProyecto.activarBoton();
		
		gbBuscarVersion = new GestionBotones(imBuscarVersion, "Buscar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {						        
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Buscar Versión Presupuesto");
		gbBuscarVersion.desActivarBoton();
	}
	
	private void buscaPresupuestos(Proyecto p) {
		cbVersion.getItems().removeAll(cbVersion.getItems());
		
		Presupuesto pres = new Presupuesto();		
		ArrayList<Presupuesto> listado = pres.buscaPresupuestos(p.id);
		
		cbVersion.getItems().addAll(listado);
		
		if (listado.size()>0) {
			
		}
	}
	
	public void cargaVersiones () {
		
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
