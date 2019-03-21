package model.metadatos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.constantes.Constantes;
import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.db.ConsultaBD;

public class MetaParametro implements Cargable, Loadable {

	public static final String PROYECTO_PERFIL_ECONOMICO = "PROY.perfEconomico";
	public static final String PROYECTO_TIPO_PROYECTO = "PROY.tpProyecto";
	public static final String PROYECTO_FX_INICIO = "PROY.fxInicio";
	public static final String PROYECTO_FX_FIN = "PROY.fxFin";
	
	public static final String FASE_PROYECTO_FX_IMPLANTACION = "F.PR.fxImplantacion";
	
	public static final String FASES_COBERTURA_DEMANDA = "F.PR.SIS.DEM.cobertura";
	
	public static final String RECURSO_JORNADA = "REC.jornada";
	public static final String RECURSO_NAT_COSTE = "REC.natCoste";
	public static final String RECURSO_COD_USUARIO = "REC.codUsuario";
	public static final String RECURSO_COD_GESTOR = "REC.codGestor";
	
	public static final String PARAMETRO_ADMIN = "admin";
	public static final String PARAMETRO_ECONOMICO_PERINTRACERT = "perIntraCert";	
	public static final String PARAMETRO_ECONOMICO_TIPOCOBROESTANDARVCT = "tipoCobroEstandVCT";	
	
	
	public int id =0;
	public String entidad = "";
	public String codParametro = "";
	public String nomParametro = "";
	public String categoria = "";
	public boolean obligatorio = false;
	public int tipoDato = 0;
	
	public static HashMap<String, MetaParametro> listado = null;
	
	public MetaParametro() {
	}
	
	@Override
	public Object clone() {
		MetaParametro mp = new MetaParametro();
		
		mp.id = this.id;
		mp.entidad = this.entidad;
		mp.codParametro = this.codParametro;
		mp.categoria = this.categoria;
		mp.obligatorio = this.obligatorio;
		mp.tipoDato = this.tipoDato;
		mp.nomParametro = this.nomParametro;
		
		return mp;
	}
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		this.id = (Integer) salida.get("tipPId");
		this.entidad = (String) salida.get("tipPEntidad");
		this.codParametro = (String) salida.get("tipPCodParm");
		this.categoria = (String) salida.get("tipPCategoria");
		this.obligatorio = (Constantes.NUM_TRUE == (Integer) salida.get("tipPObligatorio"))?true:false;
		this.tipoDato = (Integer) salida.get("tipPTipoDato");
		this.nomParametro = (String) salida.get("tipPNomParm");
	
		return this;
	}

	@Override
	public void load() {
		listado = new HashMap<String, MetaParametro>();	
			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Cargable> estados = consulta.ejecutaSQL("cConsultaTipoParametro", null, this);
		
		Iterator<Cargable> it = estados.iterator();
		while (it.hasNext()){
			MetaParametro est = (MetaParametro) it.next();
			listado.put(est.codParametro, est);
		}
	}
	
	@Override
	public String toString() {
		return this.nomParametro;
	}
	
	@Override
	public HashMap<?, ?> getListado() {
		return MetaParametro.listado;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

}
