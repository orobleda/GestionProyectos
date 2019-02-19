package ui.Economico.GestionPresupuestos;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.PopOver;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import model.beans.Concepto;
import model.beans.Coste;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.beans.RelProyectoDemanda;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import ui.ControladorPantalla;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Tabla;
import ui.Tableable;
import ui.Economico.GestionPresupuestos.Tables.DesgloseDemandasAsocidasTabla;
import ui.Economico.GestionPresupuestos.Tables.LineaCosteDesglosado;

public class GestionPresupuestos implements ControladorPantalla {
	

	public static final String fxml = "file:src/ui/Economico/GestionPresupuestos/GestionPresupuestos.fxml";
	
	@FXML
    private TextField tNomProyecto;

    @FXML
    private TableView<Tableable> tCoste;
    public Tabla tablaCoste;

    @FXML
    private TableView<Tableable> tDemandas;
    public Tabla tablaDemandas;

    @FXML
    public ComboBox<Proyecto> cbProyecto;

    @FXML
    private ImageView imGuardarNuevaVersion;

    @FXML
    private TextField tVsProyecto;

    @FXML
    private ImageView imNuevoProyecto;
    private GestionBotones gbNuevoProyecto;

    @FXML
    private ImageView imAniadirDemanda;
    private GestionBotones gbAniadirDemanda;

    @FXML
    private ImageView imGuardar;

    @FXML
    public ComboBox<Presupuesto> cbVersion;
	
	@FXML
	private AnchorPane anchor;
	
	ArrayList<Proyecto> listaDemAsociadas = null;
	Presupuesto presOperado = null;
	
	
	public void initialize(){
		tablaDemandas = new Tabla(tDemandas,new DesgloseDemandasAsocidasTabla());
		tablaCoste = new Tabla(tCoste,new LineaCosteDesglosado());
		
		Proyecto p = new Proyecto();
		ArrayList<Proyecto> listaProyectos = p.listadoProyectosGGP();
		
		cbProyecto.getItems().addAll(listaProyectos);
		cbProyecto.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { buscaPresupuestos (newValue);  	}   );
		
