package com.example.pawel.communicator.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pawel.communicator.R;
import com.example.pawel.communicator.activity.ConversationActivity;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by pawel on 02.02.2018.
 */

public class FriendsListAdapter extends ArrayAdapter<String>{
    private final Context context;
    private final ArrayList<HashMap> friends;

    public FriendsListAdapter(Context context, ArrayList<HashMap> friends) {
        super(context, R.layout.friends_row);
        this.context = context;
        this.friends = friends;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.friends_row, parent, false);
        TextView nameField = rowView.findViewById(R.id.friend_username);
        ImageView avatarField = rowView.findViewById(R.id.friend_avatar);
        final ImageButton messageButton = rowView.findViewById(R.id.message_friends);
        final Button inviteButton = rowView.findViewById(R.id.inviteButton);
        if(friends.get(position).get("isConfirmed").equals(false)){
            if(friends.get(position).get("inviter")!=null) { //displaying inviters
                inviteButton.setVisibility(View.VISIBLE);
                messageButton.setVisibility(View.INVISIBLE);
                nameField.setText((String) friends.get(position).get("usernameInviter"));
                inviteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Friends");
                        query.whereEqualTo("invited", ParseUser.getCurrentUser().getObjectId());
                        query.whereEqualTo("inviter", friends.get(position).get("inviter"));
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    for (ParseObject object : objects) {
                                        object.put("isConfirmed", true);
                                        inviteButton.setVisibility(View.INVISIBLE);
                                        messageButton.setVisibility(View.VISIBLE);
                                        object.saveEventually();
                                    }
                                }
                            }
                        });

                    }
                });
            }else
            {
                nameField.setText((String) friends.get(position).get("usernameInvited"));
                inviteButton.setText("Zaproszony");
                messageButton.setVisibility(View.INVISIBLE);
            }
        }else
            {
                inviteButton.setVisibility(View.INVISIBLE);
                if(friends.get(position).get("inviter")!=null)
                    nameField.setText((String) friends.get(position).get("usernameInviter"));
                else
                    nameField.setText((String) friends.get(position).get("usernameInvited"));
                messageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //start conversation
                        final Integer[] noConversation = {0};
                        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Conversation");
                        final ArrayList<String> users = new ArrayList<String>();
                        users.add((String) friends.get(position).get("usernameInviter"));
                        users.add((String) friends.get(position).get("usernameInvited"));
                        query1.whereContainsAll("participants",users);
                        query1.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                for(ParseObject obj : objects){
                                    if(((ArrayList<String>) obj.get("participants")).size()==2){
                                        Intent intent = new Intent(getContext(), ConversationActivity.class);
                                        intent.putExtra("conversationId", obj.getObjectId());
                                        getContext().startActivity(intent);
                                        //Toast.makeText(context, "conversation - exist", Toast.LENGTH_SHORT).show();
                                        noConversation[0] =1;
                                    }
                                }
                                if(noConversation[0]==0){
                                    final ParseObject newConversation = new ParseObject("Conversation");
                                    newConversation.put("participants",users);
                                    newConversation.saveEventually(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Intent intent = new Intent(getContext(), ConversationActivity.class);
                                            intent.putExtra("conversationId", newConversation.getObjectId());
                                            getContext().startActivity(intent);
                                            //Toast.makeText(context, "conversation - new", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });

                    }
                });
            }



        return rowView;
    }

    @Override
    public int getCount() {
        return friends.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }
}
