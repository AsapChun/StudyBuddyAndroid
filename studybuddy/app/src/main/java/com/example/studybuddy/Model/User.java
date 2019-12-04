package com.example.studybuddy.Model;

import com.example.studybuddy.Course;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    //User Object

    private String userId;
    private String email;
    private String FirstName;
    private String LastName;
    private String gender;
    private String password;
    private int graduationYear;
    private List<Course> tutorCourse;
    private List<Course> studentCourse;
    private List<Appointment> appointments;
    private List<String> ratings;
    private List<String> reviews;
    private String img_url;

    public User() {

    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(int graduationYear) {
        this.graduationYear = graduationYear;
    }

    public List<Course> getTutorCourse() {
        return tutorCourse;
    }

    public void setTutorCourse(List<Course> tutorCourse) {
        this.tutorCourse = tutorCourse;
    }

    public List<Course> getStudentCourse() {
        return studentCourse;
    }

    public void setStudentCourse(List<Course> studentCourse) {
        this.studentCourse = studentCourse;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public List<String> getRatings() {
        return ratings != null? ratings: new ArrayList<String>();
    }

    public void setRatings(List<String> ratings) {
        this.ratings = ratings;
    }

    public List<String> getReviews() {
        return reviews != null? reviews: new ArrayList<String>();
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public User(String e, String firstN, String lastN, String g, int year, String pass) {
        email = e; //set email
        FirstName = firstN;
        LastName = lastN;
        gender = g;
        graduationYear = year;
        password = pass;

    }

    public String getEmail() {

        return email;
    }

    public String getName() {
        return FirstName + " " + LastName;
    }

    public String getGender() {
        return gender;
    }

    public void addTutor(Course c) {
        tutorCourse.add(c);
    }

    public void addStudent(Course c) {
        studentCourse.add(c);
    }

    public void addAppointment(Appointment a) {
        appointments.add(a);
    }

    public float getAvgRating() {
        if (ratings == null || ratings.size() == 0)
            return 0;

        float sum = 0;
        for (String r : ratings)
            sum += Float.valueOf(r);
        return sum / ratings.size();
    }

}
