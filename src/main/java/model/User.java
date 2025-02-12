package model;

import java.util.concurrent.atomic.AtomicInteger;

public class User {
    private int id;
    private String username;
    public static AtomicInteger counter = new AtomicInteger(0);

    public User(String name) {
        this.username = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
