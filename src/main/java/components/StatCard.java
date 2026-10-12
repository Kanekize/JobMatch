package components;

import javax.swing.*;
import java.awt.*;
//eto ung mga nasa buttons sa taas ng home ung mga may counts
public class StatCard extends JButton {

    private JLabel valueLabel;

    public StatCard(String title, String value, Color valueColor) {
        this(title, value, valueColor, null);
    }

    public StatCard(String title, String value, Color valueColor, String iconFileName) {
        this(title, value, valueColor, iconFileName, Color.decode("#e8f1ff"));
    }

    public StatCard(String title, String value, Color valueColor, String iconFileName, Color iconBackground) {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setOpaque(true);
        this.setFocusable(false);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#d7dce3"), 12, 1),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        titleLabel.setForeground(Color.decode("#4b5563"));

        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 28));
        valueLabel.setForeground(valueColor);

        JPanel textBox = new JPanel();
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        textBox.setOpaque(false);
        textBox.add(titleLabel);
        textBox.add(valueLabel);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        if (iconFileName != null && !iconFileName.isEmpty()) {
            JPanel iconCircle = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(iconBackground);
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            iconCircle.setOpaque(false);
            iconCircle.setPreferredSize(new Dimension(60, 60));
            iconCircle.setMaximumSize(new Dimension(60, 60));
            iconCircle.setMinimumSize(new Dimension(60, 60));

            JLabel icon = new JLabel(loadIcon(iconFileName, 28, 28));
            iconCircle.add(icon);
            left.add(iconCircle);
            left.add(Box.createHorizontalStrut(14));
        }

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(textBox, BorderLayout.CENTER);

        ImageIcon arrowIcon = loadArrowIcon();
        JLabel arrow = new JLabel(arrowIcon);

        this.add(left, BorderLayout.WEST);
        this.add(center, BorderLayout.CENTER);
        this.add(arrow, BorderLayout.EAST);
    }

    private ImageIcon loadIcon(String fileName, int width, int height) {
        java.io.File file = new java.io.File("src/images/" + fileName);
        if (!file.exists()) file = new java.io.File("JobMatch/src/images/" + fileName);
        if (!file.exists()) return new ImageIcon();
        Image image = new ImageIcon(file.getPath()).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    private ImageIcon loadArrowIcon() {
        java.io.File file = new java.io.File("src/images/arrow-bracket.png");
        if (!file.exists()) file = new java.io.File("JobMatch/src/images/arrow-bracket.png");
        if (!file.exists()) return new ImageIcon();
        Image image = new ImageIcon(file.getPath()).getImage().getScaledInstance(10, 18, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }
}