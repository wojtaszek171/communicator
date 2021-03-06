package com.example.pawel.communicator.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pawel.communicator.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Zarejestruj się");
        Button register = findViewById(R.id.register_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText) findViewById(R.id.email)).getText().toString();
                String pseudonym = ((EditText) findViewById(R.id.pseudonym)).getText().toString();
                String password = ((EditText) findViewById(R.id.pass)).getText().toString();
                String password_2 = ((EditText) findViewById(R.id.pass_repeat)).getText().toString();
                if(email != null && pseudonym != null && password !=null && password_2 !=null){
                    final ParseUser user = new ParseUser();
                    user.setUsername(pseudonym);
                    user.setEmail(email);
                    user.setPassword(password);
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                final ParseObject userData = new ParseObject("UserData");
                                userData.saveEventually(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        user.put("UserData", userData);
                                        user.saveEventually(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                                Toast.makeText(getApplicationContext(), "Zarejestrowano pomyślnie!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });


                            } else {
                                Toast.makeText(getApplicationContext(), "Coś poszło nie tak...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
