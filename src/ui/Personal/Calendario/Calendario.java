package ui.Personal.Calendario;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DateControl.EntryDetailsParameter;

import controller.Log;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.beans.Evento;
import model.constantes.FormateadorDatos;
import model.metadatos.Festivo;
import model.metadatos.TipoEnumerado;
import ui.VentanaContextual;
import ui.interfaces.ControladorPantalla;

public class Calendario implements ControladorPantalla {

	public static final String fxml = "file:src/ui/Personal/Calendario/Calendario.fxml"; 
	
	public static final String REUNIONES = "Reuniones";
	public static final String TAREAS = "Tareas";
	public static final String PERSONAL = "Personal";
	public static final String FESTIVOS = "Festivo/Vacaciones";
	
	Calendar reuniones = null;
    Calendar eventos = null;
    Calendar personal = null;
    Calendar festivo = null;
    
    public HashMap<String,Evento> mapEventos = new HashMap<String,Evento>();
    public CalendarView calendarView = null;
    
    public VentanaContextual vc = null;
	
    @FXML
    private VBox contenedorCalendar;	
	   
    @Override
	public void resize(Scene escena) {
		
	}
    
	public Calendario(){
	}
	
	public void initialize(){
		calendarView = new CalendarView();

        reuniones = new Calendar(Calendario.REUNIONES);
        eventos = new Calendar(Calendario.TAREAS);
        personal = new Calendar(Calendario.PERSONAL);
        festivo = new Calendar(Calendario.FESTIVOS);

        reuniones.setShortName("R");
        eventos.setShortName("T");
        personal.setShortName("P");
        festivo.setShortName("F");

        reuniones.setStyle(Style.STYLE1);
        eventos.setStyle(Style.STYLE2);
        personal.setStyle(Style.STYLE3);
        festivo.setStyle(Style.STYLE4);

        CalendarSource familyCalendarSource = new CalendarSource("Trabajo");
        familyCalendarSource.getCalendars().addAll(eventos,reuniones, personal,festivo);

        calendarView.getCalendarSources().setAll(familyCalendarSource);
        calendarView.setRequestedTime(LocalTime.now());
        
        calendarView.setShowPrintButton(false);
        calendarView.setShowAddCalendarButton(false);
        calendarView.setEntryDetailsCallback(param -> {
        	try {
	            InputEvent evt = param.getInputEvent();
	            if (evt instanceof MouseEvent) {
	                MouseEvent mouseEvent = (MouseEvent) evt;
	                if (mouseEvent.getClickCount() == 2) {
	                	muestraDetalleEvento(param);
	                	return true;
	                }
	            } 
        	} catch (Exception ex) {
        		Log.e(ex);
        	}

            return false;
        });
                
        pintaFestivos(festivo);
        pintaEventos();
        
        contenedorCalendar.getChildren().addAll(calendarView); 
        
        calendarView.setToday(LocalDate.now());
        calendarView.setTime(LocalTime.now());
    }
	
	public void refrescaCalendario() {
		ArrayList<Entry<?>> listaPerdidos = new ArrayList<Entry<?>>();
					
		Iterator<Calendar> itCal  = calendarView.getCalendars().iterator();
		while (itCal.hasNext()) {
			Calendar cal = itCal.next();
			if (cal.getName()!="Festivo/Vacaciones") {
				Iterator<Entry<?>> itEntry = cal.findEntries("").iterator();
				while (itEntry.hasNext()) {
					Entry<?> entrada = itEntry.next();
					if (entrada.getUserObject()==null) {
						listaPerdidos.add(entrada);
					}
				}
			}
		}
		
		Iterator<Entry<?>> itEntry = listaPerdidos.iterator();
		while (itEntry.hasNext()) {
			Entry<?> entrada = itEntry.next();
			entrada.removeFromCalendar();
		}
		
		calendarView.refreshData();
	}
	
	public void muestraDetalleEvento(EntryDetailsParameter edp) throws Exception {
		FXMLLoader loader = new FXMLLoader();
	    ControladorPantalla controlPantalla = new DetalleEvento();
	        		        
	    loader.setLocation(new URL(controlPantalla.getFXML()));
          
	    vc = new VentanaContextual(loader.load(),new ChangeListener<Boolean>() { 
        	public void changed(
					ObservableValue<? extends Boolean> arg0,
					Boolean arg1,
					Boolean arg2) {
        		try {
        			if (!arg2)
        			 refrescaCalendario();
        		} catch (Exception ex) {}
			} 
        });
	    DetalleEvento c = (DetalleEvento) loader.getController();
	    
                
        HashMap<String, Object> variablesPaso = new HashMap<String, Object>();
        variablesPaso.put(DetalleEvento.PADRE, this);
        variablesPaso.put(DetalleEvento.EVENTO, edp);
        
        c.setParametrosPaso(variablesPaso);
        
        vc.show(null);
	}
	
	private void pintaEventos() {
		Evento evt = new Evento();
		ArrayList<Evento> listaEventos = evt.listaEventos();
		Iterator<Evento> itEvt = listaEventos.iterator();
		
		while (itEvt.hasNext()) {
			evt = itEvt.next();
			
			Entry<String> entrada = null;
			
			mapEventos.put(evt.id+"", evt);
			
			entrada = new Entry<>(evt.nombre);
			entrada.setUserObject(evt.id+"");
			if (evt.eventoDiario) {
				entrada.setFullDay(true);
				entrada.setInterval(FormateadorDatos.toLocalDate(evt.fxInicio));
			} else {
				entrada.setFullDay(false);
				entrada.changeStartDate(FormateadorDatos.toLocalDate(evt.fxInicio));
				entrada.changeStartTime(FormateadorDatos.toLocalTime(evt.fxInicio));
				entrada.changeEndDate(FormateadorDatos.toLocalDate(evt.fxFin));
				entrada.changeEndTime(FormateadorDatos.toLocalTime(evt.fxFin));
			}
			
			if (evt.tipo.codigo.equals(TipoEnumerado.TIPO_EVENTO_REU)) {
				this.reuniones.addEntry(entrada);
			}
			if (evt.tipo.codigo.equals(TipoEnumerado.TIPO_EVENTO_PERS)) {
				this.personal.addEntry(entrada);
			}
			if (evt.tipo.codigo.equals(TipoEnumerado.TIPO_EVENTO_TAR)) {
				this.eventos.addEntry(entrada);
			}			
		}
	}
	
	private void pintaFestivos(Calendar festivo) {
		Iterator<Festivo> itFestivos = Festivo.listado.values().iterator();
		Entry<String> entradaFestivo = null;
		
		while (itFestivos.hasNext()) {
			Festivo f = itFestivos.next();
			entradaFestivo = new Entry<>("Festivo");
			entradaFestivo.setFullDay(true);
			entradaFestivo.setInterval(FormateadorDatos.toLocalDate(f.dia));
			festivo.addEntry(entradaFestivo);
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
	
}


