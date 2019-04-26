package ui.Economico.CargaImputaciones;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFileChooser;

import controller.AnalizadorPresupuesto;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.beans.Concepto;
import model.beans.Coste;
import model.beans.Estimacion;
import model.beans.Imputacion;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import model.metadatos.TipoDato;
import model.utils.xls.ConsultaImputaciones;
import ui.GestionBotones;
import ui.Semaforo;
import ui.Tabla;
import ui.Economico.CargaImputaciones.Tables.LineaCosteEconomico;
import ui.Economico.CargaImputaciones.Tables.LineaResumenEconomico;
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
    private ImageView imSemaforoGeneral;
    public Semaforo semaforo;
    
    @FXML
    private TextField tFichero;
    
    @FXML
    private CheckBox chkResumen;
    
    @FXML
    private TableView<Tableable> tRestanteAnio;
	public Tabla tablaRestanteAnio;

    @FXML
    private TableView<Tableable> tRestanteTotal;
	public Tabla tablaRestanteTotal;
	
	@FXML
    private TableView<Tableable> tFotoInicial;
	public Tabla tablaFotoInicial;

    @FXML
    private VBox vbDetalle;
    

    @FXML
    private HBox hbEstadoProy;

    @FXML
    private VBox vbImputacionesGeneral;

    @FXML
    private ComboBox<Proyecto> cbProyectos;
    
    public HashMap<Integer,ArrayList<Imputacion>> listaImputacionesProyecto = null;
    public HashMap<Integer,ArrayList<Imputacion>> listaImputacionesAsignadasProyecto = null;
    public HashMap<Integer,Integer> listaSemaforosProyecto = null;
    
    public ArrayList<DetalleImputacion> lDetallesImputacion = null;
    
    public Proyecto proyecto;
    
    public AnalizadorPresupuesto ap;
    
    public ArrayList<Imputacion> listaImputacionesAsignadas = null;
    
    @FXML
    private TableView<Tableable> tResumen;
	public Tabla tablaResumen;
	
	@Override
	public AnchorPane getAnchor() {
		return null;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		listaImputacionesAsignadasProyecto = new HashMap<Integer,ArrayList<Imputacion>>(); 
		listaSemaforosProyecto = new HashMap<Integer,Integer>();
		
		semaforo = new Semaforo(imSemaforoGeneral);
		
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
					vbDetalle.setVisible(false);
					vbImputacionesGeneral.setVisible(false);
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
					ex.printStackTrace();
				}
			}
		});
		
		vbDetalle.setVisible(false);
		vbImputacionesGeneral.setVisible(false);
		this.chkResumen.setDisable(true);
		
		this.chkResumen.selectedProperty().addListener((ov, oldV, newV) -> { 
			try {
				mostrarResumen();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}
	
	public void mostrarResumen() throws Exception{
		if (!this.chkResumen.isSelected()) {
			this.hbEstadoProy.setVisible(true);
			this.cbProyectos.setDisable(false);
			this.cbProyectos.setValue(this.cbProyectos.getItems().get(0));
			cargaProyecto();
			this.vbDetalle.setVisible(true);
			return;
		}
		
		this.hbEstadoProy.setVisible(false);
		this.cbProyectos.setDisable(true);
		this.vbDetalle.setVisible(false);
		
		vbImputaciones.getChildren().removeAll(vbImputaciones.getChildren());
		
		ResumenCarga resCarga = new ResumenCarga();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(new URL(resCarga.getFXML()));
        vbImputaciones.getChildren().add(loader.load());
        resCarga = loader.getController();
        resCarga.adscribir(this, null);
	        
	}
	
	public void cargaProyecto() throws Exception{
		vbDetalle.setVisible(true);
		vbImputacionesGeneral.setVisible(true);
		
		ArrayList<Imputacion> lImputaciones = null;
		lImputaciones = listaImputacionesProyecto.get(this.cbProyectos.getValue().id);
		
		cargaPresupuesto();
		
		if (listaImputacionesAsignadasProyecto.containsKey(this.cbProyectos.getValue().id)) {
			listaImputacionesAsignadas = listaImputacionesAsignadasProyecto.get(this.cbProyectos.getValue().id);
		} else {
			listaImputacionesAsignadas = new ArrayList<Imputacion>();
			listaImputacionesAsignadasProyecto.put(this.cbProyectos.getValue().id,listaImputacionesAsignadas);
		}
		
		if (listaImputacionesAsignadasProyecto.size() == this.listaImputacionesProyecto.size()) {
			this.chkResumen.setDisable(false);
		}
				
		Collections.sort(lImputaciones);
		
		vbImputaciones.getChildren().removeAll(vbImputaciones.getChildren());
		
		lDetallesImputacion = new ArrayList<DetalleImputacion> ();
		
		Iterator<Imputacion> itImputacion = lImputaciones.iterator();
		while (itImputacion.hasNext()) {
			Imputacion i = itImputacion.next();
			
			DetalleImputacion nueEstimacion = new DetalleImputacion();
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(new URL(nueEstimacion.getFXML()));
	        vbImputaciones.getChildren().add(loader.load());
	        nueEstimacion = loader.getController();
	        nueEstimacion.adscribir(this, i, null);
	        lDetallesImputacion.add(nueEstimacion);
		}
		
		refrescaPresupuesto(null, this.listaImputacionesAsignadas);
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
		chkResumen.setDisable(true);
		
		listaImputacionesProyecto = new HashMap<Integer,ArrayList<Imputacion>>();
		this.cbProyectos.getItems().removeAll(this.cbProyectos.getItems());
		vbImputaciones.getChildren().removeAll(vbImputaciones.getChildren());
		
		
		ConsultaImputaciones ci = new ConsultaImputaciones();
		ci.abrirArchivo(this.tFichero.getText(), 0);
		ArrayList<HashMap<String,Object>> listado = ci.leeFichero();
		ci.cerrarArchivo();
		
		Date fInicio = FormateadorDatos.toDate(this.tFdesde.getValue());
		Date fFin = FormateadorDatos.toDate(this.tFhasta.getValue());
		
		 ArrayList<Imputacion> imputaciones = new Imputacion().generaImputacionesDesdeFichero(listado,fInicio,fFin);
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
		proyecto = this.cbProyectos.getValue();
		proyecto.presupuestoActual = new Presupuesto();
		proyecto.presupuestoActual = proyecto.presupuestoActual.dameUltimaVersionPresupuesto(proyecto);
		proyecto.presupuestoActual.cargaCostes();
		
		ap = new AnalizadorPresupuesto();
		ap.construyePresupuestoMensualizado(proyecto.presupuestoActual, Constantes.fechaActual(), null, null, null, null);
		this.tablaFotoInicial = new Tabla(tFotoInicial,new LineaCosteEconomico());
		HashMap<String,Coste> lCosteTotal = ap.getRestante(true, -1,AnalizadorPresupuesto.MODO_RESTANTE,false);
		this.tablaFotoInicial.pintaTabla(TipoDato.toListaObjetos(lCosteTotal.values()));
		
	}
	
	public void refrescaPresupuesto(ArrayList<Estimacion> lEstimaciones, ArrayList<Imputacion> lImputacion) {
		ap = new AnalizadorPresupuesto();
		ap.construyePresupuestoMensualizado(proyecto.presupuestoActual, Constantes.fechaActual(), lEstimaciones, lImputacion, null, null);
		
		this.tablaRestanteAnio = new Tabla(tRestanteAnio,new LineaCosteEconomico());
		this.tablaRestanteTotal = new Tabla(tRestanteTotal,new LineaCosteEconomico());
		this.tablaResumen = new Tabla(tResumen,new LineaResumenEconomico());
		
		Calendar c = Calendar.getInstance();
		c.setTime(Constantes.fechaActual());
		
		HashMap<String,Coste> lCosteTotal = ap.getRestante(true, -1,AnalizadorPresupuesto.MODO_RESTANTE, false);
		HashMap<String,Coste> lCosteAnual = ap.getRestante(false, c.get(Calendar.YEAR),AnalizadorPresupuesto.MODO_RESTANTE, false);
		
		this.tablaRestanteTotal.pintaTabla(TipoDato.toListaObjetos(lCosteTotal.values()));
		this.tablaRestanteAnio.pintaTabla(TipoDato.toListaObjetos(lCosteAnual.values()));
		
		HashMap<String,Coste> lEstimado = ap.getRestante(true, -1, AnalizadorPresupuesto.MODO_ESTIMADO, true);
		HashMap<String,Coste> lImputado = ap.getRestante(true, -1, AnalizadorPresupuesto.MODO_IMPUTADO, true);
		HashMap<Integer,Coste> lPresupuestado = proyecto.presupuestoActual.costes; 
		
		ArrayList<Object> lElementos = LineaResumenEconomico.totaliza(lEstimado, lImputado, lPresupuestado);
		this.tablaResumen.pintaTabla(lElementos);
		
		int estadoGeneral = Semaforo.VERDE;
		
		Iterator<DetalleImputacion> iDetallesImputacion = this.lDetallesImputacion.iterator();
		while (iDetallesImputacion.hasNext()) {
			DetalleImputacion di = iDetallesImputacion.next();
			MetaConcepto mc = di.imputacion.natCoste;
			Sistema s = di.imputacion.sistema;
			
			if (di.semaforo.estado != Semaforo.ROJO) {
				Coste cAux = lCosteTotal.get(s.codigo);
				Concepto concAux = cAux.conceptosCoste.get(mc.codigo);
				
				if (concAux!=null) {
					if (concAux.valor<0) {
						di.semaforo.ambar();
					}
				}
			}
			
			if (di.semaforo.estado == Semaforo.ROJO) estadoGeneral = Semaforo.ROJO;
			if (di.semaforo.estado == Semaforo.AMBAR && estadoGeneral != Semaforo.ROJO) estadoGeneral = Semaforo.AMBAR;
		}
		
		semaforo.asignaEstado(estadoGeneral);
		
		if (listaSemaforosProyecto.containsKey(this.cbProyectos.getValue().id))
			listaSemaforosProyecto.remove(this.cbProyectos.getValue().id);
		
		listaSemaforosProyecto.put(this.cbProyectos.getValue().id, estadoGeneral);
	}
	
}
