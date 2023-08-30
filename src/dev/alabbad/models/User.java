package dev.alabbad.models;

public class User extends UserCreds {
    private String fname;
    private String lname;

    public User(String username, String fname, String lname) {
        super(username, null);
        this.username = username;
        this.fname = fname;
        this.lname = lname;
    }

    public User(String username, String fname, String lname, String password) {
        super(username, password);
        this.username = username;
        this.fname = fname;
        this.lname = lname;
    }

    public String getFirstName() {
        return this.fname;
    }

    public String getLastName() {
        return this.lname;
    }

    public void displayDetails() {
        System.out.println("Username: " + this.username);
        System.out.println("First Name: " + this.fname);
        System.out.println("Last Name: " + this.lname);
    }
}
