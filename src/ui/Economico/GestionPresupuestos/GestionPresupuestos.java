package ui.Economico.GestionPresupuestos;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.PopOver;

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
import javafx.scene.layout.Pane;
import model.beans.ApunteContable;
import model.beans.BaseCalculoConcepto;
import model.beans.Concepto;
import model.beans.Coste;
import model.beans.ParametroProyecto;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.beans.RelProyectoDemanda;
import model.constantes.Constantes;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaParamProyecto;
import model.metadatos.Sistema;
import model.metadatos.TipoPresupuesto;
import model.metadatos.TipoProyecto;
import model.utils.db.ConsultaBD;
import ui.ControladorPantalla;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Tabla;
import ui.Tableable;
import ui.Economico.GestionPresupuestos.Tables.DesgloseDemandasAsocidasTabla;
import ui.Economico.GestionPresupuestos.Tables.LineaCosteDesglosado;

public class GestionPresupuestos implements ControladorPantalla {
	

	public static final String fxml = "file:src/ui/Economico/GestionPresupuestos/GestionPresupuestos.fxml";
	
	@FXML
    private TextField tNomProyecto;

    @FXML
    private TableView<Tableable> tCoste;
    public Tabla tablaCoste;

    @FXML
    private TableView<Tableable> tDemandas;
    public Tabla tablaDemandas;

    @FXML
    public ComboBox<Proyecto> cbProyecto;

    @FXML
    private ImageView imGuardarNuevaVersion;
    private GestionBotones gbGuardarNuevaVersion;

    @FXML
    private TextField tVsProyecto;

    @FXML
    private ImageView imNuevoProyecto;
    private GestionBotones gbNuevoProyecto;

    @FXML
    private ImageView imAniadirDemanda;
    private GestionBotones gbAniadirDemanda;

    @FXML
    private ImageView imGuardar;
    private GestionBotones gbActualizarVersion;
    
    @FXML
    private ImageView imBorrar;
    private GestionBotones gbBorrarVersion;

    @FXML
    public ComboBox<Presupuesto> cbVersion;
    
    @FXML
    private ScrollPane scrDatos;
	
	@FXML
	private AnchorPane anchor;
	
    @FXML
    private ComboBox<TipoProyecto> cbTipoProy;
	
	ArrayList<Proyecto> listaDemAsociadas = null;
	Presupuesto presOperado = null;
	Proyecto proyOperado = null;
	
	boolean nuevaVersion = false;
	
	
	public void initialize(){
		tablaDemandas = new Tabla(tDemandas,new DesgloseDemandasAsocidasTabla());
		tablaCoste = new Tabla(tCoste,new LineaCosteDesglosado());
		
		scrDatos.setDisable(true);
		
		Proyecto p = new Proyecto();
		ArrayList<Proyecto> listaProyectos = p.listadoProyectosGGP();
		
		cbProyecto.getItems().addAll(listaProyectos);
		cbProyecto.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { buscaPresupuestos (newValue);  	}   );
		
