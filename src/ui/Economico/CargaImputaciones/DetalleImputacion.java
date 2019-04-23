package ui.Economico.CargaImputaciones;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.controlsfx.control.PopOver;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import model.beans.Estimacion;
import model.beans.Imputacion;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Semaforo;
import ui.Tabla;
import ui.Economico.CargaImputaciones.Tables.LineaDetalleImputacion;
import ui.Economico.Tarifas.InformaTarifa;
import ui.Recursos.GestionRecursos.AltaModRecurso;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;
import ui.popUps.PopUp;

public class DetalleImputacion implements ControladorPantalla  {
		
	public static final String fxml = "file:src/ui/Economico/CargaImputaciones/DetalleImputacion.fxml"; 
	

    @FXML
    private ImageView imAltaUsuario;
    public GestionBotones gbAltaUsuario;
    
    @FXML
    private ImageView imSemaforo;
    public Semaforo semaforo;

    @FXML
    private ImageView imAltaTarifa;
    public GestionBotones gbAltaTarifa;

    @FXML
    private ComboBox<?> cbSistema;

    @FXML
    private ImageView imEnlazarEstimacion;

    @FXML
    private TableView<Tableable> tResultado;
	public Tabla tablaResultado;
	
	CargaImputaciones padre = null;
	Imputacion imputacion = null;
	
	@Override
	public AnchorPane getAnchor() {
		return null;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		semaforo = new Semaforo(imSemaforo);
		
		gbAltaUsuario = new GestionBotones(imAltaUsuario, "AltaUsuario3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				try {
					altaUsuario();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Alta Usuario", this);	
		gbAltaUsuario.activarBoton();
		
		gbAltaTarifa = new GestionBotones(imAltaTarifa, "AltaTarifa3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				try {
					altaTarifa();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Alta Tarifa", this);	
		gbAltaTarifa.activarBoton();
		
		tablaResultado = new Tabla(tResultado,new LineaDetalleImputacion());
		
	}
	
	public void adscribir(CargaImputaciones padre, Imputacion i, Estimacion est) {
		this.padre = padre; 
		this.imputacion = i;
		
		HashMap<String, Object> detalle  = new HashMap<String, Object>();
		detalle.put(LineaDetalleImputacion.IMPUTACION, i);
		
		if (est!=null) {
			detalle.put(LineaDetalleImputacion.ESTIMACION, est);	
		}
		
		ArrayList<Object> lt = new ArrayList<Object>();
		lt.add(detalle);
		
		tablaResultado.pintaTabla(lt);
		
		if (i.recurso!=null) this.gbAltaUsuario.desActivarBoton();
		if (i.tarifa!=null) this.gbAltaTarifa.desActivarBoton();
		
		if (i.recurso==null || i.tarifa==null) this.semaforo.rojo();
	}
	
	public void altaUsuario() throws Exception{
		PopUp controlPantalla = new AltaModRecurso();
    	if (ParamTable.po!=null){
    		ParamTable.po.hide();
    		ParamTable.po = null;
        }
    	
    	FXMLLoader loader = new FXMLLoader();
    	loader.setLocation(new URL(controlPantalla.getControlFXML()));
        Pane pane = loader.load();
        controlPantalla = (PopUp) loader.getController();
        
        HashMap<String, Object> paramPaso = new HashMap<String, Object> ();
        paramPaso.put(LineaDetalleImputacion.IMPUTACION, this.imputacion);
        paramPaso.put("padre", this);
        
        controlPantalla.setParametrosPaso(paramPaso);
        ParamTable.po = new PopOver(pane);
        ParamTable.po.setTitle("");
        ParamTable.po.show(this.imAltaUsuario);
        ParamTable.po.setAnimated(true);
		ParamTable.po.setAutoHide(true);
	}
	
	public void altaTarifa() throws Exception{
		PopUp controlPantalla = new InformaTarifa();
    	if (ParamTable.po!=null){
    		ParamTable.po.hide();
    		ParamTable.po = null;
        }
    	
    	FXMLLoader loader = new FXMLLoader();
    	loader.setLocation(new URL(controlPantalla.getControlFXML()));
        Pane pane = loader.load();
        controlPantalla = (PopUp) loader.getController();
        
        HashMap<String, Object> paramPaso = new HashMap<String, Object> ();
        paramPaso.put(LineaDetalleImputacion.IMPUTACION, this.imputacion);
        paramPaso.put("padre", this);

        controlPantalla.setParametrosPaso(paramPaso);
        ParamTable.po = new PopOver(pane);
        ParamTable.po.setTitle("");
        ParamTable.po.show(this.imAltaTarifa);
        ParamTable.po.setAnimated(true);
		ParamTable.po.setAutoHide(true);
	}
	
	public void altaFinalizada() {
		if (ParamTable.po!=null){
    		ParamTable.po.hide();
    		ParamTable.po = null;
        }
		
		this.padre.recargar();
	}
	
}