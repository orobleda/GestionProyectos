package model.beans;

import java.util.HashMap;
import java.util.Iterator;

import model.interfaces.Cargable;

public class GenericoConsulta implements Cargable {
	
	HashMap<String,Object> elementos = null;

	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		elementos = new HashMap<String,Object>();
		
		try {
			Iterator<String> itElementos = salida.keySet().iterator();
			
			while (itElementos.hasNext()) {
				String key = itElementos.next();
				Object elemento = salida.get(key);
				elementos.put(key, elemento);
			}
			
		} catch (Exception e) {
			
		}
		
		return this;
	}

}
