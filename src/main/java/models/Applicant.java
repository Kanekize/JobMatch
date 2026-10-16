package models;

// applicant info for job posts
public class Applicant {

    private int id;
    private int jobId;
    private String name;
    private String email;
    private String jobTitle;
    private String details;
    private String status;
    private int matchPercent;
    private String appliedAt;

    public Applicant(int id, int jobId, String name, String details, String status, int matchPercent, String appliedAt) {
        this(id, jobId, name, null, null, details, status, matchPercent, appliedAt);
    }

    public Applicant(int id, int jobId, String name, String email, String jobTitle, String details, String status, int matchPercent, String appliedAt) {
        this.id = id;
        this.jobId = jobId;
        this.name = name;
        this.email = email;
        this.jobTitle = jobTitle;
        this.details = details;
        this.status = status;
        this.matchPercent = matchPercent;
        this.appliedAt = appliedAt;
    }

    public int getId() { return id; }
    public int getJobId() { return jobId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getJobTitle() { return jobTitle; }
    public String getDetails() { return details; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getMatchPercent() { return matchPercent; }
    public String getAppliedAt() { return appliedAt; }
}