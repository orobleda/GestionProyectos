package ui;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class PanelResumible {
	GestionBotones expander;
	boolean expandido = true;
	
	HBox panelResumen = null;
	HBox panelDetalle = null;
	
	int modo = 0;
	
	public static int MODO_ALTERNADO = 0;

	ArrayList<Node> listaContenidaResumen = new ArrayList<Node>();
	ArrayList<Node> listaContenidaDetalle = new ArrayList<Node>();
	
	public PanelResumible (String nomBotonControlador, String nomBotonControladorDesp, ImageView controlador, HBox panelResumen, HBox panelDetalle, int modo) {
		this.modo = modo;
		this.panelResumen = panelResumen;
		this.panelDetalle = panelDetalle;
		
		expander = new GestionBotones(controlador, nomBotonControlador, false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				aplicaCambios ();
            } }, "",GestionBotones.MODO_SWITCH);
		expander.configuraSwitch(nomBotonControlador,nomBotonControladorDesp);
		
		aplicaCambios();
	}
	
	public void aplicaCambios () {
		expandido = !expandido;
		
		if (!expandido) {
			listaContenidaDetalle.addAll(panelDetalle.getChildren());
			panelDetalle.getChildren().removeAll(panelDetalle.getChildren());
			panelResumen.getChildren().addAll(listaContenidaResumen);
			listaContenidaResumen.removeAll(listaContenidaResumen);
		} else {
			listaContenidaResumen.addAll(panelResumen.getChildren());
			panelResumen.getChildren().removeAll(panelResumen.getChildren());
			panelDetalle.getChildren().addAll(listaContenidaDetalle);
			listaContenidaDetalle.removeAll(listaContenidaDetalle);
			
		}
	}
	
	public void compactar () {
		expandido = true;
		aplicaCambios();
	}
}
