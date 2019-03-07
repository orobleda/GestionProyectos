package ui.planificacion.Faseado;

import java.util.HashMap;
import java.util.Iterator;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.FaseProyecto;
import model.beans.FaseProyectoSistema;
import model.beans.FaseProyectoSistemaDemanda;
import model.beans.ParametroFases;
import model.beans.Proyecto;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import model.metadatos.TipoDato;
import ui.GestionBotones;
import ui.interfaces.ControladorPantalla;
import ui.popUps.PopUp;

public class AsignacionFaseSistema implements ControladorPantalla, PopUp {

    @FXML
    private TextField tFase;

    @FXML
    private ImageView imNuevaFila;
    private GestionBotones gbNuevaFila;

    @FXML
    private TextField tPorcentaje;    

    @FXML
    private ImageView imBorraFila;
    private GestionBotones gbBorraFila;
    
    @FXML
    private TextField tEstimado;
    
    FaseProyecto fase = null;
    Sistema sistema = null;
    Proyecto demanda = null;
    AsignacionFase af = null;
	
	public AsignacionFaseSistema() {
		super();
	}

	public static final String fxml = "file:src/ui/planificacion/Faseado/AsignacionFaseSistema.fxml"; 
	
	public HashMap<String, Object> variablesPaso = null;
	
    /**/
	
    @Override
	public AnchorPane getAnchor() {
		return null;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		gbNuevaFila = new GestionBotones(imNuevaFila, "NuevaFila3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {
					aniadirFase();		        
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Insertar Nueva Fase");		
	}	

	@Override
	public String getControlFXML() {
		return this.getFXML();
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		this.variablesPaso = variablesPaso;
		
		try {
			fase = (FaseProyecto) variablesPaso.get("fase");
			sistema = (Sistema) variablesPaso.get("sistema");
			demanda = (Proyecto) variablesPaso.get("demanda");
			af = (AsignacionFase) variablesPaso.get("padre");
			
			this.tFase.setText(fase.nombre);
			
			Iterator<FaseProyectoSistema> itFps = fase.fasesProyecto.values().iterator();
			while (itFps.hasNext()) {
				FaseProyectoSistema fps = itFps.next();
				if (fps.idSistema == sistema.id) {
					Iterator<FaseProyectoSistemaDemanda> itFpsd = fps.demandasSistema.iterator();
					while (itFpsd.hasNext()) {
						FaseProyectoSistemaDemanda fpsd = itFpsd.next();
						if (fpsd.idDemanda == demanda.id && fpsd.apunteContable == demanda.apunteContable) {
							ParametroFases pf = fpsd.getParametro(MetaParametro.FASES_COBERTURA_DEMANDA);
							if (pf!= null) {
								this.tPorcentaje.setText(FormateadorDatos.formateaDato(pf.getValor(), TipoDato.FORMATO_PORC));
								float estimado =  (Float)pf.getValor()* (Float) FormateadorDatos.parseaDato(af.tCosteAsignado.getText(), TipoDato.FORMATO_MONEDA);
								this.tEstimado.setText(FormateadorDatos.formateaDato(new Float(estimado), TipoDato.FORMATO_MONEDA));
							} else {
								this.tPorcentaje.setText(FormateadorDatos.formateaDato(new Float(0), TipoDato.FORMATO_PORC));
							}
							break;
						}
					} 
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void aniadirFase() {
		af.nuevaFase();
		af.pintaFases();
	}

	@Override
	public void setClaseContenida(Object claseContenida) {				
	}
	
	@Override
	public boolean noEsPopUp() {
		return false;
	}

	@Override
	public String getMetodoRetorno() {
		return null;
	}
	
		
}
