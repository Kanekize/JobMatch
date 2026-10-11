package pages;

import database.Database;
import models.Job;
import users.User;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PostJobPage extends Page {

    private Job editingJob;

    private CardLayout cards;
    private JPanel stepsPanel;
    private JLabel progressLabel;

    //post job forms
    private boolean fillingForm = false;

    public static final String[] CATEGORIES = {
            "Software Development", "Data & Analytics", "Artificial Intelligence", "Telecommunications",
            "Cybersecurity", "Healthcare", "Education", "Finance & Accounting", "Marketing",
            "Customer Service", "Human Resources", "Engineering", "Construction & Skilled Trades",
            "Hospitality & Tourism", "Retail & Sales", "Logistics & Supply Chain", "Manufacturing",
            "Media & Design", "Legal", "Agriculture"
    };

    public static final Map<String, String[]> LOCATIONS = new LinkedHashMap<>();
    static {
        LOCATIONS.put("Metro Manila", new String[]{"Quezon City", "Pasig", "Makati", "Taguig", "Manila", "Mandaluyong", "Pasay", "Paranaque", "Caloocan", "Muntinlupa"});
        LOCATIONS.put("Batangas", new String[]{"Batangas City", "Lipa", "Tanauan", "Santo Tomas"});
        LOCATIONS.put("Cebu", new String[]{"Cebu City", "Mandaue", "Lapu-Lapu", "Talisay"});
        LOCATIONS.put("Davao", new String[]{"Davao City", "Tagum", "Digos", "Panabo"});
        LOCATIONS.put("Cavite", new String[]{"Bacoor", "Dasmarinas", "Imus", "General Trias"});
        LOCATIONS.put("Laguna", new String[]{"Calamba", "Santa Rosa", "Binan", "San Pedro"});
        LOCATIONS.put("Bulacan", new String[]{"Malolos", "Meycauayan", "San Jose del Monte"});
        LOCATIONS.put("Iloilo", new String[]{"Iloilo City", "Passi", "Roxas"});
        LOCATIONS.put("Pampanga", new String[]{"San Fernando", "Angeles", "Mabalacat"});
        LOCATIONS.put("Baguio", new String[]{"Baguio City", "La Trinidad"});
    }

    private static final String[] SKILL_CHOICES = {
            "Java", "HTML", "CSS", "JavaScript", "Python", "SQL", "C++", "C#", "PHP",
            "React", "Node.js", "Spring Boot", "Excel", "Web Development", "Data Analysis",
            "Machine Learning", "Networking", "Communication", "Teamwork", "Problem Solving",
            "Leadership", "Time Management", "Customer Service", "Writing", "Photoshop"
    };

    private static final String[] ACCOMMODATION_CHOICES = {
            "Flexible Schedule", "PWD-Friendly Office", "Wheelchair Access", "Sign Language Interpreter",
            "Assistive Technology", "Remote Setup Support", "Transportation Allowance",
            "Adjustable Workstation", "Service Animal Friendly", "Extra Break Time"
    };

    private static final String[] APPLICANT_TYPE_CHOICES = {
            "Regular Job Seeker", "Student", "Senior Citizen", "Person with Disability (PWD)"
    };

    private static final String[] EXPERIENCE_CHOICES = {
            "6 months - 1 year", "1-2 years", "2-3 years", "3-5 years", "5+ years"
    };

    // step 1 all fields
    private JTextField titleField;
    private JComboBox<String> categoryBox, jobTypeBox, workplaceBox;
    private JComboBox<String> statusBox;
    private JTextField minSalaryField, maxSalaryField;
    private JTextArea descArea;
    private JLabel descCounter;

    //step 2
    private List<String> selectedSkills = new ArrayList<>();
    private JTextArea skillsArea;
    private JRadioButton accYes, accNo;
    private List<String> selectedAccommodations = new ArrayList<>();
    private JTextArea accommodationsArea;
    private JButton chooseAccBtn;
    private List<String> selectedTypes = new ArrayList<>();
    private JTextArea typesArea;
    private JRadioButton expYes, expNo;
    private String expLength = "";
    private JLabel expLengthLabel;
    private JButton chooseExpBtn;
    private JComboBox<String> locationBox;
    private String selectedCity = "";
    private JLabel cityLabel;
    private String logoPath = "";
    private JPanel logoPreview;

    public PostJobPage(User user) {
        this(user, null);
    }

    // for editing
    public PostJobPage(User user, Job editingJob) {
        super(user);
        this.editingJob = editingJob;

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JButton backLink = new JButton("< Go to My Job Posts");
        backLink.setFont(new Font("Arial", Font.PLAIN, 14));
        backLink.setForeground(Color.decode("#0d0d0d"));
        backLink.setBorderPainted(false);
        backLink.setContentAreaFilled(false);
        backLink.setFocusable(false);
        backLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLink.addActionListener(e -> getDashboard().onMenuClick("Jobs"));
        topRow.add(backLink, BorderLayout.WEST);

        progressLabel = new JLabel("(Progress: Step 1 out of 3)");
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        progressLabel.setForeground(Color.decode("#6b7280"));
        topRow.add(progressLabel, BorderLayout.EAST);
        addBlock(topRow, 5);

        if (editingJob != null) {
            statusBox = new JComboBox<>(new String[]{"Active", "Closed"});
            statusBox.setFont(new Font("Arial", Font.PLAIN, 14));
            statusBox.setBackground(Color.WHITE);
            statusBox.setPreferredSize(new Dimension(160, 32));

            JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            statusRow.setOpaque(false);
            statusRow.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel statusLbl = new JLabel("Post Status:");
            statusLbl.setFont(new Font("Arial", Font.BOLD, 13));
            statusLbl.setForeground(Color.decode("#0d0d0d"));

            statusRow.add(statusLbl);
            statusRow.add(statusBox);
            addBlock(statusRow, 5);
        }

        addBlock(makeHeading(editingJob == null ? "Post a New Job Opening" : "Edit Job Details",
                "Fill out the core requirements below.", null), 15);

        cards = new CardLayout();
        stepsPanel = new JPanel(cards);
        stepsPanel.setOpaque(false);
        stepsPanel.add(buildStep1(), "step1");
        stepsPanel.add(buildStep2(), "step2");
        addBlock(stepsPanel, 0);

        if (editingJob != null) {
            fillOldAnswers();
        }
    }

    // step 1

    private JPanel buildStep1() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        titleField = new JTextField();
        panel.add(makeField("Job Title *", titleField, "e.g. Junior Web Developer"));
        panel.add(Box.createVerticalStrut(15));

        categoryBox = new JComboBox<>(addHint(CATEGORIES, "Select Category"));
        jobTypeBox = new JComboBox<>(addHint(new String[]{"Full-time", "Part-time", "Contract"}, "Select Type"));
        JPanel row1 = twoColumns(makeComboField("Job Category *", categoryBox), makeComboField("Job Type *", jobTypeBox));
        panel.add(row1);
        panel.add(Box.createVerticalStrut(15));

        workplaceBox = new JComboBox<>(addHint(new String[]{"On-site", "Remote Work", "Hybrid Work"}, "Select Type"));
        JPanel row2 = twoColumns(
                makeComboField("Workplace Type *", workplaceBox),
                makeSalaryField());
        panel.add(row2);
        panel.add(Box.createVerticalStrut(15));

        panel.add(makeLabel("Job Description *"));
        descArea = new JTextArea(4, 20);
        descArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#e2e8f0"), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        descArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { limit(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { limit(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { limit(); }
            private void limit() {
                if (descArea.getText().length() > 300) {
                    String cut = descArea.getText().substring(0, 300);
                    SwingUtilities.invokeLater(() -> descArea.setText(cut));
                }
                descCounter.setText(descArea.getText().length() + "/300");
            }
        });
        JPanel descWrap = new JPanel(new BorderLayout());
        descWrap.setOpaque(false);
        descWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        descWrap.add(descArea, BorderLayout.CENTER);
        panel.add(descWrap);

        descCounter = new JLabel("0/300");
        descCounter.setFont(new Font("Arial", Font.PLAIN, 11));
        descCounter.setForeground(Color.decode("#6b7280"));
        descCounter.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panel.add(descCounter);
        panel.add(Box.createVerticalStrut(20));

        panel.add(makeNavButtons(null, "Next  >", e -> {
            if (checkStep1()) {
                progressLabel.setText("(Progress: Step 2 out of 3)");
                cards.show(stepsPanel, "step2");
            }
        }));
        return panel;
    }

    private boolean checkStep1() {
        if (titleField.getText().trim().isEmpty()) {
            return warn("Please fill out the Job Title.");
        }
        if (categoryBox.getSelectedIndex() == 0) {
            return warn("Please choose a Job Category.");
        }
        if (jobTypeBox.getSelectedIndex() == 0) {
            return warn("Please choose a Job Type.");
        }
        if (workplaceBox.getSelectedIndex() == 0) {
            return warn("Please choose a Workplace Type.");
        }
        if (minSalaryField.getText().isEmpty() || maxSalaryField.getText().isEmpty()) {
            return warn("Please fill out the Salary Range.");
        }
        if (Integer.parseInt(minSalaryField.getText()) > Integer.parseInt(maxSalaryField.getText())) {
            return warn("The minimum salary should not be bigger than the maximum salary.");
        }
        if (descArea.getText().trim().isEmpty()) {
            return warn("Please fill out the Job Description.");
        }
        return true;
    }

    // step 2
    private JPanel buildStep2() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(makeLabel("Required Skills Tags *"));
        JButton chooseSkillsBtn = makeGrayButton("Choose Skills");
        chooseSkillsBtn.addActionListener(e -> {
            showCheckboxPopup("Choose Skill Tags", SKILL_CHOICES, selectedSkills, true);
            skillsArea.setText(String.join(", ", selectedSkills));
        });
        panel.add(chooseSkillsBtn);
        panel.add(Box.createVerticalStrut(6));
        skillsArea = makeDisplayArea("No skills selected yet.");
        panel.add(skillsArea);
        panel.add(Box.createVerticalStrut(15));

        panel.add(makeLabel("Workplace Accommodations Available? *"));
        accYes = new JRadioButton("Yes");
        accNo = new JRadioButton("No");
        ButtonGroup accGroup = new ButtonGroup();
        accGroup.add(accYes);
        accGroup.add(accNo);
        JPanel accChoice = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        accChoice.setOpaque(false);
        accChoice.setAlignmentX(Component.LEFT_ALIGNMENT);
        accChoice.add(accYes);
        accChoice.add(accNo);
        chooseAccBtn = makeGrayButton("Choose Accommodations");
        chooseAccBtn.setEnabled(false);
        chooseAccBtn.addActionListener(e -> {
            showCheckboxPopup("Choose the supports you offer", ACCOMMODATION_CHOICES, selectedAccommodations, true);
            accommodationsArea.setText(String.join(", ", selectedAccommodations));
        });
        accChoice.add(chooseAccBtn);
        accYes.addActionListener(e -> chooseAccBtn.setEnabled(true));
        accNo.addActionListener(e -> {
            chooseAccBtn.setEnabled(false);
            selectedAccommodations.clear();
            accommodationsArea.setText("None.");
        });
        panel.add(accChoice);
        accommodationsArea = makeDisplayArea("None.");
        panel.add(accommodationsArea);
        panel.add(Box.createVerticalStrut(15));

        panel.add(makeLabel("Applicant Type *"));
        JButton chooseTypesBtn = makeGrayButton("Choose Applicant Types");
        chooseTypesBtn.addActionListener(e -> {
            showCheckboxPopup("Who can apply?", APPLICANT_TYPE_CHOICES, selectedTypes, false);
            typesArea.setText(String.join(", ", selectedTypes));
        });
        panel.add(chooseTypesBtn);
        panel.add(Box.createVerticalStrut(6));
        typesArea = makeDisplayArea("No applicant types selected yet.");
        panel.add(typesArea);
        panel.add(Box.createVerticalStrut(15));

        panel.add(makeLabel("Experience Required? *"));
        expYes = new JRadioButton("Yes");
        expNo = new JRadioButton("No");
        ButtonGroup expGroup = new ButtonGroup();
        expGroup.add(expYes);
        expGroup.add(expNo);
        JPanel expChoice = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        expChoice.setOpaque(false);
        expChoice.setAlignmentX(Component.LEFT_ALIGNMENT);
        expChoice.add(expYes);
        expChoice.add(expNo);
        chooseExpBtn = makeGrayButton("Choose Length of Experience");
        chooseExpBtn.setEnabled(false);
        chooseExpBtn.addActionListener(e -> {
            String picked = showRadioPopup("How long should the experience be?", EXPERIENCE_CHOICES, expLength);
            if (picked != null) {
                expLength = picked;
                expLengthLabel.setText(picked);
            }
        });
        expChoice.add(chooseExpBtn);
        expLengthLabel = new JLabel("");
        expLengthLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        expChoice.add(expLengthLabel);
        expYes.addActionListener(e -> chooseExpBtn.setEnabled(true));
        expNo.addActionListener(e -> {
            chooseExpBtn.setEnabled(false);
            expLength = "";
            expLengthLabel.setText("");
        });
        panel.add(expChoice);
        panel.add(Box.createVerticalStrut(15));

        panel.add(makeLabel("Company Location *"));
        locationBox = new JComboBox<>(addHint(LOCATIONS.keySet().toArray(new String[0]), "Select Location"));
        locationBox.addActionListener(e -> {
            if (fillingForm) return;
            if (locationBox.getSelectedIndex() > 0) {
                String province = (String) locationBox.getSelectedItem();
                String picked = showRadioPopup("Where in " + province + "?", LOCATIONS.get(province), selectedCity);
                if (picked != null) {
                    selectedCity = picked;
                    cityLabel.setText("City: " + picked);
                }
            }
        });
        JPanel locWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        locWrap.setOpaque(false);
        locWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        locationBox.setPreferredSize(new Dimension(250, 38));
        locWrap.add(locationBox);
        cityLabel = new JLabel("No city selected yet.");
        cityLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        cityLabel.setForeground(Color.decode("#6b7280"));
        locWrap.add(cityLabel);
        panel.add(locWrap);
        panel.add(Box.createVerticalStrut(15));

        panel.add(makeLabel("Company Logo Image (Optional)"));
        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        logoRow.setOpaque(false);
        logoRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoPreview = new JPanel(new BorderLayout());
        logoPreview.setPreferredSize(new Dimension(90, 90));
        logoPreview.setBackground(Color.WHITE);
        logoPreview.setBorder(BorderFactory.createLineBorder(Color.decode("#e2e8f0"), 1));
        JLabel noImage = new JLabel("No Image Uploaded", SwingConstants.CENTER);
        noImage.setFont(new Font("Arial", Font.PLAIN, 10));
        noImage.setForeground(Color.decode("#6b7280"));
        logoPreview.add(noImage, BorderLayout.CENTER);
        JButton uploadBtn = makeBlueButton("Upload File");
        uploadBtn.addActionListener(e -> pickLogo());
        logoRow.add(logoPreview);
        logoRow.add(uploadBtn);
        panel.add(logoRow);
        panel.add(Box.createVerticalStrut(20));

        panel.add(makeNavButtons(e -> {
            progressLabel.setText("(Progress: Step 1 out of 3)");
            cards.show(stepsPanel, "step1");
        }, "Next  >", e -> {
            if (checkStep2()) {
                showReviewStep();
            }
        }));
        return panel;
    }

    private boolean checkStep2() {
        if (selectedSkills.isEmpty()) {
            return warn("Please choose at least one Required Skill Tag.");
        }
        if (!accYes.isSelected() && !accNo.isSelected()) {
            return warn("Please answer Workplace Accommodations Available.");
        }
        if (accYes.isSelected() && selectedAccommodations.isEmpty()) {
            return warn("Please choose at least one Accommodation.");
        }
        if (selectedTypes.isEmpty()) {
            return warn("Please choose at least one Applicant Type.");
        }
        if (!expYes.isSelected() && !expNo.isSelected()) {
            return warn("Please answer Experience Required.");
        }
        if (expYes.isSelected() && expLength.isEmpty()) {
            return warn("Please choose the length of experience needed.");
        }
        if (locationBox.getSelectedIndex() == 0) {
            return warn("Please choose a Company Location.");
        }
        if (selectedCity.isEmpty()) {
            return warn("Please choose a city for the Company Location.");
        }
        return true;
    }

    private void pickLogo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            logoPath = file.getAbsolutePath();
            ImageIcon icon = new ImageIcon(logoPath);
            Image scaled = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            logoPreview.removeAll();
            logoPreview.add(new JLabel(new ImageIcon(scaled)), BorderLayout.CENTER);
            logoPreview.revalidate();
            logoPreview.repaint();
        }
    }

    //step 3
    private void showReviewStep() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel note = new JLabel("You're Almost Done! Review your answers below before finalizing.");
        note.setFont(new Font("Arial", Font.PLAIN, 15));
        note.setForeground(Color.decode("#4b5563"));
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(note);
        panel.add(Box.createVerticalStrut(15));

        panel.add(makeReviewRow("Job Title", titleField.getText().trim()));
        panel.add(makeReviewRow("Job Category", (String) categoryBox.getSelectedItem()));
        panel.add(makeReviewRow("Job Type", (String) jobTypeBox.getSelectedItem()));
        panel.add(makeReviewRow("Workplace Type", (String) workplaceBox.getSelectedItem()));
        panel.add(makeReviewRow("Salary Range",
                "₱" + minSalaryField.getText() + " - ₱" + maxSalaryField.getText() + " per month"));
        panel.add(makeReviewRow("Job Description", descArea.getText().trim()));
        panel.add(makeReviewRow("Required Skills", String.join(", ", selectedSkills)));
        panel.add(makeReviewRow("Workplace Accommodations Available", accYes.isSelected() ? "Yes" : "No"));
        if (accYes.isSelected()) {
            panel.add(makeReviewRow("Accommodations Offered", String.join(", ", selectedAccommodations)));
        }
        panel.add(makeReviewRow("Applicant Types", String.join(", ", selectedTypes)));
        panel.add(makeReviewRow("Experience Required", expYes.isSelected() ? "Yes (" + expLength + ")" : "No"));
        panel.add(makeReviewRow("Company Location", selectedCity + ", " + locationBox.getSelectedItem()));
        panel.add(makeReviewRow("Company Logo", logoPath.isEmpty() ? "No image uploaded" : logoPath));
        if (editingJob != null) {
            panel.add(makeReviewRow("Post Status", (String) statusBox.getSelectedItem()));
        }
        panel.add(Box.createVerticalStrut(15));

        panel.add(makeNavButtons(e -> {
            progressLabel.setText("(Progress: Step 2 out of 3)");
            cards.show(stepsPanel, "step2");
        }, "Finalize  >", e -> saveJob()));
        stepsPanel.add(panel, "step3");
        progressLabel.setText("(Progress: Step 3 out of 3)");
        cards.show(stepsPanel, "step3");
    }

    private JPanel makeReviewRow(String label, String value) {
        JPanel box = new JPanel(new BorderLayout(0, 4));
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#e5e9f0"), 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        nameLabel.setForeground(Color.decode("#12376b"));
        box.add(nameLabel, BorderLayout.NORTH);

        JTextArea valueArea = new JTextArea(value);
        valueArea.setFont(new Font("Arial", Font.PLAIN, 14));
        valueArea.setForeground(Color.decode("#0d0d0d"));
        valueArea.setLineWrap(true);
        valueArea.setWrapStyleWord(true);
        valueArea.setEditable(false);
        valueArea.setFocusable(false);
        box.add(valueArea, BorderLayout.CENTER);
        return box;
    }

    private void saveJob() {
        Job job = editingJob == null ? new Job() : editingJob;
        job.setEmployerEmail(getUser().getEmail());
        job.setTitle(titleField.getText().trim());
        job.setCategory((String) categoryBox.getSelectedItem());
        job.setJobType((String) jobTypeBox.getSelectedItem());
        job.setWorkplaceType((String) workplaceBox.getSelectedItem());
        job.setSalaryMin(Integer.parseInt(minSalaryField.getText()));
        job.setSalaryMax(Integer.parseInt(maxSalaryField.getText()));
        job.setDescription(descArea.getText().trim());
        job.setSkills(new ArrayList<>(selectedSkills));
        job.setAccommodationsAvailable(accYes.isSelected());
        job.setAccommodations(new ArrayList<>(selectedAccommodations));
        job.setApplicantTypes(new ArrayList<>(selectedTypes));
        job.setExperienceRequired(expYes.isSelected());
        job.setExperienceLength(expLength);
        job.setLocation((String) locationBox.getSelectedItem());
        job.setCity(selectedCity);
        job.setLogoPath(logoPath);
        if (editingJob != null && statusBox != null) {
            job.setStatus((String) statusBox.getSelectedItem());
        }

        if (editingJob == null) {
            Database.createJob(job);
            JOptionPane.showMessageDialog(this, "Job posted successfully!");
        } else {
            Database.updateJob(job);
            JOptionPane.showMessageDialog(this, "Job details updated!");
        }
        getDashboard().onMenuClick("Jobs");
    }

    private void fillOldAnswers() {
        titleField.setText(editingJob.getTitle());
        categoryBox.setSelectedItem(editingJob.getCategory());
        jobTypeBox.setSelectedItem(editingJob.getJobType());
        workplaceBox.setSelectedItem(editingJob.getWorkplaceType());
        minSalaryField.setText(String.valueOf(editingJob.getSalaryMin()));
        maxSalaryField.setText(String.valueOf(editingJob.getSalaryMax()));
        descArea.setText(editingJob.getDescription());

        if (statusBox != null) {
            statusBox.setSelectedItem(editingJob.getStatus());
        }

        selectedSkills = new ArrayList<>(editingJob.getSkills());
        skillsArea.setText(String.join(", ", selectedSkills));

        if (editingJob.isAccommodationsAvailable()) {
            accYes.setSelected(true);
            chooseAccBtn.setEnabled(true);
            selectedAccommodations = new ArrayList<>(editingJob.getAccommodations());
            accommodationsArea.setText(String.join(", ", selectedAccommodations));
        } else {
            accNo.setSelected(true);
        }

        selectedTypes = new ArrayList<>(editingJob.getApplicantTypes());
        typesArea.setText(String.join(", ", selectedTypes));

        if (editingJob.isExperienceRequired()) {
            expYes.setSelected(true);
            chooseExpBtn.setEnabled(true);
            expLength = editingJob.getExperienceLength();
            expLengthLabel.setText(expLength);
        } else {
            expNo.setSelected(true);
        }

        fillingForm = true;
        locationBox.setSelectedItem(editingJob.getLocation());
        fillingForm = false;
        selectedCity = editingJob.getCity();
        cityLabel.setText("City: " + selectedCity);

        logoPath = editingJob.getLogoPath();
        if (logoPath != null && !logoPath.isEmpty()) {
            ImageIcon icon = new ImageIcon(logoPath);
            Image scaled = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            logoPreview.removeAll();
            logoPreview.add(new JLabel(new ImageIcon(scaled)), BorderLayout.CENTER);
        }
    }

    // hints sa pag sagot
    private String[] addHint(String[] options, String hint) {
        String[] withHint = new String[options.length + 1];
        withHint[0] = hint;
        System.arraycopy(options, 0, withHint, 1, options.length);
        return withHint;
    }

    private JPanel makeField(String label, JTextField field, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#e2e8f0"), 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        field.setToolTipText(placeholder);
        panel.add(makeLabel(label), BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel makeComboField(String label, JComboBox<String> box) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        box.setFont(new Font("Arial", Font.PLAIN, 14));
        box.setPreferredSize(new Dimension(0, 38));
        box.setBackground(Color.WHITE);
        panel.add(makeLabel(label), BorderLayout.NORTH);
        panel.add(box, BorderLayout.CENTER);
        return panel;
    }

    private JPanel makeSalaryField() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.add(makeLabel("Salary Range * (numbers only)"), BorderLayout.NORTH);

        JPanel boxes = new JPanel(new GridLayout(1, 2, 10, 0));
        boxes.setOpaque(false);
        minSalaryField = makeNumberField();
        maxSalaryField = makeNumberField();
        boxes.add(wrapPeso(minSalaryField));
        boxes.add(wrapPeso(maxSalaryField));
        panel.add(boxes, BorderLayout.CENTER);
        return panel;
    }

    private JTextField makeNumberField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.isEmpty() || text.matches("\\d+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        return field;
    }

    private JPanel wrapPeso(JTextField field) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(Color.WHITE);
        wrap.setBorder(BorderFactory.createLineBorder(Color.decode("#e2e8f0"), 1));
        JLabel peso = new JLabel("  ₱ ");
        peso.setFont(new Font("Arial", Font.BOLD, 14));
        peso.setForeground(Color.decode("#4b5563"));
        wrap.add(peso, BorderLayout.WEST);
        wrap.add(field, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel twoColumns(JComponent left, JComponent right) {
        JPanel row = new JPanel(new GridLayout(1, 2, 20, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        row.add(left);
        row.add(right);
        return row;
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.decode("#0d0d0d"));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        return label;
    }

    private JTextArea makeDisplayArea(String emptyText) {
        JTextArea area = new JTextArea(emptyText);
        area.setFont(new Font("Arial", Font.PLAIN, 13));
        area.setForeground(Color.decode("#4b5563"));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setFocusable(false);
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#e2e8f0"), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return area;
    }

    private JButton makeGrayButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private JButton makeBlueButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.decode("#2563eb"));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel makeNavButtons(java.awt.event.ActionListener backAction, String nextText, java.awt.event.ActionListener nextAction) {
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        nav.setOpaque(false);
        nav.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (backAction != null) {
            JButton backBtn = makeGrayButton("< Back");
            backBtn.setPreferredSize(new Dimension(110, 40));
            backBtn.addActionListener(backAction);
            nav.add(backBtn);
        }

        JButton nextBtn = makeBlueButton(nextText);
        nextBtn.setPreferredSize(new Dimension(130, 40));
        if (nextAction != null) {
            nextBtn.addActionListener(nextAction);
        }
        nav.add(nextBtn);
        return nav;
    }

    private boolean warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Missing answer", JOptionPane.WARNING_MESSAGE);
        return false;
    }

    private void showCheckboxPopup(String title, String[] options, List<String> target, boolean allowOthers) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        List<JCheckBox> boxes = new ArrayList<>();
        for (String option : options) {
            JCheckBox box = new JCheckBox(option);
            box.setFont(new Font("Arial", Font.PLAIN, 14));
            box.setSelected(target.contains(option));
            box.setAlignmentX(Component.LEFT_ALIGNMENT);
            boxes.add(box);
            listPanel.add(box);
        }

        JTextField othersField = new JTextField();
        if (allowOthers) {
            JPanel othersRow = new JPanel(new BorderLayout(8, 0));
            othersRow.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
            othersRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
            JLabel othersLabel = new JLabel("Others:");
            othersLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            List<String> others = new ArrayList<>(target);
            for (String o : options) others.remove(o);
            othersField.setText(String.join(", ", others));
            othersRow.add(othersLabel, BorderLayout.WEST);
            othersRow.add(othersField, BorderLayout.CENTER);
            listPanel.add(othersRow);
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setPreferredSize(new Dimension(320, 260));
        scroll.setBorder(null);

        JButton okBtn = makeBlueButton("OK");
        okBtn.addActionListener(e -> {
            target.clear();
            for (JCheckBox box : boxes) {
                if (box.isSelected()) {
                    target.add(box.getText());
                }
            }
            if (allowOthers && !othersField.getText().trim().isEmpty()) {
                for (String item : othersField.getText().split(",")) {
                    String clean = item.trim();
                    if (!clean.isEmpty() && !target.contains(clean)) {
                        target.add(clean);
                    }
                }
            }
            dialog.dispose();
        });
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(okBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(scroll, BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private String showRadioPopup(String title, String[] options, String current) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        ButtonGroup group = new ButtonGroup();
        List<JRadioButton> radios = new ArrayList<>();
        for (String option : options) {
            JRadioButton radio = new JRadioButton(option);
            radio.setFont(new Font("Arial", Font.PLAIN, 14));
            radio.setSelected(option.equals(current));
            radio.setAlignmentX(Component.LEFT_ALIGNMENT);
            group.add(radio);
            radios.add(radio);
            listPanel.add(radio);
        }

        final String[] result = {null};
        JButton okBtn = makeBlueButton("OK");
        okBtn.addActionListener(e -> {
            for (JRadioButton radio : radios) {
                if (radio.isSelected()) {
                    result[0] = radio.getText();
                }
            }
            dialog.dispose();
        });
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(okBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(listPanel), BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setSize(320, Math.min(120 + options.length * 32, 400));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        return result[0];
    }
}