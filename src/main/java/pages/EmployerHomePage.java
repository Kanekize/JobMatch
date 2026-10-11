package pages;

import components.SectionPanel;
import components.StatCard;
import database.Database;
import models.Applicant;
import models.Job;
import users.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class EmployerHomePage extends Page implements ActionListener {

    public EmployerHomePage(User user) {
        super(user);
        List<Job> jobs = Database.getJobsByEmployer(user.getEmail());
        jobs.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        int activeCount = 0;
        for (Job job : jobs) {
            if (job.getStatus().equals("Active")) activeCount++;
        }
        int totalApplicants = Database.countApplicantsByEmployer(user.getEmail());
        List<Applicant> recentApplicants = Database.getRecentApplicantsByEmployer(user.getEmail(), 3);

        JButton postJobBtn = makeBlueButton("Post a Job");
        postJobBtn.addActionListener(e -> getDashboard().showPage(new PostJobPage(getUser())));

        addBlock(makeHeading(getGreeting() + ", " + user.getDisplayName(),
                "Here's an overview of your hiring activity and recent updates", postJobBtn), 15);

        JPanel counters = new JPanel(new GridLayout(1, 4, 15, 0));
        counters.setOpaque(false);
        counters.add(new StatCard("Active Job Postings", String.valueOf(activeCount), Color.decode("#2563eb")));
        counters.add(new StatCard("Total Applications", String.valueOf(totalApplicants), Color.decode("#16a34a")));
        counters.add(new StatCard("Shortlisted Candidates", "0", Color.decode("#7c3aed")));
        counters.add(new StatCard("Interviews Scheduled", "0", Color.decode("#0d0d0d")));
        addBlock(counters, 20);

        // top 3 recent job post
        SectionPanel jobPostings = new SectionPanel("Your Job Postings", "View All Posting", 3, 72, false);
        jobPostings.getViewAllButton().addActionListener(e -> getDashboard().onMenuClick("Jobs"));
        for (int i = 0; i < 3 && i < jobs.size(); i++) {
            jobPostings.getBox(i).add(makeJobRow(jobs.get(i)), BorderLayout.CENTER);
        }
        addBlock(jobPostings, 20);

        JPanel bottom = new JPanel(new GridLayout(1, 2, 15, 0));
        bottom.setOpaque(false);

        // top 3 recent applicant
        SectionPanel recentApplicantsPanel = new SectionPanel("Recent Applicants", "View All Applicants", 3, 62, false);
        recentApplicantsPanel.getViewAllButton().addActionListener(e -> getDashboard().onMenuClick("Applicants"));
        for (int i = 0; i < 3 && i < recentApplicants.size(); i++) {
            recentApplicantsPanel.getBox(i).add(makeApplicantRow(recentApplicants.get(i)), BorderLayout.CENTER);
        }
        bottom.add(recentApplicantsPanel);

        SectionPanel quickActions = new SectionPanel("Quick Actions", "", 4, 50, false);

        JButton postNewJob = makeQuickAction("Post a New Job", "Find the right talent for your team");
        postNewJob.addActionListener(e -> getDashboard().showPage(new PostJobPage(getUser())));
        quickActions.getBox(0).add(postNewJob, BorderLayout.CENTER);

        JButton browseCandidates = makeQuickAction("Browse Candidates", "Explore qualified professionals");
        browseCandidates.addActionListener(e -> getDashboard().onMenuClick("Applicants"));
        quickActions.getBox(1).add(browseCandidates, BorderLayout.CENTER);

        quickActions.getBox(2).add(makeQuickAction("View Analytics", "Track your hiring performance"), BorderLayout.CENTER);
        quickActions.getBox(3).add(makeQuickAction("Manage Company Profile", "Update your company information"), BorderLayout.CENTER);
        bottom.add(quickActions);

        addBlock(bottom, 0);
    }

    private JPanel makeJobRow(Job job) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        row.add(EmployerJobsPage.makeLogoBox(job, 44), BorderLayout.WEST);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel title = new JLabel(job.getTitle());
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(Color.decode("#0d0d0d"));

        JLabel sub = new JLabel(job.getCity() + " | " + job.getJobType());
        sub.setFont(new Font("Arial", Font.PLAIN, 11));
        sub.setForeground(Color.decode("#6b7280"));

        center.add(Box.createVerticalGlue());
        center.add(title);
        center.add(sub);
        center.add(Box.createVerticalGlue());
        row.add(center, BorderLayout.CENTER);

        JLabel count = new JLabel(Database.countApplicants(job.getId()) + " applicants");
        count.setFont(new Font("Arial", Font.PLAIN, 11));
        count.setForeground(Color.decode("#2563eb"));
        row.add(count, BorderLayout.EAST);

        return row;
    }

    private JPanel makeApplicantRow(Applicant applicant) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        String initial = applicant.getName().substring(0, 1).toUpperCase();
        JLabel avatar = new JLabel(initial, SwingConstants.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(Color.decode("#cbd5e1"));
        avatar.setForeground(Color.decode("#12376b"));
        avatar.setFont(new Font("Arial", Font.BOLD, 16));
        avatar.setPreferredSize(new Dimension(40, 40));
        row.add(avatar, BorderLayout.WEST);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel name = new JLabel(applicant.getName());
        name.setFont(new Font("Arial", Font.BOLD, 13));
        name.setForeground(Color.decode("#0d0d0d"));

        JLabel details = new JLabel(applicant.getDetails());
        details.setFont(new Font("Arial", Font.PLAIN, 11));
        details.setForeground(Color.decode("#6b7280"));

        center.add(Box.createVerticalGlue());
        center.add(name);
        center.add(details);
        center.add(Box.createVerticalGlue());
        row.add(center, BorderLayout.CENTER);

        JLabel status = new JLabel(applicant.getStatus());
        status.setFont(new Font("Arial", Font.BOLD, 11));
        status.setForeground(statusColor(applicant.getStatus()));
        row.add(status, BorderLayout.EAST);

        return row;
    }

    private Color statusColor(String status) {
        if (status.equals("Rejected")) return Color.decode("#dc2626");
        if (status.equals("Accepted")) return Color.decode("#16a34a");
        if (status.equals("Interview")) return Color.decode("#7c3aed");
        return Color.decode("#2563eb");
    }

    private JButton makeBlueButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.decode("#2563eb"));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
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