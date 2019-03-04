package model.beans;

public class ParametroProyecto extends Parametro {
	
	public String getQueryInsercion() {
		return QUERY_INSERTA_PARAMETRO;
	}
	
	public String getQueryConsulta() {
		return QUERY_CONSULTA_PARAMETRO;
	}
	
	public String getQueryBorrado() {
		return QUERY_BORRA_PARAMETRO;
	}
	
	public ParametroProyecto() {
		QUERY_INSERTA_PARAMETRO = "iInsertaParametroProyecto";
		QUERY_CONSULTA_PARAMETRO = "cConsultaParametroProyecto";
		QUERY_BORRA_PARAMETRO = "dBorraParametroProyecto";
	}
	
}
