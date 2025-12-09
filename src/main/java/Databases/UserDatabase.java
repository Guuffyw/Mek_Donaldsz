package Databases;

import Models.UserProfile;

import java.sql.Connection;
import java.sql.SQLException;

public class UserDatabase {

    private static UserDatabase instance;
    private final Connection conn;

    // Private constructor for singleton
    private UserDatabase() throws SQLException {
        conn = DatabaseManager.getConnection("userdata.db");
        createTable();
    }

    // Get singleton instance
    public static UserDatabase getInstance() throws SQLException {
        if (instance == null) {
            instance = new UserDatabase();
        }
        return instance;
    }

    private void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS user_profiles (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT UNIQUE NOT NULL,
                username TEXT NOT NULL,
                balance INTEGER DEFAULT 0,
                money_spent INTEGER DEFAULT 0,
                rank TEXT DEFAULT 'Newbie',
                coupons_500 INTEGER DEFAULT 0,
                coupons_1000 INTEGER DEFAULT 0
            );
        """;
        try (var stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void createProfile(String email, String username) throws SQLException {
        String sql = """
            INSERT INTO user_profiles (email, username)
            VALUES (?, ?)
        """;
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }

    public String getUsernameByEmail(String email) throws SQLException {
        String sql = "SELECT username FROM user_profiles WHERE email = ?";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (var rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("username") : null;
            }
        }
    }

    public int getBalanceByEmail(String email) throws SQLException {
        String sql = "SELECT balance FROM user_profiles WHERE email = ?";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (var rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("balance") : 0;
            }
        }
    }

    public void updateBalance(String email, int newBalance) throws SQLException {
        String sql = "UPDATE user_profiles SET balance = ? WHERE email = ?";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newBalance);
            stmt.setString(2, email);
            stmt.executeUpdate();
        }
    }

    public String getEmailByUsername(String username) throws SQLException {
        String sql = "SELECT email FROM user_profiles WHERE username = ?";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (var rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("email") : null;
            }
        }
    }

    // Method to update balance by username directly
    public void updateBalanceByUsername(String username, int newBalance) throws SQLException {
        String sql = "UPDATE user_profiles SET balance = ? WHERE username = ?";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newBalance);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }
    // Method to update score by username (add to existing amount)
    public void updateScoreByUsername(String username, int amountToAdd) throws SQLException {
        String sql = "UPDATE user_profiles SET money_spent = money_spent + ? WHERE username = ?";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, amountToAdd);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }


    // Method to get balance by username directly
    public int getBalanceByUsername(String username) throws SQLException {
        String sql = "SELECT balance FROM user_profiles WHERE username = ?";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (var rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("balance") : 0;
            }
        }
    }

    public int getMoneySpentByUsername(String username) throws SQLException {
        String sql = "SELECT money_spent FROM user_profiles WHERE username = ?";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (var rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("money_spent") : 0;
            }
        }
    }

    public int getCoupons500ByUsername(String username) throws SQLException {
        String sql = "SELECT coupons_500 FROM user_profiles WHERE username = ?";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (var rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("coupons_500") : 0;
            }
        }
    }

    public int getCoupons1000ByUsername(String username) throws SQLException {
        String sql = "SELECT coupons_1000 FROM user_profiles WHERE username = ?";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (var rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("coupons_1000") : 0;
            }
        }
    }

    public void update500ByUsername(String username, int newBalance) throws SQLException {
        String sql = "UPDATE user_profiles SET coupons_500 = ? WHERE username = ?";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newBalance);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }

    public void update1000ByUsername(String username, int newBalance) throws SQLException {
        String sql = "UPDATE user_profiles SET coupons_1000 = ? WHERE username = ?";
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newBalance);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }
}
