package com.ka12.ayirimatrimony;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/*
   TODO :Figure out a logic to prevent duplicate send requests.
 */
public class home extends Fragment {
    LinearLayout main_layout, profile_frag;
    DatabaseReference reference;
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
    custom_adapter custom = new custom_adapter();
    //firebase
    FirebaseDatabase firebaseDatabase;
    public static final String KEY = "com.ka12.ayiri_matrimony_this_is_where_key_is_stored";
    public static final String GENDER = "com.ka12.ayiri_matrimony_this_is_where_gender_is_stored";
    public static final String CHILD = "com.ka12.ayiri_matrimony_number_of_child_nodes";
    public static final String DUPLICATE = "com.ka12.ayiri_all_the_sent_requests_are_saved_here";
    String all_request;
    String push_data;
    String push_send;
    String temp_for_request;
    int count = 0;
    Boolean is_changed = false;
    int final_length;
    //testing
    String data;
    int s_count = 0;
    int no_of_child = 0;
    String user_gender;
    String user_key;
    Boolean is_request_already_sent=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        profile_frag = v.findViewById(R.id.profile_frag);
        list_name = v.findViewById(R.id.list_name);
        main_layout = v.findViewById(R.id.main_layout);
        loading = v.findViewById(R.id.loading);
        list_name.setAdapter(custom);

        //retreiving users gender
        SharedPreferences getgender = getActivity().getSharedPreferences(GENDER, MODE_PRIVATE);
        user_gender = getgender.getString("gender", "female");

        //retreiving user key
        SharedPreferences ediss = Objects.requireNonNull(getActivity()).getSharedPreferences(KEY, MODE_PRIVATE);
        user_key = ediss.getString("key", "999999999");

