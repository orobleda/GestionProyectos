package ui.Economico.ControlPresupuestario.EdicionEstImp;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.beans.Concepto;
import model.beans.Estimacion;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Economico.ControlPresupuestario.ControlPresupuestario;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteUsuario;
import ui.interfaces.ControladorPantalla;
import ui.popUps.PopUp;

public class General implements ControladorPantalla, PopUp {

	public General() {
		super();
	}

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/EdicionEstImp/General.fxml"; 
	
	public static HashMap<String, Object> variablesPaso = null;
	public static Object claseRetorno = null;
	public static String metodoRetorno = null;
	public static DetalleRecurso detRecursos = null;
	
	public static General objetoThis = null;
	
	public Concepto conceptoSeleccionado = null;
	
	public boolean esPopUp = false;
	
    @FXML
    private VBox vInfo;
    
    @FXML
    private VBox vDetalle;
	
	@FXML
	private AnchorPane anchor;
	
    @FXML
    private ImageView imFraccionar;
    private GestionBotones gbFraccionar;

    @FXML
    private ImageView imAprobar;
    private GestionBotones gbAprobar;

    @FXML
    private ImageView imDesaprobar;
    private GestionBotones gbDesaprobar;

    @FXML
    private ImageView imEditar;
    private GestionBotones gbEditar;

    @FXML
    private ImageView imEliminar;
    private GestionBotones gbEliminar;

    @FXML
    private ImageView imCopiarDrcha;
    private GestionBotones gbCopiarDrcha;

    @FXML
    private ImageView imNuevo;
    private GestionBotones gbNuevo;
	
	public General (Object claseRetorno, String metodoRetorno){
		General.claseRetorno = claseRetorno;
		General.metodoRetorno = metodoRetorno;
	}
	
    @Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		General.objetoThis = this;
		
		gbFraccionar = new GestionBotones(imFraccionar, "Fraccionar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {
					FraccionarImputacion fraImputacion = new FraccionarImputacion();
			        FXMLLoader loader = new FXMLLoader();
			        loader.setLocation(new URL(fraImputacion.getFXML()));
			        vDetalle.getChildren().removeAll(vDetalle.getChildren());
			        vDetalle.getChildren().add(loader.load());
			        fraImputacion = loader.getController();
			        fraImputacion.variablesPaso = General.variablesPaso;
			        fraImputacion.prefijaValores();			        
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Fraccionar Imputación");	
		gbAprobar = new GestionBotones(imAprobar, "Aprobar3", false, new EventHandler<MouseEvent>() {        
    			@Override
                public void handle(MouseEvent t)
                { 
    				cambiarEstadoEstimacion(Estimacion.APROBADA);
    				ParamTable.po.hide();
                } }, "Aprobar Estimación/Imputación");
		gbDesaprobar = new GestionBotones(imDesaprobar, "DesAprobar", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            { 
				cambiarEstadoEstimacion(Estimacion.DESAPROBADA);
				ParamTable.po.hide();
            } }, "Desaprobar Estimación/Imputación");
		gbEditar = new GestionBotones(imEditar, "Editar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            { 
				try {
					NuevaEstimacion nueEstimacion = new NuevaEstimacion();
			        FXMLLoader loader = new FXMLLoader();
			        loader.setLocation(new URL(nueEstimacion.getFXML()));
			        vDetalle.getChildren().removeAll(vDetalle.getChildren());
			        vDetalle.getChildren().add(loader.load());
			        nueEstimacion = loader.getController();
			        nueEstimacion.variablesPaso = General.variablesPaso;
			        nueEstimacion.prefijaValores();
			        nueEstimacion.fijaModo(NuevaEstimacion.MODO_MODIFICAR);
			        nueEstimacion.cargaModificacion(General.objetoThis.conceptoSeleccionado,NuevaEstimacion.CARGA_TIPO_POR_DEFECTO);
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Editar Estimación/Imputación");
		gbEliminar = new GestionBotones(imEliminar, "Eliminar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {
					NuevaEstimacion nueEstimacion = new NuevaEstimacion();
			        FXMLLoader loader = new FXMLLoader();
			        loader.setLocation(new URL(nueEstimacion.getFXML()));
			        vDetalle.getChildren().removeAll(vDetalle.getChildren());
			        vDetalle.getChildren().add(loader.load());
			        nueEstimacion = loader.getController();
			        nueEstimacion.variablesPaso = General.variablesPaso;
			        nueEstimacion.prefijaValores();
			        nueEstimacion.fijaModo(NuevaEstimacion.MODO_ELIMINAR);
			        nueEstimacion.cargaEliminacion(General.objetoThis.conceptoSeleccionado,NuevaEstimacion.CARGA_TIPO_POR_DEFECTO);
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Eliminar Estimación/Imputación");
		gbCopiarDrcha = new GestionBotones(imCopiarDrcha, "Enlazar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {
					NuevaEstimacion nueEstimacion = new NuevaEstimacion();
			        FXMLLoader loader = new FXMLLoader();
			        loader.setLocation(new URL(nueEstimacion.getFXML()));
			        vDetalle.getChildren().removeAll(vDetalle.getChildren());
			        vDetalle.getChildren().add(loader.load());
			        nueEstimacion = loader.getController();
			        nueEstimacion.variablesPaso = General.variablesPaso;
			        nueEstimacion.prefijaValores();
			        nueEstimacion.cargaCopiaDrcha(General.objetoThis.conceptoSeleccionado,NuevaEstimacion.CARGA_TIPO_POR_DEFECTO);
			        nueEstimacion.fijaModo(NuevaEstimacion.MODO_COPIAR_DRCHA);
				} catch (Exception e) {
					e.printStackTrace();
				}				
            } }, "Copiar Imputación a Estimación");
		gbNuevo = new GestionBotones(imNuevo, "Nuevo3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {
					NuevaEstimacion nueEstimacion = new NuevaEstimacion();
			        FXMLLoader loader = new FXMLLoader();
			        loader.setLocation(new URL(nueEstimacion.getFXML()));
			        vDetalle.getChildren().removeAll(vDetalle.getChildren());
			        vDetalle.getChildren().add(loader.load());
			        nueEstimacion = loader.getController();
			        nueEstimacion.variablesPaso = General.variablesPaso;
			        nueEstimacion.prefijaValores();
			        nueEstimacion.fijaModo(NuevaEstimacion.MODO_INSERTAR);
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Nueva Estimación/Imputación");
		
		gbFraccionar.desActivarBoton();
		gbAprobar.desActivarBoton();
		gbDesaprobar.desActivarBoton();
		gbEditar.desActivarBoton();
		gbEliminar.desActivarBoton();
		gbCopiarDrcha.desActivarBoton();
	}	

