package model.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.metadatos.MetaParamProyecto;
import model.metadatos.MetaParametro;
import model.metadatos.TipoDato;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;
import ui.Propiedad;
import ui.Propiediable;

public class Parametro implements Propiediable, Cargable {
	public static int SIN_ID_ELEMENTO = -1;
	
	public MetaParametro metaParam = null;
	public int idEntidadAsociada = 0;
	public int id = 0;
	public String codParametro = "";
		
	public int valorEntero = 0;
	public float valorReal = 0;
	public Object valorObjeto = 0;
	public String valorTexto = null;
	public Date valorfecha = null;
	
	public boolean modificado = false;
	
	public Object getValor() {
		if (metaParam== null) return null;
		
		if (metaParam.tipoDato == TipoDato.FORMATO_TXT|| 
				metaParam.tipoDato == TipoDato.FORMATO_URL) return valorTexto;
		if (metaParam.tipoDato == TipoDato.FORMATO_INT) return valorEntero;
		if (metaParam.tipoDato == TipoDato.FORMATO_REAL || 
				metaParam.tipoDato == TipoDato.FORMATO_MONEDA ||
						metaParam.tipoDato == TipoDato.FORMATO_PORC ) return valorReal;
		if (metaParam.tipoDato == TipoDato.FORMATO_FORMATO_PROYECTO || 
				metaParam.tipoDato == TipoDato.FORMATO_TIPO_PROYECTO ) return valorObjeto;
		if (metaParam.tipoDato == TipoDato.FORMATO_FECHA) return valorfecha;
		if (metaParam.tipoDato == TipoDato.FORMATO_BOOLEAN) return (valorEntero==Constantes.NUM_TRUE)?Constantes.TRUE:Constantes.FALSE;
		
		return null;
	}
	
	@Override
	public void setValor(Object valor) {
		if (metaParam== null) return;
		
		modificado = true;
		
		if (metaParam.tipoDato == TipoDato.FORMATO_TXT|| 
				metaParam.tipoDato == TipoDato.FORMATO_URL) valorTexto =  (String) valor;
			if (metaParam.tipoDato == TipoDato.FORMATO_INT) valorEntero =  (Integer) valor;
			if (metaParam.tipoDato == TipoDato.FORMATO_REAL || 
					metaParam.tipoDato == TipoDato.FORMATO_MONEDA ||
							metaParam.tipoDato == TipoDato.FORMATO_PORC ) valorReal =  (Float) valor;
			if (metaParam.tipoDato == TipoDato.FORMATO_FORMATO_PROYECTO || 
					metaParam.tipoDato == TipoDato.FORMATO_TIPO_PROYECTO ) valorObjeto = valor;
			if (metaParam.tipoDato == TipoDato.FORMATO_FECHA) valorfecha = (Date) valor;
			if (metaParam.tipoDato == TipoDato.FORMATO_BOOLEAN) 
				if ((Boolean)valor==Constantes.TRUE) valorEntero=Constantes.NUM_TRUE;
				else 								 valorEntero=Constantes.NUM_FALSE;
	}

	@Override
	public Propiedad toPropiedad() {
		Object valor = null;
		
		if (metaParam.tipoDato == TipoDato.FORMATO_TXT|| 
				metaParam.tipoDato == TipoDato.FORMATO_URL) valor = valorTexto;
			if (metaParam.tipoDato == TipoDato.FORMATO_INT) valor = valorEntero;
			if (metaParam.tipoDato == TipoDato.FORMATO_REAL || 
					metaParam.tipoDato == TipoDato.FORMATO_MONEDA ||
							metaParam.tipoDato == TipoDato.FORMATO_PORC ) valor = valorReal;
			if (metaParam.tipoDato == TipoDato.FORMATO_FORMATO_PROYECTO || 
					metaParam.tipoDato == TipoDato.FORMATO_TIPO_PROYECTO ) valor = valorObjeto;
			if (metaParam.tipoDato == TipoDato.FORMATO_FECHA) valor = valorfecha;
			if (metaParam.tipoDato == TipoDato.FORMATO_BOOLEAN) valor = (valorEntero==Constantes.NUM_TRUE)?Constantes.TRUE:Constantes.FALSE;
		
		return new Propiedad(metaParam.categoria, metaParam.nomParametro, metaParam.nomParametro, valor, metaParam.tipoDato, (Propiediable) this);		
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
			if (salida.get("parId")==null) {
				this.id = 0;
			} else this.id = (Integer) salida.get("parId");
			
			this.idEntidadAsociada = (Integer) salida.get("parIdElemento");
			this.codParametro = (String) salida.get("parVlCodParm");
			try { this.valorTexto = (String) salida.get("parVlTexto"); } catch (Exception e) {}
			try { this.valorEntero = (Integer) salida.get("parVlEntero");} catch (Exception e) {}
			try { this.valorReal = ((Double) salida.get("parVlReal")).floatValue();} catch (Exception e) {}
			try { this.valorfecha = (Date) FormateadorDatos.parseaDato(salida.get("parVlFx").toString(),FormateadorDatos.FORMATO_FECHA);} catch (Exception e) {}
			
		} catch (Exception e) {
			
		}
		
