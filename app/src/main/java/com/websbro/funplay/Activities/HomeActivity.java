package com.websbro.funplay.Activities;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.websbro.funplay.CheckConnection;
import com.websbro.funplay.Fragments.DownloadsFragment;
import com.websbro.funplay.Fragments.HomeFragment;
import com.websbro.funplay.Fragments.MoreFragment;
import com.websbro.funplay.Fragments.SearchFragment;
import com.websbro.funplay.R;

import java.util.Timer;

public class HomeActivity extends AppCompatActivity {

    Handler mHandler;

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()){
                        case R.id.nav_home : selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_search: selectedFragment = new SearchFragment();
                            break;
                        case R.id.nav_downloads:selectedFragment = new DownloadsFragment();
                            break;
                        case R.id.nav_more: selectedFragment = new MoreFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();


        Timer timer = new Timer();
        final int MILLISECONDS = 4000; //4 seconds
        timer.schedule(new CheckConnection(HomeActivity.this), 0, MILLISECONDS);




    }




}
