package ui.Economico.ControlPresupuestario.EdicionEstImp;

import java.util.HashMap;
import java.util.Iterator;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import model.beans.Coste;
import model.beans.FraccionImputacion;
import model.beans.Imputacion;
import model.constantes.FormateadorDatos;
import model.metadatos.Sistema;
import ui.ControladorPantalla;
import ui.Economico.ControlPresupuestario.ControlPresupuestario;
import ui.Economico.ControlPresupuestario.EdicionEstImp.Tables.LineaCosteUsuario;

public class FraccionUnitariaImputacion implements ControladorPantalla {
	
	public static FraccionUnitariaImputacion elementoThis = null;
	
	public FraccionarImputacion fracImpu = null;
	
	public HashMap<String, Object> variablesPaso = null;
	
	public Imputacion imputHija = null;
	public Imputacion imputPadre = null;

    @FXML
    public ComboBox<Sistema> cbSistema;

    @FXML
    public TextField tFraccion;

    @FXML
    private TextField tHoras;

    @FXML
    private TextField tImporte;
    
    public FraccionUnitariaImputacion() {
		super();
		FraccionUnitariaImputacion.elementoThis = this;
	}

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/EdicionEstImp/FraccionUnitariaImputacion.fxml"; 
	
	@Override
	public AnchorPane getAnchor() {
		return null;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		this.tFraccion.focusedProperty().addListener((ov, oldV, newV) -> {   
			if (!newV) { 				
					actualizaHorasImporte();				
			}  
		});
	}
	
	private void actualizaHorasImporte() {
		if (FraccionarImputacion.A_PORCENTAJE.equals(this.fracImpu.tFraccion.getValue())) {
			try {
				float porc = (Float) FormateadorDatos.parseaDato(this.tFraccion.getText(), FormateadorDatos.FORMATO_PORC);
				
				tFraccion.setText(FormateadorDatos.formateaDato(porc,FormateadorDatos.FORMATO_PORC));
				
				float horasPadre =  (Float) FormateadorDatos.parseaDato(this.fracImpu.tHoras.getText(), FormateadorDatos.FORMATO_REAL);
				float importePadre =  (Float) FormateadorDatos.parseaDato(this.fracImpu.tImporte.getText(), FormateadorDatos.FORMATO_MONEDA);
				
				tHoras.setText(FormateadorDatos.formateaDato(horasPadre*porc/100,FormateadorDatos.FORMATO_REAL));
				tImporte.setText(FormateadorDatos.formateaDato(importePadre*porc/100,FormateadorDatos.FORMATO_MONEDA));
			} catch (Exception e) {
				tHoras.setText("0");
				tImporte.setText("0 €");
			}
		}
		if (FraccionarImputacion.A_HORAS.equals(this.fracImpu.tFraccion.getValue())) {
			try {
				float porc = (Float) FormateadorDatos.parseaDato(this.tFraccion.getText(), FormateadorDatos.FORMATO_REAL);
				
				tFraccion.setText(FormateadorDatos.formateaDato(porc,FormateadorDatos.FORMATO_REAL));
				
				float horasPadre =  porc;
				float importePadre =  this.imputPadre.tarifa.costeHora * horasPadre;
				
				tHoras.setText(FormateadorDatos.formateaDato(horasPadre,FormateadorDatos.FORMATO_REAL));
				tImporte.setText(FormateadorDatos.formateaDato(importePadre,FormateadorDatos.FORMATO_MONEDA));
			} catch (Exception e) {
				tHoras.setText("0");
				tImporte.setText("0 €");
			}
		}
		if (FraccionarImputacion.A_IMPORTE.equals(this.fracImpu.tFraccion.getValue())) {
			try {
				float porc = (Float) FormateadorDatos.parseaDato(this.tFraccion.getText(), FormateadorDatos.FORMATO_MONEDA);
				
				tFraccion.setText(FormateadorDatos.formateaDato(porc,FormateadorDatos.FORMATO_MONEDA));
				
				float importePadre =  porc;
				float horasPadre =  porc/this.imputPadre.tarifa.costeHora;				
				
				tHoras.setText(FormateadorDatos.formateaDato(horasPadre,FormateadorDatos.FORMATO_REAL));
				tImporte.setText(FormateadorDatos.formateaDato(importePadre,FormateadorDatos.FORMATO_MONEDA));
			} catch (Exception e) {
				tHoras.setText("0");
				tImporte.setText("0 €");
			}
		}
	}
	
	public void limpiarConcepto(String valor) {
		try {
			if (FraccionarImputacion.A_PORCENTAJE.equals(valor)) {
				tFraccion.setText(FormateadorDatos.formateaDato("0",FormateadorDatos.FORMATO_PORC));
			}
			if (FraccionarImputacion.A_HORAS.equals(valor)) {
				tFraccion.setText(FormateadorDatos.formateaDato("0",FormateadorDatos.FORMATO_REAL));
			}
			if (FraccionarImputacion.A_IMPORTE.equals(valor)) {
				tFraccion.setText(FormateadorDatos.formateaDato("0",FormateadorDatos.FORMATO_MONEDA));
			}
			
			actualizaHorasImporte();
		} catch (Exception e) {
			
		}
	}
	
	public void prefijaValores(Imputacion i, Sistema s) throws Exception {
		cbSistema.getItems().removeAll(cbSistema.getItems());
		cbSistema.getItems().add(s);
		cbSistema.setValue(s);
		
		if (i.tipoImputacion == Imputacion.IMPUTACION_NO_FRACCIONADA) {
			this.imputPadre = i;
			this.imputHija = i;
			tFraccion.setText("");
			tHoras.setText(FormateadorDatos.formateaDato(i.getImporte(),FormateadorDatos.FORMATO_REAL));
			tImporte.setText(FormateadorDatos.formateaDato(i.getImporte(),FormateadorDatos.FORMATO_MONEDA));
		} else {
			Iterator<FraccionImputacion> iFi = null;

			if (i.listaFracciones!=null) {
				this.imputPadre = i;
				this.imputHija = i;
				iFi = i.listaFracciones.iterator();
			} 
			else  {
				this.imputPadre = i.imputacionPadre;
				this.imputHija = i;
				iFi = i.imputacionPadre.listaFracciones.iterator();
			}

			while (iFi.hasNext()) {
				FraccionImputacion fI = iFi.next();
				
				if (fI.sistema.codigo.equals(s.codigo)) {
					if (fI.tipo == FraccionImputacion.POR_PORCENTAJE) tFraccion.setText(FormateadorDatos.formateaDato(fI.porc,FormateadorDatos.FORMATO_PORC));
					if (fI.tipo == FraccionImputacion.POR_HORAS) tFraccion.setText(FormateadorDatos.formateaDato(fI.horas,FormateadorDatos.FORMATO_REAL));
					if (fI.tipo == FraccionImputacion.POR_IMPORTE) tFraccion.setText(FormateadorDatos.formateaDato(fI.importe,FormateadorDatos.FORMATO_MONEDA));					
					
					tHoras.setText(FormateadorDatos.formateaDato(fI.getHoras(),FormateadorDatos.FORMATO_REAL));
					tImporte.setText(FormateadorDatos.formateaDato(fI.getImporte(),FormateadorDatos.FORMATO_MONEDA));
				}
			}
		}
		
		
		
		
	}
}
