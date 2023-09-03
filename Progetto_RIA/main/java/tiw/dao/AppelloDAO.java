package tiw.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tiw.beans.AppelloBean;
import tiw.beans.StudentiAppelloBean;
import tiw.beans.VerbaleBean;

public class AppelloDAO {
	private Connection con;
	private int idappello;

	public AppelloDAO(Connection connection, int i) {
		this.con = connection;
		this.idappello = i;
	}

	public List<AppelloBean> findAppelli(int idcorso) throws SQLException {
		List<AppelloBean> appelli = new ArrayList<AppelloBean>();
		String query = "SELECT idappello, data FROM appelli WHERE idcorso = ? ORDER BY data DESC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idcorso);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					AppelloBean appello = new AppelloBean();

					appello.setId(result.getInt("idappello"));
					String dateString = result.getString("data");
					Date date = Date.valueOf(dateString);
					appello.setDate(date);
					appelli.add(appello);
				}
			}
		}
		return appelli;
	}
	
	public List<AppelloBean> findAppelliStud(int idcorso, int idstudente) throws SQLException {
		List<AppelloBean> appelli = new ArrayList<AppelloBean>();
		String query = "SELECT appelli.idappello, appelli.data FROM appelli, dettaglioAppello "
				+ "WHERE appelli.idappello = dettaglioAppello.idappello AND dettaglioAppello.idstudente = ? AND idcorso = ? " 
				+ "ORDER BY data DESC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idstudente);
			pstatement.setInt(2, idcorso);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					AppelloBean appello = new AppelloBean();

					appello.setId(result.getInt("idappello"));
					String dateString = result.getString("data");
					Date date = Date.valueOf(dateString);
					appello.setDate(date);
					appelli.add(appello);
				}
			}
		}
		return appelli;
	}

	public boolean hasStudente(Integer selectedStudentId) throws SQLException {
		String query = "SELECT COUNT(*) FROM dettaglioAppello WHERE idstudente = ? AND idappello = ?";

		try (PreparedStatement statement = con.prepareStatement(query)) {
			statement.setInt(1, selectedStudentId);
			statement.setInt(2, idappello);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				int count = resultSet.getInt(1);
				return count == 1;
			}
		}

		return false;
	}

	// utilizzato per il sorting
	public List<StudentiAppelloBean> findStudenti(String column, String order, int selectedAppelloId)
			throws SQLException {
		List<StudentiAppelloBean> studentiAppello = new ArrayList<StudentiAppelloBean>();
		AppelloDAO aDao = new AppelloDAO(con, idappello);

		String myColumn = null;
		if (column.equals("voto") || column.equals("stato_valutazione")) {
			myColumn = "da." + column;
		} else {
			myColumn = "s." + column;
		}
		String query = "SELECT * " + "FROM studente as s " + "JOIN dettaglioAppello as da "
				+ "ON s.idstudente = da.idstudente " + "WHERE da.idappello = ? " + "ORDER BY %s %s";

		String sql = String.format(query, myColumn, order);

		try (PreparedStatement pstatement = con.prepareStatement(sql);) {
			pstatement.setInt(1, selectedAppelloId);

			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					StudentiAppelloBean studenteAppello = new StudentiAppelloBean();
					studenteAppello.setIdstudente(result.getInt("idstudente"));
					studenteAppello.setMatricola(result.getString("matricola"));
					studenteAppello.setCognome(result.getString("cognome"));
					studenteAppello.setNome(result.getString("nome"));
					studenteAppello.setEmail(result.getString("email"));
					studenteAppello.setCorsoDiLaurea(result.getString("corsoLaurea"));
					studenteAppello.setVoto(result.getString("voto"));
					studenteAppello.setStatoValutazione(result.getString("stato_valutazione"));

					studenteAppello.setAppelloBean(aDao.findNameDate());

					studentiAppello.add(studenteAppello);
				}
			}
		}
		return studentiAppello;
	}

	// trova tutti gli studenti dell'appello
	public List<StudentiAppelloBean> findStudenti() throws SQLException {
		List<StudentiAppelloBean> studentiAppello = new ArrayList<StudentiAppelloBean>();
		AppelloDAO aDao = new AppelloDAO(con, idappello);

		String query = "SELECT * " + "FROM studente, dettaglioAppello "
				+ "WHERE studente.idstudente = dettaglioAppello.idstudente AND dettaglioAppello.idappello = ? "
				+ "ORDER BY matricola ASC";

		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idappello);

			try (ResultSet result = pstatement.executeQuery();) {

				while (result.next()) {
					StudentiAppelloBean studenteAppello = new StudentiAppelloBean();
					studenteAppello.setIdstudente(result.getInt("idstudente"));
					studenteAppello.setMatricola(result.getString("matricola"));
					studenteAppello.setCognome(result.getString("cognome"));
					studenteAppello.setNome(result.getString("nome"));
					studenteAppello.setEmail(result.getString("email"));
					studenteAppello.setCorsoDiLaurea(result.getString("corsoLaurea"));
					studenteAppello.setVoto(result.getString("voto"));
					studenteAppello.setStatoValutazione(result.getString("stato_valutazione"));

					studenteAppello.setAppelloBean(aDao.findNameDate());

					studentiAppello.add(studenteAppello);

				}
			}

		}
		return studentiAppello;
	}

	// trova dati del singolo studente
	public StudentiAppelloBean findDatiStud(int idstudente) throws SQLException {
		StudentiAppelloBean datiStud = new StudentiAppelloBean();
		AppelloDAO aDao = new AppelloDAO(con, idappello);

		String query = "SELECT * " + "FROM studente, dettaglioAppello "
				+ "WHERE studente.idstudente = dettaglioAppello.idstudente AND studente.idstudente = ? AND dettaglioAppello.idappello = ? ";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idstudente);
			pstatement.setInt(2, idappello);

			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					datiStud.setIdstudente(result.getInt("idstudente"));
					datiStud.setMatricola(result.getString("matricola"));
					datiStud.setCognome(result.getString("cognome"));
					datiStud.setNome(result.getString("nome"));
					datiStud.setEmail(result.getString("email"));
					datiStud.setVoto(result.getString("voto"));
					datiStud.setCorsoDiLaurea(result.getString("corsoLaurea"));
					datiStud.setStatoValutazione(result.getString("stato_valutazione"));
					datiStud.setAppelloBean(aDao.findNameDate());
				}
			}
		}
		return datiStud;
	}

	// trova nome esame e data riferiti all'appello
	public AppelloBean findNameDate() throws SQLException {
		AppelloBean datiApp = new AppelloBean();
		String query = "SELECT nome, data " + "FROM corsi, appelli "
				+ "WHERE appelli.idappello = ? AND appelli.idcorso = corsi.idcorso ";

		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idappello);

			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {

					datiApp.setCourseName(result.getString("nome"));
					String dateString = result.getString("data");
					Date date = Date.valueOf(dateString);
					datiApp.setDate(date);

				}
			}
		}
		return datiApp;
	}

	public void UpdateVotoStud(int selectedAppelloId, int idstudente, String votoStr) throws SQLException {
		String query = null;
		String statoValutazione = null;

		try {
			con.setAutoCommit(false);

			String statoQuery = "SELECT stato_valutazione FROM dettaglioAppello WHERE idappello = ? AND idstudente = ?";
			try (PreparedStatement statoStatement = con.prepareStatement(statoQuery)) {
				statoStatement.setInt(1, selectedAppelloId);
				statoStatement.setInt(2, idstudente);
				try (ResultSet statoResult = statoStatement.executeQuery()) {
					if (statoResult.next()) {
						statoValutazione = statoResult.getString("stato_valutazione");
					}
				}
			}

			if (!"pubblicato".equals(statoValutazione) && !"verbalizzato".equals(statoValutazione)
					&& !"rifiutato".equals(statoValutazione)) {
				if (votoStr.equals(" ") || votoStr.equals("") || votoStr.equals(null)) {
					query = "UPDATE dettaglioAppello SET voto = %s, stato_valutazione = 'non inserito' WHERE idappello = ? AND idstudente = ?";
				} else {
					query = "UPDATE dettaglioAppello SET voto = %s, stato_valutazione = 'inserito' WHERE idappello = ? AND idstudente = ?";
				}
			} else {
				throw new SQLException(
						"Update is not allowed for entries with stato di valutazione: pubblicato, verbalizzato, rifiutato ");
			}

			String voto = "'" + votoStr + "'";
			String sql = String.format(query, voto);

			try (PreparedStatement pstatement = con.prepareStatement(sql);) {
				pstatement.setInt(1, selectedAppelloId);
				pstatement.setInt(2, idstudente);
				pstatement.executeUpdate();
			}

			con.commit();

		} catch (SQLException e) {
			con.rollback();
			throw e;
		} finally {
			con.setAutoCommit(true);
		}
	}

	public void PubblicaVoti(int selectedAppelloId) throws SQLException {

		try {
			con.setAutoCommit(false);

			String checkQuery = "SELECT COUNT(*) FROM dettaglioAppello WHERE idappello = ? AND stato_valutazione = 'inserito'";

			try (PreparedStatement checkStatement = con.prepareStatement(checkQuery)) {
				checkStatement.setInt(1, selectedAppelloId);
				ResultSet resultSet = checkStatement.executeQuery();

				if (resultSet.next()) {
					int count = resultSet.getInt(1);
					if (count == 0) {
						throw new SQLException("No entries with stato di valutazione: inserito");
					}
				} else {
					throw new SQLException("Error while checking stato_valutazione");
				}
			}

			String query = "UPDATE dettaglioAppello SET stato_valutazione = 'pubblicato' WHERE idappello = ? AND stato_valutazione = 'inserito'";

			try (PreparedStatement pstatement = con.prepareStatement(query);) {
				pstatement.setInt(1, selectedAppelloId);
				pstatement.executeUpdate();
			}

			con.commit();

		} catch (SQLException e) {
			con.rollback();
			throw e;
		} finally {
			con.setAutoCommit(true);
		}

	}

	public VerbaleBean VerbalizzaVoti(int selectedAppelloId) throws SQLException {

		VerbaleBean verbale = new VerbaleBean();

		try {
			con.setAutoCommit(false);

			String checkQuery = "SELECT COUNT(*) FROM dettaglioAppello WHERE idappello = ? AND stato_valutazione IN ('pubblicato', 'rifiutato')";

			try (PreparedStatement checkStatement = con.prepareStatement(checkQuery)) {
				checkStatement.setInt(1, selectedAppelloId);
				ResultSet resultSet = checkStatement.executeQuery();

				if (resultSet.next()) {
					int count = resultSet.getInt(1);
					if (count == 0) {
						throw new SQLException("No entries with stato di valutazione: pubblicato, rifiutato");
					}
				} else {
					throw new SQLException("Error while checking stato di valutazione");
				}
			}

			String rimandatoQuery = "UPDATE dettaglioAppello SET voto = 'rimandato' WHERE idappello = ? AND stato_valutazione = 'rifiutato' ";

			try (PreparedStatement updateStatement = con.prepareStatement(rimandatoQuery)) {
				updateStatement.setInt(1, selectedAppelloId);
				updateStatement.executeUpdate();
			}
			
			String saveQuery = "SELECT * " + "FROM studente, dettaglioAppello "
					+ "WHERE studente.idstudente = dettaglioAppello.idstudente AND dettaglioAppello.idappello = ? AND dettaglioAppello.stato_valutazione IN ('pubblicato', 'rifiutato')"
					+ "ORDER BY matricola ASC";

			try (PreparedStatement pstatement = con.prepareStatement(saveQuery);) {
				pstatement.setInt(1, idappello);

				try (ResultSet result = pstatement.executeQuery();) {

					while (result.next()) {
						StudentiAppelloBean studenteAppello = new StudentiAppelloBean();
						studenteAppello.setIdstudente(result.getInt("idstudente"));
						studenteAppello.setMatricola(result.getString("matricola"));
						studenteAppello.setCognome(result.getString("cognome"));
						studenteAppello.setNome(result.getString("nome"));
						studenteAppello.setEmail(result.getString("email"));
						studenteAppello.setCorsoDiLaurea(result.getString("corsoLaurea"));
						studenteAppello.setVoto(result.getString("voto"));
						verbale.getDatiStud().add(studenteAppello);

					}
				}
			}

			String updateQuery = "UPDATE dettaglioAppello SET stato_valutazione = 'verbalizzato' WHERE idappello = ? AND stato_valutazione IN ('pubblicato', 'rifiutato')";

			try (PreparedStatement updateStatement = con.prepareStatement(updateQuery)) {
				updateStatement.setInt(1, selectedAppelloId);
				updateStatement.executeUpdate();
			}

			String newQuery = "INSERT INTO verbale (idappello) VALUES (?)";

			try (PreparedStatement updateStatement = con.prepareStatement(newQuery)) {
				updateStatement.setInt(1, selectedAppelloId);

				updateStatement.executeUpdate();
			}

			String sql = "SELECT * FROM verbale WHERE idverbale = (SELECT MAX(idverbale) FROM verbale WHERE idappello = ?)";

			try (PreparedStatement pstatement = con.prepareStatement(sql)) {
			    pstatement.setInt(1, selectedAppelloId);

			    try (ResultSet result = pstatement.executeQuery()) {
			        if (result.next()) {
			            verbale.setIdverbale(result.getInt("idverbale"));
			            verbale.setIdappello(result.getInt("idappello"));
			            String date = result.getString("data_ora");
			            verbale.setData_ora(date);
			        }
			    }
			}

			con.commit();

		} catch (SQLException e) {
			con.rollback();
			throw e;
		} finally {
			con.setAutoCommit(true);
		}

		return verbale;
	}

	public void rifiutaVoto(int idstudente) throws SQLException {
		String selectQuery = "SELECT stato_valutazione, voto FROM dettaglioAppello WHERE idstudente = ? AND idappello = ?";
		String updateQuery = "UPDATE dettaglioAppello SET stato_valutazione = 'rifiutato' WHERE idstudente = ? AND idappello = ?";

		try (PreparedStatement selectStatement = con.prepareStatement(selectQuery);
				PreparedStatement updateStatement = con.prepareStatement(updateQuery)) {

			selectStatement.setInt(1, idstudente);
			selectStatement.setInt(2, idappello);
			ResultSet resultSet = selectStatement.executeQuery();

			if (resultSet.next()) {
				String statoValutazione = resultSet.getString("stato_valutazione");
				String voto = resultSet.getString("voto");

				if ("pubblicato".equals(statoValutazione) && isVotoInRange(voto)) {
					updateStatement.setInt(1, idstudente);
					updateStatement.setInt(2, idappello);
					updateStatement.executeUpdate();
				} else {
					throw new SQLException("Cannot reject vote.");
				}
			} else {
				throw new SQLException("No entry found.");
			}
		}
	}

	private boolean isVotoInRange(String voto) {
		String[] validVotes = { "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
				"30 e lode" };

		for (String validVoto : validVotes) {
			if (validVoto.equals(voto)) {
				return true;
			}
		}
		return false;
	}

	public void inserimentoMultiplo(Map<String, String> studentDataMap) throws SQLException {
	    try {
	        con.setAutoCommit(false);

	        for (Map.Entry<String, String> entry : studentDataMap.entrySet()) {
	            int studentId = Integer.parseInt(entry.getKey());
	            String voto = entry.getValue();

	            // Call the UpdateVotoStud method to update the database for each student
	            UpdateVotoStudMultiplo(idappello, studentId, voto);
	        }

	        con.commit();
	    } catch (SQLException e) {
	        con.rollback();
	        throw e;
	    } finally {
	        con.setAutoCommit(true);
	    }
	}

	public void UpdateVotoStudMultiplo(int selectedAppelloId, int idstudente, String votoStr) throws SQLException {
		String query = null;
		String statoValutazione = null;

		String statoQuery = "SELECT stato_valutazione FROM dettaglioAppello WHERE idappello = ? AND idstudente = ?";
		try (PreparedStatement statoStatement = con.prepareStatement(statoQuery)) {
			statoStatement.setInt(1, selectedAppelloId);
			statoStatement.setInt(2, idstudente);
			try (ResultSet statoResult = statoStatement.executeQuery()) {
				if (statoResult.next()) {
					statoValutazione = statoResult.getString("stato_valutazione");
				}
			}
		}

		if (!"pubblicato".equals(statoValutazione) && !"verbalizzato".equals(statoValutazione)
				&& !"rifiutato".equals(statoValutazione)) {
			if (votoStr.equals(" ")) {
				query = "UPDATE dettaglioAppello SET voto = %s, stato_valutazione = 'non inserito' WHERE idappello = ? AND idstudente = ?";
			} else {
				query = "UPDATE dettaglioAppello SET voto = %s, stato_valutazione = 'inserito' WHERE idappello = ? AND idstudente = ?";
			}
		} else {
			throw new SQLException(
					"Update is not allowed for entries with stato di valutazione: pubblicato, verbalizzato, rifiutato ");
		}

		String voto = "'" + votoStr + "'";
		String sql = String.format(query, voto);

		try (PreparedStatement pstatement = con.prepareStatement(sql);) {
			pstatement.setInt(1, selectedAppelloId);
			pstatement.setInt(2, idstudente);
			pstatement.executeUpdate();
		}

	}

}
