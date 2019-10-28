package model.metadatos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.constantes.Constantes;
import model.constantes.ConstantesBD;
import model.interfaces.Cargable;
import model.utils.db.ConsultaBD;
import model.utils.db.ParametroBD;

public class RelBloqueConcepto implements Cargable{
	
	public RelBloqueConcepto() {
    }
	
	public void buscaRelacion() { 			
		ConsultaBD consulta = new ConsultaBD();
		
		List<ParametroBD> listaParms = new ArrayList<ParametroBD>();
				
		consulta.ejecutaSQL("cConsultaRel_bloque_concepto", listaParms, this);
		
    }
	
	
	@Override
	public Cargable cargar(Object o) {
		@SuppressWarnings("unchecked")
		HashMap<String, Object> salida = (HashMap<String, Object>) o;
		
		try {
			try {
			 	if (salida.get("relIdconcepto")!=null)  { 
			 		MetaConcepto mc = MetaConcepto.porId((Integer)salida.get("relIdconcepto"));
			 		MetaGerencia mg = MetaGerencia.porId((Integer)salida.get("relIdbloque"));
			 		
			 		if (mc.gerencias==null) 
			 			mc.gerencias = new ArrayList<MetaGerencia> ();
			 		if (mg.metaConceptos==null) 
			 			mg.metaConceptos = new ArrayList<MetaConcepto> ();
			 		
			 		if (!mc.gerencias.contains(mg))
			 			mc.gerencias.add(mg);
			 		if (!mg.metaConceptos.contains(mc))
			 			mg.metaConceptos.add(mc);
				} 
			} catch (Exception ex) {}
			
			
		} catch (Exception e) {
			
		} 
		
		return this;
	}
	
}
