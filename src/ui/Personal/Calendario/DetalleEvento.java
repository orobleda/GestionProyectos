package ui.Personal.Calendario;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.calendarfx.model.Entry;
import com.calendarfx.view.DateControl.EntryDetailsParameter;

import application.Main;
import controller.Log;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.beans.Evento;
import model.beans.Proyecto;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaParametro;
import model.metadatos.TipoDato;
import model.metadatos.TipoEnumerado;
import model.metadatos.TipoProyecto;
import model.utils.db.ConsultaBD;
import ui.GestionBotones;
import ui.VentanaContextual;
import ui.interfaces.ControladorPantalla;
import ui.popUps.ConsultaAvanzadaProyectos;
import ui.popUps.PopUp;

public class DetalleEvento implements ControladorPantalla, PopUp {

	public static final String fxml = "file:src/ui/Personal/Calendario/DetalleEvento.fxml"; 
	
	public static final String PADRE = "PADRE"; 
	public static final String EVENTO = "EVENTO"; 
	
	public Calendario padre = null;
	public EntryDetailsParameter evento = null;
	public Evento evtModificado = null;
	public Entry evtModificar = null;
	
    @FXML
    private ImageView imEditarChkList;
    private GestionBotones gbEditarChkList;

    @FXML
    private VBox vbListaAdjuntos;

    @FXML
    private ImageView imEditarAdjuntos;
    private GestionBotones gbEditarAdjuntos;

    @FXML
    private VBox vbPadreChkList;

    @FXML
    private VBox vbChkList;

    @FXML
    private CheckBox chkList;

    @FXML
    private VBox vbContenidoAdjunto;

    @FXML
    private VBox vbPropiedades;

    @FXML
    private VBox vbPadreAjuntos;

	@FXML
    private ScrollPane scrDetalles;

    @FXML
    private VBox contenedor;

    @FXML
    private TextField tHoraFin;

    @FXML
    private DatePicker dpFin;

    @FXML
    private ImageView imGuardar;
    GestionBotones gbGuardar;

    @FXML
    private Slider slUrgencia;

    @FXML
    private TextField tDurHoras;

    @FXML
    private TextField tNombreTarea;

    @FXML
    private DatePicker dpInicio;

    @FXML
    private Slider slCriticidad;

    @FXML
    private TextField hInicio;

    @FXML
    private TextArea tCuerpo;

    @FXML
    private ImageView imBorrar;
    GestionBotones gbBorrar;

    @FXML
    private DatePicker dpFinLImite;

    @FXML
    private TextField tHoraFinLimite;
    
    @FXML
    private ListView<Proyecto> lProyectos;
    
    @FXML
    private ImageView imModProyectos;  
    GestionBotones gbModProyectos; 

    @FXML
    private ImageView imVaciaProyectos;
    GestionBotones gbVaciaProyectos; 

    @FXML
    private VBox vbProyectos;    

    @FXML
    private ComboBox<TipoEnumerado> cbTipoEvento;
	   
    @Override
	public void resize(Scene escena) {
    	scrDetalles.setPrefHeight(Main.scene.getHeight()*0.9);
    	
		if (Main.resolucion() == Main.BAJA_RESOLUCION) {
			
		}
	}
    
	public DetalleEvento(){
	}
	
