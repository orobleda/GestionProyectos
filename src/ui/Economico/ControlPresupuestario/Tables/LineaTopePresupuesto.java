package ui.Economico.ControlPresupuestario.Tables;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import model.beans.Concepto;
import model.beans.Estimacion;
import model.beans.EstimacionAnio;
import model.beans.TopeImputacion;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Economico.ControlPresupuestario.ControlPresupuestario;
import ui.Economico.ControlPresupuestario.ModificaTope;
import ui.Economico.ControlPresupuestario.TopeImputaciones;
import ui.interfaces.Tableable;

public class LineaTopePresupuesto extends ParamTable implements Tableable  {
	 
    public Concepto concepto = null;
    public static Concepto conceptoGuarda = null;
    
    public static final String SISTEMA = "Sistema";
    public static final String CONCEPTO = "Concepto";
    public static final String RESTANTE_ANIO = "Restante año";
    public static final String TOTALE = "Total Estimado";
    public static final String TOTALI = "Total Imputado";
    
    public LineaTopePresupuesto(Concepto concepto) {
		this.concepto = concepto;
		LineaTopePresupuesto.conceptoGuarda = concepto;
		
		if (configuracionTabla==null || configuracionTabla.size()==0) {
    		setConfig();
    	}
    }
    
    public LineaTopePresupuesto() {
    	if (configuracionTabla==null || configuracionTabla.size()==0) {
    		setConfig();
    	}
    }
    
    public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	configuracionTabla.put(LineaTopePresupuesto.SISTEMA, new ConfigTabla(LineaTopePresupuesto.SISTEMA, LineaTopePresupuesto.SISTEMA, true,0, false));
    	configuracionTabla.put(LineaTopePresupuesto.CONCEPTO, new ConfigTabla(LineaTopePresupuesto.CONCEPTO, LineaTopePresupuesto.CONCEPTO, true,1, false));
    	configuracionTabla.put(LineaTopePresupuesto.RESTANTE_ANIO, new ConfigTabla(LineaTopePresupuesto.RESTANTE_ANIO, LineaTopePresupuesto.RESTANTE_ANIO, true,2, false));
    	
    	Iterator<EstimacionAnio> itEstimacion = LineaTopePresupuesto.conceptoGuarda.topeEstimacion.iterator();
    	ArrayList<Integer> listaAnios = new ArrayList<Integer>();
    	
    	while (itEstimacion.hasNext()) {
    		EstimacionAnio oEst = itEstimacion.next();
    		listaAnios.add(oEst.anio);
    	}   	
    	
    	Collections.sort(listaAnios);
    	int contador = 3;
    	
    	Iterator<Integer> itAnios = listaAnios.iterator();
    	while (itAnios.hasNext()) {
    		Integer anio = itAnios.next();
    		configuracionTabla.put("Est. "+anio, new ConfigTabla("Est. "+anio, "Est. "+anio, true,contador++));
    	}
    	
    	configuracionTabla.put(LineaTopePresupuesto.TOTALE, new ConfigTabla(LineaTopePresupuesto.TOTALE, LineaTopePresupuesto.TOTALE, true,contador++));
    	
    	itAnios = listaAnios.iterator();
    	while (itAnios.hasNext()) {
    		Integer anio = itAnios.next();
    		configuracionTabla.put("Imp. "+anio, new ConfigTabla("Imp. "+anio, "Imp. "+anio, false,contador++));
    	}
    	
    	configuracionTabla.put(LineaTopePresupuesto.TOTALI, new ConfigTabla(LineaTopePresupuesto.TOTALI, LineaTopePresupuesto.TOTALI, true,contador++));
    	
