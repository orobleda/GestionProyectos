package model.beans;

public class ParametroFases extends Parametro {
	
	public String getQueryInsercion() {
		return QUERY_INSERTA_PARAMETRO;
	}
	
	public String getQueryConsulta() {
		return QUERY_CONSULTA_PARAMETRO;
	}
	
	public String getQueryBorrado() {
		return QUERY_BORRA_PARAMETRO;
	}
	
	public ParametroFases() {
		QUERY_INSERTA_PARAMETRO = "iInsertaParametroFase";
		QUERY_CONSULTA_PARAMETRO = "cConsultaParametroFase";
		QUERY_BORRA_PARAMETRO = "dBorraParametroFase";
	}
	
	public ParametroFases clone() {
		ParametroFases pf = new ParametroFases();
		pf.codParametro = this.codParametro;
		pf.id = this.id;
		pf.idEntidadAsociada = this.idEntidadAsociada;
		pf.metaParam = this.metaParam;
		pf.modificado = this.modificado;
		pf.valorEntero = this.valorEntero;
		pf.valorfecha = this.valorfecha;
		pf.valorObjeto = this.valorObjeto;
		pf.valorReal = this.valorReal;
		pf.valorTexto = this.valorTexto;
		
		return pf;
	}
	
}
