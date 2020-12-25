package com.ka12.ayirimatrimony;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;

import java.io.File;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class user_data extends AppCompatActivity {
    //permission management
    public static final int uni_code = 1234;
    TextInputEditText name, age,place;
    AutoCompleteTextView family;
    public static final String PROOF_DOWNLOAD = "com.ka12.ayiri_matrimony_proof_download_link_is_saved_here";
    Button submit, male, female, upload, conti, sub_proof;
    LottieAnimationView up, loading, p_loading;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    CardView card_one, card_two, card_three, card_four;
    String gender = "", img_link;
    Boolean is_gender_clicked = false;
    CircleImageView up_img;
    // ProgressBar progress;
    public static final String D_LINK = "com.ka12.ayiri_matrimony.this_is_where_download_link_is_saved";
    public static final String P_LINK = "com.ka12.ayiri_matrimony.this_is_where_local_link_is_saved";
    public static final String LOGIN = "com.ka12.ayiri_matrimony_login_details";
    public static final String KEY = "com.ka12.ayiri_matrimony_this_is_where_key_is_stored";
    public static final String GENDER = "com.ka12.ayiri_matrimony_this_is_where_gender_is_stored";
    public static final String NAME = "com.ka12.ayiri_matrimony_this_is_where_name_is_stored";
    public static final String FAMILY = "com.ka12.ayiri_matrimony_this_is_where_family_is_stored";
    public static final String IS_OLD = "com.ka12.ayiri_matrimony_checking_for_previous_entries";
    final String TAG = "LOG ";
    //database needs
    public static final String PHONE = "com.ka12.ayiri_matrimony_phone_number_is_saved_here";
    String user_num,u_height,u_qua,u_work,u_place;
    //the following are for uploading the image
    Uri image_url;
    public static final String ONESIGNAL_APP_ID = "4359ad23-f128-46aa-aba3-caebf6058549";
    public StorageReference storageReference;
    public static final int PICK_IMAGE_REQUEST = 1;
    Boolean is_connected;
    TextView welcome, long_text;
    Boolean is_uploaded_proof = false;
    Spinner work1, height, edu;
    String download_link, download_link_proof,player_id;

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
        card_one = findViewById(R.id.card_one);
        card_two = findViewById(R.id.card_two);
        card_three = findViewById(R.id.card_three);
        up = findViewById(R.id.up);
        sub_proof = findViewById(R.id.sub_proof);
        loading = findViewById(R.id.loading);
        welcome = findViewById(R.id.welcome);
        conti = findViewById(R.id.conti);
        up_img = findViewById(R.id.up_img);
        card_four = findViewById(R.id.card_four);
        long_text = findViewById(R.id.long_text);
        p_loading = findViewById(R.id.p_loading);
        work1 = findViewById(R.id.work1);
        edu = findViewById(R.id.edu);
        height = findViewById(R.id.height);
        place=findViewById(R.id.place);
        //background task
        new do_in_background().execute();

        final String[] get_fam = {"Airira", "Balyakanda", "Mukairira", "Appachanda","Balera","dugganda","Kuttairira","Cheyanerira",
                "Bolairira", "Kannikanda", "Angira", "Ayyanira","Akkachira","Mullerira","Aimaniyanda","Malera","Aimanda","Mudiyanda",
                "Ammananda", "Ponnira", "Aleyanda","Anjapanda","Kundairira","Thattanda","Bollanamanda","Ikolanda","Minnimada",
                "Boppandira", "Athiyanda", "Aginira", "Alathanda","Inamanda","Kameyanda","Pattacharira","Manjanerira","Babbira",
                "Annalapanda","Melathanda","Aiyanira","Aiparavanda"};

        String[] get_edu = {"select", "Aeronautical engineering", "B.Arch", "Bca", "BE", "B.plan", "Bsc", "B.Tech", "Bs", "Masters in Engineering",
                "M.arch", "Mca", "ME", "Msc", "Ms", "M.Tech", "PGDCA", "Aviation", "Ba", "Bcom", "BEd",
                "BFA", "BFD", "BMM", "B.SW", "B.phil", "MA", "Mcom", "M.ed", "BFA", "MLIS", "Msc", "MSW", "M.phil",
                "BBA", "BFM", "BHM", "BHA", "MBA", "MFM", "MHRM", "PGDM", "MHA", "B.A.M.S", "BDS", "BHMS", "BSMS",
                "B.pharm", "BPT", "BUMS", "MBBS", "Bsc nursing", "MDs", "MD/MS", "M.pharm", "MPT", "MVSc", "BGL", "BL", "LLB",
                "LLM", "ML", "CA", "CFA", "ICWA", "IAS", "IES", "IFS", "IRS", "IPS", "Phd", "Diploma", "Polytechnic",};
        String[] getwork = {"select", "Government", "Private", "Defence", "Business", "Self employed", "Not working","PUC","10th"};
        String[] geth ={"select","4¼ ft","4½ ft","4¾ ft","5 ft","5¼ ft","5½ ft","5¾ ft","6 ft","6¼ ft","6½ ft","6¾ ft","7 ft"};


        ArrayAdapter<String> adapterr = new ArrayAdapter<>(user_data.this, R.layout.support_simple_spinner_dropdown_item, getwork);
        ArrayAdapter<String> adapterh = new ArrayAdapter<>(user_data.this, R.layout.support_simple_spinner_dropdown_item, geth);
        ArrayAdapter<String> adapterq = new ArrayAdapter<>(user_data.this, R.layout.support_simple_spinner_dropdown_item, get_edu);
        ArrayAdapter<String> fadapter = new ArrayAdapter<>(user_data.this, R.layout.autocomplete, get_fam);
        family.setThreshold(1);
        family.setAdapter(fadapter);
        work1.setAdapter(adapterr);
        height.setAdapter(adapterh);
        edu.setAdapter(adapterq);

        height.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                u_height=geth[i];
                Log.d("getting",geth[i]+" as height");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            u_height=geth[0];
                Log.d("getting",geth[0]+" as height");
            }
        });
        work1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                u_work=getwork[i];
                Log.d("getting",getwork[i]+" as work");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                u_work=getwork[0];
                Log.d("getting",getwork[0]+" as height");
            }
        });
        edu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                u_qua=get_edu[i];
                Log.d("getting",get_edu[i]+" as height");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                u_qua=get_edu[0];
                Log.d("getting",get_edu[0]+" as height");
            }
        });

        //TODO:safely remove this section
        //fetching the image link from firebase
        SharedPreferences edit = getSharedPreferences(D_LINK, MODE_PRIVATE);
        img_link = edit.getString("link", "something_went_wrong");
        Log.d("download ", "received " + img_link);

        //checking if old user
        SharedPreferences getold = getSharedPreferences(IS_OLD, MODE_PRIVATE);
        boolean is_old = getold.getBoolean("isold", false);

        Log.d("old", "old user " + is_old);

        if (is_old)
        {
            Log.d("old", "initiated old user protocol");
            initiate_old_user_protocol();
        }

        //fetching the user's phone number to be used as key in firebase
        SharedPreferences get_number = getSharedPreferences(PHONE, MODE_PRIVATE);
        user_num = get_number.getString("key", "9999999999");
        //step 1
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!is_connected)
                {
                    Toast.makeText(user_data.this, "Please connect to internet", Toast.LENGTH_SHORT).show();
                } else if
                (
                           Objects.requireNonNull(name.getText()).toString().equals("")
                        || Objects.requireNonNull(age.getText()).toString().equals("")
                        || Objects.requireNonNull(family.getText()).toString().equals("")
                        || Objects.requireNonNull(place.getText()).toString().equals("")
                        || u_work.equals("select")
                        || u_height.equals("select")
                        || u_qua.equals("select")
                        || gender.equals("")  )
                {
                    Toast.makeText(user_data.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor edit = getSharedPreferences(GENDER, MODE_PRIVATE).edit();
                    edit.putString("gender", gender).apply();

                    card_one.setVisibility(View.GONE);
                    card_two.setVisibility(View.VISIBLE);
                    up_img.setVisibility(View.GONE);
                    //hiding the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    //saving all details in shared preferences
                }
            }
        });
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Tada).duration(1000).repeat(0).playOn(male);
              //  SharedPreferences.Editor edit = getSharedPreferences(GENDER, MODE_PRIVATE).edit();
              //  edit.putString("gender", "male").apply();
                gender = "male";
                is_gender_clicked = true;
                male.setBackgroundColor(Color.parseColor("#ED8A6B"));
                female.setBackgroundColor(Color.parseColor("#0277BD"));
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Tada).duration(1000).repeat(0).playOn(female);
                gender = "female";
                is_gender_clicked = true;
                female.setBackgroundColor(Color.parseColor("#ED8A6B"));
                male.setBackgroundColor(Color.parseColor("#0277BD"));
            }
        });
        //step 2
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storageReference = FirebaseStorage.getInstance().getReference("profile_pictures");
                open_file_chooser();
            }
        });
        //step 3
        sub_proof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_uploaded_proof = true;
                open_file_chooser();
            }
        });

        //TODO:delete the following code
        female.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent in = new Intent(user_data.this, com.ka12.ayirimatrimony.MainActivity.class);
                startActivity(in);
                finish();
                return false;
            }
        });

        //initialising one signal for notifications
        Log.d("onesignal", "initialising one signal with context");
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        OSDeviceState device = OneSignal.getDeviceState();
        if (device != null) {
            player_id = device.getUserId();
        }
        check_network();
    }

    private void upload_image(Uri image_url) {
        if (image_url != null) {
            Toast.makeText(this, "Uploading image", Toast.LENGTH_LONG).show();
            String name_file = System.currentTimeMillis() + "." + get_file_extension(image_url);
            StorageReference filereference = storageReference.child(name_file);
            //compressing the image
            String filePath = SiliCompressor.with(user_data.this).compress(image_url.toString(), new File(Environment.DIRECTORY_PICTURES));
            Log.d(TAG, "upload_image: filepath  :" + filePath);
            Log.d(TAG, "upload_image: image_url :" + image_url);
            filereference.putFile(Uri.parse(filePath)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (is_uploaded_proof) {
                                download_link_proof = uri.toString();
                                Log.d(TAG, "download link of proof :" + download_link_proof);
                                SharedPreferences.Editor edit = getSharedPreferences(PROOF_DOWNLOAD, MODE_PRIVATE).edit();
                                edit.putString("proof", download_link_proof).apply();

                                //save everything in db
                                push_into_database_final(uri.toString());

                            } else {
                                //getting the download link
                                download_link = uri.toString();
                                Log.d("download ", "sending " + download_link);

                                //passing it to shared preferences
                                SharedPreferences.Editor edit = getSharedPreferences(D_LINK, MODE_PRIVATE).edit();
                                edit.putString("link", download_link).apply();
                                Log.d("downs ", "get download :" + download_link);

                                card_two.setVisibility(View.GONE);
                                card_four.setVisibility(View.VISIBLE);
                                p_loading.setVisibility(View.GONE);
                            }
                            //deleting the compressed file after compression and upload
                            File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures/SiliCompressor/");
                            if (dir.isDirectory()) {
                                String[] children = dir.list();
                                for (int i = 0; i < Objects.requireNonNull(children).length; i++) {
                                    new File(dir, children[i]).delete();
                                    Log.d("download", "deleted!!");
                                }
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Log.d("error :","Error while uploading the photo :"+e.getMessage());
                    AlertDialog.Builder alert=new AlertDialog.Builder(user_data.this,R.style.alert_custom);
                    alert.setTitle("Error");
                    alert.setMessage(e.getMessage());
                    alert.show();
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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            image_url = data.getData();
            SharedPreferences.Editor edit = getSharedPreferences(P_LINK, MODE_PRIVATE).edit();
            edit.putString("plink", image_url.toString()).apply();

            up.setVisibility(View.GONE);
            up_img.setVisibility(View.VISIBLE);
            up_img.setImageURI(image_url);
            loading.setVisibility(View.VISIBLE);
            if (is_uploaded_proof) {
                p_loading.setVisibility(View.VISIBLE);
                long_text.setVisibility(View.GONE);
                sub_proof.setText("Uploading...");
            }
            if (is_connected) {
                try {
                    upload_image(image_url);
                } catch (Exception e) {
                    Log.d("download", "Error in try :" + e.getMessage());
                }
                upload.setText("Uploading...");
            } else {
                Toast.makeText(this, "Please connect to internet", Toast.LENGTH_SHORT).show();
            }
        }
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

        public heplerclass()
        {

        }

        public heplerclass(String name, String family, String gender, String link, String phone,
                           String age, String sent, String received) {
            this.name = name;
            this.family = family;
            this.gender = gender;
            this.link = link;
            this.age = age;
            this.sent = sent;
            this.received = received;
            this.phone = phone;
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

    public void check_network() {
        new Handler().postDelayed(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifi_conn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo data_conn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                is_connected = (wifi_conn != null && wifi_conn.isConnected()) || (data_conn != null && data_conn.isConnected());
                check_network();
            }
        }, 2500);
    }

    @SuppressLint("SetTextI18n")
    public void initiate_old_user_protocol() {
        //retreiving the name
        SharedPreferences getname = getSharedPreferences(NAME, MODE_PRIVATE);
        String get_name = getname.getString("name", "null");
        //retreiving the family
        SharedPreferences getfamily = getSharedPreferences(FAMILY, MODE_PRIVATE);
        String fam = getfamily.getString("family", "null");

        card_one.setVisibility(View.GONE);
        card_two.setVisibility(View.GONE);
        card_four.setVisibility(View.GONE);
        card_three.setVisibility(View.VISIBLE);
        welcome.setText("Welcome back,\n" + fam + " " + get_name);

        conti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //saving the login details
                SharedPreferences.Editor edist = getSharedPreferences(LOGIN, MODE_PRIVATE).edit();
                edist.putBoolean("login", true).apply();

                Intent in = new Intent(user_data.this, MainActivity.class);
                startActivity(in);
                finish();
                Animatoo.animateZoom(user_data.this);
            }
        });
    }

    private void push_into_database_final(String image_dwonload_link) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (gender.equals("male"))
            reference = firebaseDatabase.getReference().child("male");
        else
            reference = firebaseDatabase.getReference().child("female");

        //getting download link
        SharedPreferences getlink = getSharedPreferences(D_LINK, MODE_PRIVATE);
        download_link = getlink.getString("link", "wrong link");

        //retriving the values
        String uname = Objects.requireNonNull(name.getText()).toString().trim();
        String ufamily = Objects.requireNonNull(family.getText()).toString().trim();
        String uage = Objects.requireNonNull(age.getText()).toString().trim();
        String ugender = gender.trim();
        save_in_shared_preferences(uname, ufamily);
        //TODO:do not forget to set the correct download link=done
        Log.d("downs", "proof    :" + image_dwonload_link);
        Log.d("downs", "download :" + download_link);

        //getting the decription
        description d=new description();
        String description=d.get_description(u_qua,u_work, Objects.requireNonNull(place.getText()).toString());

        //image_download_link is the id proof
        String final_data = uname + "#" + ufamily + "#" + uage + "#" + ugender + "#"
                + download_link + "#" + image_dwonload_link+"#"+u_height+"#"+u_qua+"#"+u_work+"#"+description;
        Log.d("downs", "final data :" + final_data);
        //helperclass
        heplerclass help = new heplerclass();
        help.setName(final_data);
        help.setReceived("received");
        help.setSent("seen:yes:noti_token:");

        SharedPreferences.Editor putkey = getSharedPreferences(KEY, MODE_PRIVATE).edit();
        putkey.putString("key", user_num).apply();

        reference.child(user_num).setValue(help).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(user_data.this, "success", Toast.LENGTH_SHORT).show();
                //navigate to the third card
                card_two.setVisibility(View.GONE);
                card_four.setVisibility(View.VISIBLE);
                p_loading.setVisibility(View.GONE);

                //TODO:change to true!!(done for now)
                SharedPreferences.Editor edist = getSharedPreferences(LOGIN, MODE_PRIVATE).edit();
                edist.putBoolean("login", true).apply();
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

    public void save_in_shared_preferences(String name, String family) {
        SharedPreferences.Editor getname = getSharedPreferences(NAME, MODE_PRIVATE).edit();
        getname.putString("name", name).apply();

        SharedPreferences.Editor getfamily = getSharedPreferences(FAMILY, MODE_PRIVATE).edit();
        getfamily.putString("family", family).apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == uni_code) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted successfully!", Toast.LENGTH_SHORT).show();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                AlertDialog.Builder alter = new AlertDialog.Builder(this);
                alter.setTitle("Disclaimer");
                alter.setMessage("This permission is important for the app to function normally!");
                alter.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(user_data.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, uni_code);
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.exit(0);
                    }
                }).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class do_in_background extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //hiding cards
            card_two.setVisibility(View.GONE);
            card_three.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
            card_four.setVisibility(View.GONE);
            //status bar colors
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ED8A6B"));
            //cheching for permissions
            if (ContextCompat.checkSelfPermission(user_data.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //this means the permission is granted
                Log.d(TAG, "onCreate: permission granted");
            } else {
                //asking for permission
                ActivityCompat.requestPermissions(user_data.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, uni_code);
            }
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            return null;
        }
    }
}