package com.syalux.quizapp.models;

public class Exam {
    private int id;
    private String examName;
    private int teacherId; // Foreign key to User table (teacher)
    private int numberOfQuestions;
    private boolean published; // true if published, false otherwise

    public Exam() {
    }

    public Exam(String examName, int teacherId, int numberOfQuestions, boolean published) {
        this.examName = examName;
        this.teacherId = teacherId;
        this.numberOfQuestions = numberOfQuestions;
        this.published = published;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}