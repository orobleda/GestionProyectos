package application;
	
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.HiddenSidesPane;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.constantes.CargaInicial;
import ui.GestionBotones;
import ui.Persiana;
import ui.Administracion.Festivos.GestionFestivos;
import ui.Administracion.Parametricas.AdministracionParametros;
import ui.Economico.CargaImputaciones.CargaImputaciones;
import ui.Economico.ControlPresupuestario.ControlPresupuestario;
import ui.Economico.EstimacionesInternas.EstimacionesInternas;
import ui.Economico.EstimacionesValoraciones.EstimacionesValoraciones;
import ui.Economico.GestionPresupuestos.GestionPresupuestos;
import ui.GestionProyectos.AltaModProyecto;
import ui.Recursos.GestionProveedores.AltaModProveedor;
import ui.Recursos.GestionRecursos.AltaModRecurso;
import ui.Recursos.GestionTarifas.AsignacionTarifas;
import ui.Recursos.GestionVacaciones.GestionVacaciones;
import ui.interfaces.ControladorPantalla;
import ui.planificacion.Faseado.Faseado;


public class Main extends Application {
	
	public static final String PANTALLA_ACTIVA = "pantalla_Activa"; 
	
	public static int SOLICITUDES = 0;
	public static int RECURSOS = 1;
	public static int ECONOMICO = 2;
	public static int PLANIFICACION = 3;
	public static int REPORTES = 4;
	public static int AJUSTES = 5;
	
	public HashMap<Integer, HashMap<String, ControladorPantalla>> listaMenu = null;
	
	public static HashMap<String, Object> sesion = null;
	public HiddenSidesPane pane = null;
	
	HBox hb = null;
	
	public HBox solicRecursos = null;
	public HBox econPlani = null;
	public HBox reporAjustes = null;
	
	public ArrayList<GestionBotones> listaGBs = null;
		
	@Override
	public void start(Stage primaryStage) {
		try {
			pueblaMenu();
			
			listaGBs = new ArrayList<GestionBotones>();
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
	        
	        CargaImputaciones c = new CargaImputaciones();
	        
	        Main.sesion.put(Main.PANTALLA_ACTIVA, c);
	        FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new URL(c.getFXML()));
            	        
	        pane = new HiddenSidesPane();
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

	private void mostrarMenu (HiddenSidesPane pane) {
		VBox vb = new VBox();
	    vb.setAlignment(Pos.CENTER);
	    
	    HBox hb = new HBox();
	    ImageView boton = new ImageView();
	    GestionBotones gbBoton = new GestionBotones(boton, "Cerrar3R", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				pane.setPinnedSide(null);
            } }, "Cerrar Menú", this);	
	    
	    gbBoton.activarBoton();
	    hb.setAlignment(Pos.TOP_RIGHT);
	    hb.getChildren().add(boton);	
	    
	    vb.getChildren().add(hb);
	    
	   hb.setPadding(new Insets(0,0,10,0));
	    
		vb.getChildren().add(insertaSolicitudesRecursos());
		vb.getChildren().add(insertaPlanificacionEconomico());
		vb.getChildren().add(insertaAjustesReporte());
		vb.getStyleClass().add("PanelLateral");
	    
	    BorderPane p = new BorderPane();
	    p.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
	    p.setCenter(vb);	    
	    
