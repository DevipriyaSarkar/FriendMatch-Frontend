package com.friendmatch_frontend.friendmatch;

class Hobby {
    private int hobbyID;
    private String hobbyName;
    private int hobbyImg;

    public Hobby() {
    }

    Hobby(int hobbyID, String hobbyName, int hobbyImg) {
        this.hobbyID = hobbyID;
        this.hobbyName = hobbyName;
        this.hobbyImg = hobbyImg;
    }

    public void setHobbyID(int hobbyID) {
        this.hobbyID = hobbyID;
    }

    public void setHobbyName(String hobbyName) {
        this.hobbyName = hobbyName;
    }

    public void setHobbyImg(int hobbyImg) {
        this.hobbyImg = hobbyImg;
    }

    public int getHobbyID() {
        return hobbyID;
    }

    public String getHobbyName() {
        return hobbyName;
    }

    public int getHobbyImg() {
        return hobbyImg;
    }

}
