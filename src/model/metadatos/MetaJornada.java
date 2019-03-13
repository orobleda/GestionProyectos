package model.metadatos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import model.interfaces.Cargable;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class MetaJornada implements Cargable{
	public int id = 0;
	public int codJornada = 0;
	public int diaSemana = 0;
	public float horas = 0;
	public float horasV = 0;
	public String inicioVerano = "";
	public String finVerano = "";
	public HashMap<Integer,Float> horasVerano = null;
	public HashMap<Integer,Float> horasInvierno = null;
		
	public static HashMap<Integer,MetaJornada> listaMetaJornadas = null;

	public static HashMap<Integer,MetaJornada> getlistaMetaJornadasEstatico() {
		if (listaMetaJornadas==null) {
			MetaJornada mj = new MetaJornada();
			listaMetaJornadas = mj.listado ();
		}
		
		return listaMetaJornadas;
	}
	
	@Override
	public boolean equals(Object o) {
		try {
			return this.id == ((MetaJornada) o).id;
		} catch (Exception e) {
			return false;
		}
	}
	
	public HashMap<Integer,MetaJornada> listado () {
		if (listaMetaJornadas!=null) return listaMetaJornadas;
		
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> jornadas = consulta.ejecutaSQL("cConsultaMetaJornadas", new ArrayList<ParametroBD>(), this);
		
		listaMetaJornadas = new HashMap<Integer,MetaJornada>();
		
		HashMap<Integer,Integer> prioridadHorasVerano = new HashMap<Integer,Integer>();
		
		Iterator<Cargable> itMj = jornadas.iterator();
		
		while (itMj.hasNext()) {
			MetaJornada p = (MetaJornada) itMj.next();
			
			if (listaMetaJornadas.containsKey(p.codJornada)) {
				MetaJornada pAux = (MetaJornada) listaMetaJornadas.get(p.codJornada);
				
				if (p.diaSemana==0) {
					for (int i=1;i<Calendar.SATURDAY+1;i++) {
						if (!prioridadHorasVerano.containsKey(i)){
							prioridadHorasVerano.put(i, p.diaSemana);
							pAux.horasInvierno.put(i, p.horas);
							pAux.horasVerano.put(i, p.horasV);
						} 
					}
				} else {
					if (!prioridadHorasVerano.containsKey(p.diaSemana)){
						prioridadHorasVerano.put(p.diaSemana, p.diaSemana);
						pAux.horasInvierno.put(p.diaSemana, p.horas);
						pAux.horasVerano.put(p.diaSemana, p.horasV);
					} else {
						if (prioridadHorasVerano.get(p.diaSemana)==0){
							prioridadHorasVerano.put(p.diaSemana, p.diaSemana);
							pAux.horasInvierno.put(p.diaSemana, p.horas);
							pAux.horasVerano.put(p.diaSemana, p.horasV);
						}
					}
				}	
			} else {
				p.horasInvierno = new HashMap<Integer,Float>();
				p.horasVerano = new HashMap<Integer,Float>();
				
				if (p.diaSemana==0) {
					for (int i=1;i<Calendar.SATURDAY+1;i++) {
						prioridadHorasVerano.put(i, p.diaSemana);
						p.horasInvierno.put(i, p.horas);
						p.horasVerano.put(i, p.horasV);
					}
				} else {
					prioridadHorasVerano.put(p.diaSemana, p.diaSemana);
					p.horasInvierno.put(p.diaSemana, p.horas);
					p.horasVerano.put(p.diaSemana, p.horasV);
				}
				
				listaMetaJornadas.put(p.codJornada, p);
			}
		}
				
		return listaMetaJornadas;
	}
	
	public float horasDia(Date dia) {
		Calendar fechaActual = Calendar.getInstance();
		fechaActual.setTime(dia);
		
		String [] corteInicioVerano = inicioVerano.split("/");
		
		Calendar cIniVerano = Calendar.getInstance();
		cIniVerano.set(fechaActual.get(Calendar.YEAR), new Integer(corteInicioVerano[1])-1, new Integer(corteInicioVerano[0]));
		
		String [] corteFinVerano = finVerano.split("/");
		
		Calendar cFinVerano = Calendar.getInstance();
		cFinVerano.set(fechaActual.get(Calendar.YEAR), new Integer(corteFinVerano[1])-1, new Integer(corteFinVerano[0]));
		
		if (fechaActual.getTime().compareTo(cIniVerano.getTime())<0 || fechaActual.getTime().compareTo(cFinVerano.getTime())>0) {
			return horasInvierno.get(fechaActual.get(Calendar.DAY_OF_WEEK));
		}
		
		if (fechaActual.getTime().compareTo(cIniVerano.getTime())>0 && fechaActual.getTime().compareTo(cFinVerano.getTime())<0) {
			return horasVerano.get(fechaActual.get(Calendar.DAY_OF_WEEK));
		}
		
		return 0;
	}
	
	public String toString() {
		return ""+this.codJornada;
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try { if (salida.get("mjId")==null)     { this.id = 0;            } else this.id = (Integer) salida.get("mjId");  } catch (Exception e){System.out.println();}
		try { if (salida.get("mjCodJornada")==null)   { this.codJornada = -1;    } else this.codJornada = (Integer) salida.get("mjCodJornada");     } catch (Exception e){System.out.println();}
		try { if (salida.get("mjDiaSemana")==null)  { this.diaSemana = -1; } else this.diaSemana = (Integer) salida.get("mjDiaSemana");  } catch (Exception e){System.out.println();}
		try { if (salida.get("mjHoras")==null)  { this.horas = 0;           } else this.horas = new Float((Double) salida.get("mjHoras"));                         } catch (Exception e){System.out.println();}
		try { if (salida.get("mjHorasVerano")==null) { this.horasV = 0;       } else this.horasV = new Float((Double) salida.get("mjHorasVerano"));                       } catch (Exception e){System.out.println();}
		try { if (salida.get("mjInicioVerano")==null) { this.inicioVerano = "";          } else this.inicioVerano = (String) salida.get("mjInicioVerano");     } catch (Exception e){System.out.println();}
		try { if (salida.get("mjFinVerano")==null) { this.finVerano = null;          } else this.finVerano = (String) salida.get("mjFinVerano"); } catch (Exception e){System.out.println();}	
			
		return this;
	}
}
