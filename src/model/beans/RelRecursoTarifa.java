package model.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class RelRecursoTarifa implements Cargable{
	
	public static int identificadorUnico = 0; 
	 
	public int id = 0;
	
	public Recurso recurso = null;
	public Tarifa tarifa = null;
	public Date fechaInicio = null;
	public Date fechaFin = null;
	public boolean modificado = false;
	public boolean vigente = false; 

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
	
	public RelRecursoTarifa tarifaVigente(ArrayList<RelRecursoTarifa> listaTarifas,int idRecurso) { 
		if (listaTarifas==null) listaTarifas = buscaRelacion(idRecurso);
		
		Date fechaActual = new Date();
		Calendar cActual = Calendar.getInstance();
		cActual.setTime(fechaActual);
		
		Calendar cInicio = Calendar.getInstance();
		Calendar cFin = Calendar.getInstance();
		
		Iterator<RelRecursoTarifa> itCargable = listaTarifas.iterator();
		RelRecursoTarifa rec = null;
		
		while (itCargable.hasNext()) {
			rec = (RelRecursoTarifa) itCargable.next();
			Tarifa tarifaIterada = rec.tarifa;
					
			cInicio.setTime(tarifaIterada.fInicioVig);
			cFin.setTime(tarifaIterada.fFinVig);
			
			if (cInicio.before(cActual) && cFin.after(cInicio)) {
				return rec;	
			}			
		}
		
		return rec;
    }
	
	public ArrayList<RelRecursoTarifa> buscaRelacion(int idRecurso) { 			
		ConsultaBD consulta = new ConsultaBD();
		
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
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
		listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,this.recurso.id));
		listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_INT,this.tarifa.idTarifa));
		listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_FECHA,this.fechaInicio));
		listaParms.add(new ParametroBD(6,ConstantesBD.PARAMBD_REAL,new Double(this.fechaInicio.getTime())));
		if (!(Constantes.fechaFinal.compareTo(this.fechaFin)<0)){
			listaParms.add(new ParametroBD(5,ConstantesBD.PARAMBD_FECHA,this.fechaFin));
			listaParms.add(new ParametroBD(7,ConstantesBD.PARAMBD_REAL,new Double(this.fechaFin.getTime())));
		}
			
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("iInsertaRelTarifaRecurso", listaParms, this, idTransaccion);
	}
	
	public void updateRelacion(String idTransaccion) throws Exception{
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.id));
		listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,this.recurso.id));
		listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_INT,this.tarifa.idTarifa));
		listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_FECHA,this.fechaInicio));
		listaParms.add(new ParametroBD(6,ConstantesBD.PARAMBD_REAL,new Double(this.fechaInicio.getTime())));
		
		if (!(Constantes.fechaFinal.compareTo(this.fechaFin)<0)){
			listaParms.add(new ParametroBD(5,ConstantesBD.PARAMBD_FECHA,this.fechaFin));
			listaParms.add(new ParametroBD(7,ConstantesBD.PARAMBD_REAL,new Double(this.fechaFin.getTime())));
		}
		
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
					Recurso rec = new Recurso();
					rec.id = id;
					rec.cargaRecurso();
					this.recurso = rec;
			} catch (Exception e) {}
			
			if (salida.get("reltrIdTar")==null) this.tarifa = null; else try { 
				int id = (Integer) salida.get("reltrIdTar");
				Tarifa t = new Tarifa();
				HashMap<String, Object> filtros = new HashMap<String, Object>();
				filtros.put(Tarifa.filtro_ID,id);
				ArrayList<Tarifa> tarifas = t.listado(filtros);
				this.tarifa = tarifas.get(0);
			} catch (Exception e) {}
			
			if (salida.get("reltrFIni")==null) this.fechaInicio = null; else try { 
				this.fechaInicio = (Date) FormateadorDatos.parseaDato(salida.get("reltrFIni").toString(),FormateadorDatos.FORMATO_FECHA);
			} catch (Exception e) {}
			
			if (salida.get("reltrFfin")==null) {
				Calendar c = Calendar.getInstance();
				c.setTime(Constantes.fechaFinal);
				c.add(Calendar.DAY_OF_MONTH, 1);
				this.fechaFin = c.getTime(); 
			} else try { 
				this.fechaFin = (Date) FormateadorDatos.parseaDato(salida.get("reltrFfin").toString(),FormateadorDatos.FORMATO_FECHA);
			} catch (Exception e) {}
			
		} catch (Exception e) {
			
		} 
		
		return this;
	}
	
	public String toString() {
		return this.tarifa.toString();
	}

}
