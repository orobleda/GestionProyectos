package model.utils.xls.informes;

import java.util.HashMap;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.metadatos.TipoDato;

public class EstiloCelda {
	
	public static XSSFWorkbook worbook = null;
	public static HashMap<String,XSSFCellStyle> estilos = null;
	
	public static final int IZQUIERDA = 0;
	public static final int CENTRO = 1;
	public static final int DERECHA = 2;
	public static final int ARRIBA = 3;
	public static final int ABAJO = 4;
	
	public static final int AZUL = 0;
	public static final int BLANCO = 1;
	public static final int GRIS = 2;
	public static final int VERDE = 3;
	public static final int NARANJA = 4;
	public static final int MARRÓN = 5;
	
	private static java.awt.Color TONO_AZUL = new java.awt.Color(211,213,241);
	private static java.awt.Color TONO_GRIS = new java.awt.Color(197,197,201);
	private static java.awt.Color TONO_VERDE = new java.awt.Color(216,241,198);
	private static java.awt.Color TONO_NARANJA = new java.awt.Color(240,174,52);
	private static java.awt.Color TONO_MARRÓN = new java.awt.Color(181,120,41);

	public EstiloCelda(XSSFWorkbook worbook) {
		EstiloCelda.worbook = worbook;
		EstiloCelda.estilos = new HashMap<String,XSSFCellStyle>();
	}
	
	public static String dameKey(int tipoDato,int color,int intensidad, int hAlineacion, int valineacion) {
		return color+ "/" +tipoDato + "/" + intensidad+ "/" +hAlineacion + "/" + valineacion;
	}
	
	public static java.awt.Color intensidad(int intensidad, int color) {
		TONO_AZUL = new java.awt.Color(211,213,241);
		TONO_GRIS = new java.awt.Color(197,197,201);
		TONO_VERDE = new java.awt.Color(216,241,198);
		TONO_NARANJA = new java.awt.Color(240,222,189);
		TONO_MARRÓN = new java.awt.Color(238,217,191);
		
		if (color == EstiloCelda.AZUL)
			return new java.awt.Color(EstiloCelda.TONO_AZUL.getRed()-20*intensidad,EstiloCelda.TONO_AZUL.getGreen()-20*intensidad,EstiloCelda.TONO_AZUL.getBlue());
		if (color == EstiloCelda.GRIS)
			return new java.awt.Color(EstiloCelda.TONO_GRIS.getRed()-20*intensidad,EstiloCelda.TONO_GRIS.getGreen()-20*intensidad,EstiloCelda.TONO_GRIS.getBlue()-20*intensidad);
		if (color == EstiloCelda.VERDE)
			return new java.awt.Color(EstiloCelda.TONO_VERDE.getRed()-20*intensidad,EstiloCelda.TONO_VERDE.getGreen(),EstiloCelda.TONO_VERDE.getBlue()+10*intensidad);
		if (color == EstiloCelda.NARANJA)
			return new java.awt.Color(EstiloCelda.TONO_NARANJA.getRed(),EstiloCelda.TONO_NARANJA.getGreen()-10*intensidad,EstiloCelda.TONO_NARANJA.getBlue()+10*intensidad);
		if (color == EstiloCelda.MARRÓN)
			return new java.awt.Color(EstiloCelda.TONO_MARRÓN.getRed(),EstiloCelda.TONO_MARRÓN.getGreen()-5*intensidad,EstiloCelda.TONO_MARRÓN.getBlue()-10*intensidad);
		
		return new java.awt.Color(255,255,255);
	}
	
	public static XSSFCellStyle alineacionVertical(int alineacion, XSSFCellStyle estilo) {
		if (alineacion==EstiloCelda.ARRIBA) estilo.setVerticalAlignment(VerticalAlignment.TOP);
		if (alineacion==EstiloCelda.CENTRO) estilo.setVerticalAlignment(VerticalAlignment.CENTER);
		if (alineacion==EstiloCelda.ABAJO) estilo.setVerticalAlignment(VerticalAlignment.BOTTOM);
		
		return estilo;
	}
	
	public static XSSFCellStyle alineacionHorizontal(int alineacion, XSSFCellStyle estilo) {
		if (alineacion==EstiloCelda.IZQUIERDA) estilo.setAlignment(HorizontalAlignment.LEFT);
		if (alineacion==EstiloCelda.CENTRO) estilo.setAlignment(HorizontalAlignment.CENTER);
		if (alineacion==EstiloCelda.DERECHA) estilo.setAlignment(HorizontalAlignment.RIGHT);
		
		return estilo;
	}
	
	
	
	public static XSSFCellStyle getEstiloCelda(Integer intensidad, Integer color , Integer tipoDato) {
		return getEstiloCelda(intensidad, color , tipoDato, EstiloCelda.IZQUIERDA, EstiloCelda.IZQUIERDA);
	}
	
	public static XSSFCellStyle getEstiloCelda(Integer intensidad, Integer color , Integer tipoDato,Integer halineacion,Integer valineacion) {
		XSSFCellStyle style = null;
		
		String key = EstiloCelda.dameKey(tipoDato, color, intensidad,halineacion,valineacion);
		
		if (EstiloCelda.estilos.containsKey(key)){
			return EstiloCelda.estilos.get(key);
		}
		
		style = worbook.createCellStyle();
		
		IndexedColorMap colorMap = worbook.getStylesSource().getIndexedColors();

		XSSFColor colorXSXS = new XSSFColor(intensidad(intensidad,color), colorMap);
	            		
		style.setFillForegroundColor(colorXSXS);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		EstiloCelda.alineacionVertical(valineacion, style);
		EstiloCelda.alineacionHorizontal(halineacion, style);
		
		EstiloCelda.estilos.put(key,style);
		
		if (tipoDato == TipoDato.FORMATO_MONEDA) {
			XSSFDataFormat cf = worbook.createDataFormat();
			//style.setDataFormat(cf.getFormat("_-* #.##0,00 €_-;-* #.##0,00 €_-;_-* \"-\"?? €_-;_-@_-"));
			//style.setDataFormat(cf.getFormat("#,#0.00 €"));
			style.setDataFormat((short)8);
		}	
		
		if (tipoDato == TipoDato.FORMATO_FECHA) {
			XSSFDataFormat cf = worbook.createDataFormat();
			style.setDataFormat(cf.getFormat("dd/mm/yyyy"));
			//style.setDataFormat();
		}
		
		return style;
	}

}