	@Override
	public String getControlFXML() {
		return this.getFXML();
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		ArrayList<Concepto> listaConceptos = new ArrayList<Concepto>();
		listaConceptos.add(((LineaCosteUsuario)variablesPaso.get("filaDatos")).concepto);
		ArrayList<Float> resumen = ((LineaCosteUsuario)variablesPaso.get("filaDatos")).resumen;
		General.variablesPaso = variablesPaso;		
		
		try {
			DetalleRecurso detRecursos = new DetalleRecurso();
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(new URL(detRecursos.getFXML()));
	        if (vInfo!=null) {
	        	vInfo.getChildren().add(loader.load());
		        detRecursos = loader.getController();
		        General.detRecursos = detRecursos;
		        detRecursos.pintaConcepto(listaConceptos,resumen);
	        }	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setClaseContenida(Object claseContenida) {				
	}
	
	public void cambiarEstadoEstimacion(int estadoAprobacion) {
		if (this.conceptoSeleccionado.listaEstimaciones.size()!=0) {
			Estimacion e = (Estimacion) this.conceptoSeleccionado.listaEstimaciones.get(0);
			General.objetoThis.gbCopiarDrcha.desActivarBoton();
			
			ControlPresupuestario.salvaPosicionActual();
			e.cambiaEstadoEstimacion(e.id, estadoAprobacion);
			ControlPresupuestario.cargaPosicionActual();
			
			General.detRecursos.refresca(); 
		}		
	}
	
	public void elementoSeleccionado(HashMap<String, Object> parametrosPaso) {
		try {
			Concepto c = ((ui.Economico.ControlPresupuestario.EdicionEstImp.Tables.LineaCosteUsuario) parametrosPaso.get("filaDatos")).concepto;
			this.conceptoSeleccionado = c;
			
			boolean esResto = false;
			
			if (c.listaEstimaciones.size()!=0 || c.listaImputaciones.size()!=0) {
				if (c.listaEstimaciones.size()!=0) {
					if (c.r == null) esResto = true;
				}
				if (c.listaImputaciones.size()!=0) {
					if (c.r == null) esResto = true;
				}
			} else {
				esResto = true;
			}			
			
			if (!esResto) {
				General.objetoThis.gbEliminar.activarBoton();
				General.objetoThis.gbEditar.activarBoton();
			} else {
				General.objetoThis.gbEliminar.desActivarBoton();
				General.objetoThis.gbEditar.desActivarBoton();
			}
			if (c.listaImputaciones.size()!=0) {
					if (c.r != null) {
						General.objetoThis.gbCopiarDrcha.activarBoton();
						General.objetoThis.gbFraccionar.activarBoton();
					}
					else {
						General.objetoThis.gbCopiarDrcha.desActivarBoton();
						General.objetoThis.gbFraccionar.desActivarBoton();
					}				
			} else {
				General.objetoThis.gbCopiarDrcha.desActivarBoton();
				General.objetoThis.gbFraccionar.desActivarBoton();
			}
			if (c.listaEstimaciones.size()!=0) {
				Estimacion e = (Estimacion) c.listaEstimaciones.get(0);
				General.objetoThis.gbCopiarDrcha.desActivarBoton();
				if (e.aprobacion == Estimacion.APROBADA) {
					General.objetoThis.gbDesaprobar.activarBoton();
					General.objetoThis.gbAprobar.desActivarBoton();
				}
				if (e.aprobacion == Estimacion.DESAPROBADA) {
					General.objetoThis.gbDesaprobar.desActivarBoton();
					General.objetoThis.gbAprobar.activarBoton();
				}
				if (c.r ==null) {
					General.objetoThis.gbDesaprobar.desActivarBoton();	
					General.objetoThis.gbAprobar.desActivarBoton();				
				}
			} 
								
		} catch (Exception e) {
			
		}		
	}

	@Override
	public boolean noEsPopUp() {
		if (!esPopUp) return false;
		else return true;
	}

	@Override
	public String getMetodoRetorno() {
		return metodoRetorno;
	}
	
		
}