		return this;
	}
	
	public HashMap<String,Parametro> dameParametros(String entidad, int idElemento) {
		ConsultaBD consulta = new ConsultaBD();
		
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_STR,entidad));
		
		if (idElemento!=-1)
			listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,idElemento));
		
		ArrayList<Cargable> parametros = consulta.ejecutaSQL("cConsultaParametro", listaParms, this);
		
		Iterator<Cargable> itCargable = parametros.iterator();
		HashMap<String, Parametro> salida = new HashMap<String, Parametro> ();
		
		Iterator<MetaParametro> itMetaParam = MetaParametro.listado.values().iterator();
		while (itMetaParam.hasNext()) {
			Parametro par = new Parametro();
			par.metaParam = itMetaParam.next();
			salida.put(par.metaParam.codParametro, par);
		}		
		
		while (itCargable.hasNext()) {
			Parametro par = (Parametro) itCargable.next();
			Parametro parAux = salida.get(par.codParametro);
			par.metaParam = parAux.metaParam;
			salida.put(par.metaParam.codParametro,par);						
		}
		
		return salida;
	}
	
	public void actualizaParametro(String idTransaccion) throws Exception {
		ConsultaBD consulta = new ConsultaBD();
		
		bajaParametro(idTransaccion);
		insertaParametro(idTransaccion);
		
		if (idTransaccion!=null)
			consulta.ejecutaTransaccion(idTransaccion);
		
		modificado = false;
	}
	
	private void bajaParametro(String idTransaccion) {
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_STR, this.codParametro));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.idEntidadAsociada));
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("dBorraParametro", listaParms, this, idTransaccion);
	}
	
	private void insertaParametro(String idTransaccion){
		ConsultaBD consulta = new ConsultaBD();
				
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_ID,id));
		listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,this.idEntidadAsociada));
		
		if (TipoDato.FORMATO_TXT==this.metaParam.tipoDato) listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_STR,this.valorTexto));
		if (TipoDato.FORMATO_URL==this.metaParam.tipoDato) listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_STR,this.valorTexto));
		if (TipoDato.FORMATO_INT==this.metaParam.tipoDato) listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_INT,this.valorEntero));
		if (TipoDato.FORMATO_FORMATO_PROYECTO==this.metaParam.tipoDato) listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_INT,this.valorEntero));
		if (TipoDato.FORMATO_REAL==this.metaParam.tipoDato) listaParms.add(new ParametroBD(5,ConstantesBD.PARAMBD_REAL,this.valorReal));
		if (TipoDato.FORMATO_MONEDA==this.metaParam.tipoDato) listaParms.add(new ParametroBD(5,ConstantesBD.PARAMBD_REAL,this.valorReal));
		if (TipoDato.FORMATO_FECHA==this.metaParam.tipoDato) listaParms.add(new ParametroBD(6,ConstantesBD.PARAMBD_FECHA,this.valorfecha));
		if (TipoDato.FORMATO_BOOLEAN==this.metaParam.tipoDato) listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_INT,this.valorEntero));

		listaParms.add(new ParametroBD(7,ConstantesBD.PARAMBD_STR,this.codParametro));
		
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("iInsertaParametro", listaParms, null,idTransaccion);
	}
	
	
}
