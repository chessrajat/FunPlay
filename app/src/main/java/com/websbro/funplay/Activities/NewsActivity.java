package com.websbro.funplay.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.websbro.funplay.R;

public class NewsActivity extends AppCompatActivity {

    TextView news1;
    TextView news2;
    TextView news3;
    TextView news4;
    TextView news5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);


        news1 = findViewById(R.id.news1);
        news2 = findViewById(R.id.news2);
        news3 = findViewById(R.id.news3);
        news4 = findViewById(R.id.news4);
        news5 = findViewById(R.id.news5);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("token").document("news");

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String temp1 = documentSnapshot.getString("news1");
                    if(temp1!=null && temp1.length()>0){
                        news1.setText(temp1);
                        news1.setVisibility(View.VISIBLE);
                    }

                    String temp2 = documentSnapshot.getString("news2");
                    if(temp2!=null && temp2.length()>0){
                        news2.setText(temp2);
                        news2.setVisibility(View.VISIBLE);
                    }

                    String temp3 = documentSnapshot.getString("news3");
                    if(temp3!=null && temp3.length()>0){
                        news3.setText(temp3);
                        news3.setVisibility(View.VISIBLE);
                    }

                    String temp4 = documentSnapshot.getString("news4");
                    if(temp4!=null && temp4.length()>0){
                        news4.setText(temp4);
                        news4.setVisibility(View.VISIBLE);
                    }

                    String temp5 = documentSnapshot.getString("news5");
                    if(temp5!=null && temp5.length()>0){
                        news5.setText(temp5);
                        news5.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}
