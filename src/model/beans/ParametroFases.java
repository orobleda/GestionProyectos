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
	
}
