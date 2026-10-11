package components;

import javax.swing.*;
import java.awt.*;
//eto ung mga nasa buttons sa taas ng home ung mga may counts
public class StatCard extends JButton {

    private JLabel valueLabel;

    public StatCard(String title, String value, Color valueColor) {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setOpaque(true);
        this.setFocusable(false);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#e5e9f0"), 1),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        titleLabel.setForeground(Color.decode("#4b5563"));

        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(valueColor);

        JPanel textBox = new JPanel();
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        textBox.setOpaque(false);
        textBox.add(titleLabel);
        textBox.add(valueLabel);

        JLabel arrow = new JLabel(">");
        arrow.setFont(new Font("Arial", Font.BOLD, 22));
        arrow.setForeground(Color.decode("#0d0d0d"));

        this.add(textBox, BorderLayout.CENTER);
        this.add(arrow, BorderLayout.EAST);
    }
}