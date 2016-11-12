package com.friendmatch_frontend.friendmatch;

public class Hobby {
    private String hobby_name;
    private int hobby_img;

    public Hobby() {}

    public Hobby(String hobby_name, int hobby_img) {
        this.hobby_name = hobby_name;
        this.hobby_img = hobby_img;
    }

    public void setHobby_name(String hobby_name) {
        this.hobby_name = hobby_name;
    }

    public void setHobby_img(int hobby_img) {
        this.hobby_img = hobby_img;
    }

    public String getHobby_name() {
        return hobby_name;
    }

    public int getHobby_img() {
        return hobby_img;
    }

}
