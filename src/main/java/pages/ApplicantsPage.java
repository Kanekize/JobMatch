
package pages;

import database.Database;
import dashboards.Dashboard;
import models.Applicant;
import models.Job;
import users.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ApplicantsPage extends JPanel {

    private final User employer;

    private final JTextField searchBox =
        new JTextField(18);

    private final JComboBox<String> statusBox =
        new JComboBox<>();

    private final JComboBox<String> jobBox =
        new JComboBox<>();

    private final JLabel resultLabel =
        new JLabel(" ");

    private final DefaultTableModel tableModel;
    private final JTable table;

    private List<Applicant> applicants = new ArrayList<>();
    private List<Applicant> visibleApplicants = new ArrayList<>();

    private static final String[] STATUSES = {
        "All Statuses",
        "Under Review",
        "Interview",
        "Rejected",
        "Accepted"
    };

    public ApplicantsPage(User employer) {

        this.employer = employer;

        setLayout(new BorderLayout(0, 16));

        setBackground(Color.decode("#F7F9FC"));

        setBorder(BorderFactory.createEmptyBorder(
            25, 28, 25, 28
        ));

        // Page title
        JLabel title = new JLabel("Applicants");

        title.setFont(
            new Font("Inter", Font.BOLD, 28)
        );

        title.setForeground(Color.decode("#173B63"));

        // Search and filtering controls
        JPanel filters = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 10)
        );

        filters.setBackground(Color.WHITE);

        filters.add(new JLabel("Search:"));
        filters.add(searchBox);

        for (String s : STATUSES) {
            statusBox.addItem(s);
        }

        filters.add(statusBox);

        jobBox.addItem("All Jobs");
        filters.add(jobBox);

        JButton searchButton =
            new JButton("Search / Refresh");

        filters.add(searchButton);

        JPanel top = new JPanel(
            new BorderLayout(0, 12)
        );

        top.setOpaque(false);

        top.add(title, BorderLayout.NORTH);
        top.add(filters, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);

        // Applicant table
        tableModel = new DefaultTableModel(
            new String[]{
                "Name",
                "Email",
                "Job",
                "Status",
                "Applied On"
            }, 0
        ) {

            @Override
            public boolean isCellEditable(
                    int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);

        table.setRowHeight(36);

        table.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        table.setAutoCreateRowSorter(true);

        add(
            new JScrollPane(table),
            BorderLayout.CENTER
        );

        JPanel actions = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 6)
        );

        actions.setOpaque(false);

        JButton viewButton = new JButton("View Profile");
        JButton statusButton = new JButton("Update Status");
        JButton messageButton = new JButton("Message");

        actions.add(viewButton);
        actions.add(statusButton);
        actions.add(messageButton);
        actions.add(resultLabel);

        add(actions, BorderLayout.SOUTH);

        searchButton.addActionListener(
            e -> refresh()
        );

        searchBox.addActionListener(
            e -> refresh()
        );

        viewButton.addActionListener(
            e -> showProfile()
        );

        statusButton.addActionListener(
            e -> changeStatus()
        );

        messageButton.addActionListener(
            e -> messageApplicant()
        );

        loadJobFilter();
        refresh();
    }


    private void loadJobFilter() {
        Set<String> titles = new LinkedHashSet<String>();
        List<Job> jobs = Database.getJobsByEmployer(employer.getEmail());
        for (Job job : jobs) {
            titles.add(job.getTitle());
        }

        for (String title : titles) {
            jobBox.addItem(title);
        }
    }

    // Loads applicants into the table
    private void refresh() {
        applicants = Database.getApplicantsByEmployer(employer.getEmail());

        String query = searchBox.getText() == null ? "" : searchBox.getText().trim().toLowerCase();
        String statusFilter = (String) statusBox.getSelectedItem();
        String jobFilter = (String) jobBox.getSelectedItem();

        tableModel.setRowCount(0);
        visibleApplicants.clear();

        for (Applicant person : applicants) {
            if (!query.isEmpty()
                    && !containsIgnoreCase(person.getName(), query)
                    && !containsIgnoreCase(person.getEmail(), query)
                    && !containsIgnoreCase(person.getJobTitle(), query)
                    && !containsIgnoreCase(person.getDetails(), query)) {
                continue;
            }

            if (!"All Statuses".equals(statusFilter)
                    && !statusFilter.equals(person.getStatus())) {
                continue;
            }

            if (!"All Jobs".equals(jobFilter)
                    && !jobFilter.equals(person.getJobTitle())) {
                continue;
            }

            visibleApplicants.add(person);
            tableModel.addRow(new Object[]{
                person.getName(),
                emptyValue(person.getEmail()),
                emptyValue(person.getJobTitle()),
                person.getStatus(),
                person.getAppliedAt()
            });
        }

        resultLabel.setText(
            visibleApplicants.isEmpty()
                ? "No applications found yet."
                : visibleApplicants.size() + " application(s) found"
        );

    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    private String emptyValue(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private Dashboard getDashboard() {
        return (Dashboard) SwingUtilities.getWindowAncestor(this);
    }

    private void messageApplicant() {
        Applicant p = selected();
        if (p == null) {
            return;
        }

        if (p.getEmail() == null || p.getEmail().isBlank()) {
            JOptionPane.showMessageDialog(
                this,
                "This applicant does not have a messageable email yet."
            );
            return;
        }

        getDashboard().showPage(new MessagesPage(employer, p.getEmail()));
    }

    // Displays database errors
    private void showError(Exception e) {
        JOptionPane.showMessageDialog(
            this,
            e.getMessage(),
            "Applicant Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    // Returns the selected applicant
    private Applicant selected() {
        int viewRow = table.getSelectedRow();

        if (viewRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Select an applicant first."
            );

            return null;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        return visibleApplicants.get(modelRow);
    }

    // Displays placeholders for missing profile data
    private String available(String input) {
        if (input == null || input.isBlank()) {
            return "Not provided yet";
        }
        return input;
    }

    // Shows the selected applicant's profile
    private void showProfile() {
        Applicant p = selected();
        if (p == null) {
            return;
        }

        JTextArea details = new JTextArea(
            "Name: " + p.getName() + "\n" +
            "Email: " + available(p.getEmail()) + "\n" +
            "Applied for: " + available(p.getJobTitle()) + "\n" +
            "Status: " + p.getStatus() + "\n\n" +
            "About: " + available(p.getDetails())
        );

        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        details.setFont(new Font("Inter", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(details);
        scroll.setPreferredSize(new Dimension(500, 320));

        JOptionPane.showMessageDialog(
            this,
            scroll,
            "Applicant Profile",
            JOptionPane.PLAIN_MESSAGE
        );
    }

    // Changes the selected applicant's status
    private void changeStatus() {
        Applicant p = selected();
        if (p == null) {
            return;
        }

        String[] statuses = {"Under Review", "Interview", "Rejected", "Accepted"};
        String newStatus = (String) JOptionPane.showInputDialog(
            this,
            "Choose the new status for " + p.getName() + ":",
            "Update Application",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statuses,
            p.getStatus()
        );

        if (newStatus == null) {
            return;
        }

        Database.updateApplicantStatus(p.getId(), newStatus);
        refresh();
        JOptionPane.showMessageDialog(this, "Status updated successfully.");
    }
}
