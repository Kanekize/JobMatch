package pages;

import database.Database;
import models.Job;
import users.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
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

        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(0, 38));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#e2e8f0"), 1),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refresh(); }
            public void removeUpdate(DocumentEvent e) { refresh(); }
            public void changedUpdate(DocumentEvent e) { refresh(); }
        });
        searchRow.add(searchField, BorderLayout.CENTER);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightButtons.setOpaque(false);

        JButton filterBtn = new JButton("Filter");
        filterBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        filterBtn.setBackground(Color.WHITE);
        filterBtn.setFocusable(false);
        filterBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        filterBtn.setPreferredSize(new Dimension(90, 38));
        filterBtn.addActionListener(e -> showFilterPopup());
        rightButtons.add(filterBtn);

        sortBox = new JComboBox<>(new String[]{"Best Match", "Most Recent"});
        sortBox.setFont(new Font("Arial", Font.PLAIN, 13));
        sortBox.setBackground(Color.WHITE);
        sortBox.setPreferredSize(new Dimension(130, 38));
        sortBox.addActionListener(e -> refresh());
        rightButtons.add(sortBox);

        searchRow.add(rightButtons, BorderLayout.EAST);
        addBlock(searchRow, 15);

        JPanel mainArea = new JPanel(new BorderLayout(20, 0));
        mainArea.setOpaque(false);
        mainArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        mainArea.add(listPanel, BorderLayout.CENTER);

        mainArea.add(makePreferencesPanel(), BorderLayout.EAST);
        addBlock(mainArea, 0);

        refresh();
    }

    private JPanel makePreferencesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#e5e9f0"), 1),
                BorderFactory.createEmptyBorder(15, 18, 15, 18)));
        panel.setPreferredSize(new Dimension(240, 320));

        JLabel title = new JLabel("Your Match Preferences");
        title.setFont(new Font("Arial", Font.BOLD, 15));
        title.setForeground(Color.decode("#0d0d0d"));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        JLabel empty = new JLabel("<html>No preferences yet.<br>This part is not implemented yet.</html>");
        empty.setFont(new Font("Arial", Font.PLAIN, 12));
        empty.setForeground(Color.decode("#6b7280"));
        empty.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(empty);

        return panel;
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

        String search = searchField.getText().toLowerCase();
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
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 15));
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
            } else if (group.equals("Salary Range")) {
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
            }
        }
        return true;
    }

    private boolean matchesSalary(List<String> checked, Job job) {
        int jobMin = job.getSalaryMin();
        int jobMax = job.getSalaryMax();

        for (String range : checked) {
            int filterMin, filterMax;
            if (range.equals(salaryRanges[0])) { filterMin = 0; filterMax = 14999; }
            else if (range.equals(salaryRanges[1])) { filterMin = 15000; filterMax = 30000; }
            else if (range.equals(salaryRanges[2])) { filterMin = 30001; filterMax = 50000; }
            else { filterMin = 50001; filterMax = Integer.MAX_VALUE; }

            if (jobMax >= filterMin && jobMin <= filterMax) return true;
        }
        return false;
    }

    private JPanel makeJobCard(Job job) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#e5e9f0"), 1),
                BorderFactory.createEmptyBorder(15, 18, 15, 18)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));

        card.add(EmployerJobsPage.makeLogoBox(job, 64), BorderLayout.WEST);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel titleLabel = new JLabel(job.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.decode("#0d0d0d"));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoLabel = new JLabel(job.getCity() + ", " + job.getLocation()
                + "  |  " + job.getJobType() + "  |  " + job.getWorkplaceType());
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        infoLabel.setForeground(Color.decode("#6b7280"));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel salaryLabel = new JLabel(job.getSalaryText() + " per month");
        salaryLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        salaryLabel.setForeground(Color.decode("#4b5563"));
        salaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        center.add(titleLabel);
        center.add(infoLabel);
        center.add(salaryLabel);
        center.add(EmployerJobsPage.makeWrapText("Skills: " + job.getSkillsText(), 12, Color.decode("#2563eb")));
        card.add(center, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);

        boolean isFav = Database.isFavoriteForSeeker(getUser().getEmail(), job.getId());
        JButton favBtn = new JButton(isFav ? "unfav" : "fav");
        favBtn.setFont(new Font("Arial", Font.PLAIN, 26));
        favBtn.setForeground(Color.decode("#f59e0b"));
        favBtn.setBorderPainted(false);
        favBtn.setContentAreaFilled(false);
        favBtn.setFocusable(false);
        favBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        favBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        favBtn.setToolTipText(isFav ? "Remove from favorites" : "Save to favorites");
        favBtn.addActionListener(e -> {
            Database.toggleFavoriteForSeeker(getUser().getEmail(), job.getId());
            refresh();
        });

        JLabel matchLabel = new JLabel("0% Match");
        matchLabel.setFont(new Font("Arial", Font.BOLD, 20));
        matchLabel.setForeground(Color.decode("#2563eb"));
        matchLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel matchNote = new JLabel("Matching not available yet");
        matchNote.setFont(new Font("Arial", Font.PLAIN, 11));
        matchNote.setForeground(Color.decode("#6b7280"));
        matchNote.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JButton viewBtn = new JButton("View Job");
        viewBtn.setBackground(Color.decode("#2563eb"));
        viewBtn.setForeground(Color.WHITE);
        viewBtn.setFont(new Font("Arial", Font.BOLD, 13));
        viewBtn.setBorderPainted(false);
        viewBtn.setOpaque(true);
        viewBtn.setFocusable(false);
        viewBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        viewBtn.setMaximumSize(new Dimension(120, 36));
        viewBtn.addActionListener(e -> getDashboard().showPage(new JobDetailsPage(getUser(), job)));

        right.add(favBtn);
        right.add(matchLabel);
        right.add(matchNote);
        right.add(Box.createVerticalStrut(15));
        right.add(viewBtn);
        card.add(right, BorderLayout.EAST);

        return card;
    }

    //all filter for jobs (job seek)
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
        addFilterGroup(groupsPanel, boxesByGroup, "Salary Range", salaryRanges);
        addFilterGroup(groupsPanel, boxesByGroup, "Experience Required", new String[]{"Yes", "No"});
        addFilterGroup(groupsPanel, boxesByGroup, "Job Type", new String[]{"Full-time", "Part-time", "Contract"});
        addFilterGroup(groupsPanel, boxesByGroup, "Workplace Type", new String[]{"On-site", "Remote Work", "Hybrid Work"});
        addFilterGroup(groupsPanel, boxesByGroup, "Workplace Accommodations Available", new String[]{"Yes", "No"});

        JScrollPane scroll = new JScrollPane(groupsPanel);
        scroll.setPreferredSize(new Dimension(340, 420));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JButton applyBtn = new JButton("Apply Filters");
        applyBtn.setBackground(Color.decode("#2563eb"));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setFont(new Font("Arial", Font.BOLD, 13));
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
        groupLabel.setFont(new Font("Arial", Font.BOLD, 14));
        groupLabel.setForeground(Color.decode("#12376b"));
        groupLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        groupLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        parent.add(groupLabel);

        List<JCheckBox> boxes = new ArrayList<>();
        List<String> previouslyChecked = filterChoices.getOrDefault(groupName, new ArrayList<>());
        for (String option : options) {
            JCheckBox box = new JCheckBox(option);
            box.setFont(new Font("Arial", Font.PLAIN, 13));
            box.setSelected(previouslyChecked.contains(option));
            box.setAlignmentX(Component.LEFT_ALIGNMENT);
            boxes.add(box);
            parent.add(box);
        }
        boxesByGroup.put(groupName, boxes);
    }
}