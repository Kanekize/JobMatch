package components;

import javax.swing.*;
import java.awt.*;

public class Logo extends JPanel {

    public Logo(Color jobColor, Color matchColor, int size) {
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        this.setOpaque(false);

        ImageIcon originalIcon = new ImageIcon("src/images/jobmatch-logo.png");

        Image scaledImage = originalIcon.getImage().getScaledInstance(
                size + 1,
                size + 1,
                Image.SCALE_SMOOTH
        );

        JLabel logoImage = new JLabel(new ImageIcon(scaledImage));
        logoImage.setVerticalAlignment(SwingConstants.CENTER);


        JLabel jobLabel = new JLabel("Job");
        jobLabel.setFont(new Font("Inter", Font.BOLD, size));
        jobLabel.setForeground(jobColor);


        JLabel matchLabel = new JLabel("Match");
        matchLabel.setFont(new Font("Inter", Font.BOLD, size));
        matchLabel.setForeground(matchColor);

        this.add(logoImage);
        this.add(jobLabel);
        this.add(matchLabel);
    }
}