package application;
	
import java.net.URL;
import java.util.HashMap;

import org.controlsfx.control.HiddenSidesPane;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.constantes.CargaInicial;
import ui.ControladorPantalla;
import ui.Administracion.Festivos.GestionFestivos;
import ui.Administracion.Parametricas.AdministracionParametros;
import ui.Economico.ControlPresupuestario.ControlPresupuestario;
import ui.Economico.EstimacionesInternas.EstimacionesInternas;
import ui.Economico.EstimacionesValoraciones.EstimacionesValoraciones;
import ui.Economico.GestionPresupuestos.GestionPresupuestos;
import ui.GestionProyectos.AltaModProyecto;
import ui.GestionProyectos.ConsultaProyectos;
import ui.Recursos.GestionRecursos.AltaModRecurso;
import ui.Recursos.GestionTarifas.AsignacionTarifas;
import ui.Recursos.GestionVacaciones.GestionVacaciones;


public class Main extends Application {
	
	public static final String PANTALLA_ACTIVA = "pantalla_Activa"; 
	
	public static HashMap<String, Object> sesion = null;
		
	@Override
	public void start(Stage primaryStage) {
		try {
			//Empieza el show
			Main.sesion = new HashMap<String, Object>();
			
			new CargaInicial().load();
	        
			BorderPane panelBase = new BorderPane();
			
	        ScrollPane root = new ScrollPane();
	        
	        root.setFitToHeight(true);
	        root.setFitToWidth(true);
	        root.setHbarPolicy(ScrollBarPolicy.ALWAYS);
	        panelBase.setCenter(root);

	        Scene scene = new Scene(panelBase);
	        scene.getStylesheets().add("application.css");	        
	        
	        AdministracionParametros c = new AdministracionParametros();
	        
	        Main.sesion.put(Main.PANTALLA_ACTIVA, c);
	        FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new URL(c.getFXML()));
            	        
	        HiddenSidesPane pane = new HiddenSidesPane();
	        pane.setPadding(new Insets(0, 0, 0, 0));
	        pane.setContent(loader.load());
	        mostrarMenu (pane);
	        
	        panelBase.setCenter(pane);
	        
	        primaryStage.setMaximized(true);	        
	        primaryStage.setTitle("Gestión Proyectos");			
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@SuppressWarnings("unchecked")
	private void mostrarMenu (HiddenSidesPane pane) {
		TreeItem<String> dummyRoot = new TreeItem<>();
		
		final Node rootIcon = new ImageView(new Image("proyectosDemanda.png"));
		rootIcon.getStyleClass().add("iconoMenu");
		rootIcon.setAccessibleHelp("Demanda & Proyectos");
		rootIcon.setAccessibleText("Demanda & Proyectos");
		((ImageView)rootIcon).setFitWidth(35);
		((ImageView)rootIcon).setPreserveRatio(true);
		
		TreeItem<String> model1 = new TreeItem<String>("", rootIcon);
		
		//TreeItem<String> model11 = new TreeItem<String>("Consulta");
	    TreeItem<String> model12 = new TreeItem<String>("Alta & Modificación");
	    //model1.getChildren().add(model11);
	    model1.getChildren().add(model12);
		
	    TreeItem<String> model2 = new TreeItem<String>("Recursos");
	    TreeItem<String> model21 = new TreeItem<String>("Gestión Recursos");
	    TreeItem<String> model22 = new TreeItem<String>("Asignación Tarifas");
	    TreeItem<String> model23 = new TreeItem<String>("Gestión horas trabajadas");
	    model2.getChildren().addAll(model21,model22,model23);

	    TreeItem<String> model3 = new TreeItem<String>("Económico");
		
		TreeItem<String> model31 = new TreeItem<String>("Gestión Estimaciones");
	    model3.getChildren().add(model31);
		TreeItem<String> model311 = new TreeItem<String>("Gestión Presupuestos");
	    model3.getChildren().add(model311);
	    TreeItem<String> model32 = new TreeItem<String>("Control Presupuestario");
	    model3.getChildren().add(model32);
	    TreeItem<String> model34 = new TreeItem<String>("Estimaciones Por Horas");
	    model3.getChildren().add(model34);
	    TreeItem<String> model33 = new TreeItem<String>("Gestión Tarifas");
	    model3.getChildren().add(model33);
	    
	    

	    TreeItem<String> model4 = new TreeItem<String>("Administración");
		
		TreeItem<String> model41 = new TreeItem<String>("Gestión Festivos");
	    model4.getChildren().add(model41);
	    
	    dummyRoot.getChildren().addAll(model1, model2, model3, model4);
	    
	    TreeView<String> tree = new TreeView<>(dummyRoot);
	    tree.setShowRoot(false);
	    
	    pane.setLeft(tree);    
	    
	    EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> {
	    	handleMouseClicked(event, tree, pane);
	    };

	    tree.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle); 
	    	        
	}
	
	private void handleMouseClicked(MouseEvent event, TreeView<String> tree, HiddenSidesPane pane) {
	    Node node = event.getPickResult().getIntersectedNode();
	    // Accept clicks only on node cells, and not on empty spaces of the TreeView
	    if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
	        String name = (String) ((TreeItem) tree.getSelectionModel().getSelectedItem()).getValue();
	        
	        try{
		        FXMLLoader loader = new FXMLLoader();
		        ControladorPantalla controlPantalla = null;
		        Main.sesion.put(Main.PANTALLA_ACTIVA, controlPantalla);
		        
		        if ("Consulta".equals(name))
		        	controlPantalla = new ConsultaProyectos();		        	
		        
		        if ("Alta & Modificación".equals(name))
		        	controlPantalla = new AltaModProyecto();
		        
		        if ("Gestión Recursos".equals(name))
		        	controlPantalla = new AltaModRecurso();
		        
		        if ("Asignación Tarifas".equals(name))
		        	controlPantalla = new AsignacionTarifas();

		        if ("Gestión horas trabajadas".equals(name))
		        	controlPantalla = new GestionVacaciones();
		        
		        if ("Gestión Estimaciones".equals(name))
		        	controlPantalla = new EstimacionesValoraciones();
		        
		        if ("Gestión Tarifas".equals(name))
		        	controlPantalla = new ui.Economico.Tarifas.GestionTarifas();
		        
		        if ("Gestión Presupuestos".equals(name))
		        	controlPantalla = new GestionPresupuestos();
		        
		        if ("Control Presupuestario".equals(name))
		        	controlPantalla = new ControlPresupuestario();
		        
		        if ("Estimaciones Por Horas".equals(name))
		        	controlPantalla = new EstimacionesInternas();
		        
		        if ("Gestión Festivos".equals(name))
		        	controlPantalla = new GestionFestivos();
		        
		        if (controlPantalla!=null){
		        	loader.setLocation(new URL(controlPantalla.getFXML()));
		        	pane.setContent(loader.load());
		        }
		        
		        tree.getSelectionModel().getSelectedItem().setExpanded(!tree.getSelectionModel().getSelectedItem().isExpanded());
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	    }
	}
	
}
