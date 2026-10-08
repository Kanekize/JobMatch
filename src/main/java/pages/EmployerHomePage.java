package pages;

import components.SectionPanel;
import components.StatCard;
import users.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmployerHomePage extends HomePage implements ActionListener {

    public EmployerHomePage(User user) {
        super(user);

        JButton postJobBtn = new JButton("Post a Job");
        postJobBtn.setBackground(Color.decode("#2563eb"));
        postJobBtn.setForeground(Color.WHITE);
        postJobBtn.setFont(new Font("Arial", Font.BOLD, 14));
        postJobBtn.setPreferredSize(new Dimension(140, 40));
        postJobBtn.setBorderPainted(false);
        postJobBtn.setOpaque(true);
        postJobBtn.setFocusable(false);
        postJobBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        addBlock(makeHeading(getGreeting() + ", " + user.getDisplayName(),
                "Here's an overview of your hiring activity and recent updates", postJobBtn), 15);

        JPanel counters = new JPanel(new GridLayout(1, 4, 15, 0));
        counters.setOpaque(false);
        counters.add(new StatCard("Active Job Postings", "0", Color.decode("#2563eb")));
        counters.add(new StatCard("Total Applications", "0", Color.decode("#16a34a")));
        counters.add(new StatCard("Shortlisted Candidates", "0", Color.decode("#7c3aed")));
        counters.add(new StatCard("Interviews Scheduled", "0", Color.decode("#0d0d0d")));
        addBlock(counters, 20);

        addBlock(new SectionPanel("Your Job Postings", "View All Posting", 3, 72, false), 20);

        JPanel bottom = new JPanel(new GridLayout(1, 2, 15, 0));
        bottom.setOpaque(false);

        bottom.add(new SectionPanel("Recent Applicants", "View All Applicants", 3, 62, false));

        SectionPanel quickActions = new SectionPanel("Quick Actions", "", 4, 50, false);
        quickActions.getBox(0).add(makeQuickAction("Post a New Job", "Find the right talent for your team"), BorderLayout.CENTER);
        quickActions.getBox(1).add(makeQuickAction("Browse Candidates", "Explore qualified professionals"), BorderLayout.CENTER);
        quickActions.getBox(2).add(makeQuickAction("View Analytics", "Track your hiring performance"), BorderLayout.CENTER);
        quickActions.getBox(3).add(makeQuickAction("Manage Company Profile", "Update your company information"), BorderLayout.CENTER);
        bottom.add(quickActions);

        addBlock(bottom, 0);
    }

    private JButton makeQuickAction(String title, String description) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());
        btn.setBackground(Color.decode("#f1f5fd"));
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(4, 15, 4, 15));
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        titleLabel.setForeground(Color.decode("#0d0d0d"));

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        descLabel.setForeground(Color.decode("#6b7280"));

        JPanel textBox = new JPanel();
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        textBox.setOpaque(false);
        textBox.add(Box.createVerticalGlue());
        textBox.add(titleLabel);
        textBox.add(descLabel);
        textBox.add(Box.createVerticalGlue());

        JLabel arrow = new JLabel(">");
        arrow.setFont(new Font("Arial", Font.BOLD, 16));

        btn.add(textBox, BorderLayout.CENTER);
        btn.add(arrow, BorderLayout.EAST);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}