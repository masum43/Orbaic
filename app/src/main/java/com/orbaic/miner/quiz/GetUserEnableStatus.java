package com.orbaic.miner.quiz;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GetUserEnableStatus extends ViewModel {
    MutableLiveData <List<UserDataModel>> userEnableStatus = new MutableLiveData<>();

    public MutableLiveData<List<UserDataModel>> getUserEnableStatus() {
        return userEnableStatus;
    }

    public void loadData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //userEnableStatus.setValue(snapshot.getValue(<List<UserDataModel.class>>));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.getDetails());
            }
        });
    }
}
