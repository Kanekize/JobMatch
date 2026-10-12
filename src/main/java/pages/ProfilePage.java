package pages;

import models.SeekerProfile;
import components.RoundedLineBorder;
import users.User;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// profile ng job seeker. header card sa taas, tapos 2 columns:
// left = about, skills, education, work experience, certifications
// right = job preference, assessment summary, resume
public class ProfilePage extends Page {

    private SeekerProfile profile;

    public ProfilePage(User user) {
        super(user);
        profile = SeekerProfile.forUser(user);
        build();
    }

    // binubuo ulit ung page after ng bawat edit
    private void build() {
        content.removeAll();

        addBlock(makeHeaderCard(), 15);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        addToColumn(left, makeAboutCard());
        addToColumn(left, makeSkillsCard());
        addToColumn(left, makeEducationCard());
        addToColumn(left, makeExperienceCard());
        addToColumn(left, makeCertificationsCard());

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);
        // fixed width ng right column (kapareho ng preferences panel sa JobSeekerJobsPage).
        // left aligned din dapat ung strut, kundi mali ang computation ng BoxLayout sa width
        JComponent widthStrut = (JComponent) Box.createHorizontalStrut(280);
        widthStrut.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(widthStrut);
        addToColumn(right, makePreferenceCard());
        addToColumn(right, makeAssessmentCard());
        addToColumn(right, makeResumeCard());

        JPanel mainArea = new JPanel(new BorderLayout(20, 0));
        mainArea.setOpaque(false);
        mainArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainArea.add(left, BorderLayout.CENTER);

        // para hindi mag-stretch ung right column pababa
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

    // ---------------------------------------------------------------- header

    private JPanel makeHeaderCard() {
        JPanel card = new JPanel(new BorderLayout(20, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#d7dce3"), 12, 1),
                BorderFactory.createEmptyBorder(20, 22, 20, 22)));

        String initial = getUser().getFirstName().substring(0, 1).toUpperCase();
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

        JLabel nameLabel = new JLabel(getUser().getFullName());
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

        JButton editBtn = makeBlueButton("Edit Profile");
        editBtn.addActionListener(e -> editHeader());
        JPanel btnWrap = new JPanel(new GridBagLayout());
        btnWrap.setOpaque(false);
        btnWrap.add(editBtn);
        card.add(btnWrap, BorderLayout.EAST);

        return card;
    }

    // ------------------------------------------------------------ left column

    private JPanel makeAboutCard() {
        JPanel card = makeCard("About", this::editAbout);
        if (profile.getAbout().isBlank()) {
            card.add(makeHint("Tell employers a little about yourself."));
        } else {
            card.add(EmployerJobsPage.makeWrapText(profile.getAbout(), 13, Color.decode("#4b5563")));
        }
        return card;
    }

    private JPanel makeSkillsCard() {
        JPanel card = makeCard("Skills", this::addSkill);

        // 3 per row, para nag-wrap kahit marami
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
            chip.setCursor(new Cursor(Cursor.HAND_CURSOR));
            chip.setToolTipText("Click to remove");
            chip.addActionListener(e -> removeSkill(skill));
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
        JPanel card = makeCard("Education", this::editEducation);
        if (!profile.hasEducation()) {
            card.add(makeHint("No education added yet."));
            return card;
        }
        card.add(makeLine(profile.getSchool(), 14, Font.BOLD, "#0d0d0d"));
        card.add(makeLine(profile.getDegree(), 12, Font.PLAIN, "#4b5563"));
        card.add(makeLine(profile.getYearLevel(), 12, Font.PLAIN, "#4b5563"));
        card.add(makeLine(profile.getSchoolPeriod(), 12, Font.PLAIN, "#6b7280"));
        return card;
    }

