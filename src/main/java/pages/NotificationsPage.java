
package pages;

import database.NotificationRepository;
import database.NotificationRepository.Notification;
import users.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class NotificationsPage extends JPanel {

    private final User user;

    private final DefaultListModel<Notification> model =
        new DefaultListModel<>();

    private final JList<Notification> notificationList =
        new JList<>(model);

    private final JTextArea detailsArea = new JTextArea();

    private final JLabel summary = new JLabel(" ");

    public NotificationsPage(User user) {
        this.user = user;

        setLayout(new BorderLayout(0, 15));
        setBackground(Color.decode("#F7F9FC"));

        setBorder(BorderFactory.createEmptyBorder(
            24, 28, 24, 28
        ));

        // Main title
        JLabel title = new JLabel("Notifications");

        title.setFont(new Font(
            "Arial", Font.BOLD, 28
        ));

        title.setForeground(Color.decode("#173B63"));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        header.add(title, BorderLayout.WEST);
        header.add(summary, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Notification list
        notificationList.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        notificationList.setFixedCellHeight(48);

        notificationList.setCellRenderer(
            new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(
                        JList<?> list,
                        Object value,
                        int index,
                        boolean selected,
                        boolean hasFocus) {

                    JLabel label = (JLabel)
                        super.getListCellRendererComponent(
                            list, value, index,
                            selected, hasFocus
                        );

                    Notification n = (Notification) value;

                    label.setText(
                        (n.isRead ? "[Read] " : "[New] ") +
                        n.title + "  |  " + n.createdAt
                    );

                    label.setFont(new Font(
                        "Arial",
                        n.isRead ? Font.PLAIN : Font.BOLD,
                        13
                    ));

                    label.setBorder(
                        BorderFactory.createEmptyBorder(
                            0, 12, 0, 12
                        )
                    );

                    return label;
                }
            }
        );

        // Notification details area
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);

        detailsArea.setFont(new Font(
            "Arial", Font.PLAIN, 15
        ));

        detailsArea.setBorder(
            BorderFactory.createEmptyBorder(
                14, 14, 14, 14
            )
        );

        // Two-column layout
        JPanel center = new JPanel(
            new GridLayout(1, 2, 14, 0)
        );

        center.setOpaque(false);

        center.add(
            new JScrollPane(notificationList)
        );

        center.add(
            new JScrollPane(detailsArea)
        );

        add(center, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 0)
        );

        buttons.setOpaque(false);

        JButton refreshButton =
            new JButton("Refresh");

        JButton readButton =
            new JButton("Mark Selected as Read");

        JButton readAllButton =
            new JButton("Mark All as Read");

        buttons.add(refreshButton);
        buttons.add(readButton);
        buttons.add(readAllButton);

        add(buttons, BorderLayout.SOUTH);

        // Button actions
        notificationList.addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {
                showDetails();
            }
        });

        refreshButton.addActionListener(
            e -> loadNotifications()
        );

        readButton.addActionListener(
            e -> markSelected()
        );

        readAllButton.addActionListener(
            e -> markAll()
        );

        loadNotifications();
    }

    // Load notifications for the logged-in user.
    private void loadNotifications() {

        try {
            List<Notification> items =
                NotificationRepository.getNotifications(
                    user.getEmail()
                );

            model.clear();

            int unread = 0;

            for (Notification item : items) {

                model.addElement(item);

                if (!item.isRead) {
                    unread++;
                }
            }

            summary.setText(
                unread + " unread / " +
                items.size() + " total"
            );

            detailsArea.setText(
                items.isEmpty()
                    ? "No notifications yet."
                    : "Select a notification to view its details."
            );

        } catch (SQLException e) {
            showError(e);
        }
    }

    // Show selected notification details.
    private void showDetails() {

        Notification selected =
            notificationList.getSelectedValue();

        if (selected == null) {
            return;
        }

        detailsArea.setText(
            selected.title + "\n" +
            selected.createdAt + "\n\n" +
            selected.body
        );

        detailsArea.setCaretPosition(0);
    }

    // Mark one notification as read.
    private void markSelected() {

        Notification selected =
            notificationList.getSelectedValue();

        if (selected == null) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a notification first."
            );

            return;
        }

        try {
            NotificationRepository.markAsRead(
                selected.id,
                user.getEmail()
            );

            loadNotifications();

        } catch (SQLException e) {
            showError(e);
        }
    }

    // Mark all notifications as read.
    private void markAll() {

        try {
            NotificationRepository.markAllAsRead(
                user.getEmail()
            );

            loadNotifications();

        } catch (SQLException e) {
            showError(e);
        }
    }

    // Display database errors.
    private void showError(SQLException e) {

        JOptionPane.showMessageDialog(
            this,
            e.getMessage(),
            "Notifications Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
