package pages;

import database.Database;
import models.Applicant;
import models.Job;
import models.SeekerProfile;
import components.RoundedLineBorder;
import users.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

// view resume/profile -> kopya ng ProfilePage pero walang edit
public class ViewProfilePage extends Page {
    private SeekerProfile profile;
    private Applicant applicant;
    private Job job;
    private Runnable onBackAction;
    private dashboards.Dashboard dashboard;

    public ViewProfilePage(User currentUser, Applicant applicant, Job job, dashboards.Dashboard dashboard, Runnable onBackAction) {
        super(currentUser);
        this.applicant = applicant;
        this.job = job;
        this.dashboard = dashboard;
        this.onBackAction = onBackAction;

        // specify jobseek
        users.User targetSeeker = new users.JobSeeker("", "", applicant.getEmail());
        this.profile = SeekerProfile.forUser(targetSeeker);

        buildPageLayout();
    }

    private void buildPageLayout() {
        content.removeAll();

        JPanel navRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        navRow.setOpaque(false);
        JButton backLink = new JButton("< Back to Applicants List");
        backLink.setFont(new Font("Inter", Font.PLAIN, 14));
        backLink.setForeground(Color.decode("#0d0d0d"));
        backLink.setBorderPainted(false);
        backLink.setContentAreaFilled(false);
        backLink.setFocusable(false);
        backLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLink.setHorizontalAlignment(SwingConstants.LEFT);
        backLink.addActionListener(e -> {
            if (onBackAction != null) {
                onBackAction.run();
            }
        });
        navRow.add(backLink);
        addBlock(navRow, 10);

        addBlock(makeHeaderCard(), 15);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        addToColumn(left, makeAboutCard());
        addToColumn(left, makeSkillsCard());
        addToColumn(left, makeEducationCard());
        addToColumn(left, makeExperienceCard());
        addToColumn(left, makeCertificationsCard());
        addToColumn(left, makeInfoCard("Skill Details", skillRows, "#4b5563", "Take the assessment to see your skill details here."));
        addToColumn(left, makeInfoCard("Interests", interestRows, "#4b5563", "Take the assessment to see your interests here."));
        addToColumn(left, makeInfoCard("Needs & Support", needsRows(), "#4b5563", "Take the assessment to see your needs here."));

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);

        JComponent widthStrut = (JComponent) Box.createHorizontalStrut(280);
        widthStrut.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(widthStrut);

        addToColumn(right, makePreferenceCard());
        addToColumn(right, makeInfoCard("Work Preferences", workPreferenceRows, "#2563eb", "Take the assessment to see your work preferences here."));
        addToColumn(right, makeAssessmentCard());
        addToColumn(right, makeResumeCard());

        JPanel mainArea = new JPanel(new BorderLayout(20, 0));
        mainArea.setOpaque(false);
        mainArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainArea.add(left, BorderLayout.CENTER);

        JPanel rightWrap = new JPanel(new BorderLayout());
        rightWrap.setOpaque(false);
        rightWrap.add(right, BorderLayout.NORTH);
        mainArea.add(rightWrap, BorderLayout.EAST);

        addBlock(mainArea, 0);

