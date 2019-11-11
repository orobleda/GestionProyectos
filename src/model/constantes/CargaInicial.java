package model.constantes;

import java.io.FileReader;
import java.util.Properties;

import model.beans.Proveedor;
import model.beans.Proyecto;
import model.beans.Recurso;
import model.interfaces.Loadable;
import model.metadatos.EstadoProyecto;
import model.metadatos.Festivo;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaFormatoProyecto;
import model.metadatos.MetaGerencia;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import model.metadatos.TipoCobroVCT;
import model.metadatos.TipoEnumerado;
import model.metadatos.TipoParamProyecto;
import model.metadatos.TipoProyecto;
import model.metadatos.TransicionEstados;
import model.utils.db.ConsultaBD;
import model.utils.db.ConsultaBDReplica;
import model.utils.db.QuerysBD;
import model.utils.xls.PlantillasXLS;

public class CargaInicial {
	
	public static Properties prop = null;
	
	public final Loadable[] listaInicial = {new QuerysBD(), new PlantillasXLS(), new MetaGerencia(), new TipoProyecto(), new EstadoProyecto(), 
											new TransicionEstados(), new TipoCobroVCT(),new Sistema(), new MetaConcepto(), 
											new MetaFormatoProyecto(), new TipoParamProyecto(), new Festivo(), 
											new Proveedor()	, new MetaParametro(), new TipoEnumerado()											
											};
	
	public void load(){
		FormateadorDatos.cargaAcutes();
		
		try {
			prop = new Properties();
			prop.load(new FileReader("plantillas/log4j.properties"));
			
			ConsultaBD.init(prop.getProperty("BD.URL"));
			ConsultaBDReplica.init(prop.getProperty("BD.REPLICAS.URL"));
		} catch (Exception e){
			e.printStackTrace();
		}
		
		ConsultaBD cbd = new ConsultaBD();
		cbd.connect(); 

		for (int i=0; i<listaInicial.length; i++){
			listaInicial[i].load();			
		}
	}
	
	public void loadNuevaBD(String urlBD){
		FormateadorDatos.cargaAcutes();
		
		try {
			prop = new Properties();
			prop.load(new FileReader("plantillas/log4j.properties"));
			
			ConsultaBD.init(urlBD);
			ConsultaBDReplica.init(prop.getProperty("BD.REPLICAS.URL"));
		} catch (Exception e){
			e.printStackTrace();
		}
		
		ConsultaBD cbd = new ConsultaBD();
		cbd.connect(); 

		for (int i=0; i<listaInicial.length; i++){
			listaInicial[i].load();			
		}
		
		Recurso.listadoRecursosEstatico(true);
		Proyecto.listaProyecto = null;		
	}
	
	public void reload(){
		FormateadorDatos.cargaAcutes();
		
		for (int i=0; i<listaInicial.length; i++){
			listaInicial[i].load();			
		}
		
		Recurso.listadoRecursosEstatico(true);
		Proyecto.listaProyecto = null;		
	}
}