		cbVersion.getItems().removeAll(cbVersion.getItems());
		cbVersion.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { versionSeleccionada (true);  	}   );
		cbVersion.setDisable(true);
		
		cbTipoProy.getItems().addAll(TipoProyecto.tiposNoDemanda());
		
		gbActualizarVersion = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					guardarCambiosPresupuesto(false, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Actualizar presupuesto");
		gbActualizarVersion.desActivarBoton();
		
		gbBorrarVersion = new GestionBotones(imBorrar, "Eliminar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					borrarVersion();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Guardar como nueva versión del presupuesto");
		gbBorrarVersion.desActivarBoton();
		
		gbGuardarNuevaVersion = new GestionBotones(imGuardarNuevaVersion, "GuardarAniadir3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					nuevaVersion = true;
					guardarCambiosPresupuesto(false, false);
					nuevaVersion = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Guardar como nueva versión del presupuesto");
		gbGuardarNuevaVersion.desActivarBoton();
		
		gbAniadirDemanda = new GestionBotones(imAniadirDemanda, "Editar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					aniadirEstimacion();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Añadir Demanda");
		gbAniadirDemanda.desActivarBoton();
		
		gbNuevoProyecto = new GestionBotones(imNuevoProyecto, "Nuevo3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					nuevoProyecto();
				} catch (Exception e) {
					e.printStackTrace();
				}
            } }, "Nuevo Proyecto");
		gbNuevoProyecto.activarBoton();
		
		tablaCoste.pintaTabla(new ArrayList<Object>());
		tablaDemandas.pintaTabla(new ArrayList<Object>());
	}
	
	public void nuevoProyecto() {
		cbProyecto.getItems().removeAll(cbProyecto.getItems());
		cbVersion.getItems().removeAll(cbVersion.getItems());
		cbVersion.setDisable(true);
		
		this.tNomProyecto.setText("");
		this.tVsProyecto.setText("");
		this.cbTipoProy.getItems().removeAll(cbTipoProy.getItems());
		cbTipoProy.getItems().addAll(TipoProyecto.listado.values());
		
		Proyecto p = new Proyecto();
		ArrayList<Proyecto> listaProyectos = p.listadoProyectosGGP();
		
		cbProyecto.getItems().addAll(listaProyectos);
		
		cbTipoProy.getItems().removeAll(cbTipoProy.getItems());
		cbTipoProy.getItems().addAll(TipoProyecto.tiposNoDemanda());
		cbTipoProy.setDisable(false);
		scrDatos.setDisable(false);
		
		this.tNomProyecto.setText("");
		tNomProyecto.setDisable(false);
		this.tVsProyecto.setText("");
		
		ApunteContable pApunteContable = new ApunteContable();
		pApunteContable.id = 0;
		pApunteContable.modo = Proyecto.MODIFICAR;
		pApunteContable.nombre = "Apunte Contable";
		pApunteContable.apunteContable = true;
		pApunteContable.presupuestoActual = new Presupuesto();
		pApunteContable.presupuestoActual.costes = new HashMap<Integer, Coste>();
		pApunteContable.presupuestoActual.descripcion = "Apunte Contable";
		pApunteContable.presupuestoActual.fxAlta = Constantes.fechaActual();
		pApunteContable.presupuestoActual.p = pApunteContable;
		pApunteContable.presupuestoActual.tipo = TipoPresupuesto.listado.get(1);
		pApunteContable.presupuestoActual.version = 0;
		
		proyOperado = new Proyecto();
		proyOperado.modo = Proyecto.ANIADIR;
		
		this.listaDemAsociadas = new ArrayList<Proyecto>();
		listaDemAsociadas.add(pApunteContable);
		
		this.presOperado = pApunteContable.presupuestoActual.cloneSinCostes();
		proyOperado.presupuestoActual = this.presOperado;
		presOperado.p = proyOperado;
		
		ArrayList<Object> listaPintable = new ArrayList<Object>();
		
		listaPintable.add(pApunteContable);
								
		tablaDemandas.pintaTabla(listaPintable);
		
		this.tablaCoste.limpiaTabla();
		
		this.gbAniadirDemanda.activarBoton();
		this.gbGuardarNuevaVersion.activarBoton();
		this.gbActualizarVersion.desActivarBoton();
	}
	
	public void borrarVersion() throws Exception{
		ArrayList<Presupuesto> listaPresupuestos = this.presOperado.buscaPresupuestos(this.proyOperado.id);
		
		if (listaPresupuestos.size()<2) {
			Dialogo.error("No se pudo eliminar", "No se pudo eliminar el presupuesto", "Es el último presupuesto del proyecto y por tanto no se puede eliminar");
			return;
		}
		
		setIdsPresOperado();
		
		String idTransaccion = "eliminarPresupuestoProyecto" + new Date().getTime();
		
		Iterator<Proyecto> itDemandasAsociadas = this.listaDemAsociadas.iterator();
		Proyecto pApunte = null;

		while (itDemandasAsociadas.hasNext()) {
			Proyecto pApunteAux = itDemandasAsociadas.next();
			
			if (pApunteAux.apunteContable) {
				pApunte = pApunteAux;
				break;
			}			
		}
		
		pApunte.presupuestoActual.borrarPresupuesto(idTransaccion);
		((ApunteContable) pApunte).bajaApunteContable(idTransaccion);
		
		RelProyectoDemanda rpd = new RelProyectoDemanda();
		rpd.proyecto = this.proyOperado;
		rpd.pres = this.presOperado;
		rpd.deleteRelacion(idTransaccion);
		
		this.presOperado.borrarPresupuesto(idTransaccion);
		
		ConsultaBD cbd = new ConsultaBD(); 
		cbd.ejecutaTransaccion(idTransaccion);
		
		cbProyecto.getItems().removeAll(cbProyecto.getItems());
		cbVersion.getItems().removeAll(cbVersion.getItems());
		
		this.tNomProyecto.setText("");
		this.tVsProyecto.setText("");
		this.cbTipoProy.getItems().removeAll(cbTipoProy.getItems());
		cbTipoProy.getItems().addAll(TipoProyecto.listado.values());
		
		Proyecto p = new Proyecto();
		ArrayList<Proyecto> listaProyectos = p.listadoProyectosGGP();
		
		cbProyecto.getItems().addAll(listaProyectos);
		
		tablaCoste.limpiaTabla();
		tablaDemandas.limpiaTabla();
		
		scrDatos.setDisable(true);
		cbVersion.setDisable(true);
		gbBorrarVersion.desActivarBoton();
		gbGuardarNuevaVersion.desActivarBoton();
		gbActualizarVersion.desActivarBoton();
		
		Dialogo.alert("Borrado correcto", "Se eliminó el presupuesto", "El borrado se realizó correctamente");
		
	}
	
	
	public void guardarCambiosPresupuesto(boolean nuevoForzado, boolean editar) throws Exception{
		if ("".equals(this.tNomProyecto.getText()) || "".equals(this.tVsProyecto.getText()) || null == this.cbTipoProy.getValue()) {
			Dialogo.error("No se pudo guardar", "No se pudo guardar el presupuesto", "Alguno de los campos obligatorios no están informados.");
			return;
		}
		
		if (this.listaDemAsociadas==null || this.listaDemAsociadas.size()==0) {
			Dialogo.error("No se pudo guardar", "No se pudo guardar el presupuesto", "Un proyecto debe tener al menos una demanda asociada");
			return;
		}
		
		if (this.presOperado.calculaTotal() == 0) {
			Dialogo.error("No se pudo guardar", "No se pudo guardar el presupuesto", "Un proyecto debe tener al menos un coste mayor que 0 asociado");
			return;
		}
		
		String idTransaccion = "guardarPresupuestoProyecto" + new Date().getTime();
		
		if (this.proyOperado.modo == Proyecto.ANIADIR) {
			this.proyOperado.nombre = this.tNomProyecto.getText();
			this.presOperado.descripcion = this.tVsProyecto.getText();
			this.presOperado.tipo = TipoPresupuesto.listado.get(1);
			this.presOperado.version = 1;
			this.proyOperado.presupuestoActual = this.presOperado;
			
			this.proyOperado.nuevoProyecto(idTransaccion);
			
			ParametroProyecto pp = new ParametroProyecto();
			pp.idProyecto = this.proyOperado.id;
			pp.mpProy = MetaParamProyecto.listado.get(MetaParamProyecto.TIPO_PROYECTO);
			pp.valorEntero = this.cbTipoProy.getValue().id;
			
			pp.insertaParametro(this.proyOperado.id, idTransaccion);
		}
		
		setIdsPresOperado();
		
		Presupuesto pApunteContable = null;
		
		RelProyectoDemanda rpd = null;
		Iterator<Proyecto> itDemandasAsociadas = this.listaDemAsociadas.iterator();
		rpd = new RelProyectoDemanda();
		while (itDemandasAsociadas.hasNext()) {
			Proyecto p = itDemandasAsociadas.next();
			
			if (p.apunteContable && (p.modo == Proyecto.ANIADIR || p.modo == Proyecto.MODIFICAR)) {
				pApunteContable = p.presupuestoActual;
				p.presupuestoActual.p = p;
			}
			
			if (p.modo == Proyecto.ANIADIR || p.modo == Proyecto.MODIFICAR || p.modo == Proyecto.NEUTRO) {
				rpd.listaDemandas.add(p);
			}
		}
		
		if (pApunteContable!=null) {
			boolean actualiza = true;
			
			if (this.proyOperado.modo == Proyecto.ANIADIR || nuevaVersion == true) {
				pApunteContable.idApunteContable = ((ApunteContable) pApunteContable.p).altaApunteContable((ApunteContable) pApunteContable.p,idTransaccion);
				pApunteContable.p.id = pApunteContable.idApunteContable;
				actualiza = false;
			}
			
			pApunteContable.guardarPresupuesto(actualiza, idTransaccion);
		}
		
		this.presOperado.descripcion = this.tVsProyecto.getText();
		this.presOperado.guardarPresupuesto(editar,idTransaccion);
		
		rpd.proyecto = this.proyOperado;
		rpd.pres = this.presOperado;
		rpd.actualizaRelaciones(idTransaccion);	
		
		ConsultaBD cbd = new ConsultaBD(); 
		cbd.ejecutaTransaccion(idTransaccion);
		
		Proyecto.listaProyecto = null;
		
		Proyecto.getProyectoEstatico(1);
		
		cbProyecto.getItems().removeAll(cbProyecto.getItems());
		cbVersion.getItems().removeAll(cbVersion.getItems());
		
		this.tNomProyecto.setText("");
		this.tVsProyecto.setText("");
		this.cbTipoProy.getItems().removeAll(cbTipoProy.getItems());
		cbTipoProy.getItems().addAll(TipoProyecto.listado.values());
		
		Proyecto p = new Proyecto();
		ArrayList<Proyecto> listaProyectos = p.listadoProyectosGGP();
		
		cbProyecto.getItems().addAll(listaProyectos);
		
		tablaCoste.limpiaTabla();
		tablaDemandas.limpiaTabla();
		scrDatos.setDisable(true);
		cbVersion.setDisable(true);
		gbBorrarVersion.desActivarBoton();
		gbGuardarNuevaVersion.desActivarBoton();
		gbActualizarVersion.desActivarBoton();
		
		Dialogo.alert("Guardado correcto", "Se actualizó el presupuesto", "El guardado se realizó correctamente");
	}
	
	private void setIdsPresOperado() {
		Proyecto proy = this.proyOperado;
		
		Iterator<Coste> itCostes = this.presOperado.costes.values().iterator();
		while (itCostes.hasNext()) {
			Coste c = itCostes.next();
			c.id = -1;
			
			Iterator<Concepto> itConcepto = c.conceptosCoste.values().iterator();
			while (itConcepto.hasNext()) {
				Concepto conc = itConcepto.next();
				conc.id = -1;
			}
		}
		
		if (this.proyOperado.modo == Proyecto.ANIADIR || this.nuevaVersion == true) {
			return;
		}
		
		Presupuesto pOriginal = new Presupuesto();
		pOriginal = pOriginal.buscaPresupuestos(proy.id, this.presOperado.version);
		
		itCostes = pOriginal.costes.values().iterator();
		while (itCostes.hasNext()) {
			Coste c = itCostes.next();
			
			Coste cOperado = null;
			
			Iterator<Coste> itCostesOperados = this.presOperado.costes.values().iterator();
			while (itCostesOperados.hasNext()) {
				Coste cAux = itCostesOperados.next();
				if (cAux.sistema.codigo.equals(c.sistema.codigo)) {
					cOperado = cAux;
					break;
				}
			}

			if (cOperado!=null) {
				cOperado.id = c.id;
				
				Iterator<Concepto> itConcepto = c.conceptosCoste.values().iterator();
				while (itConcepto.hasNext()) {
					Concepto conc = itConcepto.next();
					
					Concepto concOperado = cOperado.conceptosCoste.get(conc.tipoConcepto.codigo);
					
					if (concOperado !=null) {
						concOperado.id = conc.id;
						concOperado.baseCalculo = new BaseCalculoConcepto(BaseCalculoConcepto.CALCULO_BASE_COSTE);
						concOperado.respectoPorcentaje = null;
						concOperado.porcentaje = 0;
					}
				}
			}
		}
		
		itCostes = this.presOperado.costes.values().iterator();
		while (itCostes.hasNext()) {
			Coste cOperado = itCostes.next();
			
			Coste c = null;
			
			Iterator<Coste> itCostesOperados = pOriginal.costes.values().iterator();
			while (itCostesOperados.hasNext()) {
				Coste cAux = itCostesOperados.next();
				if (cAux.sistema.codigo.equals(cOperado.sistema.codigo))
					c = cAux;
					break;
			}

			if (c!=null) {
				cOperado.id = c.id;
				
				Iterator<Concepto> itConcepto = cOperado.conceptosCoste.values().iterator();
				while (itConcepto.hasNext()) {
					Concepto conc = itConcepto.next();
					
					Concepto concOperado = c.conceptosCoste.get(conc.tipoConcepto.codigo);
					
					if (concOperado !=null) {
						conc.id = concOperado.id;
						concOperado.baseCalculo = new BaseCalculoConcepto(BaseCalculoConcepto.CALCULO_BASE_COSTE);
						concOperado.respectoPorcentaje = null;
						concOperado.porcentaje = 0;
					}
				}
			}
		}
	}
	
	public void tratarModificacionesPresupuesto(Proyecto p) {
		Proyecto pEncontrado = null;
		
		Iterator<Proyecto> itAsociados = listaDemAsociadas.iterator();
		while (itAsociados.hasNext()) {
			Proyecto asoc = itAsociados.next();
			if (asoc.apunteContable == p.apunteContable && asoc.id == p.id) {
				pEncontrado = asoc;
			}
		}
		
		int operacion = 0;
		
		if (p.modo == Proyecto.ANIADIR) {
			operacion = Presupuesto.SUMAR;
			if (pEncontrado!=null && pEncontrado.modo == Proyecto.ELIMINAR)
				pEncontrado.modo = Proyecto.NEUTRO;
			else
				listaDemAsociadas.add(p);
		}
		if (p.modo == Proyecto.ELIMINAR) {
			operacion = Presupuesto.RESTAR;
			if (pEncontrado!=null && pEncontrado.modo == Proyecto.ANIADIR)
				listaDemAsociadas.remove(pEncontrado);
			else
				pEncontrado.modo = Proyecto.ELIMINAR;
		}
		if (p.modo == Proyecto.MODIFICAR) {
			this.presOperado = this.presOperado.operarPresupuestos(pEncontrado.presupuestoActual, Presupuesto.RESTAR);
			listaDemAsociadas.remove(pEncontrado);
			listaDemAsociadas.add(p);
			operacion = Presupuesto.SUMAR;
		}
	
		this.presOperado = this.presOperado.operarPresupuestos(p.presupuestoActual, operacion);
		
		versionSeleccionada(false);
		
		if (Proyecto.ANIADIR == this.proyOperado.modo) {
			this.gbActualizarVersion.desActivarBoton();
		}
		
		ParamTable.po.hide();
	}

	public void versionSeleccionada(boolean recargar) {
		try {
			gbActualizarVersion.activarBoton();
			gbGuardarNuevaVersion.activarBoton();
			gbBorrarVersion.activarBoton();
			scrDatos.setDisable(false);
			
			Presupuesto p = cbVersion.getValue();
			
			if (p == null) {
				p = this.presOperado;
			}
			
			Proyecto proy = this.proyOperado;
			
			if (proy ==null || p==null) return;
			
			proy.presupuestoActual = p;
			
			if (recargar) {
				this.tNomProyecto.setText(proy.nombre);
				this.tVsProyecto.setText(p.descripcion);
				
				proy.cargaProyecto();
				this.cbTipoProy.setValue(TipoProyecto.listado.get(new Integer((String)proy.getValorParametro(MetaParamProyecto.TIPO_PROYECTO))));
				
				this.cbTipoProy.setDisable(true);
				this.tNomProyecto.setDisable(true);
				
				RelProyectoDemanda rpD = new RelProyectoDemanda();
				rpD.pres = p;
				rpD.proyecto = proy;
				
				this.listaDemAsociadas = proy.getDemandasAsociadas();
				
				this.presOperado = p.cloneSinCostes();
				this.presOperado.p = this.proyOperado;
				Iterator<Proyecto> itDemandas = this.listaDemAsociadas.iterator();
				
				while (itDemandas.hasNext()) {
					Proyecto pDemanda = itDemandas.next();
					
					this.presOperado = this.presOperado.operarPresupuestos(pDemanda.presupuestoActual, Presupuesto.SUMAR);
				}
			}
			
			ArrayList<Object> listaPintable = new ArrayList<Object>();
			
			Iterator<Proyecto> itListaDemandasAsociadas = listaDemAsociadas.iterator();
			while (itListaDemandasAsociadas.hasNext()) {
				Proyecto demanda = itListaDemandasAsociadas.next();
				if (demanda.modo!=Proyecto.ELIMINAR)
					listaPintable.add(demanda);
			}
									
			tablaDemandas.pintaTabla(listaPintable);
			
			HashMap<String,Concepto> listaConceptos = new HashMap<String,Concepto>();
			HashMap<String,Concepto> listaConceptosDesglosado = null;
			ArrayList<Object> listaConceptosDesglosada = new ArrayList<Object>();
			
			Iterator<Coste> itCoste = this.presOperado.costes.values().iterator();
			float acumulado = 0;
			float acumuladoSistema = 0;
			Concepto caux = null;
			
			while (itCoste.hasNext()) {
				Coste c = itCoste.next();
				acumuladoSistema = 0;
							
				Iterator<Concepto> itConcepto = c.conceptosCoste.values().iterator();
				
				listaConceptosDesglosado = new HashMap<String,Concepto>();
				listaConceptosDesglosada.add(new LineaCosteDesglosado(listaConceptosDesglosado));
				
				if (c.conceptosCoste.size()!=0) {
					listaConceptosDesglosado.put("SISTEMA", (Concepto) c.conceptosCoste.values().toArray()[0]);
					if (((Concepto) c.conceptosCoste.values().toArray()[0]).coste==null)
						((Concepto) c.conceptosCoste.values().toArray()[0]).coste = c;
				}
				
				while (itConcepto.hasNext()) {
					Concepto co = itConcepto.next();
					listaConceptosDesglosado.put(co.tipoConcepto.codigo, co);
									
					if (listaConceptos.containsKey(co.tipoConcepto.codigo)) {
						caux = listaConceptos.get(co.tipoConcepto.codigo);
					} else {
						caux = new Concepto();					
						listaConceptos.put(co.tipoConcepto.codigo,caux);
					}
					acumuladoSistema+=co.valorEstimado;
					acumulado+=co.valorEstimado;
					caux.valorEstimado += co.valorEstimado;
				}
				
				caux = new Concepto();
				MetaConcepto mc = new MetaConcepto();
				mc.codigo = "TOTAL";
				mc.descripcion = "Total";
				mc.id = MetaConcepto.ID_TOTAL;
				caux.tipoConcepto = mc;
				caux.valorEstimado = acumuladoSistema;
				listaConceptosDesglosado.put(caux.tipoConcepto.codigo,caux);
			}
			
			caux = new Concepto();
			Coste coAux = new Coste();
			coAux.sistema = Sistema.getInstanceTotal();
			caux.coste = coAux;
			MetaConcepto mc = new MetaConcepto();
			mc.codigo = "TOTAL";
			mc.descripcion = "Total";
			mc.id = MetaConcepto.ID_TOTAL;
			caux.tipoConcepto = mc;
			caux.valorEstimado = acumulado;
			listaConceptos.put(caux.tipoConcepto.codigo,caux);
			listaConceptos.put("SISTEMA", caux);
			listaConceptosDesglosada.add(new LineaCosteDesglosado(listaConceptos));
			
			ArrayList<Object> listaPintableConce = new ArrayList<Object>();
			listaPintableConce.addAll(listaConceptosDesglosada);
			
			tablaCoste.pintaTabla(listaPintableConce);
			tablaCoste.formateaTabla();
			
			gbAniadirDemanda.activarBoton();
			
		} catch (Exception es) {
			es.printStackTrace();
		}
	}
	
	private void buscaPresupuestos(Proyecto p) {
		
		if (p==null) return;
		
		cbVersion.getItems().removeAll(cbVersion.getItems());
		
		Presupuesto pres = new Presupuesto();		
		ArrayList<Presupuesto> listado = pres.buscaPresupuestos(p.id);
		
		cbVersion.getItems().addAll(listado);
		
		this.proyOperado = this.cbProyecto.getValue();
		this.proyOperado.modo = Proyecto.MODIFICAR;
		
		this.presOperado = null;
		cbVersion.setDisable(false);
		scrDatos.setDisable(true);
	}
	
	public void aniadirEstimacion() {
		try {
			
			
			FXMLLoader loader = new FXMLLoader();
			
			HashMap<String, Object> parametrosPaso = new HashMap<String, Object>();
			
			parametrosPaso.put("filaDatos", null);							        	
			parametrosPaso.put("columna", null);
			parametrosPaso.put("evento", null);
			parametrosPaso.put("controladorPantalla", this);
			
			AniadeDemanda controlPantalla = new AniadeDemanda();
	    	
	    	if (ParamTable.po!=null){
	    		ParamTable.po.hide();
	    		ParamTable.po = null;
	    	}
	    	
	    	ArrayList<Proyecto> listaDemandas = new ArrayList<Proyecto>();
	    	Iterator<Proyecto> itDemandas =  this.listaDemAsociadas.iterator();
	    	while (itDemandas.hasNext()) {
	    		Proyecto p = itDemandas.next();
	    		Proyecto pAux = null;
	    		if (p.apunteContable) {
	    			pAux = ((ApunteContable)p).clone();
	    		} else {
	    			pAux = p.clone();
	    		}
	    		
	    		listaDemandas.add(pAux);
	    	}
	    	
	    	parametrosPaso.put("demandasAsignadas", listaDemandas);
	    	parametrosPaso.put("gestionPresupuestos", this);
	    	
	    	loader.setLocation(new URL(controlPantalla.getFXML()));
		    Pane pane = loader.load();
		    controlPantalla = (AniadeDemanda) loader.getController();
		    controlPantalla.setParametrosPaso(parametrosPaso);
		    ParamTable.po = new PopOver(pane);
		    parametrosPaso.put("PopOver", ParamTable.po);
		    ParamTable.po.setTitle("");
		    ParamTable.po.show(this.tDemandas);
		    ParamTable.po.setAnimated(true);
		    ParamTable.po.setAutoHide(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
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
