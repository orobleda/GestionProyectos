package ui;

import java.util.HashMap;

import javafx.scene.control.TableView;

public class ConfigTabla implements Comparable<ConfigTabla> {
	public String idColumna = null;
	public String idCampo = null;
	public boolean editable = false;
	public int orden = -1;
	public boolean desplegable = false;
	
	public ConfigTabla(String idColumna, String idCampo,  boolean editable){
		this.idColumna = idColumna;
		this.idCampo = idCampo;
		this.editable = editable;
	}
	
	public ConfigTabla(String idColumna, String idCampo,  boolean editable, int orden){
		this.idColumna = idColumna;
		this.idCampo = idCampo;
		this.editable = editable;
		this.orden = orden;
	}
	
	public ConfigTabla(String idColumna, String idCampo,  boolean editable, int orden, boolean desplegable){
		this.idColumna = idColumna;
		this.idCampo = idCampo;
		this.editable = editable;
		this.orden = orden;
		this.desplegable = desplegable;
	}
	
	public static boolean isEditable(HashMap<String, ConfigTabla> elementos, String idColumna) {
		ConfigTabla cfT = elementos.get(idColumna);
		return cfT.editable;
	}
	
	public static String getProp(HashMap<String, ConfigTabla> elementos, String idColumna) {
		ConfigTabla cfT = elementos.get(idColumna);
		return cfT.idCampo;
	}

	@Override
	public int compareTo(ConfigTabla o) {
		return new Integer(this.orden).compareTo(new Integer(o.orden));
	}
	
	public static void configuraAlto (TableView<Tableable> tabla, int tamanioLista) {
		if (tamanioLista == 0) tabla.setPrefHeight(30);
		if (tamanioLista == 1) tabla.setPrefHeight(70); 
		if (tamanioLista > 1) tabla.setPrefHeight(40*tamanioLista); 
	}
	
}
