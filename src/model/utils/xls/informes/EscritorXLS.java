package model.utils.xls.informes;

import java.util.HashMap;

public interface EscritorXLS {
	public void escribeFichero(HashMap<String,Object> datos) throws Exception;
	public void crearArchivo(String rutaArchivo) throws Exception;
	public void cerrarArchivo() throws Exception;
	public void ejecutar(HashMap<String,Object> datos) throws Exception;

}
