package model.utils.xls.informes;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellAlignment;

import javafx.scene.control.Cell;
import model.beans.CertificacionFaseParcial;
import model.beans.Concepto;
import model.beans.Foto;
import model.constantes.Constantes;
import model.metadatos.MetaConcepto;
import model.metadatos.MetaParametro;
import model.metadatos.TipoDato;

public class InformeFoto extends InformeGenerico implements EscritorXLS {
	String nombreArchivo = "Imputaciones+DSI.xlsx";
	String rutaArchivo = nombreArchivo;
	String hoja = "Hoja1";
	Foto foto1 = null;
	Foto foto2 = null;
	int minAnio = 5000;
	int maxAnio = 0;
	int minMes = 13;
	int maxMes = 0;
	
	public static final String FOTO = "FOTO";
	public static final String FOTO_COMPARAR = "FOTO_COMPARAR";
	
	XSSFSheet sheet = null;
	
	@Override
	public void escribeFichero(HashMap<String, Object> datos) throws Exception {
		if (newExcelFile==null) return;
		
		OutputStream excelNewOutputStream = null;
        excelNewOutputStream = new FileOutputStream(newExcelFile);
        
        worbook = new XSSFWorkbook();
        new EstiloCelda(worbook);
        
        Foto foto1 = (Foto) datos.get(InformeFoto.FOTO);	
		
        pintaFoto(datos,foto1,1);
        
        Foto foto2 = (Foto) datos.get(InformeFoto.FOTO_COMPARAR);
        
        if (foto2!=null) {
        	pintaFoto(datos,foto2,2);
        }
        
        worbook.write(excelNewOutputStream);
        
        excelNewOutputStream.close();
        
	}
	
	private void pintaFoto(HashMap<String, Object> datos, Foto foto, int vs) {
			
		
        XSSFSheet xssfSheetNew = worbook.createSheet((String) foto.proyecto.getValorParametro(MetaParametro.PROYECTO_ACRONPROY).getValor()+"_"+vs);
        
        int contador = 0;
        		
        contador = escribircabecera(xssfSheetNew,++contador,foto);
        contador = construyeDesglosadoMeses(xssfSheetNew, ++contador,foto);
	}
	
