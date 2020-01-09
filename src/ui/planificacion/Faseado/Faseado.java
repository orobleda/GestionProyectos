package ui.planificacion.Faseado;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.beans.Certificacion;
import model.beans.CertificacionFase;
import model.beans.Coste;
import model.beans.FaseProyecto;
import model.beans.FaseProyectoSistema;
import model.beans.FaseProyectoSistemaDemanda;
import model.beans.Parametro;
import model.beans.PlanificacionTarea;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import model.metadatos.TipoProyecto;
import model.utils.db.ConsultaBD;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Tabla;
import ui.charts.TareaGantt;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;
import ui.planificacion.Faseado.tables.DemandasAsociadasTabla;
import ui.popUps.ConsultaAvanzadaProyectos;

public class Faseado implements ControladorPantalla {
	
	public static final String fxml = "file:src/ui/planificacion/Faseado/Faseado.fxml";
	
	public Proyecto pActual = null;
	
    @FXML
    private VBox vbContenedorFases;

    @FXML
    private TableView<Tableable> tDemandas;
    public Tabla tablaDemandas;

    @FXML
    private TextField tProyecto;
    
    private Proyecto proySeleccionado;

    @FXML
    private ImageView imConsultaAvanzada;
    private GestionBotones gbConsultaAvanzada;

    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;
    

    @FXML
    private ScrollPane scrFases;

	

	@FXML
	private AnchorPane anchor;
	
	@Override
	public void resize(Scene escena) {
		
	}
	
	public void initialize(){
		scrFases.setFitToWidth(true);
		scrFases.setFitToHeight(true);
		
		tablaDemandas = new Tabla(tDemandas,new DemandasAsociadasTabla(),this);
		
		gbConsultaAvanzada = new GestionBotones(imConsultaAvanzada, "BuscarAvzdo3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					consultaAvanzadaProyectos();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Consulta elementos");
		
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					guardaDatos();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Guardar Cambios");
		this.gbGuardar.desActivarBoton();
	}
	

	private void consultaAvanzadaProyectos() throws Exception{		
		ConsultaAvanzadaProyectos.getInstance(this, 1, TipoProyecto.ID_PROYEVOLS, this.tProyecto);
	}
	
	public void fijaProyecto(ArrayList<Proyecto> listaProyecto) throws Exception{
		if (listaProyecto!=null && listaProyecto.size()==1) {
			this.proySeleccionado = listaProyecto.get(0);
			this.tProyecto.setText(this.proySeleccionado.nombre);
			ParamTable.po.hide();
			
			pActual = this.proySeleccionado;
			
			HashMap<String, Sistema> lSistemas = new HashMap<String, Sistema>();
			
			ArrayList<Proyecto> demandas = pActual.getDemandasAsociadas();
			Iterator<Proyecto> itDemanda = demandas.iterator();
			while (itDemanda.hasNext()) {
				Proyecto demanda = itDemanda.next();
				Presupuesto pres = new Presupuesto();
				
				if (demanda.apunteContable) {
					pres.idApunteContable = demanda.id;
					demanda.presupuestoActual = pres.buscaPresupuestosAPunteContable().get(0);
					
				} else {
					demanda.presupuestoActual = pres.dameUltimaVersionPresupuesto(demanda);
				}
				
				demanda.presupuestoActual.cargaCostes();
				
				Iterator<Coste> itCoste = demanda.presupuestoActual.costes.values().iterator();
				while (itCoste.hasNext()) {
					Coste c = itCoste.next();
					lSistemas.put(c.sistema.codigo, c.sistema);
				}
			}
			
			HashMap<String,Object> pasoPrimitiva = new HashMap<String,Object>();
			pasoPrimitiva.put("sistemas", lSistemas.values());
			pasoPrimitiva.put("controlPadre", this);
			
			ArrayList<Object> listaPintable = new ArrayList<Object>();
			listaPintable.addAll(demandas);	
			
			tablaDemandas.setPasoPrimitiva(pasoPrimitiva);
			
			tablaDemandas.pintaTabla(listaPintable);
			
			pActual.cargaFasesProyecto();
			pintaFases() ;		
	
			this.gbGuardar.activarBoton();
		}
	}
	
	public void pintaFases() throws Exception{
		this.vbContenedorFases.getChildren().removeAll(this.vbContenedorFases.getChildren());
		
		HashMap<String, Object> variablesPaso = null;
		
		variablesPaso = new HashMap<String, Object>();
		variablesPaso.put(VisorFases.PROYECTO, this.pActual);
		variablesPaso.put(VisorFases.PADRE, this);
		
		VisorFases asigFaseS = new VisorFases();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(new URL(asigFaseS.getFXML()));
        this.vbContenedorFases.getChildren().add(loader.load());
        asigFaseS = loader.getController();
        asigFaseS.setParametrosPaso(variablesPaso);
		
	}
	
