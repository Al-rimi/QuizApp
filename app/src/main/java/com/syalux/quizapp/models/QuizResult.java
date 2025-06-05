package com.syalux.quizapp.models;

public class QuizResult {
    private int id;
    private int userId;
    private int score;
    private int totalQuestions;
    private String quizDate;
    private String quizCategory;

    public QuizResult() {
    }

    public QuizResult(int userId, int score, int totalQuestions, String quizDate, String quizCategory) {
        this.userId = userId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.quizDate = quizDate;
        this.quizCategory = quizCategory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getQuizDate() {
        return quizDate;
    }

    public void setQuizDate(String quizDate) {
        this.quizDate = quizDate;
    }

    public String getQuizCategory() {
        return quizCategory;
    }

    public void setQuizCategory(String quizCategory) {
        this.quizCategory = quizCategory;
    }
}
