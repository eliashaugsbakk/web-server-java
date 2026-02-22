package no.eliashaugsbakk.webserver;


import no.eliashaugsbakk.webserver.db.Jdbc.DatabaseManager;
import no.eliashaugsbakk.webserver.db.Jdbc.JdbcPageRepository;
import no.eliashaugsbakk.webserver.db.Jdbc.JdbcTokenRepository;
import no.eliashaugsbakk.webserver.db.PageRepository;
import no.eliashaugsbakk.webserver.db.TokenRepository;

import java.io.IOException;

class WebApp {

	public static void main(String[] args) throws Exception {
		String dbPath = System.getenv().getOrDefault("DB_PATH", "pages.db");
		DatabaseManager databaseManager = new DatabaseManager(dbPath);
		PageRepository pageRepo = new JdbcPageRepository(databaseManager);
		TokenRepository tokenRepo = new JdbcTokenRepository(databaseManager);

		databaseManager.initialize();

		try {
			Server server = new Server();
			server.start(pageRepo, tokenRepo);
		} catch (IOException e) {
			System.err.println("Could not start server: " + e.getMessage());
		}
	}
}