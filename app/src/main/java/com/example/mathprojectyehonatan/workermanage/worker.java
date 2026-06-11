package com.example.mathprojectyehonatan.workermanage;

import android.net.Uri;

public class worker {
    private String FirstName;
    private String LastName;
    private String id;
    private String mail;
    private String factoryNumber;
    private boolean isEntered;
    private String entryTime;
    private String exitTime;

    private String lastEntryDate; // שומר את התאריך (למשל: "2026-06-10")

    public worker(String firstName, String lastName, String id, String mail, String factoryNumber) {
        this.FirstName = firstName; this.LastName = lastName; this.id = id;this.mail = mail;this.factoryNumber = factoryNumber;
    }
    public worker() {
        // בנאי ריק שפיירבייס דורש בעת שימוש ב-toObject
    }
    public worker(String firstName, String lastName, String id) {
        this.FirstName =firstName; this.LastName = lastName; this.id =id;
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


    public String getMail() {
        return mail;
    }
    public boolean isEntered() { return isEntered; }
    public void setEntered(boolean entered) { isEntered = entered; }

    public String getEntryTime() { return entryTime; }
    public void setEntryTime(String entryTime) { this.entryTime = entryTime; }

    public String getLastEntryDate() { return lastEntryDate; }
    public void setLastEntryDate(String lastEntryDate) { this.lastEntryDate = lastEntryDate; }
    public String getExitTime() { return exitTime; }
    public void setExitTime(String exitTime) { this.exitTime = exitTime; }
    public String getFactoryNumber() {
        return factoryNumber;
    }
    public void setFactoryNumber(String factoryNumber) {
        this.factoryNumber = factoryNumber;
    }
}

