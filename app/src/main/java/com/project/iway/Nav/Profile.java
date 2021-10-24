package com.project.iway.Nav;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.iway.Activity.MainActivity;
import com.project.iway.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class Profile extends DialogFragment {
    private Button backBtn, save;
    private TextView edit, role;
    private ImageView imageView;
    private static final int REQUEST_CODE = 101;
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText profile_name, profile_birth, profile_email, profile_password;
    FirebaseAuth fAuth;
    private StorageReference storageReference;
    FirebaseFirestore fStore;
    private String email, name, date, profile, password, role1;
    Uri mImageUri;
    DocumentReference documentReference;
    final Calendar myCalendar = Calendar.getInstance();

    public Profile() {
        // Required empty public constructor
    }

    public static Profile newInstance() {
      Profile fragment = new Profile();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profile, container, false);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String userID = fAuth.getCurrentUser().getUid();
         documentReference = fStore.collection("users").document(userID);
        backBtn = view.findViewById(R.id.backBtn);
        edit = view.findViewById(R.id.text_edit);
        save = view.findViewById(R.id.save);
        imageView = view.findViewById(R.id.profile_image);
        role = view.findViewById(R.id.role);
        profile_name = view.findViewById(R.id.profile_name);
        profile_birth = view.findViewById(R.id.profile_date);
        profile_email = view.findViewById(R.id.profile_email);
        profile_password = view.findViewById(R.id.profile_password);

        storageReference = FirebaseStorage.getInstance().getReference("profile");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dismiss();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        profile_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Profile.this.getContext(), datePicker, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        loadData();

        return view;
    }

    private void loadData() {
        String userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("CheckResult")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                 email = documentSnapshot.getString("email");
                 name = documentSnapshot.getString("fName");
                 date = documentSnapshot.getString("date");
                 password = documentSnapshot.getString("password");
                 role1 = documentSnapshot.getString("role");
                profile = documentSnapshot.getString("profile");
                profile_name.setText(name);
                profile_email.setText(email);
                profile_birth.setText(date);
                profile_password.setText(password);
                role.setText(role1);
                Picasso.get().load(profile).centerCrop().fit().into(imageView);

            }
        });

    }


    public void save(){
//        if(profile_password.getText().toString() != null){
//            fAuth.getCurrentUser().updatePassword(profile_password.getText().toString().trim());
//        }

        if(profile_name.getText() == null || profile_email.getText() == null || profile_birth.getText() == null || profile_password.getText() == null  ){
            Toast.makeText(getActivity(), "Please fill the blank", Toast.LENGTH_SHORT).show();
        } else {
            documentReference.update("fName", profile_name.getText().toString());
            documentReference.update("email", profile_email.getText().toString());
            documentReference.update("date", profile_birth.getText().toString());
            documentReference.update("password", profile_password.getText().toString());
            Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
        }


        if(mImageUri != null){
            final StorageReference Imagename = storageReference.child("image" + mImageUri.getLastPathSegment());

            Imagename.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                    Imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("SUCCESS", String.valueOf(uri));
                            documentReference.update("profile",  String.valueOf(uri).trim());
                        }
                    });
                }
            });
        }
        dismiss();
        ((MainActivity)getActivity()).callFragment(4);

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            Toast.makeText(getActivity(),"Added Succesfully", Toast.LENGTH_SHORT).show();
            mImageUri = data.getData();
            Glide.with(this).asBitmap().load(mImageUri).centerCrop().into(imageView);

        }
    }


    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        profile_birth.setText(sdf.format(myCalendar.getTime()));
    }

}
