package ui.Economico.ControlPresupuestario.EdicionEstImp;

import java.util.ArrayList;
import java.util.Iterator;

import controller.AnalizadorPresupuesto;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import model.beans.Concepto;
import ui.ConfigTabla;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteResumenConcepto;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;
import ui.Economico.ControlPresupuestario.EdicionEstImp.Tables.LineaCosteUsuario;

public class DetalleRecurso implements ControladorPantalla {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/EdicionEstImp/DetalleRecurso.fxml";
	
	public AnalizadorPresupuesto ap = null;
	
    @FXML
    private TableView<Tableable> tbResumen;

    @FXML
    private TableView<Tableable> tbDetalle;
	
	@FXML
	private AnchorPane anchor;
	
   @Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		
		
	}
	
	public void refresca() {
		//tbDetalle.refresh();
		//tbResumen.refresh();
	}
	
	public void pintaConcepto(ArrayList<Concepto> listaConceptos, ArrayList<Float> resumen) {
		
		ArrayList<Object> lista = new ArrayList<Object>();
		
		Iterator<Concepto> itListaConceptos = listaConceptos.iterator();
		
		while (itListaConceptos.hasNext()) {
			Concepto c = itListaConceptos.next();
			lista.add(c);
		}
				
		ObservableList<Tableable> dataTable = (new LineaCosteUsuario()).toListTableable(lista);
		
		Iterator<Tableable> itdataTable = dataTable.iterator();
		while (itdataTable.hasNext()) {
			LineaCosteUsuario lcu = (LineaCosteUsuario) itdataTable.next();
			lcu.resumen = resumen;
		}
		tbDetalle.setItems(dataTable);

		(new LineaCosteUsuario()).fijaColumnas(tbDetalle);
		ConfigTabla.configuraAlto(tbDetalle,lista.size());
		
		ArrayList<Object> listaR = new ArrayList<Object>();
		listaR.add(resumen);
		dataTable = (new LineaCosteResumenConcepto()).toListTableable(listaR);
		tbResumen.setItems(dataTable);

		(new LineaCosteResumenConcepto()).fijaColumnas(tbResumen);
		ConfigTabla.configuraAlto(tbResumen,listaR.size());
	}
	
	
}
