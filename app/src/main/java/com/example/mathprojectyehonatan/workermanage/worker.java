package com.example.mathprojectyehonatan.workermanage;

import android.net.Uri;

public class worker {
    private String FirstName;
    private String LastName;
    private String id;
    private Uri uri1;
    public worker(String firstName, String lastName, String id) {
        this.FirstName = firstName; this.LastName = lastName; this.id = id;
    }
}
