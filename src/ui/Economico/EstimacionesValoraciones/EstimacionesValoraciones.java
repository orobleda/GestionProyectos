package ui.Economico.EstimacionesValoraciones;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import application.Main;
import controller.Log;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
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
import model.metadatos.TipoProyecto;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Tabla;
import ui.VentanaContextual;
import ui.Economico.EstimacionesValoraciones.Tables.LineaCostePresupuesto;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;
import ui.popUps.ConsultaAvanzadaProyectos;
import ui.popUps.SeleccionElemento;

public class EstimacionesValoraciones implements ControladorPantalla {
	

	public static final String fxml = "file:src/ui/Economico/EstimacionesValoraciones/EstimacionesValoraciones.fxml";
	
	VentanaContextual popUp = null;
	
	@FXML
	private AnchorPane anchor;
	
	@FXML
	private TextField tProyecto = null;	
	private Proyecto proySeleccionado;
    @FXML
    private ImageView imConsultaAvanzada;
    private GestionBotones gbConsultaAvanzada;	
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
	private ImageView imAniadirSistma = null;
    private GestionBotones gbAniadirSistma;
	@FXML
	private ImageView imVersionarPres = null;
    private GestionBotones gbVersionarPres;
	@FXML
	private ImageView imGuardarPresupuesto = null;
    private GestionBotones gbGuardarPresupuesto;
	@FXML
	private ImageView imEliminarPres = null;
    private GestionBotones gbEliminarPres;
	@FXML
    private GestionBotones gbAniadirPresupuesto;

	@FXML
	private TableView<Tableable> tLineasCoste = null;
	public Tabla tablaLineasCoste = null;
	@FXML
	private TableView<Tableable> tResumenCoste = null;
	public Tabla tablaResumenCoste = null;
	
    @FXML
    private VBox vbResultados;
	
	public static Proyecto proyConsultado = null;
	public static Presupuesto presupuesto = null;
	
	@Override
	public void resize(Scene escena) {
		int res = Main.resolucion();
		
		if (res == Main.ALTA_RESOLUCION ) {
			tProyecto.setPrefWidth(escena.getWidth()*0.65);
			cbVsPresupuesto.setPrefWidth(escena.getWidth()*0.65);
			tLineasCoste.setPrefWidth(escena.getWidth()*0.65);
			tResumenCoste.setPrefWidth(escena.getWidth()*0.65);
			taDesc.setPrefWidth(escena.getWidth()*0.65);
			
			tLineasCoste.setPrefHeight(escena.getHeight()*0.30);
			tResumenCoste.setPrefHeight(escena.getHeight()*0.1);
			taDesc.setPrefHeight(escena.getWidth()*0.025);
		} else {
			tProyecto.setPrefWidth(escena.getWidth()*0.65);
			cbVsPresupuesto.setPrefWidth(escena.getWidth()*0.65);
			tLineasCoste.setPrefWidth(escena.getWidth()*0.65);
			tResumenCoste.setPrefWidth(escena.getWidth()*0.65);
			taDesc.setPrefWidth(escena.getWidth()*0.65);
			
			tLineasCoste.setPrefHeight(escena.getHeight()*0.28);
			tResumenCoste.setPrefHeight(escena.getHeight()*0.25);
			taDesc.setPrefHeight(escena.getWidth()*0.025);
		}
	}
	
	private void consultaAvanzadaProyectos() throws Exception{		
		ConsultaAvanzadaProyectos.getInstance(this, 1, TipoProyecto.ID_TODO, this.tProyecto);
	}
	
	public void fijaProyecto(ArrayList<Proyecto> listaProyecto) {
		if (listaProyecto!=null && listaProyecto.size()==1) {
			this.proySeleccionado = listaProyecto.get(0);
			this.tProyecto.setText(this.proySeleccionado.nombre);
			ParamTable.po.hide();
			buscaPresupuestos (this.proySeleccionado);

			vbResultados.setVisible(false);
		}
	}
		
	public void fotoInicial(){
		this.tProyecto.setText("");
		this.proySeleccionado = null;
		cbVsPresupuesto.getItems().removeAll(cbVsPresupuesto.getItems());
		
		gbGuardarPresupuesto.desActivarBoton();
		gbEliminarPres.desActivarBoton();
		gbVersionarPres.desActivarBoton();
		gbAniadirSistma.desActivarBoton();
		gbAniadirPresupuesto.desActivarBoton();		
		
		tFxAlta.setText("");
		tId.setText("");
		tVersion.setText("");
		taDesc.setText("");
		cbTipoPrep.getItems().removeAll(cbTipoPrep.getItems());
		
		
	}
	
