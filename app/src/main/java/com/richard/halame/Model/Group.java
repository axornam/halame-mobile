package com.richard.halame.Model;

public class Group {

    private String id;
    private String groupName;
    private String imageURL;

    public Group() {
    }

    public Group(String id, String imageURL, String groupName) {
        this.id = id;
        this.imageURL = imageURL;
        this.groupName = groupName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
