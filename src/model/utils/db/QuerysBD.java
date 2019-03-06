package model.utils.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Node;

import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.xml.LectorXML;

public class QuerysBD implements Loadable, Cargable{
	String query = null;
	String tipo = null;
	String codigo = null;
	String tabla = null;
	
	ArrayList<ParametroBD> parametros=null;
	
	public static HashMap<String, QuerysBD> querys = null;
	
	public void load() {
		
		querys = new HashMap<String, QuerysBD>();
		
		cargaFichero("AccesoBD.xml"); 
		querys.get("cConsultaEstados");
		cargaFichero("AccesoBDParametricas.xml");	
		querys.get("cConsultaEstados");
		
	}
	
	private void cargaFichero(String fichero) {
		QuerysBD qbd = null;
		LectorXML lxml = new LectorXML();
		List<Cargable> list = lxml.cargarXml("plantillas/" + fichero, "accesoBD", new QuerysBD());
		
		for ( int i = 0; i < list.size(); i++ )
        {
			qbd = (QuerysBD) list.get(i);
			querys.put(qbd.codigo, qbd);
         }
	}

	@Override
	public Cargable cargar(Object o) {
		Node n = (Node) o;
		
		for (int i = 0; i<n.getChildNodes().getLength();i++){
			String nodo = n.getChildNodes().item(i).getNodeName();
			
			if ("id".equals(nodo)) {
				this.codigo = n.getChildNodes().item(i).getTextContent();
				this.tipo = n.getChildNodes().item(i).getAttributes().item(0).getNodeValue();
			}
			if ("query".equals(nodo)) {
				this.query = n.getChildNodes().item(i).getTextContent();
			}
			
			if ("tabla".equals(nodo)) {
				this.tabla = n.getChildNodes().item(i).getTextContent();
			}
			
			if ("parametros".equals(nodo)) {
				Node n2 =  n.getChildNodes().item(i);
				this.parametros = new ArrayList<ParametroBD>();
				ParametroBD parmBD = null;
				for (int j = 0; j<n2.getChildNodes().getLength();j++){
					if ("parametro".equals(n2.getChildNodes().item(j).getNodeName())) {
						parmBD = new ParametroBD();
						parmBD.id = new Integer(n2.getChildNodes().item(j).getAttributes().item(0).getNodeValue()).intValue();
						parmBD.tipo = n2.getChildNodes().item(j).getAttributes().item(1).getNodeValue();
						parmBD.tabla = this.tabla; 
						this.parametros.add(parmBD);
					}					
				}
			}		
		}
		
		return this;
		
	}
	
	@Override
	public HashMap<?, ?> getListado() {
		return QuerysBD.querys;
	}
	
	@Override
	public int getId() {
		return -1;
	}
	
}


