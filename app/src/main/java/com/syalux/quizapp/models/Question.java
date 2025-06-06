package com.syalux.quizapp.models;

public class Question {
    private int id;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private int correctAnswer; // 0 for A, 1 for B, 2 for C, 3 for D for MCQs; 0 for True, 1 for False for T/F
    private boolean isTrueFalse;
    private int examId; // Foreign key to Exam table

    public Question() {
    }

    public Question(String questionText, String optionA, String optionB, String optionC, String optionD, int correctAnswer, boolean isTrueFalse, int examId) {
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.isTrueFalse = isTrueFalse;
        this.examId = examId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public boolean isTrueFalse() {
        return isTrueFalse;
    }

    public void setTrueFalse(boolean trueFalse) {
        isTrueFalse = trueFalse;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }
}