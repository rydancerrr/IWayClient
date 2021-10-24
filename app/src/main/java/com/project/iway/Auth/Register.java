package com.project.iway.Auth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.iway.R;
import com.project.iway.Util.Sample;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mFullName,mEmail,mPassword, mDate;
    Button mRegisterBtn;
    TextView mLoginBtn, terms;
    FirebaseAuth fAuth;
    RadioButton radioBtn, termRadio;
    RadioGroup radio;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFullName   = findViewById(R.id.fullName);
        mRegisterBtn= findViewById(R.id.registerBtn);
        mLoginBtn   = findViewById(R.id.createText);
        mEmail = findViewById(R.id.Email1);
        mPassword = findViewById(R.id.password);
        mDate = findViewById(R.id.birthDate);
        terms = findViewById(R.id.conditions);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        termRadio = findViewById(R.id.terms);

        radio = findViewById(R.id.radio);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    register();


            }
        });


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Register.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = Register.this.getSupportFragmentManager().beginTransaction();
                Fragment prev =  Register.this.getSupportFragmentManager().findFragmentByTag("Register");

                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                //Show CameraIntentFragment
                Terms terms = new Terms();
                terms.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
                terms.show(ft, "Register");

            }
        });

    }

    private void register() {
        int radioSelected = radio.getCheckedRadioButtonId();
        radioBtn = findViewById(radioSelected);

        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();
        final String fullName = mFullName.getText().toString();
        final String date = mDate.getText().toString();
        final String radio = radioBtn.getText().toString();

        if(TextUtils.isEmpty(email)){
            mEmail.setError("Email is Required.");
            return;
        }

        if(TextUtils.isEmpty(password)){
            mPassword.setError("Password is Required.");
            return;
        }

        if(password.length() < 6){
            mPassword.setError("Password Must be >= 6 Characters");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // register the user in firebase
        fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                    userID = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    Map<String, Object> user = new HashMap<>();
                    user.put("fName",fullName);
                    user.put("email",email);
                    user.put("password",password);
                    user.put("date",date);
                    user.put("role", radio);
                    user.put("profile",null);
                    user.put("workname", null);
                    user.put("work_place", null);
                    user.put("work_position", null);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.toString());
                        }
                    });
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    startActivity(new Intent(getApplicationContext(), Sample.class));
                    if(radio.equals("Road Responder (MDRRMO/Red Cross)")){
                        startActivity(new Intent(getApplicationContext(), Responder.class));
                    }

                }else {
                    Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }


            }
        });
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

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

        mDate.setText(sdf.format(myCalendar.getTime()));
    }



}