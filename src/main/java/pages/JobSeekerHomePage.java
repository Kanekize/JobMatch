package pages;

import components.SectionPanel;
import components.StatCard;
import users.User;

import javax.swing.*;
import java.awt.*;

public class JobSeekerHomePage extends Page {

    public JobSeekerHomePage(User user) {
        super(user);

        addBlock(makeHeading(getGreeting() + ", " + user.getDisplayName(),
                "Here's what's happening with your JobMatch profile.", null), 15);

        JPanel counters = new JPanel(new GridLayout(1, 3, 15, 0));
        counters.setOpaque(false);
        counters.add(new StatCard("Profile Completion", "0%", Color.decode("#0d0d0d"), "user-blue.png", Color.decode("#dbeafe")));
        counters.add(new StatCard("Assessment", "Not Taken", Color.decode("#16a34a"), "assessment.png", Color.decode("#dcfce7")));
        counters.add(new StatCard("Active Applications", "0", Color.decode("#2563eb"), "applications.png", Color.decode("#dbeafe")));
        addBlock(counters, 20);

        // click = see all jobs
        SectionPanel recommended = new SectionPanel("Recommended for You", "View all jobs >", 3, 72, false);
        recommended.setEmptyState("No recommendations yet.");
        recommended.getViewAllButton().addActionListener(e ->
                getDashboard().showPage(new JobSeekerJobsPage(getUser(), null)));
        addBlock(recommended, 20);

        SectionPanel interviews = new SectionPanel("Upcoming Interview", "View all interviews >", 1, 72, false);
        interviews.setEmptyState("No interviews yet.");
        interviews.getViewAllButton().addActionListener(e -> getDashboard().onMenuClick("Interviews"));
        addBlock(interviews, 20);

        SectionPanel industries = new SectionPanel("Recommended Industries", "View all industries >", 3, 72, true);
        industries.setEmptyState("No industries yet.");
        industries.getViewAllButton().addActionListener(e -> getDashboard().onMenuClick("Recommendations"));
        addBlock(industries, 0);
    }
}