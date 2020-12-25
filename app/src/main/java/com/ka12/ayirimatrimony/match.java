package com.ka12.ayirimatrimony;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
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
  this fragment is responsible to fill the matched list for a given user
  this fragment was able to get the sent,received and matched lists all at once.
  most of the methods are commented due to later deciding to make a seperate fragment for each of them
  please go through the methods below upon encountering bugs in future
 */
public class match extends Fragment {
    public static final String GENDER = "com.ka12.ayiri_matrimony_this_is_where_gender_is_stored";
    //defining the master array list
    public ArrayList<String> m_names = new ArrayList<>();
    public ArrayList<String> m_family = new ArrayList<>();
    public ArrayList<Integer> m_age = new ArrayList<>();
    public ArrayList<String> m_gender = new ArrayList<>();
    public ArrayList<String> m_links = new ArrayList<>();
    public ArrayList<String> m_keys = new ArrayList<>();
    public ArrayList<String> m_sent = new ArrayList<>();
    public ArrayList<String> m_received = new ArrayList<>();
    public ArrayList<String> m_height = new ArrayList<>();
    public ArrayList<String> m_description = new ArrayList<>();
    public ArrayList<String> m_qua = new ArrayList<>();
    public ArrayList<String> m_work = new ArrayList<>();
    //the following are for 'sent' list
    ListView list_name;
    public ArrayList<String> names = new ArrayList<>();
    public ArrayList<String> family = new ArrayList<>();
    public ArrayList<Integer> age = new ArrayList<>();
    public ArrayList<String> gender = new ArrayList<>();
    public ArrayList<String> links = new ArrayList<>();
    public ArrayList<String> keys = new ArrayList<>();
    public ArrayList<String> sent = new ArrayList<>();
    public ArrayList<String> received = new ArrayList<>();
    //the following are for 'received' list
    ListView requests_list;
    public ArrayList<String> names_req = new ArrayList<>();
    public ArrayList<String> family_req = new ArrayList<>();
    public ArrayList<Integer> age_req = new ArrayList<>();
    public ArrayList<String> gender_req = new ArrayList<>();
    public ArrayList<String> links_req = new ArrayList<>();
    public ArrayList<String> keys_req = new ArrayList<>();
    public ArrayList<String> sent_req = new ArrayList<>();
    public ArrayList<String> received_req = new ArrayList<>();
    public ArrayList<String> m_notification = new ArrayList<>();
    public ArrayList<String> height_req = new ArrayList<>();
    public ArrayList<String> work_req = new ArrayList<>();
    public ArrayList<String> description_req = new ArrayList<>();
    public ArrayList<String> qua_req = new ArrayList<>();
    //the following lists are for matches profiles
    ListView list_match;
    public ArrayList<String> names_match = new ArrayList<>();
    public ArrayList<String> family_match = new ArrayList<>();
    public ArrayList<Integer> age_match = new ArrayList<>();
    public ArrayList<String> gender_match = new ArrayList<>();
    public ArrayList<String> links_match = new ArrayList<>();
    public ArrayList<String> keys_match = new ArrayList<>();
    public ArrayList<String> sent_match = new ArrayList<>();
    public ArrayList<String> received_match = new ArrayList<>();
    public ArrayList<String> height_match = new ArrayList<>();
    public ArrayList<String> description_match = new ArrayList<>();
    public ArrayList<String> qua_match = new ArrayList<>();
    public ArrayList<String> work_match = new ArrayList<>();
    //database references
    DatabaseReference reference;
    FirebaseDatabase firebaseDatabase;
    LottieAnimationView loading, turtle;
    public static final String KEY = "com.ka12.ayiri_matrimony_this_is_where_key_is_stored";
    public static final String CHILD = "com.ka12.ayiri_matrimony_number_of_child_nodes";
    public static final String NAME = "com.ka12.ayiri_matrimony_this_is_where_name_is_stored";
    public static final String CUR_USER_DATA = "com.ka12.ayiri_this_is_where_current_user_data_is_aved";
    public ArrayList<String> noti_req = new ArrayList<>();
    //adapter
    custom_adapter_for_list_match custom_match = new custom_adapter_for_list_match();
    String key, user_gender, accept_data, user_name, search_gender, current_user_received;
    int no_of_children, sizz, total_count = 0, n, count = 0, asd = 0, temp_e = 0, temp = 0, get_i = 0, get_j = 0;
    Boolean is_checked = false;
    String[] separated;
    TextView no_data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_match, container, false);

        // list_name = v.findViewById(R.id.list_name);
        list_match = v.findViewById(R.id.list_match);
        //  requests_list = v.findViewById(R.id.requests);
        loading = v.findViewById(R.id.loading);

        turtle = v.findViewById(R.id.turtle);
        no_data = v.findViewById(R.id.no_data);

        turtle.setVisibility(View.GONE);
        no_data.setVisibility(View.GONE);

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FFFFFF"));

        Log.d("barry", "*************************************");
        Log.d("barry", "initiated match sequence ");
        try {
            //getting the current user 'received' data
            SharedPreferences getdata = Objects.requireNonNull(getActivity()).getSharedPreferences(CUR_USER_DATA, MODE_PRIVATE);
            current_user_received = getdata.getString("data", "something_went_wrong");

            Log.d("receivedz", "current user data from shared preferences =" + current_user_received);

            //getting the number of child nodes in main db
            SharedPreferences getchild = getActivity().getSharedPreferences(CHILD, MODE_PRIVATE);
            no_of_children = getchild.getInt("child", 0);

            //retrieving the key of the current user
            SharedPreferences ediss = Objects.requireNonNull(getActivity()).getSharedPreferences(KEY, MODE_PRIVATE);
            key = ediss.getString("key", "999999999");
            Log.d("key ", "received " + key);

            //retreieving the user gender
            SharedPreferences getgender = getActivity().getSharedPreferences(GENDER, MODE_PRIVATE);
            user_gender = getgender.getString("gender", "male");

            //retreiving user name
            SharedPreferences getname = getActivity().getSharedPreferences(NAME, MODE_PRIVATE);
            user_name = getname.getString("name", "manan");

            //fetching data
            refresh_data_final();

            list_match.setAdapter(custom_match);
        } catch (Exception e) {
            Log.d("error match", "catch in  onCreateView:" + e.getMessage());
        }
        return v;
    }

    public void clear_lists() {
        //clearing master list
        m_keys.clear();
        m_names.clear();
        m_age.clear();
        m_family.clear();
        m_received.clear();
        m_gender.clear();
        m_links.clear();
        //clearing sent requests
        names.clear();
        family.clear();
        age.clear();
        keys.clear();
        received.clear();
        gender.clear();
        links.clear();
        //clearing received requests
        names_req.clear();
        family_req.clear();
        age_req.clear();
        received_req.clear();
        keys_req.clear();
        gender_req.clear();
        links_req.clear();
        //clearing matches
        names_match.clear();
        family_match.clear();
        age_match.clear();
        keys_match.clear();
        received_match.clear();
        gender_match.clear();
        links_match.clear();
        //notifying the adapters
        //  custom.notifyDataSetChanged();
        //  custom_req.notifyDataSetChanged();
        custom_match.notifyDataSetChanged();
        Log.d("beta ", "clear list initiated");
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
            Log.d("barry", "initiated refresh_data_final of match");
            reference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("beta", "1) went inside onchildadded");
                    asd++;
                    temp = 0;
                    loading.setVisibility(View.GONE);
                    if (asd <= no_of_children) {
                        //keys.add(snapshot.getKey());
                        m_keys.add(snapshot.getKey());
                        Log.d("key ", "snap key " + snapshot.getKey());
                    }
                    String final_data = "";
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Log.d("beta", "2) inside populating master list");
                        String data = ds.getValue(String.class);
                        if (temp == 0) {
                            Log.d("delta ", "data :" + data);
                            if (data != null) {
                                separated = data.split("\\#");
                                m_names.add(separated[0]);
                                m_family.add(separated[1]);
                                m_age.add(Integer.valueOf(separated[2]));
                                m_gender.add(separated[3]);
                                m_links.add(separated[4]);
                                m_height.add(separated[6]);
                                m_qua.add(separated[7]);
                                m_work.add(separated[8]);
                                m_description.add(separated[9]);
                                Log.d("match ", "name :" + separated[0] + " fam :" + separated[1] + "age :" + age + "gen :" + gender + "link :" + separated[4]);
                            }
                        }
                        if (temp == 1) {
                            received.add(data);
                            m_received.add(data);
                        }
                        if (temp == 2) {
                            if (data != null) {
                                String[] sp = data.split("\\:");
                                m_notification.add(sp[2]);
                            }
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
                    //*********************************************************************************
                    //the following is for received section
                    if (asd == no_of_children) {
                        Log.d("receivedz", "*****************************************************");
                        Log.d("receivedz", "current data inside the loop :" + current_user_received);
                        String[] sepea = current_user_received.split("\\:");
                        for (int s = 0; s < sepea.length; s++) {
                            Log.d("receivedz", "for s =" + s + " length of data:" + sepea.length);
                            for (int d = 0; d < m_keys.size(); d++) {
                                Log.d("receivedz", "d =" + d);
                                Log.d("receivedz", "comparing " + sepea[s] + " with " + m_keys.get(d) + " (" + m_names.get(d) + ")");
                                if (sepea[s].equals(m_keys.get(d))) {
                                    Log.d("receivedz", "**********ADDED " + m_names.get(d).toUpperCase() + " *************");
                                    names_req.add(m_names.get(d));
                                    family_req.add(m_family.get(d));
                                    links_req.add(m_links.get(d));
                                    received_req.add(m_received.get(d));
                                    keys_req.add(m_keys.get(d));
                                    gender_req.add(m_gender.get(d));
                                    age_req.add(Integer.parseInt(String.valueOf(m_age.get(d))));
                                    noti_req.add(m_notification.get(d));
                                    description_req.add(m_description.get(d));
                                    qua_req.add(m_qua.get(d));
                                    height_req.add(m_height.get(d));
                                    work_req.add(m_work.get(d));
                                    keys_req.add(m_keys.get(d));
                                }
                            }
                        }
                    }

                    //the following code is to find matched
                    for (int get_i = 0; get_i < names.size(); get_i++) {
                        Log.d("beta", "5)  inside populating metched list");
                        Log.d("matriz ", "1) inside for i=" + get_i + " and name =" + names.get(get_i) + "(" + keys.get(get_i) + ")");
                        for (int get_j = 0; get_j < names_req.size(); get_j++) {
                            Log.d("matriz ", "2) inside for with j=" + get_j + " with name_req =" + names_req.get(get_j));
                            Log.d("matriz ", "   comparing " + names.get(get_i) + " and " + names_req.get(get_j));
                            //TODO:dont forget to change the name=done
                            if (names.get(get_i).equals(names_req.get(get_j)) && !names.get(get_i).equals(user_name) && !names_req.get(get_j).equals(user_name)) {
                                Log.d("matriz ", names.get(get_i) + " ***** matched with ***** " + names_req.get(get_j));
                                names_match.add(names_req.get(get_j));
                                family_match.add(family_req.get(get_j));
                                links_match.add(links_req.get(get_j));
                                gender_match.add(gender_req.get(get_j));
                                age_match.add(age_req.get(get_j));
                                description_match.add(description_req.get(get_j));
                                qua_match.add(qua_req.get(get_j));
                                height_match.add(height_req.get(get_j));
                                work_match.add(work_req.get(get_j));
                                keys_match.add(keys_req.get(get_j));

                                //trying a method of removing the duplicates from requested list
                                Log.d("removed", names_req.get(get_j));
                                names_req.remove(get_j);
                                family_req.remove(get_j);
                                links_req.remove(get_j);
                                gender_req.remove(get_j);
                                age_req.remove(get_j);
                            }
                        }
                    }
                    if (asd == no_of_children) {
                        n = 0;
                        count = 0;
                        asd = 0;
                        get_i = 0;
                        get_j = 0;
                        temp_e = 0;
                        Log.d("beta", "asd reset");
                    }
                    count++;

                    custom_match.notifyDataSetChanged();
                    Log.d("beta", "*****************************************************");
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("beta", "went inside on child changed!!! " + previousChildName);
                    custom_match.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    Log.d("beta", "went inside on child removed");
                    custom_match.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("beta", "went inside onChildMoved");
                    custom_match.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("beta", "went inside onCancelled");
                    custom_match.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            Log.d("error match", "catch in refresh_data_final :" + e.getMessage());
        }
    }

    class custom_adapter_for_list_match extends BaseAdapter {

        @Override
        public int getCount() {
            Log.d("loop ", "size " + names.size());
            if (names_match.size() == 0) {
                turtle.setVisibility(View.VISIBLE);
                no_data.setVisibility(View.VISIBLE);
            }
            return names_match.size();
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
                view = inflater.inflate(R.layout.display_list_match, null);
            }
            try {
                Log.d("try ", "inside times " + i);
                ImageView img = view.findViewById(R.id.pic);
                TextView name = view.findViewById(R.id.name);
                Button request = view.findViewById(R.id.request);
                //  CardView main_card=view.findViewById(R.id.main_card);
                //  Animation list_anim= AnimationUtils.loadAnimation(getActivity(), R.anim.list_anim);
                //  main_card.startAnimation(list_anim);
                Log.d("loop ", "**************************************************");
                Log.d("barry", "initiated adapter for match");
                if (names_match.size() != 0) {
                    turtle.setVisibility(View.GONE);
                    no_data.setVisibility(View.GONE);
                }
                name.setText("Name :" + names_match.get(i) + "\nFamily :" + family_match.get(i) + "\nAge :" + age_match.get(i));
                Picasso.get().load(links_match.get(i)).fit().centerCrop().into(img);

                request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("gotit", "before :" + names_match.get(i) + " " + family_match.get(i) + " " + age_match.get(i));
                        Intent ins = new Intent(getActivity(), final_match.class);
                        ins.putExtra("name", names_match.get(i));
                        ins.putExtra("family", family_match.get(i));
                        ins.putExtra("age", String.valueOf(age_match.get(i)));
                        ins.putExtra("link", links_match.get(i));
                        ins.putExtra("desc", description_match.get(i));
                        ins.putExtra("work", work_match.get(i));
                        ins.putExtra("height", height_match.get(i));
                        ins.putExtra("qua", qua_match.get(i));
                        ins.putExtra("key", keys_match.get(i));
                        startActivity(ins);
                        Animatoo.animateZoom(Objects.requireNonNull(getActivity()));
                    }
                });

            } catch (Exception e) {
                Log.d("error match", "catch in custom_adapter_for_list_match :" + e.getMessage());
            }
            return view;
        }
    }
}