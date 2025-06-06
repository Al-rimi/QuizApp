package com.syalux.quizapp.models;

public class User {
    private int id;
    private String studentId;
    private String phoneNumber;
    private String userType; // "student" or "teacher"

    public User() {
    }

    public User(String studentId, String phoneNumber, String userType) {
        this.studentId = studentId;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}