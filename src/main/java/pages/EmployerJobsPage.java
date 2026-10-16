package pages;

import components.RoundedLineBorder;
import components.StatCard;
import database.Database;
import models.Job;
import users.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EmployerJobsPage extends Page {

    private JTextField searchField;
    private String currentFilter = "All";
    private JPanel countersPanel;
    private JPanel listPanel;

    public EmployerJobsPage(User user) {
        super(user);

        JButton postJobBtn = new JButton("Post a Job");
        postJobBtn.setBackground(Color.decode("#2563eb"));
        postJobBtn.setForeground(Color.WHITE);
        postJobBtn.setFont(new Font("Inter", Font.BOLD, 14));
        postJobBtn.setPreferredSize(new Dimension(140, 40));
        postJobBtn.setBorderPainted(false);
        postJobBtn.setOpaque(true);
        postJobBtn.setFocusable(false);
        postJobBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        postJobBtn.addActionListener(e -> getDashboard().showPage(new PostJobPage(getUser())));
        addBlock(makeHeading("My Job Posts", "Manage, track, and monitor your active and draft job listings.", postJobBtn), 15);

        // search bar invinsible row
        JPanel searchRow = new JPanel(new BorderLayout(10, 0));
        searchRow.setOpaque(false);

        searchField = new JTextField();
        searchField.setFont(new Font("Inter", Font.PLAIN, 14));
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

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterPanel.setOpaque(false);

        JComboBox<String> filterBox = new JComboBox<>(new String[]{"All", "Active", "Closed", "Favorites"});
        filterBox.setFont(new Font("Inter", Font.PLAIN, 13));
        filterBox.setBackground(Color.WHITE);
        filterBox.setPreferredSize(new Dimension(140, 38));
        filterBox.addActionListener(e -> {
            currentFilter = (String) filterBox.getSelectedItem();
            refresh();
        });
        filterPanel.add(filterBox);
        searchRow.add(filterPanel, BorderLayout.EAST);
        addBlock(searchRow, 15);

        countersPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        countersPanel.setOpaque(false);
        addBlock(countersPanel, 20);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        addBlock(listPanel, 0);

        refresh();
    }

    private void refresh() {
        List<Job> jobs = Database.getJobsByEmployer(getUser().getEmail());
        jobs.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        int active = 0, closed = 0, totalApplicants = 0;
        for (Job job : jobs) {
            if (job.getStatus().equals("Active")) active++;
            else closed++;
            totalApplicants += Database.countApplicants(job.getId());
        }
        countersPanel.removeAll();
        countersPanel.add(new StatCard("Active Posts", String.valueOf(active), Color.decode("#2563eb")));
        countersPanel.add(new StatCard("Total Applicants", String.valueOf(totalApplicants), Color.decode("#16a34a")));
        countersPanel.add(new StatCard("Closed Posts", String.valueOf(closed), Color.decode("#6b7280")));
        countersPanel.revalidate();
        countersPanel.repaint();

        String search = searchField.getText().toLowerCase();
        List<Job> shown = new ArrayList<>();
        for (Job job : jobs) {
            if (!search.isEmpty()
                    && !job.getTitle().toLowerCase().contains(search)
                    && !job.getCategory().toLowerCase().contains(search)
                    && !job.getCity().toLowerCase().contains(search)) {
                continue;
            }
            if (currentFilter.equals("Active") && !job.getStatus().equals("Active")) continue;
            if (currentFilter.equals("Closed") && !job.getStatus().equals("Closed")) continue;
            if (currentFilter.equals("Favorites") && !job.isFavorite()) continue;
            shown.add(job);
        }

        listPanel.removeAll();
        if (shown.isEmpty()) {
            JLabel emptyLabel = new JLabel("No job posts found.");
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

    private JPanel makeJobCard(Job job) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#e5e9f0"), 1),
                BorderFactory.createEmptyBorder(15, 18, 15, 18)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        card.add(makeLogoBox(job, 64), BorderLayout.WEST);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel titleLabel = new JLabel(job.getTitle());
        titleLabel.setFont(new Font("Inter", Font.BOLD, 18));
        titleLabel.setForeground(Color.decode("#0d0d0d"));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(titleLabel);

        JLabel infoLabel = new JLabel(job.getCategory() + "  |  " + job.getCity() + ", " + job.getLocation()
                + "  |  " + job.getWorkplaceType() + "  |  " + job.getJobType());
        infoLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        infoLabel.setForeground(Color.decode("#6b7280"));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(infoLabel);

        JTextArea skillsArea = makeWrapText("Skills: " + job.getSkillsText(), 12, Color.decode("#4b5563"));
        center.add(skillsArea);

        JTextArea salaryArea = makeWrapText("Salary: " + job.getSalaryText() + " per month", 12, Color.decode("#4b5563"));
        center.add(salaryArea);

        JPanel links = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        links.setOpaque(false);
        links.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton viewBtn = makeLinkButton("View Applicants");
        viewBtn.addActionListener(e -> getDashboard().showPage(new ViewApplicantsPage(getUser(), job)));
        JButton editBtn = makeLinkButton("Edit Details");
        editBtn.addActionListener(e -> getDashboard().showPage(new PostJobPage(getUser(), job)));
        links.add(viewBtn);
        links.add(editBtn);
        center.add(links);

        card.add(center, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);

        JLabel statusLabel = new JLabel("Status: " + job.getStatus());
        statusLabel.setFont(new Font("Inter", Font.BOLD, 13));
        statusLabel.setForeground(job.getStatus().equals("Active") ? Color.decode("#16a34a") : Color.decode("#6b7280"));
        statusLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel applicantsLabel = new JLabel("Applicants: " + Database.countApplicants(job.getId()));
        applicantsLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        applicantsLabel.setForeground(Color.decode("#0d0d0d"));
        applicantsLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        String date = job.getCreatedAt() == null ? "" : job.getCreatedAt().split(" ")[0];
        JLabel postedLabel = new JLabel("Posted: " + date);
        postedLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        postedLabel.setForeground(Color.decode("#6b7280"));
        postedLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JButton favBtn = new JButton(job.isFavorite() ? "unfav" : "fav");
        favBtn.setFont(new Font("Inter", Font.PLAIN, 26));
        favBtn.setForeground(Color.decode("#f59e0b"));
        favBtn.setBorderPainted(false);
        favBtn.setContentAreaFilled(false);
        favBtn.setFocusable(false);
        favBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        favBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        favBtn.addActionListener(e -> {
            Database.setJobFavorite(job.getId(), !job.isFavorite());
            refresh();
        });

        right.add(statusLabel);
        right.add(Box.createVerticalStrut(6));
        right.add(applicantsLabel);
        right.add(Box.createVerticalStrut(6));
        right.add(postedLabel);
        right.add(Box.createVerticalStrut(6));
        right.add(favBtn);
        card.add(right, BorderLayout.EAST);

        return card;
    }

    private JButton makeLinkButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Inter", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.decode("#2563eb"));
        btn.setBorder(new RoundedLineBorder(Color.decode("#2563eb"), 9, 1));
        btn.setContentAreaFilled(true);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(142, 32));
        btn.setMargin(new Insets(0, 12, 0, 12));
        return btn;
    }

    static JTextArea makeWrapText(String text, int fontSize, Color color) {
        JTextArea area = new JTextArea(text);
        area.setFont(new Font("Inter", Font.PLAIN, fontSize));
        area.setForeground(color);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setOpaque(false);
        area.setFocusable(false);
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        area.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        return area;
    }

    static JPanel makeLogoBox(Job job, int size) {
        JPanel box = new JPanel(new BorderLayout());
        box.setPreferredSize(new Dimension(size, size));
        box.setBorder(BorderFactory.createLineBorder(Color.decode("#e5e9f0"), 1));
        box.setBackground(Color.decode("#f1f5fd"));

        if (job.getLogoPath() != null && !job.getLogoPath().isEmpty()) {
            ImageIcon icon = new ImageIcon(job.getLogoPath());
            Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            box.add(new JLabel(new ImageIcon(scaled)), BorderLayout.CENTER);
        } else {
            String letter = job.getTitle() == null || job.getTitle().isEmpty()
                    ? "?" : job.getTitle().substring(0, 1).toUpperCase();
            JLabel letterLabel = new JLabel(letter, SwingConstants.CENTER);
            letterLabel.setFont(new Font("Inter", Font.BOLD, size / 2));
            letterLabel.setForeground(Color.decode("#12376b"));
            box.add(letterLabel, BorderLayout.CENTER);
        }
        return box;
    }
}