package ui.Economico.EstimacionesInternas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.Proyecto;
import model.beans.Recurso;
import model.beans.RelRecursoTarifa;
import model.beans.Tarifa;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.metadatos.Sistema;
import ui.ControladorPantalla;
import ui.Dialogo;
import ui.GestionBotones;
import ui.Tableable;
import ui.Economico.EstimacionesInternas.tables.LineaCosteProyectoEstimacion;
import ui.Economico.EstimacionesInternas.tables.LineaDetalleMes;
import ui.Economico.EstimacionesInternas.tables.LineaDetalleUsuario;
import ui.popUps.PopUp;

public class EditarEstimacion implements ControladorPantalla, PopUp {

	public static final String fxml = "file:src/ui/Economico/EstimacionesInternas/EditarEstimacion.fxml"; 
	
	public static Object claseRetorno = null;
	public static String metodoRetorno = null;
	public static HashMap<String, Object> variablesPaso = null;
	
	public EstimacionesInternasMes em = null;
	public EstimacionesInternas eI = null;
	public float multiplicador = 0;
	public float restante = 0;
	public float importeEntrada = 0;
	
    @FXML
    private TextField tImporte;

    @FXML
    private TextField tRestante;

    @FXML
    private ComboBox<Proyecto> cbProyecto;

    @FXML
    private ComboBox<Sistema> cbSistema;

    @FXML
    private TextField tHoras;

    @FXML
    private ComboBox<Recurso> cbUsuario;

    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;
    
    @FXML
    private ComboBox<Integer> cbAnio;
    
    @FXML
    private ComboBox<String> cbMes;

	
	@Override
	public AnchorPane getAnchor() {
		return null;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public EditarEstimacion (Object claseRetorno, String metodoRetorno){
		EditarEstimacion.claseRetorno = claseRetorno;
		EditarEstimacion.metodoRetorno = metodoRetorno;
	}
	
	public EditarEstimacion (){
	}
	
	public void initialize(){
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {   
				guardaDatos();
            } }, "Guardar Cambios", this);	
		//gbGuardar.desActivarBoton();
		this.tHoras.focusedProperty().addListener((ov, oldV, newV) -> { 
			if (!newV) { 
				try {
					float horas = (Float) FormateadorDatos.parseaDato(this.tHoras.getText(),FormateadorDatos.FORMATO_REAL);
					
					this.tHoras.setText(FormateadorDatos.formateaDato(horas,FormateadorDatos.FORMATO_REAL));
					this.tImporte.setText(FormateadorDatos.formateaDato(horas*multiplicador,FormateadorDatos.FORMATO_MONEDA));
					this.tRestante.setText(FormateadorDatos.formateaDato(restante-(horas*multiplicador)+importeEntrada,FormateadorDatos.FORMATO_MONEDA));
				} catch (Exception e) {
					this.tImporte.setText("0 €");
					this.tHoras.setText("0");
				}				 
			}
		});
		this.tImporte.focusedProperty().addListener((ov, oldV, newV) -> { 
			if (!newV) { 
				try {
					float importe = (Float) FormateadorDatos.parseaDato(this.tImporte.getText(),FormateadorDatos.FORMATO_MONEDA);
					
					if (multiplicador!=0) 
						this.tHoras.setText(FormateadorDatos.formateaDato(importe/multiplicador,FormateadorDatos.FORMATO_REAL));
					else 
						this.tHoras.setText(FormateadorDatos.formateaDato(importe,FormateadorDatos.FORMATO_REAL));
					this.tImporte.setText(FormateadorDatos.formateaDato(importe,FormateadorDatos.FORMATO_MONEDA));
					
					this.tRestante.setText(FormateadorDatos.formateaDato(restante-importe+importeEntrada,FormateadorDatos.FORMATO_MONEDA));
				} catch (Exception e) {
					this.tImporte.setText("0 €");
					this.tHoras.setText("0");
				}				 
			}
		});
		
