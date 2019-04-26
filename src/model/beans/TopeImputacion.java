package model.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class TopeImputacion implements Cargable{
	public int id = 0;
	public Proyecto proyecto = null;
	public Sistema sistema = null;
	public MetaConcepto mConcepto = null;
	public int anio = 0;
	public float cantidad = 0;
	public float porcentaje = 0;
	public boolean resto = false;
	public int version = 0;
	public String txtVersion = "";
	public float cantidadTrasRepartir = 0;
	
	public static int A_RESTO = 1;
	
	public ArrayList<TopeImputacion> topes = null;
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
				
		try{
			if (salida.get("tpId")==null)  this.id = 0; else this.id = (Integer) salida.get("tpId");
			if (salida.get("tpIdConcepto")==null)  this.mConcepto = null; else this.mConcepto = MetaConcepto.porId((Integer) salida.get("tpIdConcepto"));
			if (salida.get("tpAnio")==null)  this.anio = 0; else this.anio = (Integer) salida.get("tpAnio");
			try { if (salida.get("tpCantidad")==null)     { this.cantidad = 0;            } else this.cantidad = ((Double) salida.get("tpCantidad")).floatValue();    } catch (Exception e){System.out.println();}
			try { if (salida.get("tpPorcentaje")==null)     { this.porcentaje = 0;            } else this.porcentaje = ((Double) salida.get("tpPorcentaje")).floatValue();    } catch (Exception e){System.out.println();}
			if (salida.get("tpResto")==null)  this.resto = false; else this.resto = (Integer) salida.get("tpResto")==TopeImputacion.A_RESTO?true:false;
			if (salida.get("tpVersion")==null)  this.version = 0; else this.version = (Integer) salida.get("tpVersion");
			if (salida.get("tpSistema")==null)  this.sistema = null; else this.sistema = Sistema.listado.get((Integer) salida.get("tpSistema"));
			if (salida.get("tpDsVersion")==null)  this.txtVersion = ""; else this.txtVersion = (String) salida.get("tpDsVersion");
					
		} catch (Exception e) {
			
		}
		
		return this;
	}
	
	public ArrayList<TopeImputacion> dameTopes(Sistema s, Concepto c) {
		
		ArrayList<TopeImputacion> listaTopes = new ArrayList<TopeImputacion>();
		
		if (this.topes!=null){
			Iterator<TopeImputacion> itTope = this.topes.iterator();
			
			while (itTope.hasNext()) {
				TopeImputacion tpAux = itTope.next();
				if (tpAux.sistema.codigo.equals(s.codigo) && tpAux.mConcepto.codigo.equals(c.tipoConcepto.codigo)) {
					listaTopes.add(tpAux);
				}
			}
		} else return null;		
		
		return listaTopes;
	}
	
	public TopeImputacion dameTope(Sistema s, Concepto c,  int anio) {
		if (this.topes!=null){
			Iterator<TopeImputacion> itTope = this.topes.iterator();
			
			while (itTope.hasNext()) {
				TopeImputacion tpAux = itTope.next();
				if (tpAux.anio == anio && tpAux.sistema.codigo.equals(s.codigo) && tpAux.mConcepto.codigo.equals(c.tipoConcepto.codigo)) {
					return tpAux;
				}
			}
		} else return null;		
		
		TopeImputacion tpAux = new TopeImputacion();
		tpAux.anio = anio;
		tpAux.cantidad = -1;
		tpAux.mConcepto = c.tipoConcepto;
		tpAux.sistema = (Sistema) s.clone();
		tpAux.resto = true;
		tpAux.version = -1;
		
		return tpAux;
	}
	
	public ArrayList<TopeImputacion> listadoTopes(Proyecto p) {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> proyectos = consulta.ejecutaSQL("cListaTopes", null, this);
		
		Iterator<Cargable> itProyecto = proyectos.iterator();
		ArrayList<TopeImputacion> salida = new ArrayList<TopeImputacion>();
		
		while (itProyecto.hasNext()) {
			TopeImputacion tp = (TopeImputacion) itProyecto.next();
			tp.proyecto = p;
			salida.add(tp);
		}
		
		topes = salida;
				
        return salida;
	}
	
	public void deleteTopes(String idTransaccion)  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.proyecto.id));
		
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("dBorraTopes", listaParms, this,idTransaccion);
	}
	
	public void insertTope(String idTransaccion)  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_ID, 0));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.proyecto.id));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, this.mConcepto.id));
		listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_INT, this.anio));
		listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_REAL, this.cantidad));
		listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_REAL, this.porcentaje));
		listaParms.add(new ParametroBD(7, ConstantesBD.PARAMBD_INT, this.resto?TopeImputacion.A_RESTO:0));
		listaParms.add(new ParametroBD(8, ConstantesBD.PARAMBD_INT, 1));
		listaParms.add(new ParametroBD(9, ConstantesBD.PARAMBD_STR, ""));
		listaParms.add(new ParametroBD(10, ConstantesBD.PARAMBD_INT, this.sistema.id));
		
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("iAltaTopeimputacion", listaParms, this,idTransaccion);
	}
}
