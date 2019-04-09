package model.beans;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaFormatoProyecto;
import model.metadatos.MetaJornada;
import model.metadatos.MetaParametro;
import model.metadatos.TipoCobroVCT;
import model.metadatos.TipoDato;
import model.metadatos.TipoEnumerado;
import model.metadatos.TipoProyecto;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;
import ui.Propiedad;
import ui.interfaces.Propiediable;

public class Parametro extends Observable implements Propiediable, Cargable {
	public static int SIN_ID_ELEMENTO = -1;
	public static int SOLO_METAPARAMETROS = -2;
	
	public String QUERY_INSERTA_PARAMETRO = "iInsertaParametro";
	public String QUERY_CONSULTA_PARAMETRO = "cConsultaParametro";
	public String QUERY_BORRA_PARAMETRO = "dBorraParametro";
	
	public MetaParametro metaParam = null;
	public int idEntidadAsociada = 0;
	public int id = 0;
	public String codParametro = "";
		
	public int valorEntero = 0;
	public float valorReal = 0;
	public Object valorObjeto = null;
	public String valorTexto = null;
	public Date valorfecha = null;
	
	public static HashMap<String,Parametro> listadoParametros = null;
	public static HashMap<Integer,Integer> listadoTipoDatosObjetos = null;
	
	public boolean modificado = false;
	
	private static void cargaListadoTipoDatosObjetos() {
		listadoTipoDatosObjetos = new HashMap<Integer,Integer>();
		listadoTipoDatosObjetos.put(TipoDato.FORMATO_FORMATO_PROYECTO, TipoDato.FORMATO_FORMATO_PROYECTO);
		listadoTipoDatosObjetos.put(TipoDato.FORMATO_TIPO_PROYECTO, TipoDato.FORMATO_FORMATO_PROYECTO);
		listadoTipoDatosObjetos.put(TipoDato.FORMATO_PROVEEDOR, TipoDato.FORMATO_FORMATO_PROYECTO);
		listadoTipoDatosObjetos.put(TipoDato.FORMATO_METAJORNADA, TipoDato.FORMATO_FORMATO_PROYECTO);
		listadoTipoDatosObjetos.put(TipoDato.FORMATO_NAT_COSTE, TipoDato.FORMATO_FORMATO_PROYECTO);
		listadoTipoDatosObjetos.put(TipoDato.FORMATO_RECURSO, TipoDato.FORMATO_FORMATO_PROYECTO);
		listadoTipoDatosObjetos.put(TipoDato.FORMATO_TIPO_VCT, TipoDato.FORMATO_TIPO_VCT);
		listadoTipoDatosObjetos.put(TipoDato.FORMATO_TARIFA, TipoDato.FORMATO_FORMATO_PROYECTO);
		listadoTipoDatosObjetos.put(TipoDato.FORMATO_COBRO_VCT, TipoDato.FORMATO_FORMATO_PROYECTO);
	}
	
	public static Object getParametro(String entidad, int idElemento, String codParametro) {
		Parametro p = new Parametro();
		HashMap<String, Parametro> listado = p.dameParametros(entidad, idElemento);
		return listado.get(codParametro).getValor();
	}
	
	public static boolean isObjeto(int tipo) {
		if (Parametro.listadoTipoDatosObjetos == null) {
			Parametro.cargaListadoTipoDatosObjetos();
		}
		
		return Parametro.listadoTipoDatosObjetos.containsKey(tipo);
	}
	
	public String getQueryInsercion() {
		return QUERY_INSERTA_PARAMETRO;
	}
	
	public String getQueryConsulta() {
		return QUERY_CONSULTA_PARAMETRO;
	}
	
	public String getQueryBorrado() {
		return QUERY_BORRA_PARAMETRO;
	}
	
	public String getCodigo() {
		return codParametro;
	}
	
