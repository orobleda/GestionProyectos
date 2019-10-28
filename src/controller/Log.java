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
}
