package com.richard.halame.Model;

public class Groupchat {
    private String sender;
    private String message;
    private String date;
    private String time;

    public Groupchat() {

    }

    public Groupchat(String sender, String message, String date, String time) {
        this.sender = sender;
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
