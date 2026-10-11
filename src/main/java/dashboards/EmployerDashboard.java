package dashboards;

import components.Layout;
import pages.EmployerHomePage;
import pages.EmployerJobsPage;
import pages.ApplicantsPage;
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
        // Display the Home Page inside the content window by default on startup
        showPage(new EmployerHomePage(getUser()));
    }

    // FIXED: Cleaned up the broken duplicate method collision blocks
    @Override
    public void onMenuClick(String name) {
        layout.setActive(name);

        if (name.equals("Home")) {
            showPage(new EmployerHomePage(getUser()));
        } else if (name.equals("Jobs")) {
            showPage(new EmployerJobsPage(getUser()));
        } else if (name.equals("Applicants")) {
            showPage(new ApplicantsPage(getUser()));
        } else if (name.equals("Messages")) {
            showPage(new MessagesPage(getUser()));
        } else if (name.equals("Notifications")) {
            showPage(new NotificationsPage(getUser()));
        } else {
            // Fallback default message if a button isn't wired up yet (like Interviews)
            System.out.println("Employer clicked unhandled menu item: " + name);
            showPage(new EmployerHomePage(getUser()));
        }
    }
}
