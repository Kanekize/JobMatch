package pages;

import components.RoundedLineBorder;
import database.Database;
import users.User;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class CompanyProfilePage extends Page {

    private String companyName;
    private String companyLogoPath;
    private String companyAbout;

    public CompanyProfilePage(User user) {
        super(user);
        build();
    }

    private void build() {
        content.removeAll();
        loadProfile();

        addBlock(makeHeading("Company Profile",
                "Manage your company branding and the logo used on new job posts.", null), 15);
        addBlock(makeOverviewCard(), 15);
        addBlock(makeBrandingCard(), 0);

        content.revalidate();
        content.repaint();
    }

    private void loadProfile() {
        companyName = Database.getEmployerCompanyName(getUser().getEmail());
        companyLogoPath = Database.getEmployerCompanyLogoPath(getUser().getEmail());
        companyAbout = Database.getEmployerCompanyAbout(getUser().getEmail());
    }

    private JPanel makeOverviewCard() {
        JPanel card = new JPanel(new BorderLayout(20, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#d7dce3"), 12, 1),
                BorderFactory.createEmptyBorder(20, 22, 20, 22)));

        card.add(makeLogoPreview(110), BorderLayout.WEST);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JLabel title = new JLabel(companyName == null || companyName.isBlank() ? "Your Company" : companyName);
        title.setFont(new Font("Inter", Font.BOLD, 28));
        title.setForeground(Color.decode("#0d0d0d"));

        JLabel email = new JLabel(getUser().getEmail());
        email.setFont(new Font("Inter", Font.PLAIN, 13));
        email.setForeground(Color.decode("#6b7280"));

        JTextArea about = new JTextArea(companyAbout == null || companyAbout.isBlank()
                ? "Tell job seekers what makes your company a great place to work."
                : companyAbout);
        about.setFont(new Font("Inter", Font.PLAIN, 14));
        about.setForeground(Color.decode("#4b5563"));
        about.setLineWrap(true);
        about.setWrapStyleWord(true);
        about.setEditable(false);
        about.setFocusable(false);
        about.setOpaque(false);
        about.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        about.setAlignmentX(Component.LEFT_ALIGNMENT);

        center.add(Box.createVerticalGlue());
        center.add(title);
        center.add(Box.createVerticalStrut(4));
        center.add(email);
        center.add(Box.createVerticalStrut(8));
        center.add(about);
        center.add(Box.createVerticalGlue());
        card.add(center, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JButton editBtn = makeBlueButton("Edit Details");
        editBtn.addActionListener(e -> editDetails());
        JButton logoBtn = makeWhiteButton(companyLogoPath == null || companyLogoPath.isBlank() ? "Upload Logo" : "Change Logo");
        logoBtn.addActionListener(e -> changeLogo());

        actions.add(editBtn);
        actions.add(Box.createVerticalStrut(10));
        actions.add(logoBtn);
        card.add(actions, BorderLayout.EAST);

        return card;
    }

    private JPanel makeBrandingCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#d7dce3"), 12, 1),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));

        JLabel title = new JLabel("Branding");
        title.setFont(new Font("Inter", Font.BOLD, 15));
        title.setForeground(Color.decode("#0d0d0d"));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(8));

        JLabel note = new JLabel("This logo is used automatically on new job posts.");
        note.setFont(new Font("Inter", Font.PLAIN, 13));
        note.setForeground(Color.decode("#6b7280"));
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(note);

        card.add(Box.createVerticalStrut(12));
        card.add(makeLogoPreview(84));
        return card;
    }

    private JPanel makeLogoPreview(int size) {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel box = new JPanel(new BorderLayout());
        box.setPreferredSize(new Dimension(size, size));
        box.setMaximumSize(new Dimension(size, size));
        box.setBackground(Color.decode("#f1f5fd"));
        box.setBorder(BorderFactory.createLineBorder(Color.decode("#d7dce3"), 1));

        if (companyLogoPath != null && !companyLogoPath.isBlank() && new File(companyLogoPath).exists()) {
            ImageIcon icon = new ImageIcon(companyLogoPath);
            Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            box.add(new JLabel(new ImageIcon(scaled)), BorderLayout.CENTER);
        } else {
            String labelText = companyName == null || companyName.isBlank()
                    ? "C"
                    : companyName.substring(0, 1).toUpperCase();
            JLabel label = new JLabel(labelText, SwingConstants.CENTER);
            label.setFont(new Font("Inter", Font.BOLD, size / 2));
            label.setForeground(Color.decode("#12376b"));
            box.add(label, BorderLayout.CENTER);
        }

        wrap.add(box);
        return wrap;
    }

    private void editDetails() {
        JTextField companyField = new JTextField(companyName == null ? "" : companyName, 28);
        JTextArea aboutField = new JTextArea(companyAbout == null ? "" : companyAbout, 6, 28);
        aboutField.setLineWrap(true);
        aboutField.setWrapStyleWord(true);

        if (showForm("Edit Company Profile", "Company Name", companyField,
                "About the Company", new JScrollPane(aboutField))) {
            String newName = companyField.getText().trim();
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Company name cannot be empty.", "Missing answer", JOptionPane.WARNING_MESSAGE);
                return;
            }
            companyName = newName;
            companyAbout = aboutField.getText().trim();
            Database.updateEmployerProfile(getUser().getEmail(), companyName, companyLogoPath, companyAbout);
            build();
        }
    }

    private void changeLogo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose company logo");
        chooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            companyLogoPath = file.getAbsolutePath();
            Database.updateEmployerProfile(getUser().getEmail(), companyName, companyLogoPath, companyAbout);
            build();
        }
    }

    private JButton makeBlueButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.decode("#2563eb"));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Inter", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(130, 38));
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeWhiteButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Inter", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 38));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#d7dce3"), 10, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return btn;
    }

    private boolean showForm(String title, Object... labelsAndFields) {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        for (int i = 0; i < labelsAndFields.length; i += 2) {
            JLabel label = new JLabel((String) labelsAndFields[i]);
            label.setFont(new Font("Inter", Font.BOLD, 12));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            JComponent field = (JComponent) labelsAndFields[i + 1];
            field.setAlignmentX(Component.LEFT_ALIGNMENT);
            form.add(label);
            form.add(Box.createVerticalStrut(3));
            form.add(field);
            form.add(Box.createVerticalStrut(10));
        }

        return JOptionPane.showConfirmDialog(this, form, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }
}
