package ui.planificacion.Faseado;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.beans.FaseProyecto;
import model.beans.FaseProyectoSistema;
import model.beans.FaseProyectoSistemaDemanda;
import model.beans.Parametro;
import model.beans.ParametroFases;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaParametro;
import model.metadatos.TipoDato;
import ui.PanelResumible;
import ui.Tabla;
import ui.Administracion.Parametricas.GestionParametros;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;
import ui.planificacion.Faseado.tables.DemAsocSistTabla;
import ui.planificacion.Faseado.tables.SistAsocFaseTabla;
import ui.popUps.PopUp;

public class InfoFase implements ControladorPantalla, PopUp {
	
	public static final String fxml = "file:src/ui/planificacion/Faseado/InfoFase.fxml";
	
	public static String PADRE = "padre";
	public static String FASE = "fase";
	
	Faseado padre = null;
	FaseProyecto fase = null;
	
	@FXML
    private HBox hbCabecera;

    @FXML
    private HBox hbCabPropsSist;

    @FXML
    private HBox hbDetPropsSist;
    
    @FXML
    private HBox hBPropSist;
    
    @FXML
    private HBox hBPropDem;
    

    @FXML
    private ImageView imMostrarOcultarFase;

    @FXML
    private TextField tNomFaseDetalle;

    @FXML
    private ImageView imPropsSist;

    @FXML
    private HBox hbDetalle;
    
    @FXML
    private VBox vbDemanda;

    @FXML
    private TableView<Tableable> tSistemasFase;
    public Tabla tablaSistemasFase;

    @FXML
    private HBox hbCabPropsDem;

    @FXML
    private TextField tFxImplCabecera;

    @FXML
    private TableView<Tableable> tDemandasSistema;
    public Tabla tablaDemandasSistema;

    @FXML
    private ImageView imPropsDem;

    @FXML
    private HBox hbDetPropsDem;

    @FXML
    private ImageView imPropsFases;

    @FXML
    private HBox hbDetPropsFases;

    @FXML
    private TextField tNomFaseCabecera;

    @FXML
    private HBox hbCabPropsFases;
	
    @FXML
	private AnchorPane anchor;
    
    Parametro fechaImpl = null;
    
    GestionParametros parFase = null;
    GestionParametros parSist = null;
    GestionParametros parDem = null;
    
    PanelResumible prSist = null;
    PanelResumible prDem = null;
	 
	public void initialize(){
		
	}
	
	public void setParPaso(HashMap<String, Object> variablesPaso) throws Exception {
			this.fase = (FaseProyecto) variablesPaso.get(InfoFase.FASE);
			this.padre = (Faseado) variablesPaso.get(InfoFase.PADRE);
			
			this.vbDemanda.setVisible(false);
			hBPropSist.setVisible(false);
			hBPropDem.setVisible(false);
			
			pintaFase();
			pintaSistemas();
			
			PanelResumible pr = new PanelResumible("Mostrar3R","Ocultar3R",imMostrarOcultarFase,hbCabecera,hbDetalle,PanelResumible.MODO_ALTERNADO);
			
			this.tNomFaseDetalle.focusedProperty().addListener((ov, oldV, newV) -> { 
				if (!newV) {
					try {
						fase.nombre = this.tNomFaseDetalle.getText();
					} catch (Exception ex){
					}			
				}
			});
	}
	
	private void pintaSistemas() throws Exception {
		this.tablaSistemasFase = new Tabla(tSistemasFase,new SistAsocFaseTabla(),this);
		this.tablaDemandasSistema = new Tabla(tDemandasSistema,new DemAsocSistTabla(),this);
		
		ArrayList<Object> listaPintable = new ArrayList<Object>();
		listaPintable.addAll(this.fase.fasesProyecto.values());	
		
		HashMap<String,Object> pasoPrimitiva = new HashMap<String,Object>();
		pasoPrimitiva.put(SistAsocFaseTabla.PADRE, this);
		
		tablaSistemasFase.setPasoPrimitiva(pasoPrimitiva);
		tablaSistemasFase.pintaTabla(listaPintable);
	}
	
	private void pintaFase()  throws Exception{
		this.tNomFaseCabecera.setText(this.fase.nombre);
		this.tNomFaseDetalle.setText(this.fase.nombre);
		
		fechaImpl = this.fase.parametrosFase.get(MetaParametro.FASE_PROYECTO_FX_IMPLANTACION); 
		
		class ObservadorFxImpl implements Observer {
		    public void update(Observable obj, Object arg) {
		    	try {
					fechaImpl = fase.parametrosFase.get(MetaParametro.FASE_PROYECTO_FX_IMPLANTACION); 
					
					String fxImpl = FormateadorDatos.formateaDato(fechaImpl.getValor().toString(), TipoDato.FORMATO_FECHA);
					tFxImplCabecera.setText(fxImpl);
				} catch (Exception ex) {ex.printStackTrace();}   
		    }
		}
		
		String fxImplantacion = FormateadorDatos.formateaDato(fechaImpl.getValor().toString(), TipoDato.FORMATO_FECHA);
		
		ObservableValue<Date> seguimientoFechaImpl = new SimpleObjectProperty<Date>(fechaImpl.valorfecha);
		
		fechaImpl.addObserver(new ObservadorFxImpl());
		
		this.tFxImplCabecera.setText(fxImplantacion);		
		
		this.hbDetPropsFases.getChildren().removeAll(this.hbDetPropsFases.getChildren());
		
        GestionParametros c = new GestionParametros();
		
		FXMLLoader loader = new FXMLLoader();
        loader.setLocation(new URL(c.getFXML()));
        	        
        this.hbDetPropsFases.getChildren().add(loader.load());
        parFase = loader.getController();
        
        HashMap<String, Object> variPaso = new HashMap<String, Object>();
        variPaso.put("entidadBuscar", FaseProyecto.class.getSimpleName());
        variPaso.put("subventana", new Boolean(true));
        variPaso.put("idEntidadBuscar", this.fase.id);
        variPaso.put(GestionParametros.PARAMETROS_DIRECTOS+"",GestionParametros.PARAMETROS_DIRECTOS+"");
        variPaso.put(GestionParametros.NO_FILTROS,GestionParametros.NO_FILTROS);
        variPaso.put(GestionParametros.LISTA_PARAMETROS+"",this.fase.parametrosFase);
        variPaso.put("ancho", new Double(300));
        variPaso.put("alto", new Double(100));
        this.parFase.setParametrosPaso(variPaso);
        
        PanelResumible pr = new PanelResumible("Mostrar3R","Ocultar3R",this.imPropsFases,this.hbCabPropsFases,this.hbDetPropsFases,PanelResumible.MODO_ALTERNADO);
	}
	
