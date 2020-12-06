package com.ka12.ayirimatrimony;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class splash_screen extends AppCompatActivity {
    public static final String LOGIN="com.ka12.ayiri_matrimony_login_details";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        SharedPreferences edit=getSharedPreferences(LOGIN,MODE_PRIVATE);
        Boolean is_logged_in=edit.getBoolean("login",false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                if(is_logged_in)
                {
                    Intent in = new Intent(splash_screen.this, com.ka12.ayirimatrimony.MainActivity.class);
                    startActivity(in);
                    finish();
                }else {
                    Intent in = new Intent(splash_screen.this, com.ka12.ayirimatrimony.user_data.class);
                    startActivity(in);
                    finish();
                }
            }
        },1500);
    }
}