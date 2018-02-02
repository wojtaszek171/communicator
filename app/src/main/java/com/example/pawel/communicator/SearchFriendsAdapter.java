package com.example.pawel.communicator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;


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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.search_friends_row, parent, false);
        TextView nameField = rowView.findViewById(R.id.search_username);
        ImageView avatarField = rowView.findViewById(R.id.search_avatar);
        nameField.setText(values.get(position).getString("username"));

        return rowView;
    }
}
