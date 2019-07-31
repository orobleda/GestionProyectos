package ui.Recursos.GestionTarifas;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.Proveedor;
import model.beans.Recurso;
import model.beans.RelRecursoTarifa;
import model.constantes.Constantes;
import model.utils.db.ConsultaBD;
import ui.Dialogo;
import ui.Recursos.GestionTarifas.Tables.AsignacionRecursoTarifa;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class AsignacionTarifas implements ControladorPantalla {
									   
	public static final String fxml = "file:src/ui/Recursos/GestionTarifas/AsignacionTarifas.fxml"; 
	
	public static ArrayList<RelRecursoTarifa> listaAsignaciones = null;
	public static Recurso recurso = null;
	public static Proveedor proveedor = null;
	public static boolean proveedores = false;
	
	@FXML
	private AnchorPane anchor;
	
    @FXML
    private ComboBox<Object> cbRecurso;

    @FXML
    private ImageView imBuscar;

    @FXML
    private TextField tID;

    @FXML
    private TextField tNombreProy;

    @FXML
    private ImageView imAniadir;

    @FXML
    private TableView<Tableable> tRelaciones;

    @FXML
    private ImageView imGuardar;

    @FXML
    private ImageView imEliminar;
    
    @FXML
    private RadioButton rbProveedores;

    @FXML
    private RadioButton rbUsuarios;

	
	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	@Override
	public void resize(Scene escena) {
		
	}
	
	public AsignacionTarifas(){
	}
	
	public void initialize(){			
		    imAniadir.setMouseTransparent(true);
		    imGuardar.setMouseTransparent(true);
		    imEliminar.setMouseTransparent(true);
		    
		    imGuardar.getStyleClass().remove("iconoEnabled");
		    imGuardar.getStyleClass().add("iconoDisabled");
		    imEliminar.getStyleClass().remove("iconoEnabled");
		    imEliminar.getStyleClass().add("iconoDisabled");
		    imAniadir.getStyleClass().remove("iconoEnabled");
		    imAniadir.getStyleClass().add("iconoDisabled");
			
		    imBuscar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 
		    	if (rbUsuarios.isSelected())
		    		cargaRecurso();
		    	else 
		    		cargaProveedor();
		    }	});
		    imAniadir.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 aniadirAsignacion(); }	});
		    imGuardar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 guardarElementos() ; }	});
		    imEliminar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) { eliminaElemento() ; }	});
		    
		    rbProveedores.selectedProperty().addListener(new ChangeListener<Boolean>() {
		        @Override
		        public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
		        	cargaCombo();
		        }
		    });

		    rbUsuarios.selectedProperty().addListener(new ChangeListener<Boolean>() {
		        @Override
		        public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
		        	cargaCombo();
		        }
		    });
		    
			Recurso p = new Recurso();
			cbRecurso.getItems().addAll(p.listadoRecursos());
						
			ToggleGroup toggleGroup = new ToggleGroup();

			rbUsuarios.setToggleGroup(toggleGroup);
			rbProveedores.setToggleGroup(toggleGroup);
			rbUsuarios.setSelected(true);
    }
	
	private void cargaCombo() {
		if (rbUsuarios.isSelected()) {
			cbRecurso.getItems().removeAll(cbRecurso.getItems());
        	Recurso p = new Recurso();
        	cbRecurso.getItems().addAll(p.listadoRecursos());					
			rbUsuarios.setSelected(true);
			proveedores = false;
		} else {
			cbRecurso.getItems().removeAll(cbRecurso.getItems());
        	cbRecurso.getItems().addAll(Proveedor.listado.values());
        	proveedores = true;
		}
	}
	
	private boolean guardarElementos() {
		boolean validacion = validaRangos();
		String idTransaccion = "guardarAsignacion" + Constantes.fechaActual().getTime();
		ConsultaBD consulta = new ConsultaBD();
		
		if (validacion)
			Dialogo.error("Guardar Relaciones", "Guardar Relaciones", "Se produjo un error al guardar las relaciones, algunos de los rangos se solapan");
		else{
			try{
				int contador = 0;
				Iterator<RelRecursoTarifa> itRec = AsignacionTarifas.listaAsignaciones.iterator();
				
				while (itRec.hasNext()) {
					RelRecursoTarifa relRec = itRec.next();
					if (relRec.modificado) {
						relRec.guardaRelacion(idTransaccion);
						contador++;
					}
				}
				
				consulta.ejecutaTransaccion(idTransaccion);
				
				Dialogo.alert("Guardar Relaciones", "Guardar Relaciones", "Se han actualizado " + contador + " relaciones");
			} catch (Exception e) {
				Dialogo.error("Guardar Relaciones", "Guardar Relaciones", "Se produjo un error al guardar las relaciones. No se han podido guardar");
			}
		}
			
		
		return true;
	}
	
	private void aniadirAsignacion() {
		RelRecursoTarifa rrt = new RelRecursoTarifa();
		rrt.id = -1;
		
		if (rbUsuarios.isSelected()) {
			rrt.recurso = recurso;
			rrt.usuario = true;
		}
		else {
			rrt.proveedor = proveedor;
			rrt.usuario = false;
		}
				
		AsignacionRecursoTarifa rAsigRecTar = new AsignacionRecursoTarifa();
		
		AsignacionTarifas.listaAsignaciones.add(rrt);
		tRelaciones.getItems().removeAll(tRelaciones.getItems());
		
		ArrayList<Object> lista = new ArrayList<Object>();
		lista.addAll(AsignacionTarifas.listaAsignaciones);
		ObservableList<Tableable> dataTable = rAsigRecTar.toListTableable(lista);
		tRelaciones.setItems(dataTable);
	}
	
	private boolean validaRangos() {
		boolean solapado = false;
		
		if (rbProveedores.isSelected()) return false;
		
		Iterator<RelRecursoTarifa> itPuntero = listaAsignaciones.iterator();
		Iterator<RelRecursoTarifa> itRecorrer = listaAsignaciones.iterator();
		
		while (itPuntero.hasNext()) {
			RelRecursoTarifa rrt = (RelRecursoTarifa) itPuntero.next();
			while (itRecorrer.hasNext()){
				RelRecursoTarifa rrtAux = (RelRecursoTarifa) itRecorrer.next();
				if (rrt!=rrtAux && rrt.mes == rrtAux.mes && rrt.anio == rrtAux.anio){
					solapado = true;
					
					if (solapado) break;
				}
			}
			
			if (solapado) break;
		}
		
		return solapado;
	}
	
	public void eliminaElemento() {
		try {
			AsignacionRecursoTarifa arrt = (AsignacionRecursoTarifa) tRelaciones.getSelectionModel().selectedItemProperty().get();
			
			if (arrt!=null){
				if (arrt.relRecursoTarifa.id!=-1) {
					arrt.relRecursoTarifa.deleteRelacion(arrt.relRecursoTarifa.id);
				}
				
				listaAsignaciones.remove(arrt.relRecursoTarifa);
				
				ArrayList<Object> lista = new ArrayList<Object>(); 
				lista.addAll(listaAsignaciones);
				ObservableList<Tableable> dataTable = arrt.toListTableable(lista);
				tRelaciones.setItems(dataTable);
				tRelaciones.refresh();
			} else {
				Dialogo.error("Eliminar Relación", "Eliminar Relación", "Debe seleccionar una relación");
			}
				
			
		} catch (Exception e) {
			
		}
		
	}
	
	private void cargaProveedor(){
		try {
			imGuardar.getStyleClass().remove("iconoDisabled");
			imGuardar.getStyleClass().add("iconoEnabled");
			imEliminar.getStyleClass().remove("iconoDisabled");
			imEliminar.getStyleClass().add("iconoEnabled");
			imAniadir.getStyleClass().remove("iconoDisabled");
			imAniadir.getStyleClass().add("iconoEnabled");
			
			imAniadir.setMouseTransparent(false);
		    imGuardar.setMouseTransparent(true);
		    imEliminar.setMouseTransparent(false);
			
			Proveedor p = (Proveedor) cbRecurso.getValue();
			
			AsignacionTarifas.listaAsignaciones = new ArrayList<RelRecursoTarifa>();
			AsignacionTarifas.proveedor = p;
			
			p.cargaProveedor();
			
			this.tID.setText(new Integer(p.id).toString());
			this.tNombreProy.setText(p.nomCorto);
			
			AsignacionRecursoTarifa rAsigRecTar = new AsignacionRecursoTarifa();
			RelRecursoTarifa rrt = new RelRecursoTarifa();
			listaAsignaciones = rrt.buscaRelacion(p.id,true);
			ArrayList<Object> lista = new ArrayList<Object>(); 
			lista.addAll(listaAsignaciones);
			ObservableList<Tableable> dataTable = rAsigRecTar.toListTableable(lista);
			tRelaciones.setItems(dataTable);	
			
			rAsigRecTar = new AsignacionRecursoTarifa();
			rAsigRecTar.fijaColumnas(tRelaciones);	
			
			imGuardar.setMouseTransparent(false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void cargaRecurso(){
		try {
			imGuardar.getStyleClass().remove("iconoDisabled");
			imGuardar.getStyleClass().add("iconoEnabled");
			imEliminar.getStyleClass().remove("iconoDisabled");
			imEliminar.getStyleClass().add("iconoEnabled");
			imAniadir.getStyleClass().remove("iconoDisabled");
			imAniadir.getStyleClass().add("iconoEnabled");
			
			imAniadir.setMouseTransparent(false);
		    imGuardar.setMouseTransparent(true);
		    imEliminar.setMouseTransparent(false);
			
			Recurso p = (Recurso) cbRecurso.getValue();
			
			AsignacionTarifas.listaAsignaciones = new ArrayList<RelRecursoTarifa>();
			AsignacionTarifas.recurso = p;
			
			p.cargaRecurso();
			
			this.tID.setText(new Integer(p.id).toString());
			this.tNombreProy.setText(p.nombre);
			
			AsignacionRecursoTarifa rAsigRecTar = new AsignacionRecursoTarifa();
			RelRecursoTarifa rrt = new RelRecursoTarifa();
			listaAsignaciones = rrt.buscaRelacion(p.id,false);
			ArrayList<Object> lista = new ArrayList<Object>(); 
			lista.addAll(listaAsignaciones);
			ObservableList<Tableable> dataTable = rAsigRecTar.toListTableable(lista);
			tRelaciones.setItems(dataTable);	
			
			rAsigRecTar = new AsignacionRecursoTarifa();
			rAsigRecTar.fijaColumnas(tRelaciones);	
			
			imGuardar.setMouseTransparent(false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
