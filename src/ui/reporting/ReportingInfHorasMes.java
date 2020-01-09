package ui.reporting;

import java.awt.Desktop;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.Parametro;
import model.constantes.Constantes;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaParametro;
import model.utils.xls.informes.InformeGenerico;
import model.utils.xls.informes.ReporteHorasMes;
import ui.Dialogo;
import ui.GestionBotones;
import ui.interfaces.ControladorPantalla;

public class ReportingInfHorasMes implements ControladorPantalla {

	public static final String fxml = "file:src/ui/reporting/ReportingInfHorasMes.fxml"; 
	
	@FXML
	private AnchorPane anchor;
	

    @FXML
    private ComboBox<Integer> cbAnio;

    @FXML
    private ComboBox<String> cbMes;
    
    @FXML
    private ComboBox<MetaConcepto> cbConcepto;

    @FXML
    private ImageView imDescargar;	
	private GestionBotones gbDescargar;
	
    @Override
	public void resize(Scene escena) {
		
	}
    
    public ReportingInfHorasMes(){
	}
	
	public void initialize(){				
		gbDescargar = new GestionBotones(imDescargar, "Bajar3R", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					String nomInforme = descargarInforme();
					
					Label l = new Label();
					l.setText("Abrir Informe");
					
					l.setOnMouseClicked(new EventHandler<MouseEvent>()
			        {
			            @Override
			            public void handle(MouseEvent t)
			            {
			            	try {
				            	File file = new File (nomInforme);
				            	Desktop desktop = Desktop.getDesktop();
				            	desktop.open(file);
			            	} catch (Exception e) {
								Dialogo.error("Error al abrir el informe", e);
			            	}
			            }
			        });
					
					Dialogo.alertContenido("Descarga Correcta", "", "", l);
				} catch (Exception e) {
					e.printStackTrace();
					Dialogo.error("Error al descargar el informe", "Error al descargar el informe", "Se ha producido un error al descargar el informe");
				}
            } }, "Consulta elementos");
		gbDescargar.activarBoton();
		
		Calendar c = Calendar.getInstance();
		c.setTime(Constantes.fechaActual());
		
		for (int i=c.get(Calendar.YEAR)-2; i<c.get(Calendar.YEAR)+2;i++){
			cbAnio.getItems().add(i);
		}
		
		cbAnio.setValue(c.get(Calendar.YEAR));
		
		cbMes.getItems().addAll(Constantes.meses);
		cbMes.setValue(Constantes.nomMes(c.get(Calendar.MONTH)));
		
		cbConcepto.getItems().add(MetaConcepto.porId(MetaConcepto.TREI));
		cbConcepto.getItems().add(MetaConcepto.porId(MetaConcepto.SATAD));
		cbConcepto.setValue(MetaConcepto.porId(MetaConcepto.TREI));
	}
	
	public String descargarInforme() throws Exception{
		HashMap<String,Object> valores = new HashMap<String,Object>();
		String nomArchivo = "InfoRepHorasMes" + "_" + Constantes.fechaActual().getTime() + ".xlsx";

		Parametro pAux = new Parametro();
		String sAux = pAux.getParametroRuta(MetaParametro.PARAMETRO_RUTA_REPOSITORIO);
		
		String nomFichCompleto = sAux + "\\Informes\\" + nomArchivo;
		valores.put(InformeGenerico.RUTA, nomFichCompleto);
		valores.put(ReporteHorasMes.MES, Constantes.numMes(this.cbMes.getValue()));	
		valores.put(ReporteHorasMes.ANIO, cbAnio.getValue());		
		valores.put(ReporteHorasMes.METAC, cbConcepto.getValue());
        
		ReporteHorasMes rP = new ReporteHorasMes();
		rP.ejecutar(valores);
		
		return nomFichCompleto;
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


