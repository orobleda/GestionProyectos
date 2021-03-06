package model.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class Tarifa implements Cargable{
	public int idTarifa = 0;
	public float costeHora = 0;
	public String nomTarifa = "";
	public Date fInicioVig = null;
	public Date fFinVig = null;
	public boolean esDesarrollo = false;
	public boolean esMantenimiento = false;
	public Proveedor proveedor = null;
	public boolean modificado = false;
	
	public static final String filtro_ID = "id";
	public static final String filtro_PROVEEDOR = "proveedor";
	public static final String filtro_MANTENIMIENTO = "mantenimiento";
	public static final String filtro_DESARROLLO = "desarrollo";
	public static final String filtro_VIGENTES = "vigentes";
	
	public static ArrayList<Tarifa> listaTarifas = null;
	
	public static ArrayList<Tarifa> getTarifas() {
		if (listaTarifas==null) {
			Tarifa t = new Tarifa();
			listaTarifas = t.listado(new HashMap<String, Object>() );
		}
		
		return listaTarifas;
	}
	
	public static void forzarRecargaTarifas() {
		Tarifa t = new Tarifa();
		listaTarifas = t.listado(new HashMap<String, Object>() );		
	}
	
	public void updateTarifa()  throws Exception{
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, idTarifa));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, esDesarrollo?1:0));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, esMantenimiento?1:0));
		listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_INT, proveedor.id));
		listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_REAL, costeHora));
		listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_FECHA, this.fInicioVig));
		listaParms.add(new ParametroBD(7, ConstantesBD.PARAMBD_FECHA, this.fFinVig));
		listaParms.add(new ParametroBD(8, ConstantesBD.PARAMBD_REAL, new Float(this.fInicioVig.getTime())));
		listaParms.add(new ParametroBD(9, ConstantesBD.PARAMBD_REAL, new Float(this.fFinVig.getTime())));
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("uActualizaTarifa", listaParms, this);
	}
	
	public void borraTarifa()  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
	
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, idTarifa));
	
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("iDeleteTarifa", listaParms, this);
	}
	
	public void insertTarifa()  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_ID, idTarifa));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, esDesarrollo?1:0));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, esMantenimiento?1:0));
		if (proveedor!=null)
			listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_INT, proveedor.id));
		else 
			listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_INT, -1));
		listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_REAL, costeHora));
		listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_FECHA, this.fInicioVig));
		listaParms.add(new ParametroBD(7, ConstantesBD.PARAMBD_FECHA, this.fFinVig));
		listaParms.add(new ParametroBD(8, ConstantesBD.PARAMBD_REAL, new Float(this.fInicioVig.getTime())));
		listaParms.add(new ParametroBD(9, ConstantesBD.PARAMBD_REAL, new Float(this.fFinVig.getTime())));
		
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("iInsertaTarifa", listaParms, this);
		
		this.idTarifa = ParametroBD.ultimoId;
	}
	
	public ArrayList<Tarifa> listado (HashMap<String, Object> filtros ) {		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		if (filtros.containsKey(Tarifa.filtro_ID)) {
			Integer id = (Integer) filtros.get("id");
			if (id>0) listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_INT, id));
		}
		if (filtros.containsKey(Tarifa.filtro_PROVEEDOR)) {
			Object o = filtros.get(Tarifa.filtro_PROVEEDOR);
			if (o!=null) {
				Proveedor p = (Proveedor) o;
				listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, p.id));
			}
		}
		if (filtros.containsKey(Tarifa.filtro_DESARROLLO)) {
			Object o = filtros.get(Tarifa.filtro_DESARROLLO);
			if (o!=null) {
				Boolean p = (Boolean) o;
				listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, p.booleanValue()?1:0));
			}
		}
		if (filtros.containsKey(Tarifa.filtro_MANTENIMIENTO)) {
			Object o = filtros.get(Tarifa.filtro_MANTENIMIENTO);
			if (o!=null) {
				Boolean p = (Boolean) o;
				listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, p.booleanValue()?1:0));
			}
		}
		if (filtros.containsKey(Tarifa.filtro_VIGENTES)) {
			Object o = filtros.get(Tarifa.filtro_VIGENTES);
			if (o!=null) {
				Boolean p = (Boolean) o;
				if (p.booleanValue()) {
					Date d = Constantes.fechaActual();
					listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_REAL, new Float(d.getTime())));
					listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_REAL, new Float(d.getTime())));
				}				
			}
		}
		
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> tarifas = consulta.ejecutaSQL("cConsultaTarifa", listaParms, this);
		
		Iterator<Cargable> itProyecto = tarifas.iterator();
		ArrayList<Tarifa> salida = new ArrayList<Tarifa>();
		
		while (itProyecto.hasNext()) {
			Tarifa p = (Tarifa) itProyecto.next();
			salida.add(p);
		}
				
		return salida;
	}
	
	public  Tarifa vigentes() {
		HashMap<String, Object> filtros = new HashMap<String, Object>();
		filtros.put(Tarifa.filtro_VIGENTES, new Boolean(true));
		
		ArrayList<Tarifa> salida = this.listado(filtros);
		
		if (salida.size()==1) {
			return salida.get(0);
		}
		
		return null;
		
	}
	
	public static Tarifa porId(int id) {
		HashMap<String, Object> filtros = new HashMap<String, Object>();
		filtros.put(Tarifa.filtro_ID, id);
		
		ArrayList<Tarifa> salida = new Tarifa().listado(filtros);
		
		if (salida.size()==1) {
			return salida.get(0);
		}
		
		return null;
		
	}
	
	public ArrayList<Tarifa> tarifas(boolean desarrollo) {
		HashMap<String, Object> filtros  = new HashMap<String, Object>();
		filtros.put(Tarifa.filtro_DESARROLLO, Constantes.TRUE);
		
		ArrayList<Tarifa> listaTarifas = listado (filtros);
		
		return listaTarifas;
	}
	
	public static Tarifa tarifaPorDefecto(Recurso r, Proveedor p, boolean mantenimiento) throws Exception {
		Tarifa salida = null;
		if (r!=null) {
			ParametroRecurso pr = (ParametroRecurso) r.getValorParametro(MetaParametro.RECURSO_TAR_DEF);
			
			if (pr!=null && pr.valorObjeto!=null) {
				salida = (Tarifa) pr.getValor();
				if (salida != null) return salida;
			}
			
			pr = (ParametroRecurso) r.getValorParametro(MetaParametro.RECURSO_PROVEEDOR);
			if (pr!=null && pr.valorObjeto!=null) {
				Proveedor pAux = (Proveedor) pr.getValor();
				
				HashMap<String,Object> filtros = new HashMap<String,Object>();
				filtros.put(Tarifa.filtro_PROVEEDOR, pAux);
				ArrayList<Tarifa> lTarifas= new Tarifa().listado(filtros);
				if (lTarifas!=null) {
					Calendar fUltima = Calendar.getInstance();
					fUltima.setTime(Constantes.fechaActual());
					Iterator<Tarifa> itTarifas = lTarifas.iterator();
					while (itTarifas.hasNext()) {
						Tarifa tAux = itTarifas.next();
						if (tAux.fFinVig==null) return tAux;
						
						Calendar fFin = Calendar.getInstance();
						fFin.setTime(tAux.fFinVig);
						
						if (fFin.after(fUltima)) return tAux;
					}
				}
			}
			
			pr = (ParametroRecurso) r.getValorParametro( MetaParametro.RECURSO_NAT_COSTE);
			
			if (pr!=null && pr.valorObjeto!=null) {
				MetaConcepto mc = (MetaConcepto) pr.getValor();
				
				String parametro = "";
				
				if (MetaConcepto.TREI == mc.id) parametro = MetaParametro.PARAMETRO_TARIFA_DEFECTO_INTERNAS;
				else if (MetaConcepto.SATAD == mc.id) parametro = MetaParametro.PARAMETRO_TARIFA_DEFECTO_SATAD;
				else if (MetaConcepto.CC == mc.id) parametro = MetaParametro.PARAMETRO_TARIFA_DEFECTO_CC;
				
				Parametro par = (Parametro) Parametro.getParametro(Parametro.class.getSimpleName(), r.id, parametro);
				
				if (pr!=null && par.valorObjeto!=null) {
					salida = (Tarifa) par.getValor();
					if (salida != null) return salida;
				}
			} else {
				Parametro par = (Parametro) Parametro.getParametro(Parametro.class.getSimpleName(), r.id, MetaParametro.PARAMETRO_TARIFA_DEFECTO_INTERNAS);
				
				if (pr!=null && par.valorObjeto!=null) {
					salida = (Tarifa) par.getValor();
					if (salida != null) return salida;
				}
			}
		} else {
			Parametro pr = (Parametro) Parametro.getParametro(Proveedor.class.getSimpleName(), p.id, MetaParametro.PROVEEDOR_TARIFA_DEFECTO);
			
			if (pr!=null && pr.valorObjeto!=null) {
				salida = (Tarifa) pr.getValor();
				if (salida != null) return salida;
			}
			
			pr = (Parametro) Parametro.getParametro(Parametro.class.getSimpleName(), -1, MetaParametro.PARAMETRO_TARIFA_DEFECTO_DES);
				
			if (pr!=null && pr.valorObjeto!=null) {
				salida = (Tarifa) pr.getValor();
				if (salida != null) return salida;
			}
		}		
		
		return null;
	}
	
	public Tarifa tarifaPorCoste(boolean desarrollo, float coste) {
		HashMap<String, Object> filtros  = new HashMap<String, Object>();
		filtros.put(Tarifa.filtro_DESARROLLO, Constantes.TRUE);
		
		ArrayList<Tarifa> listaTarifas = listado (filtros);
		
		Iterator<Tarifa> itTarifa = listaTarifas.iterator();
		while (itTarifa.hasNext()) {
			Tarifa t = itTarifa.next();
			
			if (t.costeHora == coste) {
				return t;
			}
		}
		
		Tarifa t = new Tarifa();
		t.idTarifa = -1;
		t.costeHora = coste;
		t.esDesarrollo = true;
		t.fFinVig = Constantes.finMes(12, 2999);
		t.fFinVig = Constantes.inicioMes(1, 1999);
		return t;
	}
	
	public Tarifa tarifaEstandar(Sistema s, Concepto c) {
		Tarifa t = new Tarifa();
		t.nomTarifa = "Tarifa1";
		t.costeHora = 42;
		t.idTarifa = 1;
		return t;
	}
	
	public String toString() {
		return this.costeHora + " " + (proveedor==null?"ENAGAS":proveedor.nomCorto);
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try { if (salida.get("tarId")==null)     { this.idTarifa = 0;            } else this.idTarifa = (Integer) salida.get("tarId");                           } catch (Exception e){System.out.println();}
		try { if (salida.get("tarDesa")==null)   { this.esDesarrollo = false;    } else this.esDesarrollo = ((Integer) salida.get("tarDesa"))==1?true:false;     } catch (Exception e){System.out.println();}
		try { if (salida.get("tarMante")==null)  { this.esMantenimiento = false; } else this.esMantenimiento = ((Integer) salida.get("tarMante"))==1?true:false; } catch (Exception e){System.out.println();}
		try { if (salida.get("tarValor")==null)  { this.costeHora = 0;           } else this.costeHora = new Float((Double) salida.get("tarValor"));                         } catch (Exception e){System.out.println();}
		try { if (salida.get("tarIniVig")==null) { this.fInicioVig = null;       } else this.fInicioVig = (Date) FormateadorDatos.parseaDato((String)salida.get("tarIniVig"),FormateadorDatos.FORMATO_FECHA);                        } catch (Exception e){System.out.println();}
		try { if (salida.get("tarFinVig")==null) { this.fFinVig = null;          } else this.fFinVig = (Date) FormateadorDatos.parseaDato((String)salida.get("tarFinVig"),FormateadorDatos.FORMATO_FECHA);                          } catch (Exception e){System.out.println();}
			
		try { if (salida.get("tarProv")==null)   { this.proveedor = null;        } else {
																						int id = (Integer) salida.get("tarProv");
																						this.proveedor = new Proveedor().getProveedor(id);
		}}  catch (Exception e){System.out.println();}		
		
		try {
			this.nomTarifa = this.idTarifa +". "+ this.proveedor.nomCorto + " " + (this.esMantenimiento?"(MANT)":"")  + (this.esDesarrollo?"(DES)":"") + ": " + new Float(this.costeHora).toString();
		} catch (Exception e) {
			this.nomTarifa = new Integer(this.idTarifa).toString();
		}
		
		return this;
	}
}
