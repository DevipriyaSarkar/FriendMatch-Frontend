package com.friendmatch_frontend.friendmatch;

class Hobby {
    private String hobbyName;
    private int hobbyImg;

    public Hobby() {
    }

    Hobby(String hobbyName, int hobbyImg) {
        this.hobbyName = hobbyName;
        this.hobbyImg = hobbyImg;
    }

    public void setHobbyName(String hobbyName) {
        this.hobbyName = hobbyName;
    }

    public void setHobbyImg(int hobbyImg) {
        this.hobbyImg = hobbyImg;
    }

    public String getHobbyName() {
        return hobbyName;
    }

    public int getHobbyImg() {
        return hobbyImg;
    }

}
