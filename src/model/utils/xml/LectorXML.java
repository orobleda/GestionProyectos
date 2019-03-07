package model.utils.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import model.interfaces.Cargable;

public class LectorXML {

	public List<Cargable> cargarXml(String fichero, String nodoPrincipal, Cargable c)
	{
		File xmlFile = new File( fichero );
	    
	    List <Cargable> listaSalida = new ArrayList<Cargable>(); 
	    
	    try
	    {
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(xmlFile);

	        NodeList list = doc.getElementsByTagName(nodoPrincipal);
	        
	        for (int i = 0;i<list.getLength();i++){
	        	c.cargar(list.item(i));
	        	listaSalida.add(c);
	        	c = c.getClass().newInstance();
	        }
	        
	        return listaSalida;
	        
	    }catch ( IOException io ) {
	        io.printStackTrace();
	    }catch ( Exception e ) {
	        e.printStackTrace();
	    }
		return null;
	}
	
}
