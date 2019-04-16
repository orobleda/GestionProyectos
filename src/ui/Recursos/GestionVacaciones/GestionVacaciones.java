package ui.Recursos.GestionVacaciones;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
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
import ui.interfaces.ControladorPantalla;

public class GestionVacaciones implements ControladorPantalla {
									   
	public static final String fxml = "file:src/ui/Recursos/GestionVacaciones/GestionVacaciones.fxml"; 
	
	public static Recurso recurso = null;
	
	@FXML
	private AnchorPane anchor;
	@FXML
    private VBox contenedorRecursos;
    
	@FXML
    private ComboBox<String> cbMeses;

    @FXML
    private ComboBox<Integer> cbAnios;
	
    @FXML
    private ImageView imGuardar;
    @FXML
    private ImageView imBuscarAn;
	
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
	
	public void initialize(){
		Date d = Constantes.fechaActual();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		cbAnios.getItems().add(c.get(Calendar.YEAR)-1);
		for (int i=c.get(Calendar.YEAR);i<c.get(Calendar.YEAR)+3;i++){
			cbAnios.getItems().add(i);	
		}
		
		new Constantes();
		
		for (int i=0;i<Constantes.meses.size();i++){
			cbMeses.getItems().add(Constantes.meses.get(i));	
		}
		
		cbAnios.setValue(c.get(Calendar.YEAR));
		cbMeses.setValue(Constantes.meses.get(c.get(Calendar.MONTH)+1));
		
		int mes = Constantes.numMes(cbMeses.getValue());
		int anio = cbAnios.getValue();
		
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
				
				if (gestorRecurso.id == Constantes.getAdministradorSistema().id && mc.id == MetaConcepto.SATAD) {
					contenedorRecurso = new HBox();
					contenedorRecursos.getChildren().add(contenedorRecurso);
					
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(new URL(new DetalleRecurso(r, mes, anio).getFXML()));
					contenedorRecurso.getChildren().add(loader.load());
				}
			}
			
			imGuardar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	guardarDatos(); }	});
			imBuscarAn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	consulta(); }	});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	public void consulta() {
		tablas = new ArrayList<DetalleRecurso>();
		HBox contenedorRecurso = null;
		
		int mes = Constantes.numMes(cbMeses.getValue());
		int anio = cbAnios.getValue();
		
		try {
			contenedorRecursos.getChildren().removeAll(contenedorRecursos.getChildren());
						
			Recurso r = new Recurso();
			ArrayList<Recurso> listaRecurso = r.listadoRecursos();
			Iterator<Recurso> itRecurso = listaRecurso.iterator();
			
			while (itRecurso.hasNext()) {
				r = itRecurso.next();
				r.cargaRecurso();
				
				Recurso gestorRecurso = ((Recurso) r.getValorParametro(MetaParametro.RECURSO_COD_GESTOR));
				
				if (gestorRecurso.id == Constantes.getAdministradorSistema().id) {
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
