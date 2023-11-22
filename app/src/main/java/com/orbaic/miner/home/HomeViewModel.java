package com.orbaic.miner.home;

import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private int miningHoursCount = 0;
    private int quizCount = 0;

    public int getMiningHoursCount() {
        return miningHoursCount;
    }

    public void setMiningHoursCount(int miningHoursCount) {
        this.miningHoursCount = miningHoursCount;
    }

    public int getQuizCount() {
        return quizCount;
    }

    public void setQuizCount(int quizCount) {
        this.quizCount = quizCount;
    }
}
