package com.example.mathprojectyehonatan.workermanage;

import android.net.Uri;

public class worker {
    private String FirstName;
    private String LastName;
    private String id;
    private Uri uri1;
    private String mail;
    public worker(String firstName, String lastName, String id, String mail, Uri uri) {
        this.FirstName = firstName; this.LastName = lastName; this.id = id;this.mail = mail;this.uri1 = uri;
    }
    public void setFirstName(String firstName) {
        this.FirstName = firstName;
    }
    public void setLastName(String lastName) {
        this.LastName = lastName;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setUri1(Uri uri1) {
        this.uri1 =uri1;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getId() {
        return id;
    }

    public Uri getUri1() {
        return uri1;
    }

    public String getMail() {
        return mail;
    }
}
