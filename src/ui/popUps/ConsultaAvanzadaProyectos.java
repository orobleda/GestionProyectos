package ui.popUps;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.PopOver;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.ParametroProyecto;
import model.beans.Proyecto;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaParametro;
import model.metadatos.TipoProyecto;
import ui.GestionBotones;
import ui.ParamTable;
import ui.interfaces.ControladorPantalla;

public class ConsultaAvanzadaProyectos implements ControladorPantalla, PopUp {
	
	public final static String SOLO_CERRADOS = "Sólo cerrados";
	public final static String SOLO_ABIERTOS = "Sólo abiertos";
	public final static String TODOS = "Todos";
	public final static String [] estadosProyecto = {ConsultaAvanzadaProyectos.TODOS,ConsultaAvanzadaProyectos.SOLO_ABIERTOS,ConsultaAvanzadaProyectos.SOLO_CERRADOS};

	public ConsultaAvanzadaProyectos() {
		super();
	}
	
	public static ConsultaAvanzadaProyectos getInstance(ControladorPantalla ventanaPadre, int maxElementos, int tipoElemento, Node elemento) throws Exception {
		
		FXMLLoader loader = new FXMLLoader();
		ConsultaAvanzadaProyectos controlPantalla = new ConsultaAvanzadaProyectos();
        loader.setLocation(new URL(controlPantalla.getFXML()));
     	ParamTable.po = new PopOver(loader.load());
     	ParamTable.po.show(elemento);
     	ParamTable.po.setAnimated(true);
     	     	
     	HashMap<String, Object> variablesPaso = new HashMap<String, Object>();
     	variablesPaso.put("ventanaPadre", ventanaPadre);
     	variablesPaso.put("maxElementos", maxElementos);
     	variablesPaso.put("tipoElemento", tipoElemento);
     	
     	controlPantalla = loader.getController();
     	controlPantalla.setParametrosPaso(variablesPaso);
     	
     	return controlPantalla;
	}
	
	public static final String fxml = "file:src/ui/popUps/ConsultaAvanzadaProyectos.fxml"; 
	
	public HashMap<String, Object> variablesPaso = null;
	public ControladorPantalla ventanaPadre = null;
	
	public int maxElementos = 0;
	
	public static ConsultaAvanzadaProyectos objetoThis = null;
	
	public boolean esPopUp = true;
	    
    @FXML
    private TextField tNombre;

    @FXML
    private ComboBox<TipoProyecto> cbTipo;

    @FXML
    private ComboBox<String> cbCerrado;

    @FXML
    private DatePicker fxImplIni;

    @FXML
    private DatePicker fxImplFin;

    @FXML
    private DatePicker fxInilIni;

    @FXML
    private DatePicker fxIniFin;

    @FXML
    private DatePicker fxFinlIni;

    @FXML
    private DatePicker fxFinFin;

    @FXML
    private ListView<Proyecto> lFiltrados;

    @FXML
    private ImageView imSubirTodo;
    private GestionBotones gbSubirTodo;

    @FXML
    private ImageView imSubir;
    private GestionBotones gbSubir;

    @FXML
    private ImageView imBajar;
    private GestionBotones gbBajar;

    @FXML
    private ImageView imBajarTodo;
    private GestionBotones gbBajarTodo;

    @FXML
    private ListView<Proyecto> lBuscar;

    @FXML
    private ImageView imBuscar;
    private GestionBotones gbBuscar;
	

    @Override
	public AnchorPane getAnchor() {
		return null;
	}
    
