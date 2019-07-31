package model.utils.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.constantes.FormateadorDatos;
import model.interfaces.Cargable;

public class ConsultaBD {
	
	public static HashMap<String, ArrayList<String>> transacciones = null;
	public static HashMap<String, HashMap<String,Integer>> idsAcumulados = null;
	
	public static String url = null;
	Connection connect;
	
	public static void init(String urlBD) {
		url = urlBD;
		transacciones = new HashMap<String, ArrayList<String>>();
		idsAcumulados = new HashMap<String, HashMap<String,Integer>>();
	}
	
	public void connect(){
		 if (connect!=null) return;
		 try {
		     connect = DriverManager.getConnection("jdbc:sqlite:"+url);
		     if (connect!=null) {
		         ////System.out.println("Conectado");
		     }
		 }catch (SQLException ex) {
			 try {
			     //connect = DriverManager.getConnection("jdbc:sqlite:"+"C:\\Users\\EN31714\\Downloads\\eclipse-java-neon-2-win32\\workspace\\Repositorio\\GestionProyectos\\gProyectos.s3db");
				 connect = DriverManager.getConnection("jdbc:sqlite:"+"C:\\Users\\Oscar\\git\\GestionProyectos\\gProyectos.s3db");
			     if (connect!=null) {
			         ////System.out.println("Conectado");
			     }
			 }catch (SQLException ex2) {
				 ex.printStackTrace();
			     System.err.println("No se ha podido conectar a la base de datos\n"+ex2.getMessage());
			 }
		     //System.err.println("No se ha podido conectar a la base de datos\n"+ex.getMessage());
		 }
		}
	
	 public void close(){
	        try {
	            connect.close();
	        } catch (SQLException ex) {
	        	System.err.println("Error al cerrar la BD\n"+ex.getMessage());
	        }
	 }
	 
	 public void ejecutaSQL( String codQuery, List<ParametroBD> filtros, Cargable objSalida, String idTransaccion ){
		 if (idTransaccion==null) {
			 ejecutaSQL( codQuery, filtros, objSalida );
			 return;
		 }
		 
		 ArrayList<String> querys = null;
		 
		 if ( ConsultaBD.transacciones.containsKey(idTransaccion)) {
			 querys = ConsultaBD.transacciones.get(idTransaccion);
		 } else {
			 querys = new ArrayList<String>();
			 ConsultaBD.transacciones.put(idTransaccion, querys);
		 }
		 
		 String query = this.preparaQuery(QuerysBD.querys.get(codQuery), filtros, idTransaccion);
		 query = FormateadorDatos.cambiaAcutes(query,FormateadorDatos.MODO_ACUTE);
		 querys.add(query);
	 }
	 
	 public ArrayList<Cargable> ejecutaSQL( String codQuery, List<ParametroBD> filtros, Cargable objSalida ) 
	  {
	    Statement stmt = null;
	    ArrayList<Cargable> salida = null;
	    this.connect();
	   
	    try {
	      String query = this.preparaQuery(QuerysBD.querys.get(codQuery), filtros, null);
	      query = FormateadorDatos.cambiaAcutes(query,FormateadorDatos.MODO_ACUTE);
	      System.out.println(query);
	       	
	      if (ConstantesBD.QUERYCONSULTA.equals(QuerysBD.querys.get(codQuery).tipo)){
	    	  stmt = connect.createStatement();
	    	  ResultSet rs = stmt.executeQuery(query);
	    	  
	    	  salida = new ArrayList<Cargable>();
	    	  
	    	  while ( rs.next() ) {
	    		  HashMap<String, Object> listadoCampos = new HashMap<String, Object>();
	    		  
	    		  for (int col=0;col<rs.getMetaData().getColumnCount();col++){
	    			  String cabecera = rs.getMetaData().getColumnName(col+1);
	    			  Object campo = rs.getObject(cabecera);
	    			  if (campo!=null)
		    			  if ("String".equals(campo.getClass().getSimpleName())) {
		    				  campo = FormateadorDatos.cambiaAcutes((String) campo,FormateadorDatos.MODO_CASTELLANO);
		    			  }
	    			  
	    			  listadoCampos.put(cabecera, campo);
	    		  }
	    		  
	    		  objSalida = objSalida.getClass().newInstance();
	    		  salida.add(objSalida.cargar(listadoCampos));	    		  
	    	  }
	    	  
		      rs.close();
		      stmt.close();
	      }
	      
	      if (ConstantesBD.QUERYINSERT.equals(QuerysBD.querys.get(codQuery).tipo) ||
	    		  ConstantesBD.QUERYUPDATE.equals(QuerysBD.querys.get(codQuery).tipo) ||
	    		  ConstantesBD.QUERYDELETE.equals(QuerysBD.querys.get(codQuery).tipo)){
	    	  stmt = connect.createStatement();
	    	  stmt.execute(query );
	    	  stmt.close();
	      }
	    	
	      return salida;
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      return null;
	    } 
	    finally {
	    	this.close();	    	
	    }
	  }
	 
