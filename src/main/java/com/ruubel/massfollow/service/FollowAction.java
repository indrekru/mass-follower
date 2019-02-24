package com.ruubel.massfollow.service;

public enum FollowAction {
    FOLLOW("https://api.twitter.com/1.1/friendships/create.json"),
    UNFOLLOW("https://api.twitter.com/1.1/friendships/destroy.json");

    private String actionUrl;

    FollowAction(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getActionUrl() {
        return actionUrl;
    }
}
