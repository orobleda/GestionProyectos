package ui.Economico.CargaImputaciones;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.Estimacion;
import model.beans.Imputacion;
import ui.GestionBotones;
import ui.Tabla;
import ui.Economico.CargaImputaciones.Tables.LineaDetalleImputacion;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class DetalleImputacion implements ControladorPantalla  {
		
	public static final String fxml = "file:src/ui/Economico/CargaImputaciones/DetalleImputacion.fxml"; 
	

    @FXML
    private ImageView imAltaUsuario;
    public GestionBotones gbAltaUsuario;

    @FXML
    private ImageView imAltaTarifa;

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
		gbAltaUsuario = new GestionBotones(imAltaUsuario, "AltaUsuario3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				
            } }, "Alta Usuario", this);	
		gbAltaUsuario.activarBoton();
		
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
	}
	
}
