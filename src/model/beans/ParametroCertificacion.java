package model.beans;

public class ParametroCertificacion extends Parametro{

	
	public String getQueryInsercion() {
		return QUERY_INSERTA_PARAMETRO;
	}
	
	public String getQueryConsulta() {
		return QUERY_CONSULTA_PARAMETRO;
	}
	
	public String getQueryBorrado() {
		return QUERY_BORRA_PARAMETRO;
	}
	
	public ParametroCertificacion() {
		QUERY_INSERTA_PARAMETRO = "iInsertaParametroCertificacion";
		QUERY_CONSULTA_PARAMETRO = "cConsultaParametroCertificacion";
		QUERY_BORRA_PARAMETRO = "dBorraParametroCertificacion";
	}
	
	public ParametroCertificacion clone() {
		ParametroCertificacion pf = new ParametroCertificacion();
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
