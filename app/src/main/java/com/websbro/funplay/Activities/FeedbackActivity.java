package com.websbro.funplay.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.websbro.funplay.R;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        final TextView feedback = findViewById(R.id.feedback);
        Button feedbackSubmit = findViewById(R.id.feedback_submit);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        final CollectionReference collectionReference = db.collection("feedback");
        feedbackSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String f = feedback.getText().toString();
                Feedback feedback1 = new Feedback(currentUser.getDisplayName(),f,currentUser.getEmail());
                collectionReference.document().set(feedback1);
                Intent intent = new Intent(FeedbackActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });


    }

    public static class Feedback{
        String name;
        String feedback;
        String email;

        public Feedback(String name, String feedback,String email) {
            this.name = name;
            this.feedback = feedback;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public String getFeedback() {
            return feedback;
        }
    }
}
