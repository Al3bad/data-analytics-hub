package dev.alabbad.models;

import java.io.InputStream;

public class AdminUser extends User {
    public AdminUser(String username, String fname, String lname) {
        super(username, fname, lname);
    }

    public AdminUser(String username, String fname, String lname, InputStream profileImg) {
        super(username, fname, lname, profileImg);
    }

}
