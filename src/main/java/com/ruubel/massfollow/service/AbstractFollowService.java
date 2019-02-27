package com.ruubel.massfollow.service;

import com.ruubel.massfollow.service.http.HttpRequestService;
import com.ruubel.massfollow.service.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractFollowService {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected HeaderService headerService;
    protected HttpRequestService httpRequestService;

    protected double waitBetweenNextPageFetchSeconds = 1;

    public AbstractFollowService(
            HeaderService headerService,
            HttpRequestService httpRequestService) {
        this.headerService = headerService;
        this.httpRequestService = httpRequestService;
    }

    /**
     * Extract the minPosition for pagination from HTML
     */
    protected String extractMinPositionFromHtml(Element body) {
        Elements gridTimelineItemss = body.select("div.GridTimeline-items");
        if (gridTimelineItemss.size() > 0) {
            Element gridTimelineElement = gridTimelineItemss.get(0);
            String minPosition = gridTimelineElement.attr("data-min-position");
            return minPosition;
        }
        return null;
    }

    /**
     * Extract the minPosition for pagination from JSON
     */
    protected String extractMinPositionFromJson(JSONObject json) {
        if (json.has("min_position")) {
            return json.getString("min_position");
        } else {
            return null;
        }
    }

    /**
     * Extract and parse the HTML property from the JSON
     */
    protected Element extractHtmlFromJson(JSONObject json) {
        if (json.has("items_html")) {
            return Jsoup.parse(json.getString("items_html"));
        } else {
            return null;
        }
    }

    /**
     * Request params needed for follow/unfollow request
     */
    protected Map<String, String> getFollowActionRequestData(String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("challenges_passed", "false");
        map.put("handles_challenges", "1");
        map.put("impression_id", "");
        map.put("include_blocked_by", "true");
        map.put("include_blocking", "true");
        map.put("include_can_dm", "true");
        map.put("include_followed_by", "true");
        map.put("include_mute_edge", "true");
        map.put("skip_status", "true");
        map.put("user_id", userId);
        return map;
    }

    /**
     * Used for both follow and unfollow actions
     */
    protected boolean followAction(FollowAction followAction, String userId) {
        HttpHeaders headers = headerService.getFollowActionHeaders();
        Map<String, String> data = getFollowActionRequestData(userId);

        HttpResponse response = httpRequestService.exchange(
                followAction.getActionUrl(),
                Connection.Method.POST,
                headers,
                data
        );

        int statusCode = response.getStatusCode();
        log.warn(String.format("%s : %s", statusCode, response.getBody()));

        if (statusCode == 403) {
            JSONObject responseObj;
            try {
                responseObj = new JSONObject(response.getBody());
            } catch (Exception e) {
                return false;
            }
            JSONArray errors = responseObj.getJSONArray("errors");
            JSONObject error = errors.getJSONObject(0);
            int errorCode = error.getInt("code");
            if (errorCode == 162) {
                // User blocked us, ignore
                return true;
            } else {
                return false;
            }
        } else if (statusCode == 500) {
            // Failed connecting
            return false;
        } else {
            return true;
        }
    }

    protected JSONObject requestJson(HttpHeaders headers, String url) {
        HttpResponse response = request(headers, url);
        if (response.getStatusCode() == 200) {
            return new JSONObject(response.getBody());
        } else {
            log.warn(String.format("%s : %s", response.getStatusCode(), url));
            return null;
        }
    }

    protected Element requestElement(HttpHeaders headers, String url) {
        HttpResponse response = request(headers, url);
        if (response.getStatusCode() == 200) {
            return Jsoup.parse(response.getBody());
        } else {
            log.warn(String.format("%s : %s", response.getStatusCode(), url));
            return null;
        }
    }

    private HttpResponse request(HttpHeaders headers, String url) {
        return httpRequestService.exchange(
                url,
                Connection.Method.GET,
                headers,
                new HashMap<>()
        );
    }

    protected void sleep(double seconds) {
        try {
            Thread.sleep((long)(seconds * 1000.0));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
