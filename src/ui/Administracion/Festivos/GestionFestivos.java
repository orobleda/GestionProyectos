package ui.Administracion.Festivos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.controlsfx.control.PopOver;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.metadatos.Festivo;
import ui.ControladorPantalla;
import ui.Dialogo;
import ui.Tableable;
import ui.Administracion.Festivos.Tables.LineaFestivo;

public class GestionFestivos  implements ControladorPantalla {
	public static final String fxml = "file:src/ui/Administracion/Festivos/GestionFestivos.fxml";
	
	PopOver popUp = null;
	
	@FXML
	private AnchorPane anchor;
	
	@FXML
	private TableView<Tableable> tEnero;
	@FXML
	private TableView<Tableable> tFebrero;
	@FXML
	private TableView<Tableable> tMarzo;
	@FXML
	private TableView<Tableable> tAbril;
	@FXML
	private TableView<Tableable> tMayo;
	@FXML
	private TableView<Tableable> tJunio;
	@FXML
	private TableView<Tableable> tJulio;
	@FXML
	private TableView<Tableable> tAgosto;
	@FXML
	private TableView<Tableable> tSeptiembre;
	@FXML
	private TableView<Tableable> tOctubre;
	@FXML
	private TableView<Tableable> tNoviembre;
	@FXML
	private TableView<Tableable> tDiciembre;
	@FXML
	private ComboBox<Integer> cbAnios;
	@FXML
	private ImageView imBuscarAn;
	@FXML
	private ImageView imGuardar;
	
