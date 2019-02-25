package ui.Economico.GestionPresupuestos;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
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
import model.beans.ApunteContable;
import model.beans.Concepto;
import model.beans.Coste;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.beans.RelProyectoDemanda;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaParamProyecto;
import model.metadatos.Sistema;
import model.metadatos.TipoProyecto;
import model.utils.db.ConsultaBD;
import ui.ControladorPantalla;
import ui.Dialogo;
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
    private GestionBotones gbGuardarNuevaVersion;

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
    private GestionBotones gbActualizarVersion;

    @FXML
    public ComboBox<Presupuesto> cbVersion;
	
	@FXML
	private AnchorPane anchor;
	
    @FXML
    private ComboBox<TipoProyecto> cbTipoProy;
	
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
		cbVersion.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { versionSeleccionada (true);  	}   );
		
		cbTipoProy.getItems().addAll(TipoProyecto.listado.values());
		
		gbActualizarVersion = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					guardarCambiosPresupuesto(false, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Actualizar presupuesto");
		gbActualizarVersion.desActivarBoton();
		
		gbGuardarNuevaVersion = new GestionBotones(imGuardarNuevaVersion, "GuardarAniadir3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					guardarCambiosPresupuesto(false, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Guardar como nueva versión del presupuesto");
		gbGuardarNuevaVersion.desActivarBoton();
		
		gbAniadirDemanda = new GestionBotones(imAniadirDemanda, "Editar3", false, new EventHandler<MouseEvent>() {        
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
	
	public void guardarCambiosPresupuesto(boolean nuevoForzado, boolean editar) throws Exception{
		if (this.listaDemAsociadas==null || this.listaDemAsociadas.size()==0) {
			Dialogo.error("No se pudo guardar", "No se pudo guardar el presupuesto", "Un proyecto debe tener al menos una demanda asociada");
			return;
		}
		
		if (this.presOperado.calculaTotal() == 0) {
			Dialogo.error("No se pudo guardar", "No se pudo guardar el presupuesto", "Un proyecto debe tener al menos un coste mayor que 0 asociado");
			return;
		}
		
		String idTransaccion = "guardarPresupuestoProyecto" + new Date().getTime();
		
		RelProyectoDemanda rpd = null;
		Iterator<Proyecto> itDemandasAsociadas = this.listaDemAsociadas.iterator();
		rpd = new RelProyectoDemanda();
		while (itDemandasAsociadas.hasNext()) {
			Proyecto p = itDemandasAsociadas.next();
			
			if (p.modo == Proyecto.ANIADIR || p.modo == Proyecto.MODIFICAR || p.modo == Proyecto.NEUTRO) {
				rpd.listaDemandas.add(p);
			}
		}
		
		rpd.actualizaRelaciones(idTransaccion);
		
		ConsultaBD cbd = new ConsultaBD(); 
		cbd.ejecutaTransaccion(idTransaccion);
		
		this.presOperado.descripcion = this.tVsProyecto.getText();
		this.presOperado.guardarPresupuesto(editar);	
		
		cbProyecto.getItems().removeAll(cbProyecto.getItems());
		cbVersion.getItems().removeAll(cbVersion.getItems());
		
		this.tNomProyecto.setText("");
		this.tVsProyecto.setText("");
		this.cbTipoProy.getItems().removeAll(cbTipoProy.getItems());
		cbTipoProy.getItems().addAll(TipoProyecto.listado.values());
		
		Proyecto p = new Proyecto();
		ArrayList<Proyecto> listaProyectos = p.listadoProyectosGGP();
		
		cbProyecto.getItems().addAll(listaProyectos);
		
		tablaCoste.limpiaTabla();
		tablaDemandas.limpiaTabla();
		
		Dialogo.error("Guardado correcto", "Se actualizó el presupuesto", "El guardado se realizó correctamente");
	}
	
	public void tratarModificacionesPresupuesto(Proyecto p) {
		Proyecto pEncontrado = null;
		
		Iterator<Proyecto> itAsociados = listaDemAsociadas.iterator();
		while (itAsociados.hasNext()) {
			Proyecto asoc = itAsociados.next();
			if (asoc.apunteContable == p.apunteContable && asoc.id == p.id) {
				pEncontrado = asoc;
			}
		}
		
		int operacion = 0;
		
		if (p.modo == Proyecto.ANIADIR) {
			operacion = Presupuesto.SUMAR;
			if (pEncontrado!=null && pEncontrado.modo == Proyecto.ELIMINAR)
				pEncontrado.modo = Proyecto.NEUTRO;
			else
				listaDemAsociadas.add(p);
		}
		if (p.modo == Proyecto.ELIMINAR) {
			operacion = Presupuesto.RESTAR;
			if (pEncontrado!=null && pEncontrado.modo == Proyecto.ANIADIR)
				listaDemAsociadas.remove(pEncontrado);
			else
				pEncontrado.modo = Proyecto.ELIMINAR;
		}
		if (p.modo == Proyecto.MODIFICAR) {
			this.presOperado = this.presOperado.operarPresupuestos(pEncontrado.presupuestoActual, Presupuesto.RESTAR);
			listaDemAsociadas.remove(pEncontrado);
			listaDemAsociadas.add(p);
			operacion = Presupuesto.SUMAR;
		}

		this.presOperado = this.presOperado.operarPresupuestos(p.presupuestoActual, operacion);
		
		versionSeleccionada(false);
		
		ParamTable.po.hide();
	}
	
	public void versionSeleccionada(boolean recargar) {
		try {
			gbActualizarVersion.activarBoton();
			gbGuardarNuevaVersion.activarBoton();
			
			Presupuesto p = cbVersion.getValue();
			Proyecto proy = cbProyecto.getValue();
			
			if (proy ==null) return;
			
			proy.presupuestoActual = p;
			
			if (recargar) {
				this.tNomProyecto.setText(proy.nombre);
				this.tVsProyecto.setText(p.toString());
				
				proy.cargaProyecto();
				this.cbTipoProy.setValue(TipoProyecto.listado.get(new Integer((String)proy.getValorParametro(MetaParamProyecto.TIPO_PROYECTO))));
				
				this.cbTipoProy.setDisable(true);
				this.tNomProyecto.setDisable(true);
				
				RelProyectoDemanda rpD = new RelProyectoDemanda();
				rpD.pres = p;
				rpD.proyecto = proy;
				
				this.listaDemAsociadas = proy.getDemandasAsociadas();
				
				this.presOperado = p.cloneSinCostes();
				Iterator<Proyecto> itDemandas = this.listaDemAsociadas.iterator();
				
				while (itDemandas.hasNext()) {
					Proyecto pDemanda = itDemandas.next();
					
					this.presOperado = this.presOperado.operarPresupuestos(pDemanda.presupuestoActual, Presupuesto.SUMAR);
				}
			}
			
			ArrayList<Object> listaPintable = new ArrayList<Object>();
			
			Iterator<Proyecto> itListaDemandasAsociadas = listaDemAsociadas.iterator();
			while (itListaDemandasAsociadas.hasNext()) {
				Proyecto demanda = itListaDemandasAsociadas.next();
				if (demanda.modo!=Proyecto.ELIMINAR)
					listaPintable.add(demanda);
			}
									
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
		
		if (p==null) return;
		
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
	    	
	    	ArrayList<Proyecto> listaDemandas = new ArrayList<Proyecto>();
	    	Iterator<Proyecto> itDemandas =  this.listaDemAsociadas.iterator();
	    	while (itDemandas.hasNext()) {
	    		Proyecto p = itDemandas.next();
	    		Proyecto pAux = null;
	    		if (p.apunteContable) {
	    			pAux = ((ApunteContable)p).clone();
	    		} else {
	    			pAux = p.clone();
	    		}
	    		
	    		listaDemandas.add(pAux);
	    	}
	    	
	    	parametrosPaso.put("demandasAsignadas", listaDemandas);
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
