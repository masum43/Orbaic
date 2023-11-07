package com.orbaic.miner.wallet;

public class RewardTokenItem {
    public int tokenId;
    public String name;
    public String coin;
    public String icon;

    public RewardTokenItem() {
    }

    public RewardTokenItem(int tokenId, String name, String coin, String icon) {
        this.tokenId = tokenId;
        this.name = name;
        this.coin = coin;
        this.icon = icon;
    }

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
