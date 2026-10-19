package pages;

import models.JobApplication;
import database.Database;
import components.RoundedLineBorder;
import users.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

// applications ng job seeker. may tabs per status tapos list ng cards
public class ApplicationsPage extends Page {

    private String currentTab = "All";
    private List<JobApplication> applications;

    private JPanel tabsPanel;
    private JPanel listPanel;

    public ApplicationsPage(User user) {
        super(user);
        applications = loadApplications();

        addBlock(makeHeading("My Applications",
                "Track the status of every job you've applied to.", null), 15);

        tabsPanel = new JPanel(new GridLayout(1, 0, 10, 0));
        tabsPanel.setOpaque(false);
        tabsPanel.setPreferredSize(new Dimension(0, 38));
        tabsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        addBlock(tabsPanel, 15);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        addBlock(listPanel, 0);

        refresh();
    }

    private List<JobApplication> loadApplications() {
        return Database.getApplicationsForSeeker(getUser().getEmail());
    }

    private void refresh() {
        tabsPanel.removeAll();
        tabsPanel.add(makeTab("All"));
        for (String status : JobApplication.STATUSES) {
            tabsPanel.add(makeTab(status));
        }
        tabsPanel.revalidate();
        tabsPanel.repaint();

        listPanel.removeAll();
        int shown = 0;
        for (JobApplication app : applications) {
            if (currentTab.equals("All") || app.getStatus().equals(currentTab)) {
                JPanel card = makeApplicationCard(app);
                card.setAlignmentX(Component.LEFT_ALIGNMENT);
                listPanel.add(card);
                listPanel.add(Box.createVerticalStrut(6));
                shown++;
            }
        }
        if (shown == 0) {
            JLabel emptyLabel = new JLabel("No applications yet. Explore recommended jobs and apply to track them here.");
            emptyLabel.setFont(new Font("Inter", Font.PLAIN, 15));
            emptyLabel.setForeground(Color.decode("#6b7280"));
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5));
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            listPanel.add(emptyLabel);
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private int countFor(String tab) {
        if (tab.equals("All")) {
            return applications.size();
        }
        int count = 0;
        for (JobApplication app : applications) {
            if (app.getStatus().equals(tab)) count++;
        }
        return count;
    }

    // blue pag selected, white pag hindi (kapareho ng ibang buttons)
    private JButton makeTab(String name) {
        JButton tab = new JButton(name + " (" + countFor(name) + ")");
        tab.setFont(new Font("Inter", Font.BOLD, 12));
        tab.setMargin(new Insets(0, 0, 0, 0));
        tab.setFocusable(false);
        tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tab.setOpaque(true);
        if (name.equals(currentTab)) {
            tab.setBackground(Color.decode("#2563eb"));
            tab.setForeground(Color.WHITE);
            tab.setBorder(new RoundedLineBorder(Color.decode("#2563eb"), 10, 1));
        } else {
            tab.setBackground(Color.WHITE);
            tab.setForeground(Color.decode("#4b5563"));
            tab.setBorder(new RoundedLineBorder(Color.decode("#e5e9f0"), 10, 1));
        }
        tab.addActionListener(e -> {
            currentTab = name;
            refresh();
        });
        return tab;
    }

    private JPanel makeApplicationCard(JobApplication app) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#d7dce3"), 12, 1),
                BorderFactory.createEmptyBorder(15, 18, 15, 18)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        card.setPreferredSize(new Dimension(900, 112));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        card.add(makeLogoBox(app, 56), BorderLayout.WEST);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel titleLabel = new JLabel(app.getJobTitle());
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));
        titleLabel.setForeground(Color.decode("#0d0d0d"));

        JLabel companyLabel = new JLabel(app.getCompany());
        companyLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        companyLabel.setForeground(Color.decode("#4b5563"));

        JLabel locationLabel = new JLabel(app.getLocation());
        locationLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        locationLabel.setForeground(Color.decode("#6b7280"));

        center.add(Box.createVerticalGlue());
        center.add(titleLabel);
        center.add(companyLabel);
        center.add(locationLabel);
        center.add(Box.createVerticalGlue());
        card.add(center, BorderLayout.CENTER);

        JLabel statusLabel = new JLabel(app.getStatus());
        statusLabel.setFont(new Font("Inter", Font.BOLD, 13));
        statusLabel.setForeground(statusColor(app.getStatus()));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(statusBackground(app.getStatus()));
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(statusBackground(app.getStatus()), 18, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        statusLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel dateLabel = new JLabel(app.getDateText());
        dateLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        dateLabel.setForeground(Color.decode("#6b7280"));
        dateLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JPanel statusBox = new JPanel();
        statusBox.setLayout(new BoxLayout(statusBox, BoxLayout.Y_AXIS));
        statusBox.setOpaque(false);
        statusBox.add(Box.createVerticalGlue());
        statusBox.add(statusLabel);
        statusBox.add(Box.createVerticalStrut(6));
        statusBox.add(dateLabel);
        statusBox.add(Box.createVerticalGlue());

        JPanel right = new JPanel(new BorderLayout(18, 0));
        right.setOpaque(false);
        right.add(statusBox, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openJobPage();
            }
        });
        return card;
    }

    private void openJobPage() {
        getDashboard().showPage(new JobSeekerJobsPage(getUser(), null));
    }

    private Color statusBackground(String status) {
        if (status.equals("Rejected")) return Color.decode("#fee2e2");
        if (status.equals("Accepted")) return Color.decode("#dcfce7");
        if (status.equals("Interview")) return Color.decode("#ede9fe");
        if (status.equals("Applied")) return Color.decode("#ccfbf1");
        return Color.decode("#dbeafe");
    }

    private Color statusColor(String status) {
        if (status.equals("Rejected")) return Color.decode("#dc2626");
        if (status.equals("Accepted")) return Color.decode("#16a34a");
        if (status.equals("Interview")) return Color.decode("#7c3aed");
        if (status.equals("Applied")) return Color.decode("#0d9488");
        return Color.decode("#2563eb");
    }

    // kapareho ng makeLogoBox ng EmployerJobsPage pero para sa application.
    // letter ng company ung ilalagay pag walang logo
    private JPanel makeLogoBox(JobApplication app, int size) {
        JPanel box = new JPanel(new BorderLayout());
        box.setPreferredSize(new Dimension(size, size));
        box.setBorder(new RoundedLineBorder(Color.decode("#d7dce3"), 10, 1));
        box.setBackground(Color.decode("#f1f5fd"));

        if (app.getLogoPath() != null && !app.getLogoPath().isEmpty()) {
            ImageIcon icon = new ImageIcon(app.getLogoPath());
            Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            box.add(new JLabel(new ImageIcon(scaled)), BorderLayout.CENTER);
        } else {
            String letter = app.getCompany() == null || app.getCompany().isEmpty()
                    ? "?" : app.getCompany().substring(0, 1).toUpperCase();
            JLabel letterLabel = new JLabel(letter, SwingConstants.CENTER);
            letterLabel.setFont(new Font("Inter", Font.BOLD, size / 2));
            letterLabel.setForeground(Color.decode("#12376b"));
            box.add(letterLabel, BorderLayout.CENTER);
        }
        return box;
    }
}
