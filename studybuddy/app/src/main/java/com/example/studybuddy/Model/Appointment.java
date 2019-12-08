package com.example.studybuddy.Model;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

//Todo Change naming convetion in both Firebase and here in to lower_case
public class Appointment implements Serializable {
    private String appId;
    private String course;
    private String tutor;
    private String location;
    private String date;
    private String price;
    private String student;


    public void setCourse(String course) {
        this.course = course;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public void setTutor(String tutor) {
        this.tutor = tutor;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStudent(){
        return this.student;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Appointment(){

    }

    public Appointment(String c, String tut, String l,  String d){
        course = c;
        tutor = tut;
        location = l;
        date = d;
    }

    public Appointment(String c, String tut, String l,  String d,String student){
        course = c;
        tutor = tut;
        location = l;
        date = d;
        this.student=student;
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