package ui.Recursos.GestionRecursos;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.beans.Parametro;
import model.beans.ParametroRecurso;
import model.beans.Recurso;
import model.utils.db.ConsultaBD;
import ui.Dialogo;
import ui.GestionBotones;
import ui.Administracion.Parametricas.GestionParametros;
import ui.interfaces.ControladorPantalla;

public class AltaModRecurso implements ControladorPantalla {

	public static final String fxml = "file:src/ui/Recursos/GestionRecursos/AltaModRecurso.fxml"; 
	
	@FXML
	private AnchorPane anchor;
	
    @FXML
    private ImageView imBuscar;
    private GestionBotones gbBuscar;

    @FXML
    private ComboBox<Recurso> cbListaRec;

    @FXML
    private VBox vbDetalle;

    @FXML
    private VBox tParametros;

    @FXML
    private ImageView imAniadir;
    private GestionBotones gbAniadir;

    @FXML
    private TextField tNombre;
    
    @FXML
    private TextField tID;

    @FXML
    private ImageView imEliminar;
    private GestionBotones gbEliminar;

    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;
    
    Recurso rActual = null;

    
	public AltaModRecurso(){
	}
	
	public void initialize(){
			vbDetalle.setDisable(true);
			
			gbBuscar = new GestionBotones(imBuscar, "Buscar3", false, new EventHandler<MouseEvent>() {        
				@Override
	            public void handle(MouseEvent t)
	            {   
					cargaRecurso();
	            } }, "Buscar Recurso", this);	
			gbBuscar.activarBoton();
			
			gbAniadir = new GestionBotones(imAniadir, "Nuevo3", false, new EventHandler<MouseEvent>() {        
				@Override
	            public void handle(MouseEvent t)
	            {   
					try {
						nuevoRecurso();
					} catch (Exception e) {
						e.printStackTrace();
					}
	            } }, "Añadir Recurso", this);	
			gbAniadir.activarBoton();
			
			gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
				@Override
	            public void handle(MouseEvent t)
	            {   
					guardarRecurso();
	            } }, "Guardar Recurso", this);	
			gbGuardar.desActivarBoton();
			
			gbEliminar = new GestionBotones(imEliminar, "Eliminar3", false, new EventHandler<MouseEvent>() {        
				@Override
	            public void handle(MouseEvent t)
	            {   
					try {
						eliminaRecurso();
					} catch (Exception e) {
						e.printStackTrace();
					}
	            } }, "Guardar Recurso", this);	
			gbEliminar.desActivarBoton();
			
			Recurso p = new Recurso();
			cbListaRec.getItems().addAll(p.listadoRecursos());
    }
	
	private void nuevoRecurso() throws Exception{
		gbGuardar.activarBoton();
		gbEliminar.activarBoton();
		vbDetalle.setDisable(false);
		
		rActual = new Recurso();
		rActual.id = -1;
		
		this.tID.setText("");
		this.tNombre.setText("");
		this.tID.setDisable(true);
		
		cargaPropiedades(Parametro.SOLO_METAPARAMETROS);				
	}
	
	private void cargaPropiedades(int idRecurso) throws Exception {
		tParametros.getChildren().removeAll(tParametros.getChildren());
		
		GestionParametros gestPar = new GestionParametros();
		
		FXMLLoader loader = new FXMLLoader();
        loader.setLocation(new URL(gestPar.getFXML()));
        	        
        tParametros.getChildren().add(loader.load());
        gestPar = loader.getController();
        
        HashMap<String, Object> variablesPaso = new HashMap<String, Object>();
        variablesPaso.put("entidadBuscar", Recurso.class.getSimpleName());
        variablesPaso.put("subventana", new Boolean(true));
        variablesPaso.put("idEntidadBuscar", idRecurso);
        variablesPaso.put("ancho", new Double(800));
        variablesPaso.put("alto", new Double(400));
        gestPar.setParametrosPaso(variablesPaso);
        
        rActual.listadoParametros = gestPar.listaParametros;
	}
	
	private void guardarRecurso(){
		boolean modificacion = false;
		
		ButtonType resultado = Dialogo.confirm("Confirmación", "¿Desea guardar el recurso?", "Se almacenará tanto el recurso como sus parámetros informados.");
		
		if (resultado == ButtonType.OK){
			rActual.nombre = this.tNombre.getText();
			
			try {
				String idTransaccion = ConsultaBD.getTicket();
				
				modificacion = rActual.guardaRecurso(idTransaccion);
				
				int contador = 0;
			
				Iterator<? extends Parametro> itParams = rActual.listadoParametros.values().iterator();
				while (itParams.hasNext()) {
					ParametroRecurso pr = (ParametroRecurso) itParams.next();
					pr.idEntidadAsociada = rActual.id;
					pr.actualizaParametro(idTransaccion, false);
				}
				
				ConsultaBD.ejecutaTicket(idTransaccion);
				
				String accion = "Alta";
				
				if (modificacion) {
					accion = "Modificación";
				}
				
				Dialogo.alert("Proceso Finalizado", accion + " de usuario completada", "Se ha guardado el usuario y sus " + contador + " asociados.");
				
				cbListaRec.getItems().clear();
				cbListaRec.getItems().addAll(rActual.listadoRecursos());
				
				rActual.listadoRecursosEstatico(true);
				
				tParametros.getChildren().removeAll(tParametros.getChildren());
				gbGuardar.desActivarBoton();
				gbEliminar.desActivarBoton();
				vbDetalle.setDisable(true);
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}				
		} else {
		}
	}
	
	private void eliminaRecurso() throws Exception{
		
		ButtonType resultado = Dialogo.confirm("Confirmación", "¿Desea eliminar el elemento?", "Se eliminará tanto el elemento como sus parámetros informados.");
		
		if (resultado == ButtonType.OK){
			Recurso p = cbListaRec.getValue();
			
			String idTransaccion = ConsultaBD.getTicket();
			
			p.bajaRecurso(idTransaccion);
			
			ConsultaBD.ejecutaTicket(idTransaccion);
			
			this.tID.setText("");
			this.tNombre.setText("");
		
			Dialogo.alert("Proceso Finalizado", "Eliminación de elemento completada", "Se ha eliminado el elemento.");
			
			p = new Recurso();
			cbListaRec.getItems().clear();
			cbListaRec.getItems().addAll(p.listadoRecursos());
			
			tParametros.getChildren().removeAll(tParametros.getChildren());
			gbGuardar.desActivarBoton();
			gbEliminar.desActivarBoton();
			vbDetalle.setDisable(true);		
			
			rActual.listadoRecursosEstatico(true);
		} else {
		}
	}
	
	private void cargaRecurso(){
		try {
			gbGuardar.activarBoton();
			gbEliminar.activarBoton();
			vbDetalle.setDisable(false);	
			
			rActual = cbListaRec.getValue();
			
			rActual.cargaRecurso();
			
			this.tID.setText(new Integer(rActual.id).toString());
			this.tID.setDisable(true);
			
			this.tNombre.setText(rActual.nombre);
			
			cargaPropiedades(rActual.id);	
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


