package ui.Economico.ControlPresupuestario.Tables;

import java.util.ArrayList;
import java.util.HashMap;

import org.controlsfx.control.table.TableRowExpanderColumn.TableRowDataFeatures;

import javafx.scene.layout.AnchorPane;
import model.beans.Concepto;
import model.beans.Estimacion;
import model.beans.Recurso;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaParamRecurso;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.Tableable;
import ui.Economico.ControlPresupuestario.EdicionEstImp.General;

public class LineaCosteUsuario extends ParamTable implements Tableable  {
	
	public static final String ID = "Id";
	public static final String USUARIO = "Usuario";
	public static final String HEST = "Horas Est.";
	public static final String CEST = "Coste Est.";
	public static final String HIMP = "Horas Imp.";
	public static final String CIMP = "Coste Imp.";
	public static final String VALIDADO = "Validado";
	public static final String SISTEMA = "Sistema";
	
	public ArrayList<Float> resumen = null;
	
	public Concepto concepto = null;
	
	public LineaCosteUsuario(Concepto concepto) {
		this.concepto = concepto;
    	setConfig();
    }
    
    public LineaCosteUsuario() {
    	setConfig();
    }
    
	public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
		configuracionTabla.put(LineaCosteUsuario.ID, new ConfigTabla(LineaCosteUsuario.ID, LineaCosteUsuario.ID, false,0, false));
		configuracionTabla.put(LineaCosteUsuario.USUARIO, new ConfigTabla(LineaCosteUsuario.USUARIO, LineaCosteUsuario.USUARIO, false,1, false));
		configuracionTabla.put(LineaCosteUsuario.HEST, new ConfigTabla(LineaCosteUsuario.HEST, LineaCosteUsuario.HEST, true,2, false));
		configuracionTabla.put(LineaCosteUsuario.CEST, new ConfigTabla(LineaCosteUsuario.CEST, LineaCosteUsuario.CEST, true,3, false));
		configuracionTabla.put(LineaCosteUsuario.HIMP, new ConfigTabla(LineaCosteUsuario.HIMP, LineaCosteUsuario.HIMP, true,4, false));
		configuracionTabla.put(LineaCosteUsuario.CIMP, new ConfigTabla(LineaCosteUsuario.CIMP, LineaCosteUsuario.CIMP, true,5, false));
		configuracionTabla.put(LineaCosteUsuario.VALIDADO, new ConfigTabla(LineaCosteUsuario.VALIDADO, LineaCosteUsuario.VALIDADO, true,6, false));
		configuracionTabla.put(LineaCosteUsuario.SISTEMA, new ConfigTabla(LineaCosteUsuario.SISTEMA, LineaCosteUsuario.SISTEMA, true,7, false));
    	this.controlPantalla =  new General(new ParamTable(), "vueltaPopUp");
    }
    
   	public void set(String campo, String valor){
   		try {
   			
   		} catch ( Exception e) {}   			
   	}
       
   	public String get(String campo) {
   		try {
   			if (LineaCosteUsuario.ID.equals(campo)) {
   				if (concepto.r==null) return "";
   				return (String) Recurso.listadoRecursosEstatico().get(concepto.r.id).getValorParametro(MetaParamRecurso.IDRecurso);
   			}
   			if (LineaCosteUsuario.USUARIO.equals(campo)) {
   				if (concepto.r==null) return "";
   				return Recurso.listadoRecursosEstatico().get(concepto.r.id).nombre;
   			}
   			if (LineaCosteUsuario.HEST.equals(campo)) {
   				if (this.concepto.listaEstimaciones!=null && this.concepto.listaEstimaciones.size()>0)
   					return FormateadorDatos.formateaDato(this.concepto.listaEstimaciones.get(0).horas,FormateadorDatos.FORMATO_REAL);
   				else return "";   				
   			}
   			if (LineaCosteUsuario.CEST.equals(campo)) {
   				if (this.concepto.listaEstimaciones!=null && this.concepto.listaEstimaciones.size()>0)
   					return FormateadorDatos.formateaDato(this.concepto.listaEstimaciones.get(0).importe,FormateadorDatos.FORMATO_MONEDA);
   				else return "";   				
   			}
   			if (LineaCosteUsuario.HIMP.equals(campo)) {
   				if (this.concepto.listaImputaciones!=null && this.concepto.listaImputaciones.size()>0)
   					return FormateadorDatos.formateaDato(this.concepto.listaImputaciones.get(0).getHoras(),FormateadorDatos.FORMATO_REAL);
   				else return "";   				
   			}
   			if (LineaCosteUsuario.CIMP.equals(campo)) {
   				if (this.concepto.listaImputaciones!=null && this.concepto.listaImputaciones.size()>0)
   					return FormateadorDatos.formateaDato(this.concepto.listaImputaciones.get(0).getImporte(),FormateadorDatos.FORMATO_MONEDA);
   				else return "";   				
   			}
   			if (LineaCosteUsuario.VALIDADO.equals(campo)) {
   				if (this.concepto.listaEstimaciones!=null && this.concepto.listaEstimaciones.size()>0)
   					if (Estimacion.APROBADA == this.concepto.listaEstimaciones.get(0).aprobacion)
   						return "SI";
   					else return "NO";
   				else return "";   				
   			} 
   			if (LineaCosteUsuario.SISTEMA.equals(campo)) {
   				if (concepto.s==null) return "";
   				return concepto.s.codigo;
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
			Concepto concepto = (Concepto) o;
    		return new LineaCosteUsuario(concepto);
    	} catch (Exception e){
    		try {
    			LineaCosteUsuario f = (LineaCosteUsuario) o;
    			return f;
    		} catch (Exception ex) {
    			return null;
    		}
    	}
	}
   
	
	@Override
	public AnchorPane getFilaEmbebida(TableRowDataFeatures<Tableable> expander) {
		return null;
	}
	  	
}
