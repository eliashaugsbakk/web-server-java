package no.eliashaugsbakk.webserver.db;

import java.sql.SQLException;

public interface TokenRepository {
    boolean addToken(String token) throws SQLException;
    boolean removeToken(String token) throws SQLException;
    boolean isValid(String token) throws SQLException;
}
