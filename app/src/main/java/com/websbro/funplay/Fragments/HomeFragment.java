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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.websbro.funplay.Activities.EpisodesListActivity;
import com.websbro.funplay.Adapter.SearchResultAdapter;
import com.websbro.funplay.R;
import com.websbro.funplay.Service.RetrofitInstance;
import com.websbro.funplay.Service.TvDataService;
import com.websbro.funplay.model.DiscoverGenresResponse;
import com.websbro.funplay.model.PopularTvResponse;
import com.websbro.funplay.model.TvShow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    Context context;
    SearchResultAdapter contentFiller;
    RecyclerView popularShows;
    RecyclerView genre1;
    RecyclerView genre2;
    ArrayList<TvShow> popularShowsArrayList;
    ArrayList<TvShow> genres1ArrayList;
    ArrayList<TvShow> genres2ArrayList;

    Random random;
    ImageView bigHome;
    TextView bigHomeName;

    FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment,container,false);
        context = getActivity();

        random = new Random();
        bigHome = view.findViewById(R.id.home_big);
        bigHomeName = view.findViewById(R.id.home_big_name);

        genre1 = view.findViewById(R.id.genres1);
        genre2 = view.findViewById(R.id.genres2);
        popularShows = view.findViewById(R.id.popular);
        popularShowsArrayList = new ArrayList<>();
        genres1ArrayList = new ArrayList<>();
        genres2ArrayList = new ArrayList<>();
        getContent();
        getGenres2();
        getPopular();

        initFirestore();


        return view;
    }


    private void initFirestore() {
        db = FirebaseFirestore.getInstance();
    }


    public void getContent() {
        TvDataService tvDataService = RetrofitInstance.getService();
        Call<DiscoverGenresResponse> discoverGenresResponseCall = tvDataService.getGenreShows(getString(R.string.api_key), "10759");

        discoverGenresResponseCall.enqueue(new Callback<DiscoverGenresResponse>() {
            @Override
            public void onResponse(Call<DiscoverGenresResponse> call, Response<DiscoverGenresResponse> response) {
                DiscoverGenresResponse discoverGenresResponse = response.body();
                if (discoverGenresResponse != null && discoverGenresResponse.getResults() != null) {
                    List<TvShow> tv = discoverGenresResponse.getResults();
                    for (TvShow t : tv) {
                        if (t.getPosterPath() != null) {
                            System.out.println(t.getName());
                            genres1ArrayList.add(t);
                            showgenre1();

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DiscoverGenresResponse> call, Throwable t) {

            }
        });
    }

    public void getGenres2() {
        TvDataService tvDataService = RetrofitInstance.getService();
        Call<DiscoverGenresResponse> discoverGenresResponseCall = tvDataService.getGenreShows(getString(R.string.api_key), "10765");

        discoverGenresResponseCall.enqueue(new Callback<DiscoverGenresResponse>() {
            @Override
            public void onResponse(Call<DiscoverGenresResponse> call, Response<DiscoverGenresResponse> response) {
                DiscoverGenresResponse discoverGenresResponse = response.body();
                if (discoverGenresResponse != null && discoverGenresResponse.getResults() != null) {
                    List<TvShow> tv = discoverGenresResponse.getResults();
                    for (TvShow t : tv) {
                        if (t.getPosterPath() != null) {
                            System.out.println(t.getName());
                            genres2ArrayList.add(t);
                            showgenre2();

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DiscoverGenresResponse> call, Throwable t) {

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
                    for (TvShow t : tv ){
                        if(t.getPosterPath()!=null){
                            popularShowsArrayList.add(t);
                            showPopularShows();
                        }
                    }
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
            }

            @Override
            public void onFailure(Call<PopularTvResponse> call, Throwable t) {

            }
        });
    }



    public void showgenre1(){
        contentFiller = new SearchResultAdapter(context,genres1ArrayList);

        genre1.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));

        genre1.setItemAnimator(new DefaultItemAnimator());
        genre1.setAdapter(contentFiller);
        genre1.getAdapter().notifyDataSetChanged();
    }
    public void showgenre2(){
        contentFiller = new SearchResultAdapter(context,genres2ArrayList);

        genre2.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));

        genre2.setItemAnimator(new DefaultItemAnimator());
        genre2.setAdapter(contentFiller);
        genre2.getAdapter().notifyDataSetChanged();
    }
    public void showPopularShows(){
        contentFiller = new SearchResultAdapter(context,popularShowsArrayList);

        popularShows.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));

        popularShows.setItemAnimator(new DefaultItemAnimator());
        popularShows.setAdapter(contentFiller);
        popularShows.getAdapter().notifyDataSetChanged();
    }

}
