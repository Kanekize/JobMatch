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
    private String currentStarFilter = "All Stars";
    private String currentSortOrder = "Most Recent";
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


        JComboBox<String> starFilterBox = new JComboBox<>(new String[]{"All Stars", "5 Stars", "4 Stars & up", "3 Stars & up"});
        starFilterBox.setFont(new Font("Inter", Font.PLAIN, 13));
        starFilterBox.setBackground(Color.WHITE);
        starFilterBox.setPreferredSize(new Dimension(120, 38));
        starFilterBox.addActionListener(e -> {
            currentStarFilter = (String) starFilterBox.getSelectedItem();
            refresh();
        });
        filterPanel.add(starFilterBox);


        JComboBox<String> sortDateBox = new JComboBox<>(new String[]{"Most Recent", "Oldest First"});
        sortDateBox.setFont(new Font("Inter", Font.PLAIN, 13));
        sortDateBox.setBackground(Color.WHITE);
        sortDateBox.setPreferredSize(new Dimension(130, 38));
        sortDateBox.addActionListener(e -> {
            currentSortOrder = (String) sortDateBox.getSelectedItem();
            refresh();
        });
        filterPanel.add(sortDateBox);

        JComboBox<String> filterBox = new JComboBox<>(new String[]{"All", "Active", "Closed", "Favorites"});
        filterBox.setFont(new Font("Inter", Font.PLAIN, 13));
        filterBox.setBackground(Color.WHITE);
        filterBox.setPreferredSize(new Dimension(110, 38));
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
        if (currentSortOrder.equals("Most Recent")) {
            jobs.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        } else {
            jobs.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
        }

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

            double ratingValue = 4.0;
            if (job.getTitle().contains("4")) ratingValue = 4.5;
            if (job.getTitle().contains("3")) ratingValue = 3.0;
            if (job.getTitle().contains("2")) ratingValue = 2.0;

            if (currentStarFilter.equals("5 Stars") && ratingValue < 5.0) continue;
            if (currentStarFilter.equals("4 Stars & up") && ratingValue < 4.0) continue;
            if (currentStarFilter.equals("3 Stars & up") && ratingValue < 3.0) continue;

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
        JPanel card = new JPanel(new BorderLayout(16, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#dde3ec"), 14, 1),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        card.add(makeLogoBox(job, 72), BorderLayout.WEST);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(job.getTitle());
        titleLabel.setFont(new Font("Inter", Font.BOLD, 17));
        titleLabel.setForeground(Color.decode("#0d0d0d"));
        titleRow.add(titleLabel);

        double averageRating = Database.getAverageJobRating(job.getId());
        int totalReviewsCount = Database.getTotalRatingCount(job.getId());

        if (totalReviewsCount > 0) {
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
        center.add(Box.createVerticalStrut(3));

        JLabel infoLabel = new JLabel(job.getCategory() + "  |  " + job.getCity() + ", " + job.getLocation()
                + "  |  " + job.getWorkplaceType() + "  |  " + job.getJobType());
        infoLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        infoLabel.setForeground(Color.decode("#6b7280"));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(infoLabel);
        center.add(Box.createVerticalStrut(6));

        JTextArea skillsArea = makeWrapText("Skills: " + job.getSkillsText(), 12, Color.decode("#4b5563"));
        center.add(skillsArea);

        JTextArea salaryArea = makeWrapText("Salary: " + job.getSalaryText() + " per month", 12, Color.decode("#1d4ed8"));
        center.add(salaryArea);

        card.add(center, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);

        JPanel topStats = new JPanel();
        topStats.setLayout(new BoxLayout(topStats, BoxLayout.Y_AXIS));
        topStats.setOpaque(false);

        JLabel statusLabel = makeStatusBadge(job.getStatus());
        statusLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        String date = job.getCreatedAt() == null ? "" : job.getCreatedAt().split(" ")[0];
        JLabel postedLabel = new JLabel("Posted " + date);
        postedLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        postedLabel.setForeground(Color.decode("#6b7280"));
        postedLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel applicantsLabel = new JLabel(Database.countApplicants(job.getId()) + " applicants");
        applicantsLabel.setFont(new Font("Inter", Font.BOLD, 12));
        applicantsLabel.setForeground(Color.decode("#2563eb"));
        applicantsLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        topStats.add(statusLabel);
        topStats.add(Box.createVerticalStrut(8));
        topStats.add(applicantsLabel);
        topStats.add(Box.createVerticalStrut(4));
        topStats.add(postedLabel);

        JPanel statsWrap = new JPanel(new BorderLayout());
        statsWrap.setOpaque(false);
        statsWrap.add(topStats, BorderLayout.EAST);
        statsWrap.setAlignmentX(Component.RIGHT_ALIGNMENT);
        right.add(statsWrap);
        right.add(Box.createVerticalStrut(18));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton favBtn = makeFavoriteButton(job.isFavorite());
        favBtn.addActionListener(e -> {
            Database.setJobFavorite(job.getId(), !job.isFavorite());
            refresh();
        });
        JButton viewBtn = makeLinkButton("View Applicants");
        viewBtn.addActionListener(e -> getDashboard().showPage(new ViewApplicantsPage(getUser(), job, getDashboard())));
        JButton editBtn = makeSecondaryButton("Edit Details");
        editBtn.addActionListener(e -> getDashboard().showPage(new PostJobPage(getUser(), job)));

        favBtn.setPreferredSize(new Dimension(102, 32));
        viewBtn.setPreferredSize(new Dimension(130, 32));
        editBtn.setPreferredSize(new Dimension(104, 32));

        actions.add(favBtn);
        actions.add(viewBtn);
        actions.add(editBtn);
        actions.setAlignmentX(Component.RIGHT_ALIGNMENT);
        right.add(actions);
        right.setPreferredSize(new Dimension(360, right.getPreferredSize().height));
        card.add(right, BorderLayout.EAST);

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
        btn.setPreferredSize(new Dimension(160, 32));
        btn.setMargin(new Insets(0, 12, 0, 12));
        return btn;
    }

    private JButton makeSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Inter", Font.BOLD, 12));
        btn.setForeground(Color.decode("#1d4ed8"));
        btn.setBackground(Color.WHITE);
        btn.setBorder(new RoundedLineBorder(Color.decode("#bfdbfe"), 9, 1));
        btn.setContentAreaFilled(true);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(160, 32));
        btn.setMargin(new Insets(0, 12, 0, 12));
        return btn;
    }

    private JButton makeFavoriteButton(boolean isFavorite) {
        JButton btn = new JButton(isFavorite ? "★ Favorited" : "☆ Favorite");
        btn.setFont(new Font("Inter", Font.BOLD, 12));
        btn.setForeground(isFavorite ? Color.decode("#1d4ed8") : Color.decode("#374151"));
        btn.setBackground(isFavorite ? Color.decode("#e1efff") : Color.decode("#f8fafc"));
        btn.setBorder(new RoundedLineBorder(isFavorite ? Color.decode("#93c5fd") : Color.decode("#d7dce3"), 9, 1));
        btn.setContentAreaFilled(true);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(160, 32));
        btn.setMargin(new Insets(0, 12, 0, 12));
        return btn;
    }

    private JLabel makeStatusBadge(String status) {
        JLabel label = new JLabel(status);
        label.setFont(new Font("Inter", Font.BOLD, 11));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(status.equals("Active") ? Color.decode("#86efac") : Color.decode("#d1d5db"), 8, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        if (status.equals("Active")) {
            label.setBackground(Color.decode("#ecfdf3"));
            label.setForeground(Color.decode("#15803d"));
        } else {
            label.setBackground(Color.decode("#f3f4f6"));
            label.setForeground(Color.decode("#4b5563"));
        }
        return label;
    }
    static JTextArea makeWrapText(String text, int fontSize, Color color) {
        JTextArea area = new JTextArea(text);
        area.setFont(new Font("Inter", Font.PLAIN, fontSize));
        area.setForeground(color);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        int approxCharsPerLine = 90;
        int rowEstimate = Math.max(1, (int) Math.ceil((double) Math.max(0, text.length()) / approxCharsPerLine));
        area.setRows(rowEstimate);
        area.setEditable(false);
        area.setOpaque(false);
        area.setFocusable(false);
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        area.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
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

}//end