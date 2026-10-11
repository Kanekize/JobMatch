package models;

// applicant info for job posts
public class Applicant {

    private int id;
    private int jobId;
    private String name;
    private String details;
    private String status;
    private int matchPercent;
    private String appliedAt;

    public Applicant(int id, int jobId, String name, String details, String status, int matchPercent, String appliedAt) {
        this.id = id;
        this.jobId = jobId;
        this.name = name;
        this.details = details;
        this.status = status;
        this.matchPercent = matchPercent;
        this.appliedAt = appliedAt;
    }

    public int getId() { return id; }
    public int getJobId() { return jobId; }
    public String getName() { return name; }
    public String getDetails() { return details; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getMatchPercent() { return matchPercent; }
    public String getAppliedAt() { return appliedAt; }
}