package ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Semaforo {
	
	ImageView boton = null;	
	
	public Semaforo(ImageView boton) {
		this.boton = boton;

		boton.setImage(new Image("semaforo_verde.png"));
	}
	
	public void verde() {
		try {
			boton.setImage(new Image("semaforo_verde.png"));
			
		} catch (Exception e) {
			System.out.println("No se encuentra el recurso semaforo_verde.png\n\r" + e.getMessage());
		}
	}
	
	public void ambar() {
		try {
			boton.setImage(new Image("semaforo_ambar.png"));
			
		} catch (Exception e) {
			System.out.println("No se encuentra el recurso semaforo_ambar.png\n\r" + e.getMessage());
		}
	}
	
	public void rojo() {
		try {
			boton.setImage(new Image("semaforo_rojo.png"));
			
		} catch (Exception e) {
			System.out.println("No se encuentra el recurso semaforo_rojo.png\n\r" + e.getMessage());
		}
	}
	
	public void visibleBoton(boolean valor) {
		boton.setVisible(valor);		
	}
}
