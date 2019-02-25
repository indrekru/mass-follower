package com.ruubel.massfollow.config;

import java.util.Optional;

public class ConfigParams {

    private String homeAccount;
    private String authorizationToken;
    private String csrfToken;
    private String cookie;

    public ConfigParams() throws Exception {
        authorizationToken = Optional.ofNullable(System.getenv("TWITTER_BEARER_TOKEN")).orElseThrow(
                () -> new Exception("TWITTER_BEARER_TOKEN is not set in the environment"));
        csrfToken = Optional.ofNullable(System.getenv("TWITTER_CSRF_TOKEN")).orElseThrow(
                () -> new Exception("TWITTER_CSRF_TOKEN is not set in the environment"));
        cookie = Optional.ofNullable(System.getenv("TWITTER_COOKIE")).orElseThrow(
                () -> new Exception("TWITTER_COOKIE is not set in the environment"));
        homeAccount = Optional.ofNullable(System.getenv("TWITTER_HOME_ACCOUNT_NAME")).orElseThrow(
                () -> new Exception("TWITTER_HOME_ACCOUNT_NAME is not set in the environment"));
    }

    public String getHomeAccount() {
        return homeAccount;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    public String getCookie() {
        return cookie;
    }
}
