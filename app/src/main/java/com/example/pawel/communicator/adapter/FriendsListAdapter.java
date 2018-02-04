package com.example.pawel.communicator.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pawel.communicator.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;


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
        ImageButton messageButton = rowView.findViewById(R.id.message_friends);
        Button inviteButton = rowView.findViewById(R.id.inviteButton);

        if(friends.get(position).get("inviter")==null){
            inviteButton.setVisibility(View.INVISIBLE);
            nameField.setText((String) friends.get(position).get("username"));
            messageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //start conversation
                }
            });
        }else{
            inviteButton.setVisibility(View.VISIBLE);
            messageButton.setVisibility(View.INVISIBLE);
            nameField.setText((String) friends.get(position).get("username"));
            inviteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<HashMap> myFriends = (ArrayList<HashMap>) ParseUser.getCurrentUser().getParseObject("UserData").get("Friends");
                    if(myFriends==null){
                        myFriends = new ArrayList<HashMap>();
                    }
                    HashMap friend = new HashMap();
                    friend.put("username", friends.get(position).get("username"));
                    friend.put("friendId", friends.get(position).get("inviter"));
                    myFriends.add(friend);
                    ParseObject user = ParseUser.getCurrentUser().getParseObject("UserData");
                    user.put("Friends",myFriends);
                    user.saveEventually();
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
                    query.getInBackground((String) friends.get(position).get("inviter"), new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    if (e==null){
                                        ArrayList<HashMap> friendsToUpdate = null;
                                        try{
                                            friendsToUpdate = (ArrayList<HashMap>) object.getParseObject("UserData").fetchIfNeeded().get("Friends");
                                        }catch (ParseException e1){

                                        }
                                        if(friendsToUpdate==null){
                                            friendsToUpdate = new ArrayList<HashMap>();
                                        }
                                        HashMap friend2 = new HashMap();
                                        friend2.put("username", ParseUser.getCurrentUser().getUsername());
                                        friend2.put("friendId", ParseUser.getCurrentUser().getObjectId());
                                        friendsToUpdate.add(friend2);
                                        ParseObject user2 = object.getParseObject("UserData");
                                        user2.put("Friends",friendsToUpdate);
                                        user2.saveEventually();

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
