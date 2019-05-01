package ui.Economico.CargaImputaciones;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.ToggleSwitch;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import model.beans.Concepto;
import model.beans.Coste;
import model.beans.Estimacion;
import model.beans.EstimacionAnio;
import model.beans.EstimacionMes;
import model.beans.Imputacion;
import model.beans.ParametroRecurso;
import model.beans.RelRecursoSistema;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Semaforo;
import ui.Tabla;
import ui.Economico.CargaImputaciones.Tables.LineaDetalleImputacion;
import ui.Economico.ControlPresupuestario.EdicionEstImp.NuevaEstimacion;
import ui.Economico.Tarifas.InformaTarifa;
import ui.Recursos.GestionRecursos.AltaModRecurso;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;
import ui.popUps.PopUp;

public class DetalleImputacion implements ControladorPantalla  {
		
	public static final String fxml = "file:src/ui/Economico/CargaImputaciones/DetalleImputacion.fxml"; 
	

    @FXML
    private ImageView imAltaUsuario;
    public GestionBotones gbAltaUsuario;
    
    @FXML
    private ImageView imSemaforo;
    public Semaforo semaforo;

    @FXML
    private ImageView imAltaTarifa;
    public GestionBotones gbAltaTarifa;

    @FXML
    private ComboBox<Sistema> cbSistema;

    @FXML
    private ImageView imEnlazarEstimacion;

    @FXML
    private TableView<Tableable> tResultado;
	public Tabla tablaResultado;
	
    @FXML
    private ToggleSwitch tsAccionEstimacion;

    @FXML
    private Label lAccionEstimacion;
	
	CargaImputaciones padre = null;
	Imputacion imputacion = null;
	
