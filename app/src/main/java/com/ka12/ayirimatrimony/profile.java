package com.ka12.ayirimatrimony;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.yeyint.customalertdialog.CustomAlertDialog;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/*
   TODO:add logout
 */
public class profile extends Fragment {
    public static final String LOGIN = "com.ka12.ayiri_matrimony_login_details";
    public static final String GENDER = "com.ka12.ayiri_matrimony_this_is_where_gender_is_stored";
    public static final String KEY = "com.ka12.ayiri_matrimony_this_is_where_key_is_stored";
    public static final String P_LINK = "com.ka12.ayiri_matrimony.this_is_where_local_link_is_saved";
    public static final String D_LINK = "com.ka12.ayiri_matrimony.this_is_where_download_link_is_saved";
    public static final String NAME = "com.ka12.ayiri_matrimony_this_is_where_name_is_stored";
    public static final String FAMILY = "com.ka12.ayiri_matrimony_this_is_where_family_is_stored";
    public static final String AGE = "com.ka12.ayiri_matrimony_this_is_where_family_is_stored";
    CircleImageView image;
    StorageReference storageReference;
    Boolean is_connected = true;
    Handler handler = new Handler();
    LottieAnimationView loading;
    ImageView a, one, two, three;
    TextView name, age, family, edit, desc, change_log, delete, logout, faq;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    String user_key, user_gender, profile_link, u_name, u_fam, u_age, all_update_data, update_conversion, dlink,description;
    int count = 0, edit_temp = 0;
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
        change_log = v.findViewById(R.id.change_log);
        delete = v.findViewById(R.id.delete);
        logout = v.findViewById(R.id.logout);
        a = v.findViewById(R.id.a);
        one = v.findViewById(R.id.one);
        two = v.findViewById(R.id.two);
        three = v.findViewById(R.id.three);
        loading = v.findViewById(R.id.loading);
        faq = v.findViewById(R.id.faq);

        Window window = Objects.requireNonNull(getActivity()).getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#ED8A6B"));

        //hiding views
        name.setVisibility(View.GONE);
        age.setVisibility(View.GONE);
        family.setVisibility(View.GONE);
        desc.setVisibility(View.GONE);
        a.setVisibility(View.GONE);
        one.setVisibility(View.GONE);
        two.setVisibility(View.GONE);
        three.setVisibility(View.GONE);

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

