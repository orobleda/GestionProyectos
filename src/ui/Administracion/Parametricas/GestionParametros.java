package ui.Administracion.Parametricas;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import application.Main;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.beans.Concepto;
import model.beans.Parametro;
import model.constantes.CargaInicial;
import model.constantes.Constantes;
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
	
	public static String PARAMETROS_DIRECTOS = "listadoDirectamente";
	public static String LISTA_PARAMETROS = "listadoParametros";
	public static String NO_FILTROS = "Sinfiltros";

	public static final String fxml = "file:src/ui/Administracion/Parametricas/GestionParametros.fxml"; 
	
	public HashMap<String, Object> variablesPaso = null;
	public VentanaPadre ventanaPadre = null;
	
	public static GestionParametros objetoThis = null;
	
	public Concepto conceptoSeleccionado = null;
	
	public boolean esPopUp = false;
	
    @FXML
    private HBox hbContenedor;

    @FXML
    private HBox hbGuardado;

    @FXML
    private VBox vbContenedor;
    
    public TablaPropiedades tp;
    
    public HashMap<String,Parametro> listaParametros;

    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;
	

    @Override
	public AnchorPane getAnchor() {
		return null;
	}
    
    @Override
	public void resize(Scene escena) {
		
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
					String idTransaccion = "guardarParametros" + Constantes.fechaActual().getTime();
					
					Iterator<Parametro> itParametro = listaParametros.values().iterator();
					while (itParametro.hasNext()) {
						Parametro p = itParametro.next();
						if (p.modificado) {
							p.actualizaParametro(idTransaccion,true);
						}
					}
					
					ConsultaBD cbd = new ConsultaBD(); 
					cbd.ejecutaTransaccion(idTransaccion);
					
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Guardar Cambios");
		gbGuardar.activarBoton();
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
			this.hbGuardado.getChildren().removeAll(this.hbGuardado.getChildren());
		} else {
			this.hbGuardado.setVisible(true);
		}
		
		String entidad = (String) variablesPaso.get("entidadBuscar");
		Double ancho = (Double) variablesPaso.get("ancho");
		Double alto = (Double) variablesPaso.get("alto");
		@SuppressWarnings("unchecked")
		HashMap<String,Boolean> readOnlyProps  = (HashMap<String,Boolean>) variablesPaso.get("readOnlyProps");
		@SuppressWarnings("unchecked")
		HashMap<Integer,Object> filtro  = (HashMap<Integer,Object>) variablesPaso.get("filtro");
		
		int idEntidad = -1;
		if (variablesPaso.get(GestionParametros.PARAMETROS_DIRECTOS)!=null) {
			listaParametros = (HashMap<String,Parametro>) variablesPaso.get(GestionParametros.LISTA_PARAMETROS);
		} else {
			idEntidad = variablesPaso.get("idEntidadBuscar") == null? -1: (Integer) variablesPaso.get("idEntidadBuscar");
			Parametro par = Propiediable.beanControlador(entidad);
			listaParametros = par.dameParametros(entidad, idEntidad); 
		}
		
		ArrayList<Parametro> listaParams = new ArrayList<Parametro>();
		listaParams.addAll(listaParametros.values());
		ArrayList<? extends Propiediable> listado = listaParams;
		 
		tp = new TablaPropiedades(TablaPropiedades.toList(listado,readOnlyProps), ancho, alto,filtro);
		
		if (variablesPaso.get(GestionParametros.NO_FILTROS)!=null) {
			tp.setSearchBoxVisible(false);
			tp.setModeSwitcherVisible(false);
		}
		
		this.hbContenedor.getChildren().removeAll(this.hbContenedor.getChildren());
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
