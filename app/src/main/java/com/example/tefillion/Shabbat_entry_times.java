package com.example.tefillion;

public class Shabbat_entry_times {

    public Shabbat_entry_times(String name,String entry, String exit) {

        this.name = name;
        this.entry = entry;
        this.exit = exit;
    }
    public String getName() {
        return name;
    }
    public String getEntry() {
        return entry;
    }

    public String getExit() {
        return exit;
    }

    private String name;
    private String entry;
    private String exit;

    @Override
    public String toString() {
        return
                "cityName='" + name + '\'' +
                ", entry='" + entry + '\'' +
                ", exit='" + exit ;
    }
}
