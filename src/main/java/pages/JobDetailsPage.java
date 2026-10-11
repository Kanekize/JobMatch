package pages;

import models.Job;
import users.User;

import javax.swing.*;
import java.awt.*;
//job seeker view job
public class JobDetailsPage extends Page {

    public JobDetailsPage(User user, Job job) {
        super(user);

        JButton backLink = new JButton("< Back to Jobs");
        backLink.setFont(new Font("Arial", Font.PLAIN, 14));
        backLink.setForeground(Color.decode("#0d0d0d"));
        backLink.setBorderPainted(false);
        backLink.setContentAreaFilled(false);
        backLink.setFocusable(false);
        backLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLink.addActionListener(e -> getDashboard().onMenuClick("Recommendations"));

        JPanel backWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backWrap.setOpaque(false);
        backWrap.add(backLink);
        addBlock(backWrap, 5);

        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#e5e9f0"), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(EmployerJobsPage.makeLogoBox(job, 80), BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel titleLabel = new JLabel(job.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.decode("#0d0d0d"));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoLabel = new JLabel(job.getCity() + ", " + job.getLocation()
                + "  |  " + job.getJobType() + "  |  " + job.getWorkplaceType());
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setForeground(Color.decode("#4b5563"));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String date = job.getCreatedAt() == null ? "" : job.getCreatedAt().split(" ")[0];
        JLabel dateLabel = new JLabel("Posted on " + date);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(Color.decode("#6b7280"));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(titleLabel);
        info.add(Box.createVerticalStrut(6));
        info.add(infoLabel);
        info.add(Box.createVerticalStrut(4));
        info.add(dateLabel);
        header.add(info, BorderLayout.CENTER);

        JButton applyBtn = new JButton("Apply Now");
        applyBtn.setBackground(Color.decode("#2563eb"));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setFont(new Font("Arial", Font.BOLD, 14));
        applyBtn.setPreferredSize(new Dimension(130, 40));
        applyBtn.setBorderPainted(false);
        applyBtn.setOpaque(true);
        applyBtn.setFocusable(false);
        applyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        applyBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "test"));
        header.add(applyBtn, BorderLayout.EAST);

        addBlock(header, 20);

        addBlock(makeSection("About the Job", job.getDescription()), 15);

        addBlock(makeDetailsBox(job), 15);

        addBlock(makeSection("Required Skills", job.getSkillsText()), 15);

        if (job.isAccommodationsAvailable()) {
            addBlock(makeSection("Workplace Accommodations",
                    String.join(", ", job.getAccommodations())), 15);
        }

        addBlock(makeSection("Who Can Apply", String.join(", ", job.getApplicantTypes())), 0);
    }

    private JPanel makeSection(String title, String text) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#e5e9f0"), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.decode("#0d0d0d"));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(8));

        JTextArea area = new JTextArea(text);
        area.setFont(new Font("Arial", Font.PLAIN, 13));
        area.setForeground(Color.decode("#4b5563"));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setFocusable(false);
        area.setOpaque(false);
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(area);

        return panel;
    }

    private JPanel makeDetailsBox(Job job) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#e5e9f0"), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("Job Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.decode("#0d0d0d"));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));

        panel.add(detailRow("Salary", job.getSalaryText() + " per month"));
        panel.add(detailRow("Category", job.getCategory()));
        panel.add(detailRow("Job Type", job.getJobType()));
        panel.add(detailRow("Workplace Type", job.getWorkplaceType()));
        panel.add(detailRow("Location", job.getCity() + ", " + job.getLocation()));
        panel.add(detailRow("Experience Required",
                job.isExperienceRequired() ? "Yes (" + job.getExperienceLength() + ")" : "No"));
        panel.add(detailRow("Workplace Accommodations",
                job.isAccommodationsAvailable() ? "Yes" : "No"));

        return panel;
    }

    private JPanel detailRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel labelL = new JLabel(label);
        labelL.setFont(new Font("Arial", Font.BOLD, 13));
        labelL.setForeground(Color.decode("#0d0d0d"));
        labelL.setPreferredSize(new Dimension(180, 20));

        JLabel valueL = new JLabel(value);
        valueL.setFont(new Font("Arial", Font.PLAIN, 13));
        valueL.setForeground(Color.decode("#4b5563"));

        row.add(labelL, BorderLayout.WEST);
        row.add(valueL, BorderLayout.CENTER);
        return row;
    }
}