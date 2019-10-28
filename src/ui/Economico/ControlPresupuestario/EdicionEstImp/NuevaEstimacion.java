package ui.Economico.ControlPresupuestario.EdicionEstImp;

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
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.Concepto;
import model.beans.Estimacion;
import model.beans.EstimacionAnio;
import model.beans.EstimacionMes;
import model.beans.Imputacion;
import model.beans.Parametro;
import model.beans.ParametroRecurso;
import model.beans.Proveedor;
import model.beans.Recurso;
import model.beans.RelRecursoSistema;
import model.beans.Tarifa;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaGerencia;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Economico.ControlPresupuestario.ControlPresupuestario;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteEvolucion;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteUsuario;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class NuevaEstimacion implements ControladorPantalla {
	
	public static NuevaEstimacion elementoThis = null;
	
	public static final int MODO_INSERTAR = 0;
	public static final int MODO_MODIFICAR = 1;
	public static final int MODO_ELIMINAR = 2;
	public static final int MODO_COPIAR_DRCHA = 3;
	
	public static final int CARGA_TIPO_POR_DEFECTO = 0;
	public static final int CARGA_TIPO_ESTIMACION = 1;
	public static final int CARGA_TIPO_IMPUTACION = 2;
	
	public Concepto c = null;
	public int modo = 0; 
	
   @FXML
    private ComboBox<String> cbTipo;

    @FXML
    private ComboBox<Recurso> cbRecurso;

    @FXML
    private ComboBox<Tarifa> cbTarifa;

    @FXML
    private ComboBox<Sistema> cbSistema;

    @FXML
    private ComboBox<MetaConcepto> cbNatCoste;

    @FXML
    private ComboBox<MetaGerencia> cbGerencia;

    @FXML
    private TextField tfHoras;

    @FXML
    private TextField tfCoste;

    @FXML
    private ComboBox<String> cbMes;
    
    @FXML
    private CheckBox ckbIncNoPred;

    @FXML
    private CheckBox ckbActPred;
    

    @FXML
    private TitledPane tDatos;

    @FXML
    private TableView<Tableable> tResumen;

    @FXML
    private TableView<Tableable> tResumenD;

    @FXML
    private TitledPane tSimulacion;

    @FXML
    private ImageView imAnalizar;
    private GestionBotones gbAnalizar;

    @FXML
    private ImageView imGuardarBorrar;
    private GestionBotones gbGuardarBorrar;

    @FXML
    private ImageView imGuardarEditar;
    private GestionBotones gbGuardarEditar;
    
    @FXML
    private ImageView imGuardarMas;
    private GestionBotones gbGuardarMas;
    
    @FXML
    private Accordion acSimulacion;
    
    public HashMap<String, Object> variablesPaso = null;
    
    public boolean inhibirCombos = false;
    
    @Override
	public void resize(Scene escena) {
		
	}

	public NuevaEstimacion() {
		super();
		NuevaEstimacion.elementoThis = this;
	}

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/EdicionEstImp/NuevaEstimacion.fxml"; 
	
	@Override
	public AnchorPane getAnchor() {
		return null;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		cargaCombos();
		
		gbAnalizar = new GestionBotones(imAnalizar, "Analizar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				NuevaEstimacion.elementoThis.analizaCambios();
            } }, "Analizar Slot de destino", this);	
		gbGuardarMas = new GestionBotones(imGuardarMas, "GuardarAniadir3", false, new EventHandler<MouseEvent>() {        
    			@Override
                public void handle(MouseEvent t)
                { try { 
                	NuevaEstimacion.elementoThis.inserta();
                	ControlPresupuestario.salvaPosicionActual();
                	ParamTable.po.hide();
                	Dialogo.alert("Alta de "+NuevaEstimacion.elementoThis.cbTipo.getValue(), "Guardado Correcto", "Se dio de alta el elemento.");
                	ControlPresupuestario.cargaPosicionActual();
                } catch (Exception e) {
                	Dialogo.error("Alta de "+NuevaEstimacion.elementoThis.cbTipo.getValue(), "Error al guardar", "Se produjo un error al guardar el elemento");
                	e.printStackTrace();}} 
    			}, "Guardar Alta");
		
		gbGuardarBorrar = new GestionBotones(imGuardarBorrar, "GuardarBorrar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            { try { 
            	NuevaEstimacion.elementoThis.elimina();
            	ControlPresupuestario.salvaPosicionActual();
            	ParamTable.po.hide();
            	Dialogo.alert("Borrado de "+NuevaEstimacion.elementoThis.cbTipo.getValue(), "Eliminación Correcto", "Se eliminó el elemento.");
            	ControlPresupuestario.cargaPosicionActual();
            } catch (Exception e) {
            	Dialogo.error("Borrado de "+NuevaEstimacion.elementoThis.cbTipo.getValue(), "Error al eliminar", "Se produjo un error al dar de baja el elemento");
            	e.printStackTrace();}} 
			}, "Guardar Borrado");
		
		gbGuardarEditar = new GestionBotones(imGuardarEditar, "GuardarEditar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            { try { 
            	NuevaEstimacion.elementoThis.modifica();
            	ControlPresupuestario.salvaPosicionActual();
            	ParamTable.po.hide();
            	Dialogo.alert("Edición de "+NuevaEstimacion.elementoThis.cbTipo.getValue(), "Modificación guardada de forma Correcta", "Se modificó el elemento.");
            	ControlPresupuestario.cargaPosicionActual();
            } catch (Exception e) {
            	Dialogo.error("Edición de "+NuevaEstimacion.elementoThis.cbTipo.getValue(), "Error al guardar la modificación", "Se produjo un error al modificar el elemento");
            	e.printStackTrace();}} 
			}, "Guardar Modificación");
		
		gbAnalizar.desActivarBoton();
		gbGuardarMas.desActivarBoton();
		gbGuardarEditar.desActivarBoton();
		
		acSimulacion.getPanes().get(1).setExpanded(false);
		acSimulacion.getPanes().get(0).setExpanded(true);
	}
	
	public void fijaModo(int modo) {
		this.modo = modo;
		
		if (this.modo == NuevaEstimacion.MODO_INSERTAR) {
			gbGuardarMas.visibleBoton(true);
			gbGuardarBorrar.visibleBoton(false);
			gbGuardarEditar.visibleBoton(false);
		}
		if (this.modo == NuevaEstimacion.MODO_ELIMINAR) {
			gbGuardarMas.visibleBoton(false);
			gbGuardarBorrar.visibleBoton(true);
			gbGuardarEditar.visibleBoton(false);
		}
		if (this.modo == NuevaEstimacion.MODO_MODIFICAR) {
			gbGuardarMas.visibleBoton(false);
			gbGuardarBorrar.visibleBoton(false);
			gbGuardarEditar.visibleBoton(true);
		}
		if (this.modo == NuevaEstimacion.MODO_COPIAR_DRCHA) {
			gbGuardarMas.visibleBoton(true);
			gbGuardarBorrar.visibleBoton(false);
			gbGuardarEditar.visibleBoton(false);
			gbAnalizar.activarBoton();
			gbGuardarMas.activarBoton();
		}
	}
	
	public void cargaCombos(){
		ArrayList<String> listaTipos = new ArrayList<String>();
		listaTipos.add("Estimación");
		listaTipos.add("Imputación");
		
		cbTipo.getItems().addAll(listaTipos);
		cbTipo.setValue("Estimación");
		
		cbTipo.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			try {
				if (this.modo == NuevaEstimacion.MODO_ELIMINAR) {
					if (newValue.equals("Estimación")) {
						this.cargaEliminacion(this.c, NuevaEstimacion.CARGA_TIPO_ESTIMACION);
					} else {
						this.cargaEliminacion(this.c, NuevaEstimacion.CARGA_TIPO_IMPUTACION);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
	    }
	    );
		
		this.cbRecurso.getItems().addAll(Recurso.listadoRecursosEstatico().values());

		cbTarifa.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			if (cbTarifa.getValue()!=null) {
				if (!this.tfCoste.getText().equals("")) {
					Tarifa tar = this.cbTarifa.getValue();
					try {
						float horas = (Float)FormateadorDatos.parseaDato(this.tfCoste.getText(),FormateadorDatos.FORMATO_MONEDA)/tar.costeHora;
						this.tfHoras.setText(FormateadorDatos.formateaDato(horas,FormateadorDatos.FORMATO_REAL));
					} catch (Exception e) {
						
					}
				} else {
					Tarifa tar = this.cbTarifa.getValue();
					try {
						float coste = tar.costeHora*(Float)FormateadorDatos.parseaDato(this.tfHoras.getText(),FormateadorDatos.FORMATO_REAL);
						this.tfCoste.setText(FormateadorDatos.formateaDato(coste,FormateadorDatos.FORMATO_MONEDA));
					} catch (Exception e) {
						try {
							this.tfCoste.setText(FormateadorDatos.formateaDato(0,FormateadorDatos.FORMATO_MONEDA));
						} catch (Exception ex){}
						
					}
				}
			}
		});
		
		cbRecurso.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			Tarifa t = new Tarifa();
			ArrayList<Tarifa> lTarifas = new ArrayList<Tarifa>();
			try {
				ParametroRecurso pr = (ParametroRecurso) cbRecurso.getValue().getValorParametro(MetaParametro.RECURSO_PROVEEDOR);
				if (pr!=null && pr.valorObjeto!=null) {
					Proveedor pAux = (Proveedor) pr.getValor();
					
					HashMap<String,Object> filtros = new HashMap<String,Object>();
					filtros.put(Tarifa.filtro_PROVEEDOR, pAux);
					lTarifas = new Tarifa().listado(filtros);
					
				}
				
				cbTarifa.getItems().removeAll(cbTarifa.getItems());
				cbTarifa.getItems().addAll(lTarifas);
			
			
				Tarifa tAux = Tarifa.tarifaPorDefecto(cbRecurso.getValue(), null, false);
				if (tAux!=null) 
					cbTarifa.setValue(tAux);
			} catch (Exception e) {
				
			}
			
			Recurso r = this.cbRecurso.getValue();
			try {
				Concepto conc = ((LineaCosteUsuario)this.variablesPaso.get("filaDatos")).concepto;
				int aBuscar = conc.tipoConcepto.id;
				
				if (MetaConcepto.listado.get(MetaConcepto.TREI) == (MetaConcepto)r.getValorParametro(MetaParametro.RECURSO_NAT_COSTE)) {
					aBuscar = 1;					
				}
				Iterator<MetaConcepto> itMGerencia = this.cbNatCoste.getItems().iterator();
				
				while (itMGerencia.hasNext()) {
					MetaConcepto g = itMGerencia.next();
					if (g.id == aBuscar) {
						cbNatCoste.setValue(g);
						break;
					}
				}
			}catch (Exception e){}
			
			actualizaSistemasPredeterminados (this.ckbIncNoPred.isSelected());
	    	}
	    );
		
		actualizaSistemasPredeterminados (this.ckbIncNoPred.isSelected());
		cbNatCoste.getItems().addAll(MetaConcepto.listado.values());
		cbGerencia.getItems().addAll(MetaGerencia.listado.values());	
		
        Iterator<EstimacionAnio> itea = ControlPresupuestario.ap.estimacionAnual.iterator();
		
		while (itea.hasNext()) {
			EstimacionAnio ea = itea.next();
			
			for (int i=0;i<12;i++) {
				try {
					EstimacionMes em = ea.estimacionesMensuales.get(i);
					
					if (em!=null) {
						String valor = Constantes.nomMes(em.mes-1) + "'" + ea.anio;
						cbMes.getItems().add(valor);
					}
				} catch (Exception e) {
					
				}
			}
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(Constantes.fechaActual());
		
		String valor = Constantes.nomMes(c.get(Calendar.MONTH)) + "'" + c.get(Calendar.YEAR);

		if (cbMes.getItems().contains(valor))
			cbMes.setValue(valor);
		else {
			cbMes.setValue(cbMes.getItems().get(cbMes.getItems().size()-1));
		}
		
		this.tfCoste.focusedProperty().addListener((ov, oldV, newV) -> { 
			if (!newV) { 
				try {
					this.tfCoste.setText(FormateadorDatos.formateaDato(this.tfCoste.getText(),FormateadorDatos.FORMATO_MONEDA));
				} catch (Exception e) {
					try {
						this.tfCoste.setText(FormateadorDatos.formateaDato("0",FormateadorDatos.FORMATO_MONEDA));}
					catch (Exception ex) {}
				}
				if (this.cbTarifa.getValue()!=null) {
					Tarifa tar = this.cbTarifa.getValue();
					try {
						float horas = (Float)FormateadorDatos.parseaDato(this.tfCoste.getText(),FormateadorDatos.FORMATO_MONEDA)/tar.costeHora;
						this.tfHoras.setText(FormateadorDatos.formateaDato(horas,FormateadorDatos.FORMATO_REAL));
					} catch (Exception e) {
						
					}
				}  
		} 
			if (!"".equals(this.tfCoste.getText())) {
				gbAnalizar.activarBoton();
				gbGuardarMas.activarBoton();
				gbGuardarEditar.activarBoton();
			} else {
				gbAnalizar.desActivarBoton();
				gbGuardarMas.desActivarBoton();
				gbGuardarEditar.desActivarBoton();
			}
		});
		this.tfHoras.focusedProperty().addListener((ov, oldV, newV) -> { 
			if (!newV) { 
				try {
					this.tfHoras.setText(FormateadorDatos.formateaDato(this.tfHoras.getText(),FormateadorDatos.FORMATO_REAL));
				} catch (Exception e) {
					try {
						this.tfHoras.setText(FormateadorDatos.formateaDato("0",FormateadorDatos.FORMATO_REAL));}
					catch (Exception ex) {}
				}
				if (this.cbTarifa.getValue()!=null) {
					Tarifa tar = this.cbTarifa.getValue();
					try {
						float coste = tar.costeHora*(Float)FormateadorDatos.parseaDato(this.tfHoras.getText(),FormateadorDatos.FORMATO_REAL);
						this.tfCoste.setText(FormateadorDatos.formateaDato(coste,FormateadorDatos.FORMATO_MONEDA));
					} catch (Exception e) {
						
					}
				}    
		} 
			if (!"".equals(this.tfCoste.getText())) {
				gbAnalizar.activarBoton();
				gbGuardarMas.desActivarBoton();
				gbGuardarEditar.activarBoton();
			} else {
				gbAnalizar.desActivarBoton();
				gbGuardarMas.desActivarBoton();
				gbGuardarEditar.desActivarBoton();
			}
			
		});
		this.ckbIncNoPred.selectedProperty().addListener((ov, oldV, newV) -> { 
			actualizaSistemasPredeterminados(newV);    
		});
	}
	
	public void modifica() throws Exception{
		
		if (!"Imputación".equals(cbTipo.getValue())) {
			int id = this.c.listaEstimaciones.get(0).id;
			
			ArrayList<Estimacion> listaEstimaciones = generaEstimacion(id);
			Iterator<Estimacion> itEst = listaEstimaciones.iterator();
					
			while (itEst.hasNext()) {
				Estimacion est = (Estimacion) itEst.next();
				est.modificaEstimacion(null);
			}
		}
		
		if ("Imputación".equals(cbTipo.getValue())) {
			int id = this.c.listaImputaciones.get(0).id;
	
			ArrayList<Imputacion> listaImputaciones = generaImputacion(id);
			Iterator<Imputacion> itImp = listaImputaciones.iterator();
					
			while (itImp.hasNext()) {
				Imputacion imp = (Imputacion) itImp.next();
				imp.modificaImputacion(null);
			}	
		}
	}
	
	public void elimina() throws Exception{
		
		if (!"Imputación".equals(cbTipo.getValue())) {
			int id = this.c.listaEstimaciones.get(0).id;
			
			ArrayList<Estimacion> listaEstimaciones = generaEstimacion(id);
			Iterator<Estimacion> itEst = listaEstimaciones.iterator();
					
			while (itEst.hasNext()) {
				Estimacion est = (Estimacion) itEst.next();
				est.borraEstimacion(null);
			}
		}		
		
		if ("Imputación".equals(cbTipo.getValue())) {
			int id = this.c.listaImputaciones.get(0).id;
			
			ArrayList<Imputacion> listaImputaciones = generaImputacion(id);
			Iterator<Imputacion> itImp = listaImputaciones.iterator();
					
			while (itImp.hasNext()) {
				Imputacion imp = (Imputacion) itImp.next();
				imp.borraImputacion(null);
			}
		}
	
				
	}
	
	public void inserta() throws Exception{
		ArrayList<Estimacion> listaEstimaciones = generaEstimacion(0);
		Iterator<Estimacion> itEst = listaEstimaciones.iterator();
				
		while (itEst.hasNext()) {
			Estimacion est = (Estimacion) itEst.next();
			if (est.existeEstimacion(est)) {
				Dialogo.error("Analizador Cambios", "Estimación existente", "La estimación no se va a poder insertar al ya existir un elemento para el mismo recurso.");
				return;
			}
			est.insertEstimacion(null);
		}		
	
		ArrayList<Imputacion> listaImputaciones = generaImputacion(0);
		Iterator<Imputacion> itImp = listaImputaciones.iterator();
				
		while (itImp.hasNext()) {
			Imputacion imp = (Imputacion) itImp.next();
			if (imp.existeImputacion(imp)) {
				Dialogo.error("Analizador Cambios", "Imputación existente", "La imputación no se va a poder insertar al ya existir un elemento para el mismo recurso.");
				return;
			}
			imp.insertImputacion(null);
		}		
	}
	
	private void actualizaSistemasPredeterminados (boolean valor) {
		cbSistema.getItems().removeAll(cbSistema.getItems());

		if (valor==true || this.cbRecurso.getValue()==null){
			cbSistema.getItems().addAll(Sistema.listado.values());
			if (this.variablesPaso!=null) {
				Concepto conc = ((LineaCosteUsuario)this.variablesPaso.get("filaDatos")).concepto;
				
				Iterator<Sistema> itSistema = this.cbSistema.getItems().iterator();
				
				while (itSistema.hasNext()) {
					Sistema s = itSistema.next();
					if (s.id == conc.s.id){
						cbSistema.setValue(s);
						break;
					}
				}
			}		
		} else {
			cbSistema.getItems().addAll(Sistema.listado.values());
						
			RelRecursoSistema rrs = new RelRecursoSistema();
			rrs.recurso = cbRecurso.getValue();
			ArrayList<Sistema> lSistemas = new ArrayList<Sistema>();
			lSistemas.addAll(cbSistema.getItems());
			Sistema sAux = rrs.getMejorSistema(lSistemas, null, -1, -1);
			
			if (cbSistema.getItems().size()>0) {
				cbSistema.setValue(sAux);
			}
		}
			
	}
	
	public void prefijaValores(){
		Concepto conc = ((LineaCosteUsuario)this.variablesPaso.get("filaDatos")).concepto;
		
		Iterator<Sistema> itSistema = this.cbSistema.getItems().iterator();
		
		while (itSistema.hasNext()) {
			Sistema s = itSistema.next();
			if (s.id == conc.s.id){
				cbSistema.setValue(s);
				break;
			}
		}
		
		Iterator<MetaConcepto> itMConcepto = this.cbNatCoste.getItems().iterator();
		
		while (itMConcepto.hasNext()) {
			MetaConcepto c = itMConcepto.next();
			if (c.id == conc.tipoConcepto.id) {
				cbNatCoste.setValue(c);
				break;
			}
		}
		
		int aBuscar = -1;
		
		if (conc.tipoConcepto.id==1) {
			aBuscar = 1;
		}
		if (conc.tipoConcepto.id==2) {
			aBuscar = 1;
		}
		if (conc.tipoConcepto.id==3) {
			aBuscar = 2;
		}
		if (conc.tipoConcepto.id==4) {
			aBuscar = 3;
		}
		if (conc.tipoConcepto.id==5) {
			aBuscar = 2;
		}
		
		Iterator<MetaGerencia> itMGerencia = this.cbGerencia.getItems().iterator();
		
		while (itMGerencia.hasNext()) {
			MetaGerencia g = itMGerencia.next();
			if (g.id == aBuscar) {
				cbGerencia.setValue(g);
				break;
			}
		}
		
		this.cbMes.setValue(ControlPresupuestario.mesActual);
		
	}
	
	public boolean formularioInformado() {
		if (!"".equals(this.tfCoste.getText()))
			return false;
		else
			return true;
	}
	
	public Date calculaFecha() {
		String valor = this.cbMes.getValue();
		String[] cadenas = valor.split("'");
		
		int mes = Constantes.numMes(cadenas[0]);
		int anio = new Integer(cadenas[1]);
		
		Date d = null;
		
		try {
			d = Constantes.inicioMes(mes, anio);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return d;
	}
	
	public ArrayList<Estimacion> generaEstimacion(int id) {
		ArrayList<Estimacion> lEstimacion = new ArrayList<Estimacion>();
		
		if ("Estimación".equals(cbTipo.getValue())) {
			Estimacion e = new Estimacion();
			
			Calendar c = Calendar.getInstance();
			c.setTime(calculaFecha());
			
			if (this.modo== NuevaEstimacion.MODO_MODIFICAR || this.modo== NuevaEstimacion.MODO_ELIMINAR) {
				e.id = id;
			}
			
			e.fxInicio = c.getTime();
			c.add(Calendar.MONTH, 1);
			c.add(Calendar.DAY_OF_YEAR, -1);
			e.fxFin = c.getTime();
			e.gerencia = this.cbGerencia.getValue();
			try { e.horas = (Float) FormateadorDatos.parseaDato(this.tfHoras.getText(), FormateadorDatos.FORMATO_REAL); } catch (Exception ex) { e.horas = 0;}
			try { e.importe = (Float) FormateadorDatos.parseaDato(this.tfCoste.getText(), FormateadorDatos.FORMATO_MONEDA); } catch (Exception ex) { e.importe = 0;}
			e.natCoste = this.cbNatCoste.getValue();
			e.proyecto = ControlPresupuestario.ap.proyecto;
			e.recurso = this.cbRecurso.getValue();
			e.sistema = this.cbSistema.getValue();
			e.modo = this.modo;
			
			if (this.modo== NuevaEstimacion.MODO_COPIAR_DRCHA) {
				e.modo = NuevaEstimacion.MODO_INSERTAR;
			}
			
			lEstimacion.add(e);
		}
		
		return lEstimacion;
	}
	
	public ArrayList<Imputacion> generaImputacion(int id) {
		ArrayList<Imputacion> lImputacion = new ArrayList<Imputacion>();
		
		if ("Imputación".equals(cbTipo.getValue())) {
			Imputacion i = new Imputacion();
			
			Calendar c = Calendar.getInstance();
			c.setTime(calculaFecha());
			
			if (this.modo== NuevaEstimacion.MODO_MODIFICAR || this.modo== NuevaEstimacion.MODO_ELIMINAR) {
				i.id = id;
			}
			
			i.fxInicio = c.getTime();
			c.add(Calendar.MONTH, 1);
			c.add(Calendar.DAY_OF_YEAR, -1);
			i.fxFin = c.getTime();
			i.gerencia = this.cbGerencia.getValue();
			try { i.setHoras((Float) FormateadorDatos.parseaDato(this.tfHoras.getText(), FormateadorDatos.FORMATO_REAL)); } catch (Exception ex) { i.setHoras(0);}
			try { i.setImporte((Float) FormateadorDatos.parseaDato(this.tfCoste.getText(), FormateadorDatos.FORMATO_MONEDA)); } catch (Exception ex) { i.setImporte(0);}
			i.natCoste = this.cbNatCoste.getValue();
			i.proyecto = ControlPresupuestario.ap.proyecto;
			i.recurso = this.cbRecurso.getValue();
			i.sistema = this.cbSistema.getValue();
			i.tarifa = this.cbTarifa.getValue();
			i.modo = this.modo;
			
			lImputacion.add(i);
		}
		
		return lImputacion;
	}
	
	private void analizaCambios() {
		ArrayList<Estimacion> lEstimacion = new ArrayList<Estimacion>();
		ArrayList<Imputacion> lImputacion = new ArrayList<Imputacion>();
		
		if (this.modo == NuevaEstimacion.MODO_MODIFICAR) {
			if ("Imputación".equals(cbTipo.getValue())) {
				lImputacion = generaImputacion(this.c.listaImputaciones.get(0).id);
			} else {
				lEstimacion = generaEstimacion(this.c.listaEstimaciones.get(0).id);
			}	
		}
		
		if (this.modo == NuevaEstimacion.MODO_INSERTAR || this.modo == NuevaEstimacion.MODO_COPIAR_DRCHA) {
			lEstimacion = generaEstimacion(0);
			lImputacion = generaImputacion(0);
			
			Iterator<Estimacion> itEst = lEstimacion.iterator();
			
			while (itEst.hasNext()) {
				Estimacion est = (Estimacion) itEst.next();
				if (est.existeEstimacion(est)) {
					Dialogo.error("Analizador Cambios", "Estimación existente", "La estimación no se va a poder insertar al ya existir un elemento para el mismo recurso.");
					return;
				}
			}		
		
			Iterator<Imputacion> itImp = lImputacion.iterator();
					
			while (itImp.hasNext()) {
				Imputacion imp = (Imputacion) itImp.next();
				if (imp.existeImputacion(imp)) {
					Dialogo.error("Analizador Cambios", "Imputación existente", "La imputación no se va a poder insertar al ya existir un elemento para el mismo recurso.");
					return;
				}
			}		
		}
		
		if (this.modo == NuevaEstimacion.MODO_ELIMINAR) {
			if (this.cbTipo.getValue().equals("Estimación")) {
				lEstimacion.add(this.c.listaEstimaciones.get(0));
				this.c.listaEstimaciones.get(0).modo = NuevaEstimacion.MODO_ELIMINAR;
			} else {
				lImputacion.add(this.c.listaImputaciones.get(0));
				this.c.listaImputaciones.get(0).modo = NuevaEstimacion.MODO_ELIMINAR;
			}
		}	
		
		AnalizadorPresupuesto ap = new AnalizadorPresupuesto();
		ap.construyePresupuestoMensualizado(ControlPresupuestario.ap.presupuesto, ControlPresupuestario.ap.fechaPivote, lEstimacion, lImputacion, null,null);
		
		acSimulacion.getPanes().get(0).setExpanded(false);
		acSimulacion.getPanes().get(1).setExpanded(true);
		
		Sistema s = cbSistema.getValue();
		s  = calculaIndicadores(ControlPresupuestario.ap,s);
		
		tSimulacion.setDisable(false);
		
		ArrayList<Object> lista = new ArrayList<Object>();
		lista.add(new LineaCosteEvolucion(s));
		ObservableList<Tableable> dataTable = (new LineaCosteEvolucion()).toListTableable(lista);
		tResumen.setItems(dataTable);
		(new LineaCosteEvolucion()).fijaColumnas(tResumen);

		tResumen.setPrefHeight(40+40*lista.size());
		tResumen.setPrefWidth(600);
		
		Sistema s2 = s.clone();
		s2  = calculaIndicadores(ap,s2);
		
		lista = new ArrayList<Object>();
		lista.add(new LineaCosteEvolucion(s2));
		dataTable = (new LineaCosteEvolucion()).toListTableable(lista);
		tResumenD.setItems(dataTable);
		(new LineaCosteEvolucion()).fijaColumnas(tResumenD);

		tResumenD.setPrefHeight(40+40*lista.size());
		tResumenD.setPrefWidth(600);
		
	}
	
	private Sistema calculaIndicadores(AnalizadorPresupuesto ap, Sistema s) {
		
		s.listaConceptos = new HashMap<String,Concepto>();
		
		Calendar c = Calendar.getInstance();
		c.setTime(calculaFecha());
		
		int anio = c.get(Calendar.YEAR);
		
		HashMap<String, Concepto> lista = ap.acumuladoPorSistemaConcepto(s, anio);
		
		Concepto cAux = lista.get(this.cbNatCoste.getValue().codigo);
		if ("Estimación".equals(cbTipo.getValue())) {
			if (cAux.listaEstimaciones.size()>0)
				cAux.valor += cAux.listaEstimaciones.get(0).importe;
		}	else {
			if (cAux.listaImputaciones.size()>0)
				cAux.valor += cAux.listaImputaciones.get(0).getImporte();
		}
		s.listaConceptos.put(LineaCosteEvolucion.AC, cAux);
		
		Iterator<Concepto> it = lista.values().iterator();
		Concepto cAux2 = new Concepto();
		while (it.hasNext()) {
			Concepto  cAux3= it.next();
			if ("Estimación".equals(cbTipo.getValue())) {
				if (cAux3.listaEstimaciones.size()>0)
					cAux2.valor += cAux3.listaEstimaciones.get(0).importe;
			}	else {
				if (cAux3.listaImputaciones.size()>0)
					cAux2.valor += cAux3.listaImputaciones.get(0).getImporte();
			}		
		}
		s.listaConceptos.put(LineaCosteEvolucion.AT, cAux2);
		
		lista = ap.acumuladoPorSistemaConcepto(s, -1);
		
		cAux = lista.get(this.cbNatCoste.getValue().codigo);
		if ("Estimación".equals(cbTipo.getValue())) {
			if (cAux.listaEstimaciones.size()>0)
				cAux.valor += cAux.listaEstimaciones.get(0).importe;
		}	else {
			if (cAux.listaImputaciones.size()>0)
				cAux.valor += cAux.listaImputaciones.get(0).getImporte();
		}
		s.listaConceptos.put(LineaCosteEvolucion.TC, cAux);
		
		it = lista.values().iterator();
		cAux2 = new Concepto();
		while (it.hasNext()) {
			Concepto  cAux3= it.next();
			if ("Estimación".equals(cbTipo.getValue())) {
				if (cAux3.listaEstimaciones.size()>0)
					cAux2.valor += cAux3.listaEstimaciones.get(0).importe;
			}	else {
				if (cAux3.listaImputaciones.size()>0)
					cAux2.valor += cAux3.listaImputaciones.get(0).getImporte();
			}
		}
		s.listaConceptos.put(LineaCosteEvolucion.TT, cAux2);
		
		return s;
	}
	
	public void setValueComboRecursos(Recurso r) {
		Iterator<Recurso> itRecurso = this.cbRecurso.getItems().iterator();
		Recurso raux=null;
		while (itRecurso.hasNext()) {
			raux = itRecurso.next();
			if (raux.id == r.id) {
				break;
			}
		}
		this.cbRecurso.setValue(raux);
	}
	
	public void setValueComboTarifa(Tarifa t) {
		this.cbTarifa.setValue(t);
	}
	
	public void setValueComboSistema(Sistema s) {
		Iterator<Sistema> itSistema = this.cbSistema.getItems().iterator();
		Sistema saux = null;
		while (itSistema.hasNext()) {
			saux = itSistema.next();
			if (saux.id == s.id) {
				break;
			}
		}
		this.cbSistema.setValue(saux);
	}
	
	public void setValueComboNatCoste(MetaConcepto mc) {
		Iterator<MetaConcepto> itMetaConcepto = this.cbNatCoste.getItems().iterator();
		MetaConcepto mcaux = null;
		while (itMetaConcepto.hasNext()) {
			mcaux = itMetaConcepto.next();
			if (mcaux.id == mc.id) {
				break;
			}
		}
		this.cbNatCoste.setValue(mcaux);
	}
	
	public void setValueComboGerencia(MetaGerencia g) {
		Iterator<MetaGerencia> itMetaGerencia = this.cbGerencia.getItems().iterator();
		MetaGerencia gaux = null;
		while (itMetaGerencia.hasNext()) {
			gaux = itMetaGerencia.next();
			if (gaux.id == g.id) {
				break;
			}
		}
		this.cbGerencia.setValue(gaux);
	}
	
	public void cargaEliminacion(Concepto c, int tipoForzado) throws Exception {
		this.c = c;
		
		if (c.r!=null)
			setValueComboRecursos(c.r);
		
		if (c.s!=null)
			setValueComboSistema(c.s);
		
		this.cbRecurso.setDisable(true);
		this.cbTarifa.setDisable(true);	
		this.cbSistema.setDisable(true);
		this.ckbActPred.setDisable(true);
		this.ckbIncNoPred.setDisable(true);
		this.cbNatCoste.setDisable(true);
		this.cbGerencia.setDisable(true);
		this.tfHoras.setDisable(true);
		this.tfCoste.setDisable(true);
		this.cbMes.setDisable(true);
		
		boolean cargaEstimacion = false;
		
		if ((c.listaEstimaciones.size()>0 && tipoForzado == NuevaEstimacion.CARGA_TIPO_POR_DEFECTO) || tipoForzado == NuevaEstimacion.CARGA_TIPO_ESTIMACION) {
			cargaEstimacion = true;
		}
		if ((c.listaEstimaciones.size()==0 && tipoForzado == NuevaEstimacion.CARGA_TIPO_POR_DEFECTO) || tipoForzado == NuevaEstimacion.CARGA_TIPO_IMPUTACION) {
			cargaEstimacion = false;
		}
		
		if (cargaEstimacion) {
			Estimacion est = c.listaEstimaciones.get(0);
			this.cbTipo.setValue("Estimación");	
			setValueComboNatCoste(c.tipoConcepto);
			if (est.gerencia!=null)
				setValueComboGerencia(est.gerencia);
			this.tfHoras.setText(FormateadorDatos.formateaDato(new Float(est.horas),FormateadorDatos.FORMATO_REAL));
			this.tfCoste.setText(FormateadorDatos.formateaDato(new Float(est.importe),FormateadorDatos.FORMATO_MONEDA));
		} else {
			Imputacion imp = c.listaImputaciones.get(0);
			this.cbTipo.setValue("Imputación");
			setValueComboTarifa(imp.tarifa);
			setValueComboNatCoste(c.tipoConcepto);
			if (imp.gerencia!=null)
				setValueComboGerencia(imp.gerencia);
			this.tfHoras.setText(FormateadorDatos.formateaDato(new Float(imp.getHoras()),FormateadorDatos.FORMATO_REAL));
			this.tfCoste.setText(FormateadorDatos.formateaDato(new Float(imp.getImporte()),FormateadorDatos.FORMATO_MONEDA));
		}
		
		if (c.listaEstimaciones.size()==0 || c.listaImputaciones.size()==0) {
			this.cbTipo.setDisable(true);
		}
		
		gbAnalizar.activarBoton();
				
	}
	
	public void cargaModificacion(Concepto c, int tipoForzado) throws Exception {
		this.c = c;
		
		if (c.r!=null)
			setValueComboRecursos(c.r);
		
		if (c.s!=null)
			setValueComboSistema(c.s);

		boolean cargaEstimacion = false;
		
		if ((c.listaEstimaciones.size()>0 && tipoForzado == NuevaEstimacion.CARGA_TIPO_POR_DEFECTO) || tipoForzado == NuevaEstimacion.CARGA_TIPO_ESTIMACION) {
			cargaEstimacion = true;
		}
		if ((c.listaEstimaciones.size()==0 && tipoForzado == NuevaEstimacion.CARGA_TIPO_POR_DEFECTO) || tipoForzado == NuevaEstimacion.CARGA_TIPO_IMPUTACION) {
			cargaEstimacion = false;
		}
		
		if (cargaEstimacion) {
			Estimacion est = c.listaEstimaciones.get(0);
			this.cbTipo.setValue("Estimación");	
			setValueComboNatCoste(c.tipoConcepto);
			if (est.gerencia!=null)
				setValueComboGerencia(est.gerencia);
			this.tfHoras.setText(FormateadorDatos.formateaDato(new Float(est.horas),FormateadorDatos.FORMATO_REAL));
			this.tfCoste.setText(FormateadorDatos.formateaDato(new Float(est.importe),FormateadorDatos.FORMATO_MONEDA));
		} else {
			Imputacion imp = c.listaImputaciones.get(0);
			this.cbTipo.setValue("Imputación");
			setValueComboTarifa(imp.tarifa);
			setValueComboNatCoste(c.tipoConcepto);
			if (imp.gerencia!=null)
				setValueComboGerencia(imp.gerencia);
			this.tfHoras.setText(FormateadorDatos.formateaDato(new Float(imp.getHoras()),FormateadorDatos.FORMATO_REAL));
			this.tfCoste.setText(FormateadorDatos.formateaDato(new Float(imp.getImporte()),FormateadorDatos.FORMATO_MONEDA));
		}
		
		if (c.listaEstimaciones.size()==0 || c.listaImputaciones.size()==0) {
			this.cbTipo.setDisable(true);
		}
				
	}
	
	public void cargaCopiaDrcha(Concepto c, int tipoForzado) throws Exception {
		this.c = c;
		
		if (c.r!=null)
			setValueComboRecursos(c.r);
		
		this.cbRecurso.setDisable(true);
		
		if (c.s!=null)
			setValueComboSistema(c.s);
		
		this.cbSistema.setDisable(true);
		this.cbTarifa.setDisable(true);
		this.cbNatCoste.setDisable(true);
		this.cbGerencia.setDisable(true);
		this.cbMes.setDisable(true);
		this.ckbActPred.setDisable(true);
		this.ckbIncNoPred.setDisable(true);
		this.tfHoras.setDisable(true);
		this.tfCoste.setDisable(true);
		
		Imputacion imp = c.listaImputaciones.get(0);
		this.cbTipo.setValue("Estimación");
		setValueComboTarifa(imp.tarifa);
		setValueComboNatCoste(c.tipoConcepto);
		if (imp.gerencia!=null)
			setValueComboGerencia(imp.gerencia);
		this.tfHoras.setText(FormateadorDatos.formateaDato(new Float(imp.getHoras()),FormateadorDatos.FORMATO_REAL));
		this.tfCoste.setText(FormateadorDatos.formateaDato(new Float(imp.getImporte()),FormateadorDatos.FORMATO_MONEDA));

		this.cbTipo.setDisable(true);
				
	}
			
}
