package components;

import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedLineBorder extends AbstractBorder {
    private final Color color;
    private final int radius;
    private final int thickness;
    public RoundedLineBorder(Color color, int radius, int thickness) {
        this.color = color;
        this.radius = radius;
        this.thickness = thickness;
    }
    @Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));
        g2.draw(new RoundRectangle2D.Float(x + thickness / 2f, y + thickness / 2f,
                width - thickness, height - thickness, radius, radius));
        g2.dispose();
    }
    @Override public Insets getBorderInsets(Component c) { return new Insets(1, 1, 1, 1); }
    @Override public Insets getBorderInsets(Component c, Insets insets) {
        insets.set(1, 1, 1, 1); return insets;
    }
}
