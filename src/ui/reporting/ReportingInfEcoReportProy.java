package ui.reporting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.beans.Foto;
import model.beans.Parametro;
import model.beans.Proyecto;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaParametro;
import model.metadatos.TipoDato;
import model.metadatos.TipoProyecto;
import model.utils.xls.informes.InformeGenerico;
import model.utils.xls.informes.ReporteEconomicoProyectos;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
import ui.interfaces.ControladorPantalla;
import ui.popUps.ConsultaAvanzadaProyectos;

public class ReportingInfEcoReportProy implements ControladorPantalla {

	public static final String fxml = "file:src/ui/reporting/ReportingInfEcoReportProy.fxml"; 
	
	ArrayList<Proyecto> proySeleccionado = null;
	
	@FXML
	private AnchorPane anchor;
	
	@FXML
    private CheckBox chkComparativa;

    @FXML
    private ComboBox<String> cbFotoGLobal;

    @FXML
    private VBox vbCamposEntada;

    @FXML
    private CheckBox chkFotoGlobal;

    @FXML
    private TextField tNomFoto;

    @FXML
    private TextField tFxCarga;

    @FXML
    private CheckBox chkenPPM;
	
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
					Dialogo.alert("Descarga Correcta", "Descarga Correcta", "Se ha descargado el informe correctamente");
				} catch (Exception e) {
					e.printStackTrace();
					Dialogo.error("Error al descargar el informe", "Error al descargar el informe", "Se ha producido un error al descargar el informe");
				}
            } }, "Consulta elementos");
		gbDescargar.activarBoton();
		
		try {
			Foto f = new Foto();
			ArrayList<Foto> lFotos = f.buscaFotos(null);
			Iterator<Foto> itFotos = lFotos.iterator();
			HashMap<String,String> lCodigos = new HashMap<String,String>();
			while (itFotos.hasNext()) {
				Foto fAux = itFotos.next();
				if (fAux.listaParametros.get(MetaParametro.FOTO_COD_PORFOLIO).getValor()!=null) {
					String codigo = (String) fAux.listaParametros.get(MetaParametro.FOTO_COD_PORFOLIO).getValor();
					if (!lCodigos.containsKey(codigo)) lCodigos.put(codigo, codigo);
				}
			}
			this.cbFotoGLobal.getItems().addAll(lCodigos.values());
		} catch (Exception ex) {
			
		}
		
		chkComparativa.setSelected(true);
		this.chkComparativa.selectedProperty().addListener((ov, oldV, newV) -> { 
			if (chkComparativa.isSelected()) {
				cbFotoGLobal.setDisable(false);
			}   else {
				cbFotoGLobal.setDisable(true);
			} 
		});
		
		this.chkenPPM.selectedProperty().addListener((ov, oldV, newV) -> { 
			if (chkenPPM.isSelected()) {
				tFxCarga.setDisable(false);
				try {
					tFxCarga.setText(FormateadorDatos.formateaDato(new Date(), TipoDato.FORMATO_FECHA));
				} catch (Exception e) {
					tFxCarga.setText("");
				}
			}   else {
				tFxCarga.setDisable(true);
				tFxCarga.setText("");
			} 
		});		
		tFxCarga.setDisable(true);
		
		this.chkFotoGlobal.selectedProperty().addListener((ov, oldV, newV) -> { 
			if (chkFotoGlobal.isSelected()) {
				tNomFoto.setDisable(false);
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				String nomFoto = "PORTFOLIO_";
				nomFoto += c.get(Calendar.YEAR) + "_" + (c.get(Calendar.MONTH)+1);
				tNomFoto.setText(nomFoto);
			}   else {
				tNomFoto.setDisable(true);
				tNomFoto.setText("");
			} 
		});
		tNomFoto.setDisable(true);
		
		
		
    }
	
	public void descargarInforme() throws Exception{
		HashMap<String,Object> valores = new HashMap<String,Object>();
		String nomArchivo = "InfoRepEco" + "_" + Constantes.fechaActual().getTime() + ".xlsx";
		valores.put(InformeGenerico.RUTA, new Parametro().getParametro(MetaParametro.PARAMETRO_RUTA_REPOSITORIO).getValor() + "\\Informes\\" + nomArchivo);
		valores.put(ReporteEconomicoProyectos.LISTA_PROYECTOS, this.lProyectos.getItems());	
		
		valores.put(ReporteEconomicoProyectos.HACER_FOTO_GLOBAL, chkFotoGlobal.isSelected());
        valores.put(ReporteEconomicoProyectos.NOMBRE_FOTO_GLOBAL, tNomFoto.getText());
        valores.put(ReporteEconomicoProyectos.EN_PPM,chkenPPM.isSelected());
        try {
        	if (!"".equals(tFxCarga.getText()))
        		valores.put(ReporteEconomicoProyectos.FX_PPM,FormateadorDatos.parseaDato(tFxCarga.getText(), TipoDato.FORMATO_FECHA));
        } catch (Exception e) {
        	valores.put(ReporteEconomicoProyectos.EN_PPM, Constantes.FALSE);
        }
        
        valores.put(ReporteEconomicoProyectos.COMPARAR,chkComparativa.isSelected());
        valores.put(ReporteEconomicoProyectos.FOTO_COMPARAR,cbFotoGLobal.getValue());
		
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


