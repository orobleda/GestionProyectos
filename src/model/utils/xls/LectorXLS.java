package model.utils.xls;

import java.util.ArrayList;
import java.util.HashMap;

public interface LectorXLS {
	public ArrayList<HashMap<String,Object>> leeFichero() throws Exception;
	public void abrirArchivo(String rutaArchivo, int numHoja) throws Exception;
	public void cerrarArchivo() throws Exception;

}
