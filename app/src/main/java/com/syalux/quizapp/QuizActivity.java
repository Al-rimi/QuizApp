package com.syalux.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.syalux.quizapp.models.Question;
import com.syalux.quizapp.models.QuizResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.syalux.quizapp.Constants.EXTRA_QUIZ_CATEGORY;
import static com.syalux.quizapp.Constants.EXTRA_QUIZ_SCORE;
import static com.syalux.quizapp.Constants.EXTRA_TOTAL_QUESTIONS;
import static com.syalux.quizapp.Constants.EXTRA_USER_ID;

public class QuizActivity extends AppCompatActivity {

    private TextView tvQuestionNumber, tvQuestion;
    private LinearLayout optionsContainer; // The LinearLayout holding your custom option cards
    private Button btnSubmit; // Declare btnSubmit as a member variable

    private List<Question> questionList;
    private int questionIndex = 0;
    private int score = 0;
    private int userId;
    private String quizCategory;

    private QuizHelper dbHelper;
    private List<RadioButton> radioButtons; // To keep track of all dynamically created RadioButtons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Initialize Views
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestion = findViewById(R.id.tvQuestion);
        optionsContainer = findViewById(R.id.optionsContainer); // Find the LinearLayout container
        btnSubmit = findViewById(R.id.btnSubmit); // Initialize the submit button

        radioButtons = new ArrayList<>(); // Initialize the list to hold dynamically created RadioButtons

        dbHelper = new QuizHelper(this);

        userId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        quizCategory = getIntent().getStringExtra(EXTRA_QUIZ_CATEGORY);

        if (userId == -1 || quizCategory == null || quizCategory.isEmpty()) {
            Toast.makeText(this, "Error: Quiz setup incomplete. Please sign in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        questionList = dbHelper.getQuestionsByCategory(quizCategory);

        if (questionList.isEmpty()) {
            Toast.makeText(this, "No questions available for '" + quizCategory + "' category.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Collections.shuffle(questionList); // Randomize question order

        displayQuestion(); // Display the first question

        btnSubmit.setOnClickListener(v -> checkAnswer()); // Set click listener for submit button

        // Modern back press handling with confirmation dialog
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(QuizActivity.this)
                        .setTitle("Exit Quiz")
                        .setMessage("Are you sure you want to exit the quiz? Your current progress will be lost.")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            finish(); // Finish the activity
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss(); // Dismiss the dialog
                        })
                        .show();
            }
        });
    }

    /**
     * Displays the current question and its options on the screen.
     */
    private void displayQuestion() {
        if (questionIndex < questionList.size()) {
            Question currentQuestion = questionList.get(questionIndex);

            // Update question number and text
            tvQuestionNumber.setText(String.format(Locale.getDefault(), "Question %d of %d", questionIndex + 1, questionList.size()));
            tvQuestion.setText(currentQuestion.getQuestionText());

            // Clear previously added options and the list of RadioButtons
            optionsContainer.removeAllViews();
            radioButtons.clear();

            // Prepare options list
            List<String> options = new ArrayList<>();
            options.add(currentQuestion.getOptionA());
            options.add(currentQuestion.getOptionB());

            // Add options C and D only if it's not a True/False question
            if (!currentQuestion.isTrueFalse()) {
                options.add(currentQuestion.getOptionC());
                options.add(currentQuestion.getOptionD());
            }

            // Dynamically create and add option cards
            for (int i = 0; i < options.size(); i++) {
                String optionText = options.get(i);
                final int optionIndex = i; // Store the current option index for the listener

                // Inflate the custom MaterialCardView layout for each option
                MaterialCardView optionCard = (MaterialCardView) LayoutInflater.from(this)
                        .inflate(R.layout.item_quiz_option, optionsContainer, false);

                // Find the RadioButton within the inflated card
                RadioButton radioButton = optionCard.findViewById(R.id.rbOption);

                // Set text for the radio button
                radioButton.setText(optionText);

                // Assign a unique ID and tag (to store the original index)
                radioButton.setId(View.generateViewId()); // Generates a unique ID for each RadioButton
                radioButton.setTag(optionIndex); // Store the original index (0, 1, 2, 3)

                radioButtons.add(radioButton); // Add to our tracking list

                // Set a click listener on the entire CardView
                optionCard.setOnClickListener(v -> {
                    // Manually manage the checked state: uncheck others, then toggle this one
                    for (RadioButton otherRb : radioButtons) {
                        if (otherRb.getId() != radioButton.getId()) {
                            otherRb.setChecked(false);
                        }
                    }
                    radioButton.setChecked(!radioButton.isChecked()); // Toggle current radio button
                });

                // Also set a listener on the RadioButton itself in case it's interacted with directly
                radioButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    if (isChecked) {
                        // If this radio button is checked, ensure all others are unchecked
                        for (RadioButton otherRb : radioButtons) {
                            if (otherRb.getId() != compoundButton.getId()) {
                                otherRb.setChecked(false);
                            }
                        }
                    }
                });

                // Add the fully configured optionCard to the LinearLayout container
                optionsContainer.addView(optionCard);
            }
        } else {
            // All questions answered, show results
            showQuizResult();
        }
    }

    /**
     * Checks the user's selected answer against the correct answer.
     */
    private void checkAnswer() {
        RadioButton selectedRadioButton = null;
        int selectedAnswerIndex = -1;

        // Iterate through the list of dynamically created radio buttons to find the checked one
        for (RadioButton rb : radioButtons) {
            if (rb.isChecked()) {
                selectedRadioButton = rb;
                selectedAnswerIndex = (int) rb.getTag(); // Retrieve the stored index
                break; // Found the selected one, exit loop
            }
        }

        if (selectedRadioButton == null) { // No option was selected
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        Question currentQuestion = questionList.get(questionIndex);

        // Compare selected answer index with the correct answer
        if (selectedAnswerIndex == currentQuestion.getCorrectAnswer()) {
            score++;
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong answer. The correct answer was: " + getCorrectOptionText(currentQuestion), Toast.LENGTH_LONG).show();
        }

        questionIndex++; // Move to the next question
        displayQuestion(); // Display the next question or show results
    }

    /**
     * Helper method to get the text of the correct option based on its index.
     * @param question The current question object.
     * @return The text of the correct answer option.
     */
    private String getCorrectOptionText(Question question) {
        switch (question.getCorrectAnswer()) {
            case 0: return question.getOptionA();
            case 1: return question.getOptionB();
            case 2: return question.getOptionC();
            case 3: return question.getOptionD();
            default: return "N/A"; // Should not happen if correct answers are within 0-3
        }
    }

    /**
     * Saves the quiz result to the database and navigates to the QuizResultActivity.
     */
    private void showQuizResult() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String quizDate = sdf.format(new Date());

        QuizResult quizResult = new QuizResult(userId, score, questionList.size(), quizDate, quizCategory);

        dbHelper.createQuizResult(quizResult); // Save result to database

        // Navigate to QuizResultActivity
        Intent intent = new Intent(QuizActivity.this, QuizResultActivity.class);
        intent.putExtra(EXTRA_QUIZ_SCORE, score);
        intent.putExtra(EXTRA_TOTAL_QUESTIONS, questionList.size());
        intent.putExtra(EXTRA_QUIZ_CATEGORY, quizCategory);
        startActivity(intent);
        finish(); // Finish QuizActivity
    }
}