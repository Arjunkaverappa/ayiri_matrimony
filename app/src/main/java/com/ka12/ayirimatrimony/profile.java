package com.ka12.ayirimatrimony;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class profile extends Fragment {
    public static final String GENDER = "com.ka12.ayiri_matrimony_this_is_where_gender_is_stored";
    public static final String KEY = "com.ka12.ayiri_matrimony_this_is_where_key_is_stored";
    public static final String P_LINK = "com.ka12.ayiri_matrimony.this_is_where_local_link_is_saved";
   // CircleImageView image;
    ImageView a,image;
    TextView name, age, family, edit;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    String user_key, user_gender;
    int count = 0;

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
        a = v.findViewById(R.id.a);

        SharedPreferences getgender = getActivity().getSharedPreferences(GENDER, MODE_PRIVATE);
        user_gender = getgender.getString("gender", "female");
        Log.d("datas", "user gender " + user_gender);

        //retrieving the key of the current user
        SharedPreferences ediss = Objects.requireNonNull(getActivity()).getSharedPreferences(KEY, MODE_PRIVATE);
        user_key = ediss.getString("key", "999999999");
        Log.d("datas", "user key " + user_key);

        refresh_data_final();

        //setting up image
        SharedPreferences get = getActivity().getSharedPreferences(P_LINK, Context.MODE_PRIVATE);
        String image_url = get.getString("plink", "something_went_wrong");
        Log.d("recc ", String.valueOf(Uri.parse(image_url)));
        Log.d("recc ", String.valueOf(Uri.parse(image_url).getPath()));
        try {
            // Picasso.get().load(Uri.parse(image_url).getPath()).into(image);
           // Picasso.get().load(Uri.parse(image_url)).into(image);
            image.setImageURI(Uri.parse(image_url));
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
        return v;
    }

    void change_fields(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.alert_custom);
        builder.setTitle("Please enter new " + text);
        View inflated = LayoutInflater.from(getContext()).inflate(R.layout.edit_fields, (ViewGroup) getView(), false);
        EditText edits = inflated.findViewById(R.id.edits);
        builder.setView(inflated);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (edits.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please enter a value", Toast.LENGTH_SHORT).show();
                } else {
                    String value = edits.getText().toString();
                    Toast.makeText(getActivity(), "coming soon", Toast.LENGTH_SHORT).show();
                    // change_values_now(text,value);
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
        reference = FirebaseDatabase.getInstance().getReference().child(user_gender);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                count = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Log.d("datas", "ds key " + ds.getKey());
                    if (count == 1) {
                        Log.d("datas", "comaring ds key " + ds.getKey() + " user key " + user_key);
                        if (user_key.equals(ds.getKey())) {
                            String data = ds.getValue(String.class);
                            Log.d("datas", data);
                        }
                    }
                    count++;
                }
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

    private void change_values_now(String text, String value) {

        Log.d("key ", "received " + user_key);

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child("male");
        reference.child(user_key).child(text).setValue(value).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity(), "updated profile!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Soemthing went wrong\nPlease try later", Toast.LENGTH_SHORT).show();
            }
        });
    }
}