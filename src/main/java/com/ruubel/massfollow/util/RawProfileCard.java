package com.ruubel.massfollow.util;

public class RawProfileCard {

    private String name;
    private String userId;
    private boolean following;

    public RawProfileCard(String name, String userId, boolean following) {
        this.name = name;
        this.userId = userId;
        this.following = following;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isFollowing() {
        return following;
    }

    @Override
    public String toString() {
        return "RawProfileCard{" +
                "name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", following=" + following +
                '}';
    }
}
