package com.example.studybuddy.Model;

import java.util.ArrayList;
import java.util.List;

public class Profile {
    //Profile Object


    private String first_name;
    private String last_name;
    private String gender;
    private String cover_url;
    private String image_url;
    private String class_year;
    private String user_id;
    private ArrayList<String>  tutor_class;
    private ArrayList<String>  tutor_session;
    private ArrayList<String>  your_class;







    public Profile(String user_id, String first_name, String last_name, String gender, String cover_url, String image_url, String class_year, ArrayList<String> tutor_class, ArrayList<String> tutor_session, ArrayList<String> your_class ){
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.cover_url = cover_url;
        this.image_url = image_url;
        this.class_year = class_year;
        this.tutor_class = tutor_class;
        this.tutor_session = tutor_session;
        this.your_class = your_class;

    }
    //Getters
    public String getUser_id(){return user_id;}
    public String getFirst_name(){
        return first_name;
    }
    public String getLast_name() {return last_name;}
    //Setters
    public String getGender(){
        return gender;
    }
    public String getCover_url(){
        return cover_url;
    }
    public String getImage_url(){return image_url;}
    public String getClass_year(){return class_year;}
    public ArrayList<String> getTutor_class(){return tutor_class;}
    public ArrayList<String> getTutor_session(){return tutor_session;}
    public ArrayList<String> getYour_class(){return your_class;}

    public void setUser_id(String user_id){this.user_id = user_id;}
    public void setFirst_name(String first_name){this.first_name = first_name;}
    public void setLast_name(String last_name){this.last_name = last_name;}
    public void setGender(String gender){
        this.gender = gender;
    }
    public void setCover_url(String cover_url){
        this.cover_url = cover_url;
    }
    public void setImage_url(String image_url){this.image_url = image_url;}
    public void setClass_year(String class_year){this.class_year = class_year;}
    public void setTutor_class(ArrayList<String> tutor_class){this.tutor_class = tutor_class;}
    public void setTutor_session(ArrayList<String> tutor_session){this.tutor_session = tutor_session;}
    public void setYour_class(ArrayList<String> your_class){this.your_class = your_class;}

    public Profile() {}

}
