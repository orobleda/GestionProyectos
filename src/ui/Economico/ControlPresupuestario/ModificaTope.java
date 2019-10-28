package ui.Economico.ControlPresupuestario;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.beans.Concepto;
import model.beans.EstimacionAnio;
import model.beans.TopeImputacion;
import model.constantes.FormateadorDatos;
import model.metadatos.Sistema;
import model.metadatos.TipoDato;
import ui.Dialogo;
import ui.GestionBotones;
import ui.ParamTable;
import ui.Economico.ControlPresupuestario.Tables.LineaTopePresupuesto;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;
import ui.popUps.PopUp;

public class ModificaTope implements ControladorPantalla, PopUp {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/ModificaTope.fxml"; 
	
	public static TableRowDataFeatures<Tableable> expander = null;
	public static TopeImputaciones ti = null;

	public static Object claseRetorno = null;
	public static String metodoRetorno = null;
	
	public boolean esPopUp = false;
	
    @FXML
    private Label lSistema;

    @FXML
    private VBox vbDetallesTopes;

    @FXML
    private ImageView imGuardar;
    private GestionBotones gbGuardar;
    
    public ArrayList<DetalleTope> listaDetalles = null;
    public Concepto c = null;
	
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
	
	public ModificaTope (Object claseRetorno, String metodoRetorno){
		ModificaTope.claseRetorno = claseRetorno;
		ModificaTope.metodoRetorno = metodoRetorno;
	}
	
	public ModificaTope (){
	}
	
	public void initialize(){		
			gbGuardar = new GestionBotones(imGuardar, "Guardar3", false, new EventHandler<MouseEvent>() {        
				@Override
	            public void handle(MouseEvent t)
	            {   
					try {
						insertaTope();
						TopeImputaciones.thisTope.recargaTopes();
						ParamTable.po.hide();
					} catch (Exception e) {
						Dialogo.error("Fallo al guardar", "Fallo al guardar", "Se produjo un error al guardar los elementos.");
						e.printStackTrace();						
					}
	            } }, "Guardar", this);
			gbGuardar.activarBoton();
	}

	@Override
	public String getControlFXML() {
		return this.getFXML();
	}

	@Override
	public void setParametrosPaso(HashMap<String, Object> variablesPaso) {
		if (lSistema!=null){
			listaDetalles = new ArrayList<DetalleTope> ();
			LineaTopePresupuesto ltp = 	(LineaTopePresupuesto) variablesPaso.get("filaDatos");	
			ti = (TopeImputaciones) variablesPaso.get("controladorPantalla");
			c = ltp.concepto;
			
			
			ArrayList<TopeImputacion> listaTopes = muestraTopes(ti, ltp.concepto);
			
			lSistema.setText(ltp.concepto.s.toString() + " - " + ltp.concepto.tipoConcepto.descripcion);
			
			Iterator<EstimacionAnio> itEa = ltp.concepto.topeEstimacion.iterator();
			while (itEa.hasNext()) {
				EstimacionAnio ea = itEa.next();
				
				VBox vb = new VBox();
				
				HashMap<String, Object> parametrosPaso = new HashMap<String, Object>();
				parametrosPaso.put(DetalleTope.ANIO, ea.anio);							        	
    			parametrosPaso.put(DetalleTope.CONCEPTO, ea.concepto);
    			parametrosPaso.put(DetalleTope.PADRE, this);
    			
    			try {
					FXMLLoader loader = new FXMLLoader();
		        	loader.setLocation(new URL(new DetalleTope().getFXML()));
					vb.getChildren().add(loader.load());
					DetalleTope controlPantalla = (DetalleTope) loader.getController();
	    	        controlPantalla.setParametrosPaso(parametrosPaso);
	
	    	        listaDetalles.add(controlPantalla);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    			
    	        vbDetallesTopes.getChildren().add(vb);
			}
			
			if (ltp.concepto.s.codigo.equals(Sistema.getInstanceTotal().codigo)) {
				this.gbGuardar.desActivarBoton();
			}
		}		
	}
	
	public void insertaTope() throws Exception {	
		Iterator<DetalleTope> itDetalleTope = this.listaDetalles.iterator();
		
		while (itDetalleTope.hasNext()) {
			DetalleTope dt = itDetalleTope.next();
			
			boolean aResto = false;
			float porcentaje = 0;
			
			if (dt.ckResto.isSelected()) {
				aResto = true;
			} else {
				aResto = false;
				try { porcentaje = (Float) FormateadorDatos.parseaDato(dt.tPorc.getText(), FormateadorDatos.FORMATO_PORC); } catch (Exception e) { porcentaje = 0;}
			}
					
			String codConcepto = this.c.tipoConcepto.codigo;
			String codSistema = this.c.s.codigo;
			int anio = (Integer) FormateadorDatos.parseaDato(dt.lConcepto.getText(),TipoDato.FORMATO_INT);
			
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
				tAux.mConcepto = this.c.tipoConcepto;
				tAux.porcentaje = porcentaje;
				tAux.proyecto = TopeImputaciones.ap.proyecto;
				tAux.resto = aResto;
				tAux.sistema = this.c.s;
				
				TopeImputaciones.listadoTopes.add(tAux);
			}	
			
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
	
	public ArrayList<TopeImputacion> muestraTopes(TopeImputaciones tpImp, Concepto c) {
		
		TopeImputacion tp = new TopeImputacion();
		
		ArrayList<TopeImputacion> listaTopes = null;
		
		if (tpImp.listadoTopes==null || tpImp.listadoTopes.size()==0) {
			listaTopes = tp.listadoTopes(tpImp.p);
		} else {
			tp.topes = TopeImputaciones.listadoTopes;
		}
				
		listaTopes = tp.dameTopes(c.s, c);
		
		Iterator<EstimacionAnio> itEA = TopeImputaciones.ap.estimacionAnual.iterator();
		
		while (itEA.hasNext()) {
			EstimacionAnio eA = itEA.next();
			
			Iterator<TopeImputacion> itTp = listaTopes.iterator();
			
			boolean encontrado = false;
			
			while (itTp.hasNext()) {
				TopeImputacion ti = itTp.next();
				
				if (ti.anio == eA.anio) {
					encontrado = true;
					break;
				}
			}
			
			if (!encontrado) {
				TopeImputacion ti = new TopeImputacion();
				ti.anio = eA.anio;
				ti.cantidad = 0;
				ti.resto = true;
				ti.id =  -1;
				ti.mConcepto =  c.tipoConcepto;
				ti.porcentaje = 0;
				ti.proyecto = TopeImputaciones.ap.proyecto;
				ti.sistema = c.s;
				listaTopes.add(ti);
			}
		}
		
		return listaTopes;
	}

}
