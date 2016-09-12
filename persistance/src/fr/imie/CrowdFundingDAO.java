package fr.imie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@ConnetionManagement
public class CrowdFundingDAO implements ICrowdFundingDAO,ConnectionSupport {

	private @Inject ConnectionProvider connectionProvider;
	private Connection connection;
	
	@Override
	public List<CrowdFundingDTO> getCrowdFundings() throws SQLException {
		Statement statement = null;
		ResultSet resultSet = null;
		List<CrowdFundingDTO> retour = new ArrayList<CrowdFundingDTO>();

		statement = connection.createStatement();
		resultSet = statement.executeQuery("SELECT id,nom,objectif FROM projet");

		while (resultSet.next()) {
			CrowdFundingDTO crowdFundingDTO = buildDTOFromResultset(resultSet);
			retour.add(crowdFundingDTO);
		}

		return retour;
	}

	@Override
	public CrowdFundingDTO addCrowdFundingDTO(CrowdFundingDTO crowdFundingDTOToInsert) throws SQLException {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		CrowdFundingDTO retour = null;

		List<ParamJDBC> paramsList = new ArrayList<ParamJDBC>();
		if (crowdFundingDTOToInsert.getName() != null) {
			paramsList.add(new ParamJDBC("nom", crowdFundingDTOToInsert.getName()));
		}
		if (crowdFundingDTOToInsert.getGoal() != null) {
			paramsList.add(new ParamJDBC("objectif", crowdFundingDTOToInsert.getGoal()));
		}
	

		String fields = "";
		String params = "";
		Boolean firstField = true;
		for (ParamJDBC paramJDBC : paramsList) {
			fields = fields.concat(firstField ? "" : ",").concat(paramJDBC.getNom());
			params = params.concat(firstField ? "" : ",").concat("?");
			firstField = false;
		}

		String query = "INSERT into personne (".concat(fields).concat(") values (").concat(params)
				.concat(") returning id,nom,objectif");

		statement = connection.prepareStatement(query);

		Integer paraNumber = 1;
		for (ParamJDBC paramJDBC : paramsList) {
			statement.setObject(paraNumber++, paramJDBC.getValue());
		}

		resultSet = statement.executeQuery();
		resultSet.next();
		retour = buildDTOFromResultset(resultSet);

		return retour;
	}

	private CrowdFundingDTO buildDTOFromResultset(ResultSet resultSet)  throws SQLException {
		CrowdFundingDTO retour;
		retour = new CrowdFundingDTO();

		retour.setName(resultSet.getString("nom"));
		retour.setGoal(resultSet.getInt("objectif"));

		return retour;
	}

	@Override
	public void setConnection(Connection conn) {
		this.connection=conn;
		
	}

	@Override
	public Connection getConnection() {
		return this.connection;
	}

}