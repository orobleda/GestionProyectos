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
import model.metadatos.TipoDato;
import model.metadatos.TipoEnumerado;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class RelProyectoEvento implements Cargable{

	public int idProy;
	public int idEvento;
	public int id;
	
	public Evento evt;
	public Proyecto proy;
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
		 	if (salida.get("relIdproy")==null)  { 
		 		this.idProy = 0;
			} else {
		 		this.idProy = (Integer) salida.get("relIdproy");
		 		this.proy = Proyecto.getProyectoEstatico(this.idProy);
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("relIdevento")==null)  { 
		 		this.idEvento = 0;
			} else {
		 		this.idEvento = (Integer) salida.get("relIdevento");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("relId")==null)  { 
		 		this.id = 0;
			} else {
		 		this.id = (Integer) salida.get("relId");
			}
		} catch (Exception ex) {}
		
		return this;
	}
	
	public ArrayList<Proyecto> listaProyectosAsociados() {
		ConsultaBD consulta = new ConsultaBD();
		
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT,this.idEvento));
		
		ArrayList<Cargable> eventos = consulta.ejecutaSQL("cConsultaRel_proyecto_evento", listaParms, this);
			
		Iterator<Cargable> itEvt = eventos.iterator();
		ArrayList<Proyecto> salida = new ArrayList<Proyecto>();
			
		while (itEvt.hasNext()) {
			RelProyectoEvento evt = (RelProyectoEvento) itEvt.next();			
			salida.add(evt.proy);
		}
				
		return salida;
	}
	
	public void borrarProyectosAsociados(String idTransaccion) throws Exception{			
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.idEvento));
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("dBorraRel_proyecto_evento", listaParms, this, idTransaccion);	
	}
	
	public void updateEvento(String idTransaccion, ArrayList<Proyecto> lProyectos) throws Exception{
		borrarProyectosAsociados(idTransaccion);
		insertEvento(idTransaccion, lProyectos);		
	}
	
	public void insertEvento(String idTransaccion, ArrayList<Proyecto> lProyectos)  throws Exception{		
		ConsultaBD consulta = new ConsultaBD();
		
		Iterator<Proyecto> itProy = lProyectos.iterator();
		while (itProy.hasNext()) {
			Proyecto p = itProy.next();
			
			ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
			listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, p.id));
			listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.idEvento));
			listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_ID, this.id));
			
			consulta = new ConsultaBD();
			consulta.ejecutaSQL("iInsertaRel_proyecto_evento", listaParms, this, idTransaccion);
			
			this.id = ParametroBD.ultimoId;
		}
	}

}
