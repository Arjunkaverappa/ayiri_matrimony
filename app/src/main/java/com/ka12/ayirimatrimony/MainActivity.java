package com.ka12.ayirimatrimony;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;

public class MainActivity extends AppCompatActivity {
    // BubbleNavigationLinearView bottom_bar;
    long mback_pressed;
    int TIME_INTERVAL_BACK = 2000;
    BubbleNavigationConstraintView bottom_bar;
    FrameLayout change_frag;
    LinearLayout main_layout, profile_frag;
    ListView list_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        profile_frag = findViewById(R.id.profile_frag);
        // check = findViewById(R.id.change);
        list_name = findViewById(R.id.list_name);
        bottom_bar = findViewById(R.id.bottom_bar);
        change_frag = findViewById(R.id.change_frag);
        main_layout = findViewById(R.id.main_layout);
        //fragment switching
        main_layout.setVisibility(View.GONE);
        FragmentManager goto_frag = getSupportFragmentManager();
        goto_frag.beginTransaction().add(R.id.change_frag, new home()).commit();
        // bottom_bar.setVisibility(View.GONE);

        bottom_bar.setCurrentActiveItem(2);
        bottom_bar.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                switch (position) {
                    case 0:
                        bottom_bar.setBackgroundColor(Color.parseColor("#A374ED"));
                        main_layout.setVisibility(View.GONE);
                        FragmentManager received = getSupportFragmentManager();
                        received.beginTransaction().replace(R.id.change_frag,
                                new match()).remove(new profile()).remove(new match()).remove(new home()).remove(new sent()).commit();
                        break;
                    case 1:
                        bottom_bar.setBackgroundColor(Color.RED);
                        main_layout.setVisibility(View.GONE);
                        FragmentManager settings = getSupportFragmentManager();
                        settings.beginTransaction().replace(R.id.change_frag,
                                new received()).remove(new profile()).remove(new match()).remove(new sent()).remove(new home()).commit();
                        break;
                    case 2:
                        bottom_bar.setBackgroundColor(Color.parseColor("#000000"));
                        main_layout.setVisibility(View.VISIBLE);
                        FragmentManager home = getSupportFragmentManager();
                        home.beginTransaction().replace(R.id.change_frag,
                                new home()).remove(new match()).remove(new profile()).remove(new sent()).remove(new received()).commit();
                        break;
                    case 3:
                        bottom_bar.setBackgroundColor(Color.parseColor("#66bb6a"));
                        main_layout.setVisibility(View.GONE);
                        FragmentManager sent = getSupportFragmentManager();
                        sent.beginTransaction().replace(R.id.change_frag, new sent()).remove(new match()).commit();
                        break;
                    case 4:
                        bottom_bar.setBackgroundColor(Color.parseColor("#ED8A6B"));
                        main_layout.setVisibility(View.GONE);
                        FragmentManager profile = getSupportFragmentManager();
                        profile.beginTransaction().replace(R.id.change_frag, new profile()).remove(new match()).remove(new home()).commit();
                        break;
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mback_pressed + TIME_INTERVAL_BACK > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            mback_pressed = System.currentTimeMillis();
        }
    }
}