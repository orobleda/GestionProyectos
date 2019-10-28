package ui.Economico.ControlPresupuestario;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import controller.AnalizadorPresupuesto;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
import model.metadatos.TipoProyecto;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Tabla;
import ui.Economico.ControlPresupuestario.Tables.LineaCoste;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteDesglosado;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteDesglosadoSumario;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteSumario;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;
import ui.popUps.ConsultaAvanzadaProyectos;

public class ControlPresupuestario implements ControladorPantalla {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/ControlPresupuestario.fxml"; 
	
	public static final String ESTIMADO = "Estimado";
	public static final String REAL = "Real";
	public static final String COMBINADO = "Estimado-Real";
	public static final String COMBINADO_AC = "Estimado-Real AC";
	public static final String COLUMNA_I = "I";
	public static final String COLUMNA_D = "D";
	
	public static final int VISTA_PRES_ESTIMADO = 0;
	public static final int VISTA_PRES_REAL = 1;
	public static final int VISTA_PRES_ESTIMADOREAL = 2;
	public static final int VISTA_PRES_ANIO = 3;	
	public static final int VISTA_PRES_ESTIMADOREALAC = 4;
	
	public static final int VISTA_PPM = 0;
	public static final int VISTA_JERARQUIZADA = 1;
	public static final int VISTA_BRUTO = 2;
	public static final int VISTA_FOTO = 3;
	
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
	public VistaFoto vFoto = null;
		
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
    private Tabla tablaResumenIzqda;

    @FXML
    private TableView<Tableable> tbIzqda;
    private Tabla tablaIzqda;

    @FXML
    private ComboBox<Presupuesto> cbDrcha;

    @FXML
    public TableView<Tableable> tbResumenDcha;
    private Tabla tablaResumenDcha;

    @FXML
    private TableView<Tableable> tbDrcha;
    private Tabla tablaDrcha;

    @FXML
    private TableView<Tableable> tbResumenDiferencia;
    private Tabla tablaResumenDiferencia;

    @FXML
    private TableView<Tableable> tbDiferencia;
    private Tabla tablaDiferencia;
    
    @FXML
	private TextField tProyecto = null;	
	private static Proyecto proySeleccionado;
	
    @FXML
    private ImageView imConsultaAvanzada;
    private GestionBotones gbConsultaAvanzada;
    
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
    private ImageView imFoto;
    private GestionBotones gbFoto;
    
    @FXML
    private TitledPane panTopeImput;

    @FXML
    private TitledPane panDetalleProyecto;
    

    @FXML
    private HBox hbContJerarquia;    

    @FXML
    private Accordion acDetalles;
    
