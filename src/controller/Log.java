package controller;

import org.apache.log4j.Logger;

public class Log {
	 private static Logger log;
	 
	 public Log(String clase) {
		 log = Logger.getLogger(clase);
	 }
	 
	 public static void t(String texto) {
		 log.debug(texto);
		 System.out.println(texto);
	 }
	 
	 public static void e(String texto) {
		 log.debug(texto);
		 System.out.println(texto);
	 }
	 
	 public static void e(String texto, Exception e) {
		 log.debug(texto);
		 System.out.println(texto);
		 Log.e(e);
	 }
	 
	 public static void e(Exception ex) {
		 
		 log.debug("Excepción: " + ex.toString());
		 System.out.println("Excepción: " + ex.toString());
		 
		 for (int i=0;i<ex.getStackTrace().length;i++) {
			 StackTraceElement ste = ex.getStackTrace()[i];
			 String espacio = "";
			 if (i!=0) {
				 espacio = "\t";
			 }
			 log.debug(espacio + ste.toString());
			 System.out.println(espacio + ste.toString());
		 }		 
		  
	 }
}
