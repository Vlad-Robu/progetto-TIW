package tiw.beans;

public class StudentiAppelloBean {
	private int idstudente;
    private String matricola;
    private String cognome;
    private String nome;
    private String email;
    private String corsoDiLaurea;
    private String voto;
    private String statoValutazione;
    private AppelloBean appelloBean;
    
    public String getMatricola() {
        return matricola;
    }

    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCorsoDiLaurea() {
        return corsoDiLaurea;
    }

    public void setCorsoDiLaurea(String corsoDiLaurea) {
        this.corsoDiLaurea = corsoDiLaurea;
    }

    public String getVoto() {
        return voto;
    }

    public void setVoto(String voto) {
        this.voto = voto;
    }

    public String getStatoValutazione() {
        return statoValutazione;
    }

    public void setStatoValutazione(String statoValutazione) {
        this.statoValutazione = statoValutazione;
    }

	public int getIdstudente() {
		return idstudente;
	}

	public void setIdstudente(int idstudente) {
		this.idstudente = idstudente;
	}

	public AppelloBean getAppelloBean() {
		return appelloBean;
	}

	public void setAppelloBean(AppelloBean appelloBean) {
		this.appelloBean = appelloBean;
	}
}

