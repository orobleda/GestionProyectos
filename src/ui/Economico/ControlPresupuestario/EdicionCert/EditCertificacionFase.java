package ui.Economico.ControlPresupuestario.EdicionCert;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.beans.Certificacion;
import model.beans.CertificacionFase;
import model.beans.CertificacionFaseParcial;
import model.beans.Concepto;
import model.beans.FaseProyecto;
import model.beans.Parametro;
import model.beans.ParametroFases;
import model.beans.Proveedor;
import model.beans.Tarifa;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaParametro;
import model.metadatos.TipoCobroVCT;
import model.metadatos.TipoDato;
import model.utils.db.ConsultaBD;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Administracion.Parametricas.GestionParametros;
import ui.Economico.ControlPresupuestario.ControlPresupuestario;
import ui.interfaces.ControladorPantalla;
import ui.popUps.PopUp;

public class EditCertificacionFase implements ControladorPantalla, PopUp {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/EdicionCert/EditCertificacionFase.fxml";
	
	public boolean esPopUp = false;
	public static HashMap<String, Object> variablesPaso = null;
	public static Object claseRetorno = null;
	public static String metodoRetorno = null;
	
	public CertificacionFase certificacion = null;
	
	public HashMap<String,Parametro> listaParametros = null;
	
	@FXML
    private TextField tImporte;

    @FXML
    private TextField tHoras;

    @FXML
    private ComboBox<Tarifa> cbTarifas;

    @FXML
    private TextField tPorcentaje;

    @FXML
    private HBox hbProperties;

    @FXML
    private CheckBox chkAdicional;

    @FXML
    private ImageView imGuardar;   
    private GestionBotones gbGuardar;

    @FXML
    private ImageView imMoverFase;   
    private GestionBotones gbMoverFase;

    @FXML
    private ImageView imNuevaFase;   
    private GestionBotones gbNuevaFase;
    
    @FXML
    private ImageView imBorraFase;    
    private GestionBotones gbBorraFase;
	
	@FXML
	private AnchorPane anchor;
	
    @FXML
    private ComboBox<FaseProyecto> cbFases;
    	
	public EditCertificacionFase (){
	}
	
   @Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		this.chkAdicional.setDisable(true);
		
