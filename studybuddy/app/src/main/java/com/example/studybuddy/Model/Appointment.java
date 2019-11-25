package com.example.studybuddy;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

//Todo Change naming convetion in both Firebase and here in to lower_case
public class Appointment implements Serializable {
    public String appId;
    public String course;
    public String tutor;
    public String location;
    public String date;
    public String price;

    public Appointment(){

    }

    public Appointment(String c, String tut, String l,  String d){
        course = c;
        tutor = tut;
        location = l;
        date = d;
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
}
