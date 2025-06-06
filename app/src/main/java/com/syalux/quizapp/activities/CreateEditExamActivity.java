package com.syalux.quizapp.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.syalux.quizapp.QuizHelper;
import com.syalux.quizapp.R;
import com.syalux.quizapp.models.Exam;

import static com.syalux.quizapp.Constants.EXTRA_EXAM_ID;
import static com.syalux.quizapp.Constants.EXTRA_USER_ID;

import java.util.Objects;

public class CreateEditExamActivity extends AppCompatActivity {

    private EditText etExamName;
    private CheckBox cbPublished;

    private QuizHelper dbHelper;
    private int teacherId;
    private int examId = -1; // -1 for new exam, otherwise existing exam ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_exam);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        etExamName = findViewById(R.id.etExamName);
        cbPublished = findViewById(R.id.cbPublished);
        Button btnSaveExam = findViewById(R.id.btnSaveExam);

        dbHelper = new QuizHelper(this);
        teacherId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        examId = getIntent().getIntExtra(EXTRA_EXAM_ID, -1);

        if (teacherId == -1) {
            Toast.makeText(this, "Error: Teacher ID not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (examId != -1) {
            // Editing an existing exam
            getSupportActionBar().setTitle("Edit Exam");
            loadExamDetails(examId);
        } else {
            // Creating a new exam
            getSupportActionBar().setTitle("Create New Exam");
        }

        btnSaveExam.setOnClickListener(v -> saveExam());
    }

    private void loadExamDetails(int id) {
        Exam exam = dbHelper.getExamById(id);
        if (exam != null) {
            etExamName.setText(exam.getExamName());
            cbPublished.setChecked(exam.isPublished());
        } else {
            Toast.makeText(this, "Error loading exam details.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveExam() {
        String examName = etExamName.getText().toString().trim();
        boolean isPublished = cbPublished.isChecked();

        if (TextUtils.isEmpty(examName)) {
            Toast.makeText(this, "Please enter exam name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (examId == -1) {
            // Create new exam
            Exam newExam = new Exam(examName, teacherId, 0, isPublished); // Initial question count is 0
            long newExamId = dbHelper.createExam(dbHelper.getWritableDatabase(), newExam); // Pass writable DB
            if (newExamId != -1) {
                Toast.makeText(this, "Exam created successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to create exam.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Update existing exam
            Exam existingExam = dbHelper.getExamById(examId);
            if (existingExam != null) {
                existingExam.setExamName(examName);
                existingExam.setPublished(isPublished);
                int rowsAffected = dbHelper.updateExam(existingExam);
                if (rowsAffected > 0) {
                    Toast.makeText(this, "Exam updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update exam.", Toast.LENGTH_SHORT).show();
                }
            }
        }
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