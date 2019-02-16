package com.jess.thesisforyou.models;

/**
 * Created by USER on 2/7/2019.
 */

public class Contact {

    String id;
    String name;
    String[] numbers;

    public Contact(String id, String name){
        this.id = id;
        this.name = name;
    }
    public Contact(String id, String name, String[] numbers) {
        this.id = id;
        this.name = name;
        this.numbers = numbers;
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

    public String[] getNumbers() {
        return numbers;
    }

    public void setNumbers(String[] numbers) {
        this.numbers = numbers;
    }
}
