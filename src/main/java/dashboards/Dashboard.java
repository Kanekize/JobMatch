package dashboards;

import components.Layout;
import database.Database;
import screens.Frame;
import users.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// commons dashboards. jobseeker at employer seperate cotent sa buildContent().
public abstract class Dashboard extends JFrame implements KeyListener {
    private User user;
    protected Layout layout;

    public Dashboard(User user) {
        this.user = user;

        this.setUndecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.addKeyListener(this);
        this.setResizable(false);
        this.getContentPane().setBackground(Color.decode("#f7f9fc"));
    }

    public User getUser() {
        return user;
    }

    protected abstract void buildContent();

    public abstract String[] getMenuItems();

    public void onMenuClick(String name) { }

    public void logout() {
        Database.forgetUser(user.getRole());

        Frame frame = new Frame();
        frame.setVisible(true);
        this.dispose();
    }

    public void showDashboard() {
        buildContent();
        this.setVisible(true);
        layout.getContentPanel().requestFocus();
    }

    @Override public void keyTyped(KeyEvent e) { }
    @Override public void keyReleased(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }
}