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
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends Fragment {
    TextView name,surname,age,phone,family,edit;
    CircleImageView image;
    ImageView a;
    public static final String P_LINK = "com.ka12.ayiri_matrimony.this_is_where_local_link_is_saved";
    public static final String KEY="com.ka12.ayiri_matrimony_this_is_where_key_is_stored";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_profile, container, false);
        name=v.findViewById(R.id.name);
        surname=v.findViewById(R.id.surname);
        age=v.findViewById(R.id.age);
        family=v.findViewById(R.id.family);
        phone=v.findViewById(R.id.phone);
        image=v.findViewById(R.id.image);
        edit=v.findViewById(R.id.edit);
        a=v.findViewById(R.id.a);
        image.setVisibility(View.GONE);
        //hinding the views
        /*
        name.setVisibility(View.GONE);
        surname.setVisibility(View.GONE);
        phone.setVisibility(View.GONE);
        age.setVisibility(View.GONE);
        family.setVisibility(View.GONE);
        a.setVisibility(View.GONE);
         */
        //setting up image
        SharedPreferences get=getActivity().getSharedPreferences(P_LINK, Context.MODE_PRIVATE);
        String image_url=get.getString("plink","something_went_wrong");
        Log.d("recc ",image_url);
        try{
            Picasso.get().load(Uri.parse(image_url)).into(image);
          //  image.setImageURI(Uri.parse(image_url));
        }catch (Exception e)
        {
            Log.d("recc ",e.getMessage());
        }

        //setting up on click listerners
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setVisibility(View.VISIBLE);
                surname.setVisibility(View.VISIBLE);
                phone.setVisibility(View.VISIBLE);
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
        surname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_fields("surname");
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
    void change_fields(String text)
    {
       AlertDialog.Builder builder=new AlertDialog.Builder(getContext(),R.style.alert_custom);
       builder.setTitle("Please enter new "+text);
       View inflated=LayoutInflater.from(getContext()).inflate(R.layout.edit_fields, (ViewGroup) getView(), false);
       EditText edits=inflated.findViewById(R.id.edits);
       builder.setView(inflated);
       builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
              if(edits.getText().toString().equals(""))
               {
                   Toast.makeText(getContext(), "Please enter a value", Toast.LENGTH_SHORT).show();
               }else {
                   String value = edits.getText().toString();
                   change_values_now(text,value);
                   Log.d("value ",value);
               }
           }
       }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {

           }
       });
       builder.show();
    }

    private void change_values_now(String text, String value)
    {
       SharedPreferences getkey=getActivity().getSharedPreferences(KEY,Context.MODE_PRIVATE);
       Integer key=getkey.getInt("key",99999999);
       Log.d("key","recieved "+ key);
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child("male");
        reference.child(String.valueOf(key)).child(text).setValue(value).addOnCompleteListener(new OnCompleteListener<Void>() {
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