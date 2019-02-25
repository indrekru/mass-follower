package com.ruubel.massfollow.service;

import com.ruubel.massfollow.config.ConfigParams;
import com.ruubel.massfollow.model.Followed;
import com.ruubel.massfollow.service.http.HttpRequestService;
import com.ruubel.massfollow.service.http.HttpResponse;
import com.ruubel.massfollow.util.RawProfileCard;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class FollowService extends AbstractFollowService {

    private FollowPersistenceService followPersistenceService;
    private ConfigParams configParams;

    private double waitBetweenFollowsSeconds = 1.5;

    @Autowired
    public FollowService(
            FollowPersistenceService followPersistenceService,
            HeaderService headerService,
            HttpRequestService httpRequestService,
            ConfigParams configParams) {
        super(headerService, httpRequestService);
        this.followPersistenceService = followPersistenceService;
        this.configParams = configParams;
    }

    public void execute(String account) {

        Element body = getAccountFollowersPageHtml(account);
        String minPosition = extractMinPositionFromHtml(body);

        List<RawProfileCard> rawProfileCards = extractProfileCardsFromHtml(body);

        log.info("First batch size: " + rawProfileCards.size());
        log.info(rawProfileCards.toString());

        boolean success = follow(rawProfileCards);
        if (!success) {
            return;
        }

        while (minPosition != null) {
            log.info("Fetching next batch for " + minPosition);
            JSONObject nextBatchJson = getNextAccountFollowersBatchJson(account, minPosition);
            if (nextBatchJson == null) {
                // Probably 429 - Too many requests
                return;
            }
            boolean hasMinPosition = nextBatchJson.has("min_position");
            if (hasMinPosition) {
                minPosition = nextBatchJson.getString("min_position");
            } else {
                minPosition = null;
            }

            String itemsHtml = nextBatchJson.getString("items_html");
            body = Jsoup.parse(itemsHtml).body();

            rawProfileCards = extractProfileCardsFromHtml(body);
            log.info("Next batch size: " + rawProfileCards.size());

            success = follow(rawProfileCards);
            if (!success) {
                return;
            }

            try {
                Thread.sleep((long)(waitBetweenNextPageFetchSeconds * 1000.0));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getCurrentlyFollowing() {
        HttpResponse response = httpRequestService.exchange(
                String.format("https://twitter.com/%s", configParams.getHomeAccount()),
                Connection.Method.GET,
                new HttpHeaders(),
                new HashMap<>());

        Document parsed = Jsoup.parse(response.getBody());
        Elements numberElements = parsed.select("span.ProfileNav-value");
        Element followingElement = numberElements.get(1);
        String followingCountStr = followingElement.attr("data-count");
        return Integer.parseInt(followingCountStr);
    }

    protected JSONObject getNextAccountFollowersBatchJson(String account, String minPosition) {
        HttpHeaders headers = headerService.getNextFollowerBatchJsonHeaders(account);
        return requestJson(
                headers,
                String.format("https://twitter.com/%s/followers/users?include_available_features=1&include_entities=1&max_position=%s&reset_error_state=false", account, minPosition)
        );
    }

    private Element getAccountFollowersPageHtml(String account) {
        HttpHeaders headers = headerService.getAccountFollowersPageHtmlHeaders(account);
        return requestElement(
                headers,
                String.format("https://twitter.com/%s/followers", account)
        );
    }

    private List<RawProfileCard> extractProfileCardsFromHtml(Element parent) {
        List<RawProfileCard> out = new ArrayList<>();
        Elements profileCards = parent.select("div.ProfileCard.js-actionable-user");
        for (Element profileCard : profileCards) {
            Elements bio = profileCard.select("p.ProfileCard-bio");
            if (bio.size() == 0) {
                log.info("No bio, skip");
                continue;
            }
            if (bio.get(0).text().trim().isEmpty()) {
                log.info("No bio, skip");
                continue;
            }
            Element actionButtonElement = profileCard.select("div.user-actions.btn-group").get(0);
            boolean following = false;
            if (actionButtonElement.hasClass("following")) {
                following = true;
            }
            if (actionButtonElement.hasClass("pending")) {
                following = true;
            }
            String name = profileCard.attr("data-screen-name");
            String userId = profileCard.attr("data-user-id");
            RawProfileCard rawProfileCardObj = new RawProfileCard(name, userId, following);
            out.add(rawProfileCardObj);
        }
        return out;
    }

    private boolean attemptFollowAndSleep(String name, String userId) {
        log.info("Trying to follow " + name + "(ID:" + userId + ")");
        boolean success = follow(userId);
        if (!success) {
            log.warn("Failed follow. Abort");
            return false;
        }
        try {
            Thread.sleep((long)(waitBetweenFollowsSeconds * 1000.0));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean follow(List<RawProfileCard> rawProfileCards) {
        for (RawProfileCard rawProfileCard : rawProfileCards) {
            if (!rawProfileCard.isFollowing()) {
                Followed followed = followPersistenceService.findByExternalId(rawProfileCard.getUserId());
                if (followed != null) {
                    log.info("Have already followed " + followed.getExternalName() + ", skip");
                    continue;
                }
                boolean success = attemptFollowAndSleep(rawProfileCard.getName(), rawProfileCard.getUserId());
                if (!success) {
                    return false;
                } else {
                    followed = new Followed(rawProfileCard.getName(), rawProfileCard.getUserId());
                    followPersistenceService.save(followed);
                }
            }
        }
        return true;
    }

    private boolean follow(String userId) {
        return followAction(FollowAction.FOLLOW, userId);
    }

}
