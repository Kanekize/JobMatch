
package main;

import components.Fonts;
import database.Database;
import database.FeatureDatabase;
import screens.Frame;

public class Main {

    public static void main(String[] args) {

        Fonts.install();

        Database.main(new String[]{});

        FeatureDatabase.initialize();

        Frame frame = new Frame();
        frame.setVisible(true);
    }
}
