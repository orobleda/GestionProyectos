package ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Semaforo {
	
	public static final int VERDE = 1;
	public static final int AMBAR = 2;
	public static final int ROJO = 3;
	
	ImageView boton = null;
	
	public int estado = 0;
	
	public Semaforo(ImageView boton) {
		this.boton = boton;

		boton.setImage(new Image("semaforo_verde.png"));
	}
	
	public void asignaEstado(int estado) {
		if (estado == Semaforo.VERDE) verde();
		if (estado == Semaforo.AMBAR) ambar();
		if (estado == Semaforo.ROJO) rojo();
	}
	
	public void verde() {
		try {
			boton.setImage(new Image("semaforo_verde.png"));
			this.estado = Semaforo.VERDE;
			
		} catch (Exception e) {
			System.out.println("No se encuentra el recurso semaforo_verde.png\n\r" + e.getMessage());
		}
	}
	
	public void ambar() {
		try {
			boton.setImage(new Image("semaforo_ambar.png"));
			this.estado = Semaforo.AMBAR;
		} catch (Exception e) {
			System.out.println("No se encuentra el recurso semaforo_ambar.png\n\r" + e.getMessage());
		}
	}
	
	public void rojo() {
		try {
			boton.setImage(new Image("semaforo_rojo.png"));
			this.estado = Semaforo.ROJO;
		} catch (Exception e) {
			System.out.println("No se encuentra el recurso semaforo_rojo.png\n\r" + e.getMessage());
		}
	}
	
	public void visibleBoton(boolean valor) {
		boton.setVisible(valor);		
	}
}

