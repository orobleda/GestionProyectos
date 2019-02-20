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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.beans.BaseCalculoConcepto;
import model.beans.Concepto;
import model.beans.Coste;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.beans.Tarifa;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import ui.ControladorPantalla;
import ui.GestionBotones;
import ui.Tabla;
import ui.Tableable;
import ui.Economico.EstimacionesValoraciones.Tables.LineaCostePresupuesto;
import ui.Economico.GestionPresupuestos.Tables.LineaCosteDesglosado;

public class AniadeDemanda implements ControladorPantalla {
	

	public static final String fxml = "file:src/ui/Economico/GestionPresupuestos/AniadeDemanda.fxml";
	
	public static HashMap<String, Object> variablesPaso = null;
	
	@FXML
	private AnchorPane anchor;
	
    @FXML
    private TableView<Tableable> tTablaConceptos;
    public Tabla tablaCoste;

    @FXML
    private ImageView imGuardarConcepto;
    private GestionBotones gbGuardarConcepto;

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
    private HBox hbCantidad;

    @FXML
    private ComboBox<BaseCalculoConcepto> cbBaseCalculo;

    @FXML
    private ComboBox<Tarifa> cbTarifa;

    @FXML
    private TextField teCantidad;    

    @FXML
    private ComboBox<Sistema> cbSistema;

    @FXML
    private ComboBox<MetaConcepto> cbPorcentaje;

    @FXML
    private ComboBox<MetaConcepto> cbConcepto;

    @FXML
    private TextField tePorcentaje;

    @FXML
    private HBox hbPorcentaje;

    @FXML
    private TextField teHoras;

    @FXML
    private TextField teCantidadEst;    

    @FXML
    private HBox hbTarifa;
    
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
					aniadePresupuesto();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Añade demanda");
		gbGuardarAniadir.desActivarBoton();
		
		gbGuardarEliminar = new GestionBotones(this.imGuardarEliminar, "GuardarBorrar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					eliminaPresupuesto();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Elimina Demanda");
		gbGuardarEliminar.desActivarBoton();
		
		gbGuardarEditar = new GestionBotones(this.imGuardarEditar, "GuardarEditar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					modificaPresupuesto();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Guarda Modificación Presupuesto");
		gbGuardarEditar.desActivarBoton();
		
		this.cbSistema.getItems().addAll(Sistema.listado.values());
		this.cbConcepto.getItems().addAll(MetaConcepto.listado.values());
		
