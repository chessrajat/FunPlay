package com.websbro.funplay.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.websbro.funplay.Activities.HomeActivity;
import com.websbro.funplay.Adapter.SearchResultAdapter;
import com.websbro.funplay.R;
import com.websbro.funplay.Service.RetrofitInstance;
import com.websbro.funplay.Service.TvDataService;
import com.websbro.funplay.model.SearchResponse;
import com.websbro.funplay.model.TvShow;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    ImageView searchAd;
    String adLink;

    FirebaseFirestore db;
    CollectionReference tvCollection;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment,container,false);
        searchView = view.findViewById(R.id.search);
        searchResult = view.findViewById(R.id.search_result);
        searchInfoText = view.findViewById(R.id.search_info_text);
        searchView.onActionViewExpanded();
        context = getActivity();

        db = FirebaseFirestore.getInstance();
        tvCollection = db.collection("TvShows");

        DocumentReference adReference = db.collection("token").document("ad");
        adReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    adLink = documentSnapshot.getString("ad1");
                }
            }
        });
        searchAd = view.findViewById(R.id.search_ad);
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));





        //start
//
//        DownloadPage downloadPage = new DownloadPage();
//        ArrayList<String> sssLinks = new ArrayList<>();
//        String result = "";
//        try {
//            result = downloadPage.execute("http://s1.dl-bia2.ir/Series/listing.php?d=What+I+Like+About+You%2Fs4").get();
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//        Pattern pattern = Pattern.compile("href=\"([^\"]*)\"");
//        Matcher m = pattern.matcher(result);
//        while(m.find()){
//            String temp = result.substring(m.start()+6,m.end()-1);
//            sssLinks.add(temp);
//            System.out.println(result.substring(m.start()+6,m.end()-1));
//        }
//
//
//
//        Map<String,String> s1 = new HashMap<>();
//        int j=0;
//        for(int i=1;i<=18;i++){
//            String e = "e"+i;
//            String l = "http://s1.dl-bia2.ir/Series/"+sssLinks.get(i+5+j);
////            j=j+1;
//            s1.put(e,l);
//        }
//        System.out.println(s1);
//
//        DocumentReference docRef = tvCollection.document("33");
//        docRef.update("s4",s1).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                System.out.println("sucessfully updated");
//            }
//        });


        //count the elements
//
//        tvCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    System.out.println("total number of elements : "+ task.getResult().size());
//                }
//            }
//        });

        //end

        tvShowArrayList= new ArrayList<>();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getSearchResult(query);
                imageLoader.displayImage(adLink,searchAd);
                searchAd.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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
                        searchAd.setVisibility(View.GONE);
                    }
                    final List<TvShow> tv = searchResponse.getResults();

                    for( int t=0;t<tv.size();t++){
                        if(tv.get(t).getPosterPath()!=null){

                            DocumentReference documentReference = tvCollection.document(tv.get(t).getId().toString());
                            final int finalT = t;
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if(documentSnapshot.exists()) {
                                            tvShowArrayList.add(tv.get(finalT));
                                            showOnRecyclerView();
                                        }else {

                                        }
                                    }

                                }

                            });


                        }
                        showOnRecyclerView();



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



    class DownloadPage extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try{
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while (data!= -1){
                    char current = (char)data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }catch (Exception e){
                System.out.println(e.getMessage());
                return null;
            }

        }


    }



}
