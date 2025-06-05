package com.syalux.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.syalux.quizapp.models.Question;
import com.syalux.quizapp.models.QuizResult;

import java.text.SimpleDateFormat;
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
    private RadioGroup radioGroupOptions;
    private RadioButton rbOptionA, rbOptionB, rbOptionC, rbOptionD;

    private List<Question> questionList;
    private int questionIndex = 0;
    private int score = 0;
    private int userId;
    private String quizCategory;

    private QuizHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestion = findViewById(R.id.tvQuestion);
        radioGroupOptions = findViewById(R.id.radioGroupOptions);
        rbOptionA = findViewById(R.id.rbOptionA);
        rbOptionB = findViewById(R.id.rbOptionB);
        rbOptionC = findViewById(R.id.rbOptionC);
        rbOptionD = findViewById(R.id.rbOptionD);
        Button btnSubmit = findViewById(R.id.btnSubmit);

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

        Collections.shuffle(questionList);

        displayQuestion();

        btnSubmit.setOnClickListener(v -> checkAnswer());

        // Modern back press handling
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(QuizActivity.this)
                        .setTitle("Exit Quiz")
                        .setMessage("Are you sure you want to exit the quiz? Your current progress will be lost.")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Finish the activity
                            finish();
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });
    }

    /**
     * Displays the current question on the screen.
     */
    private void displayQuestion() {
        if (questionIndex < questionList.size()) {
            Question currentQuestion = questionList.get(questionIndex);

            tvQuestionNumber.setText(String.format(Locale.getDefault(), "Question %d of %d", questionIndex + 1, questionList.size()));
            tvQuestion.setText(currentQuestion.getQuestionText());
            rbOptionC.setVisibility(View.VISIBLE);
            rbOptionD.setVisibility(View.VISIBLE);

            radioGroupOptions.clearCheck();

            if (currentQuestion.isTrueFalse()) {
                rbOptionC.setVisibility(View.GONE);
                rbOptionD.setVisibility(View.GONE);
                rbOptionA.setText(currentQuestion.getOptionA());
                rbOptionB.setText(currentQuestion.getOptionB());
            } else {
                rbOptionC.setVisibility(View.VISIBLE);
                rbOptionD.setVisibility(View.VISIBLE);
                rbOptionA.setText(currentQuestion.getOptionA());
                rbOptionB.setText(currentQuestion.getOptionB());
                rbOptionC.setText(currentQuestion.getOptionC());
                rbOptionD.setText(currentQuestion.getOptionD());
            }
        } else {
            showQuizResult();
        }
    }

    /**
     * Checks the user's selected answer against the correct answer.
     */
    private void checkAnswer() {
        int selectedRadioButtonId = radioGroupOptions.getCheckedRadioButtonId();

        if (selectedRadioButtonId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        int selectedAnswerIndex = -1;

        if (selectedRadioButton == rbOptionA) {
            selectedAnswerIndex = 0;
        } else if (selectedRadioButton == rbOptionB) {
            selectedAnswerIndex = 1;
        } else if (selectedRadioButton == rbOptionC) {
            selectedAnswerIndex = 2;
        } else if (selectedRadioButton == rbOptionD) {
            selectedAnswerIndex = 3;
        }

        Question currentQuestion = questionList.get(questionIndex);

        if (selectedAnswerIndex == currentQuestion.getCorrectAnswer()) {
            score++;
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong answer. The correct answer was: " + getCorrectOptionText(currentQuestion), Toast.LENGTH_LONG).show();
        }

        questionIndex++;
        displayQuestion();
    }

    /**
     * Helper method to get the text of the correct option.
     * @param question The current question object.
     * @return The text of the correct answer option.
     */
    private String getCorrectOptionText(Question question) {
        switch (question.getCorrectAnswer()) {
            case 0: return question.getOptionA();
            case 1: return question.getOptionB();
            case 2: return question.getOptionC();
            case 3: return question.getOptionD();
            default: return "N/A";
        }
    }

    /**
     * Saves the quiz result to the database and navigates to the QuizResultActivity.
     */
    private void showQuizResult() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String quizDate = sdf.format(new Date());

        QuizResult quizResult = new QuizResult(userId, score, questionList.size(), quizDate, quizCategory);

        dbHelper.createQuizResult(quizResult);

        Intent intent = new Intent(QuizActivity.this, QuizResultActivity.class);
        intent.putExtra(EXTRA_QUIZ_SCORE, score);
        intent.putExtra(EXTRA_TOTAL_QUESTIONS, questionList.size());
        intent.putExtra(EXTRA_QUIZ_CATEGORY, quizCategory);
        startActivity(intent);
        finish();
    }
}