package ui.Economico.ControlPresupuestario;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import controller.AnalizadorPresupuesto;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import model.beans.EstimacionAnio;
import model.beans.EstimacionMes;
import model.constantes.Constantes;
import model.metadatos.Sistema;
import ui.ControladorPantalla;
import ui.GestionBotones;

public class VistaJerarquizada implements ControladorPantalla {

	public static final String fxml = "file:src/ui/Economico/ControlPresupuestario/VistaJerarquizada.fxml"; 
	
	public AnalizadorPresupuesto ap = null;
	
	@FXML
	private AnchorPane anchor;
	

    @FXML
    private ImageView iMAntPag;

    private GestionBotones gbAntPag;

    @FXML
    public ComboBox<String> cbMeses;

    @FXML
    private ImageView iMSigPag;

    private GestionBotones gbSigPag;

    @FXML
    private TabPane tbPane;
    
    @FXML
    private Pane pnPanelListado;
	
    	@Override
	public AnchorPane getAnchor() {
		return anchor;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void siguienteElemento() {
		String elemento = cbMeses.getValue();
		Calendar c = procesaValor(elemento);
		c.add(Calendar.MONTH, 1);
		if (cbMeses.getItems().contains(literalValor(c))){
			cbMeses.setValue(literalValor(c));
			ControlPresupuestario.mesActual = literalValor(c);
		}
	}
	
	public void anteriorElemento() {
		String elemento = cbMeses.getValue();
		Calendar c = procesaValor(elemento);
		c.add(Calendar.MONTH, -1);
		if (cbMeses.getItems().contains(literalValor(c))){
			cbMeses.setValue(literalValor(c));
			ControlPresupuestario.mesActual = literalValor(c);
		}
	}
	
	public void initialize(){
		gbAntPag = new GestionBotones(iMAntPag, "antPagina", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				anteriorElemento();
            }
        },"Mes anterior");
		gbSigPag = new GestionBotones(iMSigPag, "sigPagina", false, new EventHandler<MouseEvent>() {        
			@Override
            public void handle(MouseEvent t)
            {
				siguienteElemento();
            }
        },"Mes siguiente");

	}
	
	public void pintaPresupuesto(AnalizadorPresupuesto ap, boolean cambioPestania) {
		try {
			pnPanelListado.setVisible(false);
			tbPane.setVisible(true);
			
			if (!cambioPestania) {
				this.ap = ap;
				informaCombo(ap);
				cbMeses.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
					ControlPresupuestario.mesActual = cbMeses.getValue();
					pintaPresupuesto(this.ap, true);
			    	}
			    ); 
			} else {
				tbPane.getTabs().removeAll(tbPane.getTabs());
			}
			
			if (ap.estimacionAnual.size()>0) {
				EstimacionAnio ea = ap.estimacionAnual.get(0);
				
				if (ea.estimacionesMensuales.size()>0) {
					EstimacionMes em = (EstimacionMes) ea.estimacionesMensuales.values().toArray()[0];
					
					Iterator<Sistema> itSistema = em.estimacionesPorSistemas.values().iterator();
					while (itSistema.hasNext()) {
						Sistema s = itSistema.next();
						Tab tab = new Tab();
						tab.setText(s.codigo);
				        tbPane.getTabs().add(tab);
				        
				        TabSistema tbSistema = new TabSistema();
				        FXMLLoader loader = new FXMLLoader();
				        loader.setLocation(new URL(tbSistema.getFXML()));
				        tab.setContent(loader.load());
				        tbSistema = loader.getController();
				        
				        Calendar c = procesaValor(cbMeses.getValue());
				        
				        tbSistema.pintaConceptos(ap,c.get(Calendar.MONTH), c.get(Calendar.YEAR),s);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getTabSelected() {
		SingleSelectionModel<Tab> selectionModel = tbPane.getSelectionModel();
		return selectionModel.getSelectedIndex();
	}
	
	public void selectTab(int index) {
		SingleSelectionModel<Tab> selectionModel = tbPane.getSelectionModel();
		selectionModel.select(index); 
	}
	
	public void pintaPresupuestoGlobal(AnalizadorPresupuesto ap, boolean cambioPestania) {
		try {
			pnPanelListado.setVisible(true);
			tbPane.setVisible(false);
			
			if (!cambioPestania) {
				this.ap = ap;
				informaCombo(ap);
				cbMeses.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
					ControlPresupuestario.mesActual = cbMeses.getValue();
					pintaPresupuestoGlobal(this.ap, true);
			    	}
			    ); 
			} else {
				pnPanelListado.getChildren().remove(pnPanelListado.getChildren());
			}
			
	        TabSistema tbSistema = new TabSistema();
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(new URL(tbSistema.getFXML()));
	        pnPanelListado.getChildren().add(loader.load());
        
	        tbSistema = loader.getController();
	        
	        Calendar c = procesaValor(cbMeses.getValue());
	        
	        tbSistema.pintaConceptos(ap,c.get(Calendar.MONTH), c.get(Calendar.YEAR),null);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String literalValor(Calendar c) {
		return Constantes.nomMes(c.get(Calendar.MONTH)) + "'" + c.get(Calendar.YEAR);
	}
	
	public Calendar procesaValor(String seleccionado) {
		String [] seleccion = seleccionado.split("'");
		
		int mes = Constantes.numMes(seleccion[0]);
		int anio = new Integer(seleccion[1]);
		
		Calendar c = Calendar.getInstance();
		c.set(anio, mes-1, 1);
		
		return c;
	}
	
	public void informaCombo(AnalizadorPresupuesto ap) {
		Iterator<EstimacionAnio> itea = ap.estimacionAnual.iterator();
		
		while (itea.hasNext()) {
			EstimacionAnio ea = itea.next();
			
			for (int i=0;i<12;i++) {
				try {
					EstimacionMes em = ea.estimacionesMensuales.get(i);
					
					if (em!=null) {
						String valor = Constantes.nomMes(em.mes-1) + "'" + ea.anio;
						this.cbMeses.getItems().add(valor);
					}
				} catch (Exception e) {
					
				}
			}
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		
		int valorI = 0;
		int valorIa = 0;
		
		if (c.get(Calendar.MONTH) == 11) {
			valorI = 0;
			valorIa = c.get(Calendar.YEAR)+1;
		} else {
			valorI = c.get(Calendar.MONTH)+1;
			valorIa = c.get(Calendar.YEAR);
		}
		
		String valor = Constantes.nomMes(valorI) + "'" + valorIa;

		if (cbMeses.getItems().contains(valor)){
			cbMeses.setValue(valor);
			ControlPresupuestario.mesActual = valor;
		}
		else {
			cbMeses.setValue(cbMeses.getItems().get(cbMeses.getItems().size()-1));
			ControlPresupuestario.mesActual = cbMeses.getItems().get(cbMeses.getItems().size()-1);
		}		
	}
	
}
