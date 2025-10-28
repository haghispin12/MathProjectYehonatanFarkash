package com.example.mathprojectyehonatan;

public class User {
    private String userName;
    private int score;
    public User(String username){
        this.userName = username;
    }
    public void setScore(int score){
        this.score += score;
    }
    public int getScore() {
        return this.score;
    }

}
