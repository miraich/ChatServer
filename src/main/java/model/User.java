package model;


public class User {
    private int id;
    private String username;
    public static int counter;

    public User(String name, int id) {
        this.username = name;
        this.id = id;
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
