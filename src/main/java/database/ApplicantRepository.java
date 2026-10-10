
package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ApplicantRepository {

    public static final String[] STATUSES = {
        "Submitted",
        "Under Review",
        "Shortlisted",
        "Interview Scheduled",
        "Accepted",
        "Declined"
    };

    private ApplicantRepository() {}

    // Stores the information of one applicant.
    public static final class Applicant {

        public final long id;

        public final String name, email, jobTitle,
                            status, appliedAt;

        public final String professionalTitle, location,
                            skills, education, experience,
                            about;

        private Applicant(ResultSet rs) throws SQLException {

            id = rs.getLong("id");
            name = rs.getString("name");
            email = rs.getString("applicant_email");
            jobTitle = rs.getString("job_title");
            status = rs.getString("status");
            appliedAt = rs.getString("applied_at");

            professionalTitle =
                rs.getString("professional_title");

            location = rs.getString("location");
            skills = rs.getString("skills");
            education = rs.getString("education");
            experience = rs.getString("experience");
            about = rs.getString("about");
        }
    }

    // Retrieves job titles posted by this employer.
    public static List<String> jobTitles(String employerEmail)
            throws SQLException {

        List<String> titles = new ArrayList<>();
        titles.add("All Jobs");

        String sql = """
            SELECT DISTINCT title
            FROM jm_job_posts
            WHERE LOWER(employer_email) = LOWER(?)
            ORDER BY title
            """;

        try (Connection conn = FeatureDatabase.connect();
             PreparedStatement ps =
                 conn.prepareStatement(sql)) {

            ps.setString(1, employerEmail);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    titles.add(rs.getString(1));
                }
            }
        }

        return titles;
    }

    // Searches and filters applicants of this employer.
    public static List<Applicant> search(
            String employerEmail,
            String text,
            String status,
            String jobTitle) throws SQLException {

        List<Applicant> result = new ArrayList<>();

        String sql = """
            SELECT
                a.id,
                a.applicant_email,
                a.status,
                a.applied_at,

                u.first_name || ' ' || u.last_name AS name,
                j.title AS job_title,

                COALESCE(p.professional_title, '')
                    AS professional_title,

                COALESCE(p.location, '') AS location,
                COALESCE(p.skills, '') AS skills,
                COALESCE(p.education, '') AS education,
                COALESCE(p.experience, '') AS experience,
                COALESCE(p.about, '') AS about

            FROM jm_applications a

            JOIN jm_job_posts j
                ON a.job_id = j.id

            JOIN users u
                ON LOWER(u.email) =
                   LOWER(a.applicant_email)
                AND u.role = 'Job Seeker'

            LEFT JOIN jm_applicant_profiles p
                ON LOWER(p.applicant_email) =
                   LOWER(a.applicant_email)

            WHERE LOWER(j.employer_email) = LOWER(?)

            AND (? = 'All Statuses' OR a.status = ?)

            AND (? = 'All Jobs' OR j.title = ?)

            AND (
                LOWER(u.first_name || ' ' ||
                      u.last_name) LIKE ?

                OR LOWER(a.applicant_email) LIKE ?

                OR LOWER(j.title) LIKE ?

                OR LOWER(COALESCE(p.skills, '')) LIKE ?
            )

            ORDER BY a.id DESC
            """;

        String pattern = "%" +
            (text == null
                ? ""
                : text.trim().toLowerCase())
            + "%";

        try (Connection conn = FeatureDatabase.connect();
             PreparedStatement ps =
                 conn.prepareStatement(sql)) {

            ps.setString(1, employerEmail);
            ps.setString(2, status);
            ps.setString(3, status);
            ps.setString(4, jobTitle);
            ps.setString(5, jobTitle);

            for (int i = 6; i <= 9; i++) {
                ps.setString(i, pattern);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    result.add(new Applicant(rs));
                }
            }
        }

        return result;
    }

    // Updates an application and notifies its applicant.
    public static boolean updateStatus(
            long applicationId,
            String employerEmail,
            String newStatus) throws SQLException {

        if (!Arrays.asList(STATUSES).contains(newStatus)) {
            throw new IllegalArgumentException(
                "Unknown application status."
            );
        }

        try (Connection conn = FeatureDatabase.connect()) {

            conn.setAutoCommit(false);

            try {
                String email;
                String jobTitle;
                String oldStatus;

                String sql = """
                    SELECT
                        a.applicant_email,
                        a.status,
                        j.title

                    FROM jm_applications a

                    JOIN jm_job_posts j
                        ON a.job_id = j.id

                    WHERE a.id = ?
                    AND LOWER(j.employer_email) = LOWER(?)
                    """;

                try (PreparedStatement ps =
                         conn.prepareStatement(sql)) {

                    ps.setLong(1, applicationId);
                    ps.setString(2, employerEmail);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }

                        email = rs.getString(1);
                        oldStatus = rs.getString(2);
                        jobTitle = rs.getString(3);
                    }
                }

                if (!oldStatus.equals(newStatus)) {

                    String updateSql = """
                        UPDATE jm_applications
                        SET status = ?
                        WHERE id = ?
                        """;

                    try (PreparedStatement ps =
                         conn.prepareStatement(updateSql)) {

                        ps.setString(1, newStatus);
                        ps.setLong(2, applicationId);
                        ps.executeUpdate();
                    }

                    String notificationSql = """
                        INSERT INTO jm_notifications
                        (recipient_email, title, body)
                        VALUES (?, ?, ?)
                        """;

                    try (PreparedStatement ps =
                         conn.prepareStatement(notificationSql)) {

                        ps.setString(1, email);

                        ps.setString(
                            2, "Application status updated"
                        );

                        ps.setString(
                            3,
                            "Your application for " +
                            jobTitle + " is now " +
                            newStatus + "."
                        );

                        ps.executeUpdate();
                    }
                }

                conn.commit();
                return true;

            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
