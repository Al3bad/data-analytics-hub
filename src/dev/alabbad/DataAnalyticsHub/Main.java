package dev.alabbad.DataAnalyticsHub;

/*

* Main.java
*
* version 0.0.1
*
* created at 25/08/2023
*
* A GUI application to analys a social media hub. Nomal users can view all
* post in the system, export a post, create post, delete their own posts,
* update their profile. VIP Users can do everthing normal users do as well
* as see shares disributions in a pie chart, bulk import posts. Admin users
* can do everything VIP users do as well as view all users in the system and
* delete non-admin users
*/

/**
 * Entry point of the data analytics program
 *
 * @author Abdullah Alabbad
 * @version 1.0.0
 */
public class Main {
    public static void main(String[] args) {
        new DataAnalyticsHub().run();
    }
}
