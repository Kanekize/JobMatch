package components;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.File;
import java.util.Collections;

// Inter ang font ng buong system. tawagin ang Fonts.install() ONCE sa simula ng main(),
// bago gumawa ng kahit anong Swing component.
//
// 1. nire-register ung Inter files galing sa src/fonts (kaya hindi na kailangang naka-install sa computer)
// 2. pinapalitan ung default font ng Swing (JOptionPane, JFileChooser, JTextField, JComboBox, tooltips, etc.)
//    para pati ung mga hindi natin sinet manually na font ay Inter din
public class Fonts {

    public static final String FAMILY = "Inter";

    // Java kasi Regular at Bold lang ang kinikilalang style sa isang family (Font.PLAIN / Font.BOLD).
    // ang ibang weights (Medium, Black, etc.) hiwalay na family name, kaya ganito ang gamit:
    //   new Font(Fonts.MEDIUM, Font.PLAIN, 14)   -> Inter Medium
    //   new Font(Fonts.BLACK, Font.PLAIN, 14)    -> Inter Black
    //   new Font(Fonts.FAMILY, Font.BOLD, 14)    -> Inter Bold
    public static final String MEDIUM = "Inter Medium";
    public static final String BLACK = "Inter Black";

    private static final String FONT_FOLDER = "src/fonts/";
    private static final String[] FILES = {
            "Inter-Regular.ttf", "Inter-Bold.ttf", "Inter-Italic.ttf", "Inter-BoldItalic.ttf",
            "Inter-Medium.ttf", "Inter-Black.ttf"
    };

    public static void install() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

        for (String file : FILES) {
            try {
                Font font = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_FOLDER + file));
                env.registerFont(font);
            } catch (Exception e) {
                System.out.println("Could not load font " + FONT_FOLDER + file + ": " + e.getMessage());
            }
        }

        // kung wala sa src/fonts at hindi rin installed, wag na galawin ung defaults
        boolean available = false;
        for (String name : env.getAvailableFontFamilyNames()) {
            if (name.equals(FAMILY)) {
                available = true;
                break;
            }
        }
        if (!available) {
            System.out.println("Inter font not found. Make sure the src/fonts folder exists.");
            return;
        }

        // palitan lahat ng default font ng Swing, keep ung style at size
        UIDefaults defaults = UIManager.getDefaults();
        for (Object key : Collections.list(defaults.keys())) {
            Object value = defaults.get(key);
            if (value instanceof Font) {
                Font old = (Font) value;
                defaults.put(key, new FontUIResource(new Font(FAMILY, old.getStyle(), old.getSize())));
            }
        }
    }
}
