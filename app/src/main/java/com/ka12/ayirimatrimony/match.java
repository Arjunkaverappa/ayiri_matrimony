package com.ka12.ayirimatrimony;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
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

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
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
  TODO :remove all the dummy image links.
 */
public class match extends Fragment {
    LottieAnimationView loading;
    LinearLayout mat, rec, sen;
    public static final String GENDER = "com.ka12.ayiri_matrimony_this_is_where_gender_is_stored";
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
    public static final String CHILD = "com.ka12.ayiri_matrimony_number_of_child_nodes";
    public static final String NAME = "com.ka12.ayiri_matrimony_this_is_where_name_is_stored";
    //  TextView nomat,noreq,nosen;
    //defining the master array list
    public ArrayList<String> m_names = new ArrayList<>();
    //initializing all the adapters here
    custom_adapter custom = new custom_adapter();
    custom_adapter_for_requests custom_req = new custom_adapter_for_requests();
    custom_adapter_for_list_match custom_match = new custom_adapter_for_list_match();
    String key;
    int no_of_children;
    //testing
    int n;
    Boolean is_checked = false;
    int sizz;
    int total_count = 0;
    int count = 0;
    int asd = 0;
    int inc = 0;
    int temp_e = 0;
    //testing
    int temp = 0;
    String user_gender;
    String[] separated;
    String accept_data;
    String user_name;
    //used in match list
    int get_i = 0, get_j = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_match, container, false);
        mat = v.findViewById(R.id.mat);
        rec = v.findViewById(R.id.rec);
        sen = v.findViewById(R.id.sen);
        list_name = v.findViewById(R.id.list_name);
        list_match = v.findViewById(R.id.list_match);
        requests_list = v.findViewById(R.id.requests);
        loading = v.findViewById(R.id.loading);

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

        //hinding the requests list initialy
        requests_list.setVisibility(View.GONE);
        list_name.setVisibility(View.GONE);
        mat.setBackgroundColor(Color.parseColor("#ED8A6B"));
        mat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mat.setBackgroundColor(Color.parseColor("#ED8A6B"));
                rec.setBackgroundColor(Color.WHITE);
                sen.setBackgroundColor(Color.WHITE);
                list_match.setVisibility(View.VISIBLE);
                requests_list.setVisibility(View.GONE);
                list_name.setVisibility(View.GONE);
            }
        });
        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mat.setBackgroundColor(Color.WHITE);
                rec.setBackgroundColor(Color.parseColor("#ED8A6B"));
                sen.setBackgroundColor(Color.WHITE);
                list_match.setVisibility(View.GONE);
                requests_list.setVisibility(View.VISIBLE);
                list_name.setVisibility(View.GONE);
            }
        });
        sen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mat.setBackgroundColor(Color.WHITE);
                rec.setBackgroundColor(Color.WHITE);
                sen.setBackgroundColor(Color.parseColor("#ED8A6B"));
                list_match.setVisibility(View.GONE);
                requests_list.setVisibility(View.GONE);
                list_name.setVisibility(View.VISIBLE);
            }
        });

        //fetching data for 'sent' list
        //refresh_data();
        refresh_data_final();
        list_name.setAdapter(custom);
        requests_list.setAdapter(custom_req);
        list_match.setAdapter(custom_match);
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
        keys.clear();
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
        custom.notifyDataSetChanged();
        custom_req.notifyDataSetChanged();
        custom_match.notifyDataSetChanged();
        Log.d("beta ", "clear list initiated");
    }

    public void accept_request(String data, String sender_gender, String sender_key, int i) {
        Log.d("send ", "*************************************");
        Log.d("send ", "data :" + data + "\nsender gender=" + sender_gender + "\nsender_key=" + sender_key);
        //key is the user_key
        //i is the sender position from the requested sender account
        accept_data = key + ":" + data;
        Log.d("send ", "accept data final" + accept_data);
        //updating received node in sender account
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child(sender_gender).child(sender_key).child("received");
        reference.setValue(accept_data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                accept_data = "";
                custom_match.notifyDataSetChanged();
                //adding it to the matched list
                names_match.add(names_req.get(i));
                family_match.add(family_req.get(i));
                links_match.add(links_req.get(i));
                gender_match.add(gender_req.get(i));
                age_match.add(age_req.get(i));
                custom_match.notifyDataSetChanged();

                //removing it from the requests list
                names_req.remove(i);
                family_req.remove(i);
                links_req.remove(i);
                gender_req.remove(i);
                age_req.remove(i);
                custom_req.notifyDataSetChanged();

                Toast.makeText(getActivity(), "Request accepted!", Toast.LENGTH_SHORT).show();
                Log.d("send ", "pushed successfully");
            }
        });
    }

    private void refresh_data_final() {
        clear_lists();
        reference = FirebaseDatabase.getInstance().getReference().child("male");
        Log.d("try ", "inside refresh_data()");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("beta", "1) went inside onchildadded");
                asd++;
                temp = 0;
                loading.setVisibility(View.GONE);
                if (asd <= no_of_children) {
                    keys.add(snapshot.getKey());
                    m_keys.add(snapshot.getKey());
                    Log.d("key ", "snap key " + snapshot.getKey());
                }
                String final_data = "";
                Log.d("beta", "    went before");
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Log.d("beta", "2) inside populating master list");
                    String data = ds.getValue(String.class);
                    if (temp == 0) {
                        Log.d("delta ", "data :" + data);
                        separated = data.split("\\#");
                        m_names.add(separated[0]);
                        m_family.add(separated[1]);
                        m_age.add(Integer.valueOf(separated[2]));
                        m_gender.add(separated[3]);
                        m_links.add(separated[4]);
                        Log.d("delta ", "\nname :" + separated[0] + "\nfam :" + separated[1] + "\nage :" + age + "\ngen :" + gender + "\nlink :" + separated[4]);
                    }
                    if (temp == 1) {
                        received.add(data);
                        m_received.add(data);
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
                            names.add(separated[0]);
                            family.add(separated[1]);
                            links.add(separated[4]);
                            gender.add(separated[3]);
                            age.add(Integer.valueOf(separated[2]));
                            Log.d("loop ", "added " + separated[0]);
                        }
                    }
                }
                //the following code is to populate 'received' requests from other users
                //looks like we need to check the send section of every user
                //asd is  total child count
                if (asd == no_of_children) {
                    Log.d("beta", "4) inside populating received list");
                    Log.d("lopp ", "Entered the loop with n=" + n);
                    for (int q = inc; q < n; q++) {
                        Log.d("lopp ", "***********************************************************");
                        Log.d("lopp", "1) comparing " + key + " with " + keys.get(q) + " while q=" + q);
                        //this loop will be entered onny once
                        //this for loop is to fetch only the required user account
                        //this is where all the decoding begins
                        if (String.valueOf(key).equals(keys.get(q))) {
                            //this means the current user is found
                            Log.d("lopp ", "2) success with key " + keys.get(q) + " and name :" + m_names.get(q));
                            Log.d("lopp ", "  received of q =" + received.get(q));
                            //now we fetch the received list of current user to break it down
                            String[] sep = received.get(q).split("\\:");
                            for (int e = temp_e; e < sep.length; e++) {
                                Log.d("lopp ", "3) e=" + e + " and sep before entering for is " + sep[e]);
                                for (int z = 0; z < m_names.size(); z++) {
                                    Log.d("lopp ", "4) z=" + z + " and size of m_names=" + m_names.size());
                                    Log.d("lopp ", "   comparing " + sep[e] + " with " + m_names.get(z) + "(" + m_keys.get(z) + ")");
                                    if (sep[e].equals(m_keys.get(z))) {
                                        Log.d("lopp ", "5) ****added " + m_names.get(z) + "**** because z=" + z);
                                        names_req.add(m_names.get(z));
                                        family_req.add(m_family.get(z));
                                        links_req.add(m_links.get(z));
                                        received_req.add(m_received.get(z));
                                        keys_req.add(m_keys.get(z));
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

                //the following code is to find matched (lucky guys)
                for (int get_i = 0; get_i < names.size(); get_i++) {
                    Log.d("beta", "5)  inside populating metched list");
                    Log.d("matriz ", "1) inside for i=" + get_i + " and name =" + names.get(get_i) + "(" + keys.get(get_i) + ")");
                    for (int get_j = 0; get_j < names_req.size(); get_j++) {
                        Log.d("matriz ", "2) inside for with j=" + get_j + " with name_req =" + names_req.get(get_j));
                        Log.d("matriz ", "   comparing " + names.get(get_i) + " and " + names_req.get(get_j));
                        //TODO:dont forget to change the name
                        if (names.get(get_i).equals(names_req.get(get_j)) && !names.get(get_i).equals(user_name) && !names_req.get(get_j).equals(user_name)) {
                            Log.d("matriz ", names.get(get_i) + " ***** matched with ***** " + names_req.get(get_j));
                            names_match.add(names_req.get(get_j));
                            family_match.add(family_req.get(get_j));
                            links_match.add(links_req.get(get_j));
                            gender_match.add(gender_req.get(get_j));
                            age_match.add(age_req.get(get_j));

                            //trying a method of removing the same from requested list
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
                    inc = 0;
                    Log.d("beta", "asd reset");
                }
                count++;
                custom.notifyDataSetChanged();
                custom_req.notifyDataSetChanged();
                custom_match.notifyDataSetChanged();
                Log.d("beta", "*****************************************************");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("beta", "went inside on child changed!!! " + previousChildName);
                custom.notifyDataSetChanged();
                custom_match.notifyDataSetChanged();
                custom_req.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d("beta", "went inside on child removed");
                custom.notifyDataSetChanged();
                custom_match.notifyDataSetChanged();
                custom_req.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("beta", "went inside onChildMoved");
                custom.notifyDataSetChanged();
                custom_match.notifyDataSetChanged();
                custom_req.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("beta", "went inside onCancelled");
                custom.notifyDataSetChanged();
                custom_match.notifyDataSetChanged();
                custom_req.notifyDataSetChanged();
            }
        });
    }

    //this adapter is to show all 'sent' requests lists
    @Keep
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

    //this adapter is to display 'requests' from other users
    @Keep
    class custom_adapter_for_requests extends BaseAdapter {

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
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getContext()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.display_list_received, null);
            }
            Log.d("try ", "inside times " + i);
            ImageView img = view.findViewById(R.id.pic);
            TextView name = view.findViewById(R.id.name);
            Button request = view.findViewById(R.id.request);
            Log.d("loop ", "**************************************************");
            Log.d("loop ", "the value of n before entering =" + n);

            name.setText("Name :" + names_req.get(i) + "\nFamily :" + family_req.get(i) + "\nAge :" + age_req.get(i));
            Picasso.get().load(links_req.get(i)).fit().centerCrop().into(img);

            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //we have all the details of the sender account
                    AlertDialog.Builder build = new AlertDialog.Builder(getActivity(), R.style.alert_custom);
                    build.setTitle("Accept request from " + names_req.get(i));
                    build.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int in) {
                            request.setText("Accepted");
                            accept_request(received_req.get(i), gender_req.get(i), keys_req.get(i), i);
                        }
                    }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int in) {

                        }
                    }).show();
                }
            });
            return view;
        }
    }

    class custom_adapter_for_list_match extends BaseAdapter {

        @Override
        public int getCount() {
            Log.d("loop ", "size " + names.size());
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
            Log.d("try ", "inside times " + i);
            ImageView img = view.findViewById(R.id.pic);
            TextView name = view.findViewById(R.id.name);
            Button request = view.findViewById(R.id.request);
            Log.d("loop ", "**************************************************");
            Log.d("loop ", "the value of n before entering =" + n);

            name.setText("Name :" + names_match.get(i) + "\nFamily :" + family_match.get(i) + "\nAge :" + age_match.get(i));
            Picasso.get().load(links_match.get(i)).fit().centerCrop().into(img);

            request.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("QueryPermissionsNeeded")
                @Override
                public void onClick(View view) {
                    Context context;
                    AlertDialog.Builder build = new AlertDialog.Builder(getActivity(), R.style.alert_custom);
                    build.setTitle("Contact " + names_req.get(i) + "?");
                    build.setMessage("number :" + keys_req.get(i));
                    build.setPositiveButton("COPY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int in) {
                            getContext();
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("+91", keys_req.get(i));
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(getActivity(), "Number copied to clipboard", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int in) {

                        }
                    }).setNeutralButton("CALL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int in) {
                            Intent intent_name = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+91" + keys_req.get(i)));
                            startActivity(intent_name);
                        }
                    }).show();
                }
            });
            return view;
        }
    }

}