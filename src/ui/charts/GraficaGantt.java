package ui.charts;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.timeline.Timeline;

public class GraficaGantt {
	
	double alto = 0;
	double ancho = 0;

	public GraficaGantt(double ancho, double alto) {
		super();
		this.alto = alto;
		this.ancho = ancho;
	}

	class Fila extends Row<Fila, Fila, TareaGantt> {
		public Fila(String name) {
			super(name);
		}
	}

	public GanttChart<?> start(ArrayList<TareaGantt> lTareasAux) {
		GanttChart<Fila> gantt = new GanttChart<Fila>(new Fila(
				"Proyectos"));

		Layer layer = new Layer("Tareas");
		gantt.getLayers().add(layer);
		
		Iterator<TareaGantt> lTareas = lTareasAux.iterator();
		while (lTareas.hasNext()) {
			TareaGantt tg = lTareas.next();
			Fila proyecto = new Fila(tg.getName());
			gantt.getRoot().getChildren().add(proyecto);
			proyecto.addActivity(layer, tg);
			
			HashMap<String,Fila> tareasProy = new HashMap<String,Fila>();
			
			Iterator<TareaGantt> itSistemas = tg.listaTareas.iterator();
			while (itSistemas.hasNext()) {
				TareaGantt tgAux = itSistemas.next();
				Fila sistema = null;
				if (tareasProy.containsKey(tgAux.getName())){
					sistema = tareasProy.get(tgAux.getName());
				} else{ 
					sistema = new Fila(tgAux.getName());
					tareasProy.put(tgAux.getName(),sistema);
					proyecto.getChildren().add(sistema);
				}
				sistema.addActivity(layer, tgAux);
			}
		}

		Timeline timeline = gantt.getTimeline();
		timeline.showTemporalUnit(ChronoUnit.MONTHS, 12);

		GraphicsBase<Fila> graphics = gantt.getGraphics();
		graphics.setActivityRenderer(TareaGantt.class, GanttLayout.class,new ActivityBarRendererPlani<>(graphics, "Flight Renderer"));
		

		Iterator<Fila> itTi = gantt.getRoot().getChildren().iterator();
		
		while (itTi.hasNext()) {
			Fila t = itTi.next();
			t.setExpanded(true);
		}
		
		gantt.getTreeTable().setShowRoot(false);
		gantt.setPrefSize(ancho, alto);
		
		return gantt;
	}
}
