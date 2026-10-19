package models;

import database.Database;
import users.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

// laman ng profile page ng job seeker.
public class SeekerProfile {

    private String email = "";
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
    private String assessmentDate = "";

    // placeholder, will change after assessment
    private String profileType = "Regular Applicant";

    private SeekerProfile() {
    }

    // kukunin ung profile ng user galing sa jm_applicant_profiles db
    public static SeekerProfile forUser(User user) {
        SeekerProfile jProfile = new SeekerProfile();
        jProfile.email = user.getEmail().toLowerCase();

        try {
            String[] dataRow = database.ApplicantRepository.loadApplicantProfile(jProfile.email);

            //head
            if (dataRow != null) {
                jProfile.headline = dataRow[0] == null ? "" : dataRow[0];
                jProfile.location = dataRow[1] == null ? "" : dataRow[1];
                jProfile.skills = toList(dataRow[2]);

                //education
                String rawEdu = dataRow[3];
                if (rawEdu != null && !rawEdu.isBlank()) {
                    String[] eduParts = rawEdu.split(";", -1);
                    if (eduParts.length >= 1) jProfile.school = eduParts[0];
                    if (eduParts.length >= 2) jProfile.degree = eduParts[1];
                    if (eduParts.length >= 3) jProfile.yearLevel = eduParts[2];
                    if (eduParts.length >= 4) jProfile.schoolPeriod = eduParts[3];
                }

                //work exp
                String rawExp = dataRow[4];
                if (rawExp != null && !rawExp.isBlank()) {
                    String[] expParts = rawExp.split(";", -1);
                    if (expParts.length >= 1) jProfile.role = expParts[0];
                    if (expParts.length >= 2) jProfile.company = expParts[1];
                    if (expParts.length >= 3) jProfile.workPeriod = expParts[2];
                    if (expParts.length >= 4 && !expParts[3].isBlank()) {
                        jProfile.workDetails = Arrays.asList(expParts[3].split("\n"));
                    }
                }

                //abt
                jProfile.about = dataRow[5] == null ? "" : dataRow[5];

                jProfile.certifications = toList(Database.getProfileDetails(jProfile.email).get("certifications"));
                jProfile.preferredRoles = Database.getProfileDetails(jProfile.email).getOrDefault("preferredRoles", "");
                jProfile.preferredIndustry = Database.getProfileDetails(jProfile.email).getOrDefault("preferredIndustry", "");
                jProfile.preferredArrangement = Database.getProfileDetails(jProfile.email).getOrDefault("preferredArrangement", "");
                jProfile.preferredEmploymentType = Database.getProfileDetails(jProfile.email).getOrDefault("preferredEmploymentType", "");
                jProfile.resumeName = Database.getProfileDetails(jProfile.email).getOrDefault("resumeName", "");
                jProfile.profileType = Database.getProfileDetails(jProfile.email).getOrDefault("profileType", "Regular Applicant");

                jProfile.assessmentDate = Database.getLatestAssessmentDate(jProfile.email);
                jProfile.assessmentTaken = !jProfile.assessmentDate.isEmpty() || Database.getProfileDetails(jProfile.email).getOrDefault("assessmentTaken", "").equals("1");

                return jProfile;
            }
        } catch (Exception e) {
            System.err.println("Error reading candidate structured records: " + e.getMessage());
        }
        return jProfile;
    }

    // sinasave ang lahat ng fields sa db
    public void save() {
        String combinedEdu = school + ";" + degree + ";" + yearLevel + ";" + schoolPeriod;
        String combinedExp = role + ";" + company + ";" + workPeriod + ";" + String.join("\n", workDetails);
        String skillsText = String.join("\n", skills);

        try {
            database.ApplicantRepository.saveApplicantProfile(email, headline, location, skillsText, combinedEdu, combinedExp, about);
        } catch (Exception e) {
            System.err.println("Error executing profile database save: " + e.getMessage());
        }

        java.util.Map<String, String> backupDetails = new java.util.LinkedHashMap<>();
        backupDetails.put("certifications", String.join("\n", certifications));
        backupDetails.put("preferredRoles", preferredRoles);
        backupDetails.put("preferredIndustry", preferredIndustry);
        backupDetails.put("preferredArrangement", preferredArrangement);
        backupDetails.put("preferredEmploymentType", preferredEmploymentType);
        backupDetails.put("resumeName", resumeName);
        backupDetails.put("profileType", profileType);
        backupDetails.put("assessmentTaken", assessmentTaken ? "1" : "0");
        Database.saveProfileDetails(email, backupDetails);
    }

