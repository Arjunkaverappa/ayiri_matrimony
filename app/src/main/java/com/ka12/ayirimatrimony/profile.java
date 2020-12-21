package com.ka12.ayirimatrimony;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class profile extends Fragment {
    public static final String GENDER = "com.ka12.ayiri_matrimony_this_is_where_gender_is_stored";
    public static final String KEY = "com.ka12.ayiri_matrimony_this_is_where_key_is_stored";
    public static final String P_LINK = "com.ka12.ayiri_matrimony.this_is_where_local_link_is_saved";
    public static final String D_LINK = "com.ka12.ayiri_matrimony.this_is_where_download_link_is_saved";
    public static final String NAME = "com.ka12.ayiri_matrimony_this_is_where_name_is_stored";
    public static final String FAMILY = "com.ka12.ayiri_matrimony_this_is_where_family_is_stored";
    public static final String AGE = "com.ka12.ayiri_matrimony_this_is_where_family_is_stored";
    CircleImageView image;
    ImageView a;//image;
    TextView name, age, family, edit, desc;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    String user_key, user_gender, profile_link, u_name, u_fam, u_age, all_update_data, update_conversion;
    int count = 0;
    String[] split_update;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        name = v.findViewById(R.id.name);
        age = v.findViewById(R.id.age);
        family = v.findViewById(R.id.family);
        image = v.findViewById(R.id.image);
        edit = v.findViewById(R.id.edit);
        desc = v.findViewById(R.id.desc);

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#ED8A6B"));

        //retrieving user name
        SharedPreferences get_n = getActivity().getSharedPreferences(NAME, MODE_PRIVATE);
        u_name = get_n.getString("name", "null");
        Log.d("User name :", u_name);

        //retrieving profile image download link
        SharedPreferences getlink = Objects.requireNonNull(getActivity()).getSharedPreferences(D_LINK, MODE_PRIVATE);
        profile_link = getlink.getString("link", "something_went_wrong");
        Log.d("profile_link", profile_link);

        //retrieving user gender
        SharedPreferences getgender = Objects.requireNonNull(getActivity()).getSharedPreferences(GENDER, MODE_PRIVATE);
        user_gender = getgender.getString("gender", "female");
        Log.d("datas", "user gender " + user_gender);

        //retrieving the key of the current user
        SharedPreferences ediss = Objects.requireNonNull(getActivity()).getSharedPreferences(KEY, MODE_PRIVATE);
        user_key = ediss.getString("key", "999999999");
        Log.d("datas", "user key " + user_key);

        //retrieving the family
        SharedPreferences get_fam = getActivity().getSharedPreferences(FAMILY, MODE_PRIVATE);
        u_fam = get_fam.getString("family", "null");

        //retrieving the family
        SharedPreferences get_a = getActivity().getSharedPreferences(AGE, MODE_PRIVATE);
        u_age = get_a.getString("age", "21");
        Log.d("User age", u_age);

        refresh_data_final();
        set_up_strings(u_name, u_fam, u_age);
        //setting up image
        SharedPreferences get = getActivity().getSharedPreferences(P_LINK, Context.MODE_PRIVATE);
        String image_url = get.getString("plink", "something_went_wrong");
        Log.d("recc ", String.valueOf(Uri.parse(image_url)));
        Log.d("recc ", String.valueOf(Uri.parse(image_url).getPath()));
        try {
            Picasso.get().load(profile_link).into(image);
        } catch (Exception e) {
            Log.d("recc ", e.getMessage());
        }

        //setting up on click listerners
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setVisibility(View.VISIBLE);
                age.setVisibility(View.VISIBLE);
                family.setVisibility(View.VISIBLE);
                a.setVisibility(View.VISIBLE);
            }
        });
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_fields("name");
            }
        });
        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_fields("age");
            }
        });
        family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_fields("family");
            }
        });
        desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_fields("bio");
            }
        });
        desc.setVisibility(View.GONE);
        return v;
    }

    public void set_up_strings(String get_name, String get_family, String get_age) {
        get_name = "Name \n" + get_name;
        SpannableString s = new SpannableString(get_name);
        s.setSpan(new RelativeSizeSpan(0.7f), 0, 5, 0);
        name.setText(s);

        get_family = "Family \n" + get_family;
        SpannableString s2 = new SpannableString(get_family);
        s2.setSpan(new RelativeSizeSpan(0.7f), 0, 7, 0);
        family.setText(s2);

        get_age = "Age \n" + get_age;
        SpannableString s3 = new SpannableString(get_age);
        s3.setSpan(new RelativeSizeSpan(0.7f), 0, 4, 0);
        age.setText(s3);

        Log.d("datas", "Strings updated successfully " + count);
    }

    void change_fields(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.alert_custom);
        builder.setTitle("Please enter your " + text);
        View inflated = LayoutInflater.from(getContext()).inflate(R.layout.edit_fields, (ViewGroup) getView(), false);
        EditText edits = inflated.findViewById(R.id.edits);
        if (text.equals("age"))
        {
            edits.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        if(text.equals("name") || text.equals("family"))
        {
            edits.setMaxLines(1);
        }else if(text.equals("bio"))
        {
            edits.setMaxLines(3);
        }
        builder.setView(inflated);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if (edits.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter a value", Toast.LENGTH_SHORT).show();
                } else {
                    String value = edits.getText().toString();
                    change_values_now(text, value);
                    Log.d("value ", value);
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    private void refresh_data_final() {
        reference = FirebaseDatabase.getInstance().getReference().child(user_gender).child(user_key);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (count == 0)
                {
                    String data = snapshot.getValue(String.class);
                    Log.d("datas ", "fetched :" + data);

                    all_update_data = data;
                    String[] split_data = data.split("\\#");
                    set_up_strings(split_data[0], split_data[1], split_data[2]);

                    //setting up description text
                    desc.setVisibility(View.VISIBLE);
                    String get_desc = "Bio \n" + split_data[9];
                    SpannableString s4 = new SpannableString(get_desc);
                    s4.setSpan(new RelativeSizeSpan(0.7f), 0, 4, 0);
                    desc.setText(s4);
                }
                count++;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("try ", "triggered on child changed");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d("try ", "triggered on child removed");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("try ", "triggered on child moved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("try ", "triggered on child cancelled");

            }
        });
    }

    private void change_values_now(String text, String new_value)
    {
        update_conversion = "";
        if (all_update_data != null)
        {
            split_update = all_update_data.split("\\#");
        }
        switch (text) {
            case "name":SharedPreferences.Editor edit_name=getActivity().getSharedPreferences(NAME,MODE_PRIVATE).edit();
              edit_name.putString("name",new_value).apply();
                update_conversion = new_value + "#" + split_update[1] + "#" + split_update[2] + "#" + split_update[3] + "#"
                        + split_update[4] + "#" + split_update[5] + "#" + split_update[6] + "#" + split_update[7] + "#" + split_update[8] + "#" + split_update[9];
                break;
            case "family":SharedPreferences.Editor edit_fam=getActivity().getSharedPreferences(FAMILY,MODE_PRIVATE).edit();
                edit_fam.putString("family",new_value).apply();
                update_conversion = split_update[0] + "#" + new_value + "#" + split_update[2] + "#" + split_update[3] + "#"
                        + split_update[4] + "#" + split_update[5] + "#" + split_update[6] + "#" + split_update[7] + "#" + split_update[8] + "#" + split_update[9];
                break;
            case "age":SharedPreferences.Editor edit_age=getActivity().getSharedPreferences(AGE,MODE_PRIVATE).edit();
                edit_age.putString("age",new_value).apply();
                update_conversion = split_update[0] + "#" + split_update[1] + "#" + new_value + "#" + split_update[3] + "#"
                        + split_update[4] + "#" + split_update[5] + "#" + split_update[6] + "#" + split_update[7] + "#" + split_update[8] + "#" + split_update[9];
                break;
            case "bio":
                update_conversion = split_update[0] + "#"+split_update[1]+"#"+ split_update[2] + "#" + split_update[3] + "#"
                        + split_update[4] + "#" + split_update[5] + "#" + split_update[6] + "#" + split_update[7] + "#" + split_update[8] + "#" + new_value;
                break;

        }
        Log.d("datas", "update_conversion :" + update_conversion);

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child(user_gender).child(user_key).child("name");
        reference.setValue(update_conversion).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                count=0;
                refresh_data_final();
                Toast.makeText(getActivity(), "updated profile!", Toast.LENGTH_SHORT).show();
                Log.d("datas", "updated in database");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Soemthing went wrong\nPlease try later", Toast.LENGTH_SHORT).show();
                Log.d("datas", "error :" + e.getMessage());
            }
        });
    }
    //we need to check connection
}