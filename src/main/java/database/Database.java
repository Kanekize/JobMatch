package database;

import models.Applicant;
import models.Job;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
                "company_logo_path TEXT, " +
                "company_about TEXT, " +
                "company_permit_path TEXT, " +
                "business_reg_num, " +
                "created_at TEXT DEFAULT CURRENT_TIMESTAMP)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE users ADD COLUMN company_name TEXT");
        } catch (SQLException e) {}
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE users ADD COLUMN company_logo_path TEXT");
        } catch (SQLException e) {}
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE users ADD COLUMN company_about TEXT");
        } catch (SQLException e) {}
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE users ADD COLUMN company_permit_path TEXT");
        } catch (SQLException e) {}
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE users ADD COLUMN business_reg_num TEXT");
        } catch (SQLException e) {}

        String rememberSql = "CREATE TABLE IF NOT EXISTS remembered (" +
                "role TEXT PRIMARY KEY, " +
                "email TEXT NOT NULL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(rememberSql);
        }

        String jobsTable = "CREATE TABLE IF NOT EXISTS jobs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "employer_email TEXT NOT NULL, " +
                "title TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "job_type TEXT NOT NULL, " +
                "workplace_type TEXT NOT NULL, " +
                "salary_min INTEGER NOT NULL, " +
                "salary_max INTEGER NOT NULL, " +
                "description TEXT, " +
                "skills TEXT, " +
                "accommodations_available INTEGER DEFAULT 0, " +
                "accommodations TEXT, " +
                "applicant_types TEXT, " +
                "experience_required INTEGER DEFAULT 0, " +
                "experience_length TEXT, " +
                "location TEXT, " +
                "city TEXT, " +
                "logo_path TEXT, " +
                "status TEXT DEFAULT 'Active', " +
                "favorite INTEGER DEFAULT 0, " +
                "created_at TEXT DEFAULT CURRENT_TIMESTAMP)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(jobsTable);
        }

        String applicantsTable = "CREATE TABLE IF NOT EXISTS applicants (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "job_id INTEGER NOT NULL, " +
                "name TEXT NOT NULL, " +
                "applicant_email TEXT, " +
                "details TEXT, " +
                "status TEXT DEFAULT 'Under Review', " +
                "match_percent INTEGER DEFAULT 0, " +
                "applied_at TEXT DEFAULT CURRENT_TIMESTAMP)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(applicantsTable);
        }
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE applicants ADD COLUMN applicant_email TEXT");
        } catch (SQLException e) {}
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE applicants ADD COLUMN applied_at TEXT");
        } catch (SQLException e) {}

        String favTables = "CREATE TABLE IF NOT EXISTS seeker_favorites (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "seeker_email TEXT NOT NULL, " +
                "job_id INTEGER NOT NULL, " +
                "UNIQUE(seeker_email, job_id))";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(favTables);
        }

        String applicationsTable = "CREATE TABLE IF NOT EXISTS job_applications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "job_id INTEGER NOT NULL, " +
                "seeker_email TEXT NOT NULL, " +
                "status TEXT NOT NULL DEFAULT 'Under Review', " +
                "applied_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "UNIQUE(job_id, seeker_email))";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(applicationsTable);
        }

        String submissionsTable = "CREATE TABLE IF NOT EXISTS assessment_submissions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_email TEXT, " +
                "profile_type TEXT, " +
                "submitted_at TEXT DEFAULT CURRENT_TIMESTAMP)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(submissionsTable);
        }
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE assessment_submissions ADD COLUMN user_email TEXT");
        } catch (SQLException e) {}
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE assessment_submissions ADD COLUMN profile_type TEXT");
        } catch (SQLException e) {}

        String answersTable = "CREATE TABLE IF NOT EXISTS assessment_answers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "submission_id INTEGER NOT NULL, " +
                "question_key TEXT NOT NULL, " +
                "answer_value TEXT)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(answersTable);
        }

        String profileTable = "CREATE TABLE IF NOT EXISTS profile_details (" +
                "user_email TEXT NOT NULL, " +
                "field_name TEXT NOT NULL, " +
                "field_value TEXT, " +
                "PRIMARY KEY (user_email, field_name))";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(profileTable);
        }

        String ratingsTable = "CREATE TABLE IF NOT EXISTS job_ratings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "job_id INTEGER NOT NULL, " +
                "seeker_email TEXT NOT NULL, " +
                "rating_value INTEGER NOT NULL, " +
                "rated_at TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "UNIQUE(job_id, seeker_email))";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(ratingsTable);
        } try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE job_ratings ADD COLUMN rated_at TEXT DEFAULT CURRENT_TIMESTAMP");
            System.out.println("Database column 'rated_at' successfully synchronized onto your job_ratings schema.");
        } catch (SQLException e) {}
    }

    public static void dropTables(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS applicants");
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

    public static boolean registerUser(String firstName, String lastName, String email, String password, String role, String companyName, String regNum, String permitPath) {
        String sql = "INSERT INTO users (first_name, last_name, email, password, role, company_name, business_reg_num, company_permit_path, company_logo_path, company_about) VALUES (?, ?, ?, ?, ?, ?, ?, ?, '', '')";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email.toLowerCase());
            ps.setString(4, password);
            ps.setString(5, role);
            ps.setString(6, companyName);
            ps.setString(7, regNum);
            ps.setString(8, permitPath);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("DB error during extended registration: " + e.getMessage());
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

    // job postng

    public static int createJob(Job job) {
        String sql = "INSERT INTO jobs (employer_email, title, category, job_type, workplace_type, " +
                "salary_min, salary_max, description, skills, accommodations_available, accommodations, " +
                "applicant_types, experience_required, experience_length, location, city, logo_path, status, favorite) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillJobStatement(ps, job);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int newId = keys.getInt(1);
                    addSampleApplicants(newId);
                    return newId;
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
        return -1;
    }

    public static void updateJob(Job job) {
        String sql = "UPDATE jobs SET title=?, category=?, job_type=?, workplace_type=?, salary_min=?, salary_max=?, " +
                "description=?, skills=?, accommodations_available=?, accommodations=?, applicant_types=?, " +
                "experience_required=?, experience_length=?, location=?, city=?, logo_path=?, status=? WHERE id=?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, job.getTitle());
            ps.setString(2, job.getCategory());
            ps.setString(3, job.getJobType());
            ps.setString(4, job.getWorkplaceType());
            ps.setInt(5, job.getSalaryMin());
            ps.setInt(6, job.getSalaryMax());
            ps.setString(7, job.getDescription());
            ps.setString(8, String.join(";", job.getSkills()));
            ps.setInt(9, job.isAccommodationsAvailable() ? 1 : 0);
            ps.setString(10, String.join(";", job.getAccommodations()));
            ps.setString(11, String.join(";", job.getApplicantTypes()));
            ps.setInt(12, job.isExperienceRequired() ? 1 : 0);
            ps.setString(13, job.getExperienceLength());
            ps.setString(14, job.getLocation());
            ps.setString(15, job.getCity());
            ps.setString(16, job.getLogoPath());
            ps.setString(17, job.getStatus());
            ps.setInt(18, job.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }

    private static void fillJobStatement(PreparedStatement ps, Job job) throws SQLException {
        ps.setString(1, job.getEmployerEmail());
        ps.setString(2, job.getTitle());
        ps.setString(3, job.getCategory());
        ps.setString(4, job.getJobType());
        ps.setString(5, job.getWorkplaceType());
        ps.setInt(6, job.getSalaryMin());
        ps.setInt(7, job.getSalaryMax());
        ps.setString(8, job.getDescription());
        ps.setString(9, String.join(";", job.getSkills()));
        ps.setInt(10, job.isAccommodationsAvailable() ? 1 : 0);
        ps.setString(11, String.join(";", job.getAccommodations()));
        ps.setString(12, String.join(";", job.getApplicantTypes()));
        ps.setInt(13, job.isExperienceRequired() ? 1 : 0);
        ps.setString(14, job.getExperienceLength());
        ps.setString(15, job.getLocation());
        ps.setString(16, job.getCity());
        ps.setString(17, job.getLogoPath());
        ps.setString(18, job.getStatus());
        ps.setInt(19, job.isFavorite() ? 1 : 0);
    }

    public static List<Job> getJobsByEmployer(String email) {
        String sql = "SELECT * FROM jobs WHERE employer_email = ?";
        List<Job> jobs = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    jobs.add(makeJob(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
        return jobs;
    }

    public static List<Job> getAllJobs() {
        String sql = "SELECT * FROM jobs";
        List<Job> jobs = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                jobs.add(makeJob(rs));
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
        return jobs;
    }

    public static String getEmployerCompanyName(String employerEmail) {
        String sql = "SELECT company_name FROM users WHERE email = ? AND role = 'Employer'";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employerEmail.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String company = rs.getString("company_name");
                    if (company != null && !company.trim().isEmpty()) {
                        return company;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
        return employerEmail;
    }

    public static String getEmployerCompanyLogoPath(String employerEmail) {
        String sql = "SELECT company_logo_path FROM users WHERE email = ? AND role = 'Employer'";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employerEmail.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String logo = rs.getString("company_logo_path");
                    if (logo != null) {
                        return logo;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
        return "";
    }

    public static String getEmployerCompanyAbout(String employerEmail) {
        String sql = "SELECT company_about FROM users WHERE email = ? AND role = 'Employer'";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employerEmail.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String about = rs.getString("company_about");
                    if (about != null) {
                        return about;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
        return "";
    }

    public static void updateEmployerProfile(String employerEmail, String companyName, String companyLogoPath, String companyAbout, String permitPath, String regNum) {
        String sql = "UPDATE users SET company_name = ?, company_logo_path = ?, company_about = ?, company_permit_path = ?, business_reg_num = ? WHERE email = ? AND role = 'Employer'";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, companyName);
            ps.setString(2, companyLogoPath);
            ps.setString(3, companyAbout);
            ps.setString(4, permitPath);
            ps.setString(5, regNum);
            ps.setString(6, employerEmail.toLowerCase());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("DB error updating extended employer profile: " + e.getMessage());
        }
    }

    public static void setJobFavorite(int jobId, boolean favorite) {
        String sql = "UPDATE jobs SET favorite = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, favorite ? 1 : 0);
            ps.setInt(2, jobId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }

    private static Job makeJob(ResultSet rs) throws SQLException {
        Job job = new Job();
        job.setId(rs.getInt("id"));
        job.setEmployerEmail(rs.getString("employer_email"));
        job.setTitle(rs.getString("title"));
        job.setCategory(rs.getString("category"));
        job.setJobType(rs.getString("job_type"));
        job.setWorkplaceType(rs.getString("workplace_type"));
        job.setSalaryMin(rs.getInt("salary_min"));
        job.setSalaryMax(rs.getInt("salary_max"));
        job.setDescription(rs.getString("description"));
        job.setSkills(splitText(rs.getString("skills")));
        job.setAccommodationsAvailable(rs.getInt("accommodations_available") == 1);
        job.setAccommodations(splitText(rs.getString("accommodations")));
        job.setApplicantTypes(splitText(rs.getString("applicant_types")));
        job.setExperienceRequired(rs.getInt("experience_required") == 1);
        job.setExperienceLength(rs.getString("experience_length"));
        job.setLocation(rs.getString("location"));
        job.setCity(rs.getString("city"));
        job.setLogoPath(rs.getString("logo_path"));
        job.setStatus(rs.getString("status"));
        job.setFavorite(rs.getInt("favorite") == 1);
        job.setCreatedAt(rs.getString("created_at"));
        return job;
    }

    private static List<String> splitText(String text) {
        List<String> list = new ArrayList<>();
        if (text != null && !text.isEmpty()) {
            list.addAll(Arrays.asList(text.split(";")));
        }
        return list;
    }

    // Job seeker applications
    public static boolean applyToJob(String seekerEmail, int jobId) {
        String sql = "INSERT OR IGNORE INTO job_applications (job_id, seeker_email) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, jobId);
                ps.setString(2, seekerEmail.toLowerCase());
                int inserted = ps.executeUpdate();
                if (inserted == 0) { conn.rollback(); return false; }
            }
            String applicantSql = "INSERT INTO applicants (job_id, name, applicant_email, details, status, match_percent, applied_at) " +
                    "SELECT ?, first_name || ' ' || last_name, email, email, 'Under Review', 0, CURRENT_TIMESTAMP " +
                    "FROM users WHERE LOWER(email)=LOWER(?) AND role='Job Seeker'";
            try (PreparedStatement ps = conn.prepareStatement(applicantSql)) {
                ps.setInt(1, jobId);
                ps.setString(2, seekerEmail);
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Application save error: " + e.getMessage());
            return false;
        }
    }

    public static boolean hasApplied(String seekerEmail, int jobId) {
        String sql = "SELECT 1 FROM job_applications WHERE LOWER(seeker_email)=LOWER(?) AND job_id=?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, seekerEmail); ps.setInt(2, jobId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { System.err.println("Application lookup error: " + e.getMessage()); return false; }
    }

    public static List<models.JobApplication> getApplicationsForSeeker(String seekerEmail) {
        List<models.JobApplication> list = new ArrayList<>();
        String sql = "SELECT j.title, j.employer_email, COALESCE(j.location,''), COALESCE(j.city,''), " +
                "COALESCE(ap.status, a.status), a.applied_at, COALESCE(j.logo_path,'') " +
                "FROM job_applications a JOIN jobs j ON j.id=a.job_id " +
                "LEFT JOIN applicants ap ON ap.job_id=j.id AND LOWER(ap.details)=LOWER(a.seeker_email) " +
                "WHERE LOWER(a.seeker_email)=LOWER(?) ORDER BY a.applied_at DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, seekerEmail);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String location = rs.getString(4).isEmpty() ? rs.getString(3) : rs.getString(4) + ", " + rs.getString(3);
                    String applied = rs.getString(6);
                    String dateText = "Applied on " + (applied == null ? "" : applied);
                    models.JobApplication app = new models.JobApplication(rs.getString(1), rs.getString(2), location,
                            normalizeApplicationStatus(rs.getString(5)), dateText);
                    app.setLogoPath(rs.getString(7));
                    list.add(app);
                }
            }
        } catch (SQLException e) { System.err.println("Applications load error: " + e.getMessage()); }
        return list;
    }

    private static String normalizeApplicationStatus(String status) {
        if (status == null) return "Under Review";
        if (status.equalsIgnoreCase("Submitted")) return "Applied";
        if (status.equalsIgnoreCase("Shortlisted") || status.equalsIgnoreCase("Interview Scheduled")) return "Interview";
        if (status.equalsIgnoreCase("Declined")) return "Rejected";
        return status;
    }

    // applicants
    public static List<Applicant> getApplicantsByJob(int jobId) {
        String sql = "SELECT a.id, a.job_id, a.name, " +
                "COALESCE(a.applicant_email, u.email, '') AS applicant_email, " +
                "COALESCE(j.title, '') AS job_title, " +
                "a.details, a.status, a.match_percent, a.applied_at " +
                "FROM applicants a " +
                "JOIN jobs j ON j.id = a.job_id " +
                "LEFT JOIN users u ON LOWER(TRIM(u.first_name || ' ' || u.last_name)) = LOWER(TRIM(a.name)) " +
                "AND u.role = 'Job Seeker' " +
                "WHERE a.job_id = ? ORDER BY a.applied_at DESC";
        List<Applicant> applicants = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    applicants.add(new Applicant(
                            rs.getInt("id"),
                            rs.getInt("job_id"),
                            rs.getString("name"),
                            rs.getString("applicant_email"),
                            rs.getString("job_title"),
                            rs.getString("details"),
                            rs.getString("status"),
                            rs.getInt("match_percent"),
                            rs.getString("applied_at")));
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
        return applicants;
    }

    public static List<Applicant> getApplicantsByEmployer(String employerEmail) {
        String sql = "SELECT a.id, a.job_id, a.name, " +
                "COALESCE(a.applicant_email, u.email, '') AS applicant_email, " +
                "COALESCE(j.title, '') AS job_title, " +
                "a.details, a.status, a.match_percent, a.applied_at " +
                "FROM applicants a " +
                "JOIN jobs j ON j.id = a.job_id " +
                "LEFT JOIN users u ON LOWER(TRIM(u.first_name || ' ' || u.last_name)) = LOWER(TRIM(a.name)) " +
                "AND u.role = 'Job Seeker' " +
                "WHERE j.employer_email = ? ORDER BY a.applied_at DESC";
        List<Applicant> applicants = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employerEmail.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    applicants.add(new Applicant(
                            rs.getInt("id"),
                            rs.getInt("job_id"),
                            rs.getString("name"),
                            rs.getString("applicant_email"),
                            rs.getString("job_title"),
                            rs.getString("details"),
                            rs.getString("status"),
                            rs.getInt("match_percent"),
                            rs.getString("applied_at")));
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
        return applicants;
    }

    public static int countApplicantsByEmployer(String employerEmail) {
        String sql = "SELECT COUNT(*) FROM applicants a " +
                "JOIN jobs j ON j.id = a.job_id WHERE j.employer_email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employerEmail.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
        return 0;
    }

    public static List<Applicant> getRecentApplicantsByEmployer(String employerEmail, int limit) {
        String sql = "SELECT a.id, a.job_id, a.name, " +
                "COALESCE(a.applicant_email, u.email, '') AS applicant_email, " +
                "COALESCE(j.title, '') AS job_title, " +
                "a.details, a.status, a.match_percent, a.applied_at " +
                "FROM applicants a JOIN jobs j ON j.id = a.job_id " +
                "LEFT JOIN users u ON LOWER(TRIM(u.first_name || ' ' || u.last_name)) = LOWER(TRIM(a.name)) " +
                "AND u.role = 'Job Seeker' " +
                "WHERE j.employer_email = ? ORDER BY a.applied_at DESC LIMIT ?";
        List<Applicant> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employerEmail.toLowerCase());
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Applicant(
                            rs.getInt("id"),
                            rs.getInt("job_id"),
                            rs.getString("name"),
                            rs.getString("applicant_email"),
                            rs.getString("job_title"),
                            rs.getString("details"),
                            rs.getString("status"),
                            rs.getInt("match_percent"),
                            rs.getString("applied_at")));
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
        return list;
    }

    public static void updateApplicantStatus(int applicantId, String status) {
        String sql = "UPDATE applicants SET status = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, applicantId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }

    public static void deleteApplicant(int applicantId) {
        String sql = "DELETE FROM applicants WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, applicantId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }

    public static int countApplicants(int jobId) {
        String sql = "SELECT COUNT(*) FROM applicants WHERE job_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
        return 0;
    }

    // test applicants lang papalitan to mamaya pag meron na ung ibang part nila
    private static void addSampleApplicants(int jobId) {
        String sql = "INSERT INTO applicants (job_id, name, details, status, match_percent, applied_at) " +
                "VALUES (?, ?, ?, ?, 0, ?)";
        String[][] samples = {
                {"Jaspir Pazt", "BSCS Graduate, looking for first job", "Under Review", "2026-11-10 09:15:00"},
                {"Jush Recintu", "2 years experience as office staff", "Under Review", "2025-11-08 14:30:00"},
                {"Marcos Makilan Hatdoggy", "Working student, available part-time", "Interview", "2024-11-05 11:00:00"}
        };
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String[] s : samples) {
                ps.setInt(1, jobId);
                ps.setString(2, s[0]);
                ps.setString(3, s[1]);
                ps.setString(4, s[2]);
                ps.setString(5, s[3]);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }

    // jobseek favs
    public static boolean isFavoriteForSeeker(String email, int jobId) {
        String sql = "SELECT 1 FROM seeker_favorites WHERE seeker_email = ? AND job_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            ps.setInt(2, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
        return false;
    }

    public static void toggleFavoriteForSeeker(String email, int jobId) {
        if (isFavoriteForSeeker(email, jobId)) {
            String sql = "DELETE FROM seeker_favorites WHERE seeker_email = ? AND job_id = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email.toLowerCase());
                ps.setInt(2, jobId);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("DB error: " + e.getMessage());
            }
        } else {
            String sql = "INSERT INTO seeker_favorites (seeker_email, job_id) VALUES (?, ?)";
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email.toLowerCase());
                ps.setInt(2, jobId);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("DB error: " + e.getMessage());
            }
        }
    }

    // assessment
    public static boolean saveUserAssessment(String email, Map<String, String> answers) {
        String submissionSql = "INSERT INTO assessment_submissions (user_email, profile_type) VALUES (?, ?)";
        String answerSql = "INSERT INTO assessment_answers (submission_id, question_key, answer_value) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);

            try {
                int submissionId = 0;
                try (PreparedStatement ps = conn.prepareStatement(submissionSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, email.toLowerCase());
                    ps.setString(2, answers.getOrDefault("Profile Type", ""));
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            submissionId = keys.getInt(1);
                        }
                    }
                }
                if (submissionId == 0) {
                    throw new SQLException("No submission id");
                }

                try (PreparedStatement ps = conn.prepareStatement(answerSql)) {
                    for (Map.Entry<String, String> entry : answers.entrySet()) {
                        ps.setInt(1, submissionId);
                        ps.setString(2, entry.getKey());
                        ps.setString(3, entry.getValue());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("DB error while saving assessment: " + e.getMessage());
            return false;
        }
    }

    // for collecting all the answers in assessment from their latest
    public static Map<String, String> getLatestAssessment(String email) {
        String sql = "SELECT question_key, answer_value FROM assessment_answers " +
                "WHERE submission_id = (SELECT id FROM assessment_submissions WHERE user_email = ? ORDER BY submitted_at DESC LIMIT 1) " +
                "ORDER BY rowid";
        Map<String, String> answers = new LinkedHashMap<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    answers.put(rs.getString("question_key"), rs.getString("answer_value"));
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
        return answers;
    }

    // for knowing the last assessment test date mo sinagutan
    public static String getLatestAssessmentDate(String email) {
        String sql = "SELECT submitted_at FROM assessment_submissions WHERE user_email = ? ORDER BY submitted_at DESC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getString("submitted_at") != null) {
                    return rs.getString("submitted_at");
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
        return "";
    }

    // job seek profile details
    // main resume = applicant profile, sub resume = profile details
    public static Map<String, String> getProfileDetails(String email) {
        String sql = "SELECT field_name, field_value FROM profile_details WHERE user_email = ?";
        Map<String, String> details = new LinkedHashMap<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String value = rs.getString("field_value");
                    details.put(rs.getString("field_name"), value == null ? "" : value);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        }
        return details;
    }

    public static boolean saveProfileDetails(String email, Map<String, String> details) {
        String sql = "INSERT OR REPLACE INTO profile_details (user_email, field_name, field_value) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Map.Entry<String, String> entry : details.entrySet()) {
                    ps.setString(1, email.toLowerCase());
                    ps.setString(2, entry.getKey());
                    ps.setString(3, entry.getValue());
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
            return false;
        }
    }

    // star rates
    public static boolean saveJobRating(int jobId, String seekerEmail, int ratingValue) {
        String sql = "INSERT OR REPLACE INTO job_ratings (job_id, seeker_email, rating_value) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ps.setString(2, seekerEmail.toLowerCase());
            ps.setInt(3, ratingValue);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("DB Error saving job rating: " + e.getMessage());
            return false;
        }
    }

    public static int getIndividualJobRating(int jobId, String seekerEmail) {
        String sql = "SELECT rating_value FROM job_ratings WHERE job_id = ? AND LOWER(seeker_email) = LOWER(?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ps.setString(2, seekerEmail);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("rating_value");
                }
            }
        } catch (SQLException e) {
            System.err.println("DB Error loading individual rating: " + e.getMessage());
        }
        return 0;
    }

    public static double getAverageJobRating(int jobId) {
        String sql = "SELECT AVG(rating_value) AS avg_score FROM job_ratings WHERE job_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble("avg_score");
                    return Math.round(avg * 10.0) / 10.0;
                }
            }
        } catch (SQLException e) {
            System.err.println("DB Error reading average job rating: " + e.getMessage());
        }
        return 0.0;
    }

    public static int getTotalRatingCount(int jobId) {
        String sql = "SELECT COUNT(*) FROM job_ratings WHERE job_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB Error counting job ratings: " + e.getMessage());
        }
        return 0;
    }

    public static int getSpecificStarCount(int jobId, int starValue) {
        String sql = "SELECT COUNT(*) FROM job_ratings WHERE job_id = ? AND rating_value = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ps.setInt(2, starValue);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB Error counting specific stars: " + e.getMessage());
        }
        return 0;
    }

    public static List<String> getVotersForStarRating(int jobId, int starValue) {
        List<String> voters = new ArrayList<>();
        String sql = "SELECT u.first_name, u.last_name, r.rated_at FROM job_ratings r " +
                "JOIN users u ON LOWER(u.email) = LOWER(r.seeker_email) " +
                "WHERE r.job_id = ? AND r.rating_value = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ps.setInt(2, starValue);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                    String rawDate = rs.getString("rated_at");
                    String reviewDate = (rawDate != null) ? rawDate.split(" ")[0] : "2026-10-17";

                    voters.add(fullName + "  (" + reviewDate + ")");
                }
            }
        } catch (SQLException e) {
            System.err.println("DB Error retrieving rating voters list: " + e.getMessage());
        }
        return voters;
    }

    //employer legal documentation
    public static String getEmployerBusinessRegNum(String employerEmail) {
        String sql = "SELECT business_reg_num FROM users WHERE email = ? AND role = 'Employer'";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employerEmail.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String reg = rs.getString("business_reg_num");
                    return reg == null ? "" : reg;
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error reading registration number: " + e.getMessage());
        }
        return "";
    }

    public static String getEmployerCompanyPermitPath(String employerEmail) {
        String sql = "SELECT company_permit_path FROM users WHERE email = ? AND role = 'Employer'";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employerEmail.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String permit = rs.getString("company_permit_path");
                    return permit == null ? "" : permit;
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error reading permit path: " + e.getMessage());
        }
        return "";
    }


} //pinaka end