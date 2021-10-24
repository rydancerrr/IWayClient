package com.project.iway.Activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.project.iway.Auth.Login;
import com.project.iway.Model.Data;
import com.project.iway.Model.Token;
import com.project.iway.Nav.About;
import com.project.iway.Nav.Help;
import com.project.iway.Nav.History;
import com.project.iway.Nav.Profile;
import com.project.iway.Nav.Setting;
import com.project.iway.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback{


    Marker marker;
    private DatabaseReference mUsers;
    private GoogleMap mMap;
    public Location lastLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    Button button, menu;
    FirebaseAuth fAuth;
    Bundle bundle;
    boolean bool;
    String tokenValue;
    private DatabaseReference tokenReference;
    FirebaseFirestore fStore;
    Button history, logout, help, aboutus, setting;
    TextView locationview, profile, profile_name, profile_mobile, profile_bio;
    ImageView profileImage;
    private DatabaseReference databaseReference;
    String role1;
    DocumentReference documentReference;
    List<String> tokens = new ArrayList<>();

    private static final int REQUEST_CODE = 101;
    private DrawerLayout drawer = null;
    private ActionBarDrawerToggle toggle = null;
    private NavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
        button = findViewById(R.id.button);
        locationview = findViewById(R.id.currentLocation);
        databaseReference = FirebaseDatabase.getInstance().getReference("data");
        tokenReference = FirebaseDatabase.getInstance().getReference("token");

        if(fAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finishAffinity();
        } else {
                location();
        }


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String tokenValue = task.getResult();
                        String id = tokenReference.push().getKey();
                        //get all info into Data model
                        Token token = new Token(id, tokenValue);
                        assert id != null;
                        //set Data into the Firebase Database

                        tokenReference.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot s : dataSnapshot.getChildren()) {
                                    Token token = s.getValue(Token.class);
                                    assert token != null;
                                    tokens.add(token.getToken());
                                    Log.d("TOKEN", token.getToken());
                                }
                                bool = true;
                                for(int i = 0; i < tokens.size(); i++){
                                       if(tokens.get(i).equals(tokenValue)){
                                             bool = false;
                                       }
                                }
                                if(bool){
                                    tokenReference.child(id).setValue(token);
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("MapsActivity", "loadPost:onCancelled", databaseError.toException());
                            }
                        });

//


                    }
                });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShareActivity.class);
                intent.putExtra("Location", lastLocation);
                startActivity(intent);

            }
        });
        drawer =  findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openNavDrawer, R.string.closeNavDrawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView = findViewById(R.id.nav_view);
        history = navigationView.findViewById(R.id.history);
        logout = navigationView.findViewById(R.id.logout);
        help = navigationView.findViewById(R.id.help);
        aboutus = navigationView.findViewById(R.id.about);
        profile = navigationView.findViewById(R.id.editProfile);
        setting = navigationView.findViewById(R.id.setting);
        profileImage = navigationView.findViewById(R.id.profile);
        profile_name = navigationView.findViewById(R.id.profile_name);
        profile_mobile = navigationView.findViewById(R.id.profile_mobile);
        profile_bio  = navigationView.findViewById(R.id.profile_bio);

        loadData();
        onClickListeners();
        bundle = getIntent().getExtras();

    }


    private void loadData() {
        String userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("CheckResult")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String profile = documentSnapshot.getString("profile");
//                String email = documentSnapshot.getString("email");
                String name = documentSnapshot.getString("fName");
                String date = documentSnapshot.getString("date");
                role1 = documentSnapshot.getString("role");
                Picasso.get().load(profile).centerCrop().fit().into(profileImage);
                profile_name.setText(name);
                profile_mobile.setText(date);
                profile_bio.setText("null");

            }
        });

    }

    private void onClickListeners() {

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFragment(1);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFragment(2);
            }
        });

        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFragment(3);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFragment(4);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFragment(5);
            }
        });




