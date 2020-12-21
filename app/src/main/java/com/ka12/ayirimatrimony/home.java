package com.ka12.ayirimatrimony;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class home extends Fragment {
    public static final String NAME = "com.ka12.ayiri_matrimony_this_is_where_name_is_stored";
    LinearLayout main_layout, profile_frag;
    DatabaseReference reference;
    TextView no_net;
    ListView list_name;
    LottieAnimationView loading;
    public ArrayList<String> names = new ArrayList<>();
    public ArrayList<String> family = new ArrayList<>();
    public ArrayList<Integer> age = new ArrayList<>();
    public ArrayList<String> links = new ArrayList<>();
    public ArrayList<String> keys = new ArrayList<>();
    public ArrayList<String> gender = new ArrayList<>();
    public ArrayList<String> seen = new ArrayList<>();
    public ArrayList<String> received = new ArrayList<>();
    public ArrayList<String> received_text = new ArrayList<>();
    public static final String FAMILY = "com.ka12.ayiri_matrimony_this_is_where_family_is_stored";
    //one signal app id
    public static final String ONESIGNAL_APP_ID = "4359ad23-f128-46aa-aba3-caebf6058549";
    public ArrayList<String> height = new ArrayList<>();
    public ArrayList<String> description = new ArrayList<>();
    custom_adapter custom = new custom_adapter();
    //firebase
    FirebaseDatabase firebaseDatabase;
    public static final String KEY = "com.ka12.ayiri_matrimony_this_is_where_key_is_stored";
    public static final String GENDER = "com.ka12.ayiri_matrimony_this_is_where_gender_is_stored";
    public static final String CHILD = "com.ka12.ayiri_matrimony_number_of_child_nodes";
    public static final String DUPLICATE = "com.ka12.ayiri_all_the_sent_requests_are_saved_here";
    public static final String CUR_USER_DATA = "com.ka12.ayiri_this_is_where_current_user_data_is_aved";
    public ArrayList<String> valid = new ArrayList<>();
    public ArrayList<String> notification_token = new ArrayList<>();
    Boolean is_connected,is_changed = false,is_request_already_sent = false,is_network_checked=false;
    String[] separated, spli;
    String all_request,push_data,data, push_send,temp_for_request,user_gender;
    String user_key,search_gender,current_user_received,last_seen_data,player_id = "";
    int count = 0,final_length,s_count = 0,no_of_child = 0, current_count = 0,last_seen_count = 0;
    Handler handler=new Handler();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        profile_frag = v.findViewById(R.id.profile_frag);
        list_name = v.findViewById(R.id.list_name);
        main_layout = v.findViewById(R.id.main_layout);
        loading = v.findViewById(R.id.loading);
        no_net=v.findViewById(R.id.no_net);
        list_name.setAdapter(custom);
        no_net.setVisibility(View.GONE);

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FFFFFF"));
        Log.d("barry ", "**************************************");
        Log.d("barry","initiated home sequence ");
        try {
            //retreiving users gender
            SharedPreferences getgender = Objects.requireNonNull(getActivity()).getSharedPreferences(GENDER, MODE_PRIVATE);
            user_gender = getgender.getString("gender", "female");

            if (user_gender.equals("male")) {
                search_gender = "female";
            } else {
                search_gender = "male";
            }

            check_network();
            //retreiving user key
            SharedPreferences ediss = Objects.requireNonNull(getActivity()).getSharedPreferences(KEY, MODE_PRIVATE);
            user_key = ediss.getString("key", "999999999");
            Log.d("start","key :"+user_key);

            //@refreshing and @updating is called inside check_network()

            //initialising one signal
            Log.d("onesignal", "initialising one signal with context");
            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
            OneSignal.initWithContext(Objects.requireNonNull(getContext()));
            OneSignal.setAppId(ONESIGNAL_APP_ID);

            //refreshing data
            pass_current_users_received_requests();
            refresh_data_final();
            update_lastseen_data();
        }catch (Exception e)
        {
            Log.d("error ","catch in onCreateView :"+e.getMessage());
        }
        return v;
    }
    public void check_network() {
        try {
            handler.postDelayed(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    ConnectivityManager connectivityManager = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo wifi_conn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    NetworkInfo data_conn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                    if ((wifi_conn != null && wifi_conn.isConnected()) || (data_conn != null && data_conn.isConnected())) {
                        is_connected = true;
                        no_net.setVisibility(View.GONE);
                        loading.setVisibility(View.VISIBLE);
                    } else {
                        is_connected = false;
                        no_net.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);
                        check_network();
                    }
                    is_network_checked=true;
                }
            }, 3000);

        }catch (Exception e)
        {
            Log.d("error ","catch in check_network :"+e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        valid.clear();
    }

    public void change_request_text(int i) {
        received_text.add(i, "Requested");
        custom.notifyDataSetChanged();
    }

    private void refresh_data_final()
    {
        try {
            count = 0;
            names.clear();
            family.clear();
            age.clear();
            keys.clear();
            gender.clear();
            links.clear();
            Log.d("barry","initiated refresh_data_final in home ");

            reference = FirebaseDatabase.getInstance().getReference().child(search_gender);
            reference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    count = 0;
                    Log.d("delta ", "triggered on child added");
                    loading.setVisibility(View.GONE);
                    keys.add(snapshot.getKey());
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String data = ds.getValue(String.class);
                        if (count == 0) {
                            if (data != null) {
                                Log.d("delta ", "data :" + data);
                                separated = data.split("\\#");
                                //we add the names only when validated
                                Log.d("home ", "name :" + separated[0] + " fam :" + separated[1] + " age :" + age + " gen :" + gender + " link :" + separated[4]);
                            }
                        }
                        if (count == 1) {
                            //we are filtering dubplicate entries
                            received.add(data);
                            Log.d("request ", "**************************************");
                            Log.d("request", "from db :" + data);
                            if (data != null) {
                                spli = data.split("\\:");
                            }
                        }
                        if (count == 2) {
                            //testing
                            if (data != null) {
                                Log.d("split", "data :" + data);
                                String[] split_last = data.split("\\:");
                                //only adding if the account is valid
                                if (split_last[1].equals("yes"))
                                {
                                    Log.d("split", "   Added :" + split_last[1]);
                                    seen.add(split_last[0]);
                                    valid.add(split_last[1]);
                                    notification_token.add(split_last[2]);
                                    names.add(separated[0]);
                                    family.add(separated[1]);
                                    age.add(Integer.valueOf(separated[2]));
                                    gender.add(separated[3]);
                                    links.add(separated[4]);
                                    height.add(separated[6]);
                                    description.add(separated[9]);
                                    //we have problems with the logic
                                    for (int e = 0; e < spli.length; e++)
                                    {
                                        Log.d("fele ", "1) comparing " + user_key + " with " + spli[e]);
                                        if (user_key.equals(spli[e]))
                                        {
                                            is_request_already_sent = true;
                                        }
                                    }
                                    if (is_request_already_sent)
                                    {
                                        received_text.add("Requested");
                                        is_request_already_sent = false;
                                    } else
                                        {
                                        received_text.add("Send Request");
                                    }
                                }
                            }
                        }
                        count++;
                    }
                    //testing
                    if (!is_changed) {
                        custom.notifyDataSetChanged();
                    }
                    //getting the number of child nodes
                    if (count > 0) {
                        no_of_child++;
                        Log.d("jiss", String.valueOf(no_of_child));
                        try {
                            SharedPreferences.Editor edit = Objects.requireNonNull(getActivity()).getSharedPreferences(CHILD, MODE_PRIVATE).edit();
                            edit.putInt("child", no_of_child).apply();
                        } catch (Exception e) {
                            Log.d("catch", "Error :" + e.getMessage());
                        }
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("try ", "triggered on child changed");
                    custom.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    Log.d("try ", "triggered on child removed");
                    custom.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("try ", "triggered on child moved");
                    custom.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("try ", "triggered on child cancelled");
                    custom.notifyDataSetChanged();
                }
            });
        }catch (Exception e)
        {
            Log.d("error ","catch in update_data_final :"+e.getMessage());
        }
    }

    public void send_request_finally_ultra(String data, String gender, String key, int notifi_index)
    {
        try {
            is_changed = true;
            Log.d("send", "Entered send request finally ultra");
            //key is the receiver key
            //tempo is passed as data

            //retrieving the user key
            SharedPreferences ediss = Objects.requireNonNull(getActivity()).getSharedPreferences(KEY, MODE_PRIVATE);
            String user_key = ediss.getString("key", "999999999");

            Log.d("send ", "user key :" + user_key);

            //creating the new push data for received
            push_data = user_key + ":" + data;
            //updating received node in receiver account
            firebaseDatabase = FirebaseDatabase.getInstance();
            reference = firebaseDatabase.getReference().child(gender).child(key).child("received");
            reference.setValue(push_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Toast.makeText(getActivity(), "Request sent!", Toast.LENGTH_SHORT).show();
                    Log.d("send ", "pushed successfully");
                    //sending notification
                    send_notification(notifi_index);
                }
            });

            push_data = "";
            count = 0;
            temp_for_request = "";
            is_changed = false;
            s_count = 0;
        }catch (Exception e)
        {
            Log.d("error ","catch in send_request_finally_ultra:"+e.getMessage());
        }
    }

    public void send_request_pro(String key, String gender, int notify_index)
    {
        try {
            Log.d("send ", "sender key   :" + user_key);
            Log.d("send ", "receiver key :" + key);
            if (user_key.equals(key)) {
                Toast.makeText(getActivity(), "You cant sent request to yourself!", Toast.LENGTH_SHORT).show();
            } else {
                //retrieving the existing requests before sending
                firebaseDatabase = FirebaseDatabase.getInstance();
                reference = firebaseDatabase.getReference().child(gender).child(key);
                reference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        //we know that received is at position 2 in database
                        s_count++;
                        if (s_count == 2) {
                            String tempo = snapshot.getValue(String.class);
                            Log.d("loop ", "tempo for request :" + tempo);
                            send_request_finally_ultra(tempo, gender, key, notify_index);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        custom.notifyDataSetChanged();
                        pass_current_users_received_requests();
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        custom.notifyDataSetChanged();
                        pass_current_users_received_requests();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        custom.notifyDataSetChanged();
                        pass_current_users_received_requests();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        pass_current_users_received_requests();
                        custom.notifyDataSetChanged();
                    }
                });
            }
        }catch (Exception e)
        {
            Log.d("error ","catch in send_request_pro :"+e.getMessage());
        }
    }

    public void update_lastseen_data()
    {
        try {
            Log.d("seen_data", "initiated");
            firebaseDatabase = FirebaseDatabase.getInstance();
            reference = firebaseDatabase.getReference().child(user_gender).child(user_key);
            reference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    last_seen_count++;
                    if (last_seen_count == 3)
                    {
                        last_seen_data = snapshot.getValue(String.class);
                        Log.d("seen", "data " + last_seen_data);
                        update_last_seen(last_seen_data);
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
        }catch (Exception e)
        {
            Log.d("error ","catch in update_last_seen_data :"+e.getMessage());
        }
    }

    public void update_last_seen(String data)
    {
        try {
            //retreiving the user id for notifications
            OSDeviceState device = OneSignal.getDeviceState();
            if (device != null) {
                player_id = device.getUserId();
            }

            Log.d("seen", "received data " + data);
            String[] split_seen = data.split("\\:");
            if (!split_seen[1].equals("no") && !split_seen[1].equals("yes")) {
                initiate_fake_user_protocol();
            } else {
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String final_date = df.format(c) + ":" + split_seen[1] + ":" + player_id;
                Log.d("seen", final_date);

                firebaseDatabase = FirebaseDatabase.getInstance();
                reference = firebaseDatabase.getReference().child(user_gender).child(user_key).child("sent");
                reference.setValue(final_date).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("seen ", "uploaded successfully");
                    }
                });
            }
        }catch (Exception e)
        {
            Log.d("error ","catch in update_last_seen :"+e.getMessage());
        }
    }

    public void pass_current_users_received_requests()
    {
        try {
            reference = FirebaseDatabase.getInstance().getReference().child(user_gender);
            reference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                /*
                current_count++;
                if(current_count==3)
                {
                    String snap=snapshot.getValue(String.class);
                    current_user_received=snap;
                    SharedPreferences.Editor putsnap=getActivity().getSharedPreferences(CUR_USER_DATA,MODE_PRIVATE).edit();
                    putsnap.putString("data",snap).apply();
                    Log.d("receivedz","current_data ="+snap);
                    current_count=0;
                }
                 */
                    //alternate method to get the desired values,use if the current method is not working
                    count = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (user_key.equals(snapshot.getKey()) && count == 1) {
                            try {
                                current_user_received = ds.getValue(String.class);
                                SharedPreferences.Editor putsnap = Objects.requireNonNull(getActivity()).getSharedPreferences(CUR_USER_DATA, MODE_PRIVATE).edit();
                                putsnap.putString("data", current_user_received).apply();
                                Log.d("receivedz", "current_data =" + current_user_received);
                            } catch (Exception e) {
                                Log.d("shared", "Error while fetch current user request :" + e.getMessage());
                            }
                        }
                        count++;
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
        }catch (Exception e)
        {
            Log.d("error ","catch in update_current_users_received_requests : "+e.getMessage());
        }
    }

    public void initiate_fake_user_protocol()
    {
        try {
            Context context;
            AlertDialog.Builder fakeAlert = new AlertDialog.Builder(getActivity(), R.style.alert_custom);
            fakeAlert.setTitle("Disclaimer!");
            fakeAlert.setMessage(R.string.fake_alert);
            fakeAlert.setCancelable(false);
            fakeAlert.setPositiveButton("Delete account", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //TODO:delete account method
                    Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    System.exit(0);
                }
            }).show();
        }catch (Exception e)
        {
            Log.d("error ","catch in fake_user_protocol :"+e.getMessage());
        }
    }

    public void send_notification(int index)
    {
        try {
            String user_id = notification_token.get(index);
            String sender_name = names.get(index);
            String sender_family = family.get(index);

            //getting user family
            SharedPreferences getfamily = Objects.requireNonNull(getActivity()).getSharedPreferences(FAMILY, MODE_PRIVATE);
            String user_family = getfamily.getString("family", "null");

            //retreiving the user_name
            SharedPreferences getname = getActivity().getSharedPreferences(NAME, MODE_PRIVATE);
            String user_name = getname.getString("name", "null");

            String message = "Hey " + sender_name + ", you have received a request from " + user_family + " " + user_name;
            Log.d("json", "player id=" + user_id);
            try {
                OneSignal.postNotification(new JSONObject("{'contents': {'en':'" + message + "'}, 'include_player_ids': ['" + user_id + "']}"), null);
            } catch (JSONException e) {
                Log.d("json", "Error :" + e.getMessage());
                e.printStackTrace();
            }
        }catch (Exception e)
        {
            Log.d("error ","catch in send_notifications :"+e.getMessage());
        }
    }

    class custom_adapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            Log.d("try ", "inside get count :" + names.size());
            return valid.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint({"InflateParams", "SetTextI18n"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getContext()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.display_list, null);
            }
            try {
                Log.d("try", "************************************");
                Log.d("barry","initiated adapterr for home ");

                ImageView img = view.findViewById(R.id.pic);
                TextView name = view.findViewById(R.id.name);
                Button request = view.findViewById(R.id.request);
                TextView last = view.findViewById(R.id.last);
                TextView desc = view.findViewById(R.id.desc);
                CardView main_card=view.findViewById(R.id.main_card);
                Animation list_anim=AnimationUtils.loadAnimation(getActivity(), R.anim.list_anim);
                main_card.startAnimation(list_anim);

                name.setText("Name  :" + names.get(i) + "\nFamily :" + family.get(i) + "\nAge      :" + age.get(i));
                last.setText("Last seen :" + seen.get(i));
                desc.setText(description.get(i));
                Picasso.get().load(links.get(i)).fit().centerCrop().into(img);

                Log.d("try ", "names :" + names.get(i));
                Log.d("try ", "link :" + links.get(i));
                request.setText(received_text.get(i));
                if (received_text.get(i).equals("Requested"))
                {
                    request.setVisibility(View.GONE);
                }

                request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (request.getText().toString().equals("Requested")) {
                            Toast.makeText(getActivity(), "Request already sent!!", Toast.LENGTH_SHORT).show();
                        } else {
                            AlertDialog.Builder b = new AlertDialog.Builder(getActivity(), R.style.alert_custom);
                            b.setMessage("Send request to " + names.get(i) + "?");
                            b.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int num) {
                                    //saving the send request in shared preferences
                                    SharedPreferences e = Objects.requireNonNull(getActivity()).getSharedPreferences(DUPLICATE, MODE_PRIVATE);
                                    String prev_key = e.getString("sent", "");
                                    String put_key = keys.get(i) + ":" + prev_key;

                                    SharedPreferences.Editor edit = getActivity().getSharedPreferences(DUPLICATE, MODE_PRIVATE).edit();
                                    edit.putString("sent", put_key).apply();
                                    change_request_text(i);
                                    send_request_pro(keys.get(i), gender.get(i), i);
                                }
                            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            b.show();
                        }
                    }
                });
            }catch (Exception e)
            {
                Log.d("error ","catch in custom_adapter :"+e.getMessage());
            }
            return view;
        }
    }
}