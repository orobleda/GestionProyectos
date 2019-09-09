package ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.ToggleSwitch;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
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
	public boolean altoLibre = false;
	
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
		
		asignaConextual();
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
		asignaConextual();
	}
	
	public Tabla (TableView<Tableable> componenteTabla, Tableable primitiva) {
		this.componenteTabla = componenteTabla;
		this.primitiva = primitiva;
		asignaConextual();
	}
	
	public Tabla (TableView<Tableable> componenteTabla, Tableable primitiva, ControladorPantalla ctrlPantalla) {
		this.componenteTabla = componenteTabla;
		this.primitiva = primitiva;
		this.componenteTabla.getProperties().put("controlador", ctrlPantalla);
		asignaConextual();
	}
	
	private void asignaConextual() {

		ContextMenu contextMenu = new ContextMenu();

		MenuItem menuItemContext = new MenuItem("Copiar a Portapapeles");
        contextMenu.getItems().addAll(menuItemContext);
        
		this.componenteTabla.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY){
                    contextMenu.show(componenteTabla,event.getScreenX(),event.getScreenY());
                }
 
            }
        });
        
		menuItemContext.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				copiarPortapapeles();
			}
		});
	}
	
	private void copiarPortapapeles() {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		
		
		ArrayList<ConfigTabla> lcf = new ArrayList<ConfigTabla>();
		
		Iterator<ConfigTabla> itCf = this.primitiva.getConfigTabla().values().iterator();
		while (itCf.hasNext()) {
			ConfigTabla cf = itCf.next();
			lcf.add(cf);
		}
		
		Collections.sort(lcf);
		
		String salida = "";
		
		itCf = lcf.iterator();
		while (itCf.hasNext()) {
			ConfigTabla cf = itCf.next();
			salida += cf.idCampo;
			
			if (itCf.hasNext()) {
				salida += "\t";
			}
		}
		
		salida +="\r\n";
		
		Iterator<Tableable> itTB = this.listaDatos.iterator();
		while (itTB.hasNext()) {
			Tableable tb = itTB.next();
			
			itCf = lcf.iterator();
			while (itCf.hasNext()) {
				ConfigTabla cf = itCf.next();
				salida += tb.get(cf.idCampo);
				
				if (itCf.hasNext()) {
					salida += "\t";
				}
			}
			
			salida +="\r\n";
		}
		
		StringSelection ss = new StringSelection(salida);
		cb.setContents(ss, ss);
		
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
		
		if (!altoLibre)
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
		
		if (!altoLibre)
			ConfigTabla.configuraAlto(componenteTabla,listaDatos.size());
		
		formateaTabla();
		componenteTabla.refresh();
	}
	
	public void formateaTabla() {
		HashMap<String,Integer> anchoColumnas = primitiva.getAnchoColumnas();
		
		if (!altoLibre && this.listaDatosFiltrada.size() == 0) componenteTabla.setPrefHeight(70);
		
		ObservableList<TableColumn<Tableable,?>> columnas = this.componenteTabla.getColumns(); 
		Iterator<TableColumn<Tableable,?>> itCol = columnas.iterator();
		Tabla elementoThis = this;
		int contadorAnchoTabla = 0;
		
		while (itCol.hasNext()) {
			@SuppressWarnings("unchecked")
			TableColumn<Tableable,String> columna = (TableColumn<Tableable,String>) itCol.next();
			int anchoCol = 0;
			
			if (!"TableRowExpanderColumn".equals(columna.getClass().getSimpleName())){
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
								
				if (anchoColumnas!=null && anchoColumnas.containsKey(columna.getId())) {
					anchoCol = anchoColumnas.get(columna.getId());
				} else {
					anchoCol = columna.getText().length()*15; 
				}
				
				columna.setPrefWidth(anchoCol);
			}
			else {
				anchoCol = 50;
			}
			

			contadorAnchoTabla += anchoCol;
			
		}
		
		componenteTabla.setPrefWidth(contadorAnchoTabla+20);
	}
	
	public void formateaTabla(int ancho, int alto) {
		HashMap<String,Integer> anchoColumnas = primitiva.getAnchoColumnas();
		
		ObservableList<TableColumn<Tableable,?>> columnas = this.componenteTabla.getColumns(); 
		Iterator<TableColumn<Tableable,?>> itCol = columnas.iterator();
		Tabla elementoThis = this;
		int contadorAnchoTabla = 0;
		
		int anchoColumnasFijas = 0;
		
		itCol = columnas.iterator();
		
		while (itCol.hasNext()) {
			@SuppressWarnings("unchecked")
			TableColumn<Tableable,String> columna = (TableColumn<Tableable,String>) itCol.next();
			int anchoCol = 0;
			
			if (anchoColumnas!=null && anchoColumnas.containsKey(columna.getId())) {
					anchoCol = anchoColumnas.get(columna.getId());
			} else {
					anchoCol = columna.getText().length()*15; 
			}
				
			anchoColumnasFijas += anchoCol;
		}
		
		itCol = columnas.iterator();
		
		while (itCol.hasNext()) {
			@SuppressWarnings("unchecked")
			TableColumn<Tableable,String> columna = (TableColumn<Tableable,String>) itCol.next();
			int anchoCol = 0;
			
			if (!"TableRowExpanderColumn".equals(columna.getClass().getSimpleName())){
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
								
				if (anchoColumnas!=null && anchoColumnas.containsKey(columna.getId())) {
					anchoCol = anchoColumnas.get(columna.getId());
				} else {
					anchoCol = columna.getText().length()*15; 
				}
				
				float ratio = new Float(anchoCol)/anchoColumnasFijas;
				
				if (ancho*ratio<anchoCol)
					columna.setPrefWidth(anchoCol);
				else
					columna.setPrefWidth(ancho*anchoCol/anchoColumnasFijas);
			}
			else {
				anchoCol = 50;
			}
			

			contadorAnchoTabla += anchoCol;
			
		}
		
		componenteTabla.setPrefWidth(ancho);
		componenteTabla.setPrefHeight(alto);
	}
}
