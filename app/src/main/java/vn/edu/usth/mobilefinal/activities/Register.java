package vn.edu.usth.mobilefinal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import vn.edu.usth.mobilefinal.R;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, confirmPasswordText;
    TextInputLayout emailLayout, passwordLayout;
    Button buttonSig;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonSig = findViewById(R.id.btn_signup);
        progressBar = findViewById(R.id.progressBar);
        textView =findViewById(R.id.loginNow);
        confirmPasswordText = findViewById(R.id.confirm_password);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();

            }
        });


        buttonSig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, confirmPassword;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                confirmPassword = String.valueOf(confirmPasswordText.getText());

                if (TextUtils.isEmpty(password)) {
                    passwordLayout.setError("Password can't be empty");
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (password.length() < 6) {
                    passwordLayout.setError("Password must be at least 6 characters");
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (!password.equals(confirmPassword)) {
                    passwordLayout.setError("Passwords do not match");
                    progressBar.setVisibility(View.GONE);
                    return;
                } else {
                    passwordLayout.setError(null);
                }
                if (TextUtils.isEmpty(email)) {
                    emailLayout.setError("Email can't be empty");
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailLayout.setError("Invalid email");
                    progressBar.setVisibility(View.GONE);
                    return;
                } else {
                    emailLayout.setError(null);
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Account created.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Register.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

}