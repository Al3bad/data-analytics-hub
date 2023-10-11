package dev.alabbad.models;

import java.sql.Connection;

public class Model {
    private static UserDao userDao;
    private static PostDao postDao;

    public static void init(Connection connection) {
        // create objects
        userDao = new UserDao(connection);
        postDao = new PostDao(connection);

        // create tables
        userDao.createTable();
        postDao.createTable();
    }

    public static UserDao getUserDao() {
        return userDao;
    }

    public static PostDao getPostDao() {
        return postDao;
    }
}
