package dev.alabbad.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

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
                byte[] imgBytes = inputStreamToBytes(profileImg);
                this.base64ProfileImg = Base64.getEncoder().encodeToString(imgBytes);
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
        return this.base64ProfileImg == null ? null
                : new ByteArrayInputStream(Base64.getDecoder().decode(this.base64ProfileImg));
    }

    /**
     * Disaply user's basic information
     */
    public void displayDetails() {
        System.out.println("Username: " + this.username);
        System.out.println("First Name: " + this.firstName);
        System.out.println("Last Name: " + this.lastName);
    }

    /**
     * Convert input stream image to bytes
     *
     * @param image
     * @return image bytes
     * @throws IOException
     */
    private static byte[] inputStreamToBytes(InputStream image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = image.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return outputStream.toByteArray();
    }
}
