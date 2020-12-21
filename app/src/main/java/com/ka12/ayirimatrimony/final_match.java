package com.ka12.ayirimatrimony;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class final_match extends AppCompatActivity {
    ImageView profile;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_match);
        text=findViewById(R.id.text);
        profile=findViewById(R.id.profile);
        ActionBar a=getSupportActionBar();
        if(a!=null)
        {
            a.hide();
        }
        Intent get=new Intent();
        String name=get.getStringExtra("name");
        String family=get.getStringExtra("family");
        String age=get.getStringExtra("age");
        String link=get.getStringExtra("link");
        String desc=get.getStringExtra("desc");
        Log.d("gotit",name+" "+age+ " "+family+ " "+link+" "+desc);

        Picasso.get().load(link).into(profile);

    }
}