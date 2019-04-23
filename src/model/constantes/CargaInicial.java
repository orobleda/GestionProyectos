package model.constantes;

import model.beans.Proveedor;
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
import model.utils.db.QuerysBD;
import model.utils.xls.ConsultaImputaciones;
import model.utils.xls.PlantillasXLS;

public class CargaInicial {
	
	public final Loadable[] listaInicial = {new QuerysBD(), new PlantillasXLS(), new TipoProyecto(), new EstadoProyecto(), 
											new TransicionEstados(), new TipoCobroVCT(),new Sistema(), new MetaConcepto(), 
											new MetaFormatoProyecto(), new TipoParamProyecto(), new Festivo(), 
											new Proveedor(), new MetaGerencia()	, new MetaParametro(), new TipoEnumerado()											
											};
	
	public void load(){
		FormateadorDatos.cargaAcutes();
		
		ConsultaBD.init("C:\\Users\\Oscar\\workspace\\Gestion Proyectos ENAGAS\\gProyectos.s3db");
		
		ConsultaBD cbd = new ConsultaBD();
		cbd.connect(); 

		for (int i=0; i<listaInicial.length; i++){
			listaInicial[i].load();			
		}
	}
}
