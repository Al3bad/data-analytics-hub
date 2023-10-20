package dev.alabbad.models;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import dev.alabbad.utils.Transformer;

/**
 * Definition of user object
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class User {
    protected String username;
    protected String password;
    protected String firstName;
    protected String lastName;
    protected String base64ProfileImg;

    public User(String username, String fname, String lname) {
        this.username = username;
        this.firstName = fname;
        this.lastName = lname;
    }

    public User(String username, String password, String fname, String lname) {
        this.username = username;
        this.password = password;
        this.firstName = fname;
        this.lastName = lname;
    }

    public User(String username, String fname, String lname, InputStream profileImg) {
        this.username = username;
        this.firstName = fname;
        this.lastName = lname;
        if (profileImg != null) {
            // convert th input stream image to base64
            try {
                byte[] imgBytes = Transformer.streamToBytes(profileImg);
                this.base64ProfileImg = Transformer.bytesToBase64(imgBytes);
            } catch (IOException e) {
                System.out.println("Faild to process profile image stream!");
            }
        }
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Convert the base64 image to byte array input stream
     *
     * @return
     */
    public ByteArrayInputStream getProfileImg() {
        return this.base64ProfileImg == null ? null : Transformer.base64ToByteArrayInputStream(this.base64ProfileImg);
    }
}
