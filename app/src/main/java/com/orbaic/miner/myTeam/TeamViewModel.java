package com.orbaic.miner.myTeam;

import androidx.lifecycle.ViewModel;

public class TeamViewModel extends ViewModel {

    private double point;
    private int quizCount = 0;
    private int miningHoursCount = 0;
    private boolean isMyDataRead = false;
    private String miningStartTime;

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }

    public String getMiningStartTime() {
        return miningStartTime;
    }

    public void setMiningStartTime(String miningStartTime) {
        this.miningStartTime = miningStartTime;
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

    public boolean isMyDataRead() {
        return isMyDataRead;
    }

    public void setMyDataRead(boolean myDataRead) {
        isMyDataRead = myDataRead;
    }
}
