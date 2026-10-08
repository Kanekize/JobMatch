package screens;

import components.Layout;
import components.Logo;
import components.PasswordField;
import database.Database;
import users.User;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class Frame extends JFrame implements ActionListener, KeyListener {
    Image scaled;
    JButton jobSeekerBtn, hiringBtn, loginBtn, closeLoginBtn, registerBtn , closeRegisterBtn, goToRegisterBtn, goToForgotBtn, backToLoginBtn, forgotBtn, closeForgotBtn, backToLoginFromForgotBtn, signInLinkBtn, switchTypeBtn, changeTypeBtn;
    //grid panel
    JPanel centerWrapper;

    JPanel invinciblePanel, jobSeekerPanel, hiringPanel, registerPanel, loginPanel, forgotPanel;
    JTextArea jobSeekerDesc, hiringDesc;

    //reg form
    JTextField emailInputLogin, firstNameInput, lastNameInput, emailInputRegister, forgetNameInput, companyInput;
    PasswordField passwordInputLogin, passwordInputRegister, confirmInputRegister, forgetPassInput;
    JCheckBox rememberCheck, termsCheck;

    JLabel jobSeekerTitle, hiringTitle, userLabel, passLabel;
    int topY = 40, labelHeight;

    String accountType = "";

    Layout layout;

    static Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    static int headerHeight = 80;

    // home page canvas size
    static int homeWidth = (int) (screen.width * 0.75);
    static int homeHeight = screen.height - headerHeight;

    // login register canvas size
    static int authWidth = screen.width;
    static int authHeight = screen.height;

    //main size of invisible panels at registration panels
    static int canvasWidth = homeWidth;
    static int canvasHeight = homeHeight;
    static Dimension canvasSize = new Dimension(canvasWidth, canvasHeight);

    //change canvas size
    public static void setCanvas(int width, int height) {
        canvasWidth = width;
        canvasHeight = height;
        canvasSize = new Dimension(width, height);
    }

    public static void fixSize(JPanel panel) {
        panel.setPreferredSize(canvasSize);
        panel.setMinimumSize(canvasSize);
        panel.setMaximumSize(canvasSize);
    }

    //center contents formula. horizontal
    public static int centerX(int contentWidth) {
        return (canvasWidth - contentWidth) / 2;
    }
    //veritcal
    public static int centerY(int contentHeight) {
        return (canvasHeight - contentHeight) / 2;
    }

    public Border inputBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#cbd5e1"), 1),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        );
    }

    public Frame() {
        this.setUndecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.addKeyListener(this);
        this.setResizable(false);
        this.getContentPane().setBackground(Color.decode("#f7f9fc"));

        setCanvas(homeWidth, homeHeight);

        layout = new Layout(this, false);

        centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(Color.decode("#f7f9fc"));
        GridBagConstraints grid = new GridBagConstraints();

        invinciblePanel = new JPanel();
        invinciblePanel.setLayout(null);
        invinciblePanel.setBackground(Color.decode("#f7f9fc"));
        fixSize(invinciblePanel);

        topY = Math.max(centerY(480), 10);

        JLabel welcomeMsg = new JLabel("How would you like to use JobMatch?", SwingConstants.CENTER);
        welcomeMsg.setFont(new Font("Arial", Font.BOLD, 38));
        welcomeMsg.setForeground(Color.decode("#091222"));
        welcomeMsg.setBounds(0, topY, canvasWidth, 50);
        invinciblePanel.add(welcomeMsg);

        JLabel welcomeMsg2 = new JLabel("Choose the account type that best describes how you will use the platform.", SwingConstants.CENTER);
        welcomeMsg2.setFont(new Font("Arial", Font.PLAIN, 18));
        welcomeMsg2.setForeground(Color.decode("#929daf"));
        welcomeMsg2.setBounds(0, topY + 60, canvasWidth, 30);
        invinciblePanel.add(welcomeMsg2);

        userChoice();

        grid.gridx = 0;
        grid.gridy = 0;
        grid.anchor = GridBagConstraints.CENTER;
        centerWrapper.add(invinciblePanel, grid);
        this.add(centerWrapper, BorderLayout.CENTER);
    }

    public void userChoice() {
        int panelWidth = Math.min(380, (canvasWidth - 100) / 2), panelHeight = 290;
        int gap = 30;
        int cardsX = centerX(panelWidth * 2 + gap);
        int cardsY = topY + 130;

        // employee
        jobSeekerPanel = new JPanel();
        jobSeekerPanel.setLayout(null);
        jobSeekerPanel.setBackground(Color.decode("#F2F6FD"));
        jobSeekerPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#1a5de3"), 2));
        jobSeekerPanel.setBounds(cardsX, cardsY, panelWidth, panelHeight);

        jobSeekerTitle = new JLabel("Job Seeker", SwingConstants.CENTER);
        jobSeekerTitle.setFont(new Font("Arial", Font.BOLD, 26));
        jobSeekerTitle.setForeground(Color.decode("#0d0d0d"));
        jobSeekerTitle.setBounds(0, 35, panelWidth, 40);
        jobSeekerPanel.add(jobSeekerTitle);

        jobSeekerDesc = new JTextArea("Find opportunities matched to your skills, interests, qualifications and preferences.");
        jobSeekerDesc.setFont(new Font("Arial", Font.PLAIN, 16));
        jobSeekerDesc.setForeground(Color.decode("#2a2a2a"));
        jobSeekerDesc.setEditable(false);
        jobSeekerDesc.setFocusable(false);
        jobSeekerDesc.setOpaque(false);
        jobSeekerDesc.setLineWrap(true);
        jobSeekerDesc.setWrapStyleWord(true);
        jobSeekerDesc.setBounds(30, 90, panelWidth - 60, 90);
        jobSeekerPanel.add(jobSeekerDesc);

        jobSeekerBtn = new JButton("Continue as Job Seeker  \u2192");
        jobSeekerBtn.setBackground(Color.decode("#195EE4"));
        jobSeekerBtn.setForeground(Color.WHITE);
        jobSeekerBtn.setFont(new Font("Arial", Font.BOLD, 16));
        jobSeekerBtn.setBounds(30, 200, panelWidth - 60, 48);
        jobSeekerBtn.setBorderPainted(false);
        jobSeekerBtn.setOpaque(true);
        jobSeekerBtn.addActionListener(this);
        jobSeekerBtn.setFocusable(false);
        jobSeekerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jobSeekerPanel.add(jobSeekerBtn);
        invinciblePanel.add(jobSeekerPanel);

        // hirer
        hiringPanel = new JPanel();
        hiringPanel.setLayout(null);
        hiringPanel.setBackground(Color.decode("#FFFFFF"));
        hiringPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#e2e8f0"), 1));
        hiringPanel.setBounds(cardsX + panelWidth + gap, cardsY, panelWidth, panelHeight);

        hiringTitle = new JLabel("Employer", SwingConstants.CENTER);
        hiringTitle.setFont(new Font("Arial", Font.BOLD, 26));
        hiringTitle.setForeground(Color.decode("#0d0d0d"));
        hiringTitle.setBounds(0, 35, panelWidth, 40);
        hiringPanel.add(hiringTitle);

        hiringDesc = new JTextArea("Post opportunities, review applicants and manage your recruitment process.");
        hiringDesc.setFont(new Font("Arial", Font.PLAIN, 16));
        hiringDesc.setForeground(Color.decode("#2a2a2a"));
        hiringDesc.setEditable(false);
        hiringDesc.setFocusable(false);
        hiringDesc.setOpaque(false);
        hiringDesc.setLineWrap(true);
        hiringDesc.setWrapStyleWord(true);
        hiringDesc.setBounds(30, 90, panelWidth - 60, 90);
        hiringPanel.add(hiringDesc);

        hiringBtn = new JButton("Continue as Employer  \u2192");
        hiringBtn.setBackground(Color.decode("#FFFFFF"));
        hiringBtn.setForeground(Color.decode("#34467B"));
        hiringBtn.setFont(new Font("Arial", Font.BOLD, 16));
        hiringBtn.setBounds(30, 200, panelWidth - 60, 48);
        hiringBtn.setBorder(BorderFactory.createLineBorder(Color.decode("#D9D9D9"), 1));
        hiringBtn.setOpaque(true);
        hiringBtn.addActionListener(this);
        hiringBtn.setFocusable(false);
        hiringBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        hiringPanel.add(hiringBtn);
        invinciblePanel.add(hiringPanel);

        signInLinkBtn = new JButton("Already have an account? Sign in");
        signInLinkBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        signInLinkBtn.setForeground(Color.decode("#2563eb"));
        signInLinkBtn.setBorderPainted(false);
        signInLinkBtn.setContentAreaFilled(false);
        signInLinkBtn.setFocusable(false);
        signInLinkBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signInLinkBtn.setBounds(centerX(400), cardsY + panelHeight + 30, 400, 25);
        signInLinkBtn.setHorizontalAlignment(SwingConstants.CENTER);
        signInLinkBtn.addActionListener(this);
        invinciblePanel.add(signInLinkBtn);
    }

    public void addSidebarLogo(JPanel leftPanel) {
        Logo logo = new Logo(Color.WHITE, Color.decode("#3AA1F8"), 30);
        logo.setBounds(50, 40, 220, 45);
        leftPanel.add(logo);
    }

    public boolean openSavedAccount(String role) {
        User saved = Database.getRememberedUser(role);
        if (saved == null) {
            return false;
        }
        saved.openDashboard();
        this.dispose();
        return true;
    }

    public void addSidebarText(JPanel leftPanel, int leftWidth, String title, String sub) {
        int textWidth = leftWidth - 120;
        int sideY = canvasHeight / 2 - 120;

        JTextArea sideTitle = new JTextArea(title);
        sideTitle.setFont(new Font("Arial", Font.BOLD, 44));
        sideTitle.setForeground(Color.WHITE);
        sideTitle.setEditable(false);
        sideTitle.setFocusable(false);
        sideTitle.setOpaque(false);
        sideTitle.setLineWrap(true);
        sideTitle.setWrapStyleWord(true);
        sideTitle.setSize(textWidth, 1);
        labelHeight = sideTitle.getPreferredSize().height;
        sideTitle.setBounds(60, sideY, textWidth, labelHeight);
        leftPanel.add(sideTitle);

        JTextArea sideSub = new JTextArea(sub);
        sideSub.setFont(new Font("Arial", Font.PLAIN, 18));
        sideSub.setForeground(Color.decode("#c7d7f5"));
        sideSub.setEditable(false);
        sideSub.setFocusable(false);
        sideSub.setOpaque(false);
        sideSub.setLineWrap(true);
        sideSub.setWrapStyleWord(true);
        sideSub.setSize(textWidth, 1);
        int subHeight = sideSub.getPreferredSize().height;
        sideSub.setBounds(60, sideY + labelHeight + 15, textWidth, subHeight);
        leftPanel.add(sideSub);
    }

    public void Login() {
        setCanvas(authWidth, authHeight);
        layout.showHeader(false);

        GridBagConstraints grid = new GridBagConstraints();

        loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBackground(Color.decode("#fffffe"));
        fixSize(loginPanel);

        int leftWidth = canvasWidth * 45 / 100;
        int rightWidth = canvasWidth - leftWidth;

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(null);
        leftPanel.setBackground(Color.decode("#12376b"));
        leftPanel.setBounds(0, 0, leftWidth, canvasHeight);
        addSidebarLogo(leftPanel);

        if (accountType.equals("Employer")) {
            addSidebarText(leftPanel, leftWidth, "Find the right talent.", "Post openings, review applicants, and grow your team with JobMatch.");
        } else {
            addSidebarText(leftPanel, leftWidth, "Find opportunities that fit you.", "Build your profile, explore opportunities, and move your applications forward.");
        }
        loginPanel.add(leftPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setBackground(Color.decode("#fffffe"));
        rightPanel.setBounds(leftWidth, 0, rightWidth, canvasHeight);

        closeLoginBtn = new JButton("X");
        closeLoginBtn.setFont(new Font("Arial", Font.BOLD, 24));
        closeLoginBtn.setForeground(Color.decode("#333333"));
        closeLoginBtn.setBorderPainted(false);
        closeLoginBtn.setContentAreaFilled(false);
        closeLoginBtn.setFocusable(false);
        closeLoginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeLoginBtn.setBounds(rightWidth - 80, 15, 60, 40);
        closeLoginBtn.addActionListener(this);
        rightPanel.add(closeLoginBtn);

        int formWidth = 400;
        int formX = (rightWidth - formWidth) / 2;
        int formY = Math.max(centerY(450), 20);

        JLabel typeLabel = new JLabel(accountType.isEmpty() ? "" : accountType + " Account", SwingConstants.CENTER);
        typeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        typeLabel.setForeground(Color.decode("#2563eb"));
        typeLabel.setBounds(formX, formY, formWidth, 20);
        rightPanel.add(typeLabel);

        JLabel loginTitle = new JLabel("Welcome back", SwingConstants.CENTER);
        loginTitle.setFont(new Font("Arial", Font.BOLD, 36));
        loginTitle.setForeground(Color.decode("#0d0d0d"));
        loginTitle.setBounds(formX, formY + 25, formWidth, 45);
        rightPanel.add(loginTitle);

        JLabel loginSubtitle = new JLabel("Sign in to continue to JobMatch", SwingConstants.CENTER);
        loginSubtitle.setFont(new Font("Arial", Font.PLAIN, 15));
        loginSubtitle.setForeground(Color.decode("#71717a"));
        loginSubtitle.setBounds(formX, formY + 75, formWidth, 22);
        rightPanel.add(loginSubtitle);

        userLabel = new JLabel("Email address");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(Color.decode("#3f3f46"));
        userLabel.setBounds(formX, formY + 120, formWidth, 20);
        rightPanel.add(userLabel);

        emailInputLogin = new JTextField();
        emailInputLogin.setFont(new Font("Arial", Font.PLAIN, 16));
        emailInputLogin.setBounds(formX, formY + 142, formWidth, 42);
        emailInputLogin.setBorder(inputBorder());
        emailInputLogin.addActionListener(e -> loginBtn.doClick());
        rightPanel.add(emailInputLogin);

        passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setForeground(Color.decode("#3f3f46"));
        passLabel.setBounds(formX, formY + 200, formWidth, 20);
        rightPanel.add(passLabel);

        passwordInputLogin = new PasswordField();
        passwordInputLogin.setBounds(formX, formY + 222, formWidth, 42);
        rightPanel.add(passwordInputLogin);

        rememberCheck = new JCheckBox("Remember me");
        rememberCheck.setFont(new Font("Arial", Font.PLAIN, 14));
        rememberCheck.setForeground(Color.decode("#3f3f46"));
        rememberCheck.setBackground(Color.decode("#fffffe"));
        rememberCheck.setFocusable(false);
        rememberCheck.setBounds(formX + formWidth - 400, formY + 280, 160, 25);
        rightPanel.add(rememberCheck);

        goToForgotBtn = new JButton("Forgot password?");
        goToForgotBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        goToForgotBtn.setForeground(Color.decode("#2563eb"));
        goToForgotBtn.setBorderPainted(false);
        goToForgotBtn.setContentAreaFilled(false);
        goToForgotBtn.setFocusable(false);
        goToForgotBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goToForgotBtn.setBounds(formX + formWidth - 160, formY + 280, 160, 25);
        goToForgotBtn.setHorizontalAlignment(SwingConstants.RIGHT);
        goToForgotBtn.addActionListener(this);
        rightPanel.add(goToForgotBtn);

        loginBtn = new JButton("Sign In");
        loginBtn.setBackground(Color.decode("#1a5de3"));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 17));
        loginBtn.setBounds(formX, formY + 320, formWidth, 46);
        loginBtn.setBorderPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.setFocusable(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(this);
        rightPanel.add(loginBtn);

        goToRegisterBtn = new JButton("Don't have an account? Create account");
        goToRegisterBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        goToRegisterBtn.setForeground(Color.decode("#2563eb"));
        goToRegisterBtn.setBorderPainted(false);
        goToRegisterBtn.setContentAreaFilled(false);
        goToRegisterBtn.setFocusable(false);
        goToRegisterBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goToRegisterBtn.setBounds(formX, formY + 385, formWidth, 25);
        goToRegisterBtn.setHorizontalAlignment(SwingConstants.CENTER);
        goToRegisterBtn.addActionListener(this);
        rightPanel.add(goToRegisterBtn);

        switchTypeBtn = new JButton("Switch account type");
        switchTypeBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        switchTypeBtn.setForeground(Color.decode("#2563eb"));
        switchTypeBtn.setBorderPainted(false);
        switchTypeBtn.setContentAreaFilled(false);
        switchTypeBtn.setFocusable(false);
        switchTypeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        switchTypeBtn.setBounds(formX, formY + 420, formWidth, 25);
        switchTypeBtn.setHorizontalAlignment(SwingConstants.CENTER);
        switchTypeBtn.addActionListener(this);
        rightPanel.add(switchTypeBtn);

        loginPanel.add(rightPanel);

        grid.gridx = 0;
        grid.gridy = 0;
        grid.anchor = GridBagConstraints.CENTER;
        centerWrapper.add(loginPanel, grid);

        centerWrapper.revalidate();
        centerWrapper.repaint();
    }

    public void Register() {
        setCanvas(authWidth, authHeight);
        layout.showHeader(false);

        GridBagConstraints grid = new GridBagConstraints();

        registerPanel = new JPanel();
        registerPanel.setLayout(null);
        registerPanel.setBackground(Color.decode("#fffffe"));
        fixSize(registerPanel);

        int leftWidth = canvasWidth * 45 / 100;
        int rightWidth = canvasWidth - leftWidth;

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(null);
        leftPanel.setBackground(Color.decode("#12376b"));
        leftPanel.setBounds(0, 0, leftWidth, canvasHeight);
        addSidebarLogo(leftPanel);

        if (accountType.equals("Employer")) {
            addSidebarText(leftPanel, leftWidth, "Your next great hire starts here.", "Build your company profile, discover the right talent, and take the next step in your hiring journey.");
        } else {
            addSidebarText(leftPanel, leftWidth, "Your next opportunity starts here.", "Build your profile, discover personalized opportunities, and take the next step in your career.");
        }
        registerPanel.add(leftPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setBackground(Color.decode("#fffffe"));
        rightPanel.setBounds(leftWidth, 0, rightWidth, canvasHeight);

        closeRegisterBtn = new JButton("X");
        closeRegisterBtn.setFont(new Font("Arial", Font.BOLD, 24));
        closeRegisterBtn.setForeground(Color.decode("#333333"));
        closeRegisterBtn.setBorderPainted(false);
        closeRegisterBtn.setContentAreaFilled(false);
        closeRegisterBtn.setFocusable(false);
        closeRegisterBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeRegisterBtn.setBounds(rightWidth - 80, 15, 60, 40);
        closeRegisterBtn.addActionListener(this);
        rightPanel.add(closeRegisterBtn);

        int formWidth = 480;
        int formX = (rightWidth - formWidth) / 2;
        int extra = 0;
        if (accountType.equals("Employer")) {
            extra = 72;
        }
        int formY = Math.max(centerY(520 + extra), 20);
        int halfWidth = (formWidth - 15) / 2;

        JLabel registeringAs = new JLabel("Registering as " + accountType, SwingConstants.RIGHT);
        registeringAs.setFont(new Font("Arial", Font.PLAIN, 13));
        registeringAs.setForeground(Color.decode("#71717a"));
        registeringAs.setBounds(formX, formY, formWidth / 2 + 40, 20);
        rightPanel.add(registeringAs);

        changeTypeBtn = new JButton("Change");
        changeTypeBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        changeTypeBtn.setForeground(Color.decode("#2563eb"));
        changeTypeBtn.setBorderPainted(false);
        changeTypeBtn.setContentAreaFilled(false);
        changeTypeBtn.setFocusable(false);
        changeTypeBtn.setMargin(new Insets(0, 0, 0, 0));
        changeTypeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changeTypeBtn.setBounds(formX + formWidth / 2 + 48, formY, 80, 20);
        changeTypeBtn.setHorizontalAlignment(SwingConstants.LEFT);
        changeTypeBtn.addActionListener(this);
        rightPanel.add(changeTypeBtn);

        JLabel registerTitle = new JLabel("Create your JobMatch account", SwingConstants.CENTER);
        registerTitle.setFont(new Font("Arial", Font.BOLD, 28));
        registerTitle.setForeground(Color.decode("#0d0d0d"));
        registerTitle.setBounds(formX, formY + 28, formWidth, 40);
        rightPanel.add(registerTitle);

        String subtitleText = "Start building your profile and discover opportunities that fit you.";
        if (accountType.equals("Employer")) {
            subtitleText = "Start building your company profile and connect with qualified candidates.";
        }
        JLabel registerSubtitle = new JLabel(subtitleText, SwingConstants.CENTER);
        registerSubtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        registerSubtitle.setForeground(Color.decode("#71717a"));
        registerSubtitle.setBounds(formX, formY + 72, formWidth, 22);
        rightPanel.add(registerSubtitle);

        JLabel firstNameLabel = new JLabel("First Name");
        firstNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        firstNameLabel.setForeground(Color.decode("#3f3f46"));
        firstNameLabel.setBounds(formX, formY + 110, halfWidth, 18);
        rightPanel.add(firstNameLabel);

        firstNameInput = new JTextField();
        firstNameInput.setFont(new Font("Arial", Font.PLAIN, 16));
        firstNameInput.setBounds(formX, formY + 130, halfWidth, 38);
        firstNameInput.setBorder(inputBorder());
        rightPanel.add(firstNameInput);

        JLabel lastNameLabel = new JLabel("Last Name");
        lastNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        lastNameLabel.setForeground(Color.decode("#3f3f46"));
        lastNameLabel.setBounds(formX + halfWidth + 15, formY + 110, halfWidth, 18);
        rightPanel.add(lastNameLabel);

        lastNameInput = new JTextField();
        lastNameInput.setFont(new Font("Arial", Font.PLAIN, 16));
        lastNameInput.setBounds(formX + halfWidth + 15, formY + 130, halfWidth, 38);
        lastNameInput.setBorder(inputBorder());
        rightPanel.add(lastNameInput);

        if (accountType.equals("Employer")) {
            JLabel companyLabel = new JLabel("Company Name");
            companyLabel.setFont(new Font("Arial", Font.BOLD, 14));
            companyLabel.setForeground(Color.decode("#3f3f46"));
            companyLabel.setBounds(formX, formY + 182, formWidth, 18);
            rightPanel.add(companyLabel);

            companyInput = new JTextField();
            companyInput.setFont(new Font("Arial", Font.PLAIN, 16));
            companyInput.setBounds(formX, formY + 202, formWidth, 38);
            companyInput.setBorder(inputBorder());
            rightPanel.add(companyInput);
        }

        JLabel emailLabel = new JLabel("Email address");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setForeground(Color.decode("#3f3f46"));
        emailLabel.setBounds(formX, formY + 182 + extra, formWidth, 18);
        rightPanel.add(emailLabel);

        emailInputRegister = new JTextField();
        emailInputRegister.setFont(new Font("Arial", Font.PLAIN, 16));
        emailInputRegister.setBounds(formX, formY + 202 + extra, formWidth, 38);
        emailInputRegister.setBorder(inputBorder());
        emailInputRegister.addActionListener(e -> registerBtn.doClick());
        rightPanel.add(emailInputRegister);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setForeground(Color.decode("#3f3f46"));
        passLabel.setBounds(formX, formY + 254 + extra, formWidth, 18);
        rightPanel.add(passLabel);

        passwordInputRegister = new PasswordField();
        passwordInputRegister.setBounds(formX, formY + 274 + extra, formWidth, 38);
        rightPanel.add(passwordInputRegister);

        JLabel confirmLabel = new JLabel("Confirm password");
        confirmLabel.setFont(new Font("Arial", Font.BOLD, 14));
        confirmLabel.setForeground(Color.decode("#3f3f46"));
        confirmLabel.setBounds(formX, formY + 326 + extra, formWidth, 18);
        rightPanel.add(confirmLabel);

        confirmInputRegister = new PasswordField();
        confirmInputRegister.setBounds(formX, formY + 346 + extra, formWidth, 38);
        rightPanel.add(confirmInputRegister);

        termsCheck = new JCheckBox("I agree to the Terms and Privacy Policy");
        termsCheck.setFont(new Font("Arial", Font.PLAIN, 14));
        termsCheck.setForeground(Color.decode("#3f3f46"));
        termsCheck.setBackground(Color.decode("#fffffe"));
        termsCheck.setFocusable(false);
        termsCheck.setBounds(formX, formY + 398 + extra, formWidth, 25);
        rightPanel.add(termsCheck);

        registerBtn = new JButton("Create Account");
        registerBtn.setBackground(Color.decode("#1A5DE3"));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Arial", Font.BOLD, 17));
        registerBtn.setBounds(formX, formY + 436 + extra, formWidth, 46);
        registerBtn.setBorderPainted(false);
        registerBtn.setOpaque(true);
        registerBtn.setFocusable(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.addActionListener(this);
        rightPanel.add(registerBtn);

        backToLoginBtn = new JButton("Already have an account? Sign in");
        backToLoginBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        backToLoginBtn.setForeground(Color.decode("#2563eb"));
        backToLoginBtn.setBorderPainted(false);
        backToLoginBtn.setContentAreaFilled(false);
        backToLoginBtn.setFocusable(false);
        backToLoginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLoginBtn.setBounds(formX, formY + 492 + extra, formWidth, 25);
        backToLoginBtn.setHorizontalAlignment(SwingConstants.CENTER);
        backToLoginBtn.addActionListener(this);
        rightPanel.add(backToLoginBtn);

        registerPanel.add(rightPanel);

        grid.gridx = 0;
        grid.gridy = 0;
        grid.anchor = GridBagConstraints.CENTER;
        centerWrapper.add(registerPanel, grid);

        centerWrapper.revalidate();
        centerWrapper.repaint();
    }

    public void Forgot() {
        setCanvas(homeWidth, homeHeight);
        layout.showHeader(true);
        GridBagConstraints grid = new GridBagConstraints();

        forgotPanel = new JPanel();
        forgotPanel.setLayout(null);
        forgotPanel.setBackground(Color.decode("#fffffe"));
        fixSize(forgotPanel);

        closeForgotBtn = new JButton("X");
        closeForgotBtn.setFont(new Font("Arial", Font.BOLD, 24));
        closeForgotBtn.setForeground(Color.decode("#333333"));
        closeForgotBtn.setBorderPainted(false);
        closeForgotBtn.setContentAreaFilled(false);
        closeForgotBtn.setFocusable(false);
        closeForgotBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeForgotBtn.setBounds(canvasWidth - 80, 20, 60, 40);
        closeForgotBtn.addActionListener(this);
        forgotPanel.add(closeForgotBtn);

        int formWidth = 450;
        int formX = centerX(formWidth);
        int formY = Math.max(centerY(460), 10);

        JLabel forgotTitle = new JLabel("Reset Password", SwingConstants.CENTER);
        forgotTitle.setFont(new Font("Arial", Font.BOLD, 40));
        forgotTitle.setForeground(Color.decode("#0d0d0d"));
        forgotTitle.setBounds(formX, formY, formWidth, 55);
        forgotPanel.add(forgotTitle);

        JTextArea forgotSubtitle = new JTextArea("Provide your account email address along with your new password choice below to update your profile credentials.");
        forgotSubtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        forgotSubtitle.setForeground(Color.decode("#71717a"));
        forgotSubtitle.setEditable(false);
        forgotSubtitle.setFocusable(false);
        forgotSubtitle.setOpaque(false);
        forgotSubtitle.setLineWrap(true);
        forgotSubtitle.setWrapStyleWord(true);
        forgotSubtitle.setBounds(formX, formY + 70, formWidth, 50);
        forgotPanel.add(forgotSubtitle);

        JLabel lookupLabel = new JLabel("Registered Email Address");
        lookupLabel.setFont(new Font("Arial", Font.BOLD, 14));
        lookupLabel.setForeground(Color.decode("#3f3f46"));
        lookupLabel.setBounds(formX, formY + 145, formWidth, 20);
        forgotPanel.add(lookupLabel);

        forgetNameInput = new JTextField();
        forgetNameInput.setFont(new Font("Arial", Font.PLAIN, 16));
        forgetNameInput.setBounds(formX, formY + 170, formWidth, 50);
        forgetNameInput.setBorder(inputBorder());
        forgotPanel.add(forgetNameInput);

        JLabel passLabel = new JLabel("New Password");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setForeground(Color.decode("#3f3f46"));
        passLabel.setBounds(formX, formY + 235, formWidth, 20);
        forgotPanel.add(passLabel);

        forgetPassInput = new PasswordField();
        forgetPassInput.setBounds(formX, formY + 260, formWidth, 50);
        forgotPanel.add(forgetPassInput);

        forgotBtn = new JButton("Reset Password");
        forgotBtn.setBackground(Color.decode("#1A5DE3"));
        forgotBtn.setForeground(Color.WHITE);
        forgotBtn.setFont(new Font("Arial", Font.BOLD, 18));
        forgotBtn.setBounds(formX, formY + 340, formWidth, 55);
        forgotBtn.setBorderPainted(false);
        forgotBtn.setOpaque(true);
        forgotBtn.setFocusable(false);
        forgotBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotBtn.addActionListener(this);
        forgotPanel.add(forgotBtn);

        backToLoginFromForgotBtn = new JButton("Return to Sign In menu layout");
        backToLoginFromForgotBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        backToLoginFromForgotBtn.setForeground(Color.decode("#2563eb"));
        backToLoginFromForgotBtn.setBorderPainted(false);
        backToLoginFromForgotBtn.setContentAreaFilled(false);
        backToLoginFromForgotBtn.setFocusable(false);
        backToLoginFromForgotBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLoginFromForgotBtn.setBounds(formX, formY + 420, formWidth, 25);
        backToLoginFromForgotBtn.setHorizontalAlignment(SwingConstants.CENTER);
        backToLoginFromForgotBtn.addActionListener(this);
        forgotPanel.add(backToLoginFromForgotBtn);

        grid.gridx = 0;
        grid.gridy = 0;
        grid.anchor = GridBagConstraints.CENTER;
        centerWrapper.add(forgotPanel, grid);

        centerWrapper.revalidate();
        centerWrapper.repaint();
    }

    public void goHome(JPanel current) {
        centerWrapper.remove(current);
        setCanvas(homeWidth, homeHeight);
        layout.showHeader(true);
        centerWrapper.add(invinciblePanel);
        centerWrapper.revalidate();
        centerWrapper.repaint();
    }

    public void loginUser() {
        String email = emailInputLogin.getText().trim();
        String password = new String(passwordInputLogin.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your email and password.", "Sign In", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = Database.loginUser(email, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Incorrect email or password.", "Sign In", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!accountType.isEmpty() && !accountType.equals(user.getRole())) {
            JOptionPane.showMessageDialog(this,
                    "This account is registered as " + user.getRole() + ".\nUse \"Switch account type\" to sign in as " + user.getRole() + ".",
                    "Sign In", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (rememberCheck.isSelected()) {
            Database.rememberUser(user.getRole(), user.getEmail());
        } else {
            Database.forgetUser(user.getRole());
        }

        user.openDashboard();
        this.dispose();
    }

    public void registerUser() {
        String firstName = firstNameInput.getText().trim();
        String lastName = lastNameInput.getText().trim();
        String email = emailInputRegister.getText().trim();
        String emailPattern = "^[A-Za-z0-9._%+-]+@(tip\\.edu\\.ph|gmail\\.com)$";
        String password = new String(passwordInputRegister.getPassword());
        String confirm = new String(confirmInputRegister.getPassword());

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Create Account", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ADD THIS: employers must also type their company name
        String companyName = "";
        if (accountType.equals("Employer")) {
            companyName = companyInput.getText().trim();
            if (companyName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your company name.", "Create Account", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        if (!email.matches(emailPattern)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Create Account", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Create Account", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Create Account", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!termsCheck.isSelected()) {
            JOptionPane.showMessageDialog(this, "Please agree to the Terms and Privacy Policy.", "Create Account", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (Database.emailExists(email)) {
            JOptionPane.showMessageDialog(this, "That email is already registered.", "Create Account", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (Database.registerUser(firstName, lastName, email, password, accountType, companyName)) {
            JOptionPane.showMessageDialog(this, "Account created! You can now sign in.", "Create Account", JOptionPane.INFORMATION_MESSAGE);
            centerWrapper.remove(registerPanel);
            Login();
        } else {
            JOptionPane.showMessageDialog(this, "Something went wrong while creating your account.", "Create Account", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jobSeekerBtn) {
            accountType = "Job Seeker";
            if (openSavedAccount("Job Seeker")) { // ADD THIS: saved login, skip the login page
                return;
            }
            centerWrapper.remove(invinciblePanel);
            Login();
        } else if (e.getSource() == hiringBtn) {
            accountType = "Employer";
            if (openSavedAccount("Employer")) { // ADD THIS: saved login, skip the login page
                return;
            }
            centerWrapper.remove(invinciblePanel);
            Login();
        } else if (e.getSource() == signInLinkBtn) {
            accountType = "";
            centerWrapper.remove(invinciblePanel);
            Login();
        } else if (e.getSource() == closeLoginBtn) {
            goHome(loginPanel);
        } else if (e.getSource() == switchTypeBtn) {
            goHome(loginPanel);
        } else if (e.getSource() == goToRegisterBtn) {
            if (accountType.isEmpty()) {
                goHome(loginPanel);
                JOptionPane.showMessageDialog(this, "Please choose an account type first to create an account.", "Create Account", JOptionPane.INFORMATION_MESSAGE);
            } else {
                centerWrapper.remove(loginPanel);
                Register();
            }
        } else if (e.getSource() == goToForgotBtn) {
            centerWrapper.remove(loginPanel);
            Forgot();
        } else if (e.getSource() == loginBtn) {
            loginUser();
        } else if (e.getSource() == closeRegisterBtn) {
            goHome(registerPanel);
        } else if (e.getSource() == changeTypeBtn) {
            goHome(registerPanel);
        } else if (e.getSource() == backToLoginBtn) {
            centerWrapper.remove(registerPanel);
            Login();
        } else if (e.getSource() == registerBtn) {
            registerUser();
        }
        else if (e.getSource() == closeForgotBtn) {
            goHome(forgotPanel);
        } else if (e.getSource() == backToLoginFromForgotBtn) {
            centerWrapper.remove(forgotPanel);
            Login();
        } else if (e.getSource() == forgotBtn) {
            String email = forgetNameInput.getText().trim();
            String newPassword = new String(forgetPassInput.getPassword()).trim();

            if (email.isEmpty() || newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill out both the email and new password fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (Database.resetPassword(email, newPassword)) {
                JOptionPane.showMessageDialog(this, "Your account password has been successfully updated!", "Success", JOptionPane.INFORMATION_MESSAGE);

                forgetNameInput.setText("");
                forgetPassInput.setText("");

            } else {
                JOptionPane.showMessageDialog(this, "No profile account found with that email address.", "Reset Failed", JOptionPane.ERROR_MESSAGE);
            }
        }

    }// end ng action


    @Override public void keyTyped(KeyEvent e) {

    }
    @Override public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (loginPanel != null && loginPanel.isShowing()) {
                loginBtn.doClick();
            } else if (registerPanel != null && registerPanel.isShowing()) {
                registerBtn.doClick();
            } else if (forgotPanel != null && forgotPanel.isShowing()) {
                forgotBtn.doClick();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            if (loginPanel != null && loginPanel.isShowing()) {
                loginBtn.requestFocusInWindow();
            } else if (registerPanel != null && registerPanel.isShowing()) {
                registerBtn.requestFocusInWindow();
            }
        }
    }

} // pinaka end