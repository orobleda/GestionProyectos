package ui.Administracion.Parametricas;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.beans.Parametro;
import model.utils.db.ConsultaBD;
import ui.GestionBotones;
import ui.TablaPropiedades;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Propiediable;

public class AdministracionParametros implements ControladorPantalla {
	Parametro par = new Parametro();
	
	HashMap<String,Parametro> listaParametros = null;
	
    @FXML
    private HBox hbCentro;
    
    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;

	public static final String fxml = "file:src/ui/Administracion/Parametricas/AdministracionParametros.fxml";
	
	@FXML
	private AnchorPane anchor;
	
	
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
		
		 Parametro par = new Parametro();
		 listaParametros = par.dameParametros(Parametro.class.getSimpleName(), -1); 
		 
		 ArrayList<Parametro> listaParams = new ArrayList<Parametro>();
		 listaParams.addAll(listaParametros.values());
		 ArrayList<? extends Propiediable> listado = listaParams;
		 
		 TablaPropiedades tp = new TablaPropiedades(TablaPropiedades.toList(listado));
 
         hbCentro.getChildren().add(tp.getTabla());
	}
	
	public void guardarCambios() {
		try {
			String idTransaccion = "eliminarPresupuestoProyecto" + new Date().getTime();
			
			Iterator<Parametro> itParametro = this.listaParametros.values().iterator();
			while (itParametro.hasNext()) {
				Parametro p = itParametro.next();
				
				if (p.modificado) p.actualizaParametro(idTransaccion);
			}
			
			ConsultaBD cbd = new ConsultaBD(); 
			cbd.ejecutaTransaccion(idTransaccion);
			
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
