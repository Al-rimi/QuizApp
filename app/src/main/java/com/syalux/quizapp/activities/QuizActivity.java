package com.syalux.quizapp.activities;

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
import androidx.core.view.WindowCompat;

import com.google.android.material.card.MaterialCardView;
import com.syalux.quizapp.QuizHelper;
import com.syalux.quizapp.R;
import com.syalux.quizapp.models.Exam; // Import Exam model
import com.syalux.quizapp.models.Question;
import com.syalux.quizapp.models.QuizResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.syalux.quizapp.Constants.EXTRA_EXAM_ID; // New extra
import static com.syalux.quizapp.Constants.EXTRA_QUIZ_CATEGORY;
import static com.syalux.quizapp.Constants.EXTRA_QUIZ_SCORE;
import static com.syalux.quizapp.Constants.EXTRA_TOTAL_QUESTIONS;
import static com.syalux.quizapp.Constants.EXTRA_USER_ID;

public class QuizActivity extends AppCompatActivity {

    private TextView tvQuestionNumber, tvQuestion;
    private LinearLayout optionsContainer;
    private Button btnSubmit;

    private List<Question> questionList;
    private int questionIndex = 0;
    private int score = 0;
    private int userId;
    private int examId; // Store exam ID
    private String examName; // Store exam name for quiz result category

    private QuizHelper dbHelper;
    private List<RadioButton> radioButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Initialize Views
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestion = findViewById(R.id.tvQuestion);
        optionsContainer = findViewById(R.id.optionsContainer);
        btnSubmit = findViewById(R.id.btnSubmit);

        radioButtons = new ArrayList<>();

        dbHelper = new QuizHelper(this);

        userId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        examId = getIntent().getIntExtra(EXTRA_EXAM_ID, -1); // Get exam ID
        examName = getIntent().getStringExtra(EXTRA_QUIZ_CATEGORY); // Get exam name for result

        if (userId == -1 || examId == -1 || examName == null || examName.isEmpty()) {
            Toast.makeText(this, "Error: Quiz setup incomplete. Please select an exam again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        questionList = dbHelper.getQuestionsByExamId(examId); // Get questions by exam ID

        if (questionList.isEmpty()) {
            Toast.makeText(this, "No questions available for '" + examName + "'.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Collections.shuffle(questionList);

        displayQuestion();

        btnSubmit.setOnClickListener(v -> checkAnswer());

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

    private void displayQuestion() {
        if (questionIndex < questionList.size()) {
            Question currentQuestion = questionList.get(questionIndex);

            tvQuestionNumber.setText(String.format(Locale.getDefault(), "Question %d of %d", questionIndex + 1, questionList.size()));
            tvQuestion.setText(currentQuestion.getQuestionText());

            optionsContainer.removeAllViews();
            radioButtons.clear();

            List<String> options = new ArrayList<>();
            options.add(currentQuestion.getOptionA());
            options.add(currentQuestion.getOptionB());

            if (!currentQuestion.isTrueFalse()) {
                options.add(currentQuestion.getOptionC());
                options.add(currentQuestion.getOptionD());
            }

            for (int i = 0; i < options.size(); i++) {
                String optionText = options.get(i);
                final int optionIndex = i;

                MaterialCardView optionCard = (MaterialCardView) LayoutInflater.from(this)
                        .inflate(R.layout.item_quiz_option, optionsContainer, false);

                RadioButton radioButton = optionCard.findViewById(R.id.rbOption);

                radioButton.setText(optionText);

                radioButton.setId(View.generateViewId());
                radioButton.setTag(optionIndex);

                radioButtons.add(radioButton);

                optionCard.setOnClickListener(v -> {
                    for (RadioButton otherRb : radioButtons) {
                        if (otherRb.getId() != radioButton.getId()) {
                            otherRb.setChecked(false);
                        }
                    }
                    radioButton.setChecked(!radioButton.isChecked());
                });

                radioButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    if (isChecked) {
                        for (RadioButton otherRb : radioButtons) {
                            if (otherRb.getId() != compoundButton.getId()) {
                                otherRb.setChecked(false);
                            }
                        }
                    }
                });

                optionsContainer.addView(optionCard);
            }
        } else {
            showQuizResult();
        }
    }

    private void checkAnswer() {
        RadioButton selectedRadioButton = null;
        int selectedAnswerIndex = -1;

        for (RadioButton rb : radioButtons) {
            if (rb.isChecked()) {
                selectedRadioButton = rb;
                selectedAnswerIndex = (int) rb.getTag();
                break;
            }
        }

        if (selectedRadioButton == null) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
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

    private String getCorrectOptionText(Question question) {
        switch (question.getCorrectAnswer()) {
            case 0: return question.getOptionA();
            case 1: return question.getOptionB();
            case 2: return question.getOptionC();
            case 3: return question.getOptionD();
            default: return "N/A";
        }
    }

    private void showQuizResult() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String quizDate = sdf.format(new Date());

        // Use examName for quizCategory in QuizResult
        QuizResult quizResult = new QuizResult(userId, score, questionList.size(), quizDate, examName);

        dbHelper.createQuizResult(quizResult);

        // Navigate to QuizResultActivity
        Intent intent = new Intent(QuizActivity.this, QuizResultActivity.class);
        intent.putExtra(EXTRA_QUIZ_SCORE, score);
        intent.putExtra(EXTRA_TOTAL_QUESTIONS, questionList.size());
        intent.putExtra(EXTRA_QUIZ_CATEGORY, examName); // Pass exam name for display in result
        startActivity(intent);
        finish(); // Finish QuizActivity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}