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
    private ComboBox<Proyecto> cbProyecto;

    @FXML
    private ImageView imGuardarNuevaVersion;

    @FXML
    private TextField tVsProyecto;

    @FXML
    private ImageView imNuevoProyecto;
    private GestionBotones gbNuevoProyecto;

    @FXML
    private ImageView imAniadirDemanda;

    @FXML
    private ImageView imBuscarVersion;
    private GestionBotones gbBuscarVersion;

    @FXML
    private ImageView imGuardar;

    @FXML
    private ComboBox<Presupuesto> cbVersion;
	
	@FXML
	private AnchorPane anchor;
	
	
	public void initialize(){
		tablaDemandas = new Tabla(tDemandas,new DesgloseDemandasAsocidasTabla());
		tablaCoste = new Tabla(tCoste,new LineaCosteDesglosado());
		
		Proyecto p = new Proyecto();
		ArrayList<Proyecto> listaProyectos = p.listadoProyectosGGP();
		
		cbProyecto.getItems().addAll(listaProyectos);
		cbProyecto.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { buscaPresupuestos (newValue);  	}   );
		
		cbVersion.getItems().removeAll(cbVersion.getItems());
		cbVersion.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { versionSeleccionada ();  	}   );
		
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
		
		gbBuscarVersion = new GestionBotones(imBuscarVersion, "Buscar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {						        
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Buscar Versión Presupuesto");
		gbBuscarVersion.desActivarBoton();
		
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
			
			ArrayList<Proyecto> listaDemRel = proy.getDemandasAsociadas();
			
			listaDemRel.add(proy);
			
			ArrayList<Object> listaPintable = new ArrayList<Object>();
			listaPintable.addAll(listaDemRel);
			
			tablaDemandas.pintaTabla(listaPintable);
			
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
	
	private void buscaPresupuestos(Proyecto p) {
		cbVersion.getItems().removeAll(cbVersion.getItems());
		
		Presupuesto pres = new Presupuesto();		
		ArrayList<Presupuesto> listado = pres.buscaPresupuestos(p.id);
		
		cbVersion.getItems().addAll(listado);
		
		if (listado.size()>0) {
			
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