	@Override
	public AnchorPane getAnchor() {
		return null;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		semaforo = new Semaforo(imSemaforo);
		
		gbAltaUsuario = new GestionBotones(imAltaUsuario, "AltaUsuario3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				try {
					altaUsuario();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Alta Usuario", this);	
		gbAltaUsuario.activarBoton();
		
		gbAltaTarifa = new GestionBotones(imAltaTarifa, "AltaTarifa3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				try {
					altaTarifa();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Alta Tarifa", this);	
		gbAltaTarifa.activarBoton();
		
		tablaResultado = new Tabla(tResultado,new LineaDetalleImputacion());
		
	}
	
	public void adscribir(CargaImputaciones padre, Imputacion i, Estimacion est) throws Exception {
		this.padre = padre; 
		this.imputacion = i;
		
		if (i.modo_fichero==NuevaEstimacion.MODO_ELIMINAR) {
			lAccionEstimacion.setText("Excluir Dato");
		}
		if (i.modo_fichero==NuevaEstimacion.MODO_MODIFICAR) {
			lAccionEstimacion.setText("Modificar Dato");
		}
		
		HashMap<String, Object> detalle  = new HashMap<String, Object>();
		detalle.put(LineaDetalleImputacion.IMPUTACION, i);
		
		Iterator<Coste> itCoste = this.padre.proyecto.presupuestoActual.costes.values().iterator();
		while (itCoste.hasNext()) {
			Coste c = itCoste.next();
			cbSistema.getItems().add(c.sistema);
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(i.fxInicio);
		
		if (i.sistema!=null) {
			cbSistema.setValue(i.sistema);
			if (i.estimacionAsociada!=null) detalle.put(LineaDetalleImputacion.ESTIMACION, i.estimacionAsociada);
			if (i.modo_fichero>=0 && i.tarifa!=null) {
				this.tsAccionEstimacion.setSelected(true);
				if (!this.padre.listaImputacionesAsignadas.contains(i))
					this.padre.listaImputacionesAsignadas.add(i);
			}
		} else {
			if (i.estimacionAsociada!=null) {
				detalle.put(LineaDetalleImputacion.ESTIMACION, i.estimacionAsociada);
				if (cbSistema.getValue()==null) {
					cbSistema.setValue(i.estimacionAsociada.sistema);
					i.modo_fichero = NuevaEstimacion.MODO_INSERTAR;
					if (!this.padre.listaImputacionesAsignadas.contains(i))
						this.padre.listaImputacionesAsignadas.add(i);
					this.tsAccionEstimacion.setSelected(true);
				}
			}
		}

		
		ArrayList<Object> lt = new ArrayList<Object>();
		lt.add(detalle);
		
		tablaResultado.pintaTabla(lt);
		
		if (i.recurso!=null) this.gbAltaUsuario.desActivarBoton();
		if (i.tarifa!=null) this.gbAltaTarifa.desActivarBoton();
		
		if (i.recurso==null || i.tarifa==null) {
			this.semaforo.rojo();
			this.cbSistema.setDisable(true);
			this.tsAccionEstimacion.setDisable(true);
		}
		else {
			if (cbSistema.getValue()==null) {
				this.semaforo.rojo();
				this.tsAccionEstimacion.setDisable(true);
			}
		}
		
		this.cbSistema.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			if (cbSistema.getValue()!=null) {
				try {
					this.imputacion.sistema = cbSistema.getValue();
					ParametroRecurso pr = (ParametroRecurso) this.imputacion.recurso.getValorParametro(MetaParametro.RECURSO_NAT_COSTE);
					MetaConcepto mc = (MetaConcepto) pr.getValor();
					this.imputacion.natCoste = mc;					
					this.tsAccionEstimacion.setDisable(false);
					this.tsAccionEstimacion.setSelected(true);
					gestionaImputacion();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		
		tsAccionEstimacion.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	
			gestionaImputacion(); }	});
		
	}
	
	public void gestionaImputacion(){	 
		if (tsAccionEstimacion.isSelected()) {
			if (!padre.listaImputacionesAsignadas.contains(imputacion)) {
				padre.listaImputacionesAsignadas.add(imputacion);
				
				if (lAccionEstimacion.getText().equals("Excluir Dato")) {
					imputacion.modo_fichero = NuevaEstimacion.MODO_ELIMINAR;
				} else {
					if (lAccionEstimacion.getText().equals("Modificar Dato")) {
						imputacion.modo_fichero = NuevaEstimacion.MODO_MODIFICAR;
					} else imputacion.modo_fichero = NuevaEstimacion.MODO_INSERTAR;
				}
			}   
		} else {
			padre.listaImputacionesAsignadas.remove(imputacion);
			imputacion.modo_fichero = -1;
		}
		semaforo.verde();
		padre.refrescaPresupuesto(null, padre.listaImputacionesAsignadas);			
	}	
	
	public void altaUsuario() throws Exception{
		PopUp controlPantalla = new AltaModRecurso();
    	if (ParamTable.po!=null){
    		ParamTable.po.hide();
    		ParamTable.po = null;
        }
    	
    	FXMLLoader loader = new FXMLLoader();
    	loader.setLocation(new URL(controlPantalla.getControlFXML()));
        Pane pane = loader.load();
        controlPantalla = (PopUp) loader.getController();
        
        HashMap<String, Object> paramPaso = new HashMap<String, Object> ();
        paramPaso.put(LineaDetalleImputacion.IMPUTACION, this.imputacion);
        paramPaso.put("padre", this);
        
        controlPantalla.setParametrosPaso(paramPaso);
        ParamTable.po = new PopOver(pane);
        ParamTable.po.setTitle("");
        ParamTable.po.show(this.imAltaUsuario);
        ParamTable.po.setAnimated(true);
		ParamTable.po.setAutoHide(true);
	}
	
	public void altaTarifa() throws Exception{
		PopUp controlPantalla = new InformaTarifa();
    	if (ParamTable.po!=null){
    		ParamTable.po.hide();
    		ParamTable.po = null;
        }
    	
    	FXMLLoader loader = new FXMLLoader();
    	loader.setLocation(new URL(controlPantalla.getControlFXML()));
        Pane pane = loader.load();
        controlPantalla = (PopUp) loader.getController();
        
        HashMap<String, Object> paramPaso = new HashMap<String, Object> ();
        paramPaso.put(LineaDetalleImputacion.IMPUTACION, this.imputacion);
        paramPaso.put("padre", this);

        controlPantalla.setParametrosPaso(paramPaso);
        ParamTable.po = new PopOver(pane);
        ParamTable.po.setTitle("");
        ParamTable.po.show(this.imAltaTarifa);
        ParamTable.po.setAnimated(true);
		ParamTable.po.setAutoHide(true);
	}
	
	public void altaFinalizada() {
		if (ParamTable.po!=null){
    		ParamTable.po.hide();
    		ParamTable.po = null;
        }
		
		this.padre.recargar();
	}
	
}
