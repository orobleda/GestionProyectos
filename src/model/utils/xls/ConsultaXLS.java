package model.utils.xls;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ConsultaXLS {

	public static void leeFichero() {
		String nombreArchivo = "Imputaciones+DSI.xlsx";
		String rutaArchivo = nombreArchivo;
		String hoja = "Hoja1";
 
		try (FileInputStream file = new FileInputStream(new File(rutaArchivo))) {

			XSSFWorkbook worbook = new XSSFWorkbook(file);

			XSSFSheet sheet = worbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();
 
			Row row;

			while (rowIterator.hasNext()) {
				row = rowIterator.next();
				
				Iterator<Cell> cellIterator = row.cellIterator();
				Cell cell;
				while (cellIterator.hasNext()) {
					cell = cellIterator.next();
					System.out.print(cell.getStringCellValue()+" | ");
				}
				System.out.println();
			}
			
			worbook.close();
			
		} catch (Exception e) {
			e.getMessage();
		}
		
	}
}
