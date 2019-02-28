package model.beans;

import java.util.Date;

import model.metadatos.TipoDato;

public class Parametro {
	public String entidadAsociada = null;
	public int idEntidadAsociada = 0;
	public int id = 0;
	public String categoria = "";
	public int tipoDato;
		
	public int valorEntero = 0;
	public float valorReal = 0;
	public int valorObjeto = 0;
	public String valorTexto = null;
	public Date valorfecha = null;
	
	public Object getValor() {
		if (this.tipoDato == TipoDato.FORMATO_TXT|| 
			this.tipoDato == TipoDato.FORMATO_URL) return valorTexto;
		if (this.tipoDato == TipoDato.FORMATO_INT) return valorEntero;
		if (this.tipoDato == TipoDato.FORMATO_REAL || 
		    this.tipoDato == TipoDato.FORMATO_MONEDA ||
		    this.tipoDato == TipoDato.FORMATO_PORC ) return valorReal;
		if (this.tipoDato == TipoDato.FORMATO_FORMATO_PROYECTO || 
		    this.tipoDato == TipoDato.FORMATO_TIPO_PROYECTO ) return valorObjeto;
		if (this.tipoDato == TipoDato.FORMATO_FECHA) return valorfecha;
		
		return null;
	}
}
