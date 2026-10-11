package pages;

import users.User;

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
        updateBtn.setBackground(Color.decode("#2563eb"));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("Arial", Font.BOLD, 13));
        updateBtn.setBorderPainted(false);
        updateBtn.setOpaque(true);
        updateBtn.setFocusable(false);
        updateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateBtn.setPreferredSize(new Dimension(170, 38));
        updateBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "test"));

        addBlock(makeHeading("Recommended Industries",
                "These industries align with your current skills, interests and career preferences.",
                updateBtn), 15);

        // seperator 2 column
        JPanel grid = new JPanel(new GridLayout(0, 2, 15, 15));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String[] industry : INDUSTRIES) {
            grid.add(makeIndustryCard(industry));
        }
        addBlock(grid, 0);
    }

    private JPanel makeIndustryCard(String[] industry) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#e5e9f0"), 1),
                BorderFactory.createEmptyBorder(15, 18, 15, 18)));

        JLabel nameLabel = new JLabel(industry[0]);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 17));
        nameLabel.setForeground(Color.decode("#0d0d0d"));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(nameLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(EmployerJobsPage.makeWrapText(industry[1], 12, Color.decode("#6b7280")));
        card.add(Box.createVerticalStrut(8));

        card.add(smallHeading("Relevant Strengths"));
        card.add(EmployerJobsPage.makeWrapText(industry[2], 12, Color.decode("#2563eb")));
        card.add(Box.createVerticalStrut(8));

        card.add(smallHeading("Common Opportunities"));
        card.add(EmployerJobsPage.makeWrapText(industry[3], 12, Color.decode("#4b5563")));
        card.add(Box.createVerticalStrut(10));

        JButton exploreBtn = new JButton("Explore Jobs");
        exploreBtn.setBackground(Color.decode("#2563eb"));
        exploreBtn.setForeground(Color.WHITE);
        exploreBtn.setFont(new Font("Arial", Font.BOLD, 13));
        exploreBtn.setBorderPainted(false);
        exploreBtn.setOpaque(true);
        exploreBtn.setFocusable(false);
        exploreBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exploreBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        exploreBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        exploreBtn.addActionListener(e ->
                getDashboard().showPage(new JobSeekerJobsPage(getUser(), industry[0])));
        card.add(exploreBtn);

        return card;
    }

    private JLabel smallHeading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(Color.decode("#0d0d0d"));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}