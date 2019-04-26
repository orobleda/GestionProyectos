package ui.Economico.ControlPresupuestario.EdicionCert;

import java.net.URL;
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
import model.beans.CertificacionFase;
import model.beans.CertificacionFaseParcial;
import model.beans.Parametro;
import model.beans.Proveedor;
import model.beans.Tarifa;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaParametro;
import model.metadatos.TipoDato;
import model.metadatos.TipoEnumerado;
import model.utils.db.ConsultaBD;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Administracion.Parametricas.GestionParametros;
import ui.Economico.ControlPresupuestario.ControlPresupuestario;
import ui.interfaces.ControladorPantalla;
import ui.popUps.PopUp;

public class EditCertificacionFaseParcial implements ControladorPantalla, PopUp {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/EdicionCert/EditCertificacionFaseParcial.fxml";
	
	public static final String MODO_HORAS = "h";
	public static final String MODO_IMPORTE = "i";
	public static final String MODO_TARIFA = "t";
	
	public boolean esPopUp = false;
	public static HashMap<String, Object> variablesPaso = null;
	public static Object claseRetorno = null;
	public static String metodoRetorno = null;
	
	public CertificacionFaseParcial certificacion = null;
	
	public HashMap<String,Parametro> listaParametros = null;
	
	@FXML
    private CheckBox chkRestoParciales;

    @FXML
    private TextField tHorasEst;

    @FXML
    private ComboBox<Tarifa> cbTarifasImp;

    @FXML
    private CheckBox chkRestoCertificacion;

    @FXML
    private ComboBox<Tarifa> cbTarifasEst;

    @FXML
    private TextField tImporteImp;

    @FXML
    private TextField tImporteEst;

    @FXML
    private TextField tHorasImp;

    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;

    @FXML
    private TextField tFxCertificacion;
    
    @FXML
    private HBox hbPropiedades;
    

    @FXML
    private ComboBox<TipoEnumerado> cbTipoEstimacion;
	
	@FXML
	private AnchorPane anchor;
	
	boolean inicializa = true;
	    	
