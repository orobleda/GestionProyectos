package application;
	
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.HiddenSidesPane;

import com.dlsc.workbenchfx.Workbench;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.constantes.CargaInicial;
import ui.GestionBotones;
import ui.Administracion.BackupBD.BackupBD;
import ui.Administracion.Festivos.GestionFestivos;
import ui.Administracion.Parametricas.AdministracionParametros;
import ui.Economico.CargaImputaciones.CargaImputaciones;
import ui.Economico.ControlPresupuestario.ControlPresupuestario;
import ui.Economico.EstimacionesInternas.EstimacionesInternas;
import ui.Economico.EstimacionesValoraciones.EstimacionesValoraciones;
import ui.Economico.GestionPresupuestos.GestionPresupuestos;
import ui.GestionProyectos.AltaModProyecto;
import ui.PanelPrincipal.PanelPrincipal;
import ui.Recursos.GestionProveedores.AltaModProveedor;
import ui.Recursos.GestionRecursos.AltaModRecurso;
import ui.Recursos.GestionVacaciones.GestionVacaciones;
import ui.interfaces.ControladorPantalla;
import ui.planificacion.Faseado.Faseado;
import ui.reporting.ReportingInformes;
import workbench.modulos.Administracion;
import workbench.modulos.Planificacion;
import workbench.utils.MenuNavegacion;


public class Main_OLD extends Application {
	
	public static final String PANTALLA_ACTIVA = "pantalla_Activa"; 
	public static final String CONTROLADOR = "controlador"; 
	static final Point2D WINDOW_UPPER_LEFT_CORNER  =  new  Point2D( 210, 50 ) ;
	
	public static int SOLICITUDES = 0;
	public static int RECURSOS = 1;
	public static int ECONOMICO = 2;
	public static int PLANIFICACION = 3;
	public static int REPORTES = 4;
	public static int AJUSTES = 5;
	
	private MenuNavegacion navigationDrawer;
	
	public static Rectangle curtain = null;
	public static ImageView imLoading = null;
	
	public HashMap<Integer, HashMap<String, ControladorPantalla>> listaMenu = null;
	public HashMap<String, String> listaImgsPeque = null;
	
	public static HashMap<String, Object> sesion = null;
	public static Scene scene;
	public HiddenSidesPane pane = null;
	
	HBox hb = null;
	
	public HBox solicRecursos = null;
	public HBox econPlani = null;
	public HBox reporAjustes = null;
	
	public ArrayList<GestionBotones> listaGBs = null;
		
