package ui.Economico.ControlPresupuestario;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import controller.AnalizadorPresupuesto;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.Concepto;
import model.beans.Coste;
import model.beans.EstimacionAnio;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.beans.TopeImputacion;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import model.utils.db.ConsultaBD;
import ui.ConfigTabla;
import ui.Dialogo;
import ui.GestionBotones;
import ui.Tabla;
import ui.Economico.ControlPresupuestario.Tables.LineaTopeImputacion;
import ui.Economico.ControlPresupuestario.Tables.LineaTopePresupuesto;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class TopeImputaciones implements ControladorPantalla {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/TopeImputaciones.fxml"; 
	public static AnalizadorPresupuesto ap = null;
	public Proyecto p = null;
	
	public static ArrayList<TopeImputacion> listadoTopes = null;
	
	public static TopeImputaciones thisTope = null; 
	
	public ControlPresupuestario cp = null;
	
	@FXML
	private AnchorPane anchor;
	
    @FXML
    public ComboBox<Sistema> cbComboSistemas;

    @FXML
    private TableView<Tableable> tbTopes;
    private Tabla tablaTopes;
 
    @FXML
    private ComboBox<MetaConcepto> cbConceptos;
    
    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;

    @FXML
    private TextField tFechaPivote;
	
	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}
	
	@Override
	public void resize(Scene escena) {
		tablaTopes.fijaAlto(cbComboSistemas.getScene().getHeight()*0.5);
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		
		TopeImputaciones.listadoTopes = new ArrayList<TopeImputacion>();
		
		try {
			if (ControlPresupuestario.ap!=null)
				this.tFechaPivote.setText(FormateadorDatos.formateaDato(ControlPresupuestario.ap.fechaPivote,FormateadorDatos.FORMATO_FECHA));
			else 
				this.tFechaPivote.setText(FormateadorDatos.formateaDato(Constantes.fechaActual(),FormateadorDatos.FORMATO_FECHA));
		} catch (Exception e) {
			
		}
		
		this.tFechaPivote.focusedProperty().addListener((ov, oldV, newV) -> { 
			if (!newV) {
				try {
					this.tFechaPivote.setText(FormateadorDatos.formateaDato(this.tFechaPivote.getText(),FormateadorDatos.FORMATO_FECHA));
					if (ControlPresupuestario.ap!=null)
						ControlPresupuestario.ap.fechaPivote = (Date) FormateadorDatos.parseaDato(this.tFechaPivote.getText(),FormateadorDatos.FORMATO_FECHA);			
					recargaTopes();
                	ControlPresupuestario.salvaPosicionActual();
                	ControlPresupuestario.cargaPosicionActual();
				} catch (Exception e) {
					try {
						this.tFechaPivote.setText(FormateadorDatos.formateaDato(Constantes.fechaActual(),FormateadorDatos.FORMATO_FECHA));
					} catch (Exception ex) {}
				}
			}	
		});
		
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				try {
					if (validaTopesParaCargar()) {
	                	ControlPresupuestario.salvaPosicionActual();
						String idTransaccion = "fraccionaImputacion" + Constantes.fechaActual().getTime();
						
						Iterator<TopeImputacion> itTopes = TopeImputaciones.listadoTopes.iterator();
						
						int contador = 0;
						
						while (itTopes.hasNext()) {
							TopeImputacion ti = itTopes.next();
							
							ti.deleteTopes(idTransaccion);							
							
							ti.insertTope(idTransaccion);
						}
						
						ConsultaBD consulta = new ConsultaBD();
						consulta.ejecutaTransaccion(idTransaccion);
						ControlPresupuestario.cargaPosicionActual();
					}
				} catch (Exception e) {
					System.out.println();
				}
            } }, "Guardar", this);
		gbGuardar.activarBoton();
		
		Iterator<Coste> costes = ControlPresupuestario.presIzqda.costes.values().iterator();
		
		while (costes.hasNext()) {
			Coste c = costes.next();
			cbComboSistemas.getItems().add(c.sistema);
		}
		
		Sistema s = Sistema.getInstanceTotal();
		
		cbComboSistemas.getItems().add(s);
		cbComboSistemas.setValue(s);
		TopeImputaciones.thisTope = this;
	}
	
	public boolean validaTopesParaCargar() {
		HashMap<String, Float> mapTopes = new HashMap<String, Float>();
		
		Iterator<TopeImputacion> itTope = this.listadoTopes.iterator();
		
		while (itTope.hasNext()) {
			TopeImputacion ti = itTope.next();
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(ControlPresupuestario.ap.fechaPivote);
			
			Calendar calMes = Calendar.getInstance();
			calMes.set(ti.anio, 12, 31);
			
			if (calMes.before(cal)) {
				if (!ti.resto) {
					Dialogo.error("Actualización de Topes", "Problema al guardar", "No se puede fijar una parametrización distinto de \"A resto\" para años a pasado ("+ti.sistema.codigo+")");
					return false;
				}
			}
			
			if (mapTopes.containsKey(ti.sistema.codigo+ti.mConcepto.codigo)) {
				Float suma = new Float((Float) mapTopes.get(ti.sistema.codigo+ti.mConcepto.codigo) + ti.porcentaje);
				mapTopes.remove(ti.sistema.codigo+ti.mConcepto.codigo);
				mapTopes.put(ti.sistema.codigo+ti.mConcepto.codigo, suma);					
			} else {
				mapTopes.put(ti.sistema.codigo+ti.mConcepto.codigo, new Float(ti.porcentaje));
			}
		}
		/*
		Iterator<String> itPorcentajes = mapTopes.keySet().iterator();
		
		while (itPorcentajes.hasNext()) {
			String key = itPorcentajes.next();
			Float porc = mapTopes.get(key);
			if (porc > 100)  {
				Dialogo.error("Actualización de Topes", "Problema al guardar", "No se puede parametrizar más de un 100% como suma de porcentajes ("+key+")");
				return false;
			}
		}*/
		
		return true;
	}
	
	public void CalculaEstimacion (Presupuesto pres){
		Date fechaPivote = null;
		
		try {
			fechaPivote = (Date) FormateadorDatos.parseaDato(this.tFechaPivote.getText(),FormateadorDatos.FORMATO_FECHA);			
		} catch (Exception e) {
			fechaPivote = Constantes.fechaActual();
		}		
		
		AnalizadorPresupuesto ap = new AnalizadorPresupuesto();
		ap.construyePresupuestoMensualizado(pres,fechaPivote,null,null,null,null);	
		
		ControlPresupuestario.ap = ap;
		this.p = ap.proyecto;
	}
	
	public void recargaTopes () {
		TopeImputaciones.ap = new AnalizadorPresupuesto();
		TopeImputaciones.ap.construyePresupuestoMensualizado(ControlPresupuestario.ap.presupuesto, ControlPresupuestario.ap.fechaPivote, null, null, null, TopeImputaciones.listadoTopes);
		
		ArrayList<Concepto> listaConceptos = getListaConceptos(null,p);
				
		ArrayList<Object> lista = new ArrayList<Object>();
		lista.addAll(listaConceptos);
		ObservableList<Tableable> dataTable = (new LineaTopePresupuesto(listaConceptos.get(0))).toListTableable(lista);
		tbTopes.setItems(dataTable);
		
		(new LineaTopePresupuesto()).fijaColumnas(tbTopes);
		ConfigTabla.configuraAlto(tbTopes,lista.size());
		
		Iterator<Sistema> itSistemas = this.cbComboSistemas.getItems().iterator();
		Sistema s = null;
		
		while (itSistemas.hasNext()) {
			s = itSistemas.next();
			
			if (s.codigo.equals(Sistema.getInstanceTotal().codigo)) {
				break;
			}
		}
		
		cbComboSistemas.setValue(s);
		
		resize(null);
	}
	
	public void adscribir(ControlPresupuestario cp, Proyecto p) {
		this.p = p;
		this.cp = cp;
		cp.tp = this;
		
		TopeImputaciones.ap = new AnalizadorPresupuesto();
		TopeImputaciones.ap.construyePresupuestoMensualizado(ControlPresupuestario.ap.presupuesto, ControlPresupuestario.ap.fechaPivote, null, null, null, null);
		
		ArrayList<Concepto> listaConceptos = getListaConceptos(null,p);
				
		ArrayList<Object> lista = new ArrayList<Object>();
		lista.addAll(listaConceptos);
		
		ArrayList<Object> listaFiltros = new ArrayList<Object>();
		listaFiltros.add(cbComboSistemas);
		listaFiltros.add(cbConceptos);

		tablaTopes = new Tabla(tbTopes, new LineaTopePresupuesto(listaConceptos.get(0)),listaFiltros,this);
		tablaTopes.pintaTabla(lista);
		resize(null);
	}
	
	public ArrayList<Concepto> getListaConceptos(Sistema s, Proyecto p) {
		String codSistema = Sistema.getInstanceTotal().codigo;
		
		if (s!=null) {
			codSistema = s.codigo;
		}
		
		cbConceptos.getItems().removeAll(cbConceptos.getItems());
		
		HashMap<String,HashMap<String, Concepto>> lConceptos = new HashMap<String,HashMap<String, Concepto>>();
		
		Iterator<EstimacionAnio> itAnios = TopeImputaciones.ap.estimacionAnual.iterator();
		
		while (itAnios.hasNext()) {
			EstimacionAnio ea = itAnios.next();
			
			HashMap<String, Concepto> listaConceptos = ea.acumuladoPorSistemaConcepto(s);
			Iterator<Concepto> itConceptos = listaConceptos.values().iterator();
			
			while (itConceptos.hasNext()) {
				Concepto c = itConceptos.next();
				
				
				if (c.tipoConcepto.tipoGestionEconomica == MetaConcepto.GESTION_HORAS) {
					
					HashMap<String, Concepto> conceptosSistema = null;
					
					if (!lConceptos.containsKey(codSistema)) {
						conceptosSistema = new HashMap<String, Concepto>();
						lConceptos.put(codSistema, conceptosSistema);
					} else {
						conceptosSistema = lConceptos.get(codSistema);
					}
					
					Concepto cAux = c;
					
					if (!conceptosSistema.containsKey(c.tipoConcepto.codigo)) {
						cbConceptos.getItems().add(c.tipoConcepto);
						conceptosSistema.put(c.tipoConcepto.codigo, c);
						c.topeEstimacion = new ArrayList<EstimacionAnio>();
					} else {
						cAux = conceptosSistema.get(c.tipoConcepto.codigo);
					}
					
					EstimacionAnio eA = new EstimacionAnio();
					eA.anio = ea.anio;
					
					if (c.listaEstimaciones.size()>0) {
						eA.cantidad = c.listaEstimaciones.get(0).importe;
					} else {
						eA.cantidad = 0;
					}
					
					if (c.listaImputaciones.size()>0) {
						eA.cantidadImputada = c.listaImputaciones.get(0).importe;
					} else {
						eA.cantidadImputada = 0;
					}
					
					eA.concepto = cAux;
					eA.fechaInicio = new Date(eA.anio,0,1);
					eA.fechaFin = new Date(eA.anio+1,12,1);
					cAux.topeEstimacion.add(eA);
					cAux.s = Sistema.getInstanceTotal();
				}						
			}
		}
		
		Iterator<Sistema> itSistema = cbComboSistemas.getItems().iterator();
		
		while (itSistema.hasNext()) {
			s = itSistema.next();
			codSistema = s.codigo;
			itAnios = TopeImputaciones.ap.estimacionAnual.iterator();
			
			while (itAnios.hasNext()) {
				EstimacionAnio ea = itAnios.next();
				
				HashMap<String, Concepto> listaConceptos = ea.acumuladoPorSistemaConcepto(s);
				Iterator<Concepto> itConceptos = listaConceptos.values().iterator();
				
				while (itConceptos.hasNext()) {
					Concepto c = itConceptos.next();
					
					
					if (c.tipoConcepto.tipoGestionEconomica == MetaConcepto.GESTION_HORAS) {
						
						HashMap<String, Concepto> conceptosSistema = null;
						
						if (!lConceptos.containsKey(codSistema)) {
							conceptosSistema = new HashMap<String, Concepto>();
							lConceptos.put(codSistema, conceptosSistema);
						} else {
							conceptosSistema = lConceptos.get(codSistema);
						}
						
						Concepto cAux = c;
						
						if (!conceptosSistema.containsKey(c.tipoConcepto.codigo)) {
							conceptosSistema.put(c.tipoConcepto.codigo, c);
							c.topeEstimacion = new ArrayList<EstimacionAnio>();
						} else {
							cAux = conceptosSistema.get(c.tipoConcepto.codigo);
						}
						
						EstimacionAnio eA = new EstimacionAnio();
						eA.anio = ea.anio;
						
						if (c.listaEstimaciones.size()>0) {
							eA.cantidad = c.listaEstimaciones.get(0).importe;
						} else {
							eA.cantidad = 0;
						}
						
						if (c.listaImputaciones.size()>0) {
							eA.cantidadImputada = c.listaImputaciones.get(0).importe;
						} else {
							eA.cantidadImputada = 0;
						}
						
						eA.concepto = cAux;
						eA.fechaInicio = new Date(eA.anio,0,1);
						eA.fechaFin = new Date(eA.anio+1,12,1);
						cAux.topeEstimacion.add(eA);
						cAux.s = s;
					}						
				}
			}
		}
				
		ArrayList<Concepto> salidaLista = new ArrayList<Concepto>();		
				
		if (lConceptos.size()>0) {
			Iterator<HashMap<String, Concepto>> itLConceptos = lConceptos.values().iterator();
			while (itLConceptos.hasNext()) {
				HashMap<String, Concepto> sistema = itLConceptos.next();
				salidaLista.addAll(sistema.values());	
			}			
			cbConceptos.getItems().add(MetaConcepto.getTotal());
			
			return salidaLista;
		}
		
		
		return 	null;	
	}
	
	
	
	

}
