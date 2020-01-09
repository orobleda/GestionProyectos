package ui.charts;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

import com.flexganttfx.model.activity.MutableActivityBase;

import model.beans.PlanificacionTarea;

public class TareaGantt extends MutableActivityBase<PlanificacionTarea> {
	public static final String RESUMEN = "RESUMEN";
	public static final String NODO_HOJA = "NODO_HOJA";
	
	String nombre;
	Instant fxInicio = Instant.now();
	Instant fxFin = Instant.now().plus(Duration.ofHours(6));
	HashMap<String,String> clase = new HashMap<String,String>();
	
	public ArrayList<TareaGantt> listaTareas = null;
	
	public TareaGantt(PlanificacionTarea pt) {
		setUserObject(pt);
		setName(pt.nombre);
		setStartTime(Instant.ofEpochMilli(pt.fxInicio.getTime()));
		setEndTime(Instant.ofEpochMilli(pt.fxFin.getTime()));
		listaTareas = new ArrayList<TareaGantt>();
		clase.put(pt.clase,pt.clase);		
	}
}
