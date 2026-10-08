package database;

import users.Employer;
import users.JobSeeker;
import users.User;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DB_FOLDER = "data";
    private static final String DB_FILE = "data/jobMatch.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;

    public static void main(String[] args) {
        new File(DB_FOLDER).mkdirs();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                System.out.println("Connected to SQLite at " + DB_URL);
                createTables(conn);
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "first_name TEXT NOT NULL, " +
                "last_name TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL, " +
                "company_name TEXT, " +
                "created_at TEXT DEFAULT CURRENT_TIMESTAMP)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE users ADD COLUMN company_name TEXT");
        } catch (SQLException e) {}

        String rememberSql = "CREATE TABLE IF NOT EXISTS remembered (" +
                "role TEXT PRIMARY KEY, " +
                "email TEXT NOT NULL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(rememberSql);
        }
    }

    public static void dropTables(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS users");
        }
    }

    public static boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
            return false;
        }
    }

    public static boolean registerUser(String firstName, String lastName, String email, String password, String role, String companyName) {
        String sql = "INSERT INTO users (first_name, last_name, email, password, role, company_name) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email.toLowerCase());
            ps.setString(4, password);
            ps.setString(5, role);
            ps.setString(6, companyName);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
            return false;
        }
    }

    public static User loginUser(String email, String password) {
        String sql = "SELECT first_name, last_name, email, password, role, company_name FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String savedPassword = rs.getString("password");

                    if (savedPassword.equals(password)) {
                        return makeUser(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
        return null;
    }

    private static User makeUser(ResultSet rs) throws SQLException {
        String first = rs.getString("first_name");
        String last = rs.getString("last_name");
        String mail = rs.getString("email");
        String company = rs.getString("company_name");

        if (rs.getString("role").equals("Employer")) {
            return new Employer(first, last, mail, company);
        }
        return new JobSeeker(first, last, mail);
    }

    public static void rememberUser(String role, String email) {
        String sql = "INSERT OR REPLACE INTO remembered (role, email) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setString(2, email.toLowerCase());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }

    public static User getRememberedUser(String role) {
        String sql = "SELECT u.first_name, u.last_name, u.email, u.password, u.role, u.company_name " +
                "FROM remembered r JOIN users u ON u.email = r.email WHERE r.role = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return makeUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
        return null;
    }

    public static void forgetUser(String role) {
        String sql = "DELETE FROM remembered WHERE role = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }

    public static boolean resetPassword(String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, email.toLowerCase());

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
            return false;
        }
    }

} //pinaka end