	private int construyeDesglosadoMeses(XSSFSheet xssfSheetNew, int contador,Foto foto) {
		
		HashMap<Integer,HashMap<Integer,HashMap<String,HashMap<String,Concepto>>>> arbol = foto.construyeArbol();
		
		HashMap<String,HashMap<String,Integer>> posSMC = construyeEsqueleto(xssfSheetNew, arbol, contador,foto);
		
		XSSFRow xssfRowNew;
        XSSFCell cellNew;
                
		int contadorCol = 3;
		for (int anio=minAnio;anio<=maxAnio; anio++) {
			for (int mes=1;mes<13;mes++) {
				
				if ((anio==minAnio && mes>=minMes) || (anio!=minAnio && anio!=maxAnio) || (anio==maxAnio && mes<=maxMes)) {
					HashMap<Integer,HashMap<String,HashMap<String,Concepto>>> anioA = arbol.get(anio);
					
					if (anioA!=null ){
						HashMap<String,HashMap<String,Concepto>> mesA = anioA.get(mes);
						
						if (mesA!=null) {
							Iterator<String> itSistemas = mesA.keySet().iterator();
							
							while (itSistemas.hasNext()) {
								String codSistema = itSistemas.next();
								HashMap<String,Concepto> sistema = mesA.get(codSistema);
								
								Iterator<Concepto> itConceptos = sistema.values().iterator();
								
								while (itConceptos.hasNext()) {
									Concepto c = itConceptos.next();
									int contadorMC = posSMC.get(codSistema).get(c.tipoConcepto.codigo);
				            		xssfRowNew = xssfSheetNew.getRow(contadorMC); 
				            		if (xssfRowNew!=null) {
				            			cellNew = xssfRowNew.createCell(contadorCol);
								        cellNew.setCellType(CellType.NUMERIC);
								            
							            if (c.tipoConcepto.tipoGestionEconomica == MetaConcepto.GESTION_HORAS){
								            if (c.listaEstimaciones!=null && c.listaEstimaciones.size()!=0){
								            	cellNew.setCellValue(c.listaEstimaciones.get(0).importe);
								                cellNew.setCellStyle(EstiloCelda.getEstiloCelda(0, EstiloCelda.BLANCO, TipoDato.FORMATO_MONEDA));
								            }
							            } else {
							            	float acumulado = 0;
							            	
							            	if (c.listaCertificaciones!=null && c.listaCertificaciones.size()!=0){
							            		Iterator<CertificacionFaseParcial> itCertificaciones = c.listaCertificaciones.iterator();
							            		while (itCertificaciones.hasNext()) {
							            			CertificacionFaseParcial cfp = itCertificaciones.next();
							            			acumulado += cfp.valEstimado;
							            		}
							            		cellNew.setCellValue(acumulado);
							            		cellNew.setCellStyle(EstiloCelda.getEstiloCelda(0, EstiloCelda.BLANCO, TipoDato.FORMATO_MONEDA));
							            	}
							            }
				            		} else {
				            			System.out.println("No encuentra " + codSistema + " " + c.tipoConcepto.codigo + " -> " + (contador+contadorMC) );
				            		}							        
								}
								
							}							
						}
					}
				}
				
				if ((anio==minAnio && mes>=minMes) || (anio!=minAnio && anio !=maxAnio) || (anio==maxAnio && mes<=maxMes)) 
						contadorCol++;
			}
		}
		
		Iterator<HashMap<String,Integer>> itSistemas = posSMC.values().iterator();
		while (itSistemas.hasNext()) {
			HashMap<String,Integer> conceptos = itSistemas.next();
			
			int minValue = 100;
			Iterator<Integer> itPosiciones = conceptos.values().iterator();
			while (itPosiciones.hasNext()) {
				int value = itPosiciones.next();
				if (value<minValue) minValue = value;
			}
			
			CellRangeAddress cellRangeAddress = new CellRangeAddress(minValue, minValue+conceptos.size()-1, 1, 1);
			xssfSheetNew.addMergedRegion(cellRangeAddress);
			
			xssfRowNew = xssfSheetNew.getRow(minValue); 
			cellNew = xssfRowNew.getCell(1);
	        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(3, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
	        
	        xssfRowNew = xssfSheetNew.getRow(minValue+conceptos.size()-1);
	        for (int i=3;i<contadorCol;i++){
	        	cellNew = xssfRowNew.createCell(i);
	        	cellNew.setCellType(CellType.FORMULA);
		        cellNew.setCellFormula("SUM("+InformeGenerico.valorColumna(i)+(1+minValue)+":"+ InformeGenerico.valorColumna(i)+(minValue+conceptos.size()-1)+")");
		        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(0, EstiloCelda.GRIS, TipoDato.FORMATO_MONEDA));
	        }
	        
	        
	        xssfSheetNew.groupRow (minValue, minValue+conceptos.size()-2);
			
		}
		
		HashMap<String,Integer> conceptos = posSMC.get("TOTAL");
		
		int minValue = 100;
		Iterator<Integer> itPosiciones = conceptos.values().iterator();
		while (itPosiciones.hasNext()) {
			int value = itPosiciones.next();
			if (value<minValue) minValue = value;
		}
				
		xssfRowNew = xssfSheetNew.getRow(minValue); 
		cellNew = xssfRowNew.getCell(1);
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(3, EstiloCelda.AZUL, TipoDato.FORMATO_TXT, EstiloCelda.CENTRO, EstiloCelda.CENTRO));
        
        
        for (int i=3;i<contadorCol;i++){
        	Iterator<String> iConceptos = conceptos.keySet().iterator();
        	
        	while (iConceptos.hasNext()) {
        		String key = iConceptos.next();
        		int pos = conceptos.get(key);
        		
        		String cadenaAcumuladaConcepto = "";
        		
        		xssfRowNew = xssfSheetNew.getRow(pos);
        		cellNew = xssfRowNew.createCell(i);
            	cellNew.setCellType(CellType.FORMULA);
            	
            	if (!"TOTAL".equals(key))
            		cellNew.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.GRIS, TipoDato.FORMATO_MONEDA));
            	else
            		cellNew.setCellStyle(EstiloCelda.getEstiloCelda(3, EstiloCelda.GRIS, TipoDato.FORMATO_MONEDA));
        		
