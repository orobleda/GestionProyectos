package model.utils.xls;

import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Node;

import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.xml.LectorXML;

public class PlantillasXLS implements Loadable, Cargable{
	String id = null;
	String cabecera = null;
	String inicioDatos = null;
	
	public static String ID = "id";
	public static String POSICION = "pos";
	
	HashMap<String,HashMap<String,String>> columnas=null;
	
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

	@Override
	public Cargable cargar(Object o) {
		Node n = (Node) o;
		
		for (int i = 0; i<n.getChildNodes().getLength();i++){
			String nodo = n.getChildNodes().item(i).getNodeName();
			
			if ("id".equals(nodo)) {
				this.id = n.getChildNodes().item(i).getTextContent();
			}
			if ("cabecera".equals(nodo)) {
				this.cabecera = n.getChildNodes().item(i).getTextContent();
			}
			
			if ("inicioDatos".equals(nodo)) {
				this.inicioDatos = n.getChildNodes().item(i).getTextContent();
			}
			
			if ("columnas".equals(nodo)) {
				Node n2 =  n.getChildNodes().item(i);
				this.columnas = new HashMap<String,HashMap<String,String>>();
				HashMap<String,String> columna = null;
				for (int j = 0; j<n2.getChildNodes().getLength();j++){
					if ("columna".equals(n2.getChildNodes().item(j).getNodeName())) {
						columna = new HashMap<String,String>();
						columna.put(PlantillasXLS.ID, n2.getChildNodes().item(j).getAttributes().item(0).getNodeValue());
						columna.put(PlantillasXLS.POSICION, n2.getChildNodes().item(j).getAttributes().item(1).getNodeValue());
						this.columnas.put(columna.get(PlantillasXLS.ID),columna);
					}					
				}
			}		
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


