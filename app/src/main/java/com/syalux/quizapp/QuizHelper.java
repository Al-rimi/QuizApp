package com.syalux.quizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.syalux.quizapp.models.Exam; // New model
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

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_USERS = "CREATE TABLE "
                + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_STUDENT_ID + " TEXT UNIQUE,"
                + KEY_PHONE_NUMBER + " TEXT,"
                + KEY_USER_TYPE + " TEXT DEFAULT '" + USER_TYPE_STUDENT + "'"
                + ")";
        db.execSQL(CREATE_TABLE_USERS);
        Log.d(TAG, "Table " + TABLE_USERS + " created.");

        String CREATE_TABLE_EXAMS = "CREATE TABLE "
                + TABLE_EXAMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_EXAM_NAME + " TEXT UNIQUE,"
                + KEY_TEACHER_ID + " INTEGER,"
                + KEY_NUMBER_OF_QUESTIONS + " INTEGER,"
                + KEY_PUBLISHED + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY(" + KEY_TEACHER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_ID + ")"
                + ")";
        db.execSQL(CREATE_TABLE_EXAMS);
        Log.d(TAG, "Table " + TABLE_EXAMS + " created.");

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
                + KEY_EXAM_ID + " INTEGER,"
                + "FOREIGN KEY(" + KEY_EXAM_ID + ") REFERENCES " + TABLE_EXAMS + "(" + KEY_ID + ")"
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

        // Pass the 'db' instance to addDefaultData
        addDefaultData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_RESULTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXAMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        // Create new tables
        // Pass the 'db' instance to onCreate
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Adds a new user to the database.
     * @param user The User object to be added.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long createUser(User user) {
        // This method should *not* be called from onCreate or onUpgrade without passing the db instance.
        // For external calls, it's fine to getWritableDatabase().
        // For calls from onCreate/onUpgrade, use the overloaded method: createUser(SQLiteDatabase db, User user)
        SQLiteDatabase db = this.getWritableDatabase(); // Original line, keep for external calls
        ContentValues values = new ContentValues();
        values.put(KEY_STUDENT_ID, user.getStudentId());
        values.put(KEY_PHONE_NUMBER, user.getPhoneNumber());
        values.put(KEY_USER_TYPE, user.getUserType());
        long result = db.insert(TABLE_USERS, null, values);
        Log.d(TAG, "User created with ID: " + result);
        return result;
    }

    /**
     * **Overloaded method for internal use during database creation/upgrade.**
     * Adds a new user to the database using the provided SQLiteDatabase instance.
     * @param db The SQLiteDatabase instance to use.
     * @param user The User object to be added.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    private long createUser(SQLiteDatabase db, User user) {
        ContentValues values = new ContentValues();
        values.put(KEY_STUDENT_ID, user.getStudentId());
        values.put(KEY_PHONE_NUMBER, user.getPhoneNumber());
        values.put(KEY_USER_TYPE, user.getUserType());
        long result = db.insert(TABLE_USERS, null, values);
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
                user.setUserType(c.getString(c.getColumnIndexOrThrow(KEY_USER_TYPE)));
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error getting column index: " + e.getMessage());
        } finally {
            if (c != null) {
                c.close();
            }
            // Do NOT close the database here if you obtained it via getReadableDatabase()
            // because it might be needed by the calling context (e.g., SignInActivity).
            // The SQLiteOpenHelper manages opening/closing.
        }
        return user;
    }

    /**
     * Adds default data including a teacher, an exam, and questions.
     * This method is called only when the database is created.
     * @param db The SQLiteDatabase instance.
     */
    private void addDefaultData(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            // Create a default teacher user
            User teacher = new User("teacher123", "000-000-0000", USER_TYPE_TEACHER);
            // Use the overloaded createUser method that accepts a db instance
            long teacherId = createUser(db, teacher);

            if (teacherId != -1) {
                // Create a default exam by the teacher
                Exam defaultExam = new Exam("General Knowledge Exam", (int) teacherId, 0, false);
                long examId = createExam(db, defaultExam);

                if (examId != -1) {
                    List<Question> defaultQuestions = new ArrayList<>();
                    defaultQuestions.add(new Question("What is the capital of France?", "Berlin", "Madrid", "Paris", "Rome", 2, false, (int) examId));
                    defaultQuestions.add(new Question("Is the sky blue?", "True", "False", null, null, 0, true, (int) examId));
                    defaultQuestions.add(new Question("Which planet is known as the Red Planet?", "Earth", "Mars", "Jupiter", "Venus", 1, false, (int) examId));
                    defaultQuestions.add(new Question("2 + 2 = 4", "True", "False", null, null, 0, true, (int) examId));
                    defaultQuestions.add(new Question("The chemical symbol for water is H2O.", "True", "False", null, null, 0, true, (int) examId));
                    defaultQuestions.add(new Question("Who wrote 'Romeo and Juliet'?", "Charles Dickens", "William Shakespeare", "Jane Austen", "Mark Twain", 1, false, (int) examId));
                    defaultQuestions.add(new Question("What is the largest ocean on Earth?", "Atlantic Ocean", "Indian Ocean", "Arctic Ocean", "Pacific Ocean", 3, false, (int) examId));
                    defaultQuestions.add(new Question("The sun is a star.", "True", "False", null, null, 0, true, (int) examId));
                    defaultQuestions.add(new Question("What is the square root of 81?", "7", "8", "9", "10", 2, false, (int) examId));
                    defaultQuestions.add(new Question("Birds are mammals.", "True", "False", null, null, 1, true, (int) examId));

                    for (Question q : defaultQuestions) {
                        createQuestion(db, q);
                    }
                    // Update the number of questions in the exam
                    updateExamQuestionCount(db, (int) examId, defaultQuestions.size());
                    publishExam(db, (int) examId, true); // Publish the default exam
                    Log.d(TAG, "Default exam and questions added successfully.");
                }
            } else {
                Log.e(TAG, "Failed to create default teacher user.");
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Creates a new exam in the database.
     * @param db The SQLiteDatabase instance.
     * @param exam The Exam object to be added.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long createExam(SQLiteDatabase db, Exam exam) {
        ContentValues values = new ContentValues();
        values.put(KEY_EXAM_NAME, exam.getExamName());
        values.put(KEY_TEACHER_ID, exam.getTeacherId());
        values.put(KEY_NUMBER_OF_QUESTIONS, exam.getNumberOfQuestions());
        values.put(KEY_PUBLISHED, exam.isPublished() ? 1 : 0);
        return db.insert(TABLE_EXAMS, null, values);
    }

    /**
     * Adds a new question to the database and associates it with an exam.
     *
     * @param db         The SQLiteDatabase instance.
     * @param question   The Question object to be added.
     */
    public long createQuestion(SQLiteDatabase db, Question question) {
        ContentValues values = new ContentValues();
        values.put(KEY_QUESTION_TEXT, question.getQuestionText());
        values.put(KEY_OPTION_A, question.getOptionA());
        values.put(KEY_OPTION_B, question.getOptionB());
        values.put(KEY_OPTION_C, question.getOptionC());
        values.put(KEY_OPTION_D, question.getOptionD());
        values.put(KEY_CORRECT_ANSWER, question.getCorrectAnswer());
        values.put(KEY_IS_TRUE_FALSE, question.isTrueFalse() ? 1 : 0);
        values.put(KEY_EXAM_ID, question.getExamId());
        return db.insert(TABLE_QUESTIONS, null, values);
    }

    /**
     * Updates the number of questions for a given exam.
     *
     * @param db                The SQLiteDatabase instance.
     * @param examId            The ID of the exam to update.
     * @param numberOfQuestions The new total number of questions for the exam.
     */
    public void updateExamQuestionCount(SQLiteDatabase db, int examId, int numberOfQuestions) {
        ContentValues values = new ContentValues();
        values.put(KEY_NUMBER_OF_QUESTIONS, numberOfQuestions);
        db.update(TABLE_EXAMS, values, KEY_ID + " = ?", new String[]{String.valueOf(examId)});
    }

    /**
     * Publishes or unpublishes an exam.
     *
     * @param db        The SQLiteDatabase instance.
     * @param examId    The ID of the exam to publish/unpublish.
     * @param published True to publish, false to unpublish.
     */
    public void publishExam(SQLiteDatabase db, int examId, boolean published) {
        ContentValues values = new ContentValues();
        values.put(KEY_PUBLISHED, published ? 1 : 0);
        db.update(TABLE_EXAMS, values, KEY_ID + " = ?", new String[]{String.valueOf(examId)});
    }

    /**
     * Retrieves all questions belonging to a specific exam.
     * @param examId The ID of the exam to retrieve questions for.
     * @return A list of Question objects.
     */
    public List<Question> getQuestionsByExamId(int examId) {
        List<Question> questions = new ArrayList<>();
        // This method is called from outside onCreate/onUpgrade, so it's fine to getWritableDatabase()
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_QUESTIONS + " WHERE " + KEY_EXAM_ID + " = ?";

        try (Cursor c = db.rawQuery(selectQuery, new String[]{String.valueOf(examId)})) {
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
                    q.setExamId(c.getInt(c.getColumnIndexOrThrow(KEY_EXAM_ID)));
                    questions.add(q);
                } while (c.moveToNext());
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error getting column index: " + e.getMessage());
        }
        return questions;
    }

    /**
     * Retrieves all distinct quiz categories (now exam names) available to students (published exams).
     * @return A list of unique quiz category strings (exam names).
     */
    public List<Exam> getPublishedExams() {
        List<Exam> exams = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_EXAMS + " WHERE " + KEY_PUBLISHED + " = 1";

        try (Cursor c = db.rawQuery(selectQuery, null)) {
            if (c.moveToFirst()) {
                do {
                    Exam exam = new Exam();
                    exam.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
                    exam.setExamName(c.getString(c.getColumnIndexOrThrow(KEY_EXAM_NAME)));
                    exam.setTeacherId(c.getInt(c.getColumnIndexOrThrow(KEY_TEACHER_ID)));
                    exam.setNumberOfQuestions(c.getInt(c.getColumnIndexOrThrow(KEY_NUMBER_OF_QUESTIONS)));
                    exam.setPublished(c.getInt(c.getColumnIndexOrThrow(KEY_PUBLISHED)) == 1);
                    exams.add(exam);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting published exams", e);
        }
        return exams;
    }

    /**
     * Retrieves all exams created by a specific teacher.
     * @param teacherId The ID of the teacher.
     * @return A list of Exam objects.
     */
    public List<Exam> getExamsByTeacherId(int teacherId) {
        List<Exam> exams = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_EXAMS + " WHERE " + KEY_TEACHER_ID + " = ?";

        try (Cursor c = db.rawQuery(selectQuery, new String[]{String.valueOf(teacherId)})) {
            if (c.moveToFirst()) {
                do {
                    Exam exam = new Exam();
                    exam.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
                    exam.setExamName(c.getString(c.getColumnIndexOrThrow(KEY_EXAM_NAME)));
                    exam.setTeacherId(c.getInt(c.getColumnIndexOrThrow(KEY_TEACHER_ID)));
                    exam.setNumberOfQuestions(c.getInt(c.getColumnIndexOrThrow(KEY_NUMBER_OF_QUESTIONS)));
                    exam.setPublished(c.getInt(c.getColumnIndexOrThrow(KEY_PUBLISHED)) == 1);
                    exams.add(exam);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting exams by teacher ID", e);
        }
        return exams;
    }

    /**
     * Retrieves a single exam by its ID.
     * @param examId The ID of the exam.
     * @return The Exam object if found, otherwise null.
     */
    public Exam getExamById(int examId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_EXAMS + " WHERE " + KEY_ID + " = ?";
        Cursor c = null;
        Exam exam = null;

        try {
            c = db.rawQuery(selectQuery, new String[]{String.valueOf(examId)});
            if (c.moveToFirst()) {
                exam = new Exam();
                exam.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
                exam.setExamName(c.getString(c.getColumnIndexOrThrow(KEY_EXAM_NAME)));
                exam.setTeacherId(c.getInt(c.getColumnIndexOrThrow(KEY_TEACHER_ID)));
                exam.setNumberOfQuestions(c.getInt(c.getColumnIndexOrThrow(KEY_NUMBER_OF_QUESTIONS)));
                exam.setPublished(c.getInt(c.getColumnIndexOrThrow(KEY_PUBLISHED)) == 1);
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error getting column index: " + e.getMessage());
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return exam;
    }

    /**
     * Deletes a question from the database.
     * @param questionId The ID of the question to delete.
     * @return The number of rows affected.
     */
    public int deleteQuestion(int questionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_QUESTIONS, KEY_ID + " = ?", new String[]{String.valueOf(questionId)});
    }

    /**
     * Updates an existing question in the database.
     * @param question The Question object with updated details.
     * @return The number of rows affected.
     */
    public int updateQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_QUESTION_TEXT, question.getQuestionText());
        values.put(KEY_OPTION_A, question.getOptionA());
        values.put(KEY_OPTION_B, question.getOptionB());
        values.put(KEY_OPTION_C, question.getOptionC());
        values.put(KEY_OPTION_D, question.getOptionD());
        values.put(KEY_CORRECT_ANSWER, question.getCorrectAnswer());
        values.put(KEY_IS_TRUE_FALSE, question.isTrueFalse() ? 1 : 0);
        values.put(KEY_EXAM_ID, question.getExamId());
        return db.update(TABLE_QUESTIONS, values, KEY_ID + " = ?", new String[]{String.valueOf(question.getId())});
    }

    /**
     * Updates an existing exam in the database.
     * @param exam The Exam object with updated details.
     * @return The number of rows affected.
     */
    public int updateExam(Exam exam) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EXAM_NAME, exam.getExamName());
        values.put(KEY_NUMBER_OF_QUESTIONS, exam.getNumberOfQuestions());
        values.put(KEY_PUBLISHED, exam.isPublished() ? 1 : 0);
        return db.update(TABLE_EXAMS, values, KEY_ID + " = ?", new String[]{String.valueOf(exam.getId())});
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
        Log.d(TAG, "Quiz result created with ID: " + result);
    }
}