	public void initialize(){
		vbResultados.setVisible(false);
		tProyecto.setDisable(true);
		
		tablaLineasCoste = new Tabla(tLineasCoste, new LineaCostePresupuesto(),this);
		tablaLineasCoste.altoLibre = true;
		
		tablaResumenCoste = new Tabla(tResumenCoste, new LineaCostePresupuesto(),this);
		
		gbConsultaAvanzada = new GestionBotones(imConsultaAvanzada, "Buscar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					consultaAvanzadaProyectos();
				} catch (Exception e) {
					Log.e(e);
				}
            } }, "Consulta elementos");
		gbConsultaAvanzada.activarBoton();
		gbAniadirSistma = new GestionBotones(imAniadirSistma, "NuevaFila3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					eligeSistema();
				} catch (Exception e) {
					Log.e(e);
				}
            } }, "Añadir Sistema");
		gbAniadirPresupuesto = new GestionBotones(GestionBotones.DER, new ImageView(), "nuevoBombilla", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					nuevoPresupuesto();
				} catch (Exception e) {
					Log.e(e);
				}
            } }, "Nuevo Presupuesto");
		gbVersionarPres = new GestionBotones(imVersionarPres, "GuardarAniadir3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					guardarNuevaVsPresupuesto(false);
				} catch (Exception e) {
					Log.e(e);
				}
            } }, "Versionar presupuesto");
		
		gbGuardarPresupuesto = new GestionBotones(imGuardarPresupuesto, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					guardarNuevaVsPresupuesto(true);
				} catch (Exception e) {
					Log.e(e);
				}
            } }, "Actualizar presupuesto");
		
		gbEliminarPres = new GestionBotones(imEliminarPres, "Eliminar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					confirmaBorrado();
				} catch (Exception e) {
					Log.e(e);
				}
            } }, "Borrar presupuesto");
		
		fotoInicial();
				
		cbVsPresupuesto.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {	
			try {	
				buscarPresupuesto();
			} catch (Exception e) {
				Log.e(e);
			} 	
		}  );
		
	}
	
	private void buscaPresupuestos(Proyecto p) {
		EstimacionesValoraciones.presupuesto = null;
		EstimacionesValoraciones.proyConsultado = p;
		cbVsPresupuesto.getItems().removeAll(cbVsPresupuesto.getItems());
		
		Presupuesto pres = new Presupuesto();		
		ArrayList<Presupuesto> listado = pres.buscaPresupuestos(p);
		
		cbVsPresupuesto.getItems().addAll(listado);

		gbAniadirPresupuesto.activarBoton();		
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
	     	popUp = new VentanaContextual((AnchorPane) loader.load());
	     	popUp.show(imAniadirSistma);
	     	popUp.setAnimated(true);
		} catch (Exception e) {
			
		}
	}
	
	public void confirmaBorrado(){
		Dialogo.confirm("¿Desea borrar la estimación?", "Se eliminará la estimación y todos sus conceptos asociados", 
				new Dialogo.Manejador<ButtonType>() {			
			@Override
			public void maneja(ButtonType buttonType) {
				borrarPresupuesto(buttonType);				
			}
		});
	}
	
	public void borrarPresupuesto(ButtonType bt){
		try {	
			if (bt.equals(ButtonType.YES)){
				EstimacionesValoraciones.presupuesto.borrarPresupuesto(null);
				
				fotoInicial();
				vbResultados.setVisible(false);
			}
			
		} catch (Exception e) {
			Log.e(e);
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
			Dialogo.alert("Falta perfil económico", "", "Es necesario administrar un perfil económico entre los parámetros del proyecto para continuar.");
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
		
		ArrayList<Object> lista = new ArrayList<Object>();
		lista.addAll(EstimacionesValoraciones.presupuesto.costes.values());
		
		try {
			HashMap<String,Object> pasoPrimitiva = new HashMap<String,Object>();
			pasoPrimitiva.put("COSTE", (Coste) EstimacionesValoraciones.presupuesto.costes.values().toArray()[0]);
			this.tablaLineasCoste.setPasoPrimitiva(pasoPrimitiva);

			this.tablaLineasCoste.pintaTabla(lista);
			
		} catch (Exception e) {}
		
		popUp.hide();
	}
	
	public void actualizaResumen() {
		EstimacionesValoraciones.presupuesto.calculaTotales();	
		HashMap<String,Object> variablesPaso = new HashMap<String,Object>();
		variablesPaso.put(LineaCostePresupuesto.COSTE, EstimacionesValoraciones.presupuesto.costesTotal.get(Sistema.getInstanceTotal().id));
		variablesPaso.put("RESUMEN", Constantes.TRUE);
		tablaResumenCoste.setPasoPrimitiva(variablesPaso);
		tablaResumenCoste.pintaTabla(TipoDato.toListaObjetos(EstimacionesValoraciones.presupuesto.costesTotal.values()));
		tablaLineasCoste.refrescaTabla();
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
			Dialogo.alert("Falta perfil económico", "", "Es necesario administrar un perfil económico entre los parámetros del proyecto para continuar.");
			return;
		}
		
		Coste c = new Coste();
		c.id=Sistema.getInstanceTotal().id;
		c.sistema = Sistema.getInstanceTotal();
		
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
		
		ArrayList<Object> lista = new ArrayList<Object>();
		lista.addAll(EstimacionesValoraciones.presupuesto.costesTotal.values());
		
		HashMap<String,Object> pasoPrimitiva = new HashMap<String,Object>();
		pasoPrimitiva.put("COSTE", (Coste) EstimacionesValoraciones.presupuesto.costes.values().toArray()[0]);
		pasoPrimitiva.put("RESUMEN", Constantes.TRUE);
		this.tablaResumenCoste.setPasoPrimitiva(pasoPrimitiva);

		this.tablaResumenCoste.pintaTabla(lista);
		
		if (popUp!=null) popUp.hide();
	}
	
	private void nuevoPresupuesto() throws Exception{
		
		gbAniadirSistma.activarBoton();
		gbVersionarPres.activarBoton();
		gbGuardarPresupuesto.activarBoton();		
		gbEliminarPres.activarBoton();
		vbResultados.setVisible(true);

		EstimacionesValoraciones.presupuesto = new Presupuesto();
		EstimacionesValoraciones.presupuesto.actualiza = true;
		int id = presupuesto.maxIdPresupuesto();
		
		EstimacionesValoraciones.presupuesto.id = new Integer(id);
		EstimacionesValoraciones.presupuesto.p = this.proySeleccionado;
		
		EstimacionesValoraciones.proyConsultado= this.proySeleccionado;
		EstimacionesValoraciones.proyConsultado.cargaProyecto();
		
		tFxAlta.setText(FormateadorDatos.formateaDato(Constantes.fechaActual(), FormateadorDatos.FORMATO_FECHA));
		tId.setText(new Integer(id).toString());
		tVersion.setText("1");
		taDesc.setText("");
		
		this.cbTipoPrep.getItems().removeAll(this.cbTipoPrep.getItems());
		cbTipoPrep.getItems().addAll(TipoEnumerado.listado.get(TipoDato.FORMATO_TIPO_VCT).values());
		
		HashMap<String,Object> pasoPrimitiva = new HashMap<String,Object>();
		pasoPrimitiva.put("COSTE", (Coste) EstimacionesValoraciones.presupuesto.costes.values().toArray()[0]);
		this.tablaLineasCoste.setPasoPrimitiva(pasoPrimitiva);

		this.tablaLineasCoste.pintaTabla(new ArrayList<Object>());
		
		resumenCoste();

	}
	
	private void buscarPresupuesto() throws Exception{
		gbAniadirSistma.activarBoton();
		gbVersionarPres.activarBoton();
		gbGuardarPresupuesto.activarBoton();		
		gbEliminarPres.activarBoton();	

		vbResultados.setVisible(true);

		EstimacionesValoraciones.presupuesto = this.cbVsPresupuesto.getValue();
		
		EstimacionesValoraciones.presupuesto.cargaCostes();
		EstimacionesValoraciones.presupuesto.p = this.proySeleccionado;
		
		EstimacionesValoraciones.proyConsultado= this.proySeleccionado;
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
		
		try {
			HashMap<String,Object> pasoPrimitiva = new HashMap<String,Object>();
			pasoPrimitiva.put("COSTE", (Coste) EstimacionesValoraciones.presupuesto.costes.values().toArray()[0]);
			this.tablaLineasCoste.setPasoPrimitiva(pasoPrimitiva);

			this.tablaLineasCoste.pintaTabla(lista);
		} catch (Exception e) {}
		
		if (popUp!=null) popUp.hide();
		
		resumenCoste();			

		this.actualizaResumen();

	}
	
	private void guardarNuevaVsPresupuesto(boolean actualiza) throws Exception {

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
