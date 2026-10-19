
package pages;

import database.Database;
import dashboards.Dashboard;
import models.Applicant;
import models.Job;
import users.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ApplicantsPage extends JPanel {
    private static final String INTERVIEW_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";

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

        if ("Interview".equals(newStatus)) {
            if (!scheduleInterviewFromTable(p)) {
                return;
            }
        }

        Database.updateApplicantStatus(p.getId(), newStatus);
        refresh();
        JOptionPane.showMessageDialog(this, "Status updated successfully.");
    }

    private boolean scheduleInterviewFromTable(Applicant p) {
        if (p.getEmail() == null || p.getEmail().isBlank()) {
            JOptionPane.showMessageDialog(this,
                "Cannot schedule interview because this applicant has no email.");
            return false;
        }

        Date initialDate = Date.from(LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0)
            .atZone(ZoneId.systemDefault()).toInstant());
        JSpinner dateTimeSpinner = new JSpinner(
            new SpinnerDateModel(initialDate, null, null, java.util.Calendar.MINUTE)
        );
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateTimeSpinner, INTERVIEW_DATE_TIME_PATTERN);
        dateTimeSpinner.setEditor(editor);
        ((JSpinner.DefaultEditor) dateTimeSpinner.getEditor()).getTextField().setColumns(16);

        JTextField locationField = new JTextField("Company Office");
        JComboBox<String> modeBox = new JComboBox<>(new String[]{"On-site", "Online", "Phone"});
        JTextField linkField = new JTextField();
        JTextArea notesArea = new JTextArea(4, 22);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 6, 10);
        form.add(new JLabel("Date & Time:"), c);
        c.gridy++;
        form.add(new JLabel("Interview Location:"), c);
        c.gridy++;
        form.add(new JLabel("Interview Mode:"), c);
        c.gridy++;
        form.add(new JLabel("Online Meeting Link:"), c);
        c.gridy++;
        c.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Notes:"), c);

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 6, 0);
        form.add(dateTimeSpinner, c);
        c.gridy++;
        form.add(locationField, c);
        c.gridy++;
        form.add(modeBox, c);
        c.gridy++;
        form.add(linkField, c);
        c.gridy++;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        form.add(new JScrollPane(notesArea), c);

        Runnable syncModeFields = () -> {
            boolean isOnline = "Online".equals(String.valueOf(modeBox.getSelectedItem()));
            linkField.setEnabled(isOnline);
            if (isOnline) {
                locationField.setText("Online");
                locationField.setEnabled(false);
            } else {
                if ("Online".equalsIgnoreCase(locationField.getText().trim())) {
                    locationField.setText("Company Office");
                }
                locationField.setEnabled(true);
            }
        };
        modeBox.addActionListener(e -> syncModeFields.run());
        syncModeFields.run();

        int result = JOptionPane.showConfirmDialog(
            this, form, "Schedule Interview for " + p.getName(),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) {
            return false;
        }

        Date selectedDate = (Date) dateTimeSpinner.getValue();
        LocalDateTime scheduledAt = LocalDateTime.ofInstant(selectedDate.toInstant(), ZoneId.systemDefault());

        String interviewMode = String.valueOf(modeBox.getSelectedItem());
        String location = locationField.getText().trim();
        if ("Online".equals(interviewMode)) {
            String link = linkField.getText().trim();
            if (link.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please provide the online meeting link.");
                return false;
            }
            location = link;
        } else if (location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Interview location is required.");
            return false;
        }

        boolean saved = Database.saveInterviewSchedule(
            p.getId(),
            p.getJobId(),
            p.getEmail(),
            employer.getEmail(),
            scheduledAt,
            location,
            interviewMode,
            notesArea.getText().trim()
        );
        if (!saved) {
            JOptionPane.showMessageDialog(this, "Could not save interview details.");
            return false;
        }

        return true;
    }
}
