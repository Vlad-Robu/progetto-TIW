package tiw.beans;

import java.util.ArrayList;
import java.util.List;

public class VerbaleBean {
	private int idverbale;
	private int idappello;
	private String data_ora;
	private List<StudentiAppelloBean> datiStud = new ArrayList<StudentiAppelloBean>();
	
	public int getIdverbale() {
		return idverbale;
	}
	public void setIdverbale(int idverbale) {
		this.idverbale = idverbale;
	}
	public int getIdappello() {
		return idappello;
	}
	public void setIdappello(int idappello) {
		this.idappello = idappello;
	}
	public String getData_ora() {
		return data_ora;
	}
	public void setData_ora(String data_ora) {
		this.data_ora = data_ora;
	}
	public List<StudentiAppelloBean> getDatiStud() {
		return datiStud;
	}
	public void setDatiStud(List<StudentiAppelloBean> datiStud) {
		this.datiStud = datiStud;
	}
	
	
}
	
	
	
