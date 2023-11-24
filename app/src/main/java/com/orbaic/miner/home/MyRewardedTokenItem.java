package com.orbaic.miner.home;

public class MyRewardedTokenItem {
    Long id;
    String name;
    String code;
    String balance;
    String icon;

    public MyRewardedTokenItem() {
    }

    public MyRewardedTokenItem(Long id, String name, String code, String balance, String icon) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.balance = balance;
        this.icon = icon;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
