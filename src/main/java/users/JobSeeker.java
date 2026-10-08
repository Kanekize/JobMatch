package users;

import dashboards.JobSeekerDashboard;

public class JobSeeker extends User {

    public JobSeeker(String firstName, String lastName, String email) {
        super(firstName, lastName, email);
    }

    @Override
    public String getRole() {
        return "Job Seeker";
    }

    public void openDashboard() {
        new JobSeekerDashboard(this).showDashboard();
    }
}