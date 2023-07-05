package com.orbaic.miner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ReferralData extends AppCompatActivity {

    private RecyclerView recyclerView;
    ImageView img,refresh;
    Timer time = new Timer();
    TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_data);

        recyclerView = findViewById(R.id.ref_rec);

        img = findViewById(R.id.backButtonRef);
        refresh = findViewById(R.id.refreshButton);
        refresh.setOnClickListener(v->{
            finish();
            startActivity(getIntent());
        });
        img.setOnClickListener(v->{
            onBackPressed();
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //RecycleView Data input
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        ArrayList list = new ArrayList<>();
        ReferralAdapter customAdapter = new ReferralAdapter(this, list);
        recyclerView.setAdapter(customAdapter);
        //end RecycleView Data*/

        DatabaseReference ref = database.getReference("referralUser").child(mAuth.getUid());
        ref.addValueEventListener(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    ReferralDataRecive data = dataSnapshot.getValue(ReferralDataRecive.class);
                    //Toast.makeText(CashOutActivity.this, ""+dataSnapshot.getValue(),Toast.LENGTH_LONG).show();
                    list.add(data);

                    customAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(""+error);
            }
        });

    }
}