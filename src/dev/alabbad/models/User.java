package dev.alabbad.models;

public class User {
    protected String username;
    protected String firstName;
    protected String lastName;

    public User(String username, String fname, String lname) {
        this.username = username;
        this.firstName = fname;
        this.lastName = lname;
    }

    public String getUsername() {
        return this.username;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void displayDetails() {
        System.out.println("Username: " + this.username);
        System.out.println("First Name: " + this.firstName);
        System.out.println("Last Name: " + this.lastName);
    }
}
