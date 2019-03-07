package ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.ToggleSwitch;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class Tabla {
	public TableView<Tableable> componenteTabla = null;
	public TextField tefiltro = null;
	public ToggleSwitch tsfiltro = null;
	public ArrayList<Object> listaDatosEnBruto  = null;
	public ObservableList<Tableable> listaDatos  = null;
	public ObservableList<Tableable> listaDatosFiltrada  = null;
	public Tableable primitiva  = null;
	public ControladorPantalla ctrlPantalla = null;
	
	public HashMap<String,Object> pasoPrimitiva = null;
	
	public Tabla (TableView<Tableable> componenteTabla, Tableable primitiva, TextField filtro, ControladorPantalla ctrlPantalla) {
		this.componenteTabla = componenteTabla;
		this.tefiltro = filtro;
		this.primitiva = primitiva;
		
		this.componenteTabla.getProperties().put("controlador", ctrlPantalla);
		
		this.tefiltro.focusedProperty().addListener((ov, oldV, newV) -> { 
			if (!newV) {
				try {
					pintaTabla(this.listaDatosEnBruto);
				} catch (Exception e) {
					
				}
			}	
		});
	}
	
	public Tabla (TableView<Tableable> componenteTabla, Tableable primitiva, ToggleSwitch tsfiltro, ControladorPantalla ctrlPantalla) {
		this.componenteTabla = componenteTabla;
		this.tsfiltro = tsfiltro;
		this.primitiva = primitiva;
		
		this.componenteTabla.getProperties().put("controlador", ctrlPantalla);
		
		this.tsfiltro.selectedProperty().addListener((ov, oldV, newV) -> { 
				try {
					pintaTabla(this.listaDatosEnBruto);
				} catch (Exception e) {
					
				}
		});
	}
	
	public Tabla (TableView<Tableable> componenteTabla, Tableable primitiva) {
		this.componenteTabla = componenteTabla;
		this.primitiva = primitiva;
	}
	
	public Tabla (TableView<Tableable> componenteTabla, Tableable primitiva, ControladorPantalla ctrlPantalla) {
		this.componenteTabla = componenteTabla;
		this.primitiva = primitiva;
		this.componenteTabla.getProperties().put("controlador", ctrlPantalla);
	}
	
	public void setPasoPrimitiva(HashMap<String,Object> pasoPrimitiva) {
		this.pasoPrimitiva = pasoPrimitiva;
		this.primitiva.fijaMetaDatos(this.pasoPrimitiva);
		this.primitiva.setConfig();
	}
	
	public void limpiaTabla() { 
		pintaTabla(new ArrayList<Object>());
	}
	
	public void pintaTabla(ArrayList<Object> lista) {
		if (componenteTabla== null) return;
		
		listaDatosEnBruto = lista;
		listaDatos = primitiva.toListTableable(lista);
		
		if (tefiltro != null)
			listaDatosFiltrada = primitiva.filtrar(tefiltro.getText(), listaDatos);
		else 
			if (tsfiltro != null)
				listaDatosFiltrada = primitiva.filtrar(tsfiltro.isSelected(), listaDatos);
			else
				listaDatosFiltrada = listaDatos;
		
		componenteTabla.setItems(listaDatosFiltrada);
		
		primitiva.limpiarColumnas(componenteTabla);
		primitiva.fijaColumnas(componenteTabla);
		ConfigTabla.configuraAlto(componenteTabla,lista.size());
		
		formateaTabla();
		componenteTabla.refresh();
	}
	
	public void refrescaTabla() {
		if (componenteTabla== null) return;
		
		listaDatos = primitiva.toListTableable(listaDatosEnBruto);
		
		if (tefiltro != null)
			listaDatosFiltrada = primitiva.filtrar(tefiltro.getText(), listaDatos);
		else 
			if (tsfiltro != null)
				listaDatosFiltrada = primitiva.filtrar(tsfiltro.isSelected(), listaDatos);
			else
				listaDatosFiltrada = listaDatos;
		
		componenteTabla.setItems(listaDatosFiltrada);
		
		primitiva.limpiarColumnas(componenteTabla);
		primitiva.fijaColumnas(componenteTabla);
		ConfigTabla.configuraAlto(componenteTabla,listaDatos.size());
		
		formateaTabla();
		componenteTabla.refresh();
	}
	
	public void formateaTabla() {
		HashMap<String,Integer> anchoColumnas = primitiva.getAnchoColumnas();
		
		if (this.listaDatosFiltrada.size() == 0) componenteTabla.setPrefHeight(70);
		
		ObservableList<TableColumn<Tableable,?>> columnas = this.componenteTabla.getColumns(); 
		Iterator<TableColumn<Tableable,?>> itCol = columnas.iterator();
		Tabla elementoThis = this;
		int contadorAnchoTabla = 0;
		
		while (itCol.hasNext()) {
			@SuppressWarnings("unchecked")
			TableColumn<Tableable,String> columna = (TableColumn<Tableable,String>) itCol.next();
			columna.setCellValueFactory(
					cellData ->
						new SimpleStringProperty(cellData.getValue().get(
							cellData.getTableColumn().getId())));
			
			columna.setCellFactory(column -> {
			    return new TableCell<Tableable, String>() {
			        protected void updateItem(String item, boolean empty) {
			        	setText(item);
			        	
			        	try {
			        		setStyle(primitiva.resaltar(this.getIndex(), this.getTableColumn().getId(),elementoThis));
			           	} catch (Exception e) {
			        		
			        	}
			        }
			    };
			});
			
			int anchoCol = 0;
			
			if (anchoColumnas!=null && anchoColumnas.containsKey(columna.getId())) {
				anchoCol = anchoColumnas.get(columna.getId());
			} else {
				anchoCol = columna.getText().length()*15; 
			}
			
			columna.setPrefWidth(anchoCol);
			contadorAnchoTabla += anchoCol;
		}
		
		componenteTabla.setPrefWidth(contadorAnchoTabla+20);
	}
}
