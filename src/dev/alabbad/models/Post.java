package dev.alabbad.models;

/**
 * Definition of post object
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class Post {
    private Integer ID;
    private String content;
    private String author;
    private Integer likes;
    private Integer shares;
    private String dateTime;

    public Post() {
    }

    public Post(Integer ID, String content, String author, Integer likes, Integer shares, String dateTime) {
        this.ID = ID;
        this.content = content;
        this.author = author;
        this.likes = likes;
        this.shares = shares;
        this.dateTime = dateTime;
    }

    public Integer getID() {
        return this.ID;
    }

    public String getContent() {
        return this.content;
    }

    public String getAuthor() {
        return this.author;
    }

    public Integer getLikes() {
        return this.likes;
    }

    public Integer getShares() {
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
     * Display all details of the post instance
     */
    public void displayDetails() {
        System.out.printf("%-7s | %-16s | %-7s | %-7s | %-7s | %-11s\n", this.getID(), this.getDateTime(),
                        this.getLikes(), this.getShares(), this.getAuthor(), this.getContent());
    }

    /**
     * Display all details of the post instance in csv format
     */
    public String getCSVFormat() {
        return String.format("%s,%s,%s,%s,%s,%s", this.getID(), this.getDateTime(), this.getLikes(), this.getShares(),
                        this.getAuthor(), this.getContent());
    }

}
