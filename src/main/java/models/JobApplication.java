package models;

// isang application ng job seeker (ung mga row sa My Applications page)
public class JobApplication {

    // order ng tabs sa page (after "All")
    public static final String[] STATUSES = {"Applied", "Under Review", "Interview", "Rejected", "Accepted"};

    private String jobTitle;
    private String company;
    private String location;
    private String status;
    private String dateText;   // "Applied on Oct 3, 2026" / "Scheduled on Oct 7, 2026"
    private String logoPath = "";

    public JobApplication(String jobTitle, String company, String location, String status, String dateText) {
        this.jobTitle = jobTitle;
        this.company = company;
        this.location = location;
        this.status = status;
        this.dateText = dateText;
    }

    public String getJobTitle() {
        return jobTitle;
    }
    public String getCompany() {
        return company;
    }
    public String getLocation() {
        return location;
    }
    public String getStatus() {
        return status;
    }
    public String getDateText() {
        return dateText;
    }
    public String getLogoPath() {
        return logoPath;
    }
    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
}
