package ui.planificacion.Faseado;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.beans.Coste;
import model.beans.FaseProyecto;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.metadatos.Sistema;
import ui.GestionBotones;
import ui.Tabla;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;
import ui.planificacion.Faseado.tables.DemandasAsociadasTabla;

public class Faseado implements ControladorPantalla {
	
	public static final String fxml = "file:src/ui/planificacion/Faseado/Faseado.fxml";
	
	public Proyecto pActual = null;
	
    @FXML
    private VBox vbContenedorFases;

    @FXML
    private TableView<Tableable> tDemandas;
    public Tabla tablaDemandas;

    @FXML
    private ComboBox<Proyecto> cbProyecto;

    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;

	

	@FXML
	private AnchorPane anchor;
	
	public void initialize(){
		tablaDemandas = new Tabla(tDemandas,new DemandasAsociadasTabla(),this);
		
		ArrayList<Proyecto> listaProyectos = (new Proyecto()).listadoProyectosGGP();
		cbProyecto.getItems().addAll(listaProyectos);
		
		cbProyecto.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			try {
				cargaProyecto();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} );
		
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Guardar Cambios");
		this.gbGuardar.desActivarBoton();
	}
	
	private void cargaProyecto() throws Exception {
		pActual = this.cbProyecto.getValue();
		
		HashMap<String, Sistema> lSistemas = new HashMap<String, Sistema>();
		
		ArrayList<Proyecto> demandas = pActual.getDemandasAsociadas();
		Iterator<Proyecto> itDemanda = demandas.iterator();
		while (itDemanda.hasNext()) {
			Proyecto demanda = itDemanda.next();
			Presupuesto pres = new Presupuesto();
			
			if (demanda.apunteContable) {
				pres.idApunteContable = demanda.id;
				demanda.presupuestoActual = pres.buscaPresupuestosAPunteContable().get(0);
				
			} else {
				demanda.presupuestoActual = pres.dameUltimaVersionPresupuesto(demanda);
			}
			
			demanda.presupuestoActual.cargaCostes();
			
			Iterator<Coste> itCoste = demanda.presupuestoActual.costes.values().iterator();
			while (itCoste.hasNext()) {
				Coste c = itCoste.next();
				lSistemas.put(c.sistema.codigo, c.sistema);
			}
		}
		
		HashMap<String,Object> pasoPrimitiva = new HashMap<String,Object>();
		pasoPrimitiva.put("sistemas", lSistemas.values());
		pasoPrimitiva.put("controlPadre", this);
		
		ArrayList<Object> listaPintable = new ArrayList<Object>();
		listaPintable.addAll(demandas);	
		
		tablaDemandas.setPasoPrimitiva(pasoPrimitiva);
		
		tablaDemandas.pintaTabla(listaPintable);
		
		pintaFases() ;		

		this.gbGuardar.activarBoton();
	}
	
	public void pintaFases() {
		this.vbContenedorFases.getChildren().removeAll(this.vbContenedorFases.getChildren());
		
		HashMap<String, Object> variablesPaso = null;
		
		if (this.pActual.fasesProyecto!=null)  {
			Iterator<FaseProyecto> itFases = this.pActual.fasesProyecto.iterator();
			
			while (itFases.hasNext()) {
				FaseProyecto fase = itFases.next();
				variablesPaso = new HashMap<String, Object>();
				variablesPaso.put(InfoFase.FASE, fase);
				variablesPaso.put(InfoFase.PADRE, this);
				
				try {
					InfoFase asigFaseS = new InfoFase();
			        FXMLLoader loader = new FXMLLoader();
			        loader.setLocation(new URL(asigFaseS.getFXML()));
			        this.vbContenedorFases.getChildren().add(loader.load());
			        asigFaseS = loader.getController();
			        asigFaseS.setParPaso(variablesPaso);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				
			}
			
			this.tablaDemandas.refrescaTabla();			
		}
		
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
