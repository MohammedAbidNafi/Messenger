package com.margsapp.messenger;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.margsapp.messenger.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class edit_profile extends AppCompatActivity {


    private static final String TAG = "edit_profile";
    CircleImageView profile_image;

    TextView username, status, joined_on;

    FirebaseUser firebaseUser;


    CardView Status_card, Account_card, Customize_card, About_card;



    DatabaseReference reference;

    StorageReference storageReference;
    private static int GALLERY_PICK = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    private ProgressDialog loadingBar;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-5615682506938042/5865260490", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });

        mAdView = findViewById(R.id.adView);

        mAdView.loadAd(adRequest);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(edit_profile.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(edit_profile.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        });




        status = findViewById(R.id.status);
        username = findViewById(R.id.username);
        joined_on = findViewById(R.id.joined_on);
        profile_image = findViewById(R.id.profile_image);
        Status_card = findViewById(R.id.status_card);
        Account_card = findViewById(R.id.fragment_chat_settings);
        Customize_card = findViewById(R.id.Customize_card);
        About_card = findViewById(R.id.About_card);


        Status_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(edit_profile.this, EditStatusActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(edit_profile.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
                finish();
            }
        });

        Account_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(edit_profile.this, Chat_settings.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(edit_profile.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
                finish();

            }
        });

        Customize_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(edit_profile.this, CustomiseActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(edit_profile.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
                finish();
            }
        });

        About_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(edit_profile.this, AboutActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(edit_profile.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
                finish();
            }
        });


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("ProfileImages/"+firebaseUser.getUid());


        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                User user = dataSnapshot.getValue(User.class);

                assert user != null;
                username.setText(user.getUsername());
                joined_on.setText(user.getJoined_on());



                status.setText(user.getDt());


                if (user.getImageUrl().equals("default")) {
                    profile_image.setImageResource(R.drawable.user);
                } else {
                    Glide.with((getApplicationContext())).load(user.getImageUrl()).into(profile_image);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        loadingBar = new ProgressDialog(this);

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });








    }

    public void onBackPressed(){
        Intent intent = new Intent(edit_profile.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        if (mInterstitialAd != null) {
            mInterstitialAd.show(edit_profile.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }

        finish();

    }



    private void openImage() {

        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, GALLERY_PICK);

    }
    /*



    private void uploadImage(){

        final ProgressDialog pd = new ProgressDialog(edit_profile.this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        assert downloadUri != null;
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    }
                    else {
                        Toast.makeText(edit_profile.this, "Failed! Error code 0x08050101", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(edit_profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            Toast.makeText(edit_profile.this,"No Image Error code 0x08050102",Toast.LENGTH_SHORT).show();
        }


    }

     */



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we update your profile picture...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(false);
                assert result != null;
                Uri resultUri = result.getUri();

                StorageReference filepath = storageReference.child(System.currentTimeMillis()
                        + "." + getFileExtension(resultUri));


                uploadTask = filepath.putFile(resultUri);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(edit_profile.this, "Image has been stored in our servers", Toast.LENGTH_SHORT).show();

                            Uri downloadUri = task.getResult();
                            assert downloadUri != null;
                            String mUri = downloadUri.toString();


                            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("imageURL", mUri);
                            reference.updateChildren(map);

                            loadingBar.dismiss();

                        }
                    }
                });
            } else {
                Toast.makeText(this, "Profile Picture updation is cancelled", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = edit_profile.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }






    private void status(String status){
        FirebaseUser firebaseUserStatus = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference statusdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUserStatus.getUid());

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yy hh:mm aa");
        String timestamp = simpleDateFormat.format(calendar.getTime());

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("status", status);
        hashMap.put("lastseen", timestamp);

        statusdatabaseReference.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    protected void onDestroy() {
        super.onDestroy();
        status("offline");
    }


}
