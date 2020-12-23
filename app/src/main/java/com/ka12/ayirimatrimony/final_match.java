package com.ka12.ayirimatrimony;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Picasso;

public class final_match extends AppCompatActivity {
    ImageView profile;
    TextView tvname, tvfam, tvage, tvbio, tvwork, tvqua, tvheight;
    String name, age, family, link, desc, work, height, qua, key;
    LottieAnimationView call, message;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_match);
        tvname = findViewById(R.id.tvname);
        tvfam = findViewById(R.id.tvfam);
        tvage = findViewById(R.id.tvage);
        tvbio = findViewById(R.id.tvbio);
        tvwork = findViewById(R.id.tvwork);
        tvqua = findViewById(R.id.tvqua);
        tvheight = findViewById(R.id.tvheight);

        profile = findViewById(R.id.profile);
        call = findViewById(R.id.call);
        message = findViewById(R.id.message);

        //status bar and notifiction
        ActionBar a = getSupportActionBar();
        if (a != null) a.hide();
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FFFFFF"));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //retrieving the values from the match fragment
        Intent get = getIntent();
        name = get.getStringExtra("name");
        family = get.getStringExtra("family");
        age = get.getStringExtra("age");
        link = get.getStringExtra("link");
        desc = get.getStringExtra("desc");
        work = get.getStringExtra("work");
        height = get.getStringExtra("height");
        qua = get.getStringExtra("qua");
        key = get.getStringExtra("key");
        Log.d("gotit", name + " " + age + " " + family + " " + link + " " + desc);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_name = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+91" + key));
                startActivity(intent_name);
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", "+91" + key, null)));
                }catch (Exception e)
                {
                    Toast.makeText(final_match.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    Log.d("error :",e.getMessage());
                }
                }
        });

        Picasso.get().load(link).into(profile);
        // text.setText(name + "\n" + family + "\n" + age + "\n" + desc + "\n" + work + "\n" + height + "\n" + qua);

        set_up_textviews(name, family, age, desc, work, height, qua);
    }

    public void set_up_textviews(String get_name, String get_family, String get_age, String get_desc, String get_work, String get_height, String get_qua) {
        get_name = "Name \n" + get_name;
        SpannableString s1 = new SpannableString(get_name);
        s1.setSpan(new RelativeSizeSpan(0.6f), 0, 5, 0);
        s1.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, 0);
        tvname.setText(s1);

        get_family = "Family \n" + get_family;
        SpannableString s2 = new SpannableString(get_family);
        s2.setSpan(new RelativeSizeSpan(0.6f), 0, 7, 0);
        s2.setSpan(new ForegroundColorSpan(Color.RED), 0, 7, 0);
        tvfam.setText(s2);

        get_age = "Age \n" + get_age;
        SpannableString s3 = new SpannableString(get_age);
        s3.setSpan(new RelativeSizeSpan(0.6f), 0, 4, 0);
        s3.setSpan(new ForegroundColorSpan(Color.RED), 0, 4, 0);
        tvage.setText(s3);

        get_desc = "Bio \n" + get_desc;
        SpannableString s4 = new SpannableString(get_desc);
        s4.setSpan(new RelativeSizeSpan(0.6f), 0, 4, 0);
        s4.setSpan(new ForegroundColorSpan(Color.RED), 0, 4, 0);
        tvbio.setText(s4);

        get_height = "Height \n" + get_height;
        SpannableString s5 = new SpannableString(get_height);
        s5.setSpan(new RelativeSizeSpan(0.6f), 0, 7, 0);
        s5.setSpan(new ForegroundColorSpan(Color.RED), 0, 7, 0);
        tvheight.setText(s5);

        get_qua = "Qualification \n" + get_qua;
        SpannableString s6 = new SpannableString(get_qua);
        s6.setSpan(new RelativeSizeSpan(0.6f), 0, 14, 0);
        s6.setSpan(new ForegroundColorSpan(Color.RED), 0, 14, 0);
        tvqua.setText(s6);

        get_work = "Working \n" + get_work + " sector";
        SpannableString s7 = new SpannableString(get_work);
        s7.setSpan(new RelativeSizeSpan(0.6f), 0, 7, 0);
        s7.setSpan(new ForegroundColorSpan(Color.RED), 0, 7, 0);
        tvwork.setText(s7);

    }
}