package com.websbro.funplay.Activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.websbro.funplay.Adapter.EpisodeAdapter;
import com.websbro.funplay.C;
import com.websbro.funplay.CheckConnection;
import com.websbro.funplay.EpisodeDetails;
import com.websbro.funplay.R;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EpisodesListActivity extends AppCompatActivity {

    String tvId;
    String name;
    String posterUrl;

    FirebaseFirestore db;

    ImageView episodeImage;
    Spinner seasonSelector;
    ListView episodeList;
    EpisodeAdapter episodeAdapter;

    ArrayList<String> spinnerContent;
    ArrayAdapter<String> adapter;
    ArrayList<EpisodeDetails> episodeDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes_list);

        episodeImage = findViewById(R.id.episode_image);
        seasonSelector = findViewById(R.id.season_selector);
        episodeList = findViewById(R.id.episode_list);
        spinnerContent = new ArrayList<>();
        episodeDetails = new ArrayList<>();
        spinnerContent.add("season1");

        adapter = new ArrayAdapter<>(this,R.layout.spinner_layout,spinnerContent);
        seasonSelector.setAdapter(adapter);


        db = FirebaseFirestore.getInstance();

        if(!C.GO_OFFLINE) {
            Timer timer = new Timer();
            final int MILLISECONDS = 4000; //4 seconds
            timer.schedule(new CheckConnection(EpisodesListActivity.this), 0, MILLISECONDS);
        }

        createNotificationChannel();



        Intent intent = getIntent();
        tvId = intent.getStringExtra("tvId");
        name = intent.getStringExtra("name");
        posterUrl = intent.getStringExtra("posterUrl");


        String imagePath = getString(R.string.image_path) + posterUrl;


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_loading_circles);
        Glide.with(this)
                .load(imagePath)
                .apply(requestOptions)
                .into(episodeImage);

        getContent();

        seasonSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int seasonNumber = position+1;
                getToken("s"+seasonNumber);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    DocumentReference documentReference;
    DocumentReference tokenReference;
    public void getContent(){
        documentReference = db.collection("TvShows").document(tvId);
        tokenReference = db.collection("token").document("token");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        int seasons = Integer.parseInt(documentSnapshot.getString("seasons"));
                        for(int i=2;i<=seasons;i++){
                            spinnerContent.add("season"+i);
                            adapter.notifyDataSetChanged();
                        }

                    }
                }
            }
        });
    }

    String token= null;

    public void getToken(final String s){
        tokenReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        token = documentSnapshot.getString("token");
                        System.out.println(token);
                        getSeasonContent(s);
                    }


                }
            }
        });
    }

    public void getSeasonContent(final String s){

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()) {
                        episodeDetails.clear();
                        String season = "season" + s.charAt(s.length() - 1);
                        String name = snapshot.getString("Name");
                        HashMap<String, String> episode = (HashMap<String, String>) snapshot.get(s);
                        for (String e : episode.keySet()) {
                            String ep = "episode" + e.substring(1);
                            String link = episode.get(e);
                            String completeLink = link + token;




                            System.out.println(completeLink);
                            System.out.println(ep);
                            System.out.println(season);
                            EpisodeDetails ed = new EpisodeDetails(name, ep, completeLink,season,tvId);
                            episodeDetails.add(ed);

                        }

                        setEpisodeAdapter();

                    }

                }
            }
        });


    }

    public void setEpisodeAdapter(){
         Collections.sort(episodeDetails, new Comparator<EpisodeDetails>() {
            @Override
            public int compare(EpisodeDetails o1, EpisodeDetails o2) {
                return o1.getEpisodeLink().compareTo(o2.getEpisodeLink());

            }
        });


        episodeAdapter = new EpisodeAdapter(this,episodeDetails);
        episodeList.setAdapter(episodeAdapter);
    }



    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = C.CHANNEL_NAME;
            String description = C.CHANNEL_DES;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(C.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



}
