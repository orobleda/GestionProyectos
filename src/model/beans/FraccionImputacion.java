package model.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.metadatos.Sistema;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;
import ui.Economico.ControlPresupuestario.EdicionEstImp.FraccionarImputacion;

public class FraccionImputacion  implements Cargable {
	
	public static int POR_PORCENTAJE = 0;
	public static int POR_HORAS = 1;
	public static int POR_IMPORTE = 2;
	
	public int id = 0;
	public int idPadre = 0;
	public float horas = 0;
	public float importe = 0;
	public float porc = 0;
	public int tipo=0;
	public Sistema sistema = null;
	
	public Imputacion imputacionPadre = null;
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try { if (salida.get("frImid")==null)     { this.id = 0;            } else this.id = (Integer) salida.get("frImid");                           } catch (Exception e){System.out.println();}
		try { if (salida.get("frImidImputacion")==null)     { this.idPadre = 0;            } else this.idPadre = (Integer) salida.get("frImidImputacion");                           } catch (Exception e){System.out.println();}
		try { if (salida.get("frImhoras")==null)     { this.horas = 0;            } else this.horas = ((Double) salida.get("frImhoras")).floatValue();    } catch (Exception e){System.out.println();}
		try { if (salida.get("frImimporte")==null)     { this.importe = 0;            } else this.importe = ((Double) salida.get("frImimporte")).floatValue();    } catch (Exception e){System.out.println();}
		try { if (salida.get("frImporcentaje")==null)     { this.porc = 0;            } else this.porc = ((Double) salida.get("frImporcentaje")).floatValue();    } catch (Exception e){System.out.println();}
		try { if (salida.get("frImtipo")==null)     { this.tipo = 0;            } else this.tipo = (Integer) salida.get("frImtipo");                           } catch (Exception e){System.out.println();}
		try { if (salida.get("frImSistema")==null)   { this.sistema = null;    } else {
			int idSistema = ((Integer) salida.get("frImSistema"));
			this.sistema = Sistema.listado.get(idSistema);
		}} catch (Exception e){this.sistema = null;}
		
		return this;
	}
	
	public ArrayList<FraccionImputacion> listado (Proyecto p) {		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
				
		String cadConexion = "";
		
		if (p==null) {
			cadConexion = "cListaFracciones";
		} else {
			listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, p.id));
			cadConexion = "cListaFraccionesProyecto";
		}

		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> tarifas = consulta.ejecutaSQL(cadConexion, listaParms, this);
		
		Iterator<Cargable> itProyecto = tarifas.iterator();
		ArrayList<FraccionImputacion> salida = new ArrayList<FraccionImputacion>();
		
		while (itProyecto.hasNext()) {
			FraccionImputacion est = (FraccionImputacion) itProyecto.next();
			salida.add(est);
		}
				
		return salida;
	}
	
	public void insertaFraccionImputacion(ArrayList<FraccionImputacion> listaImputaciones, Imputacion i, String idTransaccion)  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, i.id));
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("cEliminaFraccionesImputacion", listaParms, this, idTransaccion);
				
		if (listaImputaciones.size()!=0) {
			
			Iterator<FraccionImputacion> itFR = listaImputaciones.iterator();
			
			while (itFR.hasNext()) {
				FraccionImputacion fr = itFR.next();
								
				if ((FraccionImputacion.POR_PORCENTAJE == fr.tipo && (fr.porc>0) ) ||
				(FraccionImputacion.POR_HORAS == fr.tipo && (fr.horas>0) ) ||
				(FraccionImputacion.POR_IMPORTE == fr.tipo && (fr.importe>0) ))	{
							
					ArrayList<ParametroBD> listaParms2 = new ArrayList<ParametroBD>();
					listaParms2.add(new ParametroBD(1, ConstantesBD.PARAMBD_ID, i.id));			
					listaParms2.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, i.id));
					listaParms2.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, fr.tipo));
					listaParms2.add(new ParametroBD(4, ConstantesBD.PARAMBD_REAL, fr.porc));
					listaParms2.add(new ParametroBD(5, ConstantesBD.PARAMBD_REAL, fr.horas));
					listaParms2.add(new ParametroBD(6, ConstantesBD.PARAMBD_REAL, fr.importe));
					listaParms2.add(new ParametroBD(7, ConstantesBD.PARAMBD_INT, fr.sistema.id));
					
					consulta.ejecutaSQL("iAltaFraccionimputacion", listaParms2, this, idTransaccion);
				}
			}
		}
	}
	
	public float getHoras() {
		if (tipo == FraccionImputacion.POR_HORAS) return this.horas;
		if (tipo == FraccionImputacion.POR_PORCENTAJE) {
			return imputacionPadre.horas*porc/100;	
		}
		if (tipo == FraccionImputacion.POR_IMPORTE) {
			if (imputacionPadre.tarifa!=null) {
				return importe/imputacionPadre.tarifa.costeHora; 
			} else return 0;				
		} 
		return 0;
	}

	public float getImporte() {
		if (tipo == FraccionImputacion.POR_HORAS) { 
			if (imputacionPadre.tarifa!=null) {
				return horas*imputacionPadre.tarifa.costeHora; 
			} else return 0;
		}
		if (tipo == FraccionImputacion.POR_PORCENTAJE) {
			return imputacionPadre.importe*porc/100;	
		}
		if (tipo == FraccionImputacion.POR_IMPORTE) {
			return importe;	
		} 
		return 0;
	}
}
