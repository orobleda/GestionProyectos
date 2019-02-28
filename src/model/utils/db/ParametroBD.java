package model.utils.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.ConstantesBD;

public class ParametroBD{
	public Integer id = 0;
	public String tipo = null;
	public int valorInt = 0;
	public Date valorFec = null;
	public String valorStr = null;
	public Double valorReal = 0.0;
	public Long valorLong = new Long(0);
	public ArrayList<Object> valorInsTexto = null;
	public ArrayList<Object> valorInsInt = null;
	public ArrayList<Object> valorInsReal = null;
	public String tabla = null;
	
	public static int ultimoId = 0; 
	
	public String toQuery(){
		if (ConstantesBD.PARAMBD_ID.equals(tipo)){
			
			Integer i = calculaMaxId()+1;
			
			return i.toString();
		}	
		
		if (ConstantesBD.PARAMBD_INT.equals(tipo)){
			return "" + new Integer(valorInt).toString();
		}	
		
		if (ConstantesBD.PARAMBD_LONG.equals(tipo)){
			return "" + new Long(valorLong).toString();
		}
		
		if (ConstantesBD.PARAMBD_STR.equals(tipo)){
			return "'" + valorStr + "'";
		}
		
		if ("rea".equals(tipo)){
			return "" + new Double(valorReal).toString();
		}
		
		if (ConstantesBD.PARAMBD_FECHA.equals(tipo)){
			if (valorFec==null) return "null";
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return "'" + sdf.format((Date)valorFec) + "'";
		}
		
		if (ConstantesBD.PARAMBD_LISTA_INT.equals(tipo)){
			Iterator<Object> it = valorInsInt.iterator();
			
			String salida = "";				
			while (it.hasNext()){
				salida += ((Integer) it.next()).toString();
				
				if (it.hasNext()) {
					salida += ",";
				}
			}
			
			return salida;
		}
		
		if ("listStr".equals(tipo)){
			Iterator<Object> it = valorInsTexto.iterator();
			
			String salida = "";				
			while (it.hasNext()){
				salida += "'"+((String) it.next()).toString()+"'";
				
				if (it.hasNext()) {
					salida += ",";
				}
			}
			
			return salida;
		}
		
		if ("listRea".equals(tipo)){
			Iterator<Object> it = valorInsReal.iterator();
			
			String salida = "";				
			while (it.hasNext()){
				salida += ((Double) it.next()).toString();
				
				if (it.hasNext()) {
					salida += ",";
				}
			}
			
			return salida;
		}
		
		return "";
	}
	
	public ParametroBD (){
	}
	
	public ParametroBD (int id, String tipo, int valorInt){
		this.id = id;
		this.tipo = tipo;
		this.valorInt = valorInt;
	}
	
	public ParametroBD (int id, String tipo, Date valorFec){
		this.id = id;
		this.tipo = tipo;
		this.valorFec = valorFec;
	}
	
	public ParametroBD (int id, String tipo, Long valorLong){
		this.id = id;
		this.tipo = tipo;
		this.valorLong = valorLong;
	}
	
	public ParametroBD (int id, String tipo, String valorStr){
		this.id = id;
		this.tipo = tipo;
		this.valorStr = valorStr;
	}
	
	public ParametroBD (int id, String tipo, Float valorReal){
		this.id = id;
		this.tipo = tipo;
		this.valorReal = new Float(valorReal).doubleValue();
	}
	
	public ParametroBD (int id, String tipo, Double valorReal){
		this.id = id;
		this.tipo = tipo;
		this.valorReal = valorReal;
	}
	
	public ParametroBD (int id, String tipo, ArrayList<Object> valorIns){
		this.id = id;
		this.tipo = tipo;
		if (ConstantesBD.PARAMBD_LISTA_INT.equals(tipo)){
			this.valorInsInt = valorIns;
		}
		if (ConstantesBD.PARAMBD_LISTA_REAL.equals(tipo)){
			this.valorInsReal = valorIns;	
		}
		if (ConstantesBD.PARAMBD_LISTA_INT.equals(tipo)){
			this.valorInsTexto = valorIns;
		}
	}
	
	public int calculaMaxId(){
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Object> ids = consulta.ejecutaSQL("SELECT MAX(ID) id FROM "+this.tabla, ConstantesBD.QUERYCONSULTA);
		
		@SuppressWarnings("unchecked")
		HashMap<String,Object> id = (HashMap<String,Object>) ids.get(0);
		
		Integer i;
		
		if (id.get("id")==null) {
			i=0;
		} else{
			i = (Integer) id.get("id");
		}
		
		return i;
	}
	
	public String toQuery(String idTransaccion){
		if (ConstantesBD.PARAMBD_ID.equals(tipo)){
			int id=0;
			if (!ConsultaBD.idsAcumulados.containsKey(idTransaccion)){
				HashMap<String,Integer> ids = new HashMap<String,Integer>();
				ConsultaBD.idsAcumulados.put(idTransaccion, ids);
				id = calculaMaxId()+1;
				ultimoId = id;
				ids.put(this.tabla, id);
				return new Integer(id).toString();
			} else {
				HashMap<String,Integer> ids = ConsultaBD.idsAcumulados.get(idTransaccion);
				if (!ids.containsKey(this.tabla)){
					id = calculaMaxId()+1;
					ids.put(this.tabla, id);
					ultimoId = id;
					return new Integer(id).toString();
				} else {
					id = ids.get(this.tabla)+1;
					ids.put(this.tabla, id);
					ultimoId = id;
					return new Integer(id).toString();
				}
			}
		} else
			return toQuery();
	}
	
}
