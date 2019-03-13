package ui.planificacion.Faseado.tables;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import model.beans.Coste;
import model.beans.FaseProyecto;
import model.beans.Proyecto;
import model.beans.Tarifa;
import model.constantes.Constantes;
import model.metadatos.Sistema;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Tabla;
import ui.Economico.ControlPresupuestario.EdicionEstImp.General;
import ui.Economico.EstimacionesInternas.tables.LineaCosteProyectoEstimacion;
import ui.interfaces.Tableable;
import ui.planificacion.Faseado.AsignacionFase;
import ui.planificacion.Faseado.Faseado;

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
    	anchoColumnas.put(DemandasAsociadasTabla.DEMANDA, new Integer(470));  
    }
    
    @Override
	public Tableable toTableable(Object o) {
    	Proyecto p = (Proyecto) o;
		return new DemandasAsociadasTabla(p);
	}
    
	@Override
	public String resaltar(int fila, String columna, Tabla tabla) {
		DemandasAsociadasTabla dat = (DemandasAsociadasTabla) tabla.listaDatosFiltrada.get(fila);
		
		Faseado f = (Faseado) tabla.componenteTabla.getProperties().get("controlador");
		Proyecto pActual = f.pActual;
		
		Sistema s = Sistema.get(columna);
		
		if (s!=null) {
			if (!"X".equals(dat.get(columna))) return null;
			
			float cob = pActual.coberturaDemandaFases(dat.p, dat.p.apunteContable, s);
			
			if (cob!=100)
				return "-fx-background-color: " + Constantes.COLOR_AMARILLO;
			else
				return "-fx-background-color: " + Constantes.COLOR_VERDE;
		}
		
		return null;
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



