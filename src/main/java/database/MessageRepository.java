
package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class MessageRepository {

    private MessageRepository() {}

    // Represents another registered JobMatch user.
    public static final class Contact {

        public final String email;
        public final String name;
        public final String role;

        public Contact(String email, String name, String role) {
            this.email = email;
            this.name = name;
            this.role = role;
        }

        @Override
        public String toString() {
            return name + " (" + role + ")";
        }
    }

    // Represents one saved message.
    public static final class Message {

        public final String sender;
        public final String body;
        public final String sentAt;

        public Message(
                String sender,
                String body,
                String sentAt) {

            this.sender = sender;
            this.body = body;
            this.sentAt = sentAt;
        }
    }

    // Get contacts with the opposite account type.
    public static List<Contact> getContacts(
            String myEmail,
            String myRole) throws SQLException {

        String otherRole;

        if ("Employer".equals(myRole)) {
            otherRole = "Job Seeker";

        } else if ("Job Seeker".equals(myRole)) {
            otherRole = "Employer";

        } else {
            throw new IllegalArgumentException(
                "Unknown account role."
            );
        }

        List<Contact> result = new ArrayList<>();

        String sql = """
            SELECT email, first_name, last_name, role
            FROM users
            WHERE role = ?
            AND LOWER(email) <> LOWER(?)
            ORDER BY first_name COLLATE NOCASE,
                     last_name COLLATE NOCASE
            """;

        try (Connection conn = FeatureDatabase.connect();
             PreparedStatement ps =
                 conn.prepareStatement(sql)) {

            ps.setString(1, otherRole);
            ps.setString(2, myEmail);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    result.add(new Contact(
                        rs.getString("email"),
                        rs.getString("first_name") + " " +
                        rs.getString("last_name"),
                        rs.getString("role")
                    ));
                }
            }
        }

        return result;
    }

    // Load messages between two users.
    public static List<Message> getConversation(
            String myEmail,
            String otherEmail) throws SQLException {

        List<Message> result = new ArrayList<>();

        String sql = """
            SELECT sender_email, body, sent_at
            FROM jm_messages
            WHERE (
                LOWER(sender_email) = LOWER(?)
                AND LOWER(recipient_email) = LOWER(?)
            )
            OR (
                LOWER(sender_email) = LOWER(?)
                AND LOWER(recipient_email) = LOWER(?)
            )
            ORDER BY id ASC
            """;

        try (Connection conn = FeatureDatabase.connect();
             PreparedStatement ps =
                 conn.prepareStatement(sql)) {

            ps.setString(1, myEmail);
            ps.setString(2, otherEmail);
            ps.setString(3, otherEmail);
            ps.setString(4, myEmail);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    result.add(new Message(
                        rs.getString("sender_email"),
                        rs.getString("body"),
                        rs.getString("sent_at")
                    ));
                }
            }
        }

        return result;
    }

    // Save a message and create a notification.
    public static void sendMessage(
            String sender,
            String recipient,
            String body) throws SQLException {

        if (sender == null ||
            recipient == null ||
            sender.equalsIgnoreCase(recipient)) {

            throw new IllegalArgumentException(
                "Select a different contact."
            );
        }

        if (body == null ||
            body.isBlank() ||
            body.length() > 2000) {

            throw new IllegalArgumentException(
                "Enter a message of 1 to 2000 characters."
            );
        }

        try (Connection conn = FeatureDatabase.connect()) {

            conn.setAutoCommit(false);

            try {
                // Verify both users exist and have
                // different permitted account types.
                String verify = """
                    SELECT 1
                    FROM users s
                    JOIN users r
                        ON LOWER(r.email) = LOWER(?)
                    WHERE LOWER(s.email) = LOWER(?)
                    AND (
                        (s.role = 'Employer'
                         AND r.role = 'Job Seeker')
                        OR
                        (s.role = 'Job Seeker'
                         AND r.role = 'Employer')
                    )
                    """;

                try (PreparedStatement ps =
                         conn.prepareStatement(verify)) {

                    ps.setString(1, recipient);
                    ps.setString(2, sender);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new IllegalArgumentException(
                                "The selected contact is invalid."
                            );
                        }
                    }
                }

                // Insert the actual message.
                String insertMessage = """
                    INSERT INTO jm_messages
                    (sender_email, recipient_email, body)
                    VALUES (?, ?, ?)
                    """;

                try (PreparedStatement ps =
                         conn.prepareStatement(insertMessage)) {

                    ps.setString(1, sender);
                    ps.setString(2, recipient);
                    ps.setString(3, body.trim());

                    ps.executeUpdate();
                }

                // Notify the recipient.
                String insertNotification = """
                    INSERT INTO jm_notifications
                    (recipient_email, title, body)
                    VALUES (?, 'New Message', ?)
                    """;

                try (PreparedStatement ps =
                         conn.prepareStatement(
                             insertNotification)) {

                    ps.setString(1, recipient);

                    ps.setString(
                        2,
                        "You received a message from " +
                        sender + "."
                    );

                    ps.executeUpdate();
                }

                conn.commit();

            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
