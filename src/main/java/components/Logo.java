package components;

import javax.swing.*;
import java.awt.*;

public class Logo extends JPanel {

    public Logo(Color jobColor, Color matchColor, int size) {
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.setOpaque(false);

        JLabel jobLabel = new JLabel("Job");
        jobLabel.setFont(new Font("Arial", Font.BOLD, size));
        jobLabel.setForeground(jobColor);

        JLabel matchLabel = new JLabel("Match");
        matchLabel.setFont(new Font("Arial", Font.BOLD, size));
        matchLabel.setForeground(matchColor);

        this.add(jobLabel);
        this.add(matchLabel);
    }
}