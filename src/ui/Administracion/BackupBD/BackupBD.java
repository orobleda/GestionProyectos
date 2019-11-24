package ui.Administracion.BackupBD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.controlsfx.control.PopOver;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.constantes.CargaInicial;
import model.constantes.FormateadorDatos;
import model.metadatos.TipoDato;
import model.utils.db.ConsultaBD;
import model.utils.db.ReplicaBD;
import ui.Dialogo;
import ui.GestionBotones;
import ui.Tabla;
import ui.Administracion.BackupBD.tables.LineaCopiaBD;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;
import ui.popUps.PopUp;

public class BackupBD  implements ControladorPantalla, PopUp {
	public static final String fxml = "file:src/ui/Administracion/BackupBD/BackupBD.fxml";
	
	PopOver popUp = null;
	
	@FXML
	private AnchorPane anchor;
	
	@FXML
    private TableView<Tableable> tCopiasBD;
	Tabla tablaCopiasBD;

    @FXML
    private ImageView imHacerBackup;
    private GestionBotones gbHacerBackup;
    
    @FXML
    private ImageView imReload;
    private GestionBotones gbReload;

    @FXML
    private ImageView imRestaurar;
    private GestionBotones gbRestaurar;
    
    @FXML
    private VBox vbTabla;
    
    @FXML
    private Label lBDenUso;
    

    @FXML
    private ImageView imBorrar;
    private GestionBotones gbBorrar;
    
    public ReplicaBD rbd = null;
    public String actual = "";
	
	
	@Override
	public void resize(Scene escena) {
		vbTabla.setMaxHeight(escena.getHeight());
		vbTabla.setMaxWidth(escena.getWidth());
	}
	
	public void initialize(){
		gbHacerBackup = new GestionBotones(imHacerBackup, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					ReplicaBD.fuerzaGuardado(null);
					ArrayList<ReplicaBD> lrbd = new ArrayList<ReplicaBD>();
					lrbd.addAll(new ReplicaBD().listado().values());
					Collections.sort(lrbd);
					
					tablaCopiasBD.pintaTabla(TipoDato.toListaObjetos(lrbd));
				} catch (Exception e) {
					Dialogo.error(null, e);
				}
            } }, "Guardar Cambios");
		this.gbHacerBackup.activarBoton();	
		gbReload = new GestionBotones(imReload, "Buscar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					reload();
				} catch (Exception e) {
					Dialogo.error(null, e);
				}
            } }, "Guardar Cambios");
		this.gbReload.activarBoton();
		gbBorrar = new GestionBotones(imBorrar, "Eliminar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					borra();
				} catch (Exception e) {
					Dialogo.error(null, e);
				}
            } }, "Guardar Cambios");
		this.gbBorrar.desActivarBoton();
		gbRestaurar = new GestionBotones(imRestaurar, "Enlazar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					cambioBD();
				} catch (Exception e) {
					Dialogo.error(null, e);
				}
            } }, "Guardar Cambios");
		this.gbRestaurar.desActivarBoton();	
		
		tablaCopiasBD = new Tabla(tCopiasBD,new LineaCopiaBD(),this);
		
		ArrayList<ReplicaBD> lrbd = new ArrayList<ReplicaBD>();
		lrbd.addAll(new ReplicaBD().listado().values());
		Collections.sort(lrbd);
		
		tablaCopiasBD.pintaTabla(TipoDato.toListaObjetos(lrbd));
		
		informaBDActual();
	}
	
	public void borra() throws Exception {
		ReplicaBD.borraBackup(this.rbd);
		
		ArrayList<ReplicaBD> lrbd = new ArrayList<ReplicaBD>();
		lrbd.addAll(new ReplicaBD().listado().values());
		Collections.sort(lrbd);
		
		tablaCopiasBD.pintaTabla(TipoDato.toListaObjetos(lrbd));
		this.gbBorrar.desActivarBoton();
	}
	
	public void reload() throws Exception{
		CargaInicial ci = new CargaInicial();
		ci.reload();
	}
	
	public void cambioBD() throws Exception{
		ReplicaBD.fuerzaGuardado(null);
		
		CargaInicial ci = new CargaInicial();
		ci.loadNuevaBD(this.rbd.nomFichero);
		
		String ruta = ReplicaBD.fuerzaGuardado(FormateadorDatos.formateaDato(new Date(this.rbd.getMilis()),TipoDato.FORMATO_FECHAHORA));
		
		ci = new CargaInicial();
		ci.loadNuevaBD(ruta);
		
		this.actual = ruta;
		
		ArrayList<ReplicaBD> lrbd = new ArrayList<ReplicaBD>();
		lrbd.addAll(new ReplicaBD().listado().values());
		Collections.sort(lrbd);
		tablaCopiasBD.pintaTabla(TipoDato.toListaObjetos(lrbd));
		
		this.gbRestaurar.desActivarBoton();
		informaBDActual();
	}
	
	public void informaBDActual() {
		try {
			ReplicaBD rbd = new ReplicaBD();
			rbd.nomFichero = ConsultaBD.url;
			actual = ConsultaBD.url;
			String bdActual = FormateadorDatos.formateaDato(new Date(rbd.getMilis()),TipoDato.FORMATO_FECHAHORA) + " (" + actual +")";
			
			this.lBDenUso.setText(bdActual);
		} catch (Exception ex) {
			Dialogo.error(null, ex);
		}
	}
	
	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		// TODO Auto-generated method stub
		return fxml;
	}

	@Override
	public String getControlFXML() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		this.rbd = ((LineaCopiaBD) variablesPaso.get("filaDatos")).rbd;
		gbRestaurar.activarBoton();
		
		if (!this.rbd.nomFichero.equals(this.actual)) {
			gbBorrar.activarBoton();
		}
	}

	@Override
	public void setClaseContenida(Object claseContenida) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean noEsPopUp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMetodoRetorno() {
		return "setParametrosPaso";
	}
}
