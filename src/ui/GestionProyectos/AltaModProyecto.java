package ui.GestionProyectos;

import java.net.URL;
import java.util.Date;
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
import javafx.scene.layout.HBox;
import model.beans.Parametro;
import model.beans.Proyecto;
import model.constantes.Constantes;
import model.metadatos.MetaParametro;
import model.metadatos.TipoDato;
import model.metadatos.TipoProyecto;
import model.utils.db.ConsultaBD;
import ui.Dialogo;
import ui.GestionBotones;
import ui.Administracion.Parametricas.GestionParametros;
import ui.interfaces.ControladorPantalla;

public class AltaModProyecto implements ControladorPantalla {

	public static final String fxml = "file:src/ui/GestionProyectos/AltaModProyecto.fxml"; 
	
	GestionParametros gestPar = null;
	
	private AnchorPane anchor;
	
    @FXML
    private ImageView imBuscar;
    private GestionBotones gbBuscar;

    @FXML
    private HBox hbPropiedades;

    @FXML
    private ImageView imAniadir;
    private GestionBotones gbAniadir;

    @FXML
    private ComboBox<Proyecto> cbListaProy;

    @FXML
    private TextField tNombreProy;

    @FXML
    private ImageView imEliminar;
    private GestionBotones gbEliminar;

    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;

    @FXML
    private TextField tID;
	
	public AltaModProyecto(){
	}
	
	public void initialize(){
		gbAniadir = new GestionBotones(imAniadir, "Nuevo3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					nuevoProyecto();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Nueva Demanda");
		gbAniadir.activarBoton();
	
		gbBuscar = new GestionBotones(imBuscar, "Buscar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					cargaProyecto();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Busca Solicitud");
		gbBuscar.activarBoton();
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					guardarProyecto(); 
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Guardar Cambios");
		gbGuardar.desActivarBoton();
	
		gbEliminar = new GestionBotones(imEliminar, "Eliminar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					eliminaProyecto();
					limpiarFormulario();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Busca Solicitud");
		gbEliminar.desActivarBoton();
									
		Proyecto p = new Proyecto();
		cbListaProy.getItems().addAll(p.listadoProyectos());		
    }
	
	private void nuevoProyecto() throws Exception{		
		gbGuardar.activarBoton();
		gbEliminar.desActivarBoton();
		
		this.tID.setText("");
		this.tNombreProy.setText("");
		
		HashMap<Integer, Object> filtros = new HashMap<Integer, Object>();
		filtros.put(TipoDato.FORMATO_TIPO_PROYECTO, TipoProyecto.tiposDemanda());
		
		cargaPropiedades(Parametro.SOLO_METAPARAMETROS,filtros,null);
	}
	
	private void cargaPropiedades(int idProyecto, HashMap<Integer,Object> filtro, HashMap<String,Boolean> readOnlyProps ) throws Exception {
		hbPropiedades.getChildren().removeAll(hbPropiedades.getChildren());
		
		GestionParametros c = new GestionParametros();
		
		FXMLLoader loader = new FXMLLoader();
        loader.setLocation(new URL(c.getFXML()));
        	        
        hbPropiedades.getChildren().add(loader.load());
        gestPar = loader.getController();
        
        HashMap<String, Object> variablesPaso = new HashMap<String, Object>();
        variablesPaso.put("entidadBuscar", Proyecto.class.getSimpleName());
        variablesPaso.put("subventana", new Boolean(true));
        variablesPaso.put("idEntidadBuscar", idProyecto);
        variablesPaso.put("ancho", new Double(800));
        variablesPaso.put("alto", new Double(400));
        variablesPaso.put("readOnlyProps",readOnlyProps);
		variablesPaso.put("filtro",filtro);
        gestPar.setParametrosPaso(variablesPaso);
	}
	
	private void limpiarFormulario() {
		this.tID.setText("");
		this.tNombreProy.setText("");
		
		gbGuardar.desActivarBoton();
		gbEliminar.desActivarBoton();		

		hbPropiedades.getChildren().removeAll(hbPropiedades.getChildren());
		
		cbListaProy.getItems().clear();
		Proyecto.getProyectoEstaticoCargaForzada(1);
		cbListaProy.getItems().addAll(Proyecto.listaProyecto.values());
	}
	
	private void guardarProyecto(){		
		ButtonType resultado = Dialogo.confirm("Confirmación", "¿Desea guardar el proyecto?", "Se almacenará tanto el proyecto como sus parámetros informados.");
		
		if (resultado == ButtonType.OK){
			if (!gestPar.validaObligatoriedades()) {
				Dialogo.error("Error al guardar", "No se ha podido guardar", "Alguno de los parámetros obligatorios no está informado.");
				return;
			}			
			
			Proyecto p = new Proyecto();
			
			p.nombre = this.tNombreProy.getText();
			
			try {
				String idTransaccion = "guardarCambiosProyecto" + Constantes.fechaActual().getTime();
				
				if ("".equals(this.tID.getText())) {
					p.id = -1;
					p.altaProyecto(p,idTransaccion);
				}
				else {
					p.id = this.cbListaProy.getValue().id;
					p.actualizaProyecto(idTransaccion);
				}
				
				int contador = 0;
			
				Iterator<Parametro> itParametro = gestPar.getParametros().values().iterator();
				while (itParametro.hasNext()) {
					Parametro par = itParametro.next();
					par.idEntidadAsociada = p.id;
					
					if (par.modificado) {
						par.actualizaParametro(idTransaccion,true);
					} 
				}
				
				ConsultaBD cbd = new ConsultaBD(); 
				cbd.ejecutaTransaccion(idTransaccion);
				
				Dialogo.alert("Proceso Finalizado", "Guardado de proyecto completado", "Se ha guardado el proyecto y sus parámetros modificados.");
				
				limpiarFormulario();

			} catch (Exception e) {
				e.printStackTrace();
			}				
		} else {
		}
	}
	
	private void eliminaProyecto() throws Exception{
		
		ButtonType resultado = Dialogo.confirm("Confirmación", "¿Desea eliminar el elemento?", "Se eliminará tanto el elemento como sus parámetros informados.");
		
		if (resultado == ButtonType.OK){
			String idTransaccion = "eliminaProyecto" + Constantes.fechaActual().getTime();
			
			gbGuardar.desActivarBoton();
			gbEliminar.desActivarBoton();
			
			Proyecto p = cbListaProy.getValue();
			
			p.bajaProyecto(idTransaccion);
			
			ConsultaBD cbd = new ConsultaBD(); 
			cbd.ejecutaTransaccion(idTransaccion);
			
			this.tID.setText("");
			this.tNombreProy.setText("");
			
			hbPropiedades.getChildren().removeAll(hbPropiedades.getChildren());
				
			Dialogo.alert("Proceso Finalizado", "Eliminación de elemento completada", "Se ha eliminado el elemento.");
			
			p = new Proyecto();
			cbListaProy.getItems().clear();
			cbListaProy.getItems().addAll(p.listadoProyectos());
			
		} else {
		}
	}
	
	private void cargaProyecto() throws Exception{
	    gbGuardar.activarBoton();
		gbEliminar.activarBoton();
		
		Proyecto p = cbListaProy.getValue();
		
		p.cargaProyecto();
		
		this.tID.setText(new Integer(p.id).toString());
		this.tNombreProy.setText(p.nombre);
		
		HashMap<String,Boolean> readOnlyProps = new HashMap<String,Boolean>();
		readOnlyProps.put(MetaParametro.PROYECTO_TIPO_PROYECTO, Constantes.FALSE);
		
		cargaPropiedades(p.id,null,readOnlyProps);
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


