package com.example.android.pets.pets;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //intent to navigate  QuestionNo activity
                Intent i=new Intent(SplashActivity.this, CatalogActivity.class);
                startActivity(i);
            }
        }, 1000);




    }
    // prevents user pressing back button
    @Override
    public void onBackPressed() {

    }
}