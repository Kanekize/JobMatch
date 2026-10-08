package components;

import dashboards.Dashboard;
import users.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Layout implements ActionListener, FocusListener {
    private static Dimension normalSize = new Dimension(1000, 700);
    JPanel headerPanel, navButtonsPanel;

    Dashboard dashboard;
    JPanel sidebarPanel, topBar, contentPanel;
    JButton[] menuButtons;
    JButton logoutBtn, notificationBtn, profileBtn;
    JTextField searchField;
    String searchHint = "Search for jobs, companies, or keywords...";

    public Layout(JFrame frame, boolean showNavigation) {
        frame.setSize(normalSize);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // head
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.decode("#f7f9fc"));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 15, 25));

        headerPanel.add(new Logo(Color.decode("#12376b"), Color.decode("#2f80ed"), 28), BorderLayout.WEST);

        if (showNavigation) {
            navButtonsPanel = new JPanel(
                    new FlowLayout(FlowLayout.RIGHT, 15, 0)
            );
            navButtonsPanel.setOpaque(false);
            navButtonsPanel.add(new JButton("Home"));
            navButtonsPanel.add(new JButton("Notifications"));
            navButtonsPanel.add(new JButton("Logout"));

            headerPanel.add(navButtonsPanel, BorderLayout.EAST);
        }
        frame.add(headerPanel, BorderLayout.NORTH);
    }

    public Layout(Dashboard dashboard) {
        this.dashboard = dashboard;
        User user = dashboard.getUser();
        String[] menuItems = dashboard.getMenuItems();
        String initial = user.getFirstName().substring(0, 1).toUpperCase();

        dashboard.setLayout(new BorderLayout());

        // sidebar
        sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(Color.decode("#12376b"));
        sidebarPanel.setPreferredSize(new Dimension(240, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(25, 15, 20, 15));

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setOpaque(false);
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JLabel jobLabel = new JLabel("Job");
        jobLabel.setFont(new Font("Arial", Font.BOLD, 28));
        jobLabel.setForeground(Color.decode("#FFFFFF"));

        JLabel matchLabel = new JLabel("Match");
        matchLabel.setFont(new Font("Arial", Font.BOLD, 28));
        matchLabel.setForeground(Color.decode("#3AA1F8"));

        logoPanel.add(jobLabel);
        logoPanel.add(matchLabel);
        menuPanel.add(logoPanel);
        menuPanel.add(Box.createVerticalStrut(35));

        menuButtons = new JButton[menuItems.length];
        for (int i = 0; i < menuItems.length; i++) {
            menuButtons[i] = new JButton(menuItems[i]);
            menuButtons[i].setActionCommand(menuItems[i]);
            styleSideButton(menuButtons[i]);
            menuPanel.add(menuButtons[i]);
            menuPanel.add(Box.createVerticalStrut(6));
        }
        setActive(menuItems[0]);

        // bottom part of left panel sa dashboard
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);

        JPanel userPanel = new JPanel(new BorderLayout(12, 0));
        userPanel.setOpaque(false);
        userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPanel.setPreferredSize(new Dimension(210, 48));
        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JLabel avatarLabel = new JLabel(initial, SwingConstants.CENTER);
        avatarLabel.setOpaque(true);
        avatarLabel.setBackground(Color.decode("#cbd5e1"));
        avatarLabel.setForeground(Color.decode("#12376b"));
        avatarLabel.setFont(new Font("Arial", Font.BOLD, 18));
        avatarLabel.setPreferredSize(new Dimension(44, 44));
        userPanel.add(avatarLabel, BorderLayout.WEST);

        JPanel nameBox = new JPanel();
        nameBox.setLayout(new BoxLayout(nameBox, BoxLayout.Y_AXIS));
        nameBox.setOpaque(false);

        JLabel nameLabel = new JLabel(user.getFullName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);

        JLabel roleLabel = new JLabel(user.getRole());
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        roleLabel.setForeground(Color.decode("#9db4d6"));

        nameBox.add(Box.createVerticalGlue());
        nameBox.add(nameLabel);
        nameBox.add(roleLabel);
        nameBox.add(Box.createVerticalGlue());
        userPanel.add(nameBox, BorderLayout.CENTER);

        logoutBtn = new JButton("Logout");
        logoutBtn.setActionCommand("Logout");
        styleSideButton(logoutBtn);

        bottomPanel.add(userPanel);
        bottomPanel.add(Box.createVerticalStrut(15));
        bottomPanel.add(logoutBtn);

        sidebarPanel.add(menuPanel, BorderLayout.NORTH);
        sidebarPanel.add(bottomPanel, BorderLayout.SOUTH);
        dashboard.add(sidebarPanel, BorderLayout.WEST);

        // header sa dashboards
        topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#e5e9f0")),
                BorderFactory.createEmptyBorder(14, 25, 14, 25)
        ));

        searchField = new JTextField(searchHint);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setPreferredSize(new Dimension(400, 40));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#e2e8f0"), 1),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));
        searchField.addFocusListener(this);
        searchField.addKeyListener(dashboard);

        JPanel searchWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchWrap.setOpaque(false);
        searchWrap.add(searchField);

        notificationBtn = new JButton("Notifications");
        notificationBtn.setActionCommand("Notifications");
        notificationBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        notificationBtn.setForeground(Color.decode("#12376b"));
        notificationBtn.setBorderPainted(false);
        notificationBtn.setContentAreaFilled(false);
        notificationBtn.setFocusable(false);
        notificationBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        notificationBtn.addActionListener(this);

        profileBtn = new JButton(initial);
        profileBtn.setActionCommand("Profile");
        profileBtn.setFont(new Font("Arial", Font.BOLD, 16));
        profileBtn.setForeground(Color.decode("#12376b"));
        profileBtn.setBackground(Color.decode("#cbd5e1"));
        profileBtn.setBorderPainted(false);
        profileBtn.setMargin(new Insets(0, 0, 0, 0));
        profileBtn.setFocusable(false);
        profileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileBtn.setPreferredSize(new Dimension(40, 40));
        profileBtn.addActionListener(this);

        JPanel rightWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightWrap.setOpaque(false);
        rightWrap.add(notificationBtn);
        rightWrap.add(profileBtn);

        topBar.add(searchWrap, BorderLayout.WEST);
        topBar.add(rightWrap, BorderLayout.EAST);

        // center panel for dashboards
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.decode("#f4f6fa"));
        contentPanel.setFocusable(true);
        contentPanel.addKeyListener(dashboard);

        JPanel rightSide = new JPanel(new BorderLayout());
        rightSide.add(topBar, BorderLayout.NORTH);
        rightSide.add(contentPanel, BorderLayout.CENTER);
        dashboard.add(rightSide, BorderLayout.CENTER);
    }

    public void showHeader(boolean show) {
        headerPanel.setVisible(show);
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    private void styleSideButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.PLAIN, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.decode("#12376b"));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));
        btn.setBorderPainted(false);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setPreferredSize(new Dimension(210, 44));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.addActionListener(this);
    }

    //blue box indicator for sidebar buttons pag clinick
    public void setActive(String name) {
        for (int i = 0; i < menuButtons.length; i++) {
            if (menuButtons[i].getActionCommand().equals(name)) {
                menuButtons[i].setBackground(Color.decode("#2f80ed"));
            } else {
                menuButtons[i].setBackground(Color.decode("#12376b"));
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == logoutBtn) {
            dashboard.logout();
        } else if (e.getSource() == notificationBtn || e.getSource() == profileBtn) {
            dashboard.onMenuClick(e.getActionCommand());
        } else {
            setActive(e.getActionCommand());
            dashboard.onMenuClick(e.getActionCommand());
        }
    }

    public void focusGained(FocusEvent e) {
        if (searchField.getText().equals(searchHint)) {
            searchField.setText("");
            searchField.setForeground(Color.decode("#0d0d0d"));
        }
    }

    public void focusLost(FocusEvent e) {
        if (searchField.getText().isEmpty()) {
            searchField.setText(searchHint);
            searchField.setForeground(Color.GRAY);
        }
    }
}