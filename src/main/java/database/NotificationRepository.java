
package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class NotificationRepository {

    private NotificationRepository() {}

    // Represents one notification.
    public static final class Notification {

        public final long id;
        public final String title;
        public final String body;
        public final String createdAt;
        public final boolean isRead;

        public Notification(ResultSet rs) throws SQLException {
            id = rs.getLong("id");
            title = rs.getString("title");
            body = rs.getString("body");
            createdAt = rs.getString("created_at");
            isRead = rs.getInt("is_read") == 1;
        }
    }

    // Get notifications for the logged-in user.
    public static List<Notification> getNotifications(
            String email) throws SQLException {

        List<Notification> notifications =
            new ArrayList<>();

        String sql = """
            SELECT id, title, body, is_read, created_at
            FROM jm_notifications
            WHERE LOWER(recipient_email) = LOWER(?)
            ORDER BY id DESC
            """;

        try (Connection conn = FeatureDatabase.connect();
             PreparedStatement ps =
                 conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    notifications.add(
                        new Notification(rs)
                    );
                }
            }
        }

        return notifications;
    }

    // Mark one notification as read.
    public static boolean markAsRead(
            long id, String email) throws SQLException {

        String sql = """
            UPDATE jm_notifications
            SET is_read = 1
            WHERE id = ?
            AND LOWER(recipient_email) = LOWER(?)
            """;

        try (Connection conn = FeatureDatabase.connect();
             PreparedStatement ps =
                 conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.setString(2, email);

            return ps.executeUpdate() > 0;
        }
    }

    // Mark all the user's notifications as read.
    public static void markAllAsRead(
            String email) throws SQLException {

        String sql = """
            UPDATE jm_notifications
            SET is_read = 1
            WHERE LOWER(recipient_email) = LOWER(?)
            AND is_read = 0
            """;

        try (Connection conn = FeatureDatabase.connect();
             PreparedStatement ps =
                 conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.executeUpdate();
        }
    }
}
