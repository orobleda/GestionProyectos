package ui.Recursos.GestionVacaciones.Tables;

import java.util.Calendar;
import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.beans.JornadasMes;
import model.constantes.FormateadorDatos;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Economico.EstimacionesInternas.tables.LineaDetalleUsuario;
import ui.interfaces.Tableable;

public class HorasJornada extends ParamTable implements Tableable  {
	
	public static int id = 0; 
	 
    public JornadasMes jm = null;
	public int identificador = 0;
    
    public HorasJornada(JornadasMes jm) {
        this.jm = jm;
        
        setConfig();
    }
    
    public HorasJornada() {
    	identificador = HorasJornada.id;
    	HorasJornada.id++;
    	
        this.jm = null;
        setConfig();
    }
    
    public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	
    	Calendar c = Calendar.getInstance();
    	c.set(jm.anio, jm.mes-1, 1);
    	anchoColumnas = new HashMap<String, Integer>();
    	
    	for (int i=1; i< c.getActualMaximum(Calendar.DAY_OF_MONTH)+1;i++) {
    		configuracionTabla.put(""+i, new ConfigTabla(""+i,""+i,  false,i, false));
    		anchoColumnas.put(""+i, new Integer(30));
    	}
    	
    }
    
	@Override
	public Object muestraSelector() {
		return null;
	}
	
	@Override
	public AnchorPane getFilaEmbebida(TableRowDataFeatures<Tableable> expander) {
		return null;
	}
    
    @Override
	public Tableable toTableable(Object o) {
		return (Tableable) o;
	}
    

	public void set(String campo, String valor){
	}
    
	public String get(String campo) {
		try {
			float valor = jm.jornadas.get(new Integer(campo)-1);
			if (valor==-1) return "";
			return FormateadorDatos.formateaDato(valor, FormateadorDatos.FORMATO_REAL);
		} catch ( Exception e) {}
			
		return null;
	}
	
	
}
