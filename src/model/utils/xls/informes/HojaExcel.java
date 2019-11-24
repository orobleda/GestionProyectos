package model.utils.xls.informes;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class HojaExcel {
	
	public XSSFSheet hoja = null;
	public String nomHoja = null;
	
	public HojaExcel(XSSFWorkbook libro, String nomHoja) {
		hoja = libro.createSheet(nomHoja);
		this.nomHoja = nomHoja;
	}
	
	public XSSFCell get(PosicionExcel pos) {
		return get(pos.fila, pos.columna);
	}
	
	public XSSFCell get(int fila, int columna) {
		XSSFRow rfila = hoja.getRow(fila);
		if (rfila == null) {
			rfila = hoja.createRow(fila);
		}
		XSSFCell rCelda = rfila.getCell(columna);
		if (rCelda == null) {
			return rfila.createCell(columna);
		} else return rCelda;
	}
	
	public XSSFCell offset(XSSFCell celda, int fila, int columna) {
		int filaActual = celda.getRowIndex();
		int colActual = celda.getColumnIndex();
		
		System.out.println(filaActual+","+colActual+","+(fila+filaActual)+","+(columna+colActual));
		
		return get(fila+filaActual, columna+colActual);
	}
	
	public String posicion(XSSFCell celda) {
		return InformeGenerico.valorColumna(celda.getColumnIndex()) + (celda.getRowIndex()+1);
	}
}
