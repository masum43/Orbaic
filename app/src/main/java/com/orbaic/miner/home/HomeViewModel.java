package com.orbaic.miner.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class HomeViewModel extends ViewModel {
    private double miningHours = 0.0;

    public double getMiningHours() {
        return miningHours;
    }

    public void setMiningHours(double miningHours) {
        this.miningHours = miningHours;
    }

}
