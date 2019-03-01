package ui.Economico.ControlPresupuestario;

import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.beans.Concepto;
import model.beans.EstimacionAnio;
import model.beans.Proyecto;
import model.beans.RelRecursoTarifa;
import model.beans.Tarifa;
import model.beans.TopeImputacion;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Economico.ControlPresupuestario.Tables.LineaTopeImputacion;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;
import ui.popUps.PopUp;

public class ModificaTope implements ControladorPantalla, PopUp {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/ModificaTope.fxml"; 
	
	public static TableRowDataFeatures<Tableable> expander = null;
	public static TopeImputacion ti = null;

	public static Object claseRetorno = null;
	public static String metodoRetorno = null;
	
	public boolean esPopUp = false;
	
	@FXML
	private AnchorPane anchor;
	
	@FXML
    private TextField tFxInicio;

    @FXML
    private TextField tFxFin;

    @FXML
    private ComboBox<Integer> cbAnio;

    @FXML
    private CheckBox ckResto;

    @FXML
    private TextField tCantidad;

    @FXML
    private TextField tPorc;

    @FXML
    private ImageView imGuardarTope;
    private GestionBotones gbGuardarTope;
	
	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){		
		try {
			
			gbGuardarTope = new GestionBotones(imGuardarTope, "Guardar3", false, new EventHandler<MouseEvent>() {        
				@Override
	            public void handle(MouseEvent t)
	            {   
					insertaTope();
					TopeImputaciones.thisTope.recargaTopes();
					ParamTable.po.hide();
	            } }, "Guardar", this);
			gbGuardarTope.activarBoton();
			
			Concepto c = getListaConceptos(TopeImputaciones.thisTope.cbComboSistemas.getValue(), TopeImputaciones.ap.proyecto,ti.anio, ti.mConcepto);
			
			Concepto total = TopeImputaciones.ap.presupuesto.getCosteConcepto(ti.sistema, ti.mConcepto);
			
			this.ckResto.setSelected(ModificaTope.ti.resto);
			
			if (ModificaTope.ti.resto) {
				this.tCantidad.setDisable(true);
				this.tPorc.setDisable(true);
				this.tCantidad.setText(FormateadorDatos.formateaDato(c.valorEstimado,FormateadorDatos.FORMATO_MONEDA));
				if (total==null) this.tPorc.setText(FormateadorDatos.formateaDato(100*c.valorEstimado/1,FormateadorDatos.FORMATO_PORC));
				else this.tPorc.setText(FormateadorDatos.formateaDato(100*c.valorEstimado/total.valorEstimado,FormateadorDatos.FORMATO_PORC));
			} else {
				if (ti.cantidad!=0) {
					this.tCantidad.setText(FormateadorDatos.formateaDato(ti.cantidad,FormateadorDatos.FORMATO_MONEDA));
					if (total==null) this.tPorc.setText(FormateadorDatos.formateaDato(0,FormateadorDatos.FORMATO_PORC));
					else this.tPorc.setText(FormateadorDatos.formateaDato(100*ti.cantidad/total.valorEstimado,FormateadorDatos.FORMATO_PORC));
				} else {
					if (total==null)  this.tCantidad.setText(FormateadorDatos.formateaDato(0,FormateadorDatos.FORMATO_MONEDA));
					else this.tCantidad.setText(FormateadorDatos.formateaDato(ti.porcentaje*total.valorEstimado/100,FormateadorDatos.FORMATO_MONEDA));
					this.tPorc.setText(FormateadorDatos.formateaDato(ti.porcentaje,FormateadorDatos.FORMATO_PORC));
				}
			}
			
			this.ckResto.selectedProperty().addListener((ov, oldV, newV) -> { 
				if (newV) {
					this.tCantidad.setDisable(true);
					this.tPorc.setDisable(true);
				}  else {
					this.tCantidad.setDisable(false);
					this.tPorc.setDisable(false);
				}
			});
			
			this.tPorc.focusedProperty().addListener((ov, oldV, newV) -> { 
				if (!newV) {
					try {
						float porc = (Float) FormateadorDatos.parseaDato(this.tPorc.getText(), FormateadorDatos.FORMATO_MONEDA);
						Concepto totalAux = TopeImputaciones.ap.presupuesto.getCosteConcepto(ti.sistema, ti.mConcepto);
						this.tPorc.setText(FormateadorDatos.formateaDato(porc,FormateadorDatos.FORMATO_PORC));
						this.tCantidad.setText(FormateadorDatos.formateaDato(porc*totalAux.valorEstimado/100,FormateadorDatos.FORMATO_MONEDA));
					} catch (Exception e) {
						this.tPorc.setText("0 %");
						this.tCantidad.setText("0 €");
					}
				}	
			});
			
			this.tCantidad.focusedProperty().addListener((ov, oldV, newV) -> { 
				if (!newV) {
					try {
						float cantidad = (Float) FormateadorDatos.parseaDato(this.tCantidad.getText(), FormateadorDatos.FORMATO_MONEDA);
						Concepto totalAux = TopeImputaciones.ap.presupuesto.getCosteConcepto(ti.sistema, ti.mConcepto);
						this.tPorc.setText(FormateadorDatos.formateaDato(100*cantidad/totalAux.valorEstimado,FormateadorDatos.FORMATO_PORC));
						this.tCantidad.setText(FormateadorDatos.formateaDato(cantidad,FormateadorDatos.FORMATO_MONEDA));
					} catch (Exception e) {
						this.tPorc.setText("0 %");
						this.tCantidad.setText("0 €");
					}
				}	
			}); 
		} catch (Exception e) {
			e.printStackTrace();
			try {
				this.tCantidad.setText(FormateadorDatos.formateaDato(0,FormateadorDatos.FORMATO_MONEDA));
				this.tPorc.setText(FormateadorDatos.formateaDato(0,FormateadorDatos.FORMATO_PORC));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		this.cbAnio.setDisable(true);
	}
	
	public ModificaTope (TableRowDataFeatures<Tableable> expander){
		ModificaTope.expander = expander;
	}
	
	public ModificaTope (Object claseRetorno, String metodoRetorno){
		ModificaTope.claseRetorno = claseRetorno;
		ModificaTope.metodoRetorno = metodoRetorno;
	}
	
	public ModificaTope (){
	}

	@Override
	public String getControlFXML() {
		return this.getFXML();
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		LineaTopeImputacion lti = (LineaTopeImputacion) variablesPaso.get("filaDatos");
		ModificaTope.ti = lti.ti;			
	}
	
	public Concepto getListaConceptos(Sistema s, Proyecto p, int anio, MetaConcepto mc) {
		Concepto c = null;
				
		Iterator<EstimacionAnio> itAnios = TopeImputaciones.ap.estimacionAnual.iterator();
		
		while (itAnios.hasNext()) {
			EstimacionAnio ea = itAnios.next();
			
			Integer aAux = new Integer(ea.anio);
			this.cbAnio.getItems().add(aAux);
			
			if (anio == aAux.intValue()) {
				this.cbAnio.setValue(aAux);
				
				HashMap<String, Concepto> listaConceptos = ea.totalPorConcepto(s);
				
				c = listaConceptos.get(mc.codigo);				
			}
		}
		
		return 	c;	
	}
	
	public void insertaTope() {		
		boolean aResto = false;
		float porcentaje = 0;
		
		if (this.ckResto.isSelected()) {
			aResto = true;
		} else {
			aResto = false;
			try { porcentaje = (Float) FormateadorDatos.parseaDato(this.tPorc.getText(), FormateadorDatos.FORMATO_PORC); } catch (Exception e) { porcentaje = 0;}
		}
				
		String codConcepto = ModificaTope.ti.mConcepto.codigo;
		String codSistema = TopeImputaciones.thisTope.cbComboSistemas.getValue().codigo;
		int anio = this.cbAnio.getValue();
		
		boolean encontrado = false;
		
		Iterator<TopeImputacion> itTopes = TopeImputaciones.listadoTopes.iterator();
		
		while (itTopes.hasNext()) {
			TopeImputacion tAux = itTopes.next();
		
			if (tAux.anio == anio && tAux.mConcepto.codigo.equals(codConcepto) && tAux.sistema.codigo.equals(codSistema)) {
				tAux.porcentaje = porcentaje;
				tAux.resto = aResto;
				encontrado = true;
				break;
			}			
		}
		
		if (!encontrado) {
			TopeImputacion tAux = new TopeImputacion();
			tAux.anio = anio;
			tAux.cantidad = 0;
			tAux.mConcepto = ModificaTope.ti.mConcepto;
			tAux.porcentaje = porcentaje;
			tAux.proyecto = TopeImputaciones.ap.proyecto;
			tAux.resto = aResto;
			tAux.sistema = TopeImputaciones.thisTope.cbComboSistemas.getValue();
			
			TopeImputaciones.listadoTopes.add(tAux);
		}		
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
