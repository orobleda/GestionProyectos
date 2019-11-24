package model.utils.xls.informes;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import controller.Log;

public class InformeGenerico implements EscritorXLS {
	String nombreArchivo = "";
	String rutaArchivo = "";
	String hoja = "";
	File newExcelFile = null;
	
	XSSFWorkbook worbook = null;	

	public static final String RUTA = "RUTA";
	
	@Override
	public void escribeFichero(HashMap<String, Object> datos) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void crearArchivo(String rutaArchivo) throws Exception {
		newExcelFile = new File(rutaArchivo);
        if (!newExcelFile.exists()){
            try {
                newExcelFile.createNewFile();
            } catch (IOException ioe) {
                Log.e("(Error al crear el fichero nuevo)", ioe);
            }
        }
	}

	@Override
	public void cerrarArchivo() throws Exception {
		try {
			worbook.close();
		} catch (Exception e) {
			Log.e(e);
		}
	}
	
	@Override
	public void ejecutar(HashMap<String,Object> datos) throws Exception{
		String ruta = (String) datos.get(InformeGenerico.RUTA);
		crearArchivo(ruta);
		escribeFichero(datos);
		cerrarArchivo();
	}
	
	public static String valorColumna(int idColumna) {
		String[] columnas = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		
		int posicion = idColumna/columnas.length-1;
		String cabecera1 = "";
		if (posicion>=0)
			cabecera1 = columnas[posicion];
		String resto = columnas[idColumna%columnas.length];
		
		return cabecera1+resto;
	}
	

}
