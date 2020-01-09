package ui;

import java.util.ArrayList;

import org.controlsfx.control.PopOver;

import application.Main;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class VentanaContextual {
	
	PopOver po = null;
	VBox vb = null;
	ChangeListener<? super Boolean> conListener = null;
	
	ArrayList<Node> pilaPaneles = new ArrayList<Node>();
	
	public VentanaContextual(Node panel, ChangeListener<? super Boolean> listener) {
		conListener = listener;
		vb = new VBox();
		vb.getChildren().add(panel);
		
		Main.customWorkbench.showDrawer(vb, Side.RIGHT);
		pilaPaneles.add(panel);
	}
	
	public VentanaContextual(Node panel) {		
		vb = new VBox();
		vb.getChildren().add(panel);
		
		Main.customWorkbench.showDrawer(vb, Side.RIGHT);
	}
	
	public void setTitle(String s) {
	}
	
	public void show(Node owner) {
		Main.customWorkbench.showDrawer(vb, Side.RIGHT);
		Main.customWorkbench.getDrawerShown().resize(vb.getWidth(), vb.getHeight());
		if (conListener!=null) {
			Main.customWorkbench.getDrawerShown().visibleProperty().addListener(conListener);
		} else {
			Main.customWorkbench.getDrawerShown().visibleProperty().addListener(valor -> {});
		}
		
	}
	
	public void showSubVentana(Node panel) {
		vb = new VBox();
		vb.getChildren().add(panel);
		
		Main.customWorkbench.showDrawer(vb, Side.RIGHT);
		Main.customWorkbench.getDrawerShown().resize(vb.getWidth(), vb.getHeight());
		
		pilaPaneles.add(panel);
	}
	
	public void showAtras() {
		vb = new VBox();
		vb.getChildren().add(pilaPaneles.get(pilaPaneles.size()-2));
		
		Main.customWorkbench.showDrawer(vb, Side.RIGHT);
		Main.customWorkbench.getDrawerShown().resize(vb.getWidth(), vb.getHeight());
		
		pilaPaneles.remove(pilaPaneles.size()-1);
	}
	
	public void setAnimated(boolean b) {
	}
	
	public void autosize() {
		Main.customWorkbench.getDrawerShown().resize(vb.getWidth(), vb.getHeight());
	}
	
	public void setAutoHide(boolean b) {
		//po.setAutoHide(b);
	}
	
	public static void hide() {
		Main.customWorkbench.hideDrawer();
	}
}
