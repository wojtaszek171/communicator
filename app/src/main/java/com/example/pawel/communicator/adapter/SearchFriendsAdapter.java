package com.example.pawel.communicator.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pawel.communicator.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by pawel on 02.02.2018.
 */

public class SearchFriendsAdapter extends ArrayAdapter<String>{
    private final Context context;
    private final ArrayList<ParseObject> values;

    public SearchFriendsAdapter(Context context, ArrayList<ParseObject> values) {
        super(context, R.layout.search_friends_row);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.search_friends_row, parent, false);
        TextView nameField = rowView.findViewById(R.id.search_username);
        ImageView avatarField = rowView.findViewById(R.id.friend_avatar);
        nameField.setText(values.get(position).getString("username"));
        final Button inviteButton = rowView.findViewById(R.id.inviteButton);

                ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Friends");
                query1.whereEqualTo("inviter",ParseUser.getCurrentUser().getObjectId());
                query1.whereEqualTo("invited",values.get(position).getObjectId());

                ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Friends");
                query2.whereEqualTo("invited",ParseUser.getCurrentUser().getObjectId());
                query2.whereEqualTo("inviter",values.get(position).getObjectId());

                List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                queries.add(query1);
                queries.add(query2);

                ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
                mainQuery.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> results, ParseException e) {
                        if (e == null) {
                            for (ParseObject relation : results) {
                                if(relation.get("inviter").equals(ParseUser.getCurrentUser().getObjectId())){
                                    if(relation.get("isConfirmed").equals("false")){
                                        inviteButton.setText("Nie zaakceptował");
                                        inviteButton.setEnabled(false);
                                    }else{
                                        inviteButton.setText("Znajomy");
                                        inviteButton.setEnabled(false);
                                    }
                                }
                                if(relation.get("invited").equals(ParseUser.getCurrentUser().getObjectId())){
                                    if(relation.get("isConfirmed").equals("false")){
                                        inviteButton.setText("Zaprosił cię");
                                        inviteButton.setEnabled(false);
                                    }else{
                                        inviteButton.setText("Znajomy");
                                        inviteButton.setEnabled(false);
                                    }
                                }

                            }
                        }
                    }
                });



        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = ParseUser.getCurrentUser();

                            ParseObject newFriendInvitation = new ParseObject("Friends");
                            newFriendInvitation.put("inviter",user.getObjectId());
                            newFriendInvitation.put("usernameInviter",user.getUsername());
                            newFriendInvitation.put("invited",values.get(position).getObjectId());
                            newFriendInvitation.put("usernameInvited",values.get(position).getString("username"));
                            newFriendInvitation.put("isConfirmed",false);
                            newFriendInvitation.saveEventually();


                        inviteButton.setText("Zaproszono");
                        inviteButton.setEnabled(false);



            }
        });
        return rowView;
    }

    @Override
    public int getCount() {
        return values.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }
}
