package Databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

public class DatabaseManager {

    private static final HashMap<String, Connection> connections = new HashMap<>();

    public static synchronized Connection getConnection(String fileName) throws SQLException {
        // Check if connection exists and is still valid
        if (connections.containsKey(fileName)) {
            Connection conn = connections.get(fileName);
            try {
                if (!conn.isClosed()) {
                    return conn;
                }
            } catch (SQLException e) {
                // Connection is invalid, remove it
                connections.remove(fileName);
            }
        }

        String url = "jdbc:sqlite:src/main/resources/" + fileName;
        Connection conn = DriverManager.getConnection(url);

        // SQLite optimizations to prevent locking
        try (var stmt = conn.createStatement()) {
            // Enable WAL mode (allows concurrent reads/writes)
            stmt.execute("PRAGMA journal_mode=WAL;");

            // Increase busy timeout to 30 seconds
            stmt.execute("PRAGMA busy_timeout=30000;");

            // Optimize for speed
            stmt.execute("PRAGMA synchronous=NORMAL;");
            stmt.execute("PRAGMA cache_size=10000;");
            stmt.execute("PRAGMA temp_store=MEMORY;");
        }

        connections.put(fileName, conn);
        return conn;
    }

    public static synchronized void closeAll() {
        for (Connection conn : connections.values()) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (Exception ignored) {}
        }
        connections.clear();
    }
}

