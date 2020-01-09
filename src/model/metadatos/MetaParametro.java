package model.metadatos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.beans.Parametro;
import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.interfaces.Loadable;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;
import ui.interfaces.Propiediable;

public class MetaParametro implements Cargable, Loadable {
	
	public static final String FASE_PROYECTO_FX_IMPLANTACION = "F.PR.fxImplantacion";
	public static final String FASE_PROYECTO_SISTEMA_FX_DIFERIDA = "F.PR.SIS.fx_diferida";
	
	public static final String FASES_COBERTURA_DEMANDA = "F.PR.SIS.DEM.cobertura";

	public static final String PARAMETRO_ADMIN = "admin";
	public static final String PARAMETRO_FIJAR_FX_ACTUAL = "FX_ACTUAL_MOD";
	public static final String PARAMETRO_FX_ACTUAL_FIJADA = "FX_ACTUAL_FIJA";
	public static final String PARAMETRO_ECONOMICO_PERINTRACERT = "perIntraCert";	
	public static final String PARAMETRO_ECONOMICO_TIPOCOBROESTANDARVCT = "tipoCobroEstandVCT";
	public static final String PARAMETRO_RUTA_REPOSITORIO = "repoProy";	
	public static final String PARAMETRO_PORC_TREI_GGP = "PORC_TREI_GGP";
	public static final String PARAMETRO_TARIFA_DEFECTO_INTERNAS = "PAR_TAR_DEF_INT";	
	public static final String PARAMETRO_TARIFA_DEFECTO_SATAD = "PAR_TAR_DEF_SATAD";
	public static final String PARAMETRO_TARIFA_DEFECTO_CC = "PAR_TAR_DEF_CC";	
	public static final String PARAMETRO_TARIFA_DEFECTO_DES = "PAR_TAR_DEF_DES";
	public static final String PARAMETRO_REPO_BD_SEG = "PARM_REPO_BD_SEG";	
	public static final String PARAMETRO_OCURR_COPIA_BD = "PARM.OCURR_COPIA_BD";
	public static final String PARAMETRO_HOR_REC_ANIO = "hor.Rec.Anio";
	
	public static final String PARAMETRO_SISTEMA_PROVEEDOR = "SIST.Proveedor";
	
	public static final String PROVEEDOR_CODPPM = "PROV.NOMPPM";
	public static final String PROVEEDOR_TARIFA_DEFECTO = "PROV.TAR_DEF";
	
	public static final String PROYECTO_PERFIL_ECONOMICO = "PROY.perfEconomico";
	public static final String PROYECTO_TIPO_PROYECTO = "PROY.tpProyecto";
	public static final String PROYECTO_FX_INICIO = "PROY.fxInicio";
	public static final String PROYECTO_FX_FIN = "PROY.fxFin";
	public static final String PROYECTO_FX_FIN_IMPL = "PROY.fxFinImpl";
	public static final String PROYECTO_CERRADO = "PROY.CERRADO";
	public static final String PROYECTO_NOMPPM = "PROY.NOMPPM";
	public static final String PROYECTO_ACRONPROY = "PROY.ACRON";
	public static final String PROYECTO_CODPPM = "PROY.codPPM";
	public static final String PROYECTO_JP = "PROY.JP";
	
	public static final String RECURSO_JORNADA = "REC.jornada";
	public static final String RECURSO_NAT_COSTE = "REC.natCoste";
	public static final String RECURSO_COD_USUARIO = "REC.codUsuario";
	public static final String RECURSO_COD_GESTOR = "REC.codGestor";
	public static final String RECURSO_NOMBRE_REAL = "REC.PERSONA";
	public static final String RECURSO_COD_PPM = "REC.NOMPPM";
	public static final String RECURSO_PROVEEDOR = "REC.PROVEEDOR";
	public static final String RECURSO_TAR_DEF = "REC.TAR_DEF";	

	public static final String FOTO_COD_PORFOLIO = "FOTO.COD_PORFOLIO";
	public static final String FOTO_FX_PPM = "FOTO.EN_PPM";
	
	public static final String CERTIFICACION_DESCRIPCION = "CERT.desc";
	public static final String CERTIFICACION_FASE_TIPOVCT = "CERFAS.TipoVCT";
		
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
	
	public static HashMap<String,Parametro> dameParametros(String entidad, int idElemento) {
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
				
		return salida;
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
