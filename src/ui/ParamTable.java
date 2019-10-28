package ui;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.table.TableRowExpanderColumn;
import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import model.interfaces.Loadable;
import model.metadatos.Sistema;
import ui.interfaces.Tableable;
import ui.planificacion.Faseado.tables.DemandasAsociadasTabla;
import ui.popUps.PopUp;

public class ParamTable implements Tableable {

	public HashMap<String, ConfigTabla> configuracionTabla = null;
	public HashMap<String, Integer> anchoColumnas = null;
	public boolean modificado = false;
    public int tipoDato;
    public PopUp controlPantalla = null;
    
    public HashMap<String,Object> variablesMetaDatos = null;
    
    public static PopOver po = null;
    
    @Override
    public void limpiarColumnas(TableView<Tableable> tParametros){
    	if (tParametros.getItems().size()==0) return;
    	
    	if (tParametros.getColumns().size()!=0)
    		if ( tParametros.getItems().get(0)==null) {
    			tParametros.getColumns().removeAll(tParametros.getColumns());
    		}
    	
    	if (tParametros.getColumns().size()!=0) {
        	HashMap<String, ConfigTabla> configTabla = tParametros.getItems().get(0).getConfigTabla();
			ArrayList<ConfigTabla> listaOrdenada = new ArrayList<ConfigTabla>();
			listaOrdenada.addAll(configTabla.values());
			Collections.sort(listaOrdenada);
			
			if (listaOrdenada.get(0).desplegable) {
					tParametros.getColumns().remove(1, tParametros.getColumns().size());
			} else {
				tParametros.getColumns().removeAll(tParametros.getColumns());
			}    	 
    	}
    }
	
	@Override
	public void fijaColumnas(TableView<Tableable> tParametros) {
		ObservableList<TableColumn<Tableable,?>> columnas = tParametros.getColumns(); 
		
		if (columnas.size()<=1) {
			HashMap<String, ConfigTabla> configTabla = null;
			if (tParametros.getItems().size()!=0) 
				configTabla = tParametros.getItems().get(0).getConfigTabla();
			else 
				configTabla = this.getConfigTabla();
			if (configTabla==null) {
				return;
			}
			ArrayList<ConfigTabla> listaOrdenada = new ArrayList<ConfigTabla>();
			listaOrdenada.addAll(configTabla.values());
			Collections.sort(listaOrdenada);
			TableColumn<Tableable,String> columna = null;
			
			if (columnas.size()==0 && listaOrdenada.get(0).desplegable) {
				TableRowExpanderColumn<Tableable> expander = new TableRowExpanderColumn<>(param -> {
					try {
						 HBox editor = new HBox(10);
						 editor.getChildren().addAll(tParametros.getItems().get(0).getFilaEmbebida(param));
					     return editor;
					}
					 catch (Exception e) {
						 e.printStackTrace();
						 return null;
					 }
				 });
				
				expander.setId("expander");

				tParametros.getColumns().add(expander);
			}
			
						
			Iterator <ConfigTabla> configCols = listaOrdenada.iterator();
			while (configCols.hasNext()) {
				ConfigTabla cfgTbl = configCols.next();
				columna = new TableColumn<Tableable,String>(); 
				columna.setId(cfgTbl.idCampo);
				columna.setText(cfgTbl.idColumna);
				tParametros.getColumns().add(columna);
			}				
			
			columnas = tParametros.getColumns();
		}
		
		Iterator<TableColumn<Tableable,?>> itCol = columnas.iterator();
		
		while (itCol.hasNext()) {
			@SuppressWarnings("unchecked")
			TableColumn<Tableable,String> columna = (TableColumn<Tableable,String>) itCol.next();
			if (!"expander".equals(columna.getId())){
				
				columna.setCellValueFactory(
						cellData -> new SimpleStringProperty(cellData.getValue().get(
								cellData.getTableColumn().getId())));
				
				if (ConfigTabla.isEditable(configuracionTabla, columna.getId())){
					columna.setCellFactory(TextFieldTableCell.forTableColumn());
					tParametros.setEditable(true);
					
					columna.setOnEditStart(
							new EventHandler<CellEditEvent<Tableable, String>>() {
						        @Override
						        public void handle(CellEditEvent<Tableable, String> t) {
						        	ParamTable pP = ((ParamTable) t.getTableView().getItems().get(t.getTablePosition().getRow()));
						        	HashMap<String, Object> parametrosPaso = new HashMap<String, Object>();
						        	String idColumna = t.getTableColumn().getId();					        	
						        	Object selector = pP.muestraSelector();
						        	
						        	if (selector!=null) {
						        		try {
						        			t.getTableView().refresh();
						        			FXMLLoader loader = new FXMLLoader();
						        			parametrosPaso.put("filaDatos", pP);							        	
						        			parametrosPaso.put("columna", idColumna);
						        			parametrosPaso.put("evento", t);
						        			
						        			try {
						        				parametrosPaso.put("controladorPantalla", t.getTableView().getProperties().get("controlador"));
						        			} catch (Exception e) {
						        				
						        			}
						        			
								        	PopUp controlPantalla = pP.controlPantalla;
								        	if (!pP.controlPantalla.noEsPopUp()) {
									        	if (po!=null){
									        		po.hide();
									        		po = null;
									        	}
								        		controlPantalla.setClaseContenida(selector);
									        	controlPantalla.setParametrosPaso(parametrosPaso);
									        	loader.setLocation(new URL(controlPantalla.getControlFXML()));
							        	        Pane pane = loader.load();
							        	        controlPantalla = (PopUp) loader.getController();
							        	        controlPantalla.setParametrosPaso(parametrosPaso);
							        	        po = new PopOver(pane);
							        	        parametrosPaso.put("PopOver", po);
							        	        po.setTitle("");
							        			po.show(t.getTableView());
							        			po.setAnimated(true);
							        			po.setAutoHide(true);
								        	} else {
								        		try {
								        			Method metodo = controlPantalla.getClass().getDeclaredMethod(controlPantalla.getMetodoRetorno(), HashMap.class);
								        			metodo.invoke(controlPantalla, parametrosPaso);
								        			
								        			
								        		} catch (Exception e) {
								        			e.printStackTrace();
								        		}
								        	}								        	
						        		} catch (Exception e) {
						        			e.printStackTrace();
						        		}
						        	} else {
						        		if (po!=null){
							        		po.hide();
							        		po = null;
							        	}
						        	}
						        }
						    }
					);
					
					columna.setOnEditCommit(
						    new EventHandler<CellEditEvent<Tableable, String>>() {
						        @Override
						        public void handle(CellEditEvent<Tableable, String> t) {
						        	ParamTable pP = ((ParamTable) t.getTableView().getItems().get(t.getTablePosition().getRow()));
						        	String idColumna = t.getTableColumn().getId();
							        pP.informaValor(t.getNewValue(),idColumna);
							        pP.modificado = true;
							        t.getTableView().refresh();
						        }
						    }
						);	
				}
			}
		}
	}
	
