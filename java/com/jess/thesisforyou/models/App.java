package com.jess.thesisforyou.models;

import android.graphics.drawable.Drawable;

/**
 * Created by USER on 2/7/2019.
 */

public class App {

    private String id;      //id is package name of the app
    private String name;
    private Drawable icon;

    public App(String id, String name, Drawable icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
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

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
