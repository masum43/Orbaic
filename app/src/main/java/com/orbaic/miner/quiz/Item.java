package com.orbaic.miner.quiz;

public class Item {
    private String title;
    private String description;
    private boolean isExpanded;

    public Item(String title, String description) {
        this.title = title;
        this.description = description;
        this.isExpanded = false;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
