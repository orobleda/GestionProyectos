package model.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class Coste implements Cargable{
	public int id;
	public String descripcion;
	public int version=1;
	public Sistema sistema;
	public Presupuesto presupuesto;
	public int idPresupuesto;
	
	public HashMap<String,Concepto> conceptosCoste = null;
	
	public Coste(){
		conceptosCoste = new HashMap<String,Concepto>();
	}
	
	public Coste clone(){
		Coste c = new Coste();
		c.descripcion = this.descripcion;
		c.id = this.id;
		c.idPresupuesto = this.idPresupuesto;
		c.sistema = this.sistema;
		c.version = this.version;
		
		Iterator<Concepto> itConcepto = conceptosCoste.values().iterator();
		
		while (itConcepto.hasNext()) {
			Concepto cAux = itConcepto.next();
			Concepto cClonado = cAux.clone();
			conceptosCoste.put(cClonado.tipoConcepto.codigo, cClonado);
		}
				
		return c;		
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
			if (salida.get("costId")==null)  this.id = 0; else this.id = (Integer) salida.get("costId");
			if (salida.get("costDesc")==null)  this.descripcion = ""; else this.descripcion = (String) salida.get("costDesc");
			if (salida.get("costVersion")==null)  this.version = 0; else this.version = (Integer) salida.get("costVersion");
			if (salida.get("costSistema")==null)  this.sistema = null; else this.sistema = Sistema.listado.get((Integer)salida.get("costSistema"));
			if (salida.get("costPres")==null)  this.idPresupuesto = 0; else this.idPresupuesto = (Integer) salida.get("costPres");
			
		} catch (Exception e) {
			
		}
		
		return this;
	}
	
	public int maxIdCoste() {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> costes = consulta.ejecutaSQL("cMaxIdCoste", null, this);
		
		Coste p = (Coste) costes.get(0);
        return p.id+1;
	}
	
	public HashMap<Integer, Coste> recuperaCostes(Presupuesto p) {
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,p.id ));
		
		ArrayList<Cargable> costes = consulta.ejecutaSQL("cCostes", listaParms, this);
		
		HashMap<Integer, Coste> salida = new HashMap<Integer, Coste>();
		Iterator<Cargable> itCostes = costes.iterator();
		while (itCostes.hasNext()) {
			Coste c = (Coste) itCostes.next();
			c.presupuesto = p;
			salida.put(c.sistema.id, c);
			Concepto cpt = new Concepto(c);
			c.conceptosCoste = cpt.buscaConceptos(c.id);
		}
		
		return salida;
	}
	
	public Coste recuperaCoste(int idCoste) {
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,idCoste));
		
		ArrayList<Cargable> costes = consulta.ejecutaSQL("cCostes", listaParms, this);
		
		if (costes.size()==0) {
			return null;
		} else {
			Concepto c = new Concepto();
			Coste coste = (Coste) costes.get(0);
			coste.conceptosCoste = c.buscaConceptos(coste.id);
			return coste;
		}
	}
	
	public void borrarCoste(String idTransaccion) {		
		
		Iterator<Concepto> itConcepto = this.conceptosCoste.values().iterator();
		
		while (itConcepto.hasNext()) {
			Concepto cnc = itConcepto.next();
			cnc.borrarConcepto(idTransaccion);
		}
		
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.id));
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("dDelCoste", listaParms, this, idTransaccion);		
	}
	
	public void calculaConceptos() {
		Iterator<Concepto> it = this.conceptosCoste.values().iterator();
		
		while (it.hasNext()) {
			Concepto c = it.next();
			
			if (c.baseCalculo==null)  {
				c.valorEstimado = 0;
			} else 			
				if (c.baseCalculo.id != BaseCalculoConcepto.CALCULO_BASE_PORC && c.tipoConcepto.id != MetaConcepto.ID_TOTAL) {
					c.calculaCantidadEstimada(this.conceptosCoste);
				}
		}
		
		it = this.conceptosCoste.values().iterator();
		
		while (it.hasNext()) {
			Concepto c = it.next();
			
			if (c.baseCalculo==null)  {
				c.valorEstimado = 0;
			} else 
				if (c.baseCalculo.id == BaseCalculoConcepto.CALCULO_BASE_PORC && c.tipoConcepto.id != MetaConcepto.ID_TOTAL) {
					c.calculaCantidadEstimada(this.conceptosCoste);
				}
		}
		
		it = this.conceptosCoste.values().iterator();
		
		while (it.hasNext()) {
			Concepto c = it.next();
			
			if (c.tipoConcepto.id == MetaConcepto.ID_TOTAL) {
				c.calculaCantidadEstimada(this.conceptosCoste);
			}
		}
	}
	
	public void guardarCoste(boolean actualiza, String idTransaccion) throws Exception{
		try {
			ConsultaBD consulta = new ConsultaBD();
			boolean inserta = !actualiza;
			
			Iterator<Concepto> itConceptos = this.conceptosCoste.values().iterator();
			boolean todosCero = true;
			while (itConceptos.hasNext()){
				Concepto cpt = itConceptos.next();
				if (cpt.valorEstimado!=0) {
					todosCero = false;
					break;
				}
			}
			
			if (todosCero) {
				this.borrarCoste(idTransaccion);
				return;
			}
			
			if (actualiza) {							
			    Coste c = this.recuperaCoste(this.id);
			    if (c==null) {
			    	inserta = true;
			    }							
			} 
			
			if (inserta) {
				this.id = this.maxIdCoste();
				
				consulta = new ConsultaBD();
				List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
				listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_ID,this.id));
				listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,this.sistema.id));
				listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_INT,this.presupuesto.id));
				
				consulta.ejecutaSQL("iAltaCoste", listaParms, this, idTransaccion);
			}
			
			Iterator<Concepto> itConcepto = this.conceptosCoste.values().iterator();
			
			while (itConcepto.hasNext()) {
				Concepto c = (Concepto) itConcepto.next();
				c.coste = this;
				c.guardarConcepto(actualiza, idTransaccion);
			}
			
		} catch (Exception e){
			e.printStackTrace();
			throw e;
		}
		
	}
	
	public Coste operarCostes(Coste costeOperar, int operacion) {
		if (this.conceptosCoste == null) {
			this.conceptosCoste = new HashMap<String, Concepto>();
		}
		
		HashMap<String, String> conceptosProcesados = new HashMap<String, String>();
		
		Iterator<Concepto> itConcepto = this.conceptosCoste.values().iterator(); 
		while (itConcepto.hasNext()) {
			Concepto c = itConcepto.next();
			
			Iterator<Concepto> itConceptoOperar = costeOperar.conceptosCoste.values().iterator();
			while (itConceptoOperar.hasNext()) {
				Concepto cAux = itConceptoOperar.next();
				
				if (cAux.tipoConcepto.codigo.equals(c.tipoConcepto.codigo)) {
					conceptosProcesados.put(c.tipoConcepto.codigo,c.tipoConcepto.codigo);
					
					c.valor = c.valor + operacion*cAux.valor;
					c.valorEstimado = c.valorEstimado + operacion*cAux.valorEstimado;
				}
			}
		}
		
		itConcepto = costeOperar.conceptosCoste.values().iterator();
		while (itConcepto.hasNext()) {
			Concepto c = itConcepto.next();
			
			if (!conceptosProcesados.containsKey(c.tipoConcepto.codigo)) {
				if (operacion == Presupuesto.SUMAR)
					this.conceptosCoste.put(c.tipoConcepto.codigo, c);
			}
		}
		
		return this;
	}
	
}
