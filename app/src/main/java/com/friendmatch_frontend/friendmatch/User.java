package com.friendmatch_frontend.friendmatch;

class User {
    private int id;
    private String name;
    private String gender;

    User() {}

    User(int id, String name, String gender) {
        this.id = id;
        this.name = name;
        this.gender = (gender.equals("M")) ? "Male" : "Female" ;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }
}
