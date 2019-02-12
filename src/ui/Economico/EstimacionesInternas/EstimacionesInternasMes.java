package ui.Economico.EstimacionesInternas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import model.constantes.Constantes;
import ui.ControladorPantalla;
import ui.Tabla;
import ui.Tableable;
import ui.Economico.EstimacionesInternas.tables.LineaDetalleMes;
import ui.Economico.EstimacionesInternas.tables.LineaDetalleUsuario;

public class EstimacionesInternasMes implements ControladorPantalla {

	public static final String fxml = "file:src/ui/Economico/EstimacionesInternas/EstimacionesInternasMes.fxml"; 
	
	public EstimacionesInternas eI = null;
	public int mesRepresentado = 0;

    @FXML
    private TableView<Tableable> tMes;
    public  Tabla tTablaMes;

    @FXML
    private Label lMes;
	
    @Override
	public AnchorPane getAnchor() {
		return null;
	}

	@Override
	public String getFXML() {
		return fxml;
	}
	
	public void initialize(){
		
	}
	
	public void adscribir(EstimacionesInternas eI, int mesRepresentado) {
		this.eI = eI;
		this.mesRepresentado = mesRepresentado;
		
		tTablaMes = new Tabla(tMes,new LineaDetalleMes(), eI.tsColapsar, this);
	}
	
	public void pintaTabla() {
		Iterator<Integer> itRecursos = eI.listadatos.keySet().iterator();
		ArrayList<LineaDetalleMes> lista = new ArrayList<LineaDetalleMes>();
				
		while (itRecursos.hasNext()) {
			Integer key = itRecursos.next();
			HashMap<Integer,HashMap<String,HashMap<String,HashMap<String,Float>>>> recurso = eI.listadatos.get(key);
			HashMap<String,HashMap<String,HashMap<String,Float>>> mesCurso = recurso.get(mesRepresentado+1);
			
			LineaDetalleMes ldm = new LineaDetalleMes();
			ldm.concepto = LineaDetalleUsuario.CONCEPTO_HORAS_TOTAL;
			HashMap<String,Float> concepto = mesCurso.get(LineaDetalleUsuario.CONCEPTO_HORAS_TOTAL).get(EstimacionesInternas.SISTEMA);
			ldm.horas = concepto.get(EstimacionesInternas.HORAS);
			ldm.importe = concepto.get(EstimacionesInternas.IMPORTE);
			lista.add(ldm);
			
			ldm = new LineaDetalleMes();
			ldm.concepto = LineaDetalleUsuario.CONCEPTO_HORAS_AUSENCIAS;
			concepto = mesCurso.get(LineaDetalleUsuario.CONCEPTO_HORAS_AUSENCIAS).get(EstimacionesInternas.SISTEMA);;
			ldm.horas = concepto.get(EstimacionesInternas.HORAS);
			ldm.importe = concepto.get(EstimacionesInternas.IMPORTE);
			lista.add(ldm);
			
			ldm = new LineaDetalleMes();
			ldm.concepto = LineaDetalleUsuario.CONCEPTO_HORAS_BASE;
			concepto = mesCurso.get(LineaDetalleUsuario.CONCEPTO_HORAS_BASE).get(EstimacionesInternas.SISTEMA);;
			ldm.horas = concepto.get(EstimacionesInternas.HORAS);
			ldm.importe = concepto.get(EstimacionesInternas.IMPORTE);
			lista.add(ldm);
			
			Iterator<String> keysProyectos = mesCurso.keySet().iterator();
			
			while (keysProyectos.hasNext()) {
				String keyProyecto = keysProyectos.next();
				
				if (!eI.conceptosEstaticos.containsKey(keyProyecto)) {
					HashMap<String,HashMap<String,Float>> sistema = mesCurso.get(keyProyecto);
					
					Iterator<String> itKeySistemas = sistema.keySet().iterator();
					
					while(itKeySistemas.hasNext()) {
						String keySistema = itKeySistemas.next();
						
						concepto = sistema.get(keySistema);
						ldm = new LineaDetalleMes();
						ldm.concepto = keyProyecto;
						ldm.sistema = keySistema;
						ldm.horas = concepto.get(EstimacionesInternas.HORAS);
						ldm.importe = concepto.get(EstimacionesInternas.IMPORTE);
						ldm.recurso = concepto.get(EstimacionesInternas.RECURSO).intValue();
						lista.add(ldm);
					}
				}
			}
			
			ldm = new LineaDetalleMes();
			ldm.concepto = LineaDetalleUsuario.CONCEPTO_HORAS_ASIGNADAS;
			concepto = mesCurso.get(LineaDetalleUsuario.CONCEPTO_HORAS_ASIGNADAS).get(EstimacionesInternas.SISTEMA);;
			ldm.horas = concepto.get(EstimacionesInternas.HORAS);
			ldm.importe = concepto.get(EstimacionesInternas.IMPORTE);
			lista.add(ldm);
			
			ldm = new LineaDetalleMes();
			ldm.concepto = LineaDetalleUsuario.CONCEPTO_HORAS_PENDIENTES;
			concepto = mesCurso.get(LineaDetalleUsuario.CONCEPTO_HORAS_PENDIENTES).get(EstimacionesInternas.SISTEMA);;
			ldm.horas = concepto.get(EstimacionesInternas.HORAS);
			ldm.importe = concepto.get(EstimacionesInternas.IMPORTE);
			lista.add(ldm);
		}
		
		ArrayList<Object> listadoAux = new ArrayList<Object>();
		listadoAux.addAll(lista);
		
		this.tTablaMes.pintaTabla(listadoAux);
		
		lMes.setText(Constantes.nomMes(this.mesRepresentado) + "/" + this.eI.cbAnio.getValue());
	}
	
		
}