	@Override
	public void start(Stage primaryStage) {
		try {/*
			new Log("DEBUG");
			
			Log.t("Hola");
			
			imLoading = new ImageView();
	        imLoading.setLayoutX(((Screen.getPrimary().getBounds().getMaxX() - Screen.getPrimary().getBounds().getMinX())/2.5)-imLoading.getBoundsInLocal().getWidth()/2.5);
	        imLoading.setLayoutY(((Screen.getPrimary().getBounds().getMaxY() - Screen.getPrimary().getBounds().getMinY())/2.5)-imLoading.getBoundsInLocal().getHeight()/2.5);
	        imLoading.setImage(new Image("loading.gif"));
	        
	        pueblaMenu();
			
			listaGBs = new ArrayList<GestionBotones>();*/
			Main_OLD.sesion = new HashMap<String, Object>();
			
			new CargaInicial().load();
			/*
			//ReplicaBD.fuerzaGuardado(null); 
	        /*
			BorderPane panelBase = new BorderPane();
			
	        ScrollPane root = new ScrollPane();
	        
	        root.setFitToHeight(true);
	        root.setFitToWidth(true);
	        root.setHbarPolicy(ScrollBarPolicy.ALWAYS);
	        panelBase.setCenter(root);
	        */
			
			navigationDrawer = new MenuNavegacion();
			
	        Workbench customWorkbench = Workbench.builder(
	                new Planificacion(), new Administracion()
	            )
	        		.navigationDrawer(navigationDrawer)
	        		.build();

	        scene = new Scene(customWorkbench);/*
	        scene.getStylesheets().add("application.css");
	        
	        scene.heightProperty().addListener((observable, oldvalue, newvalue) ->
	        	Main.resizarPantalla()
	        );*/
	        /*
	        GestionVacaciones c = new GestionVacaciones();
	        Main.sesion.put(Main.PANTALLA_ACTIVA, c);
	        
	        FXMLLoader loader = new FXMLLoader();
            loader.setLocation(new URL(c.getFXML()));
            c = loader.getController();
	        Main.sesion.put(Main.PANTALLA_ACTIVA, c);
            	        
	        pane = new HiddenSidesPane();
	        pane.setPadding(new Insets(0, 0, 0, 0));	        
	        pane.getStyleClass().add("body");
	        
	        mostrarMenu (pane);
	        panelBase.setCenter(pane);
	        */
	        primaryStage.setMaximized(true);	        
	        primaryStage.setTitle("Gestión Proyectos");			
			primaryStage.setScene(scene);
			primaryStage.show();
			/*
			click(c);*/
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void resizarPantalla() {
		try {
			ControladorPantalla c = (ControladorPantalla) Main_OLD.sesion.get(Main_OLD.PANTALLA_ACTIVA);
			
			if (c!=null)
				c.resize(scene);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void cortinaON () {
		curtain.setVisible(true);
		imLoading.setVisible(true);
	}
	
	public static void cortinaOFF () {
		curtain.setVisible(false);
		imLoading.setVisible(false);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static Node cargarPantalla (String pantalla) {
		try {
			  FXMLLoader loader = new FXMLLoader();
		      ControladorPantalla controlPantalla = new PanelPrincipal();
		        		        
		      loader.setLocation(new URL(controlPantalla.getFXML()));
	
		      if (Main_OLD.sesion!=null){
		    	  Main_OLD.sesion.put(Main_OLD.PANTALLA_ACTIVA, controlPantalla);
		      }
			   	
		      Node n = loader.load(); 
	          ControladorPantalla c = loader.getController();
	          
	          n.getProperties().put(Main_OLD.CONTROLADOR, c);
			  
		      return n;
		  } catch (Exception ex) {
			  ex.printStackTrace();
		  }
		  
		  return null;
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
	    
	   Label l = new Label("Inicio");
		l.setOnMouseClicked(new EventHandler<MouseEvent>()
       {
           @Override
           public void handle(MouseEvent t)
           {
        	   click(new PanelPrincipal());
           }
       });
		vb.getChildren().add(l);
		vb.getChildren().add(insertaSolicitudesRecursos());
		vb.getChildren().add(insertaPlanificacionEconomico());
		vb.getChildren().add(insertaAjustesReporte());
		vb.getStyleClass().add("PanelLateral");
	    
	    BorderPane p = new BorderPane();
	    p.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
	    p.setCenter(vb);	    
	    
	    pane.setLeft(p);
	    
	    click(new PanelPrincipal());
	}
	
	private VBox insertaSolicitudesRecursos() {
		VBox vb = new VBox();
		
		hb = new HBox();
	    
	    ImageView boton = new ImageView();
	    GestionBotones gbBoton = new GestionBotones(boton, "Solicitudes", true, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				cambiaBoton(Main_OLD.SOLICITUDES);
            } }, "Solicitudes");	
		gbBoton.activarBoton();
		hb.getChildren().add(boton);
		listaGBs.add(gbBoton);
		
		boton = new ImageView();
	    gbBoton = new GestionBotones(boton, "Recursos", true, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				cambiaBoton(Main_OLD.RECURSOS);
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
				cambiaBoton(Main_OLD.REPORTES);
            } }, "Reporting");	
		gbBoton.activarBoton();
		hb.getChildren().add(boton);
		listaGBs.add(gbBoton);
		
		boton = new ImageView();
	    gbBoton = new GestionBotones(boton, "Ajustes", true, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				cambiaBoton(Main_OLD.AJUSTES);
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
				cambiaBoton(Main_OLD.ECONOMICO);
            } }, "Económico");	
		gbBoton.activarBoton();
		hb.getChildren().add(boton);
		listaGBs.add(gbBoton);
	    