        //testing
        refresh_data_final();
        update_lastseen();
        return v;
    }

    public void change_request_text(int i)
    {
        received_text.add(i,"Requested");
        custom.notifyDataSetChanged();
    }

    private void refresh_data_final() {
        count = 0;
        names.clear();
        family.clear();
        age.clear();
        keys.clear();
        gender.clear();
        links.clear();
        Log.d("delta ", "inside refresh_data_final()");
        //TODO:change the child(male)
        //TODO:it should be the opposite of user's gender
        reference = FirebaseDatabase.getInstance().getReference().child("male");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                count = 0;
                Log.d("delta ", "triggered on child added");
                loading.setVisibility(View.GONE);
                keys.add(snapshot.getKey());
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    String data = ds.getValue(String.class);
                    if (count == 0)
                    {
                        Log.d("delta ", "data :" + data);
                        String[] separated = data.split("\\#");
                        names.add(separated[0]);
                        family.add(separated[1]);
                        age.add(Integer.valueOf(separated[2]));
                        gender.add(separated[3]);
                        links.add(separated[4]);
                        Log.d("delta ", "\nname :" + separated[0] + "\nfam :" + separated[1] + "\nage :" + age + "\ngen :" + gender + "\nlink :" + separated[4]);
                    }
                    if(count==1)
                    {
                        //we are filtering dubplicate entries
                        received.add(data);
                        Log.d("request ","**************************************");
                        Log.d("request","from db :"+data);
                        String[] spli=data.split("\\:");
                        for(int e=0;e<spli.length;e++)
                        {
                            Log.d("request ","1) comparing "+user_key+" with "+spli[e]);
                            if(user_key.equals(spli[e]))
                            {
                            //  Log.d("request ","2) got in for "+names.get(e));
                              is_request_already_sent=true;
                            }
                        }
                        if(is_request_already_sent) {
                            received_text.add("Requested");
                            is_request_already_sent=false;
                        }else
                        {
                            received_text.add("Send Request");
                        }
                    }
                    if (count == 2)
                    {
                        seen.add(data);
                    }
                    count++;
                }
                //testing
                if (!is_changed)
                {
                    custom.notifyDataSetChanged();
                }
                //getting the number of child nodes
                if (count > 0)
                {
                    no_of_child++;
                    Log.d("jizz", String.valueOf(no_of_child));
                    try {
                        SharedPreferences.Editor edit = getActivity().getSharedPreferences(CHILD, MODE_PRIVATE).edit();
                        edit.putInt("child", no_of_child).apply();
                    }catch (Exception e)
                    {
                        Log.d("catch","Error :"+e.getMessage());
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
    }
    //TODO:send request pro is available
    /*
    public void send_request(String key, String gender)
    {
        //getting the user's key
        SharedPreferences ediss = Objects.requireNonNull(getActivity()).getSharedPreferences(KEY, MODE_PRIVATE);
        int user_key = ediss.getInt("key", 999999);
        Log.d("send ", "sender key   :" + user_key);
        Log.d("send ", "receiver key :" + key);

        //retrieving the existing requests before sending
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child(gender).child(key);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                count++;
                String tempo = snapshot.getValue(String.class);
                Log.d("tempo", tempo);
                temp_for_request = tempo + "#" + temp_for_request;
                if (count == 8)
                {
                    Log.d("loop ", "temp for request " + temp_for_request);
                    send_request_finally(temp_for_request, gender, key);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                custom.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                custom.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                custom.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                custom.notifyDataSetChanged();
            }
        });
    }

     */
     /*
    public void send_request_finally(String data, String gender, String key)
    {
        is_changed = true;
        Log.d("try", "************************************");
        //key is the receiver key

        String[] seperate = data.split("\\#");
        Log.d("tempo ", "data received is :" + seperate[1]);

        //retrieving the user key
        SharedPreferences ediss = Objects.requireNonNull(getActivity()).getSharedPreferences(KEY, MODE_PRIVATE);
        int user_key = ediss.getInt("key", 999999);
        Log.d("send ", "inside send finally with user key :" + user_key);

        //creating the new push data for received
        push_data = user_key + ":" + seperate[1];

        //creating the new push_send for sender
        push_send= key + ":" +seperate[0];
        Log.d("loop ","data     "+data);
        Log.d("loop ","previous "+seperate[0]);
        Log.d("loop ","pushing  "+push_send);

        Log.d("test ", "push received " + push_data);
        Log.d("test ", "push send     " + push_send);
        //updating received node in receiver account
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child(gender).child(key).child("received");
        reference.setValue(push_data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                Toast.makeText(getActivity(), "Request sent", Toast.LENGTH_SHORT).show();
                Log.d("send ", "pushed successfully");
            }
        });

        //retrieving the user's gender to push
        SharedPreferences edit =getActivity().getSharedPreferences(GENDER,MODE_PRIVATE);
        String ugender=edit.getString("gender","male");
        Log.d("test ","user gender is "+ugender);

        //updating send node from sender account
        //don't forget to put the users gender
        firebaseDatabase=FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference().child("male").child(String.valueOf(user_key)).child("sent");
        reference.setValue(push_send).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
              Log.d("test ","updated sent of sender account");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d("test ","something went wrong while updating sent :"+e.getMessage());
            }
        });
        push_data = "";
        push_send="";
        count = 0;
        temp_for_request = "";
        is_changed = false;
       //  refresh_data();
        refresh_data_final();
    }
      */

    public void send_request_finally_ultra(String data, String gender, String key)
    {
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
            public void onSuccess(Void aVoid)
            {

                Toast.makeText(getActivity(), "Request sent!", Toast.LENGTH_SHORT).show();
                Log.d("send ", "pushed successfully");
            }
        });

        push_data = "";
        count = 0;
        temp_for_request = "";
        is_changed = false;
        s_count = 0;
    }

    public void send_request_pro(String key, String gender) {

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
                        send_request_finally_ultra(tempo, gender, key);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    custom.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    custom.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    custom.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    custom.notifyDataSetChanged();
                }
            });
        }
    }

    public void update_lastseen()
    {
        String final_date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Log.d("date", final_date);
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child(user_gender).child(user_key).child("sent");
        reference.setValue(final_date).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("date ", "uploaded successfully");
            }
        });
    }

    /* this is a officail working method for retrieving the data when child count was 8
       the db structure was improved later and the child count was reduced to 3
       so a new method refresh_data_final() is  defined to support the new db structure


    private void refresh_data() {
        names.clear();
        family.clear();
        age.clear();
        keys.clear();
        gender.clear();
        links.clear();
        Log.d("try ", "inside refresh_data()");
        //TODO:change the child(male)
        //TODO:it should be the opposite of user's gender
        reference = FirebaseDatabase.getInstance().getReference().child("male");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                loading.setVisibility(View.GONE);
                keys.add(snapshot.getKey());
                Log.d("try ", "triggered on child added");
                String final_data = "";
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    String uname = ds.getValue(String.class);
                    final_data = uname + "#" + final_data;
                }
                Log.d("final ", "final data " + final_data);

                String[] separated = final_data.split("\\#");
                names.add(separated[3]);
                family.add(separated[6]);
                links.add(separated[4]);
                gender.add(separated[5]);
                age.add(Integer.parseInt(String.valueOf(separated[7])));
                Log.d("try ", "calling notify");
                if (!is_changed)
                {
                    custom.notifyDataSetChanged();
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
      }
     */
    class custom_adapter extends BaseAdapter {

        @Override
        public int getCount() {
            Log.d("try ", "inside get count :" + names.size());
            return names.size();
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
            Log.d("try", "************************************");
            Log.d("try ", "inside times " + i);
            ImageView img = view.findViewById(R.id.pic);
            TextView name = view.findViewById(R.id.name);
            Button request = view.findViewById(R.id.request);
            TextView last = view.findViewById(R.id.last);
            name.setText("Name :" + names.get(i) + "\nfamily :" + family.get(i) + "\nAge :" + age.get(i));
            last.setText("Last seen :" + seen.get(i));
            Picasso.get().load(links.get(i)).fit().centerCrop().into(img);
            Log.d("try ", "names :" + names.get(i));
            Log.d("try ", "link :" + links.get(i));
            request.setText(received_text.get(i));

            request.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if(request.getText().toString().equals("Requested"))
                    {
                        Toast.makeText(getActivity(), "Request already sent!!", Toast.LENGTH_SHORT).show();
                    }else {
                        AlertDialog.Builder b = new AlertDialog.Builder(getActivity(), R.style.alert_custom);
                        b.setMessage("Send request to " + names.get(i) + "?");
                        b.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int num) {
                                //saving the send request in shared preferences
                                SharedPreferences e = getActivity().getSharedPreferences(DUPLICATE, MODE_PRIVATE);
                                String prev_key = e.getString("sent", "");
                                String put_key = keys.get(i) + ":" + prev_key;

                                SharedPreferences.Editor edit = getActivity().getSharedPreferences(DUPLICATE, MODE_PRIVATE).edit();
                                edit.putString("sent", put_key).apply();
                                change_request_text(i);
                                send_request_pro(keys.get(i), gender.get(i));
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
            return view;
        }
    }
}