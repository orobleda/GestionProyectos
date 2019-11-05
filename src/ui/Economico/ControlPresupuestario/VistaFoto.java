package ui.Economico.ControlPresupuestario;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.Foto;
import model.metadatos.TipoDato;
import model.metadatos.TipoEnumerado;
import ui.Dialogo;
import ui.GestionBotones;
import ui.Tabla;
import ui.Economico.ControlPresupuestario.Tables.LineaFoto;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class VistaFoto implements ControladorPantalla {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/VistaFoto.fxml"; 
	
	@FXML
	private AnchorPane anchor;
	
    @FXML
    private ComboBox<TipoEnumerado> cbTIpoFotos;

    @FXML
    private TableView<Tableable> tListaFotos;
    public Tabla tablaListaFotos;

    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;
		
    @Override
	public AnchorPane getAnchor() {
		return anchor;
	}
    	
	@Override
	public void resize(Scene escena) {
		
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	
	public void initialize(){
		ArrayList<Object> listaFiltros = new ArrayList<Object>();
		listaFiltros.add(cbTIpoFotos);
		
		tablaListaFotos = new Tabla(tListaFotos,new LineaFoto(),listaFiltros, this);
		
		pintaFotos();
		
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {
					new Foto().hacerFoto(ControlPresupuestario.ap);
					pintaFotos();

					Dialogo.alert("Foto Guardada", "Foto Guardada", "La foto se realizó correctamente");
				} catch (Exception e) {
					Dialogo.error("Fallo al guardar", "Fallo al guardar", "Se produjo un error al guardar el elemento.");
					e.printStackTrace();
				}
            }
        },"Guardar Foto");
		gbGuardar.activarBoton();
	}
	
	public void pintaFotos() {
		Foto f = new Foto();
		f.proyecto = ControlPresupuestario.ap.proyecto;
		ArrayList<Foto> listaFotos = f.buscaFotos(null);
		
		ArrayList<Object> listado = new ArrayList<Object>();
		listado.addAll(listaFotos);
		tablaListaFotos.pintaTabla(listado);
		
		cbTIpoFotos.getItems().addAll(TipoEnumerado.getValores(TipoDato.FORMATO_TIPO_FOTO).values());
	}
	
	
	
		
}