	     boton = new ImageView();
	     gbBoton = new GestionBotones(boton, "Planificacion", true, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				cambiaBoton(Main_OLD.PLANIFICACION);
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
					
					if (index == Main_OLD.SOLICITUDES) insertaElementos(solicRecursos, index);
					if (index == Main_OLD.RECURSOS) insertaElementos(solicRecursos, index);
					if (index == Main_OLD.ECONOMICO) insertaElementos(this.econPlani, index);
					if (index == Main_OLD.PLANIFICACION) insertaElementos(this.econPlani, index);
					if (index == Main_OLD.AJUSTES) insertaElementos(this.reporAjustes, index);
					if (index == Main_OLD.REPORTES) insertaElementos(this.reporAjustes, index);
				}
			} else {
				gb.liberarBoton();
			}
			
			contador ++;
		}
		
	}
	
	private HBox dameOpcionMenu(int idSeleccion, String opcion){
		HBox salida = new HBox();
		
		ImageView iv = new ImageView();
		iv.setImage(new Image(this.listaImgsPeque.get(opcion)));
		
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
		
		salida.getChildren().add(iv);
		HBox.setMargin(iv, new Insets(0,25,0,0));
		salida.getChildren().add(l);
		salida.setAlignment(Pos.CENTER_LEFT);
		
		return salida;
	}
	
	private VBox getContenedorMenu() {
		VBox vb = new VBox();
		vb.getStyleClass().add("elementosMenu");
		vb.prefWidthProperty().bind(hb.widthProperty().multiply(1));
		vb.setPadding(new Insets(15,15,15,40));
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
		listaImgsPeque = new HashMap<String, String> ();
		
		HashMap<String, ControladorPantalla> opcionMenu = new HashMap<String, ControladorPantalla>();
		listaMenu.put(Main_OLD.SOLICITUDES, opcionMenu);
		opcionMenu.put("Gestión solicitudes", new AltaModProyecto());            
		
		opcionMenu = new HashMap<String, ControladorPantalla>();
		listaMenu.put(Main_OLD.RECURSOS, opcionMenu);
		opcionMenu.put("Gestión Recursos", new AltaModRecurso());            
		opcionMenu.put("Gestión Proveedores", new AltaModProveedor());            
		opcionMenu.put("Gestión horas trabajadas", new GestionVacaciones());            
		
		opcionMenu = new HashMap<String, ControladorPantalla>();
		listaMenu.put(Main_OLD.ECONOMICO, opcionMenu);
		opcionMenu.put("Gestión Estimaciones", new EstimacionesValoraciones());            
		opcionMenu.put("Gestión Presupuestos", new GestionPresupuestos());            
		opcionMenu.put("Planificación Económica", new ControlPresupuestario());            
		opcionMenu.put("Estimaciones Por Horas", new EstimacionesInternas());            
		opcionMenu.put("Gestión Tarifas", new ui.Economico.Tarifas.GestionTarifas());            
		opcionMenu.put("Alta de Imputaciones", new CargaImputaciones());            
		
		opcionMenu = new HashMap<String, ControladorPantalla>();
		listaMenu.put(Main_OLD.PLANIFICACION, opcionMenu);
		opcionMenu.put("Faseado Proyectos", new Faseado());            

		opcionMenu = new HashMap<String, ControladorPantalla>();
		listaMenu.put(Main_OLD.AJUSTES, opcionMenu);
		opcionMenu.put("Gestión Festivos", new GestionFestivos());            
		opcionMenu.put("Gestión paramétricas", new AdministracionParametros());   
		opcionMenu.put("Copias Seguridad", new BackupBD());           
		
		opcionMenu = new HashMap<String, ControladorPantalla>();
		listaMenu.put(Main_OLD.REPORTES, opcionMenu);	
		opcionMenu.put("Descarga Informes", new ReportingInformes()); 
		
		listaImgsPeque.put("Gestión solicitudes", "PequeGestionSolicitudes.png"); 
		listaImgsPeque.put("Gestión Recursos", "PequeConsultaRecurso.png");
		listaImgsPeque.put("Gestión Proveedores", "PequeConsultaProveedor.png");
		listaImgsPeque.put("Gestión horas trabajadas", "PequeGestionHoras.png");
		listaImgsPeque.put("Gestión Estimaciones", "PequeGestionEstimacion.png"); 
		listaImgsPeque.put("Gestión Presupuestos", "PequeGestionPresupuestos.png");
		listaImgsPeque.put("Planificación Económica", "PequePlanificacionEconomica.png");
		listaImgsPeque.put("Estimaciones Por Horas", "PequeEstimacionesHoras.png"); 
		listaImgsPeque.put("Gestión Tarifas", "PequeGestionTarifas.png"); 
		listaImgsPeque.put("Alta de Imputaciones", "PequeCargaImputaciones.png"); 
		listaImgsPeque.put("Faseado Proyectos", "PequeFaseadoProyectos.png"); 
		listaImgsPeque.put("Gestión Festivos", "PequeCalendario.png"); 
		listaImgsPeque.put("Gestión paramétricas", "PequeAjustes.png");
		listaImgsPeque.put("Descarga Informes", "PequeAjustes.png"); 
		listaImgsPeque.put("Copias Seguridad", "PequeAjustes.png"); 
	}
	
	private void click(ControladorPantalla ctl) {
        try{
        	pane.setPinnedSide(null);
        	
	        FXMLLoader loader = new FXMLLoader();
	        ControladorPantalla controlPantalla = ctl;
	        		        
	        if (controlPantalla!=null){
	        	loader.setLocation(new URL(controlPantalla.getFXML()));

		        Main_OLD.sesion.put(Main_OLD.PANTALLA_ACTIVA, controlPantalla);
	        	
	        	VBox vbContenedorGlobal = new VBox();
		        HBox hbContenidoAplicacion = new HBox();
		        hbContenidoAplicacion.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		        vbContenedorGlobal.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);	        
		        
		        vbContenedorGlobal.getChildren().add(creaCortina(hbContenidoAplicacion));
		        
		        Main_OLD.cortinaON();
		        
		        pane.setContent(vbContenedorGlobal);
		        		        
		             
		        Platform.runLater(() -> {
                    try {
                    	hbContenidoAplicacion.getChildren().add(loader.load()); 
                    	ControladorPantalla c = loader.getController();
                    	c.resize(scene);
        		        Main_OLD.sesion.put(Main_OLD.PANTALLA_ACTIVA, c);
                		
                		Main_OLD.cortinaOFF();
            	        //c.start();
                    } catch (Exception ex) {
                    	ex.printStackTrace();
                    }
		        }
                );
		        		        
	        }
	        
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public Group creaCortina(HBox contenidoAplicacion) {
		curtain = new Rectangle( Screen.getPrimary().getBounds().getMinX()
        		,Screen.getPrimary().getBounds().getMinY(),Screen.getPrimary().getBounds().getMaxX()*4 /*- Screen.getPrimary().getBounds().getMinX()*/,
        		Screen.getPrimary().getBounds().getMaxY()*4 /*- Screen.getPrimary().getBounds().getMinY()*/) ;
        curtain.getStyleClass().add("cortina");
        
        if (imLoading == null) {
        	imLoading = new ImageView();
	        imLoading.setLayoutX(((Screen.getPrimary().getBounds().getMaxX() - Screen.getPrimary().getBounds().getMinX())/2)-imLoading.getBoundsInLocal().getWidth()/2);
	        imLoading.setLayoutY(((Screen.getPrimary().getBounds().getMaxY() - Screen.getPrimary().getBounds().getMinY())/3)-imLoading.getBoundsInLocal().getHeight()/3);
	        imLoading.setImage(new Image("loading.gif"));
        } else {
        	imLoading.setLayoutX(((Screen.getPrimary().getBounds().getMaxX() - Screen.getPrimary().getBounds().getMinX())/2)-imLoading.getBoundsInLocal().getWidth()/2);
	        imLoading.setLayoutY(((Screen.getPrimary().getBounds().getMaxY() - Screen.getPrimary().getBounds().getMinY())/3)-imLoading.getBoundsInLocal().getHeight()/3);
        }
        
        Group group_for_curtains_etc = new Group() ;
        group_for_curtains_etc.setManaged( false ) ;
        group_for_curtains_etc.getChildren().addAll( contenidoAplicacion,curtain,imLoading ) ;
        
        return group_for_curtains_etc;
	}
	
}
