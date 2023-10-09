package com.orbaic.miner.myTeam;

public class Team {
    String id;
    String userName;
    String userEmail;
    String imageUrl;
    String miningStartTime;
    String miningStatus;

    public Team(String id, String userName, String userEmail, String imageUrl, String miningStartTime, String miningStatus) {
        this.id = id;
        this.userName = userName;
        this.userEmail = userEmail;
        this.imageUrl = imageUrl;
        this.miningStartTime = miningStartTime;
        this.miningStatus = miningStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getMiningStartTime() {
        return miningStartTime;
    }

    public void setMiningStartTime(String miningStartTime) {
        this.miningStartTime = miningStartTime;
    }

    public String getMiningStatus() {
        return miningStatus;
    }

    public void setMiningStatus(String miningStatus) {
        this.miningStatus = miningStatus;
    }
}
