package dev.alabbad.models;

import java.io.InputStream;

public class User {
    protected String username;
    protected String firstName;
    protected String lastName;
    protected InputStream profileImg;

    public User(String username, String fname, String lname) {
        this.username = username;
        this.firstName = fname;
        this.lastName = lname;
    }

    public User(String username, String fname, String lname, InputStream profileImg) {
        this.username = username;
        this.firstName = fname;
        this.lastName = lname;
        this.profileImg = profileImg;
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

    public InputStream getProfileImg() {
        return this.profileImg;
    }

    public void setProfileImg(InputStream value) {
        this.profileImg = value;
    }

    public void displayDetails() {
        System.out.println("Username: " + this.username);
        System.out.println("First Name: " + this.firstName);
        System.out.println("Last Name: " + this.lastName);
    }
}