        content.revalidate();
        content.repaint();
    }

    private void addToColumn(JPanel column, JPanel card) {
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        column.add(card);
        column.add(Box.createVerticalStrut(10));
    }

    private JPanel makeHeaderCard() {
        JPanel card = new JPanel(new BorderLayout(20, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#d7dce3"), 12, 1),
                BorderFactory.createEmptyBorder(20, 22, 20, 22)));

        String initial = applicant.getName().substring(0, 1).toUpperCase();
        JLabel avatar = new JLabel(initial, SwingConstants.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(Color.decode("#cbd5e1"));
        avatar.setForeground(Color.decode("#12376b"));
        avatar.setFont(new Font("Inter", Font.BOLD, 40));
        avatar.setPreferredSize(new Dimension(100, 100));
        card.add(avatar, BorderLayout.WEST);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel nameLabel = new JLabel(applicant.getName());
        nameLabel.setFont(new Font("Inter", Font.BOLD, 28));
        nameLabel.setForeground(Color.decode("#0d0d0d"));

        JLabel headlineLabel = new JLabel(profile.getHeadline().isBlank() ? "No headline yet" : profile.getHeadline());
        headlineLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        headlineLabel.setForeground(Color.decode("#4b5563"));

        JLabel locationLabel = new JLabel(profile.getLocation().isBlank() ? "No location yet" : profile.getLocation());
        locationLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        locationLabel.setForeground(Color.decode("#6b7280"));

        JLabel completionLabel = new JLabel("Profile Completion");
        completionLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        completionLabel.setForeground(Color.decode("#4b5563"));

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(profile.getCompletion());
        bar.setForeground(Color.decode("#2563eb"));
        bar.setBackground(Color.decode("#e5e9f0"));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(260, 10));

        JLabel percentLabel = new JLabel(profile.getCompletion() + "%");
        percentLabel.setFont(new Font("Inter", Font.BOLD, 12));
        percentLabel.setForeground(Color.decode("#2563eb"));

        JPanel progressRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        progressRow.setOpaque(false);
        progressRow.add(completionLabel);
        progressRow.add(bar);
        progressRow.add(percentLabel);

        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headlineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        center.add(Box.createVerticalGlue());
        center.add(nameLabel);
        center.add(Box.createVerticalStrut(4));
        center.add(headlineLabel);
        center.add(locationLabel);
        center.add(Box.createVerticalStrut(8));
        center.add(progressRow);
        center.add(Box.createVerticalGlue());
        card.add(center, BorderLayout.CENTER);

        return card;
    }

    private JPanel makeAboutCard() {
        JPanel card = makeCard("About");
        if (profile.getAbout().isBlank()) {
            card.add(makeHint("Tell employers a little about yourself."));
        } else {
            card.add(EmployerJobsPage.makeWrapText(profile.getAbout(), 13, Color.decode("#4b5563")));
        }
        return card;
    }

    private JPanel makeSkillsCard() {
        JPanel card = makeCard("Skills");
        JPanel grid = new JPanel(new GridLayout(0, 3, 8, 8));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String skill : profile.getSkills()) {
            JButton chip = new JButton(skill);
            chip.setFont(new Font("Inter", Font.PLAIN, 12));
            chip.setBackground(Color.decode("#e8f0fe"));
            chip.setForeground(Color.decode("#1d4ed8"));
            chip.setBorderPainted(false);
            chip.setOpaque(true);
            chip.setFocusable(false);
            grid.add(chip);
        }
        if (profile.getSkills().isEmpty()) {
            card.add(makeHint("No skills added yet."));
        } else {
            card.add(grid);
        }
        return card;
    }

    private JPanel makeEducationCard() {
        JPanel card = makeCard("Education");
        if (!profile.hasEducation()) {
            card.add(makeHint("No education added yet."));
            return card;
        }
        addLine(card, profile.getSchool(), 14, Font.BOLD, "#0d0d0d");
        addLine(card, profile.getDegree(), 12, Font.PLAIN, "#4b5563");
        addLine(card, profile.getYearLevel(), 12, Font.PLAIN, "#4b5563");
        addLine(card, profile.getSchoolPeriod(), 12, Font.PLAIN, "#6b7280");
        return card;
    }

    private JPanel makeExperienceCard() {
        JPanel card = makeCard("Work Experience");
        if (!profile.hasExperience()) {
            card.add(makeHint("No work experience added yet."));
            return card;
        }
        addLine(card, profile.getRole(), 14, Font.BOLD, "#0d0d0d");
        addLine(card, profile.getCompany(), 12, Font.PLAIN, "#4b5563");
        addLine(card, profile.getWorkPeriod(), 12, Font.PLAIN, "#6b7280");
        card.add(Box.createVerticalStrut(4));
        for (String detail : profile.getWorkDetails()) {
            card.add(EmployerJobsPage.makeWrapText("•  " + detail, 12, Color.decode("#4b5563")));
        }
        return card;
    }

    private JPanel makeCertificationsCard() {
        JPanel card = makeCard("Certifications");
        if (profile.getCertifications().isEmpty()) {
            card.add(makeHint("No certifications added yet."));
        }
        for (String cert : profile.getCertifications()) {
            card.add(EmployerJobsPage.makeWrapText("•  " + cert, 12, Color.decode("#4b5563")));
        }
        return card;
    }

    private JPanel makePreferenceCard() {
        JPanel card = makeCard("Job Preference");
        addPreference(card, "Preferred Roles", profile.getPreferredRoles());
        addPreference(card, "Preferred Industry", profile.getPreferredIndustry());
        addPreference(card, "Preferred Work Arrangement", profile.getPreferredArrangement());
        addPreference(card, "Preferred Employment Type", profile.getPreferredEmploymentType());
        return card;
    }

    private void addPreference(JPanel card, String label, String value) {
        card.add(makeLine(label, 12, Font.BOLD, "#0d0d0d"));
        card.add(EmployerJobsPage.makeWrapText(value.isBlank() ? "Not set" : value, 12, Color.decode("#2563eb")));
        card.add(Box.createVerticalStrut(6));
    }

    private JPanel makeAssessmentCard() {
        JPanel card = makeCard("Assessment Summary");
        if (profile.isAssessmentTaken()) {
            String done = "Assessment completed";
            if (!profile.getAssessmentDateText().isEmpty()) {
                done += " on " + profile.getAssessmentDateText();
            }
            card.add(makeHint(done + "."));
            if (!profile.get("profileType").isBlank()) {
                card.add(EmployerJobsPage.makeWrapText("Profile type: " + profile.get("profileType"), 12, Color.decode("#6b7280")));
            }
        } else {
            card.add(makeHint("You haven't taken the assessment yet."));
        }
        return card;
    }

    private JPanel makeResumeCard() {
        JPanel card = makeCard("Resume");
        boolean hasResume = !profile.getResumeName().isBlank();
        card.add(makeHint(hasResume ? profile.getResumeName() : "No resume uploaded yet."));
        return card;
    }

    private JPanel makeInfoCard(String title, String[][] rows, String valueColor, String hint) {
        JPanel card = makeCard(title);
        boolean anyAnswer = false;
        for (String[] row : rows) {
            String answer = profile.get(row[0]);
            if (answer.isBlank()) {
                continue;
            }
            anyAnswer = true;
            card.add(makeLine(row[1], 12, Font.BOLD, "#0d0d0d"));
            card.add(EmployerJobsPage.makeWrapText(answer, 12, Color.decode(valueColor)));
            card.add(Box.createVerticalStrut(6));
        }
        if (!anyAnswer) {
            card.add(makeHint(hint));
        }
        return card;
    }

    private static final String[][] skillRows = {
            {"strongestSkill", "Strongest Skill"},
            {"skillLevel", "Skill Level"},
            {"languages", "Languages"},
            {"experienceLevel", "Work Experience Level"}
    };

    private static final String[][] interestRows = {
            {"workTypes", "Types of Work I'm Interested In"},
            {"workEnjoy", "Work I Enjoy"},
            {"jobMotivators", "What Matters Most to Me"},
            {"customInterest", "Other Interests"}
    };

    private static final String[][] workPreferenceRows = {
            {"opportunity", "Looking For"},
            {"relatedOpportunities", "Open to Related Roles"},
            {"shift", "Preferred Shift"},
            {"hoursPerShift", "Hours Per Shift"},
            {"hoursPerWeek", "Hours Per Week"},
            {"workLocation", "Preferred Work Location"},
            {"scheduleDetails", "Schedule Details"}
    };

    private String[][] needsRows() {
        String type = profile.get("profileType");

        if (type.equals("Student")) {
            return new String[][]{
                    {"availability", "When I'm Available"},
                    {"classSchedule", "Work Must Fit My Class Schedule"},
                    {"educationLevel", "Education Level"},
                    {"studentSupport", "Support That Helps Me"}
            };
        } else if (type.startsWith("PWD") || type.contains("Disability")) {
            return new String[][]{
                    {"accommodations", "Workplace Support I Need"},
                    {"workEnvironment", "Work Environment"},
                    {"preferredTasks", "Tasks I Prefer to Avoid"},
                    {"accessibilityDetails", "Accessibility Details"},
                    {"otherAccommodation", "Additional Workplace Support"},
                    {"supportNeeded", "Other Support Needed"}
            };
        } else if (type.equals("Senior Citizen")) {
            return new String[][]{
                    {"seniorEmployment", "Employment Type"},
                    {"seniorSchedule", "Preferred Schedule"},
                    {"seniorWorkplace", "Workplace Preferences"},
                    {"seniorWorkload", "Workload"},
                    {"seniorSupport", "Support That Helps Me"},
                    {"seniorDetails", "Additional Preferences"}
            };
        }

        // placeholder regular applicant auto
        return new String[][]{
                {"regularPurpose", "What I'm Looking For"},
                {"desiredPosition", "Position I Want"},
                {"regularEnvironment", "Work Environment"},
                {"regularNeeds", "Other Workplace Needs"}
        };
    }

    // removed edit
    private JPanel makeCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#d7dce3"), 12, 1),
                BorderFactory.createEmptyBorder(15, 18, 15, 18)));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 15));
        titleLabel.setForeground(Color.decode("#0d0d0d"));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(titleLabel, BorderLayout.WEST);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        card.add(header);
        card.add(Box.createVerticalStrut(8));
        return card;
    }

    private void addLine(JPanel card, String text, int size, int style, String color) {
        if (!text.isBlank()) {
            card.add(makeLine(text, size, style, color));
        }
    }

    private JLabel makeLine(String text, int size, int style, String color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Inter", style, size));
        label.setForeground(Color.decode(color));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel makeHint(String text) {
        return makeLine(text, 12, Font.PLAIN, "#6b7280");
    }

}//end