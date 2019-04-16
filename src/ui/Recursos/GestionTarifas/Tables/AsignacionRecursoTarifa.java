package ui.Recursos.GestionTarifas.Tables;

import java.net.URL;
import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import model.beans.RelRecursoTarifa;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Recursos.GestionTarifas.InformaAsignacion;
import ui.interfaces.Tableable;

public class AsignacionRecursoTarifa extends ParamTable implements Tableable  {
	
	public static int id = 0; 
	 
    public RelRecursoTarifa relRecursoTarifa = null;
	public int identificador = 0;
    
 	public static final String USUARIO = "cUsuario";
	public static final String TARIFA = "cTarifa";
	public static final String MES = "cMes";
	public static final String ANIO = "cAnio";
    
    public AsignacionRecursoTarifa(RelRecursoTarifa relRecursoTarifa) {
    	identificador = AsignacionRecursoTarifa.id;
    	AsignacionRecursoTarifa.id++;
    	
        this.relRecursoTarifa = relRecursoTarifa;
        
        setConfig();
    }
    
    public AsignacionRecursoTarifa() {
    	identificador = AsignacionRecursoTarifa.id;
    	AsignacionRecursoTarifa.id++;
    	
        this.relRecursoTarifa = null;
        setConfig();
    }
    
    public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	//configuracionTabla.put(AsignacionRecursoTarifa.USUARIO, new ConfigTabla("Recurso", AsignacionRecursoTarifa.USUARIO , false,0,true));
    	configuracionTabla.put(AsignacionRecursoTarifa.TARIFA, new ConfigTabla("Tarifa",AsignacionRecursoTarifa.TARIFA,  false,1, true));
    	configuracionTabla.put(AsignacionRecursoTarifa.MES, new ConfigTabla("Inicio Vigencia",AsignacionRecursoTarifa.MES,  false,2));
    	configuracionTabla.put(AsignacionRecursoTarifa.ANIO,  new ConfigTabla("Fin Vigencia", AsignacionRecursoTarifa.ANIO, false,3));
    }
    
	@Override
	public Object muestraSelector() {
		return null;
	}
	
	@Override
	public AnchorPane getFilaEmbebida(TableRowDataFeatures<Tableable> expander) {
		try {
			FXMLLoader loader = new FXMLLoader();
			InformaAsignacion infAsignacion = new InformaAsignacion(expander);
	        loader.setLocation(new URL(infAsignacion.getFXML()));
	        
	        return (AnchorPane) loader.load();
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
    
    @Override
	public Tableable toTableable(Object o) {
    	RelRecursoTarifa art = (RelRecursoTarifa) o;
		return new AsignacionRecursoTarifa(art);
	}
    

	public void set(String campo, String valor){
	}
    
	public String get(String campo) {
		try {
			if (AsignacionRecursoTarifa.USUARIO.equals(campo)) return this.relRecursoTarifa.recurso.toString();
			if (AsignacionRecursoTarifa.TARIFA.equals(campo)) return this.relRecursoTarifa.tarifa.toString();
			if (AsignacionRecursoTarifa.MES.equals(campo)) 
				return new Integer(this.relRecursoTarifa.mes).toString();
			if (AsignacionRecursoTarifa.ANIO.equals(campo)){
				return new Integer(this.relRecursoTarifa.anio).toString();				
			} 
		} catch ( Exception e) {}
			
		return null;
	}
	
	
}
