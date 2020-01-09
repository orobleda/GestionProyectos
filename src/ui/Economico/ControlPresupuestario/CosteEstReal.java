package ui.Economico.ControlPresupuestario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import model.beans.Concepto;
import model.beans.EstimacionAnio;
import model.beans.EstimacionMes;
import model.beans.TopeImputacion;
import model.constantes.Constantes;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import ui.Tabla;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteEstReal;
import ui.Economico.ControlPresupuestario.Tables.LineaCosteTopeEstReal;
import ui.interfaces.ControladorPantalla;
import ui.interfaces.Tableable;

public class CosteEstReal implements ControladorPantalla {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/CosteEstReal.fxml"; 
	
	@FXML
	private AnchorPane anchor;
	

    @FXML
    private Label lMeses;

    @FXML
    private TableView<Tableable> tTitConcHoras;
    private Tabla tablaTitConcHoras;

    @FXML
    private TableView<Tableable> tTitConcCert;
    private Tabla tablaTitConcCert;

    @FXML
    private TableView<Tableable> tTitTOTAL;
    private Tabla tablaTitTOTAL;
    
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
		
	}
	
	public void initialize(){
	}
	
	public ArrayList<HashMap<String,Concepto>> inicializaAcumulador() {
		ArrayList<HashMap<String,Concepto>> listaSalida = new ArrayList<HashMap<String,Concepto>>();
		
		HashMap<String,Concepto> listaHoras = new HashMap<String,Concepto>();
		HashMap<String,Concepto> listaCertif = new HashMap<String,Concepto>();
		HashMap<String,Concepto> listaTotal = new HashMap<String,Concepto>();
		
		listaSalida.add(listaHoras);
		listaSalida.add(listaCertif);
		listaSalida.add(listaTotal);
		
		Iterator<MetaConcepto> itMC = MetaConcepto.listado.values().iterator();
		
		while (itMC.hasNext()) {
			MetaConcepto mc = itMC.next();
			
			if (mc.tipoGestionEconomica == MetaConcepto.GESTION_HORAS){
				Concepto c = new Concepto();
				c.tipoConcepto = mc;
				listaHoras.put(mc.codigo,c);
			}
				
			if (mc.tipoGestionEconomica == MetaConcepto.GESTION_CERTIFICACION){
				Concepto c = new Concepto();
				c.tipoConcepto = mc;
				listaCertif.put(mc.codigo,c);
			}
		}
		
		Concepto c = new Concepto();
		c.tipoConcepto = MetaConcepto.getTotal();
		listaHoras.put(c.tipoConcepto.codigo, c);
		c = new Concepto();
		c.tipoConcepto = MetaConcepto.getTotal();
		listaCertif.put(c.tipoConcepto.codigo, c);
		c = new Concepto();
		c.tipoConcepto = MetaConcepto.getTotal();
		listaTotal.put(c.tipoConcepto.codigo, c);
		
		return listaSalida;		
	}
	
	public ArrayList<HashMap<String,Concepto>> pintaCoste(int tipoCarga, EstimacionAnio ea, EstimacionMes em, Sistema s) {
		ArrayList<HashMap<String,Concepto>> listaSalida = this.inicializaAcumulador();
		
		ArrayList<Object> lista = new ArrayList<Object>();
		ArrayList<Object> listaCertif = new ArrayList<Object>();
		ArrayList<Object> listaTotal = new ArrayList<Object>();
		
		Iterator<MetaConcepto> itMC = MetaConcepto.listado.values().iterator();
		HashMap<String, Concepto> costes = null;
		
		if (tipoCarga == VistaPPM.VISTA_ANUAL) {
			lMeses.setText(new Integer(ea.anio).toString());
			costes = ea.totalPorConcepto(s);
		}
		
		if (tipoCarga == VistaPPM.VISTA_MENSUAL) {
			lMeses.setText(Constantes.nomMes(em.mes-1) + "/" + new Integer(ea.anio).toString());
			costes = em.totalPorConcepto(s);
		}
		
		HashMap<String,Concepto> listaAcum = new HashMap<String,Concepto>();
		listaAcum.put("H",new Concepto());
		listaAcum.put("C",new Concepto());
		
		while (itMC.hasNext()) {
			MetaConcepto mc = itMC.next();
			
			Concepto cA = costes.get(mc.codigo);
			HashMap<String,Float> conceptos = new HashMap<String,Float>();
			conceptos.put("Estimado", cA.valorEstimado);
			conceptos.put("Real", cA.valor);
			if (tipoCarga == VistaPPM.VISTA_ANUAL) {
				TopeImputacion tpAux = new TopeImputacion();
				TopeImputacion tpAux2 = null;
				Concepto cAux2 = new Concepto();
				cAux2.tipoConcepto = mc;
				tpAux.topes = tpAux.listadoTopes(ControlPresupuestario.ap.proyecto);
								
				float valor = 0;
				if (s==null) {
					Iterator<Sistema> itSistemas = ea.topesAnuales.values().iterator();
					while (itSistemas.hasNext()) {
						Sistema sAux = itSistemas.next();
						try {
							tpAux2 = tpAux.dameTope(sAux, cAux2, ea.anio);
							if (tpAux2.cantidad != -1) 
							 valor += tpAux2.porcentaje*ControlPresupuestario.ap.presupuesto.costes.get(sAux.id).conceptosCoste.get(mc.codigo).valorEstimado/100;
						} catch (Exception e) {
							valor += 0;
						}
					}
					if (valor==0) valor = -9999;
				} else {
					tpAux2 = tpAux.dameTope(s, cAux2, ea.anio);
					try {
						if (tpAux2.cantidad == -1) valor = -9999;
						else valor = tpAux2.porcentaje*ControlPresupuestario.ap.presupuesto.costes.get(s.id).conceptosCoste.get(mc.codigo).valorEstimado/100;
					} catch (Exception e) {
						valor = 0;
					}
				}
								
				conceptos.put("Tope Estimado", valor);
			}
			
			if (mc.tipoGestionEconomica == MetaConcepto.GESTION_HORAS){
				lista.add(conceptos);
				Concepto cAux = listaAcum.get("H");
				cAux.valor += cA.valor;
				cAux.valorEstimado += cA.valorEstimado;
				Concepto cAcumCon = listaSalida.get(0).get(mc.codigo);
				Concepto cAcumTot = listaSalida.get(0).get(MetaConcepto.COD_TOTAL);
				Concepto cAcumTotGen = listaSalida.get(2).get(MetaConcepto.COD_TOTAL);
				cAcumCon.valor += cA.valor;
				cAcumCon.valorEstimado += cA.valorEstimado;
				cAcumTot.valor += cA.valor;
				cAcumTot.valorEstimado += cA.valorEstimado;
				cAcumTotGen.valor += cA.valor;
				cAcumTotGen.valorEstimado += cA.valorEstimado;
			}
				
			if (mc.tipoGestionEconomica == MetaConcepto.GESTION_CERTIFICACION){
				listaCertif.add(conceptos);
				Concepto cAux = listaAcum.get("C");
				cAux.valor += cA.valor;
				cAux.valorEstimado += cA.valorEstimado;
				Concepto cAcumCon = listaSalida.get(1).get(mc.codigo);
				Concepto cAcumTot = listaSalida.get(1).get(MetaConcepto.COD_TOTAL);
				Concepto cAcumTotGen = listaSalida.get(2).get(MetaConcepto.COD_TOTAL);
				cAcumCon.valor += cA.valor;
				cAcumCon.valorEstimado += cA.valorEstimado;
				cAcumTot.valor += cA.valor;
				cAcumTot.valorEstimado += cA.valorEstimado;
				cAcumTotGen.valor += cA.valor;
				cAcumTotGen.valorEstimado += cA.valorEstimado;
			}
		}
		
		Concepto cH = listaAcum.get("H");
		Concepto cC = listaAcum.get("C");
		
		HashMap<String,Float> conceptos = new HashMap<String,Float>();
		conceptos.put("Estimado", cH.valorEstimado);
		conceptos.put("Real", cH.valor);
		
		lista.add(conceptos);

		conceptos = new HashMap<String,Float>();
		conceptos.put("Estimado", cC.valorEstimado);
		conceptos.put("Real", cC.valor);
		
		listaCertif.add(conceptos);
		
		conceptos = new HashMap<String,Float>();
		conceptos.put("Estimado", cC.valorEstimado + cH.valorEstimado);
		conceptos.put("Real", cC.valor+cH.valor);
		
		listaTotal.add(conceptos);
		
		ObservableList<Tableable> dataTable = null;
		
		if (tipoCarga == VistaPPM.VISTA_ANUAL) {
			tablaTitConcHoras = new Tabla(tTitConcHoras,new LineaCosteTopeEstReal(),this);
			tablaTitConcHoras.pintaTabla(lista);
			tablaTitConcHoras.fijaAlto(lista.size()==1?40+40*lista.size():40*lista.size());
			
			tablaTitConcCert = new Tabla(tTitConcCert,new LineaCosteTopeEstReal(),this);
			tablaTitConcCert.pintaTabla(listaCertif);
			tablaTitConcCert.fijaAlto(170);
			
			tablaTitTOTAL = new Tabla(tTitTOTAL,new LineaCosteTopeEstReal(),this);
			tablaTitTOTAL.pintaTabla(listaTotal);
			tablaTitTOTAL.fijaAlto(listaTotal.size()==1?40+40*listaTotal.size():40*listaTotal.size());
			
			
			
		} else {
			tablaTitConcHoras = new Tabla(tTitConcHoras,new LineaCosteEstReal(),this);
			tablaTitConcHoras.pintaTabla(lista);
			tablaTitConcHoras.fijaAlto(lista.size()==1?40+40*lista.size():40*lista.size());
			
			tablaTitConcCert = new Tabla(tTitConcCert,new LineaCosteEstReal(),this);
			tablaTitConcCert.pintaTabla(listaCertif);
			tablaTitConcCert.fijaAlto(170);
			
			tablaTitTOTAL = new Tabla(tTitTOTAL,new LineaCosteEstReal(),this);
			tablaTitTOTAL.pintaTabla(listaTotal);
			tablaTitTOTAL.fijaAlto(listaTotal.size()==1?40+40*listaTotal.size():40*listaTotal.size());
			
			/*
			dataTable = (new LineaCosteEstReal()).toListTableable(lista);
			tTitConcHoras.setItems(dataTable);
			(new LineaCosteEstReal()).fijaColumnas(tTitConcHoras);
			
			tTitConcHoras.setPrefSize(15*10, lista.size()==1?40+40*lista.size():40*lista.size());
			
			dataTable =  (new LineaCosteEstReal()).toListTableable(listaCertif);
			tTitConcCert.setItems(dataTable);
			(new LineaCosteEstReal()).fijaColumnas(tTitConcCert);
			
			tTitConcCert.setPrefSize(15*10, listaCertif.size()==1?40+40*listaCertif.size():40*listaCertif.size());
			
			dataTable = (new LineaCosteEstReal()).toListTableable(listaTotal);
			tTitTOTAL.setItems(dataTable);
			(new LineaCosteEstReal()).fijaColumnas(tTitTOTAL);
			
			tTitTOTAL.setPrefSize(15*10, listaTotal.size()==1?40+40*listaTotal.size():40*listaTotal.size());*/
		}		
		
		
		return listaSalida;
	}
	
	public void pintaCoste(EstimacionMes em) {
		
	}
	
	

}
