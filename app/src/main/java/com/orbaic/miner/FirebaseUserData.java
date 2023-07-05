package com.orbaic.miner;

public class FirebaseUserData {
    String point, name, phone,click,country, birthdate, referral, type;

    public FirebaseUserData(String point, String name, String phone, String click, String country, String birthdate, String referral, String type) {
        this.point = point;
        this.name = name;
        this.phone = phone;
        this.click = click;
        this.country = country;
        this.birthdate = birthdate;
        this.referral = referral;
        this.type = type;
    }

    public FirebaseUserData() {

    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getReferral() {
        return referral;
    }

    public void setReferral(String referral) {
        this.referral = referral;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
