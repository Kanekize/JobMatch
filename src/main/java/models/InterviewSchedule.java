package models;

public class InterviewSchedule {
    private final String jobTitle;
    private final String company;
    private final String jobLocation;
    private final String scheduledAt;
    private final String interviewLocation;
    private final String interviewMode;
    private final String notes;

    public InterviewSchedule(String jobTitle, String company, String jobLocation, String scheduledAt,
                             String interviewLocation, String interviewMode, String notes) {
        this.jobTitle = jobTitle;
        this.company = company;
        this.jobLocation = jobLocation;
        this.scheduledAt = scheduledAt;
        this.interviewLocation = interviewLocation;
        this.interviewMode = interviewMode;
        this.notes = notes;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public String getScheduledAt() {
        return scheduledAt;
    }

    public String getInterviewLocation() {
        return interviewLocation;
    }

    public String getInterviewMode() {
        return interviewMode;
    }

    public String getNotes() {
        return notes;
    }
}
