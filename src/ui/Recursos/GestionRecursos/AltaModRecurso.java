package ui.Recursos.GestionRecursos;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.beans.Imputacion;
import model.beans.Parametro;
import model.beans.ParametroRecurso;
import model.beans.Proveedor;
import model.beans.Recurso;
import model.beans.Tarifa;
import model.constantes.Constantes;
import model.metadatos.MetaParametro;
import model.metadatos.TipoDato;
import model.utils.db.ConsultaBD;
import ui.Dialogo;
import ui.GestionBotones;
import ui.Administracion.Parametricas.GestionParametros;
import ui.Economico.CargaImputaciones.DetalleImputacion;
import ui.Economico.CargaImputaciones.Tables.LineaDetalleImputacion;
import ui.interfaces.ControladorPantalla;
import ui.popUps.PopUp;

public class AltaModRecurso implements ControladorPantalla, PopUp {

	public static final String fxml = "file:src/ui/Recursos/GestionRecursos/AltaModRecurso.fxml"; 
	
	GestionParametros gestPar = null;
	
	@FXML
	private AnchorPane anchor;
	
    @FXML
    private ComboBox<Recurso> cbListaRec;

    @FXML
    private VBox vbDetalle;

    @FXML
    private HBox tParametros;
    
    @FXML
    private VBox vbCamposEntada;
    
    @FXML
    private HBox hbBotones;

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
    Imputacion imputacion = null;
    DetalleImputacion di = null;

    
    @Override
	public void resize(Scene escena) {
    	int res = Main.resolucion();
		
		if (res == Main.ALTA_RESOLUCION || res== Main.BAJA_RESOLUCION) {
			tNombre.setPrefWidth(Main.scene.getWidth()*0.75);	
			
			if (gestPar!=null) {
				gestPar.tp.setPrefHeight(Main.scene.getHeight()*0.5);
				gestPar.tp.setPrefWidth(Main.scene.getWidth()*0.9);
				tParametros.setPrefWidth(Main.scene.getWidth()*0.4);
			}
		}
	}
    
	public AltaModRecurso(){
	}
	
