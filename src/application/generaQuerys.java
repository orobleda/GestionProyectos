package application;

import java.util.HashMap;
import java.util.Iterator;

public class generaQuerys {
	public static void main(String[] args) {
		String estructura = "CREATE TABLE [PROVEEDOR]\r\n" + 
				"(\r\n" + 
				"	[id] integer NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,\r\n" + 
				"	[descripcion] text,\r\n" + 
				"	[nombreCorto] text";
		
		String [] lineasAux = estructura.split("\\(");
		String tabla = lineasAux[0].replaceAll("CREATE TABLE \\[", "").replaceAll("\\]", "").trim();
		
		String [] lineas = estructura.split("\\[");
		
		HashMap<String, String> campos = new HashMap<String, String>();
		
		for (int i=1;i<lineas.length;i++) {
			if (!lineas[i].equals("")) {
				String [] campo = lineas[i].split("\\]");
				String tipo = campo[1].trim().split(" ")[0];
				if ("text".equals(tipo)) tipo = "String";
				if ("integer".equals(tipo)) tipo = "int";
				if ("real".equals(tipo)) tipo = "float";
				if (!campo[0].equals(tabla))
					campos.put(campo[0],tipo);
			}
		}
		
		Iterator<String> valores = campos.keySet().iterator();
		
		System.out.println("<acceso>");
		System.out.print("	<query>select ");
		while (valores.hasNext()) {
			String valor = valores.next();
			System.out.print(valor + " " + tabla.substring(0, 3).toLowerCase() + valor.toLowerCase().substring(0, 1).toUpperCase() + valor.toLowerCase().substring(1, valor.length()) );
			
			if (valores.hasNext()) {
				System.out.print(" ,");
			}
		}
		System.out.println("	from  " + tabla + " where 1=1 $B$and id=||1||$E$</query>");
		System.out.println("	<id tipo=\"C\">cConsulta" + tabla.toLowerCase().substring(0, 1).toUpperCase() + tabla.toLowerCase().substring(1, tabla.length())+ "</id>");
		System.out.println("	<tabla>" + tabla + "</tabla>");
		System.out.println("	<parametros>");

		System.out.println("		<parametro id=\"1\" tipo=\"int\"></parametro>");
			
		System.out.println("	</parametros>");
		System.out.println("</acceso>");

		System.out.println("<acceso>");
		System.out.print("	<query>insert into "+tabla+" (");
		valores = campos.keySet().iterator();
		while (valores.hasNext()) {
			String valor = valores.next();
			System.out.print(valor);
			
			if (valores.hasNext()) {
				System.out.print(" ,");
			}
		}
		System.out.print(") values (");
		valores = campos.keySet().iterator();
		int contador = 1;
		while (valores.hasNext()) {
			String valor = valores.next();
			System.out.print("$B$||" + contador++ + "||$E$");
			
			if (valores.hasNext()) {
				System.out.print(" ,");
			}
		}
		System.out.println(")</query>");
		System.out.println("	<id tipo=\"I\">iInserta" + tabla.toLowerCase().substring(0, 1).toUpperCase() + tabla.toLowerCase().substring(1, tabla.length())+ "</id>");
		System.out.println("	<tabla>" + tabla + "</tabla>");
		System.out.println("	<parametros>");
		
		valores = campos.keySet().iterator();
		contador = 1;
		while (valores.hasNext()) {
			String valor = valores.next();
			System.out.println("		<parametro id=\"" + contador++ + "\" tipo=\"" + campos.get(valor).trim() + "\"></parametro>");
		}
		System.out.println("	</parametros>");
		System.out.println("</acceso>");

		System.out.println("<acceso>");
		System.out.print("	<query>delete from "+tabla+" where 1=1 $B$and id=||1||$E$");
		System.out.println("</query>");
		System.out.println("	<id tipo=\"D\">dBorra" + tabla.toLowerCase().substring(0, 1).toUpperCase() + tabla.toLowerCase().substring(1, tabla.length())+ "</id>");
		System.out.println("	<tabla>" + tabla + "</tabla>");
		System.out.println("	<parametros>");
		
		valores = campos.keySet().iterator();
		contador = 1;
		while (valores.hasNext()) {
			String valor = valores.next();
			System.out.println("		<parametro id=\"" + contador++ + "\" tipo=\"" + campos.get(valor).trim() + "\"></parametro>");
		}
		System.out.println("	</parametros>");
		System.out.println("</acceso>");

		System.out.println("<acceso>");
		System.out.print("	<query>UPDATE "+tabla+" SET ");

		valores = campos.keySet().iterator();
		contador = 2;
		while (valores.hasNext()) {
			String valor = valores.next();
			System.out.print(valor+ "= $B$||" + contador++ + "||$E$ ");
			
			if (valores.hasNext()) {
				System.out.print(", ");
			}
		}
		System.out.println("where id = $B$||1||$E$</query>");
		System.out.println("	<id tipo=\"U\">uActualiza" + tabla.toLowerCase().substring(0, 1).toUpperCase() + tabla.toLowerCase().substring(1, tabla.length())+ "</id>");
		System.out.println("	<tabla>" + tabla + "</tabla>");
		System.out.println("	<parametros>");
		
		System.out.println("		<parametro id=\"1\" tipo=\"int\"></parametro>");
		

		valores = campos.keySet().iterator();
		contador = 1;
		while (valores.hasNext()) {
			String valor = valores.next();
			System.out.println("		<parametro id=\"" + contador++ + "\" tipo=\"" + campos.get(valor).trim() + "\"></parametro>");
		}
		
		System.out.println("	</parametros>");
		System.out.println("</acceso>");

		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		
		System.out.println("=============================     CARGA BEAN    =============================================");
		
		valores = campos.keySet().iterator();
		
		while (valores.hasNext()) {
			String valor = valores.next();
			System.out.println("try {");
			System.out.println(" 	if (salida.get(\""+tabla.substring(0, 3).toLowerCase() + valor.toLowerCase().substring(0, 1).toUpperCase() + valor.toLowerCase().substring(1, valor.length())+"\")==null)  { ");
			System.out.println(" 		this."+valor+" = null;");
			System.out.println("	} else {");
			System.out.println(" 		this."+valor+" = (" + campos.get(valor) + ") salida.get(\""+tabla.substring(0, 3).toLowerCase() + valor.toLowerCase().substring(0, 1).toUpperCase() + valor.toLowerCase().substring(1, valor.length())+"\");");
			System.out.println("	}");
			System.out.println("} catch (Exception ex) {}");
						
		}
		
		System.out.println("=============================     CAMPOS BEAN    =============================================");
		
		valores = campos.keySet().iterator();
		contador = 1;
		while (valores.hasNext()) {
			String valor = valores.next();
			System.out.println("public "+campos.get(valor).trim() + " " + valor+";");
		}
		
	}
}
