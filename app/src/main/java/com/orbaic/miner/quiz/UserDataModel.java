package com.orbaic.miner.quiz;

import com.google.gson.annotations.SerializedName;

public class UserDataModel {
    @SerializedName("extra1")
    private String extra1;
    @SerializedName("miningStartTime")
    private String miningTime;

    public String getExtra1() {
        return extra1;
    }

    public long getMiningTime() {
        long mTime = Long.parseLong(miningTime);
        return mTime;
    }

}
