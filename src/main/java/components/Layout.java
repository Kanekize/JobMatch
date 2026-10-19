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
        sidebarPanel.setBackground(Color.decode("#103358"));
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
        jobLabel.setFont(new Font("Inter", Font.BOLD, 28));
        jobLabel.setForeground(Color.decode("#FFFFFF"));

        JLabel matchLabel = new JLabel("Match");
        matchLabel.setFont(new Font("Inter", Font.BOLD, 28));
        matchLabel.setForeground(Color.decode("#3AA1F8"));

        logoPanel.add(jobLabel);
        logoPanel.add(matchLabel);
        menuPanel.add(logoPanel);
        menuPanel.add(Box.createVerticalStrut(35));

        menuButtons = new JButton[menuItems.length];
        for (int i = 0; i < menuItems.length; i++) {
            menuButtons[i] = new JButton(menuItems[i]) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            menuButtons[i].setActionCommand(menuItems[i]);
            menuButtons[i].setIcon(sidebarIcon(menuItems[i]));
            menuButtons[i].setIconTextGap(14);
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

        profileBtn = new JButton(initial) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        profileBtn.setActionCommand("Profile");
        profileBtn.setOpaque(false);
        profileBtn.setContentAreaFilled(false);
        profileBtn.setBackground(Color.decode("#cbd5e1"));
        profileBtn.setForeground(Color.decode("#12376b"));
        profileBtn.setFont(new Font("Inter", Font.BOLD, 18));
        profileBtn.setBorderPainted(false);
        profileBtn.setFocusable(false);
        profileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileBtn.setPreferredSize(new Dimension(44, 44));
        profileBtn.addActionListener(this);
        userPanel.add(profileBtn, BorderLayout.WEST);

        JPanel nameBox = new JPanel();
        nameBox.setLayout(new BoxLayout(nameBox, BoxLayout.Y_AXIS));
        nameBox.setOpaque(false);

        JLabel nameLabel = new JLabel(user.getDisplayName());
        nameLabel.setFont(new Font("Inter", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);

        JLabel roleLabel = new JLabel(user.getRole());
        roleLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        roleLabel.setForeground(Color.decode("#9db4d6"));

        nameBox.add(Box.createVerticalGlue());
        nameBox.add(nameLabel);
        nameBox.add(roleLabel);
        nameBox.add(Box.createVerticalGlue());
        userPanel.add(nameBox, BorderLayout.CENTER);

        logoutBtn = new JButton("Logout");
        logoutBtn.setActionCommand("Logout");
        logoutBtn.setIcon(loadIcon("logout.png", 19, 19));
        logoutBtn.setIconTextGap(14);
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

        JPanel globalSearch = new JPanel(new BorderLayout(10, 0));
        globalSearch.setOpaque(false);
        globalSearch.setPreferredSize(new Dimension(460, 40));
        globalSearch.setMaximumSize(new Dimension(460, 40));
        globalSearch.setBorder(BorderFactory.createCompoundBorder(
                new components.RoundedLineBorder(Color.decode("#d7dce3"), 12, 1),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        JLabel globalSearchIcon = new JLabel(loadIcon("search.png", 18, 18));
        globalSearch.add(globalSearchIcon, BorderLayout.WEST);
        searchField = new JTextField(searchHint);
        searchField.setFont(new Font("Inter", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setOpaque(false);
        searchField.setBorder(BorderFactory.createEmptyBorder());
        searchField.addFocusListener(this);
        searchField.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.equals(searchHint)) {
                query = "";
            }
            
            if (contentPanel.getComponentCount() > 0) {
                Component activePage = contentPanel.getComponent(0);

                // active page = jobseeker jobs
                if (activePage instanceof pages.JobSeekerJobsPage) {
                    pages.JobSeekerJobsPage seekerJobsPage = (pages.JobSeekerJobsPage) activePage;

                    try {
                        java.lang.reflect.Field field = pages.JobSeekerJobsPage.class.getDeclaredField("searchField");
                        field.setAccessible(true);
                        JTextField pageSearchField = (JTextField) field.get(seekerJobsPage);

                        // kung ano sinearch sa taas lalabas na rin sa search bar sa loob fixed
                        pageSearchField.setText(query);
                        pageSearchField.setForeground(Color.decode("#0d0d0d"));
                    } catch (Exception ex) {
                        System.err.println("Search binding failed: " + ex.getMessage());
                    }
                }

                // active page = employer jobs
                else if (activePage instanceof pages.EmployerJobsPage) {
                    pages.EmployerJobsPage employerJobsPage = (pages.EmployerJobsPage) activePage;

                    try {
                        java.lang.reflect.Field field = pages.EmployerJobsPage.class.getDeclaredField("searchField");
                        field.setAccessible(true);
                        JTextField pageSearchField = (JTextField) field.get(employerJobsPage);

                        pageSearchField.setText(query);
                    } catch (Exception ex) {
                        System.err.println("Search binding failed: " + ex.getMessage());
                    }
                }

                // pag nag search sa different page blue box indicator sa sidebar menu fixed
                else {
                    if (dashboard instanceof dashboards.JobSeekerDashboard) {
                        dashboard.showPage(new pages.JobSeekerJobsPage(user, null));
                        SwingUtilities.invokeLater(() -> searchField.postActionEvent());
                    } else if (dashboard instanceof dashboards.EmployerDashboard) {
                        dashboard.onMenuClick("Jobs");
                        SwingUtilities.invokeLater(() -> searchField.postActionEvent());
                    }
                }
            }
        });
        globalSearch.add(searchField, BorderLayout.CENTER);

        JPanel searchWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchWrap.setOpaque(false);
        searchWrap.add(globalSearch);
        topBar.add(searchWrap, BorderLayout.CENTER);

        notificationBtn = new JButton(loadIcon("notification-bell.png", 22, 22));
        notificationBtn.setActionCommand("Notifications");
        notificationBtn.setToolTipText("Notifications");
        notificationBtn.setPreferredSize(new Dimension(32, 36));
        notificationBtn.setFont(new Font("Inter", Font.PLAIN, 14));
        notificationBtn.setForeground(Color.decode("#12376b"));
        notificationBtn.setBorderPainted(false);
        notificationBtn.setContentAreaFilled(false);
        notificationBtn.setFocusable(false);
        notificationBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        notificationBtn.addActionListener(this);

        JPanel rightWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightWrap.setOpaque(false);
        rightWrap.add(notificationBtn);
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

    private ImageIcon loadIcon(String fileName, int width, int height) {
        java.io.File file = new java.io.File("src/images/" + fileName);
        if (!file.exists()) file = new java.io.File("JobMatch/src/images/" + fileName);
        if (!file.exists()) return null;
        Image image = new ImageIcon(file.getPath()).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    private ImageIcon sidebarIcon(String item) {
        String file;
        switch (item) {
            case "Home": file = "home.png"; break;
            case "Recommendations": file = "recommendation.png"; break;
            case "Applications": file = "applications.png"; break;
            case "Assessment": file = "assessment.png"; break;
            case "Messages": file = "message.png"; break;
            case "Interviews": file = "interviews.png"; break;
            case "Jobs": file = "recommendation.png"; break;
            case "Applicants": file = "assessment.png"; break;
            default: file = "home.png";
        }
        return loadIcon(file, 20, 20);
    }

    private void styleSideButton(JButton btn) {
        btn.setFont(new Font("Inter", Font.PLAIN, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.decode("#103358"));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 10));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
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
                menuButtons[i].setBackground(Color.decode("#3284FC"));
            } else {
                menuButtons[i].setBackground(Color.decode("#103358"));
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
        if (searchField != null && searchField.getText().equals(searchHint)) {
            searchField.setText("");
            searchField.setForeground(Color.decode("#0d0d0d"));
        }
    }

    public void focusLost(FocusEvent e) {
        if (searchField != null && searchField.getText().isEmpty()) {
            searchField.setText(searchHint);
            searchField.setForeground(Color.GRAY);
        }
    }
}