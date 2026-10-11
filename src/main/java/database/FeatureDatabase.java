
package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class FeatureDatabase {

    private static final String DB_URL =
            "jdbc:sqlite:data/jobMatch.db";

    private FeatureDatabase() {
        // Prevents creating unnecessary instances.
    }

    // Connects to the existing JobMatch database.
    public static Connection connect() throws SQLException {
        Connection connection =
                DriverManager.getConnection(DB_URL);

        try (Statement statement =
                     connection.createStatement()) {

            statement.execute("PRAGMA foreign_keys = ON");

        } catch (SQLException e) {
            connection.close();
            throw e;
        }

        return connection;
    }

    // Creates tables if they do not already exist.
    public static void initialize() {

        File folder = new File("data");

        if (!folder.exists() && !folder.mkdirs()) {
            throw new IllegalStateException(
                    "Cannot create data folder.");
        }

        try (Connection connection = connect();
             Statement statement =
                     connection.createStatement()) {

            // Job posts used by applicant management.
            statement.execute("""
                CREATE TABLE IF NOT EXISTS jm_job_posts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    employer_email TEXT NOT NULL,
                    title TEXT NOT NULL,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP
                )
                """);

            // Applications submitted by job seekers.
            statement.execute("""
                CREATE TABLE IF NOT EXISTS jm_applications (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    job_id INTEGER NOT NULL,
                    applicant_email TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'Submitted',
                    applied_at TEXT DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE(job_id, applicant_email),
                    FOREIGN KEY(job_id)
                        REFERENCES jm_job_posts(id)
                )
                """);

            // Additional information about applicants.
            statement.execute("""
                CREATE TABLE IF NOT EXISTS jm_applicant_profiles (
                    applicant_email TEXT PRIMARY KEY,
                    professional_title TEXT DEFAULT '',
                    location TEXT DEFAULT '',
                    skills TEXT DEFAULT '',
                    education TEXT DEFAULT '',
                    experience TEXT DEFAULT '',
                    about TEXT DEFAULT ''
                )
                """);

            // Messages between employers and applicants.
            statement.execute("""
                CREATE TABLE IF NOT EXISTS jm_messages (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    sender_email TEXT NOT NULL,
                    recipient_email TEXT NOT NULL,
                    body TEXT NOT NULL,
                    sent_at TEXT DEFAULT CURRENT_TIMESTAMP
                )
                """);

            // Notifications for both account types.
            statement.execute("""
                CREATE TABLE IF NOT EXISTS jm_notifications (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    recipient_email TEXT NOT NULL,
                    title TEXT NOT NULL,
                    body TEXT NOT NULL,
                    is_read INTEGER NOT NULL DEFAULT 0,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP
                )
                """);

            System.out.println(
                    "JobMatch feature tables ready.");

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Database setup failed.", e);
        }
    }
}