	    pane.setLeft(p);
	}
	
	private VBox insertaSolicitudesRecursos() {
		VBox vb = new VBox();
		
		hb = new HBox();
	    
	    ImageView boton = new ImageView();
	    GestionBotones gbBoton = new GestionBotones(boton, "Solicitudes", true, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				cambiaBoton(Main.SOLICITUDES);
            } }, "Solicitudes");	
		gbBoton.activarBoton();
		hb.getChildren().add(boton);
		listaGBs.add(gbBoton);
		
		boton = new ImageView();
	    gbBoton = new GestionBotones(boton, "Recursos", true, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				cambiaBoton(Main.RECURSOS);
            } }, "Recursos");	
		gbBoton.activarBoton();
		hb.getChildren().add(boton);
		listaGBs.add(gbBoton);
		
		vb.getChildren().add(hb);

		solicRecursos = new HBox();
		vb.getChildren().add(solicRecursos);
		
		return vb;
	}
	
	private VBox insertaAjustesReporte() {
		VBox vb = new VBox();
		
		hb = new HBox();
	    
	    ImageView boton = new ImageView();
	    GestionBotones gbBoton = new GestionBotones(boton, "Reporting", true, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				cambiaBoton(Main.REPORTES);
            } }, "Reporting");	
		gbBoton.activarBoton();
		hb.getChildren().add(boton);
		listaGBs.add(gbBoton);
		
		boton = new ImageView();
	    gbBoton = new GestionBotones(boton, "Ajustes", true, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				cambiaBoton(Main.AJUSTES);
            } }, "Ajustes");	
		gbBoton.activarBoton();
		hb.getChildren().add(boton);
		listaGBs.add(gbBoton);
		
		vb.getChildren().add(hb);

		this.reporAjustes = new HBox();
		vb.getChildren().add(reporAjustes);
		
		return vb;
	}
	
	private VBox insertaPlanificacionEconomico() {
		VBox vb = new VBox();
		
		hb = new HBox();
		
		ImageView boton = new ImageView();
		GestionBotones gbBoton = new GestionBotones(boton, "Economico", true, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				cambiaBoton(Main.ECONOMICO);
            } }, "Económico");	
		gbBoton.activarBoton();
		hb.getChildren().add(boton);
		listaGBs.add(gbBoton);
	    
	     boton = new ImageView();
	     gbBoton = new GestionBotones(boton, "Planificacion", true, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				cambiaBoton(Main.PLANIFICACION);
            } }, "Planificación");	
		gbBoton.activarBoton();
		hb.getChildren().add(boton);
		listaGBs.add(gbBoton);
		
		vb.getChildren().add(hb);

		econPlani = new HBox();
		vb.getChildren().add(econPlani);
		
		return vb;
	}
	
	private void cambiaBoton(int index) {
		Iterator<GestionBotones> itGb = this.listaGBs.iterator();
		int contador = 0;
		pane.setPinnedSide(Side.LEFT);
		
		while (itGb.hasNext()) {
			GestionBotones gb = itGb.next();
			
			if (contador == index) {
				if (!gb.liberado) {
					gb.liberarBoton();
					solicRecursos.getChildren().removeAll(solicRecursos.getChildren());
				} else {
					gb.pulsarBoton();
					
					if (index == Main.SOLICITUDES) insertaElementos(solicRecursos, index);
					if (index == Main.RECURSOS) insertaElementos(solicRecursos, index);
					if (index == Main.ECONOMICO) insertaElementos(this.econPlani, index);
					if (index == Main.PLANIFICACION) insertaElementos(this.econPlani, index);
					if (index == Main.AJUSTES) insertaElementos(this.reporAjustes, index);
					if (index == Main.REPORTES) insertaElementos(this.reporAjustes, index);
				}
			} else {
				gb.liberarBoton();
			}
			
			contador ++;
		}
		
	}
	
	private Label dameOpcionMenu(int idSeleccion, String opcion){
		Label l = new Label(opcion);
		l.getStyleClass().add("elementoMenu");
		l.getProperties().put("CTRLPANTALLA", this.listaMenu.get(idSeleccion).get(opcion));
		
		l.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t)
            {
            	ControladorPantalla cp = (ControladorPantalla) l.getProperties().get("CTRLPANTALLA");
            	click(cp);
            }
        });
		
		return l;
	}
	
	private VBox getContenedorMenu() {
		VBox vb = new VBox();
		vb.getStyleClass().add("elementosMenu");
		vb.prefWidthProperty().bind(hb.widthProperty().multiply(1));
		vb.setPadding(new Insets(15,15,15,70));
		return vb;
	}
	
	private void insertaElementos(HBox hb, int idSeleccion) {
		VBox vb = getContenedorMenu();
		Iterator<String> itOpciones = this.listaMenu.get(idSeleccion).keySet().iterator();
		while (itOpciones.hasNext()) {
			vb.getChildren().add(dameOpcionMenu(idSeleccion, itOpciones.next()));
		}
		
		solicRecursos.getChildren().removeAll(solicRecursos.getChildren());
		econPlani.getChildren().removeAll(econPlani.getChildren());
		reporAjustes.getChildren().removeAll(reporAjustes.getChildren());
		
		hb.getChildren().removeAll(hb.getChildren());
		hb.getChildren().add(vb);
		
	}
	
	private void pueblaMenu() {
		listaMenu = new HashMap<Integer, HashMap<String, ControladorPantalla>>();
		
		HashMap<String, ControladorPantalla> opcionMenu = new HashMap<String, ControladorPantalla>();
		listaMenu.put(Main.SOLICITUDES, opcionMenu);
		opcionMenu.put("Gestión solicitudes", new AltaModProyecto());
		
		opcionMenu = new HashMap<String, ControladorPantalla>();
		listaMenu.put(Main.RECURSOS, opcionMenu);
		opcionMenu.put("Gestión Recursos", new AltaModRecurso());
		opcionMenu.put("Gestión Proveedores", new AltaModProveedor());
		opcionMenu.put("Asignación Tarifas", new AsignacionTarifas());
		opcionMenu.put("Gestión horas trabajadas", new GestionVacaciones());
		
		opcionMenu = new HashMap<String, ControladorPantalla>();
		listaMenu.put(Main.ECONOMICO, opcionMenu);
		opcionMenu.put("Gestión Estimaciones", new EstimacionesValoraciones());
		opcionMenu.put("Gestión Presupuestos", new GestionPresupuestos());
		opcionMenu.put("Planificación Económica", new ControlPresupuestario());
		opcionMenu.put("Estimaciones Por Horas", new EstimacionesInternas());
		opcionMenu.put("Gestión Tarifas", new ui.Economico.Tarifas.GestionTarifas());
		opcionMenu.put("Alta de Imputaciones", new CargaImputaciones());
		
		opcionMenu = new HashMap<String, ControladorPantalla>();
		listaMenu.put(Main.PLANIFICACION, opcionMenu);
		opcionMenu.put("Faseado Proyectos", new Faseado());

		opcionMenu = new HashMap<String, ControladorPantalla>();
		listaMenu.put(Main.AJUSTES, opcionMenu);
		opcionMenu.put("Gestión Festivos", new GestionFestivos());
		opcionMenu.put("Gestión paramétricas", new AdministracionParametros());
		
		opcionMenu = new HashMap<String, ControladorPantalla>();
		listaMenu.put(Main.REPORTES, opcionMenu);		
	}
	
	private void click(ControladorPantalla ctl) {
        try{
        	pane.setPinnedSide(null);
        	
	        FXMLLoader loader = new FXMLLoader();
	        ControladorPantalla controlPantalla = ctl;
	        Main.sesion.put(Main.PANTALLA_ACTIVA, controlPantalla);
	        		        
	        if (controlPantalla!=null){
	        	loader.setLocation(new URL(controlPantalla.getFXML()));
	        	pane.setContent(loader.load());
	        }
	        
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
}
