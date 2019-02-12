package model.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.metadatos.MetaParamRecurso;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class ParametroRecurso implements Cargable{
	public int id = 0;
	public int idRecurso = 0;
	public int cod_parm= 0;
	public String valorTexto = "";
	public int valorEntero = 0;
	public float valorReal = 0;
	public Date valorFecha = null;
	public Date fecFinVig = null;
	
	public MetaParamRecurso mpRecurso = null;
	
	public static final int PARAM_GESTOR = 1;
	public static final int PARAM_JORNADA = 2;
	
	public Object getValor() {
		if (FormateadorDatos.FORMATO_FECHA == mpRecurso.tipoDato) {
			return valorFecha;
		}
		
		if (FormateadorDatos.FORMATO_INT == mpRecurso.tipoDato) {
			return valorEntero;
		}
		
		if (FormateadorDatos.FORMATO_MONEDA == mpRecurso.tipoDato) {
			return valorReal;
		}
		
		if (FormateadorDatos.FORMATO_REAL == mpRecurso.tipoDato) {
			return valorReal;
		}
		
		if (FormateadorDatos.FORMATO_TXT == mpRecurso.tipoDato) {
			return valorTexto;
		}
		
		if (FormateadorDatos.FORMATO_URL == mpRecurso.tipoDato) {
			return valorTexto;
		}

		if (FormateadorDatos.FORMATO_FORMATO_PROYECTO == mpRecurso.tipoDato) {
			return valorEntero;
		}

		
		return null;
	}
	
	public void setValor(Object o) throws Exception {
		if (FormateadorDatos.FORMATO_FECHA == mpRecurso.tipoDato) {
			if ("".equals(o.toString())) this.valorFecha= null;
			else this.valorFecha = (Date) FormateadorDatos.parseaDato(o.toString(),FormateadorDatos.FORMATO_FECHA);
		}
		
		if (FormateadorDatos.FORMATO_INT == mpRecurso.tipoDato || FormateadorDatos.FORMATO_FORMATO_PROYECTO == mpRecurso.tipoDato) {
			if ("".equals(o.toString())) this.valorEntero= 0;
			else this.valorEntero = (Integer) FormateadorDatos.parseaDato(o.toString(),FormateadorDatos.FORMATO_INT);
		}
		
		if (FormateadorDatos.FORMATO_MONEDA == mpRecurso.tipoDato) {
			if ("".equals(o.toString())) this.valorReal= 0;
			else this.valorReal = (Float) FormateadorDatos.parseaDato(o.toString(),FormateadorDatos.FORMATO_MONEDA);
		}
		
		if (FormateadorDatos.FORMATO_REAL == mpRecurso.tipoDato) {
			if ("".equals(o.toString())) this.valorReal= 0;
			else this.valorReal = (Float) FormateadorDatos.parseaDato(o.toString(),FormateadorDatos.FORMATO_MONEDA);
		}
		
		if (FormateadorDatos.FORMATO_TXT == mpRecurso.tipoDato) {
			this.valorTexto = o.toString();
		}
		
		if (FormateadorDatos.FORMATO_URL == mpRecurso.tipoDato) {
			this.valorTexto = o.toString();
		}
	}
	
	public void insertaParametro(int idProyecto){
			ConsultaBD consulta = new ConsultaBD();
			ArrayList<Cargable> parametros = consulta.ejecutaSQL("cMaxIdParamRecurso", null, this);
			
			ParametroRecurso p = (ParametroRecurso) parametros.get(0);
	        this.id = p.id+1;
		
			Date d = new Date();
			ArrayList<Object> listaInts = new ArrayList<Object>();
			listaInts.add(this.mpRecurso.id);
			
			List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
			listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_FECHA,d));
			listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,idProyecto));
			listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_LISTA_INT,listaInts));
			
			consulta = new ConsultaBD();
			consulta.ejecutaSQL("uVenceParametrosRecurso", listaParms, null);
			
			listaParms = new ArrayList<ParametroBD>();
			listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,id));
			listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,idProyecto));
			listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_INT,this.mpRecurso.id));
			
			if (FormateadorDatos.FORMATO_TXT==this.mpRecurso.tipoDato) listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_STR,this.valorTexto));
			if (FormateadorDatos.FORMATO_URL==this.mpRecurso.tipoDato) listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_STR,this.valorTexto));
			if (FormateadorDatos.FORMATO_INT==this.mpRecurso.tipoDato) listaParms.add(new ParametroBD(5,ConstantesBD.PARAMBD_INT,this.valorEntero));
			if (FormateadorDatos.FORMATO_FORMATO_PROYECTO==this.mpRecurso.tipoDato) listaParms.add(new ParametroBD(5,ConstantesBD.PARAMBD_INT,this.valorEntero));
			if (FormateadorDatos.FORMATO_REAL==this.mpRecurso.tipoDato) listaParms.add(new ParametroBD(6,ConstantesBD.PARAMBD_REAL,this.valorReal));
			if (FormateadorDatos.FORMATO_MONEDA==this.mpRecurso.tipoDato) listaParms.add(new ParametroBD(6,ConstantesBD.PARAMBD_REAL,this.valorReal));
			if (FormateadorDatos.FORMATO_FECHA==this.mpRecurso.tipoDato) listaParms.add(new ParametroBD(7,ConstantesBD.PARAMBD_FECHA,this.valorFecha));
			
			consulta = new ConsultaBD();
			consulta.ejecutaSQL("iAltaParamRec", listaParms, null);
	}

	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
			if (salida.get("paramRId")==null) {
				this.id = 0;
			} else this.id = (Integer) salida.get("paramRId");
			
			this.idRecurso = (Integer) salida.get("parRecIdRec");
			this.cod_parm = (Integer) salida.get("parRecCodParm");
			this.mpRecurso = MetaParamRecurso.listado.get(salida.get("parRecCodParm"));
			try { this.valorTexto = (String) salida.get("parRecvalorTxt"); } catch (Exception e) {}
			try { this.valorEntero = (Integer) salida.get("parRecValorInt");} catch (Exception e) {}
			try { this.valorReal = ((Double) salida.get("parRecValorReal")).floatValue();} catch (Exception e) {}
			try { this.valorFecha = (Date) FormateadorDatos.parseaDato(salida.get("parRecValorFx").toString(),FormateadorDatos.FORMATO_FECHA);} catch (Exception e) {}
			try { this.fecFinVig = (Date) FormateadorDatos.parseaDato(salida.get("parRecFecFinVig").toString(),FormateadorDatos.FORMATO_FECHA);} catch (Exception e) {}
			
		} catch (Exception e) {
			
		} 
		
		return this;
	}
	
	public ArrayList<ParametroRecurso> listadoParamRecurso() {
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.idRecurso));
		
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> recursos = consulta.ejecutaSQL("cConsultaParmRecurso", listaParms, this);
		
		Iterator<Cargable> itRecurso = recursos.iterator();
		ArrayList<ParametroRecurso> salida = new ArrayList<ParametroRecurso>();
		
		while (itRecurso.hasNext()) {
			ParametroRecurso p = (ParametroRecurso) itRecurso.next();
			salida.add(p);
		}
		
        return salida;
	}
	
	public void bajaRecurso(Recurso r) {
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, r.id));
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("dDelParamRecurso", listaParms, this);
	}
}
