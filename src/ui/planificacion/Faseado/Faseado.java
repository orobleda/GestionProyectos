package ui.planificacion.Faseado;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.beans.Coste;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.metadatos.Sistema;
import ui.PanelResumible;
import ui.Tabla;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;
import ui.planificacion.Faseado.tables.DemandasAsociadasTabla;

public class Faseado implements ControladorPantalla {
	
	public static final String fxml = "file:src/ui/planificacion/Faseado/Faseado.fxml";
	
	Proyecto pActual = null;
	
    @FXML
    private HBox hbContenedorFases;

    @FXML
    private TableView<Tableable> tDemandas;
    public Tabla tablaDemandas;

    @FXML
    private ComboBox<Proyecto> cbProyecto;

    @FXML
    private ImageView imGuardar;

	/*
    private GestionBotones gbGuardar;*/

	@FXML
	private AnchorPane anchor;
	
	   @FXML
	    private HBox circulo;
	   
  

	    @FXML
	    private ImageView boton;
	    
	
	
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
		
		new PanelResumible ("Mostrar3R","Ocultar3R", boton, hbContenedorFases, circulo, PanelResumible.MODO_ALTERNADO);
		
		/*
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					guardarCambios();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Guardar Cambios");
		this.gbGuardar.desActivarBoton();*/
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