	public void filaSeleccionada(HashMap<String,Object> variablesPaso) {
		try {
			String columna = (String) variablesPaso.get("columna");
	
			if (SistAsocFaseTabla.SISTEMA.equals(columna)) {
				FaseProyectoSistema fps = ((SistAsocFaseTabla) variablesPaso.get("filaDatos")).fps;
				hBPropDem.setVisible(false);
				this.vbDemanda.setVisible(true);
				hBPropSist.setVisible(true);
				
				if (prSist!= null) 
					prSist.compactar();
				this.hbDetPropsSist.getChildren().removeAll(this.hbDetPropsSist.getChildren());
				
		        GestionParametros c = new GestionParametros();
				
				FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(c.getFXML()));
		        	        
		        this.hbDetPropsSist.getChildren().add(loader.load());
		        parSist = loader.getController();
		        
		        HashMap<String, Object> variPaso = new HashMap<String, Object>();
		        variPaso.put("entidadBuscar", FaseProyectoSistema.class.getSimpleName());
		        variPaso.put("subventana", new Boolean(true));
		        variPaso.put("idEntidadBuscar", fps.id);
		        variPaso.put(GestionParametros.PARAMETROS_DIRECTOS+"",GestionParametros.PARAMETROS_DIRECTOS+"");
		        variPaso.put(GestionParametros.LISTA_PARAMETROS+"",fps.parametrosFaseSistema);
		        variPaso.put("ancho", new Double(300));
		        variPaso.put("alto", new Double(100));
		        variPaso.put(GestionParametros.NO_FILTROS,GestionParametros.NO_FILTROS);
		        this.parSist.setParametrosPaso(variPaso);
		        
		        prSist = new PanelResumible("Mostrar3R","Ocultar3R",this.imPropsSist,this.hbCabPropsSist,this.hbDetPropsSist,PanelResumible.MODO_ALTERNADO);
		        
		        ArrayList<Object> listaPintable = new ArrayList<Object>();
				listaPintable.addAll(fps.demandasSistema);	
				
				HashMap<String,Object> pasoPrimitiva = new HashMap<String,Object>();
				pasoPrimitiva.put(SistAsocFaseTabla.PADRE, this);
				
				tablaDemandasSistema.setPasoPrimitiva(pasoPrimitiva);
				tablaDemandasSistema.pintaTabla(listaPintable);
		        
			} else {

				hBPropDem.setVisible(true);
				FaseProyectoSistemaDemanda fps = ((DemAsocSistTabla) variablesPaso.get("filaDatos")).fps;
				
				if (prDem!= null) 
					prDem.compactar();
				this.hbDetPropsDem.getChildren().removeAll(this.hbDetPropsDem.getChildren());
				
		        GestionParametros c = new GestionParametros();
				
				FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(c.getFXML()));
		        	        
		        this.hbDetPropsDem.getChildren().add(loader.load());
		        parDem = loader.getController();
		        
		        HashMap<String, Object> variPaso = new HashMap<String, Object>();
		        variPaso.put("entidadBuscar", FaseProyectoSistemaDemanda.class.getSimpleName());
		        variPaso.put("subventana", new Boolean(true));
		        variPaso.put("idEntidadBuscar", fps.id);
		        variPaso.put(GestionParametros.PARAMETROS_DIRECTOS,GestionParametros.PARAMETROS_DIRECTOS);
		        variPaso.put(GestionParametros.LISTA_PARAMETROS+"",fps.parametrosFaseSistemaDemanda);
		        variPaso.put("ancho", new Double(500));
		        variPaso.put("alto", new Double(100));
		        variPaso.put(GestionParametros.NO_FILTROS,GestionParametros.NO_FILTROS);
		        this.parDem.setParametrosPaso(variPaso);
		        
		        prDem = new PanelResumible("Mostrar3R","Ocultar3R",this.imPropsDem,this.hbCabPropsDem,this.hbDetPropsDem,PanelResumible.MODO_ALTERNADO);
		        
			}
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

	@Override
	public String getControlFXML() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setClaseContenida(Object claseContenida) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean noEsPopUp() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getMetodoRetorno() {
		return "filaSeleccionada";
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		// TODO Auto-generated method stub
		
	}

}

