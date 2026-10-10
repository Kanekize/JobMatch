
package pages;

import database.ApplicantRepository;
import users.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    private List<ApplicantRepository.Applicant> applicants =
        new ArrayList<>();

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
            new Font("Arial", Font.BOLD, 28)
        );

        title.setForeground(Color.decode("#173B63"));

        // Search and filtering controls
        JPanel filters = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 10)
        );

        filters.setBackground(Color.WHITE);

        filters.add(new JLabel("Search:"));
        filters.add(searchBox);

        statusBox.addItem("All Statuses");

        for (String s : ApplicantRepository.STATUSES) {
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

        // Buttons underneath the table
        JPanel actions = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 6)
        );

        actions.setOpaque(false);

        JButton viewButton =
            new JButton("View Profile");

        JButton statusButton =
            new JButton("Update Status");

        actions.add(viewButton);
        actions.add(statusButton);
        actions.add(resultLabel);

        add(actions, BorderLayout.SOUTH);

        // Button actions
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

        loadJobFilter();
        refresh();
    }

    // Retrieves available job titles
    private void loadJobFilter() {

        try {
            for (String title :
                ApplicantRepository.jobTitles(
                    employer.getEmail()
                )) {

                if (!title.equals("All Jobs")) {
                    jobBox.addItem(title);
                }
            }

        } catch (SQLException e) {
            showError(e);
        }
    }

    // Loads applicants into the table
    private void refresh() {

        try {
            applicants = ApplicantRepository.search(
                employer.getEmail(),
                searchBox.getText(),
                (String) statusBox.getSelectedItem(),
                (String) jobBox.getSelectedItem()
            );

            tableModel.setRowCount(0);

            for (ApplicantRepository.Applicant person
                    : applicants) {

                tableModel.addRow(new Object[]{
                    person.name,
                    person.email,
                    person.jobTitle,
                    person.status,
                    person.appliedAt
                });
            }

            resultLabel.setText(
                applicants.isEmpty()
                    ? "No applications found yet."
                    : applicants.size() +
                      " application(s) found"
            );

        } catch (SQLException e) {
            showError(e);
        }
    }

    // Returns the selected applicant
    private ApplicantRepository.Applicant selected() {

        int viewRow = table.getSelectedRow();

        if (viewRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Select an applicant first."
            );

            return null;
        }

        int modelRow =
            table.convertRowIndexToModel(viewRow);

        return applicants.get(modelRow);
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

        ApplicantRepository.Applicant p = selected();

        if (p == null) {
            return;
        }

        JTextArea details = new JTextArea(
            "Name: " + p.name + "\n" +
            "Email: " + p.email + "\n" +
            "Applied for: " + p.jobTitle + "\n" +
            "Status: " + p.status + "\n\n" +

            "Professional Title: " +
                available(p.professionalTitle) + "\n" +

            "Location: " +
                available(p.location) + "\n" +

            "Skills: " +
                available(p.skills) + "\n" +

            "Education: " +
                available(p.education) + "\n" +

            "Experience: " +
                available(p.experience) + "\n" +

            "About: " +
                available(p.about)
        );

        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);

        details.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        JScrollPane scroll =
            new JScrollPane(details);

        scroll.setPreferredSize(
            new Dimension(500, 320)
        );

        JOptionPane.showMessageDialog(
            this,
            scroll,
            "Applicant Profile",
            JOptionPane.PLAIN_MESSAGE
        );
    }

    // Changes the selected applicant's status
    private void changeStatus() {

        ApplicantRepository.Applicant p = selected();

        if (p == null) {
            return;
        }

        String newStatus = (String)
            JOptionPane.showInputDialog(
                this,

                "Choose the new status for " +
                    p.name + ":",

                "Update Application",

                JOptionPane.QUESTION_MESSAGE,

                null,

                ApplicantRepository.STATUSES,

                p.status
            );

        if (newStatus == null) {
            return;
        }

        try {
            boolean updated =
                ApplicantRepository.updateStatus(
                    p.id,
                    employer.getEmail(),
                    newStatus
                );

            if (updated) {
                refresh();

                JOptionPane.showMessageDialog(
                    this,
                    "Status updated successfully."
                );

            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Application not found."
                );
            }

        } catch (SQLException |
                 IllegalArgumentException e) {

            showError(e);
        }
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
}