	ArrayList<ControladorPantalla> listaPantallas = null; 
	
	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}
	
	@Override
	public void resize(Scene escena) {
		acDetalles.setPrefWidth(escena.getWidth()*0.99);
		
		if (this.listaPantallas!=null) {
			Iterator<ControladorPantalla> itCOntPan = this.listaPantallas.iterator();
			while (itCOntPan.hasNext()) {
				ControladorPantalla cp = itCOntPan.next();
				cp.resize(escena);
			}
		}
		
	}
	
	private void consultaAvanzadaProyectos() throws Exception{		
		ConsultaAvanzadaProyectos.getInstance(this, 1, TipoProyecto.ID_PROYEVOLS, this.tProyecto);
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void fijaProyecto(ArrayList<Proyecto> listaProyecto) {
		if (listaProyecto!=null && listaProyecto.size()==1) {
			ControlPresupuestario.proySeleccionado = listaProyecto.get(0);
			this.tProyecto.setText(ControlPresupuestario.proySeleccionado.nombre);
			ParamTable.po.hide();
		
			proyectoSeleccionado(ControlPresupuestario.proySeleccionado);
		}
	}
	
	public void initialize(){
		tablaResumenIzqda = new Tabla(tbResumenIzqda, new LineaCoste(),this);
		tablaResumenDcha = new Tabla(tbResumenDcha, new LineaCoste(),this);
		tablaResumenDiferencia = new Tabla(tbResumenDiferencia, new LineaCosteSumario(),this);

		tablaIzqda = new Tabla(tbIzqda, new LineaCosteDesglosado(),this);
		tablaDrcha = new Tabla(tbDrcha, new LineaCosteDesglosado(),this);
		tablaDiferencia = new Tabla(tbDiferencia, new LineaCosteDesglosadoSumario(),this);
		
		ControlPresupuestario.elementoThis = this;
				
		gbConsultaAvanzada = new GestionBotones(imConsultaAvanzada, "BuscarAvzdo3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					consultaAvanzadaProyectos();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Consulta elementos");
		gbConsultaAvanzada.activarBoton();
		
		cbIzquda.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			presupuestoSeleccionado(newValue,tablaResumenIzqda,ControlPresupuestario.COLUMNA_I);
	    	}
	    );
		
		cbDrcha.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			presupuestoSeleccionado(newValue,tablaResumenDcha,ControlPresupuestario.COLUMNA_D);
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
		
		migas.put("Proyecto", ControlPresupuestario.proySeleccionado);
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
		if (ControlPresupuestario.elementoThis.gbPPM.presionado) {
			migas.put("Vista", ControlPresupuestario.VISTA_FOTO);
		}
	}
	
	public static void cargaPosicionActual() {
		if (migas == null) return;
		ArrayList<Proyecto> listaProyecto = new ArrayList<Proyecto>();
		listaProyecto.add((Proyecto) migas.get("Proyecto"));
	
		ControlPresupuestario.elementoThis.fijaProyecto(listaProyecto);
		
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
			ControlPresupuestario.elementoThis.selectorTipoPulsado(ControlPresupuestario.elementoThis.gbJerarquia, ControlPresupuestario.elementoThis.gbListado, ControlPresupuestario.elementoThis.gbPPM,ControlPresupuestario.elementoThis.gbFoto);
			ControlPresupuestario.elementoThis.vJer.cbMeses.setValue((String)migas.get("Mes"));	
			ControlPresupuestario.elementoThis.vJer.selectTab((Integer)migas.get("Tab"));
		}
		
		ParamTable.po.hide();
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
		        if (!this.listaPantallas.contains(vPPM)) {
		        	this.listaPantallas.add(vPPM);
		        }
			}
			if (vista==ControlPresupuestario.VISTA_JERARQUIZADA) {
				this.vJer = new VistaJerarquizada();
		        
				FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(vJer.getFXML()));
		        panDetalleProyecto.setContent(loader.load());
		        vJer = loader.getController();
		        vJer.pintaPresupuesto(ControlPresupuestario.ap, false);
		        if (!this.listaPantallas.contains(vJer)) {
		        	this.listaPantallas.add(vJer);
		        }
			}
			if (vista==ControlPresupuestario.VISTA_BRUTO) {
				this.vJer = new VistaJerarquizada();
		        
				FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(vJer.getFXML()));
		        panDetalleProyecto.setContent(loader.load());
		        vJer = loader.getController();
		        vJer.pintaPresupuestoGlobal(ControlPresupuestario.ap, false);
		        if (!this.listaPantallas.contains(vJer)) {
		        	this.listaPantallas.add(vJer);
		        }
			}
			if (vista==ControlPresupuestario.VISTA_FOTO) {
				this.vFoto = new VistaFoto();
		        
				FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(vFoto.getFXML()));
		        panDetalleProyecto.setContent(loader.load());
		        vFoto = loader.getController();

		        if (!this.listaPantallas.contains(vFoto)) {
		        	this.listaPantallas.add(vFoto);
		        }
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
				selectorTipoPulsado(gbJerarquia, gbListado, gbPPM,gbFoto);
				seleccionVista(ControlPresupuestario.VISTA_JERARQUIZADA);
            }
        }, "Vista Jerarquía");
		gbListado = new GestionBotones(imListado, "listado", true,new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				selectorTipoPulsado(gbListado, gbJerarquia, gbPPM,gbFoto);
				seleccionVista(ControlPresupuestario.VISTA_BRUTO);
            }
        }, "Vista Listado Plano");
		gbPPM = new GestionBotones(imPPM, "PPM", true,new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				selectorTipoPulsado(gbPPM, gbListado, gbJerarquia,gbFoto);
				seleccionVista(ControlPresupuestario.VISTA_PPM);
            }
        }, "Vista PPM");
		gbPPM.pulsarBoton();
		gbFoto = new GestionBotones(imFoto, "PPM", true,new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				selectorTipoPulsado(gbFoto,gbPPM, gbListado, gbJerarquia);
				seleccionVista(ControlPresupuestario.VISTA_FOTO);
            }
        }, "Vista PPM");
	}
	
	public void selectorTipoPulsado(GestionBotones activo, GestionBotones pasivo1, GestionBotones pasivo2, GestionBotones pasivo3) {
		pasivo1.liberarBoton();
		pasivo2.liberarBoton();
		pasivo3.liberarBoton();
		activo.pulsarBoton();
	}
	
	public void proyectoSeleccionado(Proyecto p) {
		Presupuesto prep = new Presupuesto();
		ArrayList<Presupuesto> presupuestos = prep.buscaPresupuestos(p);
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
		
		Presupuesto pAux4 = new Presupuesto();
		pAux4.descripcion = ControlPresupuestario.COMBINADO_AC;
		pAux4.id=-40;
		pAux4.calculado = true;
		listaPresupuestos.add(pAux4);
		
		cbIzquda.getItems().removeAll(cbIzquda.getItems());
		cbIzquda.getItems().addAll(presupuestos);
		
		cbDrcha.getItems().removeAll(cbDrcha.getItems());
		cbDrcha.getItems().addAll(presupuestos);
		cbDrcha.getItems().addAll(listaPresupuestos);
	}
	
	public void presupuestoSeleccionado(Presupuesto p, Tabla tabla, String lado) {
		if (lado==ControlPresupuestario.COLUMNA_I) {
			if (p==null) return;
			p.cargaCostes();
			cargaPresupuesto(p,tabla, lado);
			presIzqda = p;
			
			listaPantallas = new ArrayList<ControladorPantalla>(); 
			
			try {
				TopeImputaciones c = new TopeImputaciones();
		        		        
		        FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(c.getFXML()));
		        panTopeImput.setContent(loader.load());
		        this.tp = loader.getController();
		        this.tp.CalculaEstimacion(ControlPresupuestario.presIzqda);
		        listaPantallas.add(this.tp);

		        seleccionVista(ControlPresupuestario.VISTA_PPM);
		        flujoBotoneraCambioTipoInfo();		        
		        
		        this.tp.adscribir(this,p.p);
		        
		        ListaCertificaciones lc = new ListaCertificaciones();
		        loader = new FXMLLoader();
		        loader.setLocation(new URL(lc.getFXML()));
		        panCertificacion.setContent(loader.load());
		        this.lc = loader.getController();
		        this.lc.pintaCertificaciones(ControlPresupuestario.ap);
		        listaPantallas.add(this.lc);
		        
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
					p = ControlPresupuestario.ap.toPresupuesto(ControlPresupuestario.VISTA_PRES_ANIO,p.id/10,false);
				}
				if (p.id==-10) {
					p = ControlPresupuestario.ap.toPresupuesto(ControlPresupuestario.VISTA_PRES_ESTIMADO,p.id/10,false);
				}
				if (p.id==-20) {
					p = ControlPresupuestario.ap.toPresupuesto(ControlPresupuestario.VISTA_PRES_REAL,p.id/10,false);
				}
				if (p.id==-30) {
					p = ControlPresupuestario.ap.toPresupuesto(ControlPresupuestario.VISTA_PRES_ESTIMADOREAL,0,false);
				}
				if (p.id==-40) {
					p = ControlPresupuestario.ap.toPresupuesto(ControlPresupuestario.VISTA_PRES_ESTIMADOREALAC,0,false);
				}
				presDrcha = p;
				cargaPresupuesto(p,tabla, lado);
		}
		
		Tabla tablaDesglosada = null;
		if (ControlPresupuestario.COLUMNA_I.equals(lado)) {
			tablaDesglosada = tablaIzqda;
		} else {
			tablaDesglosada = tablaDrcha;
		}
		
		cargaSumario(p, tabla);
		cargaSumarioDesglosado(p, tablaDesglosada);		
	}
	
	public void cargaPresupuesto(Presupuesto p, Tabla tabla, String lado) {
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
		
		Tabla tablaDesglosada = null;
				
		ArrayList<Object> lista = new ArrayList<Object>();
		lista.add(new LineaCoste(listaConceptos));
		tabla.pintaTabla(lista);
		
		if (ControlPresupuestario.COLUMNA_I.equals(lado)) {
			listaIzqdaResumen = lista;
			listaIzqda = listaConceptosDesglosada;
			tablaDesglosada = tablaIzqda;
		} else {
			listaDrchaResumen = lista;
			listaDrcha = listaConceptosDesglosada;
			tablaDesglosada = tablaDrcha;
		}
		
		tablaDesglosada.pintaTabla(listaConceptosDesglosada);
	}
	
	public void cargaSumario(Presupuesto p, Tabla tabla) {
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
				tablaResumenDiferencia.pintaTabla(lista);
				
			}	
	}
	
	public void cargaSumarioDesglosado(Presupuesto p, Tabla tabla) {
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
			
			tablaDiferencia.pintaTabla(lista);
			
			listaSumario = lista;
		}
				
	}

}
