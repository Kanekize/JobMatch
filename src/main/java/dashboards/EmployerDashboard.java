package dashboards;

import components.Layout;
import pages.EmployerHomePage;
import users.User;

import javax.swing.*;
import java.awt.*;

public class EmployerDashboard extends Dashboard {

    public EmployerDashboard(User user) {
        super(user);
    }

    // the sidebar buttons of Employer
    @Override
    public String[] getMenuItems() {
        return new String[] {"Home", "Jobs", "Applicants", "Messages", "Interviews"};
    }

    protected void buildContent() {
        layout = new Layout(this);
        JPanel center = layout.getContentPanel(); // center panel ng dashboard
        center.add(new EmployerHomePage(getUser()), BorderLayout.CENTER);
    }

    //fort esting
    public void onMenuClick(String name) {
        System.out.println("Employer clicked: " + name);
    }
}