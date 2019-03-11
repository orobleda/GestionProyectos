package ui.planificacion.Faseado.tables;

import java.util.HashMap;

import model.beans.FaseProyectoSistema;
import model.beans.Tarifa;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;
import ui.popUps.PopUp;

public class SistAsocFaseTabla extends ParamTable implements Tableable  {
	public FaseProyectoSistema fps = null;
	    
 	public static final String SISTEMA = "Sistema";
 	public static final String PADRE = "padre";
 	
	    
    public SistAsocFaseTabla( FaseProyectoSistema fps) {
    	this.fps = fps;
    	setConfig();
    }
    
    public SistAsocFaseTabla() {
        setConfig();
    }
    
    public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	configuracionTabla.put(SistAsocFaseTabla.SISTEMA, new ConfigTabla( SistAsocFaseTabla.SISTEMA, SistAsocFaseTabla.SISTEMA,true, 0, false));
    	anchoColumnas = new HashMap<String, Integer>();
    	anchoColumnas.put(SistAsocFaseTabla.SISTEMA, new Integer(100));  
    	
    	if (this.variablesMetaDatos!=null) {
    		this.controlPantalla = (PopUp) this.variablesMetaDatos.get(SistAsocFaseTabla.PADRE);
    		   		
    	}
    }
    
    @Override
	public Tableable toTableable(Object o) {
    	FaseProyectoSistema fps = (FaseProyectoSistema) o;
		return new SistAsocFaseTabla(fps);
	}
    

	public void set(String campo, String valor){			
	}
	
	public void set(Tarifa t) {
    	
    }
	
	@Override
	public Object muestraSelector() {
		return this;
	}
    
	public String get(String campo) {
		try {
			if (SistAsocFaseTabla.SISTEMA.equals(campo)) return fps.s.codigo;
			
		} catch ( Exception e) {}
			
		return null;
	}
	
	
}



