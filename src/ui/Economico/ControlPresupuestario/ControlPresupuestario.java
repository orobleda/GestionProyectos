package ui.Economico.ControlPresupuestario;

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
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.beans.Concepto;
import model.beans.Coste;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import ui.GestionBotones;
import ui.Economico.ControlPresupuestario.Tables.LineaCoste;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteDesglosado;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteDesglosadoSumario;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteSumario;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class ControlPresupuestario implements ControladorPantalla {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/ControlPresupuestario.fxml"; 
	
	public static final String ESTIMADO = "Estimado";
	public static final String REAL = "Real";
	public static final String COMBINADO = "Real/Estimado";
	public static final String COLUMNA_I = "I";
	public static final String COLUMNA_D = "D";
	
	public static final int VISTA_PRES_ESTIMADO = 0;
	public static final int VISTA_PRES_REAL = 1;
	public static final int VISTA_PRES_ESTIMADOREAL = 2;
	public static final int VISTA_PRES_ANIO = 3;	
	
	public static final int VISTA_PPM = 0;
	public static final int VISTA_JERARQUIZADA = 1;
	public static final int VISTA_BRUTO = 2;
	
	public static HashMap<String,Object> migas = null;
	public static ControlPresupuestario elementoThis = null;
	
	public static Presupuesto presIzqda = null;
	public static Presupuesto presDrcha = null;
	public static AnalizadorPresupuesto ap = null;
	public static String mesActual = null;
	public TopeImputaciones tp = null;
	public ListaCertificaciones lc = null;
	public VistaPPM vPPM = null;
	public VistaJerarquizada vJer = null;
		
	public static ArrayList<Object> listaIzqdaResumen = null;
	public static ArrayList<Object> listaDrchaResumen = null;
	public static ArrayList<Object> listaIzqda = null;
	public static ArrayList<Object> listaDrcha = null;
	public static ArrayList<Object> listaSumarioResumen = null;
	public static ArrayList<Object> listaSumario = null;
	
	@FXML
	private AnchorPane anchor;
	
	@FXML
    private ComboBox<Presupuesto> cbIzquda;

    @FXML
    private TableView<Tableable> tbResumenIzqda;

    @FXML
    private TableView<Tableable> tbIzqda;

    @FXML
    private ComboBox<Presupuesto> cbDrcha;

    @FXML
    public TableView<Tableable> tbResumenDcha;

    @FXML
    private TableView<Tableable> tbDrcha;

    @FXML
    private TableView<Tableable> tbResumenDiferencia;

    @FXML
    private TableView<Tableable> tbDiferencia;
    
    @FXML
    private ComboBox<Proyecto> cbProyectos;
    
    @FXML
    private TitledPane tpIzqda;
    
    @FXML
    private Accordion acIzqda;
    @FXML
    private Accordion acDrcha;
    @FXML
    private Accordion acResumen;
    @FXML
    private TitledPane tpDetalleProyecto;
    
    @FXML
    private TitledPane panCertificacion;

    @FXML
    private ImageView imJerarquia;
    private GestionBotones gbJerarquia;

    @FXML
    private ImageView imListado;
    private GestionBotones gbListado;

    @FXML
    private ImageView imPPM;
    private GestionBotones gbPPM;
    
    @FXML
    private TitledPane panTopeImput;

    @FXML
    private TitledPane panDetalleProyecto;
    

    @FXML
    private HBox hbContJerarquia;
	
	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}
	
	@Override
	public void resize(Scene escena) {
		
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		ControlPresupuestario.elementoThis = this;
		
		Proyecto p = new Proyecto();
		ArrayList<Proyecto> proyectos = p.listadoProyectos();
		
		cbProyectos.getItems().addAll(proyectos);
		
		cbProyectos.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			proyectoSeleccionado (newValue);
	    	}
	    ); 
		
		cbIzquda.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			presupuestoSeleccionado(newValue,tbResumenIzqda,ControlPresupuestario.COLUMNA_I);
	    	}
	    );
		
		cbDrcha.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			presupuestoSeleccionado(newValue,tbResumenDcha,ControlPresupuestario.COLUMNA_D);
	    	}
	    );
		
		acIzqda.getPanes().get(0).expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
	        if (isNowExpanded) {
	        	acDrcha.getPanes().get(0).setExpanded(true);
	        	acResumen.getPanes().get(0).setExpanded(true);
	        } else {
	        	acDrcha.getPanes().get(0).setExpanded(false);
	        	acResumen.getPanes().get(0).setExpanded(false);
	        }
	    });
		
		acDrcha.getPanes().get(0).expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
	        if (isNowExpanded) {
	        	acIzqda.getPanes().get(0).setExpanded(true);
	        	acResumen.getPanes().get(0).setExpanded(true);
	        } else {
	        	acIzqda.getPanes().get(0).setExpanded(false);
	        	acResumen.getPanes().get(0).setExpanded(false);
	        }
	    });
		
		acResumen.getPanes().get(0).expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
	        if (isNowExpanded) {
	        	acIzqda.getPanes().get(0).setExpanded(true);
	        	acDrcha.getPanes().get(0).setExpanded(true);
	        } else {
	        	acIzqda.getPanes().get(0).setExpanded(false);
	        	acDrcha.getPanes().get(0).setExpanded(false);
	        }
	    });
		
		flujoBotoneraCambioTipoInfo();
		
	}
	
	public static void salvaPosicionActual() {
		ControlPresupuestario.migas = new HashMap<String,Object>();
		
		migas.put("Proyecto", ControlPresupuestario.elementoThis.cbProyectos.getValue());
		migas.put("Presupuesto", ControlPresupuestario.elementoThis.cbIzquda.getValue());
		
		if (ControlPresupuestario.elementoThis.gbJerarquia.presionado) {
			migas.put("Vista", ControlPresupuestario.VISTA_JERARQUIZADA);
			migas.put("Mes", ControlPresupuestario.elementoThis.vJer.cbMeses.getValue());
			migas.put("Tab", ControlPresupuestario.elementoThis.vJer.getTabSelected());
		}
		if (ControlPresupuestario.elementoThis.gbListado.presionado) {
			migas.put("Vista", ControlPresupuestario.VISTA_BRUTO);
		}
		if (ControlPresupuestario.elementoThis.gbPPM.presionado) {
			migas.put("Vista", ControlPresupuestario.VISTA_PPM);
		}
	}
	
	public static void cargaPosicionActual() {
		if (migas == null) return;
		
		ControlPresupuestario.elementoThis.proyectoSeleccionado((Proyecto) migas.get("Proyecto"));
		
		Presupuesto p = (Presupuesto) migas.get("Presupuesto");
		
		Iterator<Presupuesto> itPres = ControlPresupuestario.elementoThis.cbIzquda.getItems().iterator();
		
		while (itPres.hasNext()) {
			Presupuesto p2 = itPres.next();
			if (p2.id == p.id) {
				p = p2;
				break;
			}
		}
		
		ControlPresupuestario.elementoThis.cbIzquda.setValue(p);
		
		ControlPresupuestario.elementoThis.seleccionVista((Integer) migas.get("Vista"));
		
		if (ControlPresupuestario.VISTA_JERARQUIZADA == (Integer) migas.get("Vista")) {
			ControlPresupuestario.elementoThis.selectorTipoPulsado(ControlPresupuestario.elementoThis.gbJerarquia, ControlPresupuestario.elementoThis.gbListado, ControlPresupuestario.elementoThis.gbPPM);
			ControlPresupuestario.elementoThis.vJer.cbMeses.setValue((String)migas.get("Mes"));	
			ControlPresupuestario.elementoThis.vJer.selectTab((Integer)migas.get("Tab"));
		}
		
		
		migas = null;
	}
	
	public void seleccionVista(int vista) {
		try {
			if (vista==ControlPresupuestario.VISTA_PPM) {
				VistaPPM vPPM = new VistaPPM();
		        
				FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(vPPM.getFXML()));
		        panDetalleProyecto.setContent(loader.load());
		        this.vPPM = loader.getController();
		        this.vPPM.pintaDesgloseCostes(ControlPresupuestario.ap,VistaPPM.VISTA_ANUAL, null);
			}
			if (vista==ControlPresupuestario.VISTA_JERARQUIZADA) {
				this.vJer = new VistaJerarquizada();
		        
				FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(vJer.getFXML()));
		        panDetalleProyecto.setContent(loader.load());
		        vJer = loader.getController();
		        vJer.pintaPresupuesto(ControlPresupuestario.ap, false);
			}
			if (vista==ControlPresupuestario.VISTA_BRUTO) {
				this.vJer = new VistaJerarquizada();
		        
				FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(vJer.getFXML()));
		        panDetalleProyecto.setContent(loader.load());
		        vJer = loader.getController();
		        vJer.pintaPresupuestoGlobal(ControlPresupuestario.ap, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void flujoBotoneraCambioTipoInfo() {
		gbJerarquia = new GestionBotones(imJerarquia, "jerarquia", true, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				selectorTipoPulsado(gbJerarquia, gbListado, gbPPM);
				seleccionVista(ControlPresupuestario.VISTA_JERARQUIZADA);
            }
        }, "Vista Jerarquía");
		gbListado = new GestionBotones(imListado, "listado", true,new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				selectorTipoPulsado(gbListado, gbJerarquia, gbPPM);
				seleccionVista(ControlPresupuestario.VISTA_BRUTO);
            }
        }, "Vista Listado Plano");
		gbPPM = new GestionBotones(imPPM, "PPM", true,new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				selectorTipoPulsado(gbPPM, gbListado, gbJerarquia);
				seleccionVista(ControlPresupuestario.VISTA_PPM);
            }
        }, "Vista PPM");
		gbPPM.pulsarBoton();
	}
	
	public void selectorTipoPulsado(GestionBotones activo, GestionBotones pasivo1, GestionBotones pasivo2) {
		pasivo1.liberarBoton();
		pasivo2.liberarBoton();
		activo.pulsarBoton();
	}
	
	public void proyectoSeleccionado(Proyecto p) {
		Presupuesto prep = new Presupuesto();
		ArrayList<Presupuesto> presupuestos = prep.buscaPresupuestos(p.id);
		ArrayList<Presupuesto> listaPresupuestos = new ArrayList<Presupuesto>();
		
		try {
			p.cargaProyecto();
		
			Date fInicio = (Date) p.getValorParametro(MetaParametro.PROYECTO_FX_INICIO).getValor();
			Date fFin = (Date) p.getValorParametro(MetaParametro.PROYECTO_FX_FIN).getValor();
			
			Calendar cInicio = Calendar.getInstance();
			cInicio.setTime(fInicio);
			
			Calendar cFin = Calendar.getInstance();
			cFin.setTime(fFin);
			
			while (cInicio.compareTo(cFin)<=0) {
				Presupuesto pAux = new Presupuesto();
				listaPresupuestos.add(pAux);
				pAux.descripcion = ""+  cInicio.get(Calendar.YEAR);
				pAux.id=cInicio.get(Calendar.YEAR)*10;
				pAux.calculado = true;
				cInicio.add(Calendar.YEAR, 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		Presupuesto pAux1 = new Presupuesto();
		pAux1.descripcion = ControlPresupuestario.ESTIMADO;
		pAux1.id=-10;
		pAux1.calculado = true;
		listaPresupuestos.add(pAux1);
		
		Presupuesto pAux2 = new Presupuesto();
		pAux2.descripcion = ControlPresupuestario.REAL;
		pAux2.id=-20;
		pAux2.calculado = true;
		listaPresupuestos.add(pAux2);
		
		Presupuesto pAux3 = new Presupuesto();
		pAux3.descripcion = ControlPresupuestario.COMBINADO;
		pAux3.id=-30;
		pAux3.calculado = true;
		listaPresupuestos.add(pAux3);
		
		cbIzquda.getItems().removeAll(cbIzquda.getItems());
		cbIzquda.getItems().addAll(presupuestos);
		
		cbDrcha.getItems().removeAll(cbDrcha.getItems());
		cbDrcha.getItems().addAll(presupuestos);
		cbDrcha.getItems().addAll(listaPresupuestos);
	}
	
	public void presupuestoSeleccionado(Presupuesto p,TableView<Tableable> tabla, String lado) {
		if (lado==ControlPresupuestario.COLUMNA_I) {
			if (p==null) return;
			p.cargaCostes();
			cargaPresupuesto(p,tabla, lado);
			presIzqda = p;
			
			try {
				TopeImputaciones c = new TopeImputaciones();
		        		        
		        FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(c.getFXML()));
		        panTopeImput.setContent(loader.load());
		        this.tp = loader.getController();
		        this.tp.CalculaEstimacion(ControlPresupuestario.presIzqda);

		        seleccionVista(ControlPresupuestario.VISTA_PPM);
		        flujoBotoneraCambioTipoInfo();		        
		        
		        this.tp.adscribir(this,p.p);
		        
		        ListaCertificaciones lc = new ListaCertificaciones();
		        loader = new FXMLLoader();
		        loader.setLocation(new URL(lc.getFXML()));
		        panCertificacion.setContent(loader.load());
		        this.lc = loader.getController();
		        this.lc.pintaCertificaciones(ControlPresupuestario.ap);
		        
		        Iterator<Presupuesto> itPres = cbDrcha.getItems().iterator();
		        Presupuesto pAux = null;
		        
		        while (itPres.hasNext()) {
		        	pAux = itPres.next();
		        	if (pAux.id == -30) break;
		        }
		        
		        cbDrcha.setValue(pAux);
		        		        
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
				if (p==null) return;
				if (p.id>20000){
					p = this.ap.toPresupuesto(ControlPresupuestario.VISTA_PRES_ANIO,p.id/10,false);
				}
				if (p.id==-10) {
					p = this.ap.toPresupuesto(ControlPresupuestario.VISTA_PRES_ESTIMADO,p.id/10,false);
				}
				if (p.id==-20) {
					p = this.ap.toPresupuesto(ControlPresupuestario.VISTA_PRES_REAL,p.id/10,false);
				}
				if (p.id==-30) {
					p = this.ap.toPresupuesto(ControlPresupuestario.VISTA_PRES_ESTIMADOREAL,0,false);
				}
				presDrcha = p;
				cargaPresupuesto(p,tabla, lado);
		}
				
		cargaSumario(p, tabla);
		cargaSumarioDesglosado(p, tabla);		
	}
	
	public void cargaPresupuesto(Presupuesto p, TableView<Tableable> tabla, String lado) {
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
		MetaConcepto mc = new MetaConcepto();
		mc.codigo = "TOTAL";
		mc.descripcion = "Total";
		mc.id = MetaConcepto.ID_TOTAL;
		caux.tipoConcepto = mc;
		caux.valorEstimado = acumulado;
		listaConceptos.put(caux.tipoConcepto.codigo,caux);
		
		TableView<Tableable> tablaDesglosada = null;
		
		ArrayList<Object> lista = new ArrayList<Object>();
		lista.add(new LineaCoste(listaConceptos));
		ObservableList<Tableable> dataTable = (new LineaCoste()).toListTableable(lista);
		tabla.setItems(dataTable);
		
		if (ControlPresupuestario.COLUMNA_I.equals(lado)) {
			listaIzqdaResumen = lista;
			listaIzqda = listaConceptosDesglosada;
			tablaDesglosada = tbIzqda;
		} else {
			listaDrchaResumen = lista;
			listaDrcha = listaConceptosDesglosada;
			tablaDesglosada = tbDrcha;
		}

		(new LineaCoste()).fijaColumnas(tabla);
		
		dataTable = (new LineaCosteDesglosado()).toListTableable(listaConceptosDesglosada);
		tablaDesglosada.setItems(dataTable);
		(new LineaCosteDesglosado()).fijaColumnas(tablaDesglosada);
		
		if (listaIzqda!=null) {
			tbIzqda.setPrefHeight(40*listaIzqda.size());
			tbDiferencia.setPrefHeight(40*listaIzqda.size()); 
			tbResumenDiferencia.setPrefHeight(40*listaIzqda.size());
		}
		else{
			tbIzqda.setPrefHeight(30);
			tbDiferencia.setPrefHeight(30);
			tbResumenDiferencia.setPrefHeight(30);
		} 		
		if (listaDrcha!=null){
			tbDrcha.setPrefHeight(40*listaDrcha.size());
			tbDiferencia.setPrefHeight(40*listaDrcha.size());
			tbResumenDiferencia.setPrefHeight(40*listaDrcha.size());
		}  else tbDrcha.setPrefHeight(30);
	}
	
	public void cargaSumario(Presupuesto p, TableView<Tableable> tabla) {
			if (listaIzqda!=null && listaDrcha!=null) {
				HashMap<String,Concepto> listaConceptos = new HashMap<String,Concepto>();
				
				float totalIzqda = ((LineaCoste) listaIzqdaResumen.get(0)).listaConceptos.get("TOTAL").valorEstimado;
				float totalDrcha = ((LineaCoste) listaDrchaResumen.get(0)).listaConceptos.get("TOTAL").valorEstimado;
				float relacion = 0;
				relacion = 100 * totalDrcha / totalIzqda;
				float diferencia = totalIzqda-totalDrcha;
				
				Concepto caux = new Concepto();
				MetaConcepto mc = new MetaConcepto();
				mc.codigo = "%";
				mc.descripcion = "%";
				mc.id = MetaConcepto.ID_PORC;
				caux.tipoConcepto = mc;
				caux.valorEstimado = relacion;
				listaConceptos.put(caux.tipoConcepto.codigo,caux);
				
				caux = new Concepto();
				mc = new MetaConcepto();
				mc.codigo = "Diferencia";
				mc.descripcion = "Diferencia";
				mc.id = MetaConcepto.ID_DIFF;
				caux.tipoConcepto = mc;
				caux.valorEstimado = diferencia;
				listaConceptos.put(caux.tipoConcepto.codigo,caux);
				
				ArrayList<Object> lista = new ArrayList<Object>();
				lista.add(new LineaCosteSumario(listaConceptos));
				ObservableList<Tableable> dataTable = (new LineaCosteSumario()).toListTableable(lista);
				tbResumenDiferencia.setItems(dataTable);
				
				(new LineaCosteSumario()).fijaColumnas(tbResumenDiferencia);
				
				//tbResumenDiferencia.setPrefHeight(40*lista.size());
			}	
	}
	
	public void cargaSumarioDesglosado(Presupuesto p, TableView<Tableable> tabla) {
		if (listaIzqdaResumen!=null && listaDrchaResumen!=null) {
			HashMap<String,ArrayList<Float>> listaResumen = new HashMap<String,ArrayList<Float>>();
			HashMap<String,Concepto> listaConceptos = new HashMap<String,Concepto>();
			
			Iterator<Object> itIzqda = ControlPresupuestario.listaIzqda.iterator();
			
			while (itIzqda.hasNext()) {
				LineaCosteDesglosado lcds = (LineaCosteDesglosado) itIzqda.next();
				
				ArrayList<Float> valores = new ArrayList<Float>();
				listaResumen.put(lcds.listaConceptos.get("SISTEMA").coste.sistema.codigo, valores);
				valores.add(lcds.listaConceptos.get("TOTAL").valorEstimado);
				valores.add(new Float(0));
			}
			
			Iterator<Object> itDrcha = ControlPresupuestario.listaDrcha.iterator();
			
			while (itDrcha.hasNext()) {
				LineaCosteDesglosado lcds = (LineaCosteDesglosado) itDrcha.next();
				
				if (listaResumen.containsKey(lcds.listaConceptos.get("SISTEMA").coste.sistema.codigo)){
					ArrayList<Float> lista = listaResumen.get(lcds.listaConceptos.get("SISTEMA").coste.sistema.codigo);
					lista.set(1, lcds.listaConceptos.get("TOTAL").valorEstimado);
				} else {
					ArrayList<Float> valores = new ArrayList<Float>();
					listaResumen.put(lcds.listaConceptos.get("SISTEMA").coste.sistema.codigo, valores);
					valores.add(new Float(0));
					valores.add(lcds.listaConceptos.get("TOTAL").valorEstimado);	
				}											
			}
			
			ArrayList<Object> lista = new ArrayList<Object>();
			itIzqda = ControlPresupuestario.listaIzqda.iterator();
			
			while (itIzqda.hasNext()) {
				LineaCosteDesglosado lcds = (LineaCosteDesglosado) itIzqda.next();
				ArrayList<Float> listaAux = listaResumen.get(lcds.listaConceptos.get("SISTEMA").coste.sistema.codigo);
				
				listaConceptos = new HashMap<String,Concepto>();
				
				Concepto caux = new Concepto();
				MetaConcepto mc = new MetaConcepto();
				mc.codigo = "%";
				mc.descripcion = "%";
				mc.id = MetaConcepto.ID_PORC;
				caux.tipoConcepto = mc;
				caux.valorEstimado = 100*listaAux.get(1)/listaAux.get(0);
				listaConceptos.put(caux.tipoConcepto.codigo,caux);
				
				caux = new Concepto();
				mc = new MetaConcepto();
				mc.codigo = "Diferencia";
				mc.descripcion = "Diferencia";
				mc.id = MetaConcepto.ID_DIFF;
				caux.tipoConcepto = mc;
				caux.valorEstimado = listaAux.get(0)-listaAux.get(1);
				listaConceptos.put(caux.tipoConcepto.codigo,caux);
				
				caux = new Concepto();
				mc = new MetaConcepto();
				mc.codigo = "SISTEMA";
				mc.descripcion = "SISTEMA";
				mc.id = MetaConcepto.ID_DIFF;
				caux.tipoConcepto = mc;
				Coste c = new Coste();
				c.sistema = Sistema.get(lcds.listaConceptos.get("SISTEMA").coste.sistema.codigo);
				caux.coste = c;
				listaConceptos.put(caux.tipoConcepto.codigo,caux);
				lista.add(new LineaCosteDesglosadoSumario(listaConceptos));
			}
			
			itDrcha = ControlPresupuestario.listaDrcha.iterator();
			
			while (itDrcha.hasNext()) {
				LineaCosteDesglosado lcds = (LineaCosteDesglosado) itDrcha.next();
				String codSistema = lcds.listaConceptos.get("SISTEMA").coste.sistema.codigo;
				
				Iterator<Object> itAux = ControlPresupuestario.listaIzqda.iterator();
				boolean encontrado = false;
				
				while (itAux.hasNext()) {
					LineaCosteDesglosado lcdsAux = (LineaCosteDesglosado) itAux.next();
					
					if (codSistema.equals(lcdsAux.listaConceptos.get("SISTEMA").coste.sistema.codigo)) {
						encontrado = true;
						break;
					}
				}
				
				if (!encontrado) {
					ArrayList<Float> listaAux = listaResumen.get(codSistema);
					
					listaConceptos = new HashMap<String,Concepto>();
					
					Concepto caux = new Concepto();
					MetaConcepto mc = new MetaConcepto();
					mc.codigo = "%";
					mc.descripcion = "%";
					mc.id = MetaConcepto.ID_PORC;
					caux.tipoConcepto = mc;
					caux.valorEstimado = 100*listaAux.get(1)/listaAux.get(0);
					listaConceptos.put(caux.tipoConcepto.codigo,caux);
					
					caux = new Concepto();
					mc = new MetaConcepto();
					mc.codigo = "Diferencia";
					mc.descripcion = "Diferencia";
					mc.id = MetaConcepto.ID_DIFF;
					caux.tipoConcepto = mc;
					caux.valorEstimado = listaAux.get(0)-listaAux.get(1);
					listaConceptos.put(caux.tipoConcepto.codigo,caux);
					
					caux = new Concepto();
					mc = new MetaConcepto();
					mc.codigo = "SISTEMA";
					mc.descripcion = "SISTEMA";
					mc.id = MetaConcepto.ID_DIFF;
					caux.tipoConcepto = mc;
					Coste c = new Coste();
					c.sistema = Sistema.listado.get(lcds.listaConceptos.get("SISTEMA").coste.sistema.codigo);
					caux.coste = c;
					listaConceptos.put(caux.tipoConcepto.codigo,caux);
					lista.add(new LineaCosteDesglosadoSumario(listaConceptos));
				}							
			}
			
			ObservableList<Tableable> dataTable = (new LineaCosteDesglosadoSumario()).toListTableable(lista);
			tbDiferencia.setItems(dataTable);
			
			(new LineaCosteDesglosadoSumario()).fijaColumnas(tbDiferencia);
			listaSumario = lista;
		}
				
	}

}
