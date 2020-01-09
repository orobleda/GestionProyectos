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
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.timeline.Timeline;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class GraficaGantt {
	
	double alto = 0;
	double ancho = 0;
	
	public GanttChart<Fila> gantt = null;

	public GraficaGantt(double ancho, double alto) {
		super();
		this.alto = alto;
		this.ancho = ancho;
	}
	
	public static String getNombreFila(Fila i) {
		return i.toString();
	}

	public class Fila extends Row<Fila, Fila, TareaGantt> {
		public Fila(String name) {
			super(name);
		}
		
		public String toString() {
			return this.getName();
		}
	}

	public GanttChart<?> start(ArrayList<TareaGantt> lTareasAux) {
		gantt = new GanttChart<Fila>(new Fila(
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
	
	public GanttChart<?> start(ArrayList<TareaGantt> lTareasAux, String cabecera, EventHandler<? super MouseEvent> manejadorClick) {
		Fila ini = new Fila(cabecera);
		gantt = new GanttChart<Fila>(new Fila(""));
		gantt.getRoot().getChildren().add(ini);

		Layer layer = new Layer("Tareas");
		gantt.getLayers().add(layer);
		
		Iterator<TareaGantt> lTareas = lTareasAux.iterator();
		while (lTareas.hasNext()) {
			TareaGantt tg = lTareas.next();
			insertaRecursivo(tg, ini, layer);
		}

		Timeline timeline = gantt.getTimeline();
		timeline.showTemporalUnit(ChronoUnit.MONTHS, 12);

		GraphicsBase<Fila> graphics = gantt.getGraphics();
		graphics.setActivityRenderer(TareaGantt.class, GanttLayout.class,new ActivityBarRendererPlani<>(graphics, "Flight Renderer"));
		
		gantt.getTreeTable().setOnMouseClicked(manejadorClick);

		Iterator<Fila> itTi = gantt.getRoot().getChildren().iterator();
		
		while (itTi.hasNext()) {
			Fila t = itTi.next();
			t.setExpanded(true);
		}
		
		gantt.getTreeTable().setShowRoot(false);
		gantt.setPrefSize(ancho, alto);
		
		ListViewGraphics<Fila> graficosVista = gantt.getGraphics();
		
		return gantt;
	}
	
	public void insertaRecursivo(TareaGantt tarea, Fila fEntrada, Layer layer) {
		TareaGantt tg = tarea;
		Fila elemento = new Fila(tg.getName());
		elemento.addActivity(layer, tg);
		fEntrada.getChildren().add(elemento);
		
		Iterator<TareaGantt> itSubtareas = tg.listaTareas.iterator();
		while (itSubtareas.hasNext()) {
			TareaGantt tgAux = itSubtareas.next();
			
			insertaRecursivo(tgAux, elemento, layer);
		}
	}
}