	public EditCertificacionFaseParcial (){
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
		this.tImporteEst.focusedProperty().addListener((ov, oldV, newV) -> { if (!newV) { try {	calculaValores(EditCertificacionFaseParcial.MODO_IMPORTE, tImporteEst, cbTarifasEst, tHorasEst);	} catch (Exception ex) { ex.printStackTrace();} }});
		this.tHorasEst.focusedProperty().addListener((ov, oldV, newV) -> { if (!newV) { try {	calculaValores(EditCertificacionFaseParcial.MODO_HORAS, tImporteEst, cbTarifasEst, tHorasEst);	} catch (Exception ex) { ex.printStackTrace();} }});
		this.tImporteImp.focusedProperty().addListener((ov, oldV, newV) -> { if (!newV) { try {	calculaValores(EditCertificacionFaseParcial.MODO_IMPORTE, tImporteImp, cbTarifasImp, tHorasImp);	} catch (Exception ex) { ex.printStackTrace();} }});
		this.tHorasImp.focusedProperty().addListener((ov, oldV, newV) -> { if (!newV) { try {	calculaValores(EditCertificacionFaseParcial.MODO_HORAS, tImporteImp, cbTarifasImp, tHorasImp);	} catch (Exception ex) { ex.printStackTrace();} }});
		this.cbTarifasEst.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { if (cbTarifasEst.getValue()!=null) {  try {calculaValores(EditCertificacionFaseParcial.MODO_TARIFA, tImporteEst, cbTarifasEst, tHorasEst); } catch (Exception e) {}}});
		this.cbTarifasImp.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { if (cbTarifasImp.getValue()!=null) {  try {calculaValores(EditCertificacionFaseParcial.MODO_TARIFA, tImporteImp, cbTarifasImp, tHorasImp); } catch (Exception e) {}}});
		
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
		
		cbTipoEstimacion.getItems().addAll(TipoEnumerado.getValores(TipoDato.FORMATO_TIPO_VCT).values());
	}
	
	public void pintaValores(CertificacionFaseParcial cert) throws Exception{
		certificacion = cert;
		
		this.tFxCertificacion.setText(FormateadorDatos.formateaDato(cert.fxCertificacion, TipoDato.FORMATO_FECHA));
		this.tHorasEst.setText(FormateadorDatos.formateaDato(cert.horEstimadas, TipoDato.FORMATO_REAL));
		this.tHorasImp.setText(FormateadorDatos.formateaDato(cert.horReal, TipoDato.FORMATO_REAL));
		this.tImporteEst.setText(FormateadorDatos.formateaDato(cert.valEstimado, TipoDato.FORMATO_MONEDA));
		this.tImporteImp.setText(FormateadorDatos.formateaDato(cert.valReal, TipoDato.FORMATO_MONEDA));
		
		this.cbTipoEstimacion.setValue(TipoEnumerado.listadoIds.get(cert.tipoEstimacion));		
		
		Parametro par = new Parametro();
		par = par.dameParametros(cert.certificacionFase.certificacion.s.getClass().getSimpleName(), cert.certificacionFase.certificacion.s.id).get(MetaParametro.PARAMETRO_SISTEMA_PROVEEDOR);
				
		Proveedor prov = (Proveedor) par.getValor();
		
		Tarifa tAux = new Tarifa(); 
		this.cbTarifasEst.getItems().addAll(prov.listaTarifas());
		this.cbTarifasImp.getItems().addAll(prov.listaTarifas());

		Tarifa t = cert.certificacionFase.certificacion.getTarifa();
		
		boolean encontrado = false;
		Iterator<Tarifa> itTarifa = this.cbTarifasEst.getItems().iterator();
		while (itTarifa.hasNext()) {
			tAux = itTarifa.next();
			if (tAux.idTarifa == t.idTarifa) {
				this.cbTarifasEst.setValue(tAux);
				this.cbTarifasImp.setValue(tAux);
				encontrado = true;
				break;
			}
		}
		
		if (t!=null) {
			if (!encontrado) {
				this.cbTarifasEst.getItems().add(t);
				this.cbTarifasImp.getItems().add(t);
				this.cbTarifasEst.setValue(t);
				this.cbTarifasImp.setValue(t);
			}
		}
		
		cargaPropiedades(this.certificacion.id, this.certificacion);
		
		inicializa = false;
		
	}
	
	public void guardarCertificacion() throws Exception{
		this.certificacion.valEstimado = (Float) FormateadorDatos.parseaDato(this.tImporteEst.getText(), TipoDato.FORMATO_MONEDA);
		this.certificacion.valReal = (Float) FormateadorDatos.parseaDato(this.tImporteImp.getText(), TipoDato.FORMATO_MONEDA);
		this.certificacion.horEstimadas = (Float) FormateadorDatos.parseaDato(this.tHorasEst.getText(), TipoDato.FORMATO_REAL);
		this.certificacion.horReal = (Float) FormateadorDatos.parseaDato(this.tHorasImp.getText(), TipoDato.FORMATO_REAL);
		try {
			this.certificacion.tarifaEstimada = this.cbTarifasEst.getValue().idTarifa;
		} catch (Exception e) {
			this.certificacion.tarifaEstimada = 0;
		}		
		try {
			this.certificacion.tarifaReal = this.cbTarifasImp.getValue().costeHora;
		} catch (Exception e) {
			this.certificacion.tarifaReal = 0;
		}
		this.certificacion.fxCertificacion =  (Date) FormateadorDatos.parseaDato(this.tFxCertificacion.getText(), TipoDato.FORMATO_FECHA);
		this.certificacion.tsCertificacion =  this.certificacion.fxCertificacion.getTime();
		this.certificacion.tipoEstimacion = this.cbTipoEstimacion.getValue().id;
		
		if (this.chkRestoParciales.isSelected()) {
			float total = 100*this.certificacion.valEstimado/this.certificacion.porcentaje;
			
			Iterator<CertificacionFaseParcial> itCfp = this.certificacion.certificacionFase.certificacionesParciales.iterator();
			while (itCfp.hasNext()) {
				CertificacionFaseParcial cfp = itCfp.next();
				
				if (cfp!=this.certificacion) {
					cfp.valEstimado = total * cfp.porcentaje /100;
					cfp.tarifaEstimada = this.certificacion.tarifaEstimada;
					Tarifa tAux = Tarifa.porId(this.certificacion.tarifaEstimada);
					cfp.horEstimadas = cfp.valEstimado / tAux.costeHora;
					cfp.tipoEstimacion = this.certificacion.tipoEstimacion;
				}
			}
		}
		
		float importeTotal = 0;
		
		Iterator<CertificacionFase> itCf = this.certificacion.certificacionFase.certificacion.certificacionesFases.iterator();
		while (itCf.hasNext()) {
			CertificacionFase cf = itCf.next();
			
			float importeFaseTotal = 0;
			Iterator<CertificacionFaseParcial> itCfp = cf.certificacionesParciales.iterator();
			while (itCfp.hasNext()) {
				CertificacionFaseParcial cfp = itCfp.next();
				
				importeFaseTotal += cfp.valEstimado;
			}
			cf.concepto.valorEstimado = importeFaseTotal;
			
			importeTotal += importeFaseTotal;
		}
		
		this.certificacion.certificacionFase.certificacion.concepto.valorEstimado = importeTotal;
		
		String idTransaccion = ConsultaBD.getTicket();
		this.certificacion.certificacionFase.certificacion.guardarCertificacion(idTransaccion);
		
		ConsultaBD.ejecutaTicket(idTransaccion);

	}
	
	private void cargaPropiedades(int idCertificacion, Object claseMostrar) throws Exception {
		hbPropiedades.getChildren().removeAll(hbPropiedades.getChildren());
		
		GestionParametros gestPar = new GestionParametros();
		
		FXMLLoader loader = new FXMLLoader();
        loader.setLocation(new URL(gestPar.getFXML()));
        	        
        hbPropiedades.getChildren().add(loader.load());
        gestPar = loader.getController();
        
        HashMap<String, Object> variablesPaso = new HashMap<String, Object>();
        variablesPaso.put("entidadBuscar", claseMostrar.getClass().getSimpleName());
        variablesPaso.put("subventana", new Boolean(true));
        
        if (idCertificacion!=-1)
           variablesPaso.put("idEntidadBuscar", idCertificacion);
        else
           variablesPaso.put("idEntidadBuscar", Parametro.SOLO_METAPARAMETROS);
        
        variablesPaso.put("ancho", new Double(800));
        variablesPaso.put("alto", new Double(100));
        
        variablesPaso.put(GestionParametros.PARAMETROS_DIRECTOS,Constantes.TRUE);
		variablesPaso.put(GestionParametros.LISTA_PARAMETROS,this.certificacion.paramCertificacionFaseParcial);
        
        gestPar.setParametrosPaso(variablesPaso);
        
        listaParametros = gestPar.listaParametros;
        certificacion.paramCertificacionFaseParcial = listaParametros;
	}
	
	public void calculaValores(String modo, TextField importe, ComboBox<Tarifa> tarifa, TextField horas) throws Exception {
		if (inicializa) return;
		
		float costehora = 0;
		float datoHoras = 0;
		float datoImporte = 0;
		
		if (EditCertificacionFaseParcial.MODO_HORAS.equals(modo)) {
			Tarifa tAux = tarifa.getValue();
			
			datoHoras = (Float) FormateadorDatos.parseaDato(horas.getText(), TipoDato.FORMATO_REAL);
			
			if (tAux!=null) {
				costehora = tAux.costeHora;
			}
			
			datoImporte = datoHoras*costehora;
		}
		if (EditCertificacionFaseParcial.MODO_IMPORTE.equals(modo)) {
			Tarifa tAux = tarifa.getValue();
			
			datoImporte = (Float) FormateadorDatos.parseaDato(importe.getText(), TipoDato.FORMATO_MONEDA);
			
			if (tAux!=null) {
				costehora = tAux.costeHora;
				datoHoras = datoImporte/costehora;
			} else {
				datoHoras = 0;
			}
		}
		if (EditCertificacionFaseParcial.MODO_TARIFA.equals(modo)) {
			Tarifa tAux = tarifa.getValue();
			
			datoHoras = (Float) FormateadorDatos.parseaDato(horas.getText(), TipoDato.FORMATO_REAL);
			
			if (tAux!=null) {
				costehora = tAux.costeHora;
			}
			
			datoImporte = datoHoras*costehora;
		}
		
		importe.setText(FormateadorDatos.formateaDato(datoImporte, TipoDato.FORMATO_MONEDA));
		horas.setText(FormateadorDatos.formateaDato(datoHoras, TipoDato.FORMATO_REAL));
	}
	
	
	@Override
	public String getControlFXML() {
		return EditCertificacionFaseParcial.fxml;
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
