package com.ruubel.massfollow.service;

import com.ruubel.massfollow.config.ConfigParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class HeaderService {

    @Value("${browser.user.agent}")
    protected String userAgent;

    private ConfigParams configParams;

    @Autowired
    public HeaderService(ConfigParams configParams) {
        this.configParams = configParams;
    }

    private HttpHeaders getCommonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("authority", "twitter.com");
        headers.set("user-agent", userAgent);
        headers.set("cookie", configParams.getCookie());
        headers.set("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
        headers.set("accept-encoding", "gzip, deflate, br");
        return headers;
    }

    protected HttpHeaders getNextFollowerBatchJsonHeaders(String account) {
        HttpHeaders headers = getCommonHeaders();
        headers.set("pragma", "no-cache");
        headers.set("accept", "application/json, text/javascript, */*; q=0.01");
        headers.set("x-requested-with", "XMLHttpRequest");
        headers.set("x-twitter-active-user", "yes");
        headers.set("referer", String.format("https://twitter.com/%s/followers", account));
        return headers;
    }

    protected HttpHeaders getNextHomeFollowerBatchJsonHeaders() {
        HttpHeaders headers = getCommonHeaders();
        headers.set("accept", "application/json, text/javascript, */*; q=0.01");
        headers.set("x-requested-with", "XMLHttpRequest");
        headers.set("x-twitter-active-user", "yes");
        headers.set("referer", "https://twitter.com/following");
        return headers;
    }

    protected HttpHeaders getAccountFollowersPageHtmlHeaders(String account) {
        HttpHeaders headers = getCommonHeaders();
        headers.set("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.set("cache-control", "max-age=0");
        headers.set("upgrade-insecure-requests", "1");
        headers.set("referer", String.format("https://twitter.com/%s/followers", account));
        return headers;
    }

    protected HttpHeaders getHomeFollowersPageHtmlHeaders() {
        HttpHeaders headers = getCommonHeaders();
        headers.set("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.set("cache-control", "max-age=0");
        headers.set("upgrade-insecure-requests", "1");
        headers.set("referer", "https://twitter.com/");
        return headers;
    }

    protected HttpHeaders getFollowActionHeaders() {
        HttpHeaders headers = getCommonHeaders();
        headers.set("accept", "application/json, text/javascript, */*; q=0.01");
        headers.set("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.set("origin", "https://twitter.com");
        headers.set("x-twitter-auth-type", "OAuth2Session");
        headers.set("x-twitter-active-user", "yes");
        headers.set("authority", "api.twitter.com");
        headers.set("dnt", "1");
        headers.set("cache-control", "no-cache");
        headers.set("x-csrf-token", configParams.getCsrfToken());
        headers.set("authorization", configParams.getAuthorizationToken());
        return headers;
    }

}
