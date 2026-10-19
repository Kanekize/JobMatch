package pages;

import database.Database;
import components.RoundedLineBorder;
import models.Job;
import users.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JobSeekerJobsPage extends Page {

    private String category;

    private JTextField searchField;
    private JComboBox<String> sortBox;
    private JPanel listPanel;

    private static final String[] salaryRanges = {"Below ₱15,000", "₱15,000 - ₱30,000", "₱30,000 - ₱50,000", "₱50,000 and up"};
    private static final String[] skillFilters = {"Java", "HTML", "CSS", "JavaScript", "Python", "SQL", "Web Development", "Data Analysis", "Communication", "Customer Service"};

    private Map<String, List<String>> filterChoices = new LinkedHashMap<>();

    public JobSeekerJobsPage(User user, String category) {
        super(user);
        this.category = category;

        addBlock(makeHeading("Recommended Jobs",
                "Opportunities ranked based on your skills, preferences and assessments.", null), 15);

        JPanel searchRow = new JPanel(new BorderLayout(10, 0));
        searchRow.setOpaque(false);

        searchField = new JTextField("Search for jobs...");
        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) { if (searchField.getText().equals("Search for jobs...")) { searchField.setText(""); searchField.setForeground(Color.decode("#0d0d0d")); } }
            @Override public void focusLost(java.awt.event.FocusEvent e) { if (searchField.getText().trim().isEmpty()) { searchField.setText("Search for jobs..."); searchField.setForeground(Color.GRAY); } }
        });
        searchField.setFont(new Font("Inter", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(0, 38));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#d7dce3"), 10, 1),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refresh(); }
            public void removeUpdate(DocumentEvent e) { refresh(); }
            public void changedUpdate(DocumentEvent e) { refresh(); }
        });
        JPanel searchBox = new JPanel(new BorderLayout(8, 0));
        searchBox.setOpaque(false);
        JLabel searchIcon = new JLabel(loadIcon("search.png", 18, 18));
        searchBox.setBorder(BorderFactory.createCompoundBorder(new RoundedLineBorder(Color.decode("#d7dce3"), 10, 1), BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        searchBox.add(searchIcon, BorderLayout.WEST);
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        searchBox.add(searchField, BorderLayout.CENTER);
        searchRow.add(searchBox, BorderLayout.CENTER);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightButtons.setOpaque(false);

        JButton filterBtn = new JButton("Filter");
        filterBtn.setFont(new Font("Inter", Font.PLAIN, 13));
        filterBtn.setBackground(Color.WHITE);
        filterBtn.setFocusable(false);
        filterBtn.setBorder(new RoundedLineBorder(Color.decode("#d7dce3"), 10, 1));
        filterBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        filterBtn.setPreferredSize(new Dimension(90, 38));
        filterBtn.addActionListener(e -> showFilterPopup());
        rightButtons.add(filterBtn);

        sortBox = new JComboBox<>(new String[]{"Best Match", "Most Recent"});
        sortBox.setFont(new Font("Inter", Font.PLAIN, 13));
        sortBox.setBackground(Color.WHITE);
        sortBox.setPreferredSize(new Dimension(130, 38));
        sortBox.addActionListener(e -> refresh());
        rightButtons.add(sortBox);

        searchRow.add(rightButtons, BorderLayout.EAST);
        addBlock(searchRow, 15);

        JPanel mainArea = new JPanel(new GridBagLayout());
        mainArea.setOpaque(false);
        mainArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        GridBagConstraints listConstraints = new GridBagConstraints();
        listConstraints.gridx = 0;
        listConstraints.gridy = 0;
        listConstraints.weightx = 1.0;
        listConstraints.weighty = 1.0;
        listConstraints.fill = GridBagConstraints.BOTH;
        listConstraints.anchor = GridBagConstraints.NORTHWEST;
        listConstraints.insets = new Insets(0, 0, 0, 20);
        mainArea.add(listPanel, listConstraints);

        GridBagConstraints prefConstraints = new GridBagConstraints();
        prefConstraints.gridx = 1;
        prefConstraints.gridy = 0;
        prefConstraints.weightx = 0.0;
        prefConstraints.weighty = 0.0;
        prefConstraints.fill = GridBagConstraints.NONE;
        prefConstraints.anchor = GridBagConstraints.NORTH;
        mainArea.add(makePreferencesPanel(), prefConstraints);
        addBlock(mainArea, 0);

        refresh();
    }

    private JPanel makePreferencesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#d7dce3"), 14, 1),
                BorderFactory.createEmptyBorder(15, 18, 15, 18)));
        panel.setMaximumSize(new Dimension(252, Integer.MAX_VALUE));

        JPanel head = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        head.setOpaque(false);
        head.setAlignmentX(Component.LEFT_ALIGNMENT);
        head.add(new JLabel(loadIcon("filter.png", 18, 18)));

        JLabel title = new JLabel("Your Match Preferences");
        title.setFont(new Font("Inter", Font.BOLD, 15));
        title.setForeground(Color.decode("#0d0d0d"));
        head.add(title);
        panel.add(head);
        panel.add(Box.createVerticalStrut(10));

        panel.add(makePreferenceGroup("Skills", new String[]{"Java", "SQL", "HTML/CSS", "Problem Solving"}));
        panel.add(Box.createVerticalStrut(10));
        panel.add(makePreferenceGroup("Work Arrangement", new String[]{"Remote (Preferred)", "Hybrid (Acceptable)", "On-site (Not Acceptable)"}));
        panel.add(Box.createVerticalStrut(10));
        panel.add(makePreferenceGroup("Location", new String[]{"Quezon City"}));
        panel.add(Box.createVerticalGlue());

        JButton editBtn = new JButton("Edit Preferences");
        editBtn.setFont(new Font("Inter", Font.PLAIN, 13));
        editBtn.setForeground(Color.decode("#2563eb"));
        editBtn.setBackground(Color.WHITE);
        editBtn.setBorderPainted(false);
        editBtn.setContentAreaFilled(false);
        editBtn.setFocusable(false);
        editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(Box.createVerticalStrut(8));
        panel.add(editBtn);

        Dimension pref = panel.getPreferredSize();
        panel.setPreferredSize(new Dimension(252, pref.height));
        panel.setMaximumSize(new Dimension(252, pref.height));

        return panel;
    }

    private JPanel makePreferenceGroup(String title, String[] values) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(title);
        label.setFont(new Font("Inter", Font.BOLD, 13));
        label.setForeground(Color.decode("#0d0d0d"));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.add(label);
        group.add(Box.createVerticalStrut(6));

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        chips.setOpaque(false);
        chips.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String value : values) {
            JLabel chip = new JLabel(value);
            chip.setFont(new Font("Inter", Font.PLAIN, 11));
            chip.setForeground(Color.decode("#2563eb"));
            chip.setOpaque(true);
            chip.setBackground(Color.decode("#e1efff"));
            chip.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            chips.add(chip);
        }
        group.add(chips);
        return group;
    }

    private void refresh() {
        List<Job> jobs = Database.getAllJobs();

        List<Job> inCategory = new ArrayList<>();
        for (Job job : jobs) {
            if (!job.getStatus().equals("Active")) continue;
            if (category == null || job.getCategory().equals(category)) {
                inCategory.add(job);
            }
        }

        String search = searchField.getText().equals("Search for jobs...") ? "" : searchField.getText().trim().toLowerCase();
        List<Job> shown = new ArrayList<>();
        for (Job job : inCategory) {
            if (!search.isEmpty()
                    && !job.getTitle().toLowerCase().contains(search)
                    && !job.getCategory().toLowerCase().contains(search)
                    && !job.getCity().toLowerCase().contains(search)
                    && !job.getSkillsText().toLowerCase().contains(search)) {
                continue;
            }
            if (passesFilters(job)) {
                shown.add(job);
            }
        }

        if (sortBox.getSelectedItem().equals("Most Recent")) {
            shown.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        }

        listPanel.removeAll();
        if (shown.isEmpty()) {
            JLabel emptyLabel = new JLabel("No jobs found.");
            emptyLabel.setFont(new Font("Inter", Font.PLAIN, 15));
            emptyLabel.setForeground(Color.decode("#6b7280"));
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5));
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            listPanel.add(emptyLabel);
        }
        for (Job job : shown) {
            JPanel card = makeJobCard(job);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(10));
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private boolean passesFilters(Job job) {
        for (String group : filterChoices.keySet()) {
            List<String> checked = filterChoices.get(group);
            if (checked.isEmpty()) continue;

            if (group.equals("Favorites")) {
                boolean isFav = Database.isFavoriteForSeeker(getUser().getEmail(), job.getId());
                String answer = isFav ? "Yes" : "No";
                if (!checked.contains(answer)) return false;
            } else if (group.equals("Applicant Type")) {
                boolean any = false;
                for (String c : checked) if (job.getApplicantTypes().contains(c)) any = true;
                if (!any) return false;
            } else if (group.equals("Company Location")) {
                if (!checked.contains(job.getLocation())) return false;
            } else if (group.equals("Required Skills")) {
                boolean any = false;
                for (String c : checked) if (job.getSkills().contains(c)) any = true;
                if (!any) return false;
            } else if (group.equals("Salary")) {
                if (!matchesSalary(checked, job)) return false;
            } else if (group.equals("Experience Required")) {
                String needed = job.isExperienceRequired() ? "Yes" : "No";
                if (!checked.contains(needed)) return false;
            } else if (group.equals("Job Type")) {
                if (!checked.contains(job.getJobType())) return false;
            } else if (group.equals("Workplace Type")) {
                if (!checked.contains(job.getWorkplaceType())) return false;
            } else if (group.equals("Workplace Accommodations Available")) {
                String has = job.isAccommodationsAvailable() ? "Yes" : "No";
                if (!checked.contains(has)) return false;
            } else if (group.equals("Minimum Star Rating")) {
                double jobAvg = Database.getAverageJobRating(job.getId());
                if (checked.contains("5 Stars") && jobAvg < 5.0) return false;
                if (checked.contains("4 Stars & up") && jobAvg < 4.0) return false;
                if (checked.contains("3 Stars & up") && jobAvg < 3.0) return false;
            }
        }
        return true;
    }

    private boolean matchesSalary(List<String> checked, Job job) {
        int salary = job.getSalary();

        for (String range : checked) {
            int filterMin, filterMax;
            if (range.equals(salaryRanges[0])) { filterMin = 0; filterMax = 14999; }
            else if (range.equals(salaryRanges[1])) { filterMin = 15000; filterMax = 30000; }
            else if (range.equals(salaryRanges[2])) { filterMin = 30001; filterMax = 50000; }
            else { filterMin = 50001; filterMax = Integer.MAX_VALUE; }

            if (salary >= filterMin && salary <= filterMax) return true;
        }
        return false;
    }

    private JPanel makeJobCard(Job job) {
        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#e5e7eb"), 16, 1),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));

        JPanel logoWrap = new JPanel(new GridBagLayout());
        logoWrap.setOpaque(false);
        logoWrap.add(EmployerJobsPage.makeLogoBox(job, 118));
        card.add(logoWrap, BorderLayout.WEST);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        final int textColumnOffset = 28;

        JPanel titleRow = new JPanel();
        titleRow.setOpaque(false);
        titleRow.setLayout(new BoxLayout(titleRow, BoxLayout.X_AXIS));
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleRow.setBorder(BorderFactory.createEmptyBorder(0, textColumnOffset, 0, 0));

        JLabel titleLabel = new JLabel(job.getTitle());
        titleLabel.setFont(new Font("Inter", Font.BOLD, 17));
        titleLabel.setForeground(Color.decode("#0d0d0d"));
        titleRow.add(titleLabel);

        double averageRating = Database.getAverageJobRating(job.getId());
        int totalReviewsCount = Database.getTotalRatingCount(job.getId());

        if (totalReviewsCount > 0) {
            titleRow.add(Box.createHorizontalStrut(8));

            JLabel starIconLabel = new JLabel();
            java.io.File file = new java.io.File("src/images/star.png");
            if (!file.exists()) file = new java.io.File("JobMatch/src/images/star.png");

            if (file.exists()) {
                Image img = new ImageIcon(file.getPath()).getImage().getScaledInstance(14, 14, Image.SCALE_SMOOTH);
                starIconLabel.setIcon(new ImageIcon(img));
            } else {
                starIconLabel.setText("★ ");
                starIconLabel.setForeground(Color.decode("#f59e0b"));
            }
            titleRow.add(starIconLabel);

            JLabel starBadge = new JLabel(String.valueOf(averageRating));
            starBadge.setFont(new Font("Inter", Font.BOLD, 13));
            starBadge.setForeground(Color.decode("#f59e0b"));
            titleRow.add(starBadge);

            JButton reviewsLinkBtn = new JButton("(" + totalReviewsCount + " reviews)");
            reviewsLinkBtn.setFont(new Font("Inter", Font.PLAIN, 12));
            reviewsLinkBtn.setForeground(Color.decode("#2563eb"));
            reviewsLinkBtn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#2563eb")));
            reviewsLinkBtn.setContentAreaFilled(false);
            reviewsLinkBtn.setOpaque(false);
            reviewsLinkBtn.setFocusable(false);
            reviewsLinkBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            reviewsLinkBtn.addActionListener(e -> showDetailedReviewsBreakdown(job));
            titleRow.add(reviewsLinkBtn);
        }

        center.add(titleRow);
        center.add(Box.createVerticalStrut(5));

        JPanel companyRow = makeJobInfoRow("building-blue.png", Database.getEmployerCompanyName(job.getEmployerEmail()));
        companyRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel setupRow = makeJobInfoRow(
                "job-location.png",
                job.getCity() + ", " + job.getLocation() + "  •  " + job.getJobType() + "  •  " + job.getWorkplaceType());
        setupRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel salaryRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        salaryRow.setOpaque(false);
        JLabel salaryLabel = new JLabel(formatFixedSalary(job) + " per month");
        salaryLabel.setFont(new Font("Inter", Font.BOLD, 15));
        salaryLabel.setForeground(Color.decode("#1f2937"));
        salaryRow.add(salaryLabel);
        salaryRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel identityGroup = new JPanel();
        identityGroup.setOpaque(false);
        identityGroup.setLayout(new BoxLayout(identityGroup, BoxLayout.Y_AXIS));
        identityGroup.setAlignmentX(Component.LEFT_ALIGNMENT);
        identityGroup.setBorder(BorderFactory.createEmptyBorder(0, textColumnOffset, 0, 0));
        identityGroup.add(companyRow);

        JPanel workSetupGroup = new JPanel();
        workSetupGroup.setOpaque(false);
        workSetupGroup.setLayout(new BoxLayout(workSetupGroup, BoxLayout.Y_AXIS));
        workSetupGroup.setAlignmentX(Component.LEFT_ALIGNMENT);
        workSetupGroup.setBorder(BorderFactory.createEmptyBorder(0, textColumnOffset, 0, 0));
        workSetupGroup.add(setupRow);
        workSetupGroup.add(Box.createVerticalStrut(2));
        workSetupGroup.add(salaryRow);

        JPanel chips = new JPanel();
        chips.setOpaque(false);
        chips.setLayout(new BoxLayout(chips, BoxLayout.X_AXIS));
        chips.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (int i = 0; i < Math.min(3, job.getSkills().size()); i++) {
            JLabel chip = new JLabel(job.getSkills().get(i));
            chip.setFont(new Font("Inter", Font.PLAIN, 11));
            chip.setForeground(Color.decode("#2563eb"));
            chip.setOpaque(true);
            chip.setBackground(Color.decode("#e1efff"));
            chip.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            chips.add(chip);
            if (i < Math.min(3, job.getSkills().size()) - 1) {
                chips.add(Box.createHorizontalStrut(6));
            }
        }

        JPanel skillsGroup = new JPanel();
        skillsGroup.setOpaque(false);
        skillsGroup.setLayout(new BoxLayout(skillsGroup, BoxLayout.Y_AXIS));
        skillsGroup.setAlignmentX(Component.LEFT_ALIGNMENT);
        skillsGroup.setBorder(BorderFactory.createEmptyBorder(0, textColumnOffset, 0, 0));
        skillsGroup.add(chips);

        center.add(identityGroup);
        center.add(Box.createVerticalStrut(6));
        center.add(workSetupGroup);
        center.add(Box.createVerticalStrut(8));
        center.add(skillsGroup);
        card.add(center, BorderLayout.CENTER);

        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(230, 0));
        right.setMaximumSize(new Dimension(230, Integer.MAX_VALUE));

        JPanel matchBox = new JPanel(new GridBagLayout());
        matchBox.setBackground(Color.decode("#f5f9ff"));
        matchBox.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#d9e7ff"), 12, 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        matchBox.setMaximumSize(new Dimension(160, 78));
        matchBox.setPreferredSize(new Dimension(160, 78));

        JPanel matchInner = new JPanel();
        matchInner.setOpaque(false);
        matchInner.setLayout(new BoxLayout(matchInner, BoxLayout.Y_AXIS));
        matchInner.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel matchLabel = new JLabel("0% Match");
        matchLabel.setFont(new Font("Inter", Font.BOLD, 22));
        matchLabel.setForeground(Color.decode("#2563eb"));
        matchLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        matchInner.add(matchLabel);

        boolean favorite = Database.isFavoriteForSeeker(getUser().getEmail(), job.getId());
        JButton favoriteBtn = new JButton();
        favoriteBtn.setFocusable(false);
        favoriteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        favoriteBtn.setBorderPainted(false);
        favoriteBtn.setContentAreaFilled(false);
        favoriteBtn.setOpaque(false);
        favoriteBtn.setMargin(new Insets(0, 0, 0, 0));

        GridBagConstraints mb = new GridBagConstraints();
        mb.gridx = 0;
        mb.gridy = 0;
        mb.weightx = 1.0;
        mb.weighty = 1.0;
        mb.anchor = GridBagConstraints.CENTER;
        mb.fill = GridBagConstraints.NONE;
        matchBox.add(matchInner, mb);

        JPanel favoriteCard = new JPanel(new GridBagLayout());
        favoriteCard.setBackground(Color.WHITE);
        favoriteCard.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#d7dce3"), 12, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        favoriteCard.setPreferredSize(new Dimension(56, 78));
        favoriteCard.setMaximumSize(new Dimension(56, 78));
        favoriteCard.add(favoriteBtn);
        updateFavoriteCardVisual(favoriteBtn, favoriteCard, favorite);
        favoriteBtn.addActionListener(e -> {
            Database.toggleFavoriteForSeeker(getUser().getEmail(), job.getId());
            boolean isFavorite = Database.isFavoriteForSeeker(getUser().getEmail(), job.getId());
            updateFavoriteCardVisual(favoriteBtn, favoriteCard, isFavorite);
        });

        JPanel rightStack = new JPanel();
        rightStack.setOpaque(false);
        rightStack.setLayout(new BoxLayout(rightStack, BoxLayout.X_AXIS));
        rightStack.setAlignmentX(Component.RIGHT_ALIGNMENT);
        favoriteCard.setAlignmentX(Component.RIGHT_ALIGNMENT);
        matchBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
        rightStack.add(matchBox);
        rightStack.add(Box.createHorizontalStrut(8));
        rightStack.add(favoriteCard);

        GridBagConstraints rightConstraints = new GridBagConstraints();
        rightConstraints.gridx = 0;
        rightConstraints.gridy = 0;
        rightConstraints.weightx = 1.0;
        rightConstraints.weighty = 1.0;
        rightConstraints.anchor = GridBagConstraints.EAST;
        rightConstraints.fill = GridBagConstraints.NONE;
        right.add(rightStack, rightConstraints);
        card.add(right, BorderLayout.EAST);

        bindCardOpenAction(card, () -> getDashboard().showPage(new JobDetailsPage(getUser(), job)));

        return card;
    }

    private void showDetailedReviewsBreakdown(Job job) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ratings Breakdown - " + job.getTitle(), true);
        dialog.setSize(560, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel columnsMasterContainer = new JPanel(new GridLayout(1, 5, 8, 0));
        columnsMasterContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        columnsMasterContainer.setBackground(Color.decode("#f8fafc"));

        JPanel detailsContainerPanel = new JPanel(new BorderLayout());
        detailsContainerPanel.setBackground(Color.WHITE);
        detailsContainerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("#e2e8f0")),
                BorderFactory.createEmptyBorder(10, 15, 15, 15)));

        JLabel sectionInfoLabel = new JLabel("Click any star column tab button above to inspect reviewer names list");
        sectionInfoLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        sectionInfoLabel.setForeground(Color.decode("#6b7280"));
        detailsContainerPanel.add(sectionInfoLabel, BorderLayout.CENTER);

        for (int starIndex = 5; starIndex >= 1; starIndex--) {
            final int targetColumnValue = starIndex;
            int reviewMatchesCount = Database.getSpecificStarCount(job.getId(), targetColumnValue);

            JPanel columnBlock = new JPanel(new BorderLayout(0, 6));
            columnBlock.setOpaque(false);

            JButton filterSelectBtn = new JButton(targetColumnValue + " Star (" + reviewMatchesCount + ")");
            filterSelectBtn.setFont(new Font("Inter", Font.BOLD, 12));
            filterSelectBtn.setBackground(Color.WHITE);
            filterSelectBtn.setFocusable(false);
            filterSelectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            filterSelectBtn.addActionListener(clickEvent -> {
                detailsContainerPanel.removeAll();

                JLabel contextTitle = new JLabel("Users who voted " + targetColumnValue + " Stars:");
                contextTitle.setFont(new Font("Inter", Font.BOLD, 14));
                contextTitle.setForeground(Color.decode("#0d0d0d"));
                contextTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
                detailsContainerPanel.add(contextTitle, BorderLayout.NORTH);

                List<String> votersInfoList = Database.getVotersForStarRating(job.getId(), targetColumnValue);
                if (votersInfoList.isEmpty()) {
                    JLabel noVotesMsg = new JLabel("No job seekers have submitted a " + targetColumnValue + "-star rating yet.");
                    noVotesMsg.setFont(new Font("Inter", Font.PLAIN, 13));
                    noVotesMsg.setForeground(Color.decode("#6b7280"));
                    detailsContainerPanel.add(noVotesMsg, BorderLayout.CENTER);
                } else {
                    DefaultListModel<String> stringListModel = new DefaultListModel<>();
                    for (String infoRow : votersInfoList) {
                        stringListModel.addElement(infoRow);
                    }
                    JList<String> itemsDisplayList = new JList<>(stringListModel);
                    itemsDisplayList.setFont(new Font("Inter", Font.PLAIN, 13));
                    detailsContainerPanel.add(new JScrollPane(itemsDisplayList), BorderLayout.CENTER);
                }
                detailsContainerPanel.revalidate();
                detailsContainerPanel.repaint();
            });

            columnBlock.add(filterSelectBtn, BorderLayout.NORTH);
            columnsMasterContainer.add(columnBlock);
        }

        dialog.add(columnsMasterContainer, BorderLayout.NORTH);
        dialog.add(detailsContainerPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }



    private ImageIcon loadIcon(String fileName, int width, int height) {
        java.io.File file = new java.io.File("src/images/" + fileName);
        if (!file.exists()) file = new java.io.File("JobMatch/src/images/" + fileName);
        if (!file.exists()) return null;
        Image image = new ImageIcon(file.getPath()).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    private JPanel makeJobInfoRow(String iconName, String text) {
        return makeJobInfoRow(iconName, text, new Font("Inter", Font.PLAIN, 12), Color.decode("#6b7280"));
    }

    private String formatFixedSalary(Job job) {
        return "₱" + String.format("%,d", job.getSalary());
    }

    private void updateFavoriteIconButton(JButton button, boolean active) {
        ImageIcon icon = loadIcon(active ? "bookmark-active.png" : "bookmark-off.png", 23, 23);
        if (icon != null) {
            button.setIcon(icon);
            button.setText("");
        } else {
            button.setIcon(null);
            button.setText(active ? "★" : "☆");
            button.setForeground(Color.decode(active ? "#2563eb" : "#9ca3af"));
            button.setFont(new Font("Inter", Font.BOLD, 16));
        }
    }

    private void updateFavoriteCardVisual(JButton button, JPanel card, boolean active) {
        updateFavoriteIconButton(button, active);
        card.setBackground(active ? Color.decode("#e1efff") : Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode(active ? "#bfdbfe" : "#d7dce3"), 12, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    private JPanel makeJobInfoRow(String iconName, String text, Font labelFont, Color labelColor) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);

        final int iconWidth = 14;
        final int iconGap = 6;

        if (iconName != null) {
            JLabel icon = new JLabel(loadIcon(iconName, 13, 13));
            Dimension iconSize = new Dimension(iconWidth, iconWidth);
            icon.setPreferredSize(iconSize);
            icon.setMinimumSize(iconSize);
            icon.setMaximumSize(iconSize);

            GridBagConstraints iconConstraints = new GridBagConstraints();
            iconConstraints.gridx = 0;
            iconConstraints.gridy = 0;
            iconConstraints.anchor = GridBagConstraints.WEST;
            iconConstraints.insets = new Insets(0, 0, 0, iconGap);
            row.add(icon, iconConstraints);
        } else {
            JPanel spacer = new JPanel();
            spacer.setOpaque(false);
            Dimension spacerSize = new Dimension(iconWidth + iconGap, 1);
            spacer.setPreferredSize(spacerSize);
            spacer.setMinimumSize(spacerSize);
            spacer.setMaximumSize(spacerSize);

            GridBagConstraints spacerConstraints = new GridBagConstraints();
            spacerConstraints.gridx = 0;
            spacerConstraints.gridy = 0;
            spacerConstraints.anchor = GridBagConstraints.WEST;
            row.add(spacer, spacerConstraints);
        }

        JLabel label = new JLabel(text);
        label.setFont(labelFont);
        label.setForeground(labelColor);

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 1;
        labelConstraints.gridy = 0;
        labelConstraints.weightx = 1.0;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        labelConstraints.anchor = GridBagConstraints.WEST;
        row.add(label, labelConstraints);
        return row;
    }

    private void bindCardOpenAction(Component component, Runnable openAction) {
        if (component instanceof AbstractButton) {
            return;
        }
        component.setCursor(new Cursor(Cursor.HAND_CURSOR));
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openAction.run();
            }
        });
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                bindCardOpenAction(child, openAction);
            }
        }
    }

    private void showFilterPopup() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Filter Jobs", true);
        JPanel groupsPanel = new JPanel();
        groupsPanel.setLayout(new BoxLayout(groupsPanel, BoxLayout.Y_AXIS));
        groupsPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        Map<String, List<JCheckBox>> boxesByGroup = new LinkedHashMap<>();
        addFilterGroup(groupsPanel, boxesByGroup, "Favorites", new String[]{"Yes", "No"});
        addFilterGroup(groupsPanel, boxesByGroup, "Applicant Type",
                new String[]{"Regular Job Seeker", "Student", "Senior Citizen", "Person with Disability (PWD)"});
        addFilterGroup(groupsPanel, boxesByGroup, "Company Location",
                PostJobPage.LOCATIONS.keySet().toArray(new String[0]));
        addFilterGroup(groupsPanel, boxesByGroup, "Required Skills", skillFilters);
        addFilterGroup(groupsPanel, boxesByGroup, "Salary", salaryRanges);
        addFilterGroup(groupsPanel, boxesByGroup, "Experience Required", new String[]{"Yes", "No"});
        addFilterGroup(groupsPanel, boxesByGroup, "Job Type", new String[]{"Full-time", "Part-time", "Contract"});
        addFilterGroup(groupsPanel, boxesByGroup, "Workplace Type", new String[]{"On-site", "Remote Work", "Hybrid Work"});
        addFilterGroup(groupsPanel, boxesByGroup, "Workplace Accommodations Available", new String[]{"Yes", "No"});
        addFilterGroup(groupsPanel, boxesByGroup, "Minimum Star Rating",
                new String[]{"5 Stars", "4 Stars & up", "3 Stars & up"});

        JScrollPane scroll = new JScrollPane(groupsPanel);
        scroll.setPreferredSize(new Dimension(340, 420));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JButton applyBtn = new JButton("Apply Filters");
        applyBtn.setBackground(Color.decode("#2563eb"));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setFont(new Font("Inter", Font.BOLD, 13));
        applyBtn.setBorderPainted(false);
        applyBtn.setOpaque(true);
        applyBtn.setFocusable(false);
        applyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        applyBtn.addActionListener(e -> {
            filterChoices.clear();
            for (String group : boxesByGroup.keySet()) {
                List<String> checked = new ArrayList<>();
                for (JCheckBox box : boxesByGroup.get(group)) {
                    if (box.isSelected()) checked.add(box.getText());
                }
                filterChoices.put(group, checked);
            }
            refresh();
            dialog.dispose();
        });

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFocusable(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> {
            for (String group : boxesByGroup.keySet()) {
                for (JCheckBox box : boxesByGroup.get(group)) {
                    box.setSelected(false);
                }
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        bottom.add(clearBtn);
        bottom.add(applyBtn);

        dialog.setLayout(new BorderLayout());
        dialog.add(scroll, BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addFilterGroup(JPanel parent, Map<String, List<JCheckBox>> boxesByGroup, String groupName, String[] options) {
        JLabel groupLabel = new JLabel(groupName);
        groupLabel.setFont(new Font("Inter", Font.BOLD, 14));
        groupLabel.setForeground(Color.decode("#12376b"));
        groupLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        groupLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        parent.add(groupLabel);

        List<JCheckBox> boxes = new ArrayList<>();
        List<String> previouslyChecked = filterChoices.getOrDefault(groupName, new ArrayList<>());
        for (String option : options) {
            JCheckBox box = new JCheckBox(option);
            box.setFont(new Font("Inter", Font.PLAIN, 13));
            box.setSelected(previouslyChecked.contains(option));
            box.setAlignmentX(Component.LEFT_ALIGNMENT);
            boxes.add(box);
            parent.add(box);
        }
        boxesByGroup.put(groupName, boxes);
    }
}