//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fAuth.signOut();
//                startActivity(new Intent(MainActivity.this, Login.class));
//                finish();
//            }
//        });
    }

    public void callFragment(int i) {
           FragmentTransaction ft = MainActivity.this.getSupportFragmentManager().beginTransaction();
           Fragment prev = MainActivity.this.getSupportFragmentManager().findFragmentByTag("history");

           if (prev != null) {
                ft.remove(prev);
            }
             ft.addToBackStack(null);


        switch(i){
            case 1:
                drawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MainActivity.this, History.class));
                break;
            case 2:
                Help help = new Help();
                drawer.closeDrawer(GravityCompat.START);
                help.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
                help.show(ft, "Help");
                break;
            case 3:
                About about = new About();
                drawer.closeDrawer(GravityCompat.START);
                about.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
                about.show(ft, "About");
                break;
            case 4:
                Profile profile = new Profile();
                profile.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
                profile.show(ft, "Profile");
                break;
            case 5:
                Setting setting = new Setting();
                setting.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
                setting.show(ft, "Setting");
                break;


        }
    }


    public void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @SuppressLint("ResourceType")
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    lastLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    View mapView = supportMapFragment.getView();
                    if (mapView != null && mapView.findViewById(1) != null) {
                        // Get the button view
                        View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
                        // and next place it, on bottom right (as Google Maps app)
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                                locationButton.getLayoutParams();
                        // position on right bottom
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                        layoutParams.setMargins(0, 0, 40, 250);
                    }
                    supportMapFragment.getMapAsync(MainActivity.this::onMapReady);

                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        if(bundle != null){
            String longi = bundle.getString("longitude");
            String lat = bundle.getString("latitude");

            final LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(longi));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        } else {
            final LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }


        mMap.getUiSettings().setMapToolbarEnabled(false);
        mUsers = FirebaseDatabase.getInstance().getReference().child("data");
        mUsers.push().setValue(marker);
        geocoder(lastLocation.getLatitude(), lastLocation.getLongitude());
        mUsers.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot s : dataSnapshot.getChildren()) {
                     Data data = s.getValue(Data.class);
                    LatLng lat = new LatLng(data.setLatitude, data.setLongitude);
                    MarkerOptions markerOptions = new MarkerOptions().position(lat).title(data.editName);;
                    Marker mMarker = mMap.addMarker(markerOptions);
                    mMarker.setTag(data);


                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            View view = MainActivity.this.getLayoutInflater().inflate(R.layout.layout_loading, null);
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setCustomTitle(view);
                            Data user = (Data) marker.getTag();
                            final AlertDialog dialog = builder.create();
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            TextView name = view.findViewById(R.id.name);
                            TextView time = view.findViewById(R.id.time);
                            TextView location = view.findViewById(R.id.location);
                            LabeledSwitch status = view.findViewById(R.id.status);
                            ImageView imageView = view.findViewById(R.id.imageView);
                            name.setText(user.getEditName());
                            time.setText(user.getEditTime().toString());
                            location.setText(user.getEditAddress());
                            if(user.getEditStatus().equals("Pending")){
                                status.setOn(false);
                            } else {
                                status.setOn(true);
                            }

                            if(getRole().equals("Standard User")){
                                status.setEnabled(false);
                            }

                            status.setOnToggledListener(new OnToggledListener() {
                                @Override
                                public void onSwitched(ToggleableView toggleableView, boolean isOn) {

                                    Query query = mUsers.orderByChild("id").equalTo(user.getId());
                                    query(query, user);
                                }

                            });


                            Glide.with(MainActivity.this).load(user.getImageUrl()).into(imageView);

                            dialog.show();

                            Button backButton = view.findViewById(R.id.back_button);
                            backButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            return  true;
                        }
                    });




                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("MapsActivity", "loadPost:onCancelled", databaseError.toException());
            }
        });

    }

    private void query(Query query, Data user) {
        query.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        //dr would refer to path                  : ApprovedEvents
                        //adding the key as a child would make it : ApprovedEvents/Record1
                        if(user.getEditStatus().equals("Pending")){
                            mUsers.child(user.getId()).child("editStatus").setValue("Solved");
                            MainActivity.super.recreate();
                        } else { mUsers.child(user.getId()).child("editStatus").setValue("Pending");
                        MainActivity.super.recreate();}

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }
                });
    }


    public void geocoder(double editLatitude, double editLonghitude){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(editLatitude, editLonghitude, 1);

            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                locationview.setText(address);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }break;
        }
    }



    public boolean isLocationEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager lm = (LocationManager) this.getBaseContext().getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
            int mode = Settings.Secure.getInt(this.getBaseContext().getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return  (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
    }


    public void location(){
        if(!isLocationEnabled()){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("GPS Location is Disabled, Turn on GPS?");
            builder1.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 516);
                    dialog.dismiss();
                }
            });
            builder1.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(MainActivity.this, "Please Open your GPS",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            AlertDialog dialog1 = builder1.create();
            dialog1.show();
        } else if (isLocationEnabled()){
            if(lastLocation == null){
                alertDialog1();
            }
        }

    }
    private String getRole() {
            return role1;
    }


    public void alertDialog1(){
        View view = Objects.requireNonNull(this.getLayoutInflater().inflate(R.layout.loading, null));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

         new CountDownTimer(4000,1000){
            @Override
            public void onFinish() {
                dialog.dismiss();
            }

            @Override
            public void onTick(long millisUntilFinished) {
            }
        }.start();

    }




}
