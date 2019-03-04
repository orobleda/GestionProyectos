package ui.GestionProyectos;

import java.util.ArrayList;
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
import model.beans.ParamProyecto;
import model.beans.Proyecto;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaParamProyecto;
import model.metadatos.TipoProyecto;
import ui.Dialogo;
import ui.GestionProyectos.Tables.ParamProyectoLinea;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;
import ui.popUps.SeleccionElemento;

public class AltaModProyecto implements ControladorPantalla {

	public static final String fxml = "file:src/ui/GestionProyectos/AltaModProyecto.fxml"; 
	
	@FXML
	private AnchorPane anchor;
	
	@FXML
	private TitledPane tpFiltros;
	@FXML
	private ComboBox<Proyecto> cbListaProy;
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
	
	public AltaModProyecto(){
	}
	
	public void initialize(){
			tpFiltros.setExpanded(true);
			
			imAniadir.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 nuevoProyecto(); }	});
			imBuscar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 cargaProyecto(); }	});
			
			imGuardar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 guardarProyecto(); }	});
			imEliminar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) { eliminaProyecto(); }	});
			
			imGuardar.setMouseTransparent(true);
			imEliminar.setMouseTransparent(true);
			
			Proyecto p = new Proyecto();
			cbListaProy.getItems().addAll(p.listadoProyectos());
		
    }
	
	private void nuevoProyecto(){
		SeleccionElemento.filtro = TipoProyecto.objetosNoDemanda();
		
		imGuardar.getStyleClass().remove("iconoDisabled");
		imGuardar.getStyleClass().add("iconoEnabled");
		
		imGuardar.setMouseTransparent(false);
		
		imEliminar.getStyleClass().remove("iconoDisabled");
		imEliminar.getStyleClass().add("iconoEnabled");
		
		imEliminar.setMouseTransparent(false);
		
		Proyecto p = new Proyecto();
		this.tID.setText(new Integer(p.maxIdProyecto()).toString());
		this.tNombreProy.setText("");
		
		tpFiltros.setExpanded(false);
		tpValores.setExpanded(true);
		
		ParamProyectoLinea pProy = new ParamProyectoLinea();
		ArrayList<Object> lista = new ArrayList<Object>();
		lista.addAll(MetaParamProyecto.listado.values());
		ObservableList<Tableable> dataTable = pProy.toListTableable(lista);
		tParametros.setItems(dataTable);
		
		ParamProyectoLinea pProyecto = new ParamProyectoLinea();
		pProyecto.fijaColumnas(tParametros);		
	}
	
	private void guardarProyecto(){
		boolean modificacion = false;
		
		ButtonType resultado = Dialogo.confirm("Confirmación", "¿Desea guardar el proyecto?", "Se almacenará tanto el proyecto como sus parámetros informados.");
		
		if (resultado == ButtonType.OK){
			Proyecto p = new Proyecto();
			p.id = new Integer(this.tID.getText());
			p.nombre = this.tNombreProy.getText();
			
			try {
				modificacion = p.altaProyecto(p);
				
				int contador = 0;
			
				ObservableList<Tableable> dataTable = tParametros.getItems();
				Iterator<Tableable> it = dataTable.iterator();
				
				while (it.hasNext()){
					ParamProyectoLinea pProyecto = (ParamProyectoLinea) it.next();
					if (pProyecto.modificado) {
						ParamProyecto bParamProyecto = new ParamProyecto();
						bParamProyecto.mpProy = new MetaParamProyecto(pProyecto);
						bParamProyecto.cod_parm = bParamProyecto.mpProy.id;
						bParamProyecto.idProyecto = p.id;
						if (MetaParamProyecto.TIPO_PROYECTO == bParamProyecto.cod_parm) {
							TipoProyecto tp = (TipoProyecto) FormateadorDatos.parseaDato(pProyecto.get(ParamProyectoLinea.VALORREAL), FormateadorDatos.FORMATO_TIPO_PROYECTO);
							bParamProyecto.setValor(new Integer(tp.id));
						} else {
							bParamProyecto.setValor(pProyecto.get(ParamProyectoLinea.VALORREAL));
						}
						
						contador++;
						
						bParamProyecto.insertaParametro(p.id,null);
					}
				}
				
				String accion = "Alta";
				
				if (modificacion) {
					accion = "Modificación";
				}
				
				Dialogo.alert("Proceso Finalizado", accion + " de proyecto completada", "Se ha guardado el proyecto y sus " + contador + " asociados.");
				
				p = new Proyecto();
				cbListaProy.getItems().clear();
				cbListaProy.getItems().addAll(p.listadoProyectos());
			} catch (Exception e) {
				e.printStackTrace();
			}				
		} else {
		}
	}
	
	private void eliminaProyecto(){
		
		ButtonType resultado = Dialogo.confirm("Confirmación", "¿Desea eliminar el elemento?", "Se eliminará tanto el elemento como sus parámetros informados.");
		
		if (resultado == ButtonType.OK){
			imGuardar.getStyleClass().remove("iconoEnabled");
			imGuardar.getStyleClass().add("iconoDisabled");
			
			imGuardar.setMouseTransparent(true);
			
			imEliminar.getStyleClass().remove("iconoEnabled");
			imEliminar.getStyleClass().add("iconoDisabled");
			
			imEliminar.setMouseTransparent(true);
			
			Proyecto p = cbListaProy.getValue();
			
			p.bajaProyecto(null);
			
			this.tID.setText("");
			this.tNombreProy.setText("");
			
			ParamProyectoLinea pProy = new ParamProyectoLinea();
			ArrayList<Object> lista = new ArrayList<Object>();
			ObservableList<Tableable> dataTable = pProy.toListTableable(lista);
			tParametros.setItems(dataTable);	
			
			ParamProyectoLinea pProyecto = new ParamProyectoLinea();
			pProyecto.fijaColumnas(tParametros);	
			
			tpFiltros.setExpanded(true);
			tpValores.setExpanded(false);
				
			Dialogo.alert("Proceso Finalizado", "Eliminación de elemento completada", "Se ha eliminado el elemento.");
			
			p = new Proyecto();
			cbListaProy.getItems().clear();
			cbListaProy.getItems().addAll(p.listadoProyectos());
			
		} else {
		}
	}
	
	private void cargaProyecto(){
		try {
			SeleccionElemento.filtro = TipoProyecto.objetosNoDemanda();
			imGuardar.getStyleClass().remove("iconoDisabled");
			imGuardar.getStyleClass().add("iconoEnabled");
			
			imGuardar.setMouseTransparent(false);
			
			imEliminar.getStyleClass().remove("iconoDisabled");
			imEliminar.getStyleClass().add("iconoEnabled");
			
			imEliminar.setMouseTransparent(false);
			
			Proyecto p = cbListaProy.getValue();
			
			p.cargaProyecto();
			
			this.tID.setText(new Integer(p.id).toString());
			this.tNombreProy.setText(p.nombre);
			
			ParamProyectoLinea pProy = new ParamProyectoLinea();
			ArrayList<Object> lista = new ArrayList<Object>();
			lista.addAll(p.listadoParametros);
			ObservableList<Tableable> dataTable = pProy.toListTableable(lista);
			tParametros.setItems(dataTable);	
			
			ParamProyectoLinea pProyecto = new ParamProyectoLinea();
			pProyecto.fijaColumnas(tParametros);	
			
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


