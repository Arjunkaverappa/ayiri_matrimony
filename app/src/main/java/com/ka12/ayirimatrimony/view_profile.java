package com.ka12.ayirimatrimony;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class view_profile extends AppCompatActivity {
    //testing
    public static final String KEY = "com.ka12.ayiri_matrimony_this_is_where_key_is_stored";
    public static final String FAMILY = "com.ka12.ayiri_matrimony_this_is_where_family_is_stored";
    public static final String NAME = "com.ka12.ayiri_matrimony_this_is_where_name_is_stored";
    String name, family, age, height, qua, desc, work, father, mother, gender, place, token, job, profile_key, link, user_key, push_data;
    Handler handler = new Handler();
    Boolean is_connected;
    DatabaseReference reference;
    FirebaseDatabase firebaseDatabase;
    int s_count = 0;
    TextView u_name,u_age,u_place,u_height,u_work,u_father,u_qua,u_bio;
    CircleImageView image;
    Button send;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        u_name = findViewById(R.id.u_name);
        image = findViewById(R.id.image);
        u_age=findViewById(R.id.age);
        u_place=findViewById(R.id.place);
        u_height=findViewById(R.id.height);
        u_work=findViewById(R.id.work);
        u_father=findViewById(R.id.father);
        u_qua=findViewById(R.id.qua);
        u_bio=findViewById(R.id.bio);
        send=findViewById(R.id.send);
        //setting up action bar and notification bar
        ActionBar a = getSupportActionBar();
        if (a != null) a.hide();
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        //retreiving user key
        SharedPreferences ediss = getSharedPreferences(KEY, MODE_PRIVATE);
        user_key = ediss.getString("key", "999999999");
        Log.d("start", "key :" + user_key);

        //retrieving the values from explore
        Intent get = getIntent();
        name = get.getStringExtra("name");
        family = get.getStringExtra("family");
        age = get.getStringExtra("age");
        height = get.getStringExtra("height");
        gender = get.getStringExtra("gender");
        qua = get.getStringExtra("qua");
        desc = get.getStringExtra("desc");
        work = get.getStringExtra("work");
        father = get.getStringExtra("father");
        mother = get.getStringExtra("mother");
        place = get.getStringExtra("place");
        job = get.getStringExtra("job");
        profile_key = get.getStringExtra("key");
        link = get.getStringExtra("link");
        token = get.getStringExtra("token");
        Log.d("receivedz ", "name :" + name + "\nfamily :" + family + "\nage :" + age + "\nheight :" + height);
        Log.d("receivedz ", "qualification :" + qua + "\ndesc :" + desc + "\nwork :" + work + "\nfather :" + father);
        Log.d("receivedz ", "mother :" + mother + "\nplace :" + place + "\njob :" + job + "\nkey :" + profile_key + "\nlink :" + link + "\ntoken " + token);
        //setting up values
        Picasso.get().load(link).into(image);
        u_name.setText(family + " " + name);
        u_age.setText(age+", "+gender);
        u_place.setText(" Currently living in "+place);
        u_height.setText(" "+height);
        u_qua.setText(" Completed "+qua);
        u_work.setText(" Working at "+work);
        u_bio.setText(desc);
        if(gender.equals("male"))
        {
            u_father.setText(" S/O "+father+" and "+mother);
        }else u_father.setText(" D/O "+father+" and "+mother);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_connected)
                {
                    retreive_current_node();
                }else
                {
                    Toast.makeText(view_profile.this, "Please connect to internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void check_network() {
        try {
            handler.postDelayed(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo wifi_conn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    NetworkInfo data_conn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                    if ((wifi_conn != null && wifi_conn.isConnected()) || (data_conn != null && data_conn.isConnected())) {
                        is_connected = true;
                    } else {
                        is_connected = false;
                        check_network();
                    }
                }
            }, 3000);

        } catch (Exception e) {
            Log.d("error ", "catch in check_network :" + e.getMessage());
        }
    }

    public void retreive_current_node() {
        try {
            Log.d("send ", "sender key   :" + user_key);
            Log.d("send ", "receiver key :" + profile_key);
            if (user_key.equals(profile_key)) {
                Toast.makeText(this, "You cant sent request to yourself!", Toast.LENGTH_SHORT).show();
            } else {
                //retrieving the existing requests before sending
                firebaseDatabase = FirebaseDatabase.getInstance();
                reference = firebaseDatabase.getReference().child(gender).child(profile_key);
                reference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        //we know that received is at position 2 in database
                        s_count++;
                        if (s_count == 2) {
                            String tempo = snapshot.getValue(String.class);
                            Log.d("loop ", "tempo for request :" + tempo);
                            send_request_finally_ultra(tempo);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        } catch (Exception e) {
            Log.d("error ", "catch in send_request_pro :" + e.getMessage());
        }
    }

    public void send_request_finally_ultra(String data) {
        try {
            Log.d("send", "Entered send request finally ultra");
            //key is the receiver key and tempo is passed as data

            Log.d("send ", "user key :" + user_key);

            //creating the new push data for received
            push_data = user_key + ":" + data;
            //updating received node in receiver account
            firebaseDatabase = FirebaseDatabase.getInstance();
            reference = firebaseDatabase.getReference().child(gender).child(profile_key).child("received");
            reference.setValue(push_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(view_profile.this, "Request sent", Toast.LENGTH_SHORT).show();
                    Log.d("send ", "pushed successfully");
                    //sending notification
                    send_notification();
                }
            });

            push_data = "";
            s_count = 0;
        } catch (Exception e) {
            Log.d("error ", "catch in send_request_finally_ultra:" + e.getMessage());
        }
    }

    public void send_notification() {
        try {
            String user_id = token;
            String sender_name = name;
            String sender_family = family;

            //retreving user family
            SharedPreferences getfamily = getSharedPreferences(FAMILY, MODE_PRIVATE);
            String user_family = getfamily.getString("family", "null");

            //retreiving the user_name
            SharedPreferences getname = getSharedPreferences(NAME, MODE_PRIVATE);
            String user_name = getname.getString("name", "null");

            String message = "Hey " + sender_name + ", you have received a request from " + user_family + " " + user_name;
            Log.d("json", "player id=" + user_id);
            try {
                OneSignal.postNotification(new JSONObject("{'contents': {'en':'" + message + "'}, 'include_player_ids': ['" + user_id + "']}"), null);
            } catch (JSONException e) {
                Log.d("json", "Error :" + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.d("error ", "catch in send_notifications :" + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacksAndMessages(null);
        super.onBackPressed();
    }
}