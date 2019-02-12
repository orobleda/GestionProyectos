package ui.Economico.EstimacionesValoraciones;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.beans.BaseCalculoConcepto;
import model.beans.Concepto;
import model.beans.Coste;
import model.beans.Tarifa;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaConcepto;
import ui.ControladorPantalla;
import ui.Tableable;
import ui.Economico.EstimacionesValoraciones.Tables.LineaCostePresupuesto;

public class InformaConcepto implements ControladorPantalla {
	
	public static final String fxml = "file:src/ui/Economico/EstimacionesValoraciones/InformaConcepto.fxml";
	
	public static TableRowDataFeatures<Tableable> expander = null;
	
	@FXML
	private ComboBox<MetaConcepto> cbConcepto = null;
	@FXML
	private ComboBox<MetaConcepto> cbPorcentaje = null;
	@FXML
	private ComboBox<BaseCalculoConcepto> cbBaseCalculo = null;
	@FXML
	private ComboBox<Tarifa> cbTarifa = null;
	@FXML
	private TextField tePorcentaje = null;
	@FXML
	private TextField teCantidad = null;
	@FXML
	private TextField teHoras = null;
	@FXML
	private TextField teCantidadEst = null;
	@FXML
	private ImageView imGuardarConcepto = null;
	@FXML
	private HBox hbHoras = null;
	@FXML
	private HBox hbCantidad = null;
	@FXML
	private HBox hbPorcentaje = null;
	
	@FXML
	private AnchorPane anchor;
	