        		Iterator<String> iSistema = posSMC.keySet().iterator();
        		while (iSistema.hasNext()) {
        			String codSistema = iSistema.next();
        			
        			if (!"TOTAL".equals(codSistema)){
        				HashMap<String,Integer> lConceptos = posSMC.get(codSistema);
            			Integer posAux = lConceptos.get(key)+1;
            			if (!cadenaAcumuladaConcepto.equals(""))
            				cadenaAcumuladaConcepto += ",";
            			cadenaAcumuladaConcepto += InformeGenerico.valorColumna(i)+""+posAux;
        			}
        		}
        		cellNew.setCellFormula("SUM("+cadenaAcumuladaConcepto+")");
        	}
        }
        		
		return contador;
	}
	
	private HashMap<String,HashMap<String,Integer>> construyeEsqueleto(XSSFSheet xssfSheetNew, HashMap<Integer,HashMap<Integer,HashMap<String,HashMap<String,Concepto>>>> arbol, int contador, Foto foto) {
		minAnio = 5000;
		maxAnio = 0;
		Iterator<Integer> iNumAnio = arbol.keySet().iterator();
		while (iNumAnio.hasNext()) {
			int iAux = iNumAnio.next();
			if (iAux > maxAnio) maxAnio = iAux;
			if (iAux < minAnio) minAnio = iAux;
		}
		
		minMes = 13;
		Iterator<Integer> iNumMes = arbol.get(minAnio).keySet().iterator();
		while (iNumMes.hasNext()) {
			int iAux = iNumMes.next();
			if (iAux < minMes) minMes = iAux;
		}
		
		maxMes = 0;
		iNumMes = arbol.get(maxAnio).keySet().iterator();
		while (iNumMes.hasNext()) {
			int iAux = iNumMes.next();
			if (iAux > maxMes) maxMes = iAux;
		}
		
		HashMap<String,HashMap<String,Integer>> posSMC = new HashMap<String,HashMap<String,Integer>>();
		
		Iterator<Integer> iAnio = arbol.keySet().iterator();
		while (iAnio.hasNext()) {
			Integer nAnio = iAnio.next();
			Iterator<Integer> iMes = arbol.get(nAnio).keySet().iterator();
			while (iMes.hasNext()) {
				Integer nMes = iMes.next();
				Iterator<String> iSistema = arbol.get(nAnio).get(nMes).keySet().iterator();
				while (iSistema.hasNext()) {
					String codSistema = iSistema.next();
					if (!posSMC.containsKey(codSistema)) {
						posSMC.put(codSistema, new HashMap<String,Integer>());
					}
				}
			}
		}
		
		int contadorOriginal = contador;
		
		HashMap<String,Integer> posMC = null;
		
		XSSFRow xssfRowNew;
        XSSFCell cellNew; 
		
		xssfRowNew = xssfSheetNew.createRow(contador++); 
        cellNew = xssfRowNew.createCell(1);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Mes");
        CellRangeAddress cellRangeAddress = new CellRangeAddress(xssfRowNew.getRowNum(), xssfRowNew.getRowNum(), 1, 2);
		xssfSheetNew.addMergedRegion(cellRangeAddress);
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(3, EstiloCelda.AZUL, TipoDato.FORMATO_TXT,EstiloCelda.CENTRO,EstiloCelda.CENTRO));
                
        Iterator<String> isistemas = posSMC.keySet().iterator();
        while (isistemas.hasNext()) {
        	String codSistema = isistemas.next();
        	posMC = posSMC.get(codSistema);
        	
        	Iterator<MetaConcepto> itMC = MetaConcepto.listado.values().iterator();
            int contadorMC = 1;
            while (itMC.hasNext()) {
            	MetaConcepto mc = itMC.next();
            	xssfRowNew = xssfSheetNew.createRow(contador); 
            	
            	if (contadorMC==1) {
            		cellNew = xssfRowNew.createCell(1);
                    cellNew.setCellType(CellType.STRING);
                    cellNew.setCellValue(codSistema);
            	}             	
            	
                cellNew = xssfRowNew.createCell(2);
                cellNew.setCellType(CellType.STRING);
                cellNew.setCellValue(mc.codigo);
                posMC.put(mc.codigo, contador++);
                cellNew.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
            }
        	xssfRowNew = xssfSheetNew.createRow(contador); 
            cellNew = xssfRowNew.createCell(2);
            cellNew.setCellType(CellType.STRING);
            cellNew.setCellValue("TOTAL");
            posMC.put(MetaConcepto.COD_TOTAL, contador++);
            cellNew.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
        }
        
        posSMC.put("TOTAL",new HashMap<String,Integer>());
        posMC = posSMC.get("TOTAL");
        Iterator<MetaConcepto> itMC = MetaConcepto.listado.values().iterator();
        int contadorMC = 1;
        while (itMC.hasNext()) {
        	MetaConcepto mc = itMC.next();
        	xssfRowNew = xssfSheetNew.createRow(contador); 
        	
        	if (contadorMC==1) {
        		cellNew = xssfRowNew.createCell(1);
                cellNew.setCellType(CellType.STRING);
                cellNew.setCellValue("TOTAL");
        	}             	
        	
            cellNew = xssfRowNew.createCell(2);
            cellNew.setCellType(CellType.STRING);
            cellNew.setCellValue(mc.codigo);
            posMC.put(mc.codigo, contador++);
            cellNew.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
        }
    	xssfRowNew = xssfSheetNew.createRow(contador); 
        cellNew = xssfRowNew.createCell(2);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("TOTAL");
        posMC.put(MetaConcepto.COD_TOTAL, contador++);
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(1, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
        
        xssfRowNew = xssfSheetNew.getRow(contadorOriginal); 
		
		int contadorCol = 3;
		for (int anio=minAnio;anio<=maxAnio; anio++) {
			for (int mes=1;mes<13;mes++) {
				
				if ((anio==minAnio && mes>=minMes) || (anio!=minAnio && anio!=maxAnio) || (anio==maxAnio && mes<=maxMes)) {
			        cellNew = xssfRowNew.createCell(contadorCol);
			        cellNew.setCellType(CellType.STRING);
			        cellNew.setCellValue(Constantes.nomMes(mes-1)+"'"+new Integer(anio).toString().substring(2,4));
			        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(3, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
			        xssfSheetNew.setColumnWidth(contadorCol,3800);
				}
				
				if ((anio==minAnio && mes>=minMes) || (anio!=minAnio)) 
						contadorCol++;
			}
		}
        
        return posSMC;
        
	}
	
	private int escribircabecera(XSSFSheet xssfSheetNew, int contador, Foto foto){
		XSSFRow xssfRowNew;
        XSSFCell cellNew; 
        
        xssfRowNew = xssfSheetNew.createRow(1); 
        cellNew = xssfRowNew.createCell(1);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Proyecto");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
        
        cellNew = xssfRowNew.createCell(2);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue(foto.proyecto.nombre);
        xssfSheetNew.setColumnWidth(1,5800);
        xssfSheetNew.setColumnWidth(2,4800);
        
        xssfRowNew = xssfSheetNew.createRow(contador++); 
        cellNew = xssfRowNew.createCell(1);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Fecha Creación");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
        
        cellNew = xssfRowNew.createCell(2);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue(foto.fxCreacion);
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(0, EstiloCelda.BLANCO, TipoDato.FORMATO_FECHA));
        
        xssfRowNew = xssfSheetNew.createRow(contador++); 
        cellNew = xssfRowNew.createCell(1);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Nombre Foto");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
        
        cellNew = xssfRowNew.createCell(2);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue(foto.nombreFoto);
        
        xssfRowNew = xssfSheetNew.createRow(contador++); 
        cellNew = xssfRowNew.createCell(1);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Estimado año Foto");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
        
        cellNew = xssfRowNew.createCell(2);
        cellNew.setCellType(CellType.NUMERIC);
        cellNew.setCellValue(foto.valorAnioCurso);
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(0, EstiloCelda.BLANCO, TipoDato.FORMATO_MONEDA));
        
        xssfRowNew = xssfSheetNew.createRow(contador++); 
        cellNew = xssfRowNew.createCell(1);
        cellNew.setCellType(CellType.STRING);
        cellNew.setCellValue("Estimado año Total");
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(4, EstiloCelda.AZUL, TipoDato.FORMATO_TXT));
        
        cellNew = xssfRowNew.createCell(2);
        cellNew.setCellType(CellType.NUMERIC);
        cellNew.setCellValue(foto.valorTotal);
        cellNew.setCellStyle(EstiloCelda.getEstiloCelda(0, EstiloCelda.BLANCO, TipoDato.FORMATO_MONEDA));
        
        return contador;
	}

}
