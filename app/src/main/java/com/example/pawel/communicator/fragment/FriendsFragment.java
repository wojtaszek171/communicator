package com.example.pawel.communicator.fragment;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pawel.communicator.R;
import com.example.pawel.communicator.adapter.FriendsListAdapter;
import com.example.pawel.communicator.adapter.SearchFriendsAdapter;
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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<ParseObject> usersListSearch;
    ArrayList<HashMap> friendsList;
    ListView listSearch;
    ListView listFriends;
    FriendsListAdapter friendsAdapter;
    SearchFriendsAdapter adapter;

    private OnFragmentInteractionListener mListener;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_friends, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Znajomi");

        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Friends");
        query1.whereEqualTo("inviter",ParseUser.getCurrentUser().getObjectId());

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Friends");
        query2.whereEqualTo("invited",ParseUser.getCurrentUser().getObjectId());

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    ArrayList<HashMap> listOfFriendsNotConfirmed = new ArrayList<HashMap>();
                    ArrayList<HashMap> listOfFriends = new ArrayList<HashMap>();
                    for (ParseObject relation : results) {

                        if(relation.get("inviter").equals(ParseUser.getCurrentUser().getObjectId())){
                            final HashMap friend = new HashMap();
                            friend.put("invited",relation.getString("invited"));
                            friend.put("usernameInvited",relation.getString("usernameInvited"));
                            friend.put("isConfirmed",relation.get("isConfirmed"));
                            if(relation.get("isConfirmed").equals("false")){
                                listOfFriendsNotConfirmed.add(friend);
                            }else{
                                listOfFriends.add(friend);
                            }
                        }
                        if(relation.get("invited").equals(ParseUser.getCurrentUser().getObjectId())){
                            final HashMap friend = new HashMap();
                            friend.put("inviter",relation.getString("inviter"));
                            friend.put("usernameInviter",relation.getString("usernameInviter"));
                            friend.put("isConfirmed",relation.get("isConfirmed"));
                            if(relation.get("isConfirmed").equals("false")){
                                listOfFriendsNotConfirmed.add(friend);
                            }else{
                                listOfFriends.add(friend);
                            }
                        }
                    }
                    listOfFriendsNotConfirmed.addAll(listOfFriends);    //all friends (confirmed and not)
                    friendsAdapter = new FriendsListAdapter(view.getContext(),listOfFriendsNotConfirmed);
                    listFriends =(ListView) view.findViewById(R.id.list_friends);
                    listFriends.setAdapter(friendsAdapter);
                }
            }
        });



        addFriendOnClickAndDialog(view);

        return view;
    }

    private void addFriendOnClickAndDialog(View view) {
        final FloatingActionButton addFriend = view.findViewById(R.id.addFriend);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog addFriendDialog = new Dialog(getContext());
                addFriendDialog.setContentView(R.layout.add_friend_dialog);
                addFriendDialog.setTitle("Dodaj znajomego");
                ImageButton searchButton = addFriendDialog.findViewById(R.id.search);
                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText usernameText = addFriendDialog.findViewById(R.id.friend_name);
                        String friendUsername =  usernameText.getText().toString();
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
                        query.whereEqualTo("username", friendUsername);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if(e==null){
                                    usersListSearch = new ArrayList<ParseObject>();
                                    if(objects.size()<=0) {
                                        ((TextView) addFriendDialog.findViewById(R.id.search_info)).setVisibility(View.VISIBLE);
                                        ((TextView) addFriendDialog.findViewById(R.id.search_info)).setText("Nie znaleziono");
                                    }
                                    else
                                        ((TextView) addFriendDialog.findViewById(R.id.search_info)).setVisibility(View.INVISIBLE);
                                    for (ParseObject user : objects) {
                                        usersListSearch.add(user);
                                    }
                                    adapter = new SearchFriendsAdapter(addFriendDialog.getContext(), usersListSearch);
                                    listSearch =(ListView) addFriendDialog.findViewById(R.id.list_search_users);
                                    listSearch.setAdapter(adapter);
                                }else{

                                }
                            }
                        });

                    }
                });

                addFriendDialog.show();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            Toast.makeText(context, "Znajomi", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
