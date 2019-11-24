package ui.Recursos.GestionVacaciones;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.PopOver;

import application.Main;
import controller.Log;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.beans.ParametroRecurso;
import model.beans.Recurso;
import model.constantes.Constantes;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaParametro;
import model.utils.db.ConsultaBD;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
import ui.interfaces.ControladorPantalla;

public class GestionVacaciones implements ControladorPantalla {
									   
	public static final String fxml = "file:src/ui/Recursos/GestionVacaciones/GestionVacaciones.fxml"; 
	
	public static Recurso recurso = null;
	
	@FXML
	private AnchorPane anchor;
	@FXML
    private VBox contenedorRecursos;
    
    private int mes;

    private int anio;
	
    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;
    @FXML
    private ImageView imBuscarAn;
    private GestionBotones gbBuscarAn;
    
    @FXML
    private ScrollPane scrollRecursos;
	
	public static ArrayList<DetalleRecurso> tablas = new ArrayList<DetalleRecurso>();
		
	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public GestionVacaciones(){
	}
	
	@Override
	public void resize(Scene escena) {	
		if (scrollRecursos==null) return;
				
		scrollRecursos.setPrefHeight(escena.getWindow().getHeight()*0.8);
		scrollRecursos.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
	}
	
	public void initialize(){ 
		Date d = Constantes.fechaActual();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
				
		mes = c.get(Calendar.MONTH)+1;
		anio = c.get(Calendar.YEAR);
		
		GestionVacaciones gv = this;
		
		tablas = new ArrayList<DetalleRecurso>();
		HBox contenedorRecurso = null;
		
		try {			
			Recurso r = new Recurso();
			ArrayList<Recurso> listaRecurso = r.listadoRecursos();
			Iterator<Recurso> itRecurso = listaRecurso.iterator();
			
			while (itRecurso.hasNext()) {
				r = itRecurso.next();
				r.cargaRecurso();
				
				 ParametroRecurso parRec = ((ParametroRecurso) r.getValorParametro(MetaParametro.RECURSO_COD_GESTOR));
				 ParametroRecurso parRecAux = ((ParametroRecurso) r.getValorParametro(MetaParametro.RECURSO_NAT_COSTE));
				 Recurso gestorRecurso = (Recurso) parRec.getValor();
				 MetaConcepto mc = (MetaConcepto) parRecAux.getValor();
				
				if (gestorRecurso!=null && gestorRecurso.id == Constantes.getAdministradorSistema().id && mc.id == MetaConcepto.SATAD) {
					contenedorRecurso = new HBox();
					contenedorRecursos.getChildren().add(contenedorRecurso);
					
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(new URL(new DetalleRecurso(r, mes, anio).getFXML()));
					contenedorRecurso.getChildren().add(loader.load());
				}
			}
			
			ImageView im = new ImageView();
			
			gbBuscarAn = new GestionBotones(GestionBotones.IZQ, new ImageView(), "Buscar3", false, new EventHandler<MouseEvent>() {        
				@Override
	            public void handle(MouseEvent t)
	            {   
					try {
						FXMLLoader loader = new FXMLLoader();
						FiltrosConsultaGV filtros = new FiltrosConsultaGV();
				        loader.setLocation(new URL(filtros.getFXML()));
				     	ParamTable.po = new PopOver(loader.load());
				     	
				     	HashMap<String,Object> mFiltros = new HashMap<String,Object>();
				     	mFiltros.put("ventanaPadre", gv);
				     	filtros = loader.getController();
				     	filtros.setParametrosPaso(mFiltros);
				     	
				     	ParamTable.po.show(contenedorRecursos);
				     	ParamTable.po.setAnimated(true);
				     	
					} catch (Exception e) {
						Log.e(e);
					}
	            } }, "Buscar Mes");	
			gbBuscarAn.activarBoton();
			
			gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
				@Override
	            public void handle(MouseEvent t)
	            {   
					try {
						guardarDatos();
					} catch (Exception e) {
						e.printStackTrace();
					}
	            } }, "Guardar asignación Mes", this);	
			gbGuardar.activarBoton();
			
			resize(Main.scene);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	public void consulta(int mesB, int anioB) {
		tablas = new ArrayList<DetalleRecurso>();
		HBox contenedorRecurso = null;
		
		mes = mesB;
		anio = anioB;
		
		try {
			contenedorRecursos.getChildren().removeAll(contenedorRecursos.getChildren());
						
			Recurso r = new Recurso();
			ArrayList<Recurso> listaRecurso = r.listadoRecursos();
			Iterator<Recurso> itRecurso = listaRecurso.iterator();
			
			while (itRecurso.hasNext()) {
				r = itRecurso.next();
				r.cargaRecurso();
				
				ParametroRecurso pGestorRecurso = ((ParametroRecurso) r.getValorParametro(MetaParametro.RECURSO_COD_GESTOR));
				Recurso gestorRecurso = ((Recurso) pGestorRecurso.getValor());
				 ParametroRecurso parRecAux = ((ParametroRecurso) r.getValorParametro(MetaParametro.RECURSO_NAT_COSTE));
				 MetaConcepto mc = (MetaConcepto) parRecAux.getValor();
				
				if (gestorRecurso!=null && gestorRecurso.id == Constantes.getAdministradorSistema().id && mc.id == MetaConcepto.SATAD) {
					contenedorRecurso = new HBox();
					contenedorRecursos.getChildren().add(contenedorRecurso);
					
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(new URL(new DetalleRecurso(r, mes, anio).getFXML()));
					contenedorRecurso.getChildren().add(loader.load());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	
    }
	
	public void guardarDatos() {
    	try {
			String idTransaccion = "updateListaVacacionesAusencias" + Constantes.fechaActual().getTime();
			
			int contador = 0;
			
			Iterator<DetalleRecurso> itDet = GestionVacaciones.tablas.iterator();
			
			while (itDet.hasNext()) {
				DetalleRecurso dr = itDet.next();
				
				contador+= dr.guardaDatos(idTransaccion);
			}
	    	
			(new ConsultaBD()).ejecutaTransaccion(idTransaccion);
			
	    	Dialogo.alert("Modificación de Ausencias y Vacaciones", "Guardado Correcto", "Se actualizaron " + contador + " festivos.");
		} catch (Exception e){
			Dialogo.error("Modificación de Ausencias y Vacaciones", "Error al guardar", "Se produjo un error al guardar los datos");
		}
    	
    	
    }
	
	public static void adscribirDetalleRecurso(DetalleRecurso dr) {
		GestionVacaciones.tablas.add(dr);
		dr.recursoObject = DetalleRecurso.recurso;
	}
	
	
	

}
