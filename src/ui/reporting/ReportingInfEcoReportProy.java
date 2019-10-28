package ui.reporting;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.beans.Parametro;
import model.beans.Proyecto;
import model.constantes.Constantes;
import model.metadatos.MetaParametro;
import model.metadatos.TipoProyecto;
import model.utils.xls.informes.InformeFoto;
import model.utils.xls.informes.InformeGenerico;
import model.utils.xls.informes.ReporteEconomicoProyectos;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Economico.ControlPresupuestario.ControlPresupuestario;
import ui.interfaces.ControladorPantalla;
import ui.popUps.ConsultaAvanzadaProyectos;

public class ReportingInfEcoReportProy implements ControladorPantalla {

	public static final String fxml = "file:src/ui/reporting/ReportingInfEcoReportProy.fxml"; 
	
	ArrayList<Proyecto> proySeleccionado = null;
	
	@FXML
	private AnchorPane anchor;
	
    @FXML
    private VBox vbCamposEntada;

    @FXML
    private ImageView imBuscar;
    private GestionBotones gbBuscar;

    @FXML
    private ListView<Proyecto> lProyectos;
    
    @FXML
    private ImageView imDescargar;
    private GestionBotones gbDescargar;

    @FXML
    private VBox vbInforme;
	
    @Override
	public void resize(Scene escena) {
		
	}
    
    public void fijaProyecto(ArrayList<Proyecto> listaProyecto) {
		if (listaProyecto!=null) {
			this.proySeleccionado = listaProyecto;
			this.lProyectos.getItems().removeAll(this.lProyectos.getItems());
			this.lProyectos.getItems().addAll(listaProyecto);
			ParamTable.po.hide();
		}
	}
    
    private void consultaAvanzadaProyectos() throws Exception{		
		ConsultaAvanzadaProyectos.getInstance(this, 20, TipoProyecto.ID_PROYEVOLS, this.lProyectos);
	}
    
	public ReportingInfEcoReportProy(){
	}
	
	public void initialize(){
		gbBuscar = new GestionBotones(imBuscar, "BuscarAvzdo3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					consultaAvanzadaProyectos();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Consulta elementos");
		gbBuscar.activarBoton();
		
		gbDescargar = new GestionBotones(imDescargar, "Bajar3R", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					descargarInforme();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Consulta elementos");
		gbDescargar.activarBoton();
    }
	
	public void descargarInforme() throws Exception{
		HashMap<String,Object> valores = new HashMap<String,Object>();
		String nomArchivo = "InfoRepEco" + "_" + Constantes.fechaActual().getTime() + ".xlsx";
		valores.put(InformeGenerico.RUTA, new Parametro().getParametro(MetaParametro.PARAMETRO_RUTA_REPOSITORIO).getValor() + "\\Informes\\" + nomArchivo);
		valores.put(ReporteEconomicoProyectos.LISTA_PROYECTOS, this.lProyectos.getItems());	
		ReporteEconomicoProyectos rP = new ReporteEconomicoProyectos();
		rP.ejecutar(valores);
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