	public void guardaDatos() throws Exception{
		if (!validaDatos()) return;
		
		String idTransaccion = ConsultaBD.getTicket();
		
		ArrayList<Sistema> sistemasSinFase = compruebaCertificaciones();
		
		if (sistemasSinFase.size()>0) {
			String salida = "";
			Iterator<Sistema> itSistemas = sistemasSinFase.iterator();
			while (itSistemas.hasNext()) {
				Sistema s = itSistemas.next();
				salida += s.codigo;
				if (itSistemas.hasNext()) salida += ",";
			}
		
			Dialogo.error("No se puede guardar", "No se puede guardar", "Los sistemas ("+ salida + ") tienen certificaciones asignadas a una fase eliminada");
		} else {
			FaseProyecto fp = new FaseProyecto();
			fp.p = this.pActual;
			this.pActual.fasesProyecto = fp.purgarFases(this.pActual.fasesProyecto);
			
			fp.updateFasesProyecto(pActual.fasesProyecto, idTransaccion);
			
			ConsultaBD.ejecutaTicket(idTransaccion);
			
			Dialogo.alert("Guardado Correcto", "Faseado Almacenado", "El mapa de fases del proyecto y su faseado se guardó correctamente.");
		}		
	}
	
	public ArrayList<Sistema> compruebaCertificaciones() {
		ArrayList<Sistema> salida = new ArrayList<Sistema> ();
		
		Certificacion c = new Certificacion();
		c.p = this.pActual;
		Iterator<Certificacion> iCertis = c.listado().iterator();
		
		while (iCertis.hasNext()) {
			c = iCertis.next();
						
			Iterator<CertificacionFase> icf = c.certificacionesFases.iterator();
			while (icf.hasNext()){
				CertificacionFase cf = icf.next();
				
				Iterator<FaseProyecto> iFases = this.pActual.fasesProyecto.iterator();
				boolean encontrado = false;
				while (iFases.hasNext()) {
					FaseProyecto f = iFases.next();
					if (f.id == cf.idFase) {
						if (f.fasesProyecto.containsKey(c.s.codigo)) {
							FaseProyectoSistema fps = f.fasesProyecto.get(c.s.codigo);
							Iterator<FaseProyectoSistemaDemanda> ifpsd = fps.demandasSistema.iterator();
							while (ifpsd.hasNext()) {
								FaseProyectoSistemaDemanda fpsd = ifpsd.next();
								try {
									Float cobertura = (Float) fpsd.parametrosFaseSistemaDemanda.get(MetaParametro.FASES_COBERTURA_DEMANDA).getValor();
									if (cobertura >0) {
										encontrado = true;
										break;
									}							
								} catch (Exception e) {
									
								}
							}
						}
					}
				}
				
				if (!encontrado) {
					salida.add(c.s);
					break;
				}
			}			
		}
		
		return salida;
	}
	
	public boolean validaDatos() throws Exception {
		ArrayList<Proyecto> demandas = pActual.getDemandasAsociadas();
		Iterator<Proyecto> itDemanda = demandas.iterator();
		while (itDemanda.hasNext()) {
			Proyecto demanda = itDemanda.next();
			Presupuesto pres = new Presupuesto();
			
			if (demanda.apunteContable) {
				pres.idApunteContable = demanda.id;
				demanda.presupuestoActual = pres.buscaPresupuestosAPunteContable().get(0);
				
			} else {
				demanda.presupuestoActual = pres.dameUltimaVersionPresupuesto(demanda);
			}
			
			demanda.presupuestoActual.cargaCostes();
			
			float porc = 0;
			
			if (demanda.presupuestoActual.costes.size()!=0) {
				Iterator<Coste> itCoste = demanda.presupuestoActual.costes.values().iterator();
				while (itCoste.hasNext()) {
					Coste c = itCoste.next();
					porc = pActual.coberturaDemandaFases(demanda, demanda.apunteContable, c.sistema);
					
					if (porc!=100) {
						Dialogo.alert("No se puede guardar", "Demandas imcompletas", "La demanda " + demanda.nombre + " no está totalmente asignada.");
						return false;
					}
				}
			} else {
				porc = 100;
			}
			
			
		}
		
		Iterator<FaseProyecto> itFases = pActual.fasesProyecto.iterator();
		Parametro p = new Parametro();
		
		while (itFases.hasNext()) {
			FaseProyecto fp = itFases.next();
			
			boolean val = p.validaParametros(fp.parametrosFase);
			
			if (!val) {
				Dialogo.alert("No se puede guardar", "Parámetros obligatorios no informados", "Los parámetros obligatorios de la fase " + fp.nombre + " no están todos informados.");
				return false;
			}
			
			Iterator<FaseProyectoSistema> itFPS = fp.fasesProyecto.values().iterator();
			while (itFPS.hasNext()) {
				FaseProyectoSistema fps = itFPS.next();
				
				val = p.validaParametros(fps.parametrosFaseSistema);
				
				if (!val) {
					Dialogo.alert("No se puede guardar", "Parámetros obligatorios no informados", "Los parámetros obligatorios de la fase " + fp.nombre + ", sistema " + fps.s.codigo + " no están todos informados.");
					return false;
				}
				
				Iterator<FaseProyectoSistemaDemanda> itFPSd = fps.demandasSistema.iterator();
				while (itFPSd.hasNext()) {
					FaseProyectoSistemaDemanda fpsd = itFPSd.next();
					
					val = p.validaParametros(fpsd.parametrosFaseSistemaDemanda);
					
					if (!val) {
						Dialogo.alert("No se puede guardar", "Parámetros obligatorios no informados", "Los parámetros obligatorios de la fase " + fp.nombre + ", sistema " + fps.s.codigo + ", demanda "+ fpsd.p.nombre + ", no están todos informados.");
						return false;
					}
					
				}
				
			}
			
		}
		
		return true;
	}
	
		
		@Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		return fxml;
	}

}
