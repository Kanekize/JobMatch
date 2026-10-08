package users;

import dashboards.EmployerDashboard;

public class Employer extends User {

    private String companyName;

    public Employer(String firstName, String lastName, String email, String companyName) {
        super(firstName, lastName, email);
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    @Override
    public String getDisplayName() {
        if (companyName == null || companyName.isEmpty()) {
            return getFirstName();
        }
        return companyName;
    }

    public String getRole() {
        return "Employer";
    }

    public void openDashboard() {
        new EmployerDashboard(this).showDashboard();
    }
}