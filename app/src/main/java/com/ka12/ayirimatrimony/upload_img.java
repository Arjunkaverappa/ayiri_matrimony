package com.ka12.ayirimatrimony;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class upload_img extends AppCompatActivity {
    ProgressBar progress;
    Button choose, upload;
    CircleImageView profile;
    //the request code
    public static final int PICK_IMAGE_REQUEST = 1;
    Uri image_url;
    String download_link;
    //database references
    public StorageReference storageReference;
    public static final String D_LINK = "com.ka12.ayiri_matrimony.this_is_where_download_link_is_saved";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_img);
        choose = findViewById(R.id.choose);
        upload = findViewById(R.id.upload);
        progress = findViewById(R.id.progress);
        profile = findViewById(R.id.profile);
        //setting up database instances
        storageReference = FirebaseStorage.getInstance().getReference("profile_pictures");

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_file_chooser();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_image();
            }
        });
    }

    private void upload_image() {
        /*
           storage reference points to uploads folder in firebase
         */
        if (image_url != null) {
            Toast.makeText(this, "uploading image", Toast.LENGTH_LONG).show();
            String name_file = System.currentTimeMillis() + "." + get_file_extension(image_url);
            StorageReference filereference = storageReference.child(name_file);

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
                    Toast.makeText(upload_img.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progress.setProgress(0);
                        }
                    }, 4000);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(upload_img.this, "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    //formula to get the current upload status
                    double prog = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progress.setProgress((int) prog);
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
            Picasso.get().load(image_url).into(profile);
        }
    }
}