package com.orbaic.miner.quiz;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LearnEarnViewModel extends ViewModel {
    private double userPoints;
    private int qzCount;

    public double getUserPoints() {
        return userPoints;
    }

    public void updateUserPoints(double newPoints) {
        userPoints = newPoints;
    }

    public int getQzCount() {
        return qzCount;
    }

    public void updateQzCount(int quizCount) {
        qzCount = quizCount;
    }
}
