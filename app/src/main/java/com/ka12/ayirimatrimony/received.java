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
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class received extends Fragment {
    public static final String CUR_USER_DATA = "com.ka12.ayiri_this_is_where_current_user_data_is_aved";
    public static final String KEY = "com.ka12.ayiri_matrimony_this_is_where_key_is_stored";
    public static final String CHILD = "com.ka12.ayiri_matrimony_number_of_child_nodes";
    public static final String NAME = "com.ka12.ayiri_matrimony_this_is_where_name_is_stored";
    public static final String GENDER = "com.ka12.ayiri_matrimony_this_is_where_gender_is_stored";
    public static final String FAMILY = "com.ka12.ayiri_matrimony_this_is_where_family_is_stored";
    //master list
    public ArrayList<String> m_names = new ArrayList<>();
    public ArrayList<String> m_family = new ArrayList<>();
    public ArrayList<Integer> m_age = new ArrayList<>();
    public ArrayList<String> m_gender = new ArrayList<>();
    public ArrayList<String> m_links = new ArrayList<>();
    public ArrayList<String> m_keys = new ArrayList<>();
    public ArrayList<String> m_sent = new ArrayList<>();
    public ArrayList<String> m_received = new ArrayList<>();
    public ArrayList<String> m_notification = new ArrayList<>();
    public ArrayList<String> names_req = new ArrayList<>();
    public ArrayList<String> family_req = new ArrayList<>();
    public ArrayList<Integer> age_req = new ArrayList<>();
    public ArrayList<String> gender_req = new ArrayList<>();
    public ArrayList<String> links_req = new ArrayList<>();
    public ArrayList<String> keys_req = new ArrayList<>();
    public ArrayList<String> sent_req = new ArrayList<>();
    public ArrayList<String> received_req = new ArrayList<>();
    public ArrayList<String> noti_req = new ArrayList<>();
    public ArrayList<String> names = new ArrayList<>();
    public ArrayList<String> family = new ArrayList<>();
    public ArrayList<Integer> age = new ArrayList<>();
    public ArrayList<String> gender = new ArrayList<>();
    public ArrayList<String> links = new ArrayList<>();
    public ArrayList<String> keys = new ArrayList<>();
    public ArrayList<String> sent = new ArrayList<>();
    public ArrayList<String> received = new ArrayList<>();
    //database references
    DatabaseReference reference;
    FirebaseDatabase firebaseDatabase;
    LottieAnimationView loading;
    //the following are for 'received' list
    ListView requests_list;
    //the following are for 'sent' list
    ListView list_name;
    custom_adapter_for_requests custom_req = new custom_adapter_for_requests();
    int no_of_children,n,sizz,total_count,count,asd,temp_e,temp;
    Boolean is_checked = false;
    String current_user_received,key,user_gender,user_name,search_gender,accept_data;
    String[] separated;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_received, container, false);
        requests_list=v.findViewById(R.id.requests);
        loading=v.findViewById(R.id.loading);
        try {
            //getting the current user 'received' data
            SharedPreferences getdata = getActivity().getSharedPreferences(CUR_USER_DATA, MODE_PRIVATE);
            current_user_received = getdata.getString("data", "something_went_wrong");

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

            requests_list.setAdapter(custom_req);

            refresh_data_final();
        }catch (Exception e)
        {
            Log.d("error received","catch in onCreate"+e.getMessage());
        }
        return v;
    }
    private void refresh_data_final()
    {
        try {
            clear_lists();
            Log.d("flash", "cuurent user received :" + current_user_received);
            SharedPreferences getgender = getActivity().getSharedPreferences(GENDER, MODE_PRIVATE);
            user_gender = getgender.getString("gender", "female");

            if (user_gender.equals("male")) {
                search_gender = "female";
            } else {
                search_gender = "male";
            }
            Log.d("flash ", "inside refresh_data()");

            reference = FirebaseDatabase.getInstance().getReference().child(search_gender);
            reference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("flash", "1) went inside onchildadded");
                    loading.setVisibility(View.GONE);
                    asd++;
                    temp = 0;
                    loading.setVisibility(View.GONE);
                    if (asd <= no_of_children) {
                        keys.add(snapshot.getKey());
                        m_keys.add(snapshot.getKey());
                        Log.d("key ", "snap key " + snapshot.getKey());
                    }
                    String final_data = "";
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Log.d("flash", "2) inside populating master list");
                        String data = ds.getValue(String.class);
                        if (temp == 0) {
                            Log.d("delta ", "data :" + data);
                            separated = data.split("\\#");
                            m_names.add(separated[0]);
                            m_family.add(separated[1]);
                            m_age.add(Integer.valueOf(separated[2]));
                            m_gender.add(separated[3]);
                            m_links.add(separated[4]);
                            Log.d("flash ", "\nname :" + separated[0] + "\nfam :" + separated[1]);
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
                    Log.d("flash", "asd=" + asd + " no_of_children=" + no_of_children);

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

                    //the following is for received section
                    if (asd == no_of_children) {
                        Log.d("flash", "////////RECEIVED////////");
                        Log.d("flash", "current data inside the loop :" + current_user_received);
                        String[] sepea = current_user_received.split("\\:");
                        for (int s = 0; s < sepea.length; s++) {
                            Log.d("flash", "for s =" + s + " length of data:" + sepea.length);
                            for (int d = 0; d < m_keys.size(); d++) {
                                Log.d("flash", "d =" + d);
                                Log.d("flash", "comparing " + sepea[s] + " with " + m_keys.get(d) + " (" + m_names.get(d) + ")");
                                if (sepea[s].equals(m_keys.get(d))) {
                                    Log.d("flash", "**********ADDED " + m_names.get(d).toUpperCase() + " *************");
                                    names_req.add(m_names.get(d));
                                    family_req.add(m_family.get(d));
                                    links_req.add(m_links.get(d));
                                    received_req.add(m_received.get(d));
                                    keys_req.add(m_keys.get(d));
                                    gender_req.add(m_gender.get(d));
                                    age_req.add(Integer.parseInt(String.valueOf(m_age.get(d))));
                                    noti_req.add(m_notification.get(d));
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
                            if (names.get(get_i).equals(names_req.get(get_j)) && !names.get(get_i).equals(user_name) && !names_req.get(get_j).equals(user_name)) {
                                Log.d("matriz ", names.get(get_i) + " ***** matched with ***** " + names_req.get(get_j));

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
                        temp_e = 0;
                        Log.d("beta", "asd reset");
                    }
                    count++;
                    custom_req.notifyDataSetChanged();
                    Log.d("flash", "*****************************************************");
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("flash", "went inside on child changed!!! " + previousChildName);
                    custom_req.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    Log.d("flash", "went inside on child removed");
                    custom_req.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("flash", "went inside onChildMoved");
                    custom_req.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("flash", "went inside onCancelled");
                    custom_req.notifyDataSetChanged();
                }
            });
        }catch (Exception e)
        {
            Log.d("error received","catch in refresh_data_final"+e.getMessage());
        }
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

        //clearing received requests
        names_req.clear();
        family_req.clear();
        age_req.clear();
        received_req.clear();
        keys_req.clear();
        gender_req.clear();
        links_req.clear();

        //notifying the adapters
        custom_req.notifyDataSetChanged();
        Log.d("beta ", "clear list initiated");
    }

    public void accept_request(String data, String sender_gender, String sender_key, int i)
    {
        try {
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
                    //sending notification
                    send_notification(i);

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
        }catch (Exception e)
        {
            Log.d("error received","catch in accept_request :"+e.getMessage());
        }
    }

    public void send_notification(int index)
    {
        try {
            String user_id = noti_req.get(index);
            String sender_name = names_req.get(index);
            String sender_family = family_req.get(index);

            //getting user family
            SharedPreferences getfamily = getActivity().getSharedPreferences(FAMILY, MODE_PRIVATE);
            String user_family = getfamily.getString("family", "null");

            //retreiving the user_name
            SharedPreferences getname = getActivity().getSharedPreferences(NAME, MODE_PRIVATE);
            String user_name = getname.getString("name", "null");

            String message = "Hey " + sender_name + ", " + user_family + " " + user_name + " has accepted your request!";
            Log.d("json", "player id=" + user_id);
            try {
                OneSignal.postNotification(new JSONObject("{'contents': {'en':'" + message + "'}, 'include_player_ids': ['" + user_id + "']}"), null);
            } catch (JSONException e) {
                Log.d("json", "Error :" + e.getMessage());
                e.printStackTrace();
            }
        }catch (Exception e)
        {
            Log.d("error received","catch in send_notification :"+e.getMessage());
        }
    }

    //this adapter is to display 'requests' from other users
    @Keep
    class custom_adapter_for_requests extends BaseAdapter {

        @Override
        public int getCount() {
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
            try {
                Log.d("flash ", "inside times " + i);
                ImageView img = view.findViewById(R.id.pic);
                TextView name = view.findViewById(R.id.name);
                Button request = view.findViewById(R.id.request);
                Log.d("flashs ", "**************************************************");
                Log.d("flashs ", "the value of n before entering =" + n);

                name.setText("Name :" + names_req.get(i) + "\nFamily :" + family_req.get(i) + "\nAge :" + age_req.get(i));
                Picasso.get().load(links_req.get(i)).fit().centerCrop().into(img);

                request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //we have all the details of the sender account

                        AlertDialog.Builder b = new AlertDialog.Builder(getActivity(), R.style.alert_custom);
                        b.setTitle("Disclaimer");
                        b.setMessage("Accepting this request will mean that you have matched with " + names_req.get(i)
                                + ".\nyour contact details will be shared with " + names_req.get(i) + ".Do you still wish to continue?");
                        b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int in) {
                                request.setText("Accepted");
                                accept_request(received_req.get(i), gender_req.get(i), keys_req.get(i), i);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int in) {

                            }
                        }).show();
                    }
                });
            }catch (Exception e)
            {
                Log.d("error received","catch in custom_adapter_for_requests :"+e.getMessage());
            }
            return view;
        }
    }
}