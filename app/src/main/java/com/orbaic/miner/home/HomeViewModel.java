package com.orbaic.miner.home;

import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private int miningHoursCount = 0;

    public int getMiningHoursCount() {
        return miningHoursCount;
    }

    public void setMiningHoursCount(int miningHoursCount) {
        this.miningHoursCount = miningHoursCount;
    }

}
