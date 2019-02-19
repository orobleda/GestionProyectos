package ui.Economico.GestionPresupuestos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.beans.Concepto;
import model.beans.Coste;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.beans.RelProyectoDemanda;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import ui.ControladorPantalla;
import ui.GestionBotones;
import ui.Tabla;
import ui.Tableable;
import ui.Economico.GestionPresupuestos.Tables.LineaCosteDesglosado;

public class AniadeDemanda implements ControladorPantalla {
	

	public static final String fxml = "file:src/ui/Economico/GestionPresupuestos/AniadeDemanda.fxml";
	
	public static HashMap<String, Object> variablesPaso = null;
	
	@FXML
	private AnchorPane anchor;
	
    @FXML
    private TextField tImporte;

    @FXML
    private TableView<Tableable> tTablaConceptos;
    public Tabla tablaCoste;

    @FXML
    private ComboBox<?> cbTarifa;

    @FXML
    private ImageView imGuardarConcepto;
    private GestionBotones gbGuardarConcepto;

    @FXML
    private ComboBox<?> cbConcepto;

    @FXML
    private TextField tHoras;

    @FXML
    private ImageView imGuardarEliminar;
    private GestionBotones gbGuardarEliminar;
    
    @FXML
    private ImageView imGuardarAniadir;
    private GestionBotones gbGuardarAniadir;

    @FXML
    private ImageView imGuardarEditar;
    private GestionBotones gbGuardarEditar;

    @FXML
    private ComboBox<Proyecto> cbEstimacion;

    @FXML
    private ComboBox<Presupuesto> cbVersionPres;
    
    @FXML
    private VBox vbDesgloseConceptos;
    
    public ArrayList<Proyecto> listaDemandasAsociadas = null;
    
    public GestionPresupuestos gp = null;
	
	
	public void initialize(){
		tablaCoste = new Tabla(tTablaConceptos,new LineaCosteDesglosado());
		
		this.cbVersionPres.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { versionSeleccionada ();  	}   );
		this.cbEstimacion.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { buscaPresupuestos (newValue);  	}   );
		
		vbDesgloseConceptos.setDisable(true);
		
		gbGuardarConcepto = new GestionBotones(imGuardarConcepto, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
				
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "GuardarConcepto");
		gbGuardarConcepto.desActivarBoton();
		
		gbGuardarAniadir = new GestionBotones(this.imGuardarAniadir, "GuardarAniadir3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
				
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "GuardarConcepto");
		gbGuardarAniadir.desActivarBoton();
		
		gbGuardarEliminar = new GestionBotones(this.imGuardarEliminar, "GuardarBorrar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
				
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "GuardarConcepto");
		gbGuardarEliminar.desActivarBoton();
		
		gbGuardarEditar = new GestionBotones(this.imGuardarEditar, "GuardarEditar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
				
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "GuardarConcepto");
		gbGuardarEditar.desActivarBoton();
	}
	
	private void buscaPresupuestos(Proyecto p) {
		this.cbVersionPres.getItems().removeAll(cbVersionPres.getItems());
		
		Presupuesto pres = new Presupuesto();
		ArrayList<Presupuesto> listado = null;
		if (p.apunteContable) {
			pres.idApunteContable = p.id;
			listado = pres.buscaPresupuestosAPunteContable();
		}
		else 
			listado = pres.buscaPresupuestos(p.id);
		
		cbVersionPres.getItems().addAll(listado);
		
	}
	
	@SuppressWarnings("unchecked")
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		AniadeDemanda.variablesPaso = variablesPaso;
		listaDemandasAsociadas = new ArrayList<Proyecto>();
		
		Proyecto p = new Proyecto();
		ArrayList<Proyecto> listaProyectos = p.listadoDemandas();
		
		this.gp = (GestionPresupuestos) variablesPaso.get("gestionPresupuestos");
		
		if (variablesPaso!=null && variablesPaso.containsKey("demandasAsignadas") && variablesPaso.get("demandasAsignadas")!=null) {
			listaDemandasAsociadas = (ArrayList<Proyecto>) variablesPaso.get("demandasAsignadas");
			
			Iterator<Proyecto> itProyectos = listaDemandasAsociadas.iterator();
			while (itProyectos.hasNext()) {
				Proyecto pAux  = itProyectos.next();
								
				if (pAux.apunteContable) {
					listaProyectos.add(pAux);
				}
				
			}
		}
		
		this.cbEstimacion.getItems().addAll(listaProyectos);
	}
	
	public void versionSeleccionada() {
		Presupuesto p = this.cbVersionPres.getValue();
		Proyecto proy = this.cbEstimacion.getValue();
		pintaPresupuesto(proy, p);
	}
	
	public void pintaPresupuesto(Proyecto proyecto, Presupuesto pres) {
		try { 
			if (pres==null) {
				ArrayList<Object> listaPintableConce = new ArrayList<Object>();
				tablaCoste.pintaTabla(listaPintableConce);
				return;
			}
			
			Presupuesto p = pres;
			Proyecto proy = proyecto;
			proy.presupuestoActual = p;
			
			if (proyecto.apunteContable) {
				this.vbDesgloseConceptos.setDisable(false);
				gbGuardarConcepto.activarBoton();
				this.gbGuardarEditar.activarBoton();
				this.gbGuardarAniadir.desActivarBoton();
				this.gbGuardarEliminar.desActivarBoton();
			} else {
				this.vbDesgloseConceptos.setDisable(true);
				gbGuardarConcepto.desActivarBoton();
				
				
				
			}
			
			p.cargaCostes();
			
			HashMap<String,Concepto> listaConceptos = new HashMap<String,Concepto>();
			HashMap<String,Concepto> listaConceptosDesglosado = null;
			ArrayList<Object> listaConceptosDesglosada = new ArrayList<Object>();
			
			Iterator<Coste> itCoste = p.costes.values().iterator();
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
			
		} catch (Exception es) {
			es.printStackTrace();
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
