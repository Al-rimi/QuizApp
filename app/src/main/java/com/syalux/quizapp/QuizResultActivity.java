package com.syalux.quizapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static com.syalux.quizapp.Constants.EXTRA_QUIZ_CATEGORY;
import static com.syalux.quizapp.Constants.EXTRA_QUIZ_SCORE;
import static com.syalux.quizapp.Constants.EXTRA_TOTAL_QUESTIONS;

public class QuizResultActivity extends AppCompatActivity {

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        TextView tvResultCategory = findViewById(R.id.tvResultCategory);
        TextView tvResultScore = findViewById(R.id.tvResultScore);
        Button btnFinish = findViewById(R.id.btnFinish);

        int score = getIntent().getIntExtra(EXTRA_QUIZ_SCORE, 0);
        int totalQuestions = getIntent().getIntExtra(EXTRA_TOTAL_QUESTIONS, 0);
        String category = getIntent().getStringExtra(EXTRA_QUIZ_CATEGORY);

        tvResultCategory.setText("Category: " + category);
        tvResultScore.setText(String.format("Your Score: %d / %d", score, totalQuestions));

        btnFinish.setOnClickListener(v -> finish());
    }

    /**
     * This activity shows results and is typically the end of the quiz flow.
     * When the user presses back, it should just finish and go to the previous screen (ExamSelectionActivity).
     * No confirmation dialog is needed here.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