	public void initialize(){
		gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					guardaEvento(); 
				} catch (Exception e) {
					Log.e(e);
				}
            } }, "Guardar Cambios");
		gbGuardar.activarBoton();
		gbBorrar = new GestionBotones(imBorrar, "Eliminar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					eliminaEvento() ;
				} catch (Exception e) {
					Log.e(e);
				}
            } }, "Guardar Cambios");
		gbBorrar.activarBoton();		
		gbModProyectos = new GestionBotones(imModProyectos, "EditListado3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					consultaAvanzadaProyectos();
				} catch (Exception e) {
					Log.e(e);
				}
            } }, "Editar Listado");
		gbModProyectos.activarBoton();
		gbVaciaProyectos = new GestionBotones(imVaciaProyectos, "Eliminar3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					lProyectos.getItems().removeAll(lProyectos.getItems());
				} catch (Exception e) {
					Log.e(e);
				}
            } }, "Editar Listado");
		gbVaciaProyectos.activarBoton();
		gbEditarAdjuntos = new GestionBotones(imEditarAdjuntos, "EditListado3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					//
				} catch (Exception e) {
					Log.e(e);
				}
            } }, "Editar Listado");
		gbEditarAdjuntos.activarBoton();
		gbEditarChkList = new GestionBotones(imEditarChkList, "EditListado3", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				try {	
					//
				} catch (Exception e) {
					Log.e(e);
				}
            } }, "Editar Listado");
		gbEditarChkList.activarBoton();
				
		cbTipoEvento.getItems().addAll(TipoEnumerado.listado.get(TipoDato.FORMATO_TIPO_EVENTO).values());
		
		this.dpFin.focusedProperty().addListener((ov, oldV, newV) -> { if (!newV) { calculaInicioFin(false);calculaLimiteFin(false); calcularDuracion();  }  });
		this.tHoraFin.focusedProperty().addListener((ov, oldV, newV) -> { if (!newV) { formateaHora(tHoraFin); calculaInicioFin(false);calculaLimiteFin(false); calcularDuracion();  }  });
		
		this.dpInicio.focusedProperty().addListener((ov, oldV, newV) -> { if (!newV) { calculaInicioFin(true);calculaLimiteFin(false); calcularDuracion();  }  });
		this.hInicio.focusedProperty().addListener((ov, oldV, newV) -> { if (!newV) { formateaHora(hInicio);calculaInicioFin(true);calculaLimiteFin(false); calcularDuracion();  }  });
		
		this.dpFinLImite.focusedProperty().addListener((ov, oldV, newV) -> { if (!newV) { calculaLimiteFin(true); calculaInicioFin(false);calcularDuracion();  }  });
		this.tHoraFinLimite.focusedProperty().addListener((ov, oldV, newV) -> { if (!newV) { formateaHora(hInicio); calculaLimiteFin(true);calculaInicioFin(false); calcularDuracion();   }  });
		
		this.tDurHoras.focusedProperty().addListener((ov, oldV, newV) -> { if (!newV) { calculaFinPorDuracion();calculaLimiteFin(false);   }  });
		
    }
	
	private void formateaHora(TextField tf) {
		try {
			String valor = tf.getText();
			tf.setText(FormateadorDatos.formateaDato(valor, TipoDato.FORMATO_HORA));
		} catch (Exception e) {
			tf.setText("");
		}
	}
	
	private void calculaInicioFin(boolean inicioModificado) {
		try {
			java.util.Calendar cInicio = java.util.Calendar.getInstance();
			cInicio.setTime(FormateadorDatos.toDate(this.dpInicio.getValue()));
			String[] horaLimite = this.hInicio.getText().split(":");			
			cInicio.set(Calendar.HOUR, new Integer(horaLimite[0]));
			cInicio.set(Calendar.MINUTE, new Integer(horaLimite[1]));
			 
			java.util.Calendar cFin = java.util.Calendar.getInstance();
			cFin.setTime(FormateadorDatos.toDate(this.dpFin.getValue()));
			String[] horaFin = this.tHoraFin.getText().split(":");			
			cFin.set(Calendar.HOUR, new Integer(horaFin[0]));
			cFin.set(Calendar.MINUTE, new Integer(horaFin[1]));
			
			if (cFin.before(cInicio)) {
				if (!inicioModificado){
					cFin.add(Calendar.MINUTE, -15);
					this.hInicio.setText(FormateadorDatos.formateaDato(cFin.getTime(), TipoDato.FORMATO_HORA));
					this.dpInicio.setValue(FormateadorDatos.toLocalDate(cFin.getTime()));
				} else {
					cInicio.add(Calendar.MINUTE, 15);
					this.tHoraFin.setText(FormateadorDatos.formateaDato(cInicio.getTime(), TipoDato.FORMATO_HORA));
					this.dpFin.setValue(FormateadorDatos.toLocalDate(cInicio.getTime()));
				}
			}			
		} catch (Exception e) {
			
		}
	}
	
	private void calculaFinPorDuracion() {
		try {
			Float duracion = (Float) FormateadorDatos.parseaDato(this.tDurHoras.getText(), TipoDato.FORMATO_REAL);
			
			java.util.Calendar cInicio = java.util.Calendar.getInstance();
			cInicio.setTime(FormateadorDatos.toDate(this.dpInicio.getValue()));
			String[] horaLimite = this.hInicio.getText().split(":");			
			cInicio.set(Calendar.HOUR, new Integer(horaLimite[0]));
			cInicio.set(Calendar.MINUTE, new Integer(horaLimite[1]));
			cInicio.add(Calendar.HOUR, duracion.intValue());
			
			this.tHoraFin.setText(FormateadorDatos.formateaDato(cInicio.getTime(), TipoDato.FORMATO_HORA));
			this.dpFin.setValue(FormateadorDatos.toLocalDate(cInicio.getTime()));
						
		} catch (Exception e) {
			this.tDurHoras.setText("0");
		}
	}
	
	private void calculaLimiteFin(boolean limiteModificado) {
		try {
			java.util.Calendar cLimite = java.util.Calendar.getInstance();
			cLimite.setTime(FormateadorDatos.toDate(this.dpFinLImite.getValue()));
			String[] horaLimite = this.tHoraFinLimite.getText().split(":");			
			cLimite.set(Calendar.HOUR, new Integer(horaLimite[0]));
			cLimite.set(Calendar.MINUTE, new Integer(horaLimite[1]));
			 
			java.util.Calendar cFin = java.util.Calendar.getInstance();
			cFin.setTime(FormateadorDatos.toDate(this.dpFin.getValue()));
			String[] horaFin = this.tHoraFin.getText().split(":");			
			cFin.set(Calendar.HOUR, new Integer(horaFin[0]));
			cFin.set(Calendar.MINUTE, new Integer(horaFin[1]));
			
			if (cFin.after(cLimite)) {
				if (!limiteModificado){
					this.tHoraFinLimite.setText(this.tHoraFin.getText());
					this.dpFinLImite.setValue(this.dpFin.getValue());
				} else {
					this.tHoraFin.setText(this.tHoraFinLimite.getText());
					this.dpFin.setValue(this.dpFinLImite.getValue());
				}
			}			
		} catch (Exception e) {
			
		}
	}
	
	private void calcularDuracion() {
		try {
			if (this.tHoraFin.getText()==null || "".equals(this.tHoraFin.getText()) ||
					this.hInicio.getText()==null || "".equals(this.hInicio.getText())) {
				this.tDurHoras.setText("");
				return;
			}
			
			java.util.Calendar cInicio = java.util.Calendar.getInstance();
			cInicio.setTime(FormateadorDatos.toDate(this.dpInicio.getValue()));
			String[] horaInicio = this.hInicio.getText().split(":");			
			cInicio.set(Calendar.HOUR, new Integer(horaInicio[0]));
			cInicio.set(Calendar.MINUTE, new Integer(horaInicio[1]));
			 
			java.util.Calendar cFin = java.util.Calendar.getInstance();
			cFin.setTime(FormateadorDatos.toDate(this.dpFin.getValue()));
			String[] horaFin = this.tHoraFin.getText().split(":");			
			cFin.set(Calendar.HOUR, new Integer(horaFin[0]));
			cFin.set(Calendar.MINUTE, new Integer(horaFin[1]));
			
			long difFechas =  cFin.getTime().getTime() - cInicio.getTime().getTime();
			float horasDiferencia = difFechas/3600000;
			this.tDurHoras.setText(FormateadorDatos.formateaDato(horasDiferencia, TipoDato.FORMATO_REAL));						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void consultaAvanzadaProyectos() throws Exception{		
		ConsultaAvanzadaProyectos.getInstance(padre.vc,this, 1000, TipoProyecto.ID_TODO, this.lProyectos);
	}
	
	public void fijaProyecto(ArrayList<Proyecto> listaProyecto) {
		if (listaProyecto!=null) {
			this.lProyectos.getItems().removeAll(this.lProyectos.getItems());
			this.lProyectos.getItems().addAll(listaProyecto);
			padre.vc.showAtras();
		}
	}
	
	@Override
	public AnchorPane getAnchor() {
		return null;
	}

	@Override
	public String getFXML() {
		return fxml;
	}

	@Override
	public String getControlFXML() {
		return fxml;
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		padre = (Calendario) variablesPaso.get(DetalleEvento.PADRE);
		evento= (EntryDetailsParameter) variablesPaso.get(DetalleEvento.EVENTO);
		
		Entry evt = evento.getEntry();
		evtModificar = evt;
		
		this.tNombreTarea.setText(evento.getEntry().getTitle());
		this.dpInicio.setValue(evt.getStartDate());
		this.dpFin.setValue(evt.getEndDate());
		this.dpFinLImite.setValue(evt.getEndDate());
		
		if (Calendario.REUNIONES.equals(evt.getCalendar().getName())) this.cbTipoEvento.setValue(TipoEnumerado.getPorCod(TipoDato.FORMATO_TIPO_EVENTO, TipoEnumerado.TIPO_EVENTO_REU));
		if (Calendario.PERSONAL.equals(evt.getCalendar().getName())) this.cbTipoEvento.setValue(TipoEnumerado.getPorCod(TipoDato.FORMATO_TIPO_EVENTO, TipoEnumerado.TIPO_EVENTO_PERS));
		if (Calendario.TAREAS.equals(evt.getCalendar().getName())) this.cbTipoEvento.setValue(TipoEnumerado.getPorCod(TipoDato.FORMATO_TIPO_EVENTO, TipoEnumerado.TIPO_EVENTO_TAR));
		if (Calendario.FESTIVOS.equals(evt.getCalendar().getName())){
			this.cbTipoEvento.setValue(TipoEnumerado.getPorCod(TipoDato.FORMATO_TIPO_EVENTO, TipoEnumerado.TIPO_EVENTO_TAR));
			this.gbBorrar.desActivarBoton();
			this.gbGuardar.desActivarBoton();
		}		
		
		try {
			java.util.Calendar cInicio = java.util.Calendar.getInstance();
			cInicio.setTime(FormateadorDatos.toDate(evt.getStartDate()));
			cInicio.set(Calendar.HOUR, evt.getStartTime().getHour());
			cInicio.set(Calendar.MINUTE, evt.getStartTime().getMinute());
			this.hInicio.setText(FormateadorDatos.formateaDato(cInicio.getTime(), TipoDato.FORMATO_HORA));
			java.util.Calendar cFin = java.util.Calendar.getInstance();
			cFin.setTime(FormateadorDatos.toDate(evt.getEndDate()));
			cFin.set(Calendar.HOUR, evt.getEndTime().getHour());
			cFin.set(Calendar.MINUTE, evt.getEndTime().getMinute());
			this.tHoraFin.setText(FormateadorDatos.formateaDato(cFin.getTime(), TipoDato.FORMATO_HORA));
			this.tHoraFinLimite.setText(FormateadorDatos.formateaDato(cFin.getTime(), TipoDato.FORMATO_HORA));
			
			long difFechas =  cFin.getTime().getTime() - cInicio.getTime().getTime();
			float horasDiferencia = difFechas/3600000;
			this.tDurHoras.setText(FormateadorDatos.formateaDato(horasDiferencia, TipoDato.FORMATO_REAL));						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Evento oEvento = padre.mapEventos.get(evt.getUserObject());
		
		if (oEvento!=null) {
			evtModificado = oEvento;
			
			if (oEvento.fxLimite!=null) {
				this.dpFinLImite.setValue(FormateadorDatos.toLocalDate(oEvento.fxLimite));
			} else {
				this.dpFinLImite.setValue(evt.getEndDate());
			}
			
			this.slCriticidad.setValue(oEvento.criticidad*100/5);
			this.slUrgencia.setValue(oEvento.urgencia*100/5);
			this.tCuerpo.setText(oEvento.descripcion);
			
			try {
				this.hInicio.setText(FormateadorDatos.formateaDato(oEvento.fxInicio, TipoDato.FORMATO_HORA));
				this.tHoraFin.setText(FormateadorDatos.formateaDato(oEvento.fxFin, TipoDato.FORMATO_HORA));
				if (oEvento.fxLimite!=null)
					this.tHoraFinLimite.setText(FormateadorDatos.formateaDato(oEvento.fxLimite, TipoDato.FORMATO_HORA));
				else 
					this.tHoraFinLimite.setText(FormateadorDatos.formateaDato(oEvento.fxFin, TipoDato.FORMATO_HORA));
				
				long difFechas =  oEvento.fxFin.getTime() - oEvento.fxInicio.getTime();
				float horasDiferencia = difFechas/3600000;
				this.tDurHoras.setText(FormateadorDatos.formateaDato(horasDiferencia, TipoDato.FORMATO_REAL));						
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (oEvento.listaProyectos!=null && oEvento.listaProyectos.size()!=0) {
				this.lProyectos.getItems().addAll(oEvento.listaProyectos);
			}
			
		} else {
			this.cbTipoEvento.setValue(TipoEnumerado.getPorCod(TipoDato.FORMATO_TIPO_EVENTO, TipoEnumerado.TIPO_EVENTO_TAR));
		}	
		
		resize(null);
	}
	
	public void eliminaEvento() throws Exception {
		if (this.evtModificado!=null){
			String idTransaccion = ConsultaBD.getTicket();
			this.evtModificado.borrarEvento(idTransaccion);
			ConsultaBD.ejecutaTicket(idTransaccion);
			
			this.evtModificar.setUserObject(null);
		}
			
		VentanaContextual.hide();
	}
	
	public void guardaEvento() throws Exception{
		Evento oEvento = new Evento();
		oEvento.listaParametros = MetaParametro.dameParametros(oEvento.getClass().getSimpleName(), -1);
		//oEvento.codAdjunto = null;
		oEvento.criticidad = new Double(this.slCriticidad.getValue()*5/100).intValue();
		oEvento.descripcion = this.tCuerpo.getText();
		//oEvento.eventoDiario = 
		oEvento.fxFin = FormateadorDatos.toDate(this.dpFin.getValue());
		oEvento.tipo = this.cbTipoEvento.getValue();
		
		java.util.Calendar cAux = java.util.Calendar.getInstance();
		cAux.setTime(oEvento.fxFin);
		String [] horasMin = this.tHoraFin.getText().split(":");
		cAux.set(java.util.Calendar.HOUR, new Integer(horasMin[0]));
		cAux.set(java.util.Calendar.MINUTE, new Integer(horasMin[1]));
		cAux.set(java.util.Calendar.SECOND, 0);
		cAux.set(java.util.Calendar.MILLISECOND, 0);
		
		oEvento.fxFin = cAux.getTime();
		
		oEvento.fxInicio = FormateadorDatos.toDate(this.dpInicio.getValue());
		cAux = java.util.Calendar.getInstance();
		cAux.setTime(oEvento.fxInicio);
		horasMin = this.hInicio.getText().split(":");
		cAux.set(java.util.Calendar.HOUR, new Integer(horasMin[0]));
		cAux.set(java.util.Calendar.MINUTE, new Integer(horasMin[1]));
		cAux.set(java.util.Calendar.SECOND, 0);
		cAux.set(java.util.Calendar.MILLISECOND, 0);		
		oEvento.fxInicio = cAux.getTime();
		
		if (this.dpFinLImite.getValue()!=null) {
			oEvento.fxLimite = FormateadorDatos.toDate(this.dpFinLImite.getValue());
			cAux = java.util.Calendar.getInstance();
			cAux.setTime(oEvento.fxLimite);
			horasMin = this.hInicio.getText().split(":");
			cAux.set(java.util.Calendar.HOUR, new Integer(horasMin[0]));
			cAux.set(java.util.Calendar.MINUTE, new Integer(horasMin[1]));
			cAux.set(java.util.Calendar.SECOND, 0);
			cAux.set(java.util.Calendar.MINUTE, 0);		
			oEvento.fxLimite = cAux.getTime();			
		}
		//oEvento.idProyEvento
		oEvento.nombre = this.tNombreTarea.getText();
		oEvento.urgencia = new Double(this.slUrgencia.getValue()*5/100).intValue();
		
		oEvento.listaProyectos = new ArrayList<Proyecto> ();
		oEvento.listaProyectos.addAll(this.lProyectos.getItems());
		
		String idTransaccion = ConsultaBD.getTicket();
		
		if (evtModificado!=null){
			oEvento.id = evtModificado.id;
			oEvento.updateEvento(idTransaccion);
			
			evtModificar.setTitle(oEvento.nombre);
			if (oEvento.eventoDiario) {
				evtModificar.setFullDay(true);
				evtModificar.setInterval(FormateadorDatos.toLocalDate(oEvento.fxInicio));
			} else {
				evtModificar.setFullDay(false);
				evtModificar.changeStartDate(FormateadorDatos.toLocalDate(oEvento.fxInicio));
				evtModificar.changeStartTime(FormateadorDatos.toLocalTime(oEvento.fxInicio));
				evtModificar.changeEndDate(FormateadorDatos.toLocalDate(oEvento.fxFin));
				evtModificar.changeEndTime(FormateadorDatos.toLocalTime(oEvento.fxFin));
			}
			padre.mapEventos.remove(oEvento.id+"");			
		}
		else {
			oEvento.insertEvento(idTransaccion);
		}
		
		evtModificar.removeFromCalendar();
		
		if (oEvento.tipo.codigo.equals(TipoEnumerado.TIPO_EVENTO_REU)) padre.reuniones.addEntry(evtModificar);
		if (oEvento.tipo.codigo.equals(TipoEnumerado.TIPO_EVENTO_PERS)) padre.personal.addEntry(evtModificar);
		if (oEvento.tipo.codigo.equals(TipoEnumerado.TIPO_EVENTO_TAR)) padre.eventos.addEntry(evtModificar);
		
		evtModificar.setUserObject(oEvento.id+"");
		ConsultaBD.ejecutaTicket(idTransaccion);
		
		padre.mapEventos.put(oEvento.id+"", oEvento);
		VentanaContextual.hide();
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


