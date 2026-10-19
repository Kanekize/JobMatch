package dashboards;

import components.Layout;
import pages.EmployerHomePage;
import pages.EmployerJobsPage;
import pages.ApplicantsPage;
import pages.CompanyProfilePage;
import pages.MessagesPage;
import pages.NotificationsPage;
import users.User;
import javax.swing.*;
import java.awt.*;

public class EmployerDashboard extends Dashboard {

    public EmployerDashboard(User user) {
        super(user);
    }

    // The sidebar buttons of Employer
    @Override
    public String[] getMenuItems() {
        return new String[] {"Home", "Jobs", "Applicants", "Messages", "Interviews"};
    }

    @Override
    protected void buildContent() {
        layout = new Layout(this);
        showPage(new EmployerHomePage(getUser()));
    }

    @Override
    public void onMenuClick(String name) {
        layout.setActive(name);

        if (name.equals("Home")) {
            showPage(new EmployerHomePage(getUser()));
        } else if (name.equals("Jobs")) {
            showPage(new EmployerJobsPage(getUser()));
        } else if (name.equals("Applicants")) {
            showPage(new ApplicantsPage(getUser()));
        } else if (name.equals("Profile")) {
            showPage(new CompanyProfilePage(getUser()));
        } else if (name.equals("Messages")) {
            showPage(new MessagesPage(getUser()));
        } else if (name.equals("Notifications")) {
            showPage(new NotificationsPage(getUser()));
        } else {
            System.out.println("Employer clicked unhandled menu item: " + name);
            showPage(new EmployerHomePage(getUser()));
        }
    }
}
