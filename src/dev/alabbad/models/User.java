package dev.alabbad.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class User {
    protected String username;
    protected String firstName;
    protected String lastName;
    protected String base64ProfileImg;

    public User(String username, String fname, String lname) {
        this.username = username;
        this.firstName = fname;
        this.lastName = lname;
    }

    public User(String username, String fname, String lname, InputStream profileImg) {
        this.username = username;
        this.firstName = fname;
        this.lastName = lname;
        if (profileImg != null) {
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

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public ByteArrayInputStream getProfileImg() {
        return this.base64ProfileImg == null ? null
                        : new ByteArrayInputStream(Base64.getDecoder().decode(this.base64ProfileImg));
    }

    public void displayDetails() {
        System.out.println("Username: " + this.username);
        System.out.println("First Name: " + this.firstName);
        System.out.println("Last Name: " + this.lastName);
    }

    private static byte[] inputStreamToBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return outputStream.toByteArray();
    }
}
