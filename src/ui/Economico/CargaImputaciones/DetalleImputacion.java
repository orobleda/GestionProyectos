package ui.Economico.CargaImputaciones;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import ui.Tabla;
import ui.Economico.CargaImputaciones.Tables.LineaDetalleImputacion;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class DetalleImputacion implements ControladorPantalla  {
		
	public static final String fxml = "file:src/ui/Economico/CargaImputaciones/DetalleImputacion.fxml"; 
	

    @FXML
    private ImageView imAltaUsuario;

    @FXML
    private ImageView imAltaTarifa;

    @FXML
    private ComboBox<?> cbSistema;

    @FXML
    private ImageView imEnlazarEstimacion;

    @FXML
    private TableView<Tableable> tResultado;
	public Tabla tablaResultado;
	
	@Override
	public AnchorPane getAnchor() {
		return null;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		/*gbBuscarFichero = new GestionBotones(imBuscarFichero, "BuscaFichero3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				buscaFichero();
            } }, "Buscar fichero imputaciones", this);	
		gbBuscarFichero.activarBoton();*/
		
		tablaResultado = new Tabla(tResultado,new LineaDetalleImputacion());
		tablaResultado.pintaTabla(new ArrayList<Object>());
	}
	
}
