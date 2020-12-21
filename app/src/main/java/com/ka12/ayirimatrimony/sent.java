package com.ka12.ayirimatrimony;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class sent extends Fragment
{
    public static final String GENDER = "com.ka12.ayiri_matrimony_this_is_where_gender_is_stored";
    public static final String KEY = "com.ka12.ayiri_matrimony_this_is_where_key_is_stored";
    public ArrayList<String> names = new ArrayList<>();
    public ArrayList<String> family = new ArrayList<>();
    public ArrayList<Integer> age = new ArrayList<>();
    public ArrayList<String> gender = new ArrayList<>();
    public ArrayList<String> links = new ArrayList<>();
    public ArrayList<String> keys = new ArrayList<>();
    public ArrayList<String> sent = new ArrayList<>();
    public ArrayList<String> received = new ArrayList<>();
    DatabaseReference reference;
    FirebaseDatabase firebaseDatabase;
    //the following are for 'sent' list
    ListView list_name;
    custom_adapter custom=new custom_adapter();
    int no_of_children,n,sizz,total_count,count,asd,temp_e,temp;
    LottieAnimationView loading;
    ListView list_names;
    String current_user_received,key,user_gender,user_name,search_gender,accept_data;
    String[] separated;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_sent, container, false);
        list_names=v.findViewById(R.id.list_name);
        loading=v.findViewById(R.id.loading);

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FFFFFF"));
        Log.d("barry","**************************************");
        Log.d("barry","initiated sent sequence ");
        try {
            //retrieving the key of the current user
            SharedPreferences ediss = Objects.requireNonNull(getActivity()).getSharedPreferences(KEY, MODE_PRIVATE);
            key = ediss.getString("key", "999999999");
            Log.d("key ", "received " + key);

            //retreieving the user gender
            SharedPreferences getgender = getActivity().getSharedPreferences(GENDER, MODE_PRIVATE);
            user_gender = getgender.getString("gender", "male");

            list_names.setAdapter(custom);
            refresh_data_final();
        }catch (Exception e)
        {
            Log.d("error sent","catch in  :"+e.getMessage());
        }
        return v;
    }

    private void refresh_data_final() {
        try {
            clear_lists();
            SharedPreferences getgender = Objects.requireNonNull(getActivity()).getSharedPreferences(GENDER, MODE_PRIVATE);
            user_gender = getgender.getString("gender", "female");
            if (user_gender.equals("male")) {
                search_gender = "female";
            } else {
                search_gender = "male";
            }
            reference = FirebaseDatabase.getInstance().getReference().child(search_gender);
            Log.d("barry","initiated refresh_data_final in sent");
            reference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("beta", "1) went inside onchildadded");
                    asd++;
                    temp = 0;
                    loading.setVisibility(View.GONE);
                    String final_data = "";
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Log.d("beta", "2) inside populating master list");
                        String data = ds.getValue(String.class);
                        if (temp == 0) {
                            Log.d("delta ", "data :" + data);
                            if (data != null) {
                                separated = data.split("\\#");
                            }
                            Log.d("sent ", "name :" + separated[0] + "fam :" + separated[1] + "age :" + age + "gen :" + gender + "link :" + separated[4]);
                        }
                        if (temp == 1) {
                            received.add(data);
                        }
                        temp++;
                    }

                    n = received.size();
                    //the following code it to populate request 'sent' list of the user
                    //we search the requests section and fill the list if key matches
                    for (int q = count; q < n; q++) {
                        Log.d("beta", "3) inside populating sent list ");
                        String[] sep = received.get(q).split("\\:");
                        for (int e = 0; e < sep.length; e++) {
                            Log.d("loop ", "comparing " + key + " with " + sep[e]);
                            if (String.valueOf(key).equals(sep[e])) {
                                keys.add(snapshot.getKey());
                                names.add(separated[0]);
                                family.add(separated[1]);
                                links.add(separated[4]);
                                gender.add(separated[3]);
                                age.add(Integer.valueOf(separated[2]));
                                Log.d("loop ", "added " + separated[0]);
                            }
                        }
                    }
                    if (asd == no_of_children)
                    {
                        temp=0;
                        n = 0;
                        count = 0;
                        asd = 0;
                        temp_e = 0;
                        Log.d("beta", "asd reset");
                    }
                    count++;
                    custom.notifyDataSetChanged();

                    Log.d("beta", "*****************************************************");
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("beta", "went inside on child changed!!! " + previousChildName);
                    custom.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    Log.d("beta", "went inside on child removed");
                    custom.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("beta", "went inside onChildMoved");
                    custom.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("beta", "went inside onCancelled");
                    custom.notifyDataSetChanged();
                }
            });
        }catch (Exception e)
        {
            Log.d("error sent","catch in refresh_data_final :"+e.getMessage());
        }
    }

    public void clear_lists() {
        //clearing sent requests
        names.clear();
        family.clear();
        age.clear();
        keys.clear();
        received.clear();
        gender.clear();
        links.clear();
        Log.d("beta ", "clear list initiated");
    }

    class custom_adapter extends BaseAdapter {

        @Override
        public int getCount() {
            Log.d("loop ", "size " + names.size());
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
                view = inflater.inflate(R.layout.display_list_sent, null);
            }
            Log.d("barry","initiated adapter in sent");
            try {
                Log.d("try ", "inside times " + i);
                ImageView img = view.findViewById(R.id.pic);
                TextView name = view.findViewById(R.id.name);
              //  CardView main_card=view.findViewById(R.id.main_card);
              //  Animation list_anim= AnimationUtils.loadAnimation(getActivity(), R.anim.list_anim);
              //  main_card.startAnimation(list_anim);
                Log.d("loop ", "**************************************************");
                Log.d("loop ", "the value of n before entering =" + n);

                name.setText("Name :" + names.get(i) + "\nFamily :" + family.get(i) + "\nAge :" + age.get(i));
                Picasso.get().load(links.get(i)).fit().centerCrop().into(img);
            }catch (Exception e)
            {
                Log.d("error sent","catch in  custom_adapter :"+e.getMessage());
            }
            return view;
        }
    }
}