package models;

import java.util.ArrayList;
import java.util.List;

// all info for job posts
public class Job {

    private int id;
    private String employerEmail;
    private String title;
    private String category;
    private String jobType;
    private String workplaceType;
    private int salaryMin;
    private int salaryMax;
    private String description;
    private List<String> skills = new ArrayList<>();
    private boolean accommodationsAvailable;
    private List<String> accommodations = new ArrayList<>();
    private List<String> applicantTypes = new ArrayList<>();
    private boolean experienceRequired;
    private String experienceLength = "";
    private String location;
    private String city;
    private String logoPath = "";
    private String status = "Active";
    private boolean favorite;
    private String createdAt = "";

    public Job() {
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getEmployerEmail() {
        return employerEmail;
    }
    public void setEmployerEmail(String employerEmail) {
        this.employerEmail = employerEmail;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getJobType() {
        return jobType;
    }
    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getWorkplaceType() {
        return workplaceType;
    }
    public void setWorkplaceType(String workplaceType) {
        this.workplaceType = workplaceType;
    }

    public int getSalaryMin() {
        return salaryMin;
    }
    public void setSalaryMin(int salaryMin) {
        this.salaryMin = salaryMin;
    }
    public int getSalaryMax() {
        return salaryMax;
    }
    public void setSalaryMax(int salaryMax) {
        this.salaryMax = salaryMax;
    }

    public int getSalary() {
        return salaryMin > 0 ? salaryMin : salaryMax;
    }

    public void setSalary(int salary) {
        this.salaryMin = salary;
        this.salaryMax = salary;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSkills() {
        return skills;
    }
    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public boolean isAccommodationsAvailable() {
        return accommodationsAvailable;
    }
    public void setAccommodationsAvailable(boolean accommodationsAvailable) {
        this.accommodationsAvailable = accommodationsAvailable;
    }

    public List<String> getAccommodations() {
        return accommodations;
    }
    public void setAccommodations(List<String> accommodations) {
        this.accommodations = accommodations;
    }

    public List<String> getApplicantTypes() {
        return applicantTypes;
    }
    public void setApplicantTypes(List<String> applicantTypes) {
        this.applicantTypes = applicantTypes;
    }

    public boolean isExperienceRequired() {
        return experienceRequired;
    }
    public void setExperienceRequired(boolean experienceRequired) {
        this.experienceRequired = experienceRequired;
    }

    public String getExperienceLength() {
        return experienceLength;
    }
    public void setExperienceLength(String experienceLength) {
        this.experienceLength = experienceLength;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getLogoPath() {
        return logoPath;
    }
    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFavorite() {
        return favorite;
    }
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getSkillsText() {
        return String.join(", ", skills);
    }

    public String getSalaryText() {
        return "₱" + String.format("%,d", getSalary());
    }
}