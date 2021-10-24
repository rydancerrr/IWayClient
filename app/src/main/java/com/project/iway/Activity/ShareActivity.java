package com.project.iway.Activity;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.iway.Model.Data;
import com.project.iway.Model.Token;
import com.project.iway.R;
import com.project.iway.Util.CameraIntentFragment1;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class ShareActivity  extends AppCompatActivity {
    //Initialization of variables
    Location currentLocation;
    private static final int REQUEST_CODE = 101;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button btnAdd, btnCancel;
    private ImageView imageView;
    private DatabaseReference databaseReference;
    private DatabaseReference tokenReference;
    private StorageReference storageReference;
    DocumentReference documentReference;
    FirebaseFirestore fStore;
    String getAddress;
    FirebaseMessaging firebaseMessaging;
    FirebaseAuth fAuth;
    CameraIntentFragment1 cameraIntentFragment1;
    FragmentTransaction ft;
    private String editImageUrl;
    TextView locationview;
    String fname;
    Fragment prev;
    CameraIntentFragment1 callFragment;
    String tokenValue;
    List<String> tokens;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentLocation = extras.getParcelable("Location");
        }

        firebaseMessaging = FirebaseMessaging.getInstance();
        //Initialize Firebase API
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("data");
        tokenReference = FirebaseDatabase.getInstance().getReference("token");
        storageReference = FirebaseStorage.getInstance().getReference("imageFolder");
        documentReference = fStore.collection("users").document( fAuth.getCurrentUser().getUid());

        //Set the content of xml
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);
        imageView = findViewById(R.id.imageView);
        locationview = findViewById(R.id.locationhere);


        //Call method and passing currentLocation parameter
        geocoder(currentLocation);
        openCamera();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call method addData
                if(editImageUrl != null ){
                    addData();
                } else Toast.makeText(ShareActivity.this, "Please capture a photo", Toast.LENGTH_SHORT).show();

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }




        //This is method is called from the Camera Intent Fragment to pass data from the Fragment back tothe Share Activity
    public void value(String base64, File file) {
//        Toast.makeText(ShareActivity.this, "Captured Image Completed!", Toast.LENGTH_SHORT).show();
        byte[] imageByteArray = Base64.decode(base64, Base64.DEFAULT);


        Uri uri = Uri.fromFile(file);

        //Firebase Storage geetting references to store image url
        final StorageReference Imagename = storageReference.child("image" + uri.getLastPathSegment());
        Imagename.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        editImageUrl = String.valueOf(uri);
                    }
                });
            }
        });
    }

    //Geocoder, This method class gets the location parameter to get address from the given coordinates
    public void geocoder(Location location){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                this.getAddress = address;
                locationview.setText(address);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addData() {
        Date currentTime = Calendar.getInstance().getTime();
        if( fAuth.getCurrentUser() != null){
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                           fname = (String) document.get("fName");
                            String id = databaseReference.push().getKey();
                            //get all info into Data model
                            Data data = new Data(id, fname, currentTime, getAddress,"Pending", currentLocation.getLongitude(),currentLocation.getLatitude(),editImageUrl);
                            //set Data into the Firebase Database
                            databaseReference.child(id).setValue(data);

                            TokenList();
                            Dialog();

                        } else {
                            Toast.makeText(ShareActivity.this,"No Document Exist", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Toast.makeText(ShareActivity.this,"Error: Failed!", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });

        }


    }

    private void TokenList() {
        tokens = new ArrayList<>();
        tokenReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    Token token = s.getValue(Token.class);
                    assert token != null;
                    String tokenValue = token.getToken();
                    tokens.add(tokenValue);
                }

                for(int i = 0; i < tokens.size(); i++){
                    sendPushdNotification(getAddress,currentLocation.getLongitude(),currentLocation.getLatitude() , "New Incident Report", tokens.get(i));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("MapsActivity", "loadPost:onCancelled", databaseError.toException());
            }
        });



    }


    public void Dialog(){
        //Infalting the view with the custom layout on Dialog
        LayoutInflater mInflater =(LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.success_sent, null);
        Dialog dialog = new Dialog(ShareActivity.this, R.style.Theme_Dialog);
        dialog.setContentView(view);
        dialog.show();

        callFragment.dismiss();
        dialog.dismiss();
        ShareActivity.super.finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

//        Button backButton = view.findViewById(R.id.back_button);
//
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                finish();
//            }
//        });

    }



    private void openCamera(){
        //Initialize Fragment since Camera Intent extends Fragment
         ft = this.getSupportFragmentManager().beginTransaction();
       prev = this.getSupportFragmentManager().findFragmentByTag("cDialog");
        FragmentManager fg = getSupportFragmentManager();


        //Show CameraIntentFragment
         callFragment = new CameraIntentFragment1();
//        callFragment.setTargetFragment(callFragment, 7);
        callFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        callFragment.show(ft, "cDialog");


    }

    public void callAdd()
    {
        btnAdd.callOnClick();
    }



    public static void sendPushdNotification(final String body, final double longitude, final double latitude, final String title, final String fcmToken) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    JSONObject notificationJson = new JSONObject();
                    JSONObject dataJson = new JSONObject();
                    notificationJson.put("body", body);
                    notificationJson.put("title", title);
                    notificationJson.put("priority", "high");
                    dataJson.put("customId", "02");
                    dataJson.put("badge", 1);
                    dataJson.put("alert", "Alert");
                    dataJson.put("longitude", longitude);
                    dataJson.put("latitude", latitude);
                    json.put("notification", notificationJson);
                    json.put("data", dataJson);
                    json.put("to", fcmToken);
                    RequestBody body1 = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=AAAA3XGI_SM:APA91bHyS0n3D1NXw9SHF5o7t1J9_H3y_fLMqV5A-PY10rkFNBiWm6rC9UrXH07J-LmngjM7ptAIoR_adtDPaMoTFqVWt2QvtWSlcn77I45AUy2OhyeKxgmjFPWgnqHD-4RzGVrHXJX1")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body1)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                    Log.i("TAG", finalResponse);
                } catch (Exception e) {

                    Log.i("TAG", e.getMessage());
                }
                return null;

            }


        }.execute();

    }



}