package com.orbaic.miner.home;

import androidx.lifecycle.ViewModel;

import java.math.BigDecimal;

public class HomeViewModel extends ViewModel {
    private int miningHoursCount = 0;
    private int quizCount = 0;
    private double point;

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

    public double getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = Double.parseDouble(point);
    }

    public void setPoint(double point) {
        this.point = point;
    }
}
