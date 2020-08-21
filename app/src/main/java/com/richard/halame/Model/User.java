package com.richard.halame.Model;

public class User {
    private String id, username,  imageURL;

    public User() {
    }

    public User(String id, String imageURL, String username) {
        this.id = id;
        this.imageURL = imageURL;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
