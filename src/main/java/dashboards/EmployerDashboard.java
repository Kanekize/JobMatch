package dashboards;

import components.Layout;
import pages.EmployerHomePage;
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

@Override
public void onMenuClick(String name) {

    JPanel center = layout.getContentPanel();

    if ("Applicants".equals(name)) {

        center.removeAll();

        center.add(
            new ApplicantsPage(getUser()),
            BorderLayout.CENTER
        );

    } else if ("Messages".equals(name)) {

        center.removeAll();

        center.add(
            new MessagesPage(getUser()),
            BorderLayout.CENTER
        );


} else if ("Notifications".equals(name)) {

    center.removeAll();

    center.add(
        new NotificationsPage(getUser()),
        BorderLayout.CENTER
    );

} else if ("Home".equals(name)) {


        center.removeAll();

        center.add(
            new EmployerHomePage(getUser()),
            BorderLayout.CENTER
        );

    } else {

        System.out.println(
            "Employer clicked: " + name
        );

        return;
    }

    center.revalidate();
    center.repaint();
}
