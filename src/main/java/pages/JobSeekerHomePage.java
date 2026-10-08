package pages;

import components.SectionPanel;
import components.StatCard;
import users.User;

import javax.swing.*;
import java.awt.*;

public class JobSeekerHomePage extends HomePage {

    public JobSeekerHomePage(User user) {
        super(user);

        addBlock(makeHeading(getGreeting() + ", " + user.getDisplayName(),
                "Here's what's happening with your JobMatch profile.", null), 15);

        JPanel counters = new JPanel(new GridLayout(1, 3, 15, 0));
        counters.setOpaque(false);
        counters.add(new StatCard("Profile Completion", "0%", Color.decode("#0d0d0d")));
        counters.add(new StatCard("Assessment", "Not Taken", Color.decode("#6b7280")));
        counters.add(new StatCard("Active Applications", "0", Color.decode("#2563eb")));
        addBlock(counters, 20);

        addBlock(new SectionPanel("Recommended for You", "View all jobs >", 3, 72, false), 20);
        addBlock(new SectionPanel("Upcoming Interview", "View all interviews >", 1, 72, false), 20);
        addBlock(new SectionPanel("Recommended Industries", "View all industries >", 3, 72, true), 0);
    }
}