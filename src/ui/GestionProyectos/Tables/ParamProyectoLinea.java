package ui.GestionProyectos.Tables;

import java.util.HashMap;

import javafx.beans.property.SimpleStringProperty;
import model.beans.ParametroProyecto;
import model.constantes.FormateadorDatos;
import model.interfaces.Loadable;
import model.metadatos.MetaFormatoProyecto;
import model.metadatos.TipoParamProyecto;
import ui.ConfigTabla;
import ui.ParamTable;
import ui.interfaces.Tableable;
import ui.popUps.SeleccionElemento;

public class ParamProyectoLinea extends ParamTable implements Tableable  {
	 
    private final SimpleStringProperty id;
    private final SimpleStringProperty tipo;
    private final SimpleStringProperty descripcion;
    private final SimpleStringProperty valor;
    private final SimpleStringProperty valorReal;
    
 	public static final String ID = "cCodigo";
	public static final String TIPO = "cTipo";
	public static final String DESCRIPCION = "cDescrip";
	public static final String VALOR = "cValor";
	public static final String VALORREAL = "valorreal";
    
    public ParamProyectoLinea(String id, String tipo, String descripcion, String valor, int tipoDato) {
        this.id = new SimpleStringProperty(id);
        this.tipo = new SimpleStringProperty(tipo);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.valor = new SimpleStringProperty(valor);
        this.valorReal = new SimpleStringProperty(valor);
        
        if (FormateadorDatos.FORMATO_FORMATO_PROYECTO == new Integer(tipoDato).intValue()){
        	try{
        		this.valor.set(MetaFormatoProyecto.listado.get(new Integer(valor)).toString());
        	} catch (Exception e) {
        		this.valor.set("");
        		this.valorReal.set("");
        	}
		}        
        
        this.tipoDato = tipoDato;
        setConfig();
    }
    
    public ParamProyectoLinea() {
        this.id = new SimpleStringProperty("");
        this.tipo = new SimpleStringProperty("");
        this.descripcion = new SimpleStringProperty("");
        this.valor = new SimpleStringProperty("");
        this.valorReal = new SimpleStringProperty();
        this.tipoDato = 0;
        setConfig();
    }
    
    public void setConfig() {
    	configuracionTabla = new HashMap<String, ConfigTabla>();
    	configuracionTabla.put("cTipo", new ConfigTabla("cTipo", "tipo", false));
    	configuracionTabla.put("cCodigo", new ConfigTabla("cCodigo", "id", false));
    	configuracionTabla.put("cDescrip", new ConfigTabla("cDescrip", "descripcion", false));
    	configuracionTabla.put("cValor",  new ConfigTabla("cValor", "valor", true));
    	this.controlPantalla =  new SeleccionElemento(new ParamTable(), "vueltaPopUp", (Loadable) this.muestraSelector());
    }
    
	@Override
	public Object muestraSelector() {
		TipoParamProyecto tpP = TipoParamProyecto.listado.get(this.tipoDato);
		
		if (tpP==null) return null;
		
		return tpP.dameSelector();
	}
    
    @Override
	public Tableable toTableable(Object o) {
    	ParametroProyecto mtp = (ParametroProyecto) o;
		return null; //new ParamProyectoLinea(new Integer(mtp.id).toString(), mtp.tipo, mtp.descripcion, mtp.valor,mtp.tipoDato);
	}
    

	public void set(String campo, String valor){
		try {
			if (ParamProyectoLinea.ID.equals(campo)) id.set(valor);
			if (ParamProyectoLinea.TIPO.equals(campo)) tipo.set(valor);
			if (ParamProyectoLinea.DESCRIPCION.equals(campo)) descripcion.set(valor);
			if (ParamProyectoLinea.VALOR.equals(campo)) {
				
				if (FormateadorDatos.FORMATO_FORMATO_PROYECTO == this.tipoDato){
		        	valorReal.set(new Integer(MetaFormatoProyecto.listado.get(new Integer(valor)).id).toString());
		        	this.valor.set(FormateadorDatos.parseaDato(MetaFormatoProyecto.listado.get(new Integer(valor)).toString(), this.tipoDato).toString());
				} else 
				if (FormateadorDatos.FORMATO_TIPO_PROYECTO == this.tipoDato){
		        	valorReal.set(new Integer(valor).toString());
		        	this.valor.set(FormateadorDatos.formateaDato(valor, FormateadorDatos.FORMATO_TIPO_PROYECTO));
				} else {
					this.valor.set(FormateadorDatos.parseaDato(valor, this.tipoDato).toString());
					valorReal.set(FormateadorDatos.parseaDato(valor, this.tipoDato).toString());
				}
			}
			if (ParamProyectoLinea.VALORREAL.equals(campo)) FormateadorDatos.formateaDato(valorReal.get(), this.tipoDato);
		} catch ( Exception e) {}
			
	}
    
	public String get(String campo) {
		try {
			if (ParamProyectoLinea.ID.equals(campo)) return id.get();
			if (ParamProyectoLinea.TIPO.equals(campo)) return tipo.get();
			if (ParamProyectoLinea.DESCRIPCION.equals(campo)) return descripcion.get();
			if (ParamProyectoLinea.VALOR.equals(campo)) return  FormateadorDatos.formateaDato(valor.get(), this.tipoDato);
			if (ParamProyectoLinea.VALORREAL.equals(campo)) return FormateadorDatos.formateaDato(valorReal.get(), this.tipoDato);
		} catch ( Exception e) {}
			
		return null;
	}
	
	
}
