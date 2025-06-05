package com.syalux.quizapp;

public class Constants {
    public static final String DATABASE_NAME = "QuizApp.db";
    public static final int DATABASE_VERSION = 2;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_QUESTIONS = "questions";
    public static final String TABLE_QUIZ_RESULTS = "quiz_results";

    public static final String KEY_ID = "id";

    public static final String KEY_STUDENT_ID = "student_id";
    public static final String KEY_PHONE_NUMBER = "phone_number";

    public static final String KEY_QUESTION_TEXT = "question_text";
    public static final String KEY_OPTION_A = "option_a";
    public static final String KEY_OPTION_B = "option_b";
    public static final String KEY_OPTION_C = "option_c";
    public static final String KEY_OPTION_D = "option_d";
    public static final String KEY_CORRECT_ANSWER = "correct_answer"; // 0: A, 1: B, 2: C, 3: D
    public static final String KEY_IS_TRUE_FALSE = "is_true_false";
    public static final String KEY_CATEGORY = "category";

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_SCORE = "score";
    public static final String KEY_TOTAL_QUESTIONS = "total_questions";
    public static final String KEY_QUIZ_DATE = "quiz_date";
    public static final String KEY_QUIZ_CATEGORY = "quiz_category";

    public static final String EXTRA_USER_ID = "USER_ID";
    public static final String EXTRA_QUIZ_CATEGORY = "QUIZ_CATEGORY";
    public static final String EXTRA_QUIZ_SCORE = "QUIZ_SCORE";
    public static final String EXTRA_TOTAL_QUESTIONS = "TOTAL_QUESTIONS";
}
