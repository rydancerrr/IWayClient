package com.project.iway.Auth;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.project.iway.R;


public class Responder extends AppCompatActivity {
    //Initialization of variables
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    DocumentReference documentReference;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    TextView work_place, work_address, work_position;
    Button mRegisterBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.responder);
        work_address = findViewById(R.id.work_address);
        work_place =findViewById(R.id.work_name);
        work_position = findViewById(R.id.work_position);
        mRegisterBtn= findViewById(R.id.registerBtn);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String userID = fAuth.getCurrentUser().getUid();
        documentReference = fStore.collection("users").document(userID);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    public void save(){
        documentReference.update("workname",  work_place.getText().toString());
        documentReference.update("work_place", work_address.getText().toString());
        documentReference.update("work_position",  work_position.getText().toString());
        onBackPressed();
    }
}