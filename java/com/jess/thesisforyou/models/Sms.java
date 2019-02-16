package com.jess.thesisforyou.models;


/**
 * Created by USER on 2/16/2019.
 */

public class Sms {

    private String id;
    private String contactID;
    private String body;
    private String date;
    private String time;
    private String state;

    public Sms(String id, String contactID, String body, String date, String time, String state) {
        this.id = id;
        this.contactID = contactID;
        this.body = body;
        this.date = date;
        this.time = time;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContactID() {
        return contactID;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
