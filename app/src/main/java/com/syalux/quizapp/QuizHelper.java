package com.syalux.quizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.syalux.quizapp.models.Question;
import com.syalux.quizapp.models.QuizResult;
import com.syalux.quizapp.models.User;

import java.util.ArrayList;
import java.util.List;

import static com.syalux.quizapp.Constants.*;

public class QuizHelper extends SQLiteOpenHelper {

    private static final String TAG = "QuizHelper";

    public QuizHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     * This is where the schema (tables) for the database should be created.
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_USERS = "CREATE TABLE "
                + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_STUDENT_ID + " TEXT UNIQUE,"
                + KEY_PHONE_NUMBER + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE_USERS);
        Log.d(TAG, "Table " + TABLE_USERS + " created.");

        String CREATE_TABLE_QUESTIONS = "CREATE TABLE "
                + TABLE_QUESTIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_QUESTION_TEXT + " TEXT,"
                + KEY_OPTION_A + " TEXT,"
                + KEY_OPTION_B + " TEXT,"
                + KEY_OPTION_C + " TEXT,"
                + KEY_OPTION_D + " TEXT,"
                + KEY_CORRECT_ANSWER + " INTEGER,"
                + KEY_IS_TRUE_FALSE + " INTEGER DEFAULT 0,"
                + KEY_CATEGORY + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE_QUESTIONS);
        Log.d(TAG, "Table " + TABLE_QUESTIONS + " created.");

        String CREATE_TABLE_QUIZ_RESULTS = "CREATE TABLE "
                + TABLE_QUIZ_RESULTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_ID + " INTEGER,"
                + KEY_SCORE + " INTEGER,"
                + KEY_TOTAL_QUESTIONS + " INTEGER,"
                + KEY_QUIZ_DATE + " TEXT,"
                + KEY_QUIZ_CATEGORY + " TEXT,"
                + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + ")"
                + ")";
        db.execSQL(CREATE_TABLE_QUIZ_RESULTS);
        Log.d(TAG, "Table " + TABLE_QUIZ_RESULTS + " created.");

        addDefaultQuestions(db);
    }

    /**
     * Called when the database needs to be upgraded.
     * This method is called when the database exists, but the stored version number is lower than newVersion.
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_RESULTS);
        onCreate(db);
    }

    /**
     * Adds a new user to the database.
     * @param user The User object to be added.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long createUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STUDENT_ID, user.getStudentId());
        values.put(KEY_PHONE_NUMBER, user.getPhoneNumber());
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        Log.d(TAG, "User created with ID: " + result);
        return result;
    }

    /**
     * Retrieves a user from the database based on student ID.
     * @param studentId The student ID to search for.
     * @return The User object if found, otherwise null.
     */
    public User getUserByStudentId(String studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_STUDENT_ID + " = ?";
        Cursor c = null;
        User user = null;

        try {
            c = db.rawQuery(selectQuery, new String[]{studentId});
            if (c.moveToFirst()) {
                user = new User();
                user.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
                user.setStudentId(c.getString(c.getColumnIndexOrThrow(KEY_STUDENT_ID)));
                user.setPhoneNumber(c.getString(c.getColumnIndexOrThrow(KEY_PHONE_NUMBER)));
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error getting column index: " + e.getMessage());
        } finally {
            if (c != null) {
                c.close();
            }
            db.close();
        }
        return user;
    }

    /**
     * Populates the questions table with default quiz questions.
     * This method is called only when the database is created.
     * @param db The SQLiteDatabase instance.
     */
    private void addDefaultQuestions(SQLiteDatabase db) {
        // Create sample questions
        Question q1 = new Question("What is the capital of France?", "Berlin", "Madrid", "Paris", "Rome", 2, false, "Geography");
        Question q2 = new Question("Is the sky blue?", "True", "False", null, null, 0, true, "General Knowledge");
        Question q3 = new Question("Which planet is known as the Red Planet?", "Earth", "Mars", "Jupiter", "Venus", 1, false, "Science");
        Question q4 = new Question("2 + 2 = 4", "True", "False", null, null, 0, true, "Mathematics");
        Question q5 = new Question("The chemical symbol for water is H2O.", "True", "False", null, null, 0, true, "Science");
        Question q6 = new Question("Who wrote 'Romeo and Juliet'?", "Charles Dickens", "William Shakespeare", "Jane Austen", "Mark Twain", 1, false, "Literature");
        Question q7 = new Question("What is the largest ocean on Earth?", "Atlantic Ocean", "Indian Ocean", "Arctic Ocean", "Pacific Ocean", 3, false, "Geography");
        Question q8 = new Question("The sun is a star.", "True", "False", null, null, 0, true, "Science");
        Question q9 = new Question("What is the square root of 81?", "7", "8", "9", "10", 2, false, "Mathematics");
        Question q10 = new Question("Birds are mammals.", "True", "False", null, null, 1, true, "General Knowledge");

        // Insert each question into the database
        // Log results to check for insertion failures
        if (createQuestion(db, q1) == -1) Log.e(TAG, "Failed to insert question: " + q1.getQuestionText());
        if (createQuestion(db, q2) == -1) Log.e(TAG, "Failed to insert question: " + q2.getQuestionText());
        if (createQuestion(db, q3) == -1) Log.e(TAG, "Failed to insert question: " + q3.getQuestionText());
        if (createQuestion(db, q4) == -1) Log.e(TAG, "Failed to insert question: " + q4.getQuestionText());
        if (createQuestion(db, q5) == -1) Log.e(TAG, "Failed to insert question: " + q5.getQuestionText());
        if (createQuestion(db, q6) == -1) Log.e(TAG, "Failed to insert question: " + q6.getQuestionText());
        if (createQuestion(db, q7) == -1) Log.e(TAG, "Failed to insert question: " + q7.getQuestionText());
        if (createQuestion(db, q8) == -1) Log.e(TAG, "Failed to insert question: " + q8.getQuestionText());
        if (createQuestion(db, q9) == -1) Log.e(TAG, "Failed to insert question: " + q9.getQuestionText());
        if (createQuestion(db, q10) == -1) Log.e(TAG, "Failed to insert question: " + q10.getQuestionText());
        Log.d(TAG, "Default questions added successfully.");
    }

    /**
     * Adds a new question to the database.
     * This method is package-private as it's primarily used by addDefaultQuestions.
     * @param db The SQLiteDatabase instance.
     * @param question The Question object to be added.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    long createQuestion(SQLiteDatabase db, Question question) {
        ContentValues values = new ContentValues();
        values.put(KEY_QUESTION_TEXT, question.getQuestionText());
        values.put(KEY_OPTION_A, question.getOptionA());
        values.put(KEY_OPTION_B, question.getOptionB());
        values.put(KEY_OPTION_C, question.getOptionC());
        values.put(KEY_OPTION_D, question.getOptionD());
        values.put(KEY_CORRECT_ANSWER, question.getCorrectAnswer());
        values.put(KEY_IS_TRUE_FALSE, question.isTrueFalse() ? 1 : 0);
        values.put(KEY_CATEGORY, question.getCategory());
        return db.insert(TABLE_QUESTIONS, null, values);
    }

    /**
     * Retrieves all questions belonging to a specific category.
     * @param category The category of questions to retrieve.
     * @return A list of Question objects.
     */
    public List<Question> getQuestionsByCategory(String category) {
        List<Question> questions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_QUESTIONS + " WHERE " + KEY_CATEGORY + " = ?";
        Cursor c = null;

        try {
            c = db.rawQuery(selectQuery, new String[]{category});
            if (c.moveToFirst()) {
                do {
                    Question q = new Question();
                    q.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
                    q.setQuestionText(c.getString(c.getColumnIndexOrThrow(KEY_QUESTION_TEXT)));
                    q.setOptionA(c.getString(c.getColumnIndexOrThrow(KEY_OPTION_A)));
                    q.setOptionB(c.getString(c.getColumnIndexOrThrow(KEY_OPTION_B)));
                    q.setOptionC(c.getString(c.getColumnIndexOrThrow(KEY_OPTION_C)));
                    q.setOptionD(c.getString(c.getColumnIndexOrThrow(KEY_OPTION_D)));
                    q.setCorrectAnswer(c.getInt(c.getColumnIndexOrThrow(KEY_CORRECT_ANSWER)));
                    q.setTrueFalse(c.getInt(c.getColumnIndexOrThrow(KEY_IS_TRUE_FALSE)) == 1);
                    q.setCategory(c.getString(c.getColumnIndexOrThrow(KEY_CATEGORY)));
                    questions.add(q);
                } while (c.moveToNext());
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error getting column index: " + e.getMessage());
        } finally {
            if (c != null) {
                c.close();
            }
            db.close();
        }
        return questions;
    }

    /**
     * Retrieves all distinct quiz categories available in the questions table.
     * @return A list of unique quiz category strings.
     */
    public List<String> getAllQuizCategories() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT DISTINCT " + KEY_CATEGORY + " FROM " + TABLE_QUESTIONS;
        Cursor c = null;

        try {
            c = db.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {
                do {
                    categories.add(c.getString(c.getColumnIndexOrThrow(KEY_CATEGORY)));
                } while (c.moveToNext());
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error getting column index: " + e.getMessage());
        } finally {
            if (c != null) {
                c.close();
            }
            db.close();
        }
        return categories;
    }

    /**
     * Adds a new quiz result to the database.
     * @param quizResult The QuizResult object to be added.
     */
    public void createQuizResult(QuizResult quizResult) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, quizResult.getUserId());
        values.put(KEY_SCORE, quizResult.getScore());
        values.put(KEY_TOTAL_QUESTIONS, quizResult.getTotalQuestions());
        values.put(KEY_QUIZ_DATE, quizResult.getQuizDate());
        values.put(KEY_QUIZ_CATEGORY, quizResult.getQuizCategory());
        long result = db.insert(TABLE_QUIZ_RESULTS, null, values);
        db.close();
        Log.d(TAG, "Quiz result created with ID: " + result);
    }
}
