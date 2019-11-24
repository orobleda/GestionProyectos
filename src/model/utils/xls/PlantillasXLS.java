package model.utils.xls;

import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Node;

import controller.Log;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.metadatos.TipoDato;
import model.utils.xml.LectorXML;

public class PlantillasXLS implements Loadable, Cargable{
	String id = null;
	int cabecera = 0;
	int inicioDatos = 0;
	
	public static String ID = "id";
	public static String POSICION = "pos";
	
	public HashMap<String,ColumnaXLS> columnas=null;
	public HashMap<Integer,ColumnaXLS> columnasporInt=null;
	public static HashMap<String, PlantillasXLS> plantillas = null;
	
	public void load() {
		
		plantillas = new HashMap<String, PlantillasXLS>();
		
		cargaFichero("AccesoXLSFicheros.xml"); 		
	}
	
	private void cargaFichero(String fichero) {
		PlantillasXLS xls = null;
		LectorXML lxml = new LectorXML();
		List<Cargable> list = lxml.cargarXml("plantillas/" + fichero, "plantilla", new PlantillasXLS());
		
		for ( int i = 0; i < list.size(); i++ )
        {
			xls = (PlantillasXLS) list.get(i);
			plantillas.put(xls.id, xls);
         }
	}
	
	public ColumnaXLS getColumna(int col) {
		return this.columnasporInt.get(col);
	}

	@Override
	public Cargable cargar(Object o) {
		Node n = (Node) o;
		
		try {
			for (int i = 0; i<n.getChildNodes().getLength();i++){
				String nodo = n.getChildNodes().item(i).getNodeName();
				
				if ("id".equals(nodo)) {
					this.id = n.getChildNodes().item(i).getTextContent();
				}
				if ("cabecera".equals(nodo)) {
					this.cabecera = (Integer) FormateadorDatos.parseaDato(n.getChildNodes().item(i).getTextContent(),TipoDato.FORMATO_INT);
				}
				
				if ("inicioDatos".equals(nodo)) {
					this.inicioDatos = (Integer) FormateadorDatos.parseaDato(n.getChildNodes().item(i).getTextContent(),TipoDato.FORMATO_INT);
				}
				
				if ("columnas".equals(nodo)) {
					Node n2 =  n.getChildNodes().item(i);
					this.columnas = new HashMap<String,ColumnaXLS>();
					this.columnasporInt = new HashMap<Integer,ColumnaXLS>();
					
					for (int j = 0; j<n2.getChildNodes().getLength();j++){
						if ("columna".equals(n2.getChildNodes().item(j).getNodeName())) {
							ColumnaXLS col = new ColumnaXLS();
							col.id = n2.getChildNodes().item(j).getAttributes().item(1).getNodeValue();
							col.columna = (Integer) FormateadorDatos.parseaDato(n2.getChildNodes().item(j).getAttributes().item(0).getNodeValue(),TipoDato.FORMATO_INT);
							col.tipo =  (Integer) FormateadorDatos.parseaDato(n2.getChildNodes().item(j).getAttributes().item(2).getNodeValue(),TipoDato.FORMATO_INT);
							this.columnas.put(col.id,col);
							this.columnasporInt.put(col.columna,col);
						}					
					}
				}		
			}
		} catch (Exception ex) {
			Log.e(ex);
		}
		return this;
		
	}
	
	@Override
	public HashMap<?, ?> getListado() {
		return PlantillasXLS.plantillas;
	}
	
	@Override
	public int getId() {
		return -1;
	}
	
}


