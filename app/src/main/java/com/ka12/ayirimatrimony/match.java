package com.ka12.ayirimatrimony;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
  TODO :get the name through shared preferences and put it in matched entries calculations.
  TODO :create a new custom list view xml file for displaying matched entries.
  TODO :get the total child count from home.
  TODO :remove all the dummy image links.
  TODO :correct line no:164.
 */
public class match extends Fragment {
   // BottomNavigationView top_bar;
    LottieAnimationView loading;
    LinearLayout mat,rec,sen;
    //defining the master array list
    public ArrayList<String> m_names = new ArrayList<>();
    public ArrayList<String> m_family = new ArrayList<>();
    public ArrayList<Integer> m_age = new ArrayList<>();
    public ArrayList<String> m_gender = new ArrayList<>();
    public ArrayList<String> m_links = new ArrayList<>();
    public ArrayList<String> m_keys = new ArrayList<>();
    public ArrayList<String> m_sent = new ArrayList<>();
    public ArrayList<String> m_received = new ArrayList<>();
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
    //database references
    DatabaseReference reference;
    FirebaseDatabase firebaseDatabase;
    public static final String KEY = "com.ka12.ayiri_matrimony_this_is_where_key_is_stored";
    //initializing all the adapters here
    custom_adapter custom = new custom_adapter();
    custom_adapter_for_requests custom_req=new custom_adapter_for_requests();
    custom_adapter_for_list_match custom_match=new custom_adapter_for_list_match();
    int key;
    //testing
    int n;
    Boolean is_checked = false;
    int sizz;
    int total_count = 0;
    int count = 0;
    int asd=0;
    int inc=0;
    int temp_e=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_match, container, false);
        mat=v.findViewById(R.id.mat);
        rec=v.findViewById(R.id.rec);
        sen=v.findViewById(R.id.sen);
        list_name = v.findViewById(R.id.list_name);
        list_match=v.findViewById(R.id.list_match);
        requests_list=v.findViewById(R.id.requests);
     //   top_bar = v.findViewById(R.id.top_bar);
        loading = v.findViewById(R.id.loading);
        //retrieving the key of the current user
        SharedPreferences ediss = Objects.requireNonNull(getActivity()).getSharedPreferences(KEY, MODE_PRIVATE);
        key = ediss.getInt("key", 999999);
        Log.d("key ", "received " + key);

        //hinding the requests list initialy
        requests_list.setVisibility(View.GONE);
        list_name.setVisibility(View.GONE);
        mat.setBackgroundColor(Color.GRAY);
        mat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                mat.setBackgroundColor(Color.GRAY);
                rec.setBackgroundColor(Color.WHITE);
                sen.setBackgroundColor(Color.WHITE);
                list_match.setVisibility(View.VISIBLE);
                requests_list.setVisibility(View.GONE);
                list_name.setVisibility(View.GONE);
            }
        });
        rec.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mat.setBackgroundColor(Color.WHITE);
                rec.setBackgroundColor(Color.GRAY);
                sen.setBackgroundColor(Color.WHITE);
                list_match.setVisibility(View.GONE);
                requests_list.setVisibility(View.VISIBLE);
                list_name.setVisibility(View.GONE);
            }
        });
        sen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                mat.setBackgroundColor(Color.WHITE);
                rec.setBackgroundColor(Color.WHITE);
                sen.setBackgroundColor(Color.GRAY);
                list_match.setVisibility(View.GONE);
                requests_list.setVisibility(View.GONE);
                list_name.setVisibility(View.VISIBLE);
            }
        });

        /*
        top_bar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.matches:
                        Toast.makeText(getActivity(), "showing matches", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.received:
                        list_name.setVisibility(View.GONE);
                        requests_list.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "showing received", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.sent:
                        list_name.setVisibility(View.VISIBLE);
                        requests_list.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "showing sent", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
         */
        //fetching data for 'sent' list
        refresh_data();
        list_name.setAdapter(custom);
        requests_list.setAdapter(custom_req);
        list_match.setAdapter(custom_match);
        return v;
    }

    private void refresh_data()
    {
        clear_lists();
        reference = FirebaseDatabase.getInstance().getReference().child("male");
        Log.d("try ", "inside refresh_data()");
        reference.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                asd++;
               // Log.d("test ",String.valueOf(snapshot.getChildrenCount()));
                loading.setVisibility(View.GONE);
                //storing the keys
                if(asd<=4)
                {
                    keys.add(snapshot.getKey());
                    m_keys.add(snapshot.getKey());
                    Log.d("key ", "snap key " + snapshot.getKey());
                }
                String final_data = "";
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    String uname = ds.getValue(String.class);
                    final_data = uname + "#" + final_data;
                }
                Log.d("match ", final_data);
                String[] separated = final_data.split("\\#");
                //filling the master array list
                m_names.add(separated[3]);
                m_family.add(separated[6]);
                m_links.add(separated[4]);
                m_gender.add(separated[5]);
              //  m_keys.add(snapshot.getKey());
                m_age.add(Integer.parseInt(String.valueOf(separated[7])));

                //the main 'received' list for all calculations
                received.add(separated[1]);
                Log.d("lopp ","received :"+separated[1]);
                n = received.size();
                //the following code it to populate request 'sent' list of the user
                //we search the requests section and fill the list if key matches
                for (int q = count; q < n; q++)
                {
                    String[] sep = received.get(q).split("\\:");
                    for (int e = 0; e < sep.length; e++)
                    {
                        Log.d("loop ", "comparing " + key + " with " + sep[e]);
                        if (String.valueOf(key).equals(sep[e]))
                        {
                            names.add(separated[3]);
                            family.add(separated[6]);
                            links.add(separated[4]);
                            gender.add(separated[5]);
                            age.add(Integer.parseInt(String.valueOf(separated[7])));
                            Log.d("loop ", "added " + separated[3]);
                        }
                    }
                }
                //the following code is to populate 'received' requests from other users
                //looks like we need to check the send section of every user
                if(asd==4)
                {
                    for (int q = inc; q < n; q++)
                    {
                        Log.d("lopp ","***********************************************************");
                        Log.d("lopp", "1) comparing " + key + " with " + keys.get(q) + " while q=" + q);
                        //this for loop is to fetch only the required user account
                        //this is where all the decoding begins
                        if (String.valueOf(key).equals(keys.get(q)))
                        {
                            Log.d("lopp ", "2) success with key " + keys.get(q)+" and name :"+m_names.get(q));
                            String[] sep = received.get(q).split("\\:");
                            for (int e = temp_e; e < sep.length; e++)
                            {
                                Log.d("lopp ","3) e="+e+" and sep before entering for is "+sep[e]);
                                for(int z=0;z<m_names.size();z++)
                                {
                                    Log.d("lopp ","4) z="+z+" and size of m_names="+m_names.size());
                                    Log.d("lopp ","   comparing "+sep[e]+" with "+m_names.get(z)+"("+m_keys.get(z)+")");
                                    if(sep[e].equals(m_keys.get(z)))
                                    {
                                        Log.d("lopp ", "5) ****added " + m_names.get(z)+"**** because z="+z);
                                        names_req.add(m_names.get(z));
                                        family_req.add(m_family.get(z));
                                        links_req.add(m_links.get(z));
                                        gender_req.add(m_gender.get(z));
                                        age_req.add(Integer.parseInt(String.valueOf(m_age.get(z))));
                                    }
                                }
                                temp_e++;
                            }
                        }
                    }
                    inc++;
                }
                /*
                //listing all the available data
                for(int i=0;i<names.size();i++)
                {
                    Log.d("matriz "," sent     :"+names.get(i));
                }
                for(int i=0;i<names_req.size();i++)
                {
                    Log.d("matriz "," received :"+names_req.get(i));
                }
                 */

                //the following code is to find matched (lucky guys)
                for(int i=0;i<names.size();i++)
                {
                    Log.d("matriz ","1) inside for i="+i+" and name ="+names.get(i)+"("+keys.get(i)+")");
                    for(int j=0;j<names_req.size();j++)
                    {
                        Log.d("matriz ","2) inside for with j="+j+" with name_req ="+names_req.get(j));
                        Log.d("matriz ","   comparing "+names.get(i)+" and "+names_req.get(j));
                        if(names.get(i).equals(names_req.get(j)) && !names.get(i).equals("monnappa") && !names_req.get(j).equals("monnappa"))
                        {
                            Log.d("matriz ",names.get(i)+" ***** matched with ***** "+names_req.get(j));
                            names_match.add(names_req.get(j));
                            family_match.add(family_req.get(j));
                            links_match.add(links_req.get(j));
                            gender_match.add(gender_req.get(j));
                            age_match.add(age_req.get(j));
                        }
                    }
                }
                count++;
                custom.notifyDataSetChanged();
                custom_req.notifyDataSetChanged();
                custom_match.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("try ", "went inside on child changed");
                custom.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d("try ", "went inside on child removed");
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
    public void clear_lists()
    {
        //clearing master list
        m_keys.clear();
        m_names.clear();
        m_age.clear();
        m_family.clear();
        m_gender.clear();
        m_links.clear();
        //clearing sent requests
        names.clear();
        family.clear();
        age.clear();
        keys.clear();
        gender.clear();
        links.clear();
        //clearing received requests
        names_req.clear();
        family_req.clear();
        age_req.clear();
        keys.clear();
        gender_req.clear();
        links_req.clear();
        //clearing matches
        names_match.clear();
        family_match.clear();
        age_match.clear();
        keys_match.clear();
        gender_match.clear();
        links_match.clear();
        //notifying the adapters
        custom.notifyDataSetChanged();
        custom_req.notifyDataSetChanged();
        custom_match.notifyDataSetChanged();
    }
    @Keep
    //this adapter is to show all 'sent' requests lists
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
                view = inflater.inflate(R.layout.display_list, null);
            }
            Log.d("try ", "inside times " + i);
            ImageView img = view.findViewById(R.id.pic);
            TextView name = view.findViewById(R.id.name);

            Log.d("loop ", "**************************************************");
            Log.d("loop ", "the value of n before entering =" + n);

            name.setText("Name :" + names.get(i) + "\nFamily :" + family.get(i) + "\nAge :" + age.get(i));
            Picasso.get().load(links.get(i)).fit().centerCrop().into(img);
            return view;
        }
    }
    @Keep
    //this adapter is to display 'requests' from other users
    class custom_adapter_for_requests extends BaseAdapter
    {

        @Override
        public int getCount() {
            Log.d("loop ", "size " + names.size());
            return names_req.size();
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
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getContext()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.display_list, null);
            }
            Log.d("try ", "inside times " + i);
            ImageView img = view.findViewById(R.id.pic);
            TextView name = view.findViewById(R.id.name);

            Log.d("loop ", "**************************************************");
            Log.d("loop ", "the value of n before entering =" + n);

            name.setText("Name :" + names_req.get(i) + "\nFamily :" + family_req.get(i) + "\nAge :" + age_req.get(i));
            Picasso.get().load(links_req.get(i)).fit().centerCrop().into(img);
            return view;
        }
    }
    class custom_adapter_for_list_match extends BaseAdapter
    {

        @Override
        public int getCount() {
            Log.d("loop ", "size " + names.size());
            return names_req.size();
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
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getContext()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.display_list, null);
            }
            Log.d("try ", "inside times " + i);
            ImageView img = view.findViewById(R.id.pic);
            TextView name = view.findViewById(R.id.name);

            Log.d("loop ", "**************************************************");
            Log.d("loop ", "the value of n before entering =" + n);

            name.setText("Name :" + names_req.get(i) + "\nFamily :" + family_req.get(i) + "\nAge :" + age_req.get(i));
            Picasso.get().load(links_req.get(i)).fit().centerCrop().into(img);
            return view;
        }
    }
}