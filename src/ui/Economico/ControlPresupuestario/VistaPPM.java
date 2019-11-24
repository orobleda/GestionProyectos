package ui.Economico.ControlPresupuestario;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import controller.AnalizadorPresupuesto;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import model.beans.Concepto;
import model.beans.EstimacionAnio;
import model.beans.EstimacionMes;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import ui.Economico.ControlPresupuestario.Tables.Cabeceras;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteEstReal;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class VistaPPM implements ControladorPantalla {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/VistaPPM.fxml"; 
	
	public static final int VISTA_ANUAL = 0;
	public static final int VISTA_TRIMESTRAL = 1;
	public static final int VISTA_MENSUAL = 2;
	
	public AnalizadorPresupuesto ap = null;
	
	@FXML
	private AnchorPane anchor;
	
    @FXML
    private HBox hbBloques;

	    @FXML
	    private Label lMeses;

	    @FXML
	    private TableView<Tableable> tTitConcHoras;

	    @FXML
	    private TableView<Tableable> tTitConcCert;

	    @FXML
	    private TableView<Tableable> tTitTOTAL;

	    @FXML
	    private Label lMeses1;

	    @FXML
	    private TableView<Tableable> tTitConcHoras1;

	    @FXML
	    private TableView<Tableable> tTitConcCert1;

	    @FXML
	    private TableView<Tableable> tTitTOTAL1;
		
    @FXML
    private ComboBox<String> cbGranularidad;
    
    @FXML
    private ComboBox<Sistema> cbSistema;
    
    @FXML
    private ScrollPane scrIterado;
    
    @FXML
    private BorderPane bpVPPM;
	
	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}
	
	@Override
	public void resize(Scene escena) {
		if (bpVPPM!=null){
			bpVPPM.setMaxWidth(bpVPPM.getScene().getWidth());
			bpVPPM.setPrefHeight(bpVPPM.getScene().getHeight());
			bpVPPM.setMaxHeight(bpVPPM.getScene().getHeight()*0.8);
			((ScrollPane) bpVPPM.getCenter()).setPrefWidth(bpVPPM.getScene().getWidth()*0.20);
			((ScrollPane) bpVPPM.getCenter()).setPrefHeight((bpVPPM.getScene().getHeight()*0.70));
		}
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		
		cbGranularidad.getItems().add("Anual");
		cbGranularidad.getItems().add("Mensual");
		
		cbGranularidad.setValue("Anual");
		
		cbGranularidad.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			Sistema sSel = null;
			
			if (cbSistema.getValue()!=null || cbSistema.getValue().id!=Sistema.ID_TODOS) {
				sSel = cbSistema.getValue();
			}
			
			if (newValue.equals("Mensual")){
				pintaDesgloseCostes(null, VistaPPM.VISTA_MENSUAL, sSel);
			}
			if (newValue.equals("Anual")){
				pintaDesgloseCostes(null, VistaPPM.VISTA_ANUAL, sSel);
			}	
		}
	    ); 
		
		cbSistema.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			Sistema sSel = newValue;
			
			if (cbSistema.getValue()!=null || cbSistema.getValue().id!=Sistema.ID_TODOS) {
				sSel = cbSistema.getValue();
			} else {
				sSel = null;
			}
			
			String desglose = cbGranularidad.getValue();			
			
			if (desglose.equals("Mensual")){
				pintaDesgloseCostes(null, VistaPPM.VISTA_MENSUAL, sSel);
			}
			if (desglose.equals("Anual")){
				pintaDesgloseCostes(null, VistaPPM.VISTA_ANUAL, sSel);
			}	
		}
	    ); 
		
		ArrayList<Object> lista = new ArrayList<Object>();
		ArrayList<Object> listaCertif = new ArrayList<Object>();
		ArrayList<Object> listaTotal = new ArrayList<Object>();
		
		Iterator<MetaConcepto> itMC = MetaConcepto.listado.values().iterator();
		
		while (itMC.hasNext()) {
			MetaConcepto mc = itMC.next();
			
			if (mc.tipoGestionEconomica == MetaConcepto.GESTION_HORAS)
				lista.add(mc.codigo);
			if (mc.tipoGestionEconomica == MetaConcepto.GESTION_CERTIFICACION)
				listaCertif.add(mc.codigo);
		}
		
		lista.add(MetaConcepto.getTotal().codigo);
		listaCertif.add(MetaConcepto.getTotal().codigo);
		listaTotal.add(MetaConcepto.getTotal().codigo);
		
		ObservableList<Tableable> dataTable = (new Cabeceras()).toListTableable(lista);
		tTitConcHoras.setItems(dataTable);
		(new Cabeceras()).fijaColumnas(tTitConcHoras);
		
		tTitConcHoras.setPrefSize(9*10, lista.size()==1?40+40*lista.size():40*lista.size());
		
		dataTable =  (new Cabeceras()).toListTableable(listaCertif);
		tTitConcCert.setItems(dataTable);
		(new Cabeceras()).fijaColumnas(tTitConcCert);
		
		tTitConcCert.setPrefSize(9*10, listaCertif.size()==1?40+40*listaCertif.size():40*listaCertif.size());
		
		dataTable = (new Cabeceras()).toListTableable(listaTotal);
		tTitTOTAL.setItems(dataTable);
		(new Cabeceras()).fijaColumnas(tTitTOTAL);
		
		tTitTOTAL.setPrefSize(9*10, listaTotal.size()==1?40+40*listaTotal.size():40*listaTotal.size());
					
	}
	
	public void pintaDesgloseCostes(AnalizadorPresupuesto ap, int flagDesglose, Sistema sSel) {
		try {
			if (ap==null) {
				ap = this.ap;
			} else {
				this.ap = ap;
			}
			
			if (VistaPPM.VISTA_ANUAL == flagDesglose) {
				pintaDesgloseCostesAnual(ap, sSel);
				lMeses.setText("Año");
			}
			if (VistaPPM.VISTA_MENSUAL == flagDesglose) {
				pintaDesgloseCostesMensual(ap, sSel);
				lMeses.setText("Mes");
			}
			
			resize(null);
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void pintaDesgloseCostesAnual(AnalizadorPresupuesto ap, Sistema sSel) throws Exception {
				
		if (sSel==null && ap.estimacionAnual.size()>0) {
			EstimacionAnio ea = ap.estimacionAnual.get(0);
			
			if (ea.estimacionesMensuales.size()>0) {
				EstimacionMes em = (EstimacionMes) ea.estimacionesMensuales.values().toArray()[0];
				
				Iterator<Sistema> itSistema = em.estimacionesPorSistemas.values().iterator();
				while (itSistema.hasNext()) {
					Sistema s = itSistema.next();
					cbSistema.getItems().add(s);
				}
				
				Sistema s = Sistema.getInstanceTodos();
				cbSistema.getItems().add(s);
				cbSistema.setValue(s);
			}
		}
		
		if (sSel!=null && sSel.id == Sistema.ID_TODOS) sSel = null;
				
		hbBloques.getChildren().removeAll(hbBloques.getChildren());
		
		ArrayList<HashMap<String,Concepto>> salida = (new CosteEstReal()).inicializaAcumulador();
		ArrayList<HashMap<String,Concepto>> iterado = null;
			
		for (int i=0;i<ap.estimacionAnual.size();i++) {
			Pane p = new Pane();
			hbBloques.getChildren().add(p);
			
			CosteEstReal c = new CosteEstReal();
			
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(new URL(c.getFXML()));
	        p.getChildren().add(loader.load());
	        
	        c = loader.getController();
	        iterado = c.pintaCoste(VistaPPM.VISTA_ANUAL,ap.estimacionAnual.get(i),null,sSel);
	        this.acumulaCostes(salida, iterado);
		} 
		
		pintaColumnaResumen(salida);			
	}
	
	public void pintaDesgloseCostesMensual(AnalizadorPresupuesto ap, Sistema sSel) throws Exception {
		
		if (sSel.id == Sistema.ID_TODOS) sSel = null;
		
		hbBloques.getChildren().removeAll(hbBloques.getChildren());
		
		ArrayList<HashMap<String,Concepto>> salida = (new CosteEstReal()).inicializaAcumulador();
		ArrayList<HashMap<String,Concepto>> iterado = null;
			
		for (int i=0;i<ap.estimacionAnual.size();i++) {
			EstimacionAnio ea = ap.estimacionAnual.get(i);
						
			for (int j=0;j<12;j++){
				EstimacionMes em =  ea.estimacionesMensuales.get(j);
				
				if (em!=null){
					Pane p = new Pane();
					hbBloques.getChildren().add(p);
					
					CosteEstReal c = new CosteEstReal();
					
					FXMLLoader loader = new FXMLLoader();
			        loader.setLocation(new URL(c.getFXML()));
			        p.getChildren().add(loader.load());
			        
			        c = loader.getController();
			        iterado = c.pintaCoste(VistaPPM.VISTA_MENSUAL,ea,em,sSel);
			        this.acumulaCostes(salida, iterado);
				}				
			}
		} 
		
		pintaColumnaResumen(salida);
			
	}
	
	public void pintaColumnaResumen (ArrayList<HashMap<String,Concepto>> acumulador){
		ArrayList<Object> lista = new ArrayList<Object>();
		ArrayList<Object> listaCertif = new ArrayList<Object>();
		ArrayList<Object> listaTotal = new ArrayList<Object>();
		
		Iterator<MetaConcepto> itMC = MetaConcepto.listado.values().iterator();
		
		while (itMC.hasNext()) {
			MetaConcepto mc = itMC.next();
			
			if (mc.tipoGestionEconomica == MetaConcepto.GESTION_HORAS){
				Concepto c = acumulador.get(0).get(mc.codigo);
				HashMap<String,Float> conceptos = new HashMap<String,Float>();
				conceptos.put("Estimado", c.valorEstimado);
				conceptos.put("Real", c.valor);
				lista.add(conceptos);
			}				
			if (mc.tipoGestionEconomica == MetaConcepto.GESTION_CERTIFICACION){
				Concepto c = acumulador.get(1).get(mc.codigo);
				HashMap<String,Float> conceptos = new HashMap<String,Float>();
				conceptos.put("Estimado", c.valorEstimado);
				conceptos.put("Real", c.valor);
				listaCertif.add(conceptos);
			}				
		}
		
		Concepto c = acumulador.get(0).get(MetaConcepto.COD_TOTAL);
		HashMap<String,Float> conceptos = new HashMap<String,Float>();
		conceptos.put("Estimado", c.valorEstimado);
		conceptos.put("Real", c.valor);
		lista.add(conceptos);
		
		c = acumulador.get(1).get(MetaConcepto.COD_TOTAL);
		conceptos = new HashMap<String,Float>();
		conceptos.put("Estimado", c.valorEstimado);
		conceptos.put("Real", c.valor);
		listaCertif.add(conceptos);
		
		c = acumulador.get(2).get(MetaConcepto.COD_TOTAL);
		conceptos = new HashMap<String,Float>();
		conceptos.put("Estimado", c.valorEstimado);
		conceptos.put("Real", c.valor);
		listaTotal.add(conceptos);
		
		ObservableList<Tableable> dataTable = (new LineaCosteEstReal()).toListTableable(lista);
		tTitConcHoras1.setItems(dataTable);
		(new LineaCosteEstReal()).fijaColumnas(tTitConcHoras1);
		
		tTitConcHoras1.setPrefSize(15*10, lista.size()==1?40+40*lista.size():40*lista.size());
		
		dataTable =  (new LineaCosteEstReal()).toListTableable(listaCertif);
		tTitConcCert1.setItems(dataTable);
		(new LineaCosteEstReal()).fijaColumnas(tTitConcCert1);
		
		tTitConcCert1.setPrefSize(15*10, listaCertif.size()==1?40+40*listaCertif.size():40*listaCertif.size());
		
		dataTable = (new LineaCosteEstReal()).toListTableable(listaTotal);
		tTitTOTAL1.setItems(dataTable);
		(new LineaCosteEstReal()).fijaColumnas(tTitTOTAL1);
		
		tTitTOTAL1.setPrefSize(15*10, listaTotal.size()==1?40+40*listaTotal.size():40*listaTotal.size());
	}
	
	public void acumulaCostes(ArrayList<HashMap<String,Concepto>> acumulador, ArrayList<HashMap<String,Concepto>> aAcumular ) {
		for (int i=0;i<3;i++)  {
			HashMap<String,Concepto> mapaConceptos = aAcumular.get(i);
			HashMap<String,Concepto> mapaConceptosAcumulado = acumulador.get(i);
			Iterator<Concepto> itConcepto = mapaConceptos.values().iterator();
			
			while (itConcepto.hasNext()) {
				Concepto c = itConcepto.next();
				Concepto cAcum = mapaConceptosAcumulado.get(c.tipoConcepto.codigo);
				
				cAcum.valor += c.valor;
				cAcum.valorEstimado += c.valorEstimado;
			}			
		}
	}
	
	

}
