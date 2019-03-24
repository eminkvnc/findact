package com.findact.Firebase;

public class UserLog {

    private String id;
    private String date;


    UserLog(String id, String date) {
        this.id = id;
        this.date = date;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
