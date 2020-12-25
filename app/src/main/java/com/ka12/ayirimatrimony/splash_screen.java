package com.ka12.ayirimatrimony;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class splash_screen extends AppCompatActivity {
    public static final String LOGIN = "com.ka12.ayiri_matrimony_login_details";
    LinearLayout back;
    TextView name;
    Boolean is_logged_in;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        back = findViewById(R.id.back);
        name = findViewById(R.id.name);
        try {
            ActionBar actionBar = getSupportActionBar();
            assert actionBar != null;
            actionBar.hide();
            //changing status bar color
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#6ABEDF"));
            //dark text in status bar
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            //getting the login details
            SharedPreferences edit = getSharedPreferences(LOGIN, MODE_PRIVATE);
            is_logged_in = edit.getBoolean("login", false);
            YoYo.with(Techniques.FadeIn).duration(1300).repeat(0).playOn(name);
        }catch (Exception e)
        {
            e.printStackTrace();
            Log.d("error","in start :"+e.getMessage());
        }
        //defining the runnable
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    //TODO:check the target intent
                    Intent in;
                    if (is_logged_in)
                    {
                        in = new Intent(splash_screen.this,com.ka12.ayirimatrimony.MainActivity.class);
                    } else
                        {
                        in = new Intent(splash_screen.this,com.ka12.ayirimatrimony.Login.class);
                        }
                    startActivity(in);
                    Animatoo.animateZoom(splash_screen.this);
                    finish();
                }catch (Exception e)
                {
                    Log.d("error ","catch in splash screen :"+e.getMessage());
                    e.printStackTrace();
                }
            }
        }, 1550);
    }
}