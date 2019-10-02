package ui.Economico.ControlPresupuestario.EdicionCert;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.beans.CertificacionFaseParcial;
import model.beans.Proveedor;
import model.beans.Proyecto;
import model.beans.Recurso;
import model.metadatos.Sistema;
import model.utils.pdf.ProcesaCertificacion;
import ui.GestionBotones;
import ui.interfaces.ControladorPantalla;
import ui.popUps.PopUp;

public class ImportaCertificacion implements ControladorPantalla, PopUp {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/EdicionCert/ImportaCertificacion.fxml";
	
	public static final String FICHERO = "FICHERO";
	public static final String SISTEMA = "SISTEMA";
	public static final String PROYECTO = "PROYECTO";
	public static final String CERTIFICACIONES = "CERTIFICACIONES";
	
	public boolean esPopUp = false;
	public static HashMap<String, Object> variablesPaso = null;
	public static Object claseRetorno = null;
	public static String metodoRetorno = null;	
	
    @FXML
    private TextField tProyecto;

    @FXML
    private TextField tTpCertificacion;

    @FXML
    private TextField tEstCertificacion;

    @FXML
    private TextField tPedido;

    @FXML
    private ComboBox<Proveedor> cbProveedor;

    @FXML
    private ComboBox<Recurso> cbRecurso;

    @FXML
    private TextField tFxCertificacion;

    @FXML
    private Label lNombreFichero;    

    @FXML
    private VBox vbLineas;

    @FXML
    private ImageView imCargaCertificacion;
    private GestionBotones gbCargaCertificacion;
	
    @FXML
    private TextField tfnombreCertif;
    
    @FXML
    private TextField tNumSoli;
    
    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;
    
    private File ficheroBuscado = null;
    private Sistema sist = null;
    private Proyecto p = null;
    private ArrayList<CertificacionFaseParcial> lcfp = null;
	   
	@FXML
	private AnchorPane anchor;	
	
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
		gbCargaCertificacion = new GestionBotones(imCargaCertificacion, "Buscar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            { 
				try {
					procesaArchivo();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
            } }, "Procesa Certificacion");
		gbCargaCertificacion.activarBoton();
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            { 
				try {
					guardarCertificacion();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
            } }, "Guarda Certificacion");
		gbGuardar.desActivarBoton();
	}

	@Override
	public String getControlFXML() {
		return ImportaCertificacion.fxml;
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		ImportaCertificacion.variablesPaso = variablesPaso;
		this.ficheroBuscado = (File) variablesPaso.get(ImportaCertificacion.FICHERO);
		this.sist = (Sistema) variablesPaso.get(ImportaCertificacion.SISTEMA);
		this.p =  (Proyecto) variablesPaso.get(ImportaCertificacion.PROYECTO);
		this.lcfp = (ArrayList<CertificacionFaseParcial>) variablesPaso.get(ImportaCertificacion.CERTIFICACIONES);
		
		this.lNombreFichero.setText(this.ficheroBuscado.getAbsolutePath());
		
		cbProveedor.getItems().addAll(Proveedor.listado.values());
		cbRecurso.getItems().addAll(Recurso.listadoRecursosEstatico().values());
	}
	
	public void procesaArchivo() {
		
		ProcesaCertificacion pc = new ProcesaCertificacion();
		HashMap<String,Object> procesada = pc.procesa(this.ficheroBuscado.getAbsolutePath());
		
		try {
			this.tfnombreCertif.setText((String) procesada.get(ProcesaCertificacion.DESCRIPCION));
			this.tNumSoli.setText((String) procesada.get(ProcesaCertificacion.SOLICITUD));
			this.tProyecto.setText((String) procesada.get(ProcesaCertificacion.PROYECTO));
			this.tTpCertificacion.setText((String) procesada.get(ProcesaCertificacion.TIPO));
			this.tEstCertificacion.setText((String) procesada.get(ProcesaCertificacion.ESTADO));
			this.tPedido.setText((String) procesada.get(ProcesaCertificacion.PEDIDO));
			
			Iterator<Proveedor> itProv = this.cbProveedor.getItems().iterator();
			Proveedor prov = null;
			while (itProv.hasNext()) {
				Proveedor prov2 = itProv.next();
				if (prov2.identificaPorPPM(((String) procesada.get(ProcesaCertificacion.PROVEEDOR)))){
					prov = prov2;
					break;
				}
			}
			if (prov!=null) this.cbProveedor.setValue(prov);
			
			Iterator<Recurso> itRec = this.cbRecurso.getItems().iterator();
			Recurso rec = null;
			while (itRec.hasNext()) {
				Recurso rec2 = itRec.next();
				if (rec2.identificaPorPPM(((String) procesada.get(ProcesaCertificacion.CREADOR)))){
					rec = rec2;
					break;
				}
			}
			if (rec!=null) this.cbRecurso.setValue(rec);
			
			this.tFxCertificacion.setText((String) procesada.get(ProcesaCertificacion.FECHA_CERTIFICACION));
			
			Iterator<HashMap<String,String>> itO = ((HashMap<String,HashMap<String,String>>) procesada.get(ProcesaCertificacion.HITOS)).values().iterator();
			while (itO.hasNext()) {
				HashMap<String,String> el = itO.next();
				HashMap<String,Object> varPaso = new HashMap<String,Object>();
				varPaso.put(ImportaCertificacion.CERTIFICACIONES,this.lcfp);
				Iterator<String> elIt = el.keySet().iterator();
				while (elIt.hasNext()) {
					String key = elIt.next();
					varPaso.put(key, el.get(key));
				}
				
				AsignacionCertificacion aCertificacion = new AsignacionCertificacion();
		        FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(aCertificacion.getFXML()));
		        Pane pane = loader.load();
		        aCertificacion = loader.getController();
		        aCertificacion.setParametrosPaso(varPaso);
		        
		        this.vbLineas.getChildren().add(pane);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void guardarCertificacion() throws Exception{
		
	}

	@Override
	public void setClaseContenida(Object claseContenida) {
	}

	@Override
	public boolean noEsPopUp() {
		if (!esPopUp) return false;
		else return true;
	}

	@Override
	public String getMetodoRetorno() {
		return metodoRetorno;
	}
	

	
}
