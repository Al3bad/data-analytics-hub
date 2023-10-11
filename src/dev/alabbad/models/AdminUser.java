package dev.alabbad.models;

import java.io.InputStream;

/**
 * Definition of admin user object
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class AdminUser extends User {
    public AdminUser(String username, String fname, String lname) {
        super(username, fname, lname);
    }

    public AdminUser(String username, String password, String fname, String lname) {
        super(username, password, fname, lname);
    }

    public AdminUser(String username, String fname, String lname, InputStream profileImg) {
        super(username, fname, lname, profileImg);
    }

}
