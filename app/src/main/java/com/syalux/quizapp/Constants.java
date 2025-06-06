package com.syalux.quizapp;

public class Constants {
    public static final String DATABASE_NAME = "QuizApp.db";
    public static final int DATABASE_VERSION = 3;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_QUESTIONS = "questions";
    public static final String TABLE_QUIZ_RESULTS = "quiz_results";
    public static final String TABLE_EXAMS = "exams";

    public static final String KEY_ID = "id";

    // User Table Columns
    public static final String KEY_STUDENT_ID = "student_id";
    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_USER_TYPE = "user_type";
    public static final String USER_TYPE_STUDENT = "student";
    public static final String USER_TYPE_TEACHER = "teacher";

    // Questions Table Columns (existing, but will link to exams)
    public static final String KEY_QUESTION_TEXT = "question_text";
    public static final String KEY_OPTION_A = "option_a";
    public static final String KEY_OPTION_B = "option_b";
    public static final String KEY_OPTION_C = "option_c";
    public static final String KEY_OPTION_D = "option_d";
    public static final String KEY_CORRECT_ANSWER = "correct_answer";
    public static final String KEY_IS_TRUE_FALSE = "is_true_false";
    public static final String KEY_EXAM_ID = "exam_id";

    // Quiz Results Table Columns
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_SCORE = "score";
    public static final String KEY_TOTAL_QUESTIONS = "total_questions";
    public static final String KEY_QUIZ_DATE = "quiz_date";
    public static final String KEY_QUIZ_CATEGORY = "quiz_category";

    // Exam Table Columns
    public static final String KEY_EXAM_NAME = "exam_name";
    public static final String KEY_TEACHER_ID = "teacher_id";
    public static final String KEY_NUMBER_OF_QUESTIONS = "number_of_questions";
    public static final String KEY_PUBLISHED = "published";

    // Extras for Intents
    public static final String EXTRA_USER_ID = "USER_ID";
    public static final String EXTRA_QUIZ_CATEGORY = "QUIZ_CATEGORY";
    public static final String EXTRA_QUIZ_SCORE = "QUIZ_SCORE";
    public static final String EXTRA_TOTAL_QUESTIONS = "TOTAL_QUESTIONS";
    public static final String EXTRA_EXAM_ID = "EXAM_ID";
}