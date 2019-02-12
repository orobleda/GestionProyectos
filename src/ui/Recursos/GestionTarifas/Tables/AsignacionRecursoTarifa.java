package ui.Recursos.GestionTarifas.Tables;

import java.net.URL;
import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import model.beans.RelRecursoTarifa;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Tableable;
import ui.Recursos.GestionTarifas.InformaAsignacion;

public class AsignacionRecursoTarifa extends ParamTable implements Tableable  {
	
	public static int id = 0; 
	 
    public RelRecursoTarifa relRecursoTarifa = null;
	public int identificador = 0;
    
 	public static final String USUARIO = "cUsuario";
	public static final String TARIFA = "cTarifa";
	public static final String FINICIO = "cFini";
	public static final String FFIN = "cFfin";
    
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
    	configuracionTabla.put(AsignacionRecursoTarifa.FINICIO, new ConfigTabla("Inicio Vigencia",AsignacionRecursoTarifa.FINICIO,  false,2));
    	configuracionTabla.put(AsignacionRecursoTarifa.FFIN,  new ConfigTabla("Fin Vigencia", AsignacionRecursoTarifa.FFIN, false,3));
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
			if (AsignacionRecursoTarifa.FINICIO.equals(campo)) 
				return FormateadorDatos.formateaDato(this.relRecursoTarifa.fechaInicio,FormateadorDatos.FORMATO_FECHA);
			if (AsignacionRecursoTarifa.FFIN.equals(campo)){
				if (Constantes.fechaFinal.compareTo(this.relRecursoTarifa.fechaFin)<1) {
					return "";
				} else {
					return FormateadorDatos.formateaDato(this.relRecursoTarifa.fechaFin,FormateadorDatos.FORMATO_FECHA);
				}				
			} 
		} catch ( Exception e) {}
			
		return null;
	}
	
	
}
