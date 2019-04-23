package ui.Economico.CargaImputaciones;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFileChooser;

import controller.AnalizadorPresupuesto;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.beans.Coste;
import model.beans.Imputacion;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.constantes.Constantes;
import model.metadatos.TipoDato;
import model.utils.xls.ConsultaImputaciones;
import ui.GestionBotones;
import ui.Tabla;
import ui.Economico.CargaImputaciones.Tables.LineaCosteEconomico;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class CargaImputaciones implements ControladorPantalla  {
		
	public static final String fxml = "file:src/ui/Economico/CargaImputaciones/CargaImputaciones.fxml"; 
	
	@FXML
    private ImageView imBuscarFichero;
    private GestionBotones gbBuscarFichero;

    @FXML
    private ScrollPane scrImputaciones;
    
    @FXML
    private VBox vbImputaciones;

    @FXML
    private ScrollPane scrDetalleProy;

    @FXML
    private DatePicker tFdesde;

    @FXML
    private DatePicker tFhasta;

    @FXML
    private ImageView imAnalizar;
    private GestionBotones gbAnalizar;

    @FXML
    private TextField tFichero;
    
    @FXML
    private TableView<Tableable> tRestanteAnio;
	public Tabla tablaRestanteAnio;

    @FXML
    private TableView<Tableable> tRestanteTotal;
	public Tabla tablaRestanteTotal;

    @FXML
    private ComboBox<Proyecto> cbProyectos;
    
    public HashMap<Integer,ArrayList<Imputacion>> listaImputacionesProyecto = null;
	
	@Override
	public AnchorPane getAnchor() {
		return null;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		gbBuscarFichero = new GestionBotones(imBuscarFichero, "BuscaFichero3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				buscaFichero();
            } }, "Buscar fichero imputaciones", this);	
		gbBuscarFichero.activarBoton();
		gbAnalizar = new GestionBotones(imAnalizar, "Analizar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				try {
					analizaFichero ();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Analiza Fichero", this);	
		gbAnalizar.activarBoton();
		
		this.cbProyectos.setDisable(true);
		
		this.tFichero.setText("C:\\Users\\Oscar\\workspace\\Gestion Proyectos ENAGAS\\Imputaciones+DSI.xlsx");
		
		this.cbProyectos.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			if (cbProyectos.getValue()!=null) {
				try {
					cargaProyecto();					
				} catch (Exception ex) {					
				}
			}
		});

	}
	
	public void cargaProyecto() throws Exception{
		ArrayList<Imputacion> lImputaciones = null;
		lImputaciones = listaImputacionesProyecto.get(this.cbProyectos.getValue().id);
		
		Collections.sort(lImputaciones);
		
		vbImputaciones.getChildren().removeAll(vbImputaciones.getChildren());
		
		Iterator<Imputacion> itImputacion = lImputaciones.iterator();
		while (itImputacion.hasNext()) {
			Imputacion i = itImputacion.next();
			
			DetalleImputacion nueEstimacion = new DetalleImputacion();
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(new URL(nueEstimacion.getFXML()));
	        vbImputaciones.getChildren().add(loader.load());
	        nueEstimacion = loader.getController();
	        nueEstimacion.adscribir(this, i, null);
		}
		
		cargaPresupuesto();
	}
	
	public void recargar() {
		try {
			Proyecto p = this.cbProyectos.getValue();
			
			analizaFichero ();
			
			this.cbProyectos.setValue(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void analizaFichero () throws Exception {
		listaImputacionesProyecto = new HashMap<Integer,ArrayList<Imputacion>>();
		this.cbProyectos.getItems().removeAll(this.cbProyectos.getItems());
		vbImputaciones.getChildren().removeAll(vbImputaciones.getChildren());
		
		
		ConsultaImputaciones ci = new ConsultaImputaciones();
		ci.abrirArchivo(this.tFichero.getText(), 0);
		ArrayList<HashMap<String,Object>> listado = ci.leeFichero();
		ci.cerrarArchivo();
		
		 ArrayList<Imputacion> imputaciones = new Imputacion().generaImputacionesDesdeFichero(listado);
		 Iterator<Imputacion> itI = imputaciones.iterator();
		 while (itI.hasNext()) {
			 Imputacion i = itI.next();
			 ArrayList<Imputacion> lImputaciones = null;
			 
			 if (!this.cbProyectos.getItems().contains(i.proyecto)) {
				 this.cbProyectos.getItems().add(i.proyecto);
				 lImputaciones = new  ArrayList<Imputacion>();
				 listaImputacionesProyecto.put(i.proyecto.id, lImputaciones);
			 } else {
				 lImputaciones = listaImputacionesProyecto.get(i.proyecto.id);
			 }
			 
			 lImputaciones.add(i);
		 }
		 
		 this.cbProyectos.setDisable(false);
		 
	}
	
	public void buscaFichero() {
		JFileChooser selectorArchivos = new JFileChooser();
		selectorArchivos.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int resultado = selectorArchivos.showOpenDialog(null);
		
		if (resultado==0) {
			File archivo = selectorArchivos.getSelectedFile();
			
			this.tFichero.setText(archivo.getAbsolutePath());
		} else 
			this.tFichero.setText("");
	}
	
	public void cargaPresupuesto() {
		Proyecto p = this.cbProyectos.getValue();
		p.presupuestoActual = new Presupuesto();
		p.presupuestoActual = p.presupuestoActual.dameUltimaVersionPresupuesto(p);
		p.presupuestoActual.cargaCostes();
		
		AnalizadorPresupuesto ap = new AnalizadorPresupuesto();
		ap.construyePresupuestoMensualizado(p.presupuestoActual, Constantes.fechaActual(), null, null, null, null);
		
		this.tablaRestanteAnio = new Tabla(tRestanteAnio,new LineaCosteEconomico());
		this.tablaRestanteTotal = new Tabla(tRestanteTotal,new LineaCosteEconomico());
		
		HashMap<String,Coste> lCosteTotal = ap.getRestante(true, -1);
		HashMap<String,Coste> lCosteAnual = ap.getRestante(true, -1);
		
		this.tablaRestanteTotal.pintaTabla(TipoDato.toListaObjetos(lCosteTotal.values()));
		this.tablaRestanteAnio.pintaTabla(TipoDato.toListaObjetos(lCosteAnual.values()));
		
	}
	
}
