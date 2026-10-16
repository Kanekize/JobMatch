package components;

import javax.swing.*;
import java.awt.*;
// eto naman para sa mga may quick access na box
public class SectionPanel extends JPanel {
    private JPanel[] boxes;
    private JButton viewAllBtn;
    private JPanel boxPanel;
    private JComponent centerContent;

    public SectionPanel(String title, String linkText, int boxCount, int boxHeight, boolean sideBySide) {
        this.setLayout(new BorderLayout(0, 10));
        this.setOpaque(false);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 20));
        titleLabel.setForeground(Color.decode("#0d0d0d"));
        topRow.add(titleLabel, BorderLayout.WEST);

        if (!linkText.isEmpty()) {
            viewAllBtn = new JButton(linkText);
            viewAllBtn.setFont(new Font("Inter", Font.PLAIN, 13));
            viewAllBtn.setForeground(Color.decode("#2563eb"));
            viewAllBtn.setBorderPainted(false);
            viewAllBtn.setContentAreaFilled(false);
            viewAllBtn.setFocusable(false);
            viewAllBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            topRow.add(viewAllBtn, BorderLayout.EAST);
        }

        // empty boxes sa baba ng mga title tulad ng Job postings
        if (sideBySide) {
            boxPanel = new JPanel(new GridLayout(1, boxCount, 15, 0));
        } else {
            boxPanel = new JPanel(new GridLayout(boxCount, 1, 0, 8));
        }
        boxPanel.setOpaque(false);

        boxes = new JPanel[boxCount];
        for (int i = 0; i < boxCount; i++) {
            boxes[i] = new JPanel(new BorderLayout());
            boxes[i].setBackground(Color.WHITE);
            boxes[i].setBorder(new RoundedLineBorder(Color.decode("#d7dce3"), 12, 1));
            boxes[i].setPreferredSize(new Dimension(100, boxHeight));
            boxPanel.add(boxes[i]);
        }
        centerContent = boxPanel;
        this.add(topRow, BorderLayout.NORTH);
        this.add(centerContent, BorderLayout.CENTER);
    }

    public JPanel getBox(int number) {
        return boxes[number];
    }

    public JButton getViewAllButton() {
        return viewAllBtn;
    }

    public void setEmptyState(String message) {
        JPanel empty = new JPanel(new GridBagLayout());
        empty.setOpaque(false);

        JLabel label = new JLabel(message);
        label.setFont(new Font("Inter", Font.PLAIN, 14));
        label.setForeground(Color.decode("#6b7280"));
        empty.add(label);

        this.remove(centerContent);
        centerContent = empty;
        this.add(centerContent, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }
}