		cbVersion.getItems().removeAll(cbVersion.getItems());
		cbVersion.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { versionSeleccionada ();  	}   );
		
		gbAniadirDemanda = new GestionBotones(imAniadirDemanda, "Mas3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					aniadirEstimacion();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Añadir Demanda");
		gbAniadirDemanda.desActivarBoton();
		
		gbNuevoProyecto = new GestionBotones(imNuevoProyecto, "Nuevo3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {						        
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Nuevo Proyecto");
		gbNuevoProyecto.activarBoton();
		
		tablaCoste.pintaTabla(new ArrayList<Object>());
		tablaDemandas.pintaTabla(new ArrayList<Object>());
	}
	
	public void versionSeleccionada() {
		try {
			Presupuesto p = cbVersion.getValue();
			Proyecto proy = cbProyecto.getValue();
			proy.presupuestoActual = p;
			
			this.tNomProyecto.setText(proy.nombre);
			this.tVsProyecto.setText(p.toString());
			
			RelProyectoDemanda rpD = new RelProyectoDemanda();
			rpD.pres = p;
			rpD.proyecto = proy;
			
			this.listaDemAsociadas = proy.getDemandasAsociadas();
			
			this.presOperado = new Presupuesto();
			Iterator<Proyecto> itDemandas = this.listaDemAsociadas.iterator();
			
			while (itDemandas.hasNext()) {
				Proyecto pDemanda = itDemandas.next();
				
				this.presOperado = this.presOperado.operarPresupuestos(pDemanda.presupuestoActual, Presupuesto.SUMAR);
				
			}
			
						
			ArrayList<Object> listaPintable = new ArrayList<Object>();
			listaPintable.addAll(listaDemAsociadas);
			
			tablaDemandas.pintaTabla(listaPintable);
			
			HashMap<String,Concepto> listaConceptos = new HashMap<String,Concepto>();
			HashMap<String,Concepto> listaConceptosDesglosado = null;
			ArrayList<Object> listaConceptosDesglosada = new ArrayList<Object>();
			
			Iterator<Coste> itCoste = this.presOperado.costes.values().iterator();
			float acumulado = 0;
			float acumuladoSistema = 0;
			Concepto caux = null;
			
			while (itCoste.hasNext()) {
				Coste c = itCoste.next();
				acumuladoSistema = 0;
							
				Iterator<Concepto> itConcepto = c.conceptosCoste.values().iterator();
				
				listaConceptosDesglosado = new HashMap<String,Concepto>();
				listaConceptosDesglosada.add(new LineaCosteDesglosado(listaConceptosDesglosado));
				
				if (c.conceptosCoste.size()!=0) {
					listaConceptosDesglosado.put("SISTEMA", (Concepto) c.conceptosCoste.values().toArray()[0]);
					if (((Concepto) c.conceptosCoste.values().toArray()[0]).coste==null)
						((Concepto) c.conceptosCoste.values().toArray()[0]).coste = c;
				}
				
				while (itConcepto.hasNext()) {
					Concepto co = itConcepto.next();
					listaConceptosDesglosado.put(co.tipoConcepto.codigo, co);
									
					if (listaConceptos.containsKey(co.tipoConcepto.codigo)) {
						caux = listaConceptos.get(co.tipoConcepto.codigo);
					} else {
						caux = new Concepto();					
						listaConceptos.put(co.tipoConcepto.codigo,caux);
					}
					acumuladoSistema+=co.valorEstimado;
					acumulado+=co.valorEstimado;
					caux.valorEstimado += co.valorEstimado;
				}
				
				caux = new Concepto();
				MetaConcepto mc = new MetaConcepto();
				mc.codigo = "TOTAL";
				mc.descripcion = "Total";
				mc.id = MetaConcepto.ID_TOTAL;
				caux.tipoConcepto = mc;
				caux.valorEstimado = acumuladoSistema;
				listaConceptosDesglosado.put(caux.tipoConcepto.codigo,caux);
			}
			
			caux = new Concepto();
			Coste coAux = new Coste();
			coAux.sistema = Sistema.getInstanceTotal();
			caux.coste = coAux;
			MetaConcepto mc = new MetaConcepto();
			mc.codigo = "TOTAL";
			mc.descripcion = "Total";
			mc.id = MetaConcepto.ID_TOTAL;
			caux.tipoConcepto = mc;
			caux.valorEstimado = acumulado;
			listaConceptos.put(caux.tipoConcepto.codigo,caux);
			listaConceptos.put("SISTEMA", caux);
			listaConceptosDesglosada.add(new LineaCosteDesglosado(listaConceptos));
			
			ArrayList<Object> listaPintableConce = new ArrayList<Object>();
			listaPintableConce.addAll(listaConceptosDesglosada);
			
			tablaCoste.pintaTabla(listaPintableConce);
			tablaCoste.formateaTabla();
			
			gbAniadirDemanda.activarBoton();
			
		} catch (Exception es) {
			es.printStackTrace();
		}
	}
	
	private void buscaPresupuestos(Proyecto p) {
		cbVersion.getItems().removeAll(cbVersion.getItems());
		
		Presupuesto pres = new Presupuesto();		
		ArrayList<Presupuesto> listado = pres.buscaPresupuestos(p.id);
		
		cbVersion.getItems().addAll(listado);
		
		if (listado.size()>0) {
			
		}
	}
	
	public void aniadirEstimacion() {
		try {
			
			
			FXMLLoader loader = new FXMLLoader();
			
			HashMap<String, Object> parametrosPaso = new HashMap<String, Object>();
			
			parametrosPaso.put("filaDatos", null);							        	
			parametrosPaso.put("columna", null);
			parametrosPaso.put("evento", null);
			parametrosPaso.put("controladorPantalla", this);
			
			AniadeDemanda controlPantalla = new AniadeDemanda();
	    	
	    	if (ParamTable.po!=null){
	    		ParamTable.po.hide();
	    		ParamTable.po = null;
	    	}
	    	
	    	parametrosPaso.put("demandasAsignadas", this.listaDemAsociadas);
	    	parametrosPaso.put("gestionPresupuestos", this);
	    	
	    	loader.setLocation(new URL(controlPantalla.getFXML()));
		    Pane pane = loader.load();
		    controlPantalla = (AniadeDemanda) loader.getController();
		    controlPantalla.setParametrosPaso(parametrosPaso);
		    ParamTable.po = new PopOver(pane);
		    parametrosPaso.put("PopOver", ParamTable.po);
		    ParamTable.po.setTitle("");
		    ParamTable.po.show(this.tDemandas);
		    ParamTable.po.setAnimated(true);
		    ParamTable.po.setAutoHide(true);
		} catch (Exception ex) {
			ex.printStackTrace();
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
