package ui.Economico.EstimacionesInternas;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.plaf.metal.MetalLookAndFeel;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.ToggleSwitch;

import controller.AnalizadorPresupuesto;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import model.beans.Concepto;
import model.beans.Estimacion;
import model.beans.JornadasMes;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.beans.Recurso;
import model.beans.RelRecursoTarifa;
import model.beans.Tarifa;
import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaGerencia;
import model.metadatos.MetaParamProyecto;
import model.metadatos.MetaParamRecurso;
import model.metadatos.Sistema;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;
import ui.ControladorPantalla;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Tabla;
import ui.Tableable;
import ui.Economico.EstimacionesInternas.tables.LineaCosteProyectoEstimacion;
import ui.Economico.EstimacionesInternas.tables.LineaDetalleUsuario;

public class EstimacionesInternas implements ControladorPantalla {

	public static final String HORAS = "H";
	public static final String IMPORTE = "I";
	public static final String SISTEMA = "SISTEMA";
	public static final String RECURSO = "RECURSO";
	public static final String PROYECTO = "PROYECTO";
	
	public static int MODO_INSERTAR = 1;
	public static int MODO_MODIFICAR = 2;
	public static int MODO_BORRAR = 3;
	public static int MODO_NEUTRO = -1;
	
	public static final String fxml = "file:src/ui/Economico/EstimacionesInternas/EstimacionesInternas.fxml"; 
	
	public ArrayList<EstimacionesInternasMes> listaMeses = null;
	
	public HashMap<Integer,HashMap<Integer,HashMap<String,HashMap<String,HashMap<String,Float>>>>> listadatos = null;
	public HashMap<String,String> conceptosEstaticos = null;
	public HashMap<String,Proyecto> proyectosDisponibles = null;
	public HashMap<String,Sistema> sistemasDisponibles = null;
	public HashMap<String,Recurso> recursosDisponibles = null;
	
	public ArrayList<Estimacion> listaEstimacionesProvisionales = null; 

    @FXML
    public ComboBox<Integer> cbAnio;

    @FXML
    private ComboBox<MetaConcepto> cbNatCoste;


    @FXML
    private ScrollPane crMes;

    @FXML
    private ScrollPane crUsuario;
    
    @FXML
    public ToggleSwitch tsColapsar;
    
    @FXML
    private HBox hbMeses;

    @FXML
    private TableView<Tableable> tResumen;
    
    public Tabla tablaResumen;
    
    @FXML
    private TextField tFiltroResumen;

    @FXML
    private TableView<Tableable> tUsuario;
    public Tabla tablaUsuario;
    
    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;
    
    @FXML
    private ImageView imAniadir;
    private GestionBotones gbAniadir;
	
