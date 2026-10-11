package pages;

import users.User;
import javax.swing.*;
import java.awt.*;

public class MessagesPage extends Page {

    public MessagesPage(User user) {
        super(user);

        JLabel label = new JLabel("Messages Panel View", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setForeground(Color.decode("#091222"));

        this.setLayout(new BorderLayout());
        this.add(label, BorderLayout.CENTER);
    }
}
