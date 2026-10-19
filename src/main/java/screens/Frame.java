package screens;

import components.Layout;
import components.Logo;
import database.Database;
import users.User;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Frame extends JFrame implements ActionListener, KeyListener {
    Image scaled;
    JButton jobSeekerBtn, hiringBtn, loginBtn, closeLoginBtn, registerBtn , closeRegisterBtn, goToRegisterBtn, goToForgotBtn, backToLoginBtn, forgotBtn, closeForgotBtn, backToLoginFromForgotBtn, signInLinkBtn, switchTypeBtn, changeTypeBtn, termsLinkBtn, privacyPolicyLinkBtn;
    JPanel centerWrapper;

    JPanel invinciblePanel, jobSeekerPanel, hiringPanel, registerPanel, loginPanel, forgotPanel;
    JTextPane jobSeekerDesc, hiringDesc;

    JTextField emailInputLogin, firstNameInput, lastNameInput, emailInputRegister, forgetNameInput, companyInput;
    RoundedPasswordField passwordInputLogin, passwordInputRegister, confirmInputRegister, forgetPassInput;
    JCheckBox rememberCheck, termsCheck;

    JLabel jobSeekerTitle, hiringTitle, userLabel, passLabel;
    int topY = 40, labelHeight;

    String accountType = "";

    Layout layout;

    static Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    static int headerHeight = 80;


    static int homeWidth = (int) (screen.width * 0.75);
    static int homeHeight = screen.height - headerHeight;


    static int authWidth = screen.width;
    static int authHeight = screen.height;

    static int canvasWidth = homeWidth;
    static int canvasHeight = homeHeight;
    static Dimension canvasSize = new Dimension(canvasWidth, canvasHeight);

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


    public static int centerX(int contentWidth) {
        return (canvasWidth - contentWidth) / 2;
    }

    public static int centerY(int contentHeight) {
        return (canvasHeight - contentHeight) / 2;
    }

    public Border inputBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#cbd5e1"), 1),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        );
    }

    private JButton createInlineLinkButton(String text) {
        JButton button = new JButton("<html><u>" + text + "</u></html>");
        button.setFont(new Font("Inter", Font.PLAIN, 14));
        button.setForeground(Color.decode("#2563eb"));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusable(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(0, 0, 0, 0));
        return button;
    }

    private void showPolicyDialog(String title, String content) {
        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Inter", Font.PLAIN, 14));
        textArea.setCaretPosition(0);
        textArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(560, 360));

        JOptionPane.showMessageDialog(this, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showTermsOfServiceDialog() {
        showPolicyDialog(
                "Terms of Service",
                "JobMatch Terms of Service\n\n" +
                        "1. Use JobMatch only for lawful recruitment and job-search activities.\n" +
                        "2. Provide accurate account information and keep your credentials secure.\n" +
                        "3. Do not post misleading, discriminatory, or harmful content.\n" +
                        "4. You are responsible for all activity under your account.\n" +
                        "5. JobMatch may suspend accounts that violate these terms.\n\n" +
                        "By creating an account, you agree to follow these terms."
        );
    }

    private void showPrivacyPolicyDialog() {
        showPolicyDialog(
                "Privacy Policy",
                "JobMatch Privacy Policy\n\n" +
                        "1. We collect profile, account, and usage information to provide the service.\n" +
                        "2. Your data is used for authentication, matching, and communication features.\n" +
                        "3. We do not sell your personal information to third parties.\n" +
                        "4. Data may be shared only when required for platform operations or by law.\n" +
                        "5. You can request account updates or deletion through support.\n\n" +
                        "By creating an account, you acknowledge this privacy policy."
        );
    }

    public Frame() {
        UIManager.put("Button.focus", new javax.swing.plaf.ColorUIResource(new Color(0, 0, 0, 0)));
        UIManager.put("Button.dashedFocus", Boolean.FALSE);
        this.setUndecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.addKeyListener(this);
        this.setResizable(false);
        this.getContentPane().setBackground(Color.decode("#F7FAFD"));

        setCanvas(homeWidth, homeHeight);

        layout = new Layout(this, false);

        centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(Color.decode("#F7FAFD"));
        GridBagConstraints grid = new GridBagConstraints();
        grid.weighty = 1.0;
        grid.fill = GridBagConstraints.VERTICAL;

        invinciblePanel = new JPanel();
        invinciblePanel.setLayout(null);
        invinciblePanel.setBackground(Color.decode("#F7FAFD"));
        fixSize(invinciblePanel);

        topY = Math.max(centerY(480), 10);

        JLabel welcomeMsg = new JLabel("How would you like to use JobMatch?", SwingConstants.CENTER);
        welcomeMsg.setFont(new Font("Inter", Font.BOLD, 43));
        welcomeMsg.setForeground(Color.decode("#091222"));
        welcomeMsg.setBounds(0, topY, canvasWidth, 50);
        invinciblePanel.add(welcomeMsg);

        JLabel welcomeMsg2 = new JLabel("Choose the account type that best describes how you will use the platform.", SwingConstants.CENTER);
        welcomeMsg2.setFont(new Font("Inter", Font.PLAIN, 23));
        welcomeMsg2.setForeground(Color.decode("#929daf"));
        welcomeMsg2.setBounds(0, topY + 60, canvasWidth, 30);
        invinciblePanel.add(welcomeMsg2);

        userChoice();

        grid.gridx = 0;
        grid.gridy = 0;
        grid.anchor = GridBagConstraints.CENTER;
        centerWrapper.add(invinciblePanel, grid);
        this.getContentPane().add(centerWrapper, BorderLayout.CENTER);
    }

    private JLabel seekerIcon, employerIcon, seekerCheck, employerCheck;
    private RoundedPanel seekerCircle, employerCircle;

    public void userChoice() {
        int panelWidth = Math.min(380, (canvasWidth - 100) / 2), panelHeight = 320;
        int gap = 18;
        int cardsX = centerX(panelWidth * 2 + gap);
        int cardsY = topY + 130;

        jobSeekerPanel = new RoundedPanel(13);
        jobSeekerPanel.setLayout(null);
        jobSeekerPanel.setBackground(Color.WHITE);
        jobSeekerPanel.setBounds(cardsX, cardsY, panelWidth, panelHeight);
        jobSeekerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        seekerCircle = new RoundedPanel(90);
        seekerCircle.setLayout(new BorderLayout());
        seekerCircle.setBackground(Color.decode("#F1F3F5"));
        seekerCircle.setBounds((panelWidth - 74) / 2, 27, 74, 74);
        seekerIcon = new JLabel(loadImage("src/images/user-blue.png", 38, 38));
        seekerIcon.setHorizontalAlignment(SwingConstants.CENTER);
        seekerCircle.add(seekerIcon);
        jobSeekerPanel.add(seekerCircle);

        seekerCheck = new JLabel(loadImage("src/images/checkmark.png", 23, 23));
        seekerCheck.setBounds(panelWidth - 43, 16, 23, 23);
        seekerCheck.setVisible(false);
        jobSeekerPanel.add(seekerCheck);

        jobSeekerTitle = new JLabel("Job Seeker", SwingConstants.CENTER);
        jobSeekerTitle.setFont(new Font("Inter", Font.BOLD, 30));
        jobSeekerTitle.setForeground(Color.decode("#0D0D0D"));
        jobSeekerTitle.setBounds(0, 115, panelWidth, 32);
        jobSeekerPanel.add(jobSeekerTitle);

        JTextPane jobSeekerDesc = new JTextPane();
        jobSeekerDesc.setText(
                "Find opportunities matched to your skills, interests, qualifications and preferences."
        );

        StyledDocument doc = jobSeekerDesc.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        styleChoiceDescription(jobSeekerDesc);
        jobSeekerDesc.setBounds(25, 153, panelWidth - 50, 70);
        jobSeekerDesc.setFont(new Font("Inter", Font.PLAIN, 18));
        jobSeekerPanel.add(jobSeekerDesc);

        jobSeekerBtn = new RoundedButton("Continue as Job Seeker");

        ImageIcon arrowIcon = loadImage("src/images/arrow.png", 20, 20);
        jobSeekerBtn.setIcon(arrowIcon);
        jobSeekerBtn.setHorizontalTextPosition(SwingConstants.LEFT);
        jobSeekerBtn.setIconTextGap(12);

        styleChoiceButton(jobSeekerBtn, true);
        jobSeekerBtn.setBounds(18, 245, panelWidth - 36, 44);
        jobSeekerBtn.addActionListener(this);
        jobSeekerBtn.setVisible(false);
        jobSeekerPanel.add(jobSeekerBtn);
        invinciblePanel.add(jobSeekerPanel);

        hiringPanel = new RoundedPanel(13);
        hiringPanel.setLayout(null);
        hiringPanel.setBackground(Color.WHITE);
        hiringPanel.setBounds(cardsX + panelWidth + gap, cardsY, panelWidth, panelHeight);
        hiringPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        employerCircle = new RoundedPanel(90);
        employerCircle.setLayout(new BorderLayout());
        employerCircle.setBackground(Color.decode("#F1F3F5"));
        employerCircle.setBounds((panelWidth - 74) / 2, 27, 74, 74);
        employerIcon = new JLabel(loadImage("src/images/building-blue.png", 38, 38));
        employerIcon.setHorizontalAlignment(SwingConstants.CENTER);
        employerCircle.add(employerIcon);
        hiringPanel.add(employerCircle);

        employerCheck = new JLabel(loadImage("src/images/checkmark.png", 23, 23));
        employerCheck.setBounds(panelWidth - 43, 16, 23, 23);
        employerCheck.setVisible(false);
        hiringPanel.add(employerCheck);

        hiringTitle = new JLabel("Employer", SwingConstants.CENTER);
        hiringTitle.setFont(new Font("Inter", Font.BOLD, 30));
        hiringTitle.setForeground(Color.decode("#0D0D0D"));
        hiringTitle.setBounds(0, 115, panelWidth, 32);
        hiringPanel.add(hiringTitle);


        hiringDesc = new JTextPane();
        hiringDesc.setText(
                "Post opportunities, review applicants and manage your recruitment process."
        );

        styleChoiceDescription(hiringDesc);
        hiringDesc.setBounds(25, 153, panelWidth - 50, 70);
        hiringDesc.setFont(new Font("Inter", Font.PLAIN, 18));
        hiringPanel.add(hiringDesc);


        hiringBtn = new RoundedButton("Continue as Employer");

        arrowIcon = loadImage("src/images/arrow.png", 20, 20);
        hiringBtn.setIcon(arrowIcon);
        hiringBtn.setHorizontalTextPosition(SwingConstants.LEFT);
        hiringBtn.setIconTextGap(12);

        styleChoiceButton(hiringBtn, false);
        hiringBtn.setBounds(18, 245, panelWidth - 36, 44);
        hiringBtn.addActionListener(this);
        hiringBtn.setVisible(false);
        hiringPanel.add(hiringBtn);
        invinciblePanel.add(hiringPanel);


        addSelectionListener(jobSeekerPanel, "Job Seeker");
        addSelectionListener(seekerCircle, "Job Seeker");
        addSelectionListener(seekerIcon, "Job Seeker");
        addSelectionListener(jobSeekerTitle, "Job Seeker");
        addSelectionListener(jobSeekerDesc, "Job Seeker");
        addSelectionListener(hiringPanel, "Employer");
        addSelectionListener(employerCircle, "Employer");
        addSelectionListener(employerIcon, "Employer");
        addSelectionListener(hiringTitle, "Employer");
        addSelectionListener(hiringDesc, "Employer");

        signInLinkBtn = new JButton(
                "<html>Already have an account? <u><font color='#195EE4'>Sign in</font></u></html>");
        signInLinkBtn.setFont(new Font("Inter", Font.PLAIN, 20));
        signInLinkBtn.setForeground(Color.decode("#929daf"));
        signInLinkBtn.setBorderPainted(false);
        signInLinkBtn.setContentAreaFilled(false);
        signInLinkBtn.setFocusable(false);
        signInLinkBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signInLinkBtn.setBounds(centerX(400), cardsY + panelHeight + 35, 400, 25);
        signInLinkBtn.setHorizontalAlignment(SwingConstants.CENTER);
        signInLinkBtn.addActionListener(this);
        invinciblePanel.add(signInLinkBtn);
    }

    private ImageIcon loadImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }


    private void styleChoiceDescription(JTextPane description) {
        description.setFont(new Font("Inter", Font.PLAIN, 15));
        description.setForeground(Color.decode("#34415F"));
        description.setEditable(false);
        description.setFocusable(false);
        description.setOpaque(false);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);
        description.setHighlighter(null);

        StyledDocument doc = description.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);

        doc.setParagraphAttributes(0, doc.getLength(), center, false);
    }


    private void styleChoiceButton(JButton button, boolean selectedStyle) {
        button.setFont(new Font("Inter", Font.PLAIN, 17));
        button.setBackground(selectedStyle ? Color.decode("#195EE4") : Color.WHITE);
        button.setForeground(selectedStyle ? Color.WHITE : Color.decode("#195EE4"));
        button.setBorder(BorderFactory.createEmptyBorder(9, 10, 9, 10));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void addSelectionListener(Component component, String role) {
        component.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { selectAccountType(role); }
        });
    }

    private void selectAccountType(String role) {
        if (openSavedAccount(role)) {
            return;
        }
        accountType = role;
        boolean seekerSelected = role.equals("Job Seeker");
        jobSeekerPanel.setBackground(seekerSelected ? Color.decode("#F0F5FF") : Color.WHITE);
        jobSeekerPanel.setBorder(BorderFactory.createLineBorder(
                seekerSelected ? Color.decode("#1D5FE8") : Color.decode("#D9DDE3"), seekerSelected ? 2 : 1));
        hiringPanel.setBackground(!seekerSelected ? Color.decode("#F0F5FF") : Color.WHITE);
        hiringPanel.setBorder(BorderFactory.createLineBorder(
                !seekerSelected ? Color.decode("#1D5FE8") : Color.decode("#D9DDE3"), !seekerSelected ? 2 : 1));
        seekerCircle.setBackground(seekerSelected ? Color.decode("#E0ECFF") : Color.decode("#F1F3F5"));
        employerCircle.setBackground(!seekerSelected ? Color.decode("#E0ECFF") : Color.decode("#F1F3F5"));
        seekerIcon.setIcon(loadImage(seekerSelected ? "src/images/user-blue.png" : "src/images/user-black.png", 38, 38));
        employerIcon.setIcon(loadImage(!seekerSelected ? "src/images/building-blue.png" : "src/images/building-black.png", 38, 38));
        seekerCheck.setVisible(seekerSelected);
        employerCheck.setVisible(!seekerSelected);
        jobSeekerBtn.setVisible(seekerSelected);
        hiringBtn.setVisible(!seekerSelected);
        styleChoiceButton(jobSeekerBtn, seekerSelected);
        styleChoiceButton(hiringBtn, !seekerSelected);
        jobSeekerPanel.repaint();
        hiringPanel.repaint();
    }

    private static class PlaceholderField extends JTextField {
        private final String placeholder;
        PlaceholderField(String placeholder) {
            this.placeholder = placeholder;
            setBackground(Color.WHITE);
            setOpaque(false);
        }
        @Override protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.decode("#cbd5e1"));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 11, 11);
            g2.dispose();
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D bg = (Graphics2D) g.create();
            bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            bg.setColor(Color.WHITE);
            bg.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 11, 11);
            bg.dispose();
            super.paintComponent(g);
            if (getText().isEmpty()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(Color.decode("#9ca3af"));
                g2.setFont(getFont());
                Insets in = getInsets();
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(placeholder, in.left, y);
                g2.dispose();
            }
        }
    }

    private static class RoundedPasswordField extends JPasswordField {
        private final String placeholder;
        private boolean revealed = false;
        private static final int EYE_ZONE = 44;

        RoundedPasswordField(String placeholder) {
            this.placeholder = placeholder;
            final char hiddenEcho = getEchoChar();
            setFont(new Font("Inter", Font.PLAIN, 16));
            setBackground(Color.WHITE);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(0, 13, 0, EYE_ZONE));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (e.getX() >= getWidth() - EYE_ZONE) {
                        revealed = !revealed;
                        setEchoChar(revealed ? (char) 0 : hiddenEcho);
                        repaint();
                    }
                }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    setCursor(Cursor.getPredefinedCursor(e.getX() >= getWidth() - EYE_ZONE
                            ? Cursor.HAND_CURSOR : Cursor.TEXT_CURSOR));
                }
            });
        }

        private void paintEye(Graphics2D g2) {
            int cx = getWidth() - EYE_ZONE / 2 - 2;
            int cy = getHeight() / 2;
            g2.setColor(Color.decode("#6b7280"));
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            java.awt.geom.Path2D eye = new java.awt.geom.Path2D.Double();
            eye.moveTo(cx - 10, cy);
            eye.quadTo(cx, cy - 10, cx + 10, cy);
            eye.quadTo(cx, cy + 10, cx - 10, cy);
            g2.draw(eye);
            g2.fillOval(cx - 3, cy - 3, 6, 6);
            if (revealed) {
                g2.drawLine(cx - 9, cy + 8, cx + 9, cy - 8);
            }
        }
        @Override protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.decode("#cbd5e1"));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 11, 11);
            g2.dispose();
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D bg = (Graphics2D) g.create();
            bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            bg.setColor(Color.WHITE);
            bg.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 11, 11);
            bg.dispose();
            super.paintComponent(g);
            Graphics2D eyeG = (Graphics2D) g.create();
            eyeG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            paintEye(eyeG);
            eyeG.dispose();
            if (getPassword().length == 0) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(Color.decode("#9ca3af"));
                g2.setFont(getFont());
                Insets in = getInsets();
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(placeholder, in.left, y);
                g2.dispose();
            }
        }
    }

    private static class RoundedButton extends JButton {
        RoundedButton(String text) { super(text); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
        @Override protected void paintBorder(Graphics g) {
            if (getForeground().equals(Color.decode("#34415F"))) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#D9DDE3"));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 9, 9);
                g2.dispose();
            }
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        RoundedPanel(int radius) { this.radius = radius; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
        @Override protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBorder() instanceof javax.swing.border.LineBorder
                    ? ((javax.swing.border.LineBorder) getBorder()).getLineColor() : Color.decode("#D9DDE3"));
            int thickness = getBorder() instanceof javax.swing.border.LineBorder
                    ? ((javax.swing.border.LineBorder) getBorder()).getThickness() : 1;
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(thickness / 2, thickness / 2, getWidth() - thickness - 1,
                    getHeight() - thickness - 1, radius, radius);
            g2.dispose();
        }
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
        sideTitle.setFont(new Font("Inter", Font.BOLD, 44));
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
        sideSub.setFont(new Font("Inter", Font.PLAIN, 18));
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
        loginPanel.setBackground(Color.decode("#F7FAFD"));

        Dimension fullScreenSize = new Dimension(authWidth, authHeight);
        loginPanel.setPreferredSize(fullScreenSize);
        loginPanel.setMinimumSize(fullScreenSize);
        loginPanel.setMaximumSize(fullScreenSize);

        int leftWidth = canvasWidth * 40 / 100;
        int rightWidth = canvasWidth - leftWidth;

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(null);
        leftPanel.setBackground(Color.decode("#103358"));
        leftPanel.setBounds(0, 0, leftWidth, authHeight);
        addSidebarLogo(leftPanel);


        addSidebarText(leftPanel, leftWidth, "Welcome back to JobMatch.", "Sign in to pick up where you left off, whether you are finding opportunities or hiring talent.");
        loginPanel.add(leftPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setBackground(Color.decode("#F7FAFD"));
        rightPanel.setBounds(leftWidth, 0, rightWidth, authHeight);

        closeLoginBtn = new JButton("X");
        closeLoginBtn.setFont(new Font("Inter", Font.BOLD, 24));
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

        JLabel loginTitle = new JLabel("Welcome back", SwingConstants.CENTER);
        loginTitle.setFont(new Font("Inter", Font.BOLD, 36));
        loginTitle.setForeground(Color.decode("#0d0d0d"));
        loginTitle.setBounds(formX, formY + 25, formWidth, 45);
        rightPanel.add(loginTitle);

        JLabel loginSubtitle = new JLabel("Sign in to continue to JobMatch", SwingConstants.CENTER);
        loginSubtitle.setFont(new Font("Inter", Font.PLAIN, 15));
        loginSubtitle.setForeground(Color.decode("#71717a"));
        loginSubtitle.setBounds(formX, formY + 75, formWidth, 22);
        rightPanel.add(loginSubtitle);

        userLabel = new JLabel("Email address");
        userLabel.setFont(new Font("Inter", Font.BOLD, 14));
        userLabel.setForeground(Color.decode("#3f3f46"));
        userLabel.setBounds(formX, formY + 120, formWidth, 20);
        rightPanel.add(userLabel);

        emailInputLogin = new PlaceholderField("email@example.com");
        emailInputLogin.setFont(new Font("Inter", Font.PLAIN, 16));
        emailInputLogin.setBounds(formX, formY + 142, formWidth, 42);
        emailInputLogin.setBorder(inputBorder());
        emailInputLogin.addActionListener(e -> loginBtn.doClick());
        rightPanel.add(emailInputLogin);

        passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Inter", Font.BOLD, 14));
        passLabel.setForeground(Color.decode("#3f3f46"));
        passLabel.setBounds(formX, formY + 200, formWidth, 20);
        rightPanel.add(passLabel);

        passwordInputLogin = new RoundedPasswordField("Enter your password");
        passwordInputLogin.setBackground(Color.WHITE);
        passwordInputLogin.setBounds(formX, formY + 222, formWidth, 42);
        rightPanel.add(passwordInputLogin);

        rememberCheck = new JCheckBox("Remember me");
        rememberCheck.setFont(new Font("Inter", Font.PLAIN, 14));
        rememberCheck.setForeground(Color.decode("#3f3f46"));
        rememberCheck.setBackground(Color.decode("#F7FAFD"));
        rememberCheck.setFocusable(false);
        rememberCheck.setBounds(formX + formWidth - 400, formY + 280, 160, 25);
        rightPanel.add(rememberCheck);

        goToForgotBtn = new JButton("Forgot password?");
        goToForgotBtn.setFont(new Font("Inter", Font.PLAIN, 14));
        goToForgotBtn.setForeground(Color.decode("#2563eb"));
        goToForgotBtn.setBorderPainted(false);
        goToForgotBtn.setContentAreaFilled(false);
        goToForgotBtn.setFocusable(false);
        goToForgotBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goToForgotBtn.setBounds(formX + formWidth - 160, formY + 280, 160, 25);
        goToForgotBtn.setHorizontalAlignment(SwingConstants.RIGHT);
        goToForgotBtn.addActionListener(this);
        rightPanel.add(goToForgotBtn);

        loginBtn = new RoundedButton("Sign In");
        loginBtn.setBackground(Color.decode("#1a5de3"));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Inter", Font.BOLD, 17));
        loginBtn.setBounds(formX, formY + 320, formWidth, 46);
        loginBtn.setBorderPainted(false);
        loginBtn.setOpaque(false);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setFocusable(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(this);
        rightPanel.add(loginBtn);

        goToRegisterBtn = new JButton("<html><font color='#7F859C'>Don't have an account?</font> <font color='#2563eb'>Create account</font></html>");
        goToRegisterBtn.setFont(new Font("Inter", Font.PLAIN, 14));
        goToRegisterBtn.setForeground(Color.decode("#2563eb"));
        goToRegisterBtn.setBorderPainted(false);
        goToRegisterBtn.setContentAreaFilled(false);
        goToRegisterBtn.setFocusable(false);
        goToRegisterBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goToRegisterBtn.setBounds(formX, formY + 385, formWidth, 25);
        goToRegisterBtn.setHorizontalAlignment(SwingConstants.CENTER);
        goToRegisterBtn.addActionListener(this);
        rightPanel.add(goToRegisterBtn);

        loginPanel.add(rightPanel);

        grid.gridx = 0;
        grid.gridy = 0;
        grid.weightx = 1.0;
        grid.weighty = 1.0;
        grid.fill = GridBagConstraints.BOTH;
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
        registerPanel.setBackground(Color.decode("#F7FAFD"));

        Dimension fullScreenSize = new Dimension(authWidth, authHeight);
        registerPanel.setPreferredSize(fullScreenSize);
        registerPanel.setMinimumSize(fullScreenSize);
        registerPanel.setMaximumSize(fullScreenSize);

        int leftWidth = canvasWidth * 40 / 100;
        int rightWidth = canvasWidth - leftWidth;

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(null);
        leftPanel.setBackground(Color.decode("#103358"));
        leftPanel.setBounds(0, 0, leftWidth, authHeight);
        addSidebarLogo(leftPanel);

        if (accountType.equals("Employer")) {
            addSidebarText(leftPanel, leftWidth, "Your next great hire starts here.", "Build your company profile, discover the right talent, and take the next step in your hiring journey.");
        } else {
            addSidebarText(leftPanel, leftWidth, "Your next opportunity starts here.", "Build your profile, discover personalized opportunities, and take the next step in your career.");
        }
        registerPanel.add(leftPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setBackground(Color.decode("#F7FAFD"));
        rightPanel.setBounds(leftWidth, 0, rightWidth, authHeight);

        closeRegisterBtn = new JButton("X");
        closeRegisterBtn.setFont(new Font("Inter", Font.BOLD, 24));
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
        int iconShift = 84;
        int titleShift = 68 + iconShift;
        int formY = Math.max(centerY(520 + extra + titleShift), 20);
        int halfWidth = (formWidth - 15) / 2;

        RoundedPanel typeCircle = new RoundedPanel(90);
        typeCircle.setLayout(new BorderLayout());
        typeCircle.setBackground(Color.decode("#E0ECFF"));
        typeCircle.setBounds((rightWidth - 74) / 2, formY, 74, 74);
        JLabel typeIcon = new JLabel(loadImage(
                accountType.equals("Employer") ? "src/images/building-blue.png" : "src/images/user-blue.png", 38, 38));
        typeIcon.setHorizontalAlignment(SwingConstants.CENTER);
        typeCircle.add(typeIcon);
        rightPanel.add(typeCircle);

        JLabel registeringAs = new JLabel("<html>Registering as <font color='#2563eb'>" + accountType + "</font></html>", SwingConstants.CENTER);
        registeringAs.setFont(new Font("Inter", Font.PLAIN, 15));
        registeringAs.setForeground(Color.decode("#71717a"));
        registeringAs.setBounds(formX, formY + iconShift, formWidth, 20);
        rightPanel.add(registeringAs);


        JLabel registerTitle = new JLabel("<html><div style='text-align:center'>Create your JobMatch<br>account</div></html>", SwingConstants.CENTER);
        registerTitle.setFont(new Font("Inter", Font.BOLD, 40));
        registerTitle.setForeground(Color.decode("#0d0d0d"));
        registerTitle.setBounds(formX, formY + iconShift + 28, formWidth, 108);
        rightPanel.add(registerTitle);

        String subtitleText = "Start building your profile and discover opportunities that fit you.";
        if (accountType.equals("Employer")) {
            subtitleText = "Start building your company profile and connect with qualified candidates.";
        }
        JLabel registerSubtitle = new JLabel(subtitleText, SwingConstants.CENTER);
        registerSubtitle.setFont(new Font("Inter", Font.PLAIN, 16));
        registerSubtitle.setForeground(Color.decode("#71717a"));
        int subtitleWidth = Math.min(rightWidth - 40, 720);
        registerSubtitle.setBounds((rightWidth - subtitleWidth) / 2, formY + iconShift + 140, subtitleWidth, 22);
        rightPanel.add(registerSubtitle);

        JLabel firstNameLabel = new JLabel("First Name");
        firstNameLabel.setFont(new Font("Inter", Font.BOLD, 14));
        firstNameLabel.setForeground(Color.decode("#3f3f46"));
        firstNameLabel.setBounds(formX, formY + titleShift + 110, halfWidth, 18);
        rightPanel.add(firstNameLabel);

        firstNameInput = new PlaceholderField("Enter your first name");
        firstNameInput.setFont(new Font("Inter", Font.PLAIN, 16));
        firstNameInput.setBounds(formX, formY + titleShift + 130, halfWidth, 38);
        firstNameInput.setBorder(inputBorder());
        rightPanel.add(firstNameInput);

        JLabel lastNameLabel = new JLabel("Last Name");
        lastNameLabel.setFont(new Font("Inter", Font.BOLD, 14));
        lastNameLabel.setForeground(Color.decode("#3f3f46"));
        lastNameLabel.setBounds(formX + halfWidth + 15, formY + titleShift + 110, halfWidth, 18);
        rightPanel.add(lastNameLabel);

        lastNameInput = new PlaceholderField("Enter your last name");
        lastNameInput.setFont(new Font("Inter", Font.PLAIN, 16));
        lastNameInput.setBounds(formX + halfWidth + 15, formY + titleShift + 130, halfWidth, 38);
        lastNameInput.setBorder(inputBorder());
        rightPanel.add(lastNameInput);

        if (accountType.equals("Employer")) {
            JLabel companyLabel = new JLabel("Company Name");
            companyLabel.setFont(new Font("Inter", Font.BOLD, 14));
            companyLabel.setForeground(Color.decode("#3f3f46"));
            companyLabel.setBounds(formX, formY + titleShift + 182, formWidth, 18);
            rightPanel.add(companyLabel);

            companyInput = new PlaceholderField("Enter your company name");
            companyInput.setFont(new Font("Inter", Font.PLAIN, 16));
            companyInput.setBounds(formX, formY + titleShift + 202, formWidth, 38);
            companyInput.setBorder(inputBorder());
            rightPanel.add(companyInput);
        }

        JLabel emailLabel = new JLabel("Email address");
        emailLabel.setFont(new Font("Inter", Font.BOLD, 14));
        emailLabel.setForeground(Color.decode("#3f3f46"));
        emailLabel.setBounds(formX, formY + titleShift + 182 + extra, formWidth, 18);
        rightPanel.add(emailLabel);

        emailInputRegister = new PlaceholderField("email@example.com");
        emailInputRegister.setFont(new Font("Inter", Font.PLAIN, 16));
        emailInputRegister.setBounds(formX, formY + titleShift + 202 + extra, formWidth, 38);
        emailInputRegister.setBorder(inputBorder());
        emailInputRegister.addActionListener(e -> registerBtn.doClick());
        rightPanel.add(emailInputRegister);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Inter", Font.BOLD, 14));
        passLabel.setForeground(Color.decode("#3f3f46"));
        passLabel.setBounds(formX, formY + titleShift + 254 + extra, formWidth, 18);
        rightPanel.add(passLabel);

        passwordInputRegister = new RoundedPasswordField("Create a password");
        passwordInputRegister.setBackground(Color.WHITE);
        passwordInputRegister.setBounds(formX, formY + titleShift + 274 + extra, formWidth, 38);
        rightPanel.add(passwordInputRegister);

        JLabel confirmLabel = new JLabel("Confirm password");
        confirmLabel.setFont(new Font("Inter", Font.BOLD, 14));
        confirmLabel.setForeground(Color.decode("#3f3f46"));
        confirmLabel.setBounds(formX, formY + titleShift + 326 + extra, formWidth, 18);
        rightPanel.add(confirmLabel);

        confirmInputRegister = new RoundedPasswordField("Re-enter your password");
        confirmInputRegister.setBackground(Color.WHITE);
        confirmInputRegister.setBounds(formX, formY + titleShift + 346 + extra, formWidth, 38);
        rightPanel.add(confirmInputRegister);

        JPanel termsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        termsPanel.setOpaque(false);
        termsPanel.setBounds(formX, formY + titleShift + 398 + extra, formWidth, 25);

        termsCheck = new JCheckBox("I agree to the ");
        termsCheck.setFont(new Font("Inter", Font.PLAIN, 14));
        termsCheck.setForeground(Color.decode("#3f3f46"));
        termsCheck.setBackground(Color.decode("#F7FAFD"));
        termsCheck.setFocusable(false);
        termsCheck.setMargin(new Insets(0, 0, 0, 0));
        termsPanel.add(termsCheck);

        termsLinkBtn = createInlineLinkButton("Terms");
        termsLinkBtn.addActionListener(this);
        termsPanel.add(termsLinkBtn);

        JLabel andLabel = new JLabel(" and ");
        andLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        andLabel.setForeground(Color.decode("#3f3f46"));
        termsPanel.add(andLabel);

        privacyPolicyLinkBtn = createInlineLinkButton("Privacy Policy");
        privacyPolicyLinkBtn.addActionListener(this);
        termsPanel.add(privacyPolicyLinkBtn);
        rightPanel.add(termsPanel);

        registerBtn = new RoundedButton("Create Account");
        registerBtn.setBackground(Color.decode("#1A5DE3"));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Inter", Font.BOLD, 17));
        registerBtn.setBounds(formX, formY + titleShift + 436 + extra, formWidth, 46);
        registerBtn.setBorderPainted(false);
        registerBtn.setOpaque(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setFocusable(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.addActionListener(this);
        rightPanel.add(registerBtn);

        backToLoginBtn = new JButton("<html><font color='#7F859C'>Already have an account?</font> <font color='#2563eb'>Sign in</font></html>");
        backToLoginBtn.setFont(new Font("Inter", Font.PLAIN, 14));
        backToLoginBtn.setForeground(Color.decode("#2563eb"));
        backToLoginBtn.setBorderPainted(false);
        backToLoginBtn.setContentAreaFilled(false);
        backToLoginBtn.setFocusable(false);
        backToLoginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLoginBtn.setBounds(formX, formY + titleShift + 492 + extra, formWidth, 25);
        backToLoginBtn.setHorizontalAlignment(SwingConstants.CENTER);
        backToLoginBtn.addActionListener(this);
        rightPanel.add(backToLoginBtn);

        registerPanel.add(rightPanel);

        grid.gridx = 0;
        grid.gridy = 0;
        grid.weightx = 1.0;
        grid.weighty = 1.0;
        grid.fill = GridBagConstraints.BOTH;
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
        forgotPanel.setBackground(Color.decode("#F7FAFD"));
        fixSize(forgotPanel);

        closeForgotBtn = new JButton("X");
        closeForgotBtn.setFont(new Font("Inter", Font.BOLD, 24));
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
        forgotTitle.setFont(new Font("Inter", Font.BOLD, 40));
        forgotTitle.setForeground(Color.decode("#0d0d0d"));
        forgotTitle.setBounds(formX, formY, formWidth, 55);
        forgotPanel.add(forgotTitle);

        JTextArea forgotSubtitle = new JTextArea("Provide your account email address along with your new password choice below to update your profile credentials.");
        forgotSubtitle.setFont(new Font("Inter", Font.PLAIN, 16));
        forgotSubtitle.setForeground(Color.decode("#71717a"));
        forgotSubtitle.setEditable(false);
        forgotSubtitle.setFocusable(false);
        forgotSubtitle.setOpaque(false);
        forgotSubtitle.setLineWrap(true);
        forgotSubtitle.setWrapStyleWord(true);
        forgotSubtitle.setBounds(formX, formY + 70, formWidth, 50);
        forgotPanel.add(forgotSubtitle);

        JLabel lookupLabel = new JLabel("Registered Email Address");
        lookupLabel.setFont(new Font("Inter", Font.BOLD, 14));
        lookupLabel.setForeground(Color.decode("#3f3f46"));
        lookupLabel.setBounds(formX, formY + 145, formWidth, 20);
        forgotPanel.add(lookupLabel);

        forgetNameInput = new PlaceholderField("email@example.com");
        forgetNameInput.setFont(new Font("Inter", Font.PLAIN, 16));
        forgetNameInput.setBounds(formX, formY + 170, formWidth, 50);
        forgetNameInput.setBorder(inputBorder());
        forgotPanel.add(forgetNameInput);

        JLabel passLabel = new JLabel("New Password");
        passLabel.setFont(new Font("Inter", Font.BOLD, 14));
        passLabel.setForeground(Color.decode("#3f3f46"));
        passLabel.setBounds(formX, formY + 235, formWidth, 20);
        forgotPanel.add(passLabel);

        forgetPassInput = new RoundedPasswordField("Enter your new password");
        forgetPassInput.setBackground(Color.WHITE);
        forgetPassInput.setBounds(formX, formY + 260, formWidth, 50);
        forgotPanel.add(forgetPassInput);

        forgotBtn = new RoundedButton("Reset Password");
        forgotBtn.setBackground(Color.decode("#1A5DE3"));
        forgotBtn.setForeground(Color.WHITE);
        forgotBtn.setFont(new Font("Inter", Font.BOLD, 18));
        forgotBtn.setBounds(formX, formY + 340, formWidth, 55);
        forgotBtn.setBorderPainted(false);
        forgotBtn.setOpaque(false);
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setFocusable(false);
        forgotBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotBtn.addActionListener(this);
        forgotPanel.add(forgotBtn);


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
            centerWrapper.remove(invinciblePanel);
            Register();

        } else if (e.getSource() == hiringBtn) {
            accountType = "Employer";
            centerWrapper.remove(invinciblePanel);
            Register();

        } else if (e.getSource() == signInLinkBtn) {
            accountType = "";
            centerWrapper.remove(invinciblePanel);
            Login();

        } else if (e.getSource() == closeLoginBtn) {
            goHome(loginPanel);

        } else if (e.getSource() == goToRegisterBtn) {
            if (accountType.isEmpty()) {
                goHome(loginPanel);
                JOptionPane.showMessageDialog(
                        this,
                        "Please choose an account type first to create an account.",
                        "Create Account",
                        JOptionPane.INFORMATION_MESSAGE
                );
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

        } else if (e.getSource() == termsLinkBtn) {
            showTermsOfServiceDialog();

        } else if (e.getSource() == privacyPolicyLinkBtn) {
            showPrivacyPolicyDialog();

        } else if (e.getSource() == closeForgotBtn) {
            goHome(forgotPanel);

        } else if (e.getSource() == backToLoginFromForgotBtn) {
            centerWrapper.remove(forgotPanel);
            Login();

        } else if (e.getSource() == forgotBtn) {
            String email = forgetNameInput.getText().trim();
            String newPassword =
                    new String(forgetPassInput.getPassword()).trim();

            if (email.isEmpty() || newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please fill out both the email and new password fields.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (Database.resetPassword(email, newPassword)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Your account password has been successfully updated!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                forgetNameInput.setText("");
                forgetPassInput.setText("");

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No profile account found with that email address.",
                        "Reset Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }



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