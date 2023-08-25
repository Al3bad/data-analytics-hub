public class User {
    private String username;
    private String fname;
    private String lname;

    public User(String username, String fname, String lname) {
        this.username = username;
        this.fname = fname;
        this.lname = lname;
    }

    public String getUsername() {
        return this.username;
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
