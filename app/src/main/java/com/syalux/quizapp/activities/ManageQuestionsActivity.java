package com.syalux.quizapp.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.syalux.quizapp.QuizHelper;
import com.syalux.quizapp.R;
import com.syalux.quizapp.models.Exam;
import com.syalux.quizapp.models.Question;
import com.syalux.quizapp.utilities.QuestionManagementAdapter; // New adapter

import java.util.List;
import java.util.Objects;

import static com.syalux.quizapp.Constants.EXTRA_EXAM_ID;

public class ManageQuestionsActivity extends AppCompatActivity implements QuestionManagementAdapter.OnQuestionActionListener {

    private RecyclerView questionsRecyclerView;
    private QuizHelper dbHelper;
    private int examId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_questions); // Create this layout

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        questionsRecyclerView = findViewById(R.id.questionsRecyclerView);
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new QuizHelper(this);
        examId = getIntent().getIntExtra(EXTRA_EXAM_ID, -1);

        if (examId == -1) {
            Toast.makeText(this, "Error: Exam ID not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Exam currentExam = dbHelper.getExamById(examId);
        if (currentExam != null) {
            getSupportActionBar().setTitle("Questions for: " + currentExam.getExamName());
        } else {
            getSupportActionBar().setTitle("Manage Questions");
        }

        loadQuestions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadQuestions(); // Refresh questions when returning
    }

    private void loadQuestions() {
        List<Question> questions = dbHelper.getQuestionsByExamId(examId);
        QuestionManagementAdapter questionAdapter = new QuestionManagementAdapter(questions, this);
        questionsRecyclerView.setAdapter(questionAdapter);

        // Update the exam's question count in the database
        dbHelper.updateExamQuestionCount(dbHelper.getWritableDatabase(), examId, questions.size());

        if (questions.isEmpty()) {
            Toast.makeText(this, "No questions added yet. Click '+' to add one.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manage_questions, menu); // Create this menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add_question) {
            showAddEditQuestionDialog(null); // Pass null for adding a new question
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditQuestion(Question question) {
        showAddEditQuestionDialog(question);
    }

    @Override
    public void onDeleteQuestion(Question question) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Question")
                .setMessage("Are you sure you want to delete this question?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    int rowsAffected = dbHelper.deleteQuestion(question.getId());
                    if (rowsAffected > 0) {
                        Toast.makeText(this, "Question deleted.", Toast.LENGTH_SHORT).show();
                        loadQuestions(); // Refresh the list
                    } else {
                        Toast.makeText(this, "Failed to delete question.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showAddEditQuestionDialog(final Question questionToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_edit_question, null); // Create this layout
        builder.setView(dialogView);

        final EditText etQuestionText = dialogView.findViewById(R.id.etQuestionText);
        final EditText etOptionA = dialogView.findViewById(R.id.etOptionA);
        final EditText etOptionB = dialogView.findViewById(R.id.etOptionB);
        final EditText etOptionC = dialogView.findViewById(R.id.etOptionC);
        final EditText etOptionD = dialogView.findViewById(R.id.etOptionD);
        final RadioGroup rgCorrectAnswer = dialogView.findViewById(R.id.rgCorrectAnswer);
        final RadioButton rbCorrectA = dialogView.findViewById(R.id.rbCorrectA);
        final RadioButton rbCorrectB = dialogView.findViewById(R.id.rbCorrectB);
        final RadioButton rbCorrectC = dialogView.findViewById(R.id.rbCorrectC);
        final RadioButton rbCorrectD = dialogView.findViewById(R.id.rbCorrectD);
        final CheckBox cbIsTrueFalse = dialogView.findViewById(R.id.cbIsTrueFalse);
        final TextView tvOptionsLabel = dialogView.findViewById(R.id.tvOptionsLabel); // Add this TextView for better UX

        // Initially hide C and D options and their radio buttons
        etOptionC.setVisibility(View.VISIBLE);
        etOptionD.setVisibility(View.VISIBLE);
        rbCorrectC.setVisibility(View.VISIBLE);
        rbCorrectD.setVisibility(View.VISIBLE);
        tvOptionsLabel.setVisibility(View.VISIBLE);

        cbIsTrueFalse.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etOptionC.setVisibility(View.GONE);
                etOptionD.setVisibility(View.GONE);
                rbCorrectC.setVisibility(View.GONE);
                rbCorrectD.setVisibility(View.GONE);
                tvOptionsLabel.setVisibility(View.GONE);
                // Ensure only A or B can be selected for true/false
                if (rgCorrectAnswer.getCheckedRadioButtonId() == R.id.rbCorrectC || rgCorrectAnswer.getCheckedRadioButtonId() == R.id.rbCorrectD) {
                    rgCorrectAnswer.clearCheck();
                }
            } else {
                etOptionC.setVisibility(View.VISIBLE);
                etOptionD.setVisibility(View.VISIBLE);
                rbCorrectC.setVisibility(View.VISIBLE);
                rbCorrectD.setVisibility(View.VISIBLE);
                tvOptionsLabel.setVisibility(View.VISIBLE);
            }
        });


        if (questionToEdit != null) {
            builder.setTitle("Edit Question");
            etQuestionText.setText(questionToEdit.getQuestionText());
            etOptionA.setText(questionToEdit.getOptionA());
            etOptionB.setText(questionToEdit.getOptionB());
            etOptionC.setText(questionToEdit.getOptionC());
            etOptionD.setText(questionToEdit.getOptionD());
            cbIsTrueFalse.setChecked(questionToEdit.isTrueFalse());

            // Set the correct answer radio button
            switch (questionToEdit.getCorrectAnswer()) {
                case 0: rgCorrectAnswer.check(R.id.rbCorrectA); break;
                case 1: rgCorrectAnswer.check(R.id.rbCorrectB); break;
                case 2: rgCorrectAnswer.check(R.id.rbCorrectC); break;
                case 3: rgCorrectAnswer.check(R.id.rbCorrectD); break;
            }
            // Trigger the listener once if it's true/false question to adjust visibility
            if (questionToEdit.isTrueFalse()) {
                etOptionC.setVisibility(View.GONE);
                etOptionD.setVisibility(View.GONE);
                rbCorrectC.setVisibility(View.GONE);
                rbCorrectD.setVisibility(View.GONE);
                tvOptionsLabel.setVisibility(View.GONE);
            }

        } else {
            builder.setTitle("Add New Question");
        }

        builder.setPositiveButton("Save", null); // Set null initially to keep dialog open on invalid input
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String questionText = etQuestionText.getText().toString().trim();
                String optionA = etOptionA.getText().toString().trim();
                String optionB = etOptionB.getText().toString().trim();
                String optionC = etOptionC.getText().toString().trim();
                String optionD = etOptionD.getText().toString().trim();
                boolean isTrueFalse = cbIsTrueFalse.isChecked();

                if (TextUtils.isEmpty(questionText) || TextUtils.isEmpty(optionA) || TextUtils.isEmpty(optionB)) {
                    Toast.makeText(this, "Please fill in question and at least two options.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isTrueFalse && (TextUtils.isEmpty(optionC) || TextUtils.isEmpty(optionD))) {
                    Toast.makeText(this, "Please fill in all four options for MCQ.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int correctAnswer = -1;
                int checkedRadioButtonId = rgCorrectAnswer.getCheckedRadioButtonId();
                if (checkedRadioButtonId == R.id.rbCorrectA) {
                    correctAnswer = 0;
                } else if (checkedRadioButtonId == R.id.rbCorrectB) {
                    correctAnswer = 1;
                } else if (checkedRadioButtonId == R.id.rbCorrectC) {
                    correctAnswer = 2;
                } else if (checkedRadioButtonId == R.id.rbCorrectD) {
                    correctAnswer = 3;
                }

                if (correctAnswer == -1 || (isTrueFalse && correctAnswer > 1)) {
                    Toast.makeText(this, "Please select the correct answer.", Toast.LENGTH_SHORT).show();
                    return;
                }


                Question question;
                if (questionToEdit == null) {
                    // Create new question
                    question = new Question(questionText, optionA, optionB, optionC, optionD, correctAnswer, isTrueFalse, examId);
                    long newQuestionId = dbHelper.createQuestion(dbHelper.getWritableDatabase(), question);
                    if (newQuestionId != -1) {
                        Toast.makeText(this, "Question added!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadQuestions();
                    } else {
                        Toast.makeText(this, "Failed to add question.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Update existing question
                    question = questionToEdit; // Use the existing object to preserve its ID
                    question.setQuestionText(questionText);
                    question.setOptionA(optionA);
                    question.setOptionB(optionB);
                    question.setOptionC(optionC);
                    question.setOptionD(optionD);
                    question.setCorrectAnswer(correctAnswer);
                    question.setTrueFalse(isTrueFalse);

                    int rowsAffected = dbHelper.updateQuestion(question);
                    if (rowsAffected > 0) {
                        Toast.makeText(this, "Question updated!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadQuestions();
                    } else {
                        Toast.makeText(this, "Failed to update question.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}