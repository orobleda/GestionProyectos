package model.utils.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import model.beans.Parametro;
import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.metadatos.MetaParametro;
import model.metadatos.TipoDato;

public class ReplicaBD implements Cargable, Comparable<ReplicaBD> {
	
	public static int ocurrencias = 0;
		
	public int id = 0;
	public Date fxSalvado = null;
	public String nomFichero = "";
	public String version = "";
		
	public ReplicaBD() {
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		this.id = (Integer) salida.get("rbdId");
		this.nomFichero = (String) salida.get("rbdNomFichero");
		this.version = (String) salida.get("rbdVersion");
		
		try {
			this.fxSalvado = (Date) FormateadorDatos.parseaDato((String) salida.get("rbdFxCopia"),FormateadorDatos.FORMATO_FECHA);
		} catch (Exception e)  {
			this.fxSalvado = Constantes.fechaActual();
		}
			
		return this;
	}

	public HashMap<Integer, ReplicaBD> listado() {
		HashMap<Integer, ReplicaBD> listado = new HashMap<Integer, ReplicaBD>();	
			
		ConsultaBDReplica consulta = new ConsultaBDReplica();
		ArrayList<Cargable> estados = consulta.ejecutaSQL("cConsultaReplicaBD", null, this);
		
		Iterator<Cargable> it = estados.iterator();
		while (it.hasNext()){
			ReplicaBD est = (ReplicaBD) it.next();
			listado.put(est.id, est);
		}
		
		consulta.close();
		
		return listado;
	}
	
	public ReplicaBD getActual() {
		HashMap<Integer, ReplicaBD> listado = new HashMap<Integer, ReplicaBD>();	
			
		ConsultaBDReplica consulta = new ConsultaBDReplica();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_STR,ConsultaBD.url));
		
		ArrayList<Cargable> estados = consulta.ejecutaSQL("cConsultaReplicaBD", listaParms, this);

		consulta.close();
		
		Iterator<Cargable> it = estados.iterator();
		while (it.hasNext()){
			ReplicaBD est = (ReplicaBD) it.next();
			return est;
		}
		
		return null;
	}
	
	public static String fuerzaGuardado(String version) {
		Parametro p = (Parametro) Parametro.getParametro(new Parametro().getClass().getSimpleName(), -1, MetaParametro.PARAMETRO_OCURR_COPIA_BD);

		ReplicaBD.ocurrencias =(Integer) p.getValor() +1;
		
		return ReplicaBD.replicaBD(version);
	}
	
	public static String replicaBD(String version) {
		try {
			Parametro p = (Parametro) Parametro.getParametro(new Parametro().getClass().getSimpleName(), -1, MetaParametro.PARAMETRO_OCURR_COPIA_BD);
			
			if (ReplicaBD.ocurrencias >= (Integer) p.getValor()) {
				ConsultaBD cons = new ConsultaBD();
				String ruta = cons.copiaBackup();
				
				ReplicaBD.ocurrencias = 0;
				
				ReplicaBD actual = new ReplicaBD().getActual();
				
				if (version==null){
					long milis = actual.getMilis();
					String vs = FormateadorDatos.formateaDato(new Date(milis),TipoDato.FORMATO_FECHAHORA);
					
					guardaBackup(Constantes.fechaActual(),ruta, vs);
				} else {
					guardaBackup(Constantes.fechaActual(),ruta, version);
				}
				
				return ruta;
			} else {
				ReplicaBD.ocurrencias++;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public String getHora() {
		long milis = this.getMilis();
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(milis);
		
		String salida = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE)+ ":" + c.get(Calendar.SECOND); 
		
		return salida;
	}
	
	public long getMilis() {
		String[] cortada = this.nomFichero.split("\\\\");
		String nomFich = cortada[cortada.length-1];
		nomFich = nomFich.replace("BD_","");
		nomFich = nomFich.replace(".s3db","").trim();
		
		long milis = new Long(nomFich);
		
		return milis;
	}
	
	public static void guardaBackup(Date fecha, String nombre, String version) throws Exception{
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_ID,1));
		listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_FECHA,fecha));
		listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_STR,nombre));
		listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_STR,version));
			
	    ConsultaBDReplica consulta = new ConsultaBDReplica();
		consulta.ejecutaSQL("iAltaReplicaBD", listaParms, null);
		
		consulta.close();
	}
	
	public static void borraBackup(ReplicaBD rbd) throws Exception{
		try {
			File fichero = new File(rbd.nomFichero);
			if (fichero.exists())
				fichero.delete();
			
			ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
			
			listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,rbd.id));
				
		    ConsultaBDReplica consulta = new ConsultaBDReplica();
			consulta.ejecutaSQL("cBorraReplicaBD", listaParms, null);
			
			consulta.close();
		} catch (Exception e) {
			
		}		
	}
	
	@Override
	public String toString() {
		return this.nomFichero;
	}

	@Override
	public int compareTo(ReplicaBD o) {
		return -1*new Long(this.getMilis()).compareTo(new Long(o.getMilis()));
	}

}
