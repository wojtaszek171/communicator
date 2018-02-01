package com.example.pawel.communicator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login = findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pseudonym = ((EditText) findViewById(R.id.pseudonym)).getText().toString();
                final String password = ((EditText) findViewById(R.id.pass)).getText().toString();
                if (pseudonym != null && password != null) {
                                    ParseUser.logInInBackground(pseudonym, password, new LogInCallback() {
                                        @Override
                                        public void done(ParseUser user, ParseException e) {
                                            if (user != null) {
                                                // Hooray! The user is logged in.
                                                Toast.makeText(LoginActivity.this, "Pomyślnie zalogowano", Toast.LENGTH_SHORT).show();
                                                finish();
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            } else {
                                                // Signup failed. Look at the ParseException to see what happened.
                                                // Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                switch (e.getMessage()){
                                                    case "password is required.":
                                                        Toast.makeText(getApplicationContext(), "Hasło jest wymagane", Toast.LENGTH_SHORT).show();
                                                        break;
                                                    case "Invalid username/password.":
                                                        Toast.makeText(getApplicationContext(), "Błędna nazwa użytkownika/hasło", Toast.LENGTH_SHORT).show();

                                                }

                                            }
                                        }
                                    });

                }
            }
        });
    }
}