	 public ArrayList<Object> ejecutaSQL( String query, String tipoQuery) 
	  {
	    Statement stmt = null;
	    this.connect();
	   
	    try {
	       if (ConstantesBD.QUERYCONSULTA.equals(tipoQuery)){
	    	  stmt = connect.createStatement();
	    	  ResultSet rs = stmt.executeQuery(query);
	    	  
	    	  ArrayList<Object> salida = new ArrayList<Object>();
	    	  
	    	  while ( rs.next() ) {
	    		  HashMap<String, Object> listadoCampos = new HashMap<String, Object>();
	    		  
	    		  for (int col=0;col<rs.getMetaData().getColumnCount();col++){
	    			  String cabecera = rs.getMetaData().getColumnName(col+1);
	    			  Object campo = rs.getObject(cabecera);
	    			  if (campo!=null)
		    			  if ("String".equals(campo.getClass().getSimpleName())) {
		    				  campo = FormateadorDatos.cambiaAcutes((String) campo,FormateadorDatos.MODO_CASTELLANO);
		    			  }
	    			  
	    			  listadoCampos.put(cabecera, campo);
	    		  }
	    		  
	    		  salida.add(listadoCampos);	    		  
	    	  }
	    	  
		      rs.close();
		      stmt.close();
		      
		      return salida;
	      }
	      
	      if (ConstantesBD.QUERYINSERT.equals(tipoQuery) ||
	    		  ConstantesBD.QUERYUPDATE.equals(tipoQuery) ||
	    		  ConstantesBD.QUERYDELETE.equals(tipoQuery)){
	    	  stmt = connect.createStatement();
	    	  stmt.execute(query );
	    	  stmt.close();
	      }
	    	
	      return null;
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      return null;
	    } 
	    finally {
	    	this.close();	    	
	    }
	  }

	 public void ejecutaTransaccion(String idTransaccion) throws Exception {
		 ArrayList<String> querys = ConsultaBD.transacciones.get(idTransaccion);
		 Statement stmt = null;
		 
		 if (querys==null) {
			 return;
		 }
		 
		 this.connect();
		 
		 try {
			 connect.setAutoCommit(false);
			 
			 for (int i = 0; i<querys.size();i++) {
				 String query = querys.get(i);
		    	  stmt = connect.createStatement();
		    	  System.out.println(query);
		    	  stmt.execute(query );				 
			 }
			 
			 connect.commit();
			 connect.setAutoCommit(true);
			 
			 ConsultaBD.transacciones.remove(idTransaccion);
			 ConsultaBD.idsAcumulados.remove(idTransaccion);
		 } catch (Exception e) {
			 System.out.println(e);
			 connect.rollback();
			 throw e;
			 
		 } finally {
			 this.close();
		 }
	 }
	 
	 public String preparaQuery(QuerysBD qbd, List<ParametroBD> filtros, String idTransaccion) {
		 String salida = null;
		 
		 String[] cortada = qbd.query.split("\\$B\\$");
		 
		 if (cortada.length==1) return qbd.query;
		 else{
			 salida = ""+cortada[0]; 
			 for (int i=1; i<cortada.length;i++) {
				  String[] resto = cortada[i].split("\\$E\\$");
				  String[] inside = resto[0].split("\\|\\|");
				  String bloque = inside[0];
				  
				  ParametroBD pbd = null;
				  if (filtros!=null){
					  for (int j=0;j<filtros.size();j++ ){
						  pbd = filtros.get(j);
						  if (pbd.id != Integer.valueOf(inside[1]).intValue())
							  pbd = null;
						  else
							  break;
					  }
					  
					  if (pbd!=null){
						  pbd.tabla = qbd.tabla;
						  if (idTransaccion==null) {
							  bloque += pbd.toQuery();
						  } else {
							  bloque += pbd.toQuery(idTransaccion);
						  }
						  
						  
						  if (resto.length>1) bloque += resto[1];
						  
						  salida += bloque;
					  } else {
						  if (ConstantesBD.QUERYINSERT.equals(qbd.tipo)) {
							  bloque += "null";
							  if (resto.length>1) bloque += resto[1];
							  
							  salida += bloque;
						  }
					  }
				}
			 }
		 }
		 
		 return salida;
		 
	 }
	 
	 public static String getTicket() {
		 String sMethodName = new String (Thread.currentThread().getStackTrace()[2].getMethodName());
		 String sClassName  = new String (Thread.currentThread().getStackTrace()[2].getClassName());
		 return sClassName+":"+sMethodName + new Date().getTime();
	 }
	 
	 public static void ejecutaTicket(String idTransaccion) throws Exception{
		ConsultaBD cbd = new ConsultaBD(); 
		cbd.ejecutaTransaccion(idTransaccion);
	 }
	
}
