package dashboards;

import components.Layout;
import pages.JobSeekerHomePage;
import pages.JobSeekerRecommendationsPage;
import users.User;

import javax.swing.*;
import java.awt.*;

public class JobSeekerDashboard extends Dashboard {

    public JobSeekerDashboard(User user) {
        super(user);
    }

    // the sidebar buttons of Job Seeker
    @Override
    public String[] getMenuItems() {
        return new String[] {"Home", "Recommendations", "Applications", "Assessment", "Messages", "Interviews"};
    }

    protected void buildContent() {
        layout = new Layout(this);
        JPanel center = layout.getContentPanel();
        center.add(new JobSeekerHomePage(getUser()), BorderLayout.CENTER);

    }

    public void onMenuClick(String name) {
        layout.setActive(name);
        if (name.equals("Home")) {
            showPage(new JobSeekerHomePage(getUser()));
        } else if (name.equals("Recommendations")) {
            showPage(new JobSeekerRecommendationsPage(getUser()));
        } else {
            showPage(new JobSeekerHomePage(getUser()));
        }
    }
}