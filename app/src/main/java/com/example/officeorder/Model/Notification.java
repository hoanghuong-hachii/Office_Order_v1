package com.example.officeorder.Model;

public class Notification {
    private int id;
    private String title;
    private String body;
    private String datetime;
    public Notification(int id, String title, String body, String datetime) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.datetime = datetime;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}