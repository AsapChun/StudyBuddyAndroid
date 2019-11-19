package com.example.studybuddy;

import java.sql.Time;
import java.util.Date;

public class Appointment {
    private String course;
    private String tutor;
    private String location;
    private String date;
    private String time;



    public Appointment(String c, String tut, String l,  String d, String t){
        course = c;
        tutor = tut;
        location = l;
        date = d;
        time = t;
    }

    public String getCourse(){
        return course;
    }
    public String getTutor(){
        return tutor;
    }
    public String getLocation(){
        return location;
    }
    public String getDate(){
        return date;
    }
    public String getTime(){
        return time;
    }
}
