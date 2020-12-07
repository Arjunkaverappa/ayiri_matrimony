package com.ka12.ayirimatrimony;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
        check_network();
    }

    private void sendvarification(String number) {
        try {
            PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + number, 60, TimeUnit.SECONDS, this, mCallbacks);
        } catch (Exception e) {
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
                if (task.isSuccessful()) {
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
}

















