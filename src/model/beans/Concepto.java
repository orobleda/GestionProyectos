package model.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import controller.Log;
import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class Concepto implements Cargable, Comparable<Concepto> {
	
	public int id=0;
	public float valor;
	public float valorEstimado;
	public float horas;
	public float horasEstimado;
	public int porcentaje;
	public int esPorcentaje;
	public MetaConcepto respectoPorcentaje;
	public Coste coste;
	public MetaConcepto tipoConcepto;
	public Tarifa tarifa;
	public BaseCalculoConcepto baseCalculo;
	public int idCoste;
	public float porc_trei_ggp = 0;
	
	public Recurso r;
	public Sistema s; 
	
	public ArrayList<EstimacionAnio> topeEstimacion = null;
	
	public ArrayList<Estimacion> listaEstimaciones = null;
	public ArrayList<Imputacion> listaImputaciones = null;
	public ArrayList<CertificacionFaseParcial> listaCertificaciones = null;
	public TopeImputacion topeImputacion = null;
	
	public Concepto () {
		valor = 0;
		valorEstimado = 0;
	}
	
	public Concepto (Coste c) {
		coste = c;
	}
	
	public Concepto (MetaConcepto c) {
		id = 0;
		valor = 0;
		valorEstimado = 0;
		horas = 0;
		horasEstimado = 0;
		porcentaje = 0;
		esPorcentaje = 0;
		respectoPorcentaje = null;
		tipoConcepto = c;
		coste = c.costeAuxiliar;
	}
	
	public HashMap<Integer,Concepto> crearListaConceptos(HashMap<Integer,MetaConcepto> metaconceptos)  {
		HashMap<Integer,Concepto> salida = new HashMap<Integer,Concepto>();
		
		Iterator<MetaConcepto> itMetaCon = metaconceptos.values().iterator();
		while(itMetaCon.hasNext()) {
			MetaConcepto mtc = itMetaCon.next();
			Concepto c = new Concepto(mtc);
			salida.put(c.tipoConcepto.id, c);
		}
		
		return salida;
	}
	
	public void recalculaConceptos(HashMap<String,Concepto> conceptosOrigen) {
		Iterator<Concepto> itConceptos = conceptosOrigen.values().iterator();
		
		while (itConceptos.hasNext()) {
			Concepto c = itConceptos.next();
			if (c.tipoConcepto.id != MetaConcepto.ID_TOTAL)
				c.calculaCantidadEstimada(conceptosOrigen);
		}
		
		Concepto c = conceptosOrigen.get(MetaConcepto.ID_TOTAL);
		c.calculaCantidadEstimada(conceptosOrigen);
	}
	
	public void calculaCantidadEstimada(HashMap<String,Concepto> conceptosOrigen) {
		if (this.baseCalculo.id == BaseCalculoConcepto.CALCULO_BASE_COSTE) {
			if (this.tipoConcepto.id == MetaConcepto.ID_TOTAL) {
				float sumaEstimada = 0;
				
				Iterator<Concepto> itConceptos = conceptosOrigen.values().iterator();
				
				while (itConceptos.hasNext()) {
					Concepto c = itConceptos.next();
					if (c.tipoConcepto.id != MetaConcepto.ID_TOTAL) {
						sumaEstimada += c.valorEstimado;
					}	
				}
				
				this.valorEstimado = sumaEstimada;
			} else this.valorEstimado = this.valor;
		}
		
		if (this.baseCalculo.id == BaseCalculoConcepto.CALCULO_BASE_HORAS) {
				this.valorEstimado = this.horas*this.tarifa.costeHora;
		}
		
		if (this.baseCalculo.id == BaseCalculoConcepto.CALCULO_BASE_PORC) {
			if (this.respectoPorcentaje== null || this.respectoPorcentaje.id == MetaConcepto.ID_TOTAL) {
				float sumaEstimada = 0;
				
				Iterator<Concepto> itConceptos = conceptosOrigen.values().iterator();
				
				while (itConceptos.hasNext()) {
					Concepto c = itConceptos.next();
					if (c.tipoConcepto.id != MetaConcepto.ID_TOTAL && c!=this) {
						if (c.respectoPorcentaje==null || c.respectoPorcentaje.id != MetaConcepto.ID_TOTAL) {
							sumaEstimada += c.valorEstimado;
						}
					}	
				}
				
				this.valorEstimado = sumaEstimada * this.porcentaje/(100-this.porcentaje);
			} else {
				Concepto c = conceptosOrigen.get(this.respectoPorcentaje.codigo);
				this.valorEstimado = c.valorEstimado * this.porcentaje/100;				
			}
		}
		
		
	}
	
	public void guardarConcepto(boolean actualiza, String idTransaccion) throws Exception{
		if (this.tipoConcepto.id == MetaConcepto.ID_TOTAL) return;
		if (this.baseCalculo == null) return;
		
		try {
			ConsultaBD consulta = new ConsultaBD();
			List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
			listaParms.add(new ParametroBD(9,ConstantesBD.PARAMBD_INT,this.coste.id));
			listaParms.add(new ParametroBD(12,ConstantesBD.PARAMBD_INT,this.baseCalculo.id));
			listaParms.add(new ParametroBD(7,ConstantesBD.PARAMBD_INT,1));
			listaParms.add(new ParametroBD(10,ConstantesBD.PARAMBD_INT,this.tipoConcepto.id));
			listaParms.add(new ParametroBD(3 ,ConstantesBD.PARAMBD_REAL,this.valorEstimado));
			listaParms.add(new ParametroBD(5 ,ConstantesBD.PARAMBD_REAL,this.horasEstimado));
			
			if (this.baseCalculo.id == BaseCalculoConcepto.CALCULO_BASE_COSTE) {
				listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_REAL,this.valor));
				listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_REAL,0));
				listaParms.add(new ParametroBD(6,ConstantesBD.PARAMBD_INT,0));
				listaParms.add(new ParametroBD(8,ConstantesBD.PARAMBD_INT,0));
				listaParms.add(new ParametroBD(11,ConstantesBD.PARAMBD_INT,0));
			}
			if (this.baseCalculo.id == BaseCalculoConcepto.CALCULO_BASE_HORAS) {
				listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_REAL,0));
				listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_REAL,this.horas));
				listaParms.add(new ParametroBD(6,ConstantesBD.PARAMBD_INT,0));
				listaParms.add(new ParametroBD(8,ConstantesBD.PARAMBD_INT,0));
				listaParms.add(new ParametroBD(11,ConstantesBD.PARAMBD_INT,this.tarifa.idTarifa));
			}
			if (this.baseCalculo.id == BaseCalculoConcepto.CALCULO_BASE_PORC) {
				listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_REAL,0));
				listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_REAL,0));
				listaParms.add(new ParametroBD(6,ConstantesBD.PARAMBD_INT,this.porcentaje));
				if (this.respectoPorcentaje!=null)
					listaParms.add(new ParametroBD(8,ConstantesBD.PARAMBD_INT,this.respectoPorcentaje.id));
				else 
					listaParms.add(new ParametroBD(8,ConstantesBD.PARAMBD_INT,-1));
				listaParms.add(new ParametroBD(11,ConstantesBD.PARAMBD_INT,0));
			}			
			
			if (actualiza) {
				Concepto c = this.buscaConcepto(this.id);
				
				if (c==null) {
					listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_ID,this.id));							
					consulta.ejecutaSQL("iAltaConcepto", listaParms, this, idTransaccion);
					this.id = ParametroBD.ultimoId;
				} else {
					listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.id));							
					consulta.ejecutaSQL("uActualizaConcepto", listaParms, this, idTransaccion);
				}
						
			} else {
				
				listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_ID,this.id));							
				consulta.ejecutaSQL("iAltaConcepto", listaParms, this, idTransaccion);
				this.id = ParametroBD.ultimoId;
			}
			
		} catch (Exception e){
			Log.e(e);
			throw e;
		}
		
	}
	
	public Concepto clone() {
		Concepto c = new Concepto();
		c.id = this.id;
		c.valor = this.valor;
		c.valorEstimado = this.valorEstimado;
		c.horas = this.horas;
		c.horasEstimado = this.horasEstimado;
		c.porcentaje = this.porcentaje;
		c.esPorcentaje = this.esPorcentaje;
		c.respectoPorcentaje = this.respectoPorcentaje;
		c.tipoConcepto = this.tipoConcepto;
		c.coste = this.coste;
		c.tarifa = this.tarifa;
		c.baseCalculo = this.baseCalculo;
		c.s = this.s;		
		
		return c;
	}
	
	public Concepto cloneConListas() {
		Concepto c = this.clone();
		
		c.listaEstimaciones = new ArrayList<Estimacion>();
		c.listaImputaciones = new ArrayList<Imputacion>();
		
		c.listaEstimaciones.addAll(this.listaEstimaciones);
		c.listaImputaciones.addAll(this.listaImputaciones);
		
		if (this.topeImputacion!=null)
			c.topeImputacion = this.topeImputacion.clone();
		
		return c;
	}

	public int maxIdConcepto() {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> conceptos = consulta.ejecutaSQL("cMaxIdConcepto", null, this);
		
		Concepto conc = (Concepto) conceptos.get(0);
        return conc.id+1;
	}
	
	public void borrarConcepto(String idTransaccion) {			
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.id));
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("dDelConcepto", listaParms, this, idTransaccion);		
	}
	
	public HashMap<String,Concepto> buscaConceptos(int idCoste) {			
		ConsultaBD consulta = new ConsultaBD();

		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,idCoste));
		
		ArrayList<Cargable> conceptos = consulta.ejecutaSQL("cConsultaConcepto", listaParms, this);
		
		HashMap<String,Concepto> salida = new HashMap<String,Concepto>();
		
		Iterator<Cargable> itCOnceptos = conceptos.iterator();
		
		while (itCOnceptos.hasNext()) {
			Concepto con = (Concepto) itCOnceptos.next();
			con.coste = this.coste;
			salida.put(con.tipoConcepto.codigo, con);
		}
		
		return salida;
	}
	
	public Concepto buscaConcepto(int idConcepto) {			
		ConsultaBD consulta = new ConsultaBD();

		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,idConcepto));
		
		ArrayList<Cargable> conceptos = consulta.ejecutaSQL("cConsultaConcepto", listaParms, this);
		
		if (conceptos.size() == 0) return null;
		else return (Concepto) conceptos.get(0);
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
			if (salida.get("concId")==null)  this.id = 0; else this.id = (Integer) salida.get("concId");
			if (salida.get("concValor")==null)  this.valor = 0; else this.valor = new Float((Double) salida.get("concValor"));
			if (salida.get("concValorEstimado")==null)  this.valorEstimado = 0; else this.valorEstimado = new Float((Double) salida.get("concValorEstimado"));
			if (salida.get("concHoras")==null)  this.horas = 0; else this.horas = new Float((Double) salida.get("concHoras"));
			if (salida.get("concHorasEstimado")==null)  this.horasEstimado = 0; else this.horasEstimado = new Float((Double) salida.get("concHorasEstimado"));
			if (salida.get("concPorcentaje")==null)  this.porcentaje = 0; else this.porcentaje = (Integer) salida.get("concPorcentaje");
			if (salida.get("concEsPorcentaje")==null)  this.esPorcentaje = 0; else this.esPorcentaje = (Integer) salida.get("concEsPorcentaje");
			if (salida.get("concRespectoPorcentaje")==null)
				this.respectoPorcentaje = null; 
			else {
				try {
					this.respectoPorcentaje = MetaConcepto.porId((Integer) salida.get("concRespectoPorcentaje"));
				} catch (Exception e){
					this.respectoPorcentaje = null; 
				}
			}
			if (salida.get("concIdCoste")==null)  this.idCoste = 0; else this.idCoste = (Integer) salida.get("concIdCoste");
			if (salida.get("concTipoConcepto")==null)
				this.tipoConcepto = null; 
			else {
				try {
					this.tipoConcepto = MetaConcepto.porId((Integer) salida.get("concTipoConcepto"));
				} catch (Exception e){
					this.tipoConcepto = null; 
				}
			}
			if (salida.get("concIdTarifa")==null)
				this.tarifa = null; 
			else {
				try {
					this.tarifa = Tarifa.porId((Integer) salida.get("concIdTarifa"));
				} catch (Exception e){
					this.tarifa = null; 
				}
			}
			if (salida.get("concBaseCalculo")==null) this.baseCalculo = null; else { this.baseCalculo = new BaseCalculoConcepto((Integer) salida.get("concBaseCalculo"));	}
		} catch (Exception e) {
			
		}
		
		return this;
	}

	@Override
	public int compareTo(Concepto arg0) {
		MetaConcepto mc = arg0.tipoConcepto;
		return this.tipoConcepto.compareTo(mc);
	}
	
	
	
	
}
