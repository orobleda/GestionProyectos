package model.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.metadatos.MetaParamProyecto;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class ParametroProyecto implements Cargable{
	public int id = 0;
	public int idProyecto = 0;
	public int cod_parm= 0;
	public String valorTexto = "";
	public int valorEntero = 0;
	public float valorReal = 0;
	public Date valorFecha = null;
	public Date fecFinVig = null;
	
	public MetaParamProyecto mpProy = null;
	
	public Object getValor() {
		if (FormateadorDatos.FORMATO_FECHA == mpProy.tipoDato) {
			return valorFecha;
		}
		
		if (FormateadorDatos.FORMATO_INT == mpProy.tipoDato) {
			return valorEntero;
		}
		
		if (FormateadorDatos.FORMATO_MONEDA == mpProy.tipoDato) {
			return valorReal;
		}
		
		if (FormateadorDatos.FORMATO_REAL == mpProy.tipoDato) {
			return valorReal;
		}
		
		if (FormateadorDatos.FORMATO_TXT == mpProy.tipoDato) {
			return valorTexto;
		}
		
		if (FormateadorDatos.FORMATO_URL == mpProy.tipoDato) {
			return valorTexto;
		}

		if (FormateadorDatos.FORMATO_FORMATO_PROYECTO == mpProy.tipoDato) {
			return valorEntero;
		}
		
		if (FormateadorDatos.FORMATO_TIPO_PROYECTO == mpProy.tipoDato) {
			return valorEntero;
		}

		
		return null;
	}
	
	public void setValor(Object o) throws Exception {
		if (FormateadorDatos.FORMATO_FECHA == mpProy.tipoDato) {
			if ("".equals(o.toString())) this.valorFecha= null;
			else this.valorFecha = (Date) FormateadorDatos.parseaDato(o.toString(),FormateadorDatos.FORMATO_FECHA);
		}
		
		if (FormateadorDatos.FORMATO_INT == mpProy.tipoDato || FormateadorDatos.FORMATO_FORMATO_PROYECTO == mpProy.tipoDato  || FormateadorDatos.FORMATO_TIPO_PROYECTO == mpProy.tipoDato) {
			if ("".equals(o.toString())) this.valorEntero= 0;
			else this.valorEntero = (Integer) FormateadorDatos.parseaDato(o.toString(),FormateadorDatos.FORMATO_INT);
		}
		
		if (FormateadorDatos.FORMATO_MONEDA == mpProy.tipoDato) {
			if ("".equals(o.toString())) this.valorReal= 0;
			else this.valorReal = (Float) FormateadorDatos.parseaDato(o.toString(),FormateadorDatos.FORMATO_MONEDA);
		}
		
		if (FormateadorDatos.FORMATO_REAL == mpProy.tipoDato) {
			if ("".equals(o.toString())) this.valorReal= 0;
			else this.valorReal = (Float) FormateadorDatos.parseaDato(o.toString(),FormateadorDatos.FORMATO_MONEDA);
		}
		
		if (FormateadorDatos.FORMATO_TXT == mpProy.tipoDato) {
			this.valorTexto = o.toString();
		}
		
		if (FormateadorDatos.FORMATO_URL == mpProy.tipoDato) {
			this.valorTexto = o.toString();
		}
	}
	
	public void insertaParametro(int idProyecto){
			ConsultaBD consulta = new ConsultaBD();
			ArrayList<Cargable> parametros = consulta.ejecutaSQL("cMaxIdParamProyecto", null, this);
			
			ParametroProyecto p = (ParametroProyecto) parametros.get(0);
	        this.id = p.id+1;
		
			Date d = new Date();
			ArrayList<Object> listaInts = new ArrayList<Object>();
			listaInts.add(this.mpProy.id);
			
			List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
			listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_FECHA,d));
			listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,idProyecto));
			listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_LISTA_INT,listaInts));
			
			consulta = new ConsultaBD();
			consulta.ejecutaSQL("uVenceParametros", listaParms, null);
			
			listaParms = new ArrayList<ParametroBD>();
			listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,id));
			listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,idProyecto));
			listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_INT,this.mpProy.id));
			
			if (FormateadorDatos.FORMATO_TXT==this.mpProy.tipoDato) listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_STR,this.valorTexto));
			if (FormateadorDatos.FORMATO_URL==this.mpProy.tipoDato) listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_STR,this.valorTexto));
			if (FormateadorDatos.FORMATO_INT==this.mpProy.tipoDato) listaParms.add(new ParametroBD(5,ConstantesBD.PARAMBD_INT,this.valorEntero));
			if (FormateadorDatos.FORMATO_FORMATO_PROYECTO==this.mpProy.tipoDato) listaParms.add(new ParametroBD(5,ConstantesBD.PARAMBD_INT,this.valorEntero));
			if (FormateadorDatos.FORMATO_REAL==this.mpProy.tipoDato) listaParms.add(new ParametroBD(6,ConstantesBD.PARAMBD_REAL,this.valorReal));
			if (FormateadorDatos.FORMATO_MONEDA==this.mpProy.tipoDato) listaParms.add(new ParametroBD(6,ConstantesBD.PARAMBD_REAL,this.valorReal));
			if (FormateadorDatos.FORMATO_FECHA==this.mpProy.tipoDato) listaParms.add(new ParametroBD(7,ConstantesBD.PARAMBD_FECHA,this.valorFecha));
			if (FormateadorDatos.FORMATO_TIPO_PROYECTO==this.mpProy.tipoDato) listaParms.add(new ParametroBD(5,ConstantesBD.PARAMBD_INT,this.valorEntero));
			
			consulta = new ConsultaBD();
			consulta.ejecutaSQL("iAltaParamProy", listaParms, null);
	}

	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
			if (salida.get("paramId")==null) {
				this.id = 0;
			} else this.id = (Integer) salida.get("paramId");
			
			this.idProyecto = (Integer) salida.get("parProyIdProy");
			this.cod_parm = (Integer) salida.get("parProyCodParm");
			this.mpProy = MetaParamProyecto.listado.get(salida.get("parProyCodParm"));
			try { this.valorTexto = (String) salida.get("parProyvalorTxt"); } catch (Exception e) {}
			try { this.valorEntero = (Integer) salida.get("parProyValorInt");} catch (Exception e) {}
			try { this.valorReal = ((Double) salida.get("parProyValorReal")).floatValue();} catch (Exception e) {}
			try { this.valorFecha = (Date) FormateadorDatos.parseaDato(salida.get("parProyValorFx").toString(),FormateadorDatos.FORMATO_FECHA);} catch (Exception e) {}
			try { this.fecFinVig = (Date) FormateadorDatos.parseaDato(salida.get("parProyFecFinVig").toString(),FormateadorDatos.FORMATO_FECHA);} catch (Exception e) {}
			
		} catch (Exception e) {
			
		} 
		
		return this;
	}
	
	public ArrayList<ParametroProyecto> listadoParamProy() {
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.idProyecto));
		
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> proyectos = consulta.ejecutaSQL("cConsultaParmProy", listaParms, this);
		
		Iterator<Cargable> itProyecto = proyectos.iterator();
		ArrayList<ParametroProyecto> salida = new ArrayList<ParametroProyecto>();
		
		while (itProyecto.hasNext()) {
			ParametroProyecto p = (ParametroProyecto) itProyecto.next();
			salida.add(p);
		}
		
        return salida;
	}
	
	public void bajaProyecto(Proyecto p) {
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, p.id));
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("dDelParamProyecto", listaParms, this);
	}
}
