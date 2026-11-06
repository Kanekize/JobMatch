package pages;

import components.SectionPanel;
import components.StatCard;
import database.Database;
import models.JobApplication;
import models.SeekerProfile;
import users.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JobSeekerHomePage extends Page {

    public JobSeekerHomePage(User user) {
        super(user);
        SeekerProfile profile = SeekerProfile.forUser(user);
        List<JobApplication> applications = Database.getApplicationsForSeeker(user.getEmail());

        int activeApplications = 0;
        for (JobApplication application : applications) {
            String status = application.getStatus();
            if (!"Rejected".equalsIgnoreCase(status) && !"Accepted".equalsIgnoreCase(status)) {
                activeApplications++;
            }
        }

        String assessmentStatus = profile.isAssessmentTaken() ? "Taken" : "Not Taken";

        addBlock(makeHeading(getGreeting() + ", " + user.getDisplayName(),
                "Here's what's happening with your JobMatch profile.", null), 15);

        JPanel counters = new JPanel(new GridLayout(1, 3, 15, 0));
        counters.setOpaque(false);

        StatCard profileCard = new StatCard("Profile Completion", profile.getCompletion() + "%", Color.decode("#0d0d0d"), "user-blue.png", Color.decode("#dbeafe"));
        profileCard.addActionListener(e -> getDashboard().showPage(new ProfilePage(getUser())));
        counters.add(profileCard);

        StatCard assessmentCard = new StatCard("Assessment", assessmentStatus, Color.decode("#16a34a"), "dashassessment.png", Color.decode("#dcfce7"));
        assessmentCard.addActionListener(e -> getDashboard().showPage(new AssessmentPage(getUser())));
        counters.add(assessmentCard);

        StatCard applicationsCard = new StatCard("Active Applications", String.valueOf(activeApplications), Color.decode("#2563eb"), "applicationlist.png", Color.decode("#dbeafe"));
        applicationsCard.addActionListener(e -> getDashboard().showPage(new ApplicationsPage(getUser())));
        counters.add(applicationsCard);
        addBlock(counters, 20);

        SectionPanel recommended = new SectionPanel("Recommended for You", "View all jobs >", 3, 72, false);
        recommended.setEmptyState("No recommendations yet.");
        recommended.getViewAllButton().addActionListener(e ->
                getDashboard().showPage(new JobSeekerJobsPage(getUser(), null)));
        addBlock(recommended, 20);

        List<models.InterviewSchedule> seekerSchedules = Database.getInterviewSchedulesForSeeker(user.getEmail());

        List<models.InterviewSchedule> activeFutureInterviews = new ArrayList<>();
        LocalDateTime currentSystemTime = LocalDateTime.now();
        DateTimeFormatter sqliteFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (models.InterviewSchedule schedule : seekerSchedules) {
            try {
                String rawDateText = schedule.getScheduledAt().trim();
                if (rawDateText.length() >= 19) {
                    rawDateText = rawDateText.substring(0, 19);
                }
                LocalDateTime interviewDateTime = LocalDateTime.parse(rawDateText, sqliteFormatter);

                if (interviewDateTime.isAfter(currentSystemTime)) {
                    activeFutureInterviews.add(schedule);
                }
            } catch (Exception ex) {
                System.err.println("Skipping malformed or past interview row mapping: " + ex.getMessage());
            }
        }

        int rowsToDisplayCount = Math.min(3, activeFutureInterviews.size());
        SectionPanel interviews = new SectionPanel("Upcoming Interview", "View all interviews >", rowsToDisplayCount, 72, false);
        interviews.getViewAllButton().addActionListener(e -> getDashboard().onMenuClick("Interviews"));

        if (activeFutureInterviews.isEmpty()) {
            interviews.setEmptyState("");
        } else {
            for (int i = 0; i < rowsToDisplayCount; i++) {
                models.InterviewSchedule upcoming = activeFutureInterviews.get(i);

                JPanel interviewRow = new JPanel(new BorderLayout(10, 0));
                interviewRow.setOpaque(false);
                interviewRow.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

                String initialLetter = upcoming.getCompany().substring(0, 1).toUpperCase();
                JLabel avatarBadge = new JLabel(initialLetter, SwingConstants.CENTER);
                avatarBadge.setOpaque(true);
                avatarBadge.setBackground(Color.decode("#fff7ed"));
                avatarBadge.setForeground(Color.decode("#ea580c"));
                avatarBadge.setFont(new Font("Inter", Font.BOLD, 16));
                avatarBadge.setPreferredSize(new Dimension(40, 40));
                interviewRow.add(avatarBadge, BorderLayout.WEST);

                JPanel textDetailsColumn = new JPanel();
                textDetailsColumn.setLayout(new BoxLayout(textDetailsColumn, BoxLayout.Y_AXIS));
                textDetailsColumn.setOpaque(false);

                JLabel jobTitleLabel = new JLabel(upcoming.getJobTitle() + " Interview");
                jobTitleLabel.setFont(new Font("Inter", Font.BOLD, 14));
                jobTitleLabel.setForeground(Color.decode("#0d0d0d"));

                JLabel scheduleTimeLabel = new JLabel("Date: " + upcoming.getScheduledAt() + "  |  " + upcoming.getCompany());
                scheduleTimeLabel.setFont(new Font("Inter", Font.PLAIN, 12));
                scheduleTimeLabel.setForeground(Color.decode("#4b5563"));

                textDetailsColumn.add(Box.createVerticalGlue());
                textDetailsColumn.add(jobTitleLabel);
                textDetailsColumn.add(Box.createVerticalStrut(2));
                textDetailsColumn.add(scheduleTimeLabel);
                textDetailsColumn.add(Box.createVerticalGlue());
                interviewRow.add(textDetailsColumn, BorderLayout.CENTER);

                JButton inspectBtn = new JButton("View Details");
                inspectBtn.setFont(new Font("Inter", Font.PLAIN, 11));
                inspectBtn.setFocusable(false);
                inspectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                inspectBtn.setBackground(Color.WHITE);
                inspectBtn.addActionListener(e -> {
                    String detailsMessage = "Job Title: " + upcoming.getJobTitle()
                            + "\nCompany: " + upcoming.getCompany()
                            + "\nJob Location: " + upcoming.getJobLocation()
                            + "\nDate & Time: " + upcoming.getScheduledAt()
                            + "\nInterview Mode: " + (upcoming.getInterviewMode() == null || upcoming.getInterviewMode().isBlank() ? "Not specified" : upcoming.getInterviewMode())
                            + "\nLocation/Link: " + (upcoming.getInterviewLocation() == null || upcoming.getInterviewLocation().isBlank() ? "Not specified" : upcoming.getInterviewLocation())
                            + "\nNotes: " + (upcoming.getNotes() == null || upcoming.getNotes().isBlank() ? "No additional notes" : upcoming.getNotes());

                    JOptionPane.showMessageDialog(this, detailsMessage, "Scheduled Appointment Card", JOptionPane.INFORMATION_MESSAGE);
                });

                JPanel btnWrapRow = new JPanel(new GridBagLayout());
                btnWrapRow.setOpaque(false);
                btnWrapRow.add(inspectBtn);
                interviewRow.add(btnWrapRow, BorderLayout.EAST);

                interviews.getBox(i).add(interviewRow, BorderLayout.CENTER);
            }
        }
        addBlock(interviews, 20);

        SectionPanel industries = new SectionPanel("Recommended Industries", "View all industries >", 3, 72, true);
        industries.setEmptyState("No industries yet.");
        industries.getViewAllButton().addActionListener(e -> getDashboard().onMenuClick("Recommendations"));
        addBlock(industries, 0);
    }
}
