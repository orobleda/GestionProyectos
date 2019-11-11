package ui.Administracion.BackupBD.tables;

import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.constantes.FormateadorDatos;
import model.utils.db.ReplicaBD;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;

public class LineaCopiaBD extends ParamTable implements Tableable, Comparable<LineaCopiaBD>  {
	
	public static final String ID = "id";
	public static final String FECHA = "Fecha";
	public static final String HORA = "Hora";
	public static final String NOMBRE = "Nombre";
	public static final String VERSION = "Versión";
		
	public ReplicaBD rbd;
	
	public LineaCopiaBD(LineaCopiaBD lcpe) {
		this.rbd = lcpe.rbd;
		
    	setConfig();
    }
	
	public LineaCopiaBD(ReplicaBD rbd) {
		this.rbd = rbd;
		
    	setConfig();
    }
    
    public LineaCopiaBD() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
		configuracionTabla.put(LineaCopiaBD.ID, new ConfigTabla(LineaCopiaBD.ID, LineaCopiaBD.ID, true,0, false));
		configuracionTabla.put(LineaCopiaBD.FECHA, new ConfigTabla(LineaCopiaBD.FECHA, LineaCopiaBD.FECHA, true,1, false));
		configuracionTabla.put(LineaCopiaBD.HORA, new ConfigTabla(LineaCopiaBD.HORA, LineaCopiaBD.HORA, true,2, false));
		configuracionTabla.put(LineaCopiaBD.NOMBRE, new ConfigTabla(LineaCopiaBD.NOMBRE, LineaCopiaBD.NOMBRE, true,3, false));
		configuracionTabla.put(LineaCopiaBD.VERSION, new ConfigTabla(LineaCopiaBD.VERSION, LineaCopiaBD.VERSION, true,4, false));
		
		anchoColumnas = new HashMap<String, Integer>();
		anchoColumnas.put(LineaCopiaBD.NOMBRE, new Integer(500));
		anchoColumnas.put(LineaCopiaBD.VERSION, new Integer(200));
		
		
    }
    
   	public void set(String campo, String valor){
   		try {
   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			if (LineaCopiaBD.ID.equals(campo)) {
   				return ""+this.rbd.id;
   			}
   			if (LineaCopiaBD.FECHA.equals(campo)) {
   				return FormateadorDatos.formateaDato(this.rbd.fxSalvado,FormateadorDatos.FORMATO_FECHA);
   			}
   			if (LineaCopiaBD.NOMBRE.equals(campo)) {
   				return this.rbd.nomFichero;
   			}
   			if (LineaCopiaBD.HORA.equals(campo)) {
   				return this.rbd.getHora();
   			}
   			if (LineaCopiaBD.VERSION.equals(campo)) {
   				return this.rbd.version;
   			}
   			   			   			
   			return "";
   		} catch ( Exception e) {
   			return "";
   		}
   	}
   	
	@Override
	public Object muestraSelector() {
		return this;
	}
    
    @Override
	public Tableable toTableable(Object o) {
    		try {
    			LineaCopiaBD f = (LineaCopiaBD) o;
    			return f;
    		} catch (Exception ex) {
    			ReplicaBD f = (ReplicaBD) o;
    			return new LineaCopiaBD(f);
    		}
	}   
	
	@Override
	public AnchorPane getFilaEmbebida(TableRowDataFeatures<Tableable> expander) {
		return null;
	}

	@Override
	public int compareTo(LineaCopiaBD o) {
		return this.rbd.compareTo(o.rbd);
	}

		  	
}
