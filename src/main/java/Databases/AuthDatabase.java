package Databases;

import java.sql.Connection;
import java.sql.SQLException;

public class AuthDatabase {

    private final Connection conn;

    public AuthDatabase() throws SQLException {
        conn = DatabaseManager.getConnection("auth.db");
        createTable();
    }

    private void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS login_users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL
            );
        """;

        // Use try-with-resources to ensure statement is closed
        try (var stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public boolean validateLogin(String email, String passwordHash) throws SQLException {
        String sql = "SELECT 1 FROM login_users WHERE email = ? AND password_hash = ?";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, passwordHash);
            try (var rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM login_users WHERE email = ?";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (var rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void registerUser(String email, String passwordHash) throws SQLException {
        String sql = "INSERT INTO login_users (email, password_hash) VALUES (?, ?)";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, passwordHash);
            stmt.executeUpdate();
        }
    }
}

