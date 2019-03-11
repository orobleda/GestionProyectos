package ui.planificacion.Faseado.tables;

import java.util.HashMap;

import model.beans.FaseProyectoSistemaDemanda;
import model.beans.Tarifa;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;
import ui.popUps.PopUp;

public class DemAsocSistTabla extends ParamTable implements Tableable  {
	public FaseProyectoSistemaDemanda fps = null;
	    
 	public static final String DEMANDA = "Demanda";
 	public static final String PADRE = "padre";
 	
	    
    public DemAsocSistTabla( FaseProyectoSistemaDemanda fps) {
    	this.fps = fps;
    	setConfig();
    }
    
    public DemAsocSistTabla() {
        setConfig();
    }
    
    public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	configuracionTabla.put(DemAsocSistTabla.DEMANDA, new ConfigTabla( DemAsocSistTabla.DEMANDA, DemAsocSistTabla.DEMANDA,true, 0, false));
    	anchoColumnas = new HashMap<String, Integer>();
    	anchoColumnas.put(DemAsocSistTabla.DEMANDA, new Integer(300));  
    	
    	if (this.variablesMetaDatos!=null) {
    		this.controlPantalla = (PopUp) this.variablesMetaDatos.get(DemAsocSistTabla.PADRE);
    		   		
    	}
    }
    
    @Override
	public Tableable toTableable(Object o) {
    	FaseProyectoSistemaDemanda fps = (FaseProyectoSistemaDemanda) o;
		return new DemAsocSistTabla(fps);
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
			if (DemAsocSistTabla.DEMANDA.equals(campo)) return fps.p.nombre;
			
		} catch ( Exception e) {}
			
		return null;
	}
	
	
}