	@Override
	public AnchorPane getAnchor() {
		return null;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				insertaEstimacionesBD();
				listaEstimacionesProvisionales = new ArrayList<Estimacion>();
				cargaUsuarios(true);
				cargaProyectos();
            } }, "Guardar Cambios", this);	
		gbGuardar.desActivarBoton();
		
		gbAniadir = new GestionBotones(imAniadir, "Nuevo3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				editarEstimacion();
            } }, "Guardar Cambios", this);	
		
		tFiltroResumen.setText("500");
		tablaResumen = new Tabla(tResumen,new LineaCosteProyectoEstimacion(),tFiltroResumen, this);
		
		listaEstimacionesProvisionales = new ArrayList<Estimacion>();
		
		tablaUsuario = new Tabla(tUsuario,new LineaDetalleUsuario(),tsColapsar,this);
		
		Iterator<MetaConcepto> itMC = MetaConcepto.listado.values().iterator();
		cbNatCoste.getItems().removeAll(cbNatCoste.getItems());
		
		MetaConcepto seleccionCBNAT = null;
		
		while (itMC.hasNext()) {
			MetaConcepto mc = itMC.next();
			if (mc.tipoGestionEconomica == MetaConcepto.GESTION_HORAS) {
				cbNatCoste.getItems().add(mc);
				if (mc.id == MetaConcepto.SATAD) seleccionCBNAT = mc;
			}
		}
		
		cbNatCoste.setValue(seleccionCBNAT);
		
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		
		cbAnio.getItems().removeAll(cbAnio.getItems());
		Integer seleccion = null;
		
		for (int i=-1;i<3;i++) {
			Integer inte = new Integer(c.get(Calendar.YEAR)+i);
			cbAnio.getItems().add(inte);
			if (i==0) seleccion = inte;			
		}
		
		cbAnio.setValue(seleccion);
		
		conceptosEstaticos = new HashMap<String,String> ();
		conceptosEstaticos.put(LineaDetalleUsuario.CONCEPTO_HORAS_BASE,"");
		conceptosEstaticos.put(LineaDetalleUsuario.CONCEPTO_HORAS_AUSENCIAS,"");
		conceptosEstaticos.put(LineaDetalleUsuario.CONCEPTO_HORAS_TOTAL,"");
		conceptosEstaticos.put(LineaDetalleUsuario.CONCEPTO_HORAS_ASIGNADAS,"");
		conceptosEstaticos.put(LineaDetalleUsuario.CONCEPTO_HORAS_PENDIENTES,"");
		
		cbAnio.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			cargaUsuarios(true);
			cargaProyectos();
	    	}
	    ); 
		
		cbNatCoste.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			cargaUsuarios(true);
			cargaProyectos();
	    	}
	    ); 
		
		crMes.vvalueProperty().addListener(new ChangeListener<Number>() 
	     {
	          @Override
	          public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) 
	          { 
	            crUsuario.setVvalue(newValue.doubleValue());
	          }
	     });
		
		crUsuario.vvalueProperty().addListener(new ChangeListener<Number>() 
	     {
	          @Override
	          public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) 
	          { 
	        	  crMes.setVvalue(newValue.doubleValue());
	          }
	     });
		
		cargaUsuarios(true);
		cargaProyectos();

	}
	
	public void cargaProyectos() {
		 ArrayList<Proyecto> proyectos = proyectosHabiles();
		 ArrayList<LineaCosteProyectoEstimacion> lista = new ArrayList<LineaCosteProyectoEstimacion>();
		 
		 Iterator<Proyecto> itProy = proyectos.iterator();
		 while (itProy.hasNext()) {
			 Proyecto p = itProy.next();
			 
			 AnalizadorPresupuesto ap = new AnalizadorPresupuesto();
			 
			 Presupuesto prep = new Presupuesto();
             prep = prep.dameUltimaVersionPresupuesto(p);
             
             if (prep!=null) {
            	 Presupuesto pAux = prep;
            	 pAux.cargaCostes();
			 	 ap.construyePresupuestoMensualizado(pAux, new Date(), null, null, null, null);
			 	 
			 	 Concepto c = new Concepto();
			 	 c.tipoConcepto = cbNatCoste.getValue();
			 	 HashMap<String, Sistema> listaSistemas = ap.detalleEstimado (c);
			 	 
			 	 Iterator<Sistema> itSistema = listaSistemas.values().iterator();
			 	 
			 	 while (itSistema.hasNext()) {
			 		Sistema sAux = itSistema.next(); 
			 		
			 		if (sAux.responsable == Constantes.USUARIORESPONSABLE) {
			 			LineaCosteProyectoEstimacion lcpe = new LineaCosteProyectoEstimacion();
				 		lcpe.proyecto = p;
				 		lcpe.sistema = sAux;
				 		
				 		if (!proyectosDisponibles.containsKey(p.nombre)) proyectosDisponibles.put(p.nombre,p);
				 		if (!sistemasDisponibles.containsKey(sAux.codigo)) sistemasDisponibles.put(sAux.codigo,sAux);
				 		
				 		Iterator<Integer> itKeys = sAux.listaEstimaciones.keySet().iterator();
				 		
				 		while (itKeys.hasNext()) {
				 			Integer key = itKeys.next();
				 			Estimacion estAux = sAux.listaEstimaciones.get(key);
				 			
				 			if (key.intValue() == cbAnio.getValue().intValue()) {
				 				lcpe.estimadoAnio += estAux.importe;
				 				lcpe.provisionadoAnio += estAux.importeProvisionado;
				 			} else {
				 				lcpe.estimadoResto += estAux.importe;
				 				lcpe.provisionadoResto += estAux.importeProvisionado;
				 			}
				 		}
				 		
				 		Concepto cAux = pAux.getCosteConcepto(sAux, c.tipoConcepto);
				 		
				 		if (cAux!=null) {
				 			lcpe.total = lcpe.estimadoAnio + lcpe.provisionadoAnio + lcpe.estimadoResto + lcpe.provisionadoResto;
					 		lcpe.presupuestado = cAux.valorEstimado;
					 		lcpe.restante = lcpe.presupuestado-lcpe.estimadoAnio - lcpe.estimadoResto;
				 		} else {
				 			lcpe.total = lcpe.estimadoAnio + lcpe.provisionadoAnio + lcpe.estimadoResto + lcpe.provisionadoResto;
					 		lcpe.presupuestado = 0;
					 		lcpe.restante = lcpe.presupuestado-lcpe.estimadoAnio - lcpe.estimadoResto;
				 		}
				 		
				 		lista.add(lcpe);
			 		}			 		
			 	 }
			 	 
             }
          }   
		
		ArrayList<Object> listadoAux = new ArrayList<Object>();
		listadoAux.addAll(lista);
		
		tablaResumen.pintaTabla(listadoAux);
	}
	
	public void cargaUsuarios(boolean actualizar) 
	{
		
		try {
			if (actualizar) 
				construyeDatos();
			
			Iterator<Integer> itRecursos = this.listadatos.keySet().iterator();
			ArrayList<LineaDetalleUsuario> lista = new ArrayList<LineaDetalleUsuario>();
			
			hbMeses.getChildren().removeAll(hbMeses.getChildren());
			listaMeses = new ArrayList<EstimacionesInternasMes>();
			
			for (int i=0; i<12; i++) {
				Pane p = new Pane();
				hbMeses.getChildren().add(p);
				
				EstimacionesInternasMes estMes = new EstimacionesInternasMes();
		        FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(estMes.getFXML()));
		        
		        p.getChildren().add(loader.load());
		        estMes = loader.getController();
		        estMes.adscribir(this, i);	
		        this.listaMeses.add(estMes);
			}
			
			while (itRecursos.hasNext()) {
				Integer key = itRecursos.next();
				HashMap<Integer,HashMap<String,HashMap<String,HashMap<String,Float>>>> recurso = this.listadatos.get(key);
				Recurso r = Recurso.listaRecursos.get(key);				
				
				LineaDetalleUsuario ldu = new LineaDetalleUsuario();
				ldu.codUsuario = (String) r.getValorParametro(MetaParamRecurso.IDRecurso);
				ldu.nomUsuario = r.nombre;
				ldu.concepto = LineaDetalleUsuario.CONCEPTO_HORAS_TOTAL;
				lista.add(ldu);
				
				ldu = new LineaDetalleUsuario();
				ldu.concepto = LineaDetalleUsuario.CONCEPTO_HORAS_AUSENCIAS;
				lista.add(ldu);
				
				ldu = (LineaDetalleUsuario) ldu.clone();
				ldu.concepto = LineaDetalleUsuario.CONCEPTO_HORAS_BASE;
				lista.add(ldu);
				
				HashMap<String, HashMap<String,String>> procesados = new HashMap<String, HashMap<String,String>>();
				
				Iterator<Integer> itMeses = recurso.keySet().iterator();
				while (itMeses.hasNext()) {
					Integer keyMes = itMeses.next();
					HashMap<String,HashMap<String,HashMap<String,Float>>> mes = recurso.get(keyMes);
					
					Iterator<String> keyConcepto = mes.keySet().iterator();
					
					while (keyConcepto.hasNext()) {
						String concepto = keyConcepto.next();
						
						if (!this.conceptosEstaticos.containsKey(concepto)) {
							HashMap<String,HashMap<String,Float>> proyecto = mes.get(concepto);
							HashMap<String,String> sistemasProyectoProcesados = null;
							
							if (procesados.containsKey(concepto)) {
								sistemasProyectoProcesados =  procesados.get(concepto);
							} else {
								sistemasProyectoProcesados = new HashMap<String,String>();
								procesados.put(concepto,sistemasProyectoProcesados);
							}
							
							Iterator<String> itSistemas = proyecto.keySet().iterator();
							
							while (itSistemas.hasNext()) {
								String keySistema = itSistemas.next();
								if (!sistemasProyectoProcesados.containsKey(keySistema) && !EstimacionesInternas.SISTEMA.equals(keySistema)) {
									sistemasProyectoProcesados.put(keySistema,keySistema);
								}
							}
						}
					}
				}
				
				Iterator<String> keysProyecto = procesados.keySet().iterator();
				
				while (keysProyecto.hasNext()) {
					String keyProyecto = keysProyecto.next();
					Iterator<String> itSistema = procesados.get(keyProyecto).values().iterator();
					
					while (itSistema.hasNext()) {
						String sistema = itSistema.next();
						ldu = (LineaDetalleUsuario) ldu.clone();
						ldu.concepto = keyProyecto;
						ldu.sistema = sistema; 
						lista.add(ldu);
					}
				}
				
				ldu = (LineaDetalleUsuario) ldu.clone();
				ldu.concepto = LineaDetalleUsuario.CONCEPTO_HORAS_ASIGNADAS;
				lista.add(ldu);
				
				ldu = (LineaDetalleUsuario) ldu.clone();
				ldu.concepto = LineaDetalleUsuario.CONCEPTO_HORAS_PENDIENTES;
				lista.add(ldu);
				
				
			}
			
			ArrayList<Object> listadoAux = new ArrayList<Object>();
			listadoAux.addAll(lista);
			
			tablaUsuario.pintaTabla(listadoAux);
			
			Iterator<EstimacionesInternasMes> itEi = this.listaMeses.iterator();
			
			while (itEi.hasNext()) {
				EstimacionesInternasMes ei = itEi.next();
				ei.pintaTabla();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void construyeDatos() throws Exception{
		proyectosDisponibles = new HashMap<String, Proyecto>();
		sistemasDisponibles = new HashMap<String, Sistema>();
		recursosDisponibles = new HashMap<String,Recurso>(); 
		
		listadatos = new HashMap<Integer,HashMap<Integer,HashMap<String,HashMap<String,HashMap<String,Float>>>>>();
		
		Iterator<Recurso> itRecursos = Recurso.listadoRecursosEstatico().values().iterator();
		
		while (itRecursos.hasNext()) {
			Recurso r = itRecursos.next();
			
			if (new Integer((String) r.getValorParametro(MetaParamRecurso.INTERNO))==this.cbNatCoste.getValue().id && new Integer((String) r.getValorParametro(MetaParamRecurso.COD_PARM_GESTOR)) == Constantes.USUARIORESPONSABLE) {
				recursosDisponibles.put(new Integer(r.id).toString(), r);
				
				Estimacion est = new Estimacion();
				ArrayList<Estimacion> listaEstim = est.listado(r, cbAnio.getValue());
				HashMap<Integer, HashMap<Integer,Estimacion>> listaEstimaciones = new HashMap<Integer, HashMap<Integer,Estimacion>>();
				this.listaEstimacionesProvisionales = new ArrayList<Estimacion>();
				this.listaEstimacionesProvisionales.addAll(listaEstim);
				
				Iterator<Estimacion> itEstimacion = listaEstim.iterator();
				while (itEstimacion.hasNext()) {
					est = itEstimacion.next();
					
					if (est.sistema.responsable == Constantes.USUARIORESPONSABLE) {
						HashMap<Integer,Estimacion> meses = null;
						
						if (listaEstimaciones.containsKey(est.proyecto.id)) {
							meses = listaEstimaciones.get(est.proyecto.id);
						} else {
							meses = new HashMap<Integer,Estimacion>();
							listaEstimaciones.put(est.proyecto.id, meses);
						}
						
						meses.put(est.mes, est);
					}
				}
				
				HashMap<Integer,HashMap<String,HashMap<String,HashMap<String,Float>>>> datosRecurso = new HashMap<Integer,HashMap<String,HashMap<String,HashMap<String,Float>>>>();
				listadatos.put(r.id, datosRecurso);
				
				float multiplicador = 0;
				
				try {
					RelRecursoTarifa rrt = new RelRecursoTarifa();
					ArrayList<RelRecursoTarifa> listaTarifas = rrt.buscaRelacion(r.id);
					Tarifa t = rrt.tarifaVigente(listaTarifas, r.id).tarifa;
					multiplicador = t.costeHora;
				} catch (Exception e) {
					multiplicador = 1;
				}
				
				JornadasMes jd = new JornadasMes(r,this.cbAnio.getValue());
				HashMap<Integer,HashMap<Integer, JornadasMes>> listadoDatosRecurso = jd.getListadoAnual();
				
				for (int i=0;i<12;i++) {
					HashMap<String,HashMap<String,HashMap<String,Float>>> datosMes = new HashMap<String,HashMap<String,HashMap<String,Float>>>();
					datosRecurso.put(i+1,datosMes);
					
					jd = listadoDatosRecurso.get(i+1).get(JornadasMes.JORNADAS);
					HashMap<String,HashMap<String,Float>> sistema = new HashMap<String,HashMap<String,Float>>();
					HashMap<String,Float> concepto = new HashMap<String,Float>();
					sistema.put(EstimacionesInternas.SISTEMA, concepto);
					datosMes.put(LineaDetalleUsuario.CONCEPTO_HORAS_BASE, sistema);
					concepto.put(EstimacionesInternas.HORAS, jd.horasAcumuladas);
					concepto.put(EstimacionesInternas.IMPORTE, jd.horasAcumuladas*multiplicador);
					
					jd = listadoDatosRecurso.get(i+1).get(JornadasMes.VACACIONES);
					JornadasMes jdAux = listadoDatosRecurso.get(i+1).get(JornadasMes.VACACIONES);
					sistema = new HashMap<String,HashMap<String,Float>>();
					concepto = new HashMap<String,Float>();
					sistema.put(EstimacionesInternas.SISTEMA, concepto);
					datosMes.put(LineaDetalleUsuario.CONCEPTO_HORAS_AUSENCIAS, sistema);
					concepto.put(EstimacionesInternas.HORAS, jd.horasAcumuladas + jdAux.horasAcumuladas);
					concepto.put(EstimacionesInternas.IMPORTE, (jd.horasAcumuladas + jdAux.horasAcumuladas)*multiplicador);
					
					float asignadas = 0;
					
					Iterator<HashMap<Integer,Estimacion>> itEstim = listaEstimaciones.values().iterator();
					
					while (itEstim.hasNext()) {
						HashMap<Integer,Estimacion> desgloseMeses = itEstim.next();
						
						if (desgloseMeses.containsKey(i+1)) {
							est = desgloseMeses.get(i+1);
							
							concepto = new HashMap<String,Float>();
							sistema = new HashMap<String,HashMap<String,Float>>();
							concepto = new HashMap<String,Float>();
							sistema.put(est.sistema.codigo, concepto);
							datosMes.put(est.proyecto.nombre, sistema);
							
							concepto.put(EstimacionesInternas.HORAS, est.horas);
							concepto.put(EstimacionesInternas.IMPORTE, est.horas*multiplicador);
							concepto.put(EstimacionesInternas.SISTEMA, est.horas*multiplicador);
							concepto.put(EstimacionesInternas.RECURSO, new Float(r.id));
							proyectosDisponibles.put(est.proyecto.nombre,est.proyecto);
							sistemasDisponibles.put(est.sistema.codigo,est.sistema);
							asignadas+= est.horas;
						} else {
							concepto = new HashMap<String,Float>();
							sistema = new HashMap<String,HashMap<String,Float>>();
							concepto = new HashMap<String,Float>();
							sistema.put(EstimacionesInternas.SISTEMA, concepto);
							datosMes.put(est.proyecto.nombre, sistema);
							concepto.put(EstimacionesInternas.HORAS, new Float(0));
							concepto.put(EstimacionesInternas.IMPORTE, new Float(0));
							concepto.put(EstimacionesInternas.SISTEMA, est.horas*multiplicador);
							concepto.put(EstimacionesInternas.RECURSO, new Float(r.id));
						}
					}
					
					sistema = new HashMap<String,HashMap<String,Float>>();
					concepto = new HashMap<String,Float>();
					sistema.put(EstimacionesInternas.SISTEMA, concepto);
					datosMes.put(LineaDetalleUsuario.CONCEPTO_HORAS_ASIGNADAS, sistema);
					concepto.put(EstimacionesInternas.HORAS, asignadas);
					concepto.put(EstimacionesInternas.IMPORTE, asignadas*multiplicador);
	
					jd = listadoDatosRecurso.get(i+1).get(JornadasMes.TOTAL);				
					sistema = new HashMap<String,HashMap<String,Float>>();
					concepto = new HashMap<String,Float>();
					sistema.put(EstimacionesInternas.SISTEMA, concepto);
					datosMes.put(LineaDetalleUsuario.CONCEPTO_HORAS_TOTAL, sistema);
					concepto.put(EstimacionesInternas.HORAS, jd.horasAcumuladas);
					concepto.put(EstimacionesInternas.IMPORTE, jd.horasAcumuladas*multiplicador);
					
					sistema = new HashMap<String,HashMap<String,Float>>();
					concepto = new HashMap<String,Float>();
					sistema.put(EstimacionesInternas.SISTEMA, concepto);
					datosMes.put(LineaDetalleUsuario.CONCEPTO_HORAS_PENDIENTES, sistema);
					concepto.put(EstimacionesInternas.HORAS, (jd.horasAcumuladas-asignadas));
					concepto.put(EstimacionesInternas.IMPORTE,  (jd.horasAcumuladas-asignadas)*multiplicador);
				}
			}
		}
		
	}
	
	public ArrayList<Proyecto> proyectosHabiles() {
		
		ArrayList<Proyecto> listaProyecto = new ArrayList<Proyecto>();
		
		Proyecto pAux = new Proyecto();
		
		Iterator<Proyecto> itProyecto = pAux.listadoProyectos().iterator();
		
		while (itProyecto.hasNext()) {
			Proyecto p = itProyecto.next();
			
			try {
				p.cargaProyecto();
				
				if (!p.isCerrado()) {
					Date fechaFin = (Date) p.getValorParametro(MetaParamProyecto.FX_FIN);
					Date fechaInicio = (Date) p.getValorParametro(MetaParamProyecto.FX_INICIO);
					
					Calendar cFechaInicio = Calendar.getInstance();
					cFechaInicio.setTime(fechaInicio);
					
					Calendar cFechaFin = Calendar.getInstance();
					cFechaFin.setTime(fechaFin);
					
					cbAnio.getValue();
					Calendar cFinAnio = Calendar.getInstance();
					cFinAnio.setTime(Constantes.finAnio(cbAnio.getValue()));
					
					Calendar cInicioAnio = Calendar.getInstance();
					cInicioAnio.setTime(Constantes.inicioAnio(cbAnio.getValue()));
					
					if ((cFechaFin.after(cFinAnio) && cFechaInicio.before(cFinAnio)) || 
						(cFechaFin.after(cInicioAnio) && cFechaInicio.before(cInicioAnio)) || 
						(cFechaFin.before(cFinAnio) && cFechaInicio.after(cInicioAnio)) ||
						(cFechaFin.after(cFinAnio) && cFechaInicio.before(cInicioAnio))  ) {
						listaProyecto.add(p);
					}
				}
			} catch (Exception e) {}
		}
		
		return listaProyecto;		
	}
	
	public void editarEstimacion() {
		try {
			FXMLLoader loader = new FXMLLoader();
			
			HashMap<String, Object> parametrosPaso = new HashMap<String, Object>();
			
			parametrosPaso.put("filaDatos", null);							        	
			parametrosPaso.put("columna", null);
			parametrosPaso.put("evento", null);
			parametrosPaso.put("controladorPantalla", this);
			
	    	EditarEstimacion controlPantalla = new EditarEstimacion();
	    	
	    	if (ParamTable.po!=null){
	    		ParamTable.po.hide();
	    		ParamTable.po = null;
	    	}
	    	
	    	loader.setLocation(new URL(controlPantalla.getControlFXML()));
		    Pane pane = loader.load();
		    controlPantalla = (EditarEstimacion) loader.getController();
		    controlPantalla.setParametrosPaso(parametrosPaso);
		    ParamTable.po = new PopOver(pane);
		    parametrosPaso.put("PopOver", ParamTable.po);
		    ParamTable.po.setTitle("");
		    ParamTable.po.show(this.crMes);
		    ParamTable.po.setAnimated(true);
		    ParamTable.po.setAutoHide(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
	}
	
	public Estimacion guardaEstimacion(Estimacion est) {
		ArrayList<Estimacion> procesados = new ArrayList<Estimacion>();
		boolean insertada = false;
		boolean todasNeutras = true;
		Estimacion salida = null;

		
		Iterator<Estimacion> itEstimacion = this.listaEstimacionesProvisionales.iterator();
		while (itEstimacion.hasNext()) {
			Estimacion estAux = itEstimacion.next();
			
			if (estAux.equals(est)) {
				insertada = true;
				
				if (est.horas==0) {
					if (estAux.modo == EstimacionesInternas.MODO_NEUTRO) {
						estAux.modo = EstimacionesInternas.MODO_BORRAR;
						procesados.add(estAux);
						salida = estAux;
						todasNeutras = false;
					}
					if (estAux.modo == EstimacionesInternas.MODO_MODIFICAR) {
						estAux.modo = EstimacionesInternas.MODO_BORRAR;
						procesados.add(estAux);
						salida = estAux;
						est.id = estAux.id;
						todasNeutras = false;
					}
				} else {
					if (estAux.modo == EstimacionesInternas.MODO_NEUTRO) {
						est.modo = EstimacionesInternas.MODO_MODIFICAR;
						procesados.add(est);
						salida = est;
						est.id = estAux.id;
						todasNeutras = false; 
					}
					if (estAux.modo == EstimacionesInternas.MODO_MODIFICAR) {
						est.modo = EstimacionesInternas.MODO_MODIFICAR;
						est.id = estAux.id;
						salida = est;
						procesados.add(est);
						todasNeutras = false;
					}
					if (estAux.modo == EstimacionesInternas.MODO_BORRAR) {
						est.modo = EstimacionesInternas.MODO_MODIFICAR;
						procesados.add(est);
						salida = est;
						est.id = estAux.id; 
						todasNeutras = false;
					}
					if (estAux.modo == EstimacionesInternas.MODO_INSERTAR) {
						est.modo = EstimacionesInternas.MODO_INSERTAR;
						procesados.add(est);
						salida = est;
						todasNeutras = false;
					}
				}
			} else {
				procesados.add(estAux);
			}
		}
		
		if (!insertada) {
			if (est.horas!=0) {
				est.modo = EstimacionesInternas.MODO_INSERTAR;
				procesados.add(est);
				salida = est;
				todasNeutras = false;
			}
		}
		
		this.listaEstimacionesProvisionales = procesados;
		
		if (!todasNeutras)
			gbGuardar.activarBoton();
		else
			gbGuardar.desActivarBoton();
		
		return salida;
	}
	
	public void insertaEstimacionesBD() {
		try {
			int contador = 0;

			String idTransaccion = "insertaEstimacionesBD" + new Date().getTime();
			
			Iterator<Estimacion> itEstimacion = this.listaEstimacionesProvisionales.iterator();
			while (itEstimacion.hasNext()) {
				Estimacion est = itEstimacion.next();
				
				if (est.modo == EstimacionesInternas.MODO_INSERTAR) {
					est.insertEstimacion(idTransaccion);
					contador++;
				}
				if (est.modo == EstimacionesInternas.MODO_MODIFICAR) {
					est.modificaEstimacion(idTransaccion);
					contador++;
				}
				if (est.modo == EstimacionesInternas.MODO_BORRAR) {
					est.borraEstimacion(idTransaccion);
					contador++;
				}
			}
			
			ConsultaBD consulta = new ConsultaBD();
			
			consulta = new ConsultaBD();
			
			consulta.ejecutaTransaccion(idTransaccion);
			
			Dialogo.alert("Guardado correcto", "Se pudieron almacenar todas las estimaciones" ,"Se guardaron correctamente " + contador + " estimaciones");
			
			gbGuardar.desActivarBoton();
			
		} catch (Exception ex) {
			Dialogo.error("Fallo al guardar", "Se produjo un error al guardar las estimaciones", "No se pudieron guardar las estimaciones");
		}
	}
	
	public void guardaProvisional(Proyecto p, Recurso r, float horas, float importe, int mes, int anio, Sistema s ) {
		Estimacion est = new Estimacion();
		est.id = -1;
		est.horas = horas;
		est.aprobacion = Estimacion.DESAPROBADA;
		est.fxFin = Constantes.finMes(mes, anio);
		est.fxInicio = Constantes.inicioMes(mes, anio);
		
		if (cbNatCoste.getValue().id == MetaConcepto.SATAD) {
			est.gerencia = MetaGerencia.porId(MetaGerencia.GGP);
		} else {
			est.gerencia = MetaGerencia.porId(MetaGerencia.GDT);
		}
		
		est.importe = importe;
		est.importeProvisionado = 0;
		est.mes = mes;
		est.anio = anio;
		est.natCoste = cbNatCoste.getValue();
		est.proyecto = p;
		est.recurso = r;
		est.sistema = s;
		
		est = guardaEstimacion(est);
		
		HashMap<Integer,HashMap<String,HashMap<String,HashMap<String,Float>>>> meses = this.listadatos.get(r.id);
		HashMap<String,HashMap<String,HashMap<String,Float>>> mesO = meses.get(mes);
		HashMap<String,HashMap<String,Float>> concepto = mesO.get(p.nombre);
		
		if (concepto == null) {
			Iterator<HashMap<String,HashMap<String,HashMap<String,Float>>>> itMeses = meses.values().iterator();
			while (itMeses.hasNext()) {
				mesO = itMeses.next();
				concepto = new HashMap<String,HashMap<String,Float>> ();
				mesO.put(p.nombre, concepto);
				HashMap<String,Float> sistema = new HashMap<String,Float> ();
				concepto.put(EstimacionesInternas.SISTEMA, sistema);
				sistema.put(EstimacionesInternas.HORAS,new Float(0));
				sistema.put(EstimacionesInternas.IMPORTE,new Float(0));
				sistema.put(EstimacionesInternas.RECURSO,new Float(r.id));
			}
			mesO = meses.get(mes);
			concepto = mesO.get(p.nombre);
		} 		
		
		HashMap<String,Float> sistema = concepto.get(s.codigo);
		float horasAntiguas = 0;
		float importeAntiguo = 0;
		if (sistema == null) {
			sistema = new HashMap<String,Float> ();
			concepto.put(s.codigo, sistema);
			concepto.remove(EstimacionesInternas.SISTEMA);
		} else {
			horasAntiguas = sistema.get(EstimacionesInternas.HORAS); 
			importeAntiguo = sistema.get(EstimacionesInternas.IMPORTE);
		}
		
		sistema.remove(EstimacionesInternas.HORAS);
		sistema.remove(EstimacionesInternas.IMPORTE);
		sistema.put(EstimacionesInternas.HORAS,horas);
		sistema.put(EstimacionesInternas.IMPORTE,importe);
		sistema.put(EstimacionesInternas.RECURSO, new Float(r.id));
		
		sistema = mesO.get(LineaDetalleUsuario.CONCEPTO_HORAS_PENDIENTES).get(EstimacionesInternas.SISTEMA);
		float pendienteHorasAntiguas = sistema.get(EstimacionesInternas.HORAS); 
		float pendienteimporteAntiguo = sistema.get(EstimacionesInternas.IMPORTE);
		sistema.remove(EstimacionesInternas.HORAS);
		sistema.remove(EstimacionesInternas.IMPORTE);
		sistema.put(EstimacionesInternas.HORAS,pendienteHorasAntiguas+horasAntiguas-horas);
		sistema.put(EstimacionesInternas.IMPORTE,pendienteimporteAntiguo+importeAntiguo-importe);
		sistema = mesO.get(LineaDetalleUsuario.CONCEPTO_HORAS_ASIGNADAS).get(EstimacionesInternas.SISTEMA);
		pendienteHorasAntiguas = sistema.get(EstimacionesInternas.HORAS); 
		pendienteimporteAntiguo = sistema.get(EstimacionesInternas.IMPORTE);
		sistema.remove(EstimacionesInternas.HORAS);
		sistema.remove(EstimacionesInternas.IMPORTE);
		sistema.put(EstimacionesInternas.HORAS,pendienteHorasAntiguas-horasAntiguas+horas);
		sistema.put(EstimacionesInternas.IMPORTE,pendienteimporteAntiguo-importeAntiguo+importe);
		
		cargaUsuarios(false);
		
		Iterator<Tableable> itResumen = this.tablaResumen.listaDatos.iterator();
		while (itResumen.hasNext()) {
			LineaCosteProyectoEstimacion lcpe = (LineaCosteProyectoEstimacion) itResumen.next();
			
			if (lcpe.proyecto.id == p.id && lcpe.sistema.id == s.id) {
				lcpe.restante = lcpe.restante + importeAntiguo - importe;
				lcpe.estimadoAnio += importe-importeAntiguo;
				break;
			}			
		}
		
		this.tablaResumen.refrescaTabla();
		
		ParamTable.po.hide();
	}
	
}