    @Override
	public void resize(Scene escena) {
		
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		cbCerrado.getItems().addAll(ConsultaAvanzadaProyectos.estadosProyecto);
		cbCerrado.setValue(ConsultaAvanzadaProyectos.SOLO_ABIERTOS);
		
		gbSubir = new GestionBotones(imSubir, "Subir3R", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	subir();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "descartar Proyecto");
		gbSubirTodo = new GestionBotones(imSubirTodo, "SubirTodo3R", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	subirTodo();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "descartar todos los Proyecto");
		gbBajar = new GestionBotones(imBajar, "Bajar3R", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	bajar();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Incluir Proyecto");
		gbBajarTodo = new GestionBotones(imBajarTodo, "BajarTodo3R", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	bajarTodo();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Incluir todos los Proyecto");
		gbBuscar = new GestionBotones(imBuscar, "Tick3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	guardarProyecto();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Seleccionar Proyecto");
		
		lBuscar.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		lFiltrados.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		this.fxImplIni.valueProperty().addListener((ov, oldV, newV) -> { filtraProyectos();   });
		this.fxImplFin.valueProperty().addListener((ov, oldV, newV) -> { filtraProyectos();    });
		this.fxInilIni.valueProperty().addListener((ov, oldV, newV) -> {  filtraProyectos();    });
		this.fxIniFin.valueProperty().addListener((ov, oldV, newV) -> { filtraProyectos();   });
		this.fxFinlIni.valueProperty().addListener((ov, oldV, newV) -> { filtraProyectos();    });
		this.fxFinFin.valueProperty().addListener((ov, oldV, newV) -> { filtraProyectos();   });
		tNombre.textProperty().addListener((ov, oldV, newV) -> { filtraProyectos();  });
		
		cbTipo.getItems().addAll(TipoProyecto.tiposCompleta());
		Iterator<TipoProyecto> itTipo = cbTipo.getItems().iterator();
		while (itTipo.hasNext()) {
			TipoProyecto tp = itTipo.next();
			if (tp.codigo == TipoProyecto.ID_TODO) {
				cbTipo.setValue(tp);
				break;
			}
		} 
		cbTipo.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { filtraProyectos ();	}  );
		this.cbCerrado.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { filtraProyectos ();	}  );
	}
	
	public void subir(){
		ArrayList<Proyecto> lProyectosOriginal = new ArrayList<Proyecto>();
		lProyectosOriginal.addAll(this.lBuscar.getItems());
		
		Iterator<Proyecto> itProyecto = this.lBuscar.selectionModelProperty().get().getSelectedItems().iterator();
		while (itProyecto.hasNext()) {
			Proyecto p = itProyecto.next();
			this.lFiltrados.getItems().add(p);
			lProyectosOriginal.remove(p);
		}
		
		this.lBuscar.getItems().removeAll(this.lBuscar.getItems());
		this.lBuscar.getItems().addAll(lProyectosOriginal);

		gestionarControles();
	}
	
	public void bajar(){
		if ((lBuscar.getItems().size()+this.lFiltrados.selectionModelProperty().get().getSelectedItems().size())>this.maxElementos ) return;
		
		ArrayList<Proyecto> lProyectosOriginal = new ArrayList<Proyecto>();
		lProyectosOriginal.addAll(this.lFiltrados.getItems());
		
		Iterator<Proyecto> itProyecto = this.lFiltrados.selectionModelProperty().get().getSelectedItems().iterator();
		while (itProyecto.hasNext()) {
			Proyecto p = itProyecto.next();
			this.lBuscar.getItems().add(p);
			lProyectosOriginal.remove(p);
		}
		
		this.lFiltrados.getItems().removeAll(this.lFiltrados.getItems());
		this.lFiltrados.getItems().addAll(lProyectosOriginal);
		
		gestionarControles();
	}
	
	public void subirTodo(){
		this.lBuscar.selectionModelProperty().get().selectAll();
		subir();
	}
	
	public void bajarTodo(){
		if ((lBuscar.getItems().size()+this.lFiltrados.getItems().size())>this.maxElementos ) return;
		this.lFiltrados.selectionModelProperty().get().selectAll();
		bajar();
	}
	
