package com.syalux.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import static com.syalux.quizapp.Constants.EXTRA_QUIZ_CATEGORY;
import static com.syalux.quizapp.Constants.EXTRA_USER_ID;

public class ExamSelectionActivity extends AppCompatActivity {

    private RecyclerView quizCategoryRecyclerView;
    private QuizHelper dbHelper;
    private int userId;
    private ExamCategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_selection);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Set the toolbar as the ActionBar

        quizCategoryRecyclerView = findViewById(R.id.quizCategoryRecyclerView);
        quizCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new QuizHelper(this);

        userId = getIntent().getIntExtra(EXTRA_USER_ID, -1);

        if (userId == -1) {
            Toast.makeText(this, "Error: User ID not found. Please sign in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadQuizCategories();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    /**
     * Loads quiz categories from the database and sets up the RecyclerView.
     */
    private void loadQuizCategories() {
        List<String> categories = dbHelper.getAllQuizCategories();

        if (categories.isEmpty()) {
            Toast.makeText(this, "No quiz categories available. Please add questions to the database.", Toast.LENGTH_LONG).show();
            return;
        }

        categoryAdapter = new ExamCategoryAdapter(categories, this::startQuiz); // Pass a lambda for click listener
        quizCategoryRecyclerView.setAdapter(categoryAdapter);
    }

    /**
     * Starts the QuizActivity with the selected quiz category and user ID.
     * @param category The selected quiz category.
     */
    private void startQuiz(String category) {
        Intent intent = new Intent(ExamSelectionActivity.this, QuizActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_QUIZ_CATEGORY, category);
        startActivity(intent);
    }
}