package model.utils.xls.informes;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import controller.AnalizadorPresupuesto;
import model.beans.Estimacion;
import model.beans.JornadasMes;
import model.beans.Parametro;
import model.beans.ParametroProyecto;
import model.beans.ParametroRecurso;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.beans.Recurso;
import model.beans.VacacionesAusencias;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaParametro;
import model.metadatos.TipoDato;

public class ReporteHorasMes extends InformeGenerico{

	HashMap<String, HojaExcel> sheets = null;
	
	HojaExcel hojaResumen = null;
	int mes = 0;
	int anio = 0;
	MetaConcepto mc = null;
	
	public static final String MES = "mes";
	public static final String ANIO = "ANIO";
	public static final String METAC = "MetaConcepto";
	
	public static final String ANCLA_RESUMEN = "ANCLA_RESUMEN";
	
	HashMap<Integer,HashMap<String,PosicionExcel>> anclas = null;
	HashMap<Integer,Float> lRestante = new HashMap<Integer,Float>();
	
	@Override
	public void escribeFichero(HashMap<String, Object> datos) throws Exception {
		if (newExcelFile==null) return;
		
		anclas = new HashMap<Integer,HashMap<String,PosicionExcel>>();
		
		OutputStream excelNewOutputStream = null;
        excelNewOutputStream = new FileOutputStream(newExcelFile);
        
        worbook = new XSSFWorkbook();
        new EstiloCelda(worbook);
        
        HojaExcel xssfSheetResumen = new HojaExcel(worbook, "Resumen");
        
        sheets = new HashMap<String, HojaExcel>();
        
        mes = (Integer) datos.get(ReporteHorasMes.MES);
        anio = (Integer) datos.get(ReporteHorasMes.ANIO);
        mc = (MetaConcepto) datos.get(ReporteHorasMes.METAC);
        
        pintaHojaResumen(xssfSheetResumen);
                
        worbook.write(excelNewOutputStream);
        
        excelNewOutputStream.close();
	}
	
	private void pintaHojaResumen(HojaExcel xssfSheetResumen) throws Exception{
		XSSFCell celdaInicio = xssfSheetResumen.get(1, 1);
		
		celdaInicio.setCellType(CellType.STRING);
		celdaInicio.setCellValue("Nombre");
		celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
		
		celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
		celdaInicio.setCellValue("Tipo");
		celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
		
		int contador = 2;
		
		Calendar c = Calendar.getInstance();
		c.setTime(Constantes.fechaActual());
		for (int i=0;i<c.getActualMaximum(Calendar.DAY_OF_MONTH);i++) {
			celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
			celdaInicio.setCellValue(i+1);
			celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
			contador++;
		}
		
		celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
		celdaInicio.setCellValue("Total");
		celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
		contador++;
		
		Recurso r = new Recurso();
		ArrayList<Recurso> listaRecurso = r.listadoRecursos();
		Iterator<Recurso> itRecurso = listaRecurso.iterator();
		
		celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, -contador+1);
		