	public void initialize(){
			vbDetalle.setDisable(true);
			hbBotones.setVisible(false);
			
			cbListaRec.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
				cargaRecurso();
		    	}
		    );
			
			gbAniadir = new GestionBotones(GestionBotones.DER, new ImageView(), "nuevoBombilla", false, new EventHandler<MouseEvent>()  {        
				@Override
	            public void handle(MouseEvent t)
	            {   
					try {
						nuevoRecurso(false);
					} catch (Exception e) {
						e.printStackTrace();
					}
	            } }, "Añadir Recurso");	
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
						confirmaEliminacionRecurso();
					} catch (Exception e) {
						e.printStackTrace();
					}
	            } }, "Eliminar Recurso", this);	
			gbEliminar.desActivarBoton();
			
			Recurso p = new Recurso();
			cbListaRec.getItems().addAll(p.listadoRecursos());
    }
	
	private void nuevoRecurso(boolean popUp) throws Exception{
		gbGuardar.activarBoton();
		gbEliminar.desActivarBoton();
		vbDetalle.setDisable(false);
		
		rActual = new Recurso();
		rActual.id = -1;
		
		this.tID.setText("");
		this.tNombre.setText("");
		this.tID.setDisable(true);

		hbBotones.setVisible(true);
		
		cargaPropiedades(Parametro.SOLO_METAPARAMETROS, popUp);				
	}
	
	private void cargaPropiedades(int idRecurso, boolean popUp) throws Exception {
		tParametros.getChildren().removeAll(tParametros.getChildren());
		
		gestPar = new GestionParametros();
		
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
        
        if (popUp) {
        	variablesPaso.put(GestionParametros.PARAMETROS_DIRECTOS,Constantes.TRUE);
        	HashMap<String,Parametro> listaParametros = new HashMap<String,Parametro>();
        	
        	ParametroRecurso par = new ParametroRecurso();
			listaParametros = par.dameParametros(Recurso.class.getSimpleName(), Parametro.SOLO_METAPARAMETROS);
			par = (ParametroRecurso) listaParametros.get(MetaParametro.RECURSO_COD_USUARIO);
			par.valorTexto = this.imputacion.codRecurso;
        	
        	variablesPaso.put(GestionParametros.LISTA_PARAMETROS,listaParametros);
		} 
        
        if (rActual.id!=-1) {
        	HashMap<Integer,Object> filtro = new HashMap<Integer,Object>();
        	ParametroRecurso pr = (ParametroRecurso) rActual.getValorParametro(MetaParametro.RECURSO_PROVEEDOR);
			if (pr!=null && pr.valorObjeto!=null) {
				Proveedor pAux = (Proveedor) pr.getValor();
				
				HashMap<String,Object> filtros = new HashMap<String,Object>();
				filtros.put(Tarifa.filtro_PROVEEDOR, pAux);
				
				ArrayList<Tarifa> lTarifas= new Tarifa().listado(filtros);
				if (lTarifas!=null) {
					filtro.put(TipoDato.FORMATO_TARIFA, lTarifas);
					variablesPaso.put("filtro", filtro);
				}
			}
			
        }
        
        gestPar.setParametrosPaso(variablesPaso);
        
        if (!popUp)
        	resize(null);
                
        rActual.listadoParametros = gestPar.listaParametros;
	}
	
	private void guardarRecurso(){
		boolean modificacion = false;
		
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
					contador++;
				}
				
				ConsultaBD.ejecutaTicket(idTransaccion);
				
				String accion = "Alta";
				
				if (modificacion) {
					accion = "Modificación";
				}
				
				Dialogo.alert("Proceso Finalizado", accion + " de usuario completada", "Se ha guardado el usuario y sus " + contador + " parámetros asociados.");
				
				Recurso.listadoRecursosEstatico(true);
				
				if (imputacion==null) {
					cbListaRec.getItems().clear();
					cbListaRec.getItems().addAll(rActual.listadoRecursos());
					tParametros.getChildren().removeAll(tParametros.getChildren());
					gbGuardar.desActivarBoton();
					gbEliminar.desActivarBoton();
					vbDetalle.setDisable(true);
				} else {
					this.di.altaFinalizada();
				}
				

				hbBotones.setVisible(false);
				
			} catch (Exception e) {
				e.printStackTrace();
			}				
	}
	
	public void confirmaEliminacionRecurso(){
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

				hbBotones.setVisible(false);
				
				Recurso.listadoRecursosEstatico(true);
			}
		} catch (Exception e) {
			Log.e(e);
		}
	}
	
	private void cargaRecurso(){
		try {
			rActual = cbListaRec.getValue();
			
			if (rActual!=null) {
				vbDetalle.setDisable(false);
				gbGuardar.activarBoton();
				gbEliminar.activarBoton();
				rActual.cargaRecurso();
				
				this.tID.setText(new Integer(rActual.id).toString());
				this.tID.setDisable(true);
				
				this.tNombre.setText(rActual.nombre);

				hbBotones.setVisible(true);
				
				cargaPropiedades(rActual.id, false);
			}			
				
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

	@Override
	public String getControlFXML() {
		return fxml;
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		try {
			vbCamposEntada.getChildren().removeAll(vbCamposEntada.getChildren());
			this.imputacion = (Imputacion) variablesPaso.get(LineaDetalleImputacion.IMPUTACION);
			this.di = (DetalleImputacion)  variablesPaso.get("padre");
			
			this.nuevoRecurso(true);
			this.gbEliminar.desActivarBoton();
			
			this.tNombre.setText(this.imputacion.nomRecurso);
			
		} catch (Exception ex) {
			
		} 
	}

	@Override
	public void setClaseContenida(Object claseContenida) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean noEsPopUp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMetodoRetorno() {
		// TODO Auto-generated method stub
		return null;
	}
		
}


