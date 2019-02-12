package ui.Economico.Tarifas.Tables;

import java.net.URL;
import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import model.beans.Tarifa;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Tableable;
import ui.Economico.Tarifas.InformaTarifa;

public class TarifaTabla extends ParamTable implements Tableable  {
	
	public Tarifa t = null;
		 
    public SimpleStringProperty id;
    public SimpleStringProperty fInicioVig;
    public SimpleStringProperty fFinVig;
    public SimpleStringProperty esDesarrollo;
    public SimpleStringProperty esMantenimiento;
    public SimpleStringProperty proveedor;
    public SimpleStringProperty coste;
    
 	public static final String ID = "cId";
	public static final String INI_VIGENCIA = "cFIniVig";
	public static final String FIN_VIGENCIA = "cFFinVig";
	public static final String ES_DESARROLLO = "cDesa";
	public static final String ES_MANTENIMIENTO = "cMante";
	public static final String PROVEEDOR = "cProve";
	public static final String COSTE = "cCoste";
    
    public TarifaTabla(Tarifa t) {
    	this.t = t;
    	
        this.id = new SimpleStringProperty(new Integer(t.idTarifa).toString());
        
        try {
        	this.fInicioVig  = new SimpleStringProperty(FormateadorDatos.formateaDato(t.fInicioVig,FormateadorDatos.FORMATO_FECHA));
        } catch (Exception e) {
        
        }
        
        try {
        	if (Constantes.fechaFinal.compareTo(t.fFinVig)==1) {
        		this.fFinVig  = new SimpleStringProperty(FormateadorDatos.formateaDato(t.fFinVig,FormateadorDatos.FORMATO_FECHA));
    		} else {
        		this.fFinVig  = new SimpleStringProperty("");    			
    		}
        	
        	
        } catch (Exception e) {
        }

        this.esDesarrollo =  new SimpleStringProperty(t.esDesarrollo?"SI":"NO");
        this.esMantenimiento =  new SimpleStringProperty(t.esMantenimiento?"SI":"NO");
        
        try {
        	this.coste = new SimpleStringProperty(FormateadorDatos.formateaDato(t.costeHora,FormateadorDatos.FORMATO_MONEDA));
        } catch (Exception e) {
        }
        
        this.proveedor = new SimpleStringProperty(t.proveedor.toString());

        setConfig();
    }
    
    public TarifaTabla() {
        this.id = new SimpleStringProperty("");
        this.fInicioVig = new SimpleStringProperty("");
        this.fFinVig = new SimpleStringProperty("");
        this.coste = new SimpleStringProperty("");
        this.esDesarrollo = new SimpleStringProperty();
        this.esMantenimiento = new SimpleStringProperty();
        this.proveedor  = new SimpleStringProperty();
        setConfig();
    }
    
    public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	configuracionTabla.put(TarifaTabla.ID, new ConfigTabla( "Id", TarifaTabla.ID,false, 0, true));
    	configuracionTabla.put(TarifaTabla.PROVEEDOR, new ConfigTabla("Proveedor", TarifaTabla.PROVEEDOR, false, 2));
    	configuracionTabla.put(TarifaTabla.COSTE, new ConfigTabla("€/Hora", TarifaTabla.COSTE,  false, 3));
    	configuracionTabla.put(TarifaTabla.INI_VIGENCIA, new ConfigTabla("Inicio Vigencia", TarifaTabla.INI_VIGENCIA, false, 4));
    	configuracionTabla.put(TarifaTabla.FIN_VIGENCIA, new ConfigTabla( "Inicio Vigencia",TarifaTabla.FIN_VIGENCIA, false, 5));
    	configuracionTabla.put(TarifaTabla.ES_DESARROLLO, new ConfigTabla("Desarrollo",  TarifaTabla.ES_DESARROLLO, false, 6));
    	configuracionTabla.put(TarifaTabla.ES_MANTENIMIENTO, new ConfigTabla("Mantenimiento",TarifaTabla.ES_MANTENIMIENTO,  false, 7));
    }
    
    @Override
	public Tableable toTableable(Object o) {
    	Tarifa t = (Tarifa) o;
		return new TarifaTabla(t);
	}
    

	public void set(String campo, String valor){			
	}
	
	public void set(Tarifa t) {
    	this.t = t;
    	
        this.id = new SimpleStringProperty(new Integer(t.idTarifa).toString());
        
        try {
        	this.fInicioVig  = new SimpleStringProperty(FormateadorDatos.formateaDato(t.fInicioVig,FormateadorDatos.FORMATO_FECHA));
        } catch (Exception e) {
        
        }
        
        try {
        	if (Constantes.fechaFinal.compareTo(t.fFinVig)==1) {
        		this.fFinVig  = new SimpleStringProperty(FormateadorDatos.formateaDato(t.fFinVig,FormateadorDatos.FORMATO_FECHA));
    		} else {
        		this.fFinVig  = new SimpleStringProperty("");    			
    		}
        	
        	
        } catch (Exception e) {
        }

        this.esDesarrollo =  new SimpleStringProperty(t.esDesarrollo?"SI":"NO");
        this.esMantenimiento =  new SimpleStringProperty(t.esMantenimiento?"SI":"NO");
        
        try {
        	this.coste = new SimpleStringProperty(FormateadorDatos.formateaDato(t.costeHora,FormateadorDatos.FORMATO_MONEDA));
        } catch (Exception e) {
        }
        
        this.proveedor = new SimpleStringProperty(t.proveedor.toString());

        setConfig();
    }
    
	public String get(String campo) {
		try {
			if (TarifaTabla.ID.equals(campo)) return this.id.get();
			if (TarifaTabla.PROVEEDOR.equals(campo)) return this.proveedor.get();
			if (TarifaTabla.COSTE.equals(campo)) return this.coste.get();
			if (TarifaTabla.INI_VIGENCIA.equals(campo)) return this.fInicioVig.get();
			if (TarifaTabla.FIN_VIGENCIA.equals(campo)) return this.fFinVig.get();
			if (TarifaTabla.ES_DESARROLLO.equals(campo)) return this.esDesarrollo.get();
			if (TarifaTabla.ES_MANTENIMIENTO.equals(campo)) return this.esMantenimiento.get();
		} catch ( Exception e) {}
			
		return null;
	}
	
	@Override
	public AnchorPane getFilaEmbebida(TableRowDataFeatures<Tableable> expander) {
		try {
			FXMLLoader loader = new FXMLLoader();
			InformaTarifa infTarifa = new InformaTarifa(expander);
	        loader.setLocation(new URL(infTarifa.getFXML()));
	        
	        return (AnchorPane) loader.load();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}



