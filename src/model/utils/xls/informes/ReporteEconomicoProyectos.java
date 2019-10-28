package model.utils.xls.informes;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import controller.AnalizadorPresupuesto;
import javafx.collections.ObservableList;
import model.beans.Certificacion;
import model.beans.CertificacionFase;
import model.beans.CertificacionFaseParcial;
import model.beans.CertificacionReal;
import model.beans.Concepto;
import model.beans.Coste;
import model.beans.Estimacion;
import model.beans.EstimacionAnio;
import model.beans.EstimacionMes;
import model.beans.Imputacion;
import model.beans.ParametroRecurso;
import model.beans.Presupuesto;
import model.beans.Proyecto;
import model.constantes.Constantes;
import model.constantes.FormateadorDatos;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaGerencia;
import model.metadatos.MetaParametro;
import model.metadatos.Sistema;
import model.metadatos.TipoDato;

public class ReporteEconomicoProyectos extends InformeGenerico{

	HashMap<String, HojaExcel> sheets = null;
	
	HojaExcel hojaResumen = null;
	
	public static final String LISTA_PROYECTOS = "lProyectos";
	
	public static final String ANCLA_RESUMEN = "ANCLA_RESUMEN";
	public static final String ANCLA_DETALLE = "ANCLA_DETALLE";
	public static final String ANCLA_CERTIF = "ANCLA_CERTIF";
	public static final String ANCLA_TOTAL_FECHAS = "ANCLA_TOTAL_FECHAS";
	public static final String ANCLA_PUNTERO_RESUMEN = "ANCLA_PUNTERO_RESUMEN";
	public static final String ANCLA_CERTIFICACIONES = "ANCLA_CERTIFICACIONES";
	public static final String ANCLA_IMPUTACIONES = "ANCLA_IMPUTACIONES";
	
	HashMap<Integer,HashMap<String,PosicionExcel>> anclas = null;
	
	AnalizadorPresupuesto ap = null;
	ArrayList<Sistema> lSistemas;
	ArrayList<Concepto> lConceptos;
	
	@Override
	public void escribeFichero(HashMap<String, Object> datos) throws Exception {
		if (newExcelFile==null) return;
		
		anclas = new HashMap<Integer,HashMap<String,PosicionExcel>>();
		
		OutputStream excelNewOutputStream = null;
        excelNewOutputStream = new FileOutputStream(newExcelFile);
        
        worbook = new XSSFWorkbook();
        new EstiloCelda(worbook);
        
        ObservableList<Proyecto> lProyectos = (ObservableList<Proyecto>) datos.get(ReporteEconomicoProyectos.LISTA_PROYECTOS);
        
        HojaExcel xssfSheetResumen = new HojaExcel(worbook, "Resumen");
        pintaHojaResumen(xssfSheetResumen);
        
        sheets = new HashMap<String, HojaExcel>();
		
        Iterator<Proyecto> itProyecto = lProyectos.iterator();
        while (itProyecto.hasNext()) {
        	Proyecto p = itProyecto.next();
        	
        	String acronProy = (String) p.getValorParametro(MetaParametro.PROYECTO_ACRONPROY).getValor();
        	HojaExcel xssfSheetNew = new HojaExcel(worbook, acronProy);
        	sheets.put(acronProy,xssfSheetNew);
        	anclas.put(p.id, new HashMap<String,PosicionExcel>());
        	
        	pintaProyecto(p,xssfSheetNew); 
        	
        	rellenaDatosHojaResumen(p, xssfSheetResumen, xssfSheetNew);
        }
        
        xssfSheetResumen.hoja.setColumnGroupCollapsed(3, true);
        
        
        worbook.write(excelNewOutputStream);
        
        excelNewOutputStream.close();
	}
	
