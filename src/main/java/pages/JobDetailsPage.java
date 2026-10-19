package pages;

import models.Job;
import database.Database;
import components.RoundedLineBorder;
import users.User;

import javax.swing.*;
import java.awt.*;
//job seeker view job
public class JobDetailsPage extends Page {

    public JobDetailsPage(User user, Job job) {
        super(user);

        JButton backLink = new JButton("< Back to Jobs");
        backLink.setFont(new Font("Inter", Font.PLAIN, 14));
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
                new RoundedLineBorder(Color.decode("#d7dce3"), 14, 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(EmployerJobsPage.makeLogoBox(job, 80), BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel titleLabel = new JLabel(job.getTitle());
        titleLabel.setFont(new Font("Inter", Font.BOLD, 26));
        titleLabel.setForeground(Color.decode("#0d0d0d"));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoLabel = new JLabel(job.getCity() + ", " + job.getLocation()
                + "  |  " + job.getJobType() + "  |  " + job.getWorkplaceType());
        infoLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        infoLabel.setForeground(Color.decode("#4b5563"));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String date = job.getCreatedAt() == null ? "" : job.getCreatedAt().split(" ")[0];
        JLabel dateLabel = new JLabel("Posted on " + date);
        dateLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        dateLabel.setForeground(Color.decode("#6b7280"));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel interactiveStarsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        interactiveStarsRow.setOpaque(false);
        interactiveStarsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel[] starButtons = new JLabel[5];
        int currentRating = Database.getIndividualJobRating(job.getId(), user.getEmail());

        java.io.File starImgFile = new java.io.File("src/images/star.png");
        if (!starImgFile.exists()) starImgFile = new java.io.File("JobMatch/src/images/star.png");
        for (int i = 0; i < 5; i++) {
            final int indexPosition = i + 1;
            starButtons[i] = new JLabel();
            starButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (starImgFile.exists()) {
                Image nativeImg = new ImageIcon(starImgFile.getPath()).getImage();
                Image scaledImg = nativeImg.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                ImageIcon activeStarIcon = new ImageIcon(scaledImg);
                starButtons[i].setIcon(activeStarIcon);
                starButtons[i].setEnabled(indexPosition <= currentRating);
            } else {
                starButtons[i].setText((indexPosition <= currentRating) ? "★" : "☆");
                starButtons[i].setFont(new Font("Inter", Font.BOLD, 22));
                starButtons[i].setForeground(Color.decode("#f59e0b"));
            }
            java.io.File finalStarImgFile = starImgFile;
            starButtons[i].addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (Database.saveJobRating(job.getId(), user.getEmail(), indexPosition)) {
                        int activeSelectionScore = Database.getIndividualJobRating(job.getId(), user.getEmail());
                        for (int k = 0; k < 5; k++) {
                            if (finalStarImgFile.exists()) {
                                starButtons[k].setEnabled(k < activeSelectionScore);
                            } else {
                                starButtons[k].setText((k < activeSelectionScore) ? "★" : "☆");
                            }
                        }
                        JOptionPane.showMessageDialog(null, "Thank you! You rated this job position " + indexPosition + " stars.", "Rating Submitted", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
            interactiveStarsRow.add(starButtons[i]);
        }

        info.add(titleLabel);
        info.add(Box.createVerticalStrut(4));
        info.add(infoLabel);
        info.add(Box.createVerticalStrut(4));
        info.add(dateLabel);
        info.add(Box.createVerticalStrut(4));
        info.add(interactiveStarsRow);
        header.add(info, BorderLayout.CENTER);

        JButton applyBtn = new JButton("Apply Now");
        applyBtn.setBackground(Color.decode("#2563eb"));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setFont(new Font("Inter", Font.BOLD, 14));
        applyBtn.setPreferredSize(new Dimension(130, 40));
        applyBtn.setBorderPainted(false);
        applyBtn.setOpaque(true);
        applyBtn.setFocusable(false);
        applyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boolean alreadyApplied = Database.hasApplied(getUser().getEmail(), job.getId());
        applyBtn.setText(alreadyApplied ? "Applied" : "Apply Now");
        applyBtn.setEnabled(!alreadyApplied);
        applyBtn.addActionListener(e -> {
            if (Database.applyToJob(getUser().getEmail(), job.getId())) {
                JOptionPane.showMessageDialog(this, "Your application has been submitted successfully.", "Application Submitted", JOptionPane.INFORMATION_MESSAGE);
                applyBtn.setText("Applied");
                applyBtn.setEnabled(false);
            } else if (Database.hasApplied(getUser().getEmail(), job.getId())) {
                JOptionPane.showMessageDialog(this, "You have already applied for this job.");
                applyBtn.setText("Applied"); applyBtn.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(this, "Could not submit your application. Please try again.", "Application Error", JOptionPane.ERROR_MESSAGE);
            }
        });
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
                new RoundedLineBorder(Color.decode("#d7dce3"), 12, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));
        titleLabel.setForeground(Color.decode("#0d0d0d"));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(8));

        JTextArea area = new JTextArea(text);
        area.setFont(new Font("Inter", Font.PLAIN, 13));
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
                new RoundedLineBorder(Color.decode("#d7dce3"), 12, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("Job Details");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));
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
        labelL.setFont(new Font("Inter", Font.BOLD, 13));
        labelL.setForeground(Color.decode("#0d0d0d"));
        labelL.setPreferredSize(new Dimension(180, 20));

        JLabel valueL = new JLabel(value);
        valueL.setFont(new Font("Inter", Font.PLAIN, 13));
        valueL.setForeground(Color.decode("#4b5563"));

        row.add(labelL, BorderLayout.WEST);
        row.add(valueL, BorderLayout.CENTER);
        return row;
    }
}