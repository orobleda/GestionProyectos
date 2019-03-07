package ui.interfaces;

import java.util.HashMap;
import java.util.List;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import ui.ConfigTabla;
import ui.Tabla;

public interface Tableable {
	public void fijaColumnas(TableView<Tableable> tParametros);
	public Tableable toTableable(Object o);
	public ObservableList<Tableable> toListTableable(List<Object> c);
	public Object muestraSelector();
	public String get(String campo);
	public void set(String campo, String valor);
	public HashMap<String, ConfigTabla> getConfigTabla();
	public AnchorPane getFilaEmbebida(TableRowDataFeatures<Tableable> expander);
	public ObservableList<Tableable> filtrar(Object valorFiltro, ObservableList<Tableable> listaOriginal);
	public String resaltar(int fila, String columna, Tabla tabla);
	public HashMap<String,Integer> getAnchoColumnas();
	public void limpiarColumnas(TableView<Tableable> tParametros);
	public void fijaMetaDatos(HashMap<String,Object> variablesPaso);
	public void setConfig();
}