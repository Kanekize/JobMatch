package pages;

import users.User;
import components.RoundedLineBorder;

import javax.swing.*;
import java.awt.*;

// induestries page
public class JobSeekerRecommendationsPage extends Page {

    private static final String[][] INDUSTRIES = {
            {"Software Development", "Programs and apps that run on computers and phones.",
                    "Java, SQL, OOP, Web Development", "Software Developer, IT Support Specialist, System Analyst, DevOps Engineer"},
            {"Artificial Intelligence", "Smart programs that learn from data.",
                    "Neural Networks, Math, Data, Machine Learning", "AI Engineer, Solutions Architect, Agents Architect, ML Ops"},
            {"Data & Analytics", "Predict future trends and build automated AI systems.",
                    "Statistics, Analysis, Excel, Critical Thinking", "Data Analyst, Data Engineer, Business Analyst, Business Intelligence"},
            {"Telecommunications", "Connect, maintain, and optimize communication networks and technologies.",
                    "Network, Support, System, Troubleshooting", "Network Engineer, Telecommunications Technician, Network Administrator, Field Service Technician"},
            {"Cybersecurity", "Protect systems and data from attacks.",
                    "Network Security, Encryption, Risk Analysis, Ethical Hacking", "Security Analyst, Penetration Tester, Security Engineer, SOC Analyst"},
            {"Healthcare", "Care for patients and support medical work.",
                    "Patient Care, First Aid, Communication, Record Keeping", "Nurse, Medical Technologist, Caregiver, Health Aide"},
            {"Finance & Accounting", "Handle money records and reports of companies.",
                    "Bookkeeping, Excel, Analysis, Auditing", "Accountant, Bookkeeper, Financial Analyst, Auditor"},
            {"Marketing", "Promote products and reach customers.",
                    "Content Writing, SEO, Social Media, Creativity", "Marketing Assistant, Social Media Manager, SEO Specialist, Brand Associate"},
            {"Education", "Teach and train students or employees.",
                    "Teaching, Communication, Patience, Lesson Planning", "Teacher, Tutor, Trainer, Teaching Assistant"},
            {"Engineering", "Design and build structures, machines, and systems.",
                    "Math, Design, Problem Solving, CAD", "Civil Engineer, Mechanical Engineer, Electrical Engineer, Project Engineer"},
            {"Customer Service", "Help customers with their concerns.",
                    "Communication, Patience, CRM, Problem Solving", "Customer Service Representative, Call Center Agent, Support Specialist, Help Desk"},
            {"Human Resources", "Hire and take care of employees.",
                    "Communication, Recruitment, Organization, Onboarding", "HR Assistant, Recruiter, HR Officer, Training Coordinator"},
            {"Hospitality & Tourism", "Serve guests in hotels, restaurants, and travel.",
                    "Service, Communication, Flexibility, Customer Care", "Front Desk Officer, Housekeeping Staff, Tour Guide, Food Server"},
            {"Logistics & Supply Chain", "Move and track goods from place to place.",
                    "Inventory, Planning, Driving, Coordination", "Warehouse Staff, Delivery Driver, Supply Chain Analyst, Inventory Clerk"},
            {"Media & Design", "Create visuals, videos, and creative content.",
                    "Creativity, Photoshop, Video Editing, Layout", "Graphic Designer, Video Editor, Content Creator, Illustrator"},
            {"Construction & Skilled Trades", "Build and repair structures by hand.",
                    "Carpentry, Welding, Electrical, Safety", "Welder, Electrician, Carpenter, Mason"}
    };

