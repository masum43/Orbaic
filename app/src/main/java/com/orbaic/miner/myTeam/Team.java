package com.orbaic.miner.myTeam;

public class Team {
    String id;
    String userName;
    String userEmail;
    String imageUrl;

    public Team(String id, String userName, String userEmail, String imageUrl) {
        this.id = id;
        this.userName = userName;
        this.userEmail = userEmail;
        this.imageUrl = imageUrl;
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
}
