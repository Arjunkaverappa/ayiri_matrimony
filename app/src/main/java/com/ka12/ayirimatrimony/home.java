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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
/*
   TODO :Figure out a logic to prevent duplicate send requests.
 */
public class home extends Fragment {
    //  FrameLayout change_frag;
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
    custom_adapter custom = new custom_adapter();
    //firebase
    FirebaseDatabase firebaseDatabase;
    public static final String KEY = "com.ka12.ayiri_matrimony_this_is_where_key_is_stored";
    public static final String GENDER = "com.ka12.ayiri_matrimony_this_is_where_gender_is_stored";
    String all_request;
    String push_data;
    String push_send;
    String temp_for_request;
    int count = 0;
    Boolean is_changed = false;
    int final_length;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        profile_frag = v.findViewById(R.id.profile_frag);
        list_name = v.findViewById(R.id.list_name);
        main_layout = v.findViewById(R.id.main_layout);
        loading=v.findViewById(R.id.loading);
        list_name.setAdapter(custom);
        refresh_data();

        return v;
    }

    private void refresh_data() {
        names.clear();
        family.clear();
        age.clear();
        keys.clear();
        gender.clear();
        links.clear();
        Log.d("try ", "inside refresh_data()");
        reference = FirebaseDatabase.getInstance().getReference().child("male");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                loading.setVisibility(View.GONE);
              //  Log.d("key ", "snap key " + snapshot.getKey());
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

            name.setText("Name :" + names.get(i) + "\nfamily :" + family.get(i) + "\nAge :" + age.get(i));
            Picasso.get().load(links.get(i)).fit().centerCrop().into(img);
            Log.d("try ", "names :" + names.get(i));
            Log.d("try ", "link :" + links.get(i));
            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity(), R.style.alert_custom);
                    b.setMessage("Send request to " + names.get(i) + "?");
                    b.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int num)
                        {
                            send_request(keys.get(i), gender.get(i));
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    b.show();
                }
            });
            return view;
        }
    }

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
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
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
        refresh_data();
    }
}