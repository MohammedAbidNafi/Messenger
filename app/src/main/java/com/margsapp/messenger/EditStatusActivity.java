package com.margsapp.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messenger.Model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class EditStatusActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    EditText statusEditText;

    AppCompatButton saveButton;

    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_status);

        statusEditText = findViewById(R.id.editText);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> startActivity(new Intent(EditStatusActivity.this, edit_profile.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));




        saveButton = findViewById(R.id.savebutton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_status = statusEditText.getText().toString();


                if(txt_status.equals("")){
                    save("Hey there am using Messenger");
                }else
                {
                    save(txt_status);
                }

                Intent intent = new Intent(EditStatusActivity.this, edit_profile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });


    }


    private void save(String txt_status) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        assert firebaseUser != null;
        String userid = firebaseUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {




                HashMap<String, Object> hash = new HashMap<>();
                hash.put("DT", txt_status);
                reference.updateChildren(hash);



            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    public void onBackPressed(){
        Intent intent = new Intent(EditStatusActivity.this, edit_profile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

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