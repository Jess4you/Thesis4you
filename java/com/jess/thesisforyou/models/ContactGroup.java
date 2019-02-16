package com.jess.thesisforyou.models;

import java.util.ArrayList;

/**
 * Created by USER on 2/8/2019.
 */

public class ContactGroup {

    String id;
    String name;
    ArrayList<Contact> contactArrayList;

    public ContactGroup(String id, String name){
        this.id = id;
        this.name = name;
    }
    public ContactGroup(String id, String name, ArrayList<Contact> contactArrayList) {
        this.id = id;
        this.name = name;
        this.contactArrayList = contactArrayList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Contact> getContactArrayList() {
        return contactArrayList;
    }

    public void setContactArrayList(ArrayList<Contact> contactArrayList) {
        this.contactArrayList = contactArrayList;
    }
}
