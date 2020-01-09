package ui.planificacion.Faseado;

import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.flexganttfx.model.ActivityRepository;
import com.flexganttfx.model.repository.IntervalTreeActivityRepository;

import application.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.beans.FaseProyecto;
import model.beans.FaseProyectoSistema;
import model.beans.FaseProyectoSistemaDemanda;
import model.beans.PlanificacionTarea;
import model.beans.Proyecto;
import model.constantes.Constantes;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import ui.VentanaContextual;
import ui.Administracion.Parametricas.GestionParametros;
import ui.charts.GraficaGantt;
import ui.charts.GraficaGantt.Fila;
import ui.charts.TareaGantt;
import ui.interfaces.ControladorPantalla;
import ui.popUps.PopUp;

public class VisorFases  implements ControladorPantalla, PopUp {
	public static final String fxml = "file:src/ui/planificacion/Faseado/VisorFases.fxml";
	public static final String PROYECTO = "PROYECTO";
	public static final String PADRE = "PADRE";
	
	@FXML
	private AnchorPane anchor;
	
	@FXML
    private HBox hbPan;
	
	private Faseado fas = null;
	private Proyecto proyecto = null;
	private GraficaGantt gg = null;
	
	@Override
	public void resize(Scene escena) {
		hbPan.setPrefSize(Main.scene.getWidth()*0.90,Main.scene.getHeight()*0.90);
	}
	
	public void initialize(){
		hbPan.setPrefSize(Main.scene.getWidth()*0.90,Main.scene.getHeight()*0.90);
		gg = new GraficaGantt(Main.scene.getWidth()*0.90,Main.scene.getHeight()*0.90);
		
		
	}
	
	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		proyecto = (Proyecto) variablesPaso.get(VisorFases.PROYECTO);
		fas = (Faseado) variablesPaso.get(VisorFases.PADRE);
		
		ArrayList<TareaGantt> lTareasAux = construyeFases();
		
