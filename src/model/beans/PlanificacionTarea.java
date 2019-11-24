package model.beans;

import java.util.Date;

public class PlanificacionTarea {
	public String nombre = "";
	public Date fxInicio = null;
	public Date fxFin = null;
	public String clase = null;
	
	
	public PlanificacionTarea() {
		super();
	}
	
	public PlanificacionTarea(String nombre, Date fxInicio, Date fxFin, String clase) {
		super();
		this.nombre = nombre;
		this.fxInicio = fxInicio;
		this.fxFin = fxFin;
		this.clase = clase;
	}
	
	
}
