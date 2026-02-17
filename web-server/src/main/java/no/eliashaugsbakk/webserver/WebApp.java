package no.eliashaugsbakk.webserver;


import no.eliashaugsbakk.webserver.db.Jdbc.DatabaseManager;
import no.eliashaugsbakk.webserver.db.Jdbc.JdbcPageRepository;

import java.io.IOException;

class WebApp {

	public static void main(String[] args) throws Exception {
		String dbPath = System.getenv().getOrDefault("DB_PATH", "pages.db");
		DatabaseManager databaseManager = new DatabaseManager(dbPath);
		JdbcPageRepository pageRepo = new JdbcPageRepository(databaseManager.getConnection());

		databaseManager.initialize();

		try {
			Server server = new Server();
			server.start(pageRepo);
		} catch (IOException e) {
			System.err.println("Could not start server: " + e.getMessage());
		}
	}
}