    public void applyAssessment(Map<String, String> answers) {
        this.assessmentTaken = true;
        this.profileType = value(answers, "Profile Type");

        //organize skills
        List<?> parsedSkills = toList(clean(value(answers, "skills")).replace(", ", "\n"));
        // avoids duplications. SKills exist -> wala, else none = add
        for (int i = 0; i < parsedSkills.size(); i++) {
            String skill = (String) parsedSkills.get(i);
            addIfMissing(skills, skill);
        }
        addIfMissing(skills, clean(value(answers, "strongestSkill")));

        //organize certificates
        for (String certificate : value(answers, "qualifications").split("[,;\\n]")) {
            addIfMissing(certifications, certificate.trim());
        }

        String roles = value(answers, "jobRoles");
        String position = value(answers, "desiredPosition");
        if (!position.isEmpty() && !position.equals(roles)) {
            roles = roles.isEmpty() ? position : roles + ", " + position;
        }

        this.preferredRoles = clean(roles);
        this.preferredIndustry = clean(value(answers, "industries"));
        this.preferredArrangement = clean(value(answers, "workSetup"));
        this.preferredEmploymentType = clean(value(answers, "employmentType"));
        this.location = clean(value(answers, "location"));

        if (this.profileType.equals("Student")) {
            this.degree = clean(value(answers, "educationLevel"));
            this.school = "Not Specified (Update in Profile)";
        }

        String recentJob = clean(value(answers, "recentJob"));
        if (this.role.isBlank() && !recentJob.isEmpty() && !recentJob.equals("No previous job")) {
            this.role = recentJob;
            this.workPeriod = clean(value(answers, "experience"));
            this.company = "Not Specified";
        }

        if (this.resumeName.isBlank()) {
            this.resumeName = value(answers, "Resume File");
        }

        // base auto for about
        if (this.about.isBlank()) {
            this.about = "I am a motivated individual looking for " + this.preferredEmploymentType.toLowerCase()
                    + " roles in the " + this.preferredIndustry + " field. My strongest core skill is "
                    + clean(value(answers, "strongestSkill")) + ", and I prefer working in a "
                    + this.preferredArrangement.toLowerCase() + " environment.";
        }

        if (this.headline.isBlank()) {
            String type = value(answers, "Profile Type");
            if (type.startsWith("PWD")) {
                type = "PWD";
            }
            String industry = this.preferredIndustry.contains(",") ? this.preferredIndustry.split(",")[0].trim() : this.preferredIndustry.trim();
            this.headline = industry.isEmpty() ? type : type + " · " + industry;
        }
    }

    // reader method para sa ProfilePage
    public String get(String key) {
        if (key.equals("profileType")) return profileType;
        if (key.equals("assessmentTaken")) return assessmentTaken ? "1" : "0";
        //sub resume
        Map<String, String> dbMeta = Database.getProfileDetails(email);
        return dbMeta.getOrDefault(key, "");
    }

    // writer method
    public void set(String key, String text) {
        if (key.equals("profileType")) {
            this.profileType = text;
        } else {
            java.util.Map<String, String> customMap = new java.util.LinkedHashMap<>();
            customMap.put(key, text);
            Database.saveProfileDetails(email, customMap);
        }
    }

    private String clean(String text) {
        List<String> items = new ArrayList<>();
        for (String part : text.split(",")) {
            String item = part.trim();
            if (!item.isEmpty() && !item.equals("Other")) {
                items.add(item);
            }
        }
        return String.join(", ", items);
    }

    private String value(Map<String, String> answers, String key) {
        String text = answers.get(key);
        return text == null ? "" : text.trim();
    }

    private void addIfMissing(List list, String item) {
        if (!item.isBlank() && !list.contains(item)) {
            list.add(item);
        }
    }

    private static List toList(String text) {
        List list = new ArrayList<>();
        if (text != null) {
            for (String line : text.split("\n")) {
                if (!line.trim().isEmpty()) {
                    list.add(line.trim());
                }
            }
        }
        return list;
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
    public String getAssessmentDateText() {
        if (assessmentDate == null || assessmentDate.length() < 10) {
            return "";
        }
        try {
            LocalDate date = LocalDate.parse(assessmentDate.substring(0, 10));
            return date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        } catch (Exception e) {
            return "";
        }
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

    public boolean isAssessmentTaken() { return assessmentTaken; }

} //end