package ui.GestionProyectos;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import application.Main;
import controller.Log;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
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
import ui.ParamTable;
import ui.Administracion.Parametricas.GestionParametros;
import ui.interfaces.ControladorPantalla;
import ui.popUps.ConsultaAvanzadaProyectos;

public class AltaModProyecto implements ControladorPantalla {

	public static final String fxml = "file:src/ui/GestionProyectos/AltaModProyecto.fxml"; 
	
	GestionParametros gestPar = null;
	
	private AnchorPane anchor;
	
	private Proyecto proySeleccionado;
	
    private GestionBotones gbBuscar;

    @FXML
    private HBox hbPropiedades;

    private GestionBotones gbAniadir;

    @FXML
    private TextField tNombreProy;

    @FXML
    private ImageView imEliminar;
    private GestionBotones gbEliminar;

    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;
    
    @FXML
    private HBox hbBotones;
    
    @FXML
    private TextField tID;
    
    public GestionParametros gp = null;
	
	public AltaModProyecto(){
	}
	
	@Override
	public void resize(Scene escena) {
		int res = Main.resolucion();
		
		if (res == Main.ALTA_RESOLUCION || res== Main.BAJA_RESOLUCION) {
			tNombreProy.setPrefWidth(Main.scene.getWidth()*0.65);	
			
			if (gestPar!=null) {
				gestPar.tp.setPrefHeight(Main.scene.getHeight()*0.6);
				gestPar.tp.setPrefWidth(Main.scene.getWidth()*0.9);
			}
		}
		
	}
	
	public void initialize(){
		gbAniadir = new GestionBotones(GestionBotones.DER, new ImageView(), "nuevoBombilla", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					nuevoProyecto();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Crear Nueva Solicitud");
		gbAniadir.activarBoton();
		
		gbBuscar = new GestionBotones(GestionBotones.IZQ, new ImageView(), "Buscar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					consultaAvanzadaProyectos();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Buscar Solicitud");
		gbBuscar.activarBoton();
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					confirmaGuardado(); 
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
					confirmaEliminacion();
					limpiarFormulario();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Borrar Solicitud");
		gbEliminar.desActivarBoton();
									
		hbBotones.setVisible(false);
    }
	
	private void consultaAvanzadaProyectos() throws Exception{		
		ConsultaAvanzadaProyectos.getInstance(this, 1, TipoProyecto.ID_TODO, this.tNombreProy);
	}
	
	public void fijaProyecto(ArrayList<Proyecto> listaProyecto) {
		if (listaProyecto!=null && listaProyecto.size()==1) {
			this.proySeleccionado = listaProyecto.get(0);
			this.tNombreProy.setText(this.proySeleccionado.nombre);
			ParamTable.po.hide();
			try {
				cargaProyecto();
			} catch (Exception e) {
				Dialogo.error(null, e);
			}
		}
	}
	
	private void nuevoProyecto() throws Exception{		
		gbGuardar.activarBoton();
		gbEliminar.desActivarBoton();
		
		this.tID.setText("");
		this.tNombreProy.setText("");
		
		HashMap<Integer, Object> filtros = new HashMap<Integer, Object>();
		filtros.put(TipoDato.FORMATO_TIPO_PROYECTO, TipoProyecto.tiposDemanda());
		
		cargaPropiedades(Parametro.SOLO_METAPARAMETROS,filtros,null);
		resize(null);

		hbBotones.setVisible(true);
	}
	
	private void cargaPropiedades(int idProyecto, HashMap<Integer,Object> filtro, HashMap<String,Boolean> readOnlyProps ) throws Exception {
		hbPropiedades.getChildren().removeAll(hbPropiedades.getChildren());
		
		GestionParametros c = new GestionParametros();
		
		gp = c;
		
		FXMLLoader loader = new FXMLLoader();
        loader.setLocation(new URL(c.getFXML()));
        	        
        hbPropiedades.getChildren().add(loader.load());
        gestPar = loader.getController();
        
        HashMap<String, Object> variablesPaso = new HashMap<String, Object>();
        variablesPaso.put("entidadBuscar", Proyecto.class.getSimpleName());
        variablesPaso.put("subventana", new Boolean(true));
        variablesPaso.put("idEntidadBuscar", idProyecto);
        variablesPaso.put("ancho", new Double(1200));
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

		hbBotones.setVisible(false);
		
	}
	
	public void confirmaGuardado(){
		Dialogo.confirm("¿Desea guardar el proyecto?", "Se almacenará tanto el proyecto como sus parámetros informados.", 
				new Dialogo.Manejador<ButtonType>() {			
			@Override
			public void maneja(ButtonType buttonType) {
				guardarProyecto(buttonType);				
			}
		});
	}
		
	private void guardarProyecto(ButtonType bt){			
		if (bt.equals(ButtonType.YES)){
			if (!gestPar.validaObligatoriedades()) {
				Dialogo.error("Error al guardar", "No se ha podido guardar", "Alguno de los parámetros obligatorios no está informado.");
				Main.cortinaOFF();
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
					p.id = this.proySeleccionado.id;
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
				
				Proyecto.getProyectoEstaticoCargaForzada(-1);				


			} catch (Exception e) {
				Log.e(e);
			}
		} 
	}
	
	public void confirmaEliminacion(){
		Dialogo.confirm("¿Desea eliminar el elemento?", "Se eliminará tanto el elemento como sus parámetros informados.", 
				new Dialogo.Manejador<ButtonType>() {			
			@Override
			public void maneja(ButtonType buttonType) {
				eliminaProyecto(buttonType);				
			}
		});
	}
		
	private void eliminaProyecto(ButtonType bt){
		try {			
			if (bt.equals(ButtonType.YES)){
				String idTransaccion = "eliminaProyecto" + Constantes.fechaActual().getTime();
				
				gbGuardar.desActivarBoton();
				gbEliminar.desActivarBoton();
				
				Proyecto p = this.proySeleccionado;
				
				p.bajaProyecto(idTransaccion);
				
				ConsultaBD cbd = new ConsultaBD(); 
				cbd.ejecutaTransaccion(idTransaccion);
				
				this.tID.setText("");
				this.tNombreProy.setText("");
				
				hbPropiedades.getChildren().removeAll(hbPropiedades.getChildren());
					
				Dialogo.alert("Proceso Finalizado", "Eliminación de elemento completada", "Se ha eliminado el elemento.");
				
				limpiarFormulario();
				
				Proyecto.getProyectoEstaticoCargaForzada(-1);
				
			} else {
			}
		} catch (Exception e) {			
			Log.e(e);
		}
	}
	
	private void cargaProyecto() throws Exception{
		try {
			Main.cortinaON();
			if (tNombreProy.getText().equals("")) {
				Dialogo.error("Campos de entrada mal informados", "Seleccione un proyecto", "Es necesario seleccionar un proyecto");
				return;
			}
			
		    gbGuardar.activarBoton();
			gbEliminar.activarBoton();
			
			Proyecto p = this.proySeleccionado;
			
			p.cargaProyecto();
			
			this.tID.setText(new Integer(p.id).toString());
			this.tNombreProy.setText(p.nombre);
			
			HashMap<String,Boolean> readOnlyProps = new HashMap<String,Boolean>();
			readOnlyProps.put(MetaParametro.PROYECTO_TIPO_PROYECTO, Constantes.FALSE);
			
			cargaPropiedades(p.id,null,readOnlyProps);
			
			hbBotones.setVisible(true);
			
			resize(null);
		}finally {
			Main.cortinaOFF();
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


