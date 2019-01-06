package com.websbro.funplay.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.websbro.funplay.C;
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
                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.nav_downloads:
                            selectedFragment = new DownloadsFragment();
                            break;
                        case R.id.nav_more:
                            selectedFragment = new MoreFragment();
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

//        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
//
//        int permissionCheck1 = ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE");
//        int permissionCheck2 = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
//
//        if (permissionCheck1 == 0 && permissionCheck2 == 0) {
//
//        }else {
//            getWriteExternalStoragePermission();
//            getReadExternalStoragePermission();
//        }



    BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

    getSupportFragmentManager().

    beginTransaction().

    replace(R.id.fragment_container,
                new HomeFragment()).

    commit();

//        ProgressBar progressBar = findViewById(R.id.download_progress);

    Timer timer = new Timer();


    if(!C.GO_OFFLINE) {
        final int MILLISECONDS = 4000; //4 seconds
        timer.schedule(new CheckConnection(HomeActivity.this), 0, MILLISECONDS);
    }
// for download progress bar
//        Intent intent = getIntent();
//        String download = intent.getStringExtra("download");
//        if(download!=null){
//            if(download.equals("1")){
//                progressBar.setVisibility(View.VISIBLE);
//            }else if(download.equals("0")){
//                progressBar.setVisibility(View.GONE);
//                Toast.makeText(this,"Download Completed",Toast.LENGTH_SHORT).show();
//            }
//        }

}

//
//
//    private void getReadExternalStoragePermission() {
//        //getting permission for external storage
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED) {
//
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
//                // app-defined int constant
//
//            } else {
//
//            }
//        }
//    }
//
//    private void getWriteExternalStoragePermission() {
//        //getting permission for external storage
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED) {
//
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
//                // app-defined int constant
//
//            } else {
//
//            }
//        }
//    }



    @Override
    protected void onStop() {
        super.onStop();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancelAll();
    }


    }




