package model.beans;

import java.util.ArrayList;

public class BaseCalculoConcepto {
	public int id = 0;
	public String desc = "";
	
	public static final int CALCULO_BASE_HORAS = 1;
	public static final int CALCULO_BASE_COSTE = 2;
	public static final int CALCULO_BASE_PORC = 3;

	public BaseCalculoConcepto() {}
	
	public BaseCalculoConcepto(int tipo) {
		if (tipo == BaseCalculoConcepto.CALCULO_BASE_HORAS){
			this.id = BaseCalculoConcepto.CALCULO_BASE_HORAS;
			this.desc = "En base a horas";
		}
		if (tipo == BaseCalculoConcepto.CALCULO_BASE_COSTE){
			this.id = BaseCalculoConcepto.CALCULO_BASE_COSTE;
			this.desc ="En base a un coste dado";
		}
		if (tipo == BaseCalculoConcepto.CALCULO_BASE_PORC){
			this.id = BaseCalculoConcepto.CALCULO_BASE_PORC;
			this.desc ="En base a un % sobre otro concepto";
		}
	}
	
	public static ArrayList<BaseCalculoConcepto> listado() {
		ArrayList<BaseCalculoConcepto> salida = new ArrayList<BaseCalculoConcepto>();
		
		BaseCalculoConcepto bcc = new BaseCalculoConcepto();
		bcc.id = BaseCalculoConcepto.CALCULO_BASE_HORAS;
		bcc.desc = "En base a horas";
		salida.add(bcc);
		
		bcc = new BaseCalculoConcepto();
		bcc.id = BaseCalculoConcepto.CALCULO_BASE_COSTE;
		bcc.desc = "En base a un coste dado";
		salida.add(bcc);
		
		bcc = new BaseCalculoConcepto();
		bcc.id = BaseCalculoConcepto.CALCULO_BASE_PORC;
		bcc.desc = "En base a un % sobre otro concepto";
		salida.add(bcc);
		
		return salida;
	} 
	
	public String toString(){
		return this.desc;
	}
}
