package ui.Administracion.Parametricas;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import application.Main;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.beans.Parametro;
import model.beans.Proyecto;
import model.constantes.CargaInicial;
import model.constantes.Constantes;
import model.utils.db.ConsultaBD;
import ui.Dialogo;
import ui.GestionBotones;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Propiediable;

public class AdministracionParametros implements ControladorPantalla {
	
	public static final String fxml = "file:src/ui/Administracion/Parametricas/AdministracionParametros.fxml";

	GestionParametros gestPar = null;
	
    @FXML
    private HBox hbCentro;
    
    @FXML
    private ComboBox<String> cbElemento;

    @FXML
    private ComboBox<Object> cbidElemento;

    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;

	@FXML
	private AnchorPane anchor;
	
	@Override
	public void resize(Scene escena) {
		
	}
	
	
	public void initialize(){
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					guardarCambios();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Guardar Cambios");
		this.gbGuardar.desActivarBoton();
		
		GestionParametros c = new GestionParametros();
        
		try {
			
			cbElemento.getItems().addAll(Propiediable.SUBCLASES);
			this.cbidElemento.setDisable(true);
						
			cbElemento.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
				ArrayList<Object> listaValores = Propiediable.listaSubValores(newValue);
				
				if (listaValores == null) {
					this.cbidElemento.setDisable(true);
			        pintaTabla(cbElemento.getValue(), null);
				} else {
					this.cbidElemento.setDisable(false);
					this.cbidElemento.getItems().removeAll(this.cbidElemento.getItems());
					this.cbidElemento.getItems().addAll(listaValores);
				}
			}
		    );
			
			cbidElemento.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
				pintaTabla(cbElemento.getValue(), newValue);
			}
		    );
			
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(new URL(c.getFXML()));
	        	        
	        hbCentro.getChildren().add(loader.load());
	        gestPar = loader.getController();	
	        
	       
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void pintaTabla(String entidad, Object elemento) {
		HashMap<String, Object> variablesPaso = new HashMap<String, Object>();
        variablesPaso.put("entidadBuscar",entidad);
        variablesPaso.put("subventana",new Boolean(true));
        variablesPaso.put("idEntidadBuscar", Propiediable.getIdEntidad(entidad, elemento));
        variablesPaso.put("ancho", new Double(800));
        variablesPaso.put("alto", new Double(400));
        gestPar.setParametrosPaso(variablesPaso);        

        this.gbGuardar.activarBoton();
	}
	
	public void guardarCambios() {
		try {
			
			if (!gestPar.validaObligatoriedades()) {
				Dialogo.error("Error al guardar", "No se ha podido guardar", "Alguno de los parámetros obligatorios no está informado.");
				return;
			}
			
			int contador = 0;
			
			String idTransaccion = "actualizaParametros" + Constantes.fechaActual().getTime();
			
			Iterator<Parametro> itParametro = gestPar.getParametros().values().iterator();
			while (itParametro.hasNext()) {
				Parametro p = itParametro.next();
				
				if (p.modificado) {
					p.actualizaParametro(idTransaccion,true);
					contador ++;
				} 
			}
			
			ConsultaBD cbd = new ConsultaBD(); 
			cbd.ejecutaTransaccion(idTransaccion);
			
			Main.cortinaON();
			
			Dialogo.alert("Éxito al guardar", "Se ha guardado correctamente", "Se han actualizado correctamente " + contador + " parámetros");
						
			Parametro p = new Parametro();
			Parametro.listadoParametros=null;
			
			Main.cortinaOFF();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
		
		@Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		return fxml;
	}

}