    	this.controlPantalla =  new ModificaTope(new ParamTable(), "vueltaPopUp");
    }
    
 
    @Override
	public Tableable toTableable(Object o) {
    	try {
			Concepto concepto = (Concepto) o;
			return new LineaTopePresupuesto(concepto);
    	} catch (Exception e){
    		e.printStackTrace();
    		return null;
    	}
	}
    
	public void set(String campo, String valor){
			
	}
	
   	@Override
	public Object muestraSelector() {
		return this;
	}
    
	public String get(String campo) {
		try {
			if (LineaTopePresupuesto.SISTEMA.equals(campo)) {
				try {
					return this.concepto.s.codigo;	
				} catch (Exception e) {}
			} 
			if (LineaTopePresupuesto.RESTANTE_ANIO.equals(campo)) {
				try {
					Calendar c = Calendar.getInstance();
					c.setTime(new Date());
					
					if (this.concepto.s.id == Sistema.getInstanceTotal().id) {
						return "";
					}
					
					Iterator<Estimacion> it = ControlPresupuestario.ap.estimaciones.iterator();
					
					float cantidad = 0;
					
					while (it.hasNext()) {
						Estimacion e = it.next();
						Calendar cAux = Calendar.getInstance();
						cAux.setTime(e.fxInicio);
						
						if (cAux.get(Calendar.YEAR) == c.get(Calendar.YEAR) && e.natCoste.id == this.concepto.tipoConcepto.id && e.sistema.id == this.concepto.s.id)
							cantidad += e.importe;
					}
					
					boolean encontrado = false;
					
					TopeImputacion tp = new TopeImputacion();					
					Iterator<TopeImputacion> listaTopes = tp.listadoTopes(ControlPresupuestario.ap.proyecto).iterator();
					while (listaTopes.hasNext()) {
						TopeImputacion ti = listaTopes.next();
						if (ti.anio == c.get(Calendar.YEAR) && ti.mConcepto.id == this.concepto.tipoConcepto.id && ti.sistema.id == this.concepto.s.id){
							encontrado = true;
							if (ti.porcentaje==0) return "";
							cantidad = ti.porcentaje*ControlPresupuestario.ap.presupuesto.getCosteConcepto(this.concepto.s, this.concepto.tipoConcepto).valor/100-cantidad;
							break;
						}
					}
					
					if (!encontrado) {
						Iterator<EstimacionAnio> itEa = ControlPresupuestario.ap.estimacionAnual.iterator();
						while (itEa.hasNext()) {
							EstimacionAnio ea = itEa.next();
							if (ea.anio == c.get(Calendar.YEAR)) {
								Concepto cAux = ea.totalPorConcepto(this.concepto.s).get(this.concepto.tipoConcepto.codigo);
								cantidad = cAux.valorEstimado-cantidad;
								break;
							}
						}
					}
					
					return FormateadorDatos.formateaDato(cantidad, FormateadorDatos.FORMATO_MONEDA);	
				} catch (Exception e) {}
			}
			if (!LineaTopePresupuesto.CONCEPTO.equals(campo)) {
				if (LineaTopePresupuesto.TOTALE.equals(campo)) {
					try {
						Iterator<EstimacionAnio> it = this.concepto.topeEstimacion.iterator();
						float suma = 0;
						
						while (it.hasNext()) {
							EstimacionAnio eA = it.next();
							suma += eA.cantidad;
						}
						
						return FormateadorDatos.formateaDato(suma, FormateadorDatos.FORMATO_MONEDA);	
					} catch (Exception e) {}
				} 
				if (LineaTopePresupuesto.TOTALI.equals(campo)) {
					try {
						Iterator<EstimacionAnio> it = this.concepto.topeEstimacion.iterator();
						float suma = 0;
						
						while (it.hasNext()) {
							EstimacionAnio eA = it.next();
							suma += eA.cantidadImputada;
						}
						
						return FormateadorDatos.formateaDato(suma, FormateadorDatos.FORMATO_MONEDA);	
					} catch (Exception e) {}
				} 
				else {
					Iterator<EstimacionAnio> it = this.concepto.topeEstimacion.iterator();
					
					String cortada = campo.replaceAll("Imp. ", "");
					cortada = cortada.replaceAll("Est. ", "");
					
					while (it.hasNext()) {
						EstimacionAnio eA = it.next();
						if (eA.anio == new Integer(cortada).intValue()){
							if (campo.contains("Imp. ")) 
								return FormateadorDatos.formateaDato(eA.cantidadImputada, FormateadorDatos.FORMATO_MONEDA);
							else 
								return FormateadorDatos.formateaDato(eA.cantidad, FormateadorDatos.FORMATO_MONEDA);
						}
					}
				}
			}	else {
					return this.concepto.tipoConcepto.codigo;
			}
			return "";
		} catch ( Exception e) {return "";}
	}
	
	@Override
	public ObservableList<Tableable> filtrar(Object valorFiltro, ObservableList<Tableable> listaOriginal){
		try {
			ObservableList<Tableable> dataTable = FXCollections.observableArrayList();
			ArrayList<Object> filtros  = ((ArrayList<Object>) valorFiltro);
			ComboBox<Sistema> cSistema = (ComboBox<Sistema>) filtros.get(0);
			ComboBox<MetaConcepto> cConcepto = (ComboBox<MetaConcepto>) filtros.get(1);
			
			Iterator<Tableable> itLista = listaOriginal.iterator();
			while (itLista.hasNext()) {
				boolean incluir = true;
				LineaTopePresupuesto f = (LineaTopePresupuesto) itLista.next();
				if (cSistema!=null && cSistema.getValue()!=null && !cSistema.getValue().codigo.equals(Sistema.getInstanceTotal().codigo) && !cSistema.getValue().codigo.equals(f.concepto.s.codigo)) {
					incluir = false;
				}
				if (cConcepto!=null && cConcepto.getValue()!=null && !cConcepto.getValue().codigo.equals(MetaConcepto.getTotal().codigo) && !cConcepto.getValue().codigo.equals(f.concepto.tipoConcepto.codigo)) {
					incluir = false;
				}
				if (incluir)
					dataTable.add(f);
			}
			
			return dataTable;
		} catch (Exception e) {
			return listaOriginal;
		}
		
	}       
    	
}
