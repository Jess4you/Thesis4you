package com.jess.thesisforyou.models;

import java.util.ArrayList;

/**
 * Created by USER on 2/8/2019.
 */

public class AppGroup {

    String id;
    String name;
    ArrayList<App> AppArrayList;

    public AppGroup(String id, String name) {
        this.id = id;
        this.name = name;
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

    public ArrayList<App> getAppArrayList() {
        return AppArrayList;
    }

    public void setAppArrayList(ArrayList<App> appArrayList) {
        AppArrayList = appArrayList;
    }
}