	public void gestionarControles() {
		this.gbBajar.activarBoton();
		this.gbBajarTodo.activarBoton();
		this.gbSubir.activarBoton();
		this.gbSubirTodo.activarBoton();
		
		if (this.lBuscar.getItems().size()!=0) {
			this.gbBuscar.activarBoton();
		} else {
			this.gbBuscar.desActivarBoton();
		}
		
	}
	
	public void guardarProyecto() {
		try {
			ArrayList<Proyecto> lProy = new ArrayList<Proyecto>();
			
			Iterator<Proyecto> itProy = this.lBuscar.getItems().iterator();
			while (itProy.hasNext()) {
				Proyecto p = itProy.next();
				lProy.add(p);
			}
			
			Method metodo = ventanaPadre.getClass().getDeclaredMethod("fijaProyecto", ArrayList.class);
			metodo.invoke(ventanaPadre, lProy);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void filtraProyectos() {
		ArrayList<Proyecto> listaProyecto =  new ArrayList<Proyecto>();
		ArrayList<Proyecto> listaProyectoAux =  new ArrayList<Proyecto>();
		
		boolean filtro = false;
		
		listaProyecto.addAll(Proyecto.getProyectosEstatico().values());
		
		if (!"".equals(this.tNombre.getText())) {
			filtro = true;
			Iterator<Proyecto> itProy = listaProyecto.iterator();
			while (itProy.hasNext()) {
				Proyecto p = itProy.next();
				if (p.nombre.contains(this.tNombre.getText())) {
					listaProyectoAux.add(p);
				}
			}
			listaProyecto = listaProyectoAux;
			listaProyectoAux =  new ArrayList<Proyecto>();
		}
		
		if (this.fxInilIni.getValue()!=null) {
			try {
				Date fxFiltro = FormateadorDatos.toDate(this.fxInilIni.getValue());
				
				filtro = true;
				Iterator<Proyecto> itProy = listaProyecto.iterator();
				while (itProy.hasNext()) {
					Proyecto p = itProy.next();
					ParametroProyecto pp = (ParametroProyecto) p.getValorParametro(MetaParametro.PROYECTO_FX_INICIO);
					if (pp.getValor()!=null){
						Date fechaFinImpl = (Date) pp.getValor();
						Calendar cxFiltro = Calendar.getInstance();
						cxFiltro.setTime(fxFiltro);
						Calendar cxProyecto = Calendar.getInstance();
						cxProyecto.setTime(fechaFinImpl);
						
						if (cxFiltro.before(cxProyecto)) {
							listaProyectoAux.add(p);
						}
					}					
				}
				listaProyecto = listaProyectoAux;
				listaProyectoAux =  new ArrayList<Proyecto>();
			} catch (Exception e) {
				
			}
		}
		
		if (this.fxIniFin.getValue()!=null) {
			try {
				Date fxFiltro = FormateadorDatos.toDate(this.fxIniFin.getValue());
				
				filtro = true;
				Iterator<Proyecto> itProy = listaProyecto.iterator();
				while (itProy.hasNext()) {
					Proyecto p = itProy.next();
					ParametroProyecto pp = (ParametroProyecto) p.getValorParametro(MetaParametro.PROYECTO_FX_INICIO);
					if (pp.getValor()!=null){
						Date fechaFinImpl = (Date) pp.getValor();
						Calendar cxFiltro = Calendar.getInstance();
						cxFiltro.setTime(fxFiltro);
						Calendar cxProyecto = Calendar.getInstance();
						cxProyecto.setTime(fechaFinImpl);
						
						if (cxFiltro.after(cxProyecto)) {
							listaProyectoAux.add(p);
						}
					}					
				}
				listaProyecto = listaProyectoAux;
				listaProyectoAux =  new ArrayList<Proyecto>();
			} catch (Exception e) {
				
			}
		}
		
		if (this.fxFinlIni.getValue()!=null) {
			try {
				Date fxFiltro = FormateadorDatos.toDate(this.fxFinlIni.getValue());
				
				filtro = true;
				Iterator<Proyecto> itProy = listaProyecto.iterator();
				while (itProy.hasNext()) {
					Proyecto p = itProy.next();
					ParametroProyecto pp = (ParametroProyecto) p.getValorParametro(MetaParametro.PROYECTO_FX_FIN);
					if (pp.getValor()!=null){
						Date fechaFinImpl = (Date) pp.getValor();
						Calendar cxFiltro = Calendar.getInstance();
						cxFiltro.setTime(fxFiltro);
						Calendar cxProyecto = Calendar.getInstance();
						cxProyecto.setTime(fechaFinImpl);
						
						if (cxFiltro.before(cxProyecto)) {
							listaProyectoAux.add(p);
						}
					}					
				}
				listaProyecto = listaProyectoAux;
				listaProyectoAux =  new ArrayList<Proyecto>();
			} catch (Exception e) {
				
			}
		}
		
		if (this.fxFinFin.getValue()!=null) {
			try {
				Date fxFiltro = FormateadorDatos.toDate(this.fxFinFin.getValue());
				
				filtro = true;
				Iterator<Proyecto> itProy = listaProyecto.iterator();
				while (itProy.hasNext()) {
					Proyecto p = itProy.next();
					ParametroProyecto pp = (ParametroProyecto) p.getValorParametro(MetaParametro.PROYECTO_FX_FIN);
					if (pp.getValor()!=null){
						Date fechaFinImpl = (Date) pp.getValor();
						Calendar cxFiltro = Calendar.getInstance();
						cxFiltro.setTime(fxFiltro);
						Calendar cxProyecto = Calendar.getInstance();
						cxProyecto.setTime(fechaFinImpl);
						
						if (cxFiltro.after(cxProyecto)) {
							listaProyectoAux.add(p);
						}
					}					
				}
				listaProyecto = listaProyectoAux;
				listaProyectoAux =  new ArrayList<Proyecto>();
			} catch (Exception e) {
				
			}
		}
		
		if (this.fxImplIni.getValue()!=null) {
			try {
				Date fxFiltro = FormateadorDatos.toDate(this.fxImplIni.getValue());
				
				filtro = true;
				Iterator<Proyecto> itProy = listaProyecto.iterator();
				while (itProy.hasNext()) {
					Proyecto p = itProy.next();
					ParametroProyecto pp = (ParametroProyecto) p.getValorParametro(MetaParametro.PROYECTO_FX_FIN_IMPL);
					if (pp.getValor()!=null){
						Date fechaFinImpl = (Date) pp.getValor();
						Calendar cxFiltro = Calendar.getInstance();
						cxFiltro.setTime(fxFiltro);
						Calendar cxProyecto = Calendar.getInstance();
						cxProyecto.setTime(fechaFinImpl);
						
						if (cxFiltro.before(cxProyecto)) {
							listaProyectoAux.add(p);
						}
					}					
				}
				listaProyecto = listaProyectoAux;
				listaProyectoAux =  new ArrayList<Proyecto>();
			} catch (Exception e) {
				
			}
		}
		
		if (this.fxImplFin.getValue()!=null) {
			try {
				Date fxFiltro = FormateadorDatos.toDate(this.fxImplFin.getValue());
				
				filtro = true;
				Iterator<Proyecto> itProy = listaProyecto.iterator();
				while (itProy.hasNext()) {
					Proyecto p = itProy.next();
					ParametroProyecto pp = (ParametroProyecto) p.getValorParametro(MetaParametro.PROYECTO_FX_FIN_IMPL);
					if (pp.getValor()!=null){
						Date fechaFinImpl = (Date) pp.getValor();
						Calendar cxFiltro = Calendar.getInstance();
						cxFiltro.setTime(fxFiltro);
						Calendar cxProyecto = Calendar.getInstance();
						cxProyecto.setTime(fechaFinImpl);
						
						if (cxFiltro.after(cxProyecto)) {
							listaProyectoAux.add(p);
						}
					}					
				}
				listaProyecto = listaProyectoAux;
				listaProyectoAux =  new ArrayList<Proyecto>();
			} catch (Exception e) {
				
			}
		}
		
		if (this.cbCerrado.getValue()!=null && this.cbCerrado.getValue()!=ConsultaAvanzadaProyectos.TODOS) {
			filtro = true;
			Iterator<Proyecto> itProy = listaProyecto.iterator();
			while (itProy.hasNext()) {
				Proyecto p = itProy.next();
				ParametroProyecto pp = (ParametroProyecto) p.getValorParametro(MetaParametro.PROYECTO_CERRADO);
				if (ConsultaAvanzadaProyectos.SOLO_ABIERTOS == this.cbCerrado.getValue() && !Constantes.toNumBoolean(pp.valorEntero)) {
					listaProyectoAux.add(p);
				}
				if (ConsultaAvanzadaProyectos.SOLO_CERRADOS == this.cbCerrado.getValue() && Constantes.toNumBoolean(pp.valorEntero)) {
					listaProyectoAux.add(p);
				}
			}
			listaProyecto = listaProyectoAux;
			listaProyectoAux =  new ArrayList<Proyecto>();
		}		
		
		if (cbTipo.getValue()!=null && cbTipo.getValue().codigo!=TipoProyecto.ID_TODO) {
			filtro = true;
			Iterator<Proyecto> itProy = listaProyecto.iterator();
			while (itProy.hasNext()) {
				Proyecto p = itProy.next();
				ParametroProyecto pp = (ParametroProyecto) p.getValorParametro(MetaParametro.PROYECTO_TIPO_PROYECTO);
     			if (pp.valorEntero == cbTipo.getValue().codigo) {
     				listaProyectoAux.add(p);
     			}
     			
     			if (cbTipo.getValue().codigo==TipoProyecto.ID_PROYEVOLS && (pp.valorEntero == TipoProyecto.ID_EVOLUTIVO || pp.valorEntero == TipoProyecto.ID_PROYECTO) ) {
     				listaProyectoAux.add(p);
     			}
			}
			listaProyecto = listaProyectoAux;
			listaProyectoAux =  new ArrayList<Proyecto>();
		}
		
		
		if (filtro) {
			this.lFiltrados.getItems().removeAll(this.lFiltrados.getItems());
			this.lFiltrados.getItems().addAll(listaProyecto);
		} else {
			this.lFiltrados.getItems().removeAll(this.lFiltrados.getItems());
			this.lFiltrados.getItems().addAll(Proyecto.getProyectosEstatico().values());
		}
	}
	
	@Override
	public String getControlFXML() {
		return this.getFXML();
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
     	this.ventanaPadre = (ControladorPantalla) variablesPaso.get("ventanaPadre");
     	
     	this.lFiltrados.getItems().addAll(Proyecto.getProyectosEstatico().values());
     	filtraProyectos();
     	
     	this.maxElementos = (Integer) variablesPaso.get("maxElementos");
     	
     	if ((Integer) variablesPaso.get("tipoElemento") != null) {
     		int tipoElementos = (Integer) variablesPaso.get("tipoElemento");
     		
     		this.cbTipo.setVisible(false);
     		Iterator<TipoProyecto> itTipo = this.cbTipo.getItems().iterator();
     		TipoProyecto tpAux = null;
     		while (itTipo.hasNext()) {
     			TipoProyecto tp = itTipo.next();
     			if (tp.codigo == tipoElementos){
     				tpAux = tp;
     				break;
     			}
     		}
     		if (tpAux!=null)
     			cbTipo.setValue(tpAux);
     	}
     	
		gestionarControles();
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

	@Override
	public void setClaseContenida(Object claseContenida) {
		// TODO Auto-generated method stub
		
	}

}
