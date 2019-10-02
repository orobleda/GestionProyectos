package model.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.metadatos.MetaParametro;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class Proveedor implements Cargable, Loadable {

	public int id =0;
	public String descripcion = "";
	public String nomCorto = "";
		
	public static HashMap<String, Proveedor> listado = null;
	public static HashMap<Integer, Proveedor> listadoId = null;	

	public HashMap<String,? extends Parametro> listadoParametros = null;
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		this.id = (Integer) salida.get("provId");
		this.descripcion = (String) salida.get("provDesc");
		this.nomCorto = (String) salida.get("provNomCorto");
		
		return this;
	}

	@Override
	public void load() {
		listado = new HashMap<String, Proveedor>();	
		listadoId = new HashMap<Integer, Proveedor>();	
			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> proveedores = consulta.ejecutaSQL("cConsultaProveedores", null, this);
		
		Iterator<Cargable> it = proveedores.iterator();
		while (it.hasNext()){
			Proveedor est = (Proveedor) it.next();
			listado.put(est.nomCorto, est);
			listadoId.put(est.id, est);
		}
	}
	
	public Parametro getParametro(String codParametro) throws Exception {
		if (this.listadoParametros == null) {
			this.cargaProveedor();
		} 
		return this.listadoParametros.get(codParametro);
	}
	
	public boolean identificaPorPPM(String valor){
		try {
			String valorParam = (String) this.getParametro(MetaParametro.PROVEEDOR_CODPPM).getValor();
			
			String [] cortada = valorParam.split(";");
			for (int i=0;i<cortada.length;i++) {
				String codigo = cortada[i];
				if (codigo.trim().toUpperCase().equals(valor.trim().toUpperCase())) {
					return true;
				}
			}
			
			return false;
			
		} catch (Exception e) {
			return false;
		}
		
	}
	
	public Proveedor getProveedorPPM(String codPPM) throws Exception {
		Iterator<Proveedor> itProv = listado.values().iterator();
		
		while (itProv.hasNext()) {
			Proveedor prov = itProv.next();
			Parametro par = (Parametro) prov.getParametro(MetaParametro.PROVEEDOR_CODPPM);
			
			if (par!=null && par.getValor()!=null) {
				String[] cortado = ((String) par.getValor()).split(";");
				
				for (int i=0; i<cortado.length;i++) {
					if (cortado[i].equals(codPPM))
						return prov;
				}
			}
		}
		
		return null;
	}
	
	public Proveedor getProveedor(int id) {
		
		Iterator<Proveedor> it = listado.values().iterator();
		while (it.hasNext()){
			Proveedor est = (Proveedor) it.next();
			if (est.id == id)
				return est;	
		}
		
		return null;
	}
	
	public ArrayList<Tarifa> listaTarifas() {
		RelRecursoTarifa rrt = new RelRecursoTarifa();
		Iterator<RelRecursoTarifa> itRel = rrt.buscaRelacion(this.id, true).iterator();
		
		ArrayList<Tarifa> salida = new ArrayList<Tarifa>();
		
		while (itRel.hasNext()) {
			rrt = itRel.next();
			salida.add(rrt.tarifa);
		}
		
		return salida;
	}
	
	public boolean guardaProveedor(String idTransaccion) {
		boolean modificacion = true;
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		String cadConexion = "";
		
		if (this.id == -1) { 
			ParametroBD pBD = new ParametroBD(2,ConstantesBD.PARAMBD_ID,this.id);
			listaParms.add(pBD);
			cadConexion = "iInsertaProveedor";
			modificacion = false;
		} else {
			ParametroBD pBD = new ParametroBD(2,ConstantesBD.PARAMBD_INT,this.id);
			listaParms.add(pBD);
			cadConexion = "uActualizaProveedor";
		}
		
		ParametroBD pBD = new ParametroBD(1,ConstantesBD.PARAMBD_STR,this.descripcion);
		listaParms.add(pBD);
		pBD = new ParametroBD(3,ConstantesBD.PARAMBD_STR,this.nomCorto);
		listaParms.add(pBD);
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL(cadConexion, listaParms, this, idTransaccion);
		
		if (!modificacion) {
			this.id = ParametroBD.ultimoId;
		}
			
		return modificacion;
	}
	
	public void bajaRecurso(String idTransaccion) throws Exception{		
		cargaProveedor(); 
		
		Iterator<? extends Parametro> itParm = this.listadoParametros.values().iterator();
		while (itParm.hasNext()) {
			Parametro pr = (Parametro) itParm.next();
			pr.bajaParametro(idTransaccion);
		}
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.id));
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("dBorraProveedor", listaParms, this, idTransaccion);
	}
	
	public void cargaProveedor() throws Exception{			
		Parametro pp = new Parametro();
				
		this.listadoParametros = pp.dameParametros(this.getClass().getSimpleName(), this.id);
	}
	
	@Override
	public String toString() {
		return descripcion;
	}

	@Override
	public HashMap<?, ?> getListado() {
		return Proveedor.listado;
	}

	@Override
	public int getId() {
		return this.id;
	}

}
