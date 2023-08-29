public class User {
    private String username;
    private String password;
    private String fname;
    private String lname;

    public User(String username, String fname, String lname) {
        this.username = username;
        this.fname = fname;
        this.lname = lname;
    }

    public User(String username, String fname, String lname, String password) {
        this.username = username;
        this.fname = fname;
        this.lname = lname;
        this.password = password;
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

    public String getPassword() {
        return this.password;
    }

    public void displayDetails() {
        System.out.println("Username: " + this.username);
        System.out.println("First Name: " + this.fname);
        System.out.println("Last Name: " + this.lname);
    }
}
