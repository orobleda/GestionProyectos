package model.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import model.metadatos.TipoPresupuesto;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class Presupuesto implements Cargable {
	public int id = 0;
	public int version =0;
	public int idProyecto = 0;
	public int idApunteContable = 0;
	public Date fxAlta = null;
	public Proyecto p = null;
	public TipoPresupuesto tipo = null;
	public String descripcion = null;
	public HashMap<Integer, Coste> costes = new HashMap<Integer, Coste>();
	public HashMap<Integer, Coste> costesTotal = new HashMap<Integer, Coste>();
	public boolean actualiza = false;
	public boolean enCurso = false;
	
	public boolean calculado = false;
	
	public static final int SUMAR = 1;
	public static final int RESTAR = -1;
	
	public void calculaTotales() {
		Coste costeTotal = (Coste) costesTotal.values().toArray()[0];
		Iterator<Concepto> itConceptoAux = costeTotal.conceptosCoste.values().iterator();
		
		while(itConceptoAux.hasNext()) {
			Concepto con = itConceptoAux.next();
			con.valorEstimado = 0;
		}
		
		Iterator<Coste> itCoste = costes.values().iterator();
		
		while (itCoste.hasNext()) {
			Coste c = itCoste.next();
			
			Iterator<Concepto> itConcepto = c.conceptosCoste.values().iterator();
			
			while(itConcepto.hasNext()) {
				Concepto con = itConcepto.next();
				Concepto conTotal = costeTotal.conceptosCoste.get(con.tipoConcepto.codigo);
				
				conTotal.valorEstimado += con.valorEstimado;
			}
		}
	}
	
	public float calculaTotal() {
		if (costes==null || costes.size()==0)
			this.cargaCostes();
		
		float total = 0;
		Iterator<Coste> itCoste = this.costes.values().iterator();
		
		while (itCoste.hasNext()) {
			Coste cost = itCoste.next();
			
			Iterator<Concepto> itConceptos = cost.conceptosCoste.values().iterator();
			while (itConceptos.hasNext()) {
				Concepto conc = itConceptos.next();
				total += conc.valorEstimado;
			}
		}
		
		return total;
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
			if (salida.get("presId")==null) {        this.id = 0;}           else this.id = (Integer) salida.get("presId");			
			if (salida.get("vsPresupuesto")==null) { this.version = 0;	}    else this.version = (Integer) salida.get("vsPresupuesto");
			if (salida.get("presProy")==null) {      this.idProyecto = 0;}   else this.idProyecto = (Integer) salida.get("presProy");
			if (salida.get("presDesc")==null) {      this.descripcion = "";} else this.descripcion = (String) salida.get("presDesc");
			if (salida.get("presFxAlta")==null) {    this.fxAlta = null;   } else this.fxAlta =  (Date) FormateadorDatos.parseaDato((String) salida.get("presFxAlta"),FormateadorDatos.FORMATO_FECHA);
			if (salida.get("presTipoP")==null) {     this.tipo = null;     } else this.tipo = TipoPresupuesto.listado.get((Integer) salida.get("presTipoP"));
			if (salida.get("presApCont")==null) {     this.idApunteContable = -1;     } else this.idApunteContable = (Integer) salida.get("presApCont");
		} catch (Exception e) {
			
		}
		
		return this;
	}
	
	public int maxIdPresupuesto() {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> presupuestos = consulta.ejecutaSQL("cMaxIdPresupuesto", null, this);
		
		Presupuesto p = (Presupuesto) presupuestos.get(0);
        return p.id+1;
	}
	
	public int consultaMaxVsPresupuesto() {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> presupuestos = consulta.ejecutaSQL("cConsultaMaxVsProy", null, this);
		
		Presupuesto p = (Presupuesto) presupuestos.get(0);
        return p.version+1;
	}
	
	public void cargaCostes(){
		Coste c = new Coste();
		this.costes = c.recuperaCostes(this);
	}
	
	public void borrarPresupuesto(String idTransaccion) throws Exception {
		
		boolean cierraTransaccion = false;
		
		if (idTransaccion == null) {
			idTransaccion = "borrarPresupuesto" + new Date().getTime();
			cierraTransaccion = true;
		}
		
		Iterator<Coste> itCoste = this.costes.values().iterator();
		
		while (itCoste.hasNext()) {
			Coste cst = itCoste.next();
			cst.borrarCoste(idTransaccion);
		}
		
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.id));
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("dDelPresupuesto", listaParms, this, idTransaccion);	
		
		if (cierraTransaccion)
			consulta.ejecutaTransaccion(idTransaccion);
	}
	
	public Concepto getCosteConcepto(Sistema s, MetaConcepto mc){
		Concepto conSal = null;
		
		Iterator<Coste> itCoste = this.costes.values().iterator();
		
		while (itCoste.hasNext()) {
			Coste c = itCoste.next();
			
			if (c.sistema.codigo.equals(s.codigo)) {
				return c.conceptosCoste.get(mc.codigo);
			}
		}
				
		return conSal;
	}
	
	public void guardarPresupuesto(boolean actualiza, String idTransaccion) throws Exception{
		try {
			boolean ejecutaTransaccion = false;
			
			if (idTransaccion == null) {
				idTransaccion = "guardarPresupuesto" + new Date().getTime();
				ejecutaTransaccion = true;
			};
							
			ConsultaBD consulta = new ConsultaBD();
			
			if (actualiza) {							
			    consulta = new ConsultaBD();
				List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
				listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.version));
				listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,this.tipo.id));
				listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_STR,this.descripcion));
				listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_INT,this.id));
				
				consulta.ejecutaSQL("uActualizaPresupuesto", listaParms, this, idTransaccion);			
			} else {
				this.fxAlta = new Date();
				this.version = this.version + 1;
				
				consulta = new ConsultaBD();
				List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
				listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_ID,1));
				listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,this.version));
				listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_STR,FormateadorDatos.formateaDato(this.fxAlta,FormateadorDatos.FORMATO_FECHA)));
				
				if (this.p.apunteContable) {
					listaParms.add(new ParametroBD(7,ConstantesBD.PARAMBD_INT,this.p.id));
					listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_INT,-1));
				} else {
					listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_INT,this.p.id));
					listaParms.add(new ParametroBD(7,ConstantesBD.PARAMBD_INT,-1));
				}
				
				listaParms.add(new ParametroBD(5,ConstantesBD.PARAMBD_INT,this.tipo.id));
				listaParms.add(new ParametroBD(6,ConstantesBD.PARAMBD_STR,this.descripcion));
				
				consulta.ejecutaSQL("iAltaPresupuesto", listaParms, this, idTransaccion);
				
				this.id = ParametroBD.ultimoId;
			}
			
			Iterator<Coste> itCoste = this.costes.values().iterator();
			
			while (itCoste.hasNext()) {
				Coste c = (Coste) itCoste.next();
				c.presupuesto = this;
				c.guardarCoste(actualiza, idTransaccion);
			}
			
			if (ejecutaTransaccion)
				consulta.ejecutaTransaccion(idTransaccion);
			
		} catch (Exception e){
			e.printStackTrace();
			throw e;
		}
		
	}
	
	public Presupuesto dameUltimaVersionPresupuesto(Proyecto p) {
		Presupuesto prep = new Presupuesto();
        ArrayList<Presupuesto> presupuestos = null;
        
        if (p.apunteContable) {
        	prep.idApunteContable = p.id;
        	presupuestos = prep.buscaPresupuestosAPunteContable();
        }
        	
        else 
        	presupuestos = prep.buscaPresupuestos(p.id);
        
        Presupuesto salida = null;
        
        if (presupuestos.size()>0) {
        	salida = presupuestos.get(0);
        } else return null;
        
        Iterator<Presupuesto> itPresupuestos = presupuestos.iterator();
        while (itPresupuestos.hasNext()) {
        	Presupuesto pAux = itPresupuestos.next();
        	
        	if (pAux.version > salida.version) {
        		salida = pAux;
        	}
        }
        
        return salida;
	}
	
	public ArrayList<Presupuesto> buscaPresupuestosAPunteContable() {
		ConsultaBD consulta = new ConsultaBD();
		
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_INT,this.idApunteContable));
			
		
		ArrayList<Cargable> presupuestos = consulta.ejecutaSQL("cConsultaPresupuesto", listaParms, this);
		
		Iterator<Cargable> itCargable = presupuestos.iterator();
		ArrayList<Presupuesto> salida = new ArrayList<Presupuesto> ();
		
		while (itCargable.hasNext()) {
			Presupuesto prep = (Presupuesto) itCargable.next();
			salida.add(prep);
		}
		
		return salida;
}
	
	public ArrayList<Presupuesto> buscaPresupuestos(int idProyecto) {
			ConsultaBD consulta = new ConsultaBD();
			
			List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
				listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,idProyecto));
				
			
			ArrayList<Cargable> presupuestos = consulta.ejecutaSQL("cConsultaPresupuesto", listaParms, this);
			
			Iterator<Cargable> itCargable = presupuestos.iterator();
			ArrayList<Presupuesto> salida = new ArrayList<Presupuesto> ();
			
			while (itCargable.hasNext()) {
				Presupuesto prep = (Presupuesto) itCargable.next();
				salida.add(prep);
			}
			
			return salida;
	}
	
	public Presupuesto buscaPresupuestos(int idProyecto, int version) {
		ConsultaBD consulta = new ConsultaBD();
		
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,idProyecto));
		
		ArrayList<Cargable> presupuestos = consulta.ejecutaSQL("cConsultaPresupuesto", listaParms, this);
		
		Iterator<Cargable> itCargable = presupuestos.iterator();
		
		while (itCargable.hasNext()) {
			Presupuesto prep = (Presupuesto) itCargable.next();
			
			if (prep.version == version) {
				prep.cargaCostes();
				return prep;
			}
			
		}
		
		return null;
}
	
	public Presupuesto operarPresupuestos(Presupuesto presOperar, int operacion) {
		if (this.costes == null) {
			this.costes = new HashMap<Integer, Coste>();
		}
		
		HashMap<String, String> sistemasProcesados = new HashMap<String, String>();
		
		Iterator<Coste> itCostes = this.costes.values().iterator(); 
		while (itCostes.hasNext()) {
			Coste c = itCostes.next();
			
			Iterator<Coste> itCosteOperar = presOperar.costes.values().iterator();
			while (itCosteOperar.hasNext()) {
				Coste cAux = itCosteOperar.next();
				
				if (cAux.sistema.codigo.equals(c.sistema.codigo)) {
					sistemasProcesados.put(c.sistema.codigo,c.sistema.codigo);
					c = c.operarCostes(cAux, operacion);
				}
			}
		}
		
		itCostes = presOperar.costes.values().iterator();
		while (itCostes.hasNext()) {
			Coste c = itCostes.next();
			
			if (!sistemasProcesados.containsKey(c.sistema.codigo)) {
				if (operacion == Presupuesto.SUMAR)
					this.costes.put(c.id, c.clone());
			}
		}
		
		return this;
	}
	
	public Presupuesto clone() {
		Presupuesto pres = new Presupuesto();
		pres.costes = new HashMap<Integer, Coste>();
		pres.descripcion = this.descripcion;
		pres.fxAlta = this.fxAlta;
		pres.id = this.id;
		pres.idProyecto = this.idProyecto;
		pres.p = this.p;
		pres.tipo = this.tipo;
		pres.version = this.version;
		
		Iterator<Coste> itCoste = this.costes.values().iterator();
		
		while (itCoste.hasNext()) {
			Coste cAux = itCoste.next();
			Coste cClonado = cAux.clone();
			cClonado.presupuesto = pres;
			pres.costes.put(cAux.id, cClonado);			
		}
		
		return pres;
	}
	
	public Presupuesto cloneSinCostes() {
		Presupuesto pres = new Presupuesto();
		pres.costes = new HashMap<Integer, Coste>();
		pres.descripcion = this.descripcion;
		pres.fxAlta = this.fxAlta;
		pres.id = this.id;
		pres.idProyecto = this.idProyecto;
		pres.p = this.p;
		pres.tipo = this.tipo;
		pres.version = this.version;
		
		return pres;
	}
		
	public String toString() {
		if (!calculado) {
			if (enCurso)
				return this.descripcion + " - Versión en curso";
			else
				return this.descripcion + " - Versión " + this.version;
		}
		else 
			return this.descripcion;
	}
}
