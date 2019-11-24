package ui.Recursos.GestionProveedores;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import application.Main;
import controller.Log;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.beans.Parametro;
import model.beans.Proveedor;
import model.utils.db.ConsultaBD;
import ui.Dialogo;
import ui.GestionBotones;
import ui.Administracion.Parametricas.GestionParametros;
import ui.interfaces.ControladorPantalla;

public class AltaModProveedor implements ControladorPantalla {

	public static final String fxml = "file:src/ui/Recursos/GestionProveedores/AltaModProveedor.fxml"; 
	
	GestionParametros gestPar = null;
	
	@FXML
	private AnchorPane anchor;
	
    @FXML
    private ComboBox<Proveedor> cbListaRec;

    @FXML
    private VBox vbDetalle;

    @FXML
    private HBox tParametros;    

    @FXML
    private HBox hbBotones;

    @FXML
    private GestionBotones gbAniadir;

    @FXML
    private TextField tNombre;
    
    @FXML
    private TextField tNombreCorto;
    
    @FXML
    private TextField tID;

    @FXML
    private ImageView imEliminar;
    private GestionBotones gbEliminar;

    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;
    
    Proveedor pActual = null;

    @Override
	public void resize(Scene escena) {
    	int res = Main.resolucion();
		
		if (res == Main.ALTA_RESOLUCION || res== Main.BAJA_RESOLUCION) {
			tNombre.setPrefWidth(Main.scene.getWidth()*0.75);	
			
			if (gestPar!=null) {
				gestPar.tp.setPrefHeight(Main.scene.getHeight()*0.45);
				gestPar.tp.setPrefWidth(Main.scene.getWidth()*0.9);
				tParametros.setPrefWidth(Main.scene.getWidth()*0.4);
			}
		}
	}
    
	public AltaModProveedor(){
	}
	
