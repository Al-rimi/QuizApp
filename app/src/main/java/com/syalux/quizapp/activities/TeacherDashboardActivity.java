package com.syalux.quizapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.syalux.quizapp.QuizHelper;
import com.syalux.quizapp.R;
import com.syalux.quizapp.models.Exam;
import com.syalux.quizapp.utilities.ExamManagementAdapter; // New adapter

import java.util.List;
import java.util.Objects;

import static com.syalux.quizapp.Constants.EXTRA_EXAM_ID;
import static com.syalux.quizapp.Constants.EXTRA_USER_ID;

public class TeacherDashboardActivity extends AppCompatActivity implements ExamManagementAdapter.OnExamActionListener {

    private RecyclerView teacherExamsRecyclerView;
    private QuizHelper dbHelper;
    private int teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard); // Create this layout

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Teacher Dashboard");

        teacherExamsRecyclerView = findViewById(R.id.teacherExamsRecyclerView);
        teacherExamsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new QuizHelper(this);
        teacherId = getIntent().getIntExtra(EXTRA_USER_ID, -1);

        if (teacherId == -1) {
            Toast.makeText(this, "Error: Teacher ID not found. Please sign in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadTeacherExams();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTeacherExams(); // Refresh exams when returning to this activity
    }

    private void loadTeacherExams() {
        List<Exam> exams = dbHelper.getExamsByTeacherId(teacherId);
        ExamManagementAdapter examAdapter = new ExamManagementAdapter(exams, this); // 'this' refers to OnExamActionListener
        teacherExamsRecyclerView.setAdapter(examAdapter);

        if (exams.isEmpty()) {
            Toast.makeText(this, "No exams created yet. Click '+' to create one.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teacher_dashboard, menu); // Create this menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add_exam) {
            Intent intent = new Intent(TeacherDashboardActivity.this, CreateEditExamActivity.class);
            intent.putExtra(EXTRA_USER_ID, teacherId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditExam(Exam exam) {
        Intent intent = new Intent(TeacherDashboardActivity.this, CreateEditExamActivity.class);
        intent.putExtra(EXTRA_USER_ID, teacherId);
        intent.putExtra(EXTRA_EXAM_ID, exam.getId()); // Pass exam ID for editing
        startActivity(intent);
    }

    @Override
    public void onManageQuestions(Exam exam) {
        Intent intent = new Intent(TeacherDashboardActivity.this, ManageQuestionsActivity.class);
        intent.putExtra(EXTRA_EXAM_ID, exam.getId());
        intent.putExtra(EXTRA_USER_ID, teacherId); // Pass teacher ID as well if needed in ManageQuestions
        startActivity(intent);
    }

    @Override
    public void onTogglePublish(Exam exam) {
        // Toggle the published status
        exam.setPublished(!exam.isPublished());
        dbHelper.updateExam(exam);
        Toast.makeText(this, exam.getExamName() + (exam.isPublished() ? " published." : " unpublished."), Toast.LENGTH_SHORT).show();
        loadTeacherExams(); // Refresh the list
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}