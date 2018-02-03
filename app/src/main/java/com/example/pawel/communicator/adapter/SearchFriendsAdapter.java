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
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;


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
        ImageView avatarField = rowView.findViewById(R.id.search_avatar);
        nameField.setText(values.get(position).getString("username"));
        final Button inviteButton = rowView.findViewById(R.id.inviteButton);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserData");

        ParseObject userData = ParseUser.getCurrentUser().getParseObject("UserData");
                ArrayList<HashMap> myInvitations = null;
                try {
                    myInvitations =(ArrayList) userData.fetchIfNeeded().get("Invitations");     //needed because if is null then throws exception
                } catch (ParseException e) {
                    Log.v("LOG_TAG", e.toString());
                    e.printStackTrace();
                }
                if(userData.get("Invitations")!=null)
                    for (HashMap invitation : myInvitations) {
                        if(values.get(position).getObjectId().equals(invitation.get("invited"))) {
                            inviteButton.setText("Zaproszony");
                            inviteButton.setEnabled(false);
                        }
                    }


        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = ParseUser.getCurrentUser();
                ParseObject userData = user.getParseObject("UserData");

                            final HashMap invitationObject = new HashMap();
                            invitationObject.put("invited", values.get(position).getObjectId());

                            ArrayList<HashMap> nestedInvitations = (ArrayList) userData.get("Invitations");
                            if(nestedInvitations==null)
                                nestedInvitations = new ArrayList<HashMap>();
                            nestedInvitations.add(invitationObject);
                            userData.put("Invitations",nestedInvitations);
                            userData.saveEventually();

                            ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
                            query.getInBackground(values.get(position).getObjectId(), new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject object, ParseException e) {
                                            ParseObject userData = object.getParseObject("UserData");
                                            ArrayList<HashMap> nestedInviters = (ArrayList) object.get("Inviters");
                                            if(nestedInviters==null)
                                                nestedInviters = new ArrayList<HashMap>();
                                            HashMap invitersObject = new HashMap();
                                            invitersObject.put("inviter", ParseUser.getCurrentUser().getObjectId());
                                            nestedInviters.add(invitersObject);
                                            userData.put("Inviters",nestedInviters);
                                            userData.saveEventually();
                                        }
                            });


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
