package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class PasswordField extends JPanel implements ActionListener {

    private JPasswordField field;
    private JButton toggleBtn;
    private char hiddenChar;
    private boolean showing = false;

    private ImageIcon showIcon;
    private ImageIcon hideIcon;

    public PasswordField() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createLineBorder(Color.decode("#cbd5e1"), 1));

        field = new JPasswordField();
        field.setFont(new Font("Inter", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 6));
        hiddenChar = field.getEchoChar();

        try {
            URL openEye = new URL("https://cdn-icons-png.flaticon.com/512/6803/6803345.png");
            URL closedEye = new URL("https://static.thenounproject.com/png/506282-200.png");

            ImageIcon show = new ImageIcon(closedEye);
            ImageIcon hide = new ImageIcon(openEye);

            Image scaledShowImg = show.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            Image scaledHideImg = hide.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);

            showIcon = new ImageIcon(scaledShowImg);
            hideIcon = new ImageIcon(scaledHideImg);

        } catch (Exception e) {
            System.out.println("Failed to load icons from web: " + e.getMessage());
            showIcon = null;
            hideIcon = null;
        }

        toggleBtn = new JButton(showIcon);
        if (showIcon == null) {
            toggleBtn.setText("Show");
        }

        toggleBtn.setBorderPainted(false);
        toggleBtn.setContentAreaFilled(false);
        toggleBtn.setFocusable(false);
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleBtn.setPreferredSize(new Dimension(40, 30));
        toggleBtn.addActionListener(this);

        this.add(field, BorderLayout.CENTER);
        this.add(toggleBtn, BorderLayout.EAST);
    }

    public char[] getPassword() {
        return field.getPassword();
    }

    public void setText(String text) {
        field.setText(text);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showing = !showing;
        if (showing) {
            field.setEchoChar((char) 0);
            if (hideIcon != null) {
                toggleBtn.setIcon(hideIcon);
            } else {
                toggleBtn.setText("Hide");
            }
        } else {
            field.setEchoChar(hiddenChar);
            if (showIcon != null) {
                toggleBtn.setIcon(showIcon);
            } else {
                toggleBtn.setText("Show");
            }
        }
    }
}