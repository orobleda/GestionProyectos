package ui.PanelPrincipal.Visores;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.beans.Coste;
import model.beans.FaseProyecto;
import model.beans.FaseProyectoSistema;
import model.beans.ParametroFases;
import model.beans.PlanificacionTarea;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.constantes.Constantes;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import ui.charts.GraficaGantt;
import ui.charts.TareaGantt;
import ui.interfaces.ControladorPantalla;

public class VisorPlanificacion  implements ControladorPantalla {
	public static final String fxml = "file:src/ui/PanelPrincipal/Visores/VisorPlanificacion.fxml";
	
	@FXML
	private AnchorPane anchor;
	
	@FXML
    private HBox hbPan;
	
	@Override
	public void resize(Scene escena) {
		hbPan.setPrefSize(Main.scene.getWidth()*0.90,Main.scene.getHeight()*0.90);
	}
	
	public void initialize(){
		hbPan.setPrefSize(Main.scene.getWidth()*0.90,Main.scene.getHeight()*0.90);
		GraficaGantt gg = new GraficaGantt(Main.scene.getWidth()*0.90,Main.scene.getHeight()*0.90);
		
		ArrayList<TareaGantt> lTareasAux = listaProyectos();
		
		hbPan.getChildren().add(gg.start(lTareasAux));
	}
	
	public ArrayList<TareaGantt> listaProyectos() {
		ArrayList<TareaGantt> lTareas = new ArrayList<TareaGantt>();
		
		Proyecto p = new Proyecto();
		
		ArrayList<Proyecto> lProyectos = p.listadoProyectosGGP();
		Collections.sort(lProyectos);
		
		Iterator<Proyecto> itProyecto = lProyectos.iterator();
		while (itProyecto.hasNext()) {
			p = itProyecto.next();
			
			if (! (Boolean) p.getValorParametro(MetaParametro.PROYECTO_CERRADO).getValor()) {
				PlanificacionTarea pt = new PlanificacionTarea();
				pt.nombre = p.nombre;
				pt.fxInicio = (Date) p.getValorParametro(MetaParametro.PROYECTO_FX_INICIO).getValor();
				pt.fxFin = (Date) p.getValorParametro(MetaParametro.PROYECTO_FX_FIN).getValor();
				pt.clase = TareaGantt.RESUMEN;
				TareaGantt tg = new TareaGantt(pt);
				lTareas.add(tg);
				
				Presupuesto pres = new Presupuesto();
				pres = pres.dameUltimaVersionPresupuesto(p);
				pres.cargaCostes();
				p.cargaFasesProyecto();
				
				Iterator<Coste> itCoste = pres.costes.values().iterator();
				while (itCoste.hasNext()) {
					Coste cost = itCoste.next();
					Sistema s = cost.sistema;
					
					Iterator<FaseProyecto> itFases = p.fasesProyecto.iterator();
					while (itFases.hasNext()) {
						FaseProyecto fp = itFases.next();
						
						ParametroFases pf = new ParametroFases();
						Date fFinFase = (Date) pf.dameParametros(fp.getClass().getSimpleName(), fp.id).get(MetaParametro.FASE_PROYECTO_FX_IMPLANTACION).getValor();
						Calendar cActual = Calendar.getInstance();
						cActual.setTime(Constantes.fechaActual());
						cActual.roll(Calendar.DAY_OF_YEAR, -45);;
						Calendar cFinFase = Calendar.getInstance();
						cFinFase.setTime(fFinFase);
						
						if (cFinFase.after(cActual)) {
							Iterator<FaseProyectoSistema> itFps = fp.fasesProyecto.values().iterator();
							while (itFps.hasNext()) {
								FaseProyectoSistema fps = itFps.next();
								if (fps.s.id == s.id) {
									pt = new PlanificacionTarea();
									pt.nombre = s.descripcion;
									pt.fxInicio = fFinFase;
									pt.fxFin = fFinFase;
									pt.clase = TareaGantt.NODO_HOJA;
									TareaGantt tgAux = new TareaGantt(pt);
									tg.listaTareas.add(tgAux);
								}
							}
						}
					}
				}
			}
		}
		
		return lTareas;
	}
	
	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		// TODO Auto-generated method stub
		return fxml;
	}

	
}
