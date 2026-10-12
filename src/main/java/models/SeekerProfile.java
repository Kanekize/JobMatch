package models;

import users.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// laman ng profile page ng job seeker.
// TODO: ilipat sa database (jm_applicant_profiles) pag ready na. for now naka-store lang sa memory per email
// para ma-edit ung profile habang nakabukas ung app.
public class SeekerProfile {

    private static final Map<String, SeekerProfile> CACHE = new HashMap<>();

    private String headline = "";
    private String location = "";
    private String about = "";
    private List<String> skills = new ArrayList<>();

    // education (isa lang muna)
    private String school = "";
    private String degree = "";
    private String yearLevel = "";
    private String schoolPeriod = "";

    // work experience (isa lang muna)
    private String role = "";
    private String company = "";
    private String workPeriod = "";
    private List<String> workDetails = new ArrayList<>();

    private List<String> certifications = new ArrayList<>();

    // job preference
    private String preferredRoles = "";
    private String preferredIndustry = "";
    private String preferredArrangement = "";
    private String preferredEmploymentType = "";

    private String resumeName = "";
    private boolean assessmentTaken = false;

    // kukunin ung profile ng user, gagawa ng default kung wala pa
    public static SeekerProfile forUser(User user) {
        return CACHE.computeIfAbsent(user.getEmail(), email -> createSample());
    }

    // sample data from the design. palitan na lang pag may database na
    private static SeekerProfile createSample() {
        SeekerProfile p = new SeekerProfile();
        p.headline = "Computer Science Student · Software Development";
        p.location = "Quezon City";
        p.about = "I am a Computer Science student with a strong interest in software development and "
                + "technology. I enjoy solving problems, learning new skills, and working projects that "
                + "allow me to be creative and analytical. I am looking for opportunities where I can "
                + "apply my skills, gain real-world experience, and grow professionally.";
        p.skills.addAll(List.of("Java", "SQL", "HTML", "Problem Solving", "Communication", "Teamwork",
                "Microsoft Excel"));

        p.school = "Technological Institute of the Philippines";
        p.degree = "Bachelor of Science in Computer Science";
        p.yearLevel = "2nd Year (Currently Enrolled)";
        p.schoolPeriod = "October 2026 (Present)";

        p.role = "IT Support Intern";
        p.company = "WG Inc.";
        p.workPeriod = "July 2026 - October 2026";
        p.workDetails.addAll(List.of("Assisted in troubleshooting and hardware issues",
                "Helped maintain computer systems", "Provided technical support to staff and users"));

        p.preferredRoles = "Software developer, Web developer, It Support";
        p.preferredIndustry = "Computer Science";
        p.preferredArrangement = "Remote (Preferred), Hybrid (Acceptable)";
        p.preferredEmploymentType = "Internship, Part-Time";
        return p;
    }

    // profile completion, may bigat bawat section (total = 100)
    public int getCompletion() {
        int score = 0;
        score += headline.isBlank() ? 0 : 10;
        score += location.isBlank() ? 0 : 5;
        score += about.isBlank() ? 0 : 15;
        score += skills.isEmpty() ? 0 : 15;
        score += school.isBlank() ? 0 : 15;
        score += role.isBlank() ? 0 : 15;
        score += preferredRoles.isBlank() ? 0 : 7;
        score += certifications.isEmpty() ? 0 : 6;
        score += resumeName.isBlank() ? 0 : 7;
        score += assessmentTaken ? 5 : 0;
        return score;
    }

    public boolean hasEducation() {
        return !school.isBlank();
    }
    public boolean hasExperience() {
        return !role.isBlank();
    }

    public String getHeadline() {
        return headline;
    }
    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getAbout() {
        return about;
    }
    public void setAbout(String about) {
        this.about = about;
    }

    public List<String> getSkills() {
        return skills;
    }

    public String getSchool() {
        return school;
    }
    public void setSchool(String school) {
        this.school = school;
    }

    public String getDegree() {
        return degree;
    }
    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getYearLevel() {
        return yearLevel;
    }
    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
    }

    public String getSchoolPeriod() {
        return schoolPeriod;
    }
    public void setSchoolPeriod(String schoolPeriod) {
        this.schoolPeriod = schoolPeriod;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }

    public String getWorkPeriod() {
        return workPeriod;
    }
    public void setWorkPeriod(String workPeriod) {
        this.workPeriod = workPeriod;
    }

    public List<String> getWorkDetails() {
        return workDetails;
    }

    public List<String> getCertifications() {
        return certifications;
    }

    public String getPreferredRoles() {
        return preferredRoles;
    }
    public void setPreferredRoles(String preferredRoles) {
        this.preferredRoles = preferredRoles;
    }

    public String getPreferredIndustry() {
        return preferredIndustry;
    }
    public void setPreferredIndustry(String preferredIndustry) {
        this.preferredIndustry = preferredIndustry;
    }

    public String getPreferredArrangement() {
        return preferredArrangement;
    }
    public void setPreferredArrangement(String preferredArrangement) {
        this.preferredArrangement = preferredArrangement;
    }

    public String getPreferredEmploymentType() {
        return preferredEmploymentType;
    }
    public void setPreferredEmploymentType(String preferredEmploymentType) {
        this.preferredEmploymentType = preferredEmploymentType;
    }

    public String getResumeName() {
        return resumeName;
    }
    public void setResumeName(String resumeName) {
        this.resumeName = resumeName;
    }

    public boolean isAssessmentTaken() {
        return assessmentTaken;
    }
}
