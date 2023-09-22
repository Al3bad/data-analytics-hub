package dev.alabbad.models;

import java.io.InputStream;

public class VIPUser extends User {
    public VIPUser(String username, String fname, String lname) {
        super(username, fname, lname);
    }

    public VIPUser(String username, String fname, String lname, InputStream profileImg) {
        super(username, fname, lname, profileImg);
    }
}
