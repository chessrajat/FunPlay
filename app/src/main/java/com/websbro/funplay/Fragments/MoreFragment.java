package com.websbro.funplay.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1beta1.FirestoreGrpc;
import com.websbro.funplay.Activities.DMCA;
import com.websbro.funplay.Activities.FeedbackActivity;
import com.websbro.funplay.Activities.NewsActivity;
import com.websbro.funplay.Activities.SignInActivity;
import com.websbro.funplay.R;

public class MoreFragment extends Fragment {

    ImageView profileImage;
    TextView profileName;
    TextView dmca;
    TextView feedback;
    TextView share;
    TextView news;
    TextView logOut;
    Context context;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.more_fragment,container,false);
        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        dmca = view.findViewById(R.id.dmca);
        share = view.findViewById(R.id.share);
        news = view.findViewById(R.id.news);
        feedback = view.findViewById(R.id.more_feedback);
        logOut = view.findViewById(R.id.log_out);
        context = getActivity();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();


        if(currentUser!=null) {
            getProfile();
        }

        dmca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                animation1.setDuration(800);
                v.startAnimation(animation1);

                Intent intent = new Intent(context,DMCA.class);
                context.startActivity(intent);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                animation1.setDuration(800);
                v.startAnimation(animation1);

                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                    String sAux = "\nBest appication to watch Tv shows for free. New added daily\n\n";
                    sAux = sAux + "http://funplay.gq/";
                    i.putExtra(Intent.EXTRA_TEXT,sAux);
                    startActivity(Intent.createChooser(i, "Share"));
                } catch(Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        if(currentUser!=null) {
            feedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                    animation1.setDuration(800);
                    v.startAnimation(animation1);

                    Intent intent = new Intent(context, FeedbackActivity.class);
                    context.startActivity(intent);
                }
            });

            news.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                    animation1.setDuration(800);
                    v.startAnimation(animation1);

                    Intent intent = new Intent(context,NewsActivity.class);
                    context.startActivity(intent);
                }
            });


            logOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                    animation1.setDuration(800);
                    v.startAnimation(animation1);

                    mAuth.signOut();
                    Intent intent = new Intent(context,SignInActivity.class);
                    context.startActivity(intent);
                }
            });

        }else {
            logOut.setVisibility(View.GONE);
            Toast.makeText(context,"Login first",Toast.LENGTH_SHORT).show();
        }



        return view;
    }

    public void getProfile() {
        String uid = mAuth.getCurrentUser().getUid();

        if (uid != null) {
            DocumentReference userReference = db.collection("Users").document(uid);
            userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        String picPath = documentSnapshot.getString("photoUrl");
                        String name = documentSnapshot.getString("name");
                        profileName.setText("Welcome\n"+name);
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.ic_loading_circles);
                        Glide.with(context)
                                .load(picPath)
                                .apply(requestOptions)
                                .into(profileImage);
                    }
                }
            });
        }
    }
}
