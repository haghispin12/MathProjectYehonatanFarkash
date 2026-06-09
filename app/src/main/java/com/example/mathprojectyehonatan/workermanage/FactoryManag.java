package com.example.mathprojectyehonatan.workermanage;

public class FactoryManag {
    private String firstName;
    private String lastName;
    private String id;
    private String  mail;
    private String factoryNumber;

    public FactoryManag(String firstName, String lastName, String id, String mail, String factoryNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.mail = mail;
        this.factoryNumber = factoryNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setId(String id) {
        this.id = id;
    }
   public void setMail(String mail) {
        this.mail = mail;
    }
    public void setFactoryNumber(String factoryNumber) {
        this.factoryNumber = factoryNumber;
   }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public String getId() {
        return id;
    }
    public String getMail() {
        return this.mail;
    }

    public String getFactoryNumber() {
        return factoryNumber;
    }
}


