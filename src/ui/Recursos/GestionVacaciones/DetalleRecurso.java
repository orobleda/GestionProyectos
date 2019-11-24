package ui.Recursos.GestionVacaciones;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import application.Main;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.beans.JornadasMes;
import model.beans.Recurso;
import model.beans.VacacionesAusencias;
import model.constantes.FormateadorDatos;
import ui.Dialogo;
import ui.Tabla;
import ui.Recursos.GestionVacaciones.Tables.HorasJornada;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class DetalleRecurso implements ControladorPantalla {
									   
	public static final String fxml = "file:src/ui/Recursos/GestionVacaciones/DetalleRecurso.fxml"; 
	
	public static Recurso recurso = null;
	public static int mes = 0;
	public static int anio = 0;
	public static VacacionesAusencias vacasAusencias = null;
	
	public  Recurso recursoObject = null;
	private JornadasMes horasJornada = null;
	private JornadasMes horasVacaciones = null;
	private JornadasMes horasAusencias = null;
	private JornadasMes horasTotal = null;
	
	@FXML
    private TextField tComputoHoras;
	@FXML
    private TextField tTotalHorasJornada;
	
	@FXML
	private AnchorPane anchor;
    @FXML
    private TextField tRecurso;

    @FXML
    private TableView<Tableable> tVacaciones;
    private Tabla tablaVacaciones;

    @FXML
    private TableView<Tableable> tAusencias;
    private Tabla tablaAusencias;

    @FXML
    private TableView<Tableable> tJornada;
    private Tabla tablaJornada;

    @FXML
    private TableView<Tableable> tTotal;
    private Tabla tablaTotal;
    
    @FXML
    private ScrollPane tJornadaSP;
    @FXML
    private ScrollPane tAusenciasSP;
    @FXML
    private ScrollPane tVacacionesSP;
    @FXML
    private ScrollPane tTotalSP;    

    @FXML
    private VBox vbDetalle;
	
	public ArrayList<TableView<Tableable>> tablas = new ArrayList<TableView<Tableable>>();
		
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
		int res = Main.resolucion();
		
		if (res == Main.ALTA_RESOLUCION || res== Main.BAJA_RESOLUCION) {
			if (vbDetalle!=null) {
			}
		}
	}
	
	public DetalleRecurso(Recurso r, int mes, int anio){
		DetalleRecurso.recurso = r;
		DetalleRecurso.mes = mes;
		DetalleRecurso.anio = anio;
	}
	
	public DetalleRecurso(){		
		
	}
	
	public void initialize(){
		 GestionVacaciones.adscribirDetalleRecurso(this);
		 
		 vacasAusencias = new VacacionesAusencias();
		 vacasAusencias.listado(recursoObject, mes, anio);
		
		 informaHorasJornada();
		 informaHorasVacaciones();
		 informaHorasAusencias();
		 informaHorasTotal();
		 
		 tRecurso.setText(recursoObject.toString());
		 
		 tJornada.getItems().remove(1, tJornada.getItems().size());
		 
		 tJornadaSP.hvalueProperty().addListener(new ChangeListener<Number>() 
	     {
	          @Override
	          public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) 
	          { 
	            tAusenciasSP.setHvalue(newValue.doubleValue());
	            tVacacionesSP.setHvalue(newValue.doubleValue());
	        	tTotalSP.setHvalue(newValue.doubleValue());
	          }
	     });
		 
		 tAusenciasSP.hvalueProperty().addListener(new ChangeListener<Number>() 
	     {
	          @Override
	          public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) 
	          {
	        	  tJornadaSP.setHvalue(newValue.doubleValue());
	        	  tVacacionesSP.setHvalue(newValue.doubleValue());
	        	  tTotalSP.setHvalue(newValue.doubleValue());
	          }
	     });
		 tVacacionesSP.hvalueProperty().addListener(new ChangeListener<Number>() 
	     {
	          @Override
	          public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) 
	          {
	        	  tJornadaSP.setHvalue(newValue.doubleValue());
	        	  tAusenciasSP.setHvalue(newValue.doubleValue());
	        	  tTotalSP.setHvalue(newValue.doubleValue());
	          }
	     });
		 tTotalSP.hvalueProperty().addListener(new ChangeListener<Number>() 
	     {
	          @Override
	          public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) 
	          {
	        	  tJornadaSP.setHvalue(newValue.doubleValue());
	        	  tAusenciasSP.setHvalue(newValue.doubleValue());
	        	  tVacacionesSP.setHvalue(newValue.doubleValue());
	          }
	     });
		 
		 resize(null);		 	 
	}
	
	private void informaHorasTotal(){
		JornadasMes jm = new JornadasMes(this.horasJornada,this.horasVacaciones, this.horasAusencias, JornadasMes.TOTAL, recursoObject, DetalleRecurso.mes, DetalleRecurso.anio);
		horasTotal = jm;
		HorasJornada hj = new HorasJornada(jm);
		ArrayList<Object> lista = new ArrayList<Object>();
		lista.add(hj);
		tablaTotal = new Tabla(tTotal,hj,this);		
		tablaTotal.pintaTabla(lista);
		
		ObservableList<TableColumn<Tableable,?>> columnas = tTotal.getColumns(); 
		Iterator<TableColumn<Tableable,?>> itCol = columnas.iterator();
		
		while (itCol.hasNext()) {
			@SuppressWarnings("unchecked")
			TableColumn<Tableable,String> columna = (TableColumn<Tableable,String>) itCol.next();
			columna.setCellValueFactory(
					cellData ->
						new SimpleStringProperty(cellData.getValue().get(
							cellData.getTableColumn().getId())));
			
			columna.setCellFactory(column -> {
			    return new TableCell<Tableable, String>() {
			        protected void updateItem(String item, boolean empty) {
			        	setText(item);
			        	
			        	if ("".equals(item)){
			        		Calendar c = Calendar.getInstance();
				        	c.set(anio, mes-1, new Integer(columna.getId()));
				        	int diaSemana = c.get(Calendar.DAY_OF_WEEK);
				        	
				        	if (Calendar.SUNDAY == diaSemana || Calendar.SATURDAY==diaSemana) {
				        		setStyle("-fx-background-color: MediumAquaMarine");
				        	} else {
				        		setStyle("-fx-background-color: LightYellow");
				        	}
			        	} 
			        }
			    };
			});
		}
		
		tTotal.refresh();
		
		try {
			tComputoHoras.setText(FormateadorDatos.formateaDato(jm.horasAcumuladas,FormateadorDatos.FORMATO_REAL));
		} catch (Exception e) {
			tComputoHoras.setText(new Double(jm.horasAcumuladas).toString());
		}
    }
	
	public int guardaDatos(String idTransaccion) throws Exception{
		return horasTotal.salvaAusenciasVacaciones(this.horasVacaciones, this.horasAusencias, idTransaccion);
	}
	
	private void informaHorasJornada(){
		JornadasMes jm = new JornadasMes(JornadasMes.JORNADAS, recursoObject, mes, anio, vacasAusencias);
		this.horasJornada = jm;
		HorasJornada hj = new HorasJornada(jm);
		ArrayList<Object> lista = new ArrayList<Object>();
		lista.add(hj);
		tablaTotal = new Tabla(tJornada,hj,this);		
		tablaTotal.pintaTabla(lista);	
		
		ObservableList<TableColumn<Tableable,?>> columnas = tJornada.getColumns(); 
		Iterator<TableColumn<Tableable,?>> itCol = columnas.iterator();
		
		while (itCol.hasNext()) {
			@SuppressWarnings("unchecked")
			TableColumn<Tableable,String> columna = (TableColumn<Tableable,String>) itCol.next();
			columna.setCellValueFactory(
					cellData ->
						new SimpleStringProperty(cellData.getValue().get(
							cellData.getTableColumn().getId())));
			
			columna.setCellFactory(column -> {
			    return new TableCell<Tableable, String>() {
			        protected void updateItem(String item, boolean empty) {
			        	setText(item);
			        	
			        	if ("".equals(item)){
			        		Calendar c = Calendar.getInstance();
				        	c.set(anio, mes-1, new Integer(columna.getId()));
				        	int diaSemana = c.get(Calendar.DAY_OF_WEEK);
				        	
				        	if (Calendar.SUNDAY == diaSemana || Calendar.SATURDAY==diaSemana) {
				        		setStyle("-fx-background-color: MediumAquaMarine");
				        	} else {
				        		setStyle("-fx-background-color: LightYellow");
				        	}
			        	} 
			        }
			    };
			});
		}
		
		tJornada.refresh();
		
		try {
			tTotalHorasJornada.setText(FormateadorDatos.formateaDato(jm.horasAcumuladas,FormateadorDatos.FORMATO_REAL));
		} catch (Exception e) {
			tTotalHorasJornada.setText(new Double(jm.horasAcumuladas).toString());
		}
    }
	
	private void informaHorasVacaciones(){
		JornadasMes jm = new JornadasMes(JornadasMes.VACACIONES, recursoObject, mes, anio, vacasAusencias);
		this.horasVacaciones = jm;
		HorasJornada hj = new HorasJornada(jm);
		ArrayList<Object> lista = new ArrayList<Object>();
		lista.add(hj);
		tablaTotal = new Tabla(tVacaciones,hj,this);		
		tablaTotal.pintaTabla(lista);
		
		tVacaciones.getSelectionModel().setCellSelectionEnabled(true);
		
		tVacaciones.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	        	diaSeleccionadoVacaciones(tVacaciones, tVacaciones.getSelectionModel().getSelectedCells().get(0).getColumn());
	        	;
	        }
	    });
		
		ObservableList<TableColumn<Tableable,?>> columnas = tVacaciones.getColumns(); 
		Iterator<TableColumn<Tableable,?>> itCol = columnas.iterator();
		
		while (itCol.hasNext()) {
			@SuppressWarnings("unchecked")
			TableColumn<Tableable,String> columna = (TableColumn<Tableable,String>) itCol.next();
			columna.setCellValueFactory(
					cellData ->
						new SimpleStringProperty(cellData.getValue().get(
							cellData.getTableColumn().getId())));
			
			columna.setCellFactory(column -> {
			    return new TableCell<Tableable, String>() {
			        protected void updateItem(String item, boolean empty) {
			        	setText(item);
			        	
			        	if ("".equals(item)){
			        		Calendar c = Calendar.getInstance();
				        	c.set(anio, mes-1, new Integer(columna.getId()));
				        	int diaSemana = c.get(Calendar.DAY_OF_WEEK);
				        	
				        	if (Calendar.SUNDAY == diaSemana || Calendar.SATURDAY==diaSemana) {
				        		setStyle("-fx-background-color: MediumAquaMarine");
				        	} 
			        	} else  {
			        		if ("-2".equals(item)) {
			        			setText("");
			        		} else {
				        		setStyle("-fx-background-color: LightYellow");
				        		setText("");
			        		}
			        	}
			        }
			    };
			});
		}
		
		tVacaciones.refresh();
    }
	
	public void diaSeleccionadoAusencia(TableView<Tableable> tabla, int columna){
		try {
			if (this.horasJornada.jornadas.get(columna)<0) {
				return;
			} 
			if (this.horasVacaciones.jornadas.get(columna)==100) {
				Dialogo.error("Modificación de Ausencias y Vacaciones", "Colisión información", "Ya hay un día de vacaciones programado para ese día");
			} else {
				if (this.horasAusencias.jornadas.get(columna)<0) {
					this.horasAusencias.jornadas.set(columna,new Float(1));
				} else {
					float horas = this.horasAusencias.jornadas.get(columna);
					horas+=1;
					if (horas>this.horasJornada.jornadas.get(columna)) {
						this.horasAusencias.jornadas.set(columna,new Float(-3));
					} else {
						this.horasAusencias.jornadas.set(columna,new Float(horas));
					}
				}
				
				JornadasMes jm = new JornadasMes(this.horasJornada,this.horasVacaciones, this.horasAusencias, JornadasMes.TOTAL, recursoObject, DetalleRecurso.mes, DetalleRecurso.anio);
				horasTotal = jm;
				HorasJornada hj = new HorasJornada(jm);
				ArrayList<Object> lista = new ArrayList<Object>();
				lista.add(hj);
				ObservableList<Tableable> dataTable = hj.toListTableable(lista);
				tTotal.setItems(dataTable);
				tTotal.refresh();
				
				try {
					tComputoHoras.setText(FormateadorDatos.formateaDato(jm.horasAcumuladas,FormateadorDatos.FORMATO_REAL));
				} catch (Exception e) {
					tComputoHoras.setText(new Double(jm.horasAcumuladas).toString());
				}
							
				tabla.refresh();
			}
			
		} catch (Exception e) {
			
		}		
	}
	
	public void diaSeleccionadoVacaciones(TableView<Tableable> tabla, int columna){
		try {
			if (this.horasJornada.jornadas.get(columna)<0) {
				return;
			} 
			if (this.horasAusencias.jornadas.get(columna)>0) {
				Dialogo.error("Modificación de Ausencias y Vacaciones", "Colisión información", "Ya hay una ausencia programada para ese día");
			} else {
				if (this.horasVacaciones.jornadas.get(columna)==100) {
					this.horasVacaciones.jornadas.set(columna,new Float(-2));
				} else {
					this.horasVacaciones.jornadas.set(columna,new Float(100));
				}
				
				JornadasMes jm = new JornadasMes(this.horasJornada,this.horasVacaciones, this.horasAusencias, JornadasMes.TOTAL, recursoObject, DetalleRecurso.mes, DetalleRecurso.anio);
				horasTotal = jm;
				HorasJornada hj = new HorasJornada(jm);
				ArrayList<Object> lista = new ArrayList<Object>();
				lista.add(hj);
				ObservableList<Tableable> dataTable = hj.toListTableable(lista);
				tTotal.setItems(dataTable);
				tTotal.refresh();
				
				try {
					tComputoHoras.setText(FormateadorDatos.formateaDato(jm.horasAcumuladas,FormateadorDatos.FORMATO_REAL));
				} catch (Exception e) {
					tComputoHoras.setText(new Double(jm.horasAcumuladas).toString());
				}
							
				tabla.refresh();
			}
			
		} catch (Exception e) {
			
		}		
	}
	
	private void informaHorasAusencias(){
		JornadasMes jm = new JornadasMes(JornadasMes.AUSENCIAS, recursoObject, mes, anio, vacasAusencias);
		this.horasAusencias = jm;
		HorasJornada hj = new HorasJornada(jm);
		ArrayList<Object> lista = new ArrayList<Object>();
		lista.add(hj);
		tablaTotal = new Tabla(tAusencias,hj,this);		
		tablaTotal.pintaTabla(lista);
		
		tAusencias.getSelectionModel().setCellSelectionEnabled(true);
		
		tAusencias.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	        	diaSeleccionadoAusencia(tAusencias, tAusencias.getSelectionModel().getSelectedCells().get(0).getColumn());
	        	;
	        }
	    });
		
		ObservableList<TableColumn<Tableable,?>> columnas = tAusencias.getColumns(); 
		Iterator<TableColumn<Tableable,?>> itCol = columnas.iterator();
		
		while (itCol.hasNext()) {
			@SuppressWarnings("unchecked")
			TableColumn<Tableable,String> columna = (TableColumn<Tableable,String>) itCol.next();
			columna.setCellValueFactory(
					cellData ->
						new SimpleStringProperty(cellData.getValue().get(
							cellData.getTableColumn().getId())));
			
			columna.setCellFactory(column -> {
			    return new TableCell<Tableable, String>() {
			        protected void updateItem(String item, boolean empty) {
			        	setText(item);
			        	
			        	if ("".equals(item)){
			        		Calendar c = Calendar.getInstance();
				        	c.set(anio, mes-1, new Integer(columna.getId()));
				        	int diaSemana = c.get(Calendar.DAY_OF_WEEK);
				        	
				        	if (Calendar.SUNDAY == diaSemana || Calendar.SATURDAY==diaSemana) {
				        		setStyle("-fx-background-color: MediumAquaMarine");
				        	} 
			        	} else  {
			        		if ("-3".equals(item)) {
			        			setText("");
			        			setStyle("-fx-background-color: White");
			        		} else {
			        			setStyle("-fx-background-color: LightYellow");
			        		}
			        	}
			        }
			    };
			});
		}
		
		tAusencias.refresh();
    }
	
	

}
