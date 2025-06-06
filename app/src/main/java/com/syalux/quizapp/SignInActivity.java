package com.syalux.quizapp;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.syalux.quizapp.models.User;

import static com.syalux.quizapp.Constants.EXTRA_USER_ID;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    private TextInputLayout tilStudentId, tilPhoneNumber;
    private TextInputEditText etStudentId, etPhoneNumber;
    private QuizHelper dbHelper;
    private BatteryStateReceiver batteryStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        tilStudentId = findViewById(R.id.tilStudentId);
        etStudentId = findViewById(R.id.etStudentId);
        tilPhoneNumber = findViewById(R.id.tilPhoneNumber);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        Button btnSignIn = findViewById(R.id.btnSignIn);

        dbHelper = new QuizHelper(this);

        btnSignIn.setOnClickListener(v -> signInUser());

        batteryStateReceiver = new BatteryStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        filter.addAction(Intent.ACTION_BATTERY_OKAY);

        registerReceiver(batteryStateReceiver, filter);
        Log.d(TAG, "BatteryStateReceiver registered dynamically.");
    }

    /**
     * Unregister the BatteryStateReceiver when the activity is destroyed
     * to prevent memory leaks and ensure it doesn't receive broadcasts when not needed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (batteryStateReceiver != null) {
            unregisterReceiver(batteryStateReceiver);
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    /**
     * Handles the sign-in or registration process for the user.
     * Validates input, checks for existing user, creates new user if necessary,
     * and navigates to the ExamSelectionActivity.
     */
    private void signInUser() {

        String studentId = Objects.requireNonNull(etStudentId.getText()).toString().trim();
        String phoneNumber = Objects.requireNonNull(etPhoneNumber.getText()).toString().trim();

        if (TextUtils.isEmpty(studentId)) {
            tilStudentId.setError("Student ID cannot be empty");
            return;
        } else {
            tilStudentId.setError(null);
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            tilPhoneNumber.setError("Phone number cannot be empty");
            return;
        } else {
            tilPhoneNumber.setError(null);
        }

        User existingUser = dbHelper.getUserByStudentId(studentId);
        int currentUserId;

        if (existingUser != null) {
            if (existingUser.getPhoneNumber().equals(phoneNumber)) {
                currentUserId = existingUser.getId();
                Toast.makeText(this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "User " + studentId + " signed in. User ID: " + currentUserId);
            } else {
                tilPhoneNumber.setError("Phone number does not match for this Student ID.");
                Toast.makeText(this, "Phone number does not match for this Student ID.", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Sign-in failed: Phone number mismatch for Student ID: " + studentId);
                return;
            }
        } else {
            User newUser = new User(studentId, phoneNumber);
            long newRowId = dbHelper.createUser(newUser);

            if (newRowId != -1) {
                currentUserId = (int) newRowId;
                Toast.makeText(this, "New user registered and signed in!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "New user " + studentId + " registered with ID: " + currentUserId);
            } else {
                Toast.makeText(this, "Failed to register user. Please try again.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to create new user: " + studentId);
                return;
            }
        }

        System.out.println("gg");

        Intent intent = new Intent(SignInActivity.this, ExamSelectionActivity.class);
        intent.putExtra(EXTRA_USER_ID, currentUserId);
        startActivity(intent);
        finish();
    }
}
