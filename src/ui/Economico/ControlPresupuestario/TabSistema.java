package ui.Economico.ControlPresupuestario;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import controller.AnalizadorPresupuesto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import model.beans.Concepto;
import model.beans.Estimacion;
import model.beans.EstimacionAnio;
import model.beans.EstimacionMes;
import model.beans.Imputacion;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import ui.interfaces.ControladorPantalla;

public class TabSistema implements ControladorPantalla {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/TabSistema.fxml"; 
	
	public AnalizadorPresupuesto ap = null;
	
	@FXML
	private AnchorPane anchor;
	
    @FXML
    private TextField tfPresTotal;

    @FXML
    private Accordion tpConceptos;
    
    @FXML
    private TextField tfPresTotalI;
	
   	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		
	}
	
	@Override
	public void resize(Scene escena) {
		
	}
	
	public void pintaConceptos(AnalizadorPresupuesto ap, int mes, int anio, Sistema s) {
		
		float contadorEstimado = 0;
		float contadorImputado = 0;
		
		HashMap<String, Concepto> acumuladoSistemaConcepto = ap.acumuladoPorSistemaConcepto(s,-1);
		
		Iterator<MetaConcepto> itMc = MetaConcepto.listado.values().iterator();
		
		Iterator<EstimacionAnio> itestAnio = ap.estimacionAnual.iterator();
		EstimacionMes em = null;
		EstimacionAnio eA = null;
				
		while (itestAnio.hasNext()) {
			eA = itestAnio.next();
			if (eA.anio == anio) {
				em = eA.estimacionesMensuales.get(mes);
				break;
			}
		}
		
		HashMap<String,Concepto> costesAnuales =  eA.acumuladoPorSistemaConcepto(s);
		
		while (itMc.hasNext()) {
			MetaConcepto mc = itMc.next();
			
			TitledPane tp = new TitledPane();
			tp.setText(mc.codigo);
			tpConceptos.getPanes().add(tp);
			
			while (itestAnio.hasNext()) {
				eA = itestAnio.next();
				if (eA.anio == anio) {
					em = eA.estimacionesMensuales.get(mes);
					break;
				}
			}
			
			ArrayList<Concepto> arConcepto = em.costesPorRecurso(mc, s);
			ArrayList<Float> estimadoImputado = estimadoImputado(arConcepto);
			tp.setText(generaCabecera(mc,estimadoImputado.get(0),estimadoImputado.get(1)));
			contadorEstimado += estimadoImputado.get(0);
			contadorImputado += estimadoImputado.get(1);
			
			ArrayList<Float> listaResumen = new ArrayList<Float>();
			listaResumen.add(estimadoImputado.get(0));
			listaResumen.add(estimadoImputado.get(1));			
			
			Estimacion estAnio = costesAnuales.get(mc.codigo).listaEstimaciones.get(0);
			Imputacion impAnio = costesAnuales.get(mc.codigo).listaImputaciones.get(0);
			listaResumen.add(estAnio.importe);
			listaResumen.add(impAnio.getImporte());
			
			Concepto cTot =  acumuladoSistemaConcepto.get(mc.codigo);
			Estimacion estTot = cTot.listaEstimaciones.get(0);
			Imputacion impTot = cTot.listaImputaciones.get(0);
			listaResumen.add(estTot.importe);
			listaResumen.add(impTot.getImporte());
				
			try {
				DetalleRecurso detRecursos = new DetalleRecurso();
		        FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(new URL(detRecursos.getFXML()));
		        tp.setContent(loader.load());
		        detRecursos = loader.getController();
		        detRecursos.pintaConcepto(arConcepto,listaResumen);
			} catch (Exception e) {
				
			}
		}
		
		try {
			tfPresTotal.setText(FormateadorDatos.formateaDato(contadorEstimado, FormateadorDatos.FORMATO_MONEDA));
			tfPresTotalI.setText(FormateadorDatos.formateaDato(contadorImputado, FormateadorDatos.FORMATO_MONEDA));
		} catch (Exception e) {
			
		}
	}
	
	public ArrayList<Float> estimadoImputado(ArrayList<Concepto> arConcepto){
		float contadorEstimado = 0;
		float contadorImputado = 0;
		
		Iterator<Concepto> itConcepto = arConcepto.iterator();
		
		while (itConcepto.hasNext()) {
			Concepto c = itConcepto.next();
			
			if (c.listaEstimaciones.size()>0)
				contadorEstimado += c.listaEstimaciones.get(0).importe;

			if (c.listaImputaciones.size()>0)
				contadorImputado += c.listaImputaciones.get(0).getImporte();
		}
		
		ArrayList<Float> salida = new ArrayList<Float>();
		salida.add(contadorEstimado);
		salida.add(contadorImputado);
		
		return salida;
	}
	
	public String generaCabecera(MetaConcepto mc, Float estimado, Float imputado){
		try {
			
			return mc.codigo + "- Estimado ("+ FormateadorDatos.formateaDato(estimado, FormateadorDatos.FORMATO_MONEDA)+ ")" + "/ Imputado ("+ FormateadorDatos.formateaDato(imputado, FormateadorDatos.FORMATO_MONEDA)+ ")";
		
		} catch (Exception e) {
			return mc.codigo;
		}
	}
	
}
