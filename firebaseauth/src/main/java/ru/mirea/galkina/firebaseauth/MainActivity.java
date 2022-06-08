package ru.mirea.galkina.firebaseauth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText emailEditText;
    private EditText passwordTextEdit;
    private TextView statusTextView;
    private FirebaseAuth auth;

    private Button signInBtn;
    private Button signOutBtn;
    private Button createAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.editEmail);
        passwordTextEdit = findViewById(R.id.editPassword);
        statusTextView = findViewById(R.id.signView);

        findViewById(R.id.btnSign).setOnClickListener(this);
        findViewById(R.id.createAccountbtn).setOnClickListener(this);
        findViewById(R.id.sgnoutbtn).setOnClickListener(this);
        auth = FirebaseAuth.getInstance();

    }
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.createAccountbtn) {
            createAccount(emailEditText.getText().toString(),
                    passwordTextEdit.getText().toString());
        } else if (i == R.id.btnSign) {
            signIn(emailEditText.getText().toString(),
                    passwordTextEdit.getText().toString());
        } else if (i == R.id.sgnoutbtn){
            signOut();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            statusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            findViewById(R.id.editEmail).setVisibility(View.GONE);
            findViewById(R.id.editPassword).setVisibility(View.GONE);
            findViewById(R.id.btnSign).setVisibility(View.GONE);
            findViewById(R.id.createAccountbtn).setVisibility(View.GONE);
            findViewById(R.id.sgnoutbtn).setVisibility(View.VISIBLE);

        } else {
            statusTextView.setText(R.string.signed_out);
            findViewById(R.id.editPassword).setVisibility(View.VISIBLE);
            findViewById(R.id.editEmail).setVisibility(View.VISIBLE);
            findViewById(R.id.btnSign).setVisibility(View.VISIBLE);
            findViewById(R.id.createAccountbtn).setVisibility(View.VISIBLE);
            findViewById(R.id.sgnoutbtn).setVisibility(View.VISIBLE);
        }
    }

    private boolean validateForm() {
        boolean valid = true;
        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Required.");
            valid = false;
        } else {
            emailEditText.setError(null);
        }
        String password = passwordTextEdit.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordTextEdit.setError("Required.");
            valid = false;
        } else {
            passwordTextEdit.setError(null);
        }
        return valid;
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void signOut() {
        auth.signOut();
        updateUI(null);
    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                    if (!task.isSuccessful()) {
                        statusTextView.setText(R.string.auth_failed);
                    }
                });
    }
}