	public void initialize(){
		
		Date d = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		cbAnios.getItems().add(c.get(Calendar.YEAR)-1);
		for (int i=c.get(Calendar.YEAR);i<c.get(Calendar.YEAR)+3;i++){
			cbAnios.getItems().add(i);	
		}
		
		cbAnios.setValue(c.get(Calendar.YEAR));
		
		informaAnio();
		
		imBuscarAn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	informaAnio(); }	});
		imGuardar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	actualizaFestivos(); }	});
	}
	
	public void informaAnio() {
		listaFestivos(tEnero, 1, cbAnios.getValue());
		listaFestivos(tFebrero, 2, cbAnios.getValue());
		listaFestivos(tMarzo, 3, cbAnios.getValue());
		listaFestivos(tAbril, 4, cbAnios.getValue());
		listaFestivos(tMayo, 5, cbAnios.getValue());
		listaFestivos(tJunio, 6, cbAnios.getValue());
		listaFestivos(tJulio, 7, cbAnios.getValue());
		listaFestivos(tAgosto, 8, cbAnios.getValue());
		listaFestivos(tSeptiembre, 9, cbAnios.getValue());
		listaFestivos(tOctubre, 10, cbAnios.getValue());
		listaFestivos(tNoviembre, 11, cbAnios.getValue());
		listaFestivos(tDiciembre, 12, cbAnios.getValue());	
	}
	
	public void listaFestivos(TableView<Tableable> tMes, int mes, int anio) {
		Calendar c = Calendar.getInstance();
		c.set(anio, mes-1, 1);
		LineaFestivo lcp = new LineaFestivo(c.getTime());
		ArrayList<Object> lista = new ArrayList<Object>();
		lista.add(lcp);
		
		Date fecha = lcp.diaFinal();

		while (fecha!=null) {
			lcp = new LineaFestivo(fecha);
			lista.add(lcp);	
			
			fecha = lcp.diaFinal();
		}
		
		ObservableList<Tableable> dataTable = tMes.getItems();
		tMes.getItems().removeAll(tMes.getItems());
		dataTable = lcp.toListTableable(lista);
		tMes.setItems(dataTable);
		tMes.getProperties().put("GestionFestivos", this);
		
		try {
			lcp = new LineaFestivo();
			lcp.limpiarColumnas(tMes);
			lcp.fijaColumnas(tMes);	
		
			tMes.getSelectionModel().setCellSelectionEnabled(true);
			
			tMes.setOnMouseClicked(new EventHandler<MouseEvent>() {
		        @Override
		        public void handle(MouseEvent event) {
		        	diaSeleccionado(tMes, tMes.getSelectionModel().getSelectedCells().get(0).getRow(), tMes.getSelectionModel().getSelectedCells().get(0).getColumn());
		        	;
		        }
		    });
			
			ObservableList<TableColumn<Tableable,?>> columnas = tMes.getColumns(); 
			Iterator<TableColumn<Tableable,?>> itCol = columnas.iterator();
			
			while (itCol.hasNext()) {
				@SuppressWarnings("unchecked")
				TableColumn<Tableable,String> columna = (TableColumn<Tableable,String>) itCol.next();
				columna.setCellValueFactory(
						cellData ->
							new SimpleStringProperty(cellData.getValue().get(
								cellData.getTableColumn().getId())));
				
				columna.setCellFactory(column -> {
				    return new TableCell<Tableable, String>() {
				        protected void updateItem(String item, boolean empty) {
				            //super.updateItem(item, empty);
				        	setText(item);
				        	
				        	if (Festivo.DOMINGO.equals(columna.getId()) || Festivo.SABADO.equals(columna.getId())) {
				        		setStyle("-fx-background-color: Linen");
				        	}
				        	
				        	try {
				        		LineaFestivo lf = (LineaFestivo) this.getTableView().getItems().get(this.getIndex());
				        		Festivo f = lf.semanaFestivos.get(Festivo.orden(columna.getId()));
				        		if (f.esFestivo) {
				        			setStyle("-fx-background-color: Red");
				        		}
				        	} catch (Exception e) {
				        		
				        	}
				        	
				        	
				        }
				    };
				});
			}
			
			tMes.refresh();
		} catch (Exception e) {}
	}
	
	public void diaSeleccionado(TableView<Tableable> tMes, int fila, int columna){
		try {
			LineaFestivo lf = (LineaFestivo) tMes.getItems().get(fila);
			Festivo f = lf.semanaFestivos.get(columna);
			
			if ((f.dia==null) || (columna==5) || (columna==6)) throw new Exception();
			
			if (f.esFestivo) f.esFestivo = false;
			else f.esFestivo = true;
			
			if (f.modificado) f.modificado = false;
			else f.modificado = true;
			
			tMes.refresh();
			
		} catch (Exception e) {
			
		}		
	}
	
	public void actualizaFestivos() {
		try {
			int contador = 0;
			contador += actualizaFestivosMes(tEnero);
			contador += actualizaFestivosMes(tFebrero);
			contador += actualizaFestivosMes(tMarzo);
			contador += actualizaFestivosMes(tAbril);
			contador += actualizaFestivosMes(tMayo);
			contador += actualizaFestivosMes(tJunio);
			contador += actualizaFestivosMes(tJulio);
			contador += actualizaFestivosMes(tAgosto);
			contador += actualizaFestivosMes(tSeptiembre);
			contador += actualizaFestivosMes(tOctubre);
			contador += actualizaFestivosMes(tNoviembre);
			contador += actualizaFestivosMes(tDiciembre);	
			
			new Festivo().load();
			Dialogo.alert("Modificación de Festivos", "Guardado Correcto", "Se actualizaron " + contador + " festivos.");
		} catch (Exception e){
			Dialogo.error("Modificación de Festivos", "Error al guardar", "Se produjo un error al guardar los festivos");
		}
	}
	
	public int actualizaFestivosMes(TableView<Tableable> tTable) {
		ObservableList<Tableable> dataTable = tTable.getItems();
		Iterator <Tableable> itLinFestivos = dataTable.iterator();
		
		int contador = 0;
		
		while (itLinFestivos.hasNext()) {
			LineaFestivo linFestivo = (LineaFestivo) itLinFestivos.next();
			
			Iterator<Festivo> itFestivo = linFestivo.semanaFestivos.iterator();
			
			while (itFestivo.hasNext()) {
				Festivo f = itFestivo.next();
				
				if (f.dia!=null && f.modificado) {
					f.actualizaFestivo();
					f.modificado = false;
					contador++;
				}
			}
			
		}
		
		return contador;
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
}
