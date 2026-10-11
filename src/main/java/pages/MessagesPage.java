
package pages;

import database.MessageRepository;
import users.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MessagesPage extends JPanel {

    private final User user;

    private final DefaultListModel<MessageRepository.Contact>
            contactModel = new DefaultListModel<>();

    private final JList<MessageRepository.Contact>
            contactList = new JList<>(contactModel);

    private final JLabel chatTitle =
            new JLabel("Select a contact");

    private final JTextArea conversationArea =
            new JTextArea();

    private final JTextArea messageInput =
            new JTextArea(3, 25);

    public MessagesPage(User user) {

        this.user = user;

        setLayout(new BorderLayout(14, 14));
        setBackground(Color.decode("#F7F9FC"));

        setBorder(BorderFactory.createEmptyBorder(
                24, 26, 24, 26
        ));

        // Main page title
        JLabel heading = new JLabel("Messages");

        heading.setFont(
                new Font("Arial", Font.BOLD, 26)
        );

        heading.setForeground(Color.decode("#173B63"));

        add(heading, BorderLayout.NORTH);

        // LEFT SIDE: Contact list
        JPanel left = new JPanel(
                new BorderLayout(0, 10)
        );

        left.setBackground(Color.WHITE);

        left.setBorder(BorderFactory.createEmptyBorder(
                12, 12, 12, 12
        ));

        left.setPreferredSize(new Dimension(260, 0));

        left.add(
                new JLabel("Contacts"),
                BorderLayout.NORTH
        );

        contactList.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        contactList.setFixedCellHeight(42);

        left.add(
                new JScrollPane(contactList),
                BorderLayout.CENTER
        );

        JButton refreshContacts =
                new JButton("Refresh Contacts");

        refreshContacts.addActionListener(
                e -> loadContacts()
        );

        left.add(
                refreshContacts,
                BorderLayout.SOUTH
        );

        add(left, BorderLayout.WEST);

        // RIGHT SIDE: Conversation
        JPanel right = new JPanel(
                new BorderLayout(0, 12)
        );

        right.setBackground(Color.WHITE);

        right.setBorder(BorderFactory.createEmptyBorder(
                14, 14, 14, 14
        ));

        JPanel header = new JPanel(
                new BorderLayout()
        );

        header.setOpaque(false);

        chatTitle.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        header.add(
                chatTitle,
                BorderLayout.CENTER
        );

        JButton refreshChat =
                new JButton("Refresh Chat");

        refreshChat.addActionListener(
                e -> loadConversation()
        );

        header.add(
                refreshChat,
                BorderLayout.EAST
        );

        right.add(
                header,
                BorderLayout.NORTH
        );

        // Chat history
        conversationArea.setEditable(false);
        conversationArea.setLineWrap(true);
        conversationArea.setWrapStyleWord(true);

        conversationArea.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        conversationArea.setBorder(
                BorderFactory.createEmptyBorder(
                        12, 12, 12, 12
                )
        );

        right.add(
                new JScrollPane(conversationArea),
                BorderLayout.CENTER
        );

        // Message composition area
        messageInput.setLineWrap(true);
        messageInput.setWrapStyleWord(true);

        JPanel composer = new JPanel(
                new BorderLayout(10, 0)
        );

        composer.setOpaque(false);

        composer.add(
                new JScrollPane(messageInput),
                BorderLayout.CENTER
        );

        JButton send =
                new JButton("Send Message");

        send.setForeground(Color.WHITE);
        send.setBackground(Color.decode("#2563EB"));

        send.addActionListener(
                e -> sendMessage()
        );

        composer.add(
                send,
                BorderLayout.EAST
        );

        right.add(
                composer,
                BorderLayout.SOUTH
        );

        add(right, BorderLayout.CENTER);

        // Open selected conversation
        contactList.addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {
                loadConversation();
            }
        });

        loadContacts();
    }

    // Get users from database.
    private void loadContacts() {

        MessageRepository.Contact selected =
                contactList.getSelectedValue();

        String selectedEmail =
                selected == null ? null : selected.email;

        try {
            List<MessageRepository.Contact> contacts =
                    MessageRepository.getContacts(
                            user.getEmail(),
                            user.getRole()
                    );

            contactModel.clear();

            int index = 0;

            for (int i = 0; i < contacts.size(); i++) {

                MessageRepository.Contact contact =
                        contacts.get(i);

                contactModel.addElement(contact);

                if (selectedEmail != null &&
                        contact.email.equalsIgnoreCase(
                                selectedEmail)) {

                    index = i;
                }
            }

            if (!contactModel.isEmpty()) {

                contactList.setSelectedIndex(index);

            } else {

                chatTitle.setText(
                        "No contacts registered yet"
                );

                conversationArea.setText("");
            }

        } catch (SQLException e) {
            showError(e);
        }
    }

    // Display messages in order.
    private void loadConversation() {

        MessageRepository.Contact contact =
                contactList.getSelectedValue();

        if (contact == null) {
            return;
        }

        chatTitle.setText(contact.name);

        try {
            List<MessageRepository.Message> messages =
                    MessageRepository.getConversation(
                            user.getEmail(),
                            contact.email
                    );

            StringBuilder text =
                    new StringBuilder();

            for (MessageRepository.Message m : messages) {

                String author =
                        m.sender.equalsIgnoreCase(
                                user.getEmail()
                        )
                                ? "You"
                                : contact.name;

                text.append(author)
                        .append(" | ")
                        .append(m.sentAt)
                        .append("\n")
                        .append(m.body)
                        .append("\n\n");
            }

            if (text.length() == 0) {

                conversationArea.setText(
                        "No messages yet. Start a conversation!"
                );

            } else {

                conversationArea.setText(
                        text.toString()
                );
            }

            conversationArea.setCaretPosition(
                    conversationArea.getDocument().getLength()
            );

        } catch (SQLException e) {
            showError(e);
        }
    }

    // Send the typed message.
    private void sendMessage() {

        MessageRepository.Contact contact =
                contactList.getSelectedValue();

        if (contact == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a contact first."
            );

            return;
        }

        try {
            MessageRepository.sendMessage(
                    user.getEmail(),
                    contact.email,
                    messageInput.getText()
            );

            messageInput.setText("");

            loadConversation();

        } catch (SQLException |
                 IllegalArgumentException e) {

            showError(e);
        }
    }

    private void showError(Exception e) {

        JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Messaging Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
