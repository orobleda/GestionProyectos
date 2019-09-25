package model.utils.xls;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ConsultaImputaciones implements LectorXLS {
	String nombreArchivo = "Imputaciones+DSI.xlsx";
	String rutaArchivo = nombreArchivo;
	String hoja = "Hoja1";
	
	XSSFWorkbook worbook = null;
	XSSFSheet sheet = null;
	
	public void abrirArchivo(String rutaArchivo, int numHoja) throws Exception {
		FileInputStream file = null;
		file = new FileInputStream(new File(rutaArchivo));
		ZipSecureFile.setMinInflateRatio(0);
		if (file!=null) {

			worbook = new XSSFWorkbook(file);

			sheet = worbook.getSheetAt(numHoja);
		}
	}
	
	public void cerrarArchivo() throws Exception {
		try {
			worbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public ArrayList<HashMap<String,Object>> leeFichero() throws Exception{
		
		if (worbook==null) return null;
		
		PlantillasXLS plant = PlantillasXLS.plantillas.get("cargaImputaciones");
		
		ArrayList<HashMap<String,Object>> salida = new ArrayList<HashMap<String,Object>>();
		
		Iterator<Row> rowIterator = sheet.iterator();
 
		Row row;
		int cont = 0;

		while (rowIterator.hasNext()) {
				row = rowIterator.next();
								
				if (cont >= plant.inicioDatos) {
					Cell cell;
					
					HashMap<String,Object> imputacion = new HashMap<String,Object>();
					Iterator<ColumnaXLS> itColumnas = plant.columnas.values().iterator();
					while (itColumnas.hasNext()) {
						ColumnaXLS colPlantilla = itColumnas.next();
						
						cell = row.getCell(colPlantilla.columna);
					
						Object leido = colPlantilla.getValorCelda(cell);
						imputacion.put(colPlantilla.id, leido);	
						System.out.println(colPlantilla.id + " - " + leido);
					}

					salida.add(imputacion);
				}
				cont++;
		}
		
		return salida;
	}
}
