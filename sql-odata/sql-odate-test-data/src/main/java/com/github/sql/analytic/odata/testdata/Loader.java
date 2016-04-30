package com.github.sql.analytic.odata.testdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

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

}
