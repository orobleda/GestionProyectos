package model.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;
import model.metadatos.MetaConcepto;
import model.metadatos.Sistema;
import model.metadatos.TipoDato;
import model.metadatos.TipoEnumerado;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class FotoDetalle implements Cargable{
	public Foto foto;
	public int idFoto;
	public Sistema sistema;
	public double valor;
	public int mes;
	public int id;
	public int anio;
	public MetaConcepto tipoConcepto;
	public String nomCertificacion;
	public FaseProyecto fase;
	public int idFase;
	public Date fxCertificacion;
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
		 	if (salida.get("detIdfoto")==null)  { 
		 		this.idFoto = 0;
			} else {
		 		this.idFoto = (Integer) salida.get("detIdfoto");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("detIdsistema")==null)  { 
		 		this.sistema = null;
			} else {
		 		int idSistema = (Integer) salida.get("detIdsistema");
		 		this.sistema = Sistema.listado.get(idSistema);
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("detValor")==null)  { 
		 		this.valor = 0;
			} else {
		 		this.valor = (Double) salida.get("detValor");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("detMes")==null)  { 
		 		this.mes = 0;
			} else {
		 		this.mes = (Integer) salida.get("detMes");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("detId")==null)  { 
		 		this.id = 0;
			} else {
		 		this.id = (Integer) salida.get("detId");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("detAnio")==null)  { 
		 		this.anio = 0;
			} else {
		 		this.anio = (Integer) salida.get("detAnio");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("detTipoconcepto")==null)  { 
		 		this.tipoConcepto = null;
			} else {
		 		this.tipoConcepto = MetaConcepto.porId((Integer) salida.get("detTipoconcepto"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
		 	if (salida.get("detFase")==null)  { 
		 		this.idFase = 0;
			} else {
		 		this.idFase = (Integer) salida.get("detFase");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("detNomCertificacion")==null)  { 
		 		this.nomCertificacion = null;
			} else {
		 		this.nomCertificacion = (String) salida.get("detNomCertificacion");
			}
		} catch (Exception ex) {}
		try {
		 	if (salida.get("detFxCertificacion")==null)  { 
		 		this.fxCertificacion = null;
			} else {
		 		this.fxCertificacion = (Date) FormateadorDatos.parseaDato((String)salida.get("detFxCertificacion"),TipoDato.FORMATO_FECHA);
			}
		} catch (Exception ex) {}
				
		return this;
	}
	
	public ArrayList<FotoDetalle> buscaDetalle() {			
		ConsultaBD consulta = new ConsultaBD();
		ArrayList<FotoDetalle> salida = new ArrayList<FotoDetalle>();

		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,foto.id));
		
		ArrayList<Cargable> conceptos = consulta.ejecutaSQL("cConsultaDetalle_foto", listaParms, this);
		
		Iterator<Cargable> itCOnceptos = conceptos.iterator();
		
		while (itCOnceptos.hasNext()) {
			FotoDetalle fd = (FotoDetalle) itCOnceptos.next();
			fd.foto = this.foto;
			salida.add(fd);
		}
		
		return salida;
	}
	
	public void borrarDetalle(String idTransaccion) {			
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1,ConstantesBD.PARAMBD_INT,this.idFoto));
		
		ConsultaBD consulta = new ConsultaBD();
		consulta.ejecutaSQL("dBorraDetalle_foto", listaParms, this, idTransaccion);		
	}
	
    public void insertFotoDetalle(String idTransaccion)  throws Exception{
		
		ConsultaBD consulta = new ConsultaBD();
			
		ArrayList<ParametroBD> listaParms = new ArrayList<ParametroBD>();
		listaParms.add(new ParametroBD(1, ConstantesBD.PARAMBD_INT, this.foto.id));
		listaParms.add(new ParametroBD(2, ConstantesBD.PARAMBD_INT, this.sistema.id));
		listaParms.add(new ParametroBD(3, ConstantesBD.PARAMBD_REAL, new Double(this.valor).floatValue()));
		listaParms.add(new ParametroBD(4, ConstantesBD.PARAMBD_INT, this.mes));
		listaParms.add(new ParametroBD(5, ConstantesBD.PARAMBD_ID, this.id));
		listaParms.add(new ParametroBD(6, ConstantesBD.PARAMBD_INT, this.anio));
		listaParms.add(new ParametroBD(7, ConstantesBD.PARAMBD_INT, this.tipoConcepto.id));
		
		if (this.tipoConcepto.id == MetaConcepto.DESARROLLO){
			listaParms.add(new ParametroBD(8, ConstantesBD.PARAMBD_INT, this.fase.id));
			listaParms.add(new ParametroBD(9, ConstantesBD.PARAMBD_STR, this.nomCertificacion));
			listaParms.add(new ParametroBD(10, ConstantesBD.PARAMBD_FECHA, this.fxCertificacion));			
		}
		consulta = new ConsultaBD();
		consulta.ejecutaSQL("iInsertaDetalle_foto", listaParms, this, idTransaccion);
		
		this.id = ParametroBD.ultimoId;
	}
}
