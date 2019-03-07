package ui.planificacion.Faseado.tables;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import model.beans.Coste;
import model.beans.Proyecto;
import model.beans.Tarifa;
import model.metadatos.Sistema;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Economico.ControlPresupuestario.EdicionEstImp.General;
import ui.interfaces.Tableable;
import ui.planificacion.Faseado.AsignacionFase;
import ui.planificacion.Faseado.GestionFases;

public class DemandasAsociadasTabla extends ParamTable implements Tableable  {
	public Proyecto p = null;
	
	public String demanda = "";
    
 	public static final String DEMANDA = "Demanda";
	    
    public DemandasAsociadasTabla(Proyecto p) {
    	this.p = p;
    	this.demanda = p.toString();
    	
        setConfig();
    }
    
    public DemandasAsociadasTabla() {
        setConfig();
    }
    
    public void setConfig() {
    	int contador = 1;
    	
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	configuracionTabla.put(DemandasAsociadasTabla.DEMANDA, new ConfigTabla( DemandasAsociadasTabla.DEMANDA, DemandasAsociadasTabla.DEMANDA,false, 0, false));
    	
    	if (this.variablesMetaDatos!=null) {
    		@SuppressWarnings("unchecked")
			Iterator<Sistema> lSistemas = ((Collection<Sistema>) this.variablesMetaDatos.get("sistemas")).iterator();
    		
    		while (lSistemas.hasNext()) {
    			Sistema s = lSistemas.next();
    			configuracionTabla.put(s.codigo, new ConfigTabla( s.codigo, s.codigo,true, contador++, false));
    		}
    		
    		this.controlPantalla = new AsignacionFase();    		
    	}
    	
    	anchoColumnas = new HashMap<String, Integer>();
    	anchoColumnas.put(DemandasAsociadasTabla.DEMANDA, new Integer(270));  
    }
    
    @Override
	public Tableable toTableable(Object o) {
    	Proyecto p = (Proyecto) o;
		return new DemandasAsociadasTabla(p);
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
			if (DemandasAsociadasTabla.DEMANDA.equals(campo)) return this.demanda;
			else {
				Iterator<Coste> itCoste = this.p.presupuestoActual.costes.values().iterator();
				while (itCoste.hasNext()) {
					Coste c = itCoste.next();
					if (campo.equals(c.sistema.codigo)) return "X";
				}
				
			}
		} catch ( Exception e) {}
			
		return null;
	}
	
	
}



