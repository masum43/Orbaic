package com.orbaic.miner.myTeam;

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

public class TeamViewModel extends ViewModel {

    private double point = 0.0;
    private int quizCount = 0;
    private int miningHoursCount = 0;
    private boolean isPointAdded = false;

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }

    public int getQuizCount() {
        return quizCount;
    }

    public void setQuizCount(int quizCount) {
        this.quizCount = quizCount;
    }

    public int getMiningHoursCount() {
        return miningHoursCount;
    }

    public void setMiningHoursCount(int miningHoursCount) {
        this.miningHoursCount = miningHoursCount;
    }

    public boolean isPointAdded() {
        return isPointAdded;
    }

    public void setPointAdded(boolean pointAdded) {
        isPointAdded = pointAdded;
    }
}
