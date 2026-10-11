
package main;

import database.Database;
import database.FeatureDatabase;
import screens.Frame;

public class Main {

    public static void main(String[] args) {

        // Initialize the existing login database.
        Database.main(new String[]{});

        // Initialize Lain's feature tables.
        FeatureDatabase.initialize();

        // Open the JobMatch application.
        Frame frame = new Frame();
        frame.setVisible(true);
    }
}
