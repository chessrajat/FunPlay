package com.websbro.funplay.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.websbro.funplay.Activities.EpisodesListActivity;
import com.websbro.funplay.Adapter.SearchResultAdapter;
import com.websbro.funplay.C;
import com.websbro.funplay.CheckConnection;
import com.websbro.funplay.R;
import com.websbro.funplay.Service.RetrofitInstance;
import com.websbro.funplay.Service.TvDataService;
import com.websbro.funplay.model.DiscoverGenresResponse;
import com.websbro.funplay.model.PopularTvResponse;
import com.websbro.funplay.model.SimilarShows;
import com.websbro.funplay.model.TvShow;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    Context context;
    SearchResultAdapter contentFiller;
    RecyclerView popularShows;
    RecyclerView genre1;
    RecyclerView genre2;
    RecyclerView genre3;
    RecyclerView genre4;
    RecyclerView genre5;

    RecyclerView similar1;
    RecyclerView similar2;
    RecyclerView similar3;
    RecyclerView similar4;
    RecyclerView similar5;

    TextView similar1Name;
    TextView similar2Name;
    TextView similar3Name;
    TextView similar4Name;
    TextView similar5Name;

    ArrayList<TvShow> popularShowsArrayList;
    ArrayList<RecyclerView> views;
    ArrayList<TextView> viewsName;

    FirebaseFirestore db ;
    CollectionReference tvCollection;


    Random random;
    ImageView bigHome;
    TextView bigHomeName;

    TextView lastWatched;
    TextView lastWatchedEpisode;

    FirebaseAuth mAuth;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment,container,false);
        context = getActivity();


        random = new Random();
        bigHome = view.findViewById(R.id.home_big);
        bigHomeName = view.findViewById(R.id.home_big_name);

        initFirestore();

        db = FirebaseFirestore.getInstance();
        tvCollection = db.collection("TvShows");

        genre1 = view.findViewById(R.id.genres1);
        genre2 = view.findViewById(R.id.genres2);
        genre3 = view.findViewById(R.id.genres3);
        genre4 = view.findViewById(R.id.genres4);
        genre5 = view.findViewById(R.id.genres5);

        similar1 = view.findViewById(R.id.similar1);
        similar2 = view.findViewById(R.id.similar2);
        similar3 = view.findViewById(R.id.similar3);
        similar4 = view.findViewById(R.id.similar4);
        similar5 = view.findViewById(R.id.similar5);

        similar1Name = view.findViewById(R.id.similar1_name);
        similar2Name = view.findViewById(R.id.similar2_name);
        similar3Name = view.findViewById(R.id.similar3_name);
        similar4Name = view.findViewById(R.id.similar4_name);
        similar5Name = view.findViewById(R.id.similar5_name);

        views = new ArrayList<>();
        views.add(similar1);
        views.add(similar2);
        views.add(similar3);
        views.add(similar4);
        views.add(similar5);

        viewsName = new ArrayList<>();
        viewsName.add(similar1Name);
        viewsName.add(similar2Name);
        viewsName.add(similar3Name);
        viewsName.add(similar4Name);
        viewsName.add(similar5Name);

        lastWatchedEpisode = view.findViewById(R.id.last_watched_episode);
        lastWatched = view.findViewById(R.id.last_watched);

        //ads


        popularShows = view.findViewById(R.id.popular);
        popularShowsArrayList = new ArrayList<>();


        getContent("10759",genre1);
        getContent("10765",genre2);
        getContent("35",genre3);
        getContent("18",genre4);
        getContent("9648",genre5);





        getPopular();

        mAuth = FirebaseAuth.getInstance();
        if(mAuth!= null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
                showAllSimilar();
        }



        return view;
    }



    public void showAllSimilar(){
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            DocumentReference documentReference = db.collection("Users").document(currentUser.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        ArrayList<String> temp = (ArrayList<String>)documentSnapshot.get("recentTvShows");
                        String lastWatched1 = documentSnapshot.getString("watching");
                        if(lastWatched!=null && lastWatched.length()>0){
                            lastWatchedEpisode.setText(lastWatched1);
                            lastWatched.setVisibility(View.VISIBLE);
                            lastWatchedEpisode.setVisibility(View.VISIBLE);
                        }
                        int j=0;
                        System.out.println("temp " + temp.size());
                        for(int i=0;i<temp.size();i+=2){
                            if (j < 5) {
                                String id = temp.get(i);
                                String name = temp.get(i+1);
                                getSililar(id,name,views.get(j),viewsName.get(j));
                                j++;
                            }

                        }
                    }
                }
            });
        }
    }


    private void initFirestore() {
        db = FirebaseFirestore.getInstance();
    }


    public void getContent(String genId, final RecyclerView recyclerView) {
        TvDataService tvDataService = RetrofitInstance.getService();
        Call<DiscoverGenresResponse> discoverGenresResponseCall = tvDataService.getGenreShows(getString(R.string.api_key), genId);

        discoverGenresResponseCall.enqueue(new Callback<DiscoverGenresResponse>() {
            @Override
            public void onResponse(Call<DiscoverGenresResponse> call, Response<DiscoverGenresResponse> response) {
                final ArrayList<TvShow> genreEpisodeList = new ArrayList<>();
                DiscoverGenresResponse discoverGenresResponse = response.body();
                if (discoverGenresResponse != null && discoverGenresResponse.getResults() != null) {
                    List<TvShow> tv = discoverGenresResponse.getResults();
                    for (final TvShow t : tv) {
                        if (t.getPosterPath() != null) {
                            DocumentReference documentReference = tvCollection.document(t.getId().toString());
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if(documentSnapshot.exists()) {
                                            genreEpisodeList.add(t);
                                            showinRecycler(genreEpisodeList,recyclerView);
                                        }else {
                                            System.out.println("not available");
                                        }
                                    }
                                }
                            });

//genreEpisodeList.add(t);


                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<DiscoverGenresResponse> call, Throwable t) {

            }
        });
    }

    public void getSililar(String tvId, final String showName, final RecyclerView recyclerView, final TextView textView){


            TvDataService tvDataService = RetrofitInstance.getService();
            Call<SimilarShows> similarShowsCall = tvDataService.getSililar(tvId, getString(R.string.api_key));

            similarShowsCall.enqueue(new Callback<SimilarShows>() {
                @Override
                public void onResponse(Call<SimilarShows> call, Response<SimilarShows> response) {
                    final ArrayList<TvShow> similarShowlist = new ArrayList<>();
                    SimilarShows similarShows = response.body();
                    System.out.println(similarShows);
                    if (similarShows != null && similarShows.getResults() != null) {
                        List<TvShow> tv = similarShows.getResults();
                        for (final TvShow t : tv) {
                            if (t.getPosterPath() != null) {
                                DocumentReference documentReference = tvCollection.document(t.getId().toString());
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            if (documentSnapshot.exists()) {
                                                similarShowlist.add(t);
                                                showinRecycler(similarShowlist, recyclerView);
                                                if (similarShowlist.size() > 0) {
                                                    textView.setText("Because you watched - \n" + showName);
                                                    textView.setVisibility(View.VISIBLE);
                                                }
                                            } else {
                                                System.out.println("not available");
                                            }
                                        }
                                    }
                                });


                            }
                        }

                    }
                }

                @Override
                public void onFailure(Call<SimilarShows> call, Throwable t) {

                }
            });

    }



    public void getPopular(){
        TvDataService tvDataService = RetrofitInstance.getService();
        Call<PopularTvResponse> popularTvResponseCall = tvDataService.getPopularShows(getString(R.string.api_key));

        popularTvResponseCall.enqueue(new Callback<PopularTvResponse>() {
            @Override
            public void onResponse(Call<PopularTvResponse> call, Response<PopularTvResponse> response) {
                PopularTvResponse popularTvResponse = response.body();
                if(popularTvResponse!=null && popularTvResponse.getResults()!=null){
                    List<TvShow> tv = popularTvResponse.getResults();
                    for (final TvShow t : tv ){
                        if(t.getPosterPath()!=null){

                            DocumentReference documentReference = tvCollection.document(t.getId().toString());
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if(documentSnapshot.exists()) {
                                            popularShowsArrayList.add(t);
                                            showinRecycler(popularShowsArrayList,popularShows);
                                            showBigImage();
                                        }else {
                                            System.out.println("not available");
                                        }
                                    }
                                }
                            });

                        }
                    }

                    //
                }
            }

            @Override
            public void onFailure(Call<PopularTvResponse> call, Throwable t) {

            }
        });
    }

    public void showBigImage(){
        final int rand = random.nextInt(popularShowsArrayList.size());

        bigHomeName.setText(popularShowsArrayList.get(rand).getName());
        String imagePath = context.getString(R.string.image_path) + popularShowsArrayList.get(rand).getBackdropPath();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_loading_circles);
        Glide.with(context)
                .load(imagePath)
                .apply(requestOptions)
                .into(bigHome);
        bigHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tvId = popularShowsArrayList.get(rand).getId().toString();
                String posterUrl = popularShowsArrayList.get(rand).getBackdropPath().toString();
                String name = bigHomeName.getText().toString();

                Intent intent = new Intent(context,EpisodesListActivity.class);
                intent.putExtra("tvId",tvId);
                intent.putExtra("posterUrl",posterUrl);
                intent.putExtra("name",name);
                context.startActivity(intent);
            }
        });
    }



    public void showinRecycler(ArrayList<TvShow> genres,RecyclerView recyclerView){
        contentFiller = new SearchResultAdapter(context,genres);

        recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contentFiller);
        recyclerView.getAdapter().notifyDataSetChanged();
    }


}