    public JobSeekerRecommendationsPage(User user) {
        super(user);
        JButton updateBtn = new JButton("Update Assessment");
        updateBtn.setIcon(loadIcon("update-assessment.png", 16, 16));
        updateBtn.setIconTextGap(8);
        updateBtn.setBackground(Color.WHITE);
        updateBtn.setForeground(Color.decode("#2563eb"));
        updateBtn.setFont(new Font("Inter", Font.BOLD, 13));
        updateBtn.setBorder(new RoundedLineBorder(Color.decode("#e5e9f0"), 12, 1));
        updateBtn.setOpaque(true);
        updateBtn.setFocusable(false);
        updateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateBtn.setPreferredSize(new Dimension(170, 38));
        updateBtn.addActionListener(e -> getDashboard().onMenuClick("Assessment"));

        JPanel assessmentBanner = new JPanel(new BorderLayout(12, 0));
        assessmentBanner.setBackground(Color.decode("#e1efff"));
        assessmentBanner.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#e1efff"), 14, 1),
                BorderFactory.createEmptyBorder(9, 12, 9, 10)));
        JLabel assessmentIcon = new JLabel(loadIcon("basedonlatestassessment.png", 30, 30));
        assessmentIcon.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 4));
        assessmentBanner.add(assessmentIcon, BorderLayout.WEST);
        JPanel assessmentText = new JPanel();
        assessmentText.setOpaque(false);
        assessmentText.setLayout(new BoxLayout(assessmentText, BoxLayout.Y_AXIS));
        JLabel assessmentTitle = new JLabel("Based on latest assessment");
        assessmentTitle.setFont(new Font("Inter", Font.BOLD, 12));
        assessmentTitle.setForeground(Color.BLACK);
        JLabel assessmentDate = new JLabel("Completed on Oct 01, 2026");
        assessmentDate.setFont(new Font("Inter", Font.PLAIN, 12));
        assessmentDate.setForeground(Color.decode("#374151"));
        assessmentText.add(assessmentTitle);
        assessmentText.add(Box.createVerticalStrut(2));
        assessmentText.add(assessmentDate);
        assessmentBanner.add(assessmentText, BorderLayout.CENTER);
        updateBtn.setFont(new Font("Inter", Font.PLAIN, 11));
        updateBtn.setPreferredSize(new Dimension(158, 38));
        assessmentBanner.add(updateBtn, BorderLayout.EAST);
        assessmentBanner.setPreferredSize(new Dimension(410, 62));

        addBlock(makeHeading("Recommended Industries",
                "These industries align with your current skills, interests and career preferences.", assessmentBanner), 20);

        // seperator 2 column
        JPanel grid = new JPanel(new GridLayout(0, 2, 20, 20));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String[] industry : INDUSTRIES) {
            grid.add(makeIndustryCard(industry));
        }
        addBlock(grid, 0);
    }

    private JPanel makeIndustryCard(String[] industry) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#dfe7f1"), 10, 1),
                BorderFactory.createEmptyBorder(16, 18, 15, 18)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setPreferredSize(new Dimension(320, 345));
        card.setMinimumSize(new Dimension(320, 345));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 345));

        // Header row with a temporary icon placeholder and alignment badge.
        JPanel titleRow = new JPanel(new BorderLayout(12, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleRow.setPreferredSize(new Dimension(0, 44));
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        JPanel iconTile = new JPanel(new GridBagLayout());
        iconTile.setBackground(industryTileBackground(industry[0]));
        iconTile.setBorder(new RoundedLineBorder(industryTileBackground(industry[0]), 8, 1));
        iconTile.setPreferredSize(new Dimension(44, 44));
        JLabel iconPlaceholder = new JLabel(industrySymbol(industry[0]));
        iconPlaceholder.setFont(new Font("Inter", Font.BOLD, 18));
        iconPlaceholder.setForeground(industrySymbolColor(industry[0]));
        iconTile.add(iconPlaceholder);
        titleRow.add(iconTile, BorderLayout.WEST);

        JLabel nameLabel = new JLabel(industry[0]);
        nameLabel.setFont(new Font("Inter", Font.BOLD, 16));
        nameLabel.setForeground(Color.decode("#0d0d0d"));
        JLabel description = new JLabel("<html><div style='width:300px'>" + industry[1] + "</div></html>");
        description.setFont(new Font("Inter", Font.PLAIN, 13));
        description.setForeground(Color.decode("#374151"));
        JPanel titleWrap = new JPanel();
        titleWrap.setOpaque(false);
        titleWrap.setLayout(new BoxLayout(titleWrap, BoxLayout.Y_AXIS));
        titleWrap.add(nameLabel);
        titleWrap.add(Box.createVerticalStrut(4));
        titleWrap.add(description);
        titleRow.add(titleWrap, BorderLayout.CENTER);

        boolean strong = industry[0].equals("Software Development");
        JLabel alignment = new JLabel((strong ? "Strong Alignment" : "Good Alignment"));
        alignment.setFont(new Font("Inter", Font.PLAIN, 11));
        alignment.setForeground(Color.decode(strong ? "#16a34a" : "#2563eb"));
        alignment.setOpaque(true);
        alignment.setBackground(Color.decode(strong ? "#e7f7ef" : "#e1efff"));
        alignment.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode(strong ? "#e7f7ef" : "#e1efff"), 8, 1),
                BorderFactory.createEmptyBorder(4, 9, 4, 9)));
        alignment.setPreferredSize(new Dimension(115, 24));
        alignment.setMinimumSize(new Dimension(115, 24));
        alignment.setMaximumSize(new Dimension(115, 24));
        titleRow.add(alignment, BorderLayout.EAST);
        card.add(titleRow);
        card.add(Box.createVerticalStrut(3));
        card.add(separator());
        card.add(Box.createVerticalStrut(4));

        JLabel strengthsTitle = smallHeading("Relevant Strengths");
        strengthsTitle.setFont(new Font("Inter", Font.PLAIN, 13));
        strengthsTitle.setForeground(Color.decode("#374151"));
        card.add(strengthsTitle);
        card.add(Box.createVerticalStrut(5));
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
        chips.setOpaque(false);
        chips.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String strength : industry[2].split(",\\s*")) {
            JLabel chip = new JLabel(strength.trim());
            chip.setFont(new Font("Inter", Font.BOLD, 11));
            chip.setForeground(Color.decode("#2563eb"));
            chip.setOpaque(true);
            chip.setBackground(Color.decode("#e1efff"));
            chip.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
            chips.add(chip);
        }
        card.add(chips);
        card.add(Box.createVerticalStrut(8));
        card.add(separator());
        card.add(Box.createVerticalStrut(6));

        JLabel opportunitiesTitle = smallHeading("Common Opportunities");
        opportunitiesTitle.setFont(new Font("Inter", Font.PLAIN, 13));
        opportunitiesTitle.setForeground(Color.decode("#374151"));
        card.add(opportunitiesTitle);
        card.add(Box.createVerticalStrut(4));
        JPanel opportunities = new JPanel();
        opportunities.setOpaque(false);
        opportunities.setLayout(new BoxLayout(opportunities, BoxLayout.Y_AXIS));
        opportunities.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] roles = industry[3].split(",\\s*");
        for (int i = 0; i < Math.min(4, roles.length); i++) {
            JLabel role = new JLabel("•  " + roles[i].trim());
            role.setFont(new Font("Inter", Font.PLAIN, 12));
            role.setForeground(Color.decode("#374151"));
            role.setAlignmentX(Component.LEFT_ALIGNMENT);
            opportunities.add(role);
            if (i < Math.min(4, roles.length) - 1) opportunities.add(Box.createVerticalStrut(2));
        }
        card.add(opportunities);
        card.add(Box.createVerticalStrut(8));

        JButton exploreBtn = new JButton("Explore Jobs");
        exploreBtn.setBackground(Color.decode("#2563eb"));
        exploreBtn.setForeground(Color.WHITE);
        exploreBtn.setFont(new Font("Inter", Font.BOLD, 14));
        exploreBtn.setBorder(new RoundedLineBorder(Color.decode("#2563eb"), 10, 1));
        exploreBtn.setOpaque(true);
        exploreBtn.setFocusable(false);
        exploreBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exploreBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        exploreBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        exploreBtn.setPreferredSize(new Dimension(300, 36));
        exploreBtn.addActionListener(e ->
                getDashboard().showPage(new JobSeekerJobsPage(getUser(), industry[0])));
        card.add(exploreBtn);
        return card;
    }

    private JComponent separator() {
        JPanel line = new JPanel();
        line.setBackground(Color.decode("#e5edf5"));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        line.setPreferredSize(new Dimension(1, 1));
        line.setMinimumSize(new Dimension(1, 1));
        line.setAlignmentX(Component.LEFT_ALIGNMENT);
        return line;
    }

    private ImageIcon loadIcon(String fileName, int width, int height) {
        java.io.File file = new java.io.File("src/images/" + fileName);
        if (!file.exists()) file = new java.io.File("JobMatch/src/images/" + fileName);
        if (!file.exists()) return null;
        Image image = new ImageIcon(file.getPath()).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    private JLabel smallHeading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Inter", Font.BOLD, 12));
        label.setForeground(Color.decode("#0d0d0d"));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private Color industryTileBackground(String industry) {
        switch (industry) {
            case "Software Development":
            case "Artificial Intelligence":
                return Color.decode("#e1efff");
            case "Data & Analytics":
                return Color.decode("#e7f7ef");
            case "Telecommunications":
            case "Cybersecurity":
                return Color.decode("#f2f4f7");
            default:
                return Color.decode("#f4f7fb");
        }
    }

    private Color industrySymbolColor(String industry) {
        switch (industry) {
            case "Data & Analytics":
                return Color.decode("#16a34a");
            case "Telecommunications":
                return Color.decode("#4b5563");
            case "Cybersecurity":
                return Color.decode("#374151");
            default:
                return Color.decode("#2563eb");
        }
    }

    private String industrySymbol(String industry) {
        return "</>";
    }
}