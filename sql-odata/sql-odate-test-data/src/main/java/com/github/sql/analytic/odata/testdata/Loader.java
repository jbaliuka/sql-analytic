package com.github.sql.analytic.odata.testdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.github.sql.analytic.JSQLParserException;
import com.github.sql.analytic.statement.policy.CreatePolicy;
import com.github.sql.analytic.test.TestUtil;

public class Loader {
	
	public static void execute(Connection connection) throws SQLException, IOException {

		loadFile(connection,"mywind/northwind.sql");
		loadFile(connection,"mywind/northwind-data.sql");
		
	}

	private static void loadFile(Connection connection, String url) throws IOException, SQLException {
		try(InputStream in = new Loader().getClass().getClassLoader().getResourceAsStream(url)){
			BufferedReader reader = new BufferedReader( new InputStreamReader(in));
			String line;
			while((line = reader.readLine()) != null){
				connection.createStatement().execute(line);
			}
		}
	}

	public static List<CreatePolicy> getPolicyList() throws IOException, JSQLParserException {
		
		List<String> policyList = new ArrayList<>();
		try(InputStream in = new Loader().getClass().getClassLoader().getResourceAsStream("northwind.policy")){
			BufferedReader reader = new BufferedReader( new InputStreamReader(in));
			String line;
			while((line = reader.readLine()) != null){
				policyList.add(line);
			}
		}		
		
		return TestUtil.parsePolicyList(policyList);
	}

}
