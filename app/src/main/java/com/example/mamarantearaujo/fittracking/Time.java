package com.example.mamarantearaujo.fittracking;

/**
 * Created by mateus on 3/10/2016.
 */
public class Time {
    private long hours = 0;
    private int minutes = 0;
    private int seconds = 0;

    public Time() {
        this.hours = 0;
        this.minutes = 0;
        this.seconds = 0;
    }

    public Time(int hours, int minutes, int seconds) {
        this.hours = hours;
        this.minutes= minutes;
        this.seconds = seconds;
    }

    public Time(long time) {
        this.hours = (time/3600);
        this.minutes = (int) ((time%3600)/60);
        this.seconds = (int) time%60;
    }

    public String toString(){
        //TODO
        return new String();
    }
}
