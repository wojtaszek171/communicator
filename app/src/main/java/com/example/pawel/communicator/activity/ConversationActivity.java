package com.example.pawel.communicator.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.pawel.communicator.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ConversationActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private MessagesListAdapter mMessageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Rozmowa");
        final String conId = getIntent().getExtras().getString("conversationId");
        ParseQuery query = new ParseQuery("message");
        query.whereEqualTo("belongsTo",conId);
        query.findInBackground(new FindCallback() {

            @Override
            public void done(Object o, Throwable throwable) {

            }

            @Override
            public void done(List objects, ParseException e) {
                mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
                mMessageAdapter = new MessagesListAdapter(ConversationActivity.this, objects);
                mMessageRecycler.setLayoutManager(new LinearLayoutManager(ConversationActivity.this));
            }
        });

        final EditText message = findViewById(R.id.edittext_chatbox);
        Button sendMessage = findViewById(R.id.button_send);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(message.getText().toString()!="") {
                    ParseObject messageObject = new ParseObject("message");
                    messageObject.put("username", ParseUser.getCurrentUser().getUsername());
                    messageObject.put("message", message.getText().toString());
                    messageObject.put("belongsTo", conId);
                    messageObject.saveEventually();
                    message.setText(null);
                }
            }
        });
    }
}
