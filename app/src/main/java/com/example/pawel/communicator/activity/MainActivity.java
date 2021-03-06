package com.example.pawel.communicator.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.pawel.communicator.fragment.ConversationsFragment;
import com.example.pawel.communicator.fragment.FriendsFragment;
import com.example.pawel.communicator.R;

public class MainActivity extends AppCompatActivity{
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Rozmowy");
        BottomNavigationView bottomMenu =(BottomNavigationView) findViewById(R.id.menuBottom);
        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (item.getItemId()){
                    case R.id.menu_friends:
                        toolbar.getMenu().clear();
                        toolbar.inflateMenu(R.menu.menu_friends);
                        //Toast.makeText(getApplicationContext(), "Znajomi", Toast.LENGTH_SHORT).show();
                        transaction.replace(R.id.container_main,new FriendsFragment()).commit();
                        break;
                    case R.id.menu_conversations:
                        toolbar.getMenu().clear();
                        //Toast.makeText(getApplicationContext(), "Rozmowy", Toast.LENGTH_SHORT).show();
                        transaction.replace(R.id.container_main,new ConversationsFragment()).commit();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.activity_main_update_menu_item:
//                Toast.makeText(this, "update clicked", Toast.LENGTH_SHORT).show();
//                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
