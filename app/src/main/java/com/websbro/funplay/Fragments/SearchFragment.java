package com.websbro.funplay.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.websbro.funplay.Adapter.SearchResultAdapter;
import com.websbro.funplay.R;
import com.websbro.funplay.Service.RetrofitInstance;
import com.websbro.funplay.Service.TvDataService;
import com.websbro.funplay.model.SearchResponse;
import com.websbro.funplay.model.TvShow;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    SearchView searchView;
    TextView searchInfoText;
    RecyclerView searchResult;
    ArrayList<TvShow> tvShowArrayList;
    SearchResultAdapter searchResultAdapter;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment,container,false);
        searchView = view.findViewById(R.id.search);
        searchResult = view.findViewById(R.id.search_result);
        searchInfoText = view.findViewById(R.id.search_info_text);
        searchView.onActionViewExpanded();
        context = getActivity();



        tvShowArrayList= new ArrayList<>();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getSearchResult(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchInfoText.setVisibility(View.GONE);
                return false;
            }
        });


        return view;
    }


    public void getSearchResult(String query){
        TvDataService tvDataService = RetrofitInstance.getService();
        Call<SearchResponse> call = tvDataService.getSearchResult(getString(R.string.api_key),query);

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                SearchResponse searchResponse = response.body();
                if(searchResponse!=null && searchResponse.getResults()!=null ){
                    tvShowArrayList.clear();
                    if(searchResponse.getResults().size()==0){
                        searchInfoText.setText("Oh! we don't have that");
                        searchInfoText.setVisibility(View.VISIBLE);
                    }
                    List<TvShow> tv = searchResponse.getResults();

                    for( TvShow t : tv){
                        if(t.getPosterPath()!=null){
                            tvShowArrayList.add(t);
                            showOnRecyclerView();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                System.out.println("failed");
                System.out.println(t.getMessage());
            }
        });
    }

    public void showOnRecyclerView(){
        searchResultAdapter = new SearchResultAdapter(context,tvShowArrayList);

        searchResult.setLayoutManager(new GridLayoutManager(context, 3));

        searchResult.setItemAnimator(new DefaultItemAnimator());
        searchResult.setAdapter(searchResultAdapter);
        searchResult.getAdapter().notifyDataSetChanged();


    }

}
