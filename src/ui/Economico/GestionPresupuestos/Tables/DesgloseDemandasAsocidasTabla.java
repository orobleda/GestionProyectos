package ui.Economico.GestionPresupuestos.Tables;

import java.util.HashMap;

import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.beans.Tarifa;
import model.constantes.FormateadorDatos;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;

public class DesgloseDemandasAsocidasTabla extends ParamTable implements Tableable  {
	public Proyecto p = null;
	
	public String demanda = "";
	public String version = "";
	public float estimado = 0;
	public String maxVersion = "";
	public float estimadoMaxVersion = 0;
    
 	public static final String DEMANDA = "Demanda";
	public static final String VERSION = "Versión";
	public static final String PRESTOTAL = "Estimado";
	public static final String MAXVERSION = "Máxima Versión Estimación";
	public static final String PRESMAX = "Estimado Máxima Versión";
	    
    public DesgloseDemandasAsocidasTabla(Proyecto p) {
    	this.p = p;
    	
    	if (p.apunteContable) 
    		this.demanda = "Apunte Contable";
    	else 
    		this.demanda = p.nombre;
    	this.version = p.presupuestoActual.toString();
    	this.estimado = p.presupuestoActual.calculaTotal();
    	
    	Presupuesto pMax = p.presupuestoActual.dameUltimaVersionPresupuesto(p);
    	p.presupuestoMaxVersion = pMax;
    	if (pMax !=null) {
    		this.maxVersion = pMax.toString();
        	this.estimadoMaxVersion = pMax.calculaTotal();
    	} else {
    		this.maxVersion = "";
        	this.estimadoMaxVersion = 0;
    	}
    	
    	
        setConfig();
    }
    
    public DesgloseDemandasAsocidasTabla() {
        setConfig();
    }
    
    public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	configuracionTabla.put(DesgloseDemandasAsocidasTabla.DEMANDA, new ConfigTabla( DesgloseDemandasAsocidasTabla.DEMANDA, DesgloseDemandasAsocidasTabla.DEMANDA,false, 0, false));
    	configuracionTabla.put(DesgloseDemandasAsocidasTabla.VERSION, new ConfigTabla(DesgloseDemandasAsocidasTabla.VERSION, DesgloseDemandasAsocidasTabla.VERSION, false, 2));
    	configuracionTabla.put(DesgloseDemandasAsocidasTabla.PRESTOTAL, new ConfigTabla(DesgloseDemandasAsocidasTabla.PRESTOTAL, DesgloseDemandasAsocidasTabla.PRESTOTAL,  false, 3));
    	configuracionTabla.put(DesgloseDemandasAsocidasTabla.MAXVERSION, new ConfigTabla(DesgloseDemandasAsocidasTabla.MAXVERSION, DesgloseDemandasAsocidasTabla.MAXVERSION, false, 4));
    	configuracionTabla.put(DesgloseDemandasAsocidasTabla.PRESMAX, new ConfigTabla( DesgloseDemandasAsocidasTabla.PRESMAX,DesgloseDemandasAsocidasTabla.PRESMAX, false, 5));
    	

    	anchoColumnas = new HashMap<String, Integer>();
    	anchoColumnas.put(DesgloseDemandasAsocidasTabla.DEMANDA, new Integer(170));
    	anchoColumnas.put(DesgloseDemandasAsocidasTabla.VERSION, new Integer(170));
    	anchoColumnas.put(DesgloseDemandasAsocidasTabla.MAXVERSION, new Integer(170));
    }
    
    @Override
	public Tableable toTableable(Object o) {
    	Proyecto p = (Proyecto) o;
		return new DesgloseDemandasAsocidasTabla(p);
	}
    

	public void set(String campo, String valor){			
	}
	
	public void set(Tarifa t) {
    	
    }
    
	public String get(String campo) {
		try {
			if (DesgloseDemandasAsocidasTabla.DEMANDA.equals(campo)) return this.demanda;
			if (DesgloseDemandasAsocidasTabla.VERSION.equals(campo)) return this.version;
			if (DesgloseDemandasAsocidasTabla.PRESTOTAL.equals(campo)) return FormateadorDatos.formateaDato(this.estimado, FormateadorDatos.FORMATO_MONEDA);
			if (DesgloseDemandasAsocidasTabla.MAXVERSION.equals(campo)) return this.maxVersion;
			if (DesgloseDemandasAsocidasTabla.PRESMAX.equals(campo)) return FormateadorDatos.formateaDato(this.estimadoMaxVersion, FormateadorDatos.FORMATO_MONEDA);
		} catch ( Exception e) {}
			
		return null;
	}
	
	
}



