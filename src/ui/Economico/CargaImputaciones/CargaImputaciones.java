package ui.Economico.CargaImputaciones;

import java.io.File;
import java.net.URL;

import javax.swing.JFileChooser;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import ui.GestionBotones;
import ui.interfaces.ControladorPantalla;

public class CargaImputaciones implements ControladorPantalla  {
		
	public static final String fxml = "file:src/ui/Economico/CargaImputaciones/CargaImputaciones.fxml"; 
	
    @FXML
    private ImageView imBuscarFichero;
    private GestionBotones gbBuscarFichero;

    @FXML
    private ScrollPane scrImputaciones;

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
    private ComboBox<?> cbProyectos;
	
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
					cargaImputaciones();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Analiza Fichero", this);	
		gbAnalizar.activarBoton();

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
	
	public void cargaImputaciones() throws Exception{
		DetalleImputacion nueEstimacion = new DetalleImputacion();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(new URL(nueEstimacion.getFXML()));
        scrImputaciones.setContent(loader.load());
        nueEstimacion = loader.getController();
	}
	
}
