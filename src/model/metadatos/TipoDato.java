package model.metadatos;

public class TipoDato {
	public static final int FORMATO_TXT = 1;
	public static final int FORMATO_INT = 2;
	public static final int FORMATO_REAL = 3;
	public static final int FORMATO_MONEDA = 4;
	public static final int FORMATO_URL = 5;
	public static final int FORMATO_FECHA = 6;
	public static final int FORMATO_FORMATO_PROYECTO = 7;
	public static final int FORMATO_PORC = 9;
	public static final int FORMATO_TIPO_PROYECTO = 8;
	public static final int FORMATO_BOOLEAN = 10;
	
	public static Class<?> getClassTipo(int tipo) {
		if (tipo == FORMATO_INT) return Integer.class;
		if (tipo == FORMATO_FORMATO_PROYECTO) return MetaFormatoProyecto.class;
		if (tipo == FORMATO_BOOLEAN) return Boolean.class;
		return null;
	}
}
