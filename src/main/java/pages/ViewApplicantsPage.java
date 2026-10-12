package pages;

import components.RoundedLineBorder;
import database.Database;
import models.Applicant;
import models.Job;
import users.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

// view applicants sa specific post ( not the general view applicant)
public class ViewApplicantsPage extends Page {

    private Job job;
    private JTextField searchField;
    private String currentFilter = "All";
    private JButton[] filterButtons;
    private JPanel listPanel;

    public ViewApplicantsPage(User user, Job job) {
        super(user);
        this.job = job;

        JButton backLink = new JButton("< View Applicants");
        backLink.setFont(new Font("Inter", Font.PLAIN, 14));
        backLink.setForeground(Color.decode("#0d0d0d"));
        backLink.setBorderPainted(false);
        backLink.setContentAreaFilled(false);
        backLink.setFocusable(false);
        backLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLink.setHorizontalAlignment(SwingConstants.LEFT);
        backLink.addActionListener(e -> getDashboard().onMenuClick("Jobs"));

        JPanel backWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backWrap.setOpaque(false);
        backWrap.add(backLink);
        addBlock(backWrap, 5);

        addBlock(makeHeading(job.getTitle(),
                job.getCity() + ", " + job.getLocation() + "  |  " + job.getWorkplaceType(), null), 15);

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
        String[] filters = {"All", "Under Review", "Interview", "Rejected", "Accepted"};
        filterButtons = new JButton[filters.length];
        for (int i = 0; i < filters.length; i++) {
            filterButtons[i] = new JButton(filters[i]);
            filterButtons[i].setFont(new Font("Inter", Font.PLAIN, 12));
            filterButtons[i].setFocusable(false);
            filterButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            filterButtons[i].addActionListener(e -> {
                currentFilter = e.getActionCommand();
                refresh();
            });
            filterPanel.add(filterButtons[i]);
        }
        searchRow.add(filterPanel, BorderLayout.EAST);
        addBlock(searchRow, 15);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        addBlock(listPanel, 0);

        refresh();
    }

    private void refresh() {
        for (JButton btn : filterButtons) {
            if (btn.getActionCommand().equals(currentFilter)) {
                btn.setBackground(Color.decode("#2563eb"));
                btn.setForeground(Color.WHITE);
                btn.setOpaque(true);
                btn.setBorderPainted(false);
            } else {
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.decode("#0d0d0d"));
                btn.setOpaque(true);
                btn.setBorderPainted(true);
            }
        }

        List<Applicant> applicants = Database.getApplicantsByJob(job.getId());
        String search = searchField.getText().toLowerCase();