	public Object getValor() {
		if (metaParam== null) return null;
		
		if (metaParam.tipoDato == TipoDato.FORMATO_TXT|| 
				metaParam.tipoDato == TipoDato.FORMATO_URL) return valorTexto;
		if (metaParam.tipoDato == TipoDato.FORMATO_INT) return valorEntero;
		if (metaParam.tipoDato == TipoDato.FORMATO_REAL || 
				metaParam.tipoDato == TipoDato.FORMATO_MONEDA ||
						metaParam.tipoDato == TipoDato.FORMATO_PORC ) return valorReal;
		if (isObjeto(metaParam.tipoDato)) return valorObjeto;
		if (metaParam.tipoDato == TipoDato.FORMATO_FECHA) return valorfecha;
		if (metaParam.tipoDato == TipoDato.FORMATO_BOOLEAN) return (valorEntero==Constantes.NUM_TRUE)?Constantes.TRUE:Constantes.FALSE;
		
		return null;
	}
	
	@Override
	public void setValor(Object valor) {
		if (metaParam== null) return;
		
		modificado = true;
		
		if (metaParam.tipoDato == TipoDato.FORMATO_TXT|| 
				metaParam.tipoDato == TipoDato.FORMATO_URL) {
			valorTexto =  (String) valor;
	
		}
			if (metaParam.tipoDato == TipoDato.FORMATO_INT) valorEntero =  (Integer) valor;
			if (metaParam.tipoDato == TipoDato.FORMATO_REAL || 
					metaParam.tipoDato == TipoDato.FORMATO_MONEDA ||
							metaParam.tipoDato == TipoDato.FORMATO_PORC ) valorReal =  (Float) valor;
			if (isObjeto(metaParam.tipoDato) ) valorObjeto = valor;
			if (metaParam.tipoDato == TipoDato.FORMATO_FECHA) {
				try {
					valorfecha = (Date) valor;
				} catch (Exception e) {
					valorfecha = Date.from(((LocalDate) valor).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());;
				}
				
			}
			if (metaParam.tipoDato == TipoDato.FORMATO_BOOLEAN) 
				if ((Boolean)valor==Constantes.TRUE) valorEntero=Constantes.NUM_TRUE;
				else 								 valorEntero=Constantes.NUM_FALSE;
		
        setChanged();
            
        notifyObservers();
	}

	@Override
	public Propiedad toPropiedad() {
		Object valor = null;
		
		if (metaParam.tipoDato == TipoDato.FORMATO_TXT|| 
				metaParam.tipoDato == TipoDato.FORMATO_URL) valor = valorTexto;
			if (metaParam.tipoDato == TipoDato.FORMATO_INT) valor = valorEntero;
			if (metaParam.tipoDato == TipoDato.FORMATO_REAL || 
					metaParam.tipoDato == TipoDato.FORMATO_MONEDA ||
							metaParam.tipoDato == TipoDato.FORMATO_PORC ) valor = valorReal;
			if (isObjeto(metaParam.tipoDato) ) valor = valorObjeto;
			if (metaParam.tipoDato == TipoDato.FORMATO_FECHA) valor = valorfecha;
			if (metaParam.tipoDato == TipoDato.FORMATO_BOOLEAN) valor = (valorEntero==Constantes.NUM_TRUE)?Constantes.TRUE:Constantes.FALSE;
		
		return new Propiedad(metaParam.categoria, metaParam.nomParametro, metaParam.nomParametro, valor, metaParam.tipoDato, (Propiediable) this);		
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
			if (salida.get("parId")==null) {
				this.id = 0;
			} else this.id = (Integer) salida.get("parId");
			
			this.idEntidadAsociada = (Integer) salida.get("parIdElemento");
			this.codParametro = (String) salida.get("parVlCodParm");
			
			MetaParametro mpp = MetaParametro.listado.get(this.codParametro);
			
			try { this.valorTexto = (String) salida.get("parVlTexto"); } catch (Exception e) {}
			try { this.valorEntero = (Integer) salida.get("parVlEntero");} catch (Exception e) {}
			try { this.valorReal = ((Double) salida.get("parVlReal")).floatValue();} catch (Exception e) {}
			try { this.valorfecha = (Date) FormateadorDatos.parseaDato(salida.get("parVlFx").toString(),FormateadorDatos.FORMATO_FECHA);} catch (Exception e) {}
			
			if (mpp.tipoDato == TipoDato.FORMATO_TIPO_PROYECTO) {
				this.valorObjeto = TipoProyecto.listado.get(this.valorEntero);				
			}
			
			if (mpp.tipoDato == TipoDato.FORMATO_FORMATO_PROYECTO) {
				this.valorObjeto = MetaFormatoProyecto.listado.get(this.valorEntero);				
			}
			
			if (mpp.tipoDato == TipoDato.FORMATO_PROVEEDOR) {
				this.valorObjeto = Proveedor.listadoId.get(this.valorEntero);				
			}
			
			if (mpp.tipoDato == TipoDato.FORMATO_METAJORNADA) {
				this.valorObjeto = MetaJornada.listaMetaJornadas.get(this.valorEntero);				
			}
			
			if (mpp.tipoDato == TipoDato.FORMATO_NAT_COSTE) {
				this.valorObjeto = MetaConcepto.listado.get(this.valorEntero);				
			}
			
			if (mpp.tipoDato == TipoDato.FORMATO_RECURSO) {
				this.valorObjeto = Recurso.listadoRecursosEstatico().get(this.valorEntero);				
			}
			
			if (mpp.tipoDato == TipoDato.FORMATO_TIPO_VCT) {
				this.valorObjeto = TipoCobroVCT.listado.get(this.valorTexto);				
			}
			
			if (mpp.tipoDato == TipoDato.FORMATO_TARIFA) {
				this.valorObjeto = Tarifa.porId(this.valorEntero);				
			}
			
			if (mpp.tipoDato == TipoDato.FORMATO_COBRO_VCT) {
				this.valorObjeto = TipoCobroVCT.listado.get(this.valorTexto);				
			}
			
			if (TipoDato.isEnumerado(mpp.tipoDato)) {
				this.valorObjeto = TipoEnumerado.listadoIds.get(this.valorEntero);				
			}
			
		} catch (Exception e) {
			
		}
		