        //retrieving the download link of profile img
        SharedPreferences get_dlink = getActivity().getSharedPreferences(D_LINK, MODE_PRIVATE);
        dlink = get_dlink.getString("link", "null");
        Log.d("download", "link :" + dlink);

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
                if (edit_temp % 2 == 0) {
                    name.setVisibility(View.VISIBLE);
                    age.setVisibility(View.VISIBLE);
                    family.setVisibility(View.VISIBLE);
                    desc.setVisibility(View.VISIBLE);
                    a.setVisibility(View.VISIBLE);
                    one.setVisibility(View.VISIBLE);
                    two.setVisibility(View.VISIBLE);
                    three.setVisibility(View.VISIBLE);
                } else {
                    name.setVisibility(View.GONE);
                    age.setVisibility(View.GONE);
                    family.setVisibility(View.GONE);
                    desc.setVisibility(View.GONE);
                    a.setVisibility(View.GONE);
                    one.setVisibility(View.GONE);
                    two.setVisibility(View.GONE);
                    three.setVisibility(View.GONE);
                }
                edit_temp++;
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
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ins = new Intent(getActivity(), com.ka12.ayirimatrimony.FAQ.class);
                startActivity(ins);
                Animatoo.animateSwipeRight(Objects.requireNonNull(getContext()));
            }
        });
        change_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder change = new AlertDialog.Builder(getActivity(), R.style.alert_custom);
                change.setTitle("Whats new?");
                change.setMessage(R.string.change_log);
                change.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                change.show();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomAlertDialog customAlertDialog = new CustomAlertDialog(Objects.requireNonNull(getActivity()), CustomAlertDialog.DialogStyle.CURVE);
                customAlertDialog.setAlertTitle("Disclaimer");
                customAlertDialog.setAlertMessage("Do you want to logout from this device?");
                customAlertDialog.setDialogType(CustomAlertDialog.DialogType.INFO);
                customAlertDialog.setPositiveButton("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SharedPreferences.Editor setlogout = getActivity().getSharedPreferences(LOGIN, MODE_PRIVATE).edit();
                        setlogout.putBoolean("login", false).apply();

                        // PreferenceManager.getDefaultSharedPreferences(getContext()).edit().clear().apply();
                        Intent in = new Intent(getActivity(), com.ka12.ayirimatrimony.Login.class);
                        startActivity(in);
                        getActivity().finish();
                        Animatoo.animateZoom(getActivity());
                    }
                });
                customAlertDialog.setNegativeButton("No", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customAlertDialog.cancel();
                    }
                });
                customAlertDialog.create();
                customAlertDialog.show();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomAlertDialog customAlertDialog = new CustomAlertDialog(Objects.requireNonNull(getActivity()), CustomAlertDialog.DialogStyle.CURVE);
                customAlertDialog.setAlertTitle("Disclaimer");
                customAlertDialog.setAlertMessage("Do you want to delete your account?\nThis cannot be undone.");
                customAlertDialog.setDialogType(CustomAlertDialog.DialogType.INFO);
                customAlertDialog.setPositiveButton("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delete_account();
                    }
                });
                customAlertDialog.setNegativeButton("No", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customAlertDialog.cancel();
                    }
                });
                customAlertDialog.create();
                customAlertDialog.show();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.setVisibility(View.GONE);
            }
        }, 2500);
        return v;
    }

    public void delete_account() {
        reference = FirebaseDatabase.getInstance().getReference().child(user_gender).child(user_key);
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (is_connected) {

                    delete_photos();
                    Toast.makeText(getActivity(), "Account deleted", Toast.LENGTH_SHORT).show();
                   Intent in = new Intent(getActivity(), Login.class);
                   startActivity(in);
                   Animatoo.animateZoom(Objects.requireNonNull(getActivity()));
                } else {
                    Toast.makeText(getActivity(), "Please connect to internet", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                Log.d("error ", "error while deleting account :" + e.getMessage());
            }
        });
    }

    public void delete_photos()
    {
      storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(dlink);
      storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
             // Toast.makeText(getActivity(), "deleted image", Toast.LENGTH_SHORT).show();
          }
      }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
              Toast.makeText(getActivity(), "Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
              Log.d("error ",e.getMessage());
          }
      });
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
        switch (text) {
            case "age":
                edits.setText(u_age);
                edits.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case "name":
                edits.setText(u_name);
                edits.setMaxLines(1);
                break;
            case "bio":
                edits.setText(description);
                edits.setMaxLines(3);
                break;
            case "family":
                edits.setText(u_fam);
                edits.setMaxLines(1);
                break;
        }
        builder.setView(inflated);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
                    if (data != null)
                    {
                        String[] split_data = data.split("\\#");
                        set_up_strings(split_data[0], split_data[1], split_data[2]);

                        String get_desc = "Bio \n" + split_data[9];
                        SpannableString s4 = new SpannableString(get_desc);
                        s4.setSpan(new RelativeSizeSpan(0.7f), 0, 4, 0);
                        desc.setText(s4);
                        description=split_data[9];
                    }
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

    private void change_values_now(String text, String new_value) {
        update_conversion = "";
        if (all_update_data != null)
        {
            split_update = all_update_data.split("\\#");
        }
        switch (text) {
            case "name":
                SharedPreferences.Editor edit_name = Objects.requireNonNull(getActivity()).getSharedPreferences(NAME, MODE_PRIVATE).edit();
                edit_name.putString("name", new_value).apply();
                update_conversion = new_value + "#" + split_update[1] + "#" + split_update[2] + "#" + split_update[3] + "#"
                        + split_update[4] + "#" + split_update[5] + "#" + split_update[6] + "#" + split_update[7] + "#" + split_update[8] + "#" + split_update[9]
                        +"#"+split_update[10]+"#"+split_update[11]+"#"+split_update[12]+"#"+split_update[13];
                break;
            case "family":
                SharedPreferences.Editor edit_fam = Objects.requireNonNull(getActivity()).getSharedPreferences(FAMILY, MODE_PRIVATE).edit();
                edit_fam.putString("family", new_value).apply();
                update_conversion = split_update[0] + "#" + new_value + "#" + split_update[2] + "#" + split_update[3] + "#"
                        + split_update[4] + "#" + split_update[5] + "#" + split_update[6] + "#" + split_update[7] + "#" + split_update[8] + "#" + split_update[9]
                        +"#"+split_update[10]+"#"+split_update[11]+"#"+split_update[12]+"#"+split_update[13];
                break;
            case "age":
                SharedPreferences.Editor edit_age = Objects.requireNonNull(getActivity()).getSharedPreferences(AGE, MODE_PRIVATE).edit();
                edit_age.putString("age", new_value).apply();
                update_conversion = split_update[0] + "#" + split_update[1] + "#" + new_value + "#" + split_update[3] + "#"
                        + split_update[4] + "#" + split_update[5] + "#" + split_update[6] + "#" + split_update[7] + "#" + split_update[8] + "#" + split_update[9]
                        +"#"+split_update[10]+"#"+split_update[11]+"#"+split_update[12]+"#"+split_update[13];
                break;
            case "bio":
                update_conversion = split_update[0] + "#" + split_update[1] + "#" + split_update[2] + "#" + split_update[3] + "#"
                        + split_update[4] + "#" + split_update[5] + "#" + split_update[6] + "#" + split_update[7] + "#" + split_update[8] + "#" + new_value
                        +"#"+split_update[10]+"#"+split_update[11]+"#"+split_update[12]+"#"+split_update[13];
                break;
        }
       //   0       1     2       3         4          5       6      7       8            9         10     11        12       13
       // name # Balera # 23 # female # link_one # link_two # 5¼ ft # BE # Private # bio is here # father #mother # living # job_title
       // "megha#Ponnira#25#link_one#link_two#null#null#null#Working at junior collegeas leacturer after having completed my Mcom degree.I'm currently living in bittangala.#kaverappa#susheelabittangala#leacturer"
        Log.d("datas", "update_conversion :" + update_conversion);

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child(user_gender).child(user_key).child("name");
        reference.setValue(update_conversion).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                count = 0;
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

    public void check_network() {
        try {
            handler.postDelayed(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    ConnectivityManager connectivityManager = (ConnectivityManager)
                            Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo wifi_conn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    NetworkInfo data_conn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                    if ((wifi_conn != null && wifi_conn.isConnected()) || (data_conn != null && data_conn.isConnected())) {
                        is_connected = true;
                    } else {
                        is_connected = false;
                        check_network();
                    }
                }
            }, 3000);

        } catch (Exception e) {
            Log.d("error ", "catch in check_network :" + e.getMessage());
        }
    }
}