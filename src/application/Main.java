package application;
	
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.controlsfx.control.HiddenSidesPane;

import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import com.dlsc.workbenchfx.view.controls.ToolbarItem;

import controller.Log;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.constantes.CargaInicial;
import ui.Dialogo;
import ui.GestionBotones;
import ui.interfaces.ControladorPantalla;
import ui.workbech.modulos.PantallaBlanca.PantallaBlanca;
import workbench.modulos.Administracion;
import workbench.modulos.Economico;
import workbench.modulos.Modulo;
import workbench.modulos.Personal;
import workbench.modulos.Planificacion;
import workbench.modulos.Recursos;
import workbench.modulos.Reporte;
import workbench.modulos.Solicitudes;
import workbench.utils.MenuNavegacion;
import workbench.utils.Pagina;
import workbench.utils.Pestania;
import workbench.utils.UtilidadesWB;


public class Main extends Application {
	
	public static boolean MODO_DEBUG = false;
	public static ContextMenu menuContexto = null;
	public static boolean mostrarMenuContexto = true;
	
	public static final String PANTALLA_ACTIVA = "pantalla_Activa"; 
	public static final String CONTROLADOR = "controlador"; 
	
	public static final int ALTA_RESOLUCION = 0; 
	public static final int BAJA_RESOLUCION = 1; 
	
	static final Point2D WINDOW_UPPER_LEFT_CORNER  =  new  Point2D( 210, 50 ) ;
	
	public static int SOLICITUDES = 0;
	public static int RECURSOS = 1;
	public static int ECONOMICO = 2;
	public static int PLANIFICACION = 3;
	public static int REPORTES = 4;
	public static int AJUSTES = 6;
	public static int PERSONAL = 5;
	
	public static Workbench customWorkbench = null;
	
	private MenuNavegacion navigationDrawer;
	public static Node pantallaActiva = null;
	
	private MenuItem menuItem;
	
	private ArrayList<WorkbenchModule> listaModulos = new ArrayList<WorkbenchModule>();
	
	public static Rectangle curtain = null;
	public static ImageView imLoading = null;
	
	public HashMap<Integer, HashMap<String, ControladorPantalla>> listaMenu = null;
	public HashMap<String, String> listaImgsPeque = null;
	
	public static HashMap<String, Object> sesion = null;
	public static Scene scene;
	public HiddenSidesPane pane = null;
	
	public static HashMap<String,Node> lNodos = null;
	public static HashMap<String, ArrayList<ToolbarItem>> lBarraHerramientas = null;
	public static HashMap<String, ArrayList<ToolbarItem>> rBarraHerramientas = null;
	
	HBox hb = null;
	
	public HBox solicRecursos = null;
	public HBox econPlani = null;
	public HBox reporAjustes = null;
	
	public ArrayList<GestionBotones> listaGBs = null;
		
	@Override
	public void start(Stage primaryStage) {
		try {
			Main.lNodos = new HashMap<String,Node>();
			Main.lBarraHerramientas = new HashMap<String, ArrayList<ToolbarItem>>(); 
			Main.rBarraHerramientas = new HashMap<String, ArrayList<ToolbarItem>>(); 
			
			new Log("DEBUG");
			
			Log.t("Hola");
			
			Main.sesion = new HashMap<String, Object>();
			
			new CargaInicial().load();
			
			//ReplicaBD.fuerzaGuardado(null); 
			
			menuContexto = new ContextMenu();
      		 
	        MenuItem item1 = new MenuItem("Menu Item 1");
	        item1.setOnAction(new EventHandler<ActionEvent>() {
	 
	            @Override
	            public void handle(ActionEvent event) {
	            	Stage stage= new Stage();
	            	HBox hb = new HBox();
	            	Scene scn = new Scene(hb);
	            	stage.setScene(scn);
	            	stage.show();
	            }
	        });
	        
	        menuContexto.getItems().add(item1);
			
			WorkbenchModule[] modulos = {new Solicitudes(), new Recursos(),new Economico(),new Planificacion(), new Reporte(), new Personal(), new Administracion()};
	        			
			navigationDrawer = new MenuNavegacion();
			
	        customWorkbench = Workbench.builder(
	        		modulos
	            )
	        		.navigationDrawer(navigationDrawer)
	                .tabFactory(Pestania::new)
	                .pageFactory(Pagina::new)
	        		.build();
	        
	        customWorkbench.activeModuleProperty().addListener(event -> {
	        	if (customWorkbench.activeModuleProperty().getValue()==null) {
	        		Main.customWorkbench.getToolbarControlsRight().removeAll(Main.customWorkbench.getToolbarControlsRight());
	        		Main.customWorkbench.getToolbarControlsLeft().removeAll(Main.customWorkbench.getToolbarControlsLeft());
	        	}
	        });
	        
	        if (Main.MODO_DEBUG)
	        	customWorkbench.getStylesheets().add("styleDebug.css");
	        else 
	        	customWorkbench.getStylesheets().add("style.css");
	        
	        for (int i=0;i<modulos.length;i++) {
	        	UtilidadesWB.insertaNodosMenu((Modulo) modulos[i]);
	        }
	        
	        scene = new Scene(customWorkbench);
	        
	        primaryStage.setMaximized(true);	 
	        if (Main.MODO_DEBUG)
		        primaryStage.setTitle("Gesti�n Proyectos DEBUG");	
	        else 
	        	primaryStage.setTitle("Gesti�n Proyectos");	
			primaryStage.setScene(scene);
			primaryStage.show();
						
		} catch(Exception e) {
			Dialogo.error(null, e);
		}
	}
	
