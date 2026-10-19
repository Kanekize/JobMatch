package pages;

import components.SectionPanel;
import components.StatCard;
import database.Database;
import models.JobApplication;
import models.SeekerProfile;
import users.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JobSeekerHomePage extends Page {

    public JobSeekerHomePage(User user) {
        super(user);
        SeekerProfile profile = SeekerProfile.forUser(user);
        List<JobApplication> applications = Database.getApplicationsForSeeker(user.getEmail());

        int activeApplications = 0;
        for (JobApplication application : applications) {
            String status = application.getStatus();
            if (!"Rejected".equalsIgnoreCase(status) && !"Accepted".equalsIgnoreCase(status)) {
                activeApplications++;
            }
        }

        String assessmentStatus = profile.isAssessmentTaken() ? "Taken" : "Not Taken";

        addBlock(makeHeading(getGreeting() + ", " + user.getDisplayName(),
                "Here's what's happening with your JobMatch profile.", null), 15);

        JPanel counters = new JPanel(new GridLayout(1, 3, 15, 0));
        counters.setOpaque(false);

        StatCard profileCard = new StatCard("Profile Completion", profile.getCompletion() + "%", Color.decode("#0d0d0d"), "user-blue.png", Color.decode("#dbeafe"));
        profileCard.addActionListener(e -> getDashboard().showPage(new ProfilePage(getUser())));
        counters.add(profileCard);

        StatCard assessmentCard = new StatCard("Assessment", assessmentStatus, Color.decode("#16a34a"), "dashassessment.png", Color.decode("#dcfce7"));
        assessmentCard.addActionListener(e -> getDashboard().showPage(new AssessmentPage(getUser())));
        counters.add(assessmentCard);

        StatCard applicationsCard = new StatCard("Active Applications", String.valueOf(activeApplications), Color.decode("#2563eb"), "applicationlist.png", Color.decode("#dbeafe"));
        applicationsCard.addActionListener(e -> getDashboard().showPage(new ApplicationsPage(getUser())));
        counters.add(applicationsCard);
        addBlock(counters, 20);


        SectionPanel recommended = new SectionPanel("Recommended for You", "View all jobs >", 3, 72, false);
        recommended.setEmptyState("No recommendations yet.");
        recommended.getViewAllButton().addActionListener(e ->
                getDashboard().showPage(new JobSeekerJobsPage(getUser(), null)));
        addBlock(recommended, 20);

        SectionPanel interviews = new SectionPanel("Upcoming Interview", "View all interviews >", 1, 72, false);
        interviews.setEmptyState("No interviews yet.");
        interviews.getViewAllButton().addActionListener(e -> getDashboard().onMenuClick("Interviews"));
        addBlock(interviews, 20);

        SectionPanel industries = new SectionPanel("Recommended Industries", "View all industries >", 3, 72, true);
        industries.setEmptyState("No industries yet.");
        industries.getViewAllButton().addActionListener(e -> getDashboard().onMenuClick("Recommendations"));
        addBlock(industries, 0);
    }
}