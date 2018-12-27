package com.websbro.funplay.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.websbro.funplay.R;
import com.websbro.funplay.model.TvShow;

import java.util.ArrayList;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {

    private FirebaseFirestore db;
    String tvId;
    private Context context;
    private ArrayList<TvShow> tvShowArrayList;

    public SearchResultAdapter(Context context, ArrayList<TvShow> tvShowArrayList) {
        this.context = context;
        this.tvShowArrayList = tvShowArrayList;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_layout,viewGroup,false);

        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder searchResultViewHolder, int i) {


        searchResultViewHolder.tvShowTitle.setText(tvShowArrayList.get(i).getName());
        String imagePath = context.getString(R.string.image_path) + tvShowArrayList.get(i).getPosterPath();
        System.out.println(imagePath);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_loading_circles);
        Glide.with(context)
                .load(imagePath)
                .apply(requestOptions)
                .into(searchResultViewHolder.tvShowImage);


    }

    @Override
    public int getItemCount() {
        return tvShowArrayList.size();
    }


    public class SearchResultViewHolder extends RecyclerView.ViewHolder{

        ImageView tvShowImage;
        TextView tvShowTitle;

        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvId = tvShowArrayList.get(getAdapterPosition()).getId().toString();
                    String posterUrl = tvShowArrayList.get(getAdapterPosition()).getBackdropPath().toString();
                    String name = tvShowArrayList.get(getAdapterPosition()).getName();

                    Intent intent = new Intent(context,EpisodesListActivity.class);
                    intent.putExtra("tvId",tvId);
                    intent.putExtra("posterUrl",posterUrl);
                    intent.putExtra("name",name);
                    context.startActivity(intent);

                }
            });


            tvShowImage = itemView.findViewById(R.id.tv_show_image);
            tvShowTitle = itemView.findViewById(R.id.tv_title);

        }

    }

}
