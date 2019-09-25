package model.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class RelRecursoTarifa implements Cargable{
	
	public static int identificadorUnico = 0; 
	 
	public int id = 0;
	
	public Recurso recurso = null;
	public Proveedor proveedor = null;
	public Tarifa tarifa = null;
	public int mes = 1;
	public int anio = 2000;
	public boolean modificado = false;
	public boolean vigente = false; 
	public boolean usuario = false; 

	public RelRecursoTarifa() {
    	id = RelRecursoTarifa.identificadorUnico;
    	RelRecursoTarifa.identificadorUnico++;
	}
	
	public boolean equals(Object o) {
		RelRecursoTarifa rrt = (RelRecursoTarifa) o;
		if (o!=null && this.id == rrt.id) {
			return true;
		}  else return false;
	}
	
	public void guardaRelacion (String idTransaccion) throws Exception{
		if (this.id==-1) {
			insertaRelacion(idTransaccion);
		}
		else {
			updateRelacion(idTransaccion);
		}
	}
	
	public ArrayList<RelRecursoTarifa> buscaRelacion(int idRecurso, boolean proveedor) { 			
		ConsultaBD consulta = new ConsultaBD();
		
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		if (proveedor)
			listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,idRecurso));
		else
			listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,idRecurso));
		
		ArrayList<Cargable> relRecursoTarifas = consulta.ejecutaSQL("cRelTarifaRec", listaParms, this);
		
		Iterator<Cargable> itCargable = relRecursoTarifas.iterator();
		ArrayList<RelRecursoTarifa> salida = new ArrayList<RelRecursoTarifa> ();
		
		while (itCargable.hasNext()) {
			RelRecursoTarifa rec = (RelRecursoTarifa) itCargable.next();
			salida.add(rec);
		}
		
		return salida;
    }
	
	public void insertaRelacion(String idTransaccion) throws Exception{
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_ID,this.id));
		if (this.usuario)
			listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,this.recurso.id));
		else
			if (this.proveedor!=null)
				listaParms.add(new ParametroBD(6,ConstantesBD.PARAMBD_INT,this.proveedor.id));
			else 
				listaParms.add(new ParametroBD(6,ConstantesBD.PARAMBD_INT,-1));
		
		listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_INT,this.tarifa.idTarifa));
		listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_INT,this.mes));
		listaParms.add(new ParametroBD(5,ConstantesBD.PARAMBD_INT,this.anio));
			
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("iInsertaRelTarifaRecurso", listaParms, this, idTransaccion);
	}
	
	public void updateRelacion(String idTransaccion) throws Exception{
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.id));	
		
		if (this.usuario) {
			listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,this.recurso.id));
			listaParms.add(new ParametroBD(6,ConstantesBD.PARAMBD_INT,0));
		}
		else {
			listaParms.add(new ParametroBD(6,ConstantesBD.PARAMBD_INT,this.proveedor.id));
			listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,0));
		}
		
		listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_INT,this.tarifa.idTarifa));
		listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_INT,this.mes));
		listaParms.add(new ParametroBD(5,ConstantesBD.PARAMBD_INT,this.anio));
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("uActualizaRelTarifaRecurso", listaParms, this, idTransaccion);
	}
	
	public void deleteRelacion(int idElemento) throws Exception{
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		
		listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,idElemento));
				
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("dRelTarRecurso", listaParms, this);
	}

	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
			if (salida.get("reltrId")==null) this.id = 0; else this.id = (Integer) salida.get("reltrId");
			
			if (salida.get("reltrIdRec")==null) this.recurso = null; else try { 
					int id = (Integer) salida.get("reltrIdRec");
					if (id!=0) {
						Recurso rec = new Recurso();
						rec.id = id;
						rec.cargaRecurso();
						this.recurso = rec;
						this.usuario = true;
					}
			} catch (Exception e) {}
			
			if (salida.get("reltrIdProv")==null) this.proveedor = null; else try { 
				int id = (Integer) salida.get("reltrIdProv");
				if (id!=0) {
					this.proveedor = Proveedor.listadoId.get(id);
					this.usuario = false;
				}
			} catch (Exception e) {}
			
			if (salida.get("reltrIdTar")==null) this.tarifa = null; else try { 
				int id = (Integer) salida.get("reltrIdTar");
				Tarifa t = new Tarifa();
				HashMap<String, Object> filtros = new HashMap<String, Object>();
				filtros.put(Tarifa.filtro_ID,id);
				ArrayList<Tarifa> tarifas = t.listado(filtros);
				this.tarifa = tarifas.get(0);
			} catch (Exception e) {}
			
			if (salida.get("relMes")==null) this.mes = 0; else try { 
				this.mes = (Integer) salida.get("relMes");
			} catch (Exception e) {}
			
			if (salida.get("relAnio")==null) this.anio = 0; else try { 
				this.anio = (Integer) salida.get("relAnio");
			} catch (Exception e) {}
			
		} catch (Exception e) {
			
		} 
		
		return this;
	}
	
	public RelRecursoTarifa tarifaVigente(int idRecurso, boolean proveedor, Date fechaBuscada) { 
		ArrayList<RelRecursoTarifa> listaTarifas = buscaRelacion(idRecurso, proveedor);
		
		Date fechaActual = null;
		
		if (fechaBuscada == null)
			fechaActual = Constantes.fechaActual();
		else 
			fechaActual = fechaBuscada;
		
		Calendar cFechaActual = Calendar.getInstance();
		cFechaActual.setTime(fechaActual);
		int mesBuscado = cFechaActual.get(Calendar.MONTH)+1;
		int anioBuscado = cFechaActual.get(Calendar.YEAR)+1;
		
		Calendar cMaxima = Calendar.getInstance();
		Calendar cIterada = Calendar.getInstance();
		
		cMaxima.setTime(Constantes.finMes(1, 1980));
		long diff = 100000;
		RelRecursoTarifa recMaximo = null;
				
		Iterator<RelRecursoTarifa> itCargable = listaTarifas.iterator();
		RelRecursoTarifa rec = null;
		
		while (itCargable.hasNext()) {
			rec = (RelRecursoTarifa) itCargable.next();
			
			if (recMaximo==null) {
				recMaximo = rec;
			}
			
			if (rec.mes == mesBuscado && rec.anio == anioBuscado) 
				return rec;
			else {
				Date fIterada = Constantes.finMes(rec.mes, rec.anio);
				cIterada.setTime(fIterada);
				
				if (Math.abs(cMaxima.getTimeInMillis()-cIterada.getTimeInMillis())<diff) {
					recMaximo = rec;
					cMaxima = cIterada;
					diff = Math.abs(cMaxima.getTimeInMillis()-cIterada.getTimeInMillis());
				}
			}
		}
		
		return recMaximo;
    }
	
	public String toString() {
		return this.tarifa.toString();
	}

}
