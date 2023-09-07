package dev.alabbad.models;

/**
 * Definition of post object
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class Post {
    private int ID;
    private String content;
    private String author;
    private int likes;
    private int shares;
    private String dateTime;

    public Post() {
    }

    public Post(int ID, String content, String author, int likes, int shares, String dateTime) {
        this.ID = ID;
        this.content = content;
        this.author = author;
        this.likes = likes;
        this.shares = shares;
        this.dateTime = dateTime;
    }

    public int getID() {
        return this.ID;
    }

    public String getContent() {
        return this.content;
    }

    public String getAuthor() {
        return this.author;
    }

    public int getLikes() {
        return this.likes;
    }

    public int getShares() {
        return this.shares;
    }

    public String getDateTime() {
        return this.dateTime;
    }

    public void setID(int iD) {
        this.ID = iD;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Display all details of the posts instance
     */
    public void displayDetails() {
        System.out.printf("%-7s | %-16s | %-7s | %-7s | %-7s | %-11s\n", "ID", "date/time", "likes", "shares", "author",
                        "content");
        System.out.printf("%-7s | %-16s | %-7s | %-7s | %-7s | %-11s\n", "-".repeat(7), "-".repeat(16), "-".repeat(7),
                        "-".repeat(7), "-".repeat(7), "-".repeat(11));
        System.out.printf("%-7s | %-16s | %-7s | %-7s | %-7s | %-11s\n", this.getID(), this.getDateTime(),
                        this.getLikes(), this.getShares(), this.getAuthor(), this.getContent());
    }

}