	public static void resizarPantalla() {
		ControladorPantalla c = (ControladorPantalla) Main.sesion.get(Main.PANTALLA_ACTIVA);
		
		if (c!=null)
			c.resize(scene);
	}
	
	public static void main(String[] args) {
		for (int i=0;i<args.length;i++) {
			if (args[i].contains("DEBUG")) {
				String [] debug = args[i].split("=");
				if (debug[1].toUpperCase().equals("SI")) {
					Main.MODO_DEBUG = true;
				}
			}
		}
		
		launch(args);
	}
	
	public static int resolucion () {
		if (Main.scene.getWidth() > (1368 - 0.1*1368) &&
			Main.scene.getWidth() < (1368 + 0.1*1368) &&
			Main.scene.getHeight() < (849.5 + 0.1*860.5) &&
			Main.scene.getHeight() > (849.5 - 0.1*860.5))
			return Main.BAJA_RESOLUCION;
		
		if (Main.scene.getWidth() > (1920 - 0.1*1920) &&
				Main.scene.getWidth() < (1920 + 0.1*1920) &&
				Main.scene.getHeight() < (1017 + 0.1*1017) &&
				Main.scene.getHeight() > (1017 - 0.1*1017))
				return Main.ALTA_RESOLUCION;
		
		return Main.BAJA_RESOLUCION;
	}
	
	public static Node cargarPantalla (String pantalla) {
		try {if (!Main.MODO_DEBUG){
				 if (Main.lNodos.containsKey(pantalla)) {
					  Main.customWorkbench.getToolbarControlsLeft().addAll(Main.lBarraHerramientas.get(pantalla));
					  Main.customWorkbench.getToolbarControlsRight().addAll(Main.rBarraHerramientas.get(pantalla));
					  return lNodos.get(pantalla);
				  }
			 }
			  
			  FXMLLoader loader = new FXMLLoader();
		      ControladorPantalla controlPantalla = null;
		        		        
		      loader.setLocation(new URL(pantalla));
	
			   	
		      Node n = loader.load(); 
		      
		   // Create ContextMenu
		        
		        
		        n.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
		 
		            @Override
		            public void handle(ContextMenuEvent event) {
		            	if (Main.mostrarMenuContexto)
		            		Log.t("Aqu� se mostrar�a el men�");
				        	//Main.menuContexto.show(n,event.getSceneX(), event.getSceneY());
		            	else 
		            		Main.mostrarMenuContexto = true;
		            }
		        });
		        
		        n.setOnMouseClicked(new EventHandler<MouseEvent>() {
		   		 
		            @Override
		            public void handle(MouseEvent event) {
		            	Main.menuContexto.hide();
		            }
		        });
		        
	          ControladorPantalla c = loader.getController();
	          

		      if (Main.sesion!=null){
		    	  Main.sesion.put(Main.PANTALLA_ACTIVA, controlPantalla);
		      }
	          
	          n.getProperties().put(Main.CONTROLADOR, c);
	          Main.pantallaActiva = n;
	          c.resize(Main.scene);
	          
	          if (!Main.MODO_DEBUG){
		          if (!lNodos.containsKey(pantalla)) {				  
					  lNodos.put(pantalla,n);
					  ArrayList<ToolbarItem> lAux = new ArrayList<ToolbarItem>();
					  lAux.addAll(Main.customWorkbench.getToolbarControlsLeft());
					  Main.lBarraHerramientas.put(pantalla,lAux);
					  lAux = new ArrayList<ToolbarItem>();
					  lAux.addAll(Main.customWorkbench.getToolbarControlsRight());
					  Main.rBarraHerramientas.put(pantalla,lAux);
				  }
	          }
			  
		      return n;
		  } catch (Exception ex) {
			  Dialogo.error(null, ex);
		  }
		
		Node n = null;
    	n = Main.cargarPantalla(new PantallaBlanca().getFXML());
		 
		return n;
	}
	
	public static void cortinaON () {
	}
	
	public static void cortinaOFF () {
	}
	
}
