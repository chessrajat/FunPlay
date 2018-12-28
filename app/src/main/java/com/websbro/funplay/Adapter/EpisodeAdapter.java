package com.websbro.funplay.Adapter;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.websbro.funplay.Activities.HomeActivity;
import com.websbro.funplay.Activities.PlayerActivity;
import com.websbro.funplay.EpisodeDetails;
import com.websbro.funplay.Fragments.DownloadsFragment;
import com.websbro.funplay.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static com.websbro.funplay.C.CHANNEL_ID;

public class EpisodeAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<EpisodeDetails> details;
    static Download download;


    public EpisodeAdapter(Context context, ArrayList<EpisodeDetails> details) {
        this.context = context;
        this.details = details;
    }

    @Override
    public int getCount() {
        return details.size();
    }

    @Override
    public Object getItem(int position) {
        return details.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.list_layout,parent,false);
        }



        ImageView downloadButton = convertView.findViewById(R.id.download_button);
        TextView season = convertView.findViewById(R.id.season_name);
        TextView episode = convertView.findViewById(R.id.episode_number);

        LinearLayout watch = convertView.findViewById(R.id.watch);
        watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                animation1.setDuration(1500);
                v.startAnimation(animation1);

                Intent intent = new Intent(context,PlayerActivity.class);
                intent.putExtra("link",details.get(position).getEpisodeLink());
                context.startActivity(intent);

            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                animation1.setDuration(1500);
                v.startAnimation(animation1);

                download = new Download();
                String url = details.get(position).getEpisodeLink();
                String tempFileName = url.substring(url.lastIndexOf('/') + 1);
                if(url.startsWith("http://bia2vip.site")){
                    tempFileName = tempFileName.replaceAll("\\(Bia2Movies\\)","");
                    tempFileName = tempFileName.replaceAll("%20","");
                    tempFileName = tempFileName.replaceAll("\\?token=0F86B08B78839F00F11DC297281AFCB9","");
                }

                Toast.makeText(context,"Download Started",Toast.LENGTH_LONG).show();
                if(download.getStatus()==AsyncTask.Status.RUNNING){
                    Toast.makeText(context,"Another download running",Toast.LENGTH_LONG).show();
                }else {
                    download.execute(url, tempFileName);
                }

            }
        });



        season.setText(details.get(position).getSeasonName());
        episode.setText(details.get(position).getSeason()+" "+details.get(position).getEpisodeNumber());


        return convertView;

    }


    public class Download extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... strings) {
            try {

                URL url = new URL(strings[0]);
                URLConnection conn = url.openConnection();
                int contentLength = conn.getContentLength();
                DataInputStream dataInputStream = new DataInputStream(url.openStream());

                File file = new File(context.getExternalFilesDir("FunPlay"),strings[1]);

                System.out.println("download start");
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_file_download_50dp)
                        .setContentTitle("Downloading....")
                        .setContentText(strings[1])
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(6, mBuilder.build());

                byte[] buffer = new byte[contentLength];

                System.out.println(buffer);
                dataInputStream.readFully(buffer);
                dataInputStream.close();

                DataOutputStream fos = new DataOutputStream(new FileOutputStream(file));
                fos.write(buffer);
                fos.flush();
                fos.close();

                mBuilder.setContentTitle("Download Complete")
                        .setContentText(strings[1]);
                notificationManager.notify(6,mBuilder.build());



                System.out.println("download complete");




            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            return null;
        }
    }




}