	public void initialize(){
		hbHoras.setManaged(false);
		hbHoras.managedProperty().bind(hbHoras.visibleProperty());
		hbCantidad.setManaged(false);
		hbCantidad.managedProperty().bind(hbCantidad.visibleProperty());
		hbPorcentaje.setManaged(false);
		hbPorcentaje.managedProperty().bind(hbPorcentaje.visibleProperty());
		
		cbConcepto.getItems().addAll(MetaConcepto.listado.values());
		cbConcepto.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			seleccionaConcepto (newValue);
	    	}
	    );
		
		MetaConcepto mc = new MetaConcepto();
		cbPorcentaje.getItems().addAll(mc.aPorcentaje());
		
		imGuardarConcepto.setMouseTransparent(true);
		imGuardarConcepto.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() { public void handle(MouseEvent event) {	 guardaElemento(); }	});
		teCantidad.focusedProperty().addListener((ov, oldV, newV) -> { if (!newV) { calculaBaseCoste();  }  });
		teHoras.focusedProperty().addListener((ov, oldV, newV) -> {    if (!newV) { calculaBaseHoras();  }  });
		cbTarifa.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { calculaBaseHoras();   	});
		tePorcentaje.focusedProperty().addListener((ov, oldV, newV) -> { if (!newV) { calculaBasePorcentaje(); } });
		cbPorcentaje.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> { calculaBasePorcentaje(); 	}   );
	
	}
	
	public void seleccionaBaseCalculo(BaseCalculoConcepto bcc){
		try {
			if (bcc.id == BaseCalculoConcepto.CALCULO_BASE_COSTE  ) {
				hbHoras.setVisible(false);
				hbCantidad.setVisible(true);
				hbPorcentaje.setVisible(false);
				teCantidad.setText("");
			}
			if (bcc.id == BaseCalculoConcepto.CALCULO_BASE_HORAS  ) {
				hbHoras.setVisible(true);
				hbCantidad.setVisible(false);
				hbPorcentaje.setVisible(false);	
				cbTarifa.getItems().removeAll(cbTarifa.getItems());
				cbTarifa.getItems().addAll(new Tarifa().vigentes());
			}
			if (bcc.id == BaseCalculoConcepto.CALCULO_BASE_PORC  ) {
				hbHoras.setVisible(false);
				hbCantidad.setVisible(false);
				hbPorcentaje.setVisible(true);	
			}
			
			imGuardarConcepto.setMouseTransparent(false);
			imGuardarConcepto.getStyleClass().remove("iconoDisabled");
			imGuardarConcepto.getStyleClass().add("iconoEnabled");
			
			teCantidadEst.setText("");
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void calculaBaseCoste() {
		try {
			LineaCostePresupuesto lcp = (LineaCostePresupuesto) expander.getValue();
			Concepto c = (Concepto) lcp.conceptos.get(cbConcepto.getValue().codigo).clone();
			c.valor = (Float) FormateadorDatos.parseaDato(teCantidad.getText(), FormateadorDatos.FORMATO_MONEDA);
			c.baseCalculo = cbBaseCalculo.getValue();	
			
			c.calculaCantidadEstimada(lcp.conceptos);
			
		    teCantidadEst.setText(FormateadorDatos.formateaDato(c.valorEstimado, FormateadorDatos.FORMATO_MONEDA)); 
		    teCantidad.setText(FormateadorDatos.formateaDato(c.valorEstimado, FormateadorDatos.FORMATO_MONEDA));
            
		} catch (Exception e)  {}
	}
	
	public void calculaBasePorcentaje() {
		try {
			MetaConcepto mcc = cbPorcentaje.getValue();
			
			if (mcc!=null) {
				Concepto c = new Concepto();
				c.respectoPorcentaje = mcc;
				c.porcentaje = (Integer) FormateadorDatos.parseaDato(tePorcentaje.getText(), FormateadorDatos.FORMATO_INT);
				c.baseCalculo = cbBaseCalculo.getValue();
				
				LineaCostePresupuesto lcp = (LineaCostePresupuesto) expander.getValue();
				c.calculaCantidadEstimada(lcp.conceptos);

				teCantidadEst.setText(FormateadorDatos.formateaDato(c.valorEstimado, FormateadorDatos.FORMATO_MONEDA));
			} else {
				String valor = FormateadorDatos.formateaDato("0", FormateadorDatos.FORMATO_MONEDA);
				teCantidadEst.setText(valor);
			}
		} catch (Exception e){
			teCantidadEst.setText("0 €");
		}
	}
	
	public void calculaBaseHoras() {
		try {
			Tarifa tf = cbTarifa.getValue();
			
			if (tf!=null) {
				Concepto c = new Concepto();
				c.tarifa = tf;
				c.horas = (Float) FormateadorDatos.parseaDato(teHoras.getText(), FormateadorDatos.FORMATO_MONEDA);
				c.baseCalculo = cbBaseCalculo.getValue();
				
				LineaCostePresupuesto lcp = (LineaCostePresupuesto) expander.getValue();
				c.calculaCantidadEstimada(lcp.conceptos);

				teCantidadEst.setText(FormateadorDatos.formateaDato(c.valorEstimado, FormateadorDatos.FORMATO_MONEDA));
				teHoras.setText(FormateadorDatos.formateaDato(teHoras.getText(), FormateadorDatos.FORMATO_REAL));
			} else {
				String valor = FormateadorDatos.formateaDato("0", FormateadorDatos.FORMATO_MONEDA);
				teCantidadEst.setText(valor);
			}
		} catch (Exception e){
			
		}
	}
	
	public void seleccionaConcepto(MetaConcepto c){
		try {
			if (cbBaseCalculo.getItems().size()==0) {
				cbBaseCalculo.getItems().addAll(BaseCalculoConcepto.listado());
				cbBaseCalculo.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
					seleccionaBaseCalculo (newValue);
			    	}
			    );
			}
				
				LineaCostePresupuesto lcp = (LineaCostePresupuesto) expander.getValue();
				Concepto conc = lcp.conceptos.get(cbConcepto.getValue().codigo);				
				
				String valor = FormateadorDatos.formateaDato(conc.valorEstimado, FormateadorDatos.FORMATO_MONEDA);
		        teCantidadEst.setText(valor); 
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void guardaElemento() {
		expander.toggleExpanded();
		
		try {
			LineaCostePresupuesto lcp = (LineaCostePresupuesto) expander.getValue();
			
			Concepto c = lcp.conceptos.get(cbConcepto.getValue().codigo);
			c.valorEstimado = (Float) FormateadorDatos.parseaDato(teCantidadEst.getText(),FormateadorDatos.FORMATO_MONEDA);
			c.baseCalculo = cbBaseCalculo.getValue();
			
			if (BaseCalculoConcepto.CALCULO_BASE_COSTE == cbBaseCalculo.getValue().id){
				c.valor = (Float) FormateadorDatos.parseaDato(teCantidad.getText(),FormateadorDatos.FORMATO_MONEDA);
			}
			if (BaseCalculoConcepto.CALCULO_BASE_HORAS == cbBaseCalculo.getValue().id){
				c.horas = (Float) FormateadorDatos.parseaDato(teHoras.getText(),FormateadorDatos.FORMATO_MONEDA);
				c.tarifa = cbTarifa.getValue();
			}
			if (BaseCalculoConcepto.CALCULO_BASE_PORC == cbBaseCalculo.getValue().id){
				c.respectoPorcentaje = cbPorcentaje.getValue();;
				c.porcentaje = (Integer) FormateadorDatos.parseaDato(tePorcentaje.getText(), FormateadorDatos.FORMATO_INT);
			}
			
			Coste coste = new Coste();
			coste.conceptosCoste = lcp.conceptos;
			coste.calculaConceptos(); 
			
			EstimacionesValoraciones ev = (EstimacionesValoraciones ) expander.getTableRow().getTableView().getProperties().get("EstimacionValoracion");
			ev.actualizaResumen();
						
			expander.getTableRow().getTableView().refresh();
		} catch (Exception e){
			
		}
		
	}
	
	
	public InformaConcepto (){
		
	}	
	
	public InformaConcepto (TableRowDataFeatures<Tableable> expander){
		InformaConcepto.expander = expander;
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