		cbProyecto.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			cargaProyecto(newValue);
	    	}
	    ); 
		
		cbUsuario.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			calcularMultiplicador(newValue);
	    	}
	    ); 
		
		cbSistema.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			cargaSistema(newValue);
	    	}
	    );
	}

	@Override
	public String getControlFXML() {
		return fxml;
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		try {
			EditarEstimacion.variablesPaso = variablesPaso;
			eI = null;
			try {
				em = (EstimacionesInternasMes)  variablesPaso.get("controladorPantalla");
				eI = em.eI;
			} catch (Exception e) {
				eI = (EstimacionesInternas)  variablesPaso.get("controladorPantalla");
			}
			
			if (cbAnio == null) return;
			
			Integer anio = eI.cbAnio.getValue(); 
			cbAnio.getItems().add(anio);
			cbAnio.setValue(anio);
			
			cbMes.getItems().removeAll(cbMes.getItems());
			cbMes.getItems().addAll(Constantes.meses);
			
			cbSistema.getItems().removeAll(cbSistema.getItems());
			cbSistema.getItems().addAll(eI.sistemasDisponibles.values());
			
			cbProyecto.getItems().removeAll(cbProyecto.getItems());
			cbProyecto.getItems().addAll(eI.proyectosDisponibles.values());
			
			cbUsuario.getItems().removeAll(cbUsuario.getItems());
			cbUsuario.getItems().addAll(eI.recursosDisponibles.values());
			
			LineaDetalleMes ldm = (LineaDetalleMes) variablesPaso.get("filaDatos");
			
			if (ldm!=null) {
				@SuppressWarnings("unchecked")
				CellEditEvent<Tableable, String> evento = (CellEditEvent<Tableable, String>) variablesPaso.get("evento");
				int fila = evento.getTablePosition().getRow();
				
				this.tHoras.setText(FormateadorDatos.formateaDato(ldm.horas, FormateadorDatos.FORMATO_REAL));
				this.tImporte.setText(FormateadorDatos.formateaDato(ldm.importe, FormateadorDatos.FORMATO_MONEDA));
				importeEntrada = ldm.importe;
				
				Sistema s = em.eI.sistemasDisponibles.get(ldm.sistema);
				Sistema sAux = null;
				
				if (s!=null) {
					Iterator<Sistema> itSistema = cbSistema.getItems().iterator();
					while (itSistema.hasNext()) {
						sAux = itSistema.next(); 
						if (sAux.id == s.id) {
							break;
						}
					}					
				} else {
					LineaDetalleUsuario ldu = (LineaDetalleUsuario) em.eI.tablaUsuario.listaDatosFiltrada.get(fila);
					sAux = em.eI.sistemasDisponibles.get(ldu.sistema);					
					s = sAux;
				}
				
				Proyecto p = em.eI.proyectosDisponibles.get(ldm.concepto);
				cbProyecto.setValue(p);
				cbProyecto.setDisable(true);
				cbSistema.setValue(sAux);
				cbSistema.setDisable(true);				
								
				Iterator<Tableable> itList = eI.tablaResumen.listaDatos.iterator();
				while (itList.hasNext()) {
					LineaCosteProyectoEstimacion lcpe = (LineaCosteProyectoEstimacion) itList.next();
					if (lcpe.proyecto.id == p.id && lcpe.sistema.id == s.id) {
						this.tRestante.setText(FormateadorDatos.formateaDato(new Float(lcpe.restante),FormateadorDatos.FORMATO_MONEDA));
						restante = lcpe.restante;
						break;
					}
				}
				
				Recurso r = em.eI.recursosDisponibles.get(new Integer(ldm.recurso).toString());
				cbUsuario.setValue(r);
				cbUsuario.setDisable(true);
				calcularMultiplicador(r); 
				
				Iterator<String> itMeses = Constantes.meses.iterator();
				while (itMeses.hasNext()) {
					String mesS = itMeses.next();
					if (mesS.equals(Constantes.nomMes(em.mesRepresentado))) {
						cbMes.setValue(mesS);
						break;
					}						
				}
				cbMes.setDisable(true);				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void cargaProyecto(Proyecto p) {
		cbSistema.getItems().removeAll(cbSistema.getItems());
		Iterator<Object> itdatos = eI.tablaResumen.listaDatosEnBruto.iterator();
		
		while (itdatos.hasNext()) {
			LineaCosteProyectoEstimacion lcpe = (LineaCosteProyectoEstimacion) itdatos.next();
			if (lcpe.proyecto.id == p.id) {
				Sistema s = lcpe.sistema;
				cbSistema.getItems().add(s);
			}
		}
	}
	
	public void cargaSistema(Sistema s) {
		try {
			if (cbProyecto.getValue()==null) return;
			if (s==null) return;
			
			Iterator<Tableable> itList = eI.tablaResumen.listaDatos.iterator();
			while (itList.hasNext()) {
				LineaCosteProyectoEstimacion lcpe = (LineaCosteProyectoEstimacion) itList.next();
				if (lcpe.proyecto.id == cbProyecto.getValue().id && lcpe.sistema.id == s.id) {
					this.tRestante.setText(FormateadorDatos.formateaDato(new Float(lcpe.restante),FormateadorDatos.FORMATO_MONEDA));
					restante = lcpe.restante;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void calcularMultiplicador(Recurso r) {
		multiplicador = 0;
		
		try {
			RelRecursoTarifa rrt = new RelRecursoTarifa();
			ArrayList<RelRecursoTarifa> listaTarifas = rrt.buscaRelacion(r.id);
			Tarifa t = rrt.tarifaVigente(listaTarifas, r.id).tarifa;
			multiplicador = t.costeHora;
		} catch (Exception e) {
			multiplicador = 1;
		}
	}
	
	public void guardaDatos() {
		try {
			if (cbProyecto.getValue()==null || this.cbUsuario.getValue() == null || this.cbSistema.getValue()==null) 
				Dialogo.error("Fallo al procesar la petición", "Faltan campos por informar", "Es obligatorio rellenar todos los campos.");
			
			float horas = (Float) FormateadorDatos.parseaDato(this.tHoras.getText(),FormateadorDatos.FORMATO_REAL);
			float importe = (Float) FormateadorDatos.parseaDato(this.tImporte.getText(),FormateadorDatos.FORMATO_MONEDA);
			int mes = Constantes.numMes(this.cbMes.getValue());		
			
			eI.guardaProvisional(this.cbProyecto.getValue(), this.cbUsuario.getValue(), horas, importe, mes, eI.cbAnio.getValue(), this.cbSistema.getValue() );
		} catch (Exception ex) {
			Dialogo.error("Fallo al procesar la petición", "Se produjo un error", "Revise que todos los campo están correctamente informados.");
			ex.printStackTrace();
		}
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
