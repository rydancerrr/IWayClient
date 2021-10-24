package com.project.iway.Util;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.project.iway.Auth.Login;
import com.project.iway.R;


public class SplashScreen extends AppCompatActivity
{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, Login.class);
                SplashScreen.this.startActivity(intent);
                SplashScreen.this.finish();
            }
        }, 1500);

    }

}