	public void initialize(){
			vbDetalle.setDisable(true);
			
			cbListaRec.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
					cargaRecurso();
	            } );
			
			gbAniadir = new GestionBotones(GestionBotones.DER, new ImageView(), "nuevoBombilla", false,  new EventHandler<MouseEvent>() {        
				@Override
	            public void handle(MouseEvent t)
	            {   
					try {
						nuevoRecurso();
					} catch (Exception e) {
						e.printStackTrace();
					}
	            } }, "Añadir Recurso");	
			gbAniadir.activarBoton();
			
			gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
				@Override
	            public void handle(MouseEvent t)
	            {   
					confirmaguardarRecurso();
	            } }, "Guardar Recurso", this);	
			gbGuardar.desActivarBoton();
			
			gbEliminar = new GestionBotones(imEliminar, "Eliminar3", false, new EventHandler<MouseEvent>() {        
				@Override
	            public void handle(MouseEvent t)
	            {   
					try {
						confirmaEliminaRecurso();
					} catch (Exception e) {
						e.printStackTrace();
					}
	            } }, "Guardar Recurso", this);	
			gbEliminar.desActivarBoton();

			cbListaRec.getItems().addAll(Proveedor.listado.values());
			
			hbBotones.setVisible(false);
    }
	
	private void nuevoRecurso() throws Exception{
		gbGuardar.activarBoton();
		gbEliminar.desActivarBoton();
		vbDetalle.setDisable(false);
		
		pActual = new Proveedor();
		pActual.id = -1;
		
		this.tID.setText("");
		this.tNombre.setText("");
		this.tNombreCorto.setText("");
		this.tID.setDisable(true);
		
		cargaPropiedades(Parametro.SOLO_METAPARAMETROS);				
	}
	
	private void cargaPropiedades(int idRecurso) throws Exception {
		tParametros.getChildren().removeAll(tParametros.getChildren());
		
		gestPar = new GestionParametros();
		
		FXMLLoader loader = new FXMLLoader();
        loader.setLocation(new URL(gestPar.getFXML()));
        	        
        tParametros.getChildren().add(loader.load());
        gestPar = loader.getController();
        
        HashMap<String, Object> variablesPaso = new HashMap<String, Object>();
        variablesPaso.put("entidadBuscar", Proveedor.class.getSimpleName());
        variablesPaso.put("subventana", new Boolean(true));
        variablesPaso.put("idEntidadBuscar", idRecurso);
        variablesPaso.put("ancho", new Double(800));
        variablesPaso.put("alto", new Double(400));
        gestPar.setParametrosPaso(variablesPaso);
        
        pActual.listadoParametros = gestPar.listaParametros;
        hbBotones.setVisible(true);
        resize(null);
	}
	
	public void confirmaguardarRecurso(){
		Dialogo.confirm("¿Desea guardar el recurso?", "Se almacenará tanto el recurso como sus parámetros informados.", 
				new Dialogo.Manejador<ButtonType>() {			
			@Override
			public void maneja(ButtonType buttonType) {
				guardarRecurso(buttonType);				
			}
		});
	}
		
	private void guardarRecurso(ButtonType bt){
		boolean modificacion = false;
				
		if (bt.equals(ButtonType.YES)){
			pActual.descripcion = this.tNombre.getText();
			pActual.nomCorto = this.tNombreCorto.getText();
			
			try {
				String idTransaccion = ConsultaBD.getTicket();
				
				modificacion = pActual.guardaProveedor(idTransaccion);
				
				int contador = 0;
			
				Iterator<? extends Parametro> itParams = pActual.listadoParametros.values().iterator();
				while (itParams.hasNext()) {
					Parametro pr = (Parametro) itParams.next();
					pr.idEntidadAsociada = pActual.id;
					pr.actualizaParametro(idTransaccion, false);
				}
				
				ConsultaBD.ejecutaTicket(idTransaccion);
				
				String accion = "Alta";
				
				if (modificacion) {
					accion = "Modificación";
				}
				
				Dialogo.alert("Proceso Finalizado", accion + " de usuario completada", "Se ha guardado el proveedor y sus " + contador + " parámetros asociados.");
				
				pActual.load();
				
				cbListaRec.getItems().clear();
				cbListaRec.getItems().addAll(Proveedor.listado.values());
				
				tParametros.getChildren().removeAll(tParametros.getChildren());
				gbGuardar.desActivarBoton();
				gbEliminar.desActivarBoton();
				vbDetalle.setDisable(true);
				
				
			} catch (Exception e) {
				Log.e(e);
			}				
		} 
	}
	
	public void confirmaEliminaRecurso(){
		Dialogo.confirm("¿Desea eliminar el elemento?", "Se eliminará tanto el elemento como sus parámetros informados.", 
				new Dialogo.Manejador<ButtonType>() {			
			@Override
			public void maneja(ButtonType buttonType) {
				eliminaRecurso(buttonType);				
			}
		});
	}
	
	private void eliminaRecurso(ButtonType bt){
		try {
		
			if (bt.equals(ButtonType.YES)){
				Proveedor p = cbListaRec.getValue();
				
				String idTransaccion = ConsultaBD.getTicket();
				
				p.bajaRecurso(idTransaccion);
				
				ConsultaBD.ejecutaTicket(idTransaccion);
				
				this.tID.setText("");
				this.tNombre.setText("");
				this.tNombreCorto.setText("");
			
				Dialogo.alert("Proceso Finalizado", "Eliminación de elemento completada", "Se ha eliminado el elemento.");
				
				p = new Proveedor();
				p.load();
				cbListaRec.getItems().clear();
				cbListaRec.getItems().addAll(Proveedor.listado.values());
				
				tParametros.getChildren().removeAll(tParametros.getChildren());
				gbGuardar.desActivarBoton();
				gbEliminar.desActivarBoton();
				vbDetalle.setDisable(true);		
				
			} else {
			}
		} catch (Exception e) {
			Log.e(e);
		}
	}
	
	private void cargaRecurso(){
		try {
			gbGuardar.activarBoton();
			gbEliminar.activarBoton();
			vbDetalle.setDisable(false);	
			
			pActual = cbListaRec.getValue();
			
			pActual.cargaProveedor();
			
			this.tID.setText(new Integer(pActual.id).toString());
			this.tID.setDisable(true);
			
			this.tNombre.setText(pActual.descripcion);
			this.tNombreCorto.setText(pActual.nomCorto);
			
			cargaPropiedades(pActual.id);	
		} catch (Exception e) {
			Log.e(e);
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


