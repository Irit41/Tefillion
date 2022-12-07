package com.example.tefillion;

import java.sql.Time;

public class Alert_Class {


    private String time;
    private String name;

    public String getName() {
        return name;
    }
    public String getTime() {
        return time;
    }


    public Alert_Class(String name , String time)
    {
        this.name = name;
        this.time =time;
    }

}
