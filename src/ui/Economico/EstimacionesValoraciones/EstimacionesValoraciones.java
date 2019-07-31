package ui.Economico.EstimacionesValoraciones;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.PopOver;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.BaseCalculoConcepto;
import model.beans.Concepto;
import model.beans.Coste;
import model.beans.ParametroProyecto;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.interfaces.Loadable;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaFormatoProyecto;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import model.metadatos.TipoDato;
import model.metadatos.TipoEnumerado;
import ui.ConfigTabla;
import ui.Dialogo;
import ui.Economico.EstimacionesValoraciones.Tables.LineaCostePresupuesto;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;
import ui.popUps.SeleccionElemento;

public class EstimacionesValoraciones implements ControladorPantalla {
	

	public static final String fxml = "file:src/ui/Economico/EstimacionesValoraciones/EstimacionesValoraciones.fxml";
	
	PopOver popUp = null;
	
	@FXML
	private AnchorPane anchor;
	
	@FXML
	private ComboBox<Proyecto> cbProyectos = null;
	@FXML
	private ComboBox<TipoEnumerado> cbTipoPrep = null;
	@FXML
	private ComboBox<Presupuesto> cbVsPresupuesto = null;
	@FXML
	private TextArea taDesc = null;
	@FXML
	private TextField tId = null;
	@FXML
	private TextField tVersion = null;
	@FXML
	private TextField tFxAlta = null;
	@FXML
	private TitledPane tpConsulta = null;
	@FXML
	private TitledPane tpResultados = null;
	@FXML
	private AnchorPane aResultados = null;
	@FXML
	private ImageView imEditDesc = null;
	@FXML
	private ImageView imEditTipoPres = null;
	@FXML
	private ImageView imAniadirSistma = null;
	@FXML
	private ImageView imVersionarPres = null;
	@FXML
	private ImageView imGuardarPresupuesto = null;
	@FXML
	private ImageView imEliminarPres = null;
	@FXML
	private ImageView imAniadirPresupuesto = null;
	@FXML
	private ImageView imBuscarPresupuesto = null;
	@FXML
	private TableView<Tableable> tLineasCoste = null;
	@FXML
	private TableView<Tableable> tResumenCoste = null;
	
	public static boolean cambiaDescripcion = false;
	public static boolean cambiaTipo = false;
	
	public static Proyecto proyConsultado = null;
	public static Presupuesto presupuesto = null;
	
	@Override
	public void resize(Scene escena) {
		
	}
		
	public void fotoInicial(){
		Proyecto p = new Proyecto();
		try { cbProyectos.getItems().removeAll(cbProyectos.getItems()); } catch (Exception e) {}
		cbProyectos.getItems().addAll(p.listadoDemandas());
		cbVsPresupuesto.getItems().removeAll(cbVsPresupuesto.getItems());
		
		cambiaEstadoBoton(false, imGuardarPresupuesto);
		cambiaEstadoBoton(false, imEliminarPres);
		cambiaEstadoBoton(false, imVersionarPres);
		cambiaEstadoBoton(false, imAniadirSistma);
		cambiaEstadoBoton(false, imEditTipoPres);
		cambiaEstadoBoton(false, imEditDesc);
		cambiaEstadoBoton(false, imBuscarPresupuesto);
		cambiaEstadoBoton(false, imAniadirPresupuesto);		
		
		tpConsulta.setExpanded(true);
		tpResultados.setExpanded(false);
		tFxAlta.setText("");
		tId.setText("");
		tVersion.setText("");
		taDesc.setText("");
		cbTipoPrep.getItems().removeAll(cbTipoPrep.getItems());
		
		tResumenCoste.getItems().removeAll(tResumenCoste.getItems());
		tLineasCoste.getItems().removeAll(tLineasCoste.getItems());
		
		tResumenCoste.getColumns().removeAll(tResumenCoste.getColumns());
		tLineasCoste.getColumns().removeAll(tLineasCoste.getColumns());
	}
	
