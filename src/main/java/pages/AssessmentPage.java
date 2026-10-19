package pages;
import components.RoundedLineBorder;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.Desktop;
import java.io.File;
import java.util.*;
import java.util.List;
import models.SeekerProfile;
import users.User;

public class AssessmentPage extends Page {
    private final CardLayout cards = new CardLayout();
    private final JPanel pages = new JPanel(cards);
    private final JPanel progress = new JPanel(new BorderLayout());
    private final Map<String, JComponent> fields = new LinkedHashMap<>();
    private final Map<String, String> answers = new LinkedHashMap<>();
    private final String[] steps = {"Profile Type", "Career Goals", "Skills", "Interest", "Preferences", "Needs", "Review"};
    private final Color blue = new Color(31, 101, 238);
    private final Color paleBlue = new Color(235, 243, 255);
    private final Color ink = new Color(25, 31, 42);
    private final Color muted = new Color(105, 112, 125);
    private int step = 0;
    private String userType = "Student";
    private File resumeFile;
    private JLabel resumeLabel;
    private final ButtonGroup typeGroup = new ButtonGroup();
    private final Map<String, JRadioButton> typeButtons = new LinkedHashMap<>();
    private final Map<String, JPanel> reviewCards = new LinkedHashMap<>();
    private JPanel needsContent;
    private JPanel reviewContent;
    private JPanel profileGrid;
    private JLabel resumePreviewBox;
    private JPanel certUploadSection;
    private List<File> uploadedCertFiles = new ArrayList<>();
    private JPanel certThumbsContainer;
    private JRadioButton certYesRadio, certNoRadio;
    private JPanel resumePanel;
    private JLabel resumeFileNameLabel;
    private JLabel resumeMetaLabel;
    private JPanel resumeActionsPanel;
    private JButton uploadButton;
    private final Map<String, JLabel> typeIconLabels = new LinkedHashMap<>();
    private boolean skipSaveOnNextShowStep = false;

    public AssessmentPage(User user) {
        super(user);
        this.setLayout(new BorderLayout());
        this.setOpaque(false);

        JPanel main = new JPanel(new BorderLayout(0, 12));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(22, 28, 22, 28));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("JobMatch Assessment");
        title.setFont(new Font("Arial", Font.BOLD, 28));

        JLabel subtitle = new JLabel("Helps us understand your skills, interests, preferences, and needs so we can identify opportunities that better match your profile");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(new Color(95, 101, 112));

        heading.add(title);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitle);

        progress.setOpaque(false);
        progress.setBorder(new EmptyBorder(5, 0, 0, 0));
        pages.setOpaque(false);

        buildProfilePage();
        buildCareerPage();
        buildSkillsPage();
        buildInterestPage();
        buildPreferencesPage();
        buildNeedsPage();
        buildReviewPage();

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(progress, BorderLayout.NORTH);
        center.add(pages, BorderLayout.CENTER);

        main.add(heading, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER);

        this.add(main, BorderLayout.CENTER);

        updateProgress();
        initializeAssessmentEntry();
    }

    private JPanel makePage(String title, String description, String key, JPanel content) {
        JPanel outer = new JPanel(new BorderLayout(0, 12));
        outer.setBackground(Color.WHITE);
        outer.setBorder(new CompoundBorder(
                new LineBorder(new Color(218, 222, 230), 1, true),
                new EmptyBorder(20, 22, 16, 22)
        ));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel h = new JLabel(title);
        h.setFont(new Font("Arial", Font.BOLD, 21));
        h.setForeground(ink);
        heading.add(h);

        if (description != null && !description.isBlank()) {
            JLabel d = new JLabel(description);
            d.setFont(new Font("Arial", Font.PLAIN, 12));
            d.setForeground(muted);
            heading.add(Box.createVerticalStrut(5));
            heading.add(d);
        }

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        outer.add(heading, BorderLayout.NORTH);
        outer.add(scroll, BorderLayout.CENTER);
        outer.add(navButtons(), BorderLayout.SOUTH);
        pages.add(outer, key);

        return outer;
    }

    private JPanel content() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    private JPanel navButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        p.setOpaque(false);

        JButton back = new JButton("Back");
        JButton next = new JButton("Continue");

        styleNavButton(back, false);
        styleNavButton(next, true);

        back.addActionListener(e -> showStep(Math.max(0, step - 1)));

        next.addActionListener(e -> {
            if (!validateStep()) {
                return;
            }

            saveAnswers();

            if (step < 6) {
                showStep(step + 1);
            } else {
                if (saveAssessmentToDatabase()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Your assessment is complete. You can now see your answers in your profile.",
                            "JobMatch Assessment",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    getDashboard().onMenuClick("Profile");
                }
            }
        });

        p.add(back);
        p.add(next);

        return p;
    }

    private void styleNavButton(JButton button, boolean primary) {
        styleButton(button, primary);
        button.setBorder(new CompoundBorder(
                new RoundedLineBorder(primary ? blue : new Color(210, 215, 225), 18, 1),
                new EmptyBorder(9, 22, 9, 22)
        ));
    }

    private void styleButton(JButton b, boolean primary) {
        b.setFont(new Font("Arial", Font.PLAIN, 12));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new CompoundBorder(
                new RoundedLineBorder(primary ? blue : new Color(210, 215, 225), 12, 1),
                new EmptyBorder(9, 22, 9, 22)
        ));
        b.setOpaque(true);
        b.setBackground(primary ? blue : new Color(235, 237, 241));
        b.setForeground(primary ? Color.WHITE : ink);
    }

    private JPanel buildProfilePage() {
        JPanel c = content();

        resumePanel = new JPanel(new BorderLayout(12, 0));
        resumePanel.setOpaque(true);
        resumePanel.setBackground(paleBlue);
        resumePanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(198, 216, 250), 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        resumePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 112));

        JPanel resumeText = new JPanel();
        resumeText.setOpaque(false);
        resumeText.setLayout(new BoxLayout(resumeText, BoxLayout.Y_AXIS));

        JLabel resumeTitle = new JLabel("Have a resume?");
        resumeTitle.setFont(new Font("Arial", Font.BOLD, 14));
        resumeTitle.setForeground(ink);
        resumeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel resumeDescription = new JLabel("<html>Upload your resume if available, or skip and continue the assessment.</html>");
        resumeDescription.setFont(new Font("Arial", Font.PLAIN, 12));
        resumeDescription.setForeground(muted);
        resumeDescription.setAlignmentX(Component.LEFT_ALIGNMENT);

        resumeFileNameLabel = new JLabel("No resume selected");
        resumeFileNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resumeFileNameLabel.setForeground(ink);
        resumeFileNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        resumeMetaLabel = new JLabel("Upload PDF, DOC, or DOCX");
        resumeMetaLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        resumeMetaLabel.setForeground(muted);
        resumeMetaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        resumeText.add(resumeTitle);
        resumeText.add(Box.createVerticalStrut(4));
        resumeText.add(resumeDescription);
        resumeText.add(Box.createVerticalStrut(4));
        resumeText.add(resumeFileNameLabel);
        resumeText.add(Box.createVerticalStrut(3));
        resumeText.add(resumeMetaLabel);

        uploadButton = new JButton("Upload Resume");
        styleButton(uploadButton, true);
        uploadButton.setPreferredSize(new Dimension(132, 38));
        uploadButton.setMaximumSize(new Dimension(132, 38));
        uploadButton.addActionListener(e -> chooseResume());

        JPanel uploadButtonPanel = new JPanel(new GridBagLayout());
        uploadButtonPanel.setOpaque(false);
        uploadButtonPanel.add(uploadButton);

        resumeActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        resumeActionsPanel.setOpaque(false);
        JButton viewBtn = linkButton("View");
        JButton replaceBtn = linkButton("Replace");
        JButton deleteBtn = linkButton("Delete");
        viewBtn.addActionListener(e -> viewResume());
        replaceBtn.addActionListener(e -> chooseResume());
        deleteBtn.addActionListener(e -> clearResume());
        resumeActionsPanel.add(viewBtn);
        resumeActionsPanel.add(replaceBtn);
        resumeActionsPanel.add(deleteBtn);

        JPanel resumeRight = new JPanel();
        resumeRight.setOpaque(false);
        resumeRight.setLayout(new BoxLayout(resumeRight, BoxLayout.Y_AXIS));
        uploadButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resumeActionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resumeRight.add(uploadButtonPanel);
        resumeRight.add(Box.createVerticalStrut(6));
        resumeRight.add(resumeActionsPanel);

        resumePanel.add(resumeText, BorderLayout.CENTER);
        resumePanel.add(resumeRight, BorderLayout.EAST);

        c.add(resumePanel);
        c.add(Box.createVerticalStrut(10));

        profileGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        profileGrid.setOpaque(false);
profileGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));        addTypeCard(profileGrid, "Student", "Currently studying or looking for internships and flexible opportunities.", "graduate-hat.png", "graduate-hat-active.png", "🎓");
        addTypeCard(profileGrid, "Regular Applicant", "Looking for employment based on your skills and professional goals.", "suitcase.png", "suitcase-active.png", "▣");
        addTypeCard(profileGrid, "Senior Citizen", "Looking for opportunities that fit your experience and preferred working conditions.", "person.png", "person-active.png", "♙");
        addTypeCard(profileGrid, "PWD (Person with Disability)", "Looking for opportunities that consider your skills and workplace accessibility needs.", "disabled.png", "disableda-active.png", "♿");
        c.add(profileGrid);
        updateResumeCardState();

        return makePage("Which best describes you?", "Choose the profile that fits you best.", "0", c);
    }

    private void chooseResume() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose Your Resume");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Resume Files (PDF, DOC, DOCX, PNG, JPG)", "pdf", "doc", "docx", "png", "jpg", "jpeg"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            resumeFile = chooser.getSelectedFile();
            answers.put("Resume File", resumeFile.getName());
            updateResumeCardState();
        }
    }

    private void addTypeCard(JPanel grid, String type, String desc, String inactiveIconFile, String activeIconFile, String fallbackSymbol) {
        JRadioButton radio = new JRadioButton();
        radio.setOpaque(false);

        typeGroup.add(radio);
        typeButtons.put(type, radio);
        radio.setSelected(type.equals(userType));

        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setPreferredSize(new Dimension(0, 118));
        card.setMinimumSize(new Dimension(0, 118));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 118));

        JLabel icon = new JLabel();
        icon.setFont(new Font("Arial", Font.PLAIN, 23));
        icon.putClientProperty("inactiveIconFile", inactiveIconFile);
        icon.putClientProperty("activeIconFile", activeIconFile);
        icon.putClientProperty("fallbackSymbol", fallbackSymbol);
        typeIconLabels.put(type, icon);
        updateTypeCardIcon(icon, radio.isSelected());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(icon, BorderLayout.WEST);
        top.add(radio, BorderLayout.EAST);

        JLabel name = new JLabel(type);
        name.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel description = new JLabel("<html>" + desc + "</html>");
        description.setFont(new Font("Arial", Font.PLAIN, 11));
        description.setForeground(muted);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(name);
        text.add(Box.createVerticalStrut(5));
        text.add(description);

        card.add(top, BorderLayout.NORTH);
        card.add(text, BorderLayout.CENTER);

        radio.addActionListener(e -> selectType(type));

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                radio.doClick();
            }
        });

        grid.add(card);
        styleTypeCards();
    }

    private void selectType(String type) {
        userType = type;

        for (Map.Entry<String, JRadioButton> e : typeButtons.entrySet()) {
            e.getValue().setSelected(e.getKey().equals(type));
        }

        styleTypeCards();
        rebuildNeeds();
    }

    private void styleTypeCards() {
        if (profileGrid == null) {
            return;
        }

        Component[] list = profileGrid.getComponents();
        String[] types = {
                "Student",
                "Regular Applicant",
                "Senior Citizen",
                "PWD (Person with Disability)"
        };

        for (int i = 0; i < list.length; i++) {
            JPanel card = (JPanel) list[i];
            boolean selected = types[i].equals(userType);
            JLabel iconLabel = typeIconLabels.get(types[i]);
            if (iconLabel != null) {
                updateTypeCardIcon(iconLabel, selected);
            }

            card.setBackground(selected ? paleBlue : Color.WHITE);
            card.setOpaque(true);
            card.setBorder(new CompoundBorder(
                    new LineBorder(
                            selected ? blue : new Color(190, 196, 207),
                            1,
                            true
                    ),
                    new EmptyBorder(12, 13, 12, 13)
            ));
        }
    }

    private JPanel buildCareerPage() {
        JPanel c = content();

        c.add(label("1. What type of opportunity are you currently looking for? Choose one."));
        c.add(choices("opportunity", new String[]{
                "Full-time", "Part-time", "Internship", "Freelance / Contract", "Open to Any"
        }, 3));

        c.add(gap());
        c.add(label("2. Which industries interest you? Select all that apply."));
        c.add(checks("industries", new String[]{
                "Information Technology", "Business Administration", "Accounting & Finance",
                "Customer Service", "Sales & Marketing", "Creative & Design", "Education",
                "Hospitality", "Healthcare & Caregiving", "Retail & Cashiering",
                "Skilled & Technical Work", "Transportation & Delivery", "Other"
        }, 3));

        c.add(gap());
        c.add(label("3. Which job roles are you interested in? Choose a suggestion or type your own."));
        c.add(combo("jobRoles", new String[]{
                "Software Developer", "Web Developer", "IT Support", "Data Entry",
                "Customer Service Representative", "Office Staff", "Sales Associate",
                "Graphic Designer", "Teacher", "Administrative Assistant", "Other"
        }));

        c.add(gap());
        c.add(label("4. Are you open to related opportunities that strongly match your skills?"));
        c.add(choices("relatedOpportunities", new String[]{"Yes", "Maybe", "No"}, 3));

        return makePage(
                "Tell us what you're looking for",
                "Choose your career goals and the types of opportunities you would consider.",
                "1",
                c
        );
    }

    private JPanel buildSkillsPage() {
        JPanel c = content();

        c.add(label("1. What skills do you have? Select all that apply."));
        c.add(checks("skills", new String[]{
                "Communication", "Customer Service", "Computer Skills", "Microsoft Office",
                "Sales", "Leadership", "Organization", "Problem Solving", "Teamwork",
                "Time Management", "Programming", "Data Entry", "Writing", "Design", "Other"
        }, 3));

        c.add(gap());
        c.add(label("2. What is your strongest skill?"));
        c.add(combo("strongestSkill", new String[]{
                "Communication", "Customer Service", "Computer Skills", "Microsoft Office",
                "Sales", "Leadership", "Organization", "Problem Solving", "Teamwork",
                "Time Management", "Programming", "Data Entry", "Writing", "Design", "Other"
        }));

        c.add(gap());
        c.add(label("3. How would you rate your current skill level?"));
        c.add(choices("skillLevel", new String[]{"Beginner", "Intermediate", "Advanced"}, 3));

        c.add(gap());
        c.add(label("4. Which languages can you speak? Select all that apply."));
        c.add(checks("languages", new String[]{
                "English", "Filipino", "Spanish", "Chinese", "Japanese", "Korean",
                "French", "German", "Arabic", "Other Philippine Language", "Other"
        }, 3));

        c.add(gap());
        c.add(label("5. What is your most recent job or role? Choose a suggestion or type your own."));
        c.add(combo("recentJob", new String[]{
                "No previous job", "Cashier", "Sales Associate",
                "Customer Service Representative", "Office Staff", "Teacher",
                "Administrative Assistant", "Service Crew", "Driver", "Freelancer",
                "Manager / Supervisor", "Other"
        }));

        c.add(gap());
        c.add(label("6. How much work experience do you have?"));
        c.add(choices("experience", new String[]{
                "No experience", "Less than 2 years", "2–4 years", "5+ years"
        }, 4));

        c.add(gap());
        c.add(label("7. Do you have any certificates, training, or other qualifications?"));

        JPanel radioWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        radioWrapper.setOpaque(false);
        certYesRadio = new JRadioButton("Yes");
        certNoRadio = new JRadioButton("No");
        certNoRadio.setSelected(true);
        ButtonGroup certGroup = new ButtonGroup();
        certGroup.add(certYesRadio);
        certGroup.add(certNoRadio);
        radioWrapper.add(certYesRadio);
        radioWrapper.add(certNoRadio);
        c.add(radioWrapper);

        certUploadSection = new JPanel();
        certUploadSection.setLayout(new BoxLayout(certUploadSection, BoxLayout.Y_AXIS));
        certUploadSection.setOpaque(false);
        certUploadSection.setVisible(false);

        certUploadSection.add(Box.createVerticalStrut(6));
        JTextField certText = text("qualifications", "Specify certificate or designation names");
        certUploadSection.add(certText);
        certUploadSection.add(Box.createVerticalStrut(6));

        certThumbsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        certThumbsContainer.setBackground(Color.WHITE);
        JScrollPane thumbScroll = new JScrollPane(certThumbsContainer);
        thumbScroll.setPreferredSize(new Dimension(400, 70));
        thumbScroll.setBorder(new LineBorder(new Color(213, 218, 227), 1, true));
        certUploadSection.add(thumbScroll);
        certUploadSection.add(Box.createVerticalStrut(6));

        JButton addImgBtn = new JButton("+ Upload Certificate Image");
        styleButton(addImgBtn, false);
        addImgBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Certificate Image");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                uploadedCertFiles.add(file);

                ImageIcon imgIcon = new ImageIcon(file.getAbsolutePath());
                Image scaled = imgIcon.getImage().getScaledInstance(65, 45, Image.SCALE_SMOOTH);
                JLabel thumb = new JLabel(new ImageIcon(scaled));
                thumb.setBorder(new LineBorder(Color.GRAY, 1));
                certThumbsContainer.add(thumb);
                certThumbsContainer.revalidate();
                certThumbsContainer.repaint();
            }
        });
        certUploadSection.add(addImgBtn);
        c.add(certUploadSection);

        certYesRadio.addActionListener(e -> certUploadSection.setVisible(true));
        certNoRadio.addActionListener(e -> {
            certUploadSection.setVisible(false);
            uploadedCertFiles.clear();
            certThumbsContainer.removeAll();
            certText.setText("");
        });

        return makePage(
                "Tell us about your skills",
                "This helps us find roles that match what you can do.",
                "2",
                c
        );
    }

    private JPanel buildInterestPage() {
        JPanel c = content();

        c.add(label("1. What type of work are you interested in? Select all that apply."));
        c.add(checks("workTypes", new String[]{
                "Office & Administration", "Retail & Cashiering", "Sales & Marketing",
                "Customer Service", "Information Technology", "Design & Creative",
                "Education & Teaching", "Accounting & Finance", "Healthcare & Caregiving",
                "Food & Hospitality", "Skilled & Technical Work", "Transportation & Delivery",
                "Production & Manufacturing", "Cleaning & Maintenance", "Agriculture",
                "Government & Public Service", "Other"
        }, 3));

        c.add(gap());
        c.add(label("2. What kind of work would you enjoy? Select all that apply."));
        c.add(checks("workEnjoy", new String[]{
                "Working with people", "Helping others", "Working with numbers",
                "Working with computers", "Organizing information",
                "Selling products or services", "Creating or designing things",
                "Teaching or mentoring", "Working with tools or equipment",
                "Working independently", "Working as part of a team", "Working outdoors",
                "Not sure", "Other"
        }, 2));

        c.add(gap());
        c.add(label("3. What matters most to you in a job? Select all that apply."));
        c.add(checks("jobMotivators", new String[]{
                "Stable income", "Flexible schedule", "Learning new skills", "Career growth",
                "Helping people", "Work-life balance", "Working close to home", "Remote work",
                "Supportive workplace", "Other"
        }, 3));

        c.add(gap());
        c.add(label("4. Add another interest or type of work (optional)."));
        c.add(text("customInterest", "Type your answer here"));

        return makePage(
                "What interests you?",
                "Select the kinds of work and workplace goals that fit you.",
                "3",
                c
        );
    }

    private JPanel buildPreferencesPage() {
        JPanel c = content();

        c.add(label("1. Desired employment type"));
        c.add(choices("employmentType", new String[]{
                "Full-time", "Part-time", "Project-based", "Internship", "Temporary", "No preference"
        }, 3));

        c.add(gap());
        c.add(label("2. Preferred work setup"));
        c.add(choices("workSetup", new String[]{
                "On-site", "Hybrid", "Remote / Online", "No preference"
        }, 4));

        c.add(gap());
        c.add(label("3. Preferred shift"));
        c.add(choices("shift", new String[]{
                "Day shift", "Night shift", "Flexible hours", "No preference"
        }, 4));

        c.add(gap());
        c.add(label("4. How many hours can you work in a shift?"));
        c.add(choices("hoursPerShift", new String[]{
                "2–3 hours", "4–6 hours", "7–8 hours", "9+ hours", "No preference"
        }, 3));

        c.add(gap());
        c.add(label("5. How many hours can you work per week?"));
        c.add(choices("hoursPerWeek", new String[]{
                "Under 10", "10–20", "21–30", "31–40", "Flexible"
        }, 3));

        c.add(gap());
        c.add(label("6. Where would you prefer to work?"));
        c.add(choices("location", new String[]{
                "Near my location", "Within my city", "Anywhere in the Philippines",
                "Remote / Online", "No preference"
        }, 3));

        c.add(gap());
        c.add(label("7. Do you need the schedule to fit around other responsibilities? (Optional)"));
        c.add(text("scheduleDetails", "For example, classes, caregiving, or another job"));

        return makePage(
                "Set your work preferences",
                "Choose the schedule and work setup that suits you.",
                "4",
                c
        );
    }

    private JPanel buildNeedsPage() {
        needsContent = content();
        rebuildNeeds();

        return makePage(
                "Tell us what you need",
                "These answers help us suggest opportunities that fit your situation. Optional details can be left blank.",
                "5",
                needsContent
        );
    }

    private void rebuildNeeds() {
        if (needsContent == null) {
            return;
        }

        String[] keys = {
                "availability", "classSchedule", "educationLevel", "studentSupport",
                "accommodations", "workEnvironment", "otherAccommodation",
                "accessibilityDetails", "supportNeeded", "preferredTasks",
                "seniorEmployment", "seniorSchedule", "seniorWorkplace",
                "seniorWorkload", "seniorSupport", "seniorDetails",
                "regularPurpose", "desiredPosition", "regularEnvironment", "regularNeeds"
        };

        for (String key : keys) {
            fields.remove(key);
            answers.remove(key);
        }

        needsContent.removeAll();

        if (userType.equals("Student")) {
            needsContent.add(label("1. When are you usually available? Select all that apply."));
            needsContent.add(checks("availability", new String[]{
                    "Weekdays", "Weekends", "After classes", "Morning", "Afternoon",
                    "Evening", "Flexible"
            }, 3));

            needsContent.add(gap());
            needsContent.add(label("2. Should opportunities fit around your class schedule?"));
            needsContent.add(choices("classSchedule", new String[]{
                    "Yes", "No", "No preference"
            }, 3));

            needsContent.add(gap());
            needsContent.add(label("3. What is your current education level?"));
            needsContent.add(choices("educationLevel", new String[]{
                    "Senior high school", "College student", "Graduate", "Other"
            }, 4));

            needsContent.add(gap());
            needsContent.add(label("4. What support would help you get started? Select all that apply."));
            needsContent.add(checks("studentSupport", new String[]{
                    "Training provided", "Internship experience", "Flexible schedule",
                    "Entry-level role", "Mentorship", "No preference", "Other"
            }, 3));

        } else if (userType.equals("PWD (Person with Disability)")) {
            needsContent.add(label("1. What workplace support or adjustments would help you work comfortably? Select all that apply."));
            needsContent.add(checks("accommodations", new String[]{
                    "Wheelchair-accessible workplace", "Accessible transportation / location",
                    "Screen reader or assistive technology", "Ergonomic workstation",
                    "Flexible working hours", "Additional rest breaks", "Remote / Online work",
                    "Accessible facilities", "Written instructions", "No specific adjustment", "Other"
            }, 2));

            needsContent.add(gap());
            needsContent.add(label("2. What type of work environment do you prefer?"));
            needsContent.add(choices("workEnvironment", new String[]{
                    "Independent work", "Team-based work", "Quiet environment",
                    "Customer-facing environment", "Flexible environment", "No preference"
            }, 3));

            needsContent.add(gap());
            needsContent.add(label("3. Are there any tasks or workplace situations you would prefer to avoid? (Optional)"));
            needsContent.add(text("preferredTasks", "Share only what you feel comfortable sharing"));

            needsContent.add(gap());
            needsContent.add(label("4. What accessibility features are important to you? (Optional)"));
            needsContent.add(text("accessibilityDetails", "For example, step-free access, captions, or assistive tools"));

            needsContent.add(gap());
            needsContent.add(label("5. Any other workplace support or adjustment you would like us to consider? (Optional)"));
            needsContent.add(area("otherAccommodation", "Add any details you would like the employer to consider"));

            needsContent.add(gap());
            needsContent.add(label("6. Is there anything else that would help you feel supported at work? (Optional)"));
            needsContent.add(area("supportNeeded", "Additional details (optional)"));

        } else if (userType.equals("Senior Citizen")) {
            needsContent.add(label("1. What employment type are you most interested in?"));
            needsContent.add(choices("seniorEmployment", new String[]{
                    "Full-time", "Part-time", "Project-based", "Consulting",
                    "Mentoring / Advisory", "Temporary", "Any suitable opportunity", "Other"
            }, 3));

            needsContent.add(gap());
            needsContent.add(label("2. What schedule do you prefer?"));
            needsContent.add(choices("seniorSchedule", new String[]{
                    "Morning", "Afternoon", "Flexible", "No preference", "Other"
            }, 3));

            needsContent.add(gap());
            needsContent.add(label("3. Which workplace preferences are important to you? Select all that apply."));
            needsContent.add(checks("seniorWorkplace", new String[]{
                    "Flexible working hours", "Part-time hours", "Remote / Online work",
                    "Comfortable workstation", "Accessible workplace", "Shorter working hours",
                    "Less physically demanding work", "No special preference", "Other"
            }, 2));

            needsContent.add(gap());
            needsContent.add(label("4. What workload would suit you best?"));
            needsContent.add(choices("seniorWorkload", new String[]{
                    "Light workload", "Moderate workload", "Regular workload",
                    "Flexible depending on role", "No preference"
            }, 3));

            needsContent.add(gap());
            needsContent.add(label("5. What kind of support would help you succeed at work? Select all that apply."));
            needsContent.add(checks("seniorSupport", new String[]{
                    "Training for new tools", "Refresher training", "Clear written instructions",
                    "Mentoring or advisory roles", "Comfortable breaks", "Flexible schedule",
                    "No preference", "Other"
            }, 2));

            needsContent.add(gap());
            needsContent.add(label("6. Any other preferences or workplace needs you want us to consider? (Optional)"));
            needsContent.add(area("seniorDetails", "Add details here if you wish"));

        } else {
            needsContent.add(label("1. What are you currently looking for?"));
            needsContent.add(choices("regularPurpose", new String[]{
                    "A new job", "A career change", "Additional income",
                    "Temporary work", "Any suitable opportunity", "Other"
            }, 3));

            needsContent.add(gap());
            needsContent.add(label("2. What position are you interested in?"));
            needsContent.add(combo("desiredPosition", new String[]{
                    "Office Staff",
                    "Customer Service Representative",
                    "Sales Associate",
                    "Administrative Assistant",
                    "IT Support"
            }));

            needsContent.add(gap());
            needsContent.add(label("3. What workplace environment helps you do your best work?"));
            needsContent.add(choices("regularEnvironment", new String[]{
                    "Independent", "Team-based", "Quiet", "Customer-facing",
                    "Flexible", "No preference"
            }, 3));

            needsContent.add(gap());
            needsContent.add(label("4. Is there anything else you need from a workplace? (Optional)"));
            needsContent.add(area("regularNeeds", "Share any preferences or requirements (optional)"));
        }

        needsContent.revalidate();
        needsContent.repaint();
    }

    private JPanel buildReviewPage() {
        reviewContent = new JPanel();
        reviewContent.setLayout(new BoxLayout(reviewContent, BoxLayout.Y_AXIS));
        reviewContent.setOpaque(false);

        return makePage(
                "Review your assessment",
                "Check your answers before JobMatch generates your recommendations.",
                "6",
                reviewContent
        );
    }

    private boolean validateStep() {
        String[] requiredKeys;

        switch (step) {
            case 0:
                if (userType == null || userType.isBlank()) {
                    return showValidationError("Please select a profile type.");
                }
                return true;

            case 1:
                requiredKeys = new String[]{
                        "opportunity", "industries", "jobRoles", "relatedOpportunities"
                };
                break;

            case 2:
                requiredKeys = new String[]{
                        "skills", "strongestSkill", "skillLevel",
                        "languages", "recentJob", "experience"
                };
                break;

            case 3:
                requiredKeys = new String[]{
                        "workTypes", "workEnjoy", "jobMotivators"
                };
                break;

            case 4:
                requiredKeys = new String[]{
                        "employmentType", "workSetup", "shift",
                        "hoursPerShift", "hoursPerWeek", "location"
                };
                break;

            case 5:
                if (userType.equals("Student")) {
                    requiredKeys = new String[]{
                            "availability", "classSchedule",
                            "educationLevel", "studentSupport"
                    };
                } else if (userType.equals("PWD (Person with Disability)")) {
                    requiredKeys = new String[]{
                            "accommodations", "workEnvironment"
                    };
                } else if (userType.equals("Senior Citizen")) {
                    requiredKeys = new String[]{
                            "seniorEmployment", "seniorSchedule",
                            "seniorWorkplace", "seniorWorkload", "seniorSupport"
                    };
                } else {
                    requiredKeys = new String[]{
                            "regularPurpose", "desiredPosition", "regularEnvironment"
                    };
                }
                break;

            default:
                return true;
        }

        for (String key : requiredKeys) {
            if (!isAnswered(key)) {
                return showValidationError(
                        "Please answer all required questions before continuing.\n\nMissing answer: "
                                + pretty(key)
                );
            }
        }

        return true;
    }

    private boolean showValidationError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Required Answer Missing",
                JOptionPane.WARNING_MESSAGE
        );
        return false;
    }

    private boolean isAnswered(String key) {
        JComponent component = fields.get(key);

        if (component == null) {
            return false;
        }

        if (component instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) component;
            Object value = combo.isEditable()
                    ? combo.getEditor().getItem()
                    : combo.getSelectedItem();

            return value != null && !value.toString().trim().isEmpty();
        }

        if (component instanceof JTextField) {
            return !((JTextField) component).getText().trim().isEmpty();
        }

        if (component instanceof JTextArea) {
            return !((JTextArea) component).getText().trim().isEmpty();
        }

        if (component instanceof JPanel) {
            for (Component child : ((JPanel) component).getComponents()) {
                if (child instanceof AbstractButton
                        && ((AbstractButton) child).isSelected()) {
                    return true;
                }
            }
        }

        return false;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        l.setForeground(new Color(34, 40, 50));
        l.setBorder(new EmptyBorder(0, 0, 7, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private Component gap() {
        return Box.createVerticalStrut(15);
    }

    private JPanel choices(String key, String[] options, int columns) {
        JPanel p = new JPanel(new GridLayout(0, columns, 8, 8));
        p.setOpaque(false);

        ButtonGroup group = new ButtonGroup();
        List<JToggleButton> buttons = new ArrayList<>();

        for (String option : options) {
            JToggleButton b = new JToggleButton(option);
            b.setFont(new Font("Arial", Font.PLAIN, 12));
            b.setFocusPainted(false);
            b.setOpaque(true);
            b.setMargin(new Insets(10, 8, 10, 8));

            group.add(b);
            buttons.add(b);
            b.addActionListener(e -> refreshChoiceButtons(buttons));
            p.add(b);
        }

        refreshChoiceButtons(buttons);
        fields.put(key, p);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        return p;
    }

    private void refreshChoiceButtons(List<JToggleButton> buttons) {
        for (JToggleButton b : buttons) {
            boolean selected = b.isSelected();

            b.setBackground(selected ? paleBlue : Color.WHITE);
            b.setForeground(selected ? blue : ink);
            b.setBorder(new CompoundBorder(
                    new LineBorder(
                            selected ? blue : new Color(213, 218, 227),
                            1,
                            true
                    ),
                    new EmptyBorder(5, 7, 5, 7)
            ));
        }
    }

    private JPanel checks(String key, String[] options, int columns) {
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setOpaque(false);
        containerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel p = new JPanel(new GridLayout(0, columns, 10, 10));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField otherField = new JTextField();
        otherField.setFont(new Font("Arial", Font.PLAIN, 12));
        otherField.setVisible(false);
        otherField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        otherField.setBorder(new CompoundBorder(new LineBorder(new Color(213, 218, 227), 1, true), new EmptyBorder(6, 8, 6, 8)));

        for (String option : options) {
            JCheckBox b = new JCheckBox(option);
            b.setFont(new Font("Arial", Font.PLAIN, 12));
            b.setFocusPainted(false);
            b.setOpaque(true);
            b.setBackground(Color.WHITE);
            b.setForeground(ink);
            b.setBorder(new CompoundBorder(new LineBorder(new Color(213, 218, 227), 1, true), new EmptyBorder(10, 11, 10, 11)));
            b.setPreferredSize(new Dimension(150, 44));

            b.addActionListener(e -> {
                boolean selected = b.isSelected();
                b.setBackground(selected ? paleBlue : Color.WHITE);
                b.setForeground(selected ? blue : ink);
                b.setBorder(new CompoundBorder(new LineBorder(selected ? blue : new Color(213, 218, 227), 1, true), new EmptyBorder(10, 11, 10, 11)));

                if (option.equalsIgnoreCase("Other")) {
                    otherField.setVisible(selected);
                    if (!selected) otherField.setText("");
                    containerPanel.revalidate();
                    containerPanel.repaint();
                }
            });
            p.add(b);
        }

        containerPanel.add(p);
        containerPanel.add(Box.createVerticalStrut(6));
        containerPanel.add(otherField);

        containerPanel.putClientProperty("otherTxt", otherField);
        fields.put(key, p);
        fields.put(key + "_wrap", containerPanel);

        return containerPanel;
    }

    private JComboBox<String> combo(String key, String[] options) {
        JComboBox<String> b = new JComboBox<>(options);
        b.setEditable(true);
        b.setSelectedIndex(-1);
        b.getEditor().setItem("");
        b.setFont(new Font("Arial", Font.PLAIN, 12));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);

        fields.put(key, b);

        return b;
    }

    private JTextField text(String key, String placeholder) {
        JTextField f = new JTextField();
        f.setFont(new Font("Arial", Font.PLAIN, 12));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(
                new LineBorder(new Color(211, 216, 225), 1, true),
                new EmptyBorder(7, 9, 7, 9)
        ));
        f.setToolTipText(placeholder);

        fields.put(key, f);

        return f;
    }

    private JScrollPane area(String key, String placeholder) {
        JTextArea area = new JTextArea(3, 20);
        area.setFont(new Font("Arial", Font.PLAIN, 12));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(7, 9, 7, 9));
        area.setToolTipText(placeholder);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        scroll.setBorder(new LineBorder(new Color(211, 216, 225), 1, true));

        fields.put(key, area);

        return scroll;
    }

    private void updateTypeCardIcon(JLabel iconLabel, boolean active) {
        String activeIcon = (String) iconLabel.getClientProperty("activeIconFile");
        String inactiveIcon = (String) iconLabel.getClientProperty("inactiveIconFile");
        String fallback = (String) iconLabel.getClientProperty("fallbackSymbol");
        ImageIcon icon = loadIcon(active ? activeIcon : inactiveIcon, 24, 24);
        if (icon != null) {
            iconLabel.setIcon(icon);
            iconLabel.setText("");
        } else {
            iconLabel.setIcon(null);
            iconLabel.setText(fallback != null ? fallback : "");
        }
    }

    private void updateResumeCardState() {
        if (resumePanel == null || resumeFileNameLabel == null || resumeMetaLabel == null || uploadButton == null || resumeActionsPanel == null) {
            return;
        }
        boolean hasResume = resumeFile != null;
        if (hasResume) {
            String fileName = resumeFile.getName();
            resumeFileNameLabel.setText(fileName);
            resumeFileNameLabel.setForeground(ink);
            long fileSizeKb = Math.max(1L, resumeFile.length() / 1024L);
            String extension = fileName.contains(".")
                    ? fileName.substring(fileName.lastIndexOf('.') + 1).toUpperCase()
                    : "FILE";
            resumeMetaLabel.setText(extension + " • " + (fileSizeKb / 1024.0 < 1 ? fileSizeKb + " KB" : String.format("%.1f MB", fileSizeKb / 1024.0)));
            resumeMetaLabel.setForeground(muted);
            uploadButton.setVisible(false);
            resumeActionsPanel.setVisible(true);
        } else {
            resumeFileNameLabel.setText("No resume selected");
            resumeFileNameLabel.setForeground(ink);
            resumeMetaLabel.setText("Upload PDF, DOC, or DOCX");
            resumeMetaLabel.setForeground(muted);
            uploadButton.setVisible(true);
            resumeActionsPanel.setVisible(false);
        }
        resumePanel.revalidate();
        resumePanel.repaint();
    }

    private JButton linkButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.PLAIN, 12));
        b.setForeground(blue);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusable(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void viewResume() {
        if (resumeFile == null || !resumeFile.exists()) return;
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(resumeFile);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unable to open resume file.", "Resume", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearResume() {
        resumeFile = null;
        answers.remove("Resume File");
        updateResumeCardState();
    }

    private ImageIcon loadIcon(String fileName, int width, int height) {
        if (fileName == null || fileName.isBlank()) return null;
        File file = new File("src/images/" + fileName);
        if (!file.exists()) file = new File("JobMatch/src/images/" + fileName);
        if (!file.exists()) return null;
        Image image = new ImageIcon(file.getPath()).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    private void updateProgress() {
        progress.removeAll();

        JLabel stepLabel = new JLabel("Step " + (step + 1) + " of 7");
        stepLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        JPanel track = new JPanel(new GridLayout(1, 7, 0, 0));
        track.setOpaque(false);

        for (int i = 0; i < steps.length; i++) {
            JPanel item = new JPanel(new BorderLayout(0, 4));
            item.setOpaque(false);

            final Color circleColor = i <= step ? blue : new Color(194, 198, 207);
            final Color textColor = i <= step ? Color.WHITE : new Color(75, 80, 91);

            JPanel circle = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(circleColor);
                    g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            circle.setOpaque(false);
            circle.setPreferredSize(new Dimension(30, 30));
            circle.setMinimumSize(new Dimension(30, 30));
            circle.setMaximumSize(new Dimension(30, 30));

            JLabel circleNumber = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
            circleNumber.setFont(new Font("Arial", Font.BOLD, 11));
            circleNumber.setForeground(textColor);
            circle.add(circleNumber);

            JLabel name = new JLabel(steps[i], SwingConstants.CENTER);
            name.setFont(new Font("Arial", Font.PLAIN, 10));
            name.setForeground(i == step ? blue : muted);

            JPanel wrap = new JPanel(new GridBagLayout());
            wrap.setOpaque(false);
            wrap.add(circle);

            item.add(wrap, BorderLayout.CENTER);
            item.add(name, BorderLayout.SOUTH);
            track.add(item);
        }

        JPanel line = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setStroke(new BasicStroke(3));

                int y = 18;
                int start = getWidth() / 14;
                int end = getWidth() - start;

                g2.setColor(new Color(194, 198, 207));
                g2.drawLine(start, y, end, y);

                g2.setColor(blue);
                g2.drawLine(
                        start,
                        y,
                        start + (int) ((end - start) * step / 6.0),
                        y
                );

                g2.dispose();
            }
        };

        line.setOpaque(false);
        line.setPreferredSize(new Dimension(100, 60));
        line.setLayout(new BorderLayout());
        line.setBorder(new EmptyBorder(2, 0, 0, 0));
        line.add(track, BorderLayout.CENTER);

        progress.add(stepLabel, BorderLayout.NORTH);
        progress.add(line, BorderLayout.CENTER);
        progress.revalidate();
        progress.repaint();
    }

    private void showStep(int next) {
        if (!skipSaveOnNextShowStep) {
            saveAnswers();
        } else {
            skipSaveOnNextShowStep = false;
        }

        step = next;
        updateProgress();
        cards.show(pages, String.valueOf(step));

        if (step == 6) {
            updateReview();
        }
    }

    private void saveAnswers() {
        answers.put("Profile Type", userType);
        if (resumeFile != null) {
            answers.put("Resume File", resumeFile.getName());
        }

        for (Map.Entry<String, JComponent> entry : fields.entrySet()) {
            String key = entry.getKey();
            if (key.endsWith("_wrap")) continue;
            JComponent component = entry.getValue();

            if (component instanceof JComboBox) {
                JComboBox<?> combo = (JComboBox<?>) component;
                Object value = combo.isEditable() ? combo.getEditor().getItem() : combo.getSelectedItem();
                if (value != null && !value.toString().trim().isEmpty()) {
                    answers.put(key, value.toString().trim());
                } else {
                    answers.remove(key);
                }
            } else if (component instanceof JTextField) {
                String value = ((JTextField) component).getText().trim();
                if (!value.isEmpty()) answers.put(key, value);
                else answers.remove(key);
            } else if (component instanceof JTextArea) {
                String value = ((JTextArea) component).getText().trim();
                if (!value.isEmpty()) answers.put(key, value);
                else answers.remove(key);
            } else if (component instanceof JPanel) {
                List<String> selected = new ArrayList<>();
                for (Component child : ((JPanel) component).getComponents()) {
                    if (child instanceof AbstractButton && ((AbstractButton) child).isSelected()) {
                        String txt = ((AbstractButton) child).getText();
                        if (txt.equalsIgnoreCase("Other")) {
                            JPanel wrap = (JPanel) fields.get(key + "_wrap");
                            if (wrap != null) {
                                JTextField tf = (JTextField) wrap.getClientProperty("otherTxt");
                                if (tf != null && !tf.getText().trim().isEmpty()) {
                                    txt = "Other (" + tf.getText().trim() + ")";
                                }
                            }
                        }
                        selected.add(txt);
                    }
                }
                if (!selected.isEmpty()) answers.put(key, String.join(", ", selected));
                else answers.remove(key);
            }
        }

        if (certYesRadio != null && certYesRadio.isSelected() && !uploadedCertFiles.isEmpty()) {
            List<String> paths = new ArrayList<>();
            for (File f : uploadedCertFiles) {
                paths.add(f.getAbsolutePath());
            }
            answers.put("cert_paths", String.join(";", paths));
        } else {
            answers.remove("cert_paths");
        }
    }

    private void updateReview() {
        saveAnswers();

        reviewContent.removeAll();
        reviewCards.clear();

        addReviewCard("Profile", 0, new String[]{"Profile Type", "Resume File"});

        addReviewCard("Career Goals", 1, new String[]{
                "opportunity", "industries", "jobRoles", "relatedOpportunities"
        });

        addReviewCard("Skills", 2, new String[]{
                "skills", "strongestSkill", "skillLevel", "languages",
                "recentJob", "experience", "qualifications"
        });

        addReviewCard("Work Interests", 3, new String[]{
                "workTypes", "workEnjoy", "jobMotivators", "customInterest"
        });

        addReviewCard("Work Preferences", 4, new String[]{
                "employmentType", "workSetup", "shift", "hoursPerShift",
                "hoursPerWeek", "location", "scheduleDetails"
        });

        String[] needsKeys;

        if (userType.equals("Student")) {
            needsKeys = new String[]{
                    "availability", "classSchedule", "educationLevel", "studentSupport"
            };
        } else if (userType.equals("PWD (Person with Disability)")) {
            needsKeys = new String[]{
                    "accommodations", "workEnvironment", "preferredTasks",
                    "accessibilityDetails", "otherAccommodation", "supportNeeded"
            };
        } else if (userType.equals("Senior Citizen")) {
            needsKeys = new String[]{
                    "seniorEmployment", "seniorSchedule", "seniorWorkplace",
                    "seniorWorkload", "seniorSupport", "seniorDetails"
            };
        } else {
            needsKeys = new String[]{
                    "regularPurpose", "desiredPosition", "regularEnvironment", "regularNeeds"
            };
        }

        addReviewCard("Needs", 5, needsKeys);

        reviewContent.revalidate();
        reviewContent.repaint();
    }

    private void addReviewCard(String title, int editStep, String[] keys) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new RoundedLineBorder(new Color(225, 230, 239), 12, 1),
                new EmptyBorder(10, 12, 10, 12)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Arial", Font.BOLD, 15));
        heading.setForeground(new Color(18, 56, 140));

        JButton edit = new JButton("Edit");
        edit.setFont(new Font("Arial", Font.PLAIN, 11));
        edit.setForeground(blue);
        edit.setBorder(new CompoundBorder(
                new RoundedLineBorder(new Color(191, 219, 254), 10, 1),
                new EmptyBorder(4, 10, 4, 10)));
        edit.setBackground(Color.WHITE);
        edit.setOpaque(true);
        edit.setFocusPainted(false);
        edit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        edit.addActionListener(e -> showStep(editStep));

        top.add(heading, BorderLayout.WEST);
        top.add(edit, BorderLayout.EAST);

        JPanel divider = new JPanel();
        divider.setPreferredSize(new Dimension(1, 1));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setBackground(new Color(231, 235, 243));

        JPanel rows = new JPanel();
        rows.setOpaque(false);
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));

        boolean found = false;

        for (String key : keys) {
            String value = answers.get(key);

            if (value != null && !value.isBlank()) {
                found = true;

                JPanel row = new JPanel(new BorderLayout(6, 3));
                row.setOpaque(false);
                row.setBorder(new EmptyBorder(4, 0, 5, 0));

                JLabel name = new JLabel(pretty(key));
                name.setFont(new Font("Arial", Font.BOLD, 10));
                name.setForeground(new Color(109, 118, 133));

                JTextArea answer = new JTextArea(value);
                answer.setFont(new Font("Arial", Font.PLAIN, 12));
                answer.setForeground(new Color(28, 33, 43));
                answer.setLineWrap(true);
                answer.setWrapStyleWord(true);
                answer.setEditable(false);
                answer.setFocusable(false);
                answer.setOpaque(false);
                answer.setBorder(null);

                row.add(name, BorderLayout.NORTH);
                row.add(answer, BorderLayout.CENTER);
                rows.add(row);
            }
        }

        if (!found) {
            JLabel empty = new JLabel("No answers added yet");
            empty.setFont(new Font("Arial", Font.ITALIC, 11));
            empty.setForeground(muted);
            rows.add(empty);
        }

        JPanel topWrap = new JPanel();
        topWrap.setOpaque(false);
        topWrap.setLayout(new BoxLayout(topWrap, BoxLayout.Y_AXIS));
        topWrap.add(top);
        topWrap.add(Box.createVerticalStrut(8));
        topWrap.add(divider);
        topWrap.add(Box.createVerticalStrut(8));

        card.add(topWrap, BorderLayout.NORTH);
        card.add(rows, BorderLayout.CENTER);

        reviewCards.put(title, card);
        reviewContent.add(card);
        reviewContent.add(Box.createVerticalStrut(10));
    }

    private void initializeAssessmentEntry() {
        Map<String, String> latestAnswers = database.Database.getLatestAssessment(getUser().getEmail());
        if (latestAnswers.isEmpty()) {
            showStep(0);
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "You already completed an assessment.\n\nTake assessment again?",
                "Assessment Found",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            showStep(0);
            return;
        }

        loadAssessmentDraft(latestAnswers);
        skipSaveOnNextShowStep = true;
        showStep(6);
    }

    private void loadAssessmentDraft(Map<String, String> latestAnswers) {
        answers.clear();
        answers.putAll(latestAnswers);
        userType = answers.getOrDefault("Profile Type", userType);

        for (Map.Entry<String, JRadioButton> entry : typeButtons.entrySet()) {
            entry.getValue().setSelected(entry.getKey().equals(userType));
        }
        styleTypeCards();
        rebuildNeeds();

        for (Map.Entry<String, JComponent> entry : fields.entrySet()) {
            String key = entry.getKey();
            String value = answers.get(key);
            if (value == null) continue;

            JComponent component = entry.getValue();
            if (component instanceof JComboBox) {
                JComboBox<?> combo = (JComboBox<?>) component;
                if (combo.isEditable()) {
                    combo.getEditor().setItem(value);
                } else {
                    combo.setSelectedItem(value);
                }
            } else if (component instanceof JTextField) {
                ((JTextField) component).setText(value);
            } else if (component instanceof JTextArea) {
                ((JTextArea) component).setText(value);
            } else if (component instanceof JPanel) {
                Set<String> selectedValues = new LinkedHashSet<>();
                for (String part : value.split("\\s*,\\s*")) {
                    if (!part.isBlank()) selectedValues.add(part.trim());
                }
                JPanel panel = (JPanel) component;
                for (Component child : panel.getComponents()) {
                    if (child instanceof AbstractButton) {
                        AbstractButton btn = (AbstractButton) child;
                        btn.setSelected(selectedValues.contains(btn.getText()));
                    }
                }
                updatePanelButtonStyles(panel);
            }
        }
    }

    private void updatePanelButtonStyles(JPanel panel) {
        List<JToggleButton> toggles = new ArrayList<>();
        for (Component child : panel.getComponents()) {
            if (child instanceof JToggleButton) {
                toggles.add((JToggleButton) child);
            } else if (child instanceof JCheckBox) {
                JCheckBox b = (JCheckBox) child;
                boolean selected = b.isSelected();
                b.setBackground(selected ? paleBlue : Color.WHITE);
                b.setForeground(selected ? blue : ink);
                b.setBorder(new CompoundBorder(
                        new LineBorder(selected ? blue : new Color(213, 218, 227), 1, true),
                        new EmptyBorder(10, 11, 10, 11)
                ));
            }
        }
        if (!toggles.isEmpty()) {
            refreshChoiceButtons(toggles);
        }
    }

    private String pretty(String key) {
        String s = key.replaceAll("([a-z])([A-Z])", "$1 $2")
                .replaceAll("([A-Z])([A-Z][a-z])", "$1 $2");

        s = s.replace("jobRoles", "Preferred Job Roles")
                .replace("workTypes", "Interested Work Types")
                .replace("workEnjoy", "Enjoyable Work")
                .replace("jobMotivators", "Job Priorities")
                .replace("hoursPerShift", "Hours Per Shift")
                .replace("hoursPerWeek", "Hours Per Week")
                .replace("relatedOpportunities", "Open to Related Roles")
                .replace("otherAccommodation", "Additional Workplace Support")
                .replace("supportNeeded", "Other Support Needed")
                .replace("seniorWorkplace", "Workplace Preferences")
                .replace("seniorDetails", "Additional Preferences")
                .replace("customInterest", "Other Interests")
                .replace("regularNeeds", "Other Workplace Needs")
                .replace("scheduleDetails", "Schedule Details")
                .replace("Profile Type", "Profile Type")
                .replace("Resume File", "Resume File");

        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private boolean saveAssessmentToDatabase() {
        boolean success = database.Database.saveUserAssessment(getUser().getEmail(), answers);
        if (!success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Something went wrong while saving your assessment.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        SeekerProfile profile = SeekerProfile.forUser(getUser());
        profile.applyAssessment(answers);
        profile.save();
        return true;
    }

}