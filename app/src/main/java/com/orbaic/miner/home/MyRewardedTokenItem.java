package com.orbaic.miner.home;

public class MyRewardedTokenItem {
    Long id;
    String name;
    String code;
    String balance;

    public MyRewardedTokenItem() {
    }

    public MyRewardedTokenItem(Long id, String name, String code, String balance) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.balance = balance;
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
}
