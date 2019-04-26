package ui.Economico.CargaImputaciones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.Estimacion;
import model.beans.Imputacion;
import model.beans.Proyecto;
import model.metadatos.TipoDato;
import ui.GestionBotones;
import ui.Semaforo;
import ui.Tabla;
import ui.Economico.CargaImputaciones.Tables.LineaEstadoProyectos;
import ui.Economico.CargaImputaciones.Tables.LineaEstadoRecursos;
import ui.Economico.ControlPresupuestario.EdicionEstImp.NuevaEstimacion;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class ResumenCarga implements ControladorPantalla  {
		
	public static final String fxml = "file:src/ui/Economico/CargaImputaciones/ResumenCarga.fxml"; 
    
    @FXML
    private ImageView imSemaforoGeneral;
    public Semaforo semaforo;

    @FXML
    private TableView<Tableable> tImputaciones;
    public Tabla tablaImputaciones;

    @FXML
    private TableView<Tableable> tEstadoProyectos;
    public Tabla tablaEstadoProyectos;

    @FXML
    private ImageView imGuardar;
    public GestionBotones gbGuardar;
    
    public CargaImputaciones padre;

	@Override
	public AnchorPane getAnchor() {
		return null;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		semaforo = new Semaforo(imSemaforoGeneral);
		
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				try {
					//altaUsuario();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Guardar Imputaciones", this);	
		gbGuardar.activarBoton();
		
		//tablaResultado = new Tabla(tResultado,new LineaDetalleImputacion());*/
		
	}
	
	public void adscribir(CargaImputaciones padre, Estimacion est) throws Exception {
		this.padre = padre;
		
		Iterator<Integer> itSemaforos = this.padre.listaSemaforosProyecto.values().iterator();
		
		int estado = Semaforo.VERDE;
		
		while (itSemaforos.hasNext()) {
			Integer i = itSemaforos.next();
			
			if (i!=semaforo.ROJO) {
				if (estado==semaforo.VERDE && i==semaforo.AMBAR) {
					estado = i;
				}
			} else {
				estado = i;
			}
		}
		
		this.semaforo.asignaEstado(estado);
		
		ArrayList<Object> proyectos = new ArrayList<Object>();
		Iterator<Integer> itProyectos = this.padre.listaImputacionesProyecto.keySet().iterator();
		while (itProyectos.hasNext()) {
			Integer i = itProyectos.next();
			Proyecto p = Proyecto.getProyectoEstatico(i);
			
			HashMap<String,Object> listaValores = new HashMap<String,Object>();
			listaValores.put(LineaEstadoProyectos.PROYECTO, p);
			listaValores.put(LineaEstadoProyectos.TOTAL_IMPUTACIONES, this.padre.listaImputacionesProyecto.get(i).size());
			listaValores.put(LineaEstadoProyectos.ESTADO, this.padre.listaSemaforosProyecto.get(i));
			
			int contaAn = 0;
			int contaEl = 0;
			int contaMod = 0;
			
			Iterator<Imputacion> itImpu = this.padre.listaImputacionesAsignadasProyecto.get(i).iterator();
			while (itImpu.hasNext()) {
				Imputacion imp = itImpu.next();
				
				if (imp.modo_fichero == NuevaEstimacion.MODO_INSERTAR) contaAn++;
				if (imp.modo_fichero == NuevaEstimacion.MODO_MODIFICAR) contaMod++;
				if (imp.modo_fichero == NuevaEstimacion.MODO_ELIMINAR) contaEl++;
			}
			
			listaValores.put(LineaEstadoProyectos.TOTAL_ANIADIR, contaAn);
			listaValores.put(LineaEstadoProyectos.TOTAL_ELIMINAR, contaEl);
			listaValores.put(LineaEstadoProyectos.TOTAL_CAMBIAR, contaMod);

			listaValores.put(LineaEstadoProyectos.TOTAL, contaAn+contaEl+contaMod);
			
			proyectos.add(listaValores);
		}
		
		this.tablaEstadoProyectos = new Tabla(this.tEstadoProyectos,new LineaEstadoProyectos());
		this.tablaEstadoProyectos.pintaTabla(proyectos);
		
		ArrayList<LineaEstadoRecursos> lImputaciones = new ArrayList<LineaEstadoRecursos>();
		Iterator<ArrayList<Imputacion>> itImputs = this.padre.listaImputacionesAsignadasProyecto.values().iterator();
		
		while (itImputs.hasNext()) {
			ArrayList<Imputacion> lImputs = itImputs.next();
			
			Iterator<Imputacion> itImput = lImputs.iterator();
			while (itImput.hasNext()) {
				Imputacion o = itImput.next();
				lImputaciones.add(new LineaEstadoRecursos(o));				
			}
		}
		
		Collections.sort(lImputaciones);
		
		this.tablaImputaciones = new Tabla(this.tImputaciones,new LineaEstadoRecursos());
		this.tablaImputaciones.pintaTabla(TipoDato.toListaObjetos(lImputaciones));
	}
		
}