	private void pintaHojaResumen(HojaExcel xssfSheetResumen) {
		
		
		XSSFCell celdaInicio = xssfSheetResumen.get(0, 0);
		
		XSSFCell celdaCabeceraAlta = xssfSheetResumen.offset(celdaInicio, 0, 3);
		celdaCabeceraAlta.setCellType(CellType.STRING);
		celdaCabeceraAlta.setCellValue("Presupuesto CDSI");
		celdaCabeceraAlta.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
		xssfSheetResumen.hoja.groupColumn(celdaCabeceraAlta.getColumnIndex(), celdaCabeceraAlta.getColumnIndex()+MetaConcepto.listado.size()-1);
		CellRangeAddress cellRangeAddress = new CellRangeAddress(celdaCabeceraAlta.getRowIndex(), celdaCabeceraAlta.getRowIndex(), celdaCabeceraAlta.getColumnIndex(), celdaCabeceraAlta.getColumnIndex()+MetaConcepto.listado.size());
		xssfSheetResumen.hoja.addMergedRegion(cellRangeAddress);
		
		celdaCabeceraAlta = xssfSheetResumen.offset(celdaCabeceraAlta, 0, 1*(MetaConcepto.listado.size()+1));
		celdaCabeceraAlta.setCellType(CellType.STRING);
		celdaCabeceraAlta.setCellValue("TOTAL");
		celdaCabeceraAlta.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
		xssfSheetResumen.hoja.groupColumn(celdaCabeceraAlta.getColumnIndex(), celdaCabeceraAlta.getColumnIndex()+1+3*(MetaConcepto.listado.size()));
		cellRangeAddress = new CellRangeAddress(celdaCabeceraAlta.getRowIndex(), celdaCabeceraAlta.getRowIndex(), celdaCabeceraAlta.getColumnIndex(), celdaCabeceraAlta.getColumnIndex()+2+3*(MetaConcepto.listado.size()));
		xssfSheetResumen.hoja.addMergedRegion(cellRangeAddress);
		
		celdaCabeceraAlta = xssfSheetResumen.offset(celdaCabeceraAlta, 0, 3*(MetaConcepto.listado.size()+1));
		celdaCabeceraAlta.setCellType(CellType.STRING);
		celdaCabeceraAlta.setCellValue("Años Anteriores");
		celdaCabeceraAlta.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
		xssfSheetResumen.hoja.groupColumn(celdaCabeceraAlta.getColumnIndex(), celdaCabeceraAlta.getColumnIndex()+1+3*(MetaConcepto.listado.size()));
		cellRangeAddress = new CellRangeAddress(celdaCabeceraAlta.getRowIndex(), celdaCabeceraAlta.getRowIndex(), celdaCabeceraAlta.getColumnIndex(), celdaCabeceraAlta.getColumnIndex()+2+3*(MetaConcepto.listado.size()));
		xssfSheetResumen.hoja.addMergedRegion(cellRangeAddress);
		
		celdaCabeceraAlta = xssfSheetResumen.offset(celdaCabeceraAlta, 0, 3*(MetaConcepto.listado.size()+1));
		celdaCabeceraAlta.setCellType(CellType.STRING);
		celdaCabeceraAlta.setCellValue("Año en Curso");
		celdaCabeceraAlta.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
		xssfSheetResumen.hoja.groupColumn(celdaCabeceraAlta.getColumnIndex(), celdaCabeceraAlta.getColumnIndex()+1+3*(MetaConcepto.listado.size()));
		cellRangeAddress = new CellRangeAddress(celdaCabeceraAlta.getRowIndex(), celdaCabeceraAlta.getRowIndex(), celdaCabeceraAlta.getColumnIndex(), celdaCabeceraAlta.getColumnIndex()+2+3*(MetaConcepto.listado.size()));
		xssfSheetResumen.hoja.addMergedRegion(cellRangeAddress);
		
		celdaCabeceraAlta = xssfSheetResumen.offset(celdaCabeceraAlta, 0, 3*(MetaConcepto.listado.size()+1));
		celdaCabeceraAlta.setCellType(CellType.STRING);
		celdaCabeceraAlta.setCellValue("Años Posteriores");
		celdaCabeceraAlta.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
		xssfSheetResumen.hoja.groupColumn(celdaCabeceraAlta.getColumnIndex(), celdaCabeceraAlta.getColumnIndex()+1+3*(MetaConcepto.listado.size()));
		cellRangeAddress = new CellRangeAddress(celdaCabeceraAlta.getRowIndex(), celdaCabeceraAlta.getRowIndex(), celdaCabeceraAlta.getColumnIndex(), celdaCabeceraAlta.getColumnIndex()+2+3*(MetaConcepto.listado.size()));
		xssfSheetResumen.hoja.addMergedRegion(cellRangeAddress);
		
		celdaCabeceraAlta = xssfSheetResumen.offset(celdaCabeceraAlta, 0, 3*(MetaConcepto.listado.size()+1));
		celdaCabeceraAlta.setCellType(CellType.STRING);
		celdaCabeceraAlta.setCellValue("TREI GDT");
		celdaCabeceraAlta.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
		xssfSheetResumen.hoja.groupColumn(celdaCabeceraAlta.getColumnIndex(), celdaCabeceraAlta.getColumnIndex()+1);
		cellRangeAddress = new CellRangeAddress(celdaCabeceraAlta.getRowIndex(), celdaCabeceraAlta.getRowIndex(), celdaCabeceraAlta.getColumnIndex(), celdaCabeceraAlta.getColumnIndex()+2);
		xssfSheetResumen.hoja.addMergedRegion(cellRangeAddress);
		
		celdaInicio = xssfSheetResumen.get(2, 0);
		
		celdaInicio.setCellType(CellType.STRING);
		celdaInicio.setCellValue("Cerrado");
		celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
	
		celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
		celdaInicio.setCellType(CellType.STRING);
		celdaInicio.setCellValue("Proyecto");
		celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
		xssfSheetResumen.hoja.setColumnWidth( celdaInicio.getColumnIndex(), 7040);
		
		celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
		celdaInicio.setCellType(CellType.STRING);
		celdaInicio.setCellValue("Sistema");
		celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
		
		ArrayList<String> listaBloques = new ArrayList<String>();
		listaBloques.add("Presupuestado TOTAL");
		listaBloques.add("Estimado TOTAL");
		listaBloques.add("Real TOTAL");
		listaBloques.add("Restante TOTAL");
		listaBloques.add("Estimado Años Anteriores");
		listaBloques.add("Real Años Anteriores");
		listaBloques.add("Restante Años Anteriores");
		listaBloques.add("Estimado Año en curso");
		listaBloques.add("Real Año en curso");
		listaBloques.add("Restante  Año en curso");
		listaBloques.add("Estimado Años Posteriores");
		listaBloques.add("Real Años Posteriores");
		listaBloques.add("Restante Años Posteriores");
		
		Iterator<String> itListaBloques = listaBloques.iterator();
		
		while (itListaBloques.hasNext()) {
			String bloque = itListaBloques.next();
			
			celdaInicio = xssfSheetResumen.offset(celdaInicio, -1, 1);
			celdaInicio.setCellType(CellType.STRING);
			celdaInicio.setCellValue(bloque);
			celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(5, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
			
			cellRangeAddress = new CellRangeAddress(celdaInicio.getRowIndex(), celdaInicio.getRowIndex(), celdaInicio.getColumnIndex(), celdaInicio.getColumnIndex()+MetaConcepto.listado.size());
			xssfSheetResumen.hoja.addMergedRegion(cellRangeAddress);
			
			xssfSheetResumen.hoja.groupColumn(celdaInicio.getColumnIndex(), celdaInicio.getColumnIndex()+MetaConcepto.listado.size()-1);
			
			celdaInicio = xssfSheetResumen.offset(celdaInicio, 1, 0);
			
			Iterator<MetaConcepto> imc = MetaConcepto.listado.values().iterator();
			while (imc.hasNext()){
				MetaConcepto mc = imc.next();
				celdaInicio.setCellType(CellType.STRING);
				celdaInicio.setCellValue(mc.codigo);
				celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(5, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
				celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
			}
			
			celdaInicio.setCellType(CellType.STRING);
			celdaInicio.setCellValue("TOTAL");
			celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
			if (bloque.contains("Restante") || bloque.contains("Presupuestado")){
				xssfSheetResumen.hoja.setColumnWidth( celdaInicio.getColumnIndex(), 7040);
			}
		}
		
		celdaInicio = xssfSheetResumen.offset(celdaInicio, -1, 1);
		celdaInicio.setCellType(CellType.STRING);
		celdaInicio.setCellValue("TREI GGP (año curso)");
		celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(5, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
		
		cellRangeAddress = new CellRangeAddress(celdaInicio.getRowIndex(), celdaInicio.getRowIndex(), celdaInicio.getColumnIndex(), celdaInicio.getColumnIndex()+2);
		xssfSheetResumen.hoja.addMergedRegion(cellRangeAddress);
		
		celdaInicio = xssfSheetResumen.offset(celdaInicio, 1, 0);
		celdaInicio.setCellType(CellType.STRING);
		celdaInicio.setCellValue("Estimado");
		celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(5, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
				
		celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
		celdaInicio.setCellType(CellType.STRING);
		celdaInicio.setCellValue("Real");
		celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
		
		celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
		celdaInicio.setCellType(CellType.STRING);
		celdaInicio.setCellValue("Restante");
		celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
		xssfSheetResumen.hoja.setColumnWidth( celdaInicio.getColumnIndex(), 7040);
		
		celdaInicio = xssfSheetResumen.offset(celdaInicio, -1, 1);
		celdaInicio.setCellType(CellType.STRING);
		celdaInicio.setCellValue("TREI GDT (año curso)");
		celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(5, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
		
		cellRangeAddress = new CellRangeAddress(celdaInicio.getRowIndex(), celdaInicio.getRowIndex(), celdaInicio.getColumnIndex(), celdaInicio.getColumnIndex()+2);
		xssfSheetResumen.hoja.addMergedRegion(cellRangeAddress);
		
		celdaInicio = xssfSheetResumen.offset(celdaInicio, 1, 0);
		celdaInicio.setCellType(CellType.STRING);
		celdaInicio.setCellValue("Estimado");
		celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(5, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
				
		celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
		celdaInicio.setCellType(CellType.STRING);
		celdaInicio.setCellValue("Real");
		celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
		
		celdaInicio = xssfSheetResumen.offset(celdaInicio, 0, 1);
		celdaInicio.setCellType(CellType.STRING);
		celdaInicio.setCellValue("Restante");
		celdaInicio.setCellStyle(EstiloCelda.getEstiloCelda(6, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
		xssfSheetResumen.hoja.setColumnWidth( celdaInicio.getColumnIndex(), 7040);
		
		HashMap<String,PosicionExcel> anclasHojaResumen = new HashMap<String,PosicionExcel>();
		anclasHojaResumen.put(ReporteEconomicoProyectos.ANCLA_PUNTERO_RESUMEN,new PosicionExcel(xssfSheetResumen.offset(celdaInicio,1,0).getRowIndex(),0,ReporteEconomicoProyectos.ANCLA_PUNTERO_RESUMEN));
		anclas.put(-1,anclasHojaResumen);
		
		
	}
	
	private void pintaProyecto(Proyecto p, HojaExcel xssfSheetNew) throws Exception{
		calculaEstimacion (p);
		
		int contador = pintaCabeceraProyecto(p, 1, xssfSheetNew);
		
		contador = pintaResumen(p,contador,xssfSheetNew);
		
		contador = pintaDesglose(p,contador,xssfSheetNew);
		
		contador = pintaCertificaciones(p,contador,xssfSheetNew);
		
		contador = rellenaDatosCertificaciones(p, xssfSheetNew, contador);
		
		contador = pintaImputaciones(p,contador,xssfSheetNew);
		
		contador = rellenaDatosImputaciones(p, xssfSheetNew, contador);
		
		rellenaDatos(p, xssfSheetNew);
		rellenaDatosResumen(p, xssfSheetNew);
		
		
	}
	
	private int pintaImputaciones(Proyecto p, int contador, HojaExcel xssfSheetNew){
		contador = contador +3;
        XSSFCell cellNew; 
        
		HashMap<String,PosicionExcel> anclaProyecto = anclas.get(p.id);
		anclaProyecto.put(ReporteEconomicoProyectos.ANCLA_IMPUTACIONES, new PosicionExcel(contador, 1, ReporteEconomicoProyectos.ANCLA_IMPUTACIONES));
				
		cellNew = xssfSheetNew.get(contador, 1);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Código");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));

        cellNew = xssfSheetNew.get(contador, 2);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Nombre");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));

        cellNew = xssfSheetNew.get(contador, 3);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Sistema");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
        
        cellNew = xssfSheetNew.get(contador, 4);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Periodo");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
        
        cellNew = xssfSheetNew.get(contador, 5);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Tarifa");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));   
        
        cellNew = xssfSheetNew.get(contador, 6);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Horas");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));    
        
        cellNew = xssfSheetNew.get(contador, 7);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Importe");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));   
        
        cellNew = xssfSheetNew.get(contador, 8);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Fraccionada");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));    
        
        cellNew = xssfSheetNew.get(contador, 9);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Total Imputación");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));    
        
        return contador;	
	}
	
	private int pintaCertificaciones(Proyecto p, int contador, HojaExcel xssfSheetNew){
		contador = contador +5;
		XSSFRow xssfRowNew;
		XSSFRow xssfRowNewAux;
        XSSFCell cellNew; 
        
		HashMap<String,PosicionExcel> anclaProyecto = anclas.get(p.id);
		anclaProyecto.put(ReporteEconomicoProyectos.ANCLA_CERTIFICACIONES, new PosicionExcel(contador, 1, ReporteEconomicoProyectos.ANCLA_CERTIFICACIONES));
				
		cellNew = xssfSheetNew.get(contador, 1);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Sistema");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));

        cellNew = xssfSheetNew.get(contador, 2);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Fase");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));

        cellNew = xssfSheetNew.get(contador, 3);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Fecha");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
        
        cellNew = xssfSheetNew.get(contador, 4);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Concepto");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
        
        cellNew = xssfSheetNew.get(contador, 5);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Valor Estimado");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));   
        
        cellNew = xssfSheetNew.get(contador, 6);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Porcentaje Estimado");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));    
        
        cellNew = xssfSheetNew.get(contador, 7);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Importe Imputado");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));     
         
        
        cellNew = xssfSheetNew.get(contador, 8);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Certificacion");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
        
		return contador;	
	}
	
	private void rellenaDatosResumen(Proyecto p, HojaExcel hoja) {
		PosicionExcel posIni = anclas.get(p.id).get(ReporteEconomicoProyectos.ANCLA_RESUMEN);
		
		XSSFSheet xssfSheetNew = hoja.hoja;
		XSSFRow xssfRowMetaC= null;
		XSSFRow xssfRowSistemas= null;
        XSSFCell cellNew;
        XSSFCell cellSistema;
        XSSFCell cellMetaC;
        
        int contadorSistemas = 0;
        Iterator<Sistema> itSistemas = this.lSistemas.iterator();
        while (itSistemas.hasNext()) {
        	itSistemas.next();
        	xssfRowSistemas = xssfSheetNew.getRow(posIni.fila+2+contadorSistemas);
        	cellSistema = xssfRowSistemas.getCell(posIni.columna);
        	Sistema s = Sistema.getPorNombre(cellSistema.getStringCellValue());
        	
        	xssfRowMetaC = xssfSheetNew.getRow(posIni.fila+1);
        	
        	String total = "0";
        	int contadorColumnas = 0;
        	
        	for (int i=0;i<lConceptos.size();i++) {
        		cellMetaC = xssfRowMetaC.getCell(i+posIni.columna+1);
        		MetaConcepto c = MetaConcepto.porCodigo(cellMetaC.getStringCellValue());
        		
        		Iterator<Coste> itCostes = ap.presupuesto.costes.values().iterator();
        		while (itCostes.hasNext()) {
        			Coste coste = itCostes.next();
        			if (coste.sistema.id == s.id) {
        				Concepto conc= coste.conceptosCoste.get(c.codigo);
        				cellNew = xssfRowSistemas.getCell(cellMetaC.getColumnIndex());
        				if (conc==null)
        					cellNew.setCellValue(0);
        				else 
        					cellNew.setCellValue(conc.valorEstimado);
        				total += "+" + InformeGenerico.valorColumna(cellNew.getColumnIndex()) + "" + (cellNew.getRowIndex()+1);
        				break;
        			}
        		}
        		contadorColumnas++;
        	}
        	
        	cellNew = xssfRowSistemas.getCell(contadorColumnas+posIni.columna+1);
			cellNew.setCellFormula(total);
			total += "+" + InformeGenerico.valorColumna(cellNew.getColumnIndex()) + "" + cellNew.getRowIndex();        	
        	
        	PosicionExcel posTotal = anclas.get(p.id).get(ReporteEconomicoProyectos.ANCLA_TOTAL_FECHAS);
        	contadorColumnas++;
        	total = "0";
        	int aux = contadorColumnas; 
        	
        	for (int i=contadorColumnas;i<lConceptos.size()+aux;i++) {
        		cellMetaC = xssfRowMetaC.getCell(i+posIni.columna+1);
        		MetaConcepto c = MetaConcepto.porCodigo(cellMetaC.getStringCellValue());
        		
        		String formula = "";
        		
        		if (c.id == MetaConcepto.TREI) {
        			XSSFCell celdaAux = this.formulaCeldas(p, hoja, s, c, MetaGerencia.listado.get(MetaGerencia.GGP));
        			celdaAux = hoja.get(celdaAux.getRowIndex(), posTotal.columna-1);
        			if (celdaAux!=null) formula = hoja.posicion(celdaAux);
        			celdaAux = this.formulaCeldas(p, hoja, s, c, MetaGerencia.listado.get(MetaGerencia.GDT));
        			celdaAux = hoja.get(celdaAux.getRowIndex(), posTotal.columna-1);
        			if (celdaAux!=null) formula += "+" + hoja.posicion(celdaAux);
        		} else {
        			XSSFCell celdaAux = this.formulaCeldas(p, hoja, s, c, null);
        			celdaAux = hoja.get(celdaAux.getRowIndex(), posTotal.columna-1);
        			if (celdaAux!=null) formula = hoja.posicion(celdaAux);
        		}
        		        		
        		cellNew = xssfRowSistemas.getCell(cellMetaC.getColumnIndex());
				cellNew.setCellFormula(formula);
				total += "+" + InformeGenerico.valorColumna(cellNew.getColumnIndex()) + "" + (cellNew.getRowIndex()+1);
				        		
        		contadorColumnas++;
        	}
        	
        	cellNew = xssfRowSistemas.getCell(contadorColumnas+posIni.columna+1);
			cellNew.setCellFormula(total);
			total += "+" + InformeGenerico.valorColumna(cellNew.getColumnIndex()) + "" + cellNew.getRowIndex();        	
			contadorColumnas++;
			total = "0";
        	
			aux = contadorColumnas; 
			
        	for (int i=contadorColumnas;i<lConceptos.size()+aux;i++) {
        		cellMetaC = xssfRowMetaC.getCell(i+posIni.columna+1);
        		MetaConcepto c = MetaConcepto.porCodigo(cellMetaC.getStringCellValue());
        		
        		String formula = "";
        		
        		if (c.id == MetaConcepto.TREI) {
        			XSSFCell celdaAux = this.formulaCeldas(p, hoja, s, c, MetaGerencia.listado.get(MetaGerencia.GGP));
        			celdaAux = hoja.get(celdaAux.getRowIndex(), posTotal.columna);
        			if (celdaAux!=null) formula = hoja.posicion(celdaAux);
        			celdaAux = this.formulaCeldas(p, hoja, s, c, MetaGerencia.listado.get(MetaGerencia.GDT));
        			celdaAux = hoja.get(celdaAux.getRowIndex(), posTotal.columna);
        			if (celdaAux!=null) formula += "+" + hoja.posicion(celdaAux);
        		} else {
        			XSSFCell celdaAux = this.formulaCeldas(p, hoja, s, c, null);
        			celdaAux = hoja.get(celdaAux.getRowIndex(), posTotal.columna);
        			if (celdaAux!=null) formula = hoja.posicion(celdaAux);
        		}
        		        		
        		cellNew = xssfRowSistemas.getCell(cellMetaC.getColumnIndex());
				cellNew.setCellFormula(formula);
				total += "+" + InformeGenerico.valorColumna(cellNew.getColumnIndex()) + "" + (cellNew.getRowIndex()+1);
				        		
        		contadorColumnas++;
        	}
        	
        	cellNew = xssfRowSistemas.getCell(contadorColumnas+posIni.columna+1);
			cellNew.setCellFormula(total);
			total += "+" + InformeGenerico.valorColumna(cellNew.getColumnIndex()) + "" + cellNew.getRowIndex();        	
        	
			
        	contadorSistemas = contadorSistemas+1;
        }
        
        xssfRowSistemas = xssfSheetNew.getRow(xssfRowSistemas.getRowNum()+1);
        
        for (int i=0;i<(lConceptos.size()+1)*3;i++) {
    		cellMetaC = xssfRowMetaC.getCell(i+posIni.columna+1);
    		
    		cellNew = xssfRowSistemas.getCell(cellMetaC.getColumnIndex());
    		String formula = "SUM(" + InformeGenerico.valorColumna(cellMetaC.getColumnIndex()) +""+  (xssfRowSistemas.getRowNum()-lSistemas.size()+1) + ":" + InformeGenerico.valorColumna(cellMetaC.getColumnIndex()) + (xssfRowSistemas.getRowNum())+")";
    		cellNew.setCellType(CellType.FORMULA);
    		cellNew.setCellFormula(formula);    	    		
    	}
	}
	
	private int rellenaDatosCertificaciones(Proyecto p, HojaExcel hoja, int contador) throws Exception{
		PosicionExcel posIni = anclas.get(p.id).get(ReporteEconomicoProyectos.ANCLA_CERTIFICACIONES);
		
		XSSFCell cellPuntero = hoja.get(posIni);
		XSSFCell cellCertif = hoja.get(posIni);
		
		cellPuntero = hoja.offset(cellPuntero, 1, 0);
        
		int nFilas = 0;
        Iterator<Certificacion> itc = this.ap.certificaciones.iterator();
        while (itc.hasNext()) {
        	Certificacion c = itc.next();
        	Iterator<CertificacionFase> itcf = c.certificacionesFases.iterator();
        	
        	while (itcf.hasNext()) {
        		CertificacionFase cf = itcf.next();
        		Iterator<CertificacionFaseParcial> itcfp = cf.certificacionesParciales.iterator();
        		
        		while (itcfp.hasNext()) {
        			CertificacionFaseParcial cfp = itcfp.next();
        			cellCertif = hoja.offset(cellPuntero, nFilas, 0);
        			
        			if (cfp.valEstimado!=0 || cfp.valReal!=0) {
        				nFilas++;
        				cellCertif = hoja.offset(cellCertif, 0, 0);
        				cellCertif.setCellType(CellType.STRING);
        				cellCertif.setCellValue(c.s.codigo);
        				cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT));

        				cellCertif = hoja.offset(cellCertif, 0, 1);
        				cellCertif.setCellType(CellType.STRING);
        				cellCertif.setCellValue(cf.fase.nombre);
        				cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT));
        				
        				cellCertif = hoja.offset(cellCertif, 0, 1);
        				cellCertif.setCellType(CellType.STRING);
        				cellCertif.setCellValue(FormateadorDatos.formateaDato(cfp.fxCertificacion,TipoDato.FORMATO_FECHA));
        				cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT));
        				
        				cellCertif = hoja.offset(cellCertif, 0, 1);
        				cellCertif.setCellType(CellType.STRING);
        				cellCertif.setCellValue(cfp.nombre);
        				cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT));
        				
        				cellCertif = hoja.offset(cellCertif, 0, 1);
        				cellCertif.setCellType(CellType.NUMERIC);
        				cellCertif.setCellValue(cfp.valEstimado);
        				cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_MONEDA));
        				
        				cellCertif = hoja.offset(cellCertif, 0, 1);
        				cellCertif.setCellType(CellType.NUMERIC);
        				cellCertif.setCellValue(cf.getPorcentaje(cfp));
        				cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT));
        				
        				cellCertif = hoja.offset(cellCertif, 0, 1);
        				cellCertif.setCellType(CellType.NUMERIC);
        				cellCertif.setCellValue(cfp.valReal);
        				cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_MONEDA));
        				        		        
        				if (cfp.valEstimado!=0 && cfp.certReal==null || cfp.certReal.size()==0) {
        					CertificacionReal cr = new CertificacionReal();
        					cr.certiAsignada = cfp;
        					cfp.certReal = cr.listado(CertificacionReal.CONSULTA_CERTIF, -1, null);
        				}
        				
        				if (cfp.certReal!=null) {
        					String certif = "";
        					Iterator<CertificacionReal> itcfpr = cfp.certReal.iterator();
        					while (itcfpr.hasNext()) {
        						certif += itcfpr.next().nSolicitud;
        						if (itcfpr.hasNext()) certif += ", ";
        					}
        					
        					cellCertif = hoja.offset(cellCertif, 0, 1);
            				cellCertif.setCellType(CellType.STRING);
            				cellCertif.setCellValue(certif);
            				cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_MONEDA));
        				}
        			}
        		}
        	}
        }
		
		return contador+nFilas;
	}
	
	private int rellenaDatosImputaciones(Proyecto p, HojaExcel hoja, int contador) throws Exception{
		PosicionExcel posIni = anclas.get(p.id).get(ReporteEconomicoProyectos.ANCLA_IMPUTACIONES);
		
		XSSFCell cellPuntero = hoja.get(posIni);
		XSSFCell cellCertif = hoja.get(posIni);
		
		cellPuntero = hoja.offset(cellPuntero, 1, 0);
        
		int nFilas = 0;
        Iterator<Imputacion> itc = this.ap.listaImputaciones.iterator();
        while (itc.hasNext()) {
        	Imputacion cfp = itc.next();
			cellCertif = hoja.offset(cellPuntero, nFilas, 0);
			nFilas++;
			cellCertif = hoja.offset(cellCertif, 0, 0);
			cellCertif.setCellType(CellType.STRING);
			cellCertif.setCellValue(((ParametroRecurso) cfp.recurso.getValorParametro(MetaParametro.RECURSO_COD_USUARIO)).valorTexto);
			cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT));
			
			cellCertif = hoja.offset(cellCertif, 0, 1);
			cellCertif.setCellType(CellType.STRING);
			cellCertif.setCellValue(cfp.recurso.nombre);
			cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT));

			cellCertif = hoja.offset(cellCertif, 0, 1);
			cellCertif.setCellType(CellType.STRING);
			cellCertif.setCellValue(cfp.sistema.codigo);
			cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT));

			cellCertif = hoja.offset(cellCertif, 0, 1);
			cellCertif.setCellType(CellType.STRING);
			cellCertif.setCellValue(FormateadorDatos.formateaDato(cfp.fxInicio,TipoDato.FORMATO_FECHA)+" - "+FormateadorDatos.formateaDato(cfp.fxFin,TipoDato.FORMATO_FECHA));
			cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT));
	        
			cellCertif = hoja.offset(cellCertif, 0, 1);
			cellCertif.setCellType(CellType.STRING);
			cellCertif.setCellValue(cfp.tarifa.costeHora);
			cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_MONEDA));
	        
			if (cfp.tipoImputacion == Imputacion.IMPUTACION_NO_FRACCIONADA) {
				cellCertif = hoja.offset(cellCertif, 0, 1);
				cellCertif.setCellType(CellType.STRING);
				cellCertif.setCellValue(cfp.horas);
				cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT));  
				
				cellCertif = hoja.offset(cellCertif, 0, 1);
				cellCertif.setCellType(CellType.STRING);
				cellCertif.setCellValue(cfp.importe);
				cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_MONEDA));				
				
				cellCertif = hoja.offset(cellCertif, 0, 1);
				cellCertif.setCellType(CellType.STRING);
				cellCertif.setCellValue("NO");
				cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT));

				cellCertif = hoja.offset(cellCertif, 0, 1);
				cellCertif.setCellType(CellType.STRING);
				cellCertif.setCellValue(cfp.importe);
				cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_MONEDA));
			}
			
			if (cfp.tipoImputacion == Imputacion.FRACCION_IMPUTACION || cfp.tipoImputacion == Imputacion.IMPUTACION_FRACCIONADA) {
				cellCertif = hoja.offset(cellCertif, 0, 1);
				cellCertif.setCellType(CellType.STRING);
				cellCertif.setCellValue(cfp.imputacionFraccion.getHoras());
				cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT));  
				
				cellCertif = hoja.offset(cellCertif, 0, 1);
				cellCertif.setCellType(CellType.STRING);
				cellCertif.setCellValue(cfp.imputacionFraccion.getImporte());
				cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_MONEDA));
				
				cellCertif = hoja.offset(cellCertif, 0, 1);
				cellCertif.setCellType(CellType.STRING);
				cellCertif.setCellValue("SI");
				cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_TXT));

				cellCertif = hoja.offset(cellCertif, 0, 1);
				cellCertif.setCellType(CellType.STRING);
				cellCertif.setCellValue(cfp.importe);
				cellCertif.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.BLANCO, TipoDato.FORMATO_MONEDA));
			}

			
			
        }
		
		return contador+nFilas;
	}
	
	
	private void rellenaDatosHojaResumen(Proyecto p, HojaExcel hojaResumen, HojaExcel hoja) throws Exception{
		PosicionExcel puntero = anclas.get(-1).get(ReporteEconomicoProyectos.ANCLA_PUNTERO_RESUMEN);
		XSSFCell filaDatos = hojaResumen.get(puntero);
		XSSFCell celdaPuntero = filaDatos;
		XSSFCell celdaConcepto = null;
		XSSFCell celdaArranque = celdaPuntero;
		
		Iterator<Sistema> lSistema = this.lSistemas.iterator();
		String acronProy = p.getValorParametro(MetaParametro.PROYECTO_ACRONPROY).valorTexto;
		
		while (lSistema.hasNext()) {
			String bloque = "";
			celdaConcepto = hojaResumen.get(2,0);
			
			Sistema s = lSistema.next();
			
			int color = celdaPuntero.getRowIndex()%2==0?EstiloCelda.BLANCO:EstiloCelda.GRIS;
			
			celdaPuntero.setCellType(CellType.STRING);
			celdaPuntero.setCellValue(FormateadorDatos.formateaDato(Constantes.toNumBoolean(p.getValorParametro(MetaParametro.PROYECTO_CERRADO).valorEntero),TipoDato.FORMATO_BOOLEAN));
			celdaPuntero.setCellStyle(EstiloCelda.getEstiloCelda(1, color, TipoDato.FORMATO_TXT));
			celdaPuntero = hojaResumen.offset(celdaPuntero, 0, 1);
			celdaConcepto = hojaResumen.offset(celdaConcepto, 0, 1);
			
			celdaPuntero.setCellType(CellType.STRING);
			celdaPuntero.setCellValue(acronProy);
			celdaPuntero.setCellStyle(EstiloCelda.getEstiloCelda(1, color, TipoDato.FORMATO_TXT));
			celdaPuntero = hojaResumen.offset(celdaPuntero, 0, 1);
			celdaConcepto = hojaResumen.offset(celdaConcepto, 0, 1);
			
			celdaPuntero.setCellType(CellType.STRING);
			celdaPuntero.setCellValue(s.codigo);
			celdaPuntero.setCellStyle(EstiloCelda.getEstiloCelda(1, color, TipoDato.FORMATO_TXT));
			celdaPuntero = hojaResumen.offset(celdaPuntero, 0, 1);
			celdaConcepto = hojaResumen.offset(celdaConcepto, 0, 1);
			
			boolean primera = true;
			XSSFCell celdaEquivProy = null;
			
			while (!celdaConcepto.getStringCellValue().equals("")){
				String bloqueAux = hojaResumen.offset(celdaConcepto, -1, 0).getStringCellValue();
				String mConcepto = celdaConcepto.getStringCellValue();
				
				if (!"".equals(bloqueAux)){
					bloque = bloqueAux;
				}
				
				if ("Presupuestado TOTAL".equals(bloque) || "Estimado TOTAL".equals(bloque) || "Real TOTAL".equals(bloque)) {
					if (primera) {
						primera = false;
						celdaEquivProy = buscaSistemaResumen(p, hoja, s);
						celdaEquivProy = hoja.offset(celdaEquivProy, 0, 1);							
					} else {
						celdaEquivProy = hoja.offset(celdaEquivProy, 0, 1);
					}
					celdaPuntero.setCellType(CellType.FORMULA);
					celdaPuntero.setCellFormula("'"+acronProy+"'!"+InformeGenerico.valorColumna(celdaEquivProy.getColumnIndex())+(celdaEquivProy.getRowIndex()+1));
					celdaPuntero.setCellStyle(EstiloCelda.getEstiloCelda(1, color, TipoDato.FORMATO_MONEDA));	
					hojaResumen.hoja.setColumnGroupCollapsed(celdaPuntero.getColumnIndex()-1, true);
				}
				
				if ("Restante TOTAL".equals(bloque)|| 
						"Restante Años Anteriores".equals(bloque) || 
						"Restante  Año en curso".equals(bloque)|| 
						"Restante Años Posteriores".equals(bloque) ) {
					String formula = "" + InformeGenerico.valorColumna(celdaPuntero.getColumnIndex()-12)+(celdaPuntero.getRowIndex()+1)+"-"+InformeGenerico.valorColumna(celdaPuntero.getColumnIndex()-6)+(celdaPuntero.getRowIndex()+1);
					celdaPuntero.setCellType(CellType.FORMULA);
					celdaPuntero.setCellFormula(formula);
					celdaPuntero.setCellStyle(EstiloCelda.getEstiloCelda(1, color, TipoDato.FORMATO_MONEDA));	
					hojaResumen.hoja.setColumnGroupCollapsed(celdaPuntero.getColumnIndex()-1, true);
				}
				
				if ("Estimado Años Anteriores".equals(bloque) || 
						"Real Años Anteriores".equals(bloque) || 
						"Estimado Año en curso".equals(bloque) ||
						"Real Año en curso".equals(bloque) ||
						"Estimado Años Posteriores".equals(bloque) ||
						"Real Años Posteriores".equals(bloque) ) {
					
					int anio = 0;
					boolean estimado = false;
					
					if (bloque.contains("Estimado")) estimado = true;
					if (bloque.contains("Anteriores")) anio = -1;
					if (bloque.contains("curso")) anio = 0;
					if (bloque.contains("Posteriores")) anio = 1;
					
					if (!mConcepto.equals("TOTAL")) {
						MetaConcepto mc = MetaConcepto.porCodigo(mConcepto);
						String formula = "";
						if (mc.id == MetaConcepto.TREI) {
							formula = this.getFormulaSumaAnio(anio, p, hoja, s, mc, MetaGerencia.porId(MetaGerencia.GGP), estimado);
							formula += "+" + this.getFormulaSumaAnio(anio, p, hoja, s, mc, MetaGerencia.porId(MetaGerencia.GDT), estimado);
						} else {
							formula = this.getFormulaSumaAnio(anio, p, hoja, s, mc, null, estimado);
						}
						
						celdaPuntero.setCellType(CellType.FORMULA);
						celdaPuntero.setCellFormula(formula);
						celdaPuntero.setCellStyle(EstiloCelda.getEstiloCelda(1, color, TipoDato.FORMATO_MONEDA));	
					} else {
						String formula = "sum(" + InformeGenerico.valorColumna(celdaPuntero.getColumnIndex()-MetaConcepto.listado.size())+(celdaPuntero.getRowIndex()+1)+":"+InformeGenerico.valorColumna(celdaPuntero.getColumnIndex()-1)+(celdaPuntero.getRowIndex()+1)+")";
						celdaPuntero.setCellType(CellType.FORMULA);
						celdaPuntero.setCellFormula(formula);
						celdaPuntero.setCellStyle(EstiloCelda.getEstiloCelda(1, color, TipoDato.FORMATO_MONEDA));
						
						hojaResumen.hoja.setColumnGroupCollapsed(celdaPuntero.getColumnIndex()-1, true);
					}
				}
				
				celdaPuntero = hojaResumen.offset(celdaPuntero, 0, 1);
				celdaConcepto = hojaResumen.offset(celdaConcepto, 0, 1);
			}
			
			celdaPuntero = hojaResumen.offset(celdaPuntero, 0, -6);
			String formula = this.getFormulaSumaAnio(0, p, hoja, s, MetaConcepto.porId(MetaConcepto.TREI), MetaGerencia.porId(MetaGerencia.GGP), true);
			celdaPuntero.setCellType(CellType.FORMULA);
			celdaPuntero.setCellFormula(formula);
			celdaPuntero.setCellStyle(EstiloCelda.getEstiloCelda(1, color, TipoDato.FORMATO_MONEDA));
			celdaPuntero = hojaResumen.offset(celdaPuntero, 0, 1);
			
			formula = this.getFormulaSumaAnio(0, p, hoja, s, MetaConcepto.porId(MetaConcepto.TREI), MetaGerencia.porId(MetaGerencia.GGP), false);
			celdaPuntero.setCellType(CellType.FORMULA);
			celdaPuntero.setCellFormula(formula);
			celdaPuntero.setCellStyle(EstiloCelda.getEstiloCelda(1, color, TipoDato.FORMATO_MONEDA));
			celdaPuntero = hojaResumen.offset(celdaPuntero, 0, 1);
			
			formula = "" + InformeGenerico.valorColumna(celdaPuntero.getColumnIndex()-2)+(celdaPuntero.getRowIndex()+1)+"-"+InformeGenerico.valorColumna(celdaPuntero.getColumnIndex()-1)+(celdaPuntero.getRowIndex()+1);
			celdaPuntero.setCellType(CellType.FORMULA);
			celdaPuntero.setCellFormula(formula);
			celdaPuntero.setCellStyle(EstiloCelda.getEstiloCelda(1, color, TipoDato.FORMATO_MONEDA));
			celdaPuntero = hojaResumen.offset(celdaPuntero, 0, 1);
			
			formula = this.getFormulaSumaAnio(0, p, hoja, s, MetaConcepto.porId(MetaConcepto.TREI), MetaGerencia.porId(MetaGerencia.GDT), true);
			celdaPuntero.setCellType(CellType.FORMULA);
			celdaPuntero.setCellFormula(formula);
			celdaPuntero.setCellStyle(EstiloCelda.getEstiloCelda(1, color, TipoDato.FORMATO_MONEDA));
			celdaPuntero = hojaResumen.offset(celdaPuntero, 0, 1);
			
			formula = this.getFormulaSumaAnio(0, p, hoja, s, MetaConcepto.porId(MetaConcepto.TREI), MetaGerencia.porId(MetaGerencia.GDT), false);
			celdaPuntero.setCellType(CellType.FORMULA);
			celdaPuntero.setCellFormula(formula);
			celdaPuntero.setCellStyle(EstiloCelda.getEstiloCelda(1, color, TipoDato.FORMATO_MONEDA));
			celdaPuntero = hojaResumen.offset(celdaPuntero, 0, 1);
			
			formula = "" + InformeGenerico.valorColumna(celdaPuntero.getColumnIndex()-2)+(celdaPuntero.getRowIndex()+1)+"-"+InformeGenerico.valorColumna(celdaPuntero.getColumnIndex()-1)+(celdaPuntero.getRowIndex()+1);
			celdaPuntero.setCellType(CellType.FORMULA);
			celdaPuntero.setCellFormula(formula);
			celdaPuntero.setCellStyle(EstiloCelda.getEstiloCelda(1, color, TipoDato.FORMATO_MONEDA));

			
			filaDatos = hojaResumen.offset(filaDatos, 1, 0);
			celdaPuntero = filaDatos;
		}
		
		hojaResumen.hoja.groupRow(celdaArranque.getRowIndex(), celdaPuntero.getRowIndex()-1);
				
		celdaPuntero.setCellType(CellType.STRING);
		celdaPuntero.setCellValue(FormateadorDatos.formateaDato(Constantes.toNumBoolean(p.getValorParametro(MetaParametro.PROYECTO_CERRADO).valorEntero),TipoDato.FORMATO_BOOLEAN));
		celdaPuntero.setCellStyle(EstiloCelda.getEstiloCelda(2, EstiloCelda.GRIS, TipoDato.FORMATO_TXT));
		celdaPuntero = hojaResumen.offset(celdaPuntero, 0, 1);
		celdaConcepto = hojaResumen.offset(celdaConcepto, 0, 1);
		
		celdaPuntero.setCellType(CellType.STRING);
		celdaPuntero.setCellValue(acronProy);
		celdaPuntero.setCellStyle(EstiloCelda.getEstiloCelda(2, EstiloCelda.GRIS, TipoDato.FORMATO_TXT));
		celdaPuntero = hojaResumen.offset(celdaPuntero, 0, 1);
		celdaConcepto = hojaResumen.offset(celdaConcepto, 0, 1);
		
		celdaPuntero.setCellType(CellType.STRING);
		celdaPuntero.setCellValue("TOTAL");
		celdaPuntero.setCellStyle(EstiloCelda.getEstiloCelda(2, EstiloCelda.GRIS, TipoDato.FORMATO_TXT));
		celdaPuntero = hojaResumen.offset(celdaPuntero, 0, 1);
		celdaConcepto = hojaResumen.offset(celdaConcepto, 0, 1);
		
		for (int i=0;i<(6+13*(1+MetaConcepto.listado.size()));i++) {
			celdaPuntero.setCellType(CellType.FORMULA);
			celdaPuntero.setCellFormula("sum("+ InformeGenerico.valorColumna(celdaPuntero.getColumnIndex()) + (celdaPuntero.getRowIndex()+1-lSistemas.size()) + ":" + InformeGenerico.valorColumna(celdaPuntero.getColumnIndex()) + celdaPuntero.getRowIndex() + ")");
			celdaPuntero.setCellStyle(EstiloCelda.getEstiloCelda(2, EstiloCelda.GRIS, TipoDato.FORMATO_MONEDA));
			celdaPuntero = hojaResumen.offset(celdaPuntero, 0, 1);
		}
		
		hojaResumen.hoja.setRowGroupCollapsed(celdaPuntero.getRowIndex()-1, true);
		
		filaDatos = hojaResumen.offset(filaDatos, 1, 0);
		HashMap<String,PosicionExcel> anclasProy = anclas.get(-1);
		anclasProy.put(ReporteEconomicoProyectos.ANCLA_PUNTERO_RESUMEN, new PosicionExcel(filaDatos.getRowIndex(),filaDatos.getColumnIndex(),ReporteEconomicoProyectos.ANCLA_PUNTERO_RESUMEN));
			
	}
	
	private void rellenaDatos(Proyecto p, HojaExcel hoja) {
		PosicionExcel posIni = anclas.get(p.id).get(ReporteEconomicoProyectos.ANCLA_DETALLE);
		
		XSSFSheet xssfSheetNew = hoja.hoja;
		
		XSSFRow xssfRowFechas;
		XSSFRow xssfRowSistemas;
        XSSFCell cellNew;
        XSSFCell cellFecha;
        XSSFCell cellSistema;
        XSSFCell cellGerencia;
        XSSFCell cellMetaC;
        
        xssfRowFechas = xssfSheetNew.getRow(posIni.fila); 
        xssfRowSistemas = xssfSheetNew.getRow(posIni.fila+2);
        
        Iterator<Sistema> itSistemas = this.lSistemas.iterator();
        int contadorFechas = 0;
        int contadorSistemas = 0;
        while (itSistemas.hasNext()) {
        	itSistemas.next();
        	xssfRowSistemas = xssfSheetNew.getRow(posIni.fila+2+contadorSistemas);
        	cellSistema = xssfRowSistemas.getCell(posIni.columna);
        	String sistema = cellSistema.getStringCellValue();
        	
        	
        	for (int i=0;i<lConceptos.size()+1;i++) {
        		
        		cellMetaC = xssfSheetNew.getRow(xssfRowSistemas.getRowNum()+i).getCell(posIni.columna+2);
        		
        		String codMetaconcepto = cellMetaC.getStringCellValue();
        		cellGerencia = null;
        		int j=0;
        		while (j!=10) {
        			try {
        				cellGerencia = xssfSheetNew.getRow(xssfRowSistemas.getRowNum()+i-j).getCell(posIni.columna+1);
        				if (cellGerencia==null) throw new Exception();
        				break;
        			} catch (Exception e) {
        				j = j+1;
        			}
        		}
        		
        		String gerencia = cellGerencia.getStringCellValue();
				MetaGerencia mg = MetaGerencia.getPorCodigo(gerencia);
        		
        		contadorFechas = 3;
        		cellFecha = xssfRowFechas.getCell(posIni.columna+contadorFechas);
            	String fecha = cellFecha.getStringCellValue();
        		
        		while (!"TOTAL".equals(fecha)) {
            		String [] corteFecha = fecha.split("'");
                	int mes = Constantes.numMes(corteFecha[0]);
                	int anio = new Integer(corteFecha[1]);
                	            	
                	float estimado = 0;
                	float imputado = 0;
                	
                	Iterator<EstimacionAnio> itEa = this.ap.estimacionAnual.iterator();
                	while (itEa.hasNext()) {
                		EstimacionAnio ea = itEa.next();
                		if (ea.anio == anio) {
                			Concepto cBuscado = null;
                			try {
                				cBuscado = ea.estimacionesMensuales.get(mes-1).estimacionesPorSistemas.get(sistema).listaConceptos.get(codMetaconcepto);
                			} catch (Exception e) {
                			}
                			try {
                				if (cBuscado!=null && cBuscado.listaEstimaciones!=null) {
                					Iterator<Estimacion> itEst = cBuscado.listaEstimaciones.iterator();
                					while (itEst.hasNext()) {
                						Estimacion e = itEst.next();
                						if (codMetaconcepto.equals(MetaConcepto.porId(MetaConcepto.TREI).codigo)) {
                							if (mg.id == e.gerencia.id) {
                								estimado += e.importe;
                							} 
                						} else                 						
                							estimado += e.importe;
                					}
                					if (cBuscado.topeImputacion!=null) {
                						if (codMetaconcepto.equals(MetaConcepto.porId(MetaConcepto.TREI).codigo)) {
                							if (mg.id == MetaGerencia.GGP) {
                								estimado += cBuscado.topeImputacion.cantidad * cBuscado.porc_trei_ggp/100;
                							} else {
                								estimado += cBuscado.topeImputacion.cantidad * (100-cBuscado.porc_trei_ggp)/100;
                							}
                						} else {
                							estimado += cBuscado.topeImputacion.cantidad;
                						}
                						
                					}
                				}
                			} catch (Exception e){
                        		estimado = 0;
                        	}                			
                			try {
                				if (cBuscado!=null && cBuscado.listaImputaciones!=null) {
                					Iterator<Imputacion> itImp = cBuscado.listaImputaciones.iterator();
                					while (itImp.hasNext()) {
                						Imputacion imp = itImp.next();
                						
                						float importe = 0;
                    					
                    					if (imp.tipoImputacion == Imputacion.IMPUTACION_NO_FRACCIONADA) {
                    						importe = imp.importe;
                    					}
                    					
                    					if (imp.tipoImputacion == Imputacion.FRACCION_IMPUTACION || imp.tipoImputacion == Imputacion.IMPUTACION_FRACCIONADA) {
                    						importe = imp.imputacionFraccion.getImporte();
                    					}
                						
                						if (codMetaconcepto.equals(MetaConcepto.porId(MetaConcepto.TREI).codigo)) {
                							if (mg.id == imp.gerencia.id) {
                								imputado += importe;
                							} 
                						} else                 						
                							imputado += importe;
                					}
                				}
                			} catch (Exception e){
                        		imputado = 0;
                        	}  
                			try {
                				if (cBuscado!=null && cBuscado.listaCertificaciones!=null) {
                					Iterator<CertificacionFaseParcial> itCfp = cBuscado.listaCertificaciones.iterator();
                					while (itCfp.hasNext()) {
                						CertificacionFaseParcial imp = itCfp.next();
                						imputado += imp.valReal;
                						estimado += imp.valEstimado;
                					}
                				}
                			} catch (Exception e){
                        		imputado = 0;
                        	} 
                			break;
                		}
                	}
                	
                	cellNew = xssfSheetNew.getRow(cellMetaC.getRowIndex()).createCell(cellFecha.getColumnIndex());
                	cellNew.setCellStyle(EstiloCelda.getEstiloCelda(0, EstiloCelda.GRIS, TipoDato.FORMATO_MONEDA,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
                	cellNew.setCellValue(estimado);
                	cellNew = xssfSheetNew.getRow(cellMetaC.getRowIndex()).createCell(cellFecha.getColumnIndex()+1);
                	cellNew.setCellStyle(EstiloCelda.getEstiloCelda(0, EstiloCelda.GRIS, TipoDato.FORMATO_MONEDA,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
                	cellNew.setCellValue(imputado);
                	
                	contadorFechas = contadorFechas + 2;
                	cellFecha = xssfRowFechas.getCell(posIni.columna+contadorFechas);
                	fecha = cellFecha.getStringCellValue();
            	}
        	}
        	contadorSistemas = contadorSistemas + lConceptos.size()+2;
        }
        
        xssfRowFechas = xssfSheetNew.getRow(posIni.fila); 
        xssfRowSistemas = xssfSheetNew.getRow(posIni.fila+2);
        
        itSistemas = this.lSistemas.iterator();
        contadorFechas = 0;
        contadorSistemas = 0;
        
        while (itSistemas.hasNext()) {
        	itSistemas.next();
        	xssfRowSistemas = xssfSheetNew.getRow(posIni.fila+2+contadorSistemas);
        	cellSistema = xssfRowSistemas.getCell(posIni.columna);
        	String sistema = cellSistema.getStringCellValue();
        	
        	for (int i=0;i<lConceptos.size()+1;i++) {
        		
        		cellMetaC = xssfSheetNew.getRow(xssfRowSistemas.getRowNum()+i).getCell(posIni.columna+2);
        		
        		contadorFechas = 3;
        		cellFecha = xssfRowFechas.getCell(posIni.columna+contadorFechas);
            	String fecha = cellFecha.getStringCellValue();
            	
            	String totalEstimado="0";
            	String totalReal="0";
        		
        		while (!"TOTAL".equals(fecha)) {              	            	
                	
                	totalEstimado += "+"   + InformeGenerico.valorColumna(cellFecha.getColumnIndex())+ ""+ (cellMetaC.getRowIndex()+1);
                	totalReal +=  "+"   + InformeGenerico.valorColumna(cellFecha.getColumnIndex()+1)+ ""+ (cellMetaC.getRowIndex()+1);               	
                	
                	contadorFechas = contadorFechas + 2;
                	cellFecha = xssfRowFechas.getCell(posIni.columna+contadorFechas);
                	fecha = cellFecha.getStringCellValue();
            	}
        		
        		cellNew = xssfSheetNew.getRow(cellMetaC.getRowIndex()).createCell(cellFecha.getColumnIndex());
            	cellNew.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.GRIS, TipoDato.FORMATO_MONEDA,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
            	cellNew.setCellFormula(totalEstimado);
            	cellNew.setCellType(CellType.FORMULA);
            	cellNew = xssfSheetNew.getRow(cellMetaC.getRowIndex()).createCell(cellFecha.getColumnIndex()+1);
            	cellNew.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.GRIS, TipoDato.FORMATO_MONEDA,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
            	cellNew.setCellFormula(totalReal);
            	cellNew.setCellType(CellType.FORMULA);
        	}
        	contadorSistemas = contadorSistemas + lConceptos.size()+2;
        }
	}
	
	private String getFormulaSumaAnio(int anio, Proyecto p, HojaExcel hoja, Sistema s, MetaConcepto c, MetaGerencia ger, boolean bestimado){
		PosicionExcel posIni = anclas.get(p.id).get(ReporteEconomicoProyectos.ANCLA_DETALLE);
		
		String acronProy = p.getValorParametro(MetaParametro.PROYECTO_ACRONPROY).valorTexto;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int anioActual = cal.get(Calendar.YEAR); 
		
		XSSFCell filaBuscada = formulaCeldas(p, hoja, s, c, ger);
		
		String formula = "0";
		
		XSSFCell fecha = hoja.get(posIni.fila,posIni.columna+3);
		
		int estimado = 0;
		if (!bestimado) estimado++;
		
		while (!"TOTAL".equals(fecha.getStringCellValue())) {
			String [] corteFecha = fecha.getStringCellValue().split("'");
        	int anioIterato = new Integer(corteFecha[1]);
        	
        	boolean incluye = false;
        	if (anio==-1 && anioIterato<anioActual) incluye = true;
        	if (anio==0 && anioIterato==anioActual) incluye = true;
        	if (anio==1 && anioIterato>anioActual) incluye = true;
        	
        	if (incluye){
        		formula += "+ '" + acronProy + "'!" + InformeGenerico.valorColumna(fecha.getColumnIndex()+estimado) + (1+filaBuscada.getRowIndex());
        	}
        	
        	fecha = hoja.offset(fecha, 0, 2);
		}
		
		return formula;
	}
	
	
	private XSSFCell formulaCeldas(Proyecto p, HojaExcel hoja, Sistema s, MetaConcepto c, MetaGerencia ger) {
		PosicionExcel posIni = anclas.get(p.id).get(ReporteEconomicoProyectos.ANCLA_DETALLE);
		
		XSSFSheet xssfSheetNew = hoja.hoja;
		
		XSSFRow xssfRowFechas;
		XSSFRow xssfRowSistemas;
        XSSFCell cellNew;
        XSSFCell cellFecha;
        XSSFCell cellSistema;
        XSSFCell cellGerencia;
        XSSFCell cellMetaC;
        
        xssfRowFechas = xssfSheetNew.getRow(posIni.fila); 
        xssfRowSistemas = xssfSheetNew.getRow(posIni.fila+2);
        
        Iterator<Sistema> itSistemas = this.lSistemas.iterator();
        int contadorFechas = 0;
        int contadorSistemas = 0;
        while (itSistemas.hasNext()) {
        	itSistemas.next();
        	xssfRowSistemas = xssfSheetNew.getRow(posIni.fila+2+contadorSistemas);
        	cellSistema = xssfRowSistemas.getCell(posIni.columna);
        	String sistema = cellSistema.getStringCellValue();
        	
        	if (Sistema.get(sistema).id == s.id) {
        		for (int i=0;i<lConceptos.size()+1;i++) {            		
            		cellMetaC = xssfSheetNew.getRow(xssfRowSistemas.getRowNum()+i).getCell(posIni.columna+2);
            		
            		String codMetaconcepto = cellMetaC.getStringCellValue();
            		
            		if (codMetaconcepto.equals(c.codigo)) {
            			if (ger!=null) {
            				cellGerencia = null;
                    		int j=0;
                    		while (j!=10) {
                    			try {
                    				cellGerencia = xssfSheetNew.getRow(xssfRowSistemas.getRowNum()+i-j).getCell(posIni.columna+1);
                    				if (cellGerencia==null) throw new Exception();
                    				break;
                    			} catch (Exception e) {
                    				j = j+1;
                    			}
                    		}
                    		
                    		String gerencia = cellGerencia.getStringCellValue();
            				MetaGerencia mg = MetaGerencia.getPorCodigo(gerencia);
            				
            				if (ger.id == mg.id) {
            					return cellMetaC;
            				}
            			} else {
            				return cellMetaC;
            			}
            		}
            	}
        	}
        	contadorSistemas = contadorSistemas + lConceptos.size()+2;
        }
        
        return null;
	}
	
	private int pintaDesglose(Proyecto p, int contador, HojaExcel hoja){
		contador = contador +3;
		XSSFRow xssfRowNew;
		XSSFRow xssfRowNewAux;
        XSSFCell cellNew; 
        
        XSSFSheet xssfSheetNew = hoja.hoja;
		
		HashMap<String,PosicionExcel> anclaProyecto = anclas.get(p.id);
		anclaProyecto.put(ReporteEconomicoProyectos.ANCLA_DETALLE, new PosicionExcel(contador, 1, ReporteEconomicoProyectos.ANCLA_DETALLE));
		
		xssfRowNew = xssfSheetNew.createRow(contador++); 
		xssfRowNewAux = xssfSheetNew.createRow(contador);
		
		int contadorColumna = 4;
		Iterator<EstimacionAnio> itAnios = ap.estimacionAnual.iterator();
		while (itAnios.hasNext()) {
			EstimacionAnio ea = itAnios.next();
			Iterator<EstimacionMes> itMeses = ea.estimacionesMensuales.values().iterator();
			while (itMeses.hasNext()) {
				EstimacionMes em = itMeses.next();
				
				cellNew = xssfRowNew.createCell(contadorColumna);
		        cellNew.setCellType(CellType.STRING);
		        cellNew.setCellValue(Constantes.nomMes(em.mes-1) + "'" + ea.anio);
		        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
		        CellRangeAddress cellRangeAddress = new CellRangeAddress(xssfRowNew.getRowNum(), xssfRowNew.getRowNum(), cellNew.getColumnIndex(), cellNew.getColumnIndex()+1);
				xssfSheetNew.addMergedRegion(cellRangeAddress);
				
				cellNew = xssfRowNewAux.createCell(contadorColumna);
		        cellNew.setCellType(CellType.STRING);
		        cellNew.setCellValue("Estimado");
		        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(3, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
		        
				cellNew = xssfRowNewAux.createCell(contadorColumna+1);
		        cellNew.setCellType(CellType.STRING);
		        cellNew.setCellValue("Real");
		        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(3, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
		        
		        contadorColumna = contadorColumna+2;
			}
		}
		
		cellNew = xssfRowNew.createCell(contadorColumna);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("TOTAL");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.GRIS, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
        CellRangeAddress cellRangeAddress = new CellRangeAddress(xssfRowNew.getRowNum(), xssfRowNew.getRowNum(), cellNew.getColumnIndex(), cellNew.getColumnIndex()+1);
		xssfSheetNew.addMergedRegion(cellRangeAddress);
		
		cellNew = xssfRowNewAux.createCell(contadorColumna);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Estimado");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(3, EstiloCelda.GRIS, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
        
		cellNew = xssfRowNewAux.createCell(contadorColumna+1);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Real");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(3, EstiloCelda.GRIS, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
        
        int finalCuadro = cellNew.getColumnIndex();
        
        anclaProyecto.put(ReporteEconomicoProyectos.ANCLA_TOTAL_FECHAS, new PosicionExcel(anclaProyecto.get(ReporteEconomicoProyectos.ANCLA_DETALLE).fila+1, finalCuadro, ReporteEconomicoProyectos.ANCLA_TOTAL_FECHAS));
		
		xssfRowNew = xssfSheetNew.createRow(xssfRowNewAux.getRowNum()+1);
		contador = xssfRowNewAux.getRowNum()+1;
		
		contadorColumna = 1;
		
		cellNew = xssfRowNew.createCell(1);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Sistema");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
        cellNew = xssfRowNew.createCell(2);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Área");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(3, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
        cellNew = xssfRowNew.createCell(3);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Concepto");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(2, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
		
		Iterator<Sistema> itSistemas = lSistemas.iterator();
		while (itSistemas.hasNext()) {
			Sistema s = itSistemas.next();
			
			cellNew = xssfRowNew.createCell(contadorColumna);
	        cellNew.setCellType(CellType.STRING);
	        cellNew.setCellValue(s.codigo);
	        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
	        cellRangeAddress = new CellRangeAddress(xssfRowNew.getRowNum(), xssfRowNew.getRowNum()+lConceptos.size(), cellNew.getColumnIndex(), cellNew.getColumnIndex());
			xssfSheetNew.addMergedRegion(cellRangeAddress);
			
			Iterator<MetaGerencia> itMg = MetaGerencia.listado.values().iterator();
			
			while (itMg.hasNext()) {
				MetaGerencia g  = itMg.next();
				
				ArrayList<MetaConcepto> listaConceptosPintar = new ArrayList<MetaConcepto>();
				
				Iterator<Concepto> itConcepto = s.listaConceptos.values().iterator();
				
				while (itConcepto.hasNext()) {
					Concepto c = itConcepto.next();
					
					if (g.metaConceptos !=null ){
						if (g.metaConceptos.contains(c.tipoConcepto)) {
							listaConceptosPintar.add(c.tipoConcepto);
						}
					}
				}
				
				if (listaConceptosPintar.size()>0) {
					cellNew = xssfRowNew.createCell(contadorColumna+1);
			        cellNew.setCellType(CellType.STRING);
			        cellNew.setCellValue(g.codigo);
			        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(3, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
			        if (listaConceptosPintar.size()-1>0){
			        	cellRangeAddress = new CellRangeAddress(xssfRowNew.getRowNum(), xssfRowNew.getRowNum()+listaConceptosPintar.size()-1, cellNew.getColumnIndex(), cellNew.getColumnIndex());
						xssfSheetNew.addMergedRegion(cellRangeAddress);
			        }
					
					Collections.sort(listaConceptosPintar);
					Iterator<MetaConcepto> itMConcepto = listaConceptosPintar.iterator();
					while (itMConcepto.hasNext()) {
						MetaConcepto mc = itMConcepto.next();
						cellNew = xssfRowNew.createCell(contadorColumna+2);
				        cellNew.setCellType(CellType.STRING);
				        cellNew.setCellValue(mc.codigo);
				        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(2, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
				        contador++;
						xssfRowNew = xssfSheetNew.createRow(contador);
					}
				}
						
			}
			
			xssfRowNew = xssfSheetNew.createRow(contador);
			cellNew = xssfRowNew.createCell(1);
			cellNew.setCellStyle(EstiloCelda.getEstiloCelda(9, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
			xssfRowNew.setHeightInPoints(5);
	        cellRangeAddress = new CellRangeAddress(xssfRowNew.getRowNum(), xssfRowNew.getRowNum(), contadorColumna, finalCuadro);
			xssfSheetNew.addMergedRegion(cellRangeAddress);
			
			contador = contador + 1;
			xssfRowNew = xssfSheetNew.createRow(contador);
		}
				
		contador = contador+1;
		
		return contador;
	}
	
	private XSSFCell buscaSistemaResumen(Proyecto p, HojaExcel hoja, Sistema s) {
			PosicionExcel posIni = anclas.get(p.id).get(ReporteEconomicoProyectos.ANCLA_RESUMEN);
			
			XSSFSheet xssfSheetNew = hoja.hoja;
			XSSFRow xssfRowMetaC= null;
			XSSFRow xssfRowSistemas= null;
	        XSSFCell cellNew;
	        XSSFCell cellSistema;
	        XSSFCell cellMetaC;
	        
	        int contadorSistemas = 0;
	        Iterator<Sistema> itSistemas = this.lSistemas.iterator();
	        while (itSistemas.hasNext()) {
	        	itSistemas.next();
	        	xssfRowSistemas = xssfSheetNew.getRow(posIni.fila+2+contadorSistemas);
	        	cellSistema = xssfRowSistemas.getCell(posIni.columna);
	        	Sistema sIterado = Sistema.getPorNombre(cellSistema.getStringCellValue());
	        	
	        	if (sIterado.id == s.id)
	        		return cellSistema;
	        	contadorSistemas++;
	        }
	        
	        return null;
	}
	
	
	private int pintaResumen(Proyecto p, int contador, HojaExcel hoja) {
		contador = contador +3;
		XSSFRow xssfRowNew;
		XSSFRow xssfRowNewAux;
        XSSFCell cellNew; 
		
        XSSFSheet xssfSheetNew = hoja.hoja;
        
		HashMap<String,PosicionExcel> anclaProyecto = anclas.get(p.id);
		anclaProyecto.put(ReporteEconomicoProyectos.ANCLA_RESUMEN, new PosicionExcel(contador, 1, ReporteEconomicoProyectos.ANCLA_RESUMEN));
		
		lSistemas = null;
		
		xssfRowNew = xssfSheetNew.createRow(contador++); 
		
		Iterator<EstimacionAnio> itAnios = ap.estimacionAnual.iterator();
		while (itAnios.hasNext()) {
			EstimacionAnio ea = itAnios.next();
			Iterator<EstimacionMes> itMeses = ea.estimacionesMensuales.values().iterator();
			while (itMeses.hasNext()) {
				EstimacionMes em = itMeses.next();
				
				lSistemas = new ArrayList<Sistema>();
				lSistemas.addAll(em.estimacionesPorSistemas.values());
				Collections.sort(lSistemas);
				break;
			}
			if (lSistemas!=null && lSistemas.size()!=0) break;
		}
		
		xssfRowNew = xssfSheetNew.createRow(contador++); 
		for (int j=0;j<lSistemas.size()+1;j++) {
			xssfRowNewAux = xssfSheetNew.createRow(contador+j);
        }
		
		int nConceptos = 0;
		
		itAnios = ap.estimacionAnual.iterator();
		while (itAnios.hasNext()) {
			EstimacionAnio ea = itAnios.next();
			Iterator<EstimacionMes> itMeses = ea.estimacionesMensuales.values().iterator();
			while (itMeses.hasNext()) {
				EstimacionMes em = itMeses.next();
				Iterator<Sistema> itSistemas = em.estimacionesPorSistemas.values().iterator();
				while (itSistemas.hasNext()) {
					Sistema s = itSistemas.next();
					
					int contadorColumna = 2;
					
					for(int i=0;i<3;i++) {
						lConceptos = new ArrayList<Concepto>();
						lConceptos.addAll(s.listaConceptos.values());
						nConceptos = lConceptos.size()+1;
						Collections.sort(lConceptos);
						Iterator<Concepto> itConcepto = lConceptos.iterator();
						int color=0;
						if (i==0) color = EstiloCelda.NARANJA;
						if (i==1) color = EstiloCelda.VERDE;
						if (i==2) color = EstiloCelda.MARRÓN;
						
						while (itConcepto.hasNext()) {
							Concepto c = itConcepto.next();
							
							cellNew = xssfRowNew.createCell(contadorColumna++);
					        cellNew.setCellType(CellType.STRING);
					        cellNew.setCellValue(c.tipoConcepto.codigo);
					        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(3, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
					        
					        for (int j=0;j<lSistemas.size()+1;j++) {
								xssfRowNewAux = xssfSheetNew.getRow(contador+j);
								cellNew = xssfRowNewAux.createCell(contadorColumna-1);
						        cellNew.setCellType(CellType.NUMERIC);
						        cellNew.setCellValue(0);
						        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(1, color, TipoDato.FORMATO_MONEDA));
					        }
						}
						
						cellNew = xssfRowNew.createCell(contadorColumna++);
				        cellNew.setCellType(CellType.STRING);
				        cellNew.setCellValue(Sistema.getInstanceTotal().codigo);
				        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
				        
				        for (int j=0;j<lSistemas.size()+1;j++) {
							xssfRowNewAux = xssfSheetNew.getRow(contador+j);
							cellNew = xssfRowNewAux.createCell(contadorColumna-1);
					        cellNew.setCellType(CellType.NUMERIC);
					        cellNew.setCellValue(0);
					        if (j!=lSistemas.size())
					        	cellNew.setCellStyle(EstiloCelda.getEstiloCelda(2, color, TipoDato.FORMATO_MONEDA));
					        else
					        	cellNew.setCellStyle(EstiloCelda.getEstiloCelda(3, color, TipoDato.FORMATO_MONEDA));
				        }
				        
					}
					break;
				}
				break;
			}
			break;
		}
		
		xssfRowNew = xssfSheetNew.createRow(xssfRowNew.getRowNum()-1);
		cellNew = xssfRowNew.createCell(2);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Presentado CDSI");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
        CellRangeAddress cellRangeAddress = new CellRangeAddress(xssfRowNew.getRowNum(), xssfRowNew.getRowNum(), cellNew.getColumnIndex(), cellNew.getColumnIndex()+nConceptos-1);
		xssfSheetNew.addMergedRegion(cellRangeAddress);
		cellNew = xssfRowNew.createCell(2+nConceptos);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Estimado");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
        cellRangeAddress = new CellRangeAddress(xssfRowNew.getRowNum(), xssfRowNew.getRowNum(), cellNew.getColumnIndex(), cellNew.getColumnIndex()+nConceptos-1);
		xssfSheetNew.addMergedRegion(cellRangeAddress);
		cellNew = xssfRowNew.createCell(2+nConceptos*2);
		cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Imputado/Certificado");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
        cellRangeAddress = new CellRangeAddress(xssfRowNew.getRowNum(), xssfRowNew.getRowNum(), cellNew.getColumnIndex(), cellNew.getColumnIndex()+nConceptos-1);
		xssfSheetNew.addMergedRegion(cellRangeAddress);
		
		
		Iterator<Sistema> itSistemas = lSistemas.iterator();
		while (itSistemas.hasNext()) {
			Sistema s = itSistemas.next();
			
			xssfRowNew = xssfSheetNew.getRow(contador++); 
	        cellNew = xssfRowNew.createCell(1);
	        cellNew.setCellType(CellType.STRING);
	        cellNew.setCellValue(s.descripcion);
	        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));			        
		}
		
		xssfRowNew = xssfSheetNew.getRow(contador++); 
        cellNew = xssfRowNew.createCell(1);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("TOTAL");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
        		
		return contador;
	}
	
	private void calculaEstimacion (Proyecto p){
		Date fechaPivote = null;
		
		Presupuesto pres = new Presupuesto();
		pres = pres.dameUltimaVersionPresupuesto(p);
		pres.cargaCostes();
		
		fechaPivote = Constantes.fechaActual();
		
	    ap = new AnalizadorPresupuesto();
		ap.construyePresupuestoMensualizado(pres,fechaPivote,null,null,null,null);	
		
	}
	
	private int pintaCabeceraProyecto(Proyecto p, int contador, HojaExcel xssfSheetNew) {
        XSSFCell cellNew; 
        
        cellNew = xssfSheetNew.get(contador,1);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Proyecto");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
        
        cellNew = xssfSheetNew.get(contador,2);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue(p.nombre);
        
        contador++;
        
        cellNew = xssfSheetNew.get(contador,1);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Acrónimo");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
        
        cellNew = xssfSheetNew.get(contador,2);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue((String) p.getValorParametro(MetaParametro.PROYECTO_ACRONPROY).getValor());
        
        contador++;
        
        cellNew = xssfSheetNew.get(contador,1);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Código PPM");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
        
        cellNew = xssfSheetNew.get(contador,2);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue((String) p.getValorParametro(MetaParametro.PROYECTO_CODPPM).getValor());
		
		return contador++;
	}
}
