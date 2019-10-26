package com.example.studybuddy;

import java.sql.Time;
import java.util.Date;

public class Appointment {
    private int appointmentID;
    private Course course;
    private User tutor;
    private User student;
    private Date date;
    private Time time;


    public Appointment(int aptID , Course c, User tut, User stu, Date d, Time t){
        appointmentID = aptID;
        course = c;
        tutor = tut;
        student = stu;
        date = d;
        time = t;
    }
}
