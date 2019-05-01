package ui;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

public class Persiana {
	
	public static final int CERRADO = 0;
	public static final int ABIERTO = 1;
	
	ImageView boton = null;
    private GestionBotones gbBoton;

    public int estado = 0;
	
	Pane saco = null;
	VBox parent = null;
	
	public Persiana(VBox parent) {
		this.parent = parent;
		
		construirPersiana();
		
		this.saco = new Pane();
		estado = Persiana.ABIERTO;
		
		gbBoton = new GestionBotones(boton, "Ocultar3R", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				if (estado == Persiana.ABIERTO) cierra();
				else abre();
            } }, "Buscar fichero imputaciones", this);	
		gbBoton.activarBoton();
	}
	
	private void construirPersiana() {
		HBox hb = new HBox();
		hb.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		hb.setAlignment(Pos.CENTER);
		HBox.setMargin(hb, new Insets(15,0,0,0));
		
		Line l = new Line();
		l.setStartX(-400);
		l.setEndX(100);
		HBox.setMargin(l, new Insets(0,20,0,0));
		hb.getChildren().add(l);
		
		this.boton = new ImageView();
		hb.getChildren().add(this.boton);
		
		l = new Line();
		l.setStartX(-400);
		l.setEndX(100);
		HBox.setMargin(l, new Insets(0,20,0,20));
		hb.getChildren().add(l);
		
		this.parent.getChildren().add(hb);
	}
	
	public void abre() {
		if (this.saco.getChildren().size()!=0) {
			saco.getChildren().addAll(parent.getChildren());
			parent.getChildren().removeAll(parent.getChildren());
			parent.getChildren().addAll(saco.getChildren());
			estado = Persiana.ABIERTO;
			gbBoton.nombreBoton = "Ocultar3R";
			gbBoton.activarBoton();
		}
	}
	
	public void cierra() {
		saco.getChildren().removeAll(saco.getChildren());
		saco.getChildren().addAll(parent.getChildren());
		parent.getChildren().addAll(boton.getParent());
		estado = Persiana.CERRADO;
		gbBoton.nombreBoton = "Mostrar3R";
		gbBoton.activarBoton();
	}
	
}

