package ui.Economico.ControlPresupuestario.EdicionEstImp;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import controller.AnalizadorPresupuesto;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.beans.Concepto;
import model.beans.Coste;
import model.beans.Estimacion;
import model.beans.FraccionImputacion;
import model.beans.Imputacion;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.metadatos.Sistema;
import ui.ControladorPantalla;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Tableable;
import ui.Economico.ControlPresupuestario.ControlPresupuestario;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteEvolucion;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteUsuario;

public class FraccionarImputacion implements ControladorPantalla {
	
	public static FraccionarImputacion elementoThis = null;
	public ArrayList<FraccionUnitariaImputacion> fui = null;
	public Imputacion imputRepresentada = null;
	
	public static String A_PORCENTAJE = "A porcentaje";
	public static String A_HORAS = "Por Horas";
	public static String A_IMPORTE = "Por Importe";
	
	public HashMap<String, Object> variablesPaso = null;
	
	@FXML
    private Accordion acSimulacion;

    @FXML
    private VBox hbFracciones;

    @FXML
    private ImageView imAnalizar;
    private GestionBotones gbAnalizar;

    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;

    @FXML
    private TitledPane tDatos;

    @FXML
    private TableView<Tableable> tResumen;

    @FXML
    private TableView<Tableable> tResumenD;

    @FXML
    private TitledPane tSimulacion;
    
    @FXML
    public ComboBox<String> tFraccion;

    @FXML
    public TextField tHoras;

    @FXML
    public TextField tImporte;

    @FXML
    private TextField tSistema;  
    
    
    public FraccionarImputacion() {
		super();
		FraccionarImputacion.elementoThis = this;
	}

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/EdicionEstImp/FraccionarImputacion.fxml"; 
	
