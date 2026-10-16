package pages;

import dashboards.Dashboard;
import users.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;

// commons for both page
public abstract class Page extends JPanel {

    private User user;
    protected JPanel content;

    public Page(User user) {
        this.user = user;

        this.setLayout(new BorderLayout());
        this.setBackground(Color.decode("#f4f6fa"));

        content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(content, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.decode("#f4f6fa"));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scroll, BorderLayout.CENTER);
    }

    protected User getUser() {
        return user;
    }

    protected Dashboard getDashboard() {
        return (Dashboard) SwingUtilities.getWindowAncestor(this);
    }

    protected String getGreeting() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) {
            return "Good morning";
        } else if (hour < 18) {
            return "Good afternoon";
        }
        return "Good evening";
    }

    protected void addBlock(JComponent block, int gap) {
        block.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(block);
        content.add(Box.createVerticalStrut(gap));
    }

    protected JPanel makeHeading(String title, String subtitle, JComponent rightSide) {
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 40));
        titleLabel.setForeground(Color.decode("#0d0d0d"));

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Inter", Font.PLAIN, 20));
        subLabel.setForeground(Color.decode("#4b5563"));

        JPanel textBox = new JPanel();
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        textBox.setOpaque(false);
        textBox.add(titleLabel);
        textBox.add(Box.createVerticalStrut(4));
        textBox.add(subLabel);
        heading.add(textBox, BorderLayout.WEST);

        if (rightSide != null) {
            JPanel rightWrap = new JPanel(new GridBagLayout());
            rightWrap.setOpaque(false);
            rightWrap.add(rightSide);
            heading.add(rightWrap, BorderLayout.EAST);
        }
        return heading;
    }
}