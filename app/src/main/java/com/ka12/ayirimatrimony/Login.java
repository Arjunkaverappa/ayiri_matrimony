package com.ka12.ayirimatrimony;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    CardView sub_card, otp_card;
    TextInputEditText get_number;
    PinView otp;
    Button submit, submit_otp;
    TextView note;
    //otp send by the server
    String received_otp_from_server;
    Boolean is_connected;
    //trying countdown timer
    CountDownTimer countDownTimer;
    long time_left_in_mili=61000;
    //login details
    public static final String LOGIN="com.ka12.ayiri_matrimony_login_details";
    //database entries
    DatabaseReference reference;
    String data="";
    int count=0;
    public static final String PHONE="com.ka12.ayiri_matrimony_phone_number_is_saved_here";
    public static final String IS_OLD="com.ka12.ayiri_matrimony_checking_for_previous_entries";
    public static final String KEY = "com.ka12.ayiri_matrimony_this_is_where_key_is_stored";
    public static final String D_LINK = "com.ka12.ayiri_matrimony.this_is_where_download_link_is_saved";
    public static final String GENDER = "com.ka12.ayiri_matrimony_this_is_where_gender_is_stored";
    public static final String NAME = "com.ka12.ayiri_matrimony_this_is_where_name_is_stored";
    public static final String FAMILY = "com.ka12.ayiri_matrimony_this_is_where_family_is_stored";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //hiding the action bar
        ActionBar act = getSupportActionBar();
        assert act != null;
        act.hide();
        sub_card = findViewById(R.id.sub_card);
        otp_card = findViewById(R.id.otp_card);
        get_number = findViewById(R.id.number);
        otp = findViewById(R.id.otp);
        submit = findViewById(R.id.submit);
        submit_otp = findViewById(R.id.get_otp);
        note = findViewById(R.id.note);
        //changing status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#A374ED"));
        //hiding the otp card
        otp_card.setVisibility(View.GONE);
        //resetting is_old preferences
        SharedPreferences.Editor getstatus=getSharedPreferences(IS_OLD,MODE_PRIVATE).edit();
        getstatus.putBoolean("isold",false).apply();
        //setting up onclick listeneres
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.requireNonNull(get_number.getText()).toString().equals("")) {
                    Toast.makeText(Login.this, "Please enter the number first", Toast.LENGTH_SHORT).show();
                } else if (Objects.requireNonNull(get_number.getText()).toString().length() < 10) {
                    Toast.makeText(Login.this, "Please check the number", Toast.LENGTH_SHORT).show();
                } else if(is_connected)
                {
                    sendvarification(get_number.getText().toString().trim());
                    Log.d("login ", "sent number " + get_number.getText().toString().trim());
                    sub_card.setVisibility(View.GONE);
                    otp_card.setVisibility(View.VISIBLE);
                    //hiding the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    start_timer();
                    //we are not calling this function for now
                   // check_if_old_account(get_number.getText().toString().trim());
                }else
                {
                    Toast.makeText(Login.this, "Please connect to internet and try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        submit_otp.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (Objects.requireNonNull(otp.getText()).toString().trim().length() < 6) {
                    Toast.makeText(Login.this, "Please check the otp", Toast.LENGTH_SHORT).show();
                } else {
                    varify_code(Objects.requireNonNull(otp.getText()).toString().trim());
                    Log.d("login ", "sent code " + otp.getText().toString().trim());
                    countDownTimer.cancel();
                    submit_otp.setText("Varifying...");
                }
            }
        });
        //TODO:remove the following code
        submit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view)
            {
                Intent in=new Intent(Login.this,user_data.class);
                startActivity(in);
                finish();
                return false;
            }
        });
        check_network();
    }

    private void sendvarification(String number) {
        try {
            PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + number, 60, TimeUnit.SECONDS, this, mCallbacks);
        } catch (Exception e)
        {
            Toast.makeText(this, "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("login ", "something went wrong in send varification :" + e.getMessage());
        }
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            //automatic varification of the code
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                Log.d("login ", "trying to initiate automatic varification");
                varify_code(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            //do the following operations when failed
            countDownTimer.cancel();
            Toast.makeText(Login.this, "Varification failed :" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("login ", "failed inside onarification failed :" + e.getMessage());
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //s is the code sent by the server
            received_otp_from_server = s;
        }
    };

    public void varify_code(String code) {
        try {
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(received_otp_from_server, code);
            signin(phoneAuthCredential);
        } catch (Exception e) {
            Log.d("login ", "error in varify_code :" + e.getMessage());
            countDownTimer.cancel();
        }
    }

    private void signin(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                //TODO save the login details here
                //TODO pass the phone number to next activity
                if (task.isSuccessful())
                {
                    //saving the login details  //testing
                 //   SharedPreferences.Editor edit=getSharedPreferences(LOGIN,MODE_PRIVATE).edit();
                 //   edit.putBoolean("login",true).apply();
                    //saving the phone number
                    SharedPreferences.Editor save=getSharedPreferences(PHONE,MODE_PRIVATE).edit();
                    save.putString("key",get_number.getText().toString().trim()).apply();

                    Toast.makeText(Login.this, "Login successfull", Toast.LENGTH_SHORT).show();
                    Intent succ = new Intent(Login.this, com.ka12.ayirimatrimony.user_data.class);
                    startActivity(succ);
                    finish();
                    Animatoo.animateZoom(Login.this);
                } else {
                    Toast.makeText(Login.this, "Error :" + task.getException(), Toast.LENGTH_SHORT).show();
                }
                countDownTimer.cancel();
            }
        });
    }

    public void check_network()
    {
        new Handler().postDelayed(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifi_conn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo data_conn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if ((wifi_conn != null && wifi_conn.isConnected()) || (data_conn != null && data_conn.isConnected()))
                {
                    is_connected = true;
                    note.setTextColor(Color.GRAY);
                    note.setText(R.string.note);
                } else
                    {
                    note.setTextColor(Color.RED);
                    note.setText("Please connect to internet");
                    is_connected = false;
                }
                check_network();
            }
        }, 3000);
    }
    public void start_timer()
    {
    countDownTimer=new CountDownTimer(time_left_in_mili,1000) {
        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long l)
        {
            time_left_in_mili=l;
            long seconds=time_left_in_mili/1000;
            submit_otp.setText("submit("+seconds+")");
        }

        @Override
        public void onFinish()
        {
           countDownTimer.cancel();
            final AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this);
            dialog.setTitle("We could not reach " + get_number.getText().toString());
            dialog.setMessage("Check your network connectivity and try again.");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    Intent restart = new Intent(Login.this, Login.class);
                    finish();
                    startActivity(restart);
                }
            });
            dialog.show();
        }
    }.start();

    }
    // androidx.browser:browser:1.2.0
    // implementation 'com.android.support:customtabs:28.0.0'
    public void check_if_old_account(String number)
    {
        data="";
       Log.d("snap ","inside check if");
       reference= FirebaseDatabase.getInstance().getReference().child("male").child(number);
       reference.addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
           {
             count++;
             String ss=snapshot.getValue(String.class);
             data=ss+"#"+data;
             Log.d("snap ",ss);
             //TODO chenge the count if it changes in future=done
             if(count==1)
             {
                 assign_values(data,number);
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
        reference= FirebaseDatabase.getInstance().getReference().child("female").child(number);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                String ss=snapshot.getValue(String.class);
                data=data+ss;
                Log.d("snap ",ss);
                if(count==7)
                {
                    assign_values(data,number);
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
    public void assign_values(String data,String number)
    {
        String[] sep=data.split("\\#");
        String name=sep[2];
        String gender=sep[4];
        String family=sep[5];
        String image_d_link=sep[3];
        Log.d("snap "," name= "+name+" gender ="+gender+" download link "+image_d_link);
        //saving the values in shared preferences
        SharedPreferences.Editor getstatus=getSharedPreferences(IS_OLD,MODE_PRIVATE).edit();
        getstatus.putBoolean("isold",true).apply();

        SharedPreferences.Editor getname=getSharedPreferences(NAME,MODE_PRIVATE).edit();
        getname.putString("name",name).apply();

        SharedPreferences.Editor getgender=getSharedPreferences(GENDER,MODE_PRIVATE).edit();
        getgender.putString("gender",gender).apply();

        SharedPreferences.Editor getimage=getSharedPreferences(D_LINK,MODE_PRIVATE).edit();
        getimage.putString("link",image_d_link).apply();

        SharedPreferences.Editor getkey=getSharedPreferences(KEY,MODE_PRIVATE).edit();
        getkey.putString("key",number).apply();

        SharedPreferences.Editor getfamily=getSharedPreferences(FAMILY,MODE_PRIVATE).edit();
        getfamily.putString("family",family).apply();
    }
}

















