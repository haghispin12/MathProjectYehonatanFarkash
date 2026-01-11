package com.example.mathprojectyehonatan;

import android.graphics.Bitmap;
import android.net.Uri;

public class User {
    private String userName;
    private int score;
    private int rating;
     private Uri uri1;
     private Bitmap bitm;
     private long id;
    public User(String username){
        this.userName = username;
    }
    public User(long id, String name, int rate, Bitmap bitmap, int score) {
        this. id = id; this.userName = name; this.rating = rate; this.bitm = bitmap; this.score = score;
    }
    public void setScore(int score){
        this.score += score;
    }
    public int getScore() {
        return this.score;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public int getRating() {
        return this.rating;
    }//לא יודע איך לשמור

    public void setRating(int rating) {
        this.rating = rating;
    }
    public void setPctr(Uri urii) {
        uri1 = urii;
    }

    public Uri getUri() {
        return uri1;
    }
    public void setId(long id) {
        this.id = id;
    }
}

