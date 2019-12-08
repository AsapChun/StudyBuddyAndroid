package com.example.studybuddy.Model;

import java.io.Serializable;
import java.util.ArrayList;

//Todo Change naming convetion in both Firebase and here in to lower_case
public class AppointmentFB implements Serializable {

    private String ClassName;
    private ArrayList<String> Date;
    private String Location;
    private String StudentId;
    private String TutorId;
    private String price;
    private Boolean rated;
    private Boolean validAppointment;

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public ArrayList<String> getDate() {
        return Date;
    }

    public void setDate(ArrayList<String> date) {
        Date = date;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getTutorId() {
        return TutorId;
    }

    public void setTutorId(String tutorId) {
        TutorId = tutorId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Boolean getRated() {
        return rated;
    }

    public void setRated(Boolean rated) {
        this.rated = rated;
    }

    public Boolean getValidAppointment() {
        return validAppointment;
    }

    public void setValidAppointment(Boolean validAppointment) {
        this.validAppointment = validAppointment;
    }

    public AppointmentFB(String ClassName, ArrayList<String> Date, String Location, String StudentId, String TutorId, String price, Boolean rated, Boolean validAppointment){
        ClassName = ClassName;
        Date = Date;
        Location = Location;
        StudentId = StudentId;
        TutorId = TutorId;
        price = price;
        rated = rated;
        validAppointment = validAppointment;
    }

    public AppointmentFB(){

    }



}
