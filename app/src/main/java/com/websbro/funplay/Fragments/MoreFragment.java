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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.websbro.funplay.Activities.SignInActivity;
import com.websbro.funplay.R;

public class MoreFragment extends Fragment {

    ImageView profileImage;
    TextView profileName;
    TextView dmca;
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
        logOut = view.findViewById(R.id.log_out);
        context = getActivity();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        getProfile();

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(context,SignInActivity.class);
                context.startActivity(intent);
            }
        });

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
