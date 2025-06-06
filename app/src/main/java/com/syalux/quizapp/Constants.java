package com.syalux.quizapp;

public class Constants {
    public static final String DATABASE_NAME = "QuizApp.db";
    public static final int DATABASE_VERSION = 3; // Increment database version

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_QUESTIONS = "questions";
    public static final String TABLE_QUIZ_RESULTS = "quiz_results";
    public static final String TABLE_EXAMS = "exams"; // New table for exams

    public static final String KEY_ID = "id";

    // User Table Columns
    public static final String KEY_STUDENT_ID = "student_id";
    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_USER_TYPE = "user_type"; // New column for user type (student/teacher)
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
    public static final String KEY_EXAM_ID = "exam_id"; // Link questions to exams

    // Quiz Results Table Columns
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_SCORE = "score";
    public static final String KEY_TOTAL_QUESTIONS = "total_questions";
    public static final String KEY_QUIZ_DATE = "quiz_date";
    public static final String KEY_QUIZ_CATEGORY = "quiz_category"; // This will become the exam name

    // Exam Table Columns
    public static final String KEY_EXAM_NAME = "exam_name";
    public static final String KEY_TEACHER_ID = "teacher_id"; // Link exam to teacher
    public static final String KEY_NUMBER_OF_QUESTIONS = "number_of_questions"; // Store the number of questions in the exam
    public static final String KEY_PUBLISHED = "published"; // Status if the exam is visible to students (0=false, 1=true)

    // Extras for Intents
    public static final String EXTRA_USER_ID = "USER_ID";
    public static final String EXTRA_QUIZ_CATEGORY = "QUIZ_CATEGORY"; // Will be used for exam name
    public static final String EXTRA_QUIZ_SCORE = "QUIZ_SCORE";
    public static final String EXTRA_TOTAL_QUESTIONS = "TOTAL_QUESTIONS";
    public static final String EXTRA_EXAM_ID = "EXAM_ID"; // New extra for passing exam ID
}