		this.cbBaseCalculo.getItems().addAll(BaseCalculoConcepto.listado());
		cbBaseCalculo.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { seleccionaBaseCalculo (newValue);	}  );
		
		HashMap<String, Object> filtros = new HashMap<String, Object>();
		filtros.put(Tarifa.filtro_VIGENTES, new Boolean(true));
		
		cbTarifa.getItems().addAll(new Tarifa().listado(filtros));
		
		teCantidad.focusedProperty().addListener((ov, oldV, newV) -> { if (!newV) { calculaBaseCoste();  }  });
		teHoras.focusedProperty().addListener((ov, oldV, newV) -> {    if (!newV) { calculaBaseHoras();  }  });
		cbTarifa.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { calculaBaseHoras();   	});
		tePorcentaje.focusedProperty().addListener((ov, oldV, newV) -> { if (!newV) { calculaBasePorcentaje(); } });
		cbPorcentaje.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { calculaBasePorcentaje(); 	}   );
	}
	
	public void calculaBaseCoste() {
		try {
			Presupuesto pres = this.cbVersionPres.getValue();
			Concepto c = null;
			Coste coste = null;
			
			Iterator<Coste> itCoste = pres.costes.values().iterator();
			while (itCoste.hasNext()) {
				coste = itCoste.next();
				Sistema sAux = coste.sistema;
				
				if (sAux.codigo.equals(cbSistema.getValue().codigo)) {
					c = coste.conceptosCoste.get(cbConcepto.getValue().codigo);
					break;
				}
			}
			
			if (c==null) {
				c = new Concepto();
			}
			
			c = c.clone();
			c.tipoConcepto = this.cbConcepto.getValue();
			c.valor = (Float) FormateadorDatos.parseaDato(teCantidad.getText(), FormateadorDatos.FORMATO_MONEDA);
			c.baseCalculo = cbBaseCalculo.getValue();	
			
			if (coste==null) {
				c.calculaCantidadEstimada(new HashMap<String,Concepto>());
			} else {
				c.calculaCantidadEstimada(coste.conceptosCoste);
			}			
			
		    teCantidadEst.setText(FormateadorDatos.formateaDato(c.valorEstimado, FormateadorDatos.FORMATO_MONEDA)); 
		    teCantidad.setText(FormateadorDatos.formateaDato(c.valorEstimado, FormateadorDatos.FORMATO_MONEDA));
            
		} catch (Exception e)  {}
	}
	
	public void calculaBasePorcentaje() {
		try {
			MetaConcepto mcc = cbPorcentaje.getValue();
			
			if (mcc!=null) {
				Presupuesto pres = this.cbVersionPres.getValue();
				Concepto c = null;
				Coste coste = null;
				
				Iterator<Coste> itCoste = pres.costes.values().iterator();
				while (itCoste.hasNext()) {
					coste = itCoste.next();
					Sistema sAux = coste.sistema;
					
					if (sAux.codigo.equals(cbSistema.getValue().codigo)) {
						c = coste.conceptosCoste.get(cbConcepto.getValue().codigo);
						break;
					}
				}
				
				if (c==null) {
					c = new Concepto();
				}
				
				c = c.clone();
				c.tipoConcepto = this.cbConcepto.getValue();
				c.respectoPorcentaje = mcc;
				c.porcentaje = (Integer) FormateadorDatos.parseaDato(tePorcentaje.getText(), FormateadorDatos.FORMATO_INT);
				c.baseCalculo = cbBaseCalculo.getValue();
				
				if (coste==null) {
					c.calculaCantidadEstimada(new HashMap<String,Concepto>());
				} else {
					c.calculaCantidadEstimada(coste.conceptosCoste);
				}
				
				teCantidadEst.setText(FormateadorDatos.formateaDato(c.valorEstimado, FormateadorDatos.FORMATO_MONEDA));
			} else {
				String valor = FormateadorDatos.formateaDato("0", FormateadorDatos.FORMATO_MONEDA);
				teCantidadEst.setText(valor);
			}
		} catch (Exception e){
			teCantidadEst.setText("0 €");
		}
	}
	
	public void calculaBaseHoras() {
		try {
			Tarifa tf = cbTarifa.getValue();
			
			if (tf!=null) {
				Presupuesto pres = this.cbVersionPres.getValue();
				Concepto c = null;
				Coste coste = null;
				
				Iterator<Coste> itCoste = pres.costes.values().iterator();
				while (itCoste.hasNext()) {
					coste = itCoste.next();
					Sistema sAux = coste.sistema;
					
					if (sAux.codigo.equals(cbSistema.getValue().codigo)) {
						c = coste.conceptosCoste.get(cbConcepto.getValue().codigo);
						break;
					}
				}
				
				if (c==null) {
					c = new Concepto();
				}
				
				c = c.clone();
				
				c.tarifa = tf;
				c.horas = (Float) FormateadorDatos.parseaDato(teHoras.getText(), FormateadorDatos.FORMATO_MONEDA);
				c.baseCalculo = cbBaseCalculo.getValue();
				
				if (coste==null) {
					c.calculaCantidadEstimada(new HashMap<String,Concepto>());
				} else {
					c.calculaCantidadEstimada(coste.conceptosCoste);
				}	
				
				teCantidadEst.setText(FormateadorDatos.formateaDato(c.valorEstimado, FormateadorDatos.FORMATO_MONEDA));
				teHoras.setText(FormateadorDatos.formateaDato(teHoras.getText(), FormateadorDatos.FORMATO_REAL));
			} else {
				String valor = FormateadorDatos.formateaDato("0", FormateadorDatos.FORMATO_MONEDA);
				teCantidadEst.setText(valor);
			}
		} catch (Exception e){
			
		}
	}
	
	public void seleccionaBaseCalculo(BaseCalculoConcepto bcc){
		try {
			if (bcc.id == BaseCalculoConcepto.CALCULO_BASE_COSTE  ) {
				hbTarifa.setVisible(false);
				hbCantidad.setVisible(true);
				hbPorcentaje.setVisible(false);
				teCantidad.setText("");
			}
			if (bcc.id == BaseCalculoConcepto.CALCULO_BASE_HORAS  ) {
				hbTarifa.setVisible(true);
				hbCantidad.setVisible(false);
				hbPorcentaje.setVisible(false);	
				cbTarifa.getItems().removeAll(cbTarifa.getItems());
				cbTarifa.getItems().addAll(new Tarifa().vigentes());
			}
			if (bcc.id == BaseCalculoConcepto.CALCULO_BASE_PORC  ) {
				hbTarifa.setVisible(false);
				hbCantidad.setVisible(false);
				hbPorcentaje.setVisible(true);	
								
				Iterator<MetaConcepto> itMcConcepto = new MetaConcepto().aPorcentaje().iterator();
				while (itMcConcepto.hasNext()) {
					MetaConcepto mc = itMcConcepto.next();
					if (!mc.codigo.equals(this.cbConcepto.getValue().codigo)) {
						this.cbPorcentaje.getItems().add(mc);
					}
				}
			}
			
			gbGuardarConcepto.desActivarBoton();
			teCantidadEst.setText("");
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void aniadePresupuesto() {
		Proyecto p = this.cbEstimacion.getValue();
		p.presupuestoActual = this.cbVersionPres.getValue();
		
		p.modo = Proyecto.ANIADIR;
		
		this.gp.tratarModificacionesPresupuesto(p);				
	}
	
	private void eliminaPresupuesto() {
		Proyecto p = this.cbEstimacion.getValue();
		p.presupuestoActual = this.cbVersionPres.getValue();
		
		p.modo = Proyecto.ELIMINAR;
		
		this.gp.tratarModificacionesPresupuesto(p);	
	}
	
	private void modificaPresupuesto() {
		Proyecto p = this.cbEstimacion.getValue();
		p.presupuestoActual = this.cbVersionPres.getValue();
		
		p.modo = Proyecto.MODIFICAR;
		
		this.gp.tratarModificacionesPresupuesto(p);				
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
	
	public void estudiaAccionPresupuesto() {
		Iterator<Proyecto> itDemandasAsociadas = this.listaDemandasAsociadas.iterator();
		boolean encontrado = false;
		
		Proyecto pSeleccionado = this.cbEstimacion.getValue();
		Proyecto pEncontrado = null;
		
		while (itDemandasAsociadas.hasNext()) {
			Proyecto p = itDemandasAsociadas.next();
			
			if (pSeleccionado.id == p.id && pSeleccionado.apunteContable == p.apunteContable) {
				encontrado = true;
				pEncontrado = p; 
				break;
			}
		}
		
		if (pSeleccionado.apunteContable) {
			this.vbDesgloseConceptos.setDisable(false);
			this.hbPorcentaje.setVisible(false);
			this.hbCantidad.setVisible(false);
			this.hbTarifa.setVisible(false);
			gbGuardarConcepto.activarBoton();
		} else {
			this.vbDesgloseConceptos.setDisable(true);			
			gbGuardarConcepto.desActivarBoton();
		}
		
		this.gbGuardarEditar.desActivarBoton();
		this.gbGuardarAniadir.desActivarBoton();
		this.gbGuardarEliminar.desActivarBoton();
		
		if (encontrado) {
			if (pEncontrado.apunteContable) {
				this.gbGuardarEditar.activarBoton();
			} else {
				if (pEncontrado.modo == Proyecto.NEUTRO || pEncontrado.modo == Proyecto.ANIADIR) {
					this.gbGuardarEliminar.activarBoton();					
				} else {
					this.gbGuardarAniadir.activarBoton();
				}
			}
		} else {
			this.gbGuardarAniadir.activarBoton();
		}		
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
			
			estudiaAccionPresupuesto();
			
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