		while (itRecurso.hasNext()) {
			r = itRecurso.next();
			r.cargaRecurso();
			
			ParametroRecurso pGestorRecurso = ((ParametroRecurso) r.getValorParametro(MetaParametro.RECURSO_COD_GESTOR));
			Recurso gestorRecurso = ((Recurso) pGestorRecurso.getValor());
			ParametroRecurso pNatCoste = ((ParametroRecurso) r.getValorParametro(MetaParametro.RECURSO_NAT_COSTE));
			
			if (gestorRecurso!=null && gestorRecurso.id == Constantes.getAdministradorSistema().id && pNatCoste.getValor().equals(this.mc)) {
				pGestorRecurso = ((ParametroRecurso) r.getValorParametro(MetaParametro.RECURSO_NOMBRE_REAL));
				
				celdaInicio = xssfSheetResumen.offset(celdaInicio, 1, 0);
				celdaInicio.setCellValue(pGestorRecurso.getValor().toString());
				xssfSheetResumen.hoja.setColumnWidth(celdaInicio.getColumnIndex(), 5000);
				celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.GRIS, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
				
				VacacionesAusencias vacasAusencias = null;
				vacasAusencias = new VacacionesAusencias();
				vacasAusencias.listado(r, mes, anio);
				
				celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
				celdaInicio.setCellValue("Horas Jornada");
				xssfSheetResumen.hoja.setColumnWidth(celdaInicio.getColumnIndex(), 3600);
				celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.GRIS, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
				
				JornadasMes jmJornadas = new JornadasMes(JornadasMes.JORNADAS, r, mes, anio, vacasAusencias);
				
				Iterator<Float> itHoras = jmJornadas.jornadas.iterator();
				int contadorDias = 1;
				float sumaJornadas = 0;
				
				while (itHoras.hasNext()) {
					Float hora = itHoras.next();
					
					celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
					xssfSheetResumen.hoja.setColumnWidth(celdaInicio.getColumnIndex(), 1000);
					if (hora !=-1) {
						celdaInicio.setCellValue(FormateadorDatos.formateaDato(hora, TipoDato.FORMATO_REAL));
						celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(-1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
						sumaJornadas += hora;
					} else {
						Calendar cAux = Calendar.getInstance();
						cAux.set(anio, mes-1, contadorDias);
						if (cAux.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cAux.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
							celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(-2, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
						else
							celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(-2, EstiloCelda.NARANJA, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
					}
					
					contadorDias++;
				}
				
				celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
				xssfSheetResumen.hoja.setColumnWidth(celdaInicio.getColumnIndex(), 2000);
				celdaInicio.setCellValue(FormateadorDatos.formateaDato(sumaJornadas, TipoDato.FORMATO_REAL));
				celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
				
				celdaInicio = xssfSheetResumen.offset(celdaInicio, 1, -contador+2);
				celdaInicio.setCellValue("Vacaciones");
				celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.GRIS, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
								
				JornadasMes jm = new JornadasMes(JornadasMes.VACACIONES, r, mes, anio, vacasAusencias);
				
				itHoras = jm.jornadas.iterator();
				contadorDias = 1;
				sumaJornadas = 0;
				
				while (itHoras.hasNext()) {
					Float hora = itHoras.next();
					
					celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
					if (hora !=-2 && hora !=-1) {
						celdaInicio.setCellType(CellType.FORMULA);
						celdaInicio.setCellFormula("-1*" + xssfSheetResumen.posicion(xssfSheetResumen.offset(celdaInicio, -1, 0)));
						celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(-1, EstiloCelda.VERDE, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
						sumaJornadas += jmJornadas.jornadas.get(contadorDias);;
					} else {
						Calendar cAux = Calendar.getInstance();
						cAux.set(anio, mes-1, contadorDias);
						if (cAux.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cAux.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
							celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(-2, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
						else{
							if (jmJornadas.jornadas.get(contadorDias-1)==-1)
								celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(-2, EstiloCelda.NARANJA, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
							else
								celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(-1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
						}
					}
					contadorDias++;
				}
				
				celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
				celdaInicio.setCellValue(FormateadorDatos.formateaDato(sumaJornadas, TipoDato.FORMATO_REAL));
				celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
				
				celdaInicio = xssfSheetResumen.offset(celdaInicio, 1, -contador+2);
				celdaInicio.setCellValue("Ausencias");
				celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.GRIS, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
								
				jm = new JornadasMes(JornadasMes.AUSENCIAS, r, mes, anio, vacasAusencias);
				
				itHoras = jm.jornadas.iterator();
				contadorDias = 1;
				sumaJornadas = 0;
				
				while (itHoras.hasNext()) {
					Float hora = itHoras.next();
					
					celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
					if (hora >0) {
						celdaInicio.setCellValue(FormateadorDatos.formateaDato(-1*hora, TipoDato.FORMATO_REAL));
						celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(-1, EstiloCelda.VERDE, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
						sumaJornadas += hora;
					} else {
						Calendar cAux = Calendar.getInstance();
						cAux.set(anio, mes-1, contadorDias);
						if (cAux.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cAux.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
							celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(-2, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
						else{
							if (jmJornadas.jornadas.get(contadorDias-1)==-1)
								celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(-2, EstiloCelda.NARANJA, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
							else
								celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(-1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
						}
					}
					
					contadorDias++;
				}
				
				celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
				celdaInicio.setCellValue(FormateadorDatos.formateaDato(sumaJornadas, TipoDato.FORMATO_REAL));
				celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
				
				celdaInicio = xssfSheetResumen.offset(celdaInicio, 1, -contador+2);
				celdaInicio.setCellValue("Total");
				celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
								
				itHoras = jmJornadas.jornadas.iterator();
				contadorDias = 1;
				sumaJornadas = 0;
				
				while (itHoras.hasNext()) {
					Float hora = itHoras.next();
					
					celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
					if (hora !=-1) {
						celdaInicio.setCellType(CellType.FORMULA);
						celdaInicio.setCellFormula(xssfSheetResumen.posicion(xssfSheetResumen.offset(celdaInicio, -3, 0)) + "+" +
												   xssfSheetResumen.posicion(xssfSheetResumen.offset(celdaInicio, -2, 0)) + "+" +
												   xssfSheetResumen.posicion(xssfSheetResumen.offset(celdaInicio, -1, 0)));
						celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(-1, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
						
					} else {
						Calendar cAux = Calendar.getInstance();
						cAux.set(anio, mes-1, contadorDias);
						if (cAux.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cAux.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
							celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(-2, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
						else
							celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(-2, EstiloCelda.NARANJA, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
					}
					
					contadorDias++;
				}
				
				celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
				celdaInicio.setCellType(CellType.FORMULA);
				celdaInicio.setCellFormula(xssfSheetResumen.posicion(xssfSheetResumen.offset(celdaInicio, -3, 0)) + "-" +
						   xssfSheetResumen.posicion(xssfSheetResumen.offset(celdaInicio, -2, 0)) + "-" +
						   xssfSheetResumen.posicion(xssfSheetResumen.offset(celdaInicio, -1, 0)));
				celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(0, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
				
				celdaInicio = xssfSheetResumen.offset(celdaInicio, -3, -contador+1);
				
				CellRangeAddress cellRangeAddress = new CellRangeAddress(celdaInicio.getRowIndex(), celdaInicio.getRowIndex()+3, celdaInicio.getColumnIndex(), celdaInicio.getColumnIndex());
				xssfSheetResumen.hoja.addMergedRegion(cellRangeAddress);
				
				celdaInicio = xssfSheetResumen.offset(celdaInicio, 4, 0);				
			}
			
		}
		
		celdaInicio = xssfSheetResumen.offset(celdaInicio, 3, 0);
		celdaInicio.setCellType(CellType.STRING);
		celdaInicio.setCellValue("(Las horas en color más claro indican el proyecto a variar en caso de que no ajusten las horas)");
		
		celdaInicio = xssfSheetResumen.offset(celdaInicio, 2, 0);
		
		celdaInicio.setCellType(CellType.STRING);
		celdaInicio.setCellValue("Nombre");
		celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
		
		celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
		celdaInicio.setCellValue("Proyecto");
		celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
		CellRangeAddress cellRangeAddress = new CellRangeAddress(celdaInicio.getRowIndex(), celdaInicio.getRowIndex(), celdaInicio.getColumnIndex(), celdaInicio.getColumnIndex()+c.getActualMaximum(Calendar.DAY_OF_MONTH));
		xssfSheetResumen.hoja.addMergedRegion(cellRangeAddress);
					
		celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, c.getActualMaximum(Calendar.DAY_OF_MONTH)+1);
		celdaInicio.setCellValue("Total");
		celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
		contador++;
		
		itRecurso = listaRecurso.iterator();
		
		celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, -c.getActualMaximum(Calendar.DAY_OF_MONTH)-2);
		
		while (itRecurso.hasNext()) {
			r = itRecurso.next();
			r.cargaRecurso();
			
			ParametroRecurso pGestorRecurso = ((ParametroRecurso) r.getValorParametro(MetaParametro.RECURSO_COD_GESTOR));
			Recurso gestorRecurso = ((Recurso) pGestorRecurso.getValor());
			ParametroRecurso pNatCoste = ((ParametroRecurso) r.getValorParametro(MetaParametro.RECURSO_NAT_COSTE));
			
			if (gestorRecurso!=null && gestorRecurso.id == Constantes.getAdministradorSistema().id && pNatCoste.getValor().equals(this.mc)) {
				pGestorRecurso = ((ParametroRecurso) r.getValorParametro(MetaParametro.RECURSO_NOMBRE_REAL));
				
				celdaInicio = xssfSheetResumen.offset(celdaInicio, 1, 0);
				celdaInicio.setCellValue(pGestorRecurso.getValor().toString());
				xssfSheetResumen.hoja.setColumnWidth(celdaInicio.getColumnIndex(), 5000);
				celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.GRIS, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
				
				celdaInicio = xssfSheetResumen.offset(celdaInicio, 0,1);
				
				int contadorProy = 0;
				ArrayList<Estimacion> lSalida = listaEstimaciones(r);
				
				Proyecto p = getProyectoResto(lSalida);
				
				Iterator<Estimacion> itEstimacion = lSalida.iterator();
				while (itEstimacion.hasNext()) {
					Estimacion est = itEstimacion.next();
					
					celdaInicio.setCellValue(est.proyecto.nombre);
					celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
					cellRangeAddress = new CellRangeAddress(celdaInicio.getRowIndex(), celdaInicio.getRowIndex(), celdaInicio.getColumnIndex(), celdaInicio.getColumnIndex()+c.getActualMaximum(Calendar.DAY_OF_MONTH));
					xssfSheetResumen.hoja.addMergedRegion(cellRangeAddress);
					
					contadorProy++;
					
					celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, c.getActualMaximum(Calendar.DAY_OF_MONTH)+1);
					celdaInicio.setCellValue(est.horas);
					if (p.id==est.proyecto.id)
						celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(0, EstiloCelda.NARANJA, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
					else 
						celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(3, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
					celdaInicio = xssfSheetResumen.offset(celdaInicio, 1, -c.getActualMaximum(Calendar.DAY_OF_MONTH)-1);
				}
				
				celdaInicio = xssfSheetResumen.offset(celdaInicio, -contadorProy, -1);
				
				if (contadorProy>1) {
					cellRangeAddress = new CellRangeAddress(celdaInicio.getRowIndex(), celdaInicio.getRowIndex()+contadorProy-1, celdaInicio.getColumnIndex(), celdaInicio.getColumnIndex());
					xssfSheetResumen.hoja.addMergedRegion(cellRangeAddress);
				}
				
				celdaInicio = xssfSheetResumen.offset(celdaInicio, contadorProy, 0);
			}			
		}
	}
	
	private ArrayList<Estimacion> listaEstimaciones(Recurso r) {
		 ArrayList<Estimacion> listaEst = new ArrayList<Estimacion>();
		 
		 Estimacion e = new Estimacion();
		 listaEst = e.listado(r, Constantes.inicioMes(this.mes, this.anio), Constantes.finMes(this.mes, this.anio));
		 
		 HashMap<Integer,Estimacion> lSalida = new HashMap<Integer,Estimacion>();
		 Iterator<Estimacion> itEst = listaEst.iterator();
		 while (itEst.hasNext()) {
			 Estimacion est = itEst.next();
			 if (lSalida.containsKey(est.proyecto.id)) {
				 Estimacion estAux = lSalida.get(est.proyecto.id);
				 estAux.horas += est.horas;
			 } else {
				 lSalida.put(est.proyecto.id,est);
			 }
		 }
		 ArrayList<Estimacion> listaSalida = new ArrayList<Estimacion>();
		 listaSalida.addAll(lSalida.values());
		 
		 return listaSalida;
	}
	
	private Proyecto getProyectoResto(ArrayList<Estimacion> listaEstimaciones) {
		HashMap<Integer,Proyecto> lProyectos = new HashMap<Integer,Proyecto>();
		Parametro par = (Parametro) Parametro.getParametro(new Parametro().getClass().getSimpleName(), -1, MetaParametro.PARAMETRO_ADMIN);
		
		Iterator<Estimacion> itEst = listaEstimaciones.iterator();
		while (itEst.hasNext()) {
			Estimacion e = itEst.next();
			Proyecto pAux = e.proyecto;
			
			ParametroProyecto pp = pAux.getValorParametro(MetaParametro.PROYECTO_JP);			
			
			if (par.getValor().equals(pp.getValor())){
				lProyectos.put(pAux.id, pAux);
			}			
		}
		
		if (lProyectos.size()==0) {
			itEst = listaEstimaciones.iterator();
			while (itEst.hasNext()) {
				Estimacion e = itEst.next();
				lProyectos.put(e.proyecto.id, e.proyecto);			
			}
		}
		
		float max = 0;
		int idMax = 0;
		
		Iterator<Proyecto> itProy = lProyectos.values().iterator();
		while (itProy.hasNext()) {
			Proyecto p = itProy.next();
			float restante = 0;
					
			if (lRestante.containsKey(p.id)){
				restante = lRestante.get(p.id);
			} else {
				Presupuesto pres = new Presupuesto();
				pres = pres.dameUltimaVersionPresupuesto(p);
				pres.cargaCostes();
					
				Date fechaPivote = Constantes.fechaActual();
					
				AnalizadorPresupuesto ap = new AnalizadorPresupuesto();
				ap.construyePresupuestoMensualizado(pres,fechaPivote,null,null,null,null);
				
				restante = ap.getRestante(((Recurso) par.getValor()).id, Constantes.inicioMes(this.mes, this.anio), MetaConcepto.porId(MetaConcepto.SATAD));
				lRestante.put(p.id, restante);				 
			}						
			
			if (restante>max) {
				idMax = p.id;
				max = restante;
			}
		}
		
		itEst = listaEstimaciones.iterator();
		while (itEst.hasNext()) {
			Estimacion e = itEst.next();
			if (e.proyecto.id == idMax) {
				if (e.horas > 30) {
					return e.proyecto;
				}
			}			
		}
		
		Estimacion eMax = new Estimacion();
		
		itEst = listaEstimaciones.iterator();
		while (itEst.hasNext()) {
			Estimacion e = itEst.next();
			if (e.horas > eMax.horas) {
				return e.proyecto;
			}			
		}
		
		if (lProyectos.size()>0)
			return lProyectos.get(0);
		else
			return null;
	}
}