	public void cambiaEstadoBoton(boolean activado, ImageView boton) {
		if (activado) {
			boton.setMouseTransparent(false);	
			boton.getStyleClass().remove("iconoDisabled");
			boton.getStyleClass().add("iconoEnabled");
		} else {
			boton.setMouseTransparent(true);	
			boton.getStyleClass().remove("iconoEnabled");
			boton.getStyleClass().add("iconoDisabled");
		}
	}
	
	public void initialize(){
		
		fotoInicial();
				
		cbProyectos.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { buscaPresupuestos (newValue);  	}   ); 
		cbVsPresupuesto.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {	cambiaEstadoBoton(true, imBuscarPresupuesto); 	}  ); 
		imEditDesc.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 editarDescripcion(); }	});
		imEditTipoPres.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 editarTipo(); }	});
		imAniadirSistma.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 eligeSistema(); }	});
		imAniadirPresupuesto.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 nuevoPresupuesto(); }	});
		imVersionarPres.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 guardarNuevaVsPresupuesto(false); }	});
		imGuardarPresupuesto.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 guardarNuevaVsPresupuesto(true); }	});
		imBuscarPresupuesto.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 buscarPresupuesto(); }	});
		imEliminarPres.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	borrarPresupuesto(); }	});
	}
	
	private void buscaPresupuestos(Proyecto p) {
		EstimacionesValoraciones.presupuesto = null;
		EstimacionesValoraciones.proyConsultado = p;
		cbVsPresupuesto.getItems().removeAll(cbVsPresupuesto.getItems());
		
		Presupuesto pres = new Presupuesto();		
		ArrayList<Presupuesto> listado = pres.buscaPresupuestos(p.id);
		
		cbVsPresupuesto.getItems().addAll(listado);
		
		cambiaEstadoBoton(true, imAniadirPresupuesto);
	}
	
	private void eligeSistema(){
		try {
			ArrayList<Object> yaIncluidos = null;
			
			if (EstimacionesValoraciones.presupuesto!=null) {
				yaIncluidos = new ArrayList<Object>();
				Iterator<Coste> itCoste = EstimacionesValoraciones.presupuesto.costes.values().iterator();
				
				while(itCoste.hasNext()) {
					Coste c = itCoste.next();
					yaIncluidos.add(c.sistema);
				}
			}
			
			FXMLLoader loader = new FXMLLoader();
			SeleccionElemento controlPantalla = new SeleccionElemento(this, "sistemaSeleccionado",  new Sistema(), new HashMap<String, Object>(),yaIncluidos);
	        loader.setLocation(new URL(controlPantalla.getFXML()));
	     	popUp = new PopOver((AnchorPane) loader.load());
	     	popUp.show(imAniadirSistma);
	     	popUp.setAnimated(true);
		} catch (Exception e) {
			
		}
	}
	
	public void borrarPresupuesto(){
		try {
			EstimacionesValoraciones.presupuesto.borrarPresupuesto(null);
			
			fotoInicial();
			
		} catch (Exception e) {
			Dialogo.error("Borrar Presupuesto", "Error al borrar", "Se produjo un error al borrar el presupuesto");
			return;
		}
	}
	
	public void sistemaSeleccionado(HashMap<String, Object> variablesPaso) {
		Loadable p = (Loadable) variablesPaso.get("seleccionado");
		
		int perfilEconomico = 0;
		MetaFormatoProyecto mfp = null;
		
		try {
			ParametroProyecto pp = EstimacionesValoraciones.proyConsultado.getValorParametro(MetaParametro.PROYECTO_PERFIL_ECONOMICO);
			mfp = (MetaFormatoProyecto) pp.getValor();
			perfilEconomico = mfp.id;
			mfp = MetaFormatoProyecto.listado.get(perfilEconomico);
			if (mfp==null) throw new Exception();
			
		} catch (Exception e) {
			Dialogo.error("Alta de Sistema", "Falta perfil económico", "Es necesario administrar un perfil económico entre los parámetros del proyecto para continuar.");
			return;
		}
		
		if (EstimacionesValoraciones.presupuesto.costes==null) EstimacionesValoraciones.presupuesto.costes = new HashMap<Integer,Coste>();
		
		Coste c = new Coste();
		c.sistema = (Sistema) p;
		c.id=9999+EstimacionesValoraciones.presupuesto.costes.size();
		
		EstimacionesValoraciones.presupuesto.costes.put(c.id, c);
		
		for (int i = 0; i<mfp.conceptos.size();i++) {
			MetaConcepto mcp = (MetaConcepto) mfp.conceptos.values().toArray()[i];
			Concepto conc = new Concepto(mcp);
			conc.coste = c;
			c.conceptosCoste.put(mcp.codigo, conc);
		}
		
		MetaConcepto mc = new MetaConcepto();
		mc.codigo = "TOTAL";
		mc.descripcion = "Total";
		mc.id = MetaConcepto.ID_TOTAL;
		
		Concepto conc = new Concepto();
		conc.tipoConcepto = mc;
		conc.baseCalculo = new BaseCalculoConcepto(BaseCalculoConcepto.CALCULO_BASE_COSTE);
		conc.id = 32000;
		c.conceptosCoste.put(mc.codigo, conc);
		
		LineaCostePresupuesto lcp = new LineaCostePresupuesto();
		ArrayList<Object> lista = new ArrayList<Object>();
		lista.addAll(EstimacionesValoraciones.presupuesto.costes.values());
		
		ObservableList<Tableable> dataTable = tLineasCoste.getItems();
		dataTable = lcp.toListTableable(lista);
		tLineasCoste.setItems(dataTable);
		tLineasCoste.getProperties().put("EstimacionValoracion", this);
		
		try {
			lcp = new LineaCostePresupuesto((Coste) EstimacionesValoraciones.presupuesto.costes.values().toArray()[0]);
			lcp.fijaColumnas(tLineasCoste);	
		
			tLineasCoste.refresh();
		} catch (Exception e) {}
		
		popUp.hide();
	}
	
	public void actualizaResumen() {
		EstimacionesValoraciones.presupuesto.calculaTotales();
		tResumenCoste.refresh();
	}
	
	public void resumenCoste() {
		int perfilEconomico = 0;
		MetaFormatoProyecto mfp = null;
		
		try {
			ParametroProyecto pp = EstimacionesValoraciones.proyConsultado.getValorParametro(MetaParametro.PROYECTO_PERFIL_ECONOMICO);
			mfp = (MetaFormatoProyecto) pp.getValor();
			perfilEconomico = mfp.id;
			mfp = MetaFormatoProyecto.listado.get(perfilEconomico);
			if (mfp==null) throw new Exception();			
		} catch (Exception e) {
			Dialogo.error("Alta de Sistema", "Falta perfil económico", "Es necesario administrar un perfil económico entre los parámetros del proyecto para continuar.");
			return;
		}
		
		Coste c = new Coste();
		c.id=9999;
		c.sistema = null;
		
		for (int i = 0; i<mfp.conceptos.size();i++) {
			MetaConcepto mcp = (MetaConcepto) mfp.conceptos.values().toArray()[i];
			Concepto conc = new Concepto(mcp);
			conc.coste = c;
			c.conceptosCoste.put(mcp.codigo, conc);
		}
		
		if (EstimacionesValoraciones.presupuesto.costesTotal==null) EstimacionesValoraciones.presupuesto.costesTotal = new HashMap<Integer,Coste>();
		EstimacionesValoraciones.presupuesto.costesTotal.put(c.id, c);
		
		MetaConcepto mc = new MetaConcepto();
		mc.codigo = "TOTAL";
		mc.descripcion = "Total";
		mc.id = MetaConcepto.ID_TOTAL;
		
		Concepto conc = new Concepto();
		conc.tipoConcepto = mc;
		conc.baseCalculo = new BaseCalculoConcepto(BaseCalculoConcepto.CALCULO_BASE_COSTE);
		conc.id = 32000;
		c.conceptosCoste.put(mc.codigo, conc);
		
		LineaCostePresupuesto lcp = new LineaCostePresupuesto();
		ArrayList<Object> lista = new ArrayList<Object>();
		lista.addAll(EstimacionesValoraciones.presupuesto.costesTotal.values());
		
		ObservableList<Tableable> dataTable = tResumenCoste.getItems();
		if (dataTable==null) dataTable = lcp.toListTableable(lista);
		else dataTable.addAll(lcp.toListTableable(lista));
		tResumenCoste.setItems(dataTable);

		HashMap<String, ConfigTabla> configTabla = tResumenCoste.getItems().get(0).getConfigTabla();
		ArrayList<ConfigTabla> listaOrdenada = new ArrayList<ConfigTabla>();
		listaOrdenada.addAll(configTabla.values());
		Collections.sort(listaOrdenada);
		listaOrdenada.get(0).desplegable = false;	
		listaOrdenada.get(0).idColumna = "Resumen Coste";	
				
		try {
			lcp = new LineaCostePresupuesto((Coste) EstimacionesValoraciones.presupuesto.costesTotal.values().toArray()[0]);
			lcp.limpiarColumnas(tResumenCoste);
			lcp.fijaColumnas(tResumenCoste);
			
		
			tResumenCoste.refresh();
		} catch (Exception e) {}
		
		if (popUp!=null) popUp.hide();
	}
	
	private void nuevoPresupuesto() {
		
		cambiaDescripcion = false;
		cambiaTipo = false;
		
		imEditDesc.setMouseTransparent(false);		
		imEditDesc.getStyleClass().remove("iconoDisabled");
		imEditDesc.getStyleClass().add("iconoEnabled");
		
		imEditTipoPres.setMouseTransparent(false);		
		imEditTipoPres.getStyleClass().remove("iconoDisabled");
		imEditTipoPres.getStyleClass().add("iconoEnabled");
		
		imAniadirSistma.setMouseTransparent(false);		
		imAniadirSistma.getStyleClass().remove("iconoDisabled");
		imAniadirSistma.getStyleClass().add("iconoEnabled");
		
		imVersionarPres.setMouseTransparent(false);		
		imVersionarPres.getStyleClass().remove("iconoDisabled");
		imVersionarPres.getStyleClass().add("iconoEnabled");
		
		imGuardarPresupuesto.setMouseTransparent(false);		
		imGuardarPresupuesto.getStyleClass().remove("iconoDisabled");
		imGuardarPresupuesto.getStyleClass().add("iconoEnabled");

		imEliminarPres.setMouseTransparent(false);		
		imEliminarPres.getStyleClass().remove("iconoDisabled");
		imEliminarPres.getStyleClass().add("iconoEnabled");
		
		tpConsulta.setExpanded(false);
		tpResultados.setExpanded(true);
		
		try {
			EstimacionesValoraciones.presupuesto = new Presupuesto();
			EstimacionesValoraciones.presupuesto.actualiza = true;
			int id = presupuesto.maxIdPresupuesto();
			
			EstimacionesValoraciones.presupuesto.id = new Integer(id);
			EstimacionesValoraciones.presupuesto.p = cbProyectos.getValue();
			
			EstimacionesValoraciones.proyConsultado= cbProyectos.getValue();
			EstimacionesValoraciones.proyConsultado.cargaProyecto();
			
			tFxAlta.setText(FormateadorDatos.formateaDato(Constantes.fechaActual(), FormateadorDatos.FORMATO_FECHA));
			tId.setText(new Integer(id).toString());
			tVersion.setText("1");
			taDesc.setText("");
			
			this.cbTipoPrep.getItems().removeAll(this.cbTipoPrep.getItems());
			cbTipoPrep.getItems().addAll(TipoEnumerado.listado.get(TipoDato.FORMATO_TIPO_VCT).values());
			
			LineaCostePresupuesto lcp = new LineaCostePresupuesto();
			lcp.limpiarColumnas(tLineasCoste);
			tLineasCoste.setItems(FXCollections.observableArrayList());
			lcp.limpiarColumnas(tResumenCoste);	
			tResumenCoste.setItems(FXCollections.observableArrayList());
			
			resumenCoste();
						
		} catch (Exception e) {
			
		}
	}
	
	private void buscarPresupuesto() {
		cambiaDescripcion = false;
		cambiaTipo = false;
		
		imEditDesc.setMouseTransparent(false);		
		imEditDesc.getStyleClass().remove("iconoDisabled");
		imEditDesc.getStyleClass().add("iconoEnabled");
		
		imEditTipoPres.setMouseTransparent(false);		
		imEditTipoPres.getStyleClass().remove("iconoDisabled");
		imEditTipoPres.getStyleClass().add("iconoEnabled");
		
		imAniadirSistma.setMouseTransparent(false);		
		imAniadirSistma.getStyleClass().remove("iconoDisabled");
		imAniadirSistma.getStyleClass().add("iconoEnabled");
		
		imVersionarPres.setMouseTransparent(false);		
		imVersionarPres.getStyleClass().remove("iconoDisabled");
		imVersionarPres.getStyleClass().add("iconoEnabled");
		
		imGuardarPresupuesto.setMouseTransparent(false);		
		imGuardarPresupuesto.getStyleClass().remove("iconoDisabled");
		imGuardarPresupuesto.getStyleClass().add("iconoEnabled");

		imEliminarPres.setMouseTransparent(false);		
		imEliminarPres.getStyleClass().remove("iconoDisabled");
		imEliminarPres.getStyleClass().add("iconoEnabled");
		
		tpConsulta.setExpanded(false);
		tpResultados.setExpanded(true);
		
		try {
			EstimacionesValoraciones.presupuesto = this.cbVsPresupuesto.getValue();
			
			EstimacionesValoraciones.presupuesto.cargaCostes();
			EstimacionesValoraciones.presupuesto.p = cbProyectos.getValue();
			
			EstimacionesValoraciones.proyConsultado= cbProyectos.getValue();
			EstimacionesValoraciones.proyConsultado.cargaProyecto();
			
			tFxAlta.setText(FormateadorDatos.formateaDato(EstimacionesValoraciones.presupuesto.fxAlta, FormateadorDatos.FORMATO_FECHA));
			tId.setText(new Integer(EstimacionesValoraciones.presupuesto.id).toString());
			tVersion.setText(""+EstimacionesValoraciones.presupuesto.version);
			taDesc.setText(EstimacionesValoraciones.presupuesto.descripcion);
			
			this.cbTipoPrep.getItems().removeAll(this.cbTipoPrep.getItems());
			cbTipoPrep.getItems().addAll(TipoEnumerado.listado.get(TipoDato.FORMATO_TIPO_VCT).values());
			this.cbTipoPrep.setValue(EstimacionesValoraciones.presupuesto.tipo);
			
			Coste cst = null;
			Iterator<Coste> itCostes = EstimacionesValoraciones.presupuesto.costes.values().iterator();
			LineaCostePresupuesto lcp = new LineaCostePresupuesto();
			ArrayList<Object> lista = new ArrayList<Object>();
			
			int perfilEconomico = 0;
			MetaFormatoProyecto mfp = null;
			ParametroProyecto pp = EstimacionesValoraciones.proyConsultado.getValorParametro(MetaParametro.PROYECTO_PERFIL_ECONOMICO);
			mfp = (MetaFormatoProyecto) pp.getValor();
			perfilEconomico = mfp.id;
			mfp = MetaFormatoProyecto.listado.get(perfilEconomico);
			if (mfp==null) throw new Exception();
			
			
			while (itCostes.hasNext()) {
				cst = itCostes.next();
				
				for (int i = 0; i<mfp.conceptos.size();i++) {
					MetaConcepto mcp = (MetaConcepto) mfp.conceptos.values().toArray()[i];
					if (!cst.conceptosCoste.containsKey(mcp.codigo)){
						Concepto conc = new Concepto(mcp);
						conc.coste = cst;
						cst.conceptosCoste.put(mcp.codigo, conc);
					}
				}
			
				MetaConcepto mc = new MetaConcepto();
				mc.codigo = "TOTAL";
				mc.descripcion = "Total";
				mc.id = MetaConcepto.ID_TOTAL;
				
				Concepto conc = new Concepto();
				conc.tipoConcepto = mc;
				conc.baseCalculo = new BaseCalculoConcepto(BaseCalculoConcepto.CALCULO_BASE_COSTE);
				conc.id = 32000;
				cst.conceptosCoste.put(mc.codigo, conc);
				
				cst.calculaConceptos();				
			}
				
			lista.addAll(EstimacionesValoraciones.presupuesto.costes.values());
			ObservableList<Tableable> dataTable = tLineasCoste.getItems();
			dataTable = lcp.toListTableable(lista);
			tLineasCoste.setItems(dataTable);
			tLineasCoste.getProperties().put("EstimacionValoracion", this);
			
			try {
				lcp = new LineaCostePresupuesto((Coste) EstimacionesValoraciones.presupuesto.costes.values().toArray()[0]);
				lcp.limpiarColumnas(tLineasCoste);
				
				lcp.fijaColumnas(tLineasCoste);	
				lcp.limpiarColumnas(tResumenCoste);	
				tResumenCoste.setItems(FXCollections.observableArrayList());
			
				tLineasCoste.refresh();
			} catch (Exception e) {}
			
			if (popUp!=null) popUp.hide();
			
			resumenCoste();			

			this.actualizaResumen();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void editarDescripcion() {
		imEditDesc.setMouseTransparent(true);		
		imEditDesc.getStyleClass().remove("iconoEnabled");
		imEditDesc.getStyleClass().add("iconoDisabled");
		
	    taDesc.setEditable(true);
	    taDesc.setDisable(false);
	    
	    EstimacionesValoraciones.cambiaDescripcion = true;
	}
	
	private void editarTipo() {
		imEditTipoPres.setMouseTransparent(true);		
		imEditTipoPres.getStyleClass().remove("iconoEnabled");
		imEditTipoPres.getStyleClass().add("iconoDisabled");
		
	    cbTipoPrep.setDisable(false);
	    
	    EstimacionesValoraciones.cambiaTipo = true;
	}
	
	private void guardarNuevaVsPresupuesto(boolean actualiza) {
		try {
			EstimacionesValoraciones.presupuesto.descripcion = taDesc.getText();
			EstimacionesValoraciones.presupuesto.tipo = cbTipoPrep.getValue();
			
			if (EstimacionesValoraciones.presupuesto.tipo==null) {
				Dialogo.error("Guardar Presupuesto", "Error al guardar", "Debe seleccionar un tipo.");
				return;
			}
			
			if (EstimacionesValoraciones.presupuesto.actualiza) actualiza = false;
			
			EstimacionesValoraciones.presupuesto.guardarPresupuesto(actualiza,null);
			
			EstimacionesValoraciones.presupuesto.actualiza = false;
			
			tVersion.setText(new Integer(EstimacionesValoraciones.presupuesto.version).toString());
			tId.setText(new Integer(EstimacionesValoraciones.presupuesto.id).toString());			

			Dialogo.alert("Guardar Presupuesto", "Presupuesto guardado", "El presupuesto ha sido guardado correctamente");
			
			buscaPresupuestos(EstimacionesValoraciones.proyConsultado);			
		} catch (Exception e) {
			Dialogo.error("Guardar Presupuesto", "Error al guardar", "No se pudo guardar el presupuesto.");
			return;
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
