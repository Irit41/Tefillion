package com.example.tefillion;

public class Pray {

    private String name, details;

    public Pray() {
    }
    public Pray(String name, String details) {
        this.name = name;
        this.details = details;
    }

    public String getName() {
        return name;
    }
    public String getDetails() {
        return details;
    }



    public String toString() {
        return  "name='" + name   ;
    }

}