	public void vueltaPopUp(HashMap<String, Object> parametrosPaso) {
		@SuppressWarnings("unchecked")
		CellEditEvent<Tableable, String> t = (CellEditEvent<Tableable, String>) parametrosPaso.get("evento");
    	ParamTable pP = (ParamTable) parametrosPaso.get("filaDatos");
    	String idColumna = (String) parametrosPaso.get("columna");
    	Loadable p = (Loadable) parametrosPaso.get("seleccionado");
        pP.informaValor(new Integer(p.getId()).toString(),idColumna);
        pP.modificado = true;
        t.getTableView().refresh();
        PopOver popUp = (PopOver) parametrosPaso.get("PopOver");
        popUp.hide();
        po=null;
    }
	
	public void informaValor(String valor, String idColumna) {
		try {
			Method metodo = this.getClass().getDeclaredMethod("set", String.class, String.class);
	
			metodo.invoke(this, idColumna, valor);
		} catch (Exception e) {
			
		}
	}
	
	public void fijaMetaDatos(HashMap<String,Object> variablesPaso) {
		variablesMetaDatos = variablesPaso;
	}

	@Override
	public Tableable toTableable(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObservableList<Tableable> toListTableable(List<Object> c) {
		ObservableList<Tableable> dataTable = FXCollections.observableArrayList();
		Iterator<Object> it = c.iterator();
		
		while (it.hasNext()) {
			Object o = it.next();
			Tableable t = this.toTableable(o);
			
			if (this.variablesMetaDatos!=null) {
				t.fijaMetaDatos(this.variablesMetaDatos);
				t.setConfig();
			}
			
			dataTable.add(t);
		}
		return dataTable;
	}
	
	@Override
	public Object muestraSelector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get(String campo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(String campo, String valor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HashMap<String, ConfigTabla> getConfigTabla() {
		return this.configuracionTabla;
	}
	
	public AnchorPane getFilaEmbebida(TableRowDataFeatures<Tableable> expander){
		return null;
	}

	@Override
	public ObservableList<Tableable> filtrar(Object valorFiltro, ObservableList<Tableable> listaOriginal) {
		return listaOriginal;
	}

	@Override
	public String resaltar(int fila, String columna, Tabla tabla) {
		return null;
	}

	@Override
	public HashMap<String, Integer> getAnchoColumnas() {
		return this.anchoColumnas;
	}
	
	@Override
	public void setConfig() {    	    	
    }


}
