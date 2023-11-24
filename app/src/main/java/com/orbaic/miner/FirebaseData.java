package com.orbaic.miner;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FirebaseData {
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    ArrayList<FirebaseUserData> list = new ArrayList<FirebaseUserData>();

    public void addMiningPoints(String mPoint){

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        myRef.child("point").setValue(mPoint);
    }
    public void addQuizRewardPoints(String mPoint){

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("point", mPoint);
        hashMap.put("qz_count", "0");
        myRef.updateChildren(hashMap);
    }

    public void addMiningRewardPoints(String mPoint){

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("point", mPoint);
        hashMap.put("mining_count", "0");
        myRef.updateChildren(hashMap);
    }

    public void addMiningCount(String count){

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        myRef.child("mining_count").setValue(count);
    }

//    public void addQuizCount(String count){
//
//        mAuth = FirebaseAuth.getInstance();
//        firebaseDatabase = FirebaseDatabase.getInstance();
//
//        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
//        myRef.child("qz_count").setValue(count);
//    }

    public void addQuizPoints(String mPoint){
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("point", mPoint);
        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        myRef.updateChildren(hashMap);
    }

    public void addQuizCount(String qzCountStr){
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("qz_count", qzCountStr);
        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        myRef.updateChildren(hashMap);
    }


    public void anyPath(String value, String pathName){

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        myRef.child(pathName).setValue(value);


    }

    public void readData(){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //String name = snapshot.child("name").getValue().toString();
                //System.out.println(name);
                //String point = snapshot.child("point").getValue().toString();
                /*for (DataSnapshot data: snapshot.getChildren()) {

                    FirebaseUserData addData = data.getValue(FirebaseUserData.class);
                    list.add(addData);

                }*/



                    //Toast.makeText(CashOutActivity.this, ""+dataSnapshot.getValue(),Toast.LENGTH_LONG).show();


                //Toast.makeText(CashOutActivity.this, ""+dataSnapshot.getValue(),Toast.LENGTH_LONG).show();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
