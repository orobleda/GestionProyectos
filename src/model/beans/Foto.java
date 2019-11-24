package model.beans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import controller.AnalizadorPresupuesto;
import controller.Log;
import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import model.metadatos.TipoDato;
import model.metadatos.TipoEnumerado;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class Foto implements Cargable{
	public int idProyecto;
	public Proyecto proyecto;
	public TipoEnumerado tipo;
	public boolean persistir;
	public Date fxCreacion;
	public int id;
	public String nombreFoto;
	public ArrayList<FotoDetalle> lDetalles = null;	
	public double valorTotal;
	public double valorAnioCurso;
	
	public HashMap<String,Parametro> listaParametros = null;

	public static int TIPO_FOTO_PLANI = 14;	
	
	public static int ANIOS_ANTERIORES = -1;
	public static int ANIO_CURSO = 0;
	public static int ANIOS_SIGUIENTES = 1;
	public static int TODO = 2;
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
		 	if (salida.get("fotIdproyecto")==null)  { 
		 		this.proyecto = null;
			} else {
				this.idProyecto = (Integer) salida.get("fotIdproyecto");
		 		this.proyecto = Proyecto.getProyectoEstatico((Integer) salida.get("fotIdproyecto"));
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("fotTipo")==null)  { 
		 		this.tipo = null;
			} else {
		 		this.tipo = TipoEnumerado.getValores(TipoDato.FORMATO_TIPO_FOTO).get((Integer) salida.get("fotTipo"));
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("fotPersistir")==null)  { 
		 		this.persistir = Constantes.FALSE;
			} else {
		 		this.persistir = Constantes.toNumBoolean((Integer) salida.get("fotPersistir"));
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("fotFxcreacion")==null)  { 
		 		this.fxCreacion = null;
			} else {
		 		this.fxCreacion = (Date) FormateadorDatos.parseaDato(salida.get("fotFxcreacion").toString(),FormateadorDatos.FORMATO_FECHA);
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("fotId")==null)  { 
		 		this.id = -1;
			} else {
		 		this.id = (Integer) salida.get("fotId");
		 		Parametro p = new Parametro();
		 		this.listaParametros = p.dameParametros(this.getClass().getSimpleName(), this.id);
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("fotNombrefoto")==null)  { 
		 		this.nombreFoto = "";
			} else {
		 		this.nombreFoto = (String) salida.get("fotNombrefoto");
			}
		} catch (Exception ex) {}
		
		try {
		 	if (salida.get("fotValAnioCurso")==null)  { 
		 		this.valorAnioCurso = 0;
			} else {
		 		this.valorAnioCurso = (Double) salida.get("fotValAnioCurso");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("fotValTotal")==null)  { 
		 		this.valorTotal = 0;
			} else {
		 		this.valorTotal = (Double) salida.get("fotValTotal");
			}
		} catch (Exception ex) {}
		
		return this;
	}
	
	public ArrayList<Foto> buscaFotos(Integer idFoto) {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<Foto> salida = new ArrayList<Foto>();

		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		if (this.proyecto!=null)
			listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.proyecto.id));
		if (idFoto!=null)
			listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, idFoto));
		
		ArrayList<Cargable> conceptos = consulta.ejecutaSQL("cConsultaFoto", listaParms, this);
		
		Iterator<Cargable> itCOnceptos = conceptos.iterator();
		
		while (itCOnceptos.hasNext()) {
			Foto ft = (Foto) itCOnceptos.next();
			
			FotoDetalle fd = new FotoDetalle();
			fd.foto = ft;
			ft.lDetalles = fd.buscaDetalle();
			
			salida.add(ft);
		}
		
		return salida;
	}
	
	public HashMap<Integer,Foto> buscaFotosPorfolio(String porfolio) {			
		HashMap<Integer,Foto> salida = new HashMap<Integer,Foto>();

		Parametro p = new Parametro();
    	ArrayList<Parametro> lParametros = p.getParametro(MetaParametro.FOTO_COD_PORFOLIO, porfolio);
    	
    	Iterator<Parametro> itParametros = lParametros.iterator();
    	while (itParametros.hasNext()) {
    		p = itParametros.next();
    		
    		ArrayList<Foto> foto = this.buscaFotos(p.idEntidadAsociada);
    		if (foto!=null && foto.size()>0) {
    			salida.put(new Integer(foto.get(0).idProyecto), foto.get(0));
    		}
    	}
		
		return salida;
	}
	
	public ArrayList<Sistema> getSistemas() {
		HashMap<String, Sistema> sistemas = new HashMap<String, Sistema>();
		
		Iterator<FotoDetalle> itDetalles = this.lDetalles.iterator();
		while (itDetalles.hasNext()) {
			FotoDetalle ft = itDetalles.next();
			if (!sistemas.containsKey(ft.sistema.codigo)) {
				sistemas.put(ft.sistema.codigo, ft.sistema);
			}
		}
		
		ArrayList<Sistema> salida = new ArrayList<Sistema>();
		salida.addAll(sistemas.values());
		
		return salida;
	}
	
	public double getAcumulado(int indAnios, MetaConcepto mc, Sistema s) {
		Calendar c = Calendar.getInstance();
		c.setTime(Constantes.fechaActual());
		
		double acumulado = 0;
		
		Iterator<FotoDetalle> itDetalles = this.lDetalles.iterator();
		while (itDetalles.hasNext()) {
			FotoDetalle ft = itDetalles.next();
			
			if (s.codigo.equals(ft.sistema.codigo) &&
					(mc.id == MetaConcepto.ID_TOTAL || ft.tipoConcepto.id == mc.id)) {
				if ((c.get(Calendar.YEAR)> ft.anio && indAnios == Foto.ANIOS_ANTERIORES) ||
						(c.get(Calendar.YEAR)== ft.anio && indAnios == Foto.ANIO_CURSO) ||
						(c.get(Calendar.YEAR)< ft.anio && indAnios == Foto.ANIOS_SIGUIENTES)||
						(indAnios == Foto.TODO))
					acumulado += ft.valor;
			}
		}
		
		return acumulado;
	}
	
	public void updateFoto(String idTransaccion) throws Exception{			
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.id));
		listaParms.add(new ParametroBD(2,ConstantesBD.PARAMBD_STR,this.nombreFoto));
		listaParms.add(new ParametroBD(3,ConstantesBD.PARAMBD_INT,Constantes.toNumBoolean(this.persistir)));
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("uActualizaFoto", listaParms, this, idTransaccion);	
		
		Iterator<Parametro> itParam = this.listaParametros.values().iterator();
		while (itParam.hasNext()) {
			Parametro param = itParam.next();
			param.actualizaParametro(idTransaccion, false);
		}
		
	}
	
	public void borrarFoto(String idTransaccion) throws Exception{			
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.id));
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("dBorraFoto", listaParms, this, idTransaccion);	
		
		Iterator<FotoDetalle> ifd = this.lDetalles.iterator();
		while(ifd.hasNext()) {
			FotoDetalle fd = ifd.next();
			fd.borrarDetalle(idTransaccion);
		}
		
		Iterator<Parametro> itParam = this.listaParametros.values().iterator();
		while (itParam.hasNext()) {
			Parametro param = itParam.next();
			param.bajaParametro(idTransaccion);
		}
	}
	
    public void insertFotoDetalle(String idTransaccion)  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
		
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.proyecto.id));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.tipo.id));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_INT, Constantes.toNumBoolean(this.persistir)));
		listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_FECHA, this.fxCreacion));
		listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_ID, this.id));
		listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_STR, this.nombreFoto));
		listaParms.add(new ParametroBD(7, ConstantesBD.PARAMBD_REAL, this.valorAnioCurso));
		listaParms.add(new ParametroBD(8, ConstantesBD.PARAMBD_REAL, this.valorTotal));
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("iInsertaFoto", listaParms, this, idTransaccion);
		
		this.id = ParametroBD.ultimoId;
		
		Iterator<FotoDetalle> ifd = this.lDetalles.iterator();
		while(ifd.hasNext()) {
			FotoDetalle fd = ifd.next();
			fd.foto = this;
			fd.insertFotoDetalle(idTransaccion);
		}
		
		Iterator<Parametro> itParam = this.listaParametros.values().iterator();
		while (itParam.hasNext()) {
			Parametro param = itParam.next();
			param.idEntidadAsociada = this.id;
			param.actualizaParametro(idTransaccion, false);
		}		
	}
    
    public Foto hacerFoto(AnalizadorPresupuesto ap) throws Exception{
    	float acumuladoAnio= 0;
    	float acumulado = 0;
    	
    	Foto f = new Foto();
		Parametro p = new Parametro();
		f.listaParametros = MetaParametro.dameParametros(f.getClass().getSimpleName(), -1);
		f.fxCreacion = new Date();
		f.id = -1;
		f.idProyecto = ap.proyecto.id;
		f.proyecto = ap.proyecto;
		f.lDetalles = new ArrayList<FotoDetalle>();
		f.nombreFoto = " ("+f.fxCreacion+")";
		f.persistir = Constantes.TRUE;
		f.tipo = TipoEnumerado.getValores(TipoDato.FORMATO_TIPO_FOTO).get(Foto.TIPO_FOTO_PLANI);	
		
		Calendar cActual = Calendar.getInstance();
		cActual.setTime(f.fxCreacion);
		
		Iterator<EstimacionAnio> listaEa = ap.estimacionAnual.iterator();
		
		while (listaEa.hasNext()) {
			EstimacionAnio ea = listaEa.next();
			Iterator<EstimacionMes> itEm = ea.estimacionesMensuales.values().iterator();
			
			while (itEm.hasNext()) {
				EstimacionMes em = itEm.next();
				Iterator<Sistema> itSistema = em.estimacionesPorSistemas.values().iterator();
				
				while (itSistema.hasNext()) {
					Sistema s = itSistema.next();
					
					HashMap<String, Concepto> lAcumCon = em.acumuladoPorSistemaConcepto(s);
					
					Iterator<Concepto> itConcepto = lAcumCon.values().iterator();
					while (itConcepto.hasNext()) {
						Concepto c = itConcepto.next();
						
						if (c.tipoConcepto.tipoGestionEconomica == MetaConcepto.GESTION_HORAS){
							FotoDetalle fd = new FotoDetalle();
							fd.anio = ea.anio;
							fd.foto = f;
							fd.id = -1;
							fd.idFoto = f.id;
							fd.mes = em.mes;
							fd.sistema = s;
							fd.tipoConcepto = c.tipoConcepto;
							
							try {
								fd.valor = c.listaEstimaciones.get(0).importe;
								acumulado += fd.valor;
								if (fd.anio == cActual.get(Calendar.YEAR))
									acumuladoAnio += fd.valor;
								if (fd.valor!=0) {
									f.lDetalles.add(fd);
								}
							} catch (Exception e) {
								Log.e(e);
							}
						}										
					}
				}				
			}
		}
		
		Iterator<Certificacion> iCertificacion = ap.certificaciones.iterator();
		
		while (iCertificacion.hasNext()) {
			Certificacion cert = iCertificacion.next();
			Iterator<CertificacionFase> iCf = cert.certificacionesFases.iterator();
			while (iCf.hasNext()) {
				CertificacionFase cf = iCf.next();
				Iterator<CertificacionFaseParcial> iCfp = cf.certificacionesParciales.iterator();
				while (iCfp.hasNext()) {
					CertificacionFaseParcial cfp = iCfp.next();
					
					Calendar c = Calendar.getInstance();
					c.setTime(cfp.fxCertificacion);
					
					FotoDetalle fd = new FotoDetalle();
					fd.anio = c.get(Calendar.YEAR);
					fd.foto = f;
					fd.id = -1;
					fd.idFoto = f.id;
					fd.mes = c.get(Calendar.MONTH)+1;
					fd.sistema = cert.s;
					fd.tipoConcepto = MetaConcepto.porId(MetaConcepto.DESARROLLO);
					fd.fase = cf.fase;
					fd.nomCertificacion = cfp.nombre;
					fd.fxCertificacion = cfp.fxCertificacion;
										
					try {
						fd.valor = cfp.valEstimado;
						acumulado += fd.valor;
						if (fd.anio == cActual.get(Calendar.YEAR))
							acumuladoAnio += fd.valor;
						if (fd.valor!=0) {
							f.lDetalles.add(fd);
						}
					} catch (Exception e) {
						Log.e(e);
					}
					
				}
			}
		}
		
		f.valorAnioCurso = acumuladoAnio;
		f.valorTotal = acumulado;
		String idTransaccion = ConsultaBD.getTicket();
		f.insertFotoDetalle(idTransaccion);
		ConsultaBD.ejecutaTicket(idTransaccion);
		
		return f;
	}
    
    public HashMap<Integer,HashMap<Integer,HashMap<String,HashMap<String,Concepto>>>> construyeArbol() {
    	HashMap<Integer,HashMap<Integer,HashMap<String,HashMap<String,Concepto>>>> salida = new HashMap<Integer,HashMap<Integer,HashMap<String,HashMap<String,Concepto>>>>();
    	
    	Iterator<FotoDetalle> ifd = this.lDetalles.iterator();
    	while (ifd.hasNext()) {
    		FotoDetalle fd = ifd.next();
    		
    		HashMap<Integer,HashMap<String,HashMap<String,Concepto>>> mesFoto = null;
    		
    		if (!salida.containsKey(fd.anio)) {
    			mesFoto = new HashMap<Integer,HashMap<String,HashMap<String,Concepto>>>();
    			salida.put(fd.anio,mesFoto);
    		} else {
    			mesFoto = salida.get(fd.anio);
    		}
    		
    		HashMap<String,HashMap<String,Concepto>> lSistema = null;
    		
    		if (!mesFoto.containsKey(fd.mes)) {
    			lSistema = new HashMap<String,HashMap<String,Concepto>>();
    			mesFoto.put(fd.mes,lSistema);
    		} else {
    			lSistema = mesFoto.get(fd.mes);
    		}
    		
    		HashMap<String,Concepto> lConceptos = null;
    		
    		if (!lSistema.containsKey(fd.sistema.codigo)) {
    			lConceptos = new HashMap<String,Concepto>();
    			lSistema.put(fd.sistema.codigo,lConceptos);
    		} else {
    			lConceptos = lSistema.get(fd.sistema.codigo);
    		}
    		
    		Concepto c = null;
    		
    		if (!lConceptos.containsKey(fd.tipoConcepto.codigo)) {
    			c = new Concepto();
    			c.tipoConcepto = fd.tipoConcepto;
    			lConceptos.put(fd.tipoConcepto.codigo,c);
    		} else {
    			c = lConceptos.get(fd.tipoConcepto.codigo);
    		}
    		
    		if (c.tipoConcepto.tipoGestionEconomica == MetaConcepto.GESTION_HORAS) {
    			Estimacion e = new Estimacion();
    			e.importe = new Double(fd.valor).floatValue();
    			c.listaEstimaciones = new ArrayList<Estimacion>();
    			c.listaEstimaciones.add(e);
    		} else {
    			if (c.listaCertificaciones==null) {
    				c.listaCertificaciones = new ArrayList<CertificacionFaseParcial>();
    				CertificacionFaseParcial cfp = new CertificacionFaseParcial();
    				cfp.valEstimado = new Double(fd.valor).floatValue();
    				cfp.nombre = fd.nomCertificacion;
    				cfp.fxCertificacion = fd.fxCertificacion;
    				c.listaCertificaciones.add(cfp);
    			}
    		}
    	}
    	
    	return salida;
    }
    
    @Override
    public String toString() {
    	return nombreFoto;
    }
}
