package model.utils.pdf;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;

import javafx.application.Application;
import javafx.stage.Stage;

public class ProcesaCertificacion {
	PDFParser parser;
	String parsedText;
	PDFTextStripper pdfStripper;
	PDDocument pdDoc;
	COSDocument cosDoc;
	PDDocumentInformation pdDocInfo;
	HashMap<String,String> Ers = null;
	
	public static final String CERTIFICACION_DESCRIPCION = "desc";
	public static final String CERTIFICACION_IMPORTE = "horas";
	public static final String CERTIFICACION_HORAS = "importe";

		public static final String HITOS = "hitos";
	public static final String FECHA_CONSULTA = "fechaInicio";
	public static final String SOLICITUD = "numSolicitud";
	public static final String DESCRIPCION = "descripcion";
	public static final String PROYECTO = "proyecto";
	public static final String ESTADO = "estado";
	public static final String TIPO = "tipo";
	public static final String FECHA_CERTIFICACION = "fechaCertificacion";
	public static final String PEDIDO = "pedido";
	public static final String PROVEEDOR = "proveedor";
	public static final String CREADOR = "creador";
	
	
	public ProcesaCertificacion () {
		Ers = new HashMap<String,String>();
		Ers.put(ProcesaCertificacion.FECHA_CONSULTA,"([0-9]{1,2}/[0-9]{1,2}/[0-9]{2,4})");
		Ers.put(ProcesaCertificacion.SOLICITUD," Solicitud n∫([0-9]+): Detalles");
		Ers.put(ProcesaCertificacion.DESCRIPCION,"\r\nDescripciÛn:([a-zA-Z0-9Ò—·ÈÌÛ˙¡…Õ”⁄‹¸ -_]*)\r\nProyecto");
		Ers.put(ProcesaCertificacion.PROYECTO,"\r\nProyecto:([a-zA-Z0-9Ò—·ÈÌÛ˙¡…Õ”⁄‹¸ -_]*) Responsable");
		Ers.put(ProcesaCertificacion.ESTADO,"Estado de solicitud:([a-zA-ZÒ—·ÈÌÛ˙¡…Õ”⁄‹¸ ]*)\r\nN∫:");
		Ers.put(ProcesaCertificacion.TIPO," Tipo: ([a-zA-ZÒ—·ÈÌÛ˙¡…Õ”⁄‹¸ ]*)\r\nCreado por:");
		Ers.put(ProcesaCertificacion.FECHA_CERTIFICACION,"Fecha CertificaciÛn: ([a-zA-Z0-9 ]*)\r\nDetalles ");
		Ers.put(ProcesaCertificacion.PEDIDO,"N˙mero de Pedido:  ([0-9 ]*) Proveedor: ");
		Ers.put(ProcesaCertificacion.PROVEEDOR," Proveedor:  ([a-zA-Z0-9Ò—·ÈÌÛ˙¡…Õ”⁄‹¸ -_]*)\r\nNaturaleza coste: ");
		Ers.put(ProcesaCertificacion.CREADOR,"Creado por:  ([a-zA-Z0-9Ò—·ÈÌÛ˙¡…Õ”⁄‹¸ -_]*) Creado el");
		Ers.put(ProcesaCertificacion.HITOS," Hito Cantidad Importe    \r\n([a-zA-Z0-9Ò—·ÈÌÛ˙¡…Õ”⁄‹¸ -_\r\n\t,]*)\r\nTotal:");
	}
	
	public HashMap<String,Object> procesa(String ruta) {
		try {			
			String pdfToText = pdftoText(ruta);
			HashMap<String,Object> salida = new HashMap<String,Object>();
			
			Iterator<String> itPatrones = this.Ers.keySet().iterator();
			while (itPatrones.hasNext()) {
				String codigo = itPatrones.next();
				
				String resultado = aplicarER(codigo, pdfToText);
				
				if (ProcesaCertificacion.HITOS.equals(codigo)) {
					HashMap<String, HashMap<String,String>> certificaciones =  certificaciones(resultado);
					salida.put(codigo, certificaciones);
				} else {
					salida.put(codigo, resultado);
				}
			}
			
			return salida;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String aplicarER(String codigo, String texto) throws Exception{
		String er = this.Ers.get(codigo);
		
		Pattern p = Pattern.compile(er);
	    Matcher m = p.matcher(texto);
	    boolean resultado = m.find();

	    if (!resultado || m.groupCount()>1) throw new Exception("Campo no encontrado: " + codigo);
	    
	    return m.group(1);
	}
	
	public HashMap<String, HashMap<String,String>> certificaciones(String certificaciones) {
		HashMap<String, HashMap<String,String>> listaCerts = new HashMap<String, HashMap<String,String>>();
		
		String[] certis = certificaciones.split("\r\n");
		
		int i = 0;
		while (i<certis.length){
			String [] datosCerti = certis[i].split(" ");
			String importe = datosCerti[datosCerti.length-1];
			String horas = datosCerti[datosCerti.length-2];
			String descripcion = certis[i].replace(horas, "");
			descripcion = descripcion.replace(importe, "");
			descripcion = descripcion.trim();
			
			HashMap<String,String> certificacion = new HashMap<String,String>();
			certificacion.put(ProcesaCertificacion.CERTIFICACION_DESCRIPCION, descripcion);
			certificacion.put(ProcesaCertificacion.CERTIFICACION_HORAS, horas);
			certificacion.put(ProcesaCertificacion.CERTIFICACION_IMPORTE, importe);
			listaCerts.put(descripcion, certificacion);
			i++;
		}
		
		return listaCerts;
	}

	public String pdftoText(String fileName) {

		File f = new File(fileName);

		if (!f.isFile()) {
			System.out.println("File " + fileName + " does not exist.");
			return null;
		}

		try {
			parser = new PDFParser(new RandomAccessBufferedFileInputStream(f));
		} catch (Exception e) {
			System.out.println("Unable to open PDF Parser.");
			return null;
		}

		try {
			parser.parse();
			cosDoc = parser.getDocument();
			pdfStripper = new PDFTextStripper();
			pdDoc = new PDDocument(cosDoc);
			parsedText = pdfStripper.getText(pdDoc);
		} catch (Exception e) {
			System.out.println("An exception occured in parsing the PDF Document.");
			e.printStackTrace();
			try {
				if (cosDoc != null)
					cosDoc.close();
				if (pdDoc != null)
					pdDoc.close();
			} catch (Exception e1) {
				e.printStackTrace();
			}
			return null;
		}
		System.out.println("-> " + parsedText);
		return parsedText;
	}
}