	@Override
	public AnchorPane getAnchor() {
		return null;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		gbAnalizar = new GestionBotones(imAnalizar, "Analizar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
			{ try { 
            	if (validaValores()) {
            		analizaCambios();
            	}
            } catch (Exception e) {
            	Dialogo.error("Guardado de Fracción", "Error al guardar", "Se produjo un error al guardar el elemento");
            	e.printStackTrace();	
            } }}, "Analizar Slot de destino", this);
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            { try { 
            	if (validaValores()) {
            		ControlPresupuestario.salvaPosicionActual();
            		guardarCambios();
                	ParamTable.po.hide();
            		Dialogo.alert("Fracción de Imputación", "Fraccionamiento correcto", "El fraccionamiento de la imputación se realizó correctamente.");
            		ControlPresupuestario.cargaPosicionActual();
            	}
            } catch (Exception e) {
            	Dialogo.error("Guardado de Fracción", "Error al guardar", "Se produjo un error al guardar el elemento");
            	e.printStackTrace();}} 
			}, "Guardar");
		this.tFraccion.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			if (this.fui==null) return; 
			Iterator<FraccionUnitariaImputacion> itFUI = this.fui.iterator();
			
			while (itFUI.hasNext()) {
				FraccionUnitariaImputacion fui = itFUI.next();
				fui.limpiarConcepto(this.tFraccion.getValue());
			}
		}
	    );
	}
	
	public void guardarCambios() throws Exception {
		ArrayList<FraccionImputacion> listaSalida = construyeFracciones();
		
		Iterator<FraccionImputacion> itFraccion = listaSalida.iterator();
		
		int contador = 0;
		boolean guardarFracciones = false;
		
		while (itFraccion.hasNext()) {
			FraccionImputacion fi = itFraccion.next();
			
			if (FraccionarImputacion.A_PORCENTAJE.equals(this.tFraccion.getValue()) && (fi.porc>0) ) contador ++;
			if (FraccionarImputacion.A_HORAS.equals(this.tFraccion.getValue()) && (fi.horas>0) ) contador ++;
			if (FraccionarImputacion.A_IMPORTE.equals(this.tFraccion.getValue()) && (fi.importe>0) ) contador ++;
						
			if (contador>1) {
				guardarFracciones = true;
				break;
			}
		}
		
		if (!guardarFracciones) listaSalida = new ArrayList<FraccionImputacion>();
		
		imputRepresentada.fraccionaImputacion(listaSalida, this.imputRepresentada);
	}
	
	private void analizaCambios() throws Exception{
		ArrayList<Estimacion> lEstimacion = new ArrayList<Estimacion>();
		ArrayList<Imputacion> lImputacion = new ArrayList<Imputacion>();
		ArrayList<FraccionImputacion> lFraccionImputacion = construyeFracciones();
		AnalizadorPresupuesto ap = new AnalizadorPresupuesto();
		ap.construyePresupuestoMensualizado(ControlPresupuestario.ap.presupuesto, ControlPresupuestario.ap.fechaPivote, lEstimacion, lImputacion,lFraccionImputacion, null);
		
		acSimulacion.getPanes().get(0).setExpanded(false);
		acSimulacion.getPanes().get(1).setExpanded(true);
		
		Iterator<FraccionUnitariaImputacion> itFUI = this.fui.iterator();
		
		tSimulacion.setDisable(false);
		ArrayList<Object> lista = new ArrayList<Object>();
		ArrayList<Object> listaModificada = new ArrayList<Object>();
		
		while (itFUI.hasNext()) {
			FraccionUnitariaImputacion fui = itFUI.next();
			Sistema s = fui.cbSistema.getValue();
			s  = calculaIndicadores(ControlPresupuestario.ap,s);	
			lista.add(new LineaCosteEvolucion(s));
			
			Sistema s2 = s.clone();
			
			s2 = calculaIndicadores(ap,s2);
			listaModificada.add(new LineaCosteEvolucion(s2));
		}
				
		ObservableList<Tableable> dataTable = (new LineaCosteEvolucion()).toListTableable(lista);
		tResumen.setItems(dataTable);
		(new LineaCosteEvolucion()).fijaColumnas(tResumen);

		tResumen.setPrefHeight(40+40*lista.size());
		tResumen.setPrefWidth(600);

		dataTable = (new LineaCosteEvolucion()).toListTableable(listaModificada);
		tResumenD.setItems(dataTable);
		(new LineaCosteEvolucion()).fijaColumnas(tResumenD);

		tResumenD.setPrefHeight(40+40*lista.size());
		tResumenD.setPrefWidth(600);
		
	}
	
	private Sistema calculaIndicadores(AnalizadorPresupuesto ap, Sistema s) {
		
		s.listaConceptos = new HashMap<String,Concepto>();
		
		Calendar c = Calendar.getInstance();
		c.setTime(this.imputRepresentada.fxInicio);		
		int anio = c.get(Calendar.YEAR);
		
		HashMap<String, Concepto> lista = ap.acumuladoPorSistemaConcepto(s, anio);
		
		Concepto cAux = lista.get(this.imputRepresentada.natCoste.codigo);
		cAux.valor += cAux.listaImputaciones.get(0).getImporte();
		s.listaConceptos.put(LineaCosteEvolucion.AC, cAux);
		
		Iterator<Concepto> it = lista.values().iterator();
		Concepto cAux2 = new Concepto();
		while (it.hasNext()) {
			Concepto  cAux3= it.next();
			cAux2.valor += cAux3.listaImputaciones.get(0).getImporte();
		}
		s.listaConceptos.put(LineaCosteEvolucion.AT, cAux2);
		
		lista = ap.acumuladoPorSistemaConcepto(s, -1);
		
		cAux = lista.get(this.imputRepresentada.natCoste.codigo);
		cAux.valor += cAux.listaImputaciones.get(0).getImporte();
		s.listaConceptos.put(LineaCosteEvolucion.TC, cAux);
		
		it = lista.values().iterator();
		cAux2 = new Concepto();
		while (it.hasNext()) {
			Concepto  cAux3= it.next();
			cAux2.valor += cAux3.listaImputaciones.get(0).getImporte();
		}
		s.listaConceptos.put(LineaCosteEvolucion.TT, cAux2);
		
		return s;
	}
	
	public ArrayList<FraccionImputacion> construyeFracciones () throws Exception {
		ArrayList<FraccionImputacion> salida = new ArrayList<FraccionImputacion>();
		
		if (this.fui==null) return salida; 
		
		Iterator<FraccionUnitariaImputacion> itFUI = this.fui.iterator();
		
		while (itFUI.hasNext()) {
			FraccionUnitariaImputacion fui = itFUI.next();
			
			FraccionImputacion fI = new FraccionImputacion();
			
			if (FraccionarImputacion.A_PORCENTAJE.equals(this.tFraccion.getValue())) {
				fI.tipo = FraccionImputacion.POR_PORCENTAJE;
				fI.porc = (Float) FormateadorDatos.parseaDato(fui.tFraccion.getText(), FormateadorDatos.FORMATO_PORC);
			}
			if (FraccionarImputacion.A_HORAS.equals(this.tFraccion.getValue())) {
				fI.tipo = FraccionImputacion.POR_HORAS;
				fI.horas = (Float) FormateadorDatos.parseaDato(fui.tFraccion.getText(), FormateadorDatos.FORMATO_REAL);
			}
			if (FraccionarImputacion.A_IMPORTE.equals(this.tFraccion.getValue())) {
				fI.tipo = FraccionImputacion.POR_IMPORTE;
				fI.importe = (Float) FormateadorDatos.parseaDato(fui.tFraccion.getText(), FormateadorDatos.FORMATO_MONEDA);
			}
			
			fI.idPadre = this.imputRepresentada.id;
			fI.id = -1;
			fI.sistema = fui.cbSistema.getValue();
			
			salida.add(fI);
		}
		
		return salida;
	}
	
	public boolean validaValores() throws Exception{
		if (FraccionarImputacion.A_PORCENTAJE.equals(this.tFraccion.getValue())) {
			float porcTotal = 0;
			if (this.fui==null) return false; 
			Iterator<FraccionUnitariaImputacion> itFUI = this.fui.iterator();
			
			while (itFUI.hasNext()) {
				FraccionUnitariaImputacion fui = itFUI.next();
				porcTotal += (Float) FormateadorDatos.parseaDato(fui.tFraccion.getText(), FormateadorDatos.FORMATO_PORC);
			}
			
			if (porcTotal!=100) {
				Dialogo.error("Fracción de Imputación", "No se puede fraccionar la imputación", "La suma de porcentajes debe ser el 100%");
				return false;
			} else {
				return true;
			}
		}
		if (FraccionarImputacion.A_HORAS.equals(this.tFraccion.getValue())) {
			float horasTotal = 0;
			if (this.fui==null) return false; 
			Iterator<FraccionUnitariaImputacion> itFUI = this.fui.iterator();
			
			while (itFUI.hasNext()) {
				FraccionUnitariaImputacion fui = itFUI.next();
				horasTotal += (Float) FormateadorDatos.parseaDato(fui.tFraccion.getText(), FormateadorDatos.FORMATO_PORC);
			}
			
			if (horasTotal!=imputRepresentada.horas) {
				Dialogo.error("Fracción de Imputación", "No se puede fraccionar la imputación", "La suma de horas de las fracciones debe ser "+imputRepresentada.horas);
				return false;
			} else {
				return true;
			}
		}
		if (FraccionarImputacion.A_IMPORTE.equals(this.tFraccion.getValue())) {
			float imporTotal = 0;
			if (this.fui==null) return false; 
			Iterator<FraccionUnitariaImputacion> itFUI = this.fui.iterator();
			
			while (itFUI.hasNext()) {
				FraccionUnitariaImputacion fui = itFUI.next();
				imporTotal += (Float) FormateadorDatos.parseaDato(fui.tFraccion.getText(), FormateadorDatos.FORMATO_PORC);
			}
			
			if (imporTotal!=imputRepresentada.importe) {
				Dialogo.error("Fracción de Imputación", "No se puede fraccionar la imputación", "La suma de importes de las fracciones debe ser "+imputRepresentada.importe);
				return false;
			} else {
				return true;
			}
		}
		
		return false;
	}
	
	public void prefijaValores() {
		Iterator<Coste> itCostes = ControlPresupuestario.ap.presupuesto.costes.values().iterator();
		
		LineaCosteUsuario lcu = (LineaCosteUsuario) this.variablesPaso.get("filaDatos");
		Imputacion iSel = lcu.concepto.listaImputaciones.get(0);
		imputRepresentada = iSel;
		
		try {
			tFraccion.getItems().removeAll(tFraccion.getItems());
			
			iSel = ControlPresupuestario.ap.getImputacion(iSel.id);
			
			this.tFraccion.getItems().add(FraccionarImputacion.A_PORCENTAJE);
			this.tFraccion.getItems().add(FraccionarImputacion.A_HORAS);
			this.tFraccion.getItems().add(FraccionarImputacion.A_IMPORTE);
			
			if (iSel.tipoImputacion != Imputacion.IMPUTACION_NO_FRACCIONADA) {
				FraccionImputacion fi = null;
				if (iSel.listaFracciones!=null) fi = iSel.listaFracciones.get(0);
				else  fi = iSel.imputacionPadre.listaFracciones.get(0);
					
				this.tFraccion.setValue(this.tFraccion.getItems().get(fi.tipo));
			}
			
			if (iSel.tipoImputacion == Imputacion.IMPUTACION_NO_FRACCIONADA) {
				tSistema.setText(iSel.sistema.descripcion);
				this.tHoras.setText(FormateadorDatos.formateaDato(iSel.getHoras(), FormateadorDatos.FORMATO_REAL));
				this.tImporte.setText(FormateadorDatos.formateaDato(iSel.getImporte(), FormateadorDatos.FORMATO_MONEDA));
			}
			if (iSel.tipoImputacion == Imputacion.IMPUTACION_FRACCIONADA || iSel.tipoImputacion == Imputacion.FRACCION_IMPUTACION) {
				if (iSel.listaFracciones!=null && iSel.listaFracciones.size()>0) {
					tSistema.setText(iSel.sistema.descripcion);
					this.tHoras.setText(FormateadorDatos.formateaDato(iSel.getHoras(), FormateadorDatos.FORMATO_REAL));
					this.tImporte.setText(FormateadorDatos.formateaDato(iSel.getImporte(), FormateadorDatos.FORMATO_MONEDA));
				}
				else {
					imputRepresentada = iSel.imputacionPadre;
					tSistema.setText(iSel.imputacionPadre.sistema.descripcion);
					this.tHoras.setText(FormateadorDatos.formateaDato(iSel.imputacionPadre.horas, FormateadorDatos.FORMATO_REAL));
					this.tImporte.setText(FormateadorDatos.formateaDato(iSel.imputacionPadre.importe, FormateadorDatos.FORMATO_MONEDA));
				}
			}
						
			this.tFraccion.getItems().get(iSel.modo);
			
			hbFracciones.getChildren().removeAll(hbFracciones.getChildren());
		} catch (Exception e ) {
			
		}
		
		this.fui = new ArrayList<FraccionUnitariaImputacion> ();
		
		while (itCostes.hasNext()) {
			Coste c = itCostes.next();
			
			try {
				
				FraccionUnitariaImputacion fraImputacion = new FraccionUnitariaImputacion();
		        FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(fraImputacion.getFXML()));
		        hbFracciones.getChildren().add(loader.load());
		        fraImputacion = loader.getController();
		        fraImputacion.variablesPaso = this.variablesPaso;
		        fraImputacion.prefijaValores(iSel, c.sistema);
		        fraImputacion.fracImpu = this;
		        fui.add(fraImputacion);
		        
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
				
}
