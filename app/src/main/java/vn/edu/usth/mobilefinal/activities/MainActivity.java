package vn.edu.usth.mobilefinal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.usth.mobilefinal.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Your welcome screen XML

        setupButtonListeners();
    }

    private void setupButtonListeners() {
        // Login button - navigate to Login activity
        TextView btnLogin = findViewById(R.id.btn_login);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                }
            });
        }

        // Sign Up button - navigate to Register activity
        TextView btnSignUp = findViewById(R.id.btn_signup);
        if (btnSignUp != null) {
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, Register.class);
                    startActivity(intent);
                }
            });
        }

        // Skip/Continue as guest - navigate to HomeActivity
        TextView tvSkip = findViewById(R.id.tvSkip);
        if (tvSkip != null) {
            tvSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish(); // Close welcome screen since we're going to home
                }
            });
        }
    }
}