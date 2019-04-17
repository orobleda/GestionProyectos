package model.utils.xls;

import org.apache.poi.ss.usermodel.Cell;

import model.metadatos.TipoDato;

public class ColumnaXLS {
	String id = null;
	int columna = 0;
	int tipo = 0;
	
	public Object getValorCelda(Cell celda) {
		if (tipo == TipoDato.FORMATO_INT || tipo == TipoDato.FORMATO_REAL) {
			if (celda==null) return new Double(0);
			return celda.getNumericCellValue();
		}
		if (tipo == TipoDato.FORMATO_FECHA) {
			if (celda==null) return null;
			return celda.getDateCellValue();
		}
		if (tipo == TipoDato.FORMATO_BOOLEAN) {
			return celda.getBooleanCellValue();
		}
		if (tipo == TipoDato.FORMATO_TXT) {
			if (celda==null) return "";
			return celda.getStringCellValue();
		}
		
		return null;
	}
}
