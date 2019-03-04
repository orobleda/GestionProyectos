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
import javafx.scene.layout.VBox;
import model.beans.Concepto;
import model.beans.Parametro;
import model.utils.db.ConsultaBD;
import ui.GestionBotones;
import ui.TablaPropiedades;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Propiediable;
import ui.interfaces.VentanaPadre;
import ui.popUps.PopUp;

public class GestionParametros implements ControladorPantalla, PopUp {

	public GestionParametros() {
		super();
	}

	public static final String fxml = "file:src/ui/Administracion/Parametricas/GestionParametros.fxml"; 
	
	public static HashMap<String, Object> variablesPaso = null;
	public static VentanaPadre ventanaPadre = null;
	
	public static GestionParametros objetoThis = null;
	
	public Concepto conceptoSeleccionado = null;
	
	public boolean esPopUp = false;
	
    @FXML
    private HBox hbContenedor;

    @FXML
    private HBox hbGuardado;

    @FXML
    private VBox vbContenedor;
    
    private TablaPropiedades tp;
    
    HashMap<String,Parametro> listaParametros;

    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;
	

    @Override
	public AnchorPane getAnchor() {
		return null;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		GestionParametros.objetoThis = this;

		this.hbGuardado.setVisible(false);
		
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					String idTransaccion = "eliminarPresupuestoProyecto" + new Date().getTime();
					
					Iterator<Parametro> itParametro = listaParametros.values().iterator();
					while (itParametro.hasNext()) {
						Parametro p = itParametro.next();
						if (p.modificado) {
							p.actualizaParametro(idTransaccion);
						}
					}
					
					ConsultaBD cbd = new ConsultaBD(); 
					cbd.ejecutaTransaccion(idTransaccion);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Guardar Cambios");
	}
	
	public boolean validaObligatoriedades() {
		Iterator<Parametro> itParametro = listaParametros.values().iterator();
		
		while (itParametro.hasNext()) {
			Parametro p = itParametro.next();
			
			boolean valorValido = true;
			
			Object retorno = p.getValor();
			if (retorno!=null) {
				try {
					if ("String".equals(p.getValor().getClass().getSimpleName())) {
						String salida = (String) p.getValor();
						if ("".equals(salida)) {
							valorValido = false;
						}
					}					
				} catch (Exception e) {}
			}
			else valorValido = false;

			if (p.metaParam.obligatorio && !valorValido) {
				return false;	
			}
		}
		
		return true;
	}

	@Override
	public String getControlFXML() {
		return this.getFXML();
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		
		boolean noEsVentanaTop = (boolean) variablesPaso.get("subventana");
		
		if (noEsVentanaTop) {
			this.hbGuardado.setVisible(false);
		} else {
			this.hbGuardado.setVisible(true);
		}
		
		String entidad = (String) variablesPaso.get("entidadBuscar");
		Double ancho = (Double) variablesPaso.get("ancho");
		Double alto = (Double) variablesPaso.get("alto");
		int idEntidad = variablesPaso.get("idEntidadBuscar") == null? -1: (Integer) variablesPaso.get("idEntidadBuscar");
		
		Parametro par = Propiediable.beanControlador(entidad);
		listaParametros = par.dameParametros(entidad, idEntidad); 
		 
		ArrayList<Parametro> listaParams = new ArrayList<Parametro>();
		listaParams.addAll(listaParametros.values());
		ArrayList<? extends Propiediable> listado = listaParams;
		 
		TablaPropiedades tp = new TablaPropiedades(TablaPropiedades.toList(listado), ancho, alto);
		this.hbContenedor.getChildren().add(tp);
		
	}
	
	public HashMap<String, Parametro> getParametros() {
		return this.listaParametros;
	}

	@Override
	public void setClaseContenida(Object claseContenida) {				
	}
	
	@Override
	public boolean noEsPopUp() {
		if (!esPopUp) return false;
		else return true;
	}

	@Override
	public String getMetodoRetorno() {
		return null;
	}

}
