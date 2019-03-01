package ui.Recursos.GestionRecursos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.ParametroRecurso;
import model.beans.Recurso;
import model.metadatos.MetaParamRecurso;
import ui.Dialogo;
import ui.Recursos.GestionRecursos.Tables.ParamRecurso;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class AltaModRecurso implements ControladorPantalla {

	public static final String fxml = "file:src/ui/Recursos/GestionRecursos/AltaModRecurso.fxml"; 
	
	@FXML
	private AnchorPane anchor;
	
	@FXML
	private TitledPane tpFiltros;
	@FXML
	private ComboBox<Recurso> cbListaRec;
	@FXML
	private ImageView imAniadir;
	@FXML
	private ImageView imBuscar;

	@FXML
	private TitledPane tpValores;
	@FXML
	private TextField tID;
	@FXML
	private TextField tNombreProy;
	@FXML
	private ImageView imGuardar;
	@FXML
	private ImageView imEliminar;
	@FXML
	private TableView<Tableable> tParametros;
	
	public AltaModRecurso(){
	}
	
	public void initialize(){
			tpFiltros.setExpanded(true);
			
			imAniadir.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 nuevoRecurso(); }	});
			imBuscar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 cargaRecurso(); }	});
			
			imGuardar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 guardarRecurso(); }	});
			imEliminar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) { eliminaRecurso(); }	});
			
			imGuardar.setMouseTransparent(true);
			imEliminar.setMouseTransparent(true);
			
			Recurso p = new Recurso();
			cbListaRec.getItems().addAll(p.listadoRecursos());
		
    }
	
	private void nuevoRecurso(){
		imGuardar.getStyleClass().remove("iconoDisabled");
		imGuardar.getStyleClass().add("iconoEnabled");
		
		imGuardar.setMouseTransparent(false);
		
		imEliminar.getStyleClass().remove("iconoDisabled");
		imEliminar.getStyleClass().add("iconoEnabled");
		
		imEliminar.setMouseTransparent(false);
		
		Recurso p = new Recurso();
		this.tID.setText(new Integer(p.maxIdRecurso()).toString());
		this.tNombreProy.setText("");
		
		tpFiltros.setExpanded(false);
		tpValores.setExpanded(true);
		
		ParamRecurso pProy = new ParamRecurso();
		ArrayList<Object> lista = new ArrayList<Object>();
		lista.addAll(MetaParamRecurso.listado.values());
		ObservableList<Tableable> dataTable = pProy.toListTableable(lista);
		tParametros.setItems(dataTable);
		
		ParamRecurso pRecurso = new ParamRecurso();
		pRecurso.fijaColumnas(tParametros);		
	}
	
	private void guardarRecurso(){
		boolean modificacion = false;
		
		ButtonType resultado = Dialogo.confirm("Confirmación", "¿Desea guardar el recurso?", "Se almacenará tanto el recurso como sus parámetros informados.");
		
		if (resultado == ButtonType.OK){
			Recurso p = new Recurso();
			p.id = new Integer(this.tID.getText());
			p.nombre = this.tNombreProy.getText();
			
			try {
				modificacion = p.altaRecurso(p);
				
				int contador = 0;
			
				ObservableList<Tableable> dataTable = tParametros.getItems();
				Iterator<Tableable> it = dataTable.iterator();
				
				while (it.hasNext()){
					ParamRecurso pRecurso = (ParamRecurso) it.next();
					if (pRecurso.modificado) {
						ParametroRecurso bParamRecurso = new ParametroRecurso();
						bParamRecurso.mpRecurso = new MetaParamRecurso(pRecurso);
						bParamRecurso.cod_parm = bParamRecurso.mpRecurso.id;
						bParamRecurso.idRecurso = p.id;
						bParamRecurso.setValor(pRecurso.get(ParamRecurso.VALORREAL));
						contador++;
						
						bParamRecurso.insertaParametro(p.id);
					}
				}
				
				String accion = "Alta";
				
				if (modificacion) {
					accion = "Modificación";
				}
				
				Dialogo.alert("Proceso Finalizado", accion + " de usuario completada", "Se ha guardado el usuario y sus " + contador + " asociados.");
				
				p = new Recurso();
				cbListaRec.getItems().clear();
				cbListaRec.getItems().addAll(p.listadoRecursos());
			} catch (Exception e) {
				e.printStackTrace();
			}				
		} else {
		}
	}
	
	private void eliminaRecurso(){
		
		ButtonType resultado = Dialogo.confirm("Confirmación", "¿Desea eliminar el elemento?", "Se eliminará tanto el elemento como sus parámetros informados.");
		
		if (resultado == ButtonType.OK){
			imGuardar.getStyleClass().remove("iconoEnabled");
			imGuardar.getStyleClass().add("iconoDisabled");
			
			imGuardar.setMouseTransparent(true);
			
			imEliminar.getStyleClass().remove("iconoEnabled");
			imEliminar.getStyleClass().add("iconoDisabled");
			
			imEliminar.setMouseTransparent(true);
			
			Recurso p = cbListaRec.getValue();
			
			p.bajaRecurso();
			
			this.tID.setText("");
			this.tNombreProy.setText("");
			
			ParamRecurso pRec = new ParamRecurso();
			ArrayList<Object> lista = new ArrayList<Object>();
			ObservableList<Tableable> dataTable = pRec.toListTableable(lista);
			tParametros.setItems(dataTable);	
			
			ParamRecurso pRecurso = new ParamRecurso();
			pRecurso.fijaColumnas(tParametros);	
			
			tpFiltros.setExpanded(true);
			tpValores.setExpanded(false);
				
			Dialogo.alert("Proceso Finalizado", "Eliminación de elemento completada", "Se ha eliminado el elemento.");
			
			p = new Recurso();
			cbListaRec.getItems().clear();
			cbListaRec.getItems().addAll(p.listadoRecursos());
			
		} else {
		}
	}
	
	private void cargaRecurso(){
		try {
			imGuardar.getStyleClass().remove("iconoDisabled");
			imGuardar.getStyleClass().add("iconoEnabled");
			
			imGuardar.setMouseTransparent(false);
			
			imEliminar.getStyleClass().remove("iconoDisabled");
			imEliminar.getStyleClass().add("iconoEnabled");
			
			imEliminar.setMouseTransparent(false);
			
			Recurso p = cbListaRec.getValue();
			
			p.cargaRecurso();
			
			this.tID.setText(new Integer(p.id).toString());
			this.tNombreProy.setText(p.nombre);
			
			ParamRecurso pProy = new ParamRecurso();
			ArrayList<Object> lista = new ArrayList<Object>();
			lista.addAll(p.listadoParametros);
			ObservableList<Tableable> dataTable = pProy.toListTableable(lista);
			tParametros.setItems(dataTable);	
			
			ParamRecurso pRecurso = new ParamRecurso();
			pRecurso.fijaColumnas(tParametros);	
			
			tpFiltros.setExpanded(false);
			tpValores.setExpanded(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
		
}


