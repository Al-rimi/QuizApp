package com.syalux.quizapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.syalux.quizapp.QuizHelper;
import com.syalux.quizapp.R;
import com.syalux.quizapp.models.User;

import static com.syalux.quizapp.Constants.EXTRA_USER_ID;
import static com.syalux.quizapp.Constants.USER_TYPE_STUDENT;
import static com.syalux.quizapp.Constants.USER_TYPE_TEACHER;

public class SignInActivity extends AppCompatActivity {

    private EditText etStudentId, etPhoneNumber;
    private RadioGroup rgUserType;
    private QuizHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        etStudentId = findViewById(R.id.etStudentId);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        rgUserType = findViewById(R.id.rgUserType);
        Button btnSignInRegister = findViewById(R.id.btnSignInRegister);

        dbHelper = new QuizHelper(this);

        btnSignInRegister.setOnClickListener(v -> handleSignInRegister());
    }

    private void handleSignInRegister() {
        String studentId = etStudentId.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String userType;

        int selectedId = rgUserType.getCheckedRadioButtonId();
        if (selectedId == R.id.rbStudent) {
            userType = USER_TYPE_STUDENT;
        } else if (selectedId == R.id.rbTeacher) {
            userType = USER_TYPE_TEACHER;
        } else {
            Toast.makeText(this, "Please select user type (Student/Teacher)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(studentId) || TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Please enter Student ID and Phone Number", Toast.LENGTH_SHORT).show();
            return;
        }

        User existingUser = dbHelper.getUserByStudentId(studentId);

        if (existingUser == null) {
            // Register new user
            User newUser = new User(studentId, phoneNumber, userType);
            long userId = dbHelper.createUser(newUser);
            if (userId != -1) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                redirectUser((int) userId, userType);
            } else {
                Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Sign in existing user
            if (existingUser.getPhoneNumber().equals(phoneNumber) && existingUser.getUserType().equals(userType)) {
                Toast.makeText(this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                redirectUser(existingUser.getId(), existingUser.getUserType());
            } else {
                Toast.makeText(this, "Invalid credentials or user type.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void redirectUser(int userId, String userType) {
        if (USER_TYPE_STUDENT.equals(userType)) {
            Intent intent = new Intent(SignInActivity.this, ExamSelectionActivity.class);
            intent.putExtra(EXTRA_USER_ID, userId);
            startActivity(intent);
        } else if (USER_TYPE_TEACHER.equals(userType)) {
            Intent intent = new Intent(SignInActivity.this, TeacherDashboardActivity.class);
            intent.putExtra(EXTRA_USER_ID, userId);
            startActivity(intent);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}