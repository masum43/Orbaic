package com.orbaic.miner;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class SendDataFirebaseDatabase {

    public void sendUserData(String uid, Map<String, String> fieldName){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usersToken");

        reference.child(uid).setValue(fieldName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        System.out.println(unused);
                    }
                });

    }
}
