package users;

public abstract class User {
    private String firstName;
    private String lastName;
    private String email;

    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return firstName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public abstract String getRole();
    public abstract void openDashboard();
}