		gbBorraFase = new GestionBotones(imBorraFase, "BorraFila3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            { 
				try {
					borraFase();
					ControlPresupuestario.salvaPosicionActual();
	            	ParamTable.po.hide();
	            	Dialogo.alert("Borrado de Certificación correcto", "Eliminación Correcta", "Se eliminó la certificación");
	            	ControlPresupuestario.cargaPosicionActual();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				//ParamTable.po.hide();
            } }, "Borrar Asignación de fase");
		gbBorraFase.activarBoton();
		
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            { 
				try {
					guardarCertificacion();
					ControlPresupuestario.salvaPosicionActual();
	            	ParamTable.po.hide();
	            	Dialogo.alert("Guardado de Certificación correcto", "Guardado Correcto", "Se guardó la certificación");
	            	ControlPresupuestario.cargaPosicionActual();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				//ParamTable.po.hide();
            } }, "Guardar Cambios fase");
		gbGuardar.activarBoton();
		
		gbMoverFase = new GestionBotones(imMoverFase, "MoverFase3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            { 
				try {
					moverCertificacion();
					ControlPresupuestario.salvaPosicionActual();
	            	ParamTable.po.hide();
	            	Dialogo.alert("Cambio de fase correcto", "Cambio de fase Correcto", "Se cambió la fase correctamente");
	            	ControlPresupuestario.cargaPosicionActual();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				//ParamTable.po.hide();
            } }, "Cambiar fase");
		gbMoverFase.activarBoton();
		
		gbNuevaFase = new GestionBotones(imNuevaFase, "NuevaFila3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            { 
				try {
					nuevaCertificacion();
					ControlPresupuestario.salvaPosicionActual();
	            	ParamTable.po.hide();
	            	Dialogo.alert("Añadida Fase", "Fase añadida correctamente", "Se añadió la fase correctamente");
	            	ControlPresupuestario.cargaPosicionActual();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				//ParamTable.po.hide();
            } }, "Borrar Asignación de fase");
		gbNuevaFase.activarBoton();
		
		this.tPorcentaje.focusedProperty().addListener((ov, oldV, newV) -> { 
			if (!newV) {
				try {
					Concepto c = certificacion.certificacion.concepto;
					float porcentaje = (Float) FormateadorDatos.parseaDato(this.tPorcentaje.getText(), TipoDato.FORMATO_PORC);
					
					float importe = porcentaje*c.valor/100; 
					
					if (cbTarifas.getValue()!=null && cbTarifas.getValue().costeHora!=0) {
						Tarifa t = cbTarifas.getValue();
						float horas = importe/t.costeHora;
						this.tHoras.setText(FormateadorDatos.formateaDato(horas, TipoDato.FORMATO_REAL));
					}
					
					this.tImporte.setText(FormateadorDatos.formateaDato(importe, TipoDato.FORMATO_MONEDA));
					this.tPorcentaje.setText(FormateadorDatos.formateaDato(porcentaje, TipoDato.FORMATO_PORC));
				} catch (Exception ex) {
					this.tImporte.setText("0 €");
				}
			}	
		});	
		
		this.tImporte.focusedProperty().addListener((ov, oldV, newV) -> { 
			if (!newV) {
				try {
					String textImporte = this.tImporte.getText();
					float importe = (Float) FormateadorDatos.parseaDato(textImporte, TipoDato.FORMATO_MONEDA);
					
					if (cbTarifas.getValue()!=null && cbTarifas.getValue().costeHora!=0) {
						Tarifa t = cbTarifas.getValue();
						float horas = importe/t.costeHora;
						this.tHoras.setText(FormateadorDatos.formateaDato(horas, TipoDato.FORMATO_REAL));
					}
					
					this.tImporte.setText(FormateadorDatos.formateaDato(importe, TipoDato.FORMATO_MONEDA));
					
					Concepto c = certificacion.certificacion.concepto;
					float porcentaje = importe/c.valor*100;
					this.tPorcentaje.setText(FormateadorDatos.formateaDato(porcentaje, TipoDato.FORMATO_PORC));
					
					
				} catch (Exception ex) {
					this.tImporte.setText("0 €");
				}
			}	
		});	
		this.tHoras.focusedProperty().addListener((ov, oldV, newV) -> { 
			if (!newV) {
				try {
					String textHoras = this.tHoras.getText();
					float horas = (Float) FormateadorDatos.parseaDato(textHoras, TipoDato.FORMATO_REAL);
					float importe = 0;
					
					if (cbTarifas.getValue()!=null && cbTarifas.getValue().costeHora!=0) {
						Tarifa t = cbTarifas.getValue();
						importe = horas * t.costeHora;
						this.tImporte.setText(FormateadorDatos.formateaDato(importe, TipoDato.FORMATO_MONEDA));
					}
					
					Concepto c = certificacion.certificacion.concepto;
					float porcentaje = importe/c.valor*100;
					this.tPorcentaje.setText(FormateadorDatos.formateaDato(porcentaje, TipoDato.FORMATO_PORC));
				} catch (Exception ex) {
					this.tHoras.setText("0");
				}
			}	
		});
		this.cbTarifas.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			if (cbTarifas.getValue()!=null) {
				try {
					String textImporte = this.tImporte.getText();
					float importe = (Float) FormateadorDatos.parseaDato(textImporte, TipoDato.FORMATO_MONEDA);
					
					Tarifa t = cbTarifas.getValue();
					float horas = importe/t.costeHora;
					this.tHoras.setText(FormateadorDatos.formateaDato(horas, TipoDato.FORMATO_REAL));					
				} catch (Exception ex) {					
				}
			}
		});
		
	}
	
	public void pintaValores(CertificacionFase cert) throws Exception{
		this.certificacion = cert;
		
		ArrayList<FaseProyecto> listaFases = new ArrayList<FaseProyecto>();
		
		if (this.certificacion.certificacion.p.fasesProyecto==null) {
			this.certificacion.certificacion.p.cargaFasesProyecto();
		}
		
		Iterator<FaseProyecto> itFp = this.certificacion.certificacion.p.fasesProyecto.iterator();
		while (itFp.hasNext()) {
			FaseProyecto fp = itFp.next();
			
			if (fp.id != this.certificacion.fase.id)
				listaFases.add(fp);
		}
		
		cbFases.getItems().addAll(listaFases);
				
		float coste = cert.calculaCoste();
		Tarifa t = cert.certificacion.getTarifa();
		
		this.tPorcentaje.setText(FormateadorDatos.formateaDato(cert.porcentaje,TipoDato.FORMATO_PORC));
		this.chkAdicional.setSelected(cert.adicional);
		
		Parametro par = new Parametro();
		par = par.dameParametros(cert.certificacion.s.getClass().getSimpleName(), cert.certificacion.s.id).get(MetaParametro.PARAMETRO_SISTEMA_PROVEEDOR);
				
		Proveedor prov = (Proveedor) par.getValor();
		
		Tarifa tAux = new Tarifa(); 
		this.cbTarifas.getItems().addAll(prov.listaTarifas());
		
		boolean encontrado = false;
		Iterator<Tarifa> itTarifa = this.cbTarifas.getItems().iterator();
		while (itTarifa.hasNext()) {
			tAux = itTarifa.next();
			if (tAux.idTarifa == t.idTarifa) {
				this.cbTarifas.setValue(tAux);
				encontrado = true;
				break;
			}
		}
		
		if (t!=null) {
			if (!encontrado) {
				this.cbTarifas.getItems().add(t);
				this.cbTarifas.setValue(t);
			}
			if (t.costeHora!=0) {
				float horas = coste / t.costeHora;
				this.tHoras.setText(FormateadorDatos.formateaDato(horas,TipoDato.FORMATO_REAL));
			}
		}		
		
		this.tImporte.setText(FormateadorDatos.formateaDato(coste,TipoDato.FORMATO_MONEDA));
		
		cargaPropiedades(cert.id, cert);
		
	}
	
	private void cargaPropiedades(int idCertificacion, Object claseMostrar) throws Exception {
		hbProperties.getChildren().removeAll(hbProperties.getChildren());
		
		GestionParametros gestPar = new GestionParametros();
		
		FXMLLoader loader = new FXMLLoader();
        loader.setLocation(new URL(gestPar.getFXML()));
        	        
        hbProperties.getChildren().add(loader.load());
        gestPar = loader.getController();
        
        HashMap<String, Object> variablesPaso = new HashMap<String, Object>();
        variablesPaso.put("entidadBuscar", claseMostrar.getClass().getSimpleName());
        variablesPaso.put("subventana", new Boolean(true));
        
        if (idCertificacion!=-1)
           variablesPaso.put("idEntidadBuscar", idCertificacion);
        else
           variablesPaso.put("idEntidadBuscar", Parametro.SOLO_METAPARAMETROS);
        
        variablesPaso.put("ancho", new Double(800));
        variablesPaso.put("alto", new Double(200));
        
        variablesPaso.put(GestionParametros.PARAMETROS_DIRECTOS,Constantes.TRUE);
		variablesPaso.put(GestionParametros.LISTA_PARAMETROS,this.certificacion.parametrosCertificacionFase);
        
        gestPar.setParametrosPaso(variablesPaso);
        
        listaParametros = gestPar.listaParametros;
        certificacion.parametrosCertificacionFase = listaParametros;
	}
	
	public void nuevaCertificacion() throws Exception{
		
		FaseProyecto fp = this.cbFases.getValue();
		
		if (fp==null) return;
		
		Iterator<CertificacionFase> itCf = this.certificacion.certificacion.certificacionesFases.iterator();
		
		while (itCf.hasNext()) {
			CertificacionFase cf = itCf.next();
			
			if (cf.fase.id == fp.id) {
				return;
			}
		}
		
		Certificacion cert = new Certificacion();
		cert.p = this.certificacion.certificacion.p;
				
		this.certificacion.certificacion.generaCertificacionFaseVacia(this.certificacion.certificacion.s, this.certificacion.certificacion.p, this.certificacion.certificacion, fp);
				
		String idTransaccion = ConsultaBD.getTicket();
		
		Parametro par = this.listaParametros.get(MetaParametro.CERTIFICACION_FASE_TIPOVCT);
		if (par.modificado)
			par.modificado = false;
				
		this.certificacion.certificacion.guardarCertificacion(idTransaccion);
		
		ConsultaBD.ejecutaTicket(idTransaccion);
	}
	
	public void moverCertificacion() throws Exception{
		
		FaseProyecto fp = this.cbFases.getValue();
		if (fp==null) return;
		CertificacionFase cf = null;
		Iterator<CertificacionFase> itCf = this.certificacion.certificacion.certificacionesFases.iterator();
		
		while (itCf.hasNext()) {
			CertificacionFase cfAux = itCf.next();
			if (cfAux.fase.id == fp.id) {
				cf = cfAux;
			}
		}
		
		if (cf==null) {
			this.certificacion.fase = fp;
			if (fp.parametrosFase== null) {
				ParametroFases pf = new ParametroFases();
				fp.parametrosFase = pf.dameParametros(pf.getClass().getSimpleName(), fp.id);
			}
			Date fFin = (Date) ((Parametro) fp.parametrosFase.get(MetaParametro.FASE_PROYECTO_FX_IMPLANTACION)).getValor();
			
			Iterator<CertificacionFaseParcial> itCfp = this.certificacion.certificacionesParciales.iterator();
			while (itCfp.hasNext()) {
				CertificacionFaseParcial cfp = itCfp.next();
				cfp.fxCertificacion = fFin;
				fFin = this.certificacion.certificacion.calcularFechaPrevia(fFin);
			}
		} else {
			float importe = 0;
			
			Iterator<CertificacionFaseParcial> itCfp = this.certificacion.certificacionesParciales.iterator();
			while (itCfp.hasNext()) {
				CertificacionFaseParcial cfp = itCfp.next();
				
				importe += cfp.valEstimado;
			}
			
			itCfp = cf.certificacionesParciales.iterator();
			while (itCfp.hasNext()) {
				CertificacionFaseParcial cfp = itCfp.next();
				
				importe += cfp.valEstimado;
			}
			
			cf.reparteCoste(importe, this.cbTarifas.getValue());
		}
		
		
		String idTransaccion = ConsultaBD.getTicket();
		
		Parametro par = this.listaParametros.get(MetaParametro.CERTIFICACION_FASE_TIPOVCT);
		if (par.modificado)
			par.modificado = false;
		
		this.certificacion.borraCertificacionFase(idTransaccion);
		
		ArrayList<CertificacionFase> lCf = new ArrayList<CertificacionFase>();
				
		itCf = this.certificacion.certificacion.certificacionesFases.iterator();
		
		while (itCf.hasNext()) {
			CertificacionFase cfAux = itCf.next();
			if (this.certificacion != cfAux) {
				lCf.add(cfAux);
			}
		}
		
		this.certificacion.certificacion.certificacionesFases = lCf;
		
		this.certificacion.certificacion.guardarCertificacion(idTransaccion);
		
		ConsultaBD.ejecutaTicket(idTransaccion);
	}
	
	public void guardarCertificacion() throws Exception{
		float importe = (Float) FormateadorDatos.parseaDato(this.tImporte.getText(), TipoDato.FORMATO_MONEDA);
		
		this.certificacion.reparteCoste(importe, this.cbTarifas.getValue());
		float importeTotal = 0;
		
		Iterator<CertificacionFase> itCf = this.certificacion.certificacion.certificacionesFases.iterator();
		while (itCf.hasNext()) {
			CertificacionFase cf = itCf.next();
			
			importeTotal += cf.concepto.valorEstimado;
		}
		
		this.certificacion.certificacion.concepto.valorEstimado = importeTotal;
		
		String idTransaccion = ConsultaBD.getTicket();
		
		Parametro par = this.listaParametros.get(MetaParametro.CERTIFICACION_FASE_TIPOVCT);
		if (par.modificado)
			this.certificacion.cambiaTipoVCT( (TipoCobroVCT) par.getValor(),idTransaccion);
		
		this.certificacion.certificacion.guardarCertificacion(idTransaccion);
		
		ConsultaBD.ejecutaTicket(idTransaccion);
	}
	
	public void borraFase() throws Exception{
		float porcentajeTotal = 0;
		
		Iterator<CertificacionFase> itCf = this.certificacion.certificacion.certificacionesFases.iterator();
		while (itCf.hasNext()) {
			CertificacionFase cf = itCf.next();
			
			if (cf!=this.certificacion)
				porcentajeTotal+= cf.porcentaje;
		}
		
		ArrayList<CertificacionFase> listCf = new ArrayList<CertificacionFase>();
		itCf = this.certificacion.certificacion.certificacionesFases.iterator();
		while (itCf.hasNext()) {
			CertificacionFase cf = itCf.next();
			
			if (cf!=this.certificacion) {
				float cantidadAsignada = (this.certificacion.certificacion.concepto.valorEstimado)*cf.porcentaje/porcentajeTotal;
				
				cf.reparteCoste(cantidadAsignada, this.cbTarifas.getValue());
				listCf.add(cf);
			}
		}
		
		this.certificacion.certificacion.certificacionesFases = listCf;
		
		String idTransaccion = ConsultaBD.getTicket();
		
		this.certificacion.borraCertificacionFase(idTransaccion);
		this.certificacion.certificacion.guardarCertificacion(idTransaccion);
		
		ConsultaBD.ejecutaTicket(idTransaccion);
		
	}
	
	@Override
	public String getControlFXML() {
		return EditCertificacionFase.fxml;
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
	}

	@Override
	public void setClaseContenida(Object claseContenida) {
	}

	@Override
	public boolean noEsPopUp() {
		if (!esPopUp) return false;
		else return true;
	}

	@Override
	public String getMetodoRetorno() {
		return metodoRetorno;
	}
	
}
