package com.websbro.funplay.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.websbro.funplay.Activities.PlayerActivity;
import com.websbro.funplay.EpisodeDetails;
import com.websbro.funplay.R;

import org.w3c.dom.Text;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

public class DownloadListAdapter extends BaseAdapter {

    Context context;
    ArrayList<EpisodeDetails> downloadedShows;

    public DownloadListAdapter(Context context, ArrayList<EpisodeDetails> downloadedShows) {
        this.context = context;
        this.downloadedShows = downloadedShows;
    }

    @Override
    public int getCount() {
        return downloadedShows.size();
    }

    @Override
    public Object getItem(int position) {
        return downloadedShows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.download_list,parent,false);
        }

        LinearLayout linearLayout = convertView.findViewById(R.id.download_list_layout);
        TextView textView = convertView.findViewById(R.id.download_name);
        final String link = downloadedShows.get(position).getEpisodeLink();


        String name = downloadedShows.get(position).getSeasonName();
        name = name.substring(0,name.length()-4);
        System.out.println(name);
        textView.setText(name);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                animation1.setDuration(1500);
                v.startAnimation(animation1);

                Intent intent = new Intent(Intent.ACTION_VIEW);

                intent.setDataAndType(Uri.parse(link), "video/*");

                context.startActivity(Intent.createChooser(intent, "Play"));

//                Intent intent = new Intent(context,PlayerActivity.class);
//                intent.putExtra("link",link);
//                context.startActivity(intent);
            }
        });

        final String finalName = downloadedShows.get(position).getSeasonName();
        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new android.support.v7.app.AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure?")
                        .setIcon(android.R.drawable.ic_delete)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File file = new File(context.getExternalFilesDir("FunPlay"), finalName);
                                boolean delete = file.delete();
                                if(delete){
                                    Toast.makeText(context,"Successful",Toast.LENGTH_SHORT).show();
                                    downloadedShows.remove(position);
                                    DownloadListAdapter.this.notifyDataSetChanged();
                                }else {
                                    Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();

                return true;
            }
        });


        return convertView;
    }
}