    private JPanel makeExperienceCard() {
        JPanel card = makeCard("Work Experience", this::editExperience);
        if (!profile.hasExperience()) {
            card.add(makeHint("No work experience added yet."));
            return card;
        }
        card.add(makeLine(profile.getRole(), 14, Font.BOLD, "#0d0d0d"));
        card.add(makeLine(profile.getCompany(), 12, Font.PLAIN, "#4b5563"));
        card.add(makeLine(profile.getWorkPeriod(), 12, Font.PLAIN, "#6b7280"));
        card.add(Box.createVerticalStrut(4));
        for (String detail : profile.getWorkDetails()) {
            card.add(EmployerJobsPage.makeWrapText("•  " + detail, 12, Color.decode("#4b5563")));
        }
        return card;
    }

    private JPanel makeCertificationsCard() {
        JPanel card = makeCard("Certifications", this::editCertifications);
        if (profile.getCertifications().isEmpty()) {
            card.add(makeHint("No certifications added yet."));
        }
        for (String cert : profile.getCertifications()) {
            card.add(EmployerJobsPage.makeWrapText("•  " + cert, 12, Color.decode("#4b5563")));
        }
        return card;
    }

    // ------------------------------------------------------------ right column

    private JPanel makePreferenceCard() {
        JPanel card = makeCard("Job Preference", this::editPreferences);
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
        JPanel card = makeCard("Assessment Summary", null);
        card.add(makeHint(profile.isAssessmentTaken() ? "Assessment completed." : "You haven't taken the assessment yet."));
        card.add(Box.createVerticalStrut(10));

        JButton viewBtn = makeWhiteButton("View");
        viewBtn.addActionListener(e -> viewAssessment());
        card.add(viewBtn);
        return card;
    }

    private JPanel makeResumeCard() {
        JPanel card = makeCard("Resume", null);
        boolean hasResume = !profile.getResumeName().isBlank();
        card.add(makeHint(hasResume ? profile.getResumeName() : "No resume uploaded yet."));
        card.add(Box.createVerticalStrut(10));

        JButton addBtn = makeWhiteButton(hasResume ? "Replace" : "+ Add");
        addBtn.addActionListener(e -> chooseResume());
        card.add(addBtn);
        return card;
    }

    // ----------------------------------------------------------------- actions

    private void editHeader() {
        JTextField headline = new JTextField(profile.getHeadline(), 28);
        JTextField location = new JTextField(profile.getLocation(), 28);
        if (showForm("Edit Profile", "Headline", headline, "Location", location)) {
            profile.setHeadline(headline.getText().trim());
            profile.setLocation(location.getText().trim());
            build();
        }
    }

    private void editAbout() {
        JTextArea area = new JTextArea(profile.getAbout(), 7, 40);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        if (showForm("Edit About", "About", new JScrollPane(area))) {
            profile.setAbout(area.getText().trim());
            build();
        }
    }

    private void addSkill() {
        String input = JOptionPane.showInputDialog(this, "Skill name:", "Add Skill", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.isBlank()) {
            return;
        }
        String skill = input.trim();
        for (String existing : profile.getSkills()) {
            if (existing.equalsIgnoreCase(skill)) {
                JOptionPane.showMessageDialog(this, "You already added \"" + existing + "\".");
                return;
            }
        }
        profile.getSkills().add(skill);
        build();
    }

    private void removeSkill(String skill) {
        int choice = JOptionPane.showConfirmDialog(this, "Remove \"" + skill + "\" from your skills?",
                "Remove Skill", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            profile.getSkills().remove(skill);
            build();
        }
    }

    private void editEducation() {
        JTextField school = new JTextField(profile.getSchool(), 28);
        JTextField degree = new JTextField(profile.getDegree(), 28);
        JTextField year = new JTextField(profile.getYearLevel(), 28);
        JTextField period = new JTextField(profile.getSchoolPeriod(), 28);
        if (showForm("Edit Education", "School", school, "Degree", degree,
                "Year level / status", year, "Period", period)) {
            profile.setSchool(school.getText().trim());
            profile.setDegree(degree.getText().trim());
            profile.setYearLevel(year.getText().trim());
            profile.setSchoolPeriod(period.getText().trim());
            build();
        }
    }

