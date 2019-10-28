package model.metadatos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
	public static final int FORMATO_PROVEEDOR = 11;
	public static final int FORMATO_METAJORNADA = 12;
	public static final int FORMATO_NAT_COSTE = 13;
	public static final int FORMATO_RECURSO = 14;
	public static final int FORMATO_TARIFA = 16;
	public static final int FORMATO_COBRO_VCT = 17;
	
	public static final int FORMATO_TIPO_VCT = 1000;
	public static final int FORMATO_ESTADO_IMPUTACION = 1001;
	public static final int FORMATO_TIPO_FOTO = 1002;
	
	public static boolean isEnumerado (int tipo) {
		if (tipo>1000) return true;
		else return false;
	}
	
	public static Class<?> getClassTipo(int tipo) {
		if (tipo == FORMATO_INT) return Integer.class;
		if (tipo == FORMATO_FORMATO_PROYECTO) return MetaFormatoProyecto.class;
		if (tipo == FORMATO_BOOLEAN) return Boolean.class;
		return null;
	}
	
	public static ArrayList<Object> toListaObjetos(ArrayList<?> listado) {
			ArrayList<Object> listaObjetos = new ArrayList<Object>();
		
			Iterator<?> itObjetos = listado.iterator();
			while (itObjetos.hasNext()) {
				Object o = (Object) itObjetos.next(); 
				listaObjetos.add(o);
			}
			
			return listaObjetos;
	}
	
	public static ArrayList<Object> toListaObjetos(Collection<?> listado) {
		ArrayList<Object> listaObjetos = new ArrayList<Object>();
	
		Iterator<?> itObjetos = listado.iterator();
		while (itObjetos.hasNext()) {
			Object o = (Object) itObjetos.next(); 
			listaObjetos.add(o);
		}
		
		return listaObjetos;
}
}