		return this;
	}
	
	public Parametro getParametro(String codParametro) {
		if (Parametro.listadoParametros==null) {
			Parametro.listadoParametros = this.dameParametros(this.getClass().getSimpleName(), Parametro.SIN_ID_ELEMENTO);
		}
		
		return Parametro.listadoParametros.get(codParametro);
	}
	
	public HashMap<String,Parametro> dameParametros(String entidad, int idElemento) {
		ConsultaBD consulta = new ConsultaBD();
		
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_STR,entidad));
		
		if (idElemento!=-1)
			listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,idElemento));
		
		ArrayList<Cargable> parametros = consulta.ejecutaSQL(this.getQueryConsulta(), listaParms, this);
		
		Iterator<Cargable> itCargable = parametros.iterator();
		HashMap<String, Parametro> salida = new HashMap<String, Parametro> ();
		
		Iterator<MetaParametro> itMetaParam = MetaParametro.listado.values().iterator();
		while (itMetaParam.hasNext()) {
			Parametro par = Propiediable.beanControlador(entidad);
			par.metaParam = itMetaParam.next();
			par.idEntidadAsociada = idElemento;
			par.codParametro = par.metaParam.codParametro;
			if (par.metaParam.entidad.equals(entidad)) 
				salida.put(par.metaParam.codParametro, par);
		}		
		
		while (itCargable.hasNext()) {
			Parametro par = (Parametro) itCargable.next();
			Parametro parAux = salida.get(par.codParametro);
			par.metaParam = parAux.metaParam;
			salida.put(par.metaParam.codParametro,par);						
		}
		
		return salida;
	}
	
	public void actualizaParametro(String idTransaccion, boolean autoejecuta) throws Exception {
		ConsultaBD consulta = new ConsultaBD();
		
		bajaParametro(idTransaccion);
		insertaParametro(idTransaccion);
		
		if (autoejecuta)
			if (idTransaccion!=null)
				consulta.ejecutaTransaccion(idTransaccion);
		
		modificado = false;
	}
	
	public void bajaParametro(String idTransaccion) {
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_STR, this.codParametro));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.idEntidadAsociada));
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL(this.getQueryBorrado(), listaParms, this, idTransaccion);
	}
	
	private void insertaParametro(String idTransaccion){
		ConsultaBD consulta = new ConsultaBD();
				
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_ID,id));
		listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_INT,this.idEntidadAsociada));
		
		if (this.valorTexto == null) this.valorTexto="";
		
		if (TipoDato.FORMATO_TXT==this.metaParam.tipoDato) listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_STR,this.valorTexto));
		if (TipoDato.FORMATO_URL==this.metaParam.tipoDato) listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_STR,this.valorTexto));
		if (TipoDato.FORMATO_INT==this.metaParam.tipoDato) listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_INT,this.valorEntero));
		if (isObjeto(metaParam.tipoDato) ) {
			if (TipoDato.FORMATO_FORMATO_PROYECTO==this.metaParam.tipoDato) this.valorEntero = ((MetaFormatoProyecto) this.valorObjeto).id;
			if (TipoDato.FORMATO_TIPO_PROYECTO==this.metaParam.tipoDato) this.valorEntero = ((TipoProyecto) this.valorObjeto).id;
			if (TipoDato.FORMATO_PROVEEDOR==this.metaParam.tipoDato)	this.valorEntero = ((Proveedor) this.valorObjeto).id;
			if (TipoDato.FORMATO_METAJORNADA==this.metaParam.tipoDato) this.valorEntero = ((MetaJornada) this.valorObjeto).id;
			if (TipoDato.FORMATO_NAT_COSTE==this.metaParam.tipoDato) 	this.valorEntero = ((MetaConcepto) this.valorObjeto).id;
			if (TipoDato.FORMATO_RECURSO==this.metaParam.tipoDato) 		this.valorEntero = ((Recurso) this.valorObjeto).id;
			if (TipoDato.FORMATO_TARIFA==this.metaParam.tipoDato) 		this.valorEntero = ((Tarifa) this.valorObjeto).idTarifa;
			if (TipoDato.FORMATO_COBRO_VCT==this.metaParam.tipoDato) {
				this.valorTexto = ((TipoCobroVCT) this.valorObjeto).codigo;
				listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_STR,this.valorTexto));
			}			
			listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_INT,this.valorEntero));
		}
		if (TipoDato.isEnumerado(this.metaParam.tipoDato)) 	{
			this.valorEntero = ((TipoEnumerado) this.valorObjeto).id;
			listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_INT,this.valorEntero));
		}
		if (TipoDato.FORMATO_REAL==this.metaParam.tipoDato) listaParms.add(new ParametroBD(5,ConstantesBD.PARAMBD_REAL,this.valorReal));
		if (TipoDato.FORMATO_MONEDA==this.metaParam.tipoDato) listaParms.add(new ParametroBD(5,ConstantesBD.PARAMBD_REAL,this.valorReal));
		if (TipoDato.FORMATO_FECHA==this.metaParam.tipoDato) listaParms.add(new ParametroBD(6,ConstantesBD.PARAMBD_FECHA,this.valorfecha));
		if (TipoDato.FORMATO_BOOLEAN==this.metaParam.tipoDato) listaParms.add(new ParametroBD(4,ConstantesBD.PARAMBD_INT,this.valorEntero));

		listaParms.add(new ParametroBD(7,ConstantesBD.PARAMBD_STR,this.codParametro));
		
		consulta = new ConsultaBD();
		consulta.ejecutaSQL(this.getQueryInsercion(), listaParms, null,idTransaccion);
	}
	
	public boolean validaParametros(HashMap<String, ? extends Parametro> parametros) {
		if (parametros!=null && parametros.size()!=0) {
			boolean validacion = true;
			Iterator<? extends Parametro> itParametro = parametros.values().iterator();
			while (itParametro.hasNext()) {
				Parametro pAux = itParametro.next();
				validacion = validacion && pAux.validaParametro();
				if (!validacion) return validacion; 
			}
			
			return validacion;
			
		} else return true;
	}
	
	public boolean validaParametro () {
		if (this.metaParam.obligatorio) {
			if (metaParam.tipoDato == TipoDato.FORMATO_TXT|| 
					metaParam.tipoDato == TipoDato.FORMATO_URL) 
					if ("".equals(valorTexto) || valorTexto==null) return false;
					else return true;
			if (isObjeto(metaParam.tipoDato) ) {
				if (this.valorObjeto==null) return false;
				else return true;
			}
			if (metaParam.tipoDato == TipoDato.FORMATO_FECHA) {
				if (this.valorfecha==null) return false;
				else return true;
			} 
			return true;
		} else return true;
	}

}
