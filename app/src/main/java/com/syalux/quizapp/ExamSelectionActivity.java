package com.syalux.quizapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import java.util.List;

import static com.syalux.quizapp.Constants.EXTRA_QUIZ_CATEGORY;
import static com.syalux.quizapp.Constants.EXTRA_USER_ID;

public class ExamSelectionActivity extends AppCompatActivity {

    private LinearLayout quizCategoryContainer;
    private QuizHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_selection);

        quizCategoryContainer = findViewById(R.id.quizCategoryContainer);

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
     * Loads quiz categories from the database and dynamically creates UI elements for each.
     */
    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceType"})
    private void loadQuizCategories() {
        List<String> categories = dbHelper.getAllQuizCategories();

        if (categories.isEmpty()) {
            Toast.makeText(this, "No quiz categories available. Please add questions to the database.", Toast.LENGTH_LONG).show();
            // Optionally, you might want to finish the activity or go back to sign-in
            // finish();
            return;
        }

        quizCategoryContainer.removeAllViews();

        for (String category : categories) {
            CardView cardView = new CardView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(16, 16, 16, 0);
            cardView.setLayoutParams(params);
            cardView.setRadius(16);
            cardView.setCardElevation(8);
            cardView.setClickable(true);
            cardView.setFocusable(true);
            cardView.setForeground(ContextCompat.getDrawable(this, android.R.attr.selectableItemBackground));


            Button categoryButton = new Button(this);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            categoryButton.setLayoutParams(buttonParams);
            categoryButton.setText(category);
            categoryButton.setTextSize(20);
            categoryButton.setPadding(32, 32, 32, 32);
            categoryButton.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            categoryButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            categoryButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

            categoryButton.setOnClickListener(v -> startQuiz(category));

            cardView.addView(categoryButton);
            quizCategoryContainer.addView(cardView);
        }
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
