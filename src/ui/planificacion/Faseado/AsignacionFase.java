package ui.planificacion.Faseado;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.beans.Concepto;
import model.beans.FaseProyecto;
import model.beans.FaseProyectoSistema;
import model.beans.FaseProyectoSistemaDemanda;
import model.beans.Parametro;
import model.beans.ParametroFases;
import model.beans.Proyecto;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import model.metadatos.TipoDato;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
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
	Proyecto proyectoOrigen = null;
	Proyecto proyectoPadre = null;
	Proyecto demanda = null;
	Sistema sistema = null;
		
	public Faseado gf = null;	
	
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
	private GestionBotones gbGuardar;
	
    @Override
	public AnchorPane getAnchor() {
		return null;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {
					guardaAsignacion();		        
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Guardar Asignación");
		gbGuardar.activarBoton();
		
	}	

	@Override
	public String getControlFXML() {
		return this.getFXML();
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		if (this.tDemanda==null) return;
		
		filaDatos = (DemandasAsociadasTabla) variablesPaso.get("filaDatos");
		this.gf = (Faseado) variablesPaso.get("controladorPantalla");
		String codSistema = (String) variablesPaso.get("columna");
		
		demanda = filaDatos.p;
		proyectoOrigen = gf.pActual;
		
		proyectoPadre = proyectoOrigen.clone();
		
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
		} else {
			FaseProyecto fp = new FaseProyecto();
			proyectoPadre.fasesProyecto = fp.purgarFases(proyectoPadre.fasesProyecto);
		}
		
		if (proyectoPadre.fasesProyecto.size()==0) {
			nuevaFase(null);
		}
		
		pintaFases();
	}
	
	public void borraFase(FaseProyecto fasePadre) {
		FaseProyectoSistema fps = fasePadre.fasesProyecto.get(this.sistema.codigo);
		ArrayList<FaseProyectoSistemaDemanda> lFpsd = new ArrayList<FaseProyectoSistemaDemanda>();
		Iterator<FaseProyectoSistemaDemanda> itDemandas = fps.demandasSistema.iterator();
		while (itDemandas.hasNext()) {
			FaseProyectoSistemaDemanda fpsd = itDemandas.next();
			if (fpsd.p.id != this.demanda.id || fpsd.p.apunteContable != this.demanda.apunteContable) {
				lFpsd.add(fpsd);
			}
		}		
		
		FaseProyecto fp = new FaseProyecto();
		proyectoPadre.fasesProyecto = fp.purgarFases(proyectoPadre.fasesProyecto);
		
		if (proyectoPadre.fasesProyecto.size()==0) {
			nuevaFase(null);
		}
		
		pintaFases();
	}
	
	public void nuevaFase(FaseProyecto fasePadre) {
		FaseProyecto fase = new FaseProyecto();
		proyectoPadre.fasesProyecto.add(fase);
		
		fase.id = Constantes.fechaActual().hashCode();
		fase.idProyecto = proyectoPadre.id;
		fase.p = proyectoPadre;
		fase.nombre = "";
		fase.fasesProyecto = new HashMap<String, FaseProyectoSistema>();
		ParametroFases pf = new ParametroFases();
		fase.parametrosFase = pf.dameParametros(fase.getClass().getSimpleName(), Parametro.SOLO_METAPARAMETROS);
		Parametro parametro = fase.parametrosFase.get(MetaParametro.FASE_PROYECTO_FX_IMPLANTACION);
		if (fasePadre == null) {
			parametro.setValor(fase.getFechaImplantacion());
		} else {
			Calendar diaSiguiente = Calendar.getInstance();
			diaSiguiente.setTime(fasePadre.getFechaImplantacion());
			diaSiguiente.add(Calendar.DAY_OF_MONTH,1);
			parametro.setValor(diaSiguiente.getTime());
		}
				
		FaseProyectoSistema fps = new FaseProyectoSistema();
		fps.id = Constantes.fechaActual().hashCode();
		fase.fasesProyecto.put(sistema.codigo, fps);
		fps.idSistema = sistema.id;
		fps.s = sistema;
		fps.idFase = -1;
		fps.demandasSistema = new ArrayList<FaseProyectoSistemaDemanda>();
		Parametro p = new Parametro();
		fps.parametrosFaseSistema = p.dameParametros(fps.getClass().getSimpleName(), Parametro.SOLO_METAPARAMETROS);	
		
		FaseProyectoSistemaDemanda fpsd = new FaseProyectoSistemaDemanda();
		fpsd.apunteContable = demanda.apunteContable;
		fpsd.id = Constantes.fechaActual().hashCode();
		fpsd.idDemanda = demanda.id;
		fpsd.idSistema = -1;
		fpsd.p = demanda;
		p = new Parametro();
		fpsd.parametrosFaseSistemaDemanda = p.dameParametros(fpsd.getClass().getSimpleName(), Parametro.SOLO_METAPARAMETROS);	
		fps.demandasSistema.add(fpsd);
	}
	
	public void nuevoSistemaDemanda(FaseProyecto fase) {
		FaseProyectoSistema fps = new FaseProyectoSistema();
		fps.id = Constantes.fechaActual().hashCode();
		fase.fasesProyecto.put(sistema.codigo, fps);
		fps.idSistema = sistema.id;
		fps.s = sistema;
		fps.idFase = -1;
		fps.demandasSistema = new ArrayList<FaseProyectoSistemaDemanda>();
		Parametro p = new Parametro();
		fps.parametrosFaseSistema = p.dameParametros(fps.getClass().getSimpleName(), Parametro.SOLO_METAPARAMETROS);	
		
		FaseProyectoSistemaDemanda fpsd = new FaseProyectoSistemaDemanda();
		fpsd.apunteContable = demanda.apunteContable;
		fpsd.id = Constantes.fechaActual().hashCode();
		fpsd.idDemanda = demanda.id;
		fpsd.idSistema = -1;
		fpsd.p = demanda;
		p = new Parametro();
		fpsd.parametrosFaseSistemaDemanda = p.dameParametros(fpsd.getClass().getSimpleName(), Parametro.SOLO_METAPARAMETROS);	
		fps.demandasSistema.add(fpsd);
	}
	
	public void pintaFases() {
		Collections.sort(proyectoPadre.fasesProyecto);
		
		this.vbFases.getChildren().removeAll(this.vbFases.getChildren());
		
		Iterator<FaseProyecto> itFases = proyectoPadre.fasesProyecto.iterator();
		while (itFases.hasNext()) {
			FaseProyecto fp = itFases.next();
			
			if (!fp.fasesProyecto.containsKey(sistema.codigo)) {
				nuevoSistemaDemanda(fp);
			}
			
			HashMap<String, Object> varPaso = new HashMap<String, Object>();
			varPaso.put("fase", fp);
			varPaso.put("sistema", sistema);
			varPaso.put("demanda", demanda);
			varPaso.put("padre", this);
			
			try {
				AsigFaseSistema asigFaseS = new AsigFaseSistema();
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
	
	public void guardaAsignacion() {
		FaseProyecto fp = new FaseProyecto();
		proyectoPadre.fasesProyecto = fp.purgarFases(proyectoPadre.fasesProyecto);
		
		float porcAsig = proyectoPadre.coberturaDemandaFases(this.demanda, this.demanda.apunteContable, this.sistema);
		
		if (porcAsig!=100) {
			Dialogo.error("No se puede actualizar la asignación", "La asignación no está completa", "La demanda para ese sistema está asignada al "+porcAsig);
			return;
		}
		
		this.proyectoOrigen.fasesProyecto = proyectoPadre.fasesProyecto;
		this.gf.pintaFases();
		ParamTable.po.hide();
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
