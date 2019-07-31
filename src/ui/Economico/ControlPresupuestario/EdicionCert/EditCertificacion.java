package ui.Economico.ControlPresupuestario.EdicionCert;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.beans.Certificacion;
import model.beans.CertificacionFase;
import model.beans.Parametro;
import model.beans.Proveedor;
import model.beans.Tarifa;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import model.metadatos.TipoDato;
import model.utils.db.ConsultaBD;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Administracion.Parametricas.GestionParametros;
import ui.Economico.ControlPresupuestario.ControlPresupuestario;
import ui.interfaces.ControladorPantalla;
import ui.popUps.PopUp;

public class EditCertificacion implements ControladorPantalla, PopUp {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/EdicionCert/EditCertificacion.fxml";
	
	public boolean esPopUp = false;
	public static HashMap<String, Object> variablesPaso = null;
	public static Object claseRetorno = null;
	public static String metodoRetorno = null;
	
	public Certificacion certificacion = null;
	
	public HashMap<String,Parametro> listaParametros = null;
	
    @FXML
    private TextField tImporte;

    @FXML
    private ComboBox<Sistema> cbSistema;

    @FXML
    private TextField tHoras;

    @FXML
    private ComboBox<Tarifa> cbTarifas;

    @FXML
    private HBox hbProperties;
    
    @FXML
    private ImageView imGuardar;
    
    private GestionBotones gbGuardar;
    
    @FXML
    private ImageView imBorrar;
    
    private GestionBotones gbBorrar;
	
	@FXML
	private AnchorPane anchor;
	
	public EditCertificacion (){
	}
	
   @Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	@Override
	public void resize(Scene escena) {
		
	}
	
	public void initialize(){
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
            } }, "Guardar Certificación");
		gbGuardar.activarBoton();
		
		gbBorrar = new GestionBotones(imBorrar, "Eliminar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            { 
				try {
					borrarCertificacion();
					ControlPresupuestario.salvaPosicionActual();
	            	ParamTable.po.hide();
	            	Dialogo.alert("Borrado de Certificación correcto", "Eliminación Correcta", "Se eliminó la certificación");
	            	ControlPresupuestario.cargaPosicionActual();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				//ParamTable.po.hide();
            } }, "Borrar Certificación");
		gbBorrar.activarBoton();
		
		this.tImporte.focusedProperty().addListener((ov, oldV, newV) -> { 
			if (!newV) {
				try {
					String textImporte = this.tImporte.getText();
					float importe = (Float) FormateadorDatos.parseaDato(textImporte, TipoDato.FORMATO_MONEDA);
					
					if (cbTarifas.getValue()!=null) {
						Tarifa t = cbTarifas.getValue();
						float horas = importe/t.costeHora;
						this.tHoras.setText(FormateadorDatos.formateaDato(horas, TipoDato.FORMATO_REAL));
					}
					
					this.tImporte.setText(FormateadorDatos.formateaDato(importe, TipoDato.FORMATO_MONEDA));
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
					
					if (cbTarifas.getValue()!=null) {
						Tarifa t = cbTarifas.getValue();
						float importe = horas * t.costeHora;
						this.tImporte.setText(FormateadorDatos.formateaDato(importe, TipoDato.FORMATO_MONEDA));
					}
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
	
	public void pintaValores(Certificacion cert) throws Exception{
		this.certificacion = cert;
		
		this.cbSistema.getItems().removeAll(this.cbSistema.getItems());
		this.cbSistema.getItems().addAll(Sistema.listado.values());
		this.cbSistema.setDisable(true);
		this.cbSistema.setValue(cert.s);
		
		float coste = cert.calculaCoste();
		Tarifa t = cert.getTarifa();
		
		this.cbTarifas.getItems().removeAll(this.cbTarifas.getItems());
		
		Parametro par = new Parametro();
		par = par.dameParametros(cert.s.getClass().getSimpleName(), cert.s.id).get(MetaParametro.PARAMETRO_SISTEMA_PROVEEDOR);
				
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
        
        if (idCertificacion==-1)
           variablesPaso.put("idEntidadBuscar", idCertificacion);
        else
           variablesPaso.put("idEntidadBuscar", Parametro.SOLO_METAPARAMETROS);
        
        variablesPaso.put("ancho", new Double(800));
        variablesPaso.put("alto", new Double(200));
        gestPar.setParametrosPaso(variablesPaso);
        
        listaParametros = gestPar.listaParametros;
	}
	
	public void guardarCertificacion() throws Exception{
		float importe = (Float) FormateadorDatos.parseaDato(this.tImporte.getText(), TipoDato.FORMATO_MONEDA);
		
		Iterator<CertificacionFase> itCf = this.certificacion.certificacionesFases.iterator();
		while (itCf.hasNext()) {
			CertificacionFase cf = itCf.next();
			cf.reparteCoste(importe * cf.porcentaje/100, this.cbTarifas.getValue());
		}
		
		String idTransaccion = ConsultaBD.getTicket();
		
		this.certificacion.guardarCertificacion(idTransaccion);
		
		ConsultaBD.ejecutaTicket(idTransaccion);
	}
	
	public void borrarCertificacion() throws Exception{
		if (this.certificacion.id==-1) return;
				
		String idTransaccion = ConsultaBD.getTicket();
		
		this.certificacion.borraCertificacion(idTransaccion);
		
		ConsultaBD.ejecutaTicket(idTransaccion);
	}
	
	@Override
	public String getControlFXML() {
		return EditCertificacion.fxml;
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