    private void editExperience() {
        JTextField role = new JTextField(profile.getRole(), 28);
        JTextField company = new JTextField(profile.getCompany(), 28);
        JTextField period = new JTextField(profile.getWorkPeriod(), 28);
        JTextArea details = new JTextArea(String.join("\n", profile.getWorkDetails()), 5, 28);
        if (showForm("Edit Work Experience", "Role", role, "Company", company, "Period", period,
                "What did you do? (one per line)", new JScrollPane(details))) {
            profile.setRole(role.getText().trim());
            profile.setCompany(company.getText().trim());
            profile.setWorkPeriod(period.getText().trim());
            profile.getWorkDetails().clear();
            profile.getWorkDetails().addAll(splitLines(details.getText()));
            build();
        }
    }

    private void editCertifications() {
        JTextArea area = new JTextArea(String.join("\n", profile.getCertifications()), 6, 28);
        if (showForm("Edit Certifications", "Certifications (one per line)", new JScrollPane(area))) {
            profile.getCertifications().clear();
            profile.getCertifications().addAll(splitLines(area.getText()));
            build();
        }
    }

    private void editPreferences() {
        JTextField roles = new JTextField(profile.getPreferredRoles(), 28);
        JTextField industry = new JTextField(profile.getPreferredIndustry(), 28);
        JTextField arrangement = new JTextField(profile.getPreferredArrangement(), 28);
        JTextField type = new JTextField(profile.getPreferredEmploymentType(), 28);
        if (showForm("Edit Job Preference", "Preferred Roles", roles, "Preferred Industry", industry,
                "Preferred Work Arrangement", arrangement, "Preferred Employment Type", type)) {
            profile.setPreferredRoles(roles.getText().trim());
            profile.setPreferredIndustry(industry.getText().trim());
            profile.setPreferredArrangement(arrangement.getText().trim());
            profile.setPreferredEmploymentType(type.getText().trim());
            build();
        }
    }

    private void viewAssessment() {
        if (!profile.isAssessmentTaken()) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "You haven't taken the assessment yet.\nGo to the Assessment page?",
                    "Assessment Summary", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                getDashboard().onMenuClick("Assessment");
            }
        }
        // TODO: ipakita ung summary pag may result na ang assessment
    }

    private void chooseResume() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose your resume");
        chooser.setFileFilter(new FileNameExtensionFilter("Resume (PDF, DOC, DOCX)", "pdf", "doc", "docx"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            profile.setResumeName(chooser.getSelectedFile().getName());
            build();
        }
    }

    // ----------------------------------------------------------------- helpers

    // white card na may title at "Edit" link sa taas. ung laman idadagdag sa returned panel
    private JPanel makeCard(String title, Runnable onEdit) {
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
        if (onEdit != null) {
            JButton editBtn = makeLinkButton("Edit");
            editBtn.addActionListener(e -> onEdit.run());
            header.add(editBtn, BorderLayout.EAST);
        }
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        card.add(header);
        card.add(Box.createVerticalStrut(8));
        return card;
    }

    // generic form dialog: label, component, label, component... true pag OK
    private boolean showForm(String title, Object... labelsAndFields) {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        for (int i = 0; i < labelsAndFields.length; i += 2) {
            JLabel label = new JLabel((String) labelsAndFields[i]);
            label.setFont(new Font("Inter", Font.BOLD, 12));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            JComponent field = (JComponent) labelsAndFields[i + 1];
            field.setAlignmentX(Component.LEFT_ALIGNMENT);
            form.add(label);
            form.add(Box.createVerticalStrut(3));
            form.add(field);
            form.add(Box.createVerticalStrut(10));
        }
        return JOptionPane.showConfirmDialog(this, form, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }

    private List<String> splitLines(String text) {
        List<String> lines = new ArrayList<>();
        for (String line : text.split("\\R")) {
            if (!line.isBlank()) {
                lines.add(line.trim());
            }
        }
        return lines;
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

    private JButton makeBlueButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.decode("#2563eb"));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Inter", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeWhiteButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Inter", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 34));
        btn.setMaximumSize(new Dimension(110, 34));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private JButton makeLinkButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Inter", Font.BOLD, 13));
        btn.setForeground(Color.decode("#2563eb"));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
