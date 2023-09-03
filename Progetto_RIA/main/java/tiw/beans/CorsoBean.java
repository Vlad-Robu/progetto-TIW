package tiw.beans;

public class CorsoBean{
	private int id;
	private String nome;
	private int cfu;
	private int professorId;
	
	public int getId() { return id;	}
	public void setId(int id) { this.id = id; }
	
	public String getName() { return nome; }
	public void setName(String nome) { this.nome = nome; }
	
	public int getCfu() { return cfu; }
	public void setCfu(int cfu) { this.cfu = cfu; }
	
	public int getProfessorId() { return professorId; }
	public void setProfessorId(int professorId) { this.professorId = professorId; }

}
