package com.websbro.funplay.Adapter;

import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.websbro.funplay.Activities.EpisodesListActivity;
import com.websbro.funplay.Activities.HomeActivity;
import com.websbro.funplay.Activities.PlayerActivity;
import com.websbro.funplay.EpisodeDetails;
import com.websbro.funplay.Fragments.DownloadsFragment;
import com.websbro.funplay.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.websbro.funplay.C.CHANNEL_ID;

public class EpisodeAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<EpisodeDetails> details;
    ArrayList<String> recents;
//    static Download download;

    String watching = "";
    String link = null;


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
        recents = new ArrayList<>();

        final LinearLayout watch = convertView.findViewById(R.id.watch);
        final View.OnClickListener watchListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Wait....",Toast.LENGTH_SHORT).show();
                watch.setOnLongClickListener(null);
                Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                animation1.setDuration(1500);
                v.startAnimation(animation1);


                recents.add(details.get(position).getEpisodeId());
                recents.add(details.get(position).getSeasonName());
                watching = details.get(position).getSeasonName()+" "+details.get(position).getSeason()+" "+details.get(position).getEpisodeNumber();
                addRecentShows();

                final String episodeLink = details.get(position).getEpisodeLink();
                Intent intent = new Intent(context,PlayerActivity.class);
                intent.putExtra("link",episodeLink);
                intent.putExtra("title",details.get(position).getSeasonName()+" "+details.get(position).getSeason()+" "+details.get(position).getEpisodeNumber());
                context.startActivity(intent);
            }
        };

        watch.setOnClickListener(watchListener);


        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                animation1.setDuration(1500);
                v.startAnimation(animation1);

//                download = new Download();
                String url = details.get(position).getEpisodeLink();
                String tempFileName = url.substring(url.lastIndexOf('/') + 1);
                tempFileName = tempFileName.replaceAll("\\(Bia2Movies\\)","");
                tempFileName = tempFileName.replaceAll(".480p","");
                tempFileName = tempFileName.replaceAll("%20","");
                tempFileName = tempFileName.replaceAll(".Grabthebeast","");
                tempFileName = tempFileName.replaceAll("\\?token=0F86B08B78839F00F11DC297281AFCB9","");
                Toast.makeText(context,"Download Started",Toast.LENGTH_LONG).show();


                downloadShow(url,tempFileName);

//                Toast.makeText(context,"Download Started",Toast.LENGTH_LONG).show();
//                if(download.getStatus()==AsyncTask.Status.RUNNING){
//                    Toast.makeText(context,"Another download running",Toast.LENGTH_LONG).show();
//                }else {
//                    if(url.startsWith("http://bia2vip.site")) {
//                        download.execute(url, tempFileName);
//                    }else if(url.startsWith("http://datadep.site")){
//                        String urlChange = extractLink(url);
//                        download.execute(urlChange,tempFileName);
//                    }
//                }

            }
        });



        season.setText(details.get(position).getSeasonName());
        episode.setText(details.get(position).getSeason()+" "+details.get(position).getEpisodeNumber());

        return convertView;

    }


    public void downloadShow(String url,String fileName){
        DownloadManager downloadManager = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        File file = new File(context.getExternalFilesDir("FunPlay"),fileName);
        request.setDescription("downloading...")
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                .setAllowedOverRoaming(true)
                .setTitle(fileName.substring(0,fileName.length()-4))
                .setDestinationInExternalFilesDir(context,"FunPlay",fileName);

        downloadManager.enqueue(request);

    }


//    public class Download extends AsyncTask<String,Void,String> {
//
//
//        @Override
//        protected String doInBackground(String... strings) {
//            try {
//
//                URL url = new URL(strings[0]);
//                URLConnection conn = url.openConnection();
//                int contentLength = conn.getContentLength();
//
//                File file = new File(context.getExternalFilesDir("FunPlay"),strings[1]);
//
//                DataInputStream dataInputStream = new DataInputStream(url.openStream());
//                DataOutputStream fos = new DataOutputStream(new FileOutputStream(file));
//
//
//
//                System.out.println("download start");
//                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                        .setSmallIcon(R.drawable.ic_file_download_50dp)
//                        .setContentTitle("Downloading....")
//                        .setContentText(strings[1])
//                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//                notificationManager.notify(6, mBuilder.build());
//
//
//
//                byte[] buffer = new byte[2048];
//
//                int count;
//                long total =0;
//                while ((count=dataInputStream.read(buffer))!= -1){
//                    total += count;
//
//                    fos.write(buffer,0,count);
//                }
//
//
//                dataInputStream.close();
//
//
//                fos.flush();
//                fos.close();
//
//                mBuilder.setContentTitle("Download Complete")
//                        .setContentText(strings[1]);
//                notificationManager.notify(6,mBuilder.build());
//
//
//
//                System.out.println("download complete");
//
//
//
//
//            }catch (Exception e){
//                System.out.println(e.getMessage());
//            }
//            return null;
//        }
//
//    }


    public void addRecentShows(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        final DocumentReference userReference = db.collection("Users").document(currentUser.getUid());

        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<String> fromUser;
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        fromUser = (ArrayList<String>)documentSnapshot.get("recentTvShows");

                        for(int i=0;i<fromUser.size();i++){
                            if(fromUser.get(i)!=null) {
                                if(!recents.contains(fromUser.get(i))) {
                                    recents.add(fromUser.get(i));
                                }
                            }
                            System.out.println(fromUser.get(i));
                        }
                        userReference.update("recentTvShows",recents)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        System.out.println("successful");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("failed to addd");
                                    }
                                });


                        userReference.update("watching",watching);

                    }
                }
            }
        });

    }

//    public String extractLink(String episodeLink){
//        DownloadPage downloadPage = new DownloadPage();
//        String temp="";
//        String link = "";
//        try {
//            String result = downloadPage.execute(episodeLink).get();
//            Pattern pattern = Pattern.compile("function freeDownload\\(\\)");
//            Matcher m = pattern.matcher(result);
//            while (m.find()){
//                System.out.println(m);
//                temp = result.substring(m.start());
//            }
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//
//
//        Pattern linkPattern = Pattern.compile("http:([^\"]*)\"");
//        Matcher matcher = linkPattern.matcher(temp);
//        while (matcher.find()){
//            link = temp.substring(matcher.start(), matcher.end()-1);
//            System.out.println(link);
//            break;
//        }
//
//        System.out.println("link"+link);
//
//        return link;
//
//    }


//
//    class DownloadPage extends AsyncTask<String,String,String> {
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String result = "";
//            try{
//                URL url = new URL(strings[0]);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                InputStream inputStream = urlConnection.getInputStream();
//                InputStreamReader reader = new InputStreamReader(inputStream);
//                int data = reader.read();
//                while (data!= -1){
//                    char current = (char)data;
//                    result += current;
//                    data = reader.read();
//                }
//                return result;
//            }catch (Exception e){
//                System.out.println(e.getMessage());
//                return null;
//            }
//
//        }
//
//
//    }




}
