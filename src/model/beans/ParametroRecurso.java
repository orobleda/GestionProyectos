package model.beans;

public class ParametroRecurso extends Parametro{

	
	public String getQueryInsercion() {
		return QUERY_INSERTA_PARAMETRO;
	}
	
	public String getQueryConsulta() {
		return QUERY_CONSULTA_PARAMETRO;
	}
	
	public String getQueryBorrado() {
		return QUERY_BORRA_PARAMETRO;
	}
	
	public ParametroRecurso() {
		QUERY_INSERTA_PARAMETRO = "iInsertaParametroRecurso";
		QUERY_CONSULTA_PARAMETRO = "cConsultaParametroRecurso";
		QUERY_BORRA_PARAMETRO = "dBorraParametroRecurso";
	}
}
