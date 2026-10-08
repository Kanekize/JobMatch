package main;

import database.Database;
import screens.Frame;

public class Main {
    public static void main(String[] args) {
        Database.main(new String[]{});
        Frame frame = new Frame();
        frame.setVisible(true);
    }
}