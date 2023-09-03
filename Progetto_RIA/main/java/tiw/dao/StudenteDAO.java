package tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tiw.beans.CorsoBean;

public class StudenteDAO {
	private Connection con;
	private int id;

	public StudenteDAO(Connection connection, int i) {
		this.con = connection;
		this.id = i;
	}

	public List<CorsoBean> findCorsi() throws SQLException {
		List<CorsoBean> corsi = new ArrayList<CorsoBean>();
		String query = "SELECT * " + "FROM corsiStudente, corsi "
				+ "WHERE corsiStudente.idstudente = ? AND corsi.idcorso = corsiStudente.idcorso "
				+ "ORDER by nome DESC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, id);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					CorsoBean corso = new CorsoBean();
					corso.setId(result.getInt("idcorso"));
					corso.setName(result.getString("nome"));
					corsi.add(corso);
				}
			}
		}
		return corsi;
	}

	public boolean hasCorso(int idcorso) throws SQLException {

		String query = "SELECT COUNT(*) FROM corsiStudente WHERE idcorso = ? AND idstudente = ?";

		try (PreparedStatement statement = con.prepareStatement(query)) {
			statement.setInt(1, idcorso);
			statement.setInt(2, id);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				int count = resultSet.getInt(1);
				return count == 1;
			}
		}

		return false;
	}

}
