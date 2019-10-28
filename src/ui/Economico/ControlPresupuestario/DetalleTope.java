package ui.Economico.ControlPresupuestario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import model.beans.Concepto;
import model.beans.TopeImputacion;
import model.constantes.FormateadorDatos;
import model.metadatos.Sistema;
import ui.interfaces.ControladorPantalla;

public class DetalleTope implements ControladorPantalla {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/DetalleTope.fxml"; 
	
	public static ModificaTope mt = null;
	
	public static final String ANIO = "ANIO";
	public static final String CONCEPTO = "CONCEPTO";
	public static final String PADRE= "PADRE";
	
	int anio = 0;
	Sistema s = null;
		
	
    @FXML
    public Label lConcepto;

    @FXML
    public CheckBox ckResto;

    @FXML
    public Slider slPorcentaje;

    @FXML
    public TextField tCantidad;

    @FXML
    public TextField tPorc;
	
	@Override
	public AnchorPane getAnchor() {
		return null;
	}
	
	@Override
	public void resize(Scene escena) {
		
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		if (lConcepto!=null) {
			Concepto c = (Concepto) variablesPaso.get(DetalleTope.CONCEPTO);
			lConcepto.setText(variablesPaso.get(DetalleTope.ANIO).toString());
			ModificaTope mt = (ModificaTope) variablesPaso.get(DetalleTope.PADRE);
			
			ArrayList<TopeImputacion> listaTopes = mt.muestraTopes(mt.ti, c);
			Iterator<TopeImputacion> itTopes = listaTopes.iterator();
			
			while(itTopes.hasNext()) {
				TopeImputacion ti = itTopes.next();
				if (ti.anio == (Integer) variablesPaso.get(DetalleTope.ANIO)){
					Concepto total = TopeImputaciones.ap.presupuesto.getCosteConcepto(ti.sistema, ti.mConcepto);
					
					this.ckResto.setSelected(ti.resto);
					
					try {
						if (ti.resto) {
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
						
						this.slPorcentaje.setValue((Float) FormateadorDatos.parseaDato(this.tPorc.getText(), FormateadorDatos.FORMATO_REAL));
						
						this.ckResto.selectedProperty().addListener((ov, oldV, newV) -> { 
							if (newV) {
								this.tCantidad.setDisable(true);
								this.tPorc.setDisable(true);
							}  else {
								this.tCantidad.setDisable(false);
								this.tPorc.setDisable(false);
							}
						});
						
						this.slPorcentaje.valueProperty().addListener((ov, oldV, newV) -> {
							nivela("SLIDER", ti);
				        });
						
						this.tPorc.focusedProperty().addListener((ov, oldV, newV) -> { 
							if (!newV) {
								nivela("PORCENTAJE", ti);
							}	
						});
						
						this.tCantidad.focusedProperty().addListener((ov, oldV, newV) -> { 
							if (!newV) {
								nivela("CANTIDAD", ti);
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
					
				}
			}
			
			if (c.s.codigo.equals(Sistema.getInstanceTotal().codigo)) {
				this.ckResto.setDisable(true);
				this.tCantidad.setDisable(true);
				this.tPorc.setDisable(true);
				this.slPorcentaje.setDisable(true);
			}
		}
		
		
	}
	
	public void nivela(String modificacion, TopeImputacion ti) {
		if ("CANTIDAD".equals(modificacion)) {
			try {
				float cantidad = (Float) FormateadorDatos.parseaDato(this.tCantidad.getText(), FormateadorDatos.FORMATO_MONEDA);
				Concepto totalAux = TopeImputaciones.ap.presupuesto.getCosteConcepto(ti.sistema, ti.mConcepto);
				if (totalAux.valorEstimado==0){
					this.tPorc.setText("0 %");
					this.slPorcentaje.setValue(0);
				}
				else{
					this.tPorc.setText(FormateadorDatos.formateaDato(100*cantidad/totalAux.valorEstimado,FormateadorDatos.FORMATO_PORC));
					this.slPorcentaje.setValue(100*cantidad/totalAux.valorEstimado);
				}
				this.tCantidad.setText(FormateadorDatos.formateaDato(cantidad,FormateadorDatos.FORMATO_MONEDA));
				
			} catch (Exception e) {
				this.tPorc.setText("0 %");
				this.tCantidad.setText("0 €");
				this.slPorcentaje.setValue(0);
			}
		}
		if ("PORCENTAJE".equals(modificacion)) {
			try {
				float porc = (Float) FormateadorDatos.parseaDato(this.tPorc.getText(), FormateadorDatos.FORMATO_MONEDA);
				Concepto totalAux = TopeImputaciones.ap.presupuesto.getCosteConcepto(ti.sistema, ti.mConcepto);
				this.tPorc.setText(FormateadorDatos.formateaDato(porc,FormateadorDatos.FORMATO_PORC));
				this.tCantidad.setText(FormateadorDatos.formateaDato(porc*totalAux.valorEstimado/100,FormateadorDatos.FORMATO_MONEDA));
				this.slPorcentaje.setValue(porc);
			} catch (Exception e) {
				this.tPorc.setText("0 %");
				this.tCantidad.setText("0 €");
				this.slPorcentaje.setValue(0);
			}
		}
		if ("SLIDER".equals(modificacion)) {
			try {
                float porc = new Double(this.slPorcentaje.getValue()).floatValue();
				Concepto totalAux = TopeImputaciones.ap.presupuesto.getCosteConcepto(ti.sistema, ti.mConcepto);
				this.tPorc.setText(FormateadorDatos.formateaDato(porc,FormateadorDatos.FORMATO_PORC));
				this.tCantidad.setText(FormateadorDatos.formateaDato(porc*totalAux.valorEstimado/100,FormateadorDatos.FORMATO_MONEDA));
				this.slPorcentaje.setValue(porc);
			} catch (Exception e) {
				this.tPorc.setText("0 %");
				this.tCantidad.setText("0 €");
				this.slPorcentaje.setValue(0);
			}
		}
	}
	
	public void initialize(){	
	}
		
}
