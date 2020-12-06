package com.ka12.ayirimatrimony;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.Random;

public class user_data extends AppCompatActivity {
    TextInputEditText name, family, age;
    Button submit, male, female, upload;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    String gender, img_link;
    Boolean is_gender_clicked = false;
    ProgressBar progress;
    public static final String D_LINK = "com.ka12.ayiri_matrimony.this_is_where_download_link_is_saved";
    public static final String P_LINK = "com.ka12.ayiri_matrimony.this_is_where_local_link_is_saved";
    public static final String LOGIN = "com.ka12.ayiri_matrimony_login_details";
    public static final String KEY = "com.ka12.ayiri_matrimony_this_is_where_key_is_stored";
    public static final String GENDER = "com.ka12.ayiri_matrimony_this_is_where_gender_is_stored";
    //the following are for uploading the image
    Uri image_url;
    String download_link;
    public StorageReference storageReference;
    public static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        //initialization
        name = findViewById(R.id.name);
        family = findViewById(R.id.family);
        age = findViewById(R.id.age);
        submit = findViewById(R.id.submit);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        upload = findViewById(R.id.upload);
        progress = findViewById(R.id.progress);
        //fetching the image link from firebase
        SharedPreferences edit = getSharedPreferences(D_LINK, MODE_PRIVATE);
        img_link = edit.getString("link", "something_went_wrong");
        Log.d("download ", "received " + img_link);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.requireNonNull(name.getText()).toString().equals("") || Objects.requireNonNull(age.getText()).toString().equals("")
                        || Objects.requireNonNull(family.getText()).toString().equals("")) {
                    Toast.makeText(user_data.this, "Please enter all the values!!", Toast.LENGTH_SHORT).show();
                } else {
                    push_into_database();
                }
            }
        });
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SharedPreferences.Editor edit=getSharedPreferences(GENDER,MODE_PRIVATE).edit();
                edit.putString("gender","male").apply();
                gender = "male";
                is_gender_clicked = true;
                male.setBackgroundColor(Color.BLACK);
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SharedPreferences.Editor edit=getSharedPreferences(GENDER,MODE_PRIVATE).edit();
                edit.putString("gender","female").apply();
                gender = "female";
                is_gender_clicked = true;
                male.setBackgroundColor(Color.BLACK);
            }
        });
        submit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    Log.d("key ", "inside try");
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    reference = firebaseDatabase.getReference().child("male").child("-MNYy-7-otGTVkRMwfcp").child("family");
                    reference.setValue("demo");
                    Toast.makeText(user_data.this, "trying to dynamically change the value!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.d("key ", "error :" + e.getMessage());
                }
                return false;
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageReference = FirebaseStorage.getInstance().getReference("profile_pictures");
                open_file_chooser();
            }
        });
        //delete the following code
        female.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent in = new Intent(user_data.this, com.ka12.ayirimatrimony.MainActivity.class);
                startActivity(in);
                finish();
                return false;
            }
        });
        //hiding submit button
        // submit.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
    }

    private void upload_image() {
        if (image_url != null) {
            Toast.makeText(this, "uploading image", Toast.LENGTH_LONG).show();
            String name_file = System.currentTimeMillis() + "." + get_file_extension(image_url);
            StorageReference filereference = storageReference.child(name_file);
            upload.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            filereference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            download_link = uri.toString();
                            Log.d("download ", "sending " + download_link);
                            SharedPreferences.Editor edit = getSharedPreferences(D_LINK, MODE_PRIVATE).edit();
                            edit.putString("link", download_link).apply();
                            Log.d("download ", "get download :" + download_link);
                        }
                    });
                    progress.setVisibility(View.GONE);
                    submit.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    //formula to get the current upload status
                    // double prog = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    //  progress.setProgress((int) prog);
                }
            });
        } else {
            Toast.makeText(this, "Please select a image", Toast.LENGTH_SHORT).show();
        }
    }

    //the following method is to get the file extension of the image file
    private String get_file_extension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void open_file_chooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            image_url = data.getData();
            SharedPreferences.Editor edit = getSharedPreferences(P_LINK, MODE_PRIVATE).edit();
            edit.putString("plink", image_url.toString()).apply();
            upload_image();
            //  Picasso.get().load(image_url).into(profile);
        }
    }

    private void push_into_database() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (gender.equals("male")) {
            reference = firebaseDatabase.getReference().child("male");
        } else {
            reference = firebaseDatabase.getReference().child("female");
        }
        //retriving the values
        String uname = Objects.requireNonNull(name.getText()).toString().trim();
        String ufamily = Objects.requireNonNull(family.getText()).toString().trim();
        String uage = Objects.requireNonNull(age.getText()).toString().trim();
        String ugender = gender.trim();
        //helperclass //TODO take the phone number too
        heplerclass help = new heplerclass();
        help.setName(uname);
        help.setFamily(ufamily);
        help.setAge(uage);
        help.setGender(ugender);
        help.setReceived("received");
        help.setSent("sent");
        help.setPhone("9980464155");
        //do not forget to uncomment the following code
        //help.setLink(download_link);
        help.setLink("this.is.a.dummy.link");
        String ran = get_random_name();
        Log.d("key ", "from ran " + ran);
        //saving the key in shared preferences
        SharedPreferences.Editor ediss = getSharedPreferences(KEY, MODE_PRIVATE).edit();
        ediss.putInt("key", Integer.parseInt(String.valueOf(ran))).apply();
        reference.child(ran).setValue(help).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(user_data.this, "success", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.VISIBLE);

                //testing change to true
                SharedPreferences.Editor edist = getSharedPreferences(LOGIN, MODE_PRIVATE).edit();
                edist.putBoolean("login", false).apply();
                Intent in = new Intent(user_data.this, com.ka12.ayirimatrimony.MainActivity.class);
                startActivity(in);
                finish();
                Log.d("push ", "from inside " + reference.getKey());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(user_data.this, "error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String get_random_name() {
        String num = "";
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            num = num + (random.nextInt(9));
        }
        Log.d("num ", num);
        return num;
    }

    @Keep
    static class heplerclass {
        public String name;
        public String family;
        public String age;
        public String gender;
        public String received;
        public String sent;
        public String link;
        public String phone;

        public heplerclass() {

        }

        public heplerclass(String name, String family, String gender,String link,String phone,
                           String age, String sent, String received) {
            this.name = name;
            this.family = family;
            this.gender = gender;
            this.link=link;
            this.age = age;
            this.sent = sent;
            this.received = received;
            this.phone=phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setReceived(String received) {
            this.received = received;
        }

        public void setSent(String sent) {
            this.sent = sent;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setFamily(String family) {
            this.family = family;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public void setLink(String link) {
            this.link = link;
        }

    }
}