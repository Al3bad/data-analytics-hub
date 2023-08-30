package dev.alabbad.models;

public class UserCreds {
    protected String username;
    private String password;

    public UserCreds(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