		hbPan.getChildren().add(gg.start(lTareasAux,proyecto.nombre, evt -> mouseClicked(evt)));
	}
	
	private void mouseClicked(MouseEvent evt) {
		try {
			if ("".equals(evt.getPickResult().getIntersectedNode().getClass().getSimpleName())) return;
			
			TreeItem<Fila> ti = (TreeItem<Fila>)((TreeTableView<Fila>) evt.getSource()).getSelectionModel().getSelectedItem();
			String elementoPulsado = GraficaGantt.getNombreFila((Fila) ti.getParent().getValue()).toString();
			
			if (elementoPulsado.equals(this.proyecto.nombre)) {
				Iterator<FaseProyecto> itFases = this.proyecto.fasesProyecto.iterator();
				FaseProyecto fase = null;
				
				while (itFases.hasNext()) {
					fase = itFases.next();
					if (fase.nombre.equals(ti.getValue().toString())) {
						break;
					}
					else {
						fase = null;
					}
				}
				
				if (fase!=null) {
					GestionParametros c = new GestionParametros();
					FXMLLoader loader = new FXMLLoader();
			        loader.setLocation(new URL(c.getFXML()));
			        			        
			        loader.load();
			        c = loader.getController();
			        
			        HashMap<String, Object> variPaso = new HashMap<String, Object>();
			        variPaso.put("entidadBuscar", FaseProyecto.class.getSimpleName());
			        variPaso.put("subventana", new Boolean(true));
			        variPaso.put("idEntidadBuscar", fase.id);
			        variPaso.put(GestionParametros.PARAMETROS_DIRECTOS+"",GestionParametros.PARAMETROS_DIRECTOS+"");
			        variPaso.put(GestionParametros.LISTA_PARAMETROS+"",fase.parametrosFase);
			        variPaso.put("ancho", new Double(500));
			        variPaso.put("alto", new Double(100));
			        variPaso.put(GestionParametros.NO_FILTROS,GestionParametros.NO_FILTROS);
			        c.setParametrosPaso(variPaso);
			        
			        VentanaContextual vc = new VentanaContextual(c.hbContenedor, new ChangeListener<Boolean>() { 
																			        	public void changed(
																								ObservableValue<? extends Boolean> arg0,
																								Boolean arg1,
																								Boolean arg2) {
																			        		try {
																			        			if (!arg2)
																			        			 repintaTareas();
																			        		} catch (Exception ex) {}
																						} 
																			        }	
			        );
			        vc.show(null);
				}				
			} else {
				elementoPulsado = GraficaGantt.getNombreFila((Fila) ti.getValue()).toString();
				Sistema s = Sistema.getPorNombre(elementoPulsado);
				
				if (s!=null) {
					Iterator<FaseProyecto> itFases = this.proyecto.fasesProyecto.iterator();
					FaseProyecto fase = null;
					FaseProyectoSistema fps = null;
					
					while (itFases.hasNext()) {
						fase = itFases.next();
						
						if (fase.nombre.equals(ti.getParent().getValue().toString())) {
							fps = fase.fasesProyecto.get(s.codigo);
							break;
						}
						else {
							fase = null;
						}
					}
					
					GestionParametros c = new GestionParametros();
					FXMLLoader loader = new FXMLLoader();
			        loader.setLocation(new URL(c.getFXML()));
			        			        
			        loader.load();
			        c = loader.getController();
			        
			        HashMap<String, Object> variPaso = new HashMap<String, Object>();
			        variPaso.put("entidadBuscar", FaseProyectoSistema.class.getSimpleName());
			        variPaso.put("subventana", new Boolean(true));
			        variPaso.put("idEntidadBuscar", fps.id);
			        variPaso.put(GestionParametros.PARAMETROS_DIRECTOS+"",GestionParametros.PARAMETROS_DIRECTOS+"");
			        variPaso.put(GestionParametros.LISTA_PARAMETROS+"",fps.parametrosFaseSistema);
			        variPaso.put("ancho", new Double(500));
			        variPaso.put("alto", new Double(100));
			        variPaso.put(GestionParametros.NO_FILTROS,GestionParametros.NO_FILTROS);
			        c.setParametrosPaso(variPaso);
			        
			        VentanaContextual vc = new VentanaContextual(c.hbContenedor, new ChangeListener<Boolean>() { 
			        	public void changed(
								ObservableValue<? extends Boolean> arg0,
								Boolean arg1,
								Boolean arg2) {
			        		try {
			        			if (!arg2)
				        			 repintaTareas();
			        		} catch (Exception ex) {}
						} 
			        });
			        vc.show(null);
				} else {
					Iterator<FaseProyecto> itFases = this.proyecto.fasesProyecto.iterator();
					FaseProyecto fase = null;
					FaseProyectoSistema fps = null;
					s = Sistema.getPorNombre(GraficaGantt.getNombreFila((Fila) ti.getParent().getValue()).toString());
					FaseProyectoSistemaDemanda fpsd = null;
					
					while (itFases.hasNext()) {
						fase = itFases.next();
						
						if (fase.nombre.equals(ti.getParent().getParent().getValue().toString())) {
							fps = fase.fasesProyecto.get(s.codigo);
							fpsd = fps.getDemandaNombre(elementoPulsado);
							break;
						}
						else {
							fase = null;
						}
					}
					
					GestionParametros c = new GestionParametros();
					FXMLLoader loader = new FXMLLoader();
			        loader.setLocation(new URL(c.getFXML()));
			        			        
			        loader.load();
			        c = loader.getController();
			        
			        HashMap<String, Object> variPaso = new HashMap<String, Object>();
			        variPaso.put("entidadBuscar", FaseProyectoSistemaDemanda.class.getSimpleName());
			        variPaso.put("subventana", new Boolean(true));
			        variPaso.put("idEntidadBuscar", fpsd.id);
			        variPaso.put(GestionParametros.PARAMETROS_DIRECTOS+"",GestionParametros.PARAMETROS_DIRECTOS+"");
			        variPaso.put(GestionParametros.LISTA_PARAMETROS+"",fpsd.parametrosFaseSistemaDemanda);
			        variPaso.put("ancho", new Double(500));
			        variPaso.put("alto", new Double(100));
			        variPaso.put(GestionParametros.NO_FILTROS,GestionParametros.NO_FILTROS);
			        c.setParametrosPaso(variPaso);
			        
			        VentanaContextual vc = new VentanaContextual(c.hbContenedor);
			        vc.show(null);
				}
			}
			
			
			System.out.println(ti.getParent().getValue().toString() + "->" + ti.getValue().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void repintaTareas() {
		Fila root = gg.gantt.getRoot();
		root = root.getChildren().get(0);
		
		Iterator<Fila> itFases = root.getChildren().iterator();
		while (itFases.hasNext()) {
			Fila f = itFases.next();
			ActivityRepository<TareaGantt> ar = f.getRepository();
			Iterator<TareaGantt> itt = ((IntervalTreeActivityRepository<TareaGantt>) ar).getAllActivities().iterator();//.getActivities(gg.gantt.getLayers().get(0), Instant.ofEpochMilli(Constantes.finMes(1, 1900).getTime()), Instant.ofEpochMilli(Constantes.fechaFinal.getTime()), null, null);
			while (itt.hasNext()) {
				TareaGantt tt = itt.next();
				
				Iterator<FaseProyecto> itFase = this.proyecto.fasesProyecto.iterator();
				FaseProyecto fp = null;
				FaseProyecto fpAux = null;
				while (itFase.hasNext()){
					fpAux = itFase.next();
					if (fpAux.nombre.equals(tt.getName())) {
						fp = fpAux;
						break;
					}
				}
					
				if (fp!=null) {
					Date fechaNueva = (Date) fp.parametrosFase.get(MetaParametro.FASE_PROYECTO_FX_IMPLANTACION).getValor();
					Instant it = Instant.ofEpochMilli(fechaNueva.getTime());
					if (!tt.getEndTime().equals(it)) {
						tt.setStartTime(it);
						tt.setEndTime(it);
					}
									
					Iterator<Fila> itFasesSistemas = f.getChildren().iterator();
					
					while (itFasesSistemas.hasNext()) {
						Fila fSistema = itFasesSistemas.next();
						ActivityRepository<TareaGantt> arfSistema = fSistema.getRepository();
						Iterator<TareaGantt> ittFaseSistema = ((IntervalTreeActivityRepository<TareaGantt>) arfSistema).getAllActivities().iterator();
						while (ittFaseSistema.hasNext()) {
							TareaGantt ttFaseSistema = ittFaseSistema.next();
							
							Iterator<FaseProyectoSistema> itFs = fp.fasesProyecto.values().iterator();
							FaseProyectoSistema fps = null;
							FaseProyectoSistema fpsAux = null;
							while (itFs.hasNext()){
								fpsAux = itFs.next();
								if (fpsAux.s.toString().equals(ttFaseSistema.getName())) {
									fps = fpsAux;
									break;
								}
							}
								
							if (fps!=null) {
								Date fechaNuevaFaseSistema = null;
								if (fps.parametrosFaseSistema!= null && 
										fps.parametrosFaseSistema.get(MetaParametro.FASE_PROYECTO_SISTEMA_FX_DIFERIDA)!=null &&
												fps.parametrosFaseSistema.get(MetaParametro.FASE_PROYECTO_SISTEMA_FX_DIFERIDA).getValor()!=null ){
									fechaNuevaFaseSistema = (Date) fps.parametrosFaseSistema.get(MetaParametro.FASE_PROYECTO_SISTEMA_FX_DIFERIDA).getValor();
								} else {
									fechaNuevaFaseSistema = fechaNueva;
								}
								
								Instant itfs = Instant.ofEpochMilli(fechaNuevaFaseSistema.getTime());
								if (!ttFaseSistema.getEndTime().equals(itfs)) {
									ttFaseSistema.setStartTime(itfs);
									ttFaseSistema.setEndTime(itfs);
								}
							}
						}			
					}
				}
			}			
		}
	}
	
	private ArrayList<TareaGantt> construyeFases() {
		ArrayList<TareaGantt> lTareas = new ArrayList<TareaGantt>();
		
		if (this.proyecto.fasesProyecto!=null)  {
			Collections.sort(this.proyecto.fasesProyecto);
			Iterator<FaseProyecto> itFases = this.proyecto.fasesProyecto.iterator();
			
			while (itFases.hasNext()) {
				FaseProyecto fase = itFases.next();
				
				PlanificacionTarea pt = new PlanificacionTarea();
				pt.nombre = fase.nombre;
				pt.fxInicio = (Date) fase.parametrosFase.get(MetaParametro.FASE_PROYECTO_FX_IMPLANTACION).getValor();
				pt.fxFin = (Date) fase.parametrosFase.get(MetaParametro.FASE_PROYECTO_FX_IMPLANTACION).getValor();
				pt.clase = TareaGantt.RESUMEN;
				TareaGantt tg = new TareaGantt(pt);
				lTareas.add(tg);
				
				Iterator<FaseProyectoSistema> itFPS = fase.fasesProyecto.values().iterator();
				while (itFPS.hasNext()) {
					FaseProyectoSistema fPS = itFPS.next();
					
					
					
					PlanificacionTarea pt1 = new PlanificacionTarea();
					pt1.nombre = fPS.s.descripcion;
					
					if (fPS.parametrosFaseSistema!= null && 
							fPS.parametrosFaseSistema.get(MetaParametro.FASE_PROYECTO_SISTEMA_FX_DIFERIDA)!=null &&
							fPS.parametrosFaseSistema.get(MetaParametro.FASE_PROYECTO_SISTEMA_FX_DIFERIDA).getValor()!=null ){
						pt1.fxInicio = (Date) fPS.parametrosFaseSistema.get(MetaParametro.FASE_PROYECTO_SISTEMA_FX_DIFERIDA).getValor();
						pt1.fxFin = (Date) fPS.parametrosFaseSistema.get(MetaParametro.FASE_PROYECTO_SISTEMA_FX_DIFERIDA).getValor();
					} else {
						pt1.fxInicio = (Date) fase.parametrosFase.get(MetaParametro.FASE_PROYECTO_FX_IMPLANTACION).getValor();
						pt1.fxFin = (Date) fase.parametrosFase.get(MetaParametro.FASE_PROYECTO_FX_IMPLANTACION).getValor();
					}					
					pt1.clase = TareaGantt.RESUMEN;
					TareaGantt tg1 = new TareaGantt(pt1);
					
					boolean insertar = false;
					Iterator<FaseProyectoSistemaDemanda> itFPSd = fPS.demandasSistema.iterator();
					while (itFPSd.hasNext()) {
						FaseProyectoSistemaDemanda fPSd = itFPSd.next();
						try {
							Float cobertura = (Float) fPSd.parametrosFaseSistemaDemanda.get(MetaParametro.FASES_COBERTURA_DEMANDA).getValor();
							if (cobertura >0) {
								PlanificacionTarea pt2 = new PlanificacionTarea();
								pt2.nombre = fPSd.p.nombre;
								pt2.fxInicio = (Date) fase.parametrosFase.get(MetaParametro.FASE_PROYECTO_FX_IMPLANTACION).getValor();
								pt2.fxFin = (Date) fase.parametrosFase.get(MetaParametro.FASE_PROYECTO_FX_IMPLANTACION).getValor();				
								pt2.clase = TareaGantt.NODO_HOJA;
								TareaGantt tg2 = new TareaGantt(pt2);
								tg1.listaTareas.add(tg2);
								insertar = true;
							}							
						} catch (Exception e) {
							
						}						
					}
					
					if (insertar) {
						tg.listaTareas.add(tg1);
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

	@Override
	public String getControlFXML() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setClaseContenida(Object claseContenida) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean noEsPopUp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMetodoRetorno() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
