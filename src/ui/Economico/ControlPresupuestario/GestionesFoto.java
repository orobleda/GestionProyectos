package ui.Economico.ControlPresupuestario;

import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.Foto;
import model.beans.Parametro;
import model.constantes.Constantes;
import model.metadatos.MetaParametro;
import model.utils.db.ConsultaBD;
import model.utils.xls.informes.InformeFoto;
import model.utils.xls.informes.InformeGenerico;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Economico.ControlPresupuestario.Tables.LineaFoto;
import ui.interfaces.ControladorPantalla;
import ui.popUps.PopUp;

public class GestionesFoto implements ControladorPantalla, PopUp {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/GestionesFoto.fxml"; 
	
	public static Object claseRetorno = null;
	public static String metodoRetorno = null;
	
	public boolean esPopUp = false;
	
    @FXML
    private TextField tNombreFoto;

    @FXML
    private ImageView imRenombrar;
    public GestionBotones gbRenombrar;

    @FXML
    private ImageView imDescargar;
    public GestionBotones gbDescargar;

    @FXML
    private ImageView imComparar;
    public GestionBotones gbComparar;
    
    @FXML
    private ImageView imEliminar;
    public GestionBotones gbEliminar;
    
    @FXML
    private ComboBox<Object> cbFotos;    

    @FXML
    private CheckBox chkPersistir;
    
    public Foto foto = null;
    public VistaFoto vf = null;
	    
	
	@Override
	public AnchorPane getAnchor() {
		return null;
	}
	
	@Override
	public void resize(Scene escena) {
		
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public GestionesFoto (Object claseRetorno, String metodoRetorno){
		GestionesFoto.claseRetorno = claseRetorno;
		GestionesFoto.metodoRetorno = metodoRetorno;
	}
	
	public GestionesFoto (){
	}
	
	public void initialize(){		
			gbRenombrar = new GestionBotones(imRenombrar, "Editar3", false, new EventHandler<MouseEvent>() {        
				@Override
	            public void handle(MouseEvent t)
	            {   
					try {
						updateFoto();
						vf.pintaFotos();
						ParamTable.po.hide();
					} catch (Exception e) {
						Dialogo.error("Fallo al guardar", "Fallo al guardar", "Se produjo un error al actualizar los elementos.");
						e.printStackTrace();						
					}
	            } }, "Actualizar Foto", this);
			gbRenombrar.activarBoton();
			gbEliminar = new GestionBotones(imEliminar, "Eliminar3", false, new EventHandler<MouseEvent>() {        
				@Override
	            public void handle(MouseEvent t)
	            {   
					try {
						borrarFoto();
						vf.pintaFotos();
						ParamTable.po.hide();
					} catch (Exception e) {
						Dialogo.error("Fallo al guardar", "Fallo al guardar", "Se produjo un error al eliminar los elementos.");
						e.printStackTrace();						
					}
	            } }, "Actualizar Foto", this);
			gbEliminar.activarBoton();
			gbDescargar = new GestionBotones(imDescargar, "MoverFase3", false, new EventHandler<MouseEvent>() {        
				@Override
	            public void handle(MouseEvent t)
	            {   
					try {
						descargar();
						ParamTable.po.hide();
					} catch (Exception e) {
						Dialogo.error("Fallo al exportar", "Fallo al exportar", "Se produjo un error al exportar los elementos.");
						e.printStackTrace();						
					}
	            } }, "Exportar Foto", this);
			gbDescargar.activarBoton();
			gbComparar = new GestionBotones(imComparar, "Analizar3", false, new EventHandler<MouseEvent>() {        
				@Override
	            public void handle(MouseEvent t)
	            {   
					try {
						comparar();
						ParamTable.po.hide();
					} catch (Exception e) {
						Dialogo.error("Fallo al exportar", "Fallo al exportar", "Se produjo un error al exportar los elementos.");
						e.printStackTrace();						
					}
	            } }, "Comparar Fotos", this);
			gbComparar.activarBoton();
	}
	
	public void descargar() throws Exception{
		HashMap<String,Object> valores = new HashMap<String,Object>();
		String nomArchivo = "InfoFoto" + (String) (foto.proyecto.getValorParametro(MetaParametro.PROYECTO_ACRONPROY).getValor()) + "_" + Constantes.fechaActual().getTime() + ".xlsx";
		valores.put(InformeGenerico.RUTA, new Parametro().getParametro(MetaParametro.PARAMETRO_RUTA_REPOSITORIO).getValor() + "\\Informes\\" + nomArchivo);
		valores.put(InformeFoto.FOTO, this.foto);
		InformeFoto iF = new InformeFoto();
		iF.ejecutar(valores);
	}
	
	public void comparar() throws Exception{
		if (this.cbFotos.getValue()==null) {
			Dialogo.alert("Falta Foto", "Falta Foto", "Es necesario seleccionar una foto a comparar");
			return;
		}
		
		HashMap<String,Object> valores = new HashMap<String,Object>();
		String nomArchivo = "InfoCompFoto" + (String) foto.proyecto.getValorParametro(MetaParametro.PROYECTO_ACRONPROY).getValor() + "_" + Constantes.fechaActual().getTime() + ".xlsx";
		valores.put(InformeGenerico.RUTA, new Parametro().getParametro(MetaParametro.PARAMETRO_RUTA_REPOSITORIO).getValor() + "\\Informes\\" + nomArchivo);
		valores.put(InformeFoto.FOTO, this.foto);		
		valores.put(InformeFoto.FOTO_COMPARAR, this.cbFotos.getValue());
		InformeFoto iF = new InformeFoto();
		iF.ejecutar(valores);
	}
	
	public void updateFoto() throws Exception{
		foto.nombreFoto = this.tNombreFoto.getText();
		foto.persistir = this.chkPersistir.isSelected();
		
		String idTransaccion = ConsultaBD.getTicket();
		foto.updateFoto(idTransaccion);
		ConsultaBD.ejecutaTicket(idTransaccion);
	}
	
	public void borrarFoto() throws Exception{		
		String idTransaccion = ConsultaBD.getTicket();
		foto.borrarFoto(idTransaccion);
		ConsultaBD.ejecutaTicket(idTransaccion);
	}

	@Override
	public String getControlFXML() {
		return this.getFXML();
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		if (this.tNombreFoto!=null) {
			LineaFoto lf = (LineaFoto) variablesPaso.get("filaDatos");
			vf = (VistaFoto) variablesPaso.get("controladorPantalla");
			this.foto = lf.f;
						
			this.tNombreFoto.setText(lf.f.nombreFoto);
			this.cbFotos.getItems().addAll(vf.tablaListaFotos.listaDatosEnBruto);
			this.chkPersistir.setSelected(foto.persistir);
			
		}		
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