        listPanel.removeAll();
        boolean anyShown = false;
        for (Applicant applicant : applicants) {
            if (!search.isEmpty() && !applicant.getName().toLowerCase().contains(search)) continue;
            if (!currentFilter.equals("All") && !applicant.getStatus().equals(currentFilter)) continue;
            JPanel card = makeApplicantCard(applicant);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(10));
            anyShown = true;
        }
        if (!anyShown) {
            JLabel emptyLabel = new JLabel("No applicants found.");
            emptyLabel.setFont(new Font("Inter", Font.PLAIN, 15));
            emptyLabel.setForeground(Color.decode("#6b7280"));
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5));
            listPanel.add(emptyLabel);
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel makeApplicantCard(Applicant applicant) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#e5e9f0"), 1),
                BorderFactory.createEmptyBorder(15, 18, 15, 18)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JPanel top = new JPanel(new BorderLayout(15, 0));
        top.setOpaque(false);
        top.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel avatar = new JPanel(new BorderLayout());
        avatar.setPreferredSize(new Dimension(52, 52));
        avatar.setMaximumSize(new Dimension(52, 52));
        avatar.setBackground(Color.decode("#cbd5e1"));
        String letter = applicant.getName().substring(0, 1).toUpperCase();
        JLabel letterLabel = new JLabel(letter, SwingConstants.CENTER);
        letterLabel.setFont(new Font("Inter", Font.BOLD, 22));
        letterLabel.setForeground(Color.decode("#12376b"));
        avatar.add(letterLabel, BorderLayout.CENTER);
        top.add(avatar, BorderLayout.WEST);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel nameLabel = new JLabel(applicant.getName());
        nameLabel.setFont(new Font("Inter", Font.BOLD, 16));
        nameLabel.setForeground(Color.decode("#0d0d0d"));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel detailsLabel = new JLabel(applicant.getDetails());
        detailsLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        detailsLabel.setForeground(Color.decode("#6b7280"));
        detailsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String date = applicant.getAppliedAt() == null ? "" : applicant.getAppliedAt().split(" ")[0];
        JLabel appliedLabel = new JLabel("Applied on " + date);
        appliedLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        appliedLabel.setForeground(Color.decode("#9ca3af"));
        appliedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        center.add(nameLabel);
        center.add(Box.createVerticalStrut(3));
        center.add(detailsLabel);
        center.add(Box.createVerticalStrut(3));
        center.add(appliedLabel);
        top.add(center, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);

        JLabel matchLabel = new JLabel(applicant.getMatchPercent() + "% Match");
        matchLabel.setFont(new Font("Inter", Font.BOLD, 18));
        matchLabel.setForeground(Color.decode("#2563eb"));
        matchLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel statusLabel = new JLabel(applicant.getStatus());
        statusLabel.setFont(new Font("Inter", Font.BOLD, 12));
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        statusLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        if (applicant.getStatus().equals("Rejected")) {
            statusLabel.setForeground(Color.decode("#dc2626"));
            statusLabel.setBackground(Color.decode("#fee2e2"));
        } else if (applicant.getStatus().equals("Accepted")) {
            statusLabel.setForeground(Color.decode("#16a34a"));
            statusLabel.setBackground(Color.decode("#dcfce7"));
        } else if (applicant.getStatus().equals("Interview")) {
            statusLabel.setForeground(Color.decode("#7c3aed"));
            statusLabel.setBackground(Color.decode("#ede9fe"));
        } else {
            statusLabel.setForeground(Color.decode("#2563eb"));
            statusLabel.setBackground(Color.decode("#dbeafe"));
        }

        right.add(matchLabel);
        right.add(Box.createVerticalStrut(6));
        right.add(statusLabel);
        top.add(right, BorderLayout.EAST);

        card.add(top);
        card.add(Box.createVerticalStrut(12));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton resumeBtn = makePlainButton("View Resume/Profile");
        resumeBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                applicant.getName() + "\n\n" + applicant.getDetails(),
                "Applicant Profile", JOptionPane.INFORMATION_MESSAGE));

        JButton scheduleBtn = makePlainButton("Schedule Interview");
        scheduleBtn.addActionListener(e -> {
            Database.updateApplicantStatus(applicant.getId(), "Interview");
            refresh();
        });

        JButton acceptBtn = new JButton("Accept");
        acceptBtn.setFont(new Font("Inter", Font.BOLD, 12));
        acceptBtn.setForeground(Color.WHITE);
        acceptBtn.setBackground(Color.decode("#16a34a"));
        styleActionButton(acceptBtn);
        acceptBtn.addActionListener(e -> {
            Database.updateApplicantStatus(applicant.getId(), "Accepted");
            refresh();
        });

        JButton rejectBtn = new JButton("Reject");
        rejectBtn.setFont(new Font("Inter", Font.BOLD, 12));
        rejectBtn.setForeground(Color.WHITE);
        rejectBtn.setBackground(Color.decode("#dc2626"));
        styleActionButton(rejectBtn);
        rejectBtn.addActionListener(e -> {
            int answer = JOptionPane.showConfirmDialog(this,
                    "Reject " + applicant.getName() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                Database.updateApplicantStatus(applicant.getId(), "Rejected");
                refresh();
            }
        });

        JButton removeBtn = new JButton("Remove");
        removeBtn.setFont(new Font("Inter", Font.BOLD, 12));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setBackground(Color.decode("#6b7280"));
        styleActionButton(removeBtn);
        removeBtn.addActionListener(e -> {
            int answer = JOptionPane.showConfirmDialog(this,
                    "Remove " + applicant.getName() + " from this job?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                Database.deleteApplicant(applicant.getId());
                refresh();
            }
        });

        JButton messageBtn = makePlainButton("Message");
        messageBtn.addActionListener(e -> {
            if (applicant.getEmail() == null || applicant.getEmail().isBlank()) {
                JOptionPane.showMessageDialog(this,
                        "This applicant does not have a messageable email yet.",
                        "Message Applicant", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            getDashboard().showPage(new MessagesPage(getUser(), applicant.getEmail()));
        });

        buttons.add(resumeBtn);
        buttons.add(scheduleBtn);
        buttons.add(acceptBtn);
        buttons.add(rejectBtn);
        buttons.add(removeBtn);
        buttons.add(messageBtn);
        card.add(buttons);

        return card;
    }

    private JButton makePlainButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Inter", Font.PLAIN, 12));
        btn.setBackground(Color.WHITE);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#d7dce3"), 10, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        btn.setOpaque(true);
        return btn;
    }

    private void styleActionButton(JButton btn) {
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(btn.getBackground(), 10, 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
    }
}