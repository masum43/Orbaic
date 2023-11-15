package com.orbaic.miner.wallet;

public class RewardModel {
    private String code;
    private String name;
    private String bonus;
    private String order;
    private String icon;

    public RewardModel() {
    }

    public RewardModel(String code, String name, String bonus, String order, String icon) {
        this.code = code;
        this.name = name;
        this.bonus = bonus;
        this.order = order;
        this.icon = icon;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
