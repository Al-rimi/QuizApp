package com.syalux.quizapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.syalux.quizapp.QuizHelper;
import com.syalux.quizapp.R;
import com.syalux.quizapp.models.Exam; // Import Exam model
import com.syalux.quizapp.utilities.ExamSelectionAdapter; // New adapter

import java.util.List;
import java.util.Objects;

import static com.syalux.quizapp.Constants.EXTRA_EXAM_ID; // Use EXTRA_EXAM_ID
import static com.syalux.quizapp.Constants.EXTRA_USER_ID;
import static com.syalux.quizapp.Constants.EXTRA_QUIZ_CATEGORY; // Still used for quiz result category

public class ExamSelectionActivity extends AppCompatActivity {

    private RecyclerView quizCategoryRecyclerView;
    private QuizHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_selection);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Select an Exam");

        quizCategoryRecyclerView = findViewById(R.id.quizCategoryRecyclerView);
        quizCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new QuizHelper(this);

        userId = getIntent().getIntExtra(EXTRA_USER_ID, -1);

        if (userId == -1) {
            Toast.makeText(this, "Error: User ID not found. Please sign in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadAvailableExams();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAvailableExams(); // Refresh exam list when returning
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    /**
     * Loads published exams from the database and sets up the RecyclerView.
     */
    private void loadAvailableExams() {
        List<Exam> publishedExams = dbHelper.getPublishedExams();

        if (publishedExams.isEmpty()) {
            Toast.makeText(this, "No published exams available at the moment.", Toast.LENGTH_LONG).show();
            return;
        }

        ExamSelectionAdapter examSelectionAdapter = new ExamSelectionAdapter(publishedExams, this::startQuiz); // Pass a lambda for click listener
        quizCategoryRecyclerView.setAdapter(examSelectionAdapter);
    }

    /**
     * Starts the QuizActivity with the selected exam.
     * @param exam The selected Exam object.
     */
    private void startQuiz(Exam exam) {
        Intent intent = new Intent(ExamSelectionActivity.this, QuizActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_EXAM_ID, exam.getId()); // Pass the exam ID
        intent.putExtra(EXTRA_QUIZ_CATEGORY, exam.getExamName()); // Pass exam name for result tracking
        startActivity(intent);
    }
}