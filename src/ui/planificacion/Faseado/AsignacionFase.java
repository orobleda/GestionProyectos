package ui.planificacion.Faseado;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.beans.Concepto;
import model.beans.FaseProyecto;
import model.beans.FaseProyectoSistema;
import model.beans.FaseProyectoSistemaDemanda;
import model.beans.Proyecto;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import model.metadatos.TipoDato;
import ui.interfaces.ControladorPantalla;
import ui.planificacion.Faseado.tables.DemandasAsociadasTabla;
import ui.popUps.PopUp;

public class AsignacionFase implements ControladorPantalla, PopUp {

	public AsignacionFase() {
		super();
	}

	public static final String fxml = "file:src/ui/planificacion/Faseado/AsignacionFase.fxml"; 
	
	public static HashMap<String, Object> variablesPaso = null;
	
	DemandasAsociadasTabla filaDatos;
	Proyecto proyectoPadre = null;
	Proyecto demanda = null;
	Sistema sistema = null;
	
	public GestionFases gf = null;	
	
	public boolean esPopUp = false;	

    @FXML
    private TextField tDemanda;

    @FXML
    private VBox vbFases;

    @FXML
    public TextField tCosteAsignado;

    @FXML
    private TextField tSistema;

    @FXML
    private ImageView imGuardar;
	
    /*private GestionBotones gbNuevo;*/
	
    @Override
	public AnchorPane getAnchor() {
		return null;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		/*
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
			        fraImputacion.variablesPaso = AsignacionFase.variablesPaso;
			        fraImputacion.prefijaValores();			        
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Fraccionar Imputación");	*/
		
	}	

	@Override
	public String getControlFXML() {
		return this.getFXML();
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		if (this.tDemanda==null) return;
		
		filaDatos = (DemandasAsociadasTabla) variablesPaso.get("filaDatos");
		this.gf = (GestionFases) variablesPaso.get("controladorPantalla");
		String codSistema = (String) variablesPaso.get("columna");
		
		demanda = filaDatos.p;
		proyectoPadre = gf.pActual;
		sistema = Sistema.get(codSistema);
		
		Concepto concepto = demanda.presupuestoActual.getCosteConcepto(Sistema.get(codSistema), MetaConcepto.listado.get(MetaConcepto.DESARROLLO));
		
		this.tDemanda.setText(demanda.toString());
		this.tSistema.setText(sistema.toString());
		
		try {
			this.tCosteAsignado.setText(FormateadorDatos.formateaDato(concepto.valorEstimado, TipoDato.FORMATO_MONEDA));
		} catch (Exception ex) {
			this.tCosteAsignado.setText("0 €");
		}
		
		if (proyectoPadre.fasesProyecto==null) {
			proyectoPadre.fasesProyecto = new ArrayList<FaseProyecto>();
		}
		
		if (proyectoPadre.fasesProyecto.size()==0) {
			nuevaFase();
		}
		
		pintaFases();
	}
	
	public void nuevaFase() {
		FaseProyecto fase = new FaseProyecto();
		proyectoPadre.fasesProyecto.add(fase);
		
		fase.id = Constantes.fechaActual().hashCode();
		fase.idProyecto = proyectoPadre.id;
		fase.p = proyectoPadre;
		fase.nombre = "";
		fase.fasesProyecto = new HashMap<String, FaseProyectoSistema>();
		
		FaseProyectoSistema fps = new FaseProyectoSistema();
		fps.id = Constantes.fechaActual().hashCode();
		fase.fasesProyecto.put(sistema.codigo, fps);
		fps.idSistema = sistema.id;
		fps.s = sistema;
		fps.idFase = -1;
		fps.demandasSistema = new ArrayList<FaseProyectoSistemaDemanda>();
		
		FaseProyectoSistemaDemanda fpsd = new FaseProyectoSistemaDemanda();
		fpsd.apunteContable = demanda.apunteContable;
		fpsd.id = Constantes.fechaActual().hashCode();
		fpsd.idDemanda = demanda.id;
		fpsd.idSistema = -1;
		fpsd.p = demanda;
		fps.demandasSistema.add(fpsd);
	}
	
	public void pintaFases() {
		this.vbFases.getChildren().removeAll(this.vbFases.getChildren());
		
		Iterator<FaseProyecto> itFases = proyectoPadre.fasesProyecto.iterator();
		while (itFases.hasNext()) {
			FaseProyecto fp = itFases.next();
			
			if (fp.fasesProyecto.containsKey(this.sistema.codigo)) {
				HashMap<String, Object> varPaso = new HashMap<String, Object>();
				varPaso.put("fase", fp);
				varPaso.put("sistema", sistema);
				varPaso.put("demanda", demanda);
				varPaso.put("padre", this);
				
				try {
					AsignacionFaseSistema asigFaseS = new AsignacionFaseSistema();
			        FXMLLoader loader = new FXMLLoader();
			        loader.setLocation(new URL(asigFaseS.getFXML()));
			        this.vbFases.getChildren().add(loader.load());
			        asigFaseS = loader.getController();
			        asigFaseS.setParametrosPaso(varPaso);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	@Override
	public void setClaseContenida(Object claseContenida) {				
	}
	
	@Override
	public boolean noEsPopUp() {
		if (!esPopUp) return false;
		else return true;
	}

	@Override
	public String getMetodoRetorno() {
		return null;
